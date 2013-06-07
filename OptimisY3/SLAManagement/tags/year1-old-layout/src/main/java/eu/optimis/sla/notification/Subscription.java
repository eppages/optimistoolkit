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
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.annotations.DataBinding;
import org.apache.cxf.xmlbeans.XmlBeansDataBinding;

import eu.optimis.sla.notification.exceptions.ResourceNotFoundException;
import eu.optimis.subscription.SubscriptionDetailDocument;

/**
 * @author owaeld
 * 
 */
@DataBinding( XmlBeansDataBinding.class )
public interface Subscription
{

    /**
     * Retrieves the details for an existing subscription, if permissible. State and failures that occur
     * during this process are specified through the corresponding HTTP response codes. For example if a
     * subscriber tries to retrieve details for an unknown subscription the subscription service will return a
     * HTTP 404 response which indicates that the referenced subscription was not found. If a subscriber tries
     * to retrieve details for a subscription that is owned by another user the subscription service will
     * return a HTTP 403 response which indicates that the termination is forbidden. In case the subscription
     * details are successfully retrieved the subscription service returns a HTTP 200 response.
     * 
     * @return the subscription details
     */
    @GET
    @Consumes( MediaType.TEXT_PLAIN )
    @Produces( MediaType.APPLICATION_XML )
    SubscriptionDetailDocument getSubscriptionDetails() throws ResourceNotFoundException;

    /**
     * Terminates an existing subscription, if permissible. The result of the termination process is indicated
     * through the corresponding HTTP response codes. For example if a subscriber tries to terminate an
     * unknown subscription the subscription service will return a HTTP 404 response which indicates that the
     * referenced subscription was not found. If a subscriber tries to terminate a subscription that is owned
     * by another user the subscription service will return a HTTP 403 response which indicates that the
     * termination is forbidden. In case the subscription was terminated successfully the subscription service
     * returns a HTTP 200 response.
     * 
     * @param subscriptionId
     *            the id of the subscription to terminate
     */
    @DELETE
    @Consumes( MediaType.TEXT_PLAIN )
    @Produces( MediaType.TEXT_PLAIN )
    void terminate() throws ResourceNotFoundException;

}
