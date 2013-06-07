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

import java.net.URI;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag4j.types.engine.GuaranteeEvaluationResultType;
import org.ogf.graap.wsag4j.types.engine.SLOEvaluationResultType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
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
 */
public class GaurqanteeEvaluationIT extends AbstractSLAIT
{
	
	String SUBSCRIPTION_URL = getApplicationURL("/sla/subscriptions");
	
	String NOTIFICATION_URL = getApplicationURL("/sla/notifications");

    /**
     * Simple test case for service 'availability' guarantee evaluation.
     */
    public void testServiceGuarantee() throws Exception
    {
    	
    	AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        //System.out.println( "OPTIMIS-SLA: " + template.toString() );
        //System.out.println( "OPTIMIS-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );

        super.updateServiceManifest(template);
        //
        // create an offer and then create the agreement
        //
        AgreementOffer offer = new AgreementOfferType( template );
        
        System.out.println( "creating agreement" );
        
        AgreementClient agreement = factory.createAgreement( offer );
        assertNotNull( agreement );
        
        System.out.println( "agreement is successfully created." );

        //
        // get the service deployment information
        //
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        assertEquals( 2, stStates.length );
        assertEquals( ServiceTermStateDefinition.NOT_READY, stStates[ 0 ].getState() );

        int maxTrys = 10;

        while ( stStates[ 0 ].getState() == ServiceTermStateDefinition.NOT_READY )
        {
            System.out.println( "Waiting to change service term state to ready..." );
            Thread.sleep( 5000 );
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if ( maxTrys == 0 )
            {
                fail( "State change of agreement failed. Monitoring not invoked." );
            }
        }

        System.out.println( "service term state changed to ready..." );
        
        ServiceTermStateType vsdState = agreement.getServiceTermState("OPTIMIS_SERVICE_SDT");
        XmlObject[] serviceMonitoring = vsdState.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        assertEquals("OPTIMIS_SERVICE_SDT state must contain exactly one SLASeriveMonitoringDocument.", 1, serviceMonitoring.length);
        
        SLASeriveMonitoringType seriveMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];
        assertNotNull(seriveMonitoringType.getServiceId());
        //assertTrue("Number of service monitoring records must be at least 1.", 0 < seriveMonitoringType.getSLASeriveMonitoringRecordArray().length);
         
        //
        // create notification service via the notification endpoint factory
        //
        NotificationEndpointFactoryService notificationService =
                        ClientFactory.create( NOTIFICATION_URL, NotificationEndpointFactoryService.class );
        System.out.println( "created notification service at " + NOTIFICATION_URL );
        
        URI notificationUri = notificationService.createNotificationEndpoint();
        System.out.println( "created otification endpoint." +  notificationUri.toString());
        
        //
        // create subscription service via the subscription endpoint factory
        //
        SubscriptionService subscriptionService = ClientFactory.create( SUBSCRIPTION_URL, SubscriptionService.class );
        System.out.println( "created subscription service at " + SUBSCRIPTION_URL );
        
        //
        // add new subscription for the endpoint
        //
        subscriptionService.subscribe( agreement.getAgreementId(), notificationUri );
        System.out.println( "created subscription with agreement-id:" +  agreement.getAgreementId() + " and notificationUri: " +  notificationUri.toString() );
        
        List<URI> subscriptions = subscriptionService.listSubscriptions();
        System.out.println( "number of subscriptions: " +  subscriptions.size() );
        
        NotificationEndpoint notification = ClientFactory.create( notificationUri, NotificationEndpoint.class );
        System.out.println( "created notification endpoint from URI " + notificationUri );
        
        List<URI> uris = notificationService.listNotifications();
        System.out.println( "number of notifications: " +  uris.size());
        
        System.out.println( "retrieving sla events from notification end point at " + notificationUri );
        
        int maxIterations = 10;
        
        while (maxIterations > 0 ) {
        	try{
        		Thread.sleep(30000);
        		System.out.println( "notification events: " + notification.getNotificationEventHistory().getNotificationEventCollection().xmlText( new XmlOptions().setSavePrettyPrint() ) );
                System.out.println( "notification events length: " + notification.getNotificationEventHistory().getNotificationEventCollection().getNotificationEventArray().length );
                
                GuaranteeEvaluationResultType guaranteeResult = notification.getNotificationEventHistory().getNotificationEventCollection().getNotificationEventArray(0).getGuaranteeEvaluationResultArray(0);
                guaranteeResult.getName();
                guaranteeResult.getImportance();
                //
                // SLOEvaluationResultType.INT_SLO_FULFILLED 
                // SLOEvaluationResultType.INT_SLO_VIOLATED
                // SLOEvaluationResultType.SLO_NOT_DETERMINED
                //
                guaranteeResult.getType(); 
                
                GuaranteeTermStateType guaranteeState = notification.getNotificationEventHistory().getNotificationEventCollection().getNotificationEventArray(0).getGuaranteeEvaluationResultArray(0).getDetails().getGuaranteeState();
                //
                // GuaranteeTermStateDefinition.FULFILLED
                // GuaranteeTermStateDefinition.VIOLATED
                // GuaranteeTermStateDefinition.NOT_DETERMINED
                //
                if(guaranteeState.getState() == GuaranteeTermStateDefinition.FULFILLED)
                	System.out.println("guarantee [" +  guaranteeState.getTermName() + "] is fulfilled");
                else if(guaranteeState.getState() == GuaranteeTermStateDefinition.VIOLATED)
                	System.out.println("guarantee [" +  guaranteeState.getTermName() + "] is violated");
                
                maxIterations--;
            }
            catch ( Exception e )
            {
                fail( "notification resource was not found on the server. " + e.getMessage() );
            }
        }
         
        agreement.terminate();
        
        System.out.println("agreement is terminated");
        
        System.out.println( "testServiceGuarantee successfully completed" );
    }
}
