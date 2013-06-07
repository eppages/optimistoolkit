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
import eu.optimis.manifest.api.sp.PricePlan;
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

public class ProduceManifest {
    
    public static String manifestAsString(ArrayList<String> componentId_List,
            String serviceId,Boolean isFederationAllowed,
            String virtualCpus,
            ArrayList<Integer> UpperBound_List,ArrayList<Integer> Initial_List,
            ArrayList<Integer> AvailabilityP1M_List,
            ArrayList<Integer> affinityConstraints_List, ArrayList<Integer> antiAffinityConstraints_List,
            String affinityRules,String antiAffinityRules)
    {
        String manifestAsString ="";
        Map <Integer,String> componentsNames = new HashMap<Integer, String>();
        ArrayList<Integer> AvailabilityAlreadyInserted = new ArrayList<Integer>();
        AddAffinityConstraints.validateInput(affinityConstraints_List,antiAffinityConstraints_List,componentId_List.size());
        
        String COMPONENT_ID_ONE = componentId_List.get(0);
        componentsNames.put(1,COMPONENT_ID_ONE);
        
        
        Properties properties = new ServiceManifestProperties();
        properties.setProperty( ServiceManifestProperties.VM_NUMBER_OF_VIRTUAL_CPU, virtualCpus );
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
				(serviceId, COMPONENT_ID_ONE , properties);
        
        VirtualMachineComponent component = 
		manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray( 0 );
	
        component.getAllocationConstraints().setUpperBound(UpperBound_List.get(0));
	component.getAllocationConstraints().setInitial(Initial_List.get(0));
        
        PricePlan plan = manifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );
	double COST_PLAN_CAP = 100;
	plan.setPlanCap( (float) COST_PLAN_CAP );
		
	manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).setEuCoCCompliant(true);
        
	manifest.getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1D", AvailabilityP1M_List.get(0) );
        manifest.getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1M", AvailabilityP1M_List.get(0) );
        
        AvailabilityAlreadyInserted.add(AvailabilityP1M_List.get(0));
        
        manifest = AddAffinityConstraints.addAffinityConstraints(manifest,1,affinityConstraints_List);
        manifest = AddAffinityConstraints.addAntiAffinityConstraints(manifest,1,antiAffinityConstraints_List);
                
        manifestAsString = manifest.toString();
        
        if(componentId_List.size()==1)return manifestAsString;
        
        for(int i=1;i<componentId_List.size();i++)
	{
		if(i==1)manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(isFederationAllowed);
			
		String COMPONENT_ID_TWO = componentId_List.get(i);
		componentsNames.put(i+1,COMPONENT_ID_TWO);
                
                
		component = 
			manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( COMPONENT_ID_TWO );
			
		component.getAllocationConstraints().setUpperBound(UpperBound_List.get(i));
                component.getAllocationConstraints().setInitial(Initial_List.get(i));
                
                Boolean AvailabilityInserted = false;
                for(int j=0;j<AvailabilityAlreadyInserted.size();j++)
                {    
                    if(AvailabilityAlreadyInserted.get(j)==AvailabilityP1M_List.get(i))
                    {
                        manifest.getTRECSection().getRiskSectionArray( j ).getScope().addComponentId( COMPONENT_ID_TWO );
                        AvailabilityInserted = true;
                    }
                }//for-j
                
                if(!AvailabilityInserted)
                {
                    
                    manifest.getTRECSection().addNewRiskSection(COMPONENT_ID_TWO).addNewAvailability( "P1M", AvailabilityP1M_List.get(i) );
                    //manifest.getTRECSection().getRiskSectionArray( i ).addNewAvailability("P1D", 99);
                    
                    AvailabilityAlreadyInserted.add(AvailabilityP1M_List.get(i));
                }//if-!AvailabilityInserted
                
                manifest = AddAffinityConstraints.addAffinityConstraints(manifest,i,affinityConstraints_List);
                manifest = AddAffinityConstraints.addAntiAffinityConstraints(manifest,i,antiAffinityConstraints_List);
                
	        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
                
	        manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        
	        manifestAsString = manifest.toString();
	}//for-i,numberOfComponents
        
        if(affinityRules!=null)
            manifest = AddAffinityRules.addAffinityRules(manifest, affinityRules, componentsNames);
        if(antiAffinityRules!=null)
            manifest = AddAffinityRules.addAntiAffinityRules(manifest, antiAffinityRules, componentsNames);
        
        manifestAsString = manifest.toString();
        
        return manifestAsString;
    }//manifestAsString()
    
    public static void writeToFile(String manifest,String filename,String targetDir)
    {
		writeToFile(manifestConverter(manifest),filename,targetDir);
    }//writeToFile()
    
    private static XmlBeanServiceManifestDocument manifestConverter(String manifest)
    {
	XmlBeanServiceManifestDocument parsedManifest = null;
            
        try {
                parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
            
            } catch (XmlException e) {
            
        	System.out.println("XmlException "+e.getMessage()+" Ignored");
                //e.printStackTrace(); 
            }//catch
		
	return parsedManifest;
    }//manifestConverter()
    
    private static void writeToFile(XmlBeanServiceManifestDocument manifest, String fileName,String targetDir) 
    {
        try {
            
            File file = new File(targetDir + File.separator + File.separator + fileName + ".xml");
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(manifest.xmlText(new XmlOptions().setSavePrettyPrint()));
            System.out.println(fileName + " was written to " + file.getAbsolutePath());
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }//writeToFile(XmlBeanServiceManifestDocument manifest, String fileName)
    
    public static void manifestToFile(ArrayList<String> componentId_List,
            String serviceId,Boolean isFederationAllowed,
            String virtualCpus,
            ArrayList<Integer> UpperBound_List,ArrayList<Integer> Initial_List,
            ArrayList<Integer> AvailabilityP1M_List,
            ArrayList<Integer> affinityConstraints_List, ArrayList<Integer> antiAffinityConstraints_List,
            String affinityRules,String antiAffinityRules,String filename,String targetDir)
    {
            writeToFile(manifestAsString(componentId_List,
            serviceId,isFederationAllowed,
            virtualCpus,
            UpperBound_List,Initial_List,
            AvailabilityP1M_List,
            affinityConstraints_List, antiAffinityConstraints_List,
            affinityRules,antiAffinityRules),filename,targetDir);
            
    }//manifestToFile()
    
}//class
