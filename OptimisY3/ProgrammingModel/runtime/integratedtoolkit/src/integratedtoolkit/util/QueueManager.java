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
package integratedtoolkit.util;

import integratedtoolkit.components.impl.TaskScheduler;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.ScheduleState;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.LinkedList;
import integratedtoolkit.types.Task;
import java.util.Map.Entry;

/**
 * The QueueManager class is an utility to manage the schedule of all the
 * dependency-free tasks. It controls if they are running, if they have been
 * scheduled in a resource slot queue, if they failed on its previous execution
 * and must be rescheduled or if they have no resource where to run.
 *
 * There are many queues: - tasks without resource where to run - tasks to be
 * rescheduled - one queue for each slot of all the resources
 */
public class QueueManager {

    private ResourceManager resManager;
    private TaskScheduler TS;
    // Pending tasks
    /**
     * Tasks with no resource where they can be run
     */
    private static LinkedList<Task> noResourceTasks;
    /**
     * Amount of tasks per core that can't be run
     */
    private static int[] noResourceCount;
    /**
     * Task to be rescheduled
     */
    private static LinkedList<Task> tasksToReschedule;
    /**
     * Amount of tasks per core to be rescheduled
     */
    private static int[] toRescheduleCount;
    /**
     * First task to be executed for that method
     */
    private Task[] firstMethodExecution;
    /**
     * Average execution time per core
     */
    private Long[] coreAverageExecutionTime;
    /**
     * Executed Tasks per Core.
     */
    private int[] coreExecutedCount;
    /**
     * Amount of tasks waiting on queues per core.
     */
    private int[] waitingOnQueuesCores;
    /**
     * Resource name --> slot queues.
     */
    private TreeMap<String, ResourceQueue> nodeToSlotQueues;
    /**
     * List with the already created vms without connection.
     */
    private LinkedList<String> creating;
    /**
     * Number of task using a license.
     */
    private static HashMap<String, Integer> licensesRunning;
    /**
     * Which slots are pending to have free threads from their core licenses.
     */
    private static LinkedList<SlotReference> licenseToSleepingSlots;

    /**
     * Constructs a new QueueManager
     *
     * @param resManager ResourceManager associated to the manager
     * @param TS TaskScheduler associated to the manager
     */
    public QueueManager(ResourceManager resManager, TaskScheduler TS) {
        this.resManager = resManager;
        this.TS = TS;
        if (noResourceTasks == null) {
            noResourceTasks = new LinkedList<Task>();
        } else {
            noResourceTasks.clear();
        }


        noResourceCount = new int[Core.coreCount];

        if (tasksToReschedule == null) {
            tasksToReschedule = new LinkedList<Task>();
        } else {
            tasksToReschedule.clear();
        }

        toRescheduleCount = new int[Core.coreCount];

        if (nodeToSlotQueues == null) {
            nodeToSlotQueues = new TreeMap<String, ResourceQueue>();
        } else {
            for (String resource : nodeToSlotQueues.keySet()) {
                nodeToSlotQueues.get(resource).clear();
            }
        }

        if (coreAverageExecutionTime == null) {
            coreAverageExecutionTime = new Long[Core.coreCount];

            for (int i = 0; i < Core.coreCount; i++) {
                coreAverageExecutionTime[i] = null;
            }
        }

        coreExecutedCount = new int[Core.coreCount];
        waitingOnQueuesCores = new int[Core.coreCount];
        firstMethodExecution = new Task[Core.coreCount];

        // Set the maximum job count for all resources, to achieve a faster matching
        List<String> allResources = resManager.findResources(ResourceManager.ALL_RESOURCES, false);
        for (String res : allResources) {
            newNode(res, resManager.getMaxTaskCount(res));
        }
        this.creating = new LinkedList();
        licensesRunning = new HashMap<String, Integer>();
        for (String license : LicenseManager.getAllLicenses()) {
            licensesRunning.put(license, 0);
        }
        licenseToSleepingSlots = new LinkedList<SlotReference>();
    }

    /**
     * * SLOTS MANAGEMENT **
     */
    /**
     * Adds new slot queues to the a new managed resource
     *
     * @param resourceName Name of the resource
     * @param slots amount of slots for that resource
     */
    public void newNode(String resourceName, int slots) {
        nodeToSlotQueues.put(resourceName, new ResourceQueue(slots));
    }

    /**
     * Removes a resource to be managed
     *
     * @param resourceName name of the resource
     */
    public void removeNode(String resourceName) {
        nodeToSlotQueues.remove(resourceName);
    }

