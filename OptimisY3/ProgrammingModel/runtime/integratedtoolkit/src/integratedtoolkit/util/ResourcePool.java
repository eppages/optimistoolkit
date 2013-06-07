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

import integratedtoolkit.types.Core;
import integratedtoolkit.types.ResourceDescription;
import integratedtoolkit.types.Task;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.LinkedList;
import java.util.Set;

public class ResourcePool {

    //Resource Sets:
    //  Physical Ressources (readed from xml)
    private HashMap<String, Resource> physicalSet;
    //Map: coreId -> List <names of the resources where core suits>
    private LinkedList<Resource>[] coreToResource;
    //Map: coreId -> maxTaskCount accepted for that core
    private Integer[] coreMaxTaskCount;
    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(integratedtoolkit.log.Loggers.RESOURCES);

    public ResourcePool(int coreCount) {
        physicalSet = new HashMap<String, Resource>();

        coreToResource = new LinkedList[coreCount];
        coreMaxTaskCount = new Integer[coreCount];
        for (int i = 0; i < coreCount; i++) {
            coreToResource[i] = new LinkedList<Resource>();
            coreMaxTaskCount[i] = 0;
        }
    }

    //Adds a new Resource on the Physical list
    public void addPhysical(String resourceName, int maxTaskCount) {
        Resource newResource = new Resource(resourceName, maxTaskCount);
        physicalSet.put(resourceName, newResource);
        logger.info(resourceName + " has been added to available resource pool as a physical resource with " + maxTaskCount + " slots");
    }

    //Returns the number of slots of the core
    public Integer[] getCoreMaxTaskCount() {
        return coreMaxTaskCount;
    }

    public int getCoreMaxTaskCountPerCore(int coreId) {
        return coreMaxTaskCount[coreId];
    }

    //Adds taskCount slots to the Core
    public void addTaskCountToCore(int coreId, int taskCount) {
        coreMaxTaskCount[coreId] += taskCount;
    }

    //Removes taskCount slots to the Core
    public void removeTaskCountFromCore(Integer coreId, int taskCount) {
        coreMaxTaskCount[coreId] -= taskCount;
    }

    //Links a resource to a core (resource resourceName can execute coreId core)
    public void joinResourceToCore(String resourceName, Integer coreId) {
        Resource resource = getResource(resourceName);
        if (resource == null) {
            return;
        }
        resource.addCore(coreId);
        LinkedList<Resource> resources = coreToResource[coreId];
        if (resources == null) {
            resources = new LinkedList<Resource>();
            coreToResource[coreId] = resources;
        }
        resources.add(resource);
        coreMaxTaskCount[coreId] += resource.getSlots();
    }

    //Returns a list with all coreIds that can be executed by the resource res
    public List<Integer> getExecutableCores(String res) {
        Resource resource = getResource(res);
        if (resource == null) {
            return new LinkedList();
        }
        return resource.executableCores;
    }

    //Assigns a number of slots to a resource
    public void setMaxTaskCount(String resourceName, Integer taskCount) {
        Resource modified = getResource(resourceName);
        if (modified == null) {
            return;
        }
        modified.setSlots(taskCount);
    }

    //Releases all the slots of the resource
    public void freeAllResources() {
        Resource[] set;
        if (physicalSet != null && !physicalSet.isEmpty()) {
            set = (Resource[]) physicalSet.values().toArray();
            for (int i = 0; i < set.length; i++) {
                set[i].taskCount = 0;
            }
        }
    }

    //Updates the number of free slots of the resource
    public void modifyTaskCount(String resourceName, int addition) {
        Resource r = getResource(resourceName);
        if (r == null) {
            return;
        }
        r.taskCount += addition;
    }

    //Deletes a resource from the pool
    public void delete(String resourceName) {
        //Remove it from the sets
        Resource resource = physicalSet.remove(resourceName);
        if (resource == null) {
            return;
        }

        //Remove all its relations
        for (int i = 0; i < resource.executableCores.size(); i++) {
            //slots of the core
            coreMaxTaskCount[resource.executableCores.get(i)] -= resource.getSlots();
            //Unlink resource of the core
            List<Resource> resources = coreToResource[resource.executableCores.get(i)];
            for (int j = 0; j < resources.size(); j++) {
                if ((resources.get(j)).name.compareTo(resourceName) == 0) {
                    resources.remove(j);
                }
            }
        }
    }

