/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.produceSM;

import eu.optimis.manifest.api.impl.ServiceManifestProperties;
import eu.optimis.manifest.api.ovf.impl.OperatingSystemType;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

public class ProduceServiceManifest {
    
    public static String ProduceSM(String ComponentIdName,int numberOfComponents,Boolean isFederationAllowed,
                ArrayList<Integer> affinityConstraints_List, ArrayList<Integer> antiAffinityConstraints_List,
                String affinityRules,String antiAffinityRules)
	{
            
            Map <Integer,String> componentsNames = new HashMap<Integer, String>();
            
            AddAffinityConstraints.validateInput(affinityConstraints_List,antiAffinityConstraints_List,numberOfComponents);
            
		String manifestAsString ="";
		
		String COMPONENT_ID_ONE = ComponentIdName+"1";
                componentsNames.put(1,COMPONENT_ID_ONE);
		
        Properties properties = new ServiceManifestProperties();
        properties.setProperty( ServiceManifestProperties.VM_NUMBER_OF_VIRTUAL_CPU, "4" );
        properties.setProperty( ServiceManifestProperties.VM_OPERATING_SYSTEM_ID,
                String.valueOf( OperatingSystemType.LINUX.number() ) );
        properties.setProperty( ServiceManifestProperties.VM_OPERATING_SYSTEM_DESCRIPTION,
                OperatingSystemType.LINUX.name() );
        properties.setProperty( ServiceManifestProperties.VM_VIRTUAL_HARDWARE_FAMILY, "xen" );
        properties.setProperty( ServiceManifestProperties.VM_MEMORY_SIZE, "528" );
        properties.setProperty( ServiceManifestProperties.VM_CPU_SPEED, "500" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MAX, "15" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MIN, "4" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_INITIAL, "10" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_AFFINITY, "High" );

        properties.setProperty( ServiceManifestProperties.TRUST_LEVEL, "5" );
        properties.setProperty( ServiceManifestProperties.RISK_LEVEL, "1" );
        properties.setProperty( ServiceManifestProperties.ECO_LEED_CERTIFICATION, "Certified" );
        properties.setProperty( ServiceManifestProperties.ECO_BREEAM_CERTIFICATION, "Excellent" );
        properties.setProperty( ServiceManifestProperties.ECO_EUCOC_COMPLIANT, "true" );
        properties.setProperty( ServiceManifestProperties.ECO_ENERGY_STAR_RATING, "5" );
        properties.setProperty( ServiceManifestProperties.COST_CURRENCY, "USD" );
        properties.setProperty( ServiceManifestProperties.COST_MAX, "120.0" );
        properties.setProperty( ServiceManifestProperties.COST_MIN, "5.0" );
        properties.setProperty( ServiceManifestProperties.DATA_PROTECTION_LEVEL,
                XmlBeanDataProtectionLevelType.NONE.toString() );
        properties.setProperty( ServiceManifestProperties.DATA_PROTECTION_ENCRYPTION_ALGORITHM,
                XmlBeanEncryptionAlgoritmType.TWOFISH.toString() );
        properties.setProperty( ServiceManifestProperties.SP_EXTENSION_SECURITY_VPN_ENABLED,
                String.valueOf( true ) );
        properties.setProperty( ServiceManifestProperties.SP_EXTENSION_SECURITY_SSH_ENABLED,
                String.valueOf( true ) );
		
		eu.optimis.manifest.api.sp.Manifest manifest = 
				eu.optimis.manifest.api.sp.Manifest.Factory.newInstance
				("DemoApp", COMPONENT_ID_ONE , properties);
		
		int availability = 80;
		manifest.getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1D", availability );
		
		/*
		AffinityRule rule =
	            getManifest().getVirtualMachineDescriptionSection().addNewAffinityRule( "xxx",
	                XmlBeanAffinityConstraintType.LOW.toString() );
		*/
		
		//rule.setAffinityConstraints( XmlBeanAffinityConstraintType.HIGH.toString() );
		
		
		VirtualMachineComponent component = 
				manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray( 0 );
		
                manifest = AddAffinityConstraints.addAffinityConstraints(manifest,1,affinityConstraints_List);
                manifest = AddAffinityConstraints.addAntiAffinityConstraints(manifest,1,antiAffinityConstraints_List);
                
		component.getAllocationConstraints().setUpperBound(2);
		component.getAllocationConstraints().setInitial(1);
		
		for(int i=2;i<=numberOfComponents;i++)
		{
                        if((i==2)&&(!isFederationAllowed))
                            manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed( false );
                        else if((i==2)&&(isFederationAllowed))
                            manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
			
			String COMPONENT_ID_TWO = ComponentIdName+i;
			componentsNames.put(i,COMPONENT_ID_TWO);
                        
			component = 
			manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( COMPONENT_ID_TWO );
			
			manifest.getTRECSection().addNewRiskSection( COMPONENT_ID_TWO );
			manifest.getTRECSection().getRiskSectionArray( i-1 ).addNewAvailability( "P1D", availability );
			
                        
                        
                        manifest = AddAffinityConstraints.addAffinityConstraints(manifest,i,affinityConstraints_List);
                        manifest = AddAffinityConstraints.addAntiAffinityConstraints(manifest,i,antiAffinityConstraints_List);
                        
			component.getAllocationConstraints().setUpperBound(2);
			component.getAllocationConstraints().setInitial(1);
			
	        //manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        //manifest.getTRECSection().getRiskSectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        
			//manifest.getElasticitySection().getRule( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        
			//manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        
	        /*
	        manifest.getTRECSection().getCostSectionArray(0).getPricePlanArray(0).addNewPriceComponent(COMPONENT_ID_TWO);
			*/
			//if(i==2)
			//manifest.getTRECSection().getRiskSectionArray( 1 ).addNewAvailability( "P1D", 100 );
			
	        manifestAsString = manifest.toString();
		}//for-i,numberOfComponents
		
                if(affinityRules!=null)
                    manifest = AddAffinityRules.addAffinityRules(manifest, affinityRules, componentsNames);
                if(antiAffinityRules!=null)
                    manifest = AddAffinityRules.addAntiAffinityRules(manifest, antiAffinityRules, componentsNames);
                
        manifestAsString = manifest.toString();
	
        return manifestAsString;
        
	}//ProduceSM()
    
    
    
}//class
