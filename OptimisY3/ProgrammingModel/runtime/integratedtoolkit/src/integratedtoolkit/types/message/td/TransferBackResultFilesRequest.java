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

import integratedtoolkit.types.data.ResultFile;
import java.util.List;

/**
 * The TransferResultFilesRequest class represents a request to send a set of
 * files resultants from the execution located in the workers to the master
 */
public class TransferBackResultFilesRequest extends TDMessage {

    /**
     * List of files to be transferred
     */
    private List<ResultFile> resFiles;

    public TransferBackResultFilesRequest() {
        super(TDMessageType.TRANSFER_RESULT_FILES);
    }

    /**
     * Constructs a new TransferResultFilesRequest
     *
     * @param resFiles List of files to be transferred
     */
    public TransferBackResultFilesRequest(List<ResultFile> resFiles) {
        super(TDMessageType.TRANSFER_RESULT_FILES);
        this.resFiles = resFiles;
    }

    /**
     * Returns the list of files to be transferred back
     *
     * @return The list of files to be transferred back
     */
    public List<ResultFile> getResFiles() {
        return resFiles;
    }

    /**
     * Sets the files to be transferred back to the master
     *
     * @param resFiles list of files to be transferred back to the master
     */
    public void setResFiles(List<ResultFile> resFiles) {
        this.resFiles = resFiles;
    }
}
