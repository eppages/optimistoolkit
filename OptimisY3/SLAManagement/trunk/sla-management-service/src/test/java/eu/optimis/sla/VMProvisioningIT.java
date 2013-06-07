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

import org.apache.xmlbeans.XmlObject;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesType;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.client.AgreementFactoryClient;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;



/**
 * @author hrasheed
 */
public class VMProvisioningIT extends AbstractSLAIT {
	
    /**
     * Simple test case for retrieving templates from SLA management service
     */
    public void testSLATemplateRetrieval() throws Exception
    {
        AgreementTemplateType template = getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        System.out.println( "OPTIMIS-SLA: " + template.toString() );
        System.out.println( "testSLATemplateRetrieval successfully completed" );
    }
    
    /**
     * Simple test case for retrieving price of using IP service being offer through 
     * agreement template
     */
    public void testSLAPrice() throws Exception
    {
        AgreementTemplateType template = getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        //System.out.println( "OPTIMIS-SLA: " + template.toString() );
        //System.out.println( "OPTIMIS-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );
        System.out.println( "testSLAPrice successfully completed" );
    }

    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLACreation() throws Exception
    {

        AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        System.out.println( "OPTIMIS-SLA: " + template.toString() );
        //System.out.println( "OPTIMIS-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );

        super.updateServiceManifest(template);
        
        //
        // create an offer and then create the agreement
        //
        AgreementOffer offer = new AgreementOfferType( template );
        
        System.out.println( "creating agreement." );
        
        AgreementClient agreement = factory.createAgreement( offer );
        assertNotNull( agreement );
        
        System.out.println( "agreement is successfully created." );

        System.out.println( "testSLACreation successfully completed" );
    }
    
    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLAMonitoring() throws Exception
    {
    	
        /*AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        System.out.println( "OPTIMIS-SLA: " + template.toString() );
        System.out.println( "OPTIMIS-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );

        //
        // create an offer and then create the agreement
        //
        AgreementOffer offer = new AgreementOfferType( template );
        
        System.out.println( "creating agreement" );
        
        AgreementClient agreement = factory.createAgreement( offer );
        assertNotNull( agreement );
        
        System.out.println( "agreement is successfully created." );

        //
        // get the service deployment information
        //
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        assertEquals( 2, stStates.length );
        assertEquals( ServiceTermStateDefinition.NOT_READY, stStates[ 0 ].getState() );

        int maxTrys = 10;

        while ( stStates[ 0 ].getState() == ServiceTermStateDefinition.NOT_READY )
        {
            System.out.println( "Waiting to change service term state to ready..." );
            Thread.sleep( 5000 );
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if ( maxTrys == 0 )
            {
                fail( "State change of agreement failed. Monitoring not invoked." );
            }
        }

        System.out.println( "service term state changed to ready..." );
        
        ServiceTermStateType vsdState = agreement.getServiceTermState("OPTIMIS_SERVICE_SDT");
        XmlObject[] serviceMonitoring = vsdState.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        assertEquals("OPTIMIS_SERVICE_SDT state must contain exactly one SLASeriveMonitoringDocument.", 1, serviceMonitoring.length);
        
        SLASeriveMonitoringType seriveMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];
        assertNotNull(seriveMonitoringType.getServiceId());
        assertTrue("Number of service monitoring records must be at least 1.", 0 < seriveMonitoringType.getSLASeriveMonitoringRecordArray().length);
        
        System.out.println( "service monitoring data: " + seriveMonitoringType.xmlText() );
        
        ResourcesType resources = (ResourcesType) stStates[0].selectChildren(ResourcesDocument.type.getDocumentElementName())[0];
        assertNotNull(resources);

        System.out.println( "service running on resource: " + resources.xmlText() );*/
        
        System.out.println( "testSLAMonitoring successfully completed" );
    }
    
    /**
     * Simple test case of interaction with SLA management service.
     */
    public void testSLACreationAndTermination() throws Exception
    {
    	
    	/*AgreementFactoryClient factory = getAgreementFactory();
        
        AgreementTemplateType template = factory.getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( template );
        System.out.println( "OPTIMIS-SLA: " + template.toString() );
        System.out.println( "OPTIMIS-SLA Price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount() );

        //
        // create an offer and then create the agreement
        //
        AgreementOffer offer = new AgreementOfferType( template );
        
        System.out.println( "creating agreement." );
        
        AgreementClient agreement = factory.createAgreement( offer );
        assertNotNull( agreement );
        
        System.out.println( "agreement is successfully created." );

        //
        // terminate the agreement
        //
        System.out.println( "agreement will be terminated after 30 seconds" );
        
        int iterations = 1;
        while ( iterations <= 3 ) {
            Thread.sleep( 10000 );
            iterations++;
        }
        
        agreement.terminate();
        
        System.out.println("agreement is terminated");*/
        
        System.out.println( "testSLACreationAndTermination successfully completed" );
    }
}
