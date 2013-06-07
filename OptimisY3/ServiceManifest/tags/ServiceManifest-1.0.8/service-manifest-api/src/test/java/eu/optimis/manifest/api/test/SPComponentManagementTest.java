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

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;

/**
 * Checks basic component management capabilities in the SP API.
 *
 * @author owaeld
 */
public class SPComponentManagementTest extends AbstractTestApi
{

    private static String id = "ADD-COMPONENT-ID";

    public void testAddComponent() throws Exception
    {
        System.out.println( "\ntestAddComponent invoked." );
        VirtualMachineDescriptionSection vm = getManifest().getVirtualMachineDescriptionSection();

        int previousLength = vm.getVirtualMachineComponentArray().length;
        System.out.println( "initial VM component number : " + previousLength );
        vm.addNewVirtualMachineComponent( id );

        VirtualMachineComponent[] vmArray = vm.getVirtualMachineComponentArray();
        int finalLength = vmArray.length;
        System.out.println( "final VM component number : " + finalLength );
        assertEquals( previousLength + 1, finalLength );
    }

    public void testRetrieveComponentById() throws Exception
    {
        System.out.println( "\ntestRetrieveComponent invoked." );
        VirtualMachineDescriptionSection vm = getManifest().getVirtualMachineDescriptionSection();
        vm.addNewVirtualMachineComponent( id );
        VirtualMachineComponent component = vm.getVirtualMachineComponentById( "ADD-COMPONENT-ID" );
        System.out.println( "retrieved component id :" + component.getComponentId() );
        assertEquals( id, component.getComponentId() );
    }

    public void testRemoveComponent() throws Exception
    {
        System.out.println( "\ntestRemoveComponent invoked." );
        VirtualMachineDescriptionSection vm = getManifest().getVirtualMachineDescriptionSection();
        vm.addNewVirtualMachineComponent( id );

        int previousLength = vm.getVirtualMachineComponentArray().length;
        System.out.println( "initial VM component number : " + previousLength );
        vm.removeVirtualMachineComponentById( id );

        VirtualMachineComponent[] vmArray = vm.getVirtualMachineComponentArray();
        int finalLength = vmArray.length;
        System.out.println( "final VM component number : " + finalLength );
        assertEquals( previousLength, finalLength + 1 );
    }

    public void testServiceEndpoints()
    {
        VirtualMachineComponent component =
                getManifest().getVirtualMachineDescriptionSection()
                        .addNewVirtualMachineComponent( "myComp" );
        assertEquals( 0, component.getServiceEndpoints().length );

        component.addNewServiceEndPoint( "name", "url" );
        component.addNewServiceEndPoint( "second", "url2" );
        assertEquals( 2, component.getServiceEndpoints().length );

        component.removeServiceEndpoint( 0 );
        assertEquals( 1, component.getServiceEndpoints().length );
    }

    public void testIsFederationAllowedFlag()
    {
        VirtualMachineDescriptionSection vmDescription =
                getManifest().getVirtualMachineDescriptionSection();
        vmDescription.setIsFederationAllowed( true );
        assertTrue( "federationAllowed flag should be true", vmDescription.isFederationAllowed() );
    }

    public void testGetIncarnatedContextualizationFileArray()
    {

        VirtualMachineComponent component =
                getManifest().getVirtualMachineDescriptionSection()
                        .addNewVirtualMachineComponent( "myComp" );
        component.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/path/to/myfile.iso" );
        component.getOVFDefinition().getReferences().getImageFile()
                .setHref( "/path/to/myfile.vmdk" );
        component.getAllocationConstraints().setUpperBound( 5 );

        String[] files = component.getIncarnatedContextualizationFileArray();

        assertEquals( 5, files.length );
        assertEquals( "/path/to/myfile_1.iso", files[ 0 ] );
        assertEquals( "/path/to/myfile_5.iso", files[ 4 ] );

        // and the incarnation process in the ip side has to produce the same filerefs

        System.out.println( getManifest().getErrors() );
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toString() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        assertEquals( files[ 0 ], ipManifest.getInfrastructureProviderExtensions()
                .getIncarnatedVirtualMachineComponentByComponentId( "myComp" )
                .getOVFDefinition().getReferences().getFileArray( 1 ).getHref() );
        assertEquals( files[ 1 ], ipManifest.getInfrastructureProviderExtensions()
                .getIncarnatedVirtualMachineComponentByComponentId( "myComp" )
                .getOVFDefinition().getReferences().getFileArray( 2 ).getHref() );
    }
}
