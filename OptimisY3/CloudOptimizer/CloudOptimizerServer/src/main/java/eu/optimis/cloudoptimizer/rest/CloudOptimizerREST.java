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
package eu.optimis.cloudoptimizer.rest;

import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.cloudoptimizer.core.CloudManager;
import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.cloudoptimizer.util.Config;
import eu.optimis.cloudoptimizer.util.Log;
import eu.optimis.cloudoptimizer.xml.*;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import net.emotivecloud.commons.ListStrings;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Cloud Optimizer REST API
 * 
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 * 
 */
@Path("/")
@Singleton
public class CloudOptimizerREST extends CloudManager {

    //@Context private HttpServletRequest hsr;
    
    public CloudOptimizerREST() {      

        
    }

    /**
     * IP external interface to deploy a service
     * @param allocationOffer
     * @return the serviceId
     */
    @POST
    @Path("/service")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String deploy(String allocationOffer, @QueryParam("slaid") String slaId) {
        Log.getLogger().debug("Deploying SLA: " + slaId);
        String ret = "";      
        try {
            ret = getSD().deploy(allocationOffer, slaId, null);
        } catch (Exception ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return ret;
    }

    /**
     * IP external interface to undeploy a service
     * @param serviceId
     */
    @DELETE
    @Path("/service/{serviceid}")
    public String undeploy(@PathParam("serviceid") String serviceId) {
        Log.getLogger().debug("Undeploying service: " + serviceId);
        return getSD().undeploy(serviceId);
    }

    @GET
    @Path("/service/{serviceid}/{imageid}/instances")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNrInstances(@PathParam("serviceid") String serviceId, @PathParam("imageid") String imageId) {
        return super.getNrInstances(serviceId, imageId) + "";
    }

    /**
     * Allows adding new VMs
     * @param serviceId
     * @param serviceManifest
     * @param num
     */
    @POST
    @Path("/service/{serviceid}/{imageid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String addVM(@PathParam("serviceid") String serviceId, String serviceManifest, @PathParam("imageid") String imageId, @QueryParam("num") String num, @QueryParam("SP_IPaddr") String SP_IPaddr,
                        @QueryParam("comments") String comments) {
        String savePath = null;
        try {
            savePath = System.getenv("OPTIMIS_HOME")+"/var/log/optimis/ElasticityManifest.xml";
            FileOutputStream fos = new FileOutputStream(savePath);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(serviceManifest);
            pw.close();
            fos.close();
        } catch(Exception e) {
            // THIS IS ONLY A TEST, IT WILL BE REMOVED
        }
        Log.getLogger().debug("addVM to " + serviceId + " (imageid = " + imageId + "). A copy is saved in " + savePath);

        if(comments != null) {
            Log.getLogger().debug("reason: " + comments);
        }
        super.addVM(serviceId, serviceManifest, imageId, Integer.parseInt(num), SP_IPaddr, comments);
        return imageId;
    }

    /**
     * Allows removing VMs
     * @param serviceId
     */
    @DELETE
    @Path("/service/{serviceid}/{imageid}")
    public void removeVM(
            @PathParam("serviceid") String serviceId,
            @PathParam("imageid") String imageId,
            @QueryParam("num") String num,
            @QueryParam("SP_IPaddr") String SP_IPaddr,
            @QueryParam("save") String save) {
        Log.getLogger().debug("removeVM from " + serviceId + " (imageid = " + imageId + ")");
        boolean doSave = save != null && (save.equalsIgnoreCase("true") || save.equals("1"));
        super.removeVM(serviceId, imageId, Integer.parseInt(num), SP_IPaddr, doSave);
    }

    @GET
    @Path("/service/ids")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getRunningServices() {
        ListStrings ret = null;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();

            List<String> ls = Queries.getRunningServiceIds(conn);
            ret = new ListStrings();
            for(String s : ls) {
                ret.add(s);
            }

        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        } finally {
            try { conn.close(); } catch(SQLException e) { }
        }
        return ret;
    }

