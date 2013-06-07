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

import integratedtoolkit.types.message.Message;
import integratedtoolkit.util.nio.NIOConstants.MessageType;
import java.io.IOException;
import java.util.HashMap;

public class Client {

    private static HashMap<String, Long> messageCount = new HashMap();

    public static void send(String host, int port, Message data) throws IOException {
        Long id;
        synchronized (messageCount) {
            id = messageCount.get(host + ":" + port);
            if (id == null) {
                id = 0l;
            }
            id++;
            messageCount.put(host + ":" + port, id);
        }
        data.setId(id);
        byte[] serializedData = Serializer.serialize(data);
        Listener.send(host, port, serializedData, MessageType.NOTIFICATION);
    }

    public static Object request(String host, int port, Message data) throws IOException, ClassNotFoundException {
        Long id;
        synchronized (messageCount) {
            id = messageCount.get(host + ":" + port);
            if (id == null) {
                id = 0l;
            }
            id++;
            messageCount.put(host + ":" + port, id);
        }
        data.setId(id);
        byte[] serializedData = Serializer.serialize(data);
        byte[] value = Listener.send(host, port, serializedData, MessageType.REQUEST);
        return Serializer.deserialize(value);
    }
}