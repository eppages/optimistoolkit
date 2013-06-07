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

import integratedtoolkit.ITConstants;
import integratedtoolkit.api.impl.IntegratedToolkitImpl;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.ServiceInstance;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.log4j.Logger;
import org.apache.xpath.domapi.XPathEvaluatorImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathResult;

/**
 * The ResourceManager class is an utility to manage all the resources available
 * for the cores execution. It keeps information about the features of each 
 * resource and is used as an endpoint to discover which resources can run a 
 * core in a certain moment, the total and the available number of slots.
 * 
 */
public class ResourceManager {

    //XML Document
    private Document resourcesDoc;
    //XPath evaluator for resourcesDoc
    private XPathEvaluator evaluator;
    //Information about resources
    private ResourcePool pool;
    //Components Description
    HashMap<String, Object[]> componentToProperties;
    public static final int ALL_RESOURCES = -1;
    private static final Logger logger = Logger.getLogger(Loggers.TS_COMP);

    /**
     * Constructs a new ResourceManager using the Resources xml file content.
     * First of all, an empty resource pool is created and the Cloud Manager is 
     * initialized without any providers.
     * Secondly the resource file is validated and parsed and the toplevel xml 
     * nodes are processed in different ways depending on its type:
     * - Resource: a new Physical resource is added to the resource pool with 
     * the same id as its Name attribute and as many slots as indicated in the
     * project file. If it has 0 slots or it is not on the project xml, the 
     * resource is not included.
     * 
     * - Service: a new Physical resource is added to the resource pool with 
     * the same id as its wsdl attribute and as many slots as indicated in the
     * project file. If it has 0 slots or it is not on the project xml, the 
     * resource is not included.
     * 
     * - Cloud Provider: if there is any CloudProvider in the project file with 
     * the same name, a new Cloud Provider is added to the CloudManager with its
     * name attribute value as identifier.
     * The CloudManager is configured as described between the project xml and 
     * the resources file. From the resource file it gets the properties which 
     * describe how to connect with it: the connector path, the endpoint, ...
     * Other properties required to manage the resources are specified on the 
     * project file: i.e. the maximum amount of resource dpeloyed on that 
     * provider. 
     * Some configurations depend on both files. One of them is the list of 
     * usable images. The images offered by the cloud provider are on a list on
     * the resources file, where there are specified the name and the software
     * description of that image. On the project file there is a description of 
     * how the resources created with that image must be used: username, working 
     * directory,... Only the images that have been described in both files are 
     * added to the CloudManager
     * 
     * @param constraintManager constraint Manager with the constraints of the
     * core that will be run in the managed resources
     * @throws Exception Parsing the xml file or creating new instances for the
     * Cloud providers connectors
     */
    public ResourceManager() throws Exception {
        SharedDiskManager.addMachine(IntegratedToolkitImpl.appHost);
        pool = new ResourcePool(Core.coreCount);
        componentToProperties = new HashMap<String, Object[]>();
        String resourceFile = System.getProperty(ITConstants.IT_RES_FILE);
        System.out.println("ResourceFile "+resourceFile);
        // Parse the XML document which contains resource information
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);

        resourcesDoc = docFactory.newDocumentBuilder().parse(resourceFile);

