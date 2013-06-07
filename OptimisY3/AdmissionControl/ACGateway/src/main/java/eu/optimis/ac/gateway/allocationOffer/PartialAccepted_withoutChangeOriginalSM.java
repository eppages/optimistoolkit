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

import eu.optimis.ac.gateway.allocationOffer.remoteAC.GetRemoteManifest;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import eu.optimis.ac.gateway.serviceManifestFunctions.ExtractComponentsFromSM;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;

public class PartialAccepted_withoutChangeOriginalSM
{
	protected String partialAcceptedReturnedServiceManifest="";
	
	protected String finalMessage="";
	
	public PartialAccepted_withoutChangeOriginalSM(String serviceManifest,ArrayList<String> ListOfAccepted_Components,
			String riskValue,XmlObject costValue,ArrayList<String> acceptedVirtualSystemComponentsIDAsList,
			MultivaluedMap<String, String> basicPhysicalHost,MultivaluedMap<String, String> elasticPhysicalHost,
			String IP_ID,Logger log,int NumberOfServiceManifest,MultivaluedMap<String, String> Params)
	{
		log.info("Start PartialAccepted_withoutChangeOriginalSM");
		
		ExtractComponentsFromSM extract = 
				new ExtractComponentsFromSM(serviceManifest,ListOfAccepted_Components,log);
		
 		String rejectedComponentsOnlyServiceManifest = extract.RemainingServiceManifest;
		
		String partialAcceptedServiceManifestWithACdecision = 
				Decisions.setAdmissionControlDecision
				(serviceManifest,"Partial accepted",log);
                                
 		String partialAcceptedServiceManifestWithAllocationOffer = 
 				Decisions.addAllocationOfferToServiceManifest
 				(partialAcceptedServiceManifestWithACdecision,acceptedVirtualSystemComponentsIDAsList,ListOfAccepted_Components,
				riskValue,costValue,basicPhysicalHost,elasticPhysicalHost,log);
 		
 		log.info("Start Call Remote Cloud with rejected Components");
 		
 		String returnedFromSDOServiceManifest = sendServiceManifestToSDO(rejectedComponentsOnlyServiceManifest,log,Params);
 		
                /*
 		if(!ReadDecisionFromSM.isDecisionAccept(returnedFromSDOServiceManifest,log))
		{
			log.info("Remote AC Rejected the SM ==> the whole SM must be Rejected");
			
			Decisions dec = new Decisions();
			
			dec.Rejection(serviceManifest, NumberOfServiceManifest, log);
			
			partialAcceptedReturnedServiceManifest = dec.getReturnedSM();
			
			finalMessage = dec.getFinalMessage();
			
			return;
		}//if Remote AC not Accepts the SM
 		*/
                
 		log.info("End Call Remote Cloud");
 		
 		partialAcceptedReturnedServiceManifest = addExternalManifest(partialAcceptedServiceManifestWithAllocationOffer,returnedFromSDOServiceManifest,IP_ID,log);
 		
 		log.info("External serviceManifest Added");
 		
 		finalMessage="p";
 		
	}//constructor
	
	public static String sendServiceManifestToSDO(String serviceManifestWithRejectedComponents,Logger log,MultivaluedMap<String, String> Params)
	{
		String ReturnedServiceManifest = GetRemoteManifest.getRemoteManifest(serviceManifestWithRejectedComponents, log, Params);
		
                log.info("remoteAC.ReturnedServiceManifest : "+ReturnedServiceManifest);
                
		return ReturnedServiceManifest;
                
	}//SendServiceManifestToSDO()
	
	public static String addExternalManifest(String partialAcceptedServiceManifestWithAllocationOffer,String returnedFromSDOServiceManifest,String IP_ID,Logger log)
	{
		ExternalManifest externalManifest =
		new ExternalManifest(partialAcceptedServiceManifestWithAllocationOffer,returnedFromSDOServiceManifest,IP_ID,log);
		
		return externalManifest.serviceManifestWithAllocationOfferWithExternalManifest;
	}//addExternalDeployment()
	
}//class