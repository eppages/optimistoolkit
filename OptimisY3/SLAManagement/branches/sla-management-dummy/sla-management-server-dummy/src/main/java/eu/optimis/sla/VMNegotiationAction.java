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

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.Quote;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis.manifest.api.ip.InfrastructureProviderExtension;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.utils.XmlValidator;
import eu.optimis.sla.rest.AdmissionControlClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

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

    private static final Logger LOG = Logger.getLogger( VMNegotiationAction.class );

    /**
     * Unsupported method. This method is deprecated.
     * 
     * @see org.ogf.graap.wsag.server.actions.INegotiationAction#negotiate(org.ogf.graap.wsag.api.Quote)
     */
    @Override
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
    @Override
    public NegotiationOfferType[] negotiate( NegotiationOfferType negotiationOffer, Map context )
        throws NegotiationException
    {

        //
        // simply grep the service manifest document provided in the negotiation offer
        //
        Manifest negotiationManifest = null;

        try
        {
            XmlBeanServiceManifestDocument serviceManifestDoc =
                Tools.offerToServiceManifest( negotiationOffer );

            negotiationManifest = Manifest.Factory.newInstance( serviceManifestDoc );

            negotiationManifest.initializeIncarnatedVirtualMachineComponents();

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "agreement offer manifest: " + negotiationManifest.toXmlBeanObject().xmlText() );
            }
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "Error in retrieving service manifest from negotiation offer.", e );
        }

        String allocationOffer = null;

        try
        {
            LOG.info( "calling admission control for negotiation" );

            AdmissionControlClient admissionControlClient = new AdmissionControlClient();

            XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();

            XmlBeanServiceManifestDocument manifestBean = negotiationManifest.toXmlBeanObject();

            MultivaluedMap<String, String> formParams = new MetadataMap<String, String>();
            formParams.add( "serviceManifest", manifestBean.xmlText( xmlOptions ) );

            MultivaluedMap<String, String> allocationOffers =
                admissionControlClient.performACTest( formParams );

            allocationOffer = allocationOffers.get( "serviceManifest" ).get( 0 );

            LOG.info( "calling admission control done: number of received allocation offers: ["
                + allocationOffers.size() + "]" );

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "admission control plain response: " + allocationOffer );
            }
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "Error calling admission control.", e );
        }

        try
        {
            //
            // response is a service manifest with IP extensions
            //
            XmlBeanServiceManifestDocument manifestDoc =
                (XmlBeanServiceManifestDocument) XmlObject.Factory.parse( allocationOffer );
            XmlValidator.validate( manifestDoc );

            Manifest acManifest = Manifest.Factory.newInstance( manifestDoc );

            XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "admission control parsed allocation offer: " + manifestDoc.xmlText( xmlOptions ) );
            }

            InfrastructureProviderExtension ipExtensions = acManifest.getInfrastructureProviderExtensions();

            if ( ipExtensions.getAllocationOffer().getDecision().equals( "rejected" ) )
            {
                // if allocation offer is rejected then incoming offer is set to rejected
                // reason of rejection can also be specified
                // TODO return offer with correct status
                return new NegotiationOfferType[] {};
            }

            // ServiceDescriptionTermType manifestSDT = null;
            //
            // ServiceDescriptionTermType[] sdts =
            // negotiationOffer.getTerms().getAll().getServiceDescriptionTermArray();
            //
            // if ( sdts != null )
            // {
            // for ( int i = 0; i < sdts.length; i++ )
            // {
            // if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT" ) )
            // {
            // manifestSDT = sdts[i];
            // break;
            // }
            // }
            // }
            //
            // String name = manifestSDT.getName();
            // String serviceName = manifestSDT.getServiceName();
            //
            // manifestSDT.set( manifestDoc );
            // manifestSDT.setName( name );
            // manifestSDT.setServiceName( serviceName );

            //
            // TODO maintain offer and counter offer IDs
            //

            negotiationOffer.getNegotiationOfferContext().setState(
                NegotiationOfferStateType.Factory.newInstance() );
            negotiationOffer.getNegotiationOfferContext().getState().addNewAcceptable();

            return new NegotiationOfferType[] { negotiationOffer };

        }
        catch ( Exception e )
        {
            throw new NegotiationException(
                "Error creating counter negotiation offer from AC received offer.", e );
        }
    }
}
