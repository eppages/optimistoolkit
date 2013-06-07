/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package eu.optimis.ac.gateway.serviceManifestFunctions;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.manifest.api.ip.Manifest;

import java.util.ArrayList;

import org.apache.xmlbeans.XmlException;
import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;

import org.apache.log4j.Logger;

public class ExtractComponentsFromSM
{
	public String ExtractedServiceManifest = "";
	
	public String RemainingServiceManifest = "";
	
	public ExtractComponentsFromSM(String ServiceManifest,ArrayList<String> componentIdList,Logger log)
	{
		try{
			
			XmlBeanServiceManifestDocument xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(ServiceManifest);
			
			Manifest ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
                        
			Manifest extractedManifest = ipManifest.extractComponentList( componentIdList );
    		
			ipManifest.initializeInfrastructureProviderExtensions();
			ipManifest.initializeIncarnatedVirtualMachineComponents();
                        
			ExtractedServiceManifest = extractedManifest.toString();
			
			RemainingServiceManifest = ipManifest.toString();
                        
                        xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(ExtractedServiceManifest);
                        ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
                        ipManifest.initializeInfrastructureProviderExtensions();
			ipManifest.initializeIncarnatedVirtualMachineComponents();
                        
                        ExtractedServiceManifest = ipManifest.toString();
                        
		} catch (XmlException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (SplittingNotAllowedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}//catch
		
	}//Constructor
	
	public ExtractComponentsFromSM(String ServiceManifest,String componentId,Logger log)
	{
		try{
			
			XmlBeanServiceManifestDocument xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(ServiceManifest);
			
	        Manifest ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
	        
	        ipManifest.initializeInfrastructureProviderExtensions();
	        ipManifest.initializeIncarnatedVirtualMachineComponents();
                
	        Manifest extractedManifest = ipManifest.extractComponent(componentId);
	        
	        ExtractedServiceManifest = extractedManifest.toString();
			RemainingServiceManifest = ipManifest.toString();
			
                        xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(ExtractedServiceManifest);
                        ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
                        ipManifest.initializeInfrastructureProviderExtensions();
			ipManifest.initializeIncarnatedVirtualMachineComponents();
                        
                        ExtractedServiceManifest = ipManifest.toString();
                        
		} catch (XmlException e) {
				log.error(e.getMessage());
				e.printStackTrace();
		} catch (SplittingNotAllowedException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}//catch
                
		
	}//ConstructorxmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(ExtractedServiceManifest);
                        
}//class