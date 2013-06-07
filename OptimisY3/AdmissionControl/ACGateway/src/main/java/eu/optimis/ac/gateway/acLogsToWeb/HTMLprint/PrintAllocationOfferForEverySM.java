/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb.HTMLprint;

import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;

import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfo;
import eu.optimis.ac.gateway.analyzeAllocationOffer.AllocationOfferInfoAsList;
import eu.optimis.ac.gateway.utils.Date_and_Time;

import org.apache.log4j.Logger;

public class PrintAllocationOfferForEverySM
{
	public String Position11 = "";
	public ArrayList<String> Header_List = new ArrayList<String>();
	public ArrayList<String> Tag_List = new ArrayList<String>();
	public ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
	
        public String timeStamp = null;
        
	private ArrayList<String> IP_ID_List = new ArrayList<String>();
	public ArrayList<String> ServiceID_List = new ArrayList<String>();
	private ArrayList<String> AdmissionControlDecision_List = new ArrayList<String>();
	private ArrayList<String> NumberOfAcceptedServiceComponents_List = new ArrayList<String>();
	private ArrayList<String> Decision_List = new ArrayList<String>();
	private ArrayList<String> TimeStamp_List = new ArrayList<String>();
	
	public PrintAllocationOfferForEverySM(AllocationOfferInfoAsList allocationOfferList,
			MultivaluedMap<String, String> Params,Logger log)
	{
		this.setHeader_List(allocationOfferList);
		this.setTag_List();
		
		this.set_Lists(allocationOfferList,Params);
		
		for(int i=0;i<Header_List.size();i++)
		{
			ArrayList<String> Row_List = new ArrayList<String>();
			
			Row_List.add(IP_ID_List.get(i));
			Row_List.add(ServiceID_List.get(i));
			Row_List.add(AdmissionControlDecision_List.get(i));
			Row_List.add(NumberOfAcceptedServiceComponents_List.get(i));
			Row_List.add(Decision_List.get(i));
			Row_List.add(TimeStamp_List.get(i));
			
                        if(i==0)timeStamp = TimeStamp_List.get(i);
                        
			Info_List.add(Row_List);
		}//for-i , each row
		
	}//constructor
	
	private void set_Lists(AllocationOfferInfoAsList allocationOfferList,
			MultivaluedMap<String, String> Params)
	{
		for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
		{
			AllocationOfferInfo allocationOffer = allocationOfferList.AllocationOfferList.get(i);
			
			String IP_ID=allocationOffer.getIP_Id();
			IP_ID_List.add(IP_ID);
			
			String ServiceID=allocationOffer.getService_Id();
			ServiceID_List.add(ServiceID);
			
			String ACdecision=Integer.toString(allocationOffer.getAdmissionControlDecision());
			AdmissionControlDecision_List.add(ACdecision);
			
			String numberOfServiceComponents = Params.get("numberOfServiceComponents").get(i);
			
			String numberOfAccepted=Integer.toString(allocationOffer.getNumberOfAcceptedServiceComponents())+"/"+numberOfServiceComponents;
			NumberOfAcceptedServiceComponents_List.add(numberOfAccepted);
			
			Decision_List.add(getAdmissionControlDecision(numberOfServiceComponents,allocationOffer.getNumberOfAcceptedServiceComponents()));
			
			TimeStamp_List.add(Date_and_Time.getDate_Time());
			
		}//for -i,each service Manifest
		
	}//set_Lists()
	
	private void setTag_List()
	{
		Tag_List.add("IP_ID");
		Tag_List.add("ServiceID");
		Tag_List.add("Model Decision");
		Tag_List.add("NumberOfAcceptedServiceComponents");
		Tag_List.add("AdmissionControlDecision");
		Tag_List.add("TimeStamp");
		
	}//setTag_List()
	
	private void setHeader_List(AllocationOfferInfoAsList allocationOfferList)
	{
		for(int i=0;i<allocationOfferList.Number_Of_Allocation_Offer;i++)
		{
			if(allocationOfferList.Number_Of_Allocation_Offer==1)
				Header_List.add("service Manifest");
			else
				Header_List.add("service Manifest -"+(i+1)+"-");
		}//for -i,each service Manifest
		
	}//setHeader_List()
	
	private String getAdmissionControlDecision(String NumberOfServiceComponents,int numberOfAcceptedServiceComponents)
	{
		int numberOfServiceComponents = Integer.parseInt(NumberOfServiceComponents);
		
		if(numberOfAcceptedServiceComponents==0)return "Rejected";
		else if(numberOfServiceComponents==numberOfAcceptedServiceComponents) return "Accepted";
		else if(numberOfServiceComponents>numberOfAcceptedServiceComponents) return "Partial Accepted";
		else return "Decision Error";
	}//getAdmissionControlDecision()
	
}//class