    /**
     * Checks if there is any pending modification (slots removal) for that
     * resource and if all the slot removals have been deleted
     *
     * @param resourceName name of the resource
     * @return true if there the current removal request has no more operations
     */
    public boolean mustModifyResource(String resourceName) {
        ResourceQueue rq = nodeToSlotQueues.get(resourceName);
        if (!rq.pendingRemovalsPerRequest.isEmpty()) {
            return rq.pendingRemovalsPerRequest.get(0) == 0;
        }
        return false;
    }

    /**
     * Notifies that the modification has been performed. it removes the current
     * slot removal request and informs to the Task Scheduler that this request
     * has been completed
     *
     * @param resourceName name of the resource which slots have been removed
     */
    public void notifyModification(String resourceName) {
        ResourceQueue rq = nodeToSlotQueues.get(resourceName);
        rq.pendingRemovalsPerRequest.remove(0);
    }

    /**
     * returns the amount of queues assciated to the resource
     *
     * @param resourceName name of the resource
     * @return amount of queues associated to the resource
     */
    public int getSafeSlotsCount(String resourceName) {
        ResourceQueue rq = nodeToSlotQueues.get(resourceName);
        return rq.slots;
    }

    /**
     * ** NO RESOURCE TASKs MANAGEMENT *****
     */
    /**
     * Adds a task to the queue of tasks with no resource
     *
     * @param t Task to be added
     */
    public void waitWithoutNode(Task t) {
        noResourceCount[t.getCore().getId()]++;
        noResourceTasks.add(t);
    }

    /**
     * Removes from the queue of tasks with no resources a set of tasks
     *
     * @param removedTasks List of tasks that must be removed
     */
    public void resourceFound(List<Task> removedTasks) {
        for (Task t : removedTasks) {
            noResourceCount[t.getCore().getId()]--;
        }
        noResourceTasks.removeAll(removedTasks);
    }

    /**
     * Checks if there is any tasks without resource
     *
     * @return true if there is some tasks on the queue of tasks without
     * resource
     */
    public boolean areTasksWithoutResource() {
        return !noResourceTasks.isEmpty();
    }

    /**
     * Gets the amount of task without resource that execute a specific core
     *
     * @param coreId identifier of the core
     * @return amount of task without resource that execute a specific core
     */
    public int getNoResourceCount(int coreId) {
        return noResourceCount[coreId];
    }

    /**
     * Returns the whole list of tasks without resource
     *
     * @return The whole list of tasks without resource
     */
    public List<Task> getPendingTasksWithoutNode() {
        return noResourceTasks;
    }

    /**
     * ** TO RESCHEDULE TASKs MANAGEMENT *****
     */
    /**
     * Adds a task to the queue of tasks to be rescheduled
     *
     * @param t Task to be added
     */
    public void newTaskToReschedule(Task t) {
        noResourceCount[t.getCore().getId()]++;
        tasksToReschedule.add(t);
    }

    /**
     * Removes from the queue of tasks to reschedule a set of tasks
     *
     * @param removedTasks List of tasks must be removed
     */
    public void rescheduledTask(Task t) {
        toRescheduleCount[t.getCore().getId()]--;
        tasksToReschedule.remove(t);
    }

    /**
     * Checks if there is any tasks to reschedule
     *
     * @return true if there is some tasks on the queue of tasks to reschedule
     */
    public boolean areTasksToReschedule() {
        return !tasksToReschedule.isEmpty();
    }

    /**
     * Gets the amount of task to be rescheduled that execute a specific core
     *
     * @param coreId identifier of the core
     * @return amount of task to be rescheduled that execute a specific core
     */
    public int getToRescheduleCount(int CoreId) {
        return toRescheduleCount[CoreId];
    }

    /**
     * Returns the whole list of tasks to reschedule
     *
     * @return The whole list of tasks to reschedule
     */
    public List<Task> getTasksToReschedule() {
        return tasksToReschedule;
    }

    /**
     * RESOURCE SLOTS QUEUE MANAGEMENT *
     */
    /**
     * Adds a task into the shortest slot queue of a resource
     *
     * @param t Task to schedule
     * @param resourceName resource where to schedule the task
     */
    public void waitOnNode(Task t, String resourceName) {
        waitingOnQueuesCores[t.getCore().getId()]++;
        nodeToSlotQueues.get(resourceName).waits(t);
    }

    /**
     * Moves a task from on slot queue to the shortest slot queue from another
     * resource
     *
     * @param t Task to move
     * @param oldResourceName Resource where the task is
     * @param newResourceName Resource where the task will be scheduled
     */
    public void moveFromNode(Task t, String oldResourceName, String newResourceName) {
        nodeToSlotQueues.get(oldResourceName).removeWaiting(t);
        nodeToSlotQueues.get(newResourceName).waits(t);
    }

