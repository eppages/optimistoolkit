/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.Negotiation;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.api.types.NegotiationOfferTypeImpl;
import org.ogf.graap.wsag.server.api.WsagEngine;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;

import eu.optimis.broker.deploymentObjectiveTypes.DeploymentObjectiveDocument;
import eu.optimis.broker.deploymentObjectiveTypes.DeploymentObjectiveType;
import eu.optimis.sla.types.service.price.SLAServicePriceDocument;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;


/**
 * Simple unit test of the Broker SLA Management system. 
 * 
 * @author hrasheed
 */
public class BrokerSLAManagementTest extends TestCase
{
    /**
     * 
     */
    private static final String TEMPLATE_NAME = "OPTIMIS-BROKER-SERVICE-INSTANTIATION";

    private static final int NUM_OF_TEMPLATES = 1;
    
    @Override
    protected void setUp() throws Exception
    {
        //
        // now we initialize/start the SLA engine
        //
        WsagEngine.initializeEngine( "http://optimis.eu/sla-management" );
    }

    @Override
    protected void tearDown() throws Exception
    {
    	//
        // now we shutdown the SLA engine
        //
        WsagEngine.shutdownEngine();
    }
    
    /**
     * Test of the OPTIMIS SLA Management component. The test case instantiates a new OPTIMIS service based on
     * the template provided by the SLA Management system. The default service is instantiated, i.e. no
     * changes are made in the template. 
     */
    public void testSLAManagement()
    {
        //
        // retrieve the agreement factory and OPTIMIS SLA template
        // and create new agreement instance
        //
    	
    	AgreementFactory factory = null;
        try
        {
            AgreementFactory[] factories = WsagEngine.getAgreementFactoryHome().list();
            assertEquals( "Exactly one factory expected.", 1, factories.length );
            factory = factories[0];
        }
        catch ( Exception e )
        {
            fail( "WSAG4J engine factory listing failed" );
        }

        AgreementTemplateType template = null;

        AgreementTemplateType[] templates = factory.getTemplates();
        assertEquals( "Exactly one template expected.", NUM_OF_TEMPLATES, templates.length );

        if ( templates[0].getName().equalsIgnoreCase( TEMPLATE_NAME ) )
        {
            template = templates[0];
        }
        assertNotNull( "template lookup failed", template );
        
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
            Map<String, Object> properties = new HashMap<String, Object>();
            Negotiation negotiation = factory.initiateNegotiation(context, null, null, properties);
            
            assertNotNull("falied to instantiate negotiation client.", negotiation);
            
            System.out.println( "negotiation instance is created successfully" );
            
            //
            // retrieve the agreement templates for which negotiation is supported,
            // and select the one with template name "OPTIMIS-SERVICE-INSTANTIATION".
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
            
            NegotiationOfferType[] counterOffers = negotiation.negotiate(new NegotiationOfferType[] {negOffer.getXMLObject()}, null);
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
                
                Agreement agreement = factory.createAgreement(negotiatedOffer);
                assertNotNull(agreement);
                
                //
                // terminate the agreement
                //
                System.out.println("terminating agreement.");
                agreement.terminate(TerminateInputType.Factory.newInstance());
                
                System.out.println( "testSLACreation successfully completed" );
                
            }
        } 
        catch ( Exception e )
        {
        	e.printStackTrace();
        	fail( "negotiation failed" );
        }  
    }
}