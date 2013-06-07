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
package integratedtoolkit.components.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import integratedtoolkit.ITConstants;
import integratedtoolkit.api.ITExecution.ParamDirection;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.ExecutionParams;
import integratedtoolkit.types.Parameter;
import integratedtoolkit.types.ServiceInstance;
import integratedtoolkit.types.Parameter.*;
import integratedtoolkit.types.ProjectWorker;
import integratedtoolkit.types.Task;
import integratedtoolkit.types.data.DataInstanceId;
import integratedtoolkit.types.data.DataAccessId.*;
import integratedtoolkit.util.ConstraintManager;
import integratedtoolkit.util.ProjectManager;
import integratedtoolkit.util.ResourceManager;
import integratedtoolkit.types.ResourceDescription;
import integratedtoolkit.types.ScheduleDecisions;
import integratedtoolkit.types.ScheduleState;
import integratedtoolkit.util.LicenseManager;
import integratedtoolkit.util.QueueManager;

public class TaskScheduler {

    // Constants definition
    private static final String RES_LOAD_ERR = "Error loading resource information";
    private static final String PROJ_LOAD_ERR = "Error loading project information";
    private static final String CREAT_INIT_VM_ERR = "Error creating initial VMs";
    private static final String DEL_VM_ERR = "Error deleting VMs";
    // Components
    private JobManager JM;
    private FileTransferManager FTM;
    // Object that stores the information about all available resources
    private ResourceManager resManager;
    // Object that stores the information about the current project
    private QueueManager queueManager;
    // Component logger - No need to configure, ProActive does
    private static final Logger monitor = Logger.getLogger(Loggers.RESOURCES);
    private static final boolean monitorDebug = monitor.isDebugEnabled();
    private static final Logger logger = Logger.getLogger(Loggers.TS_COMP);
    private static final boolean debug = logger.isDebugEnabled();
    //Number of Tasks to execute
    private static Map<Integer, Integer> taskCountToEnd;
    private boolean endRequested;
    // Preschedule
    private static final boolean presched = System.getProperty(ITConstants.IT_PRESCHED) != null
            && System.getProperty(ITConstants.IT_PRESCHED).equals("true")
            ? true : false;
    // Constants definition
    private LinkedList<String> savedMachines;
    private static HashMap<String, Integer> requestsPerLicense;

    public TaskScheduler() {
        endRequested = false;

        if (!ProjectManager.isInit()) {
            try {
                ProjectManager.init();
            } catch (Exception e) {
                logger.fatal(PROJ_LOAD_ERR, e);
                System.exit(1);
            }
        }

        try {
            resManager = new ResourceManager();
        } catch (ClassNotFoundException e) {
            logger.fatal(CREAT_INIT_VM_ERR, e);
            System.exit(1);
        } catch (Throwable e) {
            logger.fatal(RES_LOAD_ERR, e);
            System.exit(1);
        }

        queueManager = new QueueManager(resManager, this);
        for (int method_i = 0; method_i < Core.coreCount; method_i++) {
            resManager.linkCoreToMachines(method_i);
        }

        resManager.printCoresResourcesLinks();

        taskCountToEnd = new HashMap<Integer, Integer>();

        logger.info("Initialization finished");
        monitor.info("APP START:" + System.currentTimeMillis());
        requestsPerLicense = new HashMap<String, Integer>();
        for (String license : LicenseManager.getAllLicenses()) {
            requestsPerLicense.put(license, 0);
        }
        savedMachines = new LinkedList<String>();
    }

    public void setCoWorkers(JobManager JM, FileTransferManager FTM) {
        this.JM = JM;
        this.FTM = FTM;
        resizeDataStructures();
    }

    public List<ServiceInstance> getServiceInstances() {
        return resManager.getServices();
    }

    public void cleanup() {
        // Stop all Cloud VM
        try {
            resManager.stopVirtualNodes();
        } catch (Exception e) {
            logger.error(ITConstants.TS + ": " + DEL_VM_ERR, e);
        }
        logger.info("Cleanup done");
    }

