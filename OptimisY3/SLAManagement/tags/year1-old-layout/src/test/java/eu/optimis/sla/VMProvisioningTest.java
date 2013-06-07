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
package eu.optimis.sla;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.security.auth.login.LoginContext;

import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesType;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.api.client.NegotiationClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import junit.framework.TestCase;

/**
 * @author owaeld
 *
 */
public class VMProvisioningTest extends TestCase {
	
    private String URL = "http://localhost:8080/optimis-sla";
    //private String URL = "http://packcs-e0.scai.fraunhofer.de/optimis-sla/";
    //private String URL = "http://192.168.42.137:8080/optimis-sla";
    
    /**
     * Sets the SLA Management System URL based on the value specified
     * by the wsag4j.gateway.address system property.  
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        URL = System.getProperty("wsag4j.gateway.address", URL);
    }

    /**
     * Simple test case for retrieving templates from SLA management service.
     */
    public void testSLATemplateRetrieval() throws Exception {
        
        //
        // create the login context
        //
        KeystoreProperties properties = new KeystoreProperties();
        properties.setKeyStoreAlias("wsag4j-user");
        properties.setPrivateKeyPassword("user@wsag4j");

        properties.setKeyStoreType("JKS");
        properties.setKeystoreFilename("/wsag4j-client-keystore.jks");
        properties.setKeystorePassword("user@wsag4j");

        properties.setTruststoreType("JKS");
        properties.setTruststoreFilename("/wsag4j-client-keystore.jks");
        properties.setTruststorePassword("user@wsag4j");

        LoginContext loginContext = new KeystoreLoginContext(properties);
        loginContext.login();

        //
        // lookup the agreement factory service
        //
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue(URL);
        AgreementFactoryRegistryClient registry = AgreementFactoryRegistryLocator.getFactoryRegistry(epr, loginContext);
        registry.setTrace(false);
        
        //
        // there is only 1 factory configured at the given endpoint
        //
        AgreementFactoryClient[] factories = registry.listAgreementFactories();
        assertEquals(1, factories.length);
        
        //
        // get the OPTIMIS VM template
        //
        AgreementFactoryClient factory = factories[0];  
        AgreementTemplateType template = factory.getTemplate("OPTIMIS-MULTIPLE-VM-IMAGES", "1");
        assertNotNull(template);
        
    }

    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLACreationAndTermination() throws Exception {
        
        //
        // create the login context
        //
        KeystoreProperties properties = new KeystoreProperties();
        properties.setKeyStoreAlias("wsag4j-user");
        properties.setPrivateKeyPassword("user@wsag4j");

        properties.setKeyStoreType("JKS");
        properties.setKeystoreFilename("/wsag4j-client-keystore.jks");
        properties.setKeystorePassword("user@wsag4j");

        properties.setTruststoreType("JKS");
        properties.setTruststoreFilename("/wsag4j-client-keystore.jks");
        properties.setTruststorePassword("user@wsag4j");

        LoginContext loginContext = new KeystoreLoginContext(properties);
        loginContext.login();

        //
        // lookup the agreement factory service
        //
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue(URL);
        AgreementFactoryRegistryClient registry = AgreementFactoryRegistryLocator.getFactoryRegistry(epr, loginContext);
        registry.setTrace(true);
        
        //
        // there is only 1 factory configured at the given endpoint
        //
        AgreementFactoryClient[] factories = registry.listAgreementFactories();
        assertEquals(1, factories.length);
        
        //
        // get the OPTIMIS VM template
        //
        AgreementFactoryClient factory = factories[0];  
        AgreementTemplateType template = factory.getTemplate("OPTIMIS-MULTIPLE-VM-IMAGES", "1");
        assertNotNull(template);
        
        //
        // create an offer and then create the agreement
        //
        AgreementOffer offer = new AgreementOfferType(template);
        AgreementClient agreement = factory.createAgreement(offer);
        assertNotNull(agreement);
        
        //
        // get the service deployment information
        //
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        assertEquals(4, stStates.length);
        assertEquals(ServiceTermStateDefinition.NOT_READY, stStates[0].getState());
        
        int maxTrys = 6;
        
        while(stStates[0].getState() == ServiceTermStateDefinition.NOT_READY) {
            System.out.println("Waiting to change state to ready...");
            Thread.sleep(5000);
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if (maxTrys == 0) {
                fail("State change of agreement failed. Monitoring not invoked.");
            }
        }
        
        ResourcesType resources = (ResourcesType) stStates[0].selectChildren(ResourcesDocument.type.getDocumentElementName())[0];
        assertNotNull(resources);
        
        //
        // terminate the agreement
        //
        agreement.terminate();
    }
    
    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLANegotiation() throws Exception {
        
        //
        // create the login context
        //
        KeystoreProperties properties = new KeystoreProperties();
        properties.setKeyStoreAlias("wsag4j-user");
        properties.setPrivateKeyPassword("user@wsag4j");

        properties.setKeyStoreType("JKS");
        properties.setKeystoreFilename("/wsag4j-client-keystore.jks");
        properties.setKeystorePassword("user@wsag4j");

        properties.setTruststoreType("JKS");
        properties.setTruststoreFilename("/wsag4j-client-keystore.jks");
        properties.setTruststorePassword("user@wsag4j");

        LoginContext loginContext = new KeystoreLoginContext(properties);
        loginContext.login();

        //
        // lookup the agreement factory service
        //
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue(URL);
        AgreementFactoryRegistryClient registry = AgreementFactoryRegistryLocator.getFactoryRegistry(epr, loginContext);
        registry.setTrace(true);
        
        //
        // there is only 1 factory configured at the given endpoint
        //
        AgreementFactoryClient[] factories = registry.listAgreementFactories();
        assertEquals(1, factories.length);
        
        //
        // get the OPTIMIS VM template
        //
        AgreementFactoryClient factory = factories[0];  
        AgreementTemplateType template = factory.getTemplate("OPTIMIS-MULTIPLE-VM-IMAGES", "1");
        assertNotNull(template);
        
        //
        // initiate negotiation process
        //
        NegotiationContextDocument contextDoc = NegotiationContextDocument.Factory.newInstance();
        NegotiationContextType context = contextDoc.addNewNegotiationContext();
        context.setAgreementFactoryEPR(factory.getEndpoint());
        context.setAgreementResponder(NegotiationRoleType.NEGOTIATION_RESPONDER);
        GregorianCalendar expireDate = new GregorianCalendar();
        expireDate.add(Calendar.HOUR, 1);
        context.setExpirationTime(expireDate);        
        NegotiationClient negotiation = factory.initiateNegotiation(context);
        assertNotNull("Falied to instantiate negotiation client.", negotiation);
        
        NegotiationOffer offer = new NegotiationOffer(template);
        NegotiationOfferType[] co = negotiation.negotiate(new NegotiationOfferType[] {offer.getXMLObject()});
        assertNotNull(co);
        
        //
        //  TODO finalize test case
        //
        
//        //
//        // create an offer and then create the agreement
//        //
//        AgreementOffer offer = new AgreementOfferType(template);
//        AgreementClient agreement = factory.createAgreement(offer);
//        assertNotNull(agreement);
//        
//        //
//        // get the service deployment information
//        //
//        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
//        assertEquals(4, stStates.length);
//        assertEquals(ServiceTermStateDefinition.NOT_READY, stStates[0].getState());
//        
//        int maxTrys = 6;
//        
//        while(stStates[0].getState() == ServiceTermStateDefinition.NOT_READY) {
//            System.out.println("Waiting to change state to ready...");
//            Thread.sleep(5000);
//            stStates = agreement.getServiceTermStates();
//            maxTrys--;
//            if (maxTrys == 0) {
//                fail("State change of agreement failed. Monitoring not invoked.");
//            }
//        }
//        
//        ResourcesType resources = (ResourcesType) stStates[0].selectChildren(ResourcesDocument.type.getDocumentElementName())[0];
//        assertNotNull(resources);
//        
//        //
//        // terminate the agreement
//        //
//        agreement.terminate();
    }
    
    
}
