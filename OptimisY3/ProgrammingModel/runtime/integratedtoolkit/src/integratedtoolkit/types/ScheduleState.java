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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ScheduleState {

    public int[] waitingOnQueuesCores;
    public long[] coreMeanExecutionTime;
    public int noResource;
    public int[] noResourcePerCore;
    public Integer[] slotCountPerCore;
    private HashMap<String, WorkerInfo> workerToInfo;
    private HashMap<String, MasterInfo> masterToInfo;
    private WorkerInfo lastReadHost;
    private LinkedList<String> savedMachines;

    public ScheduleState() {
        workerToInfo = new HashMap<String, WorkerInfo>();
        masterToInfo = new HashMap<String, MasterInfo>();
        waitingOnQueuesCores = new int[Core.coreCount];
    }

    public void addWorker(String name, Integer[] runningMethods, long[] elapsedTime, int[][] waitingCounts, List<Integer> ableCores, int slotsBeingRemoved) {
        WorkerInfo hi = new WorkerInfo(runningMethods.length, slotsBeingRemoved);
        workerToInfo.put(name, hi);
        hi.ableCores = ableCores;
        for (int i = 0; i < runningMethods.length; i++) {
            hi.slot[i].runningMethodId = runningMethods[i];
            hi.slot[i].elapsedTime = elapsedTime[i];
            System.arraycopy(waitingCounts[i], 0, hi.slot[i].waitingCounts, 0, Core.coreCount);
        }
    }

    public void addMaster(String name, int appCount) {
        MasterInfo mi = new MasterInfo();
        mi.OECount = appCount;
        masterToInfo.put(name, mi);
    }

    public Set<String> getWorkerNames() {
        return workerToInfo.keySet();
    }

    public Set<String> getMasterNames() {
        return masterToInfo.keySet();
    }

    public int getMasterOECount(String resource) {
        return masterToInfo.get(resource).OECount;
    }

    public int getActiveSlotsCount(String resource) {
        lastReadHost = workerToInfo.get(resource);
        return lastReadHost.slotsCount - lastReadHost.slotsBeingRemoved;
    }

    public int getLastReadActiveSlotsCount() {
        return lastReadHost.slotsCount - lastReadHost.slotsBeingRemoved;
    }

    public long[] getSlotsWaitingTime(String resource, long[] limitedMeanTimes) {
        lastReadHost = workerToInfo.get(resource);
        return getLastReadSlotsWaitingTime(limitedMeanTimes);
    }

    public long[] getLastReadSlotsWaitingTime(long[] limitedMeanTimes) {
        long[] slotsTime = new long[lastReadHost.slotsCount];
        for (int slotId = 0; slotId < lastReadHost.slotsCount; slotId++) {
            if (lastReadHost.slot[slotId] == null || lastReadHost.slot[slotId].runningMethodId == null) {
                slotsTime[slotId] = 0l;
                continue;
            }
            slotsTime[slotId] = limitedMeanTimes[lastReadHost.slot[slotId].runningMethodId];
            long elapsed = lastReadHost.slot[slotId].elapsedTime;
            if (elapsed < 1) {
                elapsed = 1l;
            }
            slotsTime[slotId] -= elapsed;
            if (slotsTime[slotId] < 1) {
                slotsTime[slotId] = 1l;
            }
            for (int methodId = 0; methodId < Core.coreCount; methodId++) {
                slotsTime[slotId] += limitedMeanTimes[methodId] * lastReadHost.slot[slotId].waitingCounts[methodId];
            }
        }
        return slotsTime;
    }

    public Integer[] getSlotsRunningMethods(String resource) {
        lastReadHost = workerToInfo.get(resource);
        return getLastReadSlotsRunningMethods();
    }

    public Integer[] getLastReadSlotsRunningMethods() {
        Integer[] slotsMethods = new Integer[lastReadHost.slotsCount];
        for (int slotId = 0; slotId < lastReadHost.slotsCount; slotId++) {
            if (lastReadHost.slot[slotId] != null) {
                slotsMethods[slotId] = lastReadHost.slot[slotId].runningMethodId;
            }
        }
        return slotsMethods;
    }

    public List<Integer> getAbleCores(String resource) {
        lastReadHost = workerToInfo.get(resource);
        return lastReadHost.ableCores;
    }

    public List<Integer> getLastReadAbleCores() {
        return lastReadHost.ableCores;
    }

    public int[][] getSlotsCoreCount(String resource) {
        lastReadHost = workerToInfo.get(resource);
        int[][] slotCount = new int[lastReadHost.slotsCount][];
        for (int i = 0; i < lastReadHost.slotsCount; i++) {
            slotCount[i] = lastReadHost.slot[i].waitingCounts;
        }
        return slotCount;
    }

    public int[][] getLastReadSlotsCoreCount() {
        int[][] slotCount = new int[lastReadHost.slotsCount][];
        for (int i = 0; i < lastReadHost.slotsCount; i++) {
            slotCount[i] = lastReadHost.slot[i].waitingCounts;
        }
        return slotCount;
    }

    public void setSavedMachines(LinkedList<String> savedMachines) {
        this.savedMachines = savedMachines;
    }

    public LinkedList<String> getSavedMachines() {
        return this.savedMachines;
    }
}

class MasterInfo {

    int OECount;
}

class WorkerInfo {

    int slotsCount;
    int slotsBeingRemoved;
    List<Integer> ableCores;
    Slot[] slot;

    WorkerInfo(int slotsCount, int slotsBeingRemoved) {
        this.slotsCount = slotsCount;
        this.slotsBeingRemoved = slotsBeingRemoved;
        slot = new Slot[slotsCount];
        for (int i = 0; i < slotsCount; i++) {
            slot[i] = new Slot();
        }
    }
}

class Slot {

    Integer runningMethodId;
    long elapsedTime;
    int[] waitingCounts;

    public Slot() {
        runningMethodId = -1;
        elapsedTime = 0;
        waitingCounts = new int[Core.coreCount];
    }
}
