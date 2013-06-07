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
package eu.optimis.it;

import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.exceptions.ResourceUnavailableException;
import org.ogf.graap.wsag.api.exceptions.ResourceUnknownException;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;

/**
 * Integration test case of the Arsys client implementation used by the SLA management system. This
 * integration test performs service allocation and management actions with the Arsys backend.
 *
 * @author owaeld
 */
public class ArsysClientIT extends AbstractSLAIT
{

    /**
     * tests only the retrieval of the template by its name.
     *
     * @throws Exception
     */
    public void testSLATemplateRetrieval() throws Exception
    {
        AgreementTemplateType template =
                getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( "template is null", template );
        System.out.println( template.xmlText() );
    }

    /**
     * Tests the implementation of the Arsys client in order to allocate a new service, get monitoring
     * information for the service and finally terminate the service in the Arsys backend.
     */
    public void testDefaultAllocationWorkflowArsys() throws Exception
    {
        //retrieve the the template
        AgreementTemplateType template = getAgreementFactory().getTemplate( TEMPLATE_NAME, TEMPLATE_ID );
        assertNotNull( "No template retrieved from agreement factory", template );

        //
        // create an offer and then create the agreement
        //

        AgreementClient agreement = createAgreement( template );
        assertNotNull( "Agreement was not created", agreement );
        System.out.println( "agreement is created." );

        //TODO: monitor agreement, wait until state is ready

        try
        {
            System.out.println( "Number of service term states: " + agreement.getServiceTermStates().length );
        }
        catch ( ResourceUnknownException e )
        {
            e.printStackTrace();  //TODO: implement method body
        }
        catch ( ResourceUnavailableException e )
        {
            e.printStackTrace();  //TODO: implement method body
        }
        
        try
        {
          agreement.terminate();
        }
        catch ( Exception e )
        {
          e.printStackTrace();
          fail( "Agreement termination failed." + e.getMessage() );
        }

        System.out.println( "agreement is terminated" );
        System.out.println( "test successfully completed" );
    }

    /**
     * creates an offer and then an agreement without changing anything in the template.
     *
     * @param template
     * @return the new agreement.
     */
    private AgreementClient createAgreement( AgreementTemplateType template )
    {
        AgreementOffer offer = new AgreementOfferType( template );
        AgreementClient agreement = null;
        try
        {
            agreement = getAgreementFactory().createAgreement( offer );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail( "Unexpected exception thrown: " + e.getMessage() );
        }
        return agreement;
    }

}
