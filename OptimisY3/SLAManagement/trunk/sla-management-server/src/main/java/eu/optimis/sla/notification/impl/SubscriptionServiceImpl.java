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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import eu.optimis.sla.ComponentConfigurationProvider;
import eu.optimis.sla.notification.Subscription;
import eu.optimis.sla.notification.SubscriptionRegistry;
import eu.optimis.sla.notification.SubscriptionService;

/**
 * @author owaeld
 * 
 */
@Path( "/" )
// @Path( "/subscriptions" )
public class SubscriptionServiceImpl
    implements SubscriptionService
{
    @Context
    UriInfo uriInfo;

    private static Timer timer = new Timer( true );

    static
    {
        TimerTask task = new NotificationTimer();
        
        long interval = 60000; // 60 seconds
        
        try {
        	interval = Long.parseLong( ComponentConfigurationProvider.getString( "sla.notification.interval" ));
        } catch( Exception e) {
        	System.out.println("error in reading notificaition interval from config, using default value.");
        }
       
        timer.schedule( task, interval, interval );
    }

    /**
     * default constructor
     */
    public SubscriptionServiceImpl()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.SLASubscriptionService#subscribe(java.lang.String, java.net.URL)
     */
    public URI subscribe( String agreementId, URI notificationUrl )
    {

        String subscriptionId = UUID.randomUUID().toString();

        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI subscriptionUri = ub.path( subscriptionId ).build();

        createSubscription( agreementId, notificationUrl, subscriptionId );

        return subscriptionUri;

        //
        // TODO: check if agreement exist, otherwise throw exception
        //
        /*
         * if ( "123".equals( agreementId ) ) { String subscriptionId = UUID.randomUUID().toString();
         * 
         * UriBuilder ub = uriInfo.getAbsolutePathBuilder(); URI subscriptionUri = ub.path( subscriptionId
         * ).build();
         * 
         * SubscriptionImpl subscription = new SubscriptionImpl();
         * 
         * subscription.setId( subscriptionId ); subscription.setAgreementId( agreementId );
         * subscription.setURI( notificationUrl.toASCIIString() );
         * 
         * SubscriptionRegistry.getInstance().put( subscription );
         * 
         * return subscriptionUri; }
         * 
         * SubscriptionStatus status = SubscriptionStatus.NOT_FOUND; status.setReasonPhrase(
         * "the referenced areement is unknown" );
         * 
         * Response response = Response.status( status ).build(); WebApplicationException exception = new
         * WebApplicationException( response );
         * 
         * throw exception;
         */
    }

    protected void createSubscription( String agreementId, URI notificationUrl, String subscriptionId )
    {
        SubscriptionImpl subscription = new SubscriptionImpl();

        subscription.setId( subscriptionId );
        subscription.setAgreementId( agreementId );
        subscription.setURI( notificationUrl.toASCIIString() );

        SubscriptionRegistry.getInstance().put( subscription );
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.SLASubscriptionService#listSubscriptions()
     */
    public List<URI> listSubscriptions()
    {
        Vector<URI> result = new Vector<URI>();
        Iterator<String> it = SubscriptionRegistry.getInstance().keySet().iterator();
        while ( it.hasNext() )
        {
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI userUri = ub.path( it.next() ).build();
            result.add( userUri );

        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.optimis.sla.notification.SubscriptionService#getSubscription(java.lang.String)
     */
    public Subscription getSubscription( String subscriptionId )
    {
        if ( SubscriptionRegistry.getInstance().containsKey( subscriptionId ) )
        {
            return new SubscriptionProxy( subscriptionId );
        }

        SubscriptionStatus status = SubscriptionStatus.NOT_FOUND;
        status.setReasonPhrase( "the referenced subscription is unknown" );

        Response response = Response.status( status ).build();
        WebApplicationException exception = new WebApplicationException( response );

        throw exception;
    }

}
