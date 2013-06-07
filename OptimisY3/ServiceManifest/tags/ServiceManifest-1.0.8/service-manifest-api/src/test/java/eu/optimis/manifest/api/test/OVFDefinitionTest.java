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

import eu.optimis.manifest.api.ovf.impl.DiskFormatType;
import eu.optimis.manifest.api.ovf.impl.OperatingSystemType;
import eu.optimis.manifest.api.ovf.sp.*;
import org.apache.xmlbeans.XmlError;

import java.lang.System;

/**
 * @author arumpl
 */
public class OVFDefinitionTest extends AbstractTestApi
{

    /**
     *
     */
    private static final int WRONG_DISK_SIZE = -1337;

    private static final int DEFAULT_DISK_SIZE = 1337;

    private static final int OS_LINUX = 36;

    private static final int DEFAULT_OS = 10043;

    private static final int CPU_SPEED_DEFAULT = 500;

    public void testVirtualSystem()
    {
        VirtualSystem virtualSystem =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem();

        assertEquals( "jboss VM description", virtualSystem.getInfo() );
        assertNull( virtualSystem.getName() );
        virtualSystem.setName( "My Virtual System" );
        assertEquals( "My Virtual System", virtualSystem.getName() );
        assertEquals( "system-jboss", virtualSystem.getId() );
    }

    public void testOVFProductSection()
    {
        VirtualSystem virtualSystem =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem();
        virtualSystem.getProductSection().setProduct( "JBOSS" );
        virtualSystem.getProductSection().setVersion( "5.1" );
        assertEquals( "JBOSS", virtualSystem.getProductSection().getProduct() );
        assertEquals( "5.1", virtualSystem.getProductSection().getVersion() );
    }

    public void testOVFVirtualHardwareSection()
    {
        VirtualSystem virtualSystem =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem();

        VirtualHardwareSection hardwareSection = virtualSystem.getVirtualHardwareSection();

        assertEquals( "Virtual Hardware Family", hardwareSection.getSystem().getElementName() );
        assertEquals( "0", hardwareSection.getSystem().getInstanceID() );
        assertEquals( "xen", hardwareSection.getSystem().getVirtualSystemType() );
        // the only things we can change is the virtual hardware family
        hardwareSection.setVirtualHardwareFamily( "vbox" );
        hardwareSection.setMemorySize( 1024 );
        hardwareSection.setNumberOfVirtualCPUs( 8 );

        hardwareSection.toString();

        Item item0 = hardwareSection.getItemArray( 0 );
        assertEquals( "Number of virtual CPUs", item0.getDescription() );
        assertEquals( "1 virtual CPU", item0.getElementName() );
        assertEquals( "1", item0.getInstanceID() );
        assertEquals( 3, item0.getResourceType() );

        Item item1 = hardwareSection.getItemArray( 1 );
        item1.getResourceType();
        assertEquals( "MegaBytes", item1.getAllocationUnits().trim() );

        assertEquals( "2", item1.getInstanceID() );

        assertEquals( 1024, item1.getVirtualQuantity().intValue() );
        assertNull( item1.getParent() );

        Item item2 = hardwareSection.getItemArray( 3 );
        assertEquals( 1, item2.getConnectionArray().length );

        assertEquals( true, item2.getAutomaticAllocation().booleanValue() );
        assertEquals( "jboss-net", item2.getConnectionArray( 0 ) );

        Item item3 = hardwareSection.getItemArray( 4 );
        assertEquals( 1, item3.getHostResourceArray().length );
        assertEquals( "ovf:/disk/jboss-img-disk", item3.getHostResourceArray( 0 ) );
        assertEquals( null, item3.getHostResourceArray( 1 ) );

        for ( XmlError error : getManifest().getErrors() )
        {
            System.out.println( error );
        }
        assertFalse( getManifest().hasErrors() );
    }

    public void testOVFWrongMemorySize()
    {
        VirtualHardwareSection hardwareSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        int origMem = hardwareSection.getMemorySize();
        int wrongMem = WRONG_DISK_SIZE;
        try
        {
            hardwareSection.setMemorySize( wrongMem );
            fail( "IllegalArgumentException expected." );
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( e.getMessage() );
        }
        assertEquals( "memory does not match template value.", origMem, hardwareSection.getMemorySize() );
    }

    public void testOVFReferences()
    {
        References references =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getReferences();

        assertNotNull( references.getImageFile().getHref() );
        assertEquals( "jboss-img-file", references.getImageFile().getId() );

        assertEquals( 0, references.getImageFile().getChunkSize() );
        assertEquals( "", references.getImageFile().getCompression() );

        assertFalse( getManifest().hasErrors() );

    }

