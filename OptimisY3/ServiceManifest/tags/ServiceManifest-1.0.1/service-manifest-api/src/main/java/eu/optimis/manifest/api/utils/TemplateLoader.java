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

package eu.optimis.manifest.api.utils;


import eu.optimis.manifest.api.ovf.sp.References;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.*;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.IncarnatedVirtualMachineComponentDocument;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.IncarnatedVirtualMachineComponentType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.xmlbeans.XmlException;

import java.io.StringWriter;
import java.io.Writer;

/**
 * @author arumpl
 */
public class TemplateLoader {
    private static final String VIRTUAL_MACHINE_COMPONENT_TEMPLATE = "/VirtualMachineComponent.vm";
    private static final String ELASTICITY_RULE_TEMPLATE = "/ElasticityRule.vm";
    private static final String AFFINITY_RULE_TEMPLATE = "/AffinityRule.vm";
    private static final String SERVICE_MANIFEST_TEMPLATE = "/ServiceManifestTemplate.vm";
    private static final String INCARNATED_VIRTUAL_MACHINE_COMPONENT_TEMPLATE = "/IncarnatedVirtualMachineComponent.vm";

    public TemplateLoader() {
        // has to be set, otherwise template loading my fail
        // template root directory : src/main/resources
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    public XmlBeanServiceManifestDocument loadManifestDocument(String serviceId, String initialComponentId) {
        Template t = Velocity.getTemplate(SERVICE_MANIFEST_TEMPLATE);
        VelocityContext ctx = new VelocityContext();
        ctx.put("componentId", initialComponentId);
        ctx.put("serviceId", serviceId);

        Writer writer = new StringWriter();
        t.merge(ctx, writer);

        XmlBeanServiceManifestDocument serviceManifestDocument;
        try {
            serviceManifestDocument = XmlBeanServiceManifestDocument.Factory.parse(writer.toString());
        } catch (XmlException e) {
            throw new RuntimeException(e);
        }
        return serviceManifestDocument;
    }

    public XmlBeanVirtualMachineComponentType loadVirtualMachineComponentTemplate(String serviceId, String componentId) {
        Template t = Velocity.getTemplate(VIRTUAL_MACHINE_COMPONENT_TEMPLATE);
        VelocityContext ctx = new VelocityContext();
        ctx.put("componentId", componentId);
        ctx.put("serviceId", serviceId);
        Writer writer = new StringWriter();
        t.merge(ctx, writer);

        XmlBeanVirtualMachineComponentDocument vmComponent;
        try {
            vmComponent = XmlBeanVirtualMachineComponentDocument.Factory.parse(writer.toString());
        } catch (XmlException e) {
            throw new RuntimeException(e);
        }
        return vmComponent.getVirtualMachineComponent();
    }

    public XmlBeanRuleType loadElasticityTemplate(String componentId) {

        Template t = Velocity.getTemplate(ELASTICITY_RULE_TEMPLATE);
        VelocityContext ctx = new VelocityContext();
        ctx.put("componentId", componentId);

        Writer writer = new StringWriter();
        t.merge(ctx, writer);

        XmlBeanRuleDocument ruleDocument;
        try {
            ruleDocument = XmlBeanRuleDocument.Factory.parse(writer.toString());
        } catch (XmlException e) {
            //this should never happen.
            throw new RuntimeException(e);
        }


        return ruleDocument.getRule();
    }

    public XmlBeanAffinityRuleType loadAffinityTemplate(String componentId, String affinityLevel) {

        Template t = Velocity.getTemplate(AFFINITY_RULE_TEMPLATE);
        VelocityContext ctx = new VelocityContext();
        ctx.put("componentId", componentId);
        ctx.put("affinityConstraints", affinityLevel);

        Writer writer = new StringWriter();
        t.merge(ctx, writer);

        XmlBeanAffinityRuleDocument ruleDocument;
        try {
            ruleDocument = XmlBeanAffinityRuleDocument.Factory.parse(writer.toString());
        } catch (XmlException e) {
            throw new RuntimeException(e);
        }
        return ruleDocument.getAffinityRule();
    }

    public IncarnatedVirtualMachineComponentType loadIncarnatedVirtualMachineComponentType(VirtualMachineComponent baseComponent) {
        try {
            Template t = Velocity.getTemplate(INCARNATED_VIRTUAL_MACHINE_COMPONENT_TEMPLATE);
            VelocityContext ctx = new VelocityContext();
            References ref = baseComponent.getOVFDefinition().getReferences();
            ctx.put("upperBound", baseComponent.getAllocationConstraints().getUpperBound());
            ctx.put("fileUrl", ref.getFile().getHref());
            ctx.put("componentId", baseComponent.getComponentId());
            ctx.put("capacity", baseComponent.getOVFDefinition().getDiskSection().getDisk().getCapacity());
            ctx.put("format", baseComponent.getOVFDefinition().getDiskSection().getDisk().getFormat());
            ctx.put("virtualHardwareFamily", baseComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().getVirtualHardwareFamily());
            ctx.put("numberOfCPUs", baseComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().getNumberOfVirtualCPUs());
            ctx.put("memorySize", baseComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().getMemorySize());

            ctx.put("product", baseComponent.getOVFDefinition().getVirtualSystem().getProductSection().getProduct());
            ctx.put("version", baseComponent.getOVFDefinition().getVirtualSystem().getProductSection().getVersion());

            Writer writer = new StringWriter();
            t.merge(ctx, writer);

            IncarnatedVirtualMachineComponentType doc = IncarnatedVirtualMachineComponentDocument.Factory.parse(writer.toString()).getIncarnatedVirtualMachineComponent();


            return doc;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
