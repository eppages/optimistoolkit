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
import eu.optimis.manifest.api.impl.AllocationOfferDecision;

import org.apache.log4j.Logger;

import org.apache.xmlbeans.XmlException;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

public class SetAdmissionControlDecision {

	public String serviceManifestWithACdecision = "";
	
	//private static Logger log = Logger.getLogger("eu.optimis.manifest.api.utils.XmlValidator");
	// <logger name="eu.optimis.manifest.api.utils.XmlValidator">
	//change logger name to log4j.xml
	
	public SetAdmissionControlDecision(String serviceManifest,String ACdecision,Logger log)
	{
		log.info("Ready to attach AdmissionControlDecision to SM");
		
		//log.info("serviceManifest : "+serviceManifest);
		//Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);
		
		Manifest ipManifest = null;
		try
		{
			//ipManifest = Manifest.Factory.newInstance(serviceManifest);
			
			ipManifest =
                eu.optimis.manifest.api.ip.Manifest.Factory.newInstance( serviceManifest );


			
			//XmlBeanServiceManifestDocument xmlBeanManifest = XmlBeanServiceManifestDocument.Factory.parse(serviceManifest);
			
			//ipManifest = Manifest.Factory.newInstance(xmlBeanManifest);
			
			if(ipManifest.isSetInfrastructureProviderExtensions())
			if (ipManifest.getInfrastructureProviderExtensions().isSetAllocationOffer()) 
	            ipManifest.getInfrastructureProviderExtensions().unsetAllocationOffer(); 
	    /*    
		} catch (XmlException x) {
			String Message="XmlException "+x.getMessage();
			System.out.println(Message);
			log.error(x.getMessage());
			x.printStackTrace();
		*/
		} catch (RuntimeException re) {
			String Message="RuntimeException "+re.getMessage();
			System.out.println(Message);
			log.error(re.getMessage());
			//e.printStackTrace();
				
		} catch (Exception e) {
			String Message="RuntimeException "+e.getMessage();
			System.out.println(Message);
			log.error(e.getMessage());
			//e.printStackTrace();
			
		}
		
		ipManifest.initializeInfrastructureProviderExtensions();		
		
		log.info("initializeInfrastructureProviderExtensions");
		
		String AdmissionControlDecision = null;
		
		if(ACdecision.contains("Accepted"))AdmissionControlDecision = AllocationOfferDecision.accepted.toString();
		else if(ACdecision.contains("Rejected"))AdmissionControlDecision = AllocationOfferDecision.rejected.toString();
		else if(ACdecision.contains("Partial accepted"))AdmissionControlDecision = AllocationOfferDecision.partial.toString();
		
		ipManifest.getInfrastructureProviderExtensions().addNewAllocationOffer();
        ipManifest.getInfrastructureProviderExtensions().getAllocationOffer()
                .setDecision(AdmissionControlDecision);
        
        String smg=" ACdecision "+AdmissionControlDecision+" Attached to SM";
		System.out.println(smg);
		log.info(smg);
		
		//serviceManifestWithACdecision = ipManifest.toString();
		
		try
		{
			serviceManifestWithACdecision = ipManifest.toString();
			
		} catch (Exception e) {
			String Message="RuntimeException "+e.getMessage()+" Ignored";
			System.out.println(Message);
			log.error(e.getMessage());
			e.printStackTrace();
			
		}
		
		log.info("Finish of attach AdmissionControlDecision to SM");
		
	}//constructor
	
}//class
