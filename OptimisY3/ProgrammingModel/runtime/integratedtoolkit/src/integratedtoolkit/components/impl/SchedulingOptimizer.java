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

import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument.Vms;
import integratedtoolkit.ITConstants;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.ScheduleDecisions;
import integratedtoolkit.types.ScheduleState;
import integratedtoolkit.types.Task;
import integratedtoolkit.util.OptimisComponents;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

public class SchedulingOptimizer extends Thread {

    private Object alarmClock = new Object();
    private boolean running;
    private TaskDispatcher TD;
    //Number of Graph second level tasks per method
    private static int[] secondLevelGraphCount;
    private static boolean redo;
    //Logger
    private static final Logger logger = Logger.getLogger(Loggers.SO_COMP);
    private static final boolean debug = logger.isDebugEnabled();
    //Executed locally
    private boolean locally;

    private enum MachineStatus {

        NOT_DEFINED,
        READY,
        TOBESAVED,
        SAVED
    }

    SchedulingOptimizer() {
        secondLevelGraphCount = new int[Core.coreCount];
        redo = false;
        locally = true;
    }

    void setCoWorkers(TaskDispatcher td) {
        TD = td;
        locally = createEndpoints();
    }

    public void kill() {
        synchronized (alarmClock) {
            running = false;
            alarmClock.notify();
        }
    }

