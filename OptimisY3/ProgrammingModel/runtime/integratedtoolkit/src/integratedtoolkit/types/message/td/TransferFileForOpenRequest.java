/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
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

import integratedtoolkit.types.data.DataAccessId;
import integratedtoolkit.types.data.Location;

/**
 * The TransferOpenFileRequest represents a request to transfer some file
 * version located in a worker to the master to be opened during the execution
 */
public class TransferFileForOpenRequest extends TDMessage {

    /**
     * Data Id and version of the requested file
     */
    private DataAccessId faId;
    /**
     * Location where to leave the requested file
     */
    private Location location;
    /**
     * Constructs a new TransferOpenFileRequest
     *
     */
    public TransferFileForOpenRequest() {
        super(TDMessageType.TRANSFER_OPEN_FILE);
    }
    /**
     * Constructs a new TransferOpenFileRequest
     *
     * @param faId Data Id and version of the requested file
     * @param location Location where to leave the requested file
     */
    public TransferFileForOpenRequest(DataAccessId faId, Location location) {
        super(TDMessageType.TRANSFER_OPEN_FILE);
        this.faId = faId;
        this.location = location;
    }

    /**
     * Returns the data Id and version of the requested file
     *
     * @return Data Id and version of the requested file
     */
    public DataAccessId getFaId() {
        return faId;
    }

    /**
     * Sets the data Id and version of the requested file
     *
     * @param faId Data Id and version of the requested file
     */
    public void setFaId(DataAccessId faId) {
        this.faId = faId;
    }

    /**
     * Returns the location where to leave the requested file
     *
     * @return the location where to leave the requested file
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location where to leave the requested file
     *
     * @param location Location where to leave the requested file
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