    /**
     * The execution of a tasks starts at the specified resource
     *
     * @param t task which is running
     * @param resourceName resource where the task is being executed
     */
    public void startsExecution(Task t, String resourceName) {
        for (String license : LicenseManager.getCoreLicenses(t.getCore().getId())) {
            Integer running = licensesRunning.get(license);
            licensesRunning.put(license, running + 1);
        }
        nodeToSlotQueues.get(resourceName).executes(t);
        t.setInitialTimeStamp(System.currentTimeMillis());
        if (firstMethodExecution[t.getCore().getId()] == null) {
            firstMethodExecution[t.getCore().getId()] = t;
        }
    }

    /**
     * Updates the slot queue taking into account that the execution of a tasks
     * has ended at the specified resource
     *
     * @param task the tasks that has been run
     * @param resourceName resource where the task was being executed
     */
    public void endsExecution(Task task, String resourceName) {
        LinkedList<String> licenses = LicenseManager.getCoreLicenses(task.getCore().getId());
        for (String license : licenses) {
            int running = licensesRunning.get(license);
            licensesRunning.put(license, running - 1);
        }
        nodeToSlotQueues.get(resourceName).ends(task);
        long initialTime = task.getInitialTimeStamp();
        long duration = System.currentTimeMillis() - initialTime;
        int core = task.getCore().getId();
        Long mean = coreAverageExecutionTime[core];
        if (mean == null) {
            mean = 0l;
        }
        coreAverageExecutionTime[core] = ((mean * coreExecutedCount[core]) + duration) / (coreExecutedCount[core] + 1);
        coreExecutedCount[core]++;
    }

    /**
     * Updates the slot queue taking into account that the execution of a tasks
     * has failed at the specified resource
     *
     * @param task the failed task
     * @param resourceName name of the resource where it was running
     */
    public void cancelExecution(Task task, String resourceName) {
        LinkedList<String> licenses = LicenseManager.getCoreLicenses(task.getCore().getId());
        for (String license : licenses) {
            int running = licensesRunning.get(license);
            licensesRunning.put(license, running - 1);
        }
        nodeToSlotQueues.get(resourceName).ends(task);
    }

    /**
     * Checks if any slot of the resource is executing something
     *
     * @param resourceName name of the resource
     * @return true if all slots are idle
     */
    public boolean isExecuting(String resourceName) {
        return nodeToSlotQueues.get(resourceName).isExecuting();
    }

    /**
     * Checks if a slot of a host is executing any task
     *
     * @param host name of the resource
     * @param slot identifier of the slot
     * @return true if the slot has an assigned task and it is running
     */
    public boolean isExecuting(String host, int slot) {
        ResourceQueue hostqueue = nodeToSlotQueues.get(host);
        return hostqueue.onExecution[slot] != null;
    }

    /**
     * Gets a list of all the pending tasks in the slot queues of the specified
     * resource
     *
     * @param resourceName name of the resource
     * @return list of pending tasks on the resource
     */
    public List<Task> getPendingTasks(String resourceName) {
        return nodeToSlotQueues.get(resourceName).getAllPending();
    }

    /**
     * Looks for the next job that will be executed on the same slot where
     * oldTask ran.
     *
     * @param resourceName Name of the resource where task run
     * @param oldTask task that has leaved an empty space
     * @return
     */
    public Task getNextJob(String resourceName, Task oldTask) {
        return nodeToSlotQueues.get(resourceName).getNext(oldTask);
    }

    /**
     * Looks for the slot with a shorter queue within a set of resources
     *
     * @param resources List of resources to be analysed
     * @return the resource with the shortest slot queue
     */
    public String getMinQueueResource(List<String> resources) {
        long minWait = Long.MAX_VALUE;
        String bestResource = "";
        for (String res : resources) {
            long actualTime = nodeToSlotQueues.get(res).getMinWaitTime();
            if (actualTime < minWait) {
                minWait = actualTime;
                bestResource = res;
            }
        }
        return bestResource;
    }

    /**
     * Seeks and moves a certain amount of tasks of a specific core from one
     * slot queue to another one.
     *
     * @param sourceHost resourceName where the tasks are looked for
     * @param targetHost resourceName where the tasks are leaved
     * @param sourceSlot slot id where the tasks are taken
     * @param targetSlot slot id where the tasks are leaved
     * @param coreId core identifier of the tasks that are being moved
     * @param amount amount of tasks to move
     */
    public void seekAndMove(String sourceHost, String targetHost, int sourceSlot, int targetSlot, int coreId, int amount) {
        ResourceQueue source = nodeToSlotQueues.get(sourceHost);
        ResourceQueue target = nodeToSlotQueues.get(targetHost);
        LinkedList<Task> movedTasks = new LinkedList();
        int foundTasks = 0;
        for (Task t : source.queues[sourceSlot]) {
            if (foundTasks == amount) {
                break;
            }
            if (t.isEnforcedSceduling()) {
                continue;
            }
            if (coreId == t.getCore().getId()) {
                movedTasks.add(t);
                target.waits(t, targetSlot);
                foundTasks++;
            }
        }
        source.waitingTasksPerCore[sourceSlot][coreId] -= foundTasks;
        source.queues[sourceSlot].removeAll(movedTasks);
    }

