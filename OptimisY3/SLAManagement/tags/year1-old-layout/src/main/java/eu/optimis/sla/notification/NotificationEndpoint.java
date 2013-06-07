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
package eu.optimis.sla.notification;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.annotations.DataBinding;
import org.apache.cxf.xmlbeans.XmlBeansDataBinding;
import org.ogf.graap.wsag4j.types.engine.SLAMonitoringNotificationEventDocument;

import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.subscription.NotificationEventCollectionDocument;

/**
 * @author owaeld
 * 
 */
@DataBinding( XmlBeansDataBinding.class )
public interface NotificationEndpoint
{
    /**
     * Receives a notification for a SLA monitoring event. The notification event contains the agreement id it
     * is related to and the evaluation results for all guarantees defined in a SLA. Each evaluation result
     * consists of the description of the guarantee and the state of the guarantee after the last assessment.
     * 
     * @param notificationId
     *            the id of the notification resource
     * @param notificationEvent
     *            the notification
     */
    @POST
    @Consumes( MediaType.APPLICATION_XML )
    void publishSLAMonitoringEvent( SLAMonitoringNotificationEventDocument notificationEvent )
        throws ResourceNotFoundException;

    /**
     * Returns the history of notification events for this notification resource. The notification events can
     * be retrieved by polling the notification resource.
     * 
     * @return a collection of notification events
     */
    @GET
    @Produces( MediaType.APPLICATION_XML )
    NotificationEventCollectionDocument getNotificationEventHistory() throws ResourceNotFoundException;

    /**
     * Terminates the notification and removes the notification endpoint. If a subscription exists that
     * references this notification endpoint all calls will result in a {@link ResourceNotFoundException} in
     * the subscription service.
     * 
     * @throws ResourceNotFoundException
     */
    @DELETE
    void terminateNotification() throws ResourceNotFoundException;
}
