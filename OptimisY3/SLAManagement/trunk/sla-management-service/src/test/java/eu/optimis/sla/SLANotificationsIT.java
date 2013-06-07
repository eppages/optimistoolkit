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

import org.apache.xmlbeans.XmlObject;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesType;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.NotificationEndpointFactoryService;
import eu.optimis.sla.notification.SubscriptionService;
import eu.optimis.sla.notification.impl.ClientFactory;
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringDocument;
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringType;

/**
 * @author hrasheed
 * 
 */
public class SLANotificationsIT extends AbstractSLAIT
{
    
    String SUBSCRIPTION_URL = getApplicationURL("/sla/subscriptions");

    String NOTIFICATION_URL =  getApplicationURL("/sla/notifications");
    

    /**
     * Simple test case of interaction with SLA management service and send notification events.
     */
    public void testSLANotificationEvents() throws Exception {
        
        AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        
        super.updateServiceManifest(template);
        //
        // create an offer and then create the agreement
        //
        System.out.println( "creating agreement." );
        AgreementOffer offer = new AgreementOfferType(template);
        AgreementClient agreement = factory.createAgreement(offer);
        assertNotNull(agreement);
        System.out.println( "agreement is successfully created." );
        //
        // get the service deployment information
        //
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        assertEquals(2, stStates.length);
        assertEquals(ServiceTermStateDefinition.NOT_READY, stStates[0].getState());
        
        int maxTrys = 6;
        
        while(stStates[0].getState() == ServiceTermStateDefinition.NOT_READY) {
            System.out.println("Waiting to change state to ready...");
            Thread.sleep(10000);
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if (maxTrys == 0) {
                fail("State change of agreement failed. Monitoring not invoked.");
            }
        }
        
        System.out.println("service term state changed to Ready.");
        
        ServiceTermStateType vsdState = agreement.getServiceTermState("OPTIMIS_SERVICE_SDT");
        XmlObject[] serviceMonitoring = vsdState.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        assertEquals("OPTIMIS_SERVICE_SDT state must contain exactly one SLASeriveMonitoringDocument.", 1, serviceMonitoring.length);
        
        SLASeriveMonitoringType seriveMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];
        assertNotNull(seriveMonitoringType.getServiceId());
        //assertTrue("Number of service monitoring records must be at least 1.", 0 < seriveMonitoringType.getSLASeriveMonitoringRecordArray().length);
        
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
            System.out.println("event store size: " + notificationClient.getNotificationEventHistory().getNotificationEventCollection().sizeOfNotificationEventArray());
            tries--;
        }
        
        System.out.println("terminating agreement.");
        
        //
        // terminate the agreement
        //
        agreement.terminate();
        
        System.out.println( "testSLANotificationEvents successfully completed" );
    }

}
