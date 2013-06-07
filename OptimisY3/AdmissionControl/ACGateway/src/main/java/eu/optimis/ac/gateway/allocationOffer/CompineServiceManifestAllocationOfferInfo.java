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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerService;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfo;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class CompineServiceManifestAllocationOfferInfo
{		
	public MultivaluedMap<String, String> ReturnedServiceManifests = new MultivaluedMapImpl();
	
	public String finalMessage = "";
	
	public CompineServiceManifestAllocationOfferInfo(SMsAnalyzer smAnalyzer,
                ArrayList<AllocationOfferInfo> ListOfAllocationOffer,
                String AllocationDetails,
                Logger log,MultivaluedMap<String, String> Params)
	{
		
		if(ListOfAllocationOffer.size() != smAnalyzer.formParams.get("serviceManifest").size())
			throw new RuntimeException(
					"AllocationOffer and serviceManifest size are not equall "
			+ListOfAllocationOffer.size()+" "
			+ smAnalyzer.formParams.get("serviceManifest").size());
			
		for(int i=0;i<ListOfAllocationOffer.size();i++)
		{	
			AllocationOfferInfo allocationOffer = ListOfAllocationOffer.get(i);
			
			String serviceManifest = smAnalyzer.formParams.get("serviceManifest").get(i);
			String numberOfServiceComponents = smAnalyzer.formParams.get("numberOfServiceComponents").get(i);
			
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId = 
					smAnalyzer.ServiceComponentId_Map_componentId_List.get(i);
			
			String AllocationOffer_ServiceID = allocationOffer.getService_Id();
			String ServiceID = smAnalyzer.formParams.get("serviceId").get(i);
			
                        int federatedVMsPerService = FederatedVMsPerService.getFederatedVMsForCertainService(AllocationDetails, i+1);
                        
                        log.info ("Federated VMs for service : "+(i+1)+" is : "+federatedVMsPerService);
                        
			if(!ServiceID.contains(AllocationOffer_ServiceID))
                        {
                                log.error("ServiceID not Same "+"AllocationOffer_ServiceID is "
				+AllocationOffer_ServiceID+ " ServiceID is "+ServiceID);
				throw new RuntimeException(
						"ServiceID not Same "+"AllocationOffer_ServiceID is "
				+AllocationOffer_ServiceID+ " ServiceID is "+ServiceID);
                        }//if
			
                        String isFederationAllowed = smAnalyzer.formParams.get("isFederationAllowed").get(i);
                        
			compineServiceManifestAllocationOffer(
					serviceManifest,numberOfServiceComponents,
					allocationOffer,federatedVMsPerService,i+1,
					ServiceComponentId_Map_componentId,log,isFederationAllowed,Params);
			
		}//for-i
		
	}//constructor
	
	private void compineServiceManifestAllocationOffer(String serviceManifest,String numberOfServiceComponents,
			AllocationOfferInfo allocationOffer,int FederatedVMsPerService,int NumberOfServiceManifest,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,Logger log, String isFederationAllowed,
                        MultivaluedMap<String, String> Params)
	{
	
		int AdmissionControlDecision = allocationOffer.getAdmissionControlDecision();
		
		int NumberOfAcceptedServiceComponents = allocationOffer.getNumberOfAcceptedServiceComponents();
		
		log.info("NumberOfServiceManifest="+NumberOfServiceManifest);
		log.info("NumberOfAcceptedServiceComponents="+NumberOfAcceptedServiceComponents+"/"+numberOfServiceComponents);
		log.info("AdmissionControlDecision="+AdmissionControlDecision);
		
		finalMessage+=" "+NumberOfServiceManifest+"SM"+numberOfServiceComponents+"c"+NumberOfAcceptedServiceComponents;
		
		Decisions decisions = new Decisions();
		
                /*
		//Rejected
		if(NumberOfAcceptedServiceComponents==0)
                //if(AdmissionControlDecision==0)    
			decisions.Rejection(serviceManifest, NumberOfServiceManifest, log);
		
		else if((AdmissionControlDecision==0)&&(isFederationAllowed.contains("no")))
                        decisions.RejectionAsPartialAcceptance(serviceManifest, NumberOfServiceManifest, log);
		
		else if(AdmissionControlDecision==1)
			decisions.Acceptance(serviceManifest, allocationOffer, NumberOfServiceManifest, log, ServiceComponentId_Map_componentId);
			
	 	//Partial Accepted, Partial Rejected
	 	else if(AdmissionControlDecision==0)
			decisions.PartialAcceptance(serviceManifest, allocationOffer, NumberOfServiceManifest, log, ServiceComponentId_Map_componentId); 
	 	
                */
                //if((AdmissionControlDecision==1)&&(NumberOfAcceptedServiceComponents>0))
                if((FederatedVMsPerService==0)&&(NumberOfAcceptedServiceComponents>0))
			decisions.Acceptance(serviceManifest, allocationOffer, NumberOfServiceManifest, log, ServiceComponentId_Map_componentId);
                
                else if((NumberOfAcceptedServiceComponents>0)&&(isFederationAllowed.contains("no")))
                        decisions.RejectionAsPartialAcceptance(serviceManifest, NumberOfServiceManifest, log);
                
                else if(NumberOfAcceptedServiceComponents>0)
			decisions.PartialAcceptance(serviceManifest, allocationOffer, NumberOfServiceManifest, log, ServiceComponentId_Map_componentId,Params);
                
                else
                    decisions.Rejection(serviceManifest, NumberOfServiceManifest, log);
                
		ReturnedServiceManifests.add("serviceManifest",decisions.getReturnedSM());
		
		finalMessage += decisions.getFinalMessage();
		
	}//compineServiceManifestAllocationOffer()
	
}//class