    public void addCloudNode(HashMap<String, Object> machine) {
        String name = (String) machine.get("resourceName");
        LinkedList<Integer> methodIds = (LinkedList<Integer>) machine.get("cores");
        int slots = 0;
        if (presched) {
            slots = (Integer) machine.get("slots") * 2;
        } else {
            slots = (Integer) machine.get("slots");
        }
        resManager.addResource(name, methodIds, slots, (HashMap<String, String>) machine.get("sharedDisks"));
        queueManager.newNode(name, slots);
        ProjectWorker pw = new ProjectWorker(name, (String) machine.get("user"), slots, (String) machine.get("iDir"), (String) machine.get("wDir"));
        ProjectManager.addProjectWorker(pw);

        // Assign task to execute
        Boolean chosen = true;
        int alreadyAssigned = 0;


        //Rescheduled
        for (int i = 0; i < slots && chosen; i++) {
            if (queueManager.areTasksToReschedule()) {
                Task chosenTask = assignRescheduledTask(name);
                if (chosenTask != null) {
                    // Task rescheduled
                    if (debug) {
                        logger.debug("Freed Re-Match: Task(" + chosenTask.getSchedulingTaskId() + ", "
                                + chosenTask.getCore().getName() + ") "
                                + "Resource(" + name + ")");
                    }

                    queueManager.rescheduledTask(chosenTask);
                    sendJob(chosenTask, name, true);
                    alreadyAssigned++;
                } else {
                    chosen = false;
                }
            }
        }
        //Check if can execute Blocked Tasks
        LinkedList<Task> removedTasks = new LinkedList<Task>();

        if (queueManager.areTasksWithoutResource()) {
            for (Task t : queueManager.getPendingTasksWithoutNode()) {
                if (resManager.matches(name, t.getCore().getId())) {
                    if (alreadyAssigned < slots) {
                        if (debug) {
                            logger.debug("Unblocked Match: Task(" + t.getSchedulingTaskId() + ", "
                                    + t.getCore().getName() + ") "
                                    + "Resource(" + name + ")");
                        }
                        sendJob(t, name, false);
                    } else {
                        if (debug) {
                            logger.debug("Unblocked Pending: Task(" + t.getSchedulingTaskId() + ", "
                                    + t.getCore().getName() + ") "
                                    + "Resource(" + name + ")");
                        }
                        queueManager.waitOnNode(t, name);
                    }
                    removedTasks.add(t);
                    alreadyAssigned++;
                }
            }
        }
        queueManager.resourceFound(removedTasks);

        if (monitorDebug) {
            monitor.debug(queueManager.describeCurrentState());
        }
    }

    public void refuseCloudWorkerRequest(int oldTaskCount, ResourceDescription gR) {
        List<Integer> methodsId = ConstraintManager.findExecutableCores(gR);
        resManager.refuseCloudWorker(oldTaskCount, methodsId);
    }

    public void notifySafeCopyEnd(String hostName) {
        queueManager.removeNode(hostName);
        savedMachines.add(hostName);
    }

