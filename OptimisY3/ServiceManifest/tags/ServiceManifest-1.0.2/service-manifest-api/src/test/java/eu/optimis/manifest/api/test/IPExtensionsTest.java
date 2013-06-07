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

package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.utils.XmlValidator;
import org.apache.xmlbeans.XmlException;


public class IPExtensionsTest extends AbstractTestApi {

    public IPExtensionsTest(String testName) {
        super(testName);
    }

    public void testShouldExportIPManifestAsString() throws XmlException {
        // given a new IP Manifest exists

        Manifest im = Manifest.Factory.newInstance(manifest.toXmlBeanObject());

        //when we export the manifest as string
        String exportedManifest = im.toString();

        //then both manifests are equal
        assertTrue("exported string does not match orginal", exportedManifest.equals(manifest.toString()));
    }

    public void testShouldInitializeIPExtensionSection() {
        // given a new ipManifest
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        // and no IncarnatedVirtualMachineComponents section exists
        assertNull(ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents());

        //when we run the initialize method
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        //then a virtual machine section exists
        assertNotNull(ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents());
    }

    public void testShouldInitializeTheRightAmountOfInstances() {
        //given an spManifest with 2 components
        // we have to add only one, the first was added at initialization
        VirtualMachineComponent component = manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("mysql");


        //and an upper bound of 5 instances for the mysql component
        component.getAllocationConstraints().setUpperBound(5);

        //when we create an ip manifest
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        //and initialize the ip extensions
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        //the component should be at 2nd position
        assertEquals("mysql",ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents(1).getComponentId());

        //then there must be two incarnated components
        assertEquals(2, ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents().length);

        //and there must be 5 virtual systems in the virtual group section of the mysql component
        assertEquals(5, ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponentByComponentId("mysql").getOVFDefinition().getVirtualSystemCollection().length);

        //check that the manifest is still valid:
        assertTrue(XmlValidator.validate(ipManifest.toXmlBeanObject()));

    }

    public void testShouldChangeFileHRefInInfExtensions() throws XmlException {
        //given an ip extension was initialized
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        //when we change a file href
        ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents()[0].getOVFDefinition().getReferences().getFileArray(0).setHref("/tmp/Demo-App.iso");
        //then the infrastructure extension xml must be updated

        assertEquals("/tmp/Demo-App.iso",ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents()[0].getOVFDefinition().getReferences().getFileArray(0).getHref());
    }
}
