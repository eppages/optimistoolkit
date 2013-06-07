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
package eu.optimis.manifest.api.test;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import eu.optimis.manifest.api.exceptions.InvalidDocumentException;
import org.apache.xmlbeans.XmlException;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * Simple test case checking the Manifest import/export capabilities.
 *
 * @author owaeld
 */
public class ExportImportTest extends AbstractTestApi
{

    public void testShouldExportServiceManifestAsXmlBeansObject()
    {

        XmlBeanServiceManifestDocument exportedXmlbeansObject = getManifest().toXmlBeanObject();
        assertNotNull( "manifest id not exported.", exportedXmlbeansObject.getServiceManifest()
                .getManifestId() );
    }

    public void testShouldExportServiceManifestAsString()
    {
        String serviceProviderId = getManifest().getServiceProviderId();
        String manifestAsString = getManifest().toString();
        try
        {
            XmlBeanServiceManifestDocument parsedManifest =
                    XmlBeanServiceManifestDocument.Factory.parse( manifestAsString );
            assertEquals( "converted field mismatch.", serviceProviderId,
                    parsedManifest.getServiceManifest().getServiceProviderId() );
        }
        catch ( XmlException e )
        {
            fail( "export creates invalid manifest" );
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void testShouldNotExportInvalidManifest()
    {
        getManifest().setManifestId( "test" );
        try
        {
            getManifest().toJaxB();
            // if no exception was thrown an invalid manifest was exported
            fail( "An invalid manifest was exported." );
        }
        catch ( RuntimeException e )
        {
            assertTrue( "This exception was expected!", true );
            System.out.println( e.getMessage() );
        }
    }

    public void testShouldNotExportInvalidManifestToString()
    {
        getManifest().setManifestId( "test" );
        try
        {
            getManifest().toString();
            fail( "An invalid manifest was exported." );
        }
        catch ( RuntimeException e )
        {
            assertTrue( "This exception was expected!", true );
        }
    }

    public void testShouldNotExportInvalidManifestToXmlBeans()
    {
        getManifest().setManifestId( "test" );
        try
        {
            getManifest().toXmlBeanObject();
            fail( "An invalid manifest was exported." );
        }
        catch ( RuntimeException e )
        {
            assertTrue( "This exception was expected!", true );
        }
    }

    public void testShouldExportServiceManifestAsJaxBObject() throws JAXBException
    {
        System.out.println( "JaxbExport of manifest invoked." );
        String providerId = getManifest().getServiceProviderId();
        // export to jaxb
        JaxBServiceManifest manifestAsJaxbObject = getManifest().toJaxB();
        // check if exported field "manifestID" matches with original
        assertEquals( "converted field mismatch.", providerId,
                manifestAsJaxbObject.getServiceProviderId() );
    }

    public void testShouldImportFromJaxBObject()
    {
        JaxBServiceManifest jaxb = getManifest().toJaxB();
        Manifest ipManifest = Manifest.Factory.newInstance( jaxb );
        assertNotNull( ipManifest );
    }

    public void testShouldNotImportInvalidJaxBManifest()
    {
        JaxBServiceManifest jaxb = getManifest().toJaxB();
        jaxb.setManifestId( "test" );
        try
        {
            Manifest.Factory.newInstance( jaxb );
            fail( "An exception should have been thrown" );
        }
        catch ( InvalidDocumentException e )
        {
            assertTrue( "this exception was expected", true );
            System.out.println( e.getMessage() );
        }
    }

    public void testShouldNotImportInvalidXmlBeansManifest()
    {
        XmlBeanServiceManifestDocument manifestDocument = getManifest().toXmlBeanObject();
        manifestDocument.getServiceManifest().setManifestId( "test" );
        try
        {
            Manifest.Factory.newInstance( manifestDocument );
            fail( "An exception should have been thrown" );
        }
        catch ( InvalidDocumentException e )
        {
            assertTrue( "this exception was expected", true );
            System.out.println( e.getMessage() );
        }
    }

    public void testShouldNotImportInvalidString()
    {
        String string = getManifest().toString();
        string = string.replaceFirst( "jboss", "test" );
        try
        {
            Manifest.Factory.newInstance( string );
            fail( "An exception should have been thrown" );
        }
        catch ( InvalidDocumentException e )
        {
            System.out.println( "This exception was expected: " + e.getMessage() );
            assertTrue( "this exception was expected", true );
        }
    }

    public void testShouldImportSPManifestFromJaxB()
    {
        JaxBServiceManifest jaxBManifest = getManifest().toJaxB();
        eu.optimis.manifest.api.sp.Manifest spManifest =
                eu.optimis.manifest.api.sp.Manifest.Factory.newInstance( jaxBManifest );
        assertNotNull( spManifest );
    }

    public void testShouldImportSPManifestFromString()
    {
        String stringManifest = getManifest().toString();
        eu.optimis.manifest.api.sp.Manifest spManifest =
                eu.optimis.manifest.api.sp.Manifest.Factory.newInstance( stringManifest );
        assertNotNull( spManifest );
    }

    public void testImportIPManifestFromString()
    {
        String m = getManifest().toString();
        try
        {

            eu.optimis.manifest.api.ip.Manifest.Factory.newInstance( m );
        }
        catch ( RuntimeException ex )
        {
            fail( "Invalid IP manifest imported." );
        }
    }

    public void testShouldNotImportInvalidIPManifestFromString()
    {
        String wrongManifest = "something wrong";
        try
        {
            eu.optimis.manifest.api.ip.Manifest.Factory.newInstance( wrongManifest );
            fail( "Invalid IP manifest imported. This shouldn't occur." );
        }
        catch ( RuntimeException ex )
        {
            System.out.println(
                    "expected exception catched. invalid IP manifest detected." + ex.getCause() );
        }
    }

    public void testShouldExportIPManifestAsString() throws XmlException
    {
        // given a new IP Manifest exists

        Manifest im = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );

        // when we export the manifest as string
        String exportedManifest = im.toString();

        // then both manifests are equal
        assertTrue( "exported string does not match orginal",
                exportedManifest.equals( getManifest().toString() ) );
    }

    public void testShouldLoadManifestFromFile() throws XmlException, IOException
    {
        InputStream in = this.getClass().getResourceAsStream( "/SP-ManifestExample.xml" );
        XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.parse( in );
        Manifest manifest = Manifest.Factory.newInstance( doc );
        assertFalse( manifest.hasErrors() );
    }
}
