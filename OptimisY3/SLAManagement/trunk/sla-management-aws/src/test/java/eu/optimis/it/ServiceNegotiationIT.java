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
package eu.optimis.it;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.client.NegotiationClient;
import org.ogf.graap.wsag.api.types.NegotiationOfferTypeImpl;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextDocument;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferStateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationRoleType;

/**
 * @author hrasheed
 */
public class ServiceNegotiationIT extends AbstractSLAIT {

    /**
     * Simple test case for SLA negotiation.
     */
    public void testSLANegotiation() throws Exception {
        
        AgreementFactoryClient factory = getAgreementFactory();
        
        // Now creates a negotiation context that defines the roles and obligations
        // of the negotiating parties and specifies the type of the negotiation process.
        NegotiationContextDocument contextDoc = NegotiationContextDocument.Factory.newInstance();
        NegotiationContextType context = contextDoc.addNewNegotiationContext();
        context.setAgreementFactoryEPR(factory.getEndpoint());
        context.setAgreementResponder(NegotiationRoleType.NEGOTIATION_RESPONDER);
        GregorianCalendar expireDate = new GregorianCalendar();
        expireDate.add(Calendar.HOUR, 12);
        context.setExpirationTime(expireDate); 
        
        // set the nature of the negotiation process (e.g. negotiation or re-negotiation).
        org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationType negotiationType = context.addNewNegotiationType();
        negotiationType.addNewNegotiation();
        
        // creating negotiation instance based on a negotiation context from a selected agreement factory
        NegotiationClient negotiation = factory.initiateNegotiation(context);
        
        assertNotNull("falied to instantiate negotiation client.", negotiation);
        
        System.out.println("negotiation instance is created successfully");
        
        // retrieve the agreement templates for which negotiation is supported,
        // and select the one with template name "OPTIMIS-SERVICE-INSTANTIATION".
        AgreementTemplateType[] negotiableTemplates = negotiation.getNegotiableTemplates();
        assertNotNull(negotiableTemplates);

        AgreementTemplateType template = null;
        
        for (int i = 0; i < negotiableTemplates.length; i++)
        {
            AgreementTemplateType agreementTemplate = negotiableTemplates[i];
            if ( agreementTemplate.getName().equals(TEMPLATE_NAME))
            {
                template = agreementTemplate;
            }
        }
        assertEquals(TEMPLATE_NAME, template.getName());
        
        String offerID = template.getContext().getTemplateId() + "-" + template.getName();
        
        NegotiationOfferTypeImpl negOffer = new NegotiationOfferTypeImpl(template);
        
        // creating negotiation offer context
        NegotiationOfferContextType negOfferContext = NegotiationOfferContextType.Factory.newInstance();
        negOfferContext.setCreator(NegotiationRoleType.NEGOTIATION_INITIATOR);
        GregorianCalendar negExpireDate = new GregorianCalendar();
        expireDate.add(Calendar.MINUTE, 15);
        negOfferContext.setExpirationTime(negExpireDate);
        NegotiationOfferStateType negOfferState = NegotiationOfferStateType.Factory.newInstance();
        negOfferState.addNewAdvisory();
        negOfferContext.setState(negOfferState);
        negOfferContext.setCounterOfferTo(offerID); 
        
        negOffer.setNegotiationOfferContext(negOfferContext);
        
        NegotiationOfferType[] counterOffers = negotiation.negotiate(new NegotiationOfferType[] {negOffer.getXMLObject()});
        assertNotNull(counterOffers);
        
        NegotiationOfferType counterOffer = counterOffers[0];
        assertNotNull(counterOffer);
        
        System.out.println(counterOffer.toString());
        
        // check if negotiation offer is rejected or accepted 
        if (counterOffer.getNegotiationOfferContext().getState().isSetRejected())
        {
            System.out.println("service manifest is rejected.");
        }
        else if(counterOffer.getNegotiationOfferContext().getState().isSetAdvisory())
        {
            System.out.println("service manifest is partially accepted.");
        }
        else if (counterOffer.getNegotiationOfferContext().getState().isSetAcceptable())
        {
            System.out.println("service manifest is accepted.");
        }
        
        // finally terminate the negotiation process
        negotiation.terminate();
        
        System.out.println( "negotiation test successfully completed" );
    }
}
