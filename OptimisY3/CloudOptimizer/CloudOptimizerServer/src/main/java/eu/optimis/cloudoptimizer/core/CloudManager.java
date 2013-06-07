/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.core;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import elasticityRestinterface.RestInterface;
import eu.optimis.DataManagerClient.DataManagerClient;
import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.util.Config;
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.faulttoleranceengine.rest.client.FaultToleranceEngineRESTClient;
import eu.optimis.manifest.api.ip.*;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.treccommon.TrecApiIP;
import eu.optimis.vmmanager.rest.client.VMManagerRESTClient;
import net.emotivecloud.utils.ovf.EmotiveOVF;
import net.emotivecloud.utils.ovf.OVFNetwork;
import net.emotivecloud.utils.ovf.optimis.OptimisOVF;
import net.emotivecloud.utils.ovf.optimis.OptimisReader;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * <b>Cloud Optimizer</b>
 * The Cloud Optimizer (CO) combines the monitoring and assessment tools in the OPTIMIS Base Toolkit with 
 * various management engines in order to create a self-managed Cloud infrastructure driven by provider’s 
 * high-level objectives (i.e. BLOs).
 * 
 * Moreover, it is the entry point of OPTIMIS IPs for deploying / undeploying services. It also receives 
 * corrective management actions from the Fault Tolerance Engine and the Elasticity Engine
 * 
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 * 
 */
public class CloudManager {

    private static Logger log = null;
  
    protected static final int TIME_WAITING_TASKS_PM = 20000; //msec
    protected static final long TIMEOUT_ECO_INFR = 150000; // no son timeouts, son intervalos de polling
    protected static final long TIMEOUT_COST_INFR = 150000;
    protected static final double DEFAULT_SERVICE_AVAILABILITY = 0.95;
    private HashMap<String, String> servicesIPManifest;
    private HashMap<String, String> servicesSPowner;
    private List<String> undeployedVMsId;
    private String LocalIPid;
    private Outsourcing out;
    protected HolisticManager hm;
    VMManagerRESTClient vmm;
    //xxx RestInterface ee;
    FaultToleranceEngineRESTClient fte;
    TrecApiIP trec;
    ServiceDeployer sd;
    DataManagerClient dm;

    private BusinessDescription blos;