    /**
     * Gets the first task on a certain slot queue of a resource
     *
     * @param host name of the resource
     * @param slot identifier of the slot
     * @return the first task on the slot queue
     */
    public Task getNextJobSlot(String host, int slot) {
        Boolean[] canRunCore = new Boolean[Core.coreCount];
        ResourceQueue hostqueue = nodeToSlotQueues.get(host);
        for (Task t : hostqueue.queues[slot]) {
            if (canRunCore[t.getCore().getId()] == null) {
                canRunCore[t.getCore().getId()] = canRunCore(t.getCore().getId());
                if (canRunCore[t.getCore().getId()]) {
                    return t;
                }
            }
            //if the canRunCore is not null canRunCore is false (it would have 
            //returned the previous task. If it can't run, it's not necessary to
            //chack anything else, we go for the next task
        }

        return null;
    }

    /**
     * Constructs a new ScheduleState and adds the description of the current
     * scheduling.
     *
     * @param ss current schedule state to be complemented
     */
    public ScheduleState getCurrentState() {
        ScheduleState ss = new ScheduleState();
        ss.coreMeanExecutionTime = new long[Core.coreCount];
        ss.waitingOnQueuesCores = waitingOnQueuesCores;
        ss.noResourcePerCore = new int[Core.coreCount];
        ss.noResource = noResourceTasks.size();
        ss.slotCountPerCore = new Integer[Core.coreCount];


        for (int i = 0; i < Core.coreCount; i++) {
            System.arraycopy(resManager.getProcessorsCount(), 0, ss.slotCountPerCore, 0, Core.coreCount);
        }
        System.arraycopy(noResourceCount, 0, ss.noResourcePerCore, 0, Core.coreCount);
        for (int i = 0; i < Core.coreCount; i++) {
            if (coreAverageExecutionTime[i] != null) {
                ss.coreMeanExecutionTime[i] = coreAverageExecutionTime[i];
            } else {
                if (firstMethodExecution[i] != null) {
                    //if any has started --> take the already spent time as the mean.
                    Long initTimeStamp = firstMethodExecution[i].getInitialTimeStamp();
                    if (initTimeStamp != null) {
                        //if the first task hasn't failed
                        long elapsedTime = System.currentTimeMillis() - initTimeStamp;
                        ss.coreMeanExecutionTime[i] = elapsedTime;
                    } else {
                        ss.coreMeanExecutionTime[i] = 100l;
                    }
                } else {
                    ss.coreMeanExecutionTime[i] = 100l;
                }
            }
        }


        for (Entry<String, ResourceQueue> e : nodeToSlotQueues.entrySet()) {
            ResourceQueue rq = e.getValue();
            Integer[] runningMethods = new Integer[rq.slots];
            long[] elapsedTime = new long[rq.slots];
            int[][] waitingCounts = new int[rq.slots][Core.coreCount];
            List<Integer> ableCores = resManager.getExecutableCores(e.getKey());
            for (int slotId = 0; slotId < rq.slots; slotId++) {
                if (rq.onExecution[slotId] == null) {
                    runningMethods[slotId] = null;
                    elapsedTime[slotId] = 0l;
                } else {
                    runningMethods[slotId] = rq.onExecution[slotId].getCore().getId();
                    elapsedTime[slotId] = System.currentTimeMillis() - rq.onExecution[slotId].getInitialTimeStamp();
                }
                System.arraycopy(rq.waitingTasksPerCore[slotId], 0, waitingCounts[slotId], 0, Core.coreCount);
            }
            ss.addWorker(e.getKey(), runningMethods, elapsedTime, waitingCounts, ableCores, rq.slotsToReduce);
        }
        return ss;
    }

