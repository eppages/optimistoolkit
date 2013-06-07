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
package integratedtoolkit.types.message.tp;

import integratedtoolkit.types.message.Message;

/**
 * The TDRequest class represents any interacction with the TaskDispatcher
 * component.
 */
public class TPMessage extends Message {

    /**
     * Contains the different types of request that the Task Dispatcher can
     * response.
     */
    public enum TPMessageType {

        ANALYSE_TASK,
        UPDATE_GRAPH,
        WAIT_FOR_TASK,
        END_OF_APP,
        ALREADY_ACCESSED,
        REGISTER_DATA_ACCESS,
        NEW_VERSION_SAME_VALUE,
        IS_OBJECT_HERE,
        SET_OBJECT_VERSION_VALUE,
        GET_LAST_RENAMING,
        GET_LAST_DATA_ACCESS,
        BLOCK_AND_GET_RESULT_FILES,
        UNBLOCK_RESULT_FILES,
        SHUTDOWN,
        GRAPHSTATE,
        DELETE_FILE,
        TASKSTATE,
        CHECK_OBSOLETE
    }
    /**
     * Type of the request instance.
     */
    private TPMessageType requestType;

    public TPMessage() {
    }

    /**
     * Cosntructs a new TDRequest for that kind of notification
     *
     * @param requestType new request type name
     *
     */
    public TPMessage(TPMessageType requestType) {
        this.requestType = requestType;
    }

    /**
     * returns the type of request for this instance
     *
     * @result return the request type name of this instance
     *
     */
    public TPMessageType getRequestType() {
        return requestType;
    }

    /**
     * Sets the request type of this TDMessage
     *
     * @param requestType Type of Message
     */
    public void setRequestType(TPMessageType requestType) {
        this.requestType = requestType;
    }

    public String toString() {
        return "TPMessage (" + requestType + ")";
    }
}
