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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class SMsAnalyzer {
	
	public MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
	
	public ArrayList<ArrayList<String>> componentId_List = new ArrayList<ArrayList<String>>();
	
	public ArrayList<MultivaluedMap<String, String>> componentId_Map_ServiceComponentId_List 
	= new ArrayList<MultivaluedMap<String, String>>();
	
	public ArrayList<MultivaluedMap<String, String>> ServiceComponentId_Map_componentId_List 
	= new ArrayList<MultivaluedMap<String, String>>();
        
	public SMsAnalyzer(MultivaluedMap<String, String> Params,Logger log, Boolean DisplayAllLogs)
	{
		int numberOfSMs = Params.get("serviceManifest").size();
		
                PrintSMInfo.PrintNumberOfSMsToLog(numberOfSMs, log);
                
		for(int i=0;i<Params.get("serviceManifest").size();i++)
		{				
			String serviceManifest=Params.get("serviceManifest").get(i);
			
			SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest,log,DisplayAllLogs);
			
			formParams.add("serviceManifest", serviceManifest);
			formParams.add("serviceId", smAnalyzer.serviceId);
			formParams.add("spId", smAnalyzer.spId);
			formParams.add("isFederationAllowed", smAnalyzer.isFederationAllowed);
			formParams.add("numberOfServiceComponents", Integer.toString(smAnalyzer.numberOfServiceComponents));
            
                        componentId_List.add(smAnalyzer.componentId_List);
            
			componentId_Map_ServiceComponentId_List.add(smAnalyzer.smInfo.componentId_Map_ServiceComponentId);
			ServiceComponentId_Map_componentId_List.add(smAnalyzer.smInfo.ServiceComponentId_Map_componentId);
			
                        PrintSMInfo.PrintMultipleManifestsInfoToLog(numberOfSMs,(i+1),
			smAnalyzer.serviceId,smAnalyzer.spId,
			smAnalyzer.isFederationAllowed,smAnalyzer.numberOfServiceComponents,
			smAnalyzer.componentId_List,smAnalyzer.availability.availability_Map,
                        smAnalyzer.smInfo.serviceComponents,
                        smAnalyzer.smInfo.ServiceComponentId_Map_componentId,
                        smAnalyzer.affinityRule,smAnalyzer.antiAffinityRule,
                        smAnalyzer.totalNumberOfCores,log);
		}//for-i
		
	}//constructor
	
        public SMsAnalyzer(MultivaluedMap<String, String> Params)
	{
		int numberOfSMs = Params.get("serviceManifest").size();
		
                PrintSMInfo.PrintNumberOfSMsToScreen(numberOfSMs);
                
		for(int i=0;i<Params.get("serviceManifest").size();i++)
		{				
			String serviceManifest=Params.get("serviceManifest").get(i);
			
			SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
			
			formParams.add("serviceManifest", serviceManifest);
			formParams.add("serviceId", smAnalyzer.serviceId);
			formParams.add("spId", smAnalyzer.spId);
			formParams.add("isFederationAllowed", smAnalyzer.isFederationAllowed);
			formParams.add("numberOfServiceComponents", Integer.toString(smAnalyzer.numberOfServiceComponents));
                        for(int j=0;j<smAnalyzer.componentId_List.size();j++)
                            formParams.add("componentId",smAnalyzer.componentId_List.get(j));
                       
                        PrintSMInfo.PrintMultipleManifestsInfoToScreen(numberOfSMs,(i+1),
			smAnalyzer.serviceId,smAnalyzer.spId,
			smAnalyzer.isFederationAllowed,smAnalyzer.numberOfServiceComponents,
			smAnalyzer.componentId_List);
		}//for-i
		
	}//constructor
}//class
