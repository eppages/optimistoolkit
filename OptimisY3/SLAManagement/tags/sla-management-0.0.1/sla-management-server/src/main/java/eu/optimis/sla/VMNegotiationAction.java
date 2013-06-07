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

import java.text.MessageFormat;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.Quote;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.graap.wsag.server.actions.AbstractNegotiationAction;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import com.sun.jersey.core.util.MultivaluedMapImpl;

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
 * @author hrasheed
 * 
 */
public class VMNegotiationAction extends AbstractNegotiationAction
{

    private static final Logger LOG = Logger.getLogger( VMNegotiationAction.class );
    
    private static final String AC_KEY = "VMServiceInstantiation.url.ac"; //$NON-NLS-1$

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
        
        NegotiationOfferType counterOffer = (NegotiationOfferType) negotiationOffer.copy();
        
        counterOffer.setOfferId( negotiationOffer.getOfferId() );
        counterOffer.getNegotiationOfferContext().setCounterOfferTo( negotiationOffer.getOfferId() );

        //
        // simply grep the service manifest document provided in the negotiation offer
        //
        Manifest negotiationManifest = null;

        try
        {
            XmlBeanServiceManifestDocument serviceManifestDoc =
                Tools.negotiationOfferToServiceManifest( counterOffer );

            negotiationManifest = Manifest.Factory.newInstance( serviceManifestDoc );

            negotiationManifest.initializeIncarnatedVirtualMachineComponents();

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "negotiation offer offer manifest: " + negotiationManifest.toXmlBeanObject().xmlText() );
            }
        }
        catch ( Exception e )
        {
            throw new NegotiationException( "Error in retrieving service manifest from negotiation offer.", e );
        }
 
        String allocationOffer = null;
        
        try
        {
            String acURL = ComponentConfigurationProvider.getString( AC_KEY );

            LOG.info( MessageFormat.format( "negotiation:calling admission control at {0}", new Object[] { acURL } ) );

            AdmissionControlClient admissionControlClient = new AdmissionControlClient();

            XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();

            XmlBeanServiceManifestDocument manifestBean = negotiationManifest.toXmlBeanObject();

            MultivaluedMap<String,String> formParams = new MultivaluedMapImpl();
            formParams.add( "serviceManifest", manifestBean.xmlText( xmlOptions ) );

            MultivaluedMap<String, String> allocationOffers = admissionControlClient.performACTest( formParams );
                 
            allocationOffer = allocationOffers.get( "serviceManifest" ).get( 0 );

            LOG.info( "negotiation:calling admission control done: number of received allocation offers: ["
                + allocationOffers.size() + "] for service: " +  negotiationManifest.getVirtualMachineDescriptionSection().getServiceId());

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "admission control plain response: " + allocationOffer );
            }
            
        } catch (Exception e)
        {
            e.printStackTrace(); 
            LOG.error( e );
            throw new NegotiationException( "Error calling admission control.", e );
        }
        
        Manifest acManifest = null;
        
        try
        {
            //
            // response is a service manifest with IP extensions
            //
            XmlBeanServiceManifestDocument allocManifestDoc =
                (XmlBeanServiceManifestDocument) XmlObject.Factory.parse( allocationOffer );
            XmlValidator.validate( allocManifestDoc );

            acManifest = Manifest.Factory.newInstance( allocManifestDoc );

            if ( LOG.isTraceEnabled() )
            {
                XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
                xmlOptions.setSaveAggressiveNamespaces();
                LOG.trace( "admission control parsed allocation offer: " + allocManifestDoc.xmlText( xmlOptions ) );
            }
            
            InfrastructureProviderExtension ipExtensions =
                acManifest.getInfrastructureProviderExtensions();

            //
            // Allocation Offer from AC SHOULDN'T be part of the counter negotiation offer
            //
//            ServiceDescriptionTermType manifestSDT = null;
//            
//            ServiceDescriptionTermType[] sdts = counterOffer.getTerms().getAll().getServiceDescriptionTermArray();
//           
//            if ( sdts != null )
//            {
//                for ( int i = 0; i < sdts.length; i++ )
//                {
//                    if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT" ) )
//                    {
//                        manifestSDT = sdts[i];
//                        break;
//                    }
//                }
//            }
//           
//            String name = manifestSDT.getName();
//            String serviceName = manifestSDT.getServiceName();
//           
//            manifestSDT.set( acManifest.toXmlBeanObject() );
//            manifestSDT.setName( name );
//            manifestSDT.setServiceName( serviceName );

            if ( ipExtensions.isSetAllocationOffer() )
            {
                if ( ipExtensions.getAllocationOffer().getDecision() != null )
                {
                    if ( ipExtensions.getAllocationOffer().getDecision().equals( "rejected" ) )
                    {
                        LOG.info( "service could not be admidded by admission control - [Rejected]" );
                        counterOffer.getNegotiationOfferContext().setState(
                                                                               NegotiationOfferStateType.Factory.newInstance() );
                        counterOffer.getNegotiationOfferContext().getState().addNewRejected();
                    } 
                    else if( ipExtensions.getAllocationOffer().getDecision().equals( "accepted" ) )
                    {
                        LOG.info( "service can be admidded by admission control - [Accepted]" );
                        counterOffer.getNegotiationOfferContext().setState(
                                                                               NegotiationOfferStateType.Factory.newInstance() );
                        counterOffer.getNegotiationOfferContext().getState().addNewAcceptable();
                    } 
                    else if( ipExtensions.getAllocationOffer().getDecision().equals( "partial" ) )
                    {
                        LOG.info( "service could not be admidded by admission control - [Partially Accepted]" );
                        counterOffer.getNegotiationOfferContext().setState(
                                                                               NegotiationOfferStateType.Factory.newInstance() );
                        counterOffer.getNegotiationOfferContext().getState().addNewRejected();
                    }
                }
                else
                {
                    
                    String message =
                        "service could not be admidded by admission control: "
                            + "can't interpret decision in allocation offer";
                    LOG.error( message );
                    counterOffer.getNegotiationOfferContext().setState(
                                                                           NegotiationOfferStateType.Factory.newInstance() );
                    counterOffer.getNegotiationOfferContext().getState().addNewRejected();
                }

            }
            else
            {
                LOG.error( "no allocation offer received from AC for service: " + acManifest.getVirtualMachineDescriptionSection().getServiceId() );
                counterOffer.getNegotiationOfferContext().setState(
                                                                       NegotiationOfferStateType.Factory.newInstance() );
                counterOffer.getNegotiationOfferContext().getState().addNewRejected();
            }
            
            //
            // TODO maintain offer and counter offer IDs
            //
            
            return new NegotiationOfferType[] { counterOffer };
        }
        catch ( Exception e )
        {
            e.printStackTrace(); 
            LOG.error( e );
            throw new NegotiationException("Error creating counter negotiation offer from AC received offer.", e );
        }
    }
}
