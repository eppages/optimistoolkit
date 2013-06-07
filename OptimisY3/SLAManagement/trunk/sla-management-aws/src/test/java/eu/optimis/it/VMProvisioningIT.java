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

import eu.optimis.interopt.sla.Tools;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

/**
 * @author hrasheed
 */
public class VMProvisioningIT extends AbstractSLAIT {
    
    /**
     * Simple test case for retrieving templates from SLA management service.VM
     */
    public void testSLATemplateRetrieval() throws Exception
    {
        AgreementTemplateType template = getAgreementFactory().getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
        assertNotNull(template);
    }

    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLACreationAndTermination() throws Exception
    {

        AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate(TEMPLATE_NAME, TEMPLATE_ID);
        assertNotNull(template);
        
        System.out.println(template.toString());
        
        // create an offer
        AgreementOffer offer = new AgreementOfferType(template);
        
        XmlBeanServiceManifestDocument serviceManifestDoc = null;
        try
        {
            serviceManifestDoc = Tools.offerToServiceManifest( offer.getXMLObject() );
            serviceManifestDoc.getServiceManifest().getServiceDescriptionSection().setServiceId("DemoApp");
        }
        catch (Exception e)
        {
            throw new AgreementFactoryException(
                "Error in retrieving service manifest from agreement offer.", e );
        }
        
        ServiceDescriptionTermType manifestSDT = null;
        ServiceDescriptionTermType[] sdts = offer.getTerms().getAll().getServiceDescriptionTermArray();
        
        if (sdts != null) {
            for (int i = 0; i < sdts.length; i++) {
                if (sdts[i].getName().equals( "OPTIMIS_SERVICE_SDT")) {
                    manifestSDT = sdts[i];
                    break;
                }
            }
        }
       
        String name = manifestSDT.getName();
        String serviceName = manifestSDT.getServiceName();
       
        manifestSDT.set(serviceManifestDoc);
        manifestSDT.setName(name);
        manifestSDT.setServiceName(serviceName); 
      
        // creating agreement
        AgreementClient agreement = factory.createAgreement(offer);
        assertNotNull(agreement);
        
        System.out.println("an agreement has been created.");

        // get the service deployment information
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        assertEquals(2, stStates.length);
        
        // get 'OPTIMIS_SERVICE_SDT' service term for arsys
        ServiceTermStateType arsysServiceTerm = agreement.getServiceTermState("OPTIMIS_SERVICE_SDT");
        assertEquals(ServiceTermStateDefinition.NOT_READY, arsysServiceTerm.getState());

        // loop until 'OPTIMIS_SERVICE_SDT' service term state changed to READY
        // that means, service is 'active' in arsys infrastructure
        while (arsysServiceTerm.getState() == ServiceTermStateDefinition.NOT_READY)
        {
            System.out.println( "Waiting to change service state to active..." );
            Thread.sleep(10000);
            arsysServiceTerm = agreement.getServiceTermState("OPTIMIS_SERVICE_SDT"); 
        }

        System.out.println("service state changed to active...");

        // terminate the agreement
        agreement.terminate();
        System.out.println("agreement is terminated");
    }
}
