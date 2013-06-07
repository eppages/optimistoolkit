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

import integratedtoolkit.ITConstants;
import integratedtoolkit.components.JobStatus;
import integratedtoolkit.components.Schedule;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.Parameter;
import integratedtoolkit.types.ResourceDescription;
import integratedtoolkit.types.ScheduleDecisions;
import integratedtoolkit.types.ScheduleState;
import integratedtoolkit.types.Task;
import integratedtoolkit.types.Task.TaskState;
import integratedtoolkit.types.data.DataAccessId;
import integratedtoolkit.types.data.DataInstanceId;
import integratedtoolkit.types.data.Location;
import integratedtoolkit.types.data.ResultFile;
import integratedtoolkit.types.message.td.TDMessage;
import integratedtoolkit.types.message.td.UpdateCEIRequest;
import integratedtoolkit.types.message.td.NewDataVersionRequest;
import integratedtoolkit.types.message.td.DeleteIntermediateFilesRequest;
import integratedtoolkit.types.message.td.GetCurrentScheduleRequest;
import integratedtoolkit.types.message.td.MonitoringDataRequest;
import integratedtoolkit.types.message.td.NewWaitingTaskRequest;
import integratedtoolkit.types.message.td.NotifyTaskEndRequest;
import integratedtoolkit.types.message.td.RescheduleTaskRequest;
import integratedtoolkit.types.message.td.ScheduleTasksRequest;
import integratedtoolkit.types.message.td.SetNewScheduleRequest;
import integratedtoolkit.types.message.td.ShutdownRequest;
import integratedtoolkit.types.message.td.TransferObjectRequest;
import integratedtoolkit.types.message.td.TransferFileForOpenRequest;
import integratedtoolkit.types.message.td.TransferFileRawRequest;
import integratedtoolkit.types.message.td.TransferBackResultFilesRequest;
import integratedtoolkit.util.ConstraintManager;
import integratedtoolkit.util.LicenseManager;
import integratedtoolkit.util.nio.Client;
import integratedtoolkit.util.nio.NIOConstants;
import integratedtoolkit.util.nio.Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.apache.log4j.Logger;

public class TaskDispatcher implements Runnable, Schedule, JobStatus {

    private static final String CONSTR_LOAD_ERR = "Error loading application constraints";
    // Subcomponents
    private TaskScheduler TS;
    private JobManager JM;
    private FileTransferManager FTM;
    private SchedulingOptimizer SO;
    // Queue that can contain ready, finished or to-reschedule tasks
    private LinkedBlockingQueue<TDMessage> requestQueue;
    private LinkedBlockingQueue<TDMessage> readQueue;
    private LinkedBlockingQueue<TDMessage> prioritaryTaskQueue;
    // Scheduler thread
    private Thread dispatcher;
    private boolean keepGoing;
    // Logging
    private static final Logger logger = Logger.getLogger(Loggers.TD_COMP);
    private static final boolean debug = logger.isDebugEnabled();
    private static int nextSchedulingTaskId;
    //private Server server;
    private String tdLocation;
    private static HashMap<String, TreeSet<Long>> mastersAppCount;

    public TaskDispatcher() {
        nextSchedulingTaskId = 1;
        String appName = System.getProperty(ITConstants.IT_APP_NAME);
        try {
            ConstraintManager.init(appName + "Itf");
        } catch (ClassNotFoundException e) {
            logger.fatal(CONSTR_LOAD_ERR, e);
            System.exit(1);
        }

        SO = new SchedulingOptimizer();

        TS = new TaskScheduler();
        JM = new JobManager();
        FTM = new FileTransferManager();

        JM.setServiceInstances(TS.getServiceInstances());

        try {
            requestQueue = new Server(NIOConstants.TD_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error turning on the server for the Task Dispatcher", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Error turning on the server for the Task Dispatcher", e);
        }
        readQueue = requestQueue;
        readQueue = requestQueue;
        prioritaryTaskQueue = new LinkedBlockingQueue<TDMessage>();

        keepGoing = true;
        dispatcher = new Thread(this);
        dispatcher.setName("Task Dispatcher");

        Runtime.getRuntime().addShutdownHook(new Ender());
        this.tdLocation = "localhost";

        SO.setCoWorkers(this);
        TS.setCoWorkers(JM, FTM);
        JM.setCoWorkers(this, FTM);
        FTM.setCoWorkers(TS, JM);
        SO.start();
        dispatcher.start();
        logger.info("Initialization finished");
        mastersAppCount = new HashMap<String, TreeSet<Long>>();
    }