    public void run() {
        running = true;
        ScheduleState oldSchedule;
        ScheduleDecisions newSchedule;

        while (running) {
            try {
                do {
                    redo = false;
                    oldSchedule = TD.getCurrentSchedule();
                    newSchedule = interactWithOptimisElasticity(oldSchedule);
                } while (redo);
                TD.setNewSchedule(newSchedule);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                synchronized (alarmClock) {
                    alarmClock.wait(Long.parseLong(System.getProperty(ITConstants.IT_INTERACT_PERIOD)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void optimizeNow() {
        synchronized (alarmClock) {
            alarmClock.notify();
            redo = true;
        }
    }

    public void updateWaitingCounts(List<Task> tasks, boolean waiting, int[] waitingSet) {
        if (secondLevelGraphCount.length != waitingSet.length) {
            int[] secondTmp = new int[Core.coreCount];
            System.arraycopy(secondLevelGraphCount, 0, secondTmp, 0, secondLevelGraphCount.length);
            secondLevelGraphCount = secondTmp;
        }
        for (int i = 0; i < Core.coreCount; i++) {
            secondLevelGraphCount[i] += waitingSet[i];
        }
        for (Task currentTask : tasks) {
            if (waiting) {
                secondLevelGraphCount[currentTask.getCore().getId()]--;
            }
        }
    }

    public void newWaitingTask(int methodId) {
        secondLevelGraphCount[methodId]++;
    }

    private boolean createEndpoints() {
        System.out.println("Creating OptimisComponents Endopints");
        String contextPath = System.getProperty(ITConstants.IT_CONTEXT);
        if (contextPath == null) {
            return true;
        }

        HashMap<String, Object[]> projectDetails = TD.getComponentsProperties();
        System.out.println("Obtained " + projectDetails.size() + " components");
        for (java.util.Map.Entry<String, Object[]> e : projectDetails.entrySet()) {

            String componentName = e.getKey();
            Object[] details = e.getValue();
            OptimisComponents.completeComponentProperties(componentName, details);
        }

        return false;
    }

    //APPLYING POLICIES
    private ScheduleDecisions interactWithOptimisElasticity(ScheduleState oldSchedule) {
        ScheduleDecisions sd = new ScheduleDecisions();
        if (!locally) {
            for (String savedResource : oldSchedule.getSavedMachines()) {
                OptimisComponents.terminateResource(savedResource);
            }
        }

        if (!locally) {
            Long timestamp = System.currentTimeMillis();
            Set<String> resources = oldSchedule.getWorkerNames();
            HashMap<String, Integer[]> resourceToCoreCount = new HashMap<String, Integer[]>();

            for (String resource : resources) {
                int[][] pendingCores = oldSchedule.getSlotsCoreCount(resource);
                Integer[] runningCores = oldSchedule.getLastReadSlotsRunningMethods();
                Integer[] coreCount = new Integer[Core.coreCount];
                for (int i = 0; i < Core.coreCount; i++) {
                    coreCount[i] = 0;
                }

                int slots = runningCores.length;
                for (int slotId = 0; slotId < slots; slotId++) {
                    Integer coreId = runningCores[slotId];
                    if (coreId != null) {
                        coreCount[coreId]++;
                    }
                    for (coreId = 0; coreId < Core.coreCount; coreId++) {
                        coreCount[coreId] += pendingCores[slotId][coreId];
                    }
                }
                resourceToCoreCount.put(resource, coreCount);
            }

            resources = oldSchedule.getMasterNames();
            for (String master : resources) {
                Integer[] OEs = new Integer[Core.coreCount];
                OEs[0]=oldSchedule.getMasterOECount(master);
                resourceToCoreCount.put(master, OEs);
            }
            OptimisComponents.submitMonitoring(resourceToCoreCount, oldSchedule.coreMeanExecutionTime, timestamp);
        }


        //NEW RESOURCE DISCOVERY
        if (!locally) {
            try {
                System.out.println("MACHINE DISCOVERY");
                HashMap<String, Object[]>[] Vms = OptimisComponents.getVMs();
                HashMap<String, Object[]> mandatory = Vms[OptimisComponents.MANDATORY];
                for (java.util.Map.Entry<String, Object[]> e : mandatory.entrySet()) {
                    sd.addMandatory(e.getKey(), e.getValue());
                }

                HashMap<String, Object[]> temporary = Vms[OptimisComponents.TEMPORARY];
                for (java.util.Map.Entry<String, Object[]> e : temporary.entrySet()) {
                    sd.addTemporary(e.getKey(), e.getValue());
                }

                HashMap<String, Object[]> terminate = Vms[OptimisComponents.TERMINATE];
                for (java.util.Map.Entry<String, Object[]> e : terminate.entrySet()) {
                    sd.terminate.add(e.getKey());
                }

            } catch (Exception e) {
                logger.error("Error obtaining the VMs list from the Service Manager.", e);
            }
        }

        loadBalance(oldSchedule, sd);
        return sd;
    }

    private void loadBalance(ScheduleState ss, ScheduleDecisions sd) {
        System.out.println("Load Balancing");

        LinkedList<HostSlotsTime>[] hostListPerCore = new LinkedList[Core.coreCount];
        for (int coreId = 0; coreId < Core.coreCount; coreId++) {
            hostListPerCore[coreId] = new LinkedList();
        }

        Set<String> resources = ss.getWorkerNames();
        for (String res : resources) {
            HostSlotsTime hst = new HostSlotsTime();
            hst.hostName = res;
            hst.slotsTime = ss.getSlotsWaitingTime(res, ss.coreMeanExecutionTime);
            hst.slotCoreCount = ss.getLastReadSlotsCoreCount();
            for (int coreId : ss.getLastReadAbleCores()) {
                hostListPerCore[coreId].add(hst);
            }
        }

        for (int coreId = 0; coreId < Core.coreCount; coreId++) {
            //Busquem els recursos que poden executar pel core
            LinkedList<CoreTransferRequest> sender = new LinkedList();
            LinkedList<CoreTransferRequest> receiver = new LinkedList();
            LinkedList<HostSlotsTime> resourceList = hostListPerCore[coreId];
            int slotCount = 0;
            long slotTime = 0l;
            //Fem la suma del nombre de slots i del temps total

            for (HostSlotsTime hst : resourceList) {
                for (int i = 0; i < hst.slotsTime.length; i++) {
                    slotCount++;
                    slotTime += hst.slotsTime[i];
                }
            }
            if (slotCount == 0) {
                continue;
            }
            //Fem la mitja del temps ocupat
            long average = slotTime / (long) slotCount;

            //Calculem la  donacio/recepció
            for (HostSlotsTime hst : resourceList) {
                for (int i = 0; i < hst.slotsTime.length; i++) {
                    double ratio = (double) (hst.slotsTime[i] - average) / (double) ss.coreMeanExecutionTime[coreId];
                    if (ratio < 0) {
                        ratio -= 0.5;
                        int change = (int) ratio;
                        receiver.add(new CoreTransferRequest(hst, i, Math.abs(change)));
                    } else if (ratio > 0) {
                        ratio += 0.5;
                        int change = (int) ratio;
                        change = Math.min(change, hst.slotCoreCount[i][coreId]);
                        sender.add(new CoreTransferRequest(hst, i, change));
                    }
                }
            }
            //Fem el moviment
            for (CoreTransferRequest sndr : sender) {
                if (receiver.isEmpty()) {
                    break;
                }
                while (sndr.amount > 0) {
                    if (receiver.isEmpty()) {
                        break;
                    }
                    CoreTransferRequest rcvr = receiver.get(0);
                    int move = Math.min(rcvr.amount, sndr.amount);
                    moveTask(sndr, rcvr, move, coreId, ss.coreMeanExecutionTime[coreId]);
                    sd.addMovement(sndr.hst.hostName, sndr.slot, rcvr.hst.hostName, rcvr.slot, coreId, move);
                    if (rcvr.amount == 0) {
                        receiver.remove(0);
                    }
                }
            }
        }
    }

    private void moveTask(CoreTransferRequest sndr, CoreTransferRequest rcvr, int amount, int coreId, long coreTime) {
        sndr.amount -= amount;
        rcvr.amount -= amount;
        sndr.hst.slotCoreCount[sndr.slot][coreId] -= amount;
        rcvr.hst.slotCoreCount[rcvr.slot][coreId] += amount;
        sndr.hst.slotsTime[sndr.slot] -= coreTime * amount;
        rcvr.hst.slotsTime[rcvr.slot] += coreTime * amount;
    }

    private class HostSlotsTime {

        String hostName;
        long[] slotsTime;
        int[][] slotCoreCount;
    }

    private class CoreTransferRequest {

        HostSlotsTime hst;
        int slot;
        int amount;

        public CoreTransferRequest(HostSlotsTime hst, int slot, int amount) {
            this.hst = hst;
            this.slot = slot;
            this.amount = amount;
        }
    }
}
