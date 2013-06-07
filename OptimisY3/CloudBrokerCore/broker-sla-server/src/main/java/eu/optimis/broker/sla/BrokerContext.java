/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
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
package eu.optimis.broker.sla;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * This class holds the broker context during negotiation and agreement creation 
 *  
 * @author hrasheed
 * 
 */
public class BrokerContext
{
	
    private static final Logger logger = Logger.getLogger(BrokerContext.class);
    
    public static Map<String, HashMap<String, Provider>> negotiationContext = Collections.synchronizedMap( new HashMap<String, HashMap<String, Provider>>() );
	
	public BrokerContext()
	{
	}
	
    public AgreementFactoryClient createAgreementFactory(String url) throws Exception
    {
        //
        // lookup the agreement factory service
        //
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue( url );
        
        AgreementFactoryRegistryClient
                registry = AgreementFactoryRegistryLocator.getFactoryRegistry( epr, createLoginContext() );
        registry.setTrace( false );

        //
        // there is only 1 factory configured at the given endpoint
        //
        AgreementFactoryClient[] factories = registry.listAgreementFactories();
        
        AgreementFactoryClient factory = factories[ 0 ];
        
        return factory;
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
    
}
