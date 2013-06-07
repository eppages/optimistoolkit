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

public class NIOConstants {

    public final static int TP_PORT = 20400;
    public final static int TD_PORT = 20401;
    public final static int WORKER_PORT = 20402;
    public final static int JOB_NOTIFICATION_PORT = 20403;

    public static enum MessageType {

        NOTIFICATION,
        REQUEST,
        RECEIVED,
        RESPONSE
    }

    public static enum SocketChange {

        SERVER_REGISTER,
        REGISTER,
        READ,
        WRITE,
        CLOSE
    }
}
