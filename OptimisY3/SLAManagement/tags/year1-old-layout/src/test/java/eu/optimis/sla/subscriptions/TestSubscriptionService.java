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
package eu.optimis.sla.subscriptions;

import java.net.URI;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;

import junit.framework.TestCase;
import eu.optimis.sla.notification.NotificationEndpoint;
import eu.optimis.sla.notification.NotificationEndpointFactoryService;
import eu.optimis.sla.notification.Subscription;
import eu.optimis.sla.notification.SubscriptionService;
import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.sla.notification.impl.ClientFactory;
import eu.optimis.subscription.ResourceCollectionDocument;

/**
 * @author owaeld
 * 
 */
public class TestSubscriptionService extends TestCase
{
    String SUBSCRIPTION_URL = "http://127.0.0.1:8080/optimis-sla/sla/subscriptions";

    String NOTIFICATION_SERVICEURL = "http://127.0.0.1:8080/optimis-sla/sla/notifications";

    String NOTIFICATION_URL = "http://127.0.0.1:8080/optimis-sla/sla/notifications";

    String NA_NOTIFICATION_URL = "http://127.0.0.1:8080/optimis-sla/sla/notifications/1234";

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        //
        // first terminate all notifications
        //
        SubscriptionService subscriptionService =
            ClientFactory.create( SUBSCRIPTION_URL, SubscriptionService.class );

        List<URI> subscriptions = subscriptionService.listSubscriptions();
        for ( int i = 0; i < subscriptions.size(); i++ )
        {
            Subscription subscription = ClientFactory.create( subscriptions.get( i ), Subscription.class );
            subscription.terminate();
        }

        //
        // then terminate all subscriptions
        //
        NotificationEndpointFactoryService notificationService =
            ClientFactory.create( NOTIFICATION_SERVICEURL, NotificationEndpointFactoryService.class );