    public void testOVFImageDiskSection()
    {
        DiskSection diskSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()[ 0 ]
                        .getOVFDefinition()
                        .getDiskSection();
        diskSection.setInfo( "NEW INFO!" );
        assertNotNull( diskSection.getInfo() );

        VirtualDiskDesc disk = diskSection.getImageDisk();
        assertEquals( "1", disk.getCapacity() );
        assertEquals( "jboss-img-disk", disk.getDiskId() );
        assertEquals( "jboss-img-file", disk.getFileRef() );
        assertEquals( "http://www.gnome.org/~markmc/qcow-image-format.html", disk.getFormat() );
        assertEquals( "byte", disk.getCapacityAllocationUnits() );
        assertNull( disk.getParentRef() );
        assertEquals( 0, disk.getPopulatedSize() );

        assertTrue( !getManifest().hasErrors() );
    }

    public void testOVFNetworkSection()
    {
        NetworkSection networkSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getNetworkSection();
        assertEquals( 1, networkSection.getNetworkArray().length );
        networkSection.setInfo( "Test" );
        assertEquals( "jboss-net", networkSection.getNetworkArray( 0 ).getName() );
        assertEquals( "Network to connect to system-jboss", networkSection.getNetworkArray( 0 )
                .getDescription().trim() );

        assertTrue( !getManifest().hasErrors() );
    }

    public void testOVFCPUSectionReadCPUSpeed()
    {
        VirtualHardwareSection hardwareSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        int reservations = hardwareSection.getCPUSpeed();
        assertEquals( "cpu speed does not match default template value.", CPU_SPEED_DEFAULT, reservations );
    }

    public void testOVFCPUSectionSetCPUSpeed()
    {
        VirtualHardwareSection hardwareSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        int cpuSpeed = CPU_SPEED_DEFAULT;
        hardwareSection.setCPUSpeed( cpuSpeed );
        assertEquals( "cpu speed does not match set value.", cpuSpeed, hardwareSection.getCPUSpeed() );
    }

    public void testOVFCPUSectionSetWrongCPUSpeed()
    {
        VirtualHardwareSection hardwareSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        int cpuSpeed = WRONG_DISK_SIZE;
        try
        {
            hardwareSection.setCPUSpeed( cpuSpeed );
            fail( "IllegalArgumentException expected." );
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( e.getMessage() );
        }
        assertTrue( "values should differ", cpuSpeed != hardwareSection.getCPUSpeed() );
    }

    public void testOVFOperatingSystemSection()
    {
        VirtualSystem system =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem();
        // the system is set to microsoft windows server 2008 by default
        assertEquals( OperatingSystemType.Microsoft_Windows_Server_2008.number(), system.getOperatingSystem()
                .getId() );
        system.getOperatingSystem().setId( OperatingSystemType.LINUX.number() );
        System.out.println( "Number of linux OS: " + system.getOperatingSystem().getId() );
        assertEquals( OS_LINUX, system.getOperatingSystem().getId() );
    }

    public void testOVFDiskFormatType()
    {
        DiskSection section =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getDiskSection();
        assertEquals( DiskFormatType.QCOW2.getSpecificationUrl(), section.getImageDisk().getFormat() );
        // change the format
        section.getImageDisk().setFormat( DiskFormatType.VHD.getSpecificationUrl() );
        assertEquals( "http://technet.microsoft.com/en-us/library/bb676673.aspx", section.getImageDisk()
                .getFormat() );
    }

    public void testOVFOperatingSystemType()
    {
        // try to get a os type by not existing number
        try
        {
            OperatingSystemType.findByNumber( DEFAULT_OS );
            fail( "IllegalArgumentException was expected." );
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( "Exception was expected: " + e.getMessage() );
        }
    }

    public void testOVFHardwareSectionGetItemArray()
    {

        VirtualHardwareSection hardwareSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        Item[] itemArray = hardwareSection.getItemArray();
        // should retrieve 5 items form VirtualMachineComponent.vm
        assertEquals( 6, itemArray.length );

    }

    public void testOVFDiskSectionsetPopulatedSize()
    {

        DiskSection diskSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()[ 0 ]
                        .getOVFDefinition()
                        .getDiskSection();
        diskSection.getImageDisk().setPopulatedSize( DEFAULT_DISK_SIZE );
        assertEquals( "populated range should be 1337", DEFAULT_DISK_SIZE, diskSection.getImageDisk()
                .getPopulatedSize() );

    }

    public void testOVFDiskSectionsetWrongPopulatedSize()
    {

        DiskSection diskSection =
                getManifest().getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()[ 0 ]
                        .getOVFDefinition()
                        .getDiskSection();
        long origSize = diskSection.getImageDisk().getPopulatedSize();
        try
        {
            diskSection.getImageDisk().setPopulatedSize( WRONG_DISK_SIZE );
            fail( "An exception was expected." );
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( e.getMessage() );
        }

        assertEquals( "populated range should match orignal size (VirtualMachineComponent.vml)", origSize,
                diskSection.getImageDisk().getPopulatedSize() );

    }
}
