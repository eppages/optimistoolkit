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

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.easymock.EasyMock;
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

import eu.optimis.sla.Tools;
import eu.optimis.sla.rest.AdmissionControlClient;
import eu.optimis.sla.rest.CloudOptimizerClient;
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
        
        try
        {
            
            factory.createAgreement( new AgreementOfferType( template ) );
        }
        catch ( AgreementFactoryException e )
        {
            assertEquals( "creatAgreement() action should throw AgreementFactoryException upon agreement creation", "service could not be deployed.", e.getMessage() );
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
            System.out.println("service-price: " + Tools.getServicePrice( template.getTerms().getAll() ).getAmount());
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

    @Override
    protected void setUp() throws Exception
    {
        WsagEngine.initializeEngine( "http://optimis.eu/sla-management" );
    }

    @Override
    protected void tearDown() throws Exception
    {
        WsagEngine.shutdownEngine();
    }
}
