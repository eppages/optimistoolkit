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
package eu.optimis.broker.sla;

import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.NegotiationClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.api.types.NegotiationOfferTypeImpl;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;

import eu.optimis.broker.deploymentObjectiveTypes.DeploymentObjectiveDocument;
import eu.optimis.broker.deploymentObjectiveTypes.DeploymentObjectiveType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 */
public class ServiceInstantiationIT extends AbstractSLAIT 
{
	
    /**
     * Simple test case for retrieving templates from Broker SLA service
     */
    public void testSLATemplateRetrieval() throws Exception
    {
        AgreementTemplateType template = getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        //System.out.println( "OPTIMIS-BROKER-SLA: " + template.toString() );
        System.out.println( "testSLATemplateRetrieval successfully completed" );
    }
    
    /**
     * Simple test case for retrieving price of using IP service being offer through agreement template
     */
    public void testSLAPrice() throws Exception
    {
        AgreementTemplateType template = getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        //System.out.println( "OPTIMIS-BROKER-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );
        System.out.println( "testSLAPrice successfully completed" );
    }

    /**
     * Simple test case of interaction with Broker SLA service.
     */
    public void testSLACreation() throws Exception
    {

        AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        
        try 
        {
        	//
            // Now creates a negotiation context that defines the roles and obligations
            // of the negotiating parties and specifies the type of the negotiation process.
            //
            NegotiationContextDocument contextDoc = NegotiationContextDocument.Factory.newInstance();
            NegotiationContextType context = contextDoc.addNewNegotiationContext();
            context.setAgreementFactoryEPR(null);
            context.setAgreementResponder(NegotiationRoleType.NEGOTIATION_RESPONDER);
            GregorianCalendar expireDate = new GregorianCalendar();
            expireDate.add(Calendar.HOUR, 12);
            context.setExpirationTime(expireDate); 
            
            //
            // set the nature of the negotiation process (e.g. negotiation or re-negotiation).
            //
            org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationType negotiationType = context.addNewNegotiationType();
            negotiationType.addNewNegotiation();
            
            //
            // creating negotiation instance based on a negotiation context from a selected agreement factory
            //
            NegotiationClient negotiation = factory.initiateNegotiation(context);
            
            assertNotNull("falied to instantiate negotiation client.", negotiation);
            
            System.out.println( "negotiation instance is created successfully" );
            
            //
            // retrieve the agreement templates for which negotiation is supported,
            // and select the one with template name "OPTIMIS-BROKER-SERVICE-INSTANTIATION".
            //
            AgreementTemplateType[] negotiableTemplates = negotiation.getNegotiableTemplates();
            assertNotNull( negotiableTemplates );

            AgreementTemplateType brokerTemplate = null;
            
            for ( int i = 0; i < negotiableTemplates.length; i++ )
            {
                AgreementTemplateType agreementTemplate = negotiableTemplates[i];
                if ( agreementTemplate.getName().equals( TEMPLATE_NAME ) )
                {
                	brokerTemplate = agreementTemplate;
                }
            }
            assertEquals( TEMPLATE_NAME, brokerTemplate.getName() );
            
            //System.out.println( "OPTIMIS-BROKER-SLA: " + brokerTemplate.toString() );
            System.out.println( "OPTIMIS-BROKER-SLA Price: " + Tools.getServicePrice( brokerTemplate.getTerms().getAll() ).getAmount() );
            
            //
            // retrieving deployment objective from agreement template
            //
            DeploymentObjectiveType objectiveType = Tools.getDeploymentObjective( brokerTemplate.getTerms().getAll() );
            System.out.println( "DEPLOYMENT-OBJECTIVE: " + objectiveType.getStringValue() );
            
            DeploymentObjectiveDocument dep = DeploymentObjectiveDocument.Factory.newInstance();
            dep.setDeploymentObjective(DeploymentObjectiveType.COST);
            
            
            String offerID = brokerTemplate.getContext().getTemplateId() + "-" + brokerTemplate.getName();
            
            try {
        		InputStream in = getClass().getResourceAsStream("/service_manifest_2.xml");
                
                if (in == null) {
                    String message = "The service manifest file was not found.";
                    fail(message);
                }
                
                XmlBeanServiceManifestDocument serviceManifestDoc = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse(in);
                
                ServiceDescriptionTermType manifestSDT = null;
              
                ServiceDescriptionTermType[] sdts = brokerTemplate.getTerms().getAll().getServiceDescriptionTermArray();
             
                if ( sdts != null )
                {
                    for ( int i = 0; i < sdts.length; i++ )
                    {
                        if ( sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT" ) )
                        {
                        	manifestSDT = sdts[i];
                            break;
                        }
                    }
                }
             
                String name = manifestSDT.getName();
                String serviceName = manifestSDT.getServiceName();
             
                manifestSDT.set( serviceManifestDoc );
                manifestSDT.setName( name );
                manifestSDT.setServiceName( serviceName );
                
        	} catch(Exception e) {
        		fail( e.getMessage() );
        	}
            
            NegotiationOfferTypeImpl negOffer = new NegotiationOfferTypeImpl(brokerTemplate);
            
            //
            // creating negotiation offer context
            //
            NegotiationOfferContextType negOfferContext = NegotiationOfferContextType.Factory.newInstance();
            negOfferContext.setCreator( NegotiationRoleType.NEGOTIATION_INITIATOR );
            GregorianCalendar negExpireDate = new GregorianCalendar();
            expireDate.add( Calendar.MINUTE, 60 );
            negOfferContext.setExpirationTime( negExpireDate );
            NegotiationOfferStateType negOfferState = NegotiationOfferStateType.Factory.newInstance();
            negOfferState.addNewAdvisory();
            negOfferContext.setState( negOfferState );
            negOfferContext.setCounterOfferTo( offerID ); 
            
            negOffer.setNegotiationOfferContext( negOfferContext );
            
            System.out.println(" negotiating with negotiation instance." );
            
            NegotiationOfferType[] counterOffers = negotiation.negotiate(new NegotiationOfferType[] {negOffer.getXMLObject()});
            assertNotNull(counterOffers);
            
            System.out.println(" number of counter offers received: " + counterOffers.length );
            
            NegotiationOfferType counterOffer = counterOffers[0];
            assertNotNull(counterOffer);
            
            //System.out.println( "counter-offer: " + counterOffer.toString() );
            
            //
            // finally terminate the negotiation process
            //
            negotiation.terminate();
            System.out.println( "negotiation instance successfully terminated after negotiation round" );
                        
            //
            // check if negotiation offer is rejected or accepted 
            //
            if ( counterOffer.getNegotiationOfferContext().getState().isSetRejected() )
            {
                System.out.println("service manifest is rejected.");
            }
            else if (counterOffer.getNegotiationOfferContext().getState().isSetAcceptable() )
            {
                System.out.println("service manifest is accepted.");
               
                //
                // create an offer and then create the agreement if negotiation offer is accepted
                //
                String contextID = counterOffer.getName();
                
                AgreementOfferType negotiatedOffer = new AgreementOfferType(counterOffer);
                
                negotiatedOffer.setName(contextID);
                
                System.out.println("creating agreement for a negotiated offer with contextID: " + contextID);
                
                AgreementClient agreement = factory.createAgreement(negotiatedOffer);
                assertNotNull(agreement);
                
                //
                // terminate the agreement
                //
                System.out.println("terminating agreement.");
                agreement.terminate();
                
                System.out.println( "testSLACreation successfully completed" );
                
            }
            
        } 
        catch ( Exception e )
        {
        	e.printStackTrace();
        	fail( "test failed" );
        }
    } 

}
