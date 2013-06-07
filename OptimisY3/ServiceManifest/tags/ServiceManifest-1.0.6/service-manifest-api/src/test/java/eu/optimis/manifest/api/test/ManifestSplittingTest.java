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
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arumpl
 */
public class ManifestSplittingTest extends AbstractTestApi
{
    public void testShouldExtractAComponentByItsIdToANewManifest()
            throws SplittingNotAllowedException
    {
        String componentId = "Grumpy";
        // for the tests we add the component we are going to extract:
        getManifest().getVirtualMachineDescriptionSection()
                .addNewVirtualMachineComponent( componentId );
        // allow federation
        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );

        // add our componentId to the affinity rule at first position, which holds the rule "Low" for the
        // first the jboss component.
        getManifest().getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope()
                .addComponentId( componentId );

        getManifest().getTRECSection().addNewCostSection( componentId );
        getManifest().getTRECSection().addNewEcoEfficiencySection( componentId );
        getManifest().getTRECSection().addNewTrustSection( componentId );
        getManifest().getTRECSection().addNewRiskSection( componentId );

        // first we need an IP Manifest.
        XmlBeanServiceManifestDocument xmlBeanManifest = getManifest().toXmlBeanObject();

        Manifest ipManifest = Manifest.Factory.newInstance( xmlBeanManifest );
        ipManifest.initializeInfrastructureProviderExtensions();
        // now we want to extract

        Manifest extractedManifest = ipManifest.extractComponent( componentId );

        // check that the extracted manifest contains no extension sections
        assertNull( extractedManifest.getInfrastructureProviderExtensions() );

        // the component should have been removed from the original manifest
        assertNull( ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(
                componentId ) );

        // both manifest documents must be valid
        assertTrue( ipManifest.toXmlBeanObject().validate() );

        assertTrue( extractedManifest.toXmlBeanObject().validate() );

        // check that changes in the extracted manifest has no effect to the current
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().setRisk( 1 );
        extractedManifest.initializeInfrastructureProviderExtensions();
        extractedManifest.getInfrastructureProviderExtensions().getAllocationOffer().setRisk( 2 );

        assertNotSame(
                ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().getRisk(),
                extractedManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                        .getRisk() );

