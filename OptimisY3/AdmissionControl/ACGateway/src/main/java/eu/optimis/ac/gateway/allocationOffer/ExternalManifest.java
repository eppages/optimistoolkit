/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.allocationOffer;

import eu.optimis.manifest.api.ip.Manifest;

import org.apache.log4j.Logger;

public class ExternalManifest
{
	//private static Logger log = Logger.getLogger("eu.optimis.manifest.api.utils.XmlValidator");
	// <logger name="eu.optimis.manifest.api.utils.XmlValidator">
	//change logger name to log4j.xml
	
	public String serviceManifestWithAllocationOfferWithExternalManifest = "";
	
	public ExternalManifest(String partialAcceptedServiceManifestWithAllocationOffer,String returnedFromSDOServiceManifest,String IP_ID,Logger log)
			
	{
		Manifest ipManifest = Manifest.Factory.newInstance(partialAcceptedServiceManifestWithAllocationOffer);
		
		Manifest mreturnedFromSDOServiceManifest = Manifest.Factory.newInstance(returnedFromSDOServiceManifest);
		
		ipManifest.getInfrastructureProviderExtensions().getAllocationOffer().addNewExternalDeployment(IP_ID,mreturnedFromSDOServiceManifest);
		
		//serviceManifestWithAllocationOfferWithExternalDeployment = ipManifest.toString();
		
		//The previous line with try catch
		
		try
		{
			serviceManifestWithAllocationOfferWithExternalManifest = ipManifest.toString();
			
		} catch (RuntimeException e) {
			String Message="RuntimeException "+e.getMessage()+" Ignored";
			System.out.println(Message);
			log.error(Message);
			e.printStackTrace();
			
		}
		
		
		String smg="External Manifest attached Succefully \n";
		System.out.println(smg);
		log.info(smg);
		
		//System.out.println("serviceManifestWithAllocationOfferWithExternalDeployment :");
		//System.out.println(serviceManifestWithAllocationOfferWithExternalDeployment);
		//System.out.println("-----------------------------------------------------serviceManifestWithAllocationOfferWithExternalDeployment");
	}//constructor
	
}//class