    /**
     * Management of Physical Resources
     */
    @POST
    @Path("/physicalresources")
    @Consumes(MediaType.APPLICATION_XML)
    public Response addPhysicalResource(String resource) throws IOException {
        Log.getLogger().debug("adding physical resource: " + resource);
        Response ret = null;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            boolean a = Queries.insertPhysicalResource(conn, XmlUtil.getPhysicalResourceFromXml(resource));
            if (!a) {
                ret = Response.status(400).build();
            } else {
                ret = Response.ok().build();
            }
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                Log.getLogger().error(e.getMessage(),e);
            }

        }
        return ret;
    }

    @GET
    @Path("/physicalresources/{nodeid}")
    @Produces(MediaType.TEXT_XML)
    public String getPhysicalResource(@PathParam("nodeid") String nodeId) {
        PhysicalResource p = null;
        try {
            Connection conn = DBUtil.getConnection();
            p = Queries.getPhysicalResource(conn, nodeId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        
        }
        return p.toString();
    }

    @DELETE
    @Path("/physicalresources/{nodeid}")
    public Response deletePhysicalResource(@PathParam("nodeid") String nodeId) throws IOException {
        Log.getLogger().debug("Deleting physical resource " + nodeId);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            Queries.deletePhysicalResource(conn, nodeId);
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();   
        } finally {
            try {
                if(conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                Log.getLogger().error(e.getMessage(),e);
            }
        }
        return Response.ok().build();
    }

    @GET
    @Path("/physicalresources/service/{serviceId}")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getNodesByService(@PathParam("serviceId") String serviceId) {
        Connection conn = null;
        try {
            ListStrings ret = new ListStrings();
            conn = DBUtil.getConnection();
            List<String> vms = Queries.getVMsIdsOfService(conn,serviceId);
            for(String vmId : vms) {
                String nodeId = Queries.getNodeId(conn,vmId);
                if(!ret.contains(nodeId)) {
                    ret.add(nodeId);
                }
            }
            return ret;
        } catch(SQLException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
            throw new RuntimeException(ex);
        } finally {
            if(conn != null) try { conn.close(); } catch(Exception e) { Log.getLogger().error(e.getMessage(),e); }
        }
    }

    @GET
    @Path("/physicalresources/allids")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getInfrastructureNodesId() throws IOException {
        ListStrings ret = null;
        try {
            ret = new ListStrings();
            Connection conn = DBUtil.getConnection();
            List<String> q = Queries.getNodesId(conn);
            for (int i = 0; i < q.size(); i++) {
                ret.add(q.get(i));
            }
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        
        }
        
        return ret;
    }
    
    @GET
    @Path("/physicalresources/ids")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getNodesId() throws IOException {
        ListStrings ret = null;
        try {
            ret = new ListStrings();
            Connection conn = DBUtil.getConnection();
            List<String> q = Queries.getActiveNodesId(conn);
            for (int i = 0; i < q.size(); i++) {
                ret.add(q.get(i));
            }
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);     
        }
        
        return ret;
    }
    

    /**
     * Management of Virtual Resources
     */
    @POST
    @Path("/virtualresources")
    @Consumes(MediaType.APPLICATION_XML)
    public Response addVirtualResource(String resource) throws IOException {
        Response ret = null;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            boolean a = Queries.insertVirtualResource(conn, XmlUtil.getVirtualResourceFromXml(resource));
            if (!a) {
                ret = Response.status(400).build();
            } else {
                ret = Response.ok().build();
            }

        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch(SQLException e) {
                Log.getLogger().error(e.getMessage(),e);
            }
        }
        return ret;
    }

    @GET
    @Path("/virtualresources/{vmid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String getVirtualResource(@PathParam("vmid") String vmId) {
        String v = null;
        try {
            Connection conn = DBUtil.getConnection();
            VirtualResources vr = Queries.getVirtualResource(conn, vmId);
            if(vr != null) {
                v = vr.toString();
            }
            conn.close();

        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return v;
    }

    @POST
    @Path("/virtualresources/{vmid}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateVirtualResource(@PathParam("vmid") String vmId, String nodeId) throws IOException {
        Log.getLogger().debug("updating virtual resource " + vmId + " at " + nodeId);
        try {
            Connection conn = DBUtil.getConnection();
            Queries.updateVirtualResource(conn, vmId, nodeId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/virtualresources/{vmid}")
    public Response deleteVirtualResource(@PathParam("vmid") String vmId) throws IOException {
        Log.getLogger().debug("Deleting virtual resource " + vmId);
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            Queries.deleteVirtualResource(conn, vmId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();
        } finally {
            try {
                if(conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch(SQLException e) {
                Log.getLogger().error(e.getMessage(),e);
            }
        }
        return Response.ok().build();
    }

    @GET
    @Path("/virtualresources/vms/{nodeid}")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getVMsId(@PathParam("nodeid") String nodeId) throws IOException {
        ListStrings ret = null;
        try {
            ret = new ListStrings();
            Connection conn = DBUtil.getConnection();
            List<String> q = Queries.getVMsId(conn, nodeId);
            for (int i = 0; i < q.size(); i++) {
                ret.add(q.get(i));
            }
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return ret;
    }

    @GET
    @Path("/virtualresources/{vmid}/node")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodeId(@PathParam("vmid") String vmId) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getNodeId(conn, vmId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/virtualresources/{vmid}/name")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVMName(@PathParam("vmid") String vmId) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getVMName(conn, vmId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/virtualresources/{vmName}/public_ip_address")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVMPublicIP(@PathParam("vmName") String vmName) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getVMPublicIP(conn, vmName);
            conn.close();
            
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/virtualresources/{vmName}/id")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVMId(@PathParam("vmName") String vmName) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getVMId(conn, vmName);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/virtualresources/{vmid}/serviceid")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVMServiceId(@PathParam("vmid") String vmId) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getVMServiceId(conn, vmId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/virtualresources/{serviceid}/vms")
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings getVMsIdsOfService(@PathParam("serviceid") String serviceId) throws IOException {
        ListStrings ret = null;
        try {
            ret = new ListStrings();
            Connection conn = DBUtil.getConnection();
            List<String> q = Queries.getVMsIdsOfService(conn, serviceId);
            for (int i = 0; i < q.size(); i++) {
                ret.add(q.get(i));
            }
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return ret;
    }

    @GET
    @Path("/ip/{nodeid}/id")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNodeIpId(@PathParam("nodeid") String nodeId) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getNodeIpId(conn, nodeId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/ip/id")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIpId() throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getIpId(conn);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }

    @GET
    @Path("/ip/{ipid}/vmipaddress")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIpVmIpAddress(@PathParam("ipid") String ipId) throws IOException {
        String q = "";
        try {
            Connection conn = DBUtil.getConnection();
            q = Queries.getIpVmIpAddress(conn, ipId);
            conn.close();
        } catch (SQLException ex) {
            Log.getLogger().error(ex.getMessage(), ex);
            ex.printStackTrace();        }
        return q;
    }
    
    // Y3 functions
    @POST
    @Path("/blo")
    @Consumes(MediaType.APPLICATION_XML)
    public void addBLO(String businessDescriptorXML) throws BLOException {
        Log.getLogger().debug("Adding blo: " + businessDescriptorXML);
        BusinessDescription bd = BLOUtils.read(new ByteArrayInputStream(businessDescriptorXML.getBytes()));
        super.getHolisticManager().setBLO(bd);
    }
    
    @POST
    @Path("/hm/trust/vm/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMTrust(@PathParam("vmId") String vmId, String trust) {
        Log.getLogger().debug("notifyVMTrust vmId=" + vmId + ", trust=" +trust);
        hm.notifyVMTrust(vmId, trust);
    }

    @POST
    @Path("/hm/trust/service/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceTrust(@PathParam("serviceId") String serviceId, String trust) {
        Log.getLogger().debug("notifyServiceTrust serviceId="+serviceId+", " + trust);
        hm.notifyServiceTrust(serviceId,trust);
    }

    @POST
    @Path("/hm/trust/node/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostTrust(@PathParam("nodeId") String physicalHostID, String trust) {
        Log.getLogger().debug("notifyPhysicalHostTrust nodeId = " + physicalHostID +", trust = " + trust);
        hm.notifyPhysicalHostTrust(physicalHostID,trust);
    }

    @POST
    @Path("/hm/trust/ip")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfranstructureTrust(String trust) {
        Log.getLogger().debug("notifyInfrastructureTrust, trust = " + trust);
        hm.notifyInfranstructureTrust(trust);
    }
    
    @POST
    @Path("/hm/risk/vm/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMRiskLevel(@PathParam("vmId") String vmId, String RiskLevel) {
        Log.getLogger().debug("notifyVMRiskLevel vmId=" + vmId + ", RiskLevel=" +RiskLevel);
        //00hm.notifyVMRisk(vmId,RiskLevel,0);

    }

    @POST
    @Path("/hm/risk/service/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceRiskLevel(@PathParam("serviceId") String serviceId, String RiskLevel) {
        Log.getLogger().debug("notifyServiceRiskLevel serviceId="+serviceId+", RiskLevel=" + RiskLevel);
    }

    @POST
    @Path("/hm/risk/node/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostRiskLevel(@PathParam("nodeId") String physicalHostID, String RiskLevel) {
        Log.getLogger().debug("notifyPhysicalHostRiskLevel nodeId = " + physicalHostID +", RiskLevel = " + RiskLevel);
    }

    @POST
    @Path("/hm/risk/ip")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfranstructureRiskLevel(String RiskLevel) {
        Log.getLogger().debug("notifyInfranstructureRiskLevel, RiskLevel = " + RiskLevel);
        hm.notifyInfranstructureRisk(new Integer(RiskLevel),0);
    }
    
    @POST
    @Path("/hm/eco/vm/{type}/{interval}/{vmId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyVMEco(@PathParam("vmId") String vmId, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
//        Log.getLogger().debug("notifyVMEco vmId="+ vmId +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
        hm.notifyVMEco(vmId, eco, type, intervalMS);
    }

    @POST
    @Path("/hm/eco/service/{type}/{interval}/{serviceId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyServiceEco(@PathParam("serviceId") String serviceID, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
//        Log.getLogger().debug("notifyServiceEco serviceID="+ serviceID +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
        hm.notifyServiceEco(serviceID, eco, type, intervalMS);
    }

    @POST
    @Path("/hm/eco/node/{type}/{interval}/{nodeId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyPhysicalHostEco(@PathParam("nodeId") String physicalHostID, String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
        try {
            //Log.getLogger().debug("notifyPhysicalHostEco nodeId="+ physicalHostID +", Double.parseDouble(eco)="+eco+", type="+type + ", interval="+intervalMS);
            hm.notifyPhysicalHostEco(physicalHostID, eco, type, intervalMS);
        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println(e.toString());
            e.printStackTrace(pw);
            Log.getLogger().error(sw.toString());
        }
    }

    @POST
    @Path("/hm/eco/ip/{type}/{interval}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void notifyInfranstructureEco(String eco, @PathParam("type") String type, @PathParam("interval") long intervalMS) {
        Log.getLogger().debug("notifyInfranstructureEco eco="+eco+", type="+type + ", interval="+intervalMS);        
        hm.notifyInfranstructureEco(eco, type, intervalMS);
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
    public void notifyInfranstructureCost(String cost) {
        Log.getLogger().debug("notifyInfranstructureCost cost=" +cost);       
    }

    @POST
    @Path("/vm/{vmId}/restart")
    @Produces(MediaType.TEXT_PLAIN)
    public String restartVM(@PathParam("vmId") String vmId) {
        Log.getLogger().debug("Suggest VM restart: " + vmId);
        return super.suggestVMRestart(vmId,false).toString();
    }

    @POST
    @Path("/vm/{vmId}/saverestart")
    @Produces(MediaType.TEXT_PLAIN)
    public String saveVMAndRestart(@PathParam("vmId") String vmId) {
        Log.getLogger().debug("Suggest VM restart: " + vmId);
        return super.suggestVMRestart(vmId, true).toString();
    }


    @POST
    @Path("/vm/{vmId}/migrate")
    @Produces(MediaType.TEXT_PLAIN)
    public String suggestVMMigration(@PathParam("vmId") String vmId) {
        Log.getLogger().debug("Suggest VM migration: " + vmId);
        return super.suggestVMMigrate(vmId).toString();
    }

    @POST
    @Path("physicalresources/{nodeId}/migrateall")
    @Produces(MediaType.TEXT_PLAIN)
    public String suggestAllVMsMigration(@PathParam("nodeId") String nodeId) {
        Log.getLogger().debug("Suggest Migration of all vms for Node: " + nodeId);
        return super.suggestAllVMsMigrate(nodeId).toString();
    }

    @POST
    @Path("/node/{nodeId}/failure")
    @Produces(MediaType.TEXT_PLAIN)
    public String onNodeFailure(@PathParam("nodeId") String nodeId) {
        Log.getLogger().debug("Node " + nodeId + " failed");
        return super.onNodeFailure(nodeId);
    }

}