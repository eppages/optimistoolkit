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

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlException;

import eu.optimis.manifest.api.sp.PricePlan;

import java.io.IOException;
import java.io.FileWriter;

import java.io.BufferedWriter;
import java.io.File;

import eu.optimis.manifest.api.impl.ServiceManifestProperties;
import java.util.Properties;

import eu.optimis.manifest.api.ovf.impl.OperatingSystemType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;

import eu.optimis.manifest.api.sp.AffinityRule;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;
import java.util.ArrayList;

public class Produce_ServiceManifest {

	private static String OutputFolder="C:\\";
	
	private static String BasicFilename="SM";
	
	public Produce_ServiceManifest(){}//constructor
	
	public Produce_ServiceManifest(int NumberOfFilesToBeCreated,int NumberOfComponentForEachFile, Boolean IsFederationAllowed)
	{
            
            
		for(int i=1;i<=NumberOfFilesToBeCreated;i++)
		{	
			char c = (char)((int)'A'+i-1);
			String ComponentIdName="jboss"+c;
			
			//String manifestAsString = ProduceServiceManifest1_0_4(ComponentIdName,NumberOfComponentForEachFile);
			
			//String filename=BasicFilename+"_"+i+"c"+NumberOfComponentForEachFile+".xml";
			
			//WriteFile(OutputFolder,filename,manifestAsString);
			
                        ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>();
                        ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>();
                        
			writeToFile(ProduceServiceManifest.ProduceSM(ComponentIdName,NumberOfComponentForEachFile,IsFederationAllowed,affinityConstraints_List,antiAffinityConstraints_List,null,null), BasicFilename+"_"+i+"c"+NumberOfComponentForEachFile,OutputFolder);
		}//for-i , NumberOfFilesToBeCreated	
		
	}//Constuctor
	
	
	
	private String ProduceServiceManifest1_0_6(String ComponentIdName,int numberOfComponents)
	{
		String manifestAsString ="";
		
		String COMPONENT_ID_ONE = ComponentIdName+"1";
		
		eu.optimis.manifest.api.sp.Manifest manifest = 
				eu.optimis.manifest.api.sp.Manifest.Factory.newInstance
				("DemoApp", COMPONENT_ID_ONE);
		
		VirtualMachineComponent component = 
		manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray( 0 );
		component.getAllocationConstraints().setUpperBound(10);
		component.getAllocationConstraints().setInitial(3);
		
		
		PricePlan plan = manifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );
		double COST_PLAN_CAP = 100;
		plan.setPlanCap( (float) COST_PLAN_CAP );
		
		manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).setEuCoCCompliant(true);
        
		manifest.getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1D", 99 );
		
		//manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( COMPONENT_ID_ONE );
		//manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope().addComponentId( COMPONENT_ID_ONE );
        //manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_ONE );
        //manifest.getTRECSection().getRiskSectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_ONE );
        //manifest.getElasticitySection().getRule( 0 ).getScope().addComponentId( COMPONENT_ID_ONE );	
        
		/*
		manifest.getTRECSection().getCostSectionArray(0).getPricePlanArray( 0 ).addNewPriceComponent(COMPONENT_ID_ONE);
        */
		manifestAsString = manifest.toString();
		
		for(int i=2;i<=numberOfComponents;i++)
		{
			if(i==2)manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed( true );
			
			String COMPONENT_ID_TWO = ComponentIdName+i;
			
			component = 
			manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent( COMPONENT_ID_TWO );
			
			component.getAllocationConstraints().setUpperBound(10);
			component.getAllocationConstraints().setInitial(3);
			
	        manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        manifest.getTRECSection().getRiskSectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        //manifest.getElasticitySection().getRule( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope().addComponentId( COMPONENT_ID_TWO );
	        
	        /*
	        manifest.getTRECSection().getCostSectionArray(0).getPricePlanArray(0).addNewPriceComponent(COMPONENT_ID_TWO);
			*/
	        
	        manifestAsString = manifest.toString();
		}//for-i,numberOfComponents
		
		return manifestAsString;
	}//ProduceServiceManifest1_0_6()
	/*
	private String ProduceServiceManifest1_0_5(String ComponentIdName,int numberOfComponents)
	{
		return ProduceServiceManifest1_0_4(ComponentIdName,numberOfComponents);
	}//ProduceServiceManifest1_0_5
	
	private String ProduceServiceManifest1_0_4(String ComponentIdName,int numberOfComponents)
	{
		String manifestAsString ="SEX";
		
		eu.optimis.manifest.api.sp.Manifest manifest = 
				eu.optimis.manifest.api.sp.Manifest.Factory.newInstance
				("OptimisDemoService", ComponentIdName+"1");
		
		manifestAsString = manifest.toString();
		
		for(int i=2;i<=numberOfComponents;i++)
		{
			String componentId = ComponentIdName+i;
			manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent(componentId);
			manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(true);
			manifest.getTRECSection().getCostSection().getPricePlanArray(0).getScope().addComponentId(componentId);
			manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).getScope().addComponentId(componentId);
			
			manifestAsString = manifest.toString();
		
		}//for-i,numberOfComponents
		
		//Manifest ipManifest = Manifest.Factory.newInstance(manifestAsString);
		//ipManifest.initializeInfrastructureProviderExtensions();
		//Manifest mreturnedFromSDOServiceManifest = Manifest.Factory.newInstance(manifestAsString);
		//ipManifest.getInfrastructureProviderExtensions().addNewExternalDeployment("xxx",mreturnedFromSDOServiceManifest);
		//manifestAsString = ipManifest.toString();
		
		return manifestAsString;
		
	}//ProduceServiceManifest()
	
	private XmlBeanServiceManifestDocument ProduceSM104(String componentIDName,int numberOfComponents)
	{
		eu.optimis.manifest.api.sp.Manifest manifest = 
				eu.optimis.manifest.api.sp.Manifest.Factory.newInstance
				("OptimisDemoService", componentIDName+"1");
		
		for(int i=2;i<=numberOfComponents;i++)
		{
			String componentId = componentIDName+i;
			manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent(componentId);
			manifest.getVirtualMachineDescriptionSection().setIsFederationAllowed(true);
			manifest.getTRECSection().getCostSection().getPricePlanArray(0).getScope().addComponentId(componentId);
			manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).getScope().addComponentId(componentId);
		}//for-i,numberOfComponents
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
            parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest.toString());
            
        } catch (XmlException e) {
            
            e.printStackTrace(); 
        }
		
		return parsedManifest;
	}//ProduceSM104()
	*/
	private void WriteFile(String path,String filename,String content)
	{
		try{
			FileWriter writer = new FileWriter(path + filename);
		
			writer.append(content);
		
			writer.flush();
			writer.close();
		} catch (IOException e) {
			
    		e.printStackTrace();
    		
		}	
	}//WriteFile()
	
	
	private void writeToFile(XmlBeanServiceManifestDocument manifest, String fileName,String targetDir) {
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
	
	private XmlBeanServiceManifestDocument manifestConverter(String manifest)
	{
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
            parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
            
        } catch (XmlException e) {
            
        	System.out.println("XmlException "+e.getMessage()+" Ignored");
            //e.printStackTrace(); 
        }
		
		return parsedManifest;
	}//manifestConverter()
	
	public void writeToFile(String manifest,String filename,String targetDir)
	{
		writeToFile(manifestConverter(manifest),filename,targetDir);
	}//writeToFile()
}//Class
