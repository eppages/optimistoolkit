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

import org.apache.xmlbeans.XmlException;

import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ovf.impl.DiskFormatType;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;

/**
 * @author angela.rumpl@scai.fraunhofer.de - 2012 03 15
 */
public class IPVMIncarnationTests extends AbstractTestApi
{
    public void testShouldInitializeIPExtensionSection()
    {
        // given a new ipManifest
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        // and no IncarnatedVirtualMachineComponents section exists
        assertNull( ipManifest.getInfrastructureProviderExtensions() );

        ipManifest.initializeInfrastructureProviderExtensions();
        // when we run the initialize method
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        assertNotNull( ipManifest.getInfrastructureProviderExtensions() );

        // then a virtual machine section exists
        assertNotNull( ipManifest.getInfrastructureProviderExtensions()
                                 .getIncarnatedVirtualMachineComponents() );
    }

    /**
     * This test checks that the right amount of incarnated vms are added to the IP extensions document. When
     * the vm components are incarnated, they must be created according to the number in the allocation
     * section.
     */
    public void testShouldInitializeTheRightAmountOfInstances()
    {
        //
        // given an spManifest with 2 components
        //

        // we have to add only one, the first was added at initialization
        VirtualMachineComponent component =
            getManifest().getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( "mysql" );

        //
        // and an upper bound of 5 instances for the mysql component
        //
        component.getAllocationConstraints().setUpperBound( 5 );

        //
        // when we create an ip manifest
        //
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );

        //
        // and initialize the incarnated virtual machine components
        //
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        // the component should be at 2nd position
        assertEquals( "mysql", ipManifest.getInfrastructureProviderExtensions()
                                         .getIncarnatedVirtualMachineComponents( 1 ).getComponentId() );

        //
        // then there must be two incarnated components
        //
        assertEquals( 2, ipManifest.getInfrastructureProviderExtensions()
                                   .getIncarnatedVirtualMachineComponents().length );

        //
        // and there must be 5 virtual systems in the virtual group section of the mysql component
        //
        assertEquals( 5, ipManifest.getInfrastructureProviderExtensions()
                                   .getIncarnatedVirtualMachineComponentByComponentId( "mysql" )
                                   .getOVFDefinition().getVirtualSystemArray().length );

        // check that the manifest has no errors:
        assertFalse( ipManifest.hasErrors() );
    }

    public void testShouldChangeFileHRefInInfExtensions() throws XmlException
    {
        // given an ip extension was initialized
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        // when we change a file href
        IncarnatedVirtualMachineComponent vmComponent =
            ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents()[0];
        vmComponent.getOVFDefinition().getReferences().getFileArray( 0 ).setHref( "/tmp/Demo-App.iso" );
        // then the infrastructure extension xml must be updated

        assertEquals( "/tmp/Demo-App.iso", vmComponent.getOVFDefinition().getReferences().getFileArray( 0 )
                                                      .getHref() );
    }

    /**
     * Here it is demonstrated that when calling the initializeIncarnatedVirtualMachineComponents() method,
     * also the file refs will be updated. Each vm instance needs its own contextualization disk.
     */
    public void testShouldShowThatContextFilesAreMultiplied()
    {
        // we set the href to the iso file, used for contextualization
        VirtualMachineComponent jbossComp =
            getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" );
        jbossComp.getOVFDefinition().getReferences().getContextualizationFile().setHref( "mypathtoiso.iso" );
        jbossComp.getOVFDefinition().getReferences().getImageFile().setHref( "mypathtovmdkimage.vmdk" );
        jbossComp.getOVFDefinition().getDiskSection().getContextualizationDisk()
                 .setFormat( DiskFormatType.VMDK.getSpecificationUrl() );
        jbossComp.getOVFDefinition().getDiskSection().getContextualizationDisk().setCapacity( "1939" );
        jbossComp.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setCPUSpeed( 15 );
        jbossComp.getOVFDefinition().getDiskSection().getImageDisk().setCapacity( "1234" );
        jbossComp.getAllocationConstraints().setUpperBound( 5 );

        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );

        //
        // When we initilize the incarnated vm components
        //
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        IncarnatedVirtualMachineComponent incarnatedComponent =
            ipManifest.getInfrastructureProviderExtensions()
                      .getIncarnatedVirtualMachineComponentByComponentId( "jboss" );

        //
        // then, the iso file ref exists now 5 times, where each has a href like "mypathtoiso_1.iso"
        // + 1 for the image file ref --> 6 filerefs
        //
        assertEquals( 6, incarnatedComponent.getOVFDefinition().getReferences().getFileArray().length );
        assertEquals( "mypathtoiso_1.iso", incarnatedComponent.getOVFDefinition().getReferences()
                                                              .getFileArray( 1 ).getHref() );
        assertEquals( "mypathtoiso_5.iso", incarnatedComponent.getOVFDefinition().getReferences()
                                                              .getFileArray( 5 ).getHref() );

        //
        // and the same is true for the disks disks
        //
        assertEquals( 6, incarnatedComponent.getOVFDefinition().getDiskSection().getDiskArray().length );

        // capacity of img disk
        assertEquals( "1234", incarnatedComponent.getOVFDefinition().getDiskSection().getDiskArray( 0 )
                                                 .getCapacity() );
        // the contextualization disks are all the same size
        assertEquals( "1939", incarnatedComponent.getOVFDefinition().getDiskSection().getDiskArray( 1 )
                                                 .getCapacity() );
        assertEquals( "1939", incarnatedComponent.getOVFDefinition().getDiskSection().getDiskArray( 2 )
                                                 .getCapacity() );
        assertEquals( "1939", incarnatedComponent.getOVFDefinition().getDiskSection().getDiskArray( 3 )
                                                 .getCapacity() );

        //
        // and there must be two harddisks in the vms hardware section, one for the image, one for the iso
        //
        assertEquals( "ovf:/disk/jboss-img-disk",
            incarnatedComponent.getOVFDefinition().getVirtualSystemArray()[0].getVirtualHardwareSection()
                                                                             .getItemArray( 4 )
                                                                             .getHostResourceArray( 0 ) );
        assertEquals( "ovf:/disk/jboss-context-disk_1",
            incarnatedComponent.getOVFDefinition().getVirtualSystemArray()[0].getVirtualHardwareSection()
                                                                             .getItemArray( 5 )
                                                                             .getHostResourceArray( 0 ) );

        assertEquals( "ovf:/disk/jboss-context-disk_2",
            incarnatedComponent.getOVFDefinition().getVirtualSystemArray()[1].getVirtualHardwareSection()
                                                                             .getItemArray( 5 )
                                                                             .getHostResourceArray( 0 ) );
        // check that cpu speed is set correctly
        assertEquals( 15,
            incarnatedComponent.getOVFDefinition().getVirtualSystemArray()[1].getVirtualHardwareSection()
                                                                             .getCPUSpeed() );
    }
}
