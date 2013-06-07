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
package eu.optimis.vmmanager.rest;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import java.net.URI;

import eu.optimis.schemas.trec.blo.ObjectiveType;
import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.scheduler.drp.client.DRPClient;
import net.emotivecloud.utils.ovf.OVFException;
import net.emotivecloud.utils.ovf.OVFWrapper;
import net.emotivecloud.vrmm.scheduler.VRMMSchedulerException;
import eu.optimis.vmmanager.core.PlacementOptimizer;
import eu.optimis.vmmanager.util.Config;
import eu.optimis.vmmanager.util.Log;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import net.emotivecloud.utils.ovf.EmotiveOVF;

/**
 * <b>VM Manager API</b> 
 * The VM Manager is responsible for providing an efficient placement of VMs running in a Cloud infrastructure, 
 * and during their whole life (operation). In particular, given a number of VMs and the IP physical infrastructure, 
 * the VM Manager’s main task is to optimize how these VMs are placed on the physical resources so that the IP’s 
 * internal goals are maximized. At any given moment the VM Manager is capable of re-organizing the mapping of VMs 
 * to physical resources according to these IP’s internal goals.
 * 
 * Besides, it offers the OCCI interfaces (which allows adding, removing, getting VMs through OVFs)
 * 
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 * 
 */
@Path("/")
@Singleton
public class VMManagerREST {

    private DRPClient drp;
    private PlacementOptimizer po;

    public VMManagerREST() {
        
        Log.getLogger().info("Using EMOTIVE at address = " + Config.getString("config.drp_host"));
        drp = new DRPClient(Config.getString("config.drp_host"), Integer.parseInt(Config.getString("config.drp_port")));
        po = new PlacementOptimizer(drp);

    }

    /**
     * It allows the Cloud Optimizer to modify the actual management policy used in this engine
     * REC weights are specified in a single string, with ":" as the delimiter
     */
    @POST
    @Path("/policy")
    @Consumes(MediaType.APPLICATION_XML)
    public void changeSchedulingPolicy(String policyRules) throws Exception {
        try {
            BusinessDescription blo = BLOUtils.read(new ByteArrayInputStream(policyRules.getBytes()));
            po.changeSchedulingPolicy(blo);
        } catch(Exception e) {
            Log.getLogger().debug(e.getMessage(),e);
            throw e;
        }
    }