        // Validate the document against an XML Schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(System.getProperty(ITConstants.IT_RES_SCHEMA));
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(resourcesDoc));

        // Create an XPath evaluator to solve queries
        evaluator = new XPathEvaluatorImpl(resourcesDoc);
        // resolver = evaluator.createNSResolver(resourcesDoc);
        NodeList nl = resourcesDoc.getChildNodes().item(0).getChildNodes();
        int numRes = 0;
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeName().equals("Resource")) {
                numRes++;
                String name = n.getAttributes().getNamedItem("Name").getTextContent();
                SharedDiskManager.addMachine(name);
                String taskCount = ProjectManager.getResourceProperty(name, ITConstants.LIMIT_OF_TASKS);
                if (taskCount != null && Integer.parseInt(taskCount) > 0) {
                    pool.addPhysical(name, Integer.parseInt(taskCount));
                }
                for (int j = 0; j < n.getChildNodes().getLength(); j++) {
                    if (n.getChildNodes().item(j).getNodeName().compareTo("Disks") == 0) {
                        Node disks = n.getChildNodes().item(j);
                        for (int k = 0; k < disks.getChildNodes().getLength(); k++) {
                            if (disks.getChildNodes().item(k).getNodeName().compareTo("Disk") == 0) {
                                Node disk = disks.getChildNodes().item(k);
                                String diskName = disk.getAttributes().getNamedItem("Name").getTextContent();
                                String diskMountpoint = "";
                                for (int ki = 0; ki < disk.getChildNodes().getLength(); ki++) {

                                    if (disk.getChildNodes().item(ki).getNodeName().compareTo("MountPoint") == 0) {
                                        diskMountpoint = disk.getChildNodes().item(ki).getTextContent();
                                    }
                                }
                                SharedDiskManager.addSharedToMachine(diskName, diskMountpoint, name);
                            }
                        }
                    }
                }
            }else if (n.getNodeName().equals("Service")) {
                numRes++;
                String name = n.getAttributes().getNamedItem("wsdl").getTextContent();
                String taskCount = ProjectManager.getResourceProperty(name, ITConstants.LIMIT_OF_TASKS);
                if (taskCount != null && Integer.parseInt(taskCount) > 0) {
                    pool.addPhysical(name, Integer.parseInt(taskCount));
                }
            } else if (n.getNodeName().equals("Disk")) {
                String diskName = n.getAttributes().getNamedItem("name").getTextContent();
                String mountPoint = "";
                for (int j = 0; j < n.getChildNodes().getLength(); j++) {
                    if (n.getChildNodes().item(j).getNodeName().compareTo("name") == 0) {
                        diskName = n.getChildNodes().item(j).getTextContent();
                    } else if (n.getChildNodes().item(j).getNodeName().compareTo("MountPoint") == 0) {
                        mountPoint = n.getChildNodes().item(j).getTextContent();
                    }
                }
                SharedDiskManager.addSharedToMachine(diskName, mountPoint, IntegratedToolkitImpl.appHost);
            } else if (n.getNodeName().equals("DataNode")) {
                String host = "";
                String path = "";
                for (int j = 0; j < n.getChildNodes().getLength(); j++) {
                    if (n.getChildNodes().item(j).getNodeName().compareTo("Host") == 0) {
                        host = n.getChildNodes().item(j).getTextContent();
                    } else if (n.getChildNodes().item(j).getNodeName().compareTo("Path") == 0) {
                        path = n.getChildNodes().item(j).getTextContent();
                    }
                }
            } else if (n.getNodeName().equals("CloudProvider")) {
                
                String cloudProviderName = n.getAttributes().getNamedItem("name").getTextContent();
                System.out.println("Parsing CloudProvider "+cloudProviderName);
                if (!ProjectManager.existsCloudProvider(cloudProviderName)) {
                    System.out.println("\tDoes not exist on the Project file");
                    continue;
                }
                for (int ki = 0; ki < n.getChildNodes().getLength(); ki++) {
                    if (n.getChildNodes().item(ki).getNodeName().compareTo("#text") == 0) {
                    } else if (n.getChildNodes().item(ki).getNodeName().compareTo("ImageList") == 0) {
                        Node imageList = n.getChildNodes().item(ki);
                        for (int image = 0; image < imageList.getChildNodes().getLength(); image++) {
                            Node resourcesImageNode = imageList.getChildNodes().item(image);
                            if (resourcesImageNode.getNodeName().compareTo("Image") == 0) {
                                String imageName = resourcesImageNode.getAttributes().getNamedItem("name").getTextContent();
                                System.out.println("\tParsing Image "+imageName);
                                Node projectImageNode = ProjectManager.existsImageOnProvider(cloudProviderName, imageName);
                                if (projectImageNode == null) {
                                    System.out.println("\t\tDoes not exist on the Project file");
                                    continue;
                                }
                                //NEW COMPONENT
                                //[slots, user, installDir, wDir, shareddisks:{Title -> mountpoint}]
                                Object[] componentProperties = new Object[4];
                                HashMap<String, String> sharedDisks = new HashMap<String, String>();
                                System.out.println("\t\tShared Disks:");
                                for (int imageChildIndex = 0; imageChildIndex < resourcesImageNode.getChildNodes().getLength(); imageChildIndex++) {
                                    Node SharedDisksNode = resourcesImageNode.getChildNodes().item(imageChildIndex);
                                    if (SharedDisksNode.getNodeName().compareTo("SharedDisks") == 0) {
                                        for (int diskIndex = 0; diskIndex < SharedDisksNode.getChildNodes().getLength(); diskIndex++) {
                                            Node sharedDisk = SharedDisksNode.getChildNodes().item(diskIndex);
                                            if (sharedDisk.getNodeName().compareTo("Disk") == 0) {
                                                String diskName = sharedDisk.getAttributes().getNamedItem("name").getTextContent();
                                                String mountPoint = "";
                                                for (int j = 0; j < sharedDisk.getChildNodes().getLength(); j++) {
                                                    if (sharedDisk.getChildNodes().item(j).getNodeName().compareTo("MountPoint") == 0) {
                                                        mountPoint = sharedDisk.getChildNodes().item(j).getTextContent();
                                                    }
                                                }
                                                System.out.println("\t\t\t* "+diskName+" at "+mountPoint);
                                                sharedDisks.put(diskName, mountPoint);
                                            }
                                        }
                                    }
                                }
                                componentProperties[3] = sharedDisks;
                                for (int projectIndex = 0; projectIndex < projectImageNode.getChildNodes().getLength(); projectIndex++) {
                                    Node projectProperty = projectImageNode.getChildNodes().item(projectIndex);
                                    if (projectProperty.getNodeName().compareTo("InstallDir") == 0) {
                                        componentProperties[1] = projectProperty.getTextContent();
                                        System.out.println("\t\t InstallDir: "+componentProperties[1]);
                                    } else if (projectProperty.getNodeName().compareTo("WorkingDir") == 0) {
                                        componentProperties[2] = projectProperty.getTextContent();
                                        System.out.println("\t\t WDir: "+componentProperties[2]);
                                    } else if (projectProperty.getNodeName().compareTo("User") == 0) {
                                        componentProperties[0] = projectProperty.getTextContent();
                                        System.out.println("\t\t User: "+componentProperties[0]);
                                    }
                                }
                                componentToProperties.put(imageName, componentProperties);
                            }
                        }
                    }
                }
            }
        }
        logger.info(SharedDiskManager.getSharedStatus());
        System.out.println("ResourceManager fully initialized");
    }

    /**
     * 
     * @return 
     */
    public List<ServiceInstance> getServices() {
        List<ServiceInstance> services = new LinkedList<ServiceInstance>();
        NodeList nl = resourcesDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            ServiceInstance service = new ServiceInstance();
            if (n.getNodeName().equals("Service")) {
                String wsdl = n.getAttributes().getNamedItem("wsdl").getTextContent();
                service.setWsdl(wsdl);
                if (ProjectManager.containsServiceInstance(wsdl)) {
                    NodeList fields = n.getChildNodes();
                    for (int j = 0; j < fields.getLength(); j++) {
                        Node field = fields.item(j);
                        if (field.getNodeName().equals("Name")) {
                            service.setName(field.getTextContent());
                        } else if (field.getNodeName().equals("Namespace")) {
                            service.setNamespace(field.getTextContent());
                        } else if (field.getNodeName().equals("Port")) {
                            //service.addPort(field.getTextContent());
                        }
                    }
                    services.add(service);
                }
            }
        }
        return services;
    }

    //Deletes all the information about a request
    public void refuseCloudWorker(int oldTaskCount, List<Integer> coresId) {
        for (int i = 0; i < coresId.size(); i++) {
            pool.removeTaskCountFromCore(coresId.get(i), oldTaskCount);
        }
    }

    //Links a core with all the resources able to run it
    //(called only for the Physical Machines stored in the XML)
    public void linkCoreToMachines(int coreId) {
        String constraints = ConstraintManager.getConstraints(coreId);
        XPathResult matchingRes = (XPathResult) evaluator.evaluate(constraints,
                resourcesDoc,
                /*resolver*/ null,
                XPathResult.UNORDERED_NODE_ITERATOR_TYPE,
                null);
        Node n;
        String resourceName;
        while ((n = matchingRes.iterateNext()) != null) {
            // Get current resource and add it to the list
            if (n.getNodeName().equals("Resource")) {
                resourceName = n.getAttributes().getNamedItem("Name").getTextContent();
                pool.joinResourceToCore(resourceName, coreId);
            } else if (n.getNodeName().equals("Service")) {
                resourceName = n.getAttributes().getNamedItem("wsdl").getTextContent();
                pool.joinResourceToCore(resourceName, coreId);
            }
        }

    }

    //Returns the name of all the resources able to execute coreId
    public List<String> findResources(int coreId) {
        return pool.findResources(coreId);
    }

    //return all the rescources able to execute a task of the core taking care the prescheduling policy
    public List<String> findResources(int coreId, boolean preschedule) {
        List<String> resources;
        resources = pool.findResources(coreId, false);
        if (preschedule && resources.isEmpty()) {
            resources = pool.findResources(coreId, true);
        }

        return resources;
    }

    //Checks if resourcename can execute coreId
    public boolean matches(String resourceName, int coreId) {
        return pool.matches(resourceName, coreId);
    }

    //Checks if resourceName can execute coreId taking care the slots
    public boolean matches(String resourceName, int coreId, boolean presched) {
        return pool.matches(resourceName, coreId, presched);

    }

    //Assigns a number of slots to a resource
    public void setMaxTaskCount(String resourceName, String taskCount) {
        pool.setMaxTaskCount(resourceName, Integer.parseInt(taskCount));
    }

    //Occupies a free slot
    public void reserveResource(String resourceName) {
        pool.modifyTaskCount(resourceName, 1);
    }

    //Releases a busy slot
    public void freeResource(String resourceName) {
        pool.modifyTaskCount(resourceName, -1);
    }

    //Releases all the slots of the resource
    public void freeAllResources() {
        pool.freeAllResources();
    }

    //Returns a list with all coreIds that can be executed by the resource res
    public List<Integer> getExecutableCores(String resourceName) {
        return pool.getExecutableCores(resourceName);
    }

    //returns the number of slots of a resource
    public int getMaxTaskCount(String resourceName) {
        return pool.getMaxTaskCount(resourceName);
    }

    //Returns the number of slots of the core
    public Integer[] getProcessorsCount() {
        return pool.getCoreMaxTaskCount();
    }

    public int getProcessorsCount(int coreId) {
        return pool.getCoreMaxTaskCountPerCore(coreId);
    }

    //Removes from the pool the resource
    public void stopResource(String resourceName) {
        pool.delete(resourceName);
    }

    //Shuts down all cloud resources
    public void stopVirtualNodes()
            throws Exception {
    }

    public String[] getBestSafeResourcePerCore() {
        int coreCount = Core.coreCount;
        String[] bestResource = new String[coreCount];
        for (int i = 0; i < coreCount; i++) {
            if (ConstraintManager.getResourceConstraints(i) != null) {
                bestResource[i] = pool.getSafeResource(i);
            } else {
                bestResource[i] = null;
            }
        }
        return bestResource;
    }

    public void printCoresResourcesLinks() {
        List<String> allResources = findResources(ALL_RESOURCES, false);
        pool.relacions(allResources);
    }

    public boolean hasFreeSlots(String chosenResource, boolean presched) {
        return pool.hasFreeSlots(chosenResource, presched);

    }

    /**
     * Returns all the data to be printed in the monitor
     * @return data to be monitorized
     */
    public String getMonitoringData() {
        return pool.getMonitoringData();
    }

    public void addResource(String name, LinkedList<Integer> methodIds, int slots, HashMap<String, String> sharedDisks) {
        pool.addPhysical(name, slots);
        for (int coreId : methodIds) {
            pool.joinResourceToCore(name, coreId);
        }
        List<String> allResources = findResources(ALL_RESOURCES, false);
        pool.relacions(allResources);

        SharedDiskManager.addMachine(name);
        for (java.util.Map.Entry<String, String> e : sharedDisks.entrySet()) {
            SharedDiskManager.addSharedToMachine(e.getKey(), e.getValue(), name);
        }
    }

    public HashMap<String, Object[]> getComponentProperties() {
        return this.componentToProperties;
    }

    public void resizeDataStructures() {
        pool.resizeDataStructures();
    }
}
