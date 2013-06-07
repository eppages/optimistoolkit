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
package integratedtoolkit.types;

import java.io.Serializable;

public class ExecutionParams implements Serializable {

    // Parameters of a concrete execution of a task
    private String user;
    private String host;
    private String installDir;
    private String workingDir;
    private int cost;
    private String queue;

    public ExecutionParams(){
        
    }
    
    public ExecutionParams(String user,
            String host,
            String installDir,
            String workingDir) {
        this(user, host, installDir, workingDir, 0, null);
    }

    public ExecutionParams(String user,
            String host,
            String installDir,
            String workingDir,
            int cost,
            String queue) {
        this.user = user;
        this.host = host;
        this.installDir = installDir;
        this.workingDir = workingDir;
        this.cost = cost;
        this.queue = queue;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getInstallDir() {
        return installDir;
    }

    public void setInstallDir(String installDir) {
        this.installDir = installDir;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
