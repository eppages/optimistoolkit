/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.analyzeAllocationOffer;

import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class printAllocationOfferInfo
{
	private String Message="";
		
	private static Logger log ;
	
	public String FinalMessage="";
	
	public printAllocationOfferInfo(AllocationOfferInfoAsList allocationOfferList,MultivaluedMap<String, String> Params,Logger the_log)
	{
		log = the_log;
		
		Message= allocationOfferList.Number_Of_Allocation_Offer+" allocationOffer ";
		print(Message);
		
		for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
		{
			AllocationOfferInfo allocationOfferInfo = allocationOfferList.AllocationOfferList.get(i);
			
			String numberOfServiceComponents = Params.get("numberOfServiceComponents").get(i);
			
			print_AllocationOfferInfo(allocationOfferInfo,numberOfServiceComponents);
			
			Message="------------ "+(i+1)+"/"+allocationOfferList.Number_Of_Allocation_Offer+" -----";
			print(Message);
		}//for-i ,each AllocationOffer		
		
	}//Constructor
	
	private void print_AllocationOfferInfo(AllocationOfferInfo allocationOffer,String numberOfServiceComponents)
	{
		FinalMessage = allocationOffer.getIP_Id();
				
		Message="IP_ID : "+allocationOffer.getIP_Id();print(Message);
		Message="ServiceID : "+allocationOffer.getService_Id();print(Message);
		Message="AdmissionControlDecision : "+allocationOffer.getAdmissionControlDecision();print(Message);
		Message="NumberOfAcceptedServiceComponents : "+allocationOffer.getNumberOfAcceptedServiceComponents()+"/"+numberOfServiceComponents;print(Message);
		
		for(int j=0;j<allocationOffer.getNumberOfAcceptedServiceComponents();j++)
		{
			String AcceptedComponentId=allocationOffer.getListOfAccepted_ServiceComponents().get(j);
			Message="AcceptedComponentId : "+AcceptedComponentId;print(Message);				
			
                        Message=" Basic : ";
				for(int x=0;x<allocationOffer.BasicPhysicalHost.get(AcceptedComponentId).size();x++)
					Message+=allocationOffer.BasicPhysicalHost.get(AcceptedComponentId).get(x)+" ";
				print(Message);
                        
			if(allocationOffer.ElasticPhysicalHost.get(AcceptedComponentId)!=null)
			{
				Message=" Elastic : ";
				for(int x=0;x<allocationOffer.ElasticPhysicalHost.get(AcceptedComponentId).size();x++)
					Message+=allocationOffer.ElasticPhysicalHost.get(AcceptedComponentId).get(x)+" ";
				print(Message);
			}
			
		}//for-j
		
		//Message+=allocationOffer.getPricePlan().toString()+"\n";
		//Message+=allocationOffer.getRisk()+"\n";
		//Message+="allocation_pattern  : \n";
		//Message+=allocationOffer.getAllocationPatternAsString()+"\n";
	}//print_AllocationOfferInfo()
	
	private void print(String msg)
	{
		//System.out.println(msg);
		log.info(msg);
	}//print()
}//class