    //Returns the name of all the resources able to execute coreId
    public List<String> findResources(int coreId) {
        LinkedList<String> resourceNames = new LinkedList<String>();
        if (coreId == ResourceManager.ALL_RESOURCES) {
            if (physicalSet != null && !physicalSet.isEmpty()) {
                for (int i = 0; i < physicalSet.size(); i++) {
                    resourceNames.add(((Resource) physicalSet.values().toArray()[i]).name);
                }
            }
            return resourceNames;
        }
        LinkedList<Resource> resources = coreToResource[coreId];

        for (int res = 0; res < resources.size(); res++) {
            resourceNames.add(resources.get(res).name);
        }
        return resourceNames;
    }

    //return all the rescources able to execute a task of the core taking care the slots
    public List<String> findResources(int coreId, boolean preschedule) {
        LinkedList<String> resourceNames = new LinkedList<String>();
        if (coreId == ResourceManager.ALL_RESOURCES) {
            if (physicalSet != null && !physicalSet.isEmpty()) {
                for (int i = 0; i < physicalSet.size(); i++) {
                    resourceNames.add(((Resource) physicalSet.values().toArray()[i]).name);
                }
            }
            return resourceNames;
        }
        LinkedList<Resource> resources = coreToResource[coreId];
        for (int res = 0; res < resources.size(); res++) {
            Resource resource = resources.get(res);
            if (preschedule) {
                if (resource.taskCount < 2 * resource.getSlots()) {
                    resourceNames.add(resource.name);
                }
            } else {
                if (resource.taskCount < resource.getSlots()) {
                    resourceNames.add(resources.get(res).name);
                }
            }

        }
        return resourceNames;
    }

    //Checks if resourcename can execute coreId
    public boolean matches(String resourceName, int coreId) {
        Resource resource = getResource(resourceName);
        if (resource == null) {
            return false;
        }
        boolean exists = false;
        for (int i = 0; i < resource.executableCores.size() && !exists; i++) {
            exists |= resource.executableCores.get(i) == coreId;
        }
        return exists;
    }

    //Checks if resourcename can execute coreId taking care the slots
    public boolean matches(String resourceName, int coreId, boolean presched) {
        Resource resource = getResource(resourceName);
        if (resource == null) {
            return false;
        }
        for (int i = 0; i < resource.executableCores.size(); i++) {
            if (resource.executableCores.get(i) == coreId) {
                if (presched) {
                    return resource.taskCount < 2 * resource.getSlots();
                } else {
                    return resource.taskCount < resource.getSlots();
                }
            }
        }
        return false;
    }

    //returns the number of slots of a resource
    public int getMaxTaskCount(String resourceName) {
        Resource resource = getResource(resourceName);
        if (resource == null) {
            return 0;
        }
        return resource.getSlots();
    }

    //Returns a critical machine able to execute the core
    public String getSafeResource(int coreId) {
        LinkedList<Resource> resources = coreToResource[coreId];
        String ret = "";
        for (Resource r : resources) {
            if (physicalSet.containsKey(r.name)) {
                ret = r.name;
            }
        }
        return ret;
    }

    //deletes all the resources that are not going to execute any task
    public List stopZeros(Map<Integer, Integer> count) {
        LinkedList<String> toShutdown = new LinkedList();
        for (int coreIndex = 0; coreIndex < coreToResource.length; coreIndex++) {
            if (count.get(coreIndex) == null || count.get(coreIndex) == 0) { //si no tinc tasques del tipus al graf
                LinkedList<Resource> candidates = coreToResource[coreIndex];
                for (int canIndex = 0; canIndex < candidates.size(); canIndex++) {
                    boolean needed = false;
                    Resource actualCandidate = candidates.get(canIndex);
                    if (!physicalSet.containsKey(actualCandidate.name)) {
                        for (int executableIndex = 0; executableIndex < actualCandidate.executableCores.size() && !needed; executableIndex++) {
                            needed = ((count.get(actualCandidate.executableCores.get(executableIndex)) != null && count.get(actualCandidate.executableCores.get(executableIndex)) != 0) || actualCandidate.executableCores.get(executableIndex) < coreIndex);
                        }
                        if (!needed) {
                            toShutdown.add(actualCandidate.name);
                            delete(actualCandidate.name);
                        }
                    }
                }
            }
        }
        return toShutdown;
    }