    /**
     * Creates a description of the current schedule for all the resources. The
     * string pattern is described as follows: On execution: hostName1: taskId
     * taskId ... (all running tasks for hostName1) hostName2: taskId ... (all
     * running tasks for hostName2) ...
     *
     * Pending: taskId taskId taskId ... (all pending tasks in slots, to
     * reschedule or without resource)
     *
     * @return description of the current schedule state
     */
    public String describeCurrentState() {
        List<String> recursos = resManager.findResources(resManager.ALL_RESOURCES);
        String pending = "";
        String info = "\tOn execution:\n";
        for (String hostName : recursos) {
            info += "\t\t" + hostName + ":";
            for (int i = 0; i < nodeToSlotQueues.get(hostName).getOnExecution().size(); i++) {
                info += " " + nodeToSlotQueues.get(hostName).getOnExecution().get(i).getSchedulingTaskId() + "(" + nodeToSlotQueues.get(hostName).getOnExecution().get(i).getCore().getId() + ")";
            }
            info += "\n";
            for (int i = 0; i < nodeToSlotQueues.get(hostName).getAllPending().size(); i++) {
                pending += " " + nodeToSlotQueues.get(hostName).getAllPending().get(i).getSchedulingTaskId();
            }
        }

        for (int i = 0; i < noResourceTasks.size(); i++) {
            pending += " " + noResourceTasks.get(i).getSchedulingTaskId();
        }
        for (int i = 0; i < tasksToReschedule.size(); i++) {
            pending += " " + tasksToReschedule.get(i).getSchedulingTaskId();
        }
        return info + "\n\tPending:" + pending;
    }

    /**
     * Obtains the data that must be shown on the monitor
     *
     * @return String with core Execution information in an XML format
     */
    public String getMonitoringInfo() {
        StringBuilder sb = new StringBuilder("\t<CoresInfo>\n");
        for (int core = 0; core < Core.coreCount; core++) {
            sb.append("\t\t<Core id=\"").append(core).append("\">\n");
            if (coreAverageExecutionTime[core] != null) {
                sb.append("\t\t\t<MeanExecutionTime>").append((coreAverageExecutionTime[core] / 1000) + 1).append("</MeanExecutionTime>\n");
                sb.append("\t\t\t<MinExecutionTime>").append((coreAverageExecutionTime[core] / 1000) + 1).append("</MinExecutionTime>\n");
                sb.append("\t\t\t<MaxExecutionTime>").append((coreAverageExecutionTime[core] / 1000) + 1).append("</MaxExecutionTime>\n");
            } else {
                sb.append("\t\t\t<MeanExecutionTime>0</MeanExecutionTime>\n");
                sb.append("\t\t\t<MinExecutionTime>0</MinExecutionTime>\n");
                sb.append("\t\t\t<MaxExecutionTime>0</MaxExecutionTime>\n");
            }
            sb.append("\t\t\t<ExecutedCount>").append(coreExecutedCount[core]).append("</ExecutedCount>\n");
            sb.append("\t\t</Core>\n");
        }
        sb.append("\t</CoresInfo>\n");
        sb.append("\t<Licenses>\n");
        for (String license : LicenseManager.getAllLicenses()) {
            sb.append("\t\t<License name=\"" + license + "\">\n");
            sb.append("\t\t\t<Using>" + licensesRunning.get(license) + "</Using>\n");
            sb.append("\t\t\t<Threads>" + LicenseManager.getLicenseThreads(license) + "</Threads>\n");
            sb.append("\t\t</License>\n");
        }
        sb.append("\t</Licenses>\n");
        sb.append("\t<ResourceInfo>\n");
        for (java.util.Map.Entry<String, ResourceQueue> entry : nodeToSlotQueues.entrySet()) {
            sb.append("\t\t<Resource id=\"").append(entry.getKey()).append("\">\n");
            if (entry.getValue().slotsToReduce > 0) {
                sb.append("\t\t\t<Status>Removing</Status>\n");
            } else {
                sb.append("\t\t\t<Status>Ready</Status>\n");
            }
            ResourceQueue rq = entry.getValue();
            for (int slot = 0; slot < rq.onExecution.length; slot++) {
                if (rq.onExecution[slot] == null) {
                    sb.append("\t\t\t<Slot id=\"").append(slot).append("\" />\n");
                } else {
                    sb.append("\t\t\t<Slot id=\"").append(slot).append("\">").append(rq.onExecution[slot].getSchedulingTaskId()).append("</Slot>\n");
                }
            }
            sb.append("\t\t</Resource>\n");
        }
        for (String VMName : this.creating) {
            sb.append("\t\t<Resource id=\"").append(VMName).append("\">\n");
            sb.append("\t\t\t<Status>Waiting</Status>\n");
            sb.append("\t\t</Resource>\n");
        }
        sb.append("\t</ResourceInfo>\n");
        return sb.toString();
    }

    public void removeSlots(String name) {
        ResourceQueue rq = nodeToSlotQueues.get(name);
        int slots = rq.deleteSlots();
    }

