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

import javax.ws.rs.Path;

import eu.optimis.subscription.SubscriptionDetailDocument;
import eu.optimis.subscription.SubscriptionDetailType;

/**
 * @author owaeld
 * 
 */
@Path( "/subscriptions" )
public class SubscriptionImpl
{

    private String id;

    private SubscriptionDetailDocument details;

    /**
     * @return the details
     */
    public SubscriptionDetailDocument getDetails()
    {
        return details;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * default constructor
     */
    public SubscriptionImpl()
    {
        details = SubscriptionDetailDocument.Factory.newInstance();
        details.addNewSubscriptionDetail();
    }

    /**
     * @return
     * @see eu.optimis.subscription.SubscriptionDetailType#getAgreementId()
     */
    public String getAgreementId()
    {
        return details.getSubscriptionDetail().getAgreementId();
    }

    /**
     * @return
     * @see eu.optimis.subscription.SubscriptionDetailType#getURI()
     */
    public String getURI()
    {
        return details.getSubscriptionDetail().getURI();
    }

    /**
     * @param arg0
     * @see eu.optimis.subscription.SubscriptionDetailType#setAgreementId(java.lang.String)
     */
    public void setAgreementId( String arg0 )
    {
        details.getSubscriptionDetail().setAgreementId( arg0 );
    }

    /**
     * @param arg0
     * @see eu.optimis.subscription.SubscriptionDetailType#setURI(java.lang.String)
     */
    public void setURI( String arg0 )
    {
        details.getSubscriptionDetail().setURI( arg0 );
    }

}
