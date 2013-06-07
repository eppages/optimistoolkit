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

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.manifest.api.ip.AffinityRule;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author arumpl
 */
public class ManifestSplittingTest extends AbstractTestApi {

//    public ManifestSplittingTest(String testName) {
//        super(testName);
//    }

    public void testShouldExctractAComponentByItsIdToANewManifest() {
        String componentId = "Grumpy";
        //for the tests we add the component we are going to extract:
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent(componentId);
        //allow federation
        manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(true);
        //add our componentId to the price plan so that it will not be empty after splitting
        manifest.getTRECSection().getCostSection().getPricePlanArray(0).getScope().addComponentId(componentId);

        //add our componentId to the affinity rule at first position, which holds the rule "Low" for the first the jboss component.
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).getScope().addComponentId(componentId);

        //first we need an IP Manifest.
        XmlBeanServiceManifestDocument xmlBeanManifest = manifest.toXmlBeanObject();

        Manifest ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
        ipManifest.initializeInfrastructureProviderExtensions();
        //now we want to extract

        Manifest extractedManifest = ipManifest.extractComponent(componentId);

        //check that the extracted manifest contains no extension sections
        assertNull(extractedManifest.getInfrastructureProviderExtensions());

        //the component should have been removed from the original manifest
        assertNull(ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById(componentId));

        //both manifest documents must be valid
        assertTrue(ipManifest.toXmlBeanObject().validate());
        assertTrue(extractedManifest.toXmlBeanObject().validate());

        //check that changes in the extracted manifest has no effect to the current
        ipManifest.getInfrastructureProviderExtensions().setRisk(1);
        extractedManifest.initializeInfrastructureProviderExtensions();
        extractedManifest.getInfrastructureProviderExtensions().setRisk(2);
        
        assertNotSame(ipManifest.getInfrastructureProviderExtensions().getRisk(), extractedManifest.getInfrastructureProviderExtensions().getRisk());
    }

    public void testShouldNotSplitManifestIfAffinityIsHigh() {
        manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(true);
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("Test");
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).getScope().addComponentId("Test");
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).setAffinityConstraints("High");
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        try {
            ipManifest.extractComponent("Test");
            fail("Expected exception not thrown.");
        } catch (SplittingNotAllowedException e) {
            System.out.println("Exception was expected here: " + e.getMessage());
        }
    }

    public void testShouldNotSplitManifestIfFederationIsFalse(){

        manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(false);
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("Test");
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        try {
            ipManifest.extractComponent("Test");
            fail("Expected exception not thrown.");
        } catch (SplittingNotAllowedException e) {
            System.out.println("Exception was expected here: " + e.getMessage());
        }
    }

}
