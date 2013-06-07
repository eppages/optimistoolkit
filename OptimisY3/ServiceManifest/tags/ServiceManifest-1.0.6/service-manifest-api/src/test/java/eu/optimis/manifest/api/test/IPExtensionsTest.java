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

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import eu.optimis.manifest.api.ip.AllocationPattern;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import org.apache.xmlbeans.XmlObject;

/**
 * Simple test of the IP extension mechanisms, such as incarnation and verification.
 *
 * @author owaeld
 */
public class IPExtensionsTest extends AbstractTestApi
{
    private static final String COMPONENT_ID_ONE = "First";

    private static final String COMPONENT_ID_TWO = "Second";

    public void setUp() throws Exception
    {
        super.setUp();

        // we will add two components which we can export and set them in the external deployment section
        getManifest().getVirtualMachineDescriptionSection()
                .addNewVirtualMachineComponent( COMPONENT_ID_ONE );
        getManifest().getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope()
                .addComponentId( COMPONENT_ID_ONE );
        getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope()
                .addComponentId( COMPONENT_ID_ONE );
        getManifest().getTRECSection().getRiskSectionArray( 0 ).getScope()
                .addComponentId( COMPONENT_ID_ONE );
        // getManifest().getElasticitySection().getRule( 0 ).getScope().addComponentId( COMPONENT_ID_ONE );

        getManifest().getVirtualMachineDescriptionSection()
                .addNewVirtualMachineComponent( COMPONENT_ID_TWO );
        getManifest().getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope()
                .addComponentId( COMPONENT_ID_TWO );
        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );

        getManifest().getTRECSection().getRiskSectionArray( 0 ).getScope()
                .addComponentId( COMPONENT_ID_TWO );
        getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope()
                .addComponentId( COMPONENT_ID_TWO );

        // add a price plan for each component