    // Schedule interface
    public void scheduleTask(Task currentTask) {
        LinkedList<String> licenses = LicenseManager.getCoreLicenses(currentTask.getCore().getId());
        Integer reqs = 0;
        for (String license : licenses) {
            reqs = requestsPerLicense.get(license);
            if (reqs == null) {
                reqs = new Integer(0);
            }
            requestsPerLicense.put(license, reqs + 1);
        }
        String chosenResource = "";
        currentTask.unforceScheduling();
        // Schedule task
        List<String> validResources = resManager.findResources(currentTask.getCore().getId());
        if (validResources.isEmpty()) {
            //There's no point on getting scores, any existing machines can run this task <- score=0
            queueManager.waitWithoutNode(currentTask);
            if (debug) {
                logger.debug("Blocked: Task(" + currentTask.getSchedulingTaskId() + ", "
                        + currentTask.getCore().getName() + ") ");
            }
        } else {
            HashMap<String, Integer> hostToScore = new HashMap<String, Integer>(validResources.size());
            for (Parameter p : currentTask.getCore().getParameters()) {
                DataInstanceId dId = null;
                if (p instanceof DependencyParameter && p.getDirection() != ParamDirection.OUT) {
                    DependencyParameter dp = (DependencyParameter) p;
                    switch (dp.getDirection()) {
                        case IN:
                            RAccessId raId = (RAccessId) dp.getDataAccessId();
                            dId = raId.getReadDataInstance();
                            break;
                        case INOUT:
                            RWAccessId rwaId = (RWAccessId) dp.getDataAccessId();
                            dId = rwaId.getReadDataInstance();
                            break;
                        case OUT:
                            continue;
                    }
                }
                if (dId != null) {
                    TreeSet<String> hosts = FTM.getHosts(dId);
                    if (currentTask.isEnforcedSceduling()) {
                        if (dId.getDataId() == currentTask.getEnforcingDataId()) {
                            int maxScore = -1;
                            for (String host : hosts) {
                                Integer score;
                                if ((score = hostToScore.get(host)) == null) {
                                    score = new Integer(0);
                                    hostToScore.put(host, score);
                                }
                                hostToScore.put(host, score + 1);
                                if (score > maxScore) {
                                    maxScore = score;
                                    chosenResource = host;
                                }
                            }
                        }
                    } else {
                        for (String host : hosts) {
                            Integer score;
                            if ((score = hostToScore.get(host)) == null) {
                                score = new Integer(0);
                                hostToScore.put(host, score);
                            }
                            hostToScore.put(host, score + 1);
                        }
                    }
                }
            }


            if (currentTask.isEnforcedSceduling()) {
                //Task is forced to run in this resource
                if (resManager.hasFreeSlots(chosenResource, presched) && queueManager.canRunCore(currentTask.getCore().getId())) {
                    if (debug) {
                        logger.debug("Match: Task(" + currentTask.getSchedulingTaskId() + ", "
                                + currentTask.getCore().getName() + ") "
                                + "Resource(" + chosenResource + ")");
                    }
                    // Request the creation of a job for the task
                    sendJob(currentTask, chosenResource, false);
                } else {
                    queueManager.waitOnNode(currentTask, chosenResource);
                    if (debug) {
                        logger.debug("Pending: Task(" + currentTask.getSchedulingTaskId() + ", "
                                + currentTask.getCore().getName() + ") "
                                + "Resource(" + chosenResource + ")");
                    }
                }
            } else {
                // Try to assign task to available resources
                List<String> resources = resManager.findResources(currentTask.getCore().getId(), presched);

                if (debug) {
                    StringBuilder sb = new StringBuilder("Available suitable resources for task ");
                    sb.append(currentTask.getSchedulingTaskId()).append(", ").append(currentTask.getCore().getName()).append(":");
                    for (String s : resources) {
                        sb.append(" ").append(s);
                    }

                    logger.debug(sb);
                }

                if (!resources.isEmpty() && queueManager.canRunCore(currentTask.getCore().getId())) {
                    int maxScore = Integer.MIN_VALUE;
                    for (String resource : resources) {
                        Integer score = hostToScore.get(resource);
                        if (score == null) {
                            score = 0;
                        }
                        if (score > maxScore) {
                            chosenResource = resource;
                            maxScore = score;
                        }
                    }
                    if (debug) {
                        logger.debug("Match: Task(" + currentTask.getSchedulingTaskId() + ", "
                                + currentTask.getCore().getName() + ") "
                                + "Resource(" + chosenResource + ")");
                    }
                    // Request the creation of a job for the task
                    sendJob(currentTask, chosenResource, false);
                } else {
                    chosenResource = queueManager.getMinQueueResource(validResources);
                    queueManager.waitOnNode(currentTask, chosenResource);
                    if (debug) {
                        logger.debug("Pending: Task(" + currentTask.getSchedulingTaskId() + ", "
                                + currentTask.getCore().getName() + ") "
                                + "Resource(" + chosenResource + ")");
                    }
                }

                if (monitorDebug) {
                    monitor.debug(queueManager.describeCurrentState());
                }
            }
        }
    }