    /**************************************************************************************************
     * 
     *                                         OCCI API
     * 
     * REST interface. Implementation by means of HTTP protocol and its methods GET, POST, PUT y DELETE.
     **************************************************************************************************/
    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        String info = drp.info();
        Log.getLogger().info(info);
        return info;
    }

    /**
     * If we are deploying a service, the ovf must contain the optimum allocation pattern; otherwise, it should be filled here
     * @param ovfXml: OVF representing the VM to deploy
     * @return OVF
     * @throws VRMMSchedulerException 
     */
    @POST
    @Path("/compute")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String createCompute(String ovfXml, @QueryParam("trec_opt") String trec_opt) throws VRMMSchedulerException { // (Map<String, Object>);        
        OVFWrapper ret = null;
        Log.getLogger().debug("calling createCompute: " + ovfXml.substring(0,100) + "...");
                
        try {
            
            //TODO Y3- consider AC allocation pattern
            EmotiveOVF emotiveOVF = new EmotiveOVF(ovfXml);
            if (trec_opt.matches("true")) {
                String destinationNode = po.assessVMOptimalPlacement(emotiveOVF); //Second parameter: ID of virtual system --> vmName
                Log.getLogger().info("The Placement Optimizer has chosen the node '" + destinationNode + "' for the deployment of this VM.");
                emotiveOVF.setProductProperty("VM.destination.host", destinationNode);
            } else {
                Log.getLogger().info("Admission Control chose the node '" + emotiveOVF.getProductProperty("VM.destination.host") + "' for the deployment of this VM.");
            }
            ret = drp.createCompute(new OVFWrapper(emotiveOVF.toString()));

        } catch(Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            Log.getLogger().error(sw.toString(),ex);
            throw new VRMMSchedulerException(ex.getMessage(), ex);            
        }
        Log.getLogger().debug("Returning: " + ret.toString().substring(0, 100) + " ...");
        return ret.toString();
    }

    @DELETE
    @Path("/compute/{envid}")
    public void deleteCompute(@PathParam("envid") String envId) throws VRMMSchedulerException {
        try {
            drp.deleteCompute(envId);
        } catch (VRMMSchedulerException e) {
            e.printStackTrace();
            if (e.getMessage().contains("cannot be found in any node")) {
                throw new WebApplicationException(426);
            } else if (e.getMessage().contains("VirtMonitor")) {
                throw new WebApplicationException(420);
            } else if (e.getMessage().contains("Not enough resources")) {
                throw new WebApplicationException(425);
            } else if (e.getMessage().contains("VM does not exist")) {
                throw new WebApplicationException(427);
            } else if (e.getMessage().contains("No available nodes")) {
                throw new WebApplicationException(428);
            } else if (e.getMessage().contains("Cannot connect with the Scheduler")) {
                throw new WebApplicationException(429);
            } else if (e.getMessage().contains("Cannot connect with Simple Scheduler")) {
                throw new WebApplicationException(430);
            } else if (e.getMessage().contains("Cannot connect with Hadoop Scheduler")) {
                throw new WebApplicationException(431);
            } else if (e.getMessage().contains("Cannot recognize address")) {
                throw new WebApplicationException(432);
            } else if (e.getMessage().contains("is not in any VM")) {
                throw new WebApplicationException(433);
            } else if (e.getMessage().contains("cannot be found in any node")) {
                throw new WebApplicationException(434);
            } else if (e.getMessage().contains("Can not delete")) {
                throw new WebApplicationException(435);
            } else {
                throw new WebApplicationException(424);
            }
        }
    }

    @GET
    @Path("/compute/{envid}")
    @Produces(MediaType.APPLICATION_XML)
    public String getCompute(@PathParam("envid") String envId) throws VRMMSchedulerException {
        OVFWrapper o = drp.getCompute(envId);
        return o.getId();
    }

    @GET
    @Path("/compute/location")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getLocation(@QueryParam("vmid") String vmId) throws VRMMSchedulerException {
        URI location_uri = null;
        try {
            location_uri = new URI(drp.getLocation(vmId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location_uri.getHost();
    }
    
    
    @POST
    @Path("/hm/trust/vm/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMTrust(@PathParam("vmId") String vmId, String trust) {
        Log.getLogger().debug("notifyVMTrust vmId=" + vmId + ", trust=" +trust);
    }

    @POST
    @Path("/hm/trust/service/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceTrust(@PathParam("serviceId") String serviceId, String trust) {
        Log.getLogger().debug("notifyServiceTrust serviceId="+serviceId+", " + trust);
    }

    @POST
    @Path("/hm/trust/node/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostTrust(@PathParam("nodeId") String physicalHostID, String trust) {
        Log.getLogger().debug("notifyPhysicalHostTrust nodeId = " + physicalHostID +", trust = " + trust);
    }

    @POST
    @Path("/hm/trust/ip")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfrastructureTrust(String trust) {
        Log.getLogger().debug("notifyInfrastructureTrust, trust = " + trust);
        po.optimizeInfrastructure();
    }    
    
    @POST
    @Path("/hm/risk/vm/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMRiskLevel(@PathParam("vmId") String vmId, String RiskLevel) {
//        Log.getLogger().debug("notifyVMRiskLevel vmId=" + vmId + ", RiskLevel=" +RiskLevel);
        
    }

    @POST
    @Path("/hm/risk/service/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceRiskLevel(@PathParam("serviceId") String serviceId, String RiskLevel) {
//        Log.getLogger().debug("notifyServiceRiskLevel serviceId="+serviceId+", RiskLevel=" + RiskLevel);
    }

    @POST
    @Path("/hm/risk/node/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostRiskLevel(@PathParam("nodeId") String physicalHostID, String RiskLevel) {
//        Log.getLogger().debug("notifyPhysicalHostRiskLevel nodeId = " + physicalHostID +", RiskLevel = " + RiskLevel);
    }

    @POST
    @Path("/hm/risk/ip")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfrastructureRiskLevel(String RiskLevel) {
        Log.getLogger().debug("notifyInfranstructureRiskLevel, RiskLevel = " + RiskLevel);
        po.optimizeInfrastructure();
    }
    
    @POST
    @Path("/hm/eco/vm/{type}/{interval}/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMEco(@PathParam("vmId") String vmId, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
//        Log.getLogger().debug("notifyVMEco vmId="+ vmId +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
    }

    @POST
    @Path("/hm/eco/service/{type}/{interval}/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceEco(@PathParam("serviceId") String serviceID, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
//        Log.getLogger().debug("notifyServiceEco serviceID="+ serviceID +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
    }

    @POST
    @Path("/hm/eco/node/{type}/{interval}/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostEco(@PathParam("nodeId") String physicalHostID, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {        
//        Log.getLogger().debug("notifyPhysicalHostEco nodeId="+ physicalHostID +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
    }

    @POST
    @Path("/hm/eco/ip/{type}/{interval}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfrastructureEco(String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
        if((ObjectiveType.MAX_ENERGY_EFF.equals(po.getMaximizationPolicy())
                && type.equals("energy")) ||
            (ObjectiveType.MAX_ECO.equals(po.getMaximizationPolicy())
                    && type.equals("ecological"))) {
            if(intervalMS == 0) {
                Log.getLogger().debug("notifyInfranstructureEco eco="+eco+", type="+type + ", interval="+intervalMS);
                po.optimizeInfrastructure();
            }
        }
    }
    
    
    @POST
    @Path("/hm/cost/vm/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMCost(@PathParam("vmId") String vmId, String cost) {
        Log.getLogger().debug("notifyVMCost vmId=" + vmId + ", Double.parseDouble(cost)=" +cost);    
        
    }

    @POST
    @Path("/hm/cost/service/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceCost(@PathParam("serviceId") String serviceID, String cost) {
        Log.getLogger().debug("notifyServiceCost serviceID=" + serviceID + ", Double.parseDouble(cost)=" +cost);       
    }

    @POST
    @Path("/hm/cost/node/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostCost(@PathParam("nodeId") String physicalHostID, String cost) {
        Log.getLogger().debug("notifyPhysicalHostCost nodeId=" + physicalHostID + ", Double.parseDouble(cost)=" +cost);       
    }

    @POST
    @Path("/hm/cost/ip")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfrastructureCost(String cost) {
        Log.getLogger().debug("notifyInfranstructureCost cost=" +cost);
        po.optimizeInfrastructure();
    }

    @POST
    @Path("/compute/{vmId}/migrate")
    @Produces(MediaType.TEXT_PLAIN)
    public String migrateVM(@PathParam("vmId") String vmId) throws VRMMSchedulerException {
        try {
            return String.valueOf(po.migrateVM(new EmotiveOVF(drp.getCompute(vmId))));
        } catch (VRMMSchedulerException e) {
            Log.getLogger().error(Log.getStackTrace(e));
            throw e;
        }
    }

    @GET
    @Path("/allcompute/{nodeId}")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getVMsAtNode(@PathParam("nodeId") String nodeId) {
        return po.getVMsAtNode(nodeId);
    }

    

}