/*
 *  Copyright 2002-2011 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.types.message;

import integratedtoolkit.util.nio.Server;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * The Request class represents any interacction with the main components of the
 * COMPSs runtime
 */
public class Message implements Serializable {

    private SocketChannel source;
    private String sourceName;
    private Server server;
    private long id;

    public Message() {
    }

    public void setSource(SocketChannel source) {
        this.source = source;
        this.sourceName = source.socket().getInetAddress().getHostAddress();
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SocketChannel getSource() {
        return source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Server getServer() {
        return server;
    }

    public long getId() {
        return id;
    }

    public void reply(Object response) throws IOException {
        server.reply(source, response);
    }
}