        // check that the componentid is not anymore in the ipManifest
        assertFalse( "ip manifest still contains extracted component id. ",
                ipManifest.toString().contains( componentId ) );
        // and check that the remaining componentId is not in the extracted manifest
        assertFalse( "extracted manifest still contains the jboss component id. ",
                extractedManifest.toString().contains( "jboss" ) );
    }

    public void testShouldNotSplitManifestIfAffinityIsHigh()
    {
        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
        getManifest().getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( "Test" );

        String[] scope = { "jboss", "Test" };
        getManifest().getVirtualMachineDescriptionSection().addNewAffinityRule( scope, "High" );

        System.out.println( getManifest().getVirtualMachineDescriptionSection() );
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        try
        {
            ipManifest.extractComponent( "Test" );
            fail( "Expected exception not thrown." );
        }
        catch ( SplittingNotAllowedException e )
        {
            System.out.println( "Exception was expected here: " + e.getMessage() );
        }
    }

    public void testShouldSplitManifestIfFederationIsFalse()
    {

        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( false );
        getManifest().getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( "Test" );
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );
        try
        {
            ipManifest.extractComponent( "Test" );
        }
        catch ( SplittingNotAllowedException e )
        {
            fail( "Unexpected exception thrown." + e );
        }
    }

    /**
     * It should be ok to split two components with high affintiy to each other, if they are going to be
     * extracted to the same manifest. As long as they do not have high or medium affinity to other components
     */
    public void testShouldSplitMultipleComponentsWithHighAffinity()
            throws SplittingNotAllowedException
    {
        //
        // set the stage
        //
        // allow federation
        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
        addVmComponent( "mysql", "High" );

        // we have to add a third component, so that the original manifest is still valid after splitting
        getManifest().getVirtualMachineDescriptionSection()
                .addNewVirtualMachineComponent( "reamining " );
        //
        // when we try to extract jboss and mysql, as they have high affinity to each other there should be no
        // problem.
        //

        // splitting is only possible on IP side:
        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toJaxB() );
        // we also want to split the incarnated section:
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        // now do the splitting
        List<String> componentList = new ArrayList<String>();
        componentList.add( "jboss" );
        componentList.add( "mysql" );

        Manifest extracted = ipManifest.extractComponentList( componentList );
        assertNotNull( extracted );

        //
        // check that all components are removed from the original manifest
        //
        assertNull( ipManifest.getInfrastructureProviderExtensions()
                .getIncarnatedVirtualMachineComponentByComponentId( "jboss" ) );
        assertNull( ipManifest.getVirtualMachineDescriptionSection()
                .getVirtualMachineComponentById( "jboss" ) );
        assertNull( ipManifest.getInfrastructureProviderExtensions()
                .getIncarnatedVirtualMachineComponentByComponentId( "mysql" ) );
        assertNull( ipManifest.getVirtualMachineDescriptionSection()
                .getVirtualMachineComponentById( "mysql" ) );
    }

    private void addVmComponent( String componentId, String affinityConstraints )
    {
        // first we have to add a second component to the manifest
        String mysqlCompId = componentId;
        getManifest().getVirtualMachineDescriptionSection()
                .addNewVirtualMachineComponent( mysqlCompId );
        // now we add the second component to the rule, which already exists for the jboss component
        getManifest().getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope()
                .addComponentId( mysqlCompId );
        // now we set the affinity in this rule to high
        getManifest().getVirtualMachineDescriptionSection().getAffinityRule( 0 )
                .setAffinityConstraints( affinityConstraints );

        // add a price component for mysql
        getManifest().getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 )
                .addNewPriceComponent( componentId );
    }

    public void testManifestSplittingWithInvalidComponentList()
    {
        List<String> componentIds = new ArrayList<String>();
        componentIds.add( "someNonexistingComponent" );
        componentIds.add( "someNonexistingComponent2" );

        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
        addVmComponent( "mysql", "Low" );

        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toXmlBeanObject() );

        try
        {
            ipManifest.extractComponentList( componentIds );
            fail( "an exception should be thrown here." );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }

    public void testManifestSplittingRemovesComponentFromSPExtensions()
            throws SplittingNotAllowedException
    {
        addVmComponent( "splitted", "Low" );
        getManifest().getServiceProviderExtensionSection()
                .addNewVirtualMachineComponentConfiguration( "splitted" );
        //before splitting, both vm configurations exist
        assertNotNull(
                "original manifest does not contain splitted vm contextualization info in sp extension",
                getManifest().getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( "splitted" ) );
        assertNotNull(
                "original manifest does not contain jboss vm contextualization info in sp extension",
                getManifest().getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( "jboss" ) );

        eu.optimis.manifest.api.sp.Manifest manifest = getManifest().extractComponent( "splitted" );

        System.out.println( manifest.getServiceProviderExtensionSection().toString() );
        System.out.println( getManifest().getServiceProviderExtensionSection().toString() );

        assertNull( "The splitted manifest still contains vm contextualization info",
                getManifest().getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( "splitted" ) );
        assertNull( "The extracted manifest still contains wrong vm contextualization info",
                manifest.getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( "jboss" ) );
    }

    public void testManifestSplittingMultipleTimes() throws SplittingNotAllowedException
    {

        getManifest().getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
        //
        //  add a view components to the manifest
        //
        addVmComponent( "mysql", "Low" );
        addVmComponent( "dummy", "Low" );
        addVmComponent( "dummy2", "Low" );

        //
        //original manifest serialized to string
        //

        String manifestAsString = getManifest().toString();

        //
        // the first manifest for splitting contains all components
        //
        Manifest firstManifestForSplitting = Manifest.Factory.newInstance( manifestAsString );

        List<String> componentIds = new ArrayList<String>();
        componentIds.add( "jboss" );
        componentIds.add( "mysql" );

        //
        // we extract the jboss and mysql to a new manifest, the firstManifestForSplitting contains
        // now only dummy and dummy2
        //
        Manifest jbossAndMysql = firstManifestForSplitting.extractComponentList( componentIds );

        //
        // now we want to extract jboss and dummy2 to a new manifest, therefore we
        // need to instantate the manifest again from the string
        //
        List<String> componentIds2 = new ArrayList<String>();
        componentIds.add( "jboss" );
        componentIds.add( "dummy2" );

        Manifest secondManifestForSplitting = Manifest.Factory.newInstance( manifestAsString );

        //
        // the new manifest contains only jboss and dummy component the secondManifestForSplitting
        // contains only the remaining dummy and mysql components
        //
        Manifest jbossAndDummy = secondManifestForSplitting.extractComponentList( componentIds2 );
    }
}
