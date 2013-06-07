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

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.utils.TemplateLoader;
import eu.optimis.manifest.api.utils.XmlValidator;
import eu.optimis.types.xmlbeans.servicemanifest.*;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentType;
import junit.framework.TestCase;
import org.apache.xmlbeans.XmlException;

import java.math.BigDecimal;

/**
 * @author arumpl
 */
public class TemplateLoaderTest extends TestCase {

    private static String id = "Super-ID";
    private static BigDecimal tolerance = new BigDecimal(5);
    private static String constraint = XmlBeanAffinityConstraintType.LOW.toString();

    public void testManifestTemplate() throws XmlException {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanServiceManifestDocument doc = loader.loadManifestDocument("crazy-service", "initial-component");
        assertTrue(XmlValidator.validate(doc));
    }

    public void testVMComponentTemplate() throws Exception {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanVirtualMachineComponentType component = loader.loadVirtualMachineComponentTemplate("DemoApp","jboss");

        assertTrue(XmlValidator.validate(component));

        assertEquals("jboss", component.getComponentId());
    }

    public void testElasticityRuleLoadingValidBean() throws Exception {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanElasticityRuleType rule_type = loader.loadElasticityRuleTemplate(id,"test");

        assertTrue(XmlValidator.validate(rule_type));
    }

    public void testElasticityRuleValueComparison() throws Exception {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanElasticityRuleType ruleType = loader.loadElasticityRuleTemplate(id,"test");

        assertEquals(id, ruleType.getScope().getComponentIdArray(0));
        //TODO update teh elasticity rule test
//        String kpiname = "ThreadCount";
//        assertEquals(kpiname, ruleType.getKPIName());
//        String duration = "P1M";
//        assertEquals(duration, ruleType.getWindow().toString());
//        long frequency = 1;
//        assertEquals(BigInteger.valueOf(frequency), ruleType.getFrequency());
//        long quota = 100;
//        assertEquals(BigInteger.valueOf(quota), ruleType.getQuota());
//        assertEquals(tolerance, ruleType.getTolerance());

    }

    public void testShouldLoadElasticityVariable(){
        TemplateLoader loader = new TemplateLoader();
        XmlBeanVariableType variableType = loader.loadElasticityVariableTemplate("myname", "1", XmlBeanElasticityLocationTypeEnum.INTERNAL, "http://monitoring/xxx/myname" );
        assertEquals("myname", variableType.getName());
        assertEquals("1", variableType.getMetric());
        assertEquals(XmlBeanElasticityLocationTypeEnum.INTERNAL, variableType.getType());
        assertEquals("http://monitoring/xxx/myname", variableType.getLocation());
    }

    public void testAffinityRuleLoadingValindBean() throws XmlException {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanAffinityRuleType rule_type = loader.loadAffinityTemplate(id, constraint);
        assertTrue(XmlValidator.validate(rule_type));

    }

    public void testAffinityRuleValueComparision() throws XmlException {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanAffinityRuleType rule_type = loader.loadAffinityTemplate(id, constraint);

        assertEquals(id, rule_type.getScope().getComponentIdArray(0));
        assertEquals(constraint, rule_type.getAffinityConstraints().toString());
    }

    public void testIPIncarnation() throws XmlException {
        TemplateLoader loader = new TemplateLoader();
        Manifest manifest = Manifest.Factory.newInstance("Teeest", "jboss");
        VirtualMachineComponent virtualMachineComponent = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss");
        virtualMachineComponent.getAllocationConstraints().setUpperBound(4);
        virtualMachineComponent.getOVFDefinition().getVirtualSystem().getProductSection().setProduct("JBOSS");
        virtualMachineComponent.getOVFDefinition().getVirtualSystem().getProductSection().setVersion("5.5");

        XmlBeanIncarnatedVirtualMachineComponentType doc = loader.loadIncarnatedVirtualMachineComponentType(manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss"));

        assertTrue(XmlValidator.validate(doc));
    }

}
