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
 * Date: 19.01.2012
 * Time: 12:13:17
 *
 */
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.utils.ManifestElementParser;
import eu.optimis.manifest.api.utils.ManifestParser;

import java.util.ArrayList;
import java.util.List;

public class ManifestParserTest extends AbstractTestApi {
    ManifestParser parser;

    public void setUp() throws Exception {
        super.setUp();
        super.initializeElasticity();
        manifest.getElasticitySection().addNewRule("jboss", "MaxCPULoad");
        manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("LB");
        manifest.getElasticitySection().addNewRule("LB", "maxCPULoad");
        manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).getScope().addComponentId("LB");
        manifest.getTRECSection().getCostSection().getPricePlanArray(0).getScope().addComponentId("LB");
        manifest.getServiceProviderExtensionSection().addNewVirtualMachineComponentConfiguration("LB");
        Manifest ipManifest = Manifest.Factory.newInstance(manifest.toXmlBeanObject());
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        parser = new ManifestParser(ipManifest.toXmlBeanObject());
    }


    public void testShouldRemoveComponentFromElasticityScope() {
        assertEquals(2, parser.selectObjects("$this//opt:ElasticityRule/opt:Scope[opt:ComponentId = 'jboss']").length);

        parser.removeComponent("jboss");

        Manifest cleaned = Manifest.Factory.newInstance(parser.getManifest());

        assertNull(cleaned.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponentByComponentId("jboss"));
        assertNull(cleaned.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss"));


        //there should not be any rules
        assertEquals(0, parser.selectObjects("$this//opt:ElasticityRule/opt:Scope[opt:ComponentId = 'jboss']").length);
    }

    public void testShouldGetAllComponentIDs() {

        List expected = new ArrayList();
        expected.add("jboss");
        expected.add("LB");

        assertTrue(expected.containsAll(parser.selectAllComponentIds()));
    }

    public void testShouldGetAllAffinityConstraints(){
        System.out.println(parser.selectAllAffinityConstraintsByComponentId("jboss"));
    }


}
