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

import eu.optimis.manifest.api.utils.ManifestParser;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityRuleDocument;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityRuleType;
import org.apache.xmlbeans.XmlObject;

public class ManifestParserTest extends AbstractTestApi {
    public ManifestParserTest(String testName) {
        super(testName);

    }

    public void setUp() throws Exception {
        super.setUp();
        super.initializeElasticity();
    }

    public void testIfReferenceIsInsideManifest() {

        String namespaceDecl = "declare namespace opt='http://schemas.optimis.eu/optimis/'; ";
        XmlObject[] elements = null;
        try {
            elements = manifest.toXmlBeanObject().selectPath(namespaceDecl + "$this//opt:VirtualMachineComponent[ @opt:componentId = 'jboss' ]");
//            elements = manifest.toXmlBeanObject().selectPath(namespaceDecl + "$this//ovf:Item[ rasd:Connection[@rasd:id = 'jboss' ] ]");
        } catch (Exception e) {
            System.out.println("this should not happen.");
                    e.printStackTrace();
        }
        if (elements.length > 0) {
            System.out.println("OK.");
        }

    }

    public void testFindAllElasticityRules(){

        manifest.getElasticitySection().addNewRule("jboss", "MaxCPULoad");
        ManifestParser parser = new ManifestParser(manifest.toXmlBeanObject());

        assertEquals(2,parser.copyAllElasticityRules("jboss").length);
    }

    public void testRemoveComponentFromElasticityRule(){

        manifest.getElasticitySection().addNewRule("jboss", "MaxCPULoad");
        ManifestParser parser = new ManifestParser(manifest.toXmlBeanObject());

        XmlBeanElasticityRuleDocument[] rulesCopy = parser.copyAllElasticityRules("jboss");

        XmlBeanElasticityRuleType[] rules = parser.selectAllElasticityRules("jboss");
        
        parser.removeComponentIdFromScope("jboss");


        //there should not be any rules
     //   assertEquals(0,parser.selectAllElasticityRules("jboss").length);
    }

    public void testReferences(){
        //add an affinity rule for a non existing component
        manifest.getVirtualMachineDescriptionSection().addNewAffinityRule("xyz", "Low");
        
        try {
            manifest.toXmlBeanObject();
//            fail("An exception should have been thrown");
        } catch (Exception e){
            
        }
    }
}
