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

/**
 * @author arumpl
 */
public class ManifestReferencesTest extends AbstractTestApi {

    public void testShouldExportValidManifestWhenElasticityReferencesAreCorrect() {
        //add an affinity rule for a non existing component
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("LB");

        manifest.getElasticitySection().addNewExternalVariable("test", "int", "test");
        manifest.getElasticitySection().addNewRule("LB", "MaxCPULoad");
        manifest.getElasticitySection().getRule(0).getScope().addComponentId("jboss");
        try {
            manifest.toXmlBeanObject();

        } catch (Exception e) {
            fail("Unexpected exception occurred: " + e.getMessage());
        }
    }

    public void testShouldNotExportManifestWhenWrongElasticityReferencesExist() {
        //add an affinity rule for a non existing component

        manifest.getElasticitySection().addNewExternalVariable("test", "int", "test");
        manifest.getElasticitySection().addNewRule("xxyyzz", "MaxCPULoad");
        manifest.getElasticitySection().getRule(0).getScope().addComponentId("jboss");
        try {
            manifest.toXmlBeanObject();
            fail("An exception was expected.");
        } catch (Exception e) {
            System.out.println("Exception was expected: " + e.getMessage());
        }
    }

    public void testShouldExportValidManifestWhenAffinityReferencesAreCorrect() {
        //add an affinity rule for a non existing component
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("LB");

        manifest.getVirtualMachineDescriptionSection().addNewAffinityRule("LB", "High");
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(1).getScope().addComponentId("jboss");
        try {
            manifest.toXmlBeanObject();
        } catch (Exception e) {
            fail("Unexpected exception occurred: " + e.getMessage());
        }
    }

    public void testShouldNotExportManifestWhenWrongAffinityReferencesExist() {
        //add an affinity rule for a non existing component

        manifest.getVirtualMachineDescriptionSection().addNewAffinityRule("zzz", "High");
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(1).getScope().addComponentId("jboss");
        try {
            manifest.toXmlBeanObject();
            fail("An exception was expected.");
        } catch (Exception e) {
            System.out.println("Exception was expected: " + e.getMessage());

        }
    }

    public void testShouldNotExportManifestWhenComponentWasRemovedWithoutReferences(){
        manifest.getVirtualMachineDescriptionSection().removeVirtualMachineComponentById("jboss");
        try {
            manifest.toXmlBeanObject();
            fail("An exception was expected.");
        } catch (Exception e) {
            System.out.println("Exception was expected: " + e.getMessage());
        }
    }


}
