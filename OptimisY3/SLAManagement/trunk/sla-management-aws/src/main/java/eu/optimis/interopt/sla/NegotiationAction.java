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
package eu.optimis.interopt.sla;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.Quote;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import java.util.Map;
import java.util.Properties;

/**
 * This class is the default negotiation implementation for the OPTIMIS-Arsys template.
 *
 * @author hrasheed
 * @see: eu.optimis.interopt.sla.ManifestValidator for validation parameters
 */
public class NegotiationAction extends AbstractNegotiationAction
{

    private static final Logger LOG = Logger.getLogger(NegotiationAction.class);

    /**
     * Unsupported method. This method is deprecated.
     *
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.graap.wsag.api.Quote)
     */
    @Override
    public AgreementTemplateType[] negotiate(Quote quote) throws NegotiationException
    {
        throw new UnsupportedOperationException("This method is not supported by " + getClass().getName());
    }

    /**
     * Negotiates an agreement offer based on the incoming quote.
     *
     * @todo add description on negotiation strategy
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType,
     *      java.util.Map)
     */
    @Override
    public NegotiationOfferType[] negotiate(NegotiationOfferType negotiationOffer, Map context)
            throws NegotiationException
    {

        NegotiationOfferType counterOffer = (NegotiationOfferType) negotiationOffer.copy();

        counterOffer.setOfferId(negotiationOffer.getOfferId());
        counterOffer.getNegotiationOfferContext().setCounterOfferTo(negotiationOffer.getOfferId());

        // grep the service manifest document provided in the negotiation offer
        Manifest negotiationManifest = null;
        
        String serviceId = null;

        try
        {
            XmlBeanServiceManifestDocument serviceManifestDoc =
                    Tools.negotiationOfferToServiceManifest(counterOffer);
            negotiationManifest = Manifest.Factory.newInstance(serviceManifestDoc);
            serviceId = negotiationManifest.getVirtualMachineDescriptionSection().getServiceId();
            
            // incarnation of the Manifest
            negotiationManifest.initializeIncarnatedVirtualMachineComponents();
            
            if (LOG.isTraceEnabled())
            {
                LOG.trace("negotiation offer offer manifest: " + negotiationManifest.toXmlBeanObject().xmlText());
            }
        }
        catch (Exception e)
        {
            throw new NegotiationException("Error in retrieving service manifest from negotiation offer.", e);
        }

        // validate the manifest properties
        try
        {
            Properties serviceConfigProps = Tools.loadServiceConfigurationProperties();
            ManifestValidator validator = new ManifestValidator(serviceConfigProps);
            validator.validate(negotiationManifest);
        }
        catch ( Exception e )
        {
            // service manifest is rejected
            LOG.info("service [" + serviceId + "] can not be admitted - [Rejected]");
            LOG.info("Negotiation Offer Validation Failed for service : [" + serviceId + "] - " + e.getMessage());

            counterOffer.getNegotiationOfferContext().setState(NegotiationOfferStateType.Factory.newInstance());
            counterOffer.getNegotiationOfferContext().getState().addNewRejected();

            throw new NegotiationException("Negotiation Offer Validation Failed for service [" + serviceId + "] : Reason.", e);
        }

        // service manifest is accepted
        LOG.info( "service [" + serviceId + "] can be admitted - [Accepted]" );
        counterOffer.getNegotiationOfferContext().setState(NegotiationOfferStateType.Factory.newInstance());
        counterOffer.getNegotiationOfferContext().getState().addNewAcceptable();

        return new NegotiationOfferType[]{ counterOffer };
    }

}