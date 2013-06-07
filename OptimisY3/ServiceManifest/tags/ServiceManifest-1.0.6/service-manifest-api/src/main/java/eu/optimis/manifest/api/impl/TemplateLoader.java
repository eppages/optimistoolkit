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
package eu.optimis.manifest.api.impl;

import eu.optimis.manifest.api.ovf.sp.References;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.*;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentDocument;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanVirtualMachineComponentConfigurationDocument;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanVirtualMachineComponentConfigurationType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.xmlbeans.XmlException;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

/**
 * @author arumpl
 */
public class TemplateLoader
{
    private static final String VIRTUAL_MACHINE_COMPONENT_TEMPLATE = "/VirtualMachineComponent.vm";

    private static final String ELASTICITY_RULE_TEMPLATE = "/ElasticityRuleY1.vm";

    private static final String AFFINITY_RULE_TEMPLATE = "/AffinityRule.vm";

    private static final String SERVICE_MANIFEST_TEMPLATE = "/ServiceManifestTemplate.vm";

    private static final String INCARNATED_VIRTUAL_MACHINE_COMPONENT_TEMPLATE =
            "/IncarnatedVirtualMachineComponent.vm";

    private static final String VIRTUAL_MACHINE_COMPONENT_CONFIGURATION_TEMPLATE =
            "/VirtualMachineComponentConfiguration.vm";

    private static final String ELASTICITY_VARIABLE_TEMPLATE = "/ElasticityVariable.vm";

    private static final String ECO_EFFICIENCY_TEMPLATE = "/EcoEfficiencySection.vm";

    private static final String RISK_TEMPLATE = "/RiskSection.vm";

    private static final String TRUST_TEMPLATE = "/TrustSection.vm";

    private static final String PRICE_COMPONENT_TEMPLATE = "/PriceComponent.vm";

    private static final String COST_SECTION_TEMPLATE = "CostSection.vm";

    private static final String DEFAULT_PROPERTIES_FILE = "/manifest.properties";
    public static final String COMPONENT_ID_LIST_KEY = "componentIdList";
    public static final String SERVICE_ID_KEY = "serviceId";
    public static final String COMPONENT_ID_KEY = "componentId";

    private Properties defaultProperties;

    protected TemplateLoader()
    {
        // has to be set, otherwise template loading my fail
        // template root directory : src/main/resources
        Velocity.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        Velocity.setProperty( "classpath.resource.loader.class",
                ClasspathResourceLoader.class.getName() );
        Velocity.init();
        // load the default properties
        loadDefaultProperties();
    }

    public XmlBeanServiceManifestDocument loadManifestDocument( String serviceId,
                                                                String[] componentIdList )
    {

        return loadManifestDocument( serviceId, componentIdList, defaultProperties );
    }

    private void loadDefaultProperties()
    {
        if ( defaultProperties == null )
        {
            defaultProperties = new ServiceManifestProperties();
        }
    }