    public CloudManager() {
        try {
            if (log == null) {
                log = Log.getLogger();
            }

            log.info("Starting communication with TREC common API...");
            trec = new TrecApiIP(Config.getString("config.ipvm_host"), Integer.parseInt(Config.getString("config.ipvm_port")));

            out = new Outsourcing(this, trec);
            log.info("Outsourcing component has been started.");
            servicesIPManifest = new HashMap<String, String>();
            servicesSPowner = new HashMap<String, String>();
            undeployedVMsId = new LinkedList<String>();

            Connection conn = null;
            try {
                /**
                // ERASE THIS, only testing
                Log.getLogger().info("testing connection to DB");
                String CONFIG_FILE_PATH = System.getenv("OPTIMIS_HOME") + "/etc/CloudOptimizer/config.properties";
                Log.getLogger().info("Loading properties at " + CONFIG_FILE_PATH);
                Properties properties = new Properties();
                properties.load(new FileInputStream(CONFIG_FILE_PATH));
                String location = properties.getProperty("db.location"); //database location
                String DB_DRIVER = properties.getProperty("db.driver");
                String DB_USER = properties.getProperty("db.username");
                String DB_PASSWORD = properties.getProperty("db.password");
                String URL = properties.getProperty(location + ".url");
                Log.getLogger().info("location: " + location + "\ndriver: " + DB_DRIVER + "\nuser: " + DB_USER + "\npassword: " + DB_PASSWORD + "\nurl: " + URL);
                Class.forName(DB_DRIVER);
                Log.getLogger().info("getting connection to bd...");
                conn = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
                Log.getLogger().info(conn.toString());
                Log.getLogger().info("getting ip id...");
                String sqlStatement = "SELECT id FROM infrastructure_provider";
                String ret = "";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sqlStatement);
                while (rs.next()) {
                    ret = rs.getString("id");
                }
                Log.getLogger().info("IP ID FROM DB: " + ret);
                conn.close();
                // end of ERASE THIS
                */

                conn = DBUtil.getConnection();
                LocalIPid = Queries.getIpId(conn);
                log.info("Infrastructure Provider ID = '" + LocalIPid + "'");
            } catch (SQLException ex) {
                log.error(Log.getStackTrace(ex));
            } catch (Error e) {
                log.error(Log.getStackTrace(e));
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            vmm = new VMManagerRESTClient(Config.getString("config.ipvm_host"), Integer.parseInt(Config.getString("config.ipvm_port")));
            hm = new HolisticManager(this);
            fte = new FaultToleranceEngineRESTClient();
            //xxx ee = new RestInterface();
            sd = new ServiceDeployer(this);


            DefaultClientConfig config = new DefaultClientConfig();
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            dm = new DataManagerClient();
            dm.specifyObjective(LocalIPid, DataManagerClient.TREC_OBJECTIVE_FUNCTION_TRUST,  2,5,2);

            hm.setBasicMode();

            log.info("CO started successfully.");
        } catch (Throwable t) {
            Log.getLogger().error(Log.getStackTrace(t));
            throw new RuntimeException(t);
        }
    }

    public String getLocalIPid() {
        return LocalIPid;
    }

    //lastTimeBeforeDeployment: used for calculating deployment time
    private void sendNewVMNotifications(String serviceId, String vmName, String vmState, OptimisOVF ovf, ServiceManagerClient sm_client, long lastTimeBeforeDeployingMS, String comments, VMtype type) {
        EmotiveOVF emotiveOVF = new EmotiveOVF(ovf);
        addVirtualResource(new OptimisOVF(emotiveOVF), serviceId, emotiveOVF.getProductProperty("VM.destination.host"), emotiveOVF.getProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME), type, comments); //"true" because it is a deployment and the virtual resource's type must be 'base'
        log.info("New VM instance of " + serviceId.concat(".").concat(vmName) + "; Total instances = " + getNrInstances(serviceId, vmName));
        log.info("Adding a new VM into Service Manager resource.");

        //Applying patch for the static mapping between public and private IPs in ATOS testbed
        if (Config.getString("db.location").contains("atos")) {
            String vm_ip_address = Config.getString("support.atos.public.ip.mapping." + emotiveOVF.getNetworks().get("private").getIp());
            sendNotificationToSM_AddVM(serviceId, LocalIPid, vm_ip_address, vmName, vmState, sm_client,lastTimeBeforeDeployingMS);
        } else {
            sendNotificationToSM_AddVM(serviceId, LocalIPid, emotiveOVF.getNetworks().get("public").getIp(), vmName, vmState, sm_client,lastTimeBeforeDeployingMS);
        }
    }

    /**
     * Creates VMs using VirtualSystem of an incarnated virtual machine component
     * @param imageId "ALL" --> create all VMs described into the manifest; "id" ---> only create the image with this id
     */
    protected String createVM(String serviceId, String componentId, VirtualSystem vmDef, OptimisOVF ovf, String trec_opt, ServiceManagerClient sm_client, String comments, VMtype type) {
        String vm_uuid = "";
        try {
            //used for calculating deployment time
            long lastTimeBeforeDeployment = System.currentTimeMillis();
            EmotiveOVF emotiveOVF = new EmotiveOVF(ovf);

            String vmName = vmDef.getId();

            if (trec_opt == null || trec_opt.equals("false")) {
                setOptimumAllocationPatternIntoOVF(emotiveOVF, serviceId, componentId);        //Putting into ovf with information of destination hosts
            }
            emotiveOVF.setProductProperty(EmotiveOVF.PROPERTYNAME_VM_NAME, vmName);

            vm_uuid = UUID.randomUUID().toString();
            emotiveOVF.setId(vm_uuid);  //Setting an uuid as the vm identifier

            emotiveOVF = setEmotiveOVFNetworks(vmDef, emotiveOVF, vmName);   //Setting networks

            // Sets the service ID. It will be used by VMM when contacting TREC
            emotiveOVF.setProductProperty("serviceId",serviceId);
            //Going to create the VM
            log.info("Going to add the OVF with ID=" + emotiveOVF.getId());

            ovf = new OptimisOVF(vmm.addVM(emotiveOVF.toString(), trec_opt));

            //Sending VM-level notifications
            sendNewVMNotifications(serviceId, vmName, "Ready", ovf, sm_client, lastTimeBeforeDeployment, comments, type);

        } catch (Exception ex) {                        
            log.error(Log.getStackTrace(ex));
        }
        return vm_uuid;
    }

