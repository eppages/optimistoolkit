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

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.log4j.Logger;

import eu.optimis.sla.ComponentConfigurationProvider;
import eu.optimis.sla.rest.impl.ACJersyClient;
import eu.optimis.sla.rest.impl.ACModelApi;

/**
 * Simple client for communication with the admission control service.
 * 
 * @author hrasheed
 * 
 */
public class AdmissionControlClient
{

    private static final Logger LOG = Logger.getLogger( AdmissionControlClient.class );

    private static ACModelApi delegate;

    /**
     * @param opModel
     * @param serviceManifest
     * @return java.lang.String
     * @see eu.optimis.sla.rest.impl.ACModelApi#admissionControl(MultivaluedMap<String, String> formParams)
     */
    public String admissionControl( MultivaluedMap<String, String> formParams )
    {
        return getAdmissionControlClient().getAdmissionControl( formParams );
    }

    /**
     * @param serviceManifest
     * @return MultivaluedMap<String, String> params
     * @see eu.optimis.sla.rest.impl.ACModelApi#performACTest(MultivaluedMap<String, String> params)
     */
    public MultivaluedMap<String, String> performACTest( MultivaluedMap<String, String> params )
        throws Exception
    {

        return getAdmissionControlClient().performACTest( params );
    }

    /**
     * @param wlanalysis
     * @param trec
     * @return
     * @see eu.optimis.sla.rest.impl.ACModelApi#createModel(MultivaluedMap<String, String> formParams)
     */
    public String createModel( MultivaluedMap<String, String> formParams )
    {
        return getAdmissionControlClient().createModel( formParams );
    }

    /**
     * @return
     * @see eu.optimis.sla.rest.impl.ACModelApi#getTRECFactors()
     */
    public String getTRECFactors()
    {
        return getAdmissionControlClient().getTRECFactors();
    }

    /**
     * @param resources
     * @param period
     * @return
     * @see eu.optimis.sla.rest.impl.ACModelApi#getMonitoringData(java.lang.String, java.lang.String)
     */
    public String getMonitoringData( String resources, String period )
    {
        return getAdmissionControlClient().getMonitoringData( resources, period );
    }

    protected ACModelApi createJaxClient()
    {
        String acURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.ac" ); //$NON-NLS-1$
        LOG.info( MessageFormat.format( "create new admission control client at {0}", new Object[] { acURL } ) );
        ACModelApi ac = JAXRSClientFactory.create( acURL, ACModelApi.class );
        return ac;
    }

    protected ACModelApi createJersyClient()
    {
        ACJersyClient acClient = new ACJersyClient();
        return acClient;
    }

    protected synchronized ACModelApi getAdmissionControlClient()
    {
        if ( getDefaultAdmissionControlClient() == null )
        {
            delegate = createJersyClient();
        }
        return getDefaultAdmissionControlClient();
    }

    public static ACModelApi getDefaultAdmissionControlClient()
    {
        return delegate;
    }

    public static void setDefaultAdmissionControlClient( ACModelApi defaultACClient )
    {
        AdmissionControlClient.delegate = defaultACClient;
    }

}