    public XmlBeanServiceManifestDocument loadManifestDocument( String serviceId,
                                                                String[] componentIdList,
                                                                Properties properties )
    {
        Template t = Velocity.getTemplate( SERVICE_MANIFEST_TEMPLATE );
        VelocityContext ctx = createVelocityContext( properties );
        ctx.put( COMPONENT_ID_LIST_KEY, componentIdList );
        ctx.put( SERVICE_ID_KEY, serviceId );

        // add all properties to the velocity context
        putPropertiesToVelocityContext( properties, ctx );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanServiceManifestDocument serviceManifestDocument;
        try
        {
            serviceManifestDocument =
                    XmlBeanServiceManifestDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return serviceManifestDocument;
    }

    private void putPropertiesToVelocityContext( Properties properties, VelocityContext ctx )
    {
        for ( Object key : properties.keySet() )
        {
            ctx.put( key.toString(), properties.get( key ) );
        }
    }

    public XmlBeanVirtualMachineComponentType loadVirtualMachineComponentTemplate( String serviceId,
                                                                                   String componentId )
    {
        Template t = Velocity.getTemplate( VIRTUAL_MACHINE_COMPONENT_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_KEY, componentId );
        ctx.put( SERVICE_ID_KEY, serviceId );
        // no properties provided, we have to add the default properties to the context
        putPropertiesToVelocityContext( defaultProperties, ctx );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanVirtualMachineComponentDocument vmComponent;
        try
        {
            vmComponent = XmlBeanVirtualMachineComponentDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return vmComponent.getVirtualMachineComponent();
    }

    public XmlBeanElasticityRuleType loadElasticityRuleTemplate( String componentId, String name )
    {

        Template t = Velocity.getTemplate( ELASTICITY_RULE_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_KEY, componentId );
        ctx.put( "name", name );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanElasticityRuleDocument ruleDocument;
        try
        {
            ruleDocument = XmlBeanElasticityRuleDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            // this should never happen.
            throw new RuntimeException( e );
        }

        return ruleDocument.getElasticityRule();
    }

    public XmlBeanRuleType loadOldElasticityRuleTemplate( String[] componentList, String name )
    {

        Template t = Velocity.getTemplate( ELASTICITY_RULE_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_LIST_KEY, componentList );
        ctx.put( "name", name );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanRuleDocument ruleDocument;
        try
        {
            ruleDocument = XmlBeanRuleDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            // this should never happen.
            throw new RuntimeException( e );
        }

        return ruleDocument.getRule();
    }

    public XmlBeanVariableType loadElasticityVariableTemplate( String name, String metric,
                                                               XmlBeanElasticityLocationTypeEnum.Enum type,
                                                               String location )
    {
        Template t = Velocity.getTemplate( ELASTICITY_VARIABLE_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( "name", name );
        ctx.put( "metric", metric );
        ctx.put( "type", type );
        ctx.put( "path", location );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanVariableDocument variableDocument;
        try
        {
            variableDocument = XmlBeanVariableDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            // this should never happen.
            throw new RuntimeException( e );
        }
        return variableDocument.getVariable();
    }

    public XmlBeanAffinityRuleType loadAffinityTemplate( String[] componentList,
                                                         String affinityLevel )
    {

        Template t = Velocity.getTemplate( AFFINITY_RULE_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );
        ctx.put( COMPONENT_ID_LIST_KEY, componentList );
        ctx.put( "affinityConstraints", affinityLevel );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanAffinityRuleDocument ruleDocument;
        try
        {
            ruleDocument = XmlBeanAffinityRuleDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return ruleDocument.getAffinityRule();
    }

    public XmlBeanIncarnatedVirtualMachineComponentType
    loadIncarnatedVirtualMachineComponentType( VirtualMachineComponent baseComponent )
    {
        try
        {
            Template t = Velocity.getTemplate( INCARNATED_VIRTUAL_MACHINE_COMPONENT_TEMPLATE );
            VelocityContext ctx = new VelocityContext();
            References ref = baseComponent.getOVFDefinition().getReferences();
            ctx.put( "upperBound", baseComponent.getAllocationConstraints().getUpperBound() );
            ctx.put( "fileUrl_img", ref.getImageFile().getHref() );

            // there will be multiple files of the context file, therefore we need to provide the
            // file url without extension + the extension separately.
            String filePath = getFilePath( ref.getContextualizationFile().getHref() );
            String fileExtension = getFileExtension( ref.getContextualizationFile().getHref() );
            ctx.put( "fileUrl_context_path", filePath );
            ctx.put( "fileUrl_context_ext", fileExtension );

            ctx.put( "fileId_img", ref.getImageFile().getId() );
            ctx.put( "fileId_context", ref.getContextualizationFile().getId() );

            ctx.put( COMPONENT_ID_KEY, baseComponent.getComponentId() );

            ctx.put( "capacity_img",
                    baseComponent.getOVFDefinition().getDiskSection().getImageDisk()
                            .getCapacity() );
            ctx.put( "format_img", baseComponent.getOVFDefinition().getDiskSection().getImageDisk()
                    .getFormat() );

            ctx.put( "capacity_context", baseComponent.getOVFDefinition().getDiskSection()
                    .getContextualizationDisk().getCapacity() );
            ctx.put( "format_context", baseComponent.getOVFDefinition().getDiskSection()
                    .getContextualizationDisk().getFormat() );

            ctx.put( "diskId_img", baseComponent.getOVFDefinition().getDiskSection().getImageDisk()
                    .getDiskId() );
            ctx.put( "diskId_context", baseComponent.getOVFDefinition().getDiskSection()
                    .getContextualizationDisk().getDiskId() );

            ctx.put( "virtualHardwareFamily", baseComponent.getOVFDefinition().getVirtualSystem()
                    .getVirtualHardwareSection()
                    .getVirtualHardwareFamily() );
            ctx.put( "numberOfCPUs", baseComponent.getOVFDefinition().getVirtualSystem()
                    .getVirtualHardwareSection().getNumberOfVirtualCPUs() );
            ctx.put( "memorySize", baseComponent.getOVFDefinition().getVirtualSystem()
                    .getVirtualHardwareSection().getMemorySize() );
            ctx.put( "cpuSpeed", baseComponent.getOVFDefinition().getVirtualSystem()
                    .getVirtualHardwareSection().getCPUSpeed() );

            ctx.put( "product",
                    baseComponent.getOVFDefinition().getVirtualSystem().getProductSection()
                            .getProduct() );
            ctx.put( "version",
                    baseComponent.getOVFDefinition().getVirtualSystem().getProductSection()
                            .getVersion() );

            ctx.put( "operatingSystemId", baseComponent.getOVFDefinition().getVirtualSystem()
                    .getOperatingSystem().getId() );
            ctx.put( "operatingSystemDescription",
                    baseComponent.getOVFDefinition().getVirtualSystem()
                            .getOperatingSystem().getDescription() );

            Writer writer = new StringWriter();
            t.merge( ctx, writer );

            XmlBeanIncarnatedVirtualMachineComponentType doc =
                    XmlBeanIncarnatedVirtualMachineComponentDocument.Factory
                            .parse( writer.toString() )
                            .getIncarnatedVirtualMachineComponent();

            return doc;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private String getFilePath( String href )
    {
        if ( href != null && !href.isEmpty() )
        {
            int dotPos = href.lastIndexOf( "." );
            String strFilename = href.substring( 0, dotPos );
            return strFilename;
        }
        return href;
    }

    private String getFileExtension( String href )
    {
        if ( href != null && !href.isEmpty() )
        {
            int dotPos = href.lastIndexOf( "." );
            String strExtension = href.substring( dotPos + 1 );
            return strExtension;
        }
        return href;
    }

    public XmlBeanVirtualMachineComponentConfigurationType
    loadVirtualMachineComponentConfigurationTemplate( String componentId )
    {
        Template t = Velocity.getTemplate( VIRTUAL_MACHINE_COMPONENT_CONFIGURATION_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );
        ctx.put( COMPONENT_ID_KEY, componentId );
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanVirtualMachineComponentConfigurationDocument vmComponentConfig;
        try
        {
            vmComponentConfig =
                    XmlBeanVirtualMachineComponentConfigurationDocument.Factory
                            .parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return vmComponentConfig.getVirtualMachineComponentConfiguration();
    }

    public XmlBeanEcoEfficiencySectionType loadEcoEfficiencyTemplate( String[] componentIdList )
    {
        Template t = Velocity.getTemplate( ECO_EFFICIENCY_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_LIST_KEY, componentIdList );
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanEcoEfficiencySectionDocument doc;
        try
        {
            doc = XmlBeanEcoEfficiencySectionDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return doc.getEcoEfficiencySection();
    }

    private VelocityContext createVelocityContext( Properties properties )
    {
        VelocityContext ctx = new VelocityContext();
        putPropertiesToVelocityContext( properties, ctx );
        return ctx;
    }

    public XmlBeanRiskSectionType loadRiskSectionTemplate( String[] componentIdList )
    {

        Template t = Velocity.getTemplate( RISK_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_LIST_KEY, componentIdList );
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanRiskSectionDocument doc;
        try
        {
            doc = XmlBeanRiskSectionDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return doc.getRiskSection();
    }

    public XmlBeanTrustSectionType loadTrustTemplate( String[] componentIdList )
    {
        Template t = Velocity.getTemplate( TRUST_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );
        ctx.put( COMPONENT_ID_LIST_KEY, componentIdList );
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanTrustSectionDocument doc;
        try
        {
            doc = XmlBeanTrustSectionDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return doc.getTrustSection();
    }

    public XmlBeanPriceComponentType loadPriceComponentTemplate( String name )
    {
        Template t = Velocity.getTemplate( PRICE_COMPONENT_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );
        ctx.put( "name", name );

        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanPriceComponentDocument doc;
        try
        {
            doc = XmlBeanPriceComponentDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return doc.getPriceComponent();
    }

    public XmlBeanCostSectionType loadCostSectionTemplate( String[] componentIdList )
    {
        Template t = Velocity.getTemplate( COST_SECTION_TEMPLATE );
        VelocityContext ctx = createVelocityContext( defaultProperties );

        ctx.put( COMPONENT_ID_LIST_KEY, componentIdList );
        Writer writer = new StringWriter();
        t.merge( ctx, writer );

        XmlBeanCostSectionDocument doc;
        try
        {
            doc = XmlBeanCostSectionDocument.Factory.parse( writer.toString() );
        }
        catch ( XmlException e )
        {
            throw new RuntimeException( e );
        }
        return doc.getCostSection();
    }
}
