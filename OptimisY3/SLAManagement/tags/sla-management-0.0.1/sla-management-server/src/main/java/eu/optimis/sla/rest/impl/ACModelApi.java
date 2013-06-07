/* 
 * Copyright (c) 2011, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.sla.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Admission Control API
 * 
 * @author owaeld
 * 
 */
@Path( "/model" )
public interface ACModelApi
{

    @POST
    @Produces( value = "text/plain" )
    @Consumes( "application/x-www-form-urlencoded" )
    @Path( "/admissionControl" )
    String getAdmissionControl( MultivaluedMap<String, String> formParams );

    /**
     * Perform admission control on the services that comprise the given manifest
     * 
     * @param an
     *            array of Service Manifests
     * @return Allocation offers
     */
    @POST
    @Path( "/performACTest" )
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    MultivaluedMap<String, String> performACTest( MultivaluedMap<String, String> params ) throws Exception;

    @POST
    @Produces( value = "text/plain" )
    @Consumes( "application/x-www-form-urlencoded" )
    @Path( "/createModel" )
    String createModel( MultivaluedMap<String, String> formParams );

    /**
     * Provides the trust rank of the entities that use the trust framework
     * 
     * @return TREC factors
     */
    @GET
    @Path( "/getTRECFactors" )
    String getTRECFactors();

    /**
     * Ask the monitoring system for the data of the resources in the list for a certain period
     * 
     * @param resources
     *            list
     * @param period
     * @return statistics between the two dates for a given entity
     */
    @GET
    @Path( "/getMonitoringData/{period}" )
    String getMonitoringData( String resources, @PathParam( "period" ) String period );
}
