/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.smInfoCollector;

import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class AllServiceManifestsInfo
{   
        public LinkedList<SMAnalyzer> smAnalyzerList = new LinkedList<SMAnalyzer>();
    
	public LinkedList<String> AvailabilityList = new LinkedList<String>();
	
	public LinkedList<String> isFederationAllowedList = new LinkedList<String>();
	
	public LinkedList<String> NumberOfServiceComponentsList = new LinkedList<String>();
	
        public LinkedList<LinkedList<String>> AvailabilityPerComponentList = new LinkedList<LinkedList<String>>();
        
	public LinkedList<LinkedList<String>> BasicList = new LinkedList<LinkedList<String>>();
	
	public LinkedList<LinkedList<String>> ElasticList = new LinkedList<LinkedList<String>>();
	
	public LinkedList<LinkedList<String>> MemoryPerComponentList = new LinkedList<LinkedList<String>>();
	
        public LinkedList<LinkedList<String>> TableList = new LinkedList<LinkedList<String>>();
        
	public LinkedList<LinkedList<String>> AffinityConstraintsList = new LinkedList<LinkedList<String>>();
	
        public LinkedList<LinkedList<String>> AntiAffinityConstraintsList = new LinkedList<LinkedList<String>>();
        
	public LinkedList<LinkedList<String>> ServiceComponentList = new LinkedList<LinkedList<String>>();
	
	public LinkedList<LinkedList<String>> IdList = new LinkedList<LinkedList<String>>();
	
	public ArrayList<ArrayList<ArrayList<String>>> affinityRule_List = new ArrayList<ArrayList<ArrayList<String>>>();
	
        public ArrayList<ArrayList<ArrayList<String>>> antiAffinityRule_List = new ArrayList<ArrayList<ArrayList<String>>>();
        
	public int max_numberOfComponents=0;
	public int max_numberOfServices=0;
	
        private static String doNotFederate = "no";
        
	public AllServiceManifestsInfo(MultivaluedMap<String, String> formParams,Logger log)
	{
		for(int i=0;i<formParams.get("serviceManifest").size();i++)
	    {           
                        String serviceManifest = formParams.get("serviceManifest").get(i);
	    		
                        log.info("Ready to Exctract SMInfo from manifest number : "+(i+1));
	    		
                        SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest, log,true);
                        smAnalyzerList.add(smAnalyzer);
                        
                        log.info("Finish Exctraction of SMInfo from manifest number : "+(i+1));
                        
	    		int availability=smAnalyzer.availability.Max_Availability;
	    		AvailabilityList.addLast("0."+Integer.toString(availability));
	    		
	    		String IsFederationAllowed=smAnalyzer.isFederationAllowed;
                        IsFederationAllowed = doNotFederate;
                        if(formParams.containsKey("isFederationAllowed"))
                            log.info("contains key isFerederationAllowed with value : "+formParams.get("isFederationAllowed").get(i));
                        if((formParams.containsKey("isFederationAllowed"))&&(formParams.get("isFederationAllowed").get(i).contains("No")))
                            IsFederationAllowed = "yes";
                        log.info("doNotFederate Flag for manifest number :"+(i+1)+" : "+IsFederationAllowed);
	    		isFederationAllowedList.addLast(IsFederationAllowed);
                           
	    		int numberOfServiceComponents=smAnalyzer.numberOfServiceComponents;
	    		NumberOfServiceComponentsList.addLast(Integer.toString(numberOfServiceComponents));
	    		
                        ServiceComponentsInfoAsLists sci_asList = new ServiceComponentsInfoAsLists(smAnalyzer.smInfo,smAnalyzer.availability);
                        
                        AvailabilityPerComponentList.addLast(sci_asList.ListOfAvailabilities);
                        
	    		BasicList.addLast(sci_asList.Basic);
	    		
	    		ElasticList.addLast(sci_asList.Elastic);
	    		
	    		TableList.addLast(sci_asList.Table);
	    		
                        MemoryPerComponentList.addLast(sci_asList.MemoryPerComponent);
                        
	    		AffinityConstraintsList.addLast(sci_asList.AffinityConstraints);
	    		log.info("Affinity : "+sci_asList.AffinityConstraints);
                        AntiAffinityConstraintsList.addLast(sci_asList.AntiAffinityConstraints);
                        log.info("AntiAffinity : "+sci_asList.AntiAffinityConstraints);
	    		ServiceComponentList.addLast(sci_asList.ServiceComponents);
	    		
	    		IdList.addLast(sci_asList.ListOfId);
	    		
                        affinityRule_List.add(smAnalyzer.affinityRule.AffinityRuleList);
	    		
                        antiAffinityRule_List.add(smAnalyzer.antiAffinityRule.AffinityRuleList);
                        
                        log.info("Service Manifest Info gothered");
		}//for
	    
		max_numberOfComponents=MaxServiceComponents();
		max_numberOfServices=formParams.get("serviceManifest").size();
	}//Constructor
	
	private int MaxServiceComponents()
        {
            int max=0;
            for(int i=0;i<NumberOfServiceComponentsList.size();i++)
            {
                String str=NumberOfServiceComponentsList.get(i);
			
		int value = Integer.parseInt(str);
			
		if(max<value)
                    max=value;	
			
            }//for-i
    	
            return max;
        }//MaxServiceComponents
    
}//AllServiceManifestInfo