    public void reschedule(Task task) {
        // Get the corresponding task to reschedule

        int taskId = task.getSchedulingTaskId();
        String failedResource = task.getExecParams().getHost();
        logger.debug("Reschedule: Task " + taskId + " failed to run in " + failedResource);

        //notifyJobEnd(oldJobId, JobEndStatus.TO_RESCHEDULE, null);
        notifyEnd(task);

        // Find available resources that match user constraints for this task
        List<String> resources = resManager.findResources(task.getCore().getId(), presched);

        /* Get the host where the task failed and remove it from the list
         * so that it will not be chosen again
         */
        // Notify the job end to free the resource, in case another task can be scheduled in it
        resources.remove(failedResource);

        // Reschedule task
        if (!resources.isEmpty()) {
            String newResource = assignTaskToBestResource(task, resources);
            if (newResource == null) {
                return;
            }
            if (debug) {
                logger.debug("Re-Match: Task(" + task.getSchedulingTaskId() + ", "
                        + task.getCore().getName() + ") "
                        + "Resource(" + newResource + ")");
            }

            // Request the creation of a job for the task
            sendJob(task, newResource, true);
        } else {
            queueManager.newTaskToReschedule(task);
            if (debug) {
                logger.debug("To Reschedule: Task(" + task.getSchedulingTaskId() + ", "
                        + task.getCore().getName() + ") ");
            }
        }

    }

    private void sendJob(Task task, String resource, boolean rescheduled) {
        String installDir = ProjectManager.getResourceProperty(resource, ITConstants.INSTALL_DIR);
        String workingDir = ProjectManager.getResourceProperty(resource, ITConstants.WORKING_DIR);
        String user = ProjectManager.getResourceProperty(resource, ITConstants.USER);

        // Prepare the execution parameters
        // TODO: Cost
        ExecutionParams execParams = new ExecutionParams(user,
                resource,
                installDir,
                workingDir);

        resManager.reserveResource(resource);
        queueManager.startsExecution(task, resource);
        // Request the creation of the job
        task.setExecParams(execParams);
        if (rescheduled) {
            JM.jobRescheduled(task);
        } else {
            JM.newJob(task);
        }
    }

