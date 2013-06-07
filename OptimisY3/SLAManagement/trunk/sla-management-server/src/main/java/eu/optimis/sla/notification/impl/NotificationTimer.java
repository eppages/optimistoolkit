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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventDocument;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventType;

import eu.optimis.sla.accounting.NotificationEventRegistry;
import eu.optimis.sla.accounting.NotificationEventStore;
import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.SubscriptionRegistry;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;

/**
 * @author hrasheed
 * 
 */
public class NotificationTimer extends TimerTask
{

    @Override
    public void run()
    {
    	
        Map<String, NotificationEventStore> cloneNotificationEventRegistry = null;

        //
        // synchronize the SLA events registry
        //
        synchronized ( NotificationEventRegistry.class )
        {
            cloneNotificationEventRegistry = NotificationEventRegistry.copy();
            // reinitialize the notification event registry
            NotificationEventRegistry.clearnotificationEventMap();
        }
        
        Iterator<String> keys = SubscriptionRegistry.getInstance().keySet().iterator();

        while ( keys.hasNext() )
        {
            SubscriptionImpl subscription = SubscriptionRegistry.getInstance().get( keys.next() );

            try
            {
                String uri = subscription.getURI();
                String agreementID = subscription.getAgreementId();

                NotificationEndpoint notificationClient =
                    ClientFactory.create( uri, NotificationEndpoint.class );
                
                NotificationEventStore eventStore = cloneNotificationEventRegistry.get( agreementID );

                if ( eventStore != null )
                {
                    List<SLAMonitoringNotificationEventType> notificationEvents = eventStore.getNotificationEvents();
                    
                    for ( SLAMonitoringNotificationEventType notificationEvent : notificationEvents )
                    {
                        SLAMonitoringNotificationEventDocument notificationEventDoc =
                            SLAMonitoringNotificationEventDocument.Factory.newInstance();
                        notificationEventDoc.addNewSLAMonitoringNotificationEvent().set( notificationEvent.copy() );

                        if ( notificationEvent.getGuaranteeEvaluationResultArray().length < 1 )
                        {
                            notificationEventDoc.getSLAMonitoringNotificationEvent().addNewGuaranteeEvaluationResult();
                        }

                        //
                        // TODO: workaround -> needs review for default name space uri in original document
                        //
                        XmlObject parsed = XmlObject.Factory.parse( notificationEventDoc.xmlText() );
                        notificationEventDoc = (SLAMonitoringNotificationEventDocument) parsed;
                        notificationClient.publishSLAMonitoringEvent( notificationEventDoc );
                    }
                }
            }
            catch ( ResourceNotFoundException e )
            {
                e.printStackTrace();
            }
            catch ( XmlException e )
            {
                e.printStackTrace();
            }
        }
    }
}
