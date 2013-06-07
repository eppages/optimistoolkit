/*
 *  Copyright 2002-2012 Barcelona Supercomputing Center (www.bsc.es)
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument.Vms;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis_project.monitoring.Measurement;
import integratedtoolkit.ITConstants;
import integratedtoolkit.log.Loggers;
import integratedtoolkit.types.Core;
import integratedtoolkit.types.ResourceDescription;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class OptimisComponents {

    private static String serviceId;
    private static String endpointsFolder;
    private static eu.optimis.manifest.api.sp.Manifest serviceManifest = null;
    //
    private static SMClient serviceManager;
    //Logger
    private static final Logger logger = Logger.getLogger(Loggers.SO_COMP);
    private static final boolean debug = logger.isDebugEnabled();
    private static HashMap<String, IP> ips;
    private static HashMap<String, Component> components;
    private static HashMap<String, Instance> instances;
    private static HashMap<String, Resource> resources;
    public static final int MANDATORY = 0;
    public static final int TEMPORARY = 1;
    public static final int TERMINATE = 2;
    public static final String OPTIMIS_PREFIX = "system-";
    //public static final String OPTIMIS_PREFIX = "system-optimis-pm-";
    public static final String OPTIMIS_SUFFIX = "_instance-";

    private enum MachineStatus {

        NOT_DEFINED,
        READY,
        TOBESAVED,
        SAVED
    }

    public static void init() {
        /*IPv4ToIP = new HashMap<String, String>();
         IPv4ToInstanceName = new HashMap<String, String>();
         IPv4ToMachineStatus = new HashMap<String, MachineStatus>();
         IPv4Temp = new HashMap<String, Long>();
         componentToRepresentative = new HashMap<String, String>();
         componentDeployTime = new HashMap<String, Long>();
         componentToProperties = new HashMap<String, HashMap<String, Object[]>>();*/

        ips = new HashMap<String, IP>();
        components = new HashMap<String, Component>();
        instances = new HashMap<String, Instance>();
        resources = new HashMap<String, Resource>();


        String contextPath = System.getProperty(ITConstants.IT_CONTEXT);
        if (contextPath
                == null) {
            return;
        }
        String manifestPath = System.getProperty(ITConstants.IT_MANIFEST_LOCATION);
        if (contextPath
                == null) {
            return;
        }
        endpointsFolder = contextPath + File.separator + "endpoints";


        try {
            //java.io.InputStream in = new java.io.FileInputStream(contextPath + File.separator + "manifest.xml");
            java.io.InputStream in = new java.io.FileInputStream(manifestPath);
            XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse(in);
            serviceManifest = eu.optimis.manifest.api.sp.Manifest.Factory.newInstance(doc);
        } catch (FileNotFoundException ex) {
            logger.fatal("Could not find the service Manifest File", ex);
            System.exit(1);
        } catch (Exception ex) {
            logger.fatal("Error parsing the service Manifest file", ex);
            System.exit(1);
        }
        serviceId = serviceManifest.getVirtualMachineDescriptionSection().getServiceId();

        initServiceManagerEndpoint();

        System.out.println("Working with Service " + serviceId);
    }

    public static boolean isScheduler(String appHost) {
        String ownComponent = System.getProperty(ITConstants.IT_COMPONENT);
        String schedulerComponent = System.getProperty(ITConstants.IT_SCHEDULER_COMPONENT);
        if (ownComponent == null || schedulerComponent == null || ownComponent.compareTo(schedulerComponent) == 0) {
            try {
                int schedComponents = 0;
                LinkedList<InfrastructureProvider> ips = serviceManager.getProvidersData(serviceId);
                for (InfrastructureProvider ip : ips) {
                    for (Vm vm : ip.getVms().getVmArray()) {
                        String instanceName = vm.getType();
                        String VMtype = instanceName.substring(instanceName.indexOf(OPTIMIS_PREFIX) + OPTIMIS_PREFIX.length(), instanceName.lastIndexOf(OPTIMIS_SUFFIX));
                        if (VMtype.compareTo(schedulerComponent) == 0) {
                            schedComponents++;
                        }
                    }

                }
                if (schedComponents < 2) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                return true;
            }
        } else {
            return false;
        }

    }

    public static String getScheduler() {
        String schedulerComponent = System.getProperty(ITConstants.IT_SCHEDULER_COMPONENT);
        try {
            LinkedList<InfrastructureProvider> ips = serviceManager.getProvidersData(serviceId);

            for (InfrastructureProvider ip : ips) {
                for (Vm vm : ip.getVms().getVmArray()) {
                    String instanceName = vm.getType();
                    String VMtype = instanceName.substring(instanceName.indexOf(OPTIMIS_PREFIX) + OPTIMIS_PREFIX.length(), instanceName.lastIndexOf(OPTIMIS_SUFFIX));
                    if (VMtype.compareTo(schedulerComponent) == 0) {
                        int instanceId = Integer.parseInt(instanceName.substring(instanceName.lastIndexOf(OPTIMIS_SUFFIX) + OPTIMIS_SUFFIX.length()));
                        if (instanceId == 1) {
                            return vm.getId();
                        }
                    }
                }
            }

        } catch (Exception e) {
            return "127.0.0.1";
        }
        return "127.0.0.1";
    }

    public static void completeComponentProperties(String componentName, Object[] details) {
        VirtualSystem component = (VirtualSystem) serviceManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(componentName).getOVFDefinition().getVirtualSystem();
        String productInfo = component.getProductSection().getProduct();
        HashMap<String, LocationProperties> componentProperties = parseProductInfo(productInfo, details);
        Component comp = new Component(componentName);
        comp.locations = componentProperties;
        components.put(componentName, comp);
        System.out.println("\tAdding " + componentName + " properties");
    }

    public static void initServiceManagerEndpoint() {
        System.out.println("initServiceManagerEndpoint");
        PropertiesConfiguration config;
        /*-------------------------------------------
         * Service Manager Endpint definition
         *-------------------------------------------*/
        try {
            System.out.println("Obtaining endpoints from " + endpointsFolder + File.separator + "ServiceManager.properties");
            config = new PropertiesConfiguration(endpointsFolder + File.separator + "ServiceManager.properties");
            String serviceManagerEndpoint = config.getString("ServiceManager");
            System.out.println("Using Endpoint: " + serviceManagerEndpoint);
            serviceManagerEndpoint = serviceManagerEndpoint.substring(0, serviceManagerEndpoint.lastIndexOf("/"));
            serviceManager = new SMClient(serviceManagerEndpoint);
        } catch (Exception e) {
            logger.fatal("Could not create the service manager client", e);
            System.exit(1);
        }
    }

    private static HashMap<String, LocationProperties> parseProductInfo(String productInfo, Object[] details) {

        HashMap<String, LocationProperties> properties = new HashMap<String, LocationProperties>();
        LocationProperties locationProperties;
        int index = productInfo.indexOf("[");
        while (index > -1) {
            String location;
            String signatures;
            productInfo = productInfo.substring(index + 1);
            index = productInfo.indexOf("]");
            productInfo = productInfo.substring(0, index);

            index = productInfo.indexOf("|");
            location = productInfo.substring(0, index);
            productInfo = productInfo.substring(index + 1);


            index = productInfo.indexOf("|");
            signatures = productInfo.substring(0, index);
            productInfo = productInfo.substring(index + 1);


            locationProperties = new LocationProperties();

            locationProperties.slots = Integer.parseInt(productInfo);//slots
            LinkedList<Integer> cores = new LinkedList();
            locationProperties.cores = cores;
            locationProperties.user = (String) details[0];
            locationProperties.iDir = (String) details[1];
            locationProperties.wDir = (String) details[2];
            locationProperties.sharedDisks = (HashMap<String,String>)details[3];


            String[] methods = signatures.split(";");
            System.out.println("Methods");
            for (String signature : methods) {
                Integer coreId = Core.signatureToId.get(signature);
                System.out.println("\t" + signature + ": " + coreId);
                if (coreId == null) {
                    coreId = Core.coreCount;
                    ConstraintManager.addMethods(new String[]{signature}, new String[]{null}, new ResourceDescription[]{null});
                    LicenseManager.updateStructures();
                }
                cores.add(coreId);
            }
            properties.put(location, locationProperties);
            index = productInfo.indexOf("[");
        }
        if (properties.isEmpty()){
            locationProperties = new LocationProperties();
            locationProperties.slots = 0;//slots
            LinkedList<Integer> cores = new LinkedList();
            locationProperties.cores = cores;
            locationProperties.user = (String) details[0];
            locationProperties.iDir = (String) details[1];
            locationProperties.wDir = (String) details[2];
            locationProperties.sharedDisks = (HashMap<String,String>)details[3];
            properties.put("", locationProperties);
        }
        return properties;
    }

    /*public static void initMonitoringEndpoint() {
     System.out.println("initMonitoringEndpoint");
     PropertiesConfiguration config;
     /*-------------------------------------------
     * Monitoring Infrastructure Endpoint definition
     *-------------------------------------------*/
    /*   try {
     config = new PropertiesConfiguration(endpointsFolder + File.separator + "MonitoringInfrastructure.properties");
     String monitoringEndpoint = config.getString("MonitoringInfrastructure");
     Client client = Client.create();
     monitoringWR = client.resource(monitoringEndpoint);
     } catch (Exception e) {
     logger.fatal("Could not create the monitoring infrastructure client", e);
     System.exit(1);
     }

     }*/
    public static WebResource initMonitoringEndpoint(String ip) {
        try {
            Client client = Client.create();
            WebResource monitoringWR = client.resource("http://" + ip + ":7070/data");
            System.out.println("Initializing Monitoring Endpoint:" + "http://" + ip + ":7070/data");
            return monitoringWR;
        } catch (Exception e) {
            logger.fatal("Could not create the monitoring infrastructure client", e);
            System.exit(1);
        }
        return null;

    }

    public static HashMap<String, Object[]>[] getVMs() throws XmlException {
        System.out.println("GETTING VMs");
        HashMap<String, Object[]>[] vmlist = new HashMap[3];
        for (int i = 0; i < 3; i++) {
            vmlist[i] = new HashMap<String, Object[]>();
        }
        try {

            LinkedList<InfrastructureProvider> ips = serviceManager.getProvidersData(serviceId);
            for (InfrastructureProvider ip : ips) {
                String ipName = ip.getId();
                String ipv4 = ip.getIpAddress();
                System.out.println("\t " + ipName + "-" + ipv4);
                Vm[] vms = ip.getVms().getVmArray();
                for (Vm vm : vms) {
                    String IPv4 = vm.getId();
                    System.out.println("\t\t " + IPv4);
                    MachineStatus currentStatus;
                    if (vm.getStatus().toUpperCase().compareTo("READY") == 0) {
                        currentStatus = MachineStatus.READY;
                    } else if (vm.getStatus().toUpperCase().compareTo("TOBESAVED") == 0) {
                        currentStatus = MachineStatus.TOBESAVED;
                    } else if (vm.getStatus().toUpperCase().compareTo("SAVED") == 0) {
                        currentStatus = MachineStatus.SAVED;
                    } else {
                        currentStatus = MachineStatus.NOT_DEFINED;
                    }
                    Instance instance = instances.get(IPv4);
                    if (instance == null) {
                        System.out.println("\t\t\tInstance seen for the first time");
                        String instanceName = vm.getType();
                        instance = createInstance(IPv4, instanceName, ipName, ipv4);
                        instances.put(IPv4, instance);
                    }

                    MachineStatus oldStatus = instance.status;
                    if (currentStatus == MachineStatus.READY) {
                        if (oldStatus == null) {
                            System.out.println("\t\t\tUnused VM. Starting...");
                            //New VM detected
                            boolean available = true;
                            if (instance.component == null) { //Machine out of the service system
                                instance.status = MachineStatus.READY;
                            } else {
                                HashMap<String, LocationProperties> properties = instance.component.locations;
                                if (properties != null) {
                                System.out.println("\t\t\tComponent has "+properties.size()+" locations");
                                    System.out.println("\t\t\t Locations:");
                                    for (java.util.Map.Entry<String, LocationProperties> e : properties.entrySet()) {
                                        String location = e.getKey();
                                        LocationProperties prop = e.getValue();
                                        String user = prop.user;
                                        int slots = prop.slots;
                                        if (slots > 0) { //if its not a master
                                            String resourceName = getResourceName(IPv4, location);
                                            System.out.println("\t\t\t *" + resourceName);
                                            Resource resource = instance.resources.get(resourceName);
                                            if (resource == null) {
                                                if (connectionAvailable(resourceName, user)) {
                                                    resource = new Resource(resourceName);
                                                    resource.instance = instance;
                                                    resources.put(resourceName, resource);
                                                    instance.resources.put(resourceName, resource);
                                                } else {
                                                    available = false;
                                                }
                                            } //else It was already available
                                        }// else it's a useless resource. No need to do anything
                                    }
                                    System.out.println("\t\t\t End resources analysis:");
                                    long deploymentTime = System.currentTimeMillis() - instance.firstSeenTimestamp;
                                    String timeString = "";
                                    try {
                                        String xml = vm.toString();
                                        int init = xml.indexOf("<ser:deployment_duration_in_ms>") + 31;
                                        int end = xml.indexOf("</ser:deployment_duration_in_ms>");
                                        timeString = xml.substring(init, end);
                                    } catch (Exception ex) {
                                    }

                                    deploymentTime = deploymentTime + Long.parseLong(timeString);
                                    deploymentTime += 20000l;
                                    
                                    if (available) {
                                        System.out.println("\t\t\tis Available");
                                        instance.status = MachineStatus.READY;
                                        instance.deploymentTime = deploymentTime;
                                        for (java.util.Map.Entry<String, LocationProperties> entry : instance.component.locations.entrySet()) {
                                            String resourceName = getResourceName(IPv4, entry.getKey());
                                            vmlist[MANDATORY].put(resourceName, entry.getValue().getArray());
                                            System.out.println("Added as a Mandatory VM to the VM list");
                                        }
                                    } else {
                                        System.out.println("\t\t\thas not been available for "+deploymentTime);
                                        if ((deploymentTime - Long.parseLong(timeString)) > 300000) {
                                            destroyInstance(instance);
                                            System.out.println("VM is not accessible. Should restart it");
                                        }
                                    }
                                }
                            }
                        }
                    } else if (currentStatus == MachineStatus.TOBESAVED) {
                        if (oldStatus == null) {
                            destroyInstance(instance);
                        } else if (oldStatus == MachineStatus.READY) {
                            instance.status = MachineStatus.TOBESAVED;
                            for (String resourceName : instance.resources.keySet()) {
                                vmlist[TERMINATE].put(resourceName, null);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vmlist;
    }

    private static String getResourceName(String IPv4, String location) {
        if (location.compareTo("") == 0) {
            return IPv4;
        } else {
            return "http://" + IPv4 + location;
        }
    }

    private static Instance createInstance(String IPv4, String instanceName, String ipName, String ipIPv4) {
        Instance instance = new Instance(IPv4);
        instance.instanceName = instanceName;

        IP ipStored = ips.get(ipName);
        if (ipStored == null) {
            ipStored = new IP();
            ipStored.name = ipName;
            ipStored.IPv4 = ipIPv4;
            ips.put(ipName, ipStored);
        }
        instance.provider = ipStored;

        String componentName = instanceName.substring(instanceName.indexOf(OPTIMIS_PREFIX) + OPTIMIS_PREFIX.length(), instanceName.lastIndexOf(OPTIMIS_SUFFIX));
        System.out.println("\t\tComponent Name ="+componentName);
        instance.component = components.get(componentName);
        if (instance.component != null) {
            instance.component.instances.put(IPv4, instance);
        }
        return instance;
    }

    public static void terminateResource(String resourceName) {
        System.out.println("Terminating resource:" + resourceName);
        int resourceCount = resources.size();
        Resource resource = resources.remove(resourceName);
        System.out.println("\tTotal Resource Count " + resourceCount + "-->" + resources.size());


        Instance instance = resource.instance;
        resourceCount = instance.resources.size();
        instance.resources.remove(resourceName);
        System.out.println("\tInstance Resource Count " + resourceCount + "-->" + instance.resources.size());

        if (instance.resources.isEmpty()) {
            destroyInstance(instance);
        } else {
            for (String location : instance.resources.keySet()) {
                System.out.println("\t\tLocation " + location);
            }
        }
    }

    private static void destroyInstance(Instance instance) {
        System.out.println("\t\tDestroying instance: " + instance.instanceName + "-" + instance.IPv4);
        int instanceCount = instances.size();
        instances.remove(instance.IPv4);
        System.out.println("\t\t\t Total instance Count " + instanceCount + "->" + instances.size());
        Component comp = instance.component;
        instanceCount = comp.instances.size();
        comp.instances.remove(instance.IPv4);
        System.out.println("\t\t\t Component instance Count " + instanceCount + "->" + instances.size());
        for (String key : comp.instances.keySet()) {
            System.out.println("\t\t\t\t " + key);
        }
        System.out.println("\t\t\t\t Trying to remove " + instance.IPv4);
        for (String resource : instance.resources.keySet()) {
            resources.remove(resource);
        }
        serviceManager.updateVmStatus(serviceId, instance.provider.name, instance.IPv4, "Saved");
    }

    public static void submitMonitoring(HashMap<String, Integer[]> resourceToCoreCount, long[] coreMeanExecutionTime, long timeStamp) {

        for (Component comp : components.values()) {
            int compCoreCount = 0;
            long compCoreTime = 0;
            Instance representative = null;
            for (Instance instance : comp.instances.values()) {
                if (representative == null || instance.status == MachineStatus.READY) {
                    representative = instance;
                }
                int coreCount = 0;
                for (Resource resource : instance.resources.values()) {
                    Integer[] resourceCount = resourceToCoreCount.get(resource.location);
                    for (int i = 0; i < Core.coreCount; i++) {
                        coreCount += resourceCount[i];
                        compCoreTime += resourceCount[i] * coreMeanExecutionTime[i];
                    }
                }
                compCoreCount += coreCount;
                postMonitoring(instance, "coreCount-" + comp.name, coreCount, timeStamp);
            }
            long avgCoreTime = 0l;
            if (compCoreCount == 0) {
                avgCoreTime = 100l;
            } else {
                avgCoreTime = compCoreTime / compCoreCount;
            }
            if (representative == null) {
                continue;
            }
            postMonitoring(representative, "coreTime-" + comp.name, (int) avgCoreTime, timeStamp);
            if (representative.deploymentTime != null) {
                postMonitoring(representative, "coreVMDeploymentTime-" + comp.name, representative.deploymentTime.intValue(), timeStamp);
            } else {
                postMonitoring(representative, "coreVMDeploymentTime-" + comp.name, (int) (System.currentTimeMillis() - representative.firstSeenTimestamp), timeStamp);
            }
        }
    }

    private static void postMonitoring(Instance instance, String measureTag, int value, long timeStamp) {
        WebResource monitoringWR = instance.provider.monitoringEndpoint;
        if (monitoringWR == null) {
            try {
                monitoringWR = initMonitoringEndpoint(instance.provider.IPv4);
                Measurement measurement = new Measurement(serviceId, instance.instanceName, measureTag, "" + value, timeStamp);
                ClientResponse response = monitoringWR.path(serviceId).post(ClientResponse.class, measurement);
                if (response.getClientResponseStatus().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                    System.out.println("Service: " + serviceId + " instance: " + instance.instanceName + " name: " + measureTag + " value: " + value + " date: " + timeStamp);
                } else {
                    System.out.println("Error sending to monitoring --> Service: " + serviceId + " instance: " + instance.instanceName + " name: " + measureTag + " value: " + value + " date: " + timeStamp);
                    logger.error("Failed to send monitoring data to the server: " + response);
                }
            } catch (Exception e) {
                System.out.println("Error sending to monitoring --> Service: " + serviceId + " instance: " + instance.instanceName + " name: " + measureTag + " value: " + value + " date: " + timeStamp);
            }
        }
    }

    private static boolean connectionAvailable(String resourceName, String user) {

        String[] command;
        if (resourceName.startsWith("http://")) {
            command = new String[]{"/bin/sh", "-c", "wget " + resourceName};
        } else {
            command = new String[]{"/bin/sh", "-c", "ssh " + user + "@" + resourceName + " ls"};
        }
        System.out.println("--------------- Connection Available??----------");
        System.out.println("USER:" + System.getProperty("user.name"));
        System.out.println("COMMAND:" + command[2]);
        System.out.println("--------------- Connection Available  END----------");
        Process p;
        int exitValue = -1;
        try {
            p = Runtime.getRuntime().exec(command);
            Thread.sleep(5000);
            exitValue = p.exitValue();

        } catch (IllegalThreadStateException ex) {
            exitValue = -1;
        } catch (IOException ex) {
            exitValue = -1;
        } catch (InterruptedException ex) {
            exitValue = -1;
        }
        return (exitValue == 0);
    }

    private static class IP {

        String name;
        String IPv4;
        WebResource monitoringEndpoint;
    }

    private static class Component {

        String name;
        HashMap<String, LocationProperties> locations;
        HashMap<String, Instance> instances;

        public Component(String name) {
            this.name = name;
            this.instances = new HashMap<String, Instance>();
        }
    }

    private static class LocationProperties {

        int slots;
        LinkedList<Integer> cores;
        String user;
        String iDir;
        String wDir;
        HashMap<String,String> sharedDisks;

        private Object[] getArray() {
            Object[] values = new Object[6];
            values[0] = slots;
            values[1] = cores;
            values[2] = user;
            values[3] = iDir;
            values[4] = wDir;
            values[5] = sharedDisks;
            return values;
        }
    }

    private static class Instance {

        IP provider;
        Component component;
        String IPv4;
        String instanceName;
        Long firstSeenTimestamp;
        Long deploymentTime;
        MachineStatus status;
        HashMap<String, Resource> resources;

        public Instance(String IPv4) {
            this.IPv4 = IPv4;
            this.firstSeenTimestamp = System.currentTimeMillis();
            this.resources = new HashMap<String, Resource>();
        }
    }

    private static class Resource {

        String location;
        Instance instance;

        public Resource(String location) {
            this.location = location;
        }
    }

    /*private static void printStructures() {
     System.out.println("Printing structures");
     System.out.println("Instances:");
     for (java.util.Map.Entry<String, Instance> e : instances.entrySet()) {
     System.out.println("\t" + e.getKey());
     System.out.println("\t\t InstanceName:" + e.getValue().instanceName);
     if (e.getValue().component != null) {
     System.out.println("\t\t Component:" + e.getValue().component.name);
     } else {
     System.out.println("\t\t Component: Master");
     }
     System.out.println("\t\t First seen:" + e.getValue().firstSeenTimestamp);
     System.out.println("\t\t Deployment Time:" + e.getValue().deploymentTime);
     System.out.println("\t\t Provider:" + e.getValue().provider.ipName);
     System.out.println("\t\t Status:" + e.getValue().status);

     System.out.println("\t\t Resources:" + e.getValue().resources.keySet());
     }
     System.out.println("Resources");
     for (String resource : resources.keySet()) {
     System.out.println("\t" + resource);
     }
     System.out.println("Components");
     for (java.util.Map.Entry<String, Component> component : components.entrySet()) {
     System.out.println("\t" + component.getKey());
     System.out.println("\t" + component.getValue().instances);
     }
     }*/
}
