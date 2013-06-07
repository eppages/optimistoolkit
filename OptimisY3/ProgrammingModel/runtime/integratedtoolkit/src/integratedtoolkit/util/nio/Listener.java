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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Listener implements Runnable {

    /**
     * The selector we'll be monitoring
     */
    private static Selector selector;
    /**
     * A list of PendingChange instances
     */
    private static LinkedList<ChangeRequest> pendingChanges;
    /**
     * Maps a SocketChannel to a list of ByteBuffer instances
     */
    private static HashMap<SocketChannel, ArrayList<Message>> pendingData;
    /**
     * Maps a SocketChannel to a list of ByteBuffer instances
     */
    private static HashMap<SocketChannel, Message> socketToMessage;
    /**
     * Maps a SocketChannel to a server
     */
    private static HashMap<SocketChannel, Server> socketToServer;
    /**
     * Maps a SocketChannel to a server
     */
    private static HashMap<ServerSocketChannel, Server> serverSocketToServer;
    /**
     * The buffer into which we'll read data when it's available
     */
    private static HashMap<Integer, StringBuilder> channelsLog;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    public static final boolean debug = false;

    static {
        try {
            start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the Listener and raises a new thread
     */
    private static void start() throws IOException {
        channelsLog = new HashMap<Integer, StringBuilder>();
        pendingChanges = new LinkedList();
        selector = SelectorProvider.provider().openSelector();
        Thread t = new Thread(new Listener());
        t.setDaemon(true);
        t.start();
        pendingChanges = new LinkedList();
        pendingData = new HashMap();
        socketToMessage = new HashMap();
        socketToServer = new HashMap();
        serverSocketToServer = new HashMap();

    }

    public static byte[] send(String host, int port, byte[] data, MessageType type) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Create a non-blocking socket channel
        SocketChannel socket = SocketChannel.open();

        socket.configureBlocking(false);
        // Kick off connection establishment
        socket.connect(new InetSocketAddress(host, port));
        if (debug) {
            channelsLog.put(socket.hashCode(), sb);
            sb.append("Connect ").append(type).append(" --> ").append(socket.hashCode()).append(" \n");
        }
        Message message;

        // And queue the data we want written
        synchronized (pendingData) {
            ArrayList<Message> queue = pendingData.get(socket);
            if (queue == null) {
                queue = new ArrayList();
                pendingData.put(socket, queue);
            }
            message = new Message(type, data);
            queue.add(message);
            if (debug) {
                sb.append("Message written in the queues\n");
            }
            synchronized (socketToMessage) {
                socketToMessage.put(socket, message);
            }
        }
        change(socket, SocketChange.REGISTER);

        if (type == MessageType.REQUEST) {
            return message.getValue();
        } else {
            return null;
        }

    }

    /**
     * Registers a new Server Socket Channel on a given selector
     *
     * @param serverChannel Server socket channel
     * @param op The interest set for the resulting key
     * @throws ClosedChannelException
     */
    static ServerSocketChannel registerServer(int port, int op, Server requests)
            throws IOException, ClosedChannelException, InterruptedException {

        // Create a new non-blocking server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        if (debug) {
            StringBuilder log = new StringBuilder();
            channelsLog.put(serverChannel.hashCode(), log);
            log.append(System.currentTimeMillis()).append(":").append("Registering a new Server channel -->").append(serverChannel.hashCode()).append("\n");
        }
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        InetSocketAddress isa = new InetSocketAddress((InetAddress) null, port);
        serverChannel.socket().bind(isa);
        // Register the server socket channel, indicating an interest in 
        // accepting new connections
        change(serverChannel, SocketChange.SERVER_REGISTER);
        serverSocketToServer.put(serverChannel, requests);
        return serverChannel;
    }

    public static void reply(SocketChannel socket, byte[] data, MessageType type) throws IOException {
        Message message;
        // And queue the data we want written
        synchronized (pendingData) {
            ArrayList<Message> queue = pendingData.get(socket);
            if (queue == null) {
                queue = new ArrayList();
                pendingData.put(socket, queue);
            }
            message = new Message(type, data);
            queue.add(message);
            synchronized (socketToMessage) {
                socketToMessage.put(socket, message);
            }
        }

        change(socket, SocketChange.WRITE);
    }

    /**
     * Queue a channel registration since the caller is not the selecting
     * thread. As part of the registration we'll register an interest in
     * connection events. These are raised when a channel is ready to complete
     * connection establishment.
     *
     * @param socketChannel socket to be changed
     * @param change desired change
     * @param operation operation to do in the channel
     */
    public static void change(Channel socketChannel, SocketChange change) {

        synchronized (pendingChanges) {
            if (debug) {
                StringBuilder sb = channelsLog.get(socketChannel.hashCode());
                sb.append("Ordering change ").append(change).append("\n");
            }
            pendingChanges.add(new ChangeRequest(socketChannel, change));

        }
        // Finally, wake up our selecting thread so it can make the required changes
        selector.wakeup();

    }

    @Override
    public void run() {

        while (true) {
            try {
                // Process any pending changes
                synchronized (pendingChanges) {
                    Iterator changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        SelectionKey key;
                        if (debug) {
                            StringBuilder sb = channelsLog.get(change.channel.hashCode());
                            sb.append("Changing to ").append(change.type).append(" \n");
                        }
                        switch (change.type) {
                            case SERVER_REGISTER:
                                ((ServerSocketChannel) change.channel).register(selector, SelectionKey.OP_ACCEPT);
                                break;
                            case REGISTER:
                                ((SocketChannel) change.channel).register(selector, SelectionKey.OP_CONNECT);
                                break;
                            case READ:
                                key = ((SocketChannel) change.channel).keyFor(selector);
                                if (key != null) {
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                                break;
                            case WRITE:
                                key = ((SocketChannel) change.channel).keyFor(selector);
                                if (key != null) {
                                    key.interestOps(SelectionKey.OP_WRITE);
                                }
                                break;
                            case CLOSE:
                                key = ((SocketChannel) change.channel).keyFor(selector);
                                if (key != null) {
                                    key.cancel();
                                }
                                socketToServer.remove(((SocketChannel) change.channel));
                                socketToMessage.remove(((SocketChannel) change.channel));
                                pendingData.remove(((SocketChannel) change.channel));
                                change.channel.close();
                                break;
                        }
                    }
                    pendingChanges.clear();
                }
                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    if (debug) {
                        StringBuilder log = channelsLog.get(key.channel().hashCode());
                        log.append(System.currentTimeMillis()).append(":").append(System.currentTimeMillis()).append(":").append("tractant una clau del channel ").append(key.channel().hashCode()).append("\n");
                    }
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        if (debug) {
                            StringBuilder log = channelsLog.get(key.channel().hashCode());
                            log.append(System.currentTimeMillis()).append(":").append("key is invalid\n");
                        }
                        continue;
                    }
                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isConnectable()) {
                        this.finishConnection(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {

        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();


        if (debug) {
            StringBuilder sb = new StringBuilder();
            channelsLog.put(socketChannel.hashCode(), sb);
            sb.append("Accepted by the Server --> ").append(socketChannel.toString()).append(" \n");
        }


        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketToServer.put(socketChannel, serverSocketToServer.get(serverSocketChannel));
        synchronized (pendingData) {
            ArrayList<Message> queue = pendingData.get(socketChannel);
            if (queue == null) {
                queue = new ArrayList();
                pendingData.put(socketChannel, queue);
            }
        }

    }

    /**
     * Closes the connection on the channel of a given key
     *
     * @param key key whose channel must be closed
     */
    private void finishConnection(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder log;
        if (debug) {
            log = channelsLog.get(socketChannel.hashCode());
            log.append(System.currentTimeMillis()).append(":").append("Finishing connection ").append(" \n");
        }
        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
            if (debug) {
                log.append(System.currentTimeMillis()).append(":").append("Connection Finished: ").append(socketChannel.hashCode()).append(" --> ").append(socketChannel.toString()).append("\n");
            }
        } catch (java.net.ConnectException e) {
            e.printStackTrace();
            change(socketChannel, SocketChange.CLOSE);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            change(socketChannel, SocketChange.CLOSE);
            return;
        }

        // Register an interest in writing on this channel
        change(socketChannel, SocketChange.WRITE);

    }

    private void read(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder log = null;
        if (debug) {
            log = channelsLog.get(socketChannel.hashCode());
            log.append(System.currentTimeMillis()).append(":").append("Reading  ").append(" \n");
        }
        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
            if (debug) {
                log.append(System.currentTimeMillis()).append(":").append(numRead).append(" bytes readed\n");
            }
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            e.printStackTrace();
            change(socketChannel, SocketChange.CLOSE);
            return;
        }
        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            change(socketChannel, SocketChange.CLOSE);
            return;
        }

        Message message;
        synchronized (socketToMessage) {
            message = socketToMessage.get(socketChannel);
            if (message == null) {
                message = new Message();
                socketToMessage.put(socketChannel, message);
            }
        }
        if (message.readData(readBuffer.array(), numRead, log)) {
            if (message.type == MessageType.RECEIVED) {
                Server server = socketToServer.get(socketChannel);
                if (debug) {
                    log.append(System.currentTimeMillis()).append(":").append("Appending Message to the server queue\n");
                }
                server.addServerEvent(socketChannel, message);
            } else {
                change(socketChannel, SocketChange.CLOSE);
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder log;
        if (debug) {
            log = channelsLog.get(socketChannel.hashCode());
            log.append(System.currentTimeMillis()).append(":").append("Writting  ").append(" \n");
        }
        synchronized (this.pendingData) {
            ArrayList<Message> queue = this.pendingData.get(socketChannel);
            boolean waitResponse = false;

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                Message message = queue.get(0);
                waitResponse |= (message.type == MessageType.REQUEST);
                ByteBuffer buf = message.sentData;
                int written = socketChannel.write(buf);
                if (debug) {
                    log = channelsLog.get(socketChannel.hashCode());
                    log.append(System.currentTimeMillis()).append(":").append(written).append(" bytes written to channel ").append(" \n");
                }
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                if (message.type != MessageType.REQUEST) {
                    synchronized (socketToMessage) {
                        message = socketToMessage.remove(socketChannel);
                    }
                }
                message.sentData = null;
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data or close the channel
                if (waitResponse) {
                    //Switch back to Read
                    change(socketChannel, SocketChange.READ);
                } else {
                    change(socketChannel, SocketChange.CLOSE);
                }
            }
        }
    }

    public static void end() {
        if (debug) {
            for (StringBuilder log : channelsLog.values()) {
                System.out.println(log);
            }
        }
    }
}

class ChangeRequest {

    public Channel channel;
    public SocketChange type;
    public Object attach;

    public ChangeRequest(Channel socket, SocketChange type) {
        this.channel = socket;
        this.type = type;
    }
}

class Message {

    MessageType type;
    ByteBuffer sentData;
    byte[] readData;
    private NIORead receivedData;

    public Message() {
        this.type = MessageType.RECEIVED;
        receivedData = new NIORead();
    }

    public Message(MessageType type, byte[] data) {

        this.type = type;
        ByteBuffer message = ByteBuffer.allocate(4 + data.length);
        message.putInt(data.length);
        message.put(data);
        message.flip();
        this.sentData = message;
        if (type == MessageType.REQUEST) {
            receivedData = new NIORead();
        }
    }

    public byte[] getValue() {
        readData = receivedData.waitForResponse();
        return readData;
    }

    public boolean readData(byte[] array, int numRead, StringBuilder log) {
        return receivedData.handleResponse(array, numRead, log);
    }
}

class NIORead {

    private int count;
    private int sizeCount;
    private int size;
    private byte[] sizeArray = new byte[4];
    private byte[] value = null;

    public NIORead() {
        count = 0;
        sizeCount = 0;
        size = 0;
    }

    public synchronized boolean handleResponse(byte[] rsp, int numRead, StringBuilder log) {

        int rspIndex = 0;
        //if we don't know the size of the package
        if (sizeCount < 4) {
            int readableBytes = Math.min(4 - sizeCount, rsp.length);
            System.arraycopy(rsp, 0, sizeArray, sizeCount, readableBytes);
            sizeCount += readableBytes;
            rspIndex = readableBytes;
            if (sizeCount < 4) {
                //we read all what we can , but still enough to get size. Must 
                // wait for new bytes from this channel
                return false;
            }
            size = (((int) sizeArray[0] & 0x000000FF) << 24) + (((int) sizeArray[1] & 0x000000FF) << 16) + (((int) sizeArray[2] & 0x000000FF) << 8) + ((int) sizeArray[3] & 0x000000FF);
            value = new byte[size];
        }
        if (Listener.debug) {
            log.append(System.currentTimeMillis()).append(":").append("CONTENT COMPOSED BY: ").append(size).append("\n");
        }

        int readableBytes = Math.min(numRead - rspIndex, size - count);
        System.arraycopy(rsp, rspIndex, value, count, readableBytes);
        count += readableBytes;
        if (count == size) {
            if (Listener.debug) {
                log.append(System.currentTimeMillis()).append(":").append("CONTENT COMPLETELY READ \n");
            }
            this.notify();
            return true;
        } else {
            return false;
        }
    }

    public synchronized byte[] waitForResponse() {
        while (this.value == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        return value;
    }
}
