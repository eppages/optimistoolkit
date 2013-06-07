/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.sla.ComponentConfigurationProvider;

/**
 * @TODO
 * 
 * @author owaeld
 * 
 */
public class ACJersyClient
    implements ACModelApi
{

    private final String path = "/model/performACTest";

    private WebResource service = null;

    public ACJersyClient()
    {

        String acURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.ac" ); //$NON-NLS-1$
        String url = acURL + path;

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );
        service = client.resource( url );
    }

    @Override
    public MultivaluedMap<String, String> performACTest( MultivaluedMap<String, String> params )
        throws Exception
    {
        ClientResponse response =
            service.type( MediaType.APPLICATION_FORM_URLENCODED ).post( ClientResponse.class, params );
        
        System.out.println("AC-response: " + response.toString());
        
        if( response.getClientResponseStatus().getStatusCode() == 200)
        {
            MultivaluedMap<String, String> allocationOffers = response.getEntity( MultivaluedMap.class );
            return allocationOffers;
        }
        
        String message = "reaon-phrase: " + response.getClientResponseStatus().getReasonPhrase() + 
                        " - status-code: " + response.getClientResponseStatus().getStatusCode();
        throw new Exception(message);
    }

    @Override
    public String getAdmissionControl( MultivaluedMap<String, String> formParams )
    {
        throw new UnsupportedOperationException( "not yet implemented" );
    }

    @Override
    public String createModel( MultivaluedMap<String, String> formParams )
    {
        throw new UnsupportedOperationException( "not yet implemented" );
    }

    @Override
    public String getTRECFactors()
    {
        throw new UnsupportedOperationException( "not yet implemented" );
    }

    @Override
    public String getMonitoringData( String resources, String period )
    {
        throw new UnsupportedOperationException( "not yet implemented" );
    }

}
