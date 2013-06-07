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

import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import com.sun.jersey.api.client.UniformInterfaceException;
import eu.optimis.cloudoptimizer.util.Config;
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.ds.client.DeploymentServiceClient;
import eu.optimis.manifest.api.ip.*;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.service_manager.client.ServiceManagerClient;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import javax.ws.rs.WebApplicationException;
import net.emotivecloud.utils.ovf.optimis.OptimisOVF;
import net.emotivecloud.utils.ovf.optimis.OptimisReader;
import org.apache.log4j.Logger;


public class ServiceDeployer {
    private CloudManager cm;
    
    private OperationMode operationMode;
    
    public ServiceDeployer(CloudManager cm) {        
        this.cm = cm;      
    }
    
    /**
     * IP external interface to deploy a service
     * @returns serviceId
     */
    public String deploy(String serviceManifest, String slaId, String comments) throws IOException {
        String savePath = null;
        try {
            savePath = System.getenv("OPTIMIS_HOME")+"/var/log/optimis/LastManifest.xml";
            Log.getLogger().debug("Saving a copy of the service manifest in: " + savePath);
            FileOutputStream fos = new FileOutputStream(savePath);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(serviceManifest);
            pw.close();
            fos.close();
        } catch(Exception e) {
            // THIS IS ONLY A TEST, IT WILL BE REMOVED
        }
        Log.getLogger().info("Manifest stored in " + savePath);

        Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);
        String serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();

        try {
            Log.getLogger().info("Looking for external deployment options...");
            ExternalDeployment[] eds = ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().getExternalDeploymentArray();

            String deploymentObjective = "COST";
            if( cm.getBLOs() != null && cm.getBLOs().getObjective() != null) {
                int type = cm.getBLOs().getObjective().getType().ordinal();
                if( type == ObjectiveType.MAX_ECO.ordinal()) {
                    deploymentObjective = "ECO";
                } else if(type == ObjectiveType.MAX_ENERGY_EFF.ordinal()) {
                    deploymentObjective = "ENERGY_EFF" ;
                }else if(type == ObjectiveType.MAX_TRUST.ordinal()) {
                    deploymentObjective = "TRUST";
                } else if(type == ObjectiveType.MIN_RISK.ordinal()) {
                    deploymentObjective = "RISK";
                }
            }

            for(ExternalDeployment e : eds) {
                try {
                    DeploymentServiceClient sdo = new DeploymentServiceClient(Config.getString("config.spvm_host_sd"), Integer.parseInt(Config.getString("config.spvm_port_sd")));
                    Log.getLogger().info("Sending manifest " + e.exportServiceManifest().getManifestId() + " to " + Config.getString("config.spvm_host_sd")+":"+Config.getString("config.spvm_port_sd") );
                    if(!sdo.deploy(e.exportServiceManifest().toString(), deploymentObjective)) {
                        Log.getLogger().warn("The federated deployment of external resource did not succeed for external manifest " + e.exportServiceManifest().getManifestId());
                    }
                } catch(Exception ex) {
                    Log.getLogger().error("Exception when federating external manifest: " + Log.getStackTrace(ex));
                }
            }

        } catch(NullPointerException e) {
            Log.getLogger().info("No external deployment options found");
        }

        ServiceDeployerUndeployer sdThread = new ServiceDeployerUndeployer(serviceManifest, serviceId, slaId, true, comments);
        sdThread.start();