        getManifest().getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 )
                .addNewPriceComponent( COMPONENT_ID_ONE );
        getManifest().getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 )
                .addNewPriceComponent( COMPONENT_ID_TWO );
    }

    /**
     * here we extract a component to a new manifest and then set the extracted manifest in the
     * "ExternalDeployment" section of the IP extensions section.
     */
    public void testShouldSetManifestInExternalDeployment() throws SplittingNotAllowedException
    {

        System.out.println( getManifest().getErrors() );
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        Manifest firstExtracted = ipManifest.extractComponent( COMPONENT_ID_ONE );
        Manifest secondExtracted = ipManifest.extractComponent( COMPONENT_ID_TWO );

        firstExtracted.initializeInfrastructureProviderExtensions();

        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer();
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .addNewExternalDeployment( "xxx", firstExtracted );
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .addNewExternalDeployment( "xxx", secondExtracted );

        assertNotNull( ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .getExternalDeployment( 0 ) );
    }

    /**
     * in this test we are going to replace a manifest in the ExternalDeployment with another one
     */
    public void testShouldReplaceExternalManifest() throws SplittingNotAllowedException
    {

        // federation has to be allowed to extract components
        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );

        //
        // given is an ip manifest with an already set external deployment
        //
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();

        Manifest extracted = ipManifest.extractComponent( COMPONENT_ID_ONE );
        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer();
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .addNewExternalDeployment( "xxx", extracted );

        // when we try to replace the external manifest

        Manifest second = ipManifest.extractComponent( COMPONENT_ID_TWO );
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .getExternalDeployment( 0 )
                .replaceServiceManifest( second );

        //
        // then the componentId of the manifest in the external deployment section must equal to our second
        // component.
        //
        assertEquals( COMPONENT_ID_TWO,
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .getExternalDeployment( 0 ).exportServiceManifest()
                        .getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentArray( 0 ).getComponentId() );
    }

    /**
     * shows how to set the admission control decision
     */
    public void testShouldSetAdmissionControlDecision()
    {
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();

        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer();
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .setDecision( AllocationOfferDecision.accepted.toString() );

        assertEquals( "accepted",
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .getDecision() );
    }

    public void testShouldReplaceExistingAllocationOffer()
    {
        //given an IP extension with an allocation offer
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();
        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer()
                .addNewAllocationPattern( "jboss" );

        //when we unset the allocation offer
        if ( ipManifest.getInfrastructureProviderExtensions().isSetAllocationOffer() )
        {
            ipManifest.getInfrastructureProviderExtensions().unsetAllocationOffer();
        }

        //then the allocation offer is not set anymore
        assertFalse( ipManifest.getInfrastructureProviderExtensions().isSetAllocationOffer() );

        //and we can set a new one

        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer()
                .addNewAllocationPattern( "jboss" ).addNewPhysicalHost( "dummy" );

        //and the manifest is valid.

        assertFalse( "Manifest is not valid", ipManifest.hasErrors() );
    }

    /**
     * adds an allocation pattern for component "jboss" and adds physical hosts and sets the risk and the cost
     * for the allocation offer
     */
    public void testShouldVerifyValidAllocationPatternMechanisms()
    {
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();
        AllocationPattern pattern =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .addNewAllocationPattern( "jboss" );

        pattern.addNewPhysicalHost( "test" );
        pattern.removePhysicalHost( 0 );
        pattern.addNewPhysicalHost( "dummy" );
        assertEquals( "dummy", pattern.getPhysicalHostArray( 0 ).getHostName() );

        assertEquals( "dummy", ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .getAllocationPattern( "jboss" ).getPhysicalHostArray( 0 )
                .getHostName() );
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().setRisk( 2 );
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .setCost( XmlObject.Factory.newInstance() );
    }

    public void testGetPhysicalHostArray() throws Exception
    {

        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();

        AllocationPattern pattern =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .addNewAllocationPattern( "jboss" );
        int originalLength = pattern.getPhysicalHostArray().length;

        pattern.addNewPhysicalHost( "test" );

        int currentLength = pattern.getPhysicalHostArray().length;

        assertTrue( "host array should increase after adding 1 new physical host.",
                originalLength < currentLength );
    }

    public void testBasicAndElasticPhysicalHosts()
    {
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();

        AllocationPattern pattern =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .addNewAllocationPattern( "jboss" );

        pattern.addNewPhysicalHost( "test" );
        pattern.addNewPhysicalHost( "elastic" ).setElastic( true );
        pattern.addNewPhysicalHost( "basic" ).setElastic( false );

        assertFalse( pattern.getPhysicalHostArray( 0 ).isElastic() );
        assertTrue( pattern.getPhysicalHostArray( 1 ).isElastic() );
        assertFalse( pattern.getPhysicalHostArray( 2 ).isElastic() );
    }

    public void testGetExternalDeploymentArray() throws SplittingNotAllowedException
    {

        // create new instance of IP manifest
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeInfrastructureProviderExtensions();
        int originalLen =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .getExternalDeploymentArray().length;

        // START SNIPPET externalDeployment
        // extract a component from the manifest
        Manifest extracted = ipManifest.extractComponent( COMPONENT_ID_ONE );

        // and now add it to the external deployment section
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .addNewExternalDeployment( "provider IP", extracted );

        // END SNIPPET externalDeployment

        int currentLen =
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .getExternalDeploymentArray().length;

        // test if external deployment array was increased by 1
        assertEquals( originalLen + 1, currentLen );
    }

    public void testGetIncarnatedVirtualSystemById()
    {
        // create new instance of IP manifest
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        // try to retrieve the jboss instance 1 virtual system
        VirtualSystem system =
                ipManifest.getInfrastructureProviderExtensions()
                        .getVirtualSystem( "system-jboss_instance-1" );

        assertEquals( "The system id does not match.", "system-jboss_instance-1", system.getId() );
    }

    public void testShouldNotGetVirtualSystemByNonExistingId()
    {
        // create new instance of IP manifest
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        // try to retrieve the jboss instance 1 virtual system
        try
        {
            ipManifest.getInfrastructureProviderExtensions()
                    .getVirtualSystem( "system-non-existing-id" );
            fail( "Exception was expected" );
        }
        catch ( Exception e )
        {
            System.out.println( "Exception expected: " + e.getMessage() );
        }
    }

    public void testIncarnateVMComponentsWithAlreadyExistingIPExtensionsShouldNotFail()
    {
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        //now we add some allocation offer
        ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer();

        //now we initialize ip incarnated vm components again.

        ipManifest.initializeIncarnatedVirtualMachineComponents();

        //then the manifest should still be valid
        assertFalse( ipManifest.hasErrors() );
    }
}
