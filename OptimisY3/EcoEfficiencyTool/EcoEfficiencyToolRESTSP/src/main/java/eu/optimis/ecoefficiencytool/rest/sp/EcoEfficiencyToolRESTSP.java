/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.rest.sp;

import com.sun.jersey.spi.resource.Singleton;
import eu.optimis.ecoefficiencytool.core.EcoEffAssessorSP;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import net.emotivecloud.commons.HashMapStrings;
import net.emotivecloud.commons.ListStrings;

@Path("/")
@Singleton
/**
 * RESTFul Webservice interface of the EcoEffAssessorSP class. Refer to the parent
 * class for documentation of each method.
 */
public class EcoEfficiencyToolRESTSP extends EcoEffAssessorSP {

    @POST
    @Path("/service/{serviceid}")
    public void startServiceAssessmentREST(@PathParam("serviceid") String serviceId, @QueryParam("timeout") String timeout) {
        Long tout = null;
        if (timeout != null) {
            tout = Long.valueOf(timeout);
        }

        super.startServiceAssessment(serviceId, tout);
    }

    @DELETE
    @Path("/service/{serviceid}")
    public void stopServiceAssessmentREST(@PathParam("serviceid") String serviceId) {
        super.stopServiceAssessment(serviceId);
    }

    @GET
    @Path("/service/{serviceid}/assessecoefficiency")
    @Produces(MediaType.TEXT_PLAIN)
    public String assessServiceEcoEfficiencyREST(@PathParam("serviceid") String serviceId, @QueryParam("type") String type) {
        if(type == null) {
            //log.info("ECO: Warning: eco-efficiency type wasn't specified.");
            type = "energy";
        }
        return super.assessServiceEcoEfficiency(serviceId, type);
    }

    @POST
    @Path("/service/forecastecoefficiency")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastServiceEcoEfficiencyREST(@QueryParam("providerId") String providerId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan, String manifest) {

        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            //log.info("ECO: Warning: eco-efficiency type wasn't specified.");
            type = "energy";
        }

        return super.forecastServiceEcoEfficiency(providerId,manifest,type,tspan);
    }
    
    @POST
    @Path("/service/forecastenecoeff")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings forecastServiceEnEcoEffREST(@QueryParam("providerId") String providerId, @QueryParam("timeSpan") String timeSpan, String manifest) {

        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        double[] results = super.forecastServiceEnEcoEff(providerId, manifest, tspan);
        
        ListStrings ret = new ListStrings();
        ret.add(Double.toString(results[0]));
        ret.add(Double.toString(results[1]));
        return ret;
    }
    
    @GET
    @Path("/service/alldeploymentmessages")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllDeploymentMessages() {
        return super.getAllDeploymentMessages();
    }
    
    /*@POST
    @Path("/service/forecastecoefficiency")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    public String forecastServiceEcoEfficiencyREST(@QueryParam("newLayout") String newLayout,@QueryParam("providerId") String providerId, @QueryParam("type") String type, @QueryParam("timeSpan") String timeSpan, String manifest) {

        Long tspan = null;
        if (timeSpan != null) {
            tspan = Long.valueOf(timeSpan);
        }
        if(type == null) {
            //log.info("ECO: Warning: eco-efficiency type wasn't specified.");
            type = "energy";
        }

        //System.out.println("Forecasting ecoefficiency for the service " + manifest);
        if(newLayout.equalsIgnoreCase("no")) {
            futureServiceLayout = null;
        }
        return super.forecastServiceEcoEfficiency(providerId,manifest,futureServiceLayout,type,tspan);
    }

    @POST
    @Path("/service/updatefuturedeployment")
    @Consumes(MediaType.APPLICATION_XML)
    public void updateFutureDeploymentREST(HashMapStrings<String,String> typeIdReplicas) {

        futureServiceLayout = new HashMap<String,Integer>();

        Map<String,String> receivedmap= typeIdReplicas.getMapProperty();
        System.out.println("I received a HashMap of " + receivedmap.size() + " elements. Contents:");
        Iterator it = receivedmap.keySet().iterator();
        while(it.hasNext()) {
            String key = (String) it.next();
            System.out.println("KEY: " + key + "    VALUE: " + receivedmap.get(key));
            futureServiceLayout.put(key, Integer.valueOf(typeIdReplicas.getMapProperty().get(key)));
        }

    }*/
}
