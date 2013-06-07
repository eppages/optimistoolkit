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

import integratedtoolkit.types.message.Message;

/**
 * The TDRequest class represents any interacction with the TaskDispatcher
 * component.
 */
public class TDMessage extends Message {

    /**
     * Contains the different types of request that the Task Dispatcher can
     * response.
     */
    public enum TDMessageType {
        //Schedule new tasks
        SCHEDULE_TASKS,
        //Request task reschedule
        RESCHEDULE_TASK,
        //Notify job end
        FINISHED_TASK,
        // New Waiting Task on the TA
        NEW_WAITING_TASK,
        //Register New Data creation/access
        NEW_DATA_VERSION,
        //Get Scheduling state for SO
        GET_STATE,
        //Apply resource changes to the scheduling system
        SET_STATE,
        //Get current
        MONITOR_DATA,
        //NEW VM Creation - review Core constraints and links
        UPDATE_CEI,
        //Transfer raw file
        TRANSFER_RAW_FILE,
        //Transfer file for open
        TRANSFER_OPEN_FILE,
        //Transfer object to the TP
        TRANSFER_OBJECT,
        //Transfer back result files
        TRANSFER_RESULT_FILES,
        //Delete intermediate files at the end of the execution
        DELETE_INTERMEDIATE_FILES,
        //Shutdown Task Dispatcher
        SHUTDOWN
        
    }
    /**
     * Type of the request instance.
     */
    private TDMessageType requestType;

    public TDMessage() {
    }

    /**
     * Cosntructs a new TDRequest for that kind of notification
     *
     * @param requestType new request type name
     *
     */
    public TDMessage(TDMessageType requestType) {
        this.requestType = requestType;
    }

    /**
     * returns the type of request for this instance
     *
     * @result return the request type name of this instance
     *
     */
    public TDMessageType getRequestType() {
        return requestType;
    }

    /**
     * Sets the request type of this TDMessage
     *
     * @param requestType Type of Message
     */
    public void setRequestType(TDMessageType requestType) {
        this.requestType = requestType;
    }
    
    public String toString(){
        return "TDMessage ("+requestType+")";
    }
}
