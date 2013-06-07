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
package eu.optimis.faulttoleranceengine.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.faulttoleranceengine.core.FTAssessor;
import eu.optimis.faulttoleranceengine.util.Log;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import java.io.ByteArrayInputStream;

@Path("/")
@Singleton
public class FaultToleranceEngineREST extends FTAssessor {

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info() {
        return "Ok";
    }

    /**
     * It allows the Cloud Optimizer to modify the actual management policy used in this engine
     */
    @POST
    @Path("/service/{serviceid}/policy")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String setPolicyREST(@PathParam("serviceid") String serviceId, String policyRules) {
        return ""; //super.setPolicy(serviceId, policyRules);
    }

    /**
     * Is is used by the Cloud Optimizer in order to update the policy being used by this engine
     */
    @PUT
    @Path("/service/{serviceid}/policy")
    @Consumes(MediaType.TEXT_PLAIN)
    public void updatePolicyREST(@PathParam("serviceid") String serviceId, String policyRules) {
        //super.updatePolicy(serviceId, policyRules);
    }

    /**
     * Used by the Cloud Optimizer in order to send a notification to this engine when a new service has been deployed
     */
    @POST
    @Path("/service/{serviceid}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String newServiceDeployed(@PathParam("serviceid") String serviceId, String serviceManifest) {
        return super.newServiceDeployed(serviceId, serviceManifest);
    }

    /**
     * Used by the Cloud Optimizer in order to send a notification to this engine when a new service has been undeployed
     */
    @DELETE
    @Path("/service/{serviceid}")
    public void newServiceUndeployed(@PathParam("serviceid") String serviceId) {
        super.newServiceUndeployed(serviceId);
    }

    @POST
    @Path("/vm/{vmid}/risk")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String putVMRiskPoFREST(@PathParam("vmid") String vmId, String riskLevel) {
        //super.putVMRiskPoF(vmId, Integer.parseInt(riskLevel));
        return vmId;
    }
    
    @POST
    @Path("/config/availability")
    @Consumes(MediaType.TEXT_PLAIN)
    public void setTargetAvailability(String availability) {
        Float a = new Float(availability);
        //super.setTargetAvailability(a);
    }
    
    @POST
    @Path("/policy")
    @Consumes(MediaType.APPLICATION_XML)
    public void changeSchedulingPolicy(String policyRules) throws Exception {
        try {
            Log.getLogger().debug("Scheduling policy changed: " + policyRules);
            BusinessDescription blo = BLOUtils.read(new ByteArrayInputStream(policyRules.getBytes()));
            super.setBLO(blo);
        } catch(Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
            throw e;
        }        
    }    
}