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

import java.util.GregorianCalendar;

import javax.ws.rs.Path;

import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventDocument;

import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.sla.types.subscription.NotificationEventCollectionDocument;
import eu.optimis.sla.types.subscription.NotificationEventType;

/**
 * @author owaeld
 * 
 */
@Path( "/notifications/{notificationId}" )
public class NotificationImpl
    implements NotificationEndpoint
{
    private NotificationEventCollectionDocument events;

    private String id;

    /**
     * default constructor
     */
    public NotificationImpl( String notificationId )
    {
        id = notificationId;

        events = NotificationEventCollectionDocument.Factory.newInstance();
        events.addNewNotificationEventCollection();
        //
        // TODO
        //
        events.getNotificationEventCollection().setAgreementId( "123" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.optimis.sla.notification.Notification#sendSLAMonitoringNofification(org.ogf.graap.wsag4j.types.engine
     * .SLAMonitoringNotificationEventDocument)
     */
    public void publishSLAMonitoringEvent( SLAMonitoringNotificationEventDocument notificationEvent )
    {
        System.out.println( "received notification for resource: " + id );
        System.out.println( notificationEvent.xmlText( new XmlOptions().setSavePrettyPrint() ) );

        NotificationEventType event =
            (NotificationEventType) events.getNotificationEventCollection().addNewNotificationEvent()
                                          .set( notificationEvent.getSLAMonitoringNotificationEvent() );

        event.setTimestamp( new GregorianCalendar() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.Notification#getNotificationEventhistory()
     */
    public NotificationEventCollectionDocument getNotificationEventHistory()
    {
        return events;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.Notification#terminateNotification()
     */
    public void terminateNotification() throws ResourceNotFoundException
    {
        NotificationServiceImpl.getNotificationRegistry().remove( id );
    }

}