    public void setWaitingVMs(LinkedList<HashMap<String, Object>> temporary) {
        this.creating = new LinkedList<String>();
        for (HashMap<String, Object> machine : temporary) {
            this.creating.add((String) machine.get("ip"));
        }
    }

    /**
     * Checks if a core fulfills the licenses to be run
     *
     * @param coreId identifier of the core
     * @return true if the core can be run at that moment
     */
    public boolean canRunCore(int coreId) {
        boolean canRun = true;
        LinkedList<String> licenses = LicenseManager.getCoreLicenses(coreId);
        for (String license : licenses) {
            int running = licensesRunning.get(license);
            int threads = LicenseManager.getLicenseThreads(license);
            if (running == threads) {
                canRun = false;
                break;
            }
        }
        return canRun;
    }

    /**
     * Returns the number of slots of a given resource
     *
     * @param resource name of the resource
     * @return number of slots
     */
    public int getSlotCount(String resource) {
        return nodeToSlotQueues.get(resource).slots;
    }

    public void resizeDataStructures() {
        int[] noResourceCountTmp = new int[Core.coreCount];
        System.arraycopy(noResourceCount, 0, noResourceCountTmp, 0, noResourceCount.length);
        noResourceCount = noResourceCountTmp;

        int[] toRescheduleCountTmp = new int[Core.coreCount];
        System.arraycopy(toRescheduleCount, 0, noResourceCountTmp, 0, toRescheduleCount.length);
        toRescheduleCount = toRescheduleCountTmp;

        Long[] coreAverageExecutionTimeTmp = new Long[Core.coreCount];
        System.arraycopy(coreAverageExecutionTime, 0, coreAverageExecutionTimeTmp, 0, coreAverageExecutionTime.length);
        coreAverageExecutionTime = coreAverageExecutionTimeTmp;

        int[] coreExecutedCountTmp = new int[Core.coreCount];
        System.arraycopy(coreExecutedCount, 0, coreExecutedCountTmp, 0, coreExecutedCount.length);
        coreExecutedCount = coreExecutedCountTmp;

        int[] waitingOnQueuesCoresTmp = new int[Core.coreCount];
        System.arraycopy(waitingOnQueuesCores, 0, waitingOnQueuesCoresTmp, 0, waitingOnQueuesCores.length);
        waitingOnQueuesCores = waitingOnQueuesCoresTmp;

        Task[] firstMethodExecutionTmp = new Task[Core.coreCount];
        System.arraycopy(firstMethodExecution, 0, firstMethodExecutionTmp, 0, firstMethodExecution.length);
        firstMethodExecution = firstMethodExecutionTmp;

        for (ResourceQueue rq : nodeToSlotQueues.values()) {
            for (int slot = 0; slot < rq.waitingTasksPerCore.length; slot++) {
                int[] temp = new int[Core.coreCount];
                System.arraycopy(rq.waitingTasksPerCore[slot], 0, temp, 0, rq.waitingTasksPerCore[slot].length);
                rq.waitingTasksPerCore[slot] = temp;
            }
        }
    }

    class ResourceQueue {

        /**
         * Amount of slots for the resource
         */
        int slots;
        /**
         * Amont of tasks being executed
         */
        int executing = 0;
        /**
         * Task Queue for each slot
         */
        LinkedList<Task>[] queues;
        /**
         * Amount of tasks waiting on the queue per slot & core
         */
        int[][] waitingTasksPerCore;
        /**
         * task that is running on each slots
         */
        Task[] onExecution;
        /**
         * total amount of slots to be reduced for that resource
         */
        int slotsToReduce = 0;
        /**
         * List of pending slots removal per request
         */
        LinkedList<Integer> pendingRemovalsPerRequest;

        ResourceQueue(int slots) {
            this.slots = slots;
            this.waitingTasksPerCore = new int[slots][Core.coreCount];
            this.queues = new LinkedList[slots];
            this.onExecution = new Task[slots];
            this.executing = 0;
            this.slotsToReduce = 0;
            pendingRemovalsPerRequest = new LinkedList();
            for (int i = 0; i < slots; i++) {
                this.queues[i] = new LinkedList<Task>();
                this.onExecution[i] = null;
            }
        }

        public void clear() {
            this.executing = 0;
            this.waitingTasksPerCore = new int[slots][Core.coreCount];
            for (int i = 0; i < slots; i++) {
                this.queues[i] = new LinkedList<Task>();
                this.onExecution[i] = null;
            }
        }

        private int deleteSlots() {
            LinkedList<Integer> removableSlots = new LinkedList();
            if (slots == executing) {
                slotsToReduce += slots;
                pendingRemovalsPerRequest.add(slots);
                return slots;
            }
            int i;
            for (i = 0; i < this.slots; i++) {
                if (onExecution[i] == null) {
                    removableSlots.add(i);
                }
            }
            deleteSlots(removableSlots);
            slotsToReduce += slots;
            pendingRemovalsPerRequest.add(slots);
            return slots;
        }

