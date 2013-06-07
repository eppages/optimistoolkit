/* 
X * Copyright (c) 2011, Fraunhofer-Gesellschaft
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

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import eu.optimis.sla.notification.Subscription;
import eu.optimis.sla.notification.SubscriptionRegistry;
import eu.optimis.subscription.SubscriptionDetailDocument;

/**
 * @author owaeld
 * 
 */
//@Path( "/subscriptions/{subscriptionId}/" )
public class SubscriptionProxy implements Subscription
{
    private String subscriptionId;
    private String agreementId;
    private URI notificationURI;

    
//    public SubscriptionProxy( @PathParam("{subscriptionId}") String id )
//    {
//        subscriptionId = id;
//    }
    
    public SubscriptionProxy( String id )
    {
        subscriptionId = id;
    }
    
    /**
     * Creates a new resource with the given id and a specific notification target url.
     * 
     * @param id
     *            the notification id
     * @param notificationURI
     *            the URL where SLA monitoring events are published to
     */
    public SubscriptionProxy( String id, String agreementId, URI notificationURI )
    {
        subscriptionId = id;
        this.agreementId = agreementId;
        this.notificationURI = notificationURI;
    }

//    /**
//     * 
//     * @return the id of this subscription
//     */
//    protected String getId() {
//        return subscriptionId;
//    }
    
    /**
     * {@inheritDoc}
     * 
     * @see eu.optimis.sla.notification.Subscription#getSubscriptionDetails()
     */
    public SubscriptionDetailDocument getSubscriptionDetails()
    {
        SubscriptionDetailDocument result = SubscriptionDetailDocument.Factory.newInstance();
        result.addNewSubscriptionDetail();
        
        //
        // TODO
        //
        result.getSubscriptionDetail().setAgreementId( agreementId );
        result.getSubscriptionDetail().setURI( notificationURI.toASCIIString() );
        return result;
    }
    
    /**
     * {@inheritDoc}}
     * 
     * @see eu.optimis.sla.notification.Subscription#terminate()
     */
    public void terminate()
    {
        SubscriptionRegistry.getInstance().remove( subscriptionId );
    }

}
