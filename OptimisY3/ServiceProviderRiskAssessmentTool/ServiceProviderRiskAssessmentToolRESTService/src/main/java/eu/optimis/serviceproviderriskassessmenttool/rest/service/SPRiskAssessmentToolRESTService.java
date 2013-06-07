/*
 *  Copyright 2013 University of Leeds
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

package eu.optimis.serviceproviderriskassessmenttool.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.resource.Singleton;
import java.util.List;
import eu.optimis.serviceproviderriskassessmenttool.core.ServiceProviderRiskAssessmentServer;

/**
 *
 * @author scsmj
 */
@Path("/")
@Singleton
/**
 * RESTFul Webservice interface of the ServiceProviderRiskAssessmentServer class.
 * Refer to the parent class for documentation of each method.
 */
public class SPRiskAssessmentToolRESTService extends ServiceProviderRiskAssessmentServer {

    @GET
    @Path("/ip/calculaterisklevelofslaofferreliability")
    @Produces(MediaType.TEXT_PLAIN)
    public String calculateRiskLevelOfSLAOfferReliability(@QueryParam("providerID") String providerID, @QueryParam("serviceID") String serviceID, @QueryParam("proposedPoF") String proposedPoF) {

        int ret = super.calculateRiskLevelOfSLAOfferReliability(providerID, serviceID, Double.valueOf(proposedPoF));
        return ret + "";
    }
    
    @GET
    @Path("/ip/calculaterisklevelofslaofferreliabilitydeployment")
    @Produces(MediaType.TEXT_PLAIN)
    public String calculateRiskLevelOfSLAOfferReliabilityDeployment(@QueryParam("providerID") String providerID, @QueryParam("serviceID") String serviceID, @QueryParam("proposedPoF") String proposedPoF) {
                  
        int ret = super.calculateRiskLevelOfSLAOfferReliabilityDeployment(providerID, serviceID, Double.valueOf(proposedPoF));
        return ret + "";
    }
    
    @GET
    @Path("/ip/adjustedpofcal")
    @Produces(MediaType.TEXT_PLAIN)
    public String adjustedPOFCal(@QueryParam("providerId") String providerId, @QueryParam("proposedPoF") String proposedPoF) {

        double ret = super.adjustedPOFCal(providerId, Double.valueOf(proposedPoF));
        return ret + "";
    }
    
    @POST
    @Path("/ip/prenegotiateipdeploymentphase")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public ListStrings preNegotiateIPDeploymentPhase(ListStrings IPNames) {

        ListStrings rankedIPs = new ListStrings();

        List<String> rankedIPNames = super.preNegotiateIPDeploymentPhase(IPNames);
        for (String IPName : rankedIPNames) {
            rankedIPs.add(IPName);
        }

        return rankedIPs;
    }
    
    @POST
    @Path("/ip/{serviceid}")
    public void startServiceAssessmentREST(@PathParam("serviceid") String serviceId) {
        super.startServiceAssessment(serviceId);
    }

    @DELETE
    @Path("/ip/{serviceid}")
    public void stopServiceAssessmentREST(@PathParam("serviceid") String serviceId) {
        super.stopServiceAssessment(serviceId);
    }
}
