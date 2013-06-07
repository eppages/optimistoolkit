/*
 *  Copyright 2002-2012 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package integratedtoolkit.util.nio;

import integratedtoolkit.util.nio.NIOConstants.MessageType;
import integratedtoolkit.util.nio.NIOConstants.SocketChange;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends LinkedBlockingQueue {

    private HashMap<String, Long> messageCount;
    private HashMap<String, HashMap<Long, integratedtoolkit.types.message.Message>> pendingMessages;
    /**
     * The channel on which we'll accept connections
     */
    private ServerSocketChannel serverChannel;
    public int port;

    /**
     * Constructs a new Server to attend new NIO requests
     *
     * @param queue Queue where all the received requests will be stored
     * @throws IOException
     */
    public Server(int port) throws IOException, ClosedChannelException, InterruptedException {
        super();
        messageCount = new HashMap<String, Long>();
        pendingMessages = new HashMap<String, HashMap<Long, integratedtoolkit.types.message.Message>>();
        this.port = port;
        this.serverChannel = Listener.registerServer(port, SelectionKey.OP_ACCEPT, this);
    }

    void addServerEvent(SocketChannel socketChannel, Message message) {
        String source = socketChannel.socket().getInetAddress().getHostAddress();
        Long count = messageCount.get(source);
        if (count == null) {
            count = 0l;
        }
        count++;
        byte[] value = message.getValue();
        try {
            Object o = Serializer.deserialize(value);
            integratedtoolkit.types.message.Message se = (integratedtoolkit.types.message.Message) o;
            se.setSource(socketChannel);
            if (se.getId() == count) {
                super.offer(se);
                //Recuperar missatges anteriors
                HashMap<Long, integratedtoolkit.types.message.Message> pending = pendingMessages.get(source);
                if (pending != null) {
                    while ((se = pending.get(count + 1)) != null) {
                        super.offer(se);
                        count++;
                    }
                }
                messageCount.put(source, count);
            } else {
                HashMap<Long, integratedtoolkit.types.message.Message> pending = pendingMessages.get(source);
                if (pending == null) {
                    pending = new HashMap<Long, integratedtoolkit.types.message.Message>();
                    pendingMessages.put(source, pending);
                }
                pending.put(se.getId(), se);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public integratedtoolkit.types.message.Message take() throws InterruptedException {
        integratedtoolkit.types.message.Message m = (integratedtoolkit.types.message.Message) super.take();
        m.setServer(this);
        return m;
    }

    public void terminateConnection(SocketChannel source) throws IOException {
        Listener.change(source, SocketChange.CLOSE);
    }

    public void reply(SocketChannel source, Object response) throws IOException {
        byte[] serializedResponse = Serializer.serialize(response);
        //Listener.change(source, SocketChange.WRITE);
        Listener.reply(source, serializedResponse, MessageType.RESPONSE);
    }
}
