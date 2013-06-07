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
package eu.optimis.sla;

import java.net.URI;
import java.util.List;

import javax.security.auth.login.LoginContext;

import org.apache.xmlbeans.XmlObject;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesType;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryRegistryClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.client.AgreementFactoryRegistryLocator;
import org.ogf.graap.wsag.security.core.KeystoreProperties;
import org.ogf.graap.wsag.security.core.keystore.KeystoreLoginContext;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.w3.x2005.x08.addressing.EndpointReferenceType;

import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.NotificationEndpointFactoryService;
import eu.optimis.sla.notification.SubscriptionService;
import eu.optimis.sla.notification.impl.ClientFactory;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringDocument;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringType;

import junit.framework.TestCase;

/**
 * @author hrasheed
 * 
 */
public class TestSLANotifications extends TestCase
{
    
    private String URL = "http://localhost:8080/optimis-sla";
    //private String URL = "http://192.168.42.137:8080/optimis-sla";
    
    String SUBSCRIPTION_URL = "http://127.0.0.1:8080/optimis-sla/sla/subscriptions";

    String NOTIFICATION_URL = "http://127.0.0.1:8080/optimis-sla/sla/notifications";
    
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
     * Simple test case of interaction with SLA management service and send notification events.
     */
    public void testSLANotificationEvents() throws Exception {
        
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
            Thread.sleep(3000);
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if (maxTrys == 0) {
                fail("State change of agreement failed. Monitoring not invoked.");
            }
        }
        
        System.out.println("service term state changed to Ready.");
        
        ServiceTermStateType vsdState = agreement.getServiceTermState("VirtualSystemDescription");
        XmlObject[] serviceMonitoring = vsdState.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        assertEquals("VirtualSystemDescription state must contain exactly one SLASeriveMonitoringDocument.", 1, serviceMonitoring.length);
        
        SLASeriveMonitoringType seriveMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];
        assertNotNull(seriveMonitoringType.getServiceId());
        assertTrue("Number of service monitoring records must be at least 1.", 0 < seriveMonitoringType.getSLASeriveMonitoringRecordArray().length);
        
        ResourcesType resources = (ResourcesType) stStates[0].selectChildren(ResourcesDocument.type.getDocumentElementName())[0];
        assertNotNull(resources);
        
        NotificationEndpointFactoryService notificationService =
                        ClientFactory.create( NOTIFICATION_URL, NotificationEndpointFactoryService.class );

        URI notificationUri = notificationService.createNotificationEndpoint();
        
        SubscriptionService subscriptionService =
                        ClientFactory.create( SUBSCRIPTION_URL, SubscriptionService.class );
        
        URI subscriptionUri = subscriptionService.subscribe( agreement.getAgreementId(), notificationUri );
        List<URI> subscriptions = subscriptionService.listSubscriptions();
        //assertEquals( "unexpected no. subscriptions (iteration 1)", 1, subscriptions.size() );
        assertTrue( "missing subscription uri 1 (iteration 1)", subscriptions.contains( subscriptionUri ) );
        
        NotificationEndpoint notificationClient = ClientFactory.create( notificationUri, NotificationEndpoint.class );
        assertNotNull( notificationClient.getNotificationEventHistory() );
        
        int tries = 6;
        
        while(tries != 0) {
            Thread.sleep(3000);
            System.out.println("event history size: " + notificationClient.getNotificationEventHistory().getNotificationEventCollection().sizeOfNotificationEventArray());
            tries--;
        }
        
        System.out.println("terminating agreement.");
        
        //
        // terminate the agreement
        //
        agreement.terminate();
    }

}
