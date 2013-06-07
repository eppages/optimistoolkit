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
package eu.optimis.interopt.sla;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.ogf.graap.wsag.server.actions.impl.VelocityAgreementTemplateAction;

import eu.optimis.manifest.api.impl.ServiceManifestProperties;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.PriceComponent;
import eu.optimis.manifest.api.sp.PriceLevel;
import eu.optimis.manifest.api.sp.PricePlan;

/**
 * @author hrasheed
 */
public class TemplateAction extends VelocityAgreementTemplateAction
{

    //private static final String SERVICE_ID = "fbf3767f-203a-4b69-a8f9-0463bb7d7678";
    private static final String SERVICE_ID = "DemoApp";

    private static final String COMPONENT_ID = "COMPONENT-ID";

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.server.actions.impl.VelocityAgreementTemplateAction#getTemplateParameter()
     */
    @Override
    protected Map<String, ?> getTemplateParameter()
    {
        //
        // the agreement template has a service manifest as parameter.
        // we will create the service manifest here with the service-manifest-api
        //
        Properties serviceConfigProps = loadServiceConfigurationProperties();
        
        Manifest manifest = createManifest( serviceConfigProps );

        Map<String, Object> templateParameter = new HashMap<String, Object>();
        templateParameter.put( "serviceManifest", manifest.toXmlBeanObject() );

        templateParameter.put("maxInstances", serviceConfigProps.getProperty( "max.instances" ) );
        templateParameter.put("maxMemory", serviceConfigProps.getProperty( "max.memory" ) );
        templateParameter.put("maxCpu", serviceConfigProps.getProperty( "max.cpu" ) );
        templateParameter.put("maxSize", serviceConfigProps.getProperty( "max.hd.size" ) );
        templateParameter.put("maxAvailability", serviceConfigProps.getProperty( "max.availability" ) );

        return templateParameter;
    }

    private Manifest createManifest( Properties serviceConfigProps )
    {
        
        ServiceManifestProperties properties = new ServiceManifestProperties();
        
        properties.setProperty( ServiceManifestProperties.VM_IMAGE_FILE_HREF,
                                serviceConfigProps.getProperty("vm.image"));

//        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MAX,
//                                serviceConfigProps.getProperty( "max.instances" ) );
        
//      properties.setProperty( ServiceManifestProperties.VM_NUMBER_OF_VIRTUAL_CPU,
//      serviceConfigProps.getProperty( "max.cpu" ) );
        
        properties.setProperty( ServiceManifestProperties.VM_NUMBER_OF_VIRTUAL_CPU, "1" );
        
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MAX, "1" );
        
        properties.setProperty( ServiceManifestProperties.VM_MEMORY_SIZE,
                                serviceConfigProps.getProperty( "max.memory" ) );
                
        properties.setProperty( ServiceManifestProperties.VM_IMAGE_FILE_CAPACITY,
                                serviceConfigProps.getProperty( "max.hd.size" ) );
        
        properties.setProperty( "AVAILABILITY_PER_MONTH",
                                serviceConfigProps.getProperty( "max.availability" ) );

        Manifest manifest = Manifest.Factory.newInstance( SERVICE_ID, COMPONENT_ID, properties );
        
        addPriceComponents( serviceConfigProps, manifest );

        return manifest;
    }

    private void addPriceComponents( Properties serviceConfigProps, Manifest manifest )
    {
        //
        // the multiplier is used for all price components. All components are
        // priced per hour.
        //
        String multiplier = serviceConfigProps.getProperty( "price.multiplier" );

        //
        // all components have the same price plan, so we need only one cost section (which is already in
        // the template) and add the components to the first price plan which is empty in the template.
        //
        PricePlan pricePlan = manifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );

        //
        // set the currency
        //
        String currency = serviceConfigProps.getProperty( "price.currency" );
        pricePlan.setCurrency( currency );

        //
        // price per server is 0.02 EUR per Hour
        //
        float serverPrice = Float.valueOf( serviceConfigProps.getProperty( "price.server" ) );

        addPriceComponent( "Server", serverPrice, multiplier, pricePlan );

        //
        // cpu price: 0.02 EUR per hour
        //
        float cpuPrice = Float.valueOf( serviceConfigProps.getProperty( "price.cpu" ) );
        addPriceComponent( "CPU", cpuPrice, multiplier, pricePlan );

        //
        // price per gb ram: 0.02 EUR per hour
        //
        float ramPrice = Float.valueOf( serviceConfigProps.getProperty( "price.ram.gb" ) );
        addPriceComponent( "RAM", ramPrice, multiplier, pricePlan );

        //
        // price per gb hard disk: 0.02 EUR per hour
        //
        float hdPrice = Float.valueOf( serviceConfigProps.getProperty( "price.hd.gb" ) );
        addPriceComponent( "HD", hdPrice, multiplier, pricePlan );
    }

    private void addPriceComponent( String componentName, float absoluteAmount, String multiplier,
                                    PricePlan pricePlan )
    {
        PriceComponent serverPrice = pricePlan.addNewPriceComponent( componentName );
        PriceLevel serverLevel = serverPrice.addNewPriceLevel();
        serverLevel.setPriceType("Absolute");
        serverLevel.setAbsoluteAmount( absoluteAmount );
        serverLevel.setMultiplier( multiplier );
        serverLevel.setName( "Price per " + multiplier );
    }

    /**
     * retrieve the configuration for arsys which is stored in the service.configuration.properties file.
     * <ul>
     * <li>max.cpu=8</li>
     * <li>max.instances=10</li>
     * <li>max.memory=8192</li>
     * <li>max.hd.size=512000</li>
     * <li>max.availability=99.995</li>
     * </ul>
     * The price is defined hourly
     * <ul>
     * <li>price.multiplier=Hr</li>
     * <li>price.server=0.02</li>
     * <li>price.cpu=0.02</li>
     * <li>price.ram.gb=0.02</li>
     * <li>price.hd.gb=0.028</li>
     * <li>price.currency=EUR</li>
     * </ul>
     * 
     * @return the Properties
     */
    private Properties loadServiceConfigurationProperties()
    {
        Properties serviceConfigurationProperties = new Properties();
        try
        {
            InputStream inputStream = this.getClass().getResourceAsStream( "/service.configuration.properties" );
            serviceConfigurationProperties.load( inputStream );
            inputStream.close();
        }
        catch ( IOException e )
        {
            // this will never happen
            throw new RuntimeException( "Problem loading service configuration properties. ", e );
        }
        return serviceConfigurationProperties;
    }
}
