/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.smanalyzer;

import eu.optimis.ac.smanalyzer.smInfo.AffinityRule;
import eu.optimis.ac.smanalyzer.smInfo.ServiceComponentInfo;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class PrintSMInfo {

	private static ArrayList<String> printSMInfo(String serviceId,String spId,String isFederationAllowed,
                        int numberOfServiceComponents,
			ArrayList<String> componentId_List,
                        MultivaluedMap<String, String> availability_Map,
			ArrayList<ServiceComponentInfo> serviceComponents,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,
                        AffinityRule affinityRule,AffinityRule antiAffinityRule,
                        int totalNumberOfCores)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		String line = "serviceId : "+serviceId;
		list.add(line);
		
		line = "spId : "+spId;
		list.add(line);
		
		line = "isFederationAllowed : "+isFederationAllowed;
		list.add(line);
		
		line = "numberOfServiceComponents : "+numberOfServiceComponents;
		list.add(line);
		
		line = "Components : ";
		for(int i=0;i<componentId_List.size();i++)
                {
			line =line+componentId_List.get(i);
                        
                        if(i!=componentId_List.size()-1)
                            line = line + ", ";
                }//for-i
		list.add(line);
		
                line = "totalNumberOfCores : "+totalNumberOfCores;
		list.add(line);
                
		if(availability_Map.isEmpty())
		{
			line ="Availability Info are empty";
			list.add(line);
		}
		else
			for(int i=0;i<componentId_List.size();i++)
			{
				String componentId=componentId_List.get(i);
				
                                
                                if(availability_Map.containsKey(componentId)==false)
					line = "Availability("+componentId+") = -";
				else
				{
					String availability_value = availability_Map.get(componentId).get(0);
				
					line = "Availability("+componentId+") = "+availability_value;
				}
				
				list.add(line);
			}//int-i
		
		for(int i=0;i<serviceComponents.size();i++)
		{
			ServiceComponentInfo serviceComponent = serviceComponents.get(i);
			
			String ServiceComponentId = serviceComponent.getId();
			String componentId = ServiceComponentId_Map_componentId.get(ServiceComponentId).get(0); 
			
			line = componentId+":";
			list.add(line);
			
			line = "	ServiceComponentId:"+ServiceComponentId;
			list.add(line);
			
			line = "	BaseVms:"+serviceComponent.getBaseVms();
			list.add(line);
			
			line = "	ElasticVms:"+serviceComponent.getElasticVms();
			list.add(line);
			
			line = "	VirtualCpus:"+serviceComponent.getVirtualCpus();
			list.add(line);
			
                        line = "	memoryInMBs:"+serviceComponent.getMemoryInMBs();
			list.add(line);
                        
			line = "	AffinityConstraints:"+serviceComponent.getAffinityConstraints();
			list.add(line);
		}//for-i
		
		line = "Affinity : "+affinityRule.AffinityRuleList;
		list.add(line);
                line = "AntiAffinity : "+antiAffinityRule.AffinityRuleList;
		list.add(line);
		
		return list;
	}//printSMInfo()
	
        private static ArrayList<String> printSMInfo(String serviceId,String spId,String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		String line = "serviceId : "+serviceId;
		list.add(line);
		
		line = "spId : "+spId;
		list.add(line);
		
		line = "isFederationAllowed : "+isFederationAllowed;
		list.add(line);
		
		line = "numberOfServiceComponents : "+numberOfServiceComponents;
		list.add(line);
		
		line = "Components : ";
		for(int i=0;i<componentId_List.size();i++)
                {
			line =line+componentId_List.get(i);
                        
                        if(i!=componentId_List.size()-1)
                            line = line + ", ";
                }//for-i
		list.add(line);
		
		
		return list;
	}//printSMInfo()
	
        
	public static String printSMtoShortHand(String isFederationAllowed,int numberOfServiceComponents)
	{
		String Result = "SM"+numberOfServiceComponents+"c"+"F"+isFederationAllowed+" ";
		
		return Result;
	}//printSMtoShortHand()
	
	private static void printSMInfoToScreen(ArrayList<String> list)
	{
		for(int i=0;i<list.size();i++)
			System.out.println("		"+list.get(i));
		
	}//printSMInfoToScreen()
        
        private static void printSMInfoToLog(ArrayList<String> list,Logger log)
	{
		for(int i=0;i<list.size();i++)
			log.info(list.get(i));
		
	}//printSMInfoToLog()
	
        public static void PrintMultipleManifestsInfoToLog(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List,MultivaluedMap<String, String> availability_Map,
			ArrayList<ServiceComponentInfo> serviceComponents,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,
                        AffinityRule affinityRule,AffinityRule antiAffinityRule,
                        int totalNumberOfCores,Logger log)
	{
		
		printSMInfoToLog(PrintMultipleManifestsInfo(numberOfSMs,currentSMnumber,
			serviceId,spId,
			isFederationAllowed,numberOfServiceComponents,
			componentId_List,availability_Map,
			serviceComponents,
			ServiceComponentId_Map_componentId,affinityRule,antiAffinityRule,totalNumberOfCores),
                        log);
                        
	}//PrintMultipleManifestsInfoToLog()
        
	public static void PrintMultipleManifestsInfoToScreen(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List,MultivaluedMap<String, String> availability_Map,
			ArrayList<ServiceComponentInfo> serviceComponents,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,
                        AffinityRule affinityRule,AffinityRule antiAffinityRule,
                        int totalNumberOfCores)
	{
		printSMInfoToScreen(PrintMultipleManifestsInfo(numberOfSMs,currentSMnumber,
			serviceId,spId,
			isFederationAllowed,numberOfServiceComponents,
			componentId_List,availability_Map,
			serviceComponents,
			ServiceComponentId_Map_componentId,affinityRule,antiAffinityRule,totalNumberOfCores));
                        
	}//PrintMultipleManifestsInfoToScreen()
        
        private static ArrayList<String> PrintMultipleManifestsInfo(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List,MultivaluedMap<String, String> availability_Map,
			ArrayList<ServiceComponentInfo> serviceComponents,
			MultivaluedMap<String, String> ServiceComponentId_Map_componentId,
                        AffinityRule affinityRule,AffinityRule antiAffinityRule,int totalNumberOfCores)
	{
		
                ArrayList<String> list = new ArrayList<String>();
                
                String msg = "printing info of SM "+currentSMnumber+"/"+numberOfSMs+" :";
                list.add(msg);
                
		ArrayList<String> list1 = PrintSMInfo.printSMInfo(serviceId,spId,isFederationAllowed,numberOfServiceComponents,
				componentId_List,availability_Map,
				serviceComponents,
				ServiceComponentId_Map_componentId,
                                affinityRule,antiAffinityRule,totalNumberOfCores);
		
                list.addAll(list1);
		
                msg = "-----"+currentSMnumber+"/"+numberOfSMs+"-----";
                list.add(msg);
                
                return list;
	}//PrintMultipleManifestsInfo()
        
       private static ArrayList<String> PrintMultipleManifestsInfo(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List)
	{
		ArrayList<String> list = new ArrayList<String>();
                
                String msg = "printing info of SM "+currentSMnumber+"/"+numberOfSMs+" :";
                list.add(msg);
		
		ArrayList<String> list1 = PrintSMInfo.printSMInfo(serviceId,spId,isFederationAllowed,numberOfServiceComponents,
				componentId_List);
		
		list.addAll(list1);
		
                msg = "-----"+currentSMnumber+"/"+numberOfSMs+"-----";
                list.add(msg);
                
                msg = "";
                list.add(msg);
		
                return list;
		
	}//PrintMultipleManifestsInfo()
       
       public static void PrintMultipleManifestsInfoToScreen(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List)
	{
		printSMInfoToScreen(PrintMultipleManifestsInfo(numberOfSMs,currentSMnumber,
			serviceId,spId,
			isFederationAllowed,numberOfServiceComponents,
			componentId_List));
		
	}//PrintMultipleManifestsInfo()
       
       public static void PrintMultipleManifestsInfoToLog(int numberOfSMs,int currentSMnumber,
			String serviceId,String spId,
			String isFederationAllowed,int numberOfServiceComponents,
			ArrayList<String> componentId_List,Logger log)
	{
		printSMInfoToLog(PrintMultipleManifestsInfo(numberOfSMs,currentSMnumber,
			serviceId,spId,
			isFederationAllowed,numberOfServiceComponents,
			componentId_List),log);
		
	}//PrintMultipleManifestsInfo()
        
	public static void PrintNumberOfSMsToScreen(int numberOfSMS)
	{
		System.out.println("number Of ServiceManifests: "+numberOfSMS);
		System.out.println();
		
	}//PrintNumberOfSMsToScreen(int numberOfSms)
	
        public static void PrintNumberOfSMsToLog(int numberOfSMS,Logger log)
	{
		log.info("number Of ServiceManifests: "+numberOfSMS);
		log.info("");
		
	}//PrintNumberOfSMsToLog(int numberOfSms,Logger log)
}//Class
