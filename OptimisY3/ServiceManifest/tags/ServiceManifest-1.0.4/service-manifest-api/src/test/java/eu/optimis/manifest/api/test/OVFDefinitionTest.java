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

import eu.optimis.manifest.api.ovf.impl.*;
import eu.optimis.manifest.api.ovf.sp.*;
import eu.optimis.manifest.api.utils.XmlValidator;

/**
 * @author arumpl
 */
public class OVFDefinitionTest extends AbstractTestApi {


    public void testVirtualSystem() {
        VirtualSystem virtualSystem = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss").getOVFDefinition().getVirtualSystem();

        assertEquals("jboss VM description", virtualSystem.getInfo());
        assertNull(virtualSystem.getName());
        virtualSystem.setName("My Virtual System");
        assertEquals("My Virtual System", virtualSystem.getName());
        assertEquals("system-jboss", virtualSystem.getId());
    }

    public void testOVFProductSection() {
        VirtualSystem virtualSystem = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss").getOVFDefinition().getVirtualSystem();
        virtualSystem.getProductSection().setProduct("JBOSS");
        virtualSystem.getProductSection().setVersion("5.1");
        assertEquals("JBOSS", virtualSystem.getProductSection().getProduct());
        assertEquals("5.1", virtualSystem.getProductSection().getVersion());
    }

    public void testOVFVirtualHardwareSection() {
        VirtualSystem virtualSystem = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss").getOVFDefinition().getVirtualSystem();

        VirtualHardwareSection hardwareSection = virtualSystem.getVirtualHardwareSection();

        assertEquals("Virtual Hardware Family", hardwareSection.getSystem().getElementName());
        assertEquals("0", hardwareSection.getSystem().getInstanceID());
        assertEquals("xen", hardwareSection.getSystem().getVirtualSystemType());
        //the only things we can change is the virtual hardware family
        hardwareSection.setVirtualHardwareFamily("vbox");
        hardwareSection.setMemorySize(1024);
        hardwareSection.setNumberOfVirtualCPUs(8);

        hardwareSection.toString();


        Item item0 = hardwareSection.getItemArray(0);
        assertEquals("Number of virtual CPUs", item0.getDescription());
        assertEquals("1 virtual CPU", item0.getElementName());
        assertEquals("1", item0.getInstanceID());
        assertEquals(3, item0.getResourceType());




        Item item1 = hardwareSection.getItemArray(1);
        item1.getResourceType();
        assertEquals("MegaBytes", item1.getAllocationUnits().trim());

        assertEquals("2", item1.getInstanceID());

        assertEquals(1024, item1.getVirtualQuantity().intValue());
        assertNull(item1.getParent());

        Item item2 = hardwareSection.getItemArray(2);
        assertEquals(1, item2.getConnectionArray().length);

        assertEquals(true, item2.getAutomaticAllocation().booleanValue());
        assertEquals("jboss-net", item2.getConnectionArray(0));

        Item item3 = hardwareSection.getItemArray(3);
        assertEquals(1, item3.getHostResourceArray().length);
        assertEquals("ovf:/disk/jboss-disk", item3.getHostResourceArray(0));
        assertEquals(null, item3.getHostResourceArray(1));



        assertTrue(XmlValidator.validate(manifest.toXmlBeanObject()));
    }

    public void testOVFReferences() {
        References references = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss").getOVFDefinition().getReferences();

        assertNotNull(references.getFile().getHref());
        assertEquals("jboss-img", references.getFile().getId());


        assertEquals(0, references.getFile().getChunkSize());
        assertEquals("", references.getFile().getCompression());


        assertTrue(XmlValidator.validate(manifest.toXmlBeanObject()));

    }

    public void testOVFDiskSection() {
        DiskSection diskSection = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()[0].getOVFDefinition().getDiskSection();
        diskSection.setInfo("NEW INFO!");
        assertNotNull(diskSection.getInfo());

        VirtualDiskDesc disk = diskSection.getDisk();
        assertEquals("1", disk.getCapacity());
        assertEquals("jboss-disk", disk.getDiskId());
        assertEquals("jboss-img", disk.getFileRef());
        assertEquals("http://www.gnome.org/~markmc/qcow-image-format.html", disk.getFormat());
        assertEquals("byte", disk.getCapacityAllocationUnits());
        assertNull(disk.getParentRef());
        assertEquals(0,disk.getPopulatedSize());

        assertTrue(XmlValidator.validate(manifest.toXmlBeanObject()));

    }

    public void testOVFNetworkSection() {
        NetworkSection networkSection = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss").getOVFDefinition().getNetworkSection();
        assertEquals(1,networkSection.getNetworkArray().length);
        networkSection.setInfo("Test");
        assertEquals("jboss-net",networkSection.getNetworkArray(0).getName());
        assertEquals("Network to connect to system-jboss", networkSection.getNetworkArray(0).getDescription().trim());



        assertTrue(XmlValidator.validate(manifest.toXmlBeanObject()));
    }


}
