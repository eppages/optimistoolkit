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
package eu.optimis.test;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.server.api.WsagEngine;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.VMManagementClientFactory;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * The test case checks basic functionality such as dynamic template creation, SLA creation, monitoring and
 * termination. The IP backend used for the tests is mocked.
 * 
 * @author hrasheed
 */
public class InteroperabilityProxyTest extends TestCase
{
    
    /**
     * Dynamically creates a new template based on the given configuration.
     */
    public void testAgreementLifecycle()
    {
        // get the factory and check for the OPTIMIS template
        AgreementFactory factory = null;
        
        try
        {
            AgreementFactory[] factories = WsagEngine.getAgreementFactoryHome().list();
            assertEquals("unexpected number of factories", 1, factories.length);
            factory = factories[0];
        }
        catch (Exception e)
        {
            fail("failed to list factories");
        }
        
        AgreementTemplateType template = null;
        try
        {
            AgreementTemplateType[] templates = factory.getTemplates();
            
            for (int i = 0; i < templates.length; i++) {
                if ("OPTIMIS-ARSYS-SERVICE-INSTANTIATION".equals(templates[i].getName())
                    && "1".equals( templates[i].getTemplateId())) {
                    template = templates[i];
                    break;
                }
            }
            assertNotNull("template not found", template);
        }
        catch (Exception e)
        {
            fail("failed to load template");
        }
        
        System.out.println(template.toString());
        
        // create a new agreement
        Agreement agreement = null;
        try
        {
            AgreementOffer offer = new AgreementOfferType(template);
            agreement = factory.createAgreement(offer);
        }
        catch (AgreementFactoryException e)
        {
            fail("agreement creation failed");
        }

        // monitor agreement and wait for completion
        checkAgreementMonitoring(agreement);

        // finally terminate the agreement
        agreement.terminate(TerminateInputType.Factory.newInstance());
    }
    
    /**
     * The method checks if the service monitoring state changes to ready, indicating that the service can now
     * be used.
     * 
     * @param agreement
     */
    private void checkAgreementMonitoring(Agreement agreement)
    {
        int maxRetries = 50;

        // TODO: We should check the service term state in order to determine that the service is up and
        // running (service term state READY). Once the state is READY, we can leave the loop. If the service
        // is not ready after 10 iterations, there is most likely an error in the monitoring handler.
        while (agreement.getState().getState() != AgreementStateDefinition.COMPLETE)
        {
            synchronized (this)
            {
                try
                {
                    wait(1000);
                }
                catch ( InterruptedException e )
                {
                    // not used here, the test case thread should not be interrupted while waiting
                }

                maxRetries--;
                if (maxRetries < 0)
                {
                    // Arsys services must be terminated, they are not limited by an end time
                    break;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {

        // create mock client and record client behavior
        // the mock object basically records the sequence of method invocations that are expected during test
        // execution.
        //
        VMManagementSystemClient client = EasyMock.createMock(VMManagementSystemClient.class);

        @SuppressWarnings( "unchecked" )
        List<ServiceComponent> deployInput = (List<ServiceComponent>) EasyMock.notNull();
        XmlBeanServiceManifestDocument dManifest = EasyMock.notNull(XmlBeanServiceManifestDocument.class);

        // we expect one deploy call and return a default service id
        client.deployService(EasyMock.eq("123"), deployInput, dManifest);
        EasyMock.expectLastCall();

        List<VMProperties> running = new Vector<VMProperties>();
        VMProperties pRunning = new VMProperties();
        pRunning.setStatus("running");
        pRunning.setId("1");
        pRunning.setHostname("127.0.0.1");
        running.add(pRunning);

        // next we expect two deploy calls and return the service provisioning details
        client.queryServiceProperties("123");
        EasyMock.expectLastCall().andReturn(running);

        client.queryServiceProperties("123");
        EasyMock.expectLastCall().andReturn(running);

        // After recording the mock behavior we switch the mock to playback mode and set it as VM management
        // default client
        EasyMock.replay(client);

        VMManagementClientFactory.setDefaultClient(client);

        // initialize WSAG4J engine
        WsagEngine.initializeEngine("http://127.0.0.1:8080/wsag4j-test");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        // shutdown WSAG4J engine
        WsagEngine.shutdownEngine();

        // check mock state
        EasyMock.verify(VMManagementClientFactory.getDefaultClient());
    }
}
