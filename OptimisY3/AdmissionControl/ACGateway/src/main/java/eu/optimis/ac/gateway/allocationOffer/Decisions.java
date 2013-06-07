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

import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfo;
import eu.optimis.ac.gateway.serviceManifestFunctions.AttachAllocationOfferToServiceManifest;
import eu.optimis.ac.gateway.serviceManifestFunctions.SetAdmissionControlDecision;

public class Decisions {
	
	private String finalMessage;
	
	public String getFinalMessage() {
		return finalMessage;
	}

	public void setFinalMessage(String finalMessage) {
		this.finalMessage = finalMessage;
	}

	private String ReturnedSM;
	
	public String getReturnedSM() {
		return ReturnedSM;
	}

	public void setReturnedSM(String returnedSM) {
		ReturnedSM = returnedSM;
	}

	public Decisions()
	{
		
	}//constructor
	
	public void Rejection(String serviceManifest,int NumberOfServiceManifest,Logger log)
	{
		String rejectedServiceManifest = setAdmissionControlDecision(serviceManifest,"Rejected",log);
		
 		log.info("---Rejected "+NumberOfServiceManifest);
 	
 		this.setFinalMessage("r");
 		
 		this.setReturnedSM( rejectedServiceManifest);
 		
	}//Rejection()
        
        public void RejectionAsPartialAcceptance(String serviceManifest,int NumberOfServiceManifest,Logger log)
	{
		String rejectedServiceManifest = setAdmissionControlDecision(serviceManifest,"Partial accepted",log);
		
 		log.info("---RejectionAsPartialAcceptance "+NumberOfServiceManifest);
 	
 		this.setFinalMessage("rp");
 		
 		this.setReturnedSM( rejectedServiceManifest);
 		
	}//RejectionAsPartialAcceptance()
	
	public void Acceptance(String serviceManifest,AllocationOfferInfo allocationOffer,int NumberOfServiceManifest,Logger log,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId)
	{
 		
		String riskValue = allocationOffer.getRisk();
		XmlObject costValue = allocationOffer.getPricePlan();
 		
		ArrayList<String> ListOfAccepted_Components = 
				correspondAcceptedVirtualSystemIdToComponentId(allocationOffer.getListOfAccepted_ServiceComponents(),
						ServiceComponentId_Map_componentId);
		
		String acceptedServiceManifestWithACdecision = setAdmissionControlDecision(serviceManifest,"Accepted",log);
		
		log.info("ListOfAccepted_Components="+ListOfAccepted_Components);
		
		String acceptedReturnedServiceManifest = addAllocationOfferToServiceManifest
				(acceptedServiceManifestWithACdecision,
						allocationOffer.getListOfAccepted_ServiceComponents(),
						ListOfAccepted_Components,
				riskValue,costValue,
				allocationOffer.BasicPhysicalHost,
				allocationOffer.ElasticPhysicalHost,log);
		 
 		System.out.println("---Accepted "+NumberOfServiceManifest);
 		log.info("---Accepted "+NumberOfServiceManifest);
 		
 		this.setFinalMessage("a");
 		
 		this.setReturnedSM( acceptedReturnedServiceManifest);
 		
	}//Acceptance()
	
	public void PartialAcceptance(String serviceManifest,AllocationOfferInfo allocationOffer,int NumberOfServiceManifest,Logger log,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,MultivaluedMap<String, String> Params)
	{
		String riskValue = allocationOffer.getRisk();
		XmlObject costValue = allocationOffer.getPricePlan();
 		
		ArrayList<String> ListOfAccepted_Components = 
				correspondAcceptedVirtualSystemIdToComponentId(allocationOffer.getListOfAccepted_ServiceComponents(),
						ServiceComponentId_Map_componentId);
		
		//AppendContentTo_AC_GUI_logs.AppendContentToMonitoringAClogsFile("Partial Acceptance ==> Federation !");
 		
 		String IP_ID =  allocationOffer.getIP_Id();
 	
 		PartialAccepted_ChangeOriginalSM pasm = new PartialAccepted_ChangeOriginalSM(serviceManifest,ListOfAccepted_Components,riskValue,costValue,allocationOffer.getListOfAccepted_ServiceComponents(),allocationOffer.BasicPhysicalHost,allocationOffer.ElasticPhysicalHost,IP_ID,log,NumberOfServiceManifest,Params);
 		//PartialAccepted_withoutChangeOriginalSM pasm = new PartialAccepted_withoutChangeOriginalSM(serviceManifest,ListOfAccepted_Components,riskValue,costValue,allocationOffer.getListOfAccepted_ServiceComponents(),allocationOffer.BasicPhysicalHost,allocationOffer.ElasticPhysicalHost,IP_ID,log,NumberOfServiceManifest);
 
 		System.out.println("---PartialAccepted "+NumberOfServiceManifest);
 		log.info("---PartialAccepted "+NumberOfServiceManifest); 		

 		this.setFinalMessage(pasm.finalMessage);
 		
 		this.setReturnedSM(pasm.partialAcceptedReturnedServiceManifest);
 		
	}//PartialAcceptance()
	
	public static String addAllocationOfferToServiceManifest(String serviceManifest,ArrayList<String> acceptedVirtualSystemComponentsIDAsList,ArrayList<String> acceptedComponentsIDAsList,
			String riskValue,XmlObject costValue,MultivaluedMap<String, String> basicPhysicalHost,MultivaluedMap<String, String> elasticPhysicalHost,Logger log)
	{
		AttachAllocationOfferToServiceManifest attach = new AttachAllocationOfferToServiceManifest(serviceManifest,acceptedVirtualSystemComponentsIDAsList,acceptedComponentsIDAsList,
				riskValue,costValue,basicPhysicalHost,elasticPhysicalHost,log);
		
		return attach.serviceManifestWithAllocationOffer;
	}//addAllocationOfferToServiceManifest()
	
	public static String setAdmissionControlDecision(String serviceManifest,String ACdecision,Logger log)
	{
		SetAdmissionControlDecision decision = new SetAdmissionControlDecision(serviceManifest,ACdecision,log);
		
		return decision.serviceManifestWithACdecision;
	}//setAdmissionControlDecision()
	
	public static ArrayList<String> correspondAcceptedVirtualSystemIdToComponentId
	(ArrayList<String> ListOfAcceptedVirtualSystemComponents,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId)
	{
		ArrayList<String> ListOfAccepted_Components = new ArrayList<String>();
		
		for(int i=0;i<ListOfAcceptedVirtualSystemComponents.size();i++)
		{
			String VirtualSystemId = ListOfAcceptedVirtualSystemComponents.get(i);
			
			String ComponentId = ServiceComponentId_Map_componentId.get(VirtualSystemId).get(0);
			
			ListOfAccepted_Components.add(ComponentId);
			
		}//for-i, each VirtualSystemId == AcceptedComponentId
		
		return ListOfAccepted_Components;
	}//correspondAcceptedVirtualSystemIdToComponentId()
	
}//class
