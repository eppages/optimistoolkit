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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * 
 * @author hrasheed
 */
public class ProviderNameTest extends TestCase
{
    
	public void testProvider() throws Exception
    {
    	
        Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        Velocity.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
        Velocity.init();
        
        final String SERVICE_MANIFEST_LEGAL_TEMPLATE = "/service_manifest_legal.vm";
        
    	Template t = Velocity.getTemplate( SERVICE_MANIFEST_LEGAL_TEMPLATE );
    	
    	Properties properties = new Properties();
    	
    	VelocityContext ctx = new VelocityContext();
        
    	for ( Object key : properties.keySet() )
        {
            ctx.put( key.toString(), properties.get( key ) );
        }
    	
        ctx.put( "provider", "velocity-sla-made" );
        
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanServiceManifestDocument legalManifest = null;
        
        legalManifest = XmlBeanServiceManifestDocument.Factory.parse( writer.toString() );
        
        System.out.println( "legal section: " + legalManifest.getServiceManifest().getDataProtectionSection().toString() );
        
    }
	
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
    	super.tearDown();
    }
}