    public void notifyEnd(Task task) {
        // Obtain freed resource
        String hostName = task.getExecParams().getHost();
        // Tell the resource manager that the resource is free
        resManager.freeResource(hostName);

        switch (task.getStatus()) {
            case FINISHED:
                LinkedList<String> licenses = LicenseManager.getCoreLicenses(task.getCore().getId());
                for (String license : licenses) {
                    Integer reqs = requestsPerLicense.get(license);
                    requestsPerLicense.put(license, reqs - 1);
                }
                for (Parameter p : task.getCore().getParameters()) {
                    if (p instanceof DependencyParameter && p.getDirection() != ParamDirection.OUT) {
                        DependencyParameter dp = (DependencyParameter) p;
                        DataInstanceId dId = null;
                        switch (dp.getDirection()) {
                            case IN:
                                RAccessId raId = (RAccessId) dp.getDataAccessId();
                                dId = raId.getReadDataInstance();
                                break;
                            case INOUT:
                                RWAccessId rwaId = (RWAccessId) dp.getDataAccessId();
                                dId = rwaId.getReadDataInstance();
                                break;
                            case OUT:
                                continue;
                        }
                    }
                }
                int coreId = task.getCore().getId();
                Integer coreCount = taskCountToEnd.get(coreId);
                if (coreCount != null) {
                    taskCountToEnd.put(coreId, coreCount - 1);
                }
                queueManager.endsExecution(task, hostName);
                break;
            case TO_RESCHEDULE:
                queueManager.cancelExecution(task, hostName);
                break;
            case FAILED:
                queueManager.cancelExecution(task, hostName);
                break;
            default: //This Task should not be here
                logger.fatal("INVALID KIND OF TASK ENDED: " + task.getStatus());
                System.exit(1);
                break;
        }
        if (queueManager.mustModifyResource(hostName)) {
            queueManager.notifyModification(hostName);
        }
        //Check if resource must be powered off
        if (queueManager.getSafeSlotsCount(hostName) == 0) {
            FTM.transferStopFiles(hostName, resManager.getBestSafeResourcePerCore());
            return; // resource shut down, no need to continue looking for a task
        }

        // First check if there is some task to reschedule
        if (queueManager.areTasksToReschedule()) {
            Task chosenTask = assignRescheduledTask(hostName);

            if (chosenTask != null) {
                // Task rescheduled
                if (debug) {
                    logger.debug("Freed Re-Match: Task(" + chosenTask.getSchedulingTaskId() + ", "
                            + chosenTask.getCore().getName() + ") "
                            + "Resource(" + hostName + ")");
                }

                queueManager.rescheduledTask(chosenTask);
                sendJob(chosenTask, hostName, true);
                wakeupSleepingSlots(task.getCore().getId());
                return;
            }
        }

        Task[] chosenTask = new Task[1];
        // Now assign, if possible, one of the pending tasks to the resource
        chosenTask[0] = queueManager.getNextJob(hostName, task);

        if (chosenTask[0] == null) {
            chosenTask = stealTaskFromOtherResourcesToExecute(hostName, 1);
            if (debug && chosenTask[0] != null) {
                logger.debug("Moved Match: Task(" + chosenTask[0].getSchedulingTaskId() + ", "
                        + chosenTask[0].getCore().getName() + ") "
                        + "Resource(" + hostName + ")");
            }
        } else {
            if (debug) {
                logger.debug("Freed Match: Task(" + chosenTask[0].getSchedulingTaskId() + ", "
                        + chosenTask[0].getCore().getName() + ") "
                        + "Resource(" + hostName + ")");
            }
        }

        if (chosenTask[0] != null) {
            sendJob(chosenTask[0], hostName, false);
        } else {
            if (debug) {
                logger.debug("Resource " + hostName + " FREE");
            }
        }

        wakeupSleepingSlots(task.getCore().getId());

        if (monitorDebug) {
            monitor.debug(queueManager.describeCurrentState());
        }
    }

