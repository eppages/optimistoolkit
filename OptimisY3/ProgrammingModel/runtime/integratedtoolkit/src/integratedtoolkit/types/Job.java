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


import org.apache.log4j.Logger;

import integratedtoolkit.log.Loggers;
import integratedtoolkit.components.impl.JobManager;


public abstract class Job {

	// Job identifier management
	protected static int nextJobId;
	protected static final int FIRST_JOB_ID = 1;


        protected static JobManager associatedJM;
	// Job history
	public enum JobHistory {
		NEW,
		RESUBMITTED_FILES,
		RESUBMITTED,
		RESCHEDULED;
	}
	
	// Job kind
	public enum JobKind {
		METHOD,
		SERVICE;
	}

	// Information of the job
	protected int jobId;
	protected Task task;
	protected JobHistory history;


        protected static final Logger logger = Logger.getLogger(Loggers.JM_COMP);
	protected static final boolean debug = logger.isDebugEnabled();
	protected static final String workerDebug = Boolean.toString(Logger.getLogger(Loggers.WORKER).isDebugEnabled());


	public static void init(JobManager associatedJM) {
		nextJobId = FIRST_JOB_ID;
                Job.associatedJM = associatedJM;
	}

	public int getJobId() {
		return jobId;
	}

	public Core getCore() {
		return task.getCore();
	}

        public Task getTask() {
		return task;
	}

	public ExecutionParams getExecutionParams() {
		return task.getExecParams();
	}

	public JobHistory getHistory() {
		return history;
	}

	public void setExecutionParams(ExecutionParams newExecParams) {
		task.setExecParams(newExecParams);
	}



	public void setHistory(JobHistory newHistoryState) {
		this.history = newHistoryState;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();

		buffer.append("[[Job id: ").append(getJobId()).append("]");
		buffer.append(", ").append(getCore().toString());
		buffer.append(", [Target host: ").append(getExecutionParams().getHost()).append("]");
		buffer.append(", [User: ").append(getExecutionParams().getUser()).append("]]");

		return buffer.toString();
	}

    public void submit() throws Exception{
        throw new Exception("not defined");
    }

    public void stop() throws Exception{
        throw new Exception("not defined");
    }

    public Object getReturnValue() {
        return null;
    }
    
    public abstract JobKind getKind();
    
}
