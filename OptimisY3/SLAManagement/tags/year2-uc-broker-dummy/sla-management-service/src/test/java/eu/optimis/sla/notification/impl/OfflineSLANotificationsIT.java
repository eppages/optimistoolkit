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
package eu.optimis.sla.notification.impl;

import java.net.URI;
import java.util.TimerTask;
import java.util.UUID;

import eu.optimis.sla.AbstractSLAIT;

import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventDocument;

import eu.optimis.sla.accounting.SimpleAccountingSystem;
import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.NotificationEndpointFactoryService;

/**
 * @author hrasheed
 * 
 */
public class OfflineSLANotificationsIT extends AbstractSLAIT
{
    
    private String NOTIFICATION_URL = getApplicationURL("/sla/notifications");


    public void testNotificats() throws Exception {

        //
        // create notification endpoint via the notification endpoint factory
        //
        NotificationEndpointFactoryService notificationService =
                        ClientFactory.create( NOTIFICATION_URL, NotificationEndpointFactoryService.class );

        URI notificationUri = notificationService.createNotificationEndpoint();
        
        //
        // add new subscription for the endpoint
        //
        SubscriptionServiceImpl subscriptionService = new SubscriptionServiceImpl();
        subscriptionService.createSubscription( "123", notificationUri, UUID.randomUUID().toString() );
        
        //
        // create a notification event for agreement with id=123 and use the accouting system to 
        // add a new entry in the notification registry
        //
        SimpleAccountingSystem acc = new SimpleAccountingSystem();

        SLAMonitoringNotificationEventDocument doc1 = SLAMonitoringNotificationEventDocument.Factory.newInstance();
        doc1.addNewSLAMonitoringNotificationEvent();
        
        SLAMonitoringNotificationEventDocument doc = SLAMonitoringNotificationEventDocument.Factory.parse( OfflineSLANotificationsIT.class.getResourceAsStream("/notificationEvent.xml"));
        acc.issueCompensation( doc.getSLAMonitoringNotificationEvent() );
        
        
        //
        // run notification service (task)
        //
        TimerTask task = new NotificationTimer();
        task.run();

        //
        // add two additional subscriptions, issue a new event and run notification task 
        //
        subscriptionService.createSubscription( "123", notificationUri, UUID.randomUUID().toString() );
        subscriptionService.createSubscription( "123", notificationUri, UUID.randomUUID().toString() );

        acc.issueCompensation( doc.getSLAMonitoringNotificationEvent() );
        
        task.run();

        //
        // finally the notification history of the endpoint should contain 4 entries
        //
        NotificationEndpoint notificationClient = ClientFactory.create( notificationUri, NotificationEndpoint.class );
        assertNotNull( notificationClient.getNotificationEventHistory() );
        
        System.out.println("event history: " + notificationClient.getNotificationEventHistory().getNotificationEventCollection().sizeOfNotificationEventArray());  
    }

}
