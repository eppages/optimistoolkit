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
import eu.optimis.ac.utils.FileFunctions;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class AppSMAnalyzer 
{
    private static Logger log = Logger.getLogger(AppSMAnalyzer.class);
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        String path = "C:\\Dropbox\\NetBeansWorkspace\\AdmissionControl\\SMAnalyzer\\src\\main\\resources\\manifestSPECWeb2005.xml";
        
        String serviceManifest = FileFunctions.readFileAsStringWithPath(path, log);
        
        SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest,log,true);
        
        System.out.println(smAnalyzer.isFederationAllowed);
        System.out.println(smAnalyzer.numberOfServiceComponents);
        System.out.println(smAnalyzer.componentId_List);
        System.out.println(smAnalyzer.availability.Max_Availability);
        for(int i=0;i<smAnalyzer.smInfo.serviceComponents.size();i++)
        {
            System.out.println("Elastic Vms for :"+smAnalyzer.smInfo.serviceComponents.get(i).getId()+" = "+smAnalyzer.smInfo.serviceComponents.get(i).getElasticVms());
        }//for-i
        
        
        PrintSMInfo.PrintMultipleManifestsInfoToScreen(1,1,
			smAnalyzer.serviceId,smAnalyzer.spId,
			smAnalyzer.isFederationAllowed,smAnalyzer.numberOfServiceComponents,
			smAnalyzer.componentId_List,smAnalyzer.availability.availability_Map,
                        smAnalyzer.smInfo.serviceComponents,
                        smAnalyzer.smInfo.ServiceComponentId_Map_componentId,
                        smAnalyzer.affinityRule,smAnalyzer.antiAffinityRule,
                        smAnalyzer.totalNumberOfCores);
                        
        
        //System.out.println(smAnalyzer.affinityRule.ComponentId_RuleList);
        //System.out.println(smAnalyzer.affinityRule.RuleList);
        //System.out.println(smAnalyzer.antiAffinityRule.ComponentId_RuleList);
        //System.out.println(smAnalyzer.antiAffinityRule.RuleList);
        
        System.out.println( "Buy World!" );
    }//main
    
}//class
