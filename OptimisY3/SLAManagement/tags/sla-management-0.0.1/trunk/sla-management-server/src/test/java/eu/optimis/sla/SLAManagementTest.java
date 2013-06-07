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
package eu.optimis.sla;

import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;

import junit.framework.TestCase;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesType;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementFactory;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.types.AgreementOfferType;
import org.ogf.graap.wsag.server.api.WsagEngine;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.sla.rest.AdmissionControlClient;
import eu.optimis.sla.rest.CloudOptimizerClient;
import eu.optimis.sla.rest.MonitoringClient;
import eu.optimis.sla.rest.impl.ACModelApi;
import eu.optimis.sla.rest.impl.CloudOptimizerREST;
import eu.optimis.sla.rest.impl.MonitoringREST;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * Simple unit test of the SLA Management system. In this unit test the backend systems are emulated by mock
 * objects. the expected input and output files (XML) are located in the test resources directory.
 * 
 * @author owaeld
 */
public class SLAManagementTest extends TestCase
{
    /**
     * 
     */
    private static final int WAIT_TIME = 5000;
    
    private static final String TEMPLATE_NAME = "OPTIMIS-SERVICE-INSTANTIATION";

    private static final int NUM_OF_TEMPLATES = 1;
    
    private static final int NUM_OF_SDT_TERMS = 2;
    
    //
    // file containing the expected AC request and response
    //
    private static final String MOCK_AC_RESPONSE_0_XML = "/mock/ac/response_0.xml";

    private static final String MOCK_AC_REQUEST_0_XML = "/mock/ac/request_0.xml";

    /**
     * Admission control mock used by the SLA engine.
     */
    private ACModelApi acMock;

    /**
     * Cloud optimizer mock used by the SLA engine.
     */
    private CloudOptimizerREST coMock;

    /**
     * Monitoring manager mock used by the SLA engine.
     */
    private MonitoringREST monitoringMock;

    /**
     * Test of the OPTIMIS SLA Management component. The test case instantiates a new OPTIMIS service based on
     * the template provided by the SLA Management system. The default service is instantiated, i.e. no
     * changes are made in the template. Admissin Control and Cloud Optimizer are mocked in the backend.
     */
    public void testSLAManagement()
    {
        //
        // retrieve the agreement factory and OPTIMIS SLA template
        // and create new agreement instance
        //
        AgreementFactory factory = getFactory();
        
        AgreementTemplateType template = getTemplate( factory );
        
        Agreement agreement = createAgreement( factory, template );

        agreement.terminate( TerminateInputType.Factory.newInstance() );
    }

    private Agreement createAgreement( AgreementFactory factory, AgreementTemplateType template )
    {
        try
        {
            Agreement agreement = factory.createAgreement( new AgreementOfferType( template ) );
            assertNotNull( "Agreement instance must not be null.", agreement );
            return agreement;
        }
        catch ( AgreementFactoryException e )
        {
            fail( "failed to create agreement. Reason: " + e.getMessage() );

            //
            // never reach this point
            //
            return null;
        }
    }

