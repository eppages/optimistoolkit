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
package integratedtoolkit.types.message.td;

import integratedtoolkit.types.data.DataInstanceId;

/**
 * The NewDataVersionRequest represents a notification about the creation of a
 * new data version.
 */
public class NewDataVersionRequest extends TDMessage {

    /**
     * Previous version Data Id
     */
    private DataInstanceId lastDID;
    /**
     * name of the data containing file
     */
    private String fileName;
    /**
     * location of the data containing file
     */
    //Access protocol
    private String protocol;
    //Host
    private String host;
    //Path
    private String path;

    /**
     * Constructs an empty NewDataVersionRequest
     *
     */
    public NewDataVersionRequest() {
        super(TDMessageType.NEW_DATA_VERSION);
    }

    /**
     * Constructs a NewDataVersionRequest
     *
     * @param lastDID Previous version Data Id
     * @param fileName Name of the data containing file
     * @param protocol Protocol used to access the file
     * @param host Host containing the file
     * @param path Path where to find the file
     *
     */
    public NewDataVersionRequest(DataInstanceId lastDID, String protocol, String host, String path, String fileName) {
        super(TDMessageType.NEW_DATA_VERSION);
        this.lastDID = lastDID;
        this.fileName = fileName;
        this.protocol = protocol;
        this.host = host;
        this.path = path;
    }

    /**
     * Returns the previous version Data Id
     *
     * @result the previous version Data Id
     *
     */
    public DataInstanceId getLastDID() {
        return lastDID;
    }

    /**
     * Sets the previous version Data Id
     *
     * @param lastDID the previous version Data Id
     *
     */
    public void setLastDID(DataInstanceId lastDID) {
        this.lastDID = lastDID;
    }

    /**
     * Returns the name of the data containing file
     *
     * @result the name of the data containing file
     *
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the data containing file
     *
     * @param fileName the name of the data containing file
     *
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the protocol used to access the file
     *
     * @result the protocol to access the file
     *
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol to access the file
     *
     * @param protocol the protocol to access the file
     *
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Returns the host containing file
     *
     * @result the host containing the file
     *
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host containing the file
     *
     * @param host the host containing the file
     *
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns the path of the data containing file
     *
     * @result the path of the data containing file
     *
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the location of the data containing file
     *
     * @param location the location of the data containing file
     *
     */
    public void setPath(String path) {
        this.path = path;
    }
}
