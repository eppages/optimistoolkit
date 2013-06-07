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
 */

/**
 * Created by IntelliJ IDEA.
 * Email: karl.catewicz@scai.fraunhofer.de
 * Date: 05.01.2012
 * Time: 14:55:27
 */
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.ServiceManifestDocument;
import org.apache.xmlbeans.XmlException;

import javax.xml.bind.JAXBException;


public class ExportImportTest extends AbstractTestApi {
    public ExportImportTest(String testName) {
        super(testName);
    }

    public void testShouldExportServiceManifestAsXmlBeansObject() {

        ServiceManifestDocument exportedXmlbeansObject = manifest.toXmlBeanObject();
        assertNotNull("manifest id not exported.", exportedXmlbeansObject.getServiceManifest().getManifestId());

    }

    public void testShouldExportServiceManifestAsString() {
        String serviceProviderId = manifest.getServiceProviderId();
        String manifestAsString = manifest.toString();
        try {
            ServiceManifestDocument parsedManifest = ServiceManifestDocument.Factory.parse(manifestAsString);
            assertEquals("converted field mismatch.", serviceProviderId, parsedManifest.getServiceManifest().getServiceProviderId());
        } catch (XmlException e) {
            fail("export creates invalid manifest");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void testShouldNotExportInvalidManifest() {
        manifest.setManifestId("test");
        try {
            manifest.toJaxB();
            //if no exception was thrown an invalid manifest was exported
            fail("An invalid manifest was exported.");
        } catch (RuntimeException e) {
            assertTrue("This exception was expected!", true);
        }


    }

    public void testShouldNotExportInvalidManifestToString() {
        manifest.setManifestId("test");
        try {
            manifest.toString();
            fail("An invalid manifest was exported.");
        } catch (RuntimeException e) {
            assertTrue("This exception was expected!", true);
        }
    }

    public void testShouldNotExportInvalidManifestToXmlBeans() {
        manifest.setManifestId("test");
        try {
            manifest.toXmlBeanObject();
            fail("An invalid manifest was exported.");
        } catch (RuntimeException e) {
            assertTrue("This exception was expected!", true);
        }
    }

    public void testShouldExportServiceManifestAsJaxBObject() throws JAXBException {
        System.out.println("JaxbExport of manifest invoked.");
        String providerId = manifest.getServiceProviderId();
        manifest.getElasticitySection().getRuleArray(0).setKPIName("Test");

        // export to jaxb
        JaxBServiceManifest manifestAsJaxbObject = manifest.toJaxB();
        // check if exported field "manifestID" matches with original
        assertEquals("converted field mismatch.", providerId, manifestAsJaxbObject.getServiceProviderId());
    }

    public void testShouldImportFromJaxBObject() {
        JaxBServiceManifest jaxb = manifest.toJaxB();
        Manifest ipManifest = Manifest.Factory.newInstance(jaxb);
        assertNotNull(ipManifest);
    }

    public void testShouldNotImportInvalidJaxBManifest() {
        JaxBServiceManifest jaxb = manifest.toJaxB();
        jaxb.setManifestId("test");
        try {
            Manifest.Factory.newInstance(jaxb);
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertTrue("this exception was expected", true);
        }
    }

    public void testShouldNotImportInvalidXmlBeansManifest() {
        ServiceManifestDocument manifestDocument = manifest.toXmlBeanObject();
        manifestDocument.getServiceManifest().setManifestId("test");
        try {
            Manifest.Factory.newInstance(manifestDocument);
            fail("An exception should have been thrown");
        } catch (Exception e) {
            assertTrue("this exception was expected", true);
        }

    }

    public void testShouldNotImportInvalidString() {
        String string = manifest.toString();
        string = string.toUpperCase();
        try {
            Manifest.Factory.newInstance(string);
            fail("An exception should have been thrown");
        } catch (Exception e) {
            System.out.println("This exception was expected: " + e.getMessage());
            assertTrue("this exception was expected", true);
        }
    }
}