        private void deleteSlot(int slotId) {
            int[][] waitingMethods = new int[slots - 1][Core.coreCount];
            LinkedList<Task>[] queues = new LinkedList[slots - 1];
            Task[] onExecution = new Task[slots - 1];

            for (int i = 0, j = 0; i < slots; i++) {
                if (slotId == i) {
                    continue;
                }
                waitingMethods[j] = this.waitingTasksPerCore[i];
                queues[j] = this.queues[i];
                onExecution[j] = this.onExecution[i];
                j++;
            }
            if (slots - 1 > 0) {
                int i = 0;
                for (Task t : this.queues[slotId]) {
                    queues[i % queues.length].add(t);
                    waitingMethods[i % queues.length][t.getCore().getId()]++;
                }
            } else {
                for (Task t : this.queues[slotId]) {
                    List<String> compatibleResources = resManager.findResources(t.getCore().getId());
                    if (compatibleResources.size() > 0) {
                        String resource = compatibleResources.get(0);
                        nodeToSlotQueues.get(resource).waits(t);
                    } else {
                        noResourceCount[t.getCore().getId()]++;
                        noResourceTasks.add(t);
                    }
                }
            }
            this.waitingTasksPerCore = waitingMethods;
            this.queues = queues;
            this.onExecution = onExecution;
            this.slots--;
        }

        private void deleteSlots(LinkedList<Integer> removableSlots) {
            if (removableSlots.isEmpty()) {
                return;
            }
            int removeCount = removableSlots.size();
            int[][] waitingMethods = new int[slots - removeCount][Core.coreCount];
            LinkedList<Task>[] queues = new LinkedList[slots - removeCount];
            Task[] onExecution = new Task[slots - removeCount];
            int i = 0;
            int j = 0;

            for (int removedSlot : removableSlots) {
                while (i != removedSlot) {
                    waitingMethods[j] = this.waitingTasksPerCore[i];
                    queues[j] = this.queues[i];
                    onExecution[j] = this.onExecution[i];
                    i++;
                    j++;
                }
                i++;
            }

            for (; i < slots; i++) {
                waitingMethods[j] = this.waitingTasksPerCore[i];
                queues[j] = this.queues[i];
                onExecution[j] = this.onExecution[i];
                j++;
            }

            for (int slotId : removableSlots) {
                if (slots - removeCount > 0) {
                    for (Task t : this.queues[slotId]) {
                        queues[i % queues.length].add(t);
                        waitingMethods[i % queues.length][t.getCore().getId()]++;
                    }
                } else {
                    for (Task t : this.queues[slotId]) {
                        String resource = resManager.findResources(t.getCore().getId()).get(0);
                        nodeToSlotQueues.get(resource).waits(t);
                    }
                }
            }

            this.waitingTasksPerCore = waitingMethods;
            this.queues = queues;
            this.onExecution = onExecution;

            this.slots -= removeCount;
        }

        public void waits(Task t) {
            int minIndex = 0;
            long minTime = Long.MAX_VALUE;
            for (int i = 0; i < slots; i++) {
                long waitingTime = getSlotWaitingTime(i, coreAverageExecutionTime);
                if (waitingTime < minTime) {
                    minIndex = i;
                    minTime = waitingTime;
                }
            }
            queues[minIndex].add(t);
            waitingTasksPerCore[minIndex][t.getCore().getId()]++;
        }

        public void waits(Task t, int slotId) {
            queues[slotId].add(t);
            waitingTasksPerCore[slotId][t.getCore().getId()]++;
        }

        public void removeWaiting(Task t) {
            int foundIndex = 0;
            boolean found = false;
            for (int i = 0; i < slots & !found; i++) {
                if (queues[i].remove(t)) {
                    found = true;
                    foundIndex = i;
                }
            }
            waitingTasksPerCore[foundIndex][t.getCore().getId()]--;
        }

        public void executes(Task t) {
            this.executing++;
            int foundIndex = 0;
            boolean found = false;
            for (int i = 0; i < slots & !found; i++) {
                if (queues[i].remove(t)) {
                    found = true;
                    foundIndex = i;
                    waitingTasksPerCore[foundIndex][t.getCore().getId()]--;
                }
            }

            for (int i = 0; i < slots; i++) {
                if (onExecution[(foundIndex + i) % slots] == null) {
                    onExecution[(foundIndex + i) % slots] = t;
                    return;
                }
            }
        }

