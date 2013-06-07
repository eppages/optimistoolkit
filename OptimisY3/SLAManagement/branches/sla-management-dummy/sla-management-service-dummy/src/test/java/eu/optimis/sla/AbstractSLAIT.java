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
package eu.optimis.sla;

import junit.framework.TestCase;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * @author hrasheed
 */
public abstract class AbstractSLAIT extends TestCase
{

    //private static String URL_PART = "http://localhost:9090/optimis-sla"; 
    
    //private static String URL_PART = "http://optimis-ipvm.atosorigin.es:8080/optimis-sla"; // ATOS testbed
    
    //private static String URL_PART = "http://optimis-ipvm2.ds.cs.umu.se:8080/optimis-sla"; // UMEA testbed
    
    //private static String URL_PART = "http://109.231.120.19:8080/optimis-sla"; // FLEXIANT testbed
    
    private static String URL_PART = "http://217.33.61.84:8080/optimis-sla"; // Broker
    
    private String URL = getApplicationURL( "" );
    
    private AgreementFactoryClient factory = null;
    
    public static final String TEMPLATE_NAME = "OPTIMIS-SERVICE-INSTANTIATION";
    public static final String TEMPLATE_ID = "1";

    protected static String getApplicationURL( String PATH )
    {
        return URL_PART + PATH;
    }

    /**
     * Sets the SLA Management System URL based on the value specified
     * by the wsag4j.gateway.address system property.
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        URL = System.getProperty( "wsag4j.gateway.address", URL );
    }

    private LoginContext createLoginContext() throws LoginException
    {
        //
        // create the login context
        //
        KeystoreProperties properties = new KeystoreProperties();
        properties.setKeyStoreAlias( "wsag4j-user" );
        properties.setPrivateKeyPassword( "user@wsag4j" );

        properties.setKeyStoreType( "JKS" );
        properties.setKeystoreFilename( "/wsag4j-client-keystore.jks" );
        properties.setKeystorePassword( "user@wsag4j" );

        properties.setTruststoreType( "JKS" );
        properties.setTruststoreFilename( "/wsag4j-client-keystore.jks" );
        properties.setTruststorePassword( "user@wsag4j" );

        LoginContext loginContext = new KeystoreLoginContext( properties );
        loginContext.login();
        
        return loginContext;
    }

    private void createAgreementFactory() throws Exception
    {
        //
        // lookup the agreement factory service
        //
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue( URL );
        AgreementFactoryRegistryClient
                registry = AgreementFactoryRegistryLocator.getFactoryRegistry( epr, createLoginContext() );
        registry.setTrace( false );

        //
        // there is only 1 factory configured at the given endpoint
        //
        AgreementFactoryClient[] factories = registry.listAgreementFactories();
        assertEquals( 1, factories.length );

        factory = factories[ 0 ];
    }
    
    protected AgreementFactoryClient getAgreementFactory() throws Exception
    {
        if(factory == null)
            createAgreementFactory();
        
        return factory;
    }
}