        return serviceId;
    }

    // NOTE: This is indirectly called by deploy() method, through the ServiceDeployerUndeployer class
    private String deploy_asynchronous(String serviceManifest, String slaId) {
        Log.getLogger().info("Looking for internal VMs to deploy...");
        String serviceId = "";
        String SP_IPaddress = "";

        List<String> vmIds = new LinkedList<String>();
        try {
            //Getting service identifier
            Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);
            serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();
            if (!cm.getServicesIPManifest().containsKey(serviceId)) {
                cm.getServicesIPManifest().put(serviceId, serviceManifest);
            }
            //Getting SP deploying that service
            VirtualMachineComponent virtualMachineComponent = ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray(0);
            ServiceEndpoint[] serviceEndpoints = virtualMachineComponent.getServiceEndpoints();
            for (ServiceEndpoint s : serviceEndpoints) {
                if (s.getName().equals("ServiceManager")) {
                    URL sm_url = new URL(s.getURI());
                    SP_IPaddress = sm_url.getHost();
                }
            }
            Log.getLogger().info("Deploying serviceId="+serviceId+" whose SP has IPadress="+SP_IPaddress);
            if (!cm.getServicesSPowner().containsKey(serviceId)) {
                cm.getServicesSPowner().put(serviceId, SP_IPaddress);
            }

            ServiceManagerClient serviceManagerClient;
            if (!SP_IPaddress.isEmpty()) {
                Log.getLogger().info("Service Manager end point obtained from manifest: " + SP_IPaddress);
                serviceManagerClient = new ServiceManagerClient(SP_IPaddress, Config.getString("config.spvm_port"));
            } else {
                Log.getLogger().info("Service Manager end point is not specified into the manifest. Using 'optimis-spvm' as the default value...");
                serviceManagerClient = new ServiceManagerClient(Config.getString("config.spvm_host"), Config.getString("config.spvm_port"));
            }
            int serviceInitialNumOfVMs = 0;
            VirtualMachineComponent[] virtualMachineComponentArray = ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray();
            for (VirtualMachineComponent vm_component : virtualMachineComponentArray) {
                serviceInitialNumOfVMs += vm_component.getAllocationConstraints().getInitial();
            }
            Log.getLogger().info("Initial number of VMs = " + serviceInitialNumOfVMs + " for service = " + serviceId);


            CODecision where = cm.getOutsourcing().decideServicePlacement(ipManifest);

            switch (where) {
                case ACCEPT_LOCAL:
                    IncarnatedVirtualMachineComponent[] incarnatedVirtualMachineComponents = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents();
                    if(incarnatedVirtualMachineComponents == null) {
                        Log.getLogger().warn("It seems that there are no in Incarnated Virtual Machine Components to be deployed locally.");
                    } else {
                        for (IncarnatedVirtualMachineComponent incarnatedVM : incarnatedVirtualMachineComponents) { // one per service component
                            String componentId = incarnatedVM.getComponentId();

                            OptimisReader ow = new OptimisReader(new ByteArrayInputStream(ipManifest.toString().getBytes("UTF-8")), componentId);
                            OptimisOVF[] ovfs = ow.getOvfs();

                            int componentNumVMs = ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(componentId).getAllocationConstraints().getInitial();
                            Log.getLogger().info("New service component '" + componentId + "', which has " + ovfs.length + " OVFs. Going to deploy " + componentNumVMs + " VMs of this type.");
                            for (int k = 0; k < componentNumVMs; k++) {
                                VirtualSystem vmDef = incarnatedVM.getOVFDefinition().getVirtualSystemArray(k);
                                OptimisOVF ovf = ovfs[k];

//                              String vmId = cm.createVM(serviceId, componentId, vmDef, ovf, "true", serviceManagerClient);
                                String vmId = cm.createVM(serviceId, componentId, vmDef, ovf, "false", serviceManagerClient, null, CloudManager.VMtype.local);
                                vmIds.add(vmId);
                            }
                        }

                        startServiceContext(serviceId, ipManifest.toString(), vmIds, serviceManagerClient, SP_IPaddress);
                    }
                    break;
                case ACCEPT_REMOTE:
                    Log.getLogger().info("Bursting to an external provider");
                    cm.getOutsourcing().outsourceVMs(serviceManifest);
                    break;
                case REJECT:
                    //Y3...
                    //for each VM in the OVF...
                    //vmm.addVM(OVF);
                    break;
            }

        } catch (WebApplicationException eapp) {
            Log.getLogger().error("WebApplicationException when deploying the service:" + eapp.getResponse().toString());
            Log.getLogger().debug(Log.getStackTrace(eapp));
            return "";
        } catch (UniformInterfaceException uni) {
            Log.getLogger().error("UniformInterfaceException when deploying the service: " + uni.getResponse().toString());
            Log.getLogger().debug(Log.getStackTrace(uni));
            return "";
        } catch (Exception e) {
            Log.getLogger().error("Exception when deploying the service: " + e.getMessage());
            Log.getLogger().debug(Log.getStackTrace(e));
            return "";
        }
        return serviceId;
    }

    /*
     * IP external interface to undeploy a service
     */
    public String undeploy(String serviceId) {
        ServiceDeployerUndeployer sdThread = new ServiceDeployerUndeployer("", serviceId, "", false, null);
        sdThread.start();
        return serviceId;
    }

    private String undeploy_asynchronous(String serviceId) {
        boolean changeStatus = true;

        //Getting SP deploying that service
        String SP_IPaddress = cm.getServicesSPowner().get(serviceId);
        Log.getLogger().debug("Undeploy_asynchronous. serviceId="+serviceId + " SP_IPaddress="+SP_IPaddress);

        ServiceManagerClient serviceManagerClient = new ServiceManagerClient(SP_IPaddress, Config.getString("config.spvm_port"));

        Connection conn = null;
        try {
            //Remove local VMs
            conn = DBUtil.getConnection();
            List<String> localVMs = Queries.getVMsIdsOfService(conn, serviceId);

            stopServiceContext(serviceId, localVMs);

            /*String[] allVMs = sm_client.getVmIdsAsArray(serviceId, LocalIPid);
            if (allVMs.length > localVMs.size()) {  // Need to notify external providers
                changeStatus = false;
            }*/

            Log.getLogger().info("Undeploying the service... with " + localVMs.size() + " VMs in the local infrastructure.");
            for (String id : localVMs) {
                Log.getLogger().info("Removing local VM with id = " + id);
                cm.getVMM().removeVM(id);
                cm.getUndeployedVMsId().add(id);
                String ip = Queries.getVMPublicIP(conn, id);
                try {
                    serviceManagerClient.deleteVm(serviceId, cm.getLocalIPId(), ip); //notification to SM
                } catch(Exception e) {
                    Log.getLogger().error("When deleting VM " + ip + ": "  + Log.getStackTrace(e));
                }
                try {
                    Queries.deleteVirtualResource(conn, id);
                } catch(Exception e) {
                    Log.getLogger().error("error when deleting vm " + id + " from database: " + Log.getStackTrace(e));
                }
            }


            //If needed, remove VMs running in external Clouds
            //else, update service status
            if (!changeStatus) {
                cm.getOutsourcing().removeBurstedVMs(serviceManagerClient.getInfrastructureProviderIds(serviceId), cm.getLocalIPId(), serviceId, "ALL", SP_IPaddress);
            } else {
                try {
                    serviceManagerClient.updateServiceStatus(serviceId, "Undeployed");
                } catch(Exception e) {
                    Log.getLogger().error("When undeploying service " +serviceId + ": " + Log.getStackTrace(e));
                }
            }

            cm.getServicesIPManifest().remove(serviceId);
            cm.getServicesSPowner().remove(serviceId);

        } catch (Exception ex) {
            Log.getLogger().error("Exception when undeploying the service " + serviceId + ": " + Log.getStackTrace(ex));
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return serviceId;
    }

        /**
     * Class responsible of performing asynchronous deployment and undeployment of services
     */
    private class ServiceDeployerUndeployer extends Thread {

        private boolean deploy_undeploy;    // Deployment = true; Undeployment = false;
        private String serviceManifest;
        private String slaID;
        private String serviceId;

        public ServiceDeployerUndeployer(String _manifest, String _serviceId, String _slaid, boolean _deployUndeploy, String comments) {
            deploy_undeploy = _deployUndeploy;
            serviceManifest = _manifest;
            slaID = _slaid;
            serviceId = _serviceId;
        }

        @Override
        public void run() {
            if (deploy_undeploy) {
                deploy_asynchronous(serviceManifest, slaID);
            } else {
                undeploy_asynchronous(serviceId);
            }
        }
    }

     private void startServiceContext(String serviceId, String manifest, List<String> vmIds, ServiceManagerClient sm_client, String SP_IPaddr) {
         StringBuilder sb = new StringBuilder("Starting TREC IP monitoring with the next Parameters: \n\tvms Ids: ");
         for(String vmId : vmIds) {
             sb.append(vmId).append(" ");
         }
         sb.append("\n\tserviceId: ").append(serviceId).append("\n\tSP IP address: ").append(SP_IPaddr);

        Log.getLogger().info(sb.toString());

        cm.getTREC().TREC_IP_startmonitoring(manifest, vmIds, serviceId, CloudManager.TIMEOUT_ECO_INFR, CloudManager.TIMEOUT_COST_INFR);
        Log.getLogger().info("Storing IP manifest into TREC common database...");

        Connection conn_trec = DBUtil.getTRECConnection();
        try {
            Queries.insertManifest(conn_trec, serviceId, manifest.toString());
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
        }
        try { conn_trec.close(); } catch(Exception e) { Log.getLogger().warn(Log.getStackTrace(e)); }

        Log.getLogger().info("Starting EE...");
         try {
            cm.startEE(serviceId, manifest, SP_IPaddr);
         } catch(Exception ex) {
            Log.getLogger().error("It seems that EE is not working\n" + Log.getStackTrace(ex));
         }
        Log.getLogger().info("Notifying SM that the service '" + serviceId + "' is running.");
        sm_client.updateServiceStatus(serviceId, "Deployed");

        cm.startMonitoring(serviceId);

        Log.getLogger().info("Starting FTE for service '" + serviceId + "'.");
        cm.startFTE(serviceId, manifest);

         //DM FCS
         Log.getLogger().info("Starting FCS Job Checker (Data manager)");
         Timer t = new Timer();
         jobCheckerTasks.put(serviceId,t);
         t.schedule(new FCSJobChecker(serviceId), FCSJobChecker.PERIOD, FCSJobChecker.PERIOD);
    }

    private Map<String, Timer> jobCheckerTasks = new HashMap<String, Timer>();

    private class FCSJobChecker extends TimerTask {
        public static final long PERIOD = 5000;
        private static final String NUM_PREDICTIONS = "4";

        protected String serviceId;
        protected String token = null;

        private FCSJobChecker(String serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public void run() {
            if(token == null) {
                token = cm.getDM().startFCSJob(cm.getLocalIPId(), serviceId, NUM_PREDICTIONS);
//                Log.getLogger().info("FCS Job Token: " + token);
            } else {
                String result = cm.getDM().finishedFCSJob(cm.getLocalIPId(), token);
//                Log.getLogger().info("FCS result for token " + token + ": " + result);
            }
        }
    }

    private void stopServiceContext(String serviceId, List<String> vmIds) {
        Log.getLogger().info("Stopping TREC IP monitoring...");
        cm.getTREC().TREC_IP_stopmonitoring(serviceId, vmIds);
        Log.getLogger().info("Stopping FTE...");
        cm.stopFTE(serviceId);
        Log.getLogger().info("Stopping EE...");
        cm.stopEE(serviceId);
        cm.stopMonitoring(serviceId);
        Log.getLogger().debug("Canceling FCS job...");
        Timer t = jobCheckerTasks.remove(serviceId);
        if(t != null) {
            t.cancel();
        }
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(OperationMode operationMode) {
        this.operationMode = operationMode;
    }
    
    
   
    public enum OperationMode {
        LOW_COST, LOW_RISK
    }
    
    
}