        public void ends(Task task) {
            this.executing--;
            for (int i = 0; i < slots; i++) {
                if (onExecution[i] != null && (onExecution[i].getSchedulingTaskId()) == task.getSchedulingTaskId()) {
                    onExecution[i] = null;
                    if (slotsToReduce > 0) {
                        deleteSlot(i);
                        slotsToReduce--;
                        Integer slotCount = pendingRemovalsPerRequest.remove(0);
                        slotCount--;
                        pendingRemovalsPerRequest.addFirst(slotCount);
                    }
                }
            }
        }

        public List<Task> getOnExecution() {
            List<Task> ret = new LinkedList<Task>();
            for (int i = 0; i < slots; i++) {
                if (onExecution[i] != null) {
                    ret.add(onExecution[i]);
                }
            }
            return ret;
        }

        public List<Task> getAllPending() {
            List<Task> ret = new LinkedList<Task>();
            for (int i = 0; i < slots; i++) {
                ret.addAll(queues[i]);
            }
            return ret;
        }

        public Task getNext(Task oldTask) {

            Integer executedSlot = null;
            //Find which slot has run the oldTask or is free
            for (int i = 0; i < slots; i++) {
                if (onExecution[i] != null && onExecution[i].getSchedulingTaskId() == oldTask.getSchedulingTaskId()) {
                    executedSlot = i;
                }
            }
            if (executedSlot == null) {
                for (int i = 0; i < slots; i++) {
                    if (onExecution[i] == null) {
                        executedSlot = i;
                    }
                }
            }

            //If all slots are busy, no task to execute (:S)
            if (executedSlot == null) {
                return null;
            }

            Boolean[] canRunCore = new Boolean[Core.coreCount];

            //Select the best task
            for (int i = 0; i < slots; i++) {
                //First on the same slot queue
                int slot = (executedSlot + i) % slots;
                for (Task t : queues[slot]) {
                    if (canRunCore[t.getCore().getId()] == null) {
                        boolean canRun = canRunCore(t.getCore().getId());
                        canRunCore[t.getCore().getId()] = canRun;
                        if (canRun) {
                            return t;
                        }// if cannot be run, try next task on the queue
                    } else if (canRunCore[t.getCore().getId()]) {
                        return t;
                    }//if cannot be run, try next task on the queue
                }
            }

            return null;
        }

        public long getMinWaitTime() {
            long minTime = 0l;
            for (int i = 0; i < slots; i++) {
                long waitingTime = getSlotWaitingTime(i, coreAverageExecutionTime);
                if (minTime > waitingTime) {
                    minTime = waitingTime;
                }
            }
            return minTime;
        }

        public long getMaxWaitTime() {
            long maxTime = 0l;
            for (int i = 0; i < slots; i++) {
                long waitingTime = getSlotWaitingTime(i, coreAverageExecutionTime);
                if (maxTime < waitingTime) {
                    maxTime = waitingTime;
                }
            }
            return maxTime;
        }

        private long getSlotWaitingTime(int slotId, Long[] coreAverageExecutionTime) {

            long waitingTime = 0l;
            long now = System.currentTimeMillis();

            //No hi ha res en execució
            if (onExecution[slotId] == null) {
                return 0;
            }

            //Temps de la tasca en execucio
            Task t = onExecution[slotId];
            Long coreTime = coreAverageExecutionTime[t.getCore().getId()];
            if (coreTime == null) {
                if (firstMethodExecution[t.getCore().getId()] != null) {
                    Long elapsedTime = firstMethodExecution[t.getCore().getId()].getInitialTimeStamp();
                    if (elapsedTime != null) {
                        coreTime = now - firstMethodExecution[t.getCore().getId()].getInitialTimeStamp();
                    } else {
                        coreTime = 1l;
                    }
                } else {
                    coreTime = 1l;
                }
            }
            coreTime -= (now - t.getInitialTimeStamp());
            if (coreTime < 0) {
                coreTime = 0l;
            }
            waitingTime = coreTime;
            //Temps de les tasques en espera
            for (int coreIndex = 0; coreIndex < Core.coreCount; coreIndex++) {
                long meanTime = 0l;
                if (coreAverageExecutionTime[coreIndex] == null) {
                    if (firstMethodExecution[coreIndex] != null) {
                        meanTime = now - firstMethodExecution[coreIndex].getInitialTimeStamp();
                    } else {
                        meanTime = 1l;
                    }
                } else {
                    meanTime = coreAverageExecutionTime[coreIndex];
                }
                waitingTime += waitingTasksPerCore[slotId][coreIndex] * meanTime;
            }
            return waitingTime;

        }

        public boolean isExecuting() {
            return executing > 0;
        }
    }

    class SlotReference {

        ResourceQueue rq;
        int slot;
    }
}