    private AgreementTemplateType getTemplate( AgreementFactory factory )
    {
        AgreementTemplateType template = null;

        AgreementTemplateType[] templates = factory.getTemplates();
        assertEquals( "Exactly one template expected.", NUM_OF_TEMPLATES, templates.length );

        if ( templates[0].getName().equalsIgnoreCase( TEMPLATE_NAME ) )
        {
            template = templates[0];
        }
        assertNotNull( "template lookup failed", template );
        
        try {
            System.out.println("price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount());
        } catch (Exception e) {
            fail( e.getMessage() );
        }
        
        return template;
    }

    private AgreementFactory getFactory()
    {
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
        return factory;
    }

    public void testSLAMonitoring() throws Exception
    {

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

        Agreement agreement = null;

        try
        {
            agreement = factory.createAgreement( new AgreementOfferType( template ) );
            assertNotNull( "Agreement instance must not be null.", agreement );
        }
        catch ( AgreementFactoryException e )
        {
            fail( "failed to create agreement. Reason: " + e.getMessage() );
        }

        //
        // start monitoring of the agreement and assess the correct state evaluation
        // with respect to the SLA compliance rules and the mocked input values.
        //

        //
        // get the service deployment information
        //
        ServiceTermStateType[] stStates = agreement.getServiceTermStates();
        
        assertEquals( NUM_OF_SDT_TERMS, stStates.length );
        assertEquals( ServiceTermStateDefinition.NOT_READY, stStates[0].getState() );

        int maxTrys = 10;

        while ( stStates[0].getState() != ServiceTermStateDefinition.READY )
        {
            System.out.println( "Waiting to change state to ready..." );
            Thread.sleep( WAIT_TIME );
            stStates = agreement.getServiceTermStates();
            maxTrys--;
            if ( maxTrys == 0 )
            {
                fail( "State change of agreement failed. Monitoring not invoked." );
            }
        }

        System.out.println( "state changed to ready..." );

        ResourcesType resources =
            (ResourcesType) stStates[0].selectChildren( ResourcesDocument.type.getDocumentElementName() )[0];
        assertNotNull( resources );

        //
        // terminate the agreement
        //
        agreement.terminate( TerminateInputType.Factory.newInstance() );
    }

    @Override
    protected void setUp() throws Exception
    {
        //
        // load expected input/output documents for the test case
        //
        XmlBeanServiceManifestDocument acManifestIn = null;
        XmlBeanServiceManifestDocument acManifestOut = null;

        try
        {
            InputStream acInput0 = SLAManagementTest.class.getResourceAsStream( MOCK_AC_REQUEST_0_XML );
            InputStream acOutput0 = SLAManagementTest.class.getResourceAsStream( MOCK_AC_RESPONSE_0_XML );

            XmlObject parsedManifest = XmlObject.Factory.parse( acInput0 );
            acManifestIn = (XmlBeanServiceManifestDocument) parsedManifest;
            acManifestOut = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse( acOutput0 );
        }
        catch ( Exception e )
        {
            fail( "Failed to read test input files. Reason: " + e.getMessage() );
        }

        XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
        xmlOptions.setSaveAggressiveNamespaces();

        //
        // Prepare the mock objects. By invoking the method on the mock objects, the sequence of operations
        // for the backend/clients is recorded. This process can be repeated multiple times with multiple
        // inputs/responses.
        //
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        formParams.add( "serviceManifest", acManifestIn.xmlText( xmlOptions ) );

        acMock = EasyMock.createMock( ACModelApi.class );

        MultivaluedMap<String, String> outParams = new MultivaluedMapImpl();
        outParams.add( "serviceManifest", acManifestOut.xmlText( xmlOptions ) );

        EasyMock.expect( acMock.performACTest( EasyMock.notNull( MultivaluedMap.class ) ) ).andReturn(
            outParams );

        coMock = EasyMock.createMock( CloudOptimizerREST.class );

        EasyMock.expect( coMock.deploy( EasyMock.notNull( String.class ), EasyMock.notNull( String.class ) ) )
                .andReturn( "123" );
        // EasyMock.expect( coMock.deploy( EasyMock.eq( acManifestOut.xmlText() ) ) ).andReturn(
        // coManifestOut.xmlText() );

        //
        // Undeploy the service when an agreement is terminated
        //
        EasyMock.expect( coMock.undeploy( EasyMock.notNull( String.class ) ) ).andReturn( "123" );

        monitoringMock = EasyMock.createMock( MonitoringREST.class );

        //
        // we return 3 times monitoring result "monResultActive" -> representing service is active
        //
        MonitoringResourceDatasets monResultActive = new MonitoringResourceDatasets();
        EasyMock.expect(monitoringMock.getLatestReportForService( EasyMock.notNull( String.class ) )).andReturn( monResultActive );
        //IExpectationSetters<MonitoringResourceDatasets> expActive =
        //EasyMock.expect(monitoringMock.getLatestReportForService( EasyMock.notNull( String.class ) ));
        //expActive.times( 3 ).andReturn( monResultActive );

        //
        // we return 1 times monitoring result "monResultFinished" -> representing service is finished
        //
        MonitoringResourceDatasets monResultFinished = new MonitoringResourceDatasets();
        EasyMock.expect(monitoringMock.getLatestReportForService( EasyMock.notNull( String.class ) )).andReturn( monResultFinished );
        EasyMock.expectLastCall().atLeastOnce();

        //
        // After recording the test behavior we start the mock playback.
        //
        EasyMock.replay( acMock, coMock, monitoringMock );

        //
        // In order to invoke the mock clients instead of the web service clients we tell the SLA management
        // to use the mock clients as default client.
        //
        AdmissionControlClient.setDefaultAdmissionControlClient( acMock );
        CloudOptimizerClient.setDefaultCloudOptimizerClient( coMock );
        MonitoringClient.setDefaultMonitoringClient( monitoringMock );

        //
        // now we initialize/start the SLA engine
        //
        WsagEngine.initializeEngine( "http://optimis.eu/sla-management" );

    }

    @Override
    protected void tearDown() throws Exception
    {
        try
        {
            //
            // finally verify mock objects
            //
            try
            {
                EasyMock.verify( acMock );
            }
            catch ( AssertionError e )
            {
                e.printStackTrace();
                fail( "Admission Control Mock verification failed.\n" + e.getMessage() );
            }

            try
            {
                EasyMock.verify( coMock );
            }
            catch ( AssertionError e )
            {
                e.printStackTrace();
                fail( "Cloud Optimizer Mock verification failed." );
            }

            
//            try 
//            {
//                EasyMock.verify( monitoringMock ); 
//            } 
//            catch ( AssertionError e ) 
//            {
//                e.printStackTrace();
//                fail( "Monitoring Manager Mock verification failed." ); 
//            }
            
        }
        finally
        {
            WsagEngine.shutdownEngine();
        }
    }
}