    private void wakeupSleepingSlots(int coreId) {
        TreeSet<String> resources = new TreeSet<String>();
        Boolean[] canRun = new Boolean[Core.coreCount];
        LinkedList<String> licenses = LicenseManager.getCoreLicenses(coreId);
        for (String license : licenses) {
            LinkedList<Integer> cores = LicenseManager.getCores(license);
            for (Integer core : cores) {
                if (canRun[core] == null) {
                    canRun[core] = queueManager.canRunCore(core);
                    if (canRun[core]) {
                        for (String resource : resManager.findResources(coreId)) {
                            if (!resources.contains(resource)) {
                                int slots = queueManager.getSlotCount(resource);
                                for (int slotId = 0; slotId < slots; slotId++) {
                                    if (!queueManager.isExecuting(resource, slotId)) {
                                        Task t = queueManager.getNextJobSlot(resource, slotId);
                                        if (t != null) {
                                            sendJob(t, resource, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public void noMoreTasks(Map<Integer, Integer> counts) {
        taskCountToEnd = counts;
    }

    // Private methods
    // Schedule decision - number of dependency parameters (files/objects)
    private String assignTaskToBestResource(Task t, List<String> resources) {
        Parameter[] params = t.getCore().getParameters();
        HashMap<String, Integer> hostToScore = new HashMap<String, Integer>(params.length * 2);

        // Obtain the scores for each host: number of task parameters that are located in the host
        for (Parameter p : params) {
            if (p instanceof DependencyParameter && p.getDirection() != ParamDirection.OUT) {
                DependencyParameter dp = (DependencyParameter) p;
                DataInstanceId dId = null;
                switch (dp.getDirection()) {
                    case IN:
                        RAccessId raId = (RAccessId) dp.getDataAccessId();
                        dId = raId.getReadDataInstance();
                        break;
                    case INOUT:
                        RWAccessId rwaId = (RWAccessId) dp.getDataAccessId();
                        dId = rwaId.getReadDataInstance();
                        break;
                    case OUT:
                        break;
                }

                if (dId != null) {
                    TreeSet<String> hosts = FTM.getHosts(dId);
                    for (String host : hosts) {
                        Integer score;
                        if ((score = hostToScore.get(host)) == null) {
                            score = new Integer(0);
                            hostToScore.put(host, score);
                        }
                        hostToScore.put(host, score + 1);
                    }
                }
            }
        }

        if (hostToScore.isEmpty()) {
            return resources.get(0);
        }

        Collections.sort(resources);

        // Select the resource with best score among those that are suitable for the task (they match its constraints).
        String bestResource = null;
        int bestScore = 0;
        for (Map.Entry<String, Integer> e : hostToScore.entrySet()) {
            String host = e.getKey();
            Integer score = e.getValue();
            // Check whether the host is actually in the list of suitable resources
            if (score > bestScore && Collections.binarySearch(resources, host) >= 0) {
                bestResource = host;
                bestScore = score;
            }
        }

        if (debug) {
            logger.debug("Best scoring resource is " + bestResource + " with score " + bestScore);
        }

        if (bestResource == null) {
            return resources.get(0);
        } else {
            return bestResource;
        }
    }

    // Schedule decision - number of files
    private Task[] stealTaskFromOtherResourcesToExecute(String resourceName, int numberOfTasks) {
        Boolean[] fulfillsLicenses = new Boolean[Core.coreCount];

        Task[] stolenTask = new Task[numberOfTasks];
        Task[] bestTask = new Task[numberOfTasks];
        String[] taskOwner = new String[numberOfTasks];
        int[] bestScore = new int[numberOfTasks];

        for (int i = 0; i < numberOfTasks; i++) {
            bestTask[i] = null;
            bestScore[i] = Integer.MIN_VALUE;
            taskOwner[i] = "";
        }
        int minimumScore = Integer.MIN_VALUE;
        int minimumPosition = 0;




        Task[] bestTaskEnforced = new Task[numberOfTasks];
        String[] taskOwnerEnforced = new String[numberOfTasks];
        int[] bestScoreEnforced = new int[numberOfTasks];

        for (int i = 0; i < numberOfTasks; i++) {
            bestTaskEnforced[i] = null;
            bestScoreEnforced[i] = Integer.MIN_VALUE;
            taskOwnerEnforced[i] = "";
        }
        int minimumScoreEnforced = Integer.MIN_VALUE;
        int minimumPositionEnforced = 0;

        TreeSet<String> compatibleResources = new TreeSet<String>();
        List<Integer> executableMethods = resManager.getExecutableCores(resourceName);
        for (Integer methodId : executableMethods) {
            compatibleResources.addAll(resManager.findResources(methodId));
        }
        compatibleResources.remove(resourceName);

        for (String ownerName : compatibleResources) {
            // Find best scoring task whose constraints are fulfilled by the resource
            for (Task t : queueManager.getPendingTasks(ownerName)) {
                if (fulfillsLicenses[t.getCore().getId()] == null) {
                    fulfillsLicenses[t.getCore().getId()] = queueManager.canRunCore(t.getCore().getId());
                }
                if (!fulfillsLicenses[t.getCore().getId()]) {
                    continue;
                }
                boolean matches = resManager.matches(resourceName, t.getCore().getId());
                if (matches) {
                    int score = 0;
                    Parameter[] params = t.getCore().getParameters();
                    for (Parameter p : params) {
                        if (p instanceof DependencyParameter) {
                            DependencyParameter dp = (DependencyParameter) p;
                            DataInstanceId dId = null;
                            switch (dp.getDirection()) {
                                case IN:
                                    RAccessId raId = (RAccessId) dp.getDataAccessId();
                                    dId = raId.getReadDataInstance();
                                    break;
                                case INOUT:
                                    RWAccessId rwaId = (RWAccessId) dp.getDataAccessId();
                                    dId = rwaId.getReadDataInstance();
                                    break;
                                case OUT:
                                    break;
                            }

                            if (dId != null) {

                                TreeSet<String> hosts = FTM.getHosts(dId);
                                for (String host : hosts) {
                                    if (host.equals(ownerName)) {
                                        score--;
                                        break;
                                    }
                                    if (host.equals(resourceName)) {
                                        score += 2;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (t.isStrongEnforcedScheduling()) {
                        continue;
                    } else if (t.isEnforcedSceduling()) {
                        if (score > minimumScore) {
                            bestScoreEnforced[minimumPositionEnforced] = score;
                            bestTaskEnforced[minimumPositionEnforced] = t;
                            taskOwnerEnforced[minimumPositionEnforced] = ownerName;

                            minimumScoreEnforced = bestScoreEnforced[0];
                            minimumPositionEnforced = 0;



                            for (int i = 1; i < numberOfTasks; i++) {
                                if (minimumScoreEnforced > bestScoreEnforced[i]) {
                                    minimumScoreEnforced = bestScoreEnforced[i];
                                    minimumPositionEnforced = i;
                                }
                            }
                        }
                    } else {
                        if (score > minimumScore) {
                            bestScore[minimumPosition] = score;
                            bestTask[minimumPosition] = t;
                            taskOwner[minimumPosition] = ownerName;

                            minimumScore = bestScore[0];
                            minimumPosition = 0;



                            for (int i = 1; i < numberOfTasks; i++) {
                                if (minimumScore > bestScore[i]) {
                                    minimumScore = bestScore[i];
                                    minimumPosition = i;
                                }
                            }
                        }
                    }
                }
            }
        }
        int count = 0;
        for (int i = 0; i < numberOfTasks; i++) {
            if (bestTask[i] != null) {
                queueManager.moveFromNode(bestTask[i], taskOwner[i], resourceName);
                stolenTask[count] = bestTask[i];
                count++;
            }
        }
        for (int i = count; i < numberOfTasks; i++) {
            if (bestTaskEnforced[i] != null) {
                queueManager.moveFromNode(bestTaskEnforced[i], taskOwnerEnforced[i], resourceName);
                stolenTask[count] = bestTaskEnforced[i];
                count++;
            }
        }
        return stolenTask;
    }

    private Task assignRescheduledTask(String hostName) {
        Task bestTask = null;
        int bestScore = -1;
        Boolean[] fulfillsLicenses = new Boolean[Core.coreCount];
        for (Task t : queueManager.getTasksToReschedule()) {
            if (fulfillsLicenses[t.getCore().getId()] == null) {
                fulfillsLicenses[t.getCore().getId()] = queueManager.canRunCore(t.getCore().getId());
            }
            if (!fulfillsLicenses[t.getCore().getId()]) {
                continue;
            }
            boolean matches = resManager.matches(hostName, t.getCore().getId(), presched);
            if (matches) {
                // Now we must ensure that the freed host is not the one where the task failed to run
                String failedHost = t.getExecParams().getHost();
                if (failedHost.equals(hostName)) {
                    continue;
                } else {
                    int score = 0;
                    Parameter[] params = t.getCore().getParameters();
                    for (Parameter p : params) {
                        if (p instanceof DependencyParameter) {
                            DependencyParameter fp = (DependencyParameter) p;
                            DataInstanceId dId = null;
                            switch (fp.getDirection()) {
                                case IN:
                                    RAccessId raId = (RAccessId) fp.getDataAccessId();
                                    dId = raId.getReadDataInstance();
                                    break;
                                case INOUT:
                                    RWAccessId rwaId = (RWAccessId) fp.getDataAccessId();
                                    dId = rwaId.getReadDataInstance();
                                    break;
                                case OUT:
                                    break;
                            }

                            if (dId != null) {
                                TreeSet<String> hosts = FTM.getHosts(dId);
                                for (String host : hosts) {
                                    if (host.compareTo(hostName) == 0) {
                                        score++;
                                    }
                                }
                            }

                        }
                    }
                    if (bestScore < score) {
                        bestTask = t;
                        bestScore = score;
                    }
                }
            }
        }
        if (bestScore > -1) {
            return bestTask;
        }
        return null;
    }

    public ScheduleState getSchedulingState() {
        try {
            ScheduleState ss = queueManager.getCurrentState();
            ss.setSavedMachines(savedMachines);
            savedMachines = new LinkedList();
            return ss;
        } catch (Exception e) {
            logger.fatal("Can not get the current schedule", e);
            System.exit(1);
        }
        return null;
    }

    void setSchedulingState(ScheduleDecisions newState) {
        try {

            for (HashMap machine : newState.mandatory) {
                addCloudNode(machine);
            }

            queueManager.setWaitingVMs(newState.temporary);

            for (String IP : newState.terminate) {
                resManager.stopResource(IP);
                queueManager.removeSlots(IP);
                //Check if resource must be powered off
                if (queueManager.getSafeSlotsCount(IP) == 0) {
                    FTM.transferStopFiles(IP, resManager.getBestSafeResourcePerCore());
                    return; // resource shut down, no need to continue looking for a task
                }
            }

            logger.debug(queueManager.describeCurrentState());

            LinkedList<Object[]> movements = newState.getMovements();
            if (!movements.isEmpty()) {
                for (Object[] movement : movements) {
                    String sourceHost = (String) movement[0];
                    String targetHost = (String) movement[1];
                    int sourceSlot = (Integer) movement[2];
                    int targetSlot = (Integer) movement[3];
                    int coreId = (Integer) movement[4];
                    int amount = (Integer) movement[5];
                    queueManager.seekAndMove(sourceHost, targetHost, sourceSlot, targetSlot, coreId, amount);
                    if (!queueManager.isExecuting(targetHost, targetSlot)) {
                        Task[] chosenTask = new Task[1];
                        // Now assign, if possible, one of the pending tasks to the resource
                        chosenTask[0] = queueManager.getNextJobSlot(targetHost, targetSlot);
                        if (chosenTask[0] != null) {
                            if (debug) {
                                logger.debug("Freed Match: Task(" + chosenTask[0].getSchedulingTaskId() + ", "
                                        + chosenTask[0].getCore().getName() + ") "
                                        + "Resource(" + targetHost + ")");
                            }
                            sendJob(chosenTask[0], targetHost, false);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("CAN NOT UPDATE THE CURRENT STATE", e);
        }
    }

    /**
     * Returns the current state that should be printed in the monitor
     *
     * @return current state in an XML format
     */
    public String getMonitoringState() {
        StringBuilder sb = new StringBuilder();
        sb.append(queueManager.getMonitoringInfo());
        return sb.toString();
    }

    HashMap<String, Object[]> getComponentProperties() {
        return resManager.getComponentProperties();
    }

    public void resizeDataStructures() {
        queueManager.resizeDataStructures();
        resManager.resizeDataStructures();
    }

    public void linkCoreToResources(LinkedList<Integer> newMethods) {
        for (int coreId : newMethods) {
            resManager.linkCoreToMachines(coreId);
        }
    }
}