    /*
     * Allows adding new VMs
     * @num: number of VMs to be added
     * This method is created for compatibility with elasticityCallback. For use within CO, use suggestAddVM method.
      *
     */
    public void addVM(String serviceId, String serviceManifest, String compId_imageId, int numRequested, String SP_addr, String comments) {
        suggestAddVM(serviceId,serviceManifest,compId_imageId,numRequested,SP_addr,comments,true, VMtype.elasticity);
    }

    public CODecision suggestAddVM(String serviceId, String serviceManifest, String compId_imageId, int numRequested, String SP_addr, String comments, boolean canBurst, VMtype type) {
        CODecision where = CODecision.ACCEPT_LOCAL;
        try {
            ServiceManagerClient sm_client = new ServiceManagerClient(SP_addr, Config.getString("config.spvm_port"));
            Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);
            serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();
            if (!servicesIPManifest.containsKey(serviceId)) {
                servicesIPManifest.put(serviceId, serviceManifest);
            }

            log.info("Going to add a new VM of the component Id / image id '" + compId_imageId + "'...");
            //TODO Y3
            //VirtualMachineComponent virtualMachineComponentById = ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(compId);
            //int maxVMs = virtualMachineComponentById.getAllocationConstraints().getUpperBound();
            
            for (int k = 0; k < numRequested; k++) {    // for each VM requested...
                String imageId = "";
                if (compId_imageId.contains("system")) { // receiving vm hostname
                    imageId = compId_imageId;
                    IncarnatedVirtualMachineComponent[] incarnatedVirtualMachineComponents = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents();
                    for (IncarnatedVirtualMachineComponent incarnatedVM : incarnatedVirtualMachineComponents) { // one per service component
                        String componentId = incarnatedVM.getComponentId();
                        OptimisReader ow = new OptimisReader(new ByteArrayInputStream(serviceManifest.getBytes("UTF-8")), componentId);
                        OptimisOVF[] ovfs = ow.getOvfs();
                        VirtualSystem[] virtualSystemArray = incarnatedVM.getOVFDefinition().getVirtualSystemArray();
                        for (VirtualSystem v : virtualSystemArray) {
                            if (v.getId().equals(imageId)) {
                                char lastChar = imageId.charAt(imageId.length() - 1);
                                String num = "" + lastChar;
                                OptimisOVF ovf = ovfs[Integer.parseInt(num)];

                                if(canBurst) {
                                    where = out.decideVMPlacement(ovf);
                                }
                                if(CODecision.ACCEPT_LOCAL.equals(where)) {
                                    createVM(serviceId, componentId, v, ovf, "true", sm_client, comments, type);
                                } else {
                                    Manifest singleVMManifest = Manifest.Factory.newInstance(serviceManifest);
                                    singleVMManifest = ipManifest.extractComponent(componentId);
                                    out.outsourceVMs(singleVMManifest.toString());
                                }
                            }
                        }
                    }
                } else { // receiving component id
                    IncarnatedVirtualMachineComponent[] incarnatedVirtualMachineComponents = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents();
                    for (IncarnatedVirtualMachineComponent incarnatedVM : incarnatedVirtualMachineComponents) { // one per service component
                        String componentId = incarnatedVM.getComponentId();
                        if (componentId.equals(compId_imageId)) {
                            OptimisReader ow = new OptimisReader(new ByteArrayInputStream(serviceManifest.getBytes("UTF-8")), componentId);
                            OptimisOVF[] ovfs = ow.getOvfs();
                            int auxaux = Integer.parseInt(getNrInstances(serviceId, componentId));
                            log.info("Going to get Virtual System to use. Index = " + auxaux);

                            try {
                                VirtualSystem vmDef = incarnatedVM.getOVFDefinition().getVirtualSystemArray(auxaux);
                                OptimisOVF ovf = ovfs[Integer.parseInt(getNrInstances(serviceId, componentId))];

                                if(canBurst) {
                                    where = out.decideVMPlacement(ovf);
                                }
                                if(CODecision.ACCEPT_LOCAL.equals(where)) {
                                    createVM(serviceId, componentId, vmDef, ovf, "true", sm_client, comments, type);
                                } else {
                                    Manifest singleVMManifest = Manifest.Factory.newInstance(serviceManifest);
                                    singleVMManifest = ipManifest.extractComponent(componentId);
                                    log.info(componentId + " from service " + serviceId + " will be outsourced.");
                                    out.outsourceVMs(singleVMManifest.toString());
                                }
                            } catch(IndexOutOfBoundsException ex) {
                                Log.getLogger().warn("Trying to add more VMs than the actually allowed in the manifest. Ignoring.");
                            }
                        }
                    }
                }
                break;

//                    case ACCEPT_REMOTE:
//                        log.info("Bursting to an external provider");
//                        out.outsourceVMs(ipManifest.toString());
//                        break;
            }
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 425) {   //EMOTIVE (VtM) notifies that there aren't enough resources
                log.info("EMOTIVE exception: there aren't enough resources in the private cloud.");
                //TODO Y3 - sdo.deploy(serviceManifest);
            }
        } catch (Exception e) {
            log.error("Exception when adding a new VM: " + Log.getStackTrace(e));
            e.printStackTrace();
        }

        return where;
    }

    /*
     * Allows removing VMs (If any, vmId = null)
     * @num: number of VMs to be removed
     */
    public void removeVM(String serviceId, String compId_imageId, int num, String SP_addr, boolean saveVM) {
        ServiceManagerClient sm_client = new ServiceManagerClient(SP_addr, Config.getString("config.spvm_port"));
        Connection conn = DBUtil.getConnection();
        String manifestByServiceId = servicesIPManifest.get(serviceId);
        String vmIPaddr = "";
        log.info("Going to remove " + num + " VM(s) of service '" + serviceId + "'. comp Id / image id = " + compId_imageId);
        Set<String> imageIds = new HashSet<String>();

        if (compId_imageId.contains("system")) { // receiving vm hostname
            imageIds.add(compId_imageId);
        } else { // receiving component id
            Manifest ipManifest = Manifest.Factory.newInstance(manifestByServiceId);
            IncarnatedVirtualMachineComponent[] incarnatedVirtualMachineComponents = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents();
            for (IncarnatedVirtualMachineComponent incarnatedVM : incarnatedVirtualMachineComponents) { // one per service component
                String componentId = incarnatedVM.getComponentId();
                if (componentId.equals(compId_imageId)) {
                    // selecting the "num" latests instances to remove
                    int totalInstances = Integer.parseInt(getNrInstances(serviceId, compId_imageId));
                    StringBuffer sb = new StringBuffer("Going to remove the next VMs: ");
                    for(int vmIndex = 1 ; vmIndex <= num ; vmIndex++) {
                        String id = incarnatedVM.getOVFDefinition().getVirtualSystemArray(totalInstances - vmIndex).getId();
                        sb.append("\n\t").append(id);
                        imageIds.add(id);
                    }
                }
            }
        }
        
        // This only has to be done if detecting anticipated failure
        for(String imageId : imageIds) {
            try {
                String vmId = Queries.getVMId(conn, imageId);
                vmIPaddr = Queries.getVMPublicIP(conn, vmId);
                Log.getLogger().debug("Public IP of " + vmId + ": " + vmIPaddr);

                // patch: vms only have to be saved when anticipate vm failure (not when actual vm failure)
                // and only in the programming model use case
                if (saveVM && imageId.contains("optimis-pm")) {
                    log.info("VM of PM.");

                    log.info("Going to update VM status of vmId = '" + vmId + "' with public IP = " + vmIPaddr + " (SM address: "+ SP_addr+ ")");
                    //Y3 SM to fix this - sm_client.updateVmStatus(serviceId, LocalIPid, Queries.getVMPublicIP(conn, vmId), "ToBeSaved");

                    //sm_client.updateVmStatus(serviceId, LocalIPid, vmIPaddr, "ToBeSaved");
                            // recordar que en vez de vmId, va la ip address
                            // borrar esto siguiente, cuando se haya cambiado por el updateVMStatus
                            // recordarle a jorge que este cambio que voy a hacer es del Y3...
                    myUpdateVmStatus(serviceId, SP_addr, vmIPaddr);

                    log.info("Updated!. Waiting for VM status...");

                    boolean ok = false;
                    while (!ok) {
                        Thread.sleep(TIME_WAITING_TASKS_PM);
                        //String status = sm_client.getVmStatus(serviceId, LocalIPid, vmIPaddr);
                        String status = myGetVmStatus(serviceId, SP_addr, vmIPaddr);
                        log.info("Current VM status is '" + status + "'");
                        if (status.equals("Saved")) {
                            ok = true;
                        }
                    }
                }

                String id = Queries.getVMId(conn, imageId);
                log.info("Removing VM with id = " + id + " and hostname = " + imageId);
                if (!undeployedVMsId.contains(id)) {
                    for (int i = 0; i < num; i++) {
                        Queries.deleteVirtualResource(conn, id);
                        vmm.removeVM(id);
                        //Y3 SM to fix this - sm_client.deleteVm(serviceId, LocalIPid, Queries.getVMPublicIP(conn, id));
                        try {
                            sm_client.deleteVm(serviceId,getLocalIPId(),vmIPaddr);
                        } catch(Exception e) {
                            Log.getLogger().warn("ServiceManager can't remove the VM "+ id +": " + e.getMessage());
                        }
    //                    myDeleteVm(serviceId, SP_addr, vmIPaddr);
                        undeployedVMsId.add(id);
                    }
                } else {
                    log.warn("Trying to remove the VM = " + id + ", but it has been already undeployed. Cloud Optimizer omits the call.");
                }
            } catch (Exception ex) {
                log.error("Exception when removing a VM", ex);
            }
        }

        try {
            if(conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private EmotiveOVF setEmotiveOVFNetworks(VirtualSystem vmDef, EmotiveOVF emotiveOVF, String vmName) {
        emotiveOVF.getNetworks().clear();
        //Applying patch in case of atos testbed - private IPs are already mapped to public IPs
        if (!Config.getString("db.location").contains("atos")) {
            emotiveOVF.getNetworks().put("public", new OVFNetwork("public", null, null));
        } else {
            emotiveOVF.getNetworks().put("private", new OVFNetwork("private", null, null));
        }
        return emotiveOVF;
    }

    public String getNrInstances(String serviceId, String imageId) {
        int ret = 0;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            List<String> vMsIdsOfService = Queries.getVMsIdsOfService(conn, serviceId);
            for (String id : vMsIdsOfService) {
                String name = Queries.getVMName(conn, id);
//                log.info("Retrieving num of vms. VM id = " + id + "; VM name = " + name + "; componentId = " + imageId);
                if (name.contains(imageId)) {
                    ret++;
                }
            }
        } catch (SQLException ex) {
            log.error(Log.getStackTrace(ex));
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return ret + "";
    }

    private void sendNotificationToSM_AddVM(String serviceId, String IP_id, String vm_ip_address, String type, String status, ServiceManagerClient sm_client, long lastTimeBeforeDeployingMS) {
        //Applying patch in case of atos testbed - private IPs are already mapped to public IPs
        log.info("Notification to Service Manager. New VM in IP = '" + IP_id + "' with id = '" + vm_ip_address + "'; type = '" + type + "'; status = '" + status + "' --- assigned to the service '" + serviceId + "'");
        long deploymentTime = System.currentTimeMillis() - lastTimeBeforeDeployingMS;
        sm_client.addVm(serviceId, IP_id, vm_ip_address, type.toString(), status, (int) deploymentTime);
    }

    protected void startEE(String serviceId, String serviceManifest, String SP_IPaddr) {
        // LowRiskMode parameter to false, the EE works in Low cost / High risk mode (reactive only mode)
        boolean mode = false;
        if(blos != null) {
            mode = blos.getObjective().getType().equals(ObjectiveType.MAX_TRUST)
                    || blos.getObjective().getType().equals(ObjectiveType.MIN_RISK);
        }

        try {
            new RestInterface().startElasticity(serviceId, serviceManifest, mode, SP_IPaddr);
        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        }
    }

    protected void startFTE(String serviceId, String serviceManifest) {
        log.info("Setting FTE policy for the new service...");
        fte.setPolicy(serviceId, Double.toString(DEFAULT_SERVICE_AVAILABILITY));
        fte.newServiceDeployed(serviceId, serviceManifest);
    }

    protected void stopEE(String serviceId) {
        try {
            new RestInterface().stopElasticity(serviceId);
        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        }
    }

    protected void stopFTE(String serviceId) {
        fte.newServiceUndeployed(serviceId);
    }

    protected void startMonitoring(String serviceId) {
//        log.info("Starting monitoring...");        
//        WebResource resource = client.resource("http://" + Config.getString("config.ipvm_host") + ":" + Config.getString("config.ipvm_port") + "/MonitoringManager/control/startmonitoring");
//        resource.post();
    }

    protected void stopMonitoring(String serviceId) {
//        log.info("Stopping monitoring...");        
//        WebResource resource = client.resource("http://" + Config.getString("config.ipvm_host") + ":" + Config.getString("config.ipvm_port") + "/MonitoringManager/control/stopmonitoring");
//        resource.post();
    }

    public enum VMtype {
        local, remote, elasticity
    }
    /**
     * Inserts a new record into the virtual resource table
     * @param ovf
     * @param VMtype - true = basic / false = elastic
     */
    private void addVirtualResource(OptimisOVF ovf, String serviceId, String hostId, String name, VMtype type, String comments) {
        try {
            String ip_public = "";
            String ip_private = "";
            VirtualResource v = new VirtualResource();

            if (Config.getString("db.location").contains("atos")) {
                ip_private = ovf.getNetworks().get("private").getIp();
                ip_public = Config.getString("support.atos.public.ip.mapping." + ip_private);
            } else {
                ip_public = ovf.getNetworks().get("public").getIp();
                ip_private = "NoPrivateIP";
            }

            v = new VirtualResource(ovf.getId(), name, hostId, serviceId, type.toString(), "xen",
                    1 + "", ovf.getCPUsNumber() + "",
                    ovf.getMemoryMB() / 1024 + "", "centos",
                    "network_adapter", ip_public,
                    ip_private, comments);

            log.info("Adding the following virtual resource = " + v.toXml().toString());
            Connection conn = DBUtil.getConnection();
            Queries.insertVirtualResource(conn, v);
            conn.close();
        } catch (SQLException ex) {
            log.error(Log.getStackTrace(ex));
        }
    }

    /**
     * @param manifest - String representation of manifest (XML)
     * @return allocation pattern - String (XML)
     */
    private void setOptimumAllocationPatternIntoOVF(EmotiveOVF ovf, String serviceId, String componentId) {

        String m = servicesIPManifest.get(serviceId);
        Manifest manifest = Manifest.Factory.newInstance(m);
        AllocationPattern[] allocPatternFromAC = manifest.getInfrastructureProviderExtensions().getAllocationOffer().getAllocationPatternArray();

        log.info("Looking for allocation pattern for component '"+componentId+"' from service '"+serviceId+"'");

        boolean modified = false;
        for (AllocationPattern a : allocPatternFromAC) {
            String compId = a.getComponentId();
            if(componentId.equals(compId)) {
                modified = true;
                PhysicalHost physicalHostArray = a.getPhysicalHostArray(0);
                log.info("AC allocation: Optimal host for component '" + compId + "' is = '" + physicalHostArray.getHostName() + "'.");
                ovf.setProductProperty("VM.destination.host", physicalHostArray.getHostName());
            }
        }
        if(!modified) {
            StringBuilder sb = new StringBuilder("AC didn't provided any allocation for component ").append(componentId);
            sb.append(". Provided ones:");
            for(AllocationPattern a  : allocPatternFromAC) {
                String compId = a.getComponentId();
                sb.append("\n\t").append(compId);
            }
            Log.getLogger().warn(sb.toString());

        }
//        servicesIPManifest.put(serviceId, manifest.toString());   //update service manifest with allocation pattern into OVF
    }

    private String parseRequiredAvailability(String serviceId) {
        String l = null;
        try {
            Manifest ipManifest = Manifest.Factory.newInstance(servicesIPManifest.get(serviceId));
            Availability[] availabilityArray = ipManifest.getTRECSection().getRiskSectionArray(0).getAvailabilityArray();
            for (Availability avail : availabilityArray) {
                if (avail.getAssessmentInterval().equals("P1D")) {
                    l = Double.toString(avail.getValue());
                    log.info("AVAIL required = " + l);
                }
            }
        } catch (Exception e) {
            log.error(Log.getStackTrace(e),e);
        }
        return l;
    }

    public HashMap<String, String> getServicesIPManifest() {
        return servicesIPManifest;
    }

    public HashMap<String, String> getServicesSPowner() {
        return servicesSPowner;
    }

    public VMManagerRESTClient getVMM() {
        return vmm;
    }

    public RestInterface getEE() {
        return new RestInterface();
    }

    public FaultToleranceEngineRESTClient getFTE() {
        return fte;
    }

    public TrecApiIP getTREC() {
        return trec;
    }

    public ServiceDeployer getSD() {
        return sd;
    }

    protected HolisticManager getHolisticManager() {
        return hm;
    }
    
    private void myUpdateVmStatus(String serviceId, String SP_addr, String vmId) {
        Client c = Client.create();
        WebResource serviceManager = c.resource("http://" + SP_addr + ":"
                + Config.getString("config.spvm_port") + "/ServiceManager/services");
        serviceManager.path(serviceId).path("ip").path(LocalIPid).path("vms").path(vmId).path("status").put(String.class, "ToBeSaved");
    }
    
    private String myGetVmStatus(String serviceId, String SP_addr, String vmId) {
        Client c = Client.create();
        WebResource serviceManager = c.resource("http://" + SP_addr + ":"
                + Config.getString("config.spvm_port") + "/ServiceManager/services");
        return serviceManager.path(serviceId).path("ip").path(LocalIPid).path("vms").path(vmId).path("status").get(String.class);
    }
    
//    private void myDeleteVm(String serviceId, String SP_addr, String vmId) {
//        Client c = Client.create();
//        WebResource serviceManager = c.resource("http://" + SP_addr + ":"
//                + Config.getString("config.spvm_port") + "/ServiceManager/services");
//        serviceManager.path(serviceId).path("ip").path(LocalIPid).path("vms").path(vmId).delete();
//
//    }

    public String Fake() {
        return "called method fake at Cloud Optimizer";
    }

    public BusinessDescription getBLOs() {
        return blos;
    }

    public void setBLOs(BusinessDescription blos) {
        this.blos = blos;
    }

    public Outsourcing getOutsourcing() {
        return out;
    }

    
    public List<String> getUndeployedVMsId() {
        return undeployedVMsId;
    }
    
    public String getLocalIPId() {
        return LocalIPid;
    }


    public CODecision suggestVMRestart(String vmId, boolean saveVM) {
        // todo: que hi hagi possibilitat d'executar en altres nodes
        CODecision ret = null;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String vmName = Queries.getVMName(conn, vmId);
            VirtualResource v = null;
            if(vmName != null) {
                v = Queries.getVirtualResource(conn,vmId).getVirtualResource().get(0);
            }
            String serviceId = Queries.getVMServiceId(conn, vmId);

            String SP_IPaddr = servicesSPowner.get(serviceId);

            log.info("Anticipated VM failure received. VM id = " + vmId + "; VM name = " + vmName);
            removeVM(serviceId, vmName, 1, SP_IPaddr, saveVM);
            log.info("Anticipated - removed!");
            VMtype type;
            try {
                type = VMtype.valueOf(v.getType());
            } catch(Exception e) {
                type = VMtype.local;
            }
            ret = suggestAddVM(serviceId, servicesIPManifest.get(serviceId), vmName, 1, SP_IPaddr, v!=null?v.getComments():null, false, type);
            log.info("Anticipated - added!");
        } catch (SQLException ex) {
            log.error(Log.getStackTrace(ex));
        }
        try {
            if(conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public CODecision suggestAllVMsMigrate(String nodeId) {
        Log.getLogger().info("All VMs are going to be migrated for node " + nodeId);

        Connection conn = null;
        try {

            List<String> vmsIds = Queries.getVMsId(conn, nodeId);
            if(vmsIds == null || vmsIds.size() == 0) {
                Log.getLogger().warn("strange... DB returns that there are no VMs on node " + nodeId);
            } else {
                for(String v : vmsIds) {
                    if(suggestVMMigrate(v) == CODecision.ACCEPT_LOCAL) {
                        VirtualResource vr = Queries.getVirtualResource(conn, v).getVirtualResource().get(0);
                        Log.getLogger().info(v + " is migrated to " + vr.getPhysical_resource_id());
                    } else {
                        Log.getLogger().info("Placement optimizer decided to NOT migrate " + v);
                    }
                }
            }
        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        }
        return CODecision.REJECT;
    }

    public CODecision suggestVMMigrate(String vmId) {

        if(vmm.migrateVM(vmId)) {
            return CODecision.ACCEPT_LOCAL;
        } else {
            return CODecision.REJECT;
        }
    }

    public DataManagerClient getDM() {
        return dm;
    }

    public String onNodeFailure(String nodeId) {
        for(String vmId : vmm.getVMsAtNode(nodeId)) {
            suggestVMRestart(vmId, false);
        }
        return "Ack";
    }
}