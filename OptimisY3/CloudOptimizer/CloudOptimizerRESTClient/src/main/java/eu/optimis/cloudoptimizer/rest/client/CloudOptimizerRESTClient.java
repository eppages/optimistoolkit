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
package eu.optimis.cloudoptimizer.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.core.MediaType;

import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.xml.VirtualResource;
import eu.optimis.cloudoptimizer.xml.XmlUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import net.emotivecloud.commons.ListStrings;
import org.apache.log4j.Logger;


/**
 * Client of the Cloud Optimizer REST API
 * 
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 * 
 */
public class CloudOptimizerRESTClient {

    private Logger logger = null;

    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "CloudOptimizer";                                       
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public CloudOptimizerRESTClient() {
        this(HOST, PORT, PATH);
    }

    public CloudOptimizerRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public CloudOptimizerRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public CloudOptimizerRESTClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    public CloudOptimizerRESTClient(Logger logger) {
        this();
        this.logger = logger;
    }

    public CloudOptimizerRESTClient(String host, Logger logger) {
        this(host);
        this.logger = logger;
    }

    public CloudOptimizerRESTClient(String host, int port, Logger logger) {
        this(host, port);
        this.logger = logger;
    }

    public CloudOptimizerRESTClient(String host, int port, String path, Logger logger) {
        this(host,port,path);
        this.logger = logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private String getAddress() throws URISyntaxException {
        //return "http://"+host+":"+port+"/"+path
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }

    public synchronized String deploy(String allocationOffer, String slaId) {
        String serviceId = "";
        try {
            if (allocationOffer != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").queryParam("slaid", slaId);
                serviceId = resource.type(MediaType.APPLICATION_XML).post(String.class, allocationOffer);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return serviceId;
    }

    public synchronized boolean undeploy(String serviceId) {
        boolean ret = false;
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress());
                resource.path("service").path(serviceId).delete();
                ret = true;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return ret;
    }

    public synchronized String addVM(String serviceId, String serviceManifest, String imageId, int NoOfVMsToAdd, String SP_IPaddr) {
        return addVM(serviceId, serviceManifest, imageId, NoOfVMsToAdd, SP_IPaddr, null);
    }

    public synchronized String addVM(String serviceId, String serviceManifest, String imageId, int NoOfVMsToAdd, String SP_IPaddr, String comments) {
        String componentId = "";
        try {
            if (serviceId != null && serviceManifest != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path(imageId).queryParam("num", Integer.toString(NoOfVMsToAdd)).queryParam("SP_IPaddr", SP_IPaddr);
                if(comments != null) {
                    resource = resource.queryParam("comments",comments);
                }
                componentId = resource.type(MediaType.APPLICATION_XML).post(String.class, serviceManifest);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return componentId;
    }

    public synchronized void removeVM(String serviceId, String imageId, int NoOfVMsToRemove, String SP_IPaddr) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path(imageId)
                .queryParam("num", Integer.toString(NoOfVMsToRemove)).queryParam("SP_IPaddr", SP_IPaddr);
                //else  //choose one vm to remove
                resource.delete();
                info("Removing VM\n\tserviceId: "+serviceId+"\n\timageId: "+imageId+"\n\tNoOfVMsToRemove: "+NoOfVMsToRemove
                        + "\n\tSP_IPaddr: " + SP_IPaddr + "\n\tURL: " + resource.toString());

            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public synchronized void removeVM(String serviceId, String imageId, int NoOfVMsToRemove, String SP_IPaddr, boolean save) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path(imageId)
                        .queryParam("num", Integer.toString(NoOfVMsToRemove))
                        .queryParam("SP_IPaddr", SP_IPaddr)
                        .queryParam("save",Boolean.toString(save));
                //else  //choose one vm to remove

                info("Removing VM\n\tserviceId: "+serviceId+"\n\timageId: "+imageId+"\n\tNoOfVMsToRemove: "+NoOfVMsToRemove
                     + "\n\tSP_IPaddr: " + SP_IPaddr + "\n\tsave: " + save + "\n\tURL: " + resource.toString());
                resource.delete();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    /**
     * Returns the number of running instances of the imageId in the serviceid 
     * @param serviceId
     * @param imageId
     * @return
     */
    public int getNrInstances(String serviceId, String imageId) {
        int ret = -1;
        try {
            if (serviceId != null && imageId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path(imageId).path("instances");
                ret = Integer.parseInt(resource.get(String.class));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return ret;
    }


    public void addPolicy(String policyId, String policyRules) {
        try {
            if (policyId != null) {
                WebResource resource = client.resource(this.getAddress()).path("policy").queryParam("policyid", policyId);
                resource.post(String.class, policyRules);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void updatePolicy(String policyId, String newPolicyRules) {
        try {
            if (policyId != null) {
                WebResource resource = client.resource(this.getAddress()).path("policy").path(policyId);
                resource.put(String.class, newPolicyRules);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void removePolicy(String policyId) {
        try {
            if (policyId != null) {
                WebResource resource = client.resource(this.getAddress()).path("policy").path(policyId);
                resource.delete(String.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    /**
     * PHYSICAL RESOURCES
     */
    public String addPhysicalResource(String physicalResource) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources");
            resource.type(MediaType.APPLICATION_XML).post(String.class, physicalResource);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            ex.printStackTrace();
            return "0";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "0";
        }
        return "1";
    }

    public String getPhysicalResource(String nodeId) {
        String ret = "";
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/" + nodeId);
            ret = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return ret;
    }

    public void deletePhysicalResource(String nodeId) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/" + nodeId);
            resource.delete();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public List<String> getNodesByService(String serviceId) {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/service/"+ serviceId);
            l = resource.get(ListStrings.class);
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return l;
    }

    public List<String> getNodesId() {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/ids");
            l = resource.get(ListStrings.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }
    
    public List<String> getInfrastructureNodesId() {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/allids");
            l = resource.get(ListStrings.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    /**
     * VIRTUAL RESOURCES
     */
    public void addVirtualResource(String virtualResource) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources");
            resource.type(MediaType.APPLICATION_XML).post(String.class, virtualResource);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void updateVirtualResource(String vmId, String nodeId) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId);
            resource.post(String.class, nodeId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public VirtualResource getVirtualResource(String vmId) {
        String ret = "";
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId);
            ret = resource.get(String.class);

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        System.out.println(ret);
        return XmlUtil.getVirtualResourceFromXml(ret);
    }

    public void deleteVirtualResource(String vmId) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId);
            resource.delete();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public List<String> pdtegetVMsId(String physicalNodeId) {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/vms/" + physicalNodeId);
            l = resource.get(ListStrings.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getNodeId(String vmId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId + "/node");
            //TODO return node & IP identifiers
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getVMPublicIP(String vmId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId + "/public_ip_address");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getVMName(String vmId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId + "/name");
            l = resource.get(String.class);
         } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getVMId(String vmName) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmName + "/id");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getVMServiceId(String vmId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + vmId + "/serviceid");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public List<String> getRunningServices() {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("/service/ids");
            l = resource.get(ListStrings.class);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return l;
    }

    public List<String> getVMsIdsOfService(String serviceId) {
        List<String> l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("virtualresources/" + serviceId + "/vms");
            l = resource.get(ListStrings.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getNodeIpId(String nodeId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("ip/" + nodeId + "/id");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getIpId() {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("ip/id");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String getIpVmIpAddress(String ipId) {
        String l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("ip/" + ipId + "/vmipaddress");
            l = resource.get(String.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    /**
     * Used by another CO in multi-cloud scenarios to update the VMs related to a given service
     * The CO receives this call and forwards it to the service manager
     */
//    public void updateServiceResource(String serviceId, String IP_id, String vm_IP_address, String name, String status) {
//        sm_client.addVm(serviceId, IP_id, vm_IP_address, name, status,-1);
//    }
//    public void updateServiceResource(String serviceId, String IP_id, String vm_IP_address, String name, String status, int deploymentDurationMS) {
//        sm_client.addVm(serviceId, IP_id, vm_IP_address, name, status,deploymentDurationMS);
//    }

    public void putSPTrust(String SPid, String trust) {
        try {
            if (SPid != null) {
                WebResource resource = client.resource(this.getAddress()).path("sp/" + SPid + "/trec/trust");
                resource.type(MediaType.TEXT_PLAIN).post(String.class, trust);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void putIPEco(String IPid, String eco) {
        try {
            if (IPid != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip/" + IPid + "/trec/eco");
                resource.type(MediaType.TEXT_PLAIN).post(String.class, eco);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void putNodeEco(String nodeId, String eco) {
        try {
            if (nodeId != null) {
                WebResource resource = client.resource(this.getAddress()).path("physicalresources/" + nodeId + "/trec/eco");
                resource.type(MediaType.TEXT_PLAIN).post(String.class, eco);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }

    public void putServiceEco(String serviceId, String eco) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service/" + serviceId + "/trec/eco");
                resource.type(MediaType.TEXT_PLAIN).post(String.class, eco);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }
    
    // Y3 functions
    public void addBLO(BusinessDescription bd) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("blo");
            resource.type(MediaType.APPLICATION_XML).post(BLOUtils.toString(bd));
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public CODecision saveVMAndRestart(String vmId) {
        CODecision l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("vm/" + vmId + "/saverestart");
            l = CODecision.valueOf(resource.post(String.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public CODecision restartVM(String vmId) {
        CODecision l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("vm/"+vmId+"/restart");
            l = CODecision.valueOf(resource.post(String.class));
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return l;
    }

    public CODecision suggestVMMigration(String vmId) {
        CODecision l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("vm/" + vmId + "/migrate");
            l = CODecision.valueOf(resource.post(String.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public CODecision suggestAllVMsMigration(String nodeId) {
        CODecision l = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("physicalresources/" + nodeId + "/migrateall");
            l = CODecision.valueOf(resource.post(String.class));
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(),ex);
        }
        return l;
    }

    public String onNodeFailure(String nodeId) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("node/"+nodeId+"/failure");
            return resource.post(String.class);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }


    private void info(String message) {
        if(logger != null) {
            logger.info(message);
        }
    }
}