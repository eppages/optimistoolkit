/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
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
package eu.optimis.sla;

import java.util.Map;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Quote;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis.sla.rest.ACModelApi;
import eu.optimis.types.servicemanifest.ServiceManifestDocument;

/**
 * This class is the default negotiation implementation for the OPTIMIS-VM template.
 * 
 * @todo: add description on negotiable parameters
 * 
 * @author owaeld
 * 
 */
public class VMNegotiationAction extends AbstractNegotiationAction
{

    Logger log = Logger.getLogger( VMNegotiationAction.class );

    /**
     * Unsupported method. This method is deprecated.
     * 
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.graap.wsag.api.Quote)
     */
    public AgreementTemplateType[] negotiate( Quote quote ) throws NegotiationException
    {
        throw new UnsupportedOperationException( "This method is not supported by " + getClass().getName() );
    }

    /**
     * Negotiates an agreement offer based on the incoming quote.
     * 
     * @todo add description on negotiation strategy
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType,
     *      java.util.Map)
     */
    public NegotiationOfferType[] negotiate( NegotiationOfferType quote, Map context )
        throws NegotiationException
    {

        try
        {
            String ac_url = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.ac" ); //$NON-NLS-1$

            ServiceManifestDocument manifest = Tools.offerToServiceManifest( quote );

            try
            {
                ACModelApi ac = JAXRSClientFactory.create( ac_url, ACModelApi.class );
                String response = ac.performACTest( manifest.xmlText() );
                System.out.println( response );
            }
            catch ( Exception e )
            {
                throw new NegotiationException( "Error calling admission control.", e );
            }

            quote.getNegotiationOfferContext().setState( NegotiationOfferStateType.Factory.newInstance() );
            quote.getNegotiationOfferContext().getState().addNewAcceptable();
            return new NegotiationOfferType[] { quote };
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "negotiation strategy failed", e );
        }
    }

}