        List<URI> notifications = notificationService.listNotifications();
        for ( int i = 0; i < notifications.size(); i++ )
        {
            NotificationEndpoint notification = ClientFactory.create( notifications.get( i ), NotificationEndpoint.class );
            notification.terminateNotification();
        }
    }

    /**
     * Tests the subscription service with respect to the following features:
     * <ul>
     * <li>create new subscription instances</li>
     * <li>terminate existing subscription instances</li>
     * </ul>
     */
    public void testCreateSubscription()
    {
        NotificationEndpointFactoryService notificationService =
            ClientFactory.create( NOTIFICATION_URL, NotificationEndpointFactoryService.class );

        URI notificationUri1 = notificationService.createNotificationEndpoint();
        URI notificationUri2 = notificationService.createNotificationEndpoint();
        URI notificationUri3 = notificationService.createNotificationEndpoint();

        SubscriptionService subscriptionService =
            ClientFactory.create( SUBSCRIPTION_URL, SubscriptionService.class );
        //
        // when a new subscription was created the subscription resources are
        // maintained in the same order
        //
        URI subscriptionUri1 = subscriptionService.subscribe( "123", notificationUri1 );
        List<URI> subscriptions = subscriptionService.listSubscriptions();
        assertEquals( "unexpected no. subscriptions (iteration 1)", 1, subscriptions.size() );
        assertTrue( "missing subscription uri 1 (iteration 1)", subscriptions.contains( subscriptionUri1 ) );

        URI subscriptionUri2 = subscriptionService.subscribe( "123", notificationUri2 );
        subscriptions = subscriptionService.listSubscriptions();
        assertEquals( "unexpected no. subscriptions (iteration 2)", 2, subscriptions.size() );
        assertTrue( "missing subscription uri 1 (iteration 2)", subscriptions.contains( subscriptionUri1 ) );
        assertTrue( "missing subscription uri 2 (iteration 2)", subscriptions.contains( subscriptionUri2 ) );

        URI subscriptionUri3 = subscriptionService.subscribe( "123", notificationUri3 );
        subscriptions = subscriptionService.listSubscriptions();
        assertEquals( "unexpected no. subscriptions (iteration 3)", 3, subscriptions.size() );
        assertTrue( "missing subscription uri 1 (iteration 3)", subscriptions.contains( subscriptionUri1 ) );
        assertTrue( "missing subscription uri 2 (iteration 3)", subscriptions.contains( subscriptionUri2 ) );
        assertTrue( "missing subscription uri 3 (iteration 3)", subscriptions.contains( subscriptionUri3 ) );

        //
        // GIVEN we have 3 subscriptions
        //
        Subscription subscription1 = ClientFactory.create( subscriptionUri1, Subscription.class );
        Subscription subscription2 = ClientFactory.create( subscriptionUri2, Subscription.class );
        Subscription subscription3 = ClientFactory.create( subscriptionUri3, Subscription.class );

        //
        // WHEN a subscription is terminated
        //
        try
        {
            subscription2.terminate();
        }
        catch ( ResourceNotFoundException e )
        {
            fail( "The subscription resource was not found. " + e.getMessage() );
        }

        //
        // THEN the subscription is removed
        //
        subscriptions = subscriptionService.listSubscriptions();
        assertEquals( "unexpected no. subscriptions", 2, subscriptions.size() );

        //
        // AND the remaining subscriptions are in the same order as created
        //
        assertTrue( "missing subscription uri 1 (after terminate)", subscriptions.contains( subscriptionUri1 ) );
        assertTrue( "missing subscription uri 3 (after terminate)", subscriptions.contains( subscriptionUri3 ) );

        //
        // WHEN the remaining subscriptions are terminated
        //
        try
        {
            subscription1.terminate();
            subscription3.terminate();
        }
        catch ( ResourceNotFoundException e )
        {
            fail( "The subscription resource was not found. " + e.getMessage() );
        }

        //
        // THEN there are no subscriptions left
        //
        subscriptions = subscriptionService.listSubscriptions();
        assertEquals( "unexpected no. subscriptions (after terminate all)", 0, subscriptions.size() );
    }

    private boolean containsURL( String url, ResourceCollectionDocument resources )
    {
        String[] uris = resources.getResourceCollection().getURIArray();
        for ( int i = 0; i < uris.length; i++ )
        {
            if ( url.equals( uris[i] ) )
            {
                return true;
            }
        }
        return false;
    }

    public void testCreateNotification()
    {
        NotificationEndpointFactoryService notificationService =
            ClientFactory.create( NOTIFICATION_SERVICEURL, NotificationEndpointFactoryService.class );

        URI notificationURL = notificationService.createNotificationEndpoint();
        System.out.println( "created notification endpoint at " + notificationURL );

        NotificationEndpoint notification = ClientFactory.create( notificationURL, NotificationEndpoint.class );

        try
        {
            System.out.println( notification.getNotificationEventHistory().xmlText( new XmlOptions().setSavePrettyPrint() ) );
        }
        catch ( ResourceNotFoundException e )
        {
            fail( "The requested notification resource was not found on the server. " + e.getMessage() );
        }

        List<URI> uris = notificationService.listNotifications();
        assertEquals( 1, uris.size() );
    }

    public void testAccessNotExistingNotification()
    {
        NotificationEndpointFactoryService notificationService =
            ClientFactory.create( NOTIFICATION_SERVICEURL, NotificationEndpointFactoryService.class );

        try
        {
            //
            // getNotification() constructs the REST uri without invoking it.
            //
            NotificationEndpoint notification = notificationService.getNotification( "1234" );

            //
            // getNotificationEventHistory() executes the REST HTTP call. The HTTP GET request may result
            // in a HTTP 404 error.
            //
            System.out.println( notification.getNotificationEventHistory().xmlText( new XmlOptions().setSavePrettyPrint() ) );

        }
        catch ( ResourceNotFoundException e )
        {
            System.out.println( "ResourcenotFoundException successfully catched" );
        }
        catch ( Exception e )
        {
            fail( "The error received is not a ResourcenotFoundException. " + e.getMessage() );
        }
    }
}
