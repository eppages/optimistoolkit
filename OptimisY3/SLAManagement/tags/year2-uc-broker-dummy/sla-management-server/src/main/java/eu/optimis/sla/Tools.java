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

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.ogf.schemas.graap.wsAgreement.AgreementType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.TermCompositorType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

import eu.optimis.sla.types.service.price.SLAServicePriceDocument;
import eu.optimis.sla.types.service.price.SLAServicePriceType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanManifestType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * Tools class to extract Manifest from Agreement Offer.
 * 
 * @author hrasheed
 */
public class Tools
{

    private static final Logger LOG = Logger.getLogger( Tools.class );

    private static String serviceXPath =
        "declare namespace opt ='http://schemas.optimis.eu/optimis/';//opt:ServiceManifest";

    public static final String SERVIC_MANIFEST_XPATH =
        "declare namespace ws='http://schemas.ggf.org/graap/2007/03/ws-agreement';"
            + "declare namespace opt='http://schemas.optimis.eu/optimis/';"
            + "$this//ws:ServiceDescriptionTerm[@ws:Name = 'OPTIMIS_SERVICE_SDT']/opt:ServiceManifest";
    
    public static final String SERVIC_PRICE_XPATH =
                    "declare namespace ws='http://schemas.ggf.org/graap/2007/03/ws-agreement';"
                        + "declare namespace service-price='http://www.optimis.eu/sla/service-price-types';"
                        + "$this//ws:ServiceDescriptionTerm[@ws:Name = 'OPTIMIS_SERVICE__PRICE_SDT']/service-price:SLAServicePrice";

    public static XmlBeanServiceManifestDocument offerToServiceManifest( AgreementType offer )
        throws Exception
    {

        LOG.info( "retrieving service manifest from service description term." );
        
        ServiceDescriptionTermType serviceSDT = null;
        
        ServiceDescriptionTermType[] sdts = offer.getTerms().getAll().getServiceDescriptionTermArray();
        
        if ( sdts != null )
        {
            for ( int i = 0; i < sdts.length; i++ )
            {
                if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT" ) )
                {
                    serviceSDT = sdts[i];
                    break;
                }   
            }  
        }

        if ( serviceSDT == null )
        {
            throw new Exception( "there is no (OPTIMIS_SERVICE_SDT) service description term in agreement offer." );
        }

        // XmlObject[] serviceXML = serviceSDT.selectPath(serviceXPath);

        XmlObject[] serviceXML =
            serviceSDT.selectChildren( XmlBeanServiceManifestDocument.type.getDocumentElementName() );

        if ( serviceXML.length == 0 )
        {
            throw new Exception( "there is no service manifest doc in service description terms." );
        }

        XmlBeanManifestType serviceManifestType = (XmlBeanManifestType) serviceXML[0];

        XmlBeanServiceManifestDocument serviceManifestDoc =
            XmlBeanServiceManifestDocument.Factory.newInstance();
        serviceManifestDoc.addNewServiceManifest().set( serviceManifestType );

        return serviceManifestDoc;
    }

    public static XmlBeanServiceManifestDocument
        negotiationOfferToServiceManifest( NegotiationOfferType quote ) throws Exception
    {

        LOG.info( "retrieving service manifest from negotiation offer." );

        ServiceDescriptionTermType serviceSDT = null;

        ServiceDescriptionTermType[] sdts = quote.getTerms().getAll().getServiceDescriptionTermArray();
        
        if ( sdts != null )
        {
            for ( int i = 0; i < sdts.length; i++ )
            {
                if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT" ) )
                {
                    serviceSDT = sdts[i];
                    break;
                }   
            }  
        }

        if ( serviceSDT == null )
        {
            throw new Exception( "there is no (OPTIMIS_SERVICE_SDT) service description term in negotiation offer." );
        }

        // XmlObject[] serviceXML = serviceSDT.selectPath(serviceXPath);

        XmlObject[] serviceXML =
            serviceSDT.selectChildren( XmlBeanServiceManifestDocument.type.getDocumentElementName() );

        if ( serviceXML.length == 0 )
        {
            throw new Exception( "there is no service manifest doc in service term state." );
        }

        XmlBeanManifestType serviceManifestType = (XmlBeanManifestType) serviceXML[0];

        XmlBeanServiceManifestDocument serviceManifestDoc =
            XmlBeanServiceManifestDocument.Factory.newInstance();
        serviceManifestDoc.addNewServiceManifest().set( serviceManifestType );

        return serviceManifestDoc;
    }
    
    public static SLAServicePriceType getServicePrice( TermCompositorType terms ) throws Exception
    {
        LOG.info( "retrieving service price doc from Terms." );
        
        ServiceDescriptionTermType servicePriceSDT = null;
        
        ServiceDescriptionTermType[] sdts = terms.getServiceDescriptionTermArray();
        
        if ( sdts != null )
        {
            for ( int i = 0; i < sdts.length; i++ )
            {
                if ( sdts[i].getName().equals( "OPTIMIS_SERVICE__PRICE_SDT" ) )
                {
                    servicePriceSDT = sdts[i];
                    break;
                }   
            }  
        }
        
        if ( servicePriceSDT == null )
        {
            throw new Exception( "there is no service price SDT in agreement template." );
        }
            
        XmlObject[] servicePriceXML = servicePriceSDT.selectChildren( SLAServicePriceDocument.type.getDocumentElementName() );
        
        if ( servicePriceXML.length == 0 )
        {
            throw new Exception( "there is no service price document in service description terms." );
        }
        
        SLAServicePriceType servicePriceType = (SLAServicePriceType) servicePriceXML[0];
        
        return servicePriceType;
    }
}