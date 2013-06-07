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

import java.util.concurrent.atomic.AtomicInteger;

public class Task implements Comparable<Task> {

    // Task states
    public enum TaskState {

        TO_ANALYSE,
        TO_SCHEDULE,
        TO_RESCHEDULE,
        TO_EXECUTE,
        FINISHED,
        FAILED
    }
    // Task fields
    private long appId;
    //Task Dispatcher Id
    private int taskId;
    //Scheduling Id
    private int schedulingTaskId;
    private String source;
    
    private TaskState status;
    private Core core;
    private ExecutionParams execParams;
    // Scheduling info
    private boolean enforcedSceduling;
    private boolean strongEnforcedScheduling;
    private int enforcingDataId;
    // Execution info
    private long initialTimeStamp;
    // Task ID management
    private static final int FIRST_TASK_ID = 1;
    private static AtomicInteger nextTaskId = new AtomicInteger(FIRST_TASK_ID);

    public Task() {
        taskId = -1;
    }

    public Task(Parameter[] parameters) {
        this.core = new Method(parameters);
    }

    public Task(Long appId, String methodClass, String methodName, boolean hasTarget, Parameter[] parameters) {
        this.appId = appId;
        this.taskId = nextTaskId.getAndIncrement();//nextTaskId++;
        this.status = TaskState.TO_ANALYSE;
        this.core = new Method(methodClass, methodName, hasTarget, parameters);
    }

    public Task(Long appId, String namespace, String service, String port, String operation, boolean hasTarget, Parameter[] parameters) {
        this.appId = appId;
        this.taskId = nextTaskId.getAndIncrement();//nextTaskId++;
        this.status = TaskState.TO_ANALYSE;
        this.core = new Service(namespace, service, port, operation, hasTarget, parameters);
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSchedulingTaskId() {
        return schedulingTaskId;
    }

    public void setSchedulingTaskId(int schedulingTaskId) {
        this.schedulingTaskId = schedulingTaskId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public TaskState getStatus() {
        return status;
    }

    public void setStatus(TaskState status) {
        this.status = status;
    }

    public Core getCore() {
        return core;
    }

    public void setCore(Core core) {
        this.core = core;
    }

    public ExecutionParams getExecParams() {
        return execParams;
    }

    public void setExecParams(ExecutionParams execParams) {
        this.execParams = execParams;
    }

    public boolean isEnforcedSceduling() {
        return this.enforcedSceduling;
    }

    public void setEnforcedSceduling(boolean enforcedSceduling) {
        this.enforcedSceduling = enforcedSceduling;
    }

    public boolean isStrongEnforcedScheduling() {
        return this.strongEnforcedScheduling;
    }

    public void setStrongEnforcedScheduling(boolean strongEnforcedScheduling) {
        this.strongEnforcedScheduling = strongEnforcedScheduling;
    }

    public int getEnforcingDataId() {
        return this.enforcingDataId;
    }

    public void setEnforcingDataId(int dataId) {
        this.enforcingDataId = dataId;
    }

    public long getInitialTimeStamp() {
        return initialTimeStamp;
    }

    public void setInitialTimeStamp(long time) {
        this.initialTimeStamp = time;
    }

    public void forceScheduling() {
        this.enforcedSceduling = true;
        this.strongEnforcedScheduling = false;
    }

    public void forceStrongScheduling() {
        this.enforcedSceduling = true;
        this.strongEnforcedScheduling = true;
    }

    public void unforceScheduling() {
        this.enforcedSceduling = false;
        this.strongEnforcedScheduling = false;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("[[Task id: ").append(taskId).append("]");
        buffer.append(", [Status: ").append(getStatus()).append("]");
        buffer.append(", ").append(getCore().toString()).append("]");

        return buffer.toString();
    }

    // Comparable interface implementation
    public int compareTo(Task task) throws NullPointerException, ClassCastException {
        if (task == null) {
            throw new NullPointerException();
        }

        return this.taskId - task.taskId;
    }
}
