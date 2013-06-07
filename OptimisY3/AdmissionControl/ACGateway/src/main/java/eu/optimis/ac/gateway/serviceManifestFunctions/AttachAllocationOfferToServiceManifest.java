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

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.AllocationPattern;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.xmlbeans.XmlObject;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class AttachAllocationOfferToServiceManifest
{
	public String serviceManifestWithAllocationOffer = "";
	
	//private static Logger log = Logger.getLogger("eu.optimis.manifest.api.utils.XmlValidator");
	// <logger name="eu.optimis.manifest.api.utils.XmlValidator">
	//change logger name to log4j.xml
	
	public AttachAllocationOfferToServiceManifest(String serviceManifest,ArrayList<String> acceptedVirtualSystemComponentsIDAsList,ArrayList<String> AcceptedComponentsIDAsList,
			String riskValue,XmlObject costValue,MultivaluedMap<String, String> basicPhysicalHost,MultivaluedMap<String, String> elasticPhysicalHost,
			Logger log)
	{
		log.info("Ready to attach allocation pattern to SM");
		
		Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);
		
		for(int j=0;j<AcceptedComponentsIDAsList.size();j++)	 
	 	{
	 		String AcceptedServiceComponentID = AcceptedComponentsIDAsList.get(j);
	 		
	 		String AcceptedVirtualSystemComponentID = acceptedVirtualSystemComponentsIDAsList.get(j);
	 		
	 		AllocationPattern pattern = ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
	                .addNewAllocationPattern(AcceptedServiceComponentID);
	 		
	 		//String BasicPhysicalHost = basicPhysicalHost.get(AcceptedVirtualSystemComponentID).get(0);
			//pattern.addNewPhysicalHost(BasicPhysicalHost).setElastic( false );
			
	 		if(basicPhysicalHost.get(AcceptedVirtualSystemComponentID)==null){String message ="basicPhysicalHost.get("+AcceptedVirtualSystemComponentID+")==null";log.error(message);throw new RuntimeException(message);}
			
			for(int x=0;x<basicPhysicalHost.get(AcceptedVirtualSystemComponentID).size();x++)
			{
				String BasicPhysicalHost = basicPhysicalHost.get(AcceptedVirtualSystemComponentID).get(x);
				pattern.addNewPhysicalHost(BasicPhysicalHost).setElastic( false );
					
			}//for-x, each BasicPhysicalHost
			
			
			if(elasticPhysicalHost.get(AcceptedVirtualSystemComponentID)!=null)
			for(int x=0;x<elasticPhysicalHost.get(AcceptedVirtualSystemComponentID).size();x++)
			{
				String ElasticPlysicalHost = elasticPhysicalHost.get(AcceptedVirtualSystemComponentID).get(x);
				pattern.addNewPhysicalHost(ElasticPlysicalHost).setElastic( true );
				
			}//for-x, each ElasticPhysicalHost
			
			String smg=" Accepted "+AcceptedServiceComponentID+" Attached to SM";
			log.info(smg);
	 	}//for-j
		
		float riskAsFloat = (Float.valueOf(riskValue)).floatValue();
		ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().setRisk(riskAsFloat);
		ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().setCost(costValue);
		
		String smg=" Risk,Cost "+" Attached to SM";
		log.info(smg);
		
		serviceManifestWithAllocationOffer = ipManifest.toString();
		
	}//constructor
	
}//class