    //returns all the resource information
    private Resource getResource(String resourceName) {
        Resource resource = physicalSet.get(resourceName);
        return resource;
    }

    boolean hasFreeSlots(String chosenResource, boolean preschedule) {
        Resource resource = getResource(chosenResource);
        if (preschedule) {
            return (resource.taskCount < 2 * resource.getSlots());
        } else {
            return resource.taskCount < resource.getSlots();
        }
    }

//Print all links between resources and cores
    public void relacions(List<String> allResources) {

        for (int coreId = 0; coreId < coreToResource.length; coreId++) {
            logger.info("Core " + coreId + " can be executed on " + coreMaxTaskCount[coreId] + " slots");
            LinkedList<Resource> resources = coreToResource[coreId];
            for (int res = 0; res < resources.size(); res++) {
                logger.info("\t|-" + resources.get(res).name + "   (" + resources.get(res).getSlots() + " slots)");
            }
        }

        for (String res : allResources) {
            Resource resource = getResource(res);

            logger.info("- Resource " + res + " can execute the cores:");
            LinkedList<Integer> resources = resource.executableCores;
            for (int j = 0; j < resources.size(); j++) {
                logger.info("\t|-" + resources.get(j));
            }
        }

    }

    /**
     * Obtains a description of all the resources
     *
     * @return String with an XML with data about the current resources
     */
    public String getMonitoringData() {
        StringBuilder sb = new StringBuilder();
        for (Resource resource : physicalSet.values()) {
            sb.append(resource.getMonitoringData());
        }
        return sb.toString();
    }

    void resizeDataStructures() {
        LinkedList[] coreToResourceTmp = new LinkedList[Core.coreCount];
        System.arraycopy(coreToResource, 0, coreToResourceTmp, 0, coreToResource.length);
        for (int i = coreToResource.length; i < Core.coreCount; i++) {
            coreToResourceTmp[i] = new LinkedList<String>();
        }
        coreToResource = coreToResourceTmp;

        Integer[] coreMaxTaskCountTmp = new Integer[Core.coreCount];
        System.arraycopy(coreMaxTaskCount, 0, coreMaxTaskCountTmp, 0, coreMaxTaskCount.length);
        for (int i = coreMaxTaskCount.length; i < Core.coreCount; i++) {
            coreMaxTaskCountTmp[i] = 0;
        }
        coreMaxTaskCount = coreMaxTaskCountTmp;
    }
}

class Resource {
    // Name of the resource

    String name;
    //number of slots to execute tasks
    private int slotsCount;
    //number of tasks assigned to the resource
    int taskCount;
    //CoreId that can be executed by this resource
    LinkedList<Integer> executableCores;

    public Resource(String name, Integer maxTaskCount) {
        this.name = name;
        this.slotsCount = maxTaskCount;
        this.taskCount = 0;
        this.executableCores = new LinkedList<Integer>();
    }

    public void addCore(int id) {
        executableCores.add(id);
    }

    public int getSlots() {
        return slotsCount;
    }

    public void setSlots(int qty) {
        slotsCount = qty;

    }

    public void updateSlots(int variation) {
        slotsCount += variation;

    }

    public String getMonitoringData() {
        StringBuilder sb = new StringBuilder("\t\t<Resource id=\"").append(name).append("\">\n");
        sb.append("\t\t\t<TotalSlots>").append(slotsCount).append("</TotalSlots>\n");
        sb.append("\t\t\t<FreeSlots>").append(slotsCount - taskCount).append("</FreeSlots>\n");
        sb.append("\t\t</Resource>\n");
        return sb.toString();
    }
}
