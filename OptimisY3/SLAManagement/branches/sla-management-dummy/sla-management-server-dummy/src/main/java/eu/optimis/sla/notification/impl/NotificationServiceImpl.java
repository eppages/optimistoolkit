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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.NotificationEndpointFactoryService;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;

/**
 * @author owaeld
 * 
 */
public class NotificationServiceImpl
    implements NotificationEndpointFactoryService
{
    @Context
    UriInfo uriInfo;

    //
    // TODO make the notification registry as a singleton, so that the service can be included in other
    // applications and the creation of new notifications can be done bypassing the REST interface
    //
    private static Map<String, NotificationEndpoint> notificationResources =
        new HashMap<String, NotificationEndpoint>();

    protected static Map<String, NotificationEndpoint> getNotificationRegistry()
    {
        return notificationResources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.optimis.sla.notification.NotificationReceiver#receiveSLAMonitoringNofification(org.ogf.graap.wsag4j
     * .types.engine.SLAMonitoringNotificationEventType)
     */
    public NotificationEndpoint getNotification( String notificationId ) throws ResourceNotFoundException
    {
        if ( notificationResources.containsKey( notificationId ) )
        {
            return notificationResources.get( notificationId );
        }
        else
        {
            final String message = "the referenced notification was not found";
            throw new ResourceNotFoundException( message );
            // SubscriptionStatus status = SubscriptionStatus.NOT_FOUND;
            // status.setReasonPhrase( message );
            //
            // Response response = Response.status( status ).entity( message ).type( MediaType.TEXT_PLAIN
            // ).build();
            // throw new WebApplicationException( new Exception( message ), response );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.NotificationService#createNotification(java.lang.String)
     */
    public URI createNotificationEndpoint()
    {
        String notificationId = UUID.randomUUID().toString();

        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI subscriptionUri = ub.path( notificationId ).build();

        notificationResources.put( notificationId, new NotificationImpl( notificationId ) );

        return subscriptionUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.NotificationService#listNotifications()
     */
    public List<URI> listNotifications()
    {
        // ResourceCollectionDocument result = ResourceCollectionDocument.Factory.newInstance();
        // result.addNewResourceCollection();
        Vector<URI> uris = new Vector<URI>();

        Iterator<String> it = notificationResources.keySet().iterator();
        while ( it.hasNext() )
        {
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI userUri = ub.path( it.next() ).build();
            // result.getResourceCollection().addNewURI().setStringValue( userUri.toASCIIString() );
            uris.add( userUri );

        }
        return uris;
    }

}
