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
package eu.optimis.sla.rest;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;

import eu.optimis.sla.ComponentConfigurationProvider;
import eu.optimis.sla.rest.impl.CloudOptimizerREST;

/**
 * @author owaeld
 */
public class CloudOptimizerClient
{

    private static final Logger LOG = Logger.getLogger( CloudOptimizerClient.class );

    private static CloudOptimizerREST defaultCloudOptimizerClient;

    /**
     * @param allocationOffer
     * @return
     * @see eu.optimis.sla.rest.impl.CloudOptimizerREST#deploy(java.lang.String, java.lang.String)
     */
    public String deploy( String allocationOffer, String slaId )
    {
        return getDefaultCloudOptimizerClient().deploy( allocationOffer, slaId );
    }

    /**
     * @param serviceId
     * @see eu.optimis.sla.rest.impl.CloudOptimizerREST#undeploy(java.lang.String)
     */
    public String undeploy( String serviceId ) throws InterruptedException
    {
        return getDefaultCloudOptimizerClient().undeploy( serviceId );
    }

    public synchronized CloudOptimizerREST getDefaultCloudOptimizerClient()
    {
        if ( defaultCloudOptimizerClient == null )
        {
            defaultCloudOptimizerClient = createCloudOptimizerClient();
        }
        return defaultCloudOptimizerClient;
    }

    /**
     * @return a new cloud optimizer res client
     */
    protected CloudOptimizerREST createCloudOptimizerClient()
    {
        String coURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.co" ); //$NON-NLS-1$
        LOG.info( MessageFormat.format( "create new cloud optimizer client at {0}", new Object[] { coURL } ) );

        CloudOptimizerREST co = JAXRSClientFactory.create( coURL, CloudOptimizerREST.class );
        WebClient.client( co ).type( MediaType.APPLICATION_XML );

        return co;
    }

    public static void setDefaultCloudOptimizerClient( CloudOptimizerREST defaultCloudOptimizerClient )
    {
        CloudOptimizerClient.defaultCloudOptimizerClient = defaultCloudOptimizerClient;
    }
}