    public TaskDispatcher(String tdLocation) {
        String appName = System.getProperty(ITConstants.IT_APP_NAME);
        try {
            ConstraintManager.init(appName + "Itf");
        } catch (ClassNotFoundException e) {
            logger.fatal(CONSTR_LOAD_ERR, e);
            System.exit(1);
        }
        System.out.println("Task Dispatcher on Location: " + tdLocation);
        this.tdLocation = tdLocation;
        try {
            TreeMap<String, Integer> newCoreIndexes = updateCoreElementInterface();
            Core.signatureToId = newCoreIndexes;
            Core.coreCount = newCoreIndexes.size();
        } catch (Exception e) {
            logger.fatal("COULD NOT FOUND CLASS" + appName, e);
            System.exit(1);
        }
    }

    public void cleanup() {
        if (tdLocation.compareTo("localhost") == 0) {
            TS.cleanup();
            FTM.cleanup();
            JM.cleanup();
            SO.kill();
            keepGoing = false;
            dispatcher.interrupt();
        }
    }

    // Dispatcher thread
    public void run() {
        try {
            while (keepGoing) {

                TDMessage request = null;
                Location location = null;
                try {
                    request = readQueue.take();
                } catch (InterruptedException e) {
                    continue;
                }
                switch (request.getRequestType()) {
                    case UPDATE_CEI:
                        UpdateCEIRequest uCEIReq = (UpdateCEIRequest) request;
                        LinkedList<Integer> newMethods = ConstraintManager.addMethods(uCEIReq.getSignatures(), uCEIReq.getConstraints(), uCEIReq.getRds());
                        LicenseManager.updateStructures();
                        LinkedList<String>[] licenses = uCEIReq.getLicenses();
                        for (int i = 0; i < uCEIReq.getSignatures().length; i++) {
                            String signature = (String) uCEIReq.getSignatures()[i];
                            int coreId = Core.signatureToId.get(signature);
                            if (newMethods.contains(coreId)) {
                                for (String license : licenses[i]) {
                                    LicenseManager.addLicenseToken(coreId, license);
                                }
                            }
                        }

                        TS.resizeDataStructures();
                        TS.linkCoreToResources(newMethods);
                        try {
                            //Remote resource tries to update
                            uCEIReq.reply(Core.signatureToId);
                        } catch (IOException ex) {
                            logger.error("Cannot reply UPDATE_CEI request coming from " + uCEIReq.getSourceName(), ex);
                        }
                        break;
                    case SCHEDULE_TASKS:
                        ScheduleTasksRequest stRequest = (ScheduleTasksRequest) request;
                        List<Task> toSchedule = stRequest.getToSchedule();
                        LinkedList<String> obsoletes = stRequest.getObsoletes();
                        if (obsoletes != null) {
                            FTM.obsoleteVersions(obsoletes);
                        }
                        SO.updateWaitingCounts(toSchedule, stRequest.getWaiting(), stRequest.getWaitingCount());
                        for (Task currentTask : toSchedule) {
                            TreeSet<Long> apps = mastersAppCount.get(currentTask.getSource());
                            if (apps == null) {
                                apps = new TreeSet<Long>();
                                mastersAppCount.put(currentTask.getSource(), apps);
                            }
                            apps.add(currentTask.getAppId());
                            for (Parameter p : currentTask.getCore().getParameters()) {
                                if (p instanceof Parameter.DependencyParameter.FileParameter) {
                                    Parameter.DependencyParameter.FileParameter dp = (Parameter.DependencyParameter.FileParameter) p;
                                    if (dp.getHost() == null) {
                                        if (stRequest.getSourceName().startsWith("127.")) {
                                            // Initialization of application attributes
                                            try {
                                                InetAddress localHost = InetAddress.getLocalHost();
                                                dp.setHost(localHost.getCanonicalHostName());
                                            } catch (UnknownHostException e) {
                                                logger.error("CAN NOT DISCOVER THE NAME OF THE HOST", e);
                                                System.exit(1);
                                            }
                                        } else {
                                            dp.setHost(stRequest.getSourceName());
                                        }

                                    }
                                }
                            }
                            int schedulingTaskId = nextSchedulingTaskId;
                            nextSchedulingTaskId++;
                            currentTask.setSchedulingTaskId(schedulingTaskId);
                            currentTask.setStatus(TaskState.TO_SCHEDULE);
                            currentTask.setSource(request.getSourceName());
                            TS.scheduleTask(currentTask);
                        }
                        break;
                    case FINISHED_TASK:
                        TS.notifyEnd(((NotifyTaskEndRequest) request).getTask());
                        break;
                    case RESCHEDULE_TASK:
                        TS.reschedule(((RescheduleTaskRequest) request).getTask());
                        break;
                    case NEW_WAITING_TASK:
                        NewWaitingTaskRequest nwtRequest = (NewWaitingTaskRequest) request;
                        obsoletes = nwtRequest.getObsoletes();
                        if (obsoletes != null) {
                            FTM.obsoleteVersions(obsoletes);
                        }
                        SO.newWaitingTask(nwtRequest.getMethodId());
                        break;
                    case NEW_DATA_VERSION:
                        NewDataVersionRequest ndvRequest = (NewDataVersionRequest) request;
                        if (ndvRequest.getHost() == null) {
                            if (ndvRequest.getSourceName().startsWith("127.")) {
                                // Initialization of application attributes
                                try {
                                    InetAddress localHost = InetAddress.getLocalHost();
                                    location = new Location(localHost.getCanonicalHostName(), ndvRequest.getPath());
                                } catch (UnknownHostException e) {
                                    logger.error("CAN NOT DISCOVER THE NAME OF THE HOST");
                                    System.exit(1);
                                }
                            } else {
                                location = new Location(ndvRequest.getSourceName(), ndvRequest.getPath());
                            }
                        } else {
                            location = new Location(ndvRequest.getHost(), ndvRequest.getPath());
                        }
                        FTM.newDataVersion(ndvRequest.getLastDID().getRenaming(), ndvRequest.getFileName(), location);
                        break;

                    case TRANSFER_OPEN_FILE:
                        TransferFileForOpenRequest tofRequest = (TransferFileForOpenRequest) request;
                        FTM.transferFileForOpen(tofRequest.getFaId(), tofRequest.getLocation(), tofRequest);
                        break;
                    case TRANSFER_RAW_FILE:
                        TransferFileRawRequest trfRequest = (TransferFileRawRequest) request;
                        FTM.transferFileRaw(trfRequest.getFaId(), trfRequest.getLocation(), trfRequest);
                        break;
                    case TRANSFER_OBJECT:
                        TransferObjectRequest tor = (TransferObjectRequest) request;
                        logger.info("Trying to obtain object " + tor.getDaId());
                        if (tor.getSourceName().startsWith("127.")) {
                            // Initialization of application attributes
                            try {
                                InetAddress localHost = InetAddress.getLocalHost();
                                tor.setHost(localHost.getCanonicalHostName());
                            } catch (UnknownHostException e) {
                                logger.error("CAN NOT DISCOVER THE NAME OF THE HOST");
                                System.exit(1);
                            }
                        } else {
                            tor.setHost(tor.getSourceName());
                        }
                        FTM.transferObjectValue(tor);
                        break;
                    case TRANSFER_RESULT_FILES:
                        TransferBackResultFilesRequest tresfRequest = (TransferBackResultFilesRequest) request;
                        for (ResultFile rf : tresfRequest.getResFiles()) {
                            Location loc = rf.getOriginalLocation();

                            if (loc.getHost() == null) {
                                if (tresfRequest.getSourceName().startsWith("127.")) {
                                    // Initialization of application attributes
                                    try {
                                        InetAddress localHost = InetAddress.getLocalHost();
                                        loc.setHost(localHost.getCanonicalHostName());
                                    } catch (UnknownHostException e) {
                                        logger.error("CAN NOT DISCOVER THE NAME OF THE HOST");
                                        System.exit(1);
                                    }
                                } else {
                                    loc.setHost(tresfRequest.getSourceName());
                                }
                            }
                        }
                        FTM.transferBackResultFiles(tresfRequest.getResFiles(), tresfRequest);
                        break;
                    case DELETE_INTERMEDIATE_FILES:
                        FTM.deleteIntermediateFiles(((DeleteIntermediateFilesRequest) request));
                        break;
                    case GET_STATE:
                        ScheduleState state = TS.getSchedulingState();
                        for (java.util.Map.Entry<String, TreeSet<Long>> resource : mastersAppCount.entrySet()) {
                            state.addMaster(resource.getKey(), resource.getValue().size());
                        }
                        ((GetCurrentScheduleRequest) request).setResponse(state);
                        ((GetCurrentScheduleRequest) request).getSemaphore().release();
                        break;
                    case SET_STATE:
                        ScheduleDecisions decisions = ((SetNewScheduleRequest) request).getNewState();
                        TS.setSchedulingState(decisions);
                        if (!decisions.mandatory.isEmpty()) {
                            SO.optimizeNow();
                        }
                        break;
                    case MONITOR_DATA:
                        String monitorData = TS.getMonitoringState();
                        try {
                            //Remote resource tries to update
                            request.reply(monitorData);
                        } catch (IOException ex) {
                            logger.error("Cannot reply UPDATE_CEI request coming from " + request.getSourceName(), ex);
                        }
                        break;
                    case SHUTDOWN:
                        ShutdownRequest sRequest = (ShutdownRequest) request;
                        obsoletes = sRequest.getObsoletes();
                        if (obsoletes != null) {
                            FTM.obsoleteVersions(obsoletes);
                        }
                        TS.noMoreTasks(sRequest.getCurrentTaskCount());

                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRequest(TDMessage request) throws IOException {
        Client.send(tdLocation, NIOConstants.TD_PORT, request);
    }

    private Object addRequestAndWait(TDMessage request) throws IOException, ClassNotFoundException {
        return Client.request(tdLocation, NIOConstants.TD_PORT, request);
    }

    private void addPrioritaryRequest(TDMessage request) {
        prioritaryTaskQueue.offer(request);
        readQueue = prioritaryTaskQueue;
        dispatcher.interrupt();
        while (prioritaryTaskQueue.size() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
            }
        }
        readQueue = requestQueue;
        dispatcher.interrupt();
    }

    // TP (TA)
    public void scheduleTasks(List<Task> toSchedule, boolean waiting, int[] waitingCount, LinkedList<String> obsoletes) throws IOException {
        if (debug) {
            StringBuilder sb = new StringBuilder("Schedule tasks: ");
            for (Task t : toSchedule) {
                sb.append(t.getCore().getName()).append("(").append(t.getSchedulingTaskId() + " - " + t.getTaskId() + "").append(") ");
            }
            logger.debug(sb);
        }
        addRequest(new ScheduleTasksRequest(toSchedule, waiting, waitingCount, obsoletes));
    }

    // Notification thread (JM)
    public void notifyJobEnd(Task task) {
        try {
            readQueue.offer(new NotifyTaskEndRequest(task));
        } catch (Exception e) {
            logger.error("Can not notify job end", e);
            System.exit(1);
        }
    }

    // Notification thread (JM) / Transfer threads (FTM)
    public void rescheduleJob(Task task) {
        task.setStatus(TaskState.TO_RESCHEDULE);
        try {
            readQueue.offer(new RescheduleTaskRequest(task));
        } catch (Exception e) {
            logger.error("Can not notify job end", e);
            System.exit(1);
        }
    }

    // TP (TA)
    public void newWaitingTask(int methodId, LinkedList<String> obsoletes) throws IOException {
        addRequest(new NewWaitingTaskRequest(methodId, obsoletes));
    }

    // TP (DIP)
    public void newDataVersion(DataInstanceId lastDID, String protocol, String host, String path, String fileName) throws IOException {
        addRequest(new NewDataVersionRequest(lastDID, protocol, host, path, fileName));
    }

    // App
    public void transferFileForOpen(DataAccessId faId, Location location) throws IOException, ClassNotFoundException {
        addRequestAndWait(new TransferFileForOpenRequest(faId, location));

    }

    // App
    public void transferFileRaw(DataAccessId faId, Location location) throws IOException, ClassNotFoundException {
        TransferFileRawRequest request = new TransferFileRawRequest(faId, location);

        addRequestAndWait(request);


    }

    // App
    public Object transferObject(DataAccessId daId, String path, String wRename) throws IOException, ClassNotFoundException {

        TransferObjectRequest request = new TransferObjectRequest(daId, path);
        Object o = addRequestAndWait(request);
        return o;

    }

    // App
    public void transferBackResultFiles(List<ResultFile> resFiles) throws IOException, ClassNotFoundException {


        TransferBackResultFilesRequest request = new TransferBackResultFilesRequest(resFiles);
        addRequestAndWait(request);


    }

    // App
    public void deleteIntermediateFiles() throws IOException, ClassNotFoundException {
        DeleteIntermediateFilesRequest request = new DeleteIntermediateFilesRequest();
        addRequestAndWait(request);

    }

    // Scheduling optimizer thread
    public ScheduleState getCurrentSchedule() {
        Semaphore sem = new Semaphore(0);
        GetCurrentScheduleRequest request = new GetCurrentScheduleRequest(sem);
        addPrioritaryRequest(request);
        try {
            sem.acquire();
        } catch (InterruptedException e) {
        }
        return request.getResponse();
    }

    // Scheduling optimizer thread
    public void setNewSchedule(ScheduleDecisions newSchedule) {
        SetNewScheduleRequest request = new SetNewScheduleRequest(newSchedule);
        addPrioritaryRequest(request);
    }

    // TP (TA)
    public void shutdown(HashMap<Integer, Integer> currentTaskCount, LinkedList<String> obsoletes) throws IOException {
        ShutdownRequest request = new ShutdownRequest(currentTaskCount, obsoletes);
        addRequest(request);
    }

    /**
     * Returs a string with the description of the tasks in the graph
     *
     * @return description of the current tasks in the graph
     */
    public String getCurrentMonitoringData() throws IOException, ClassNotFoundException {
        return (String) addRequestAndWait(new MonitoringDataRequest());
    }

    protected TreeMap<String, Integer> updateCoreElementInterface() throws IOException, ClassNotFoundException {
        String[] signatures = new String[Core.coreCount];
        String[] constraints = new String[Core.coreCount];
        ResourceDescription[] rd = new ResourceDescription[Core.coreCount];
        LinkedList<String>[] licenses = new LinkedList[Core.coreCount];

        for (java.util.Map.Entry<String, Integer> e : Core.signatureToId.entrySet()) {

            Integer coreId = e.getValue();
            signatures[coreId] = e.getKey();
            constraints[coreId] = ConstraintManager.getConstraints(coreId);
            rd[coreId] = ConstraintManager.getResourceConstraints()[coreId];
            licenses[coreId] = LicenseManager.getCoreLicenses(coreId);
        }
        UpdateCEIRequest request = new UpdateCEIRequest(signatures, constraints, rd, licenses);
        return (TreeMap<String, Integer>) addRequestAndWait(request);
    }

    public HashMap<String, Object[]> getComponentsProperties() {
        return TS.getComponentProperties();
    }

    class Ender extends Thread {

        public void run() {
            try {
                TS.cleanup();
                SO.kill();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
