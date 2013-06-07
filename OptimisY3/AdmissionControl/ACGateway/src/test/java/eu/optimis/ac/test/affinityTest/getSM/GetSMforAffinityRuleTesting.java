/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.affinityTest.getSM;

import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.print.PrintAffinity;
import eu.optimis.ac.test.produceSM.ProduceManifest;
import java.util.ArrayList;
import java.util.Arrays;


public class GetSMforAffinityRuleTesting {
    
    public static String forMediumAffinityRuleTest()
    {
    	
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(3,3,2,2));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1));
         
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         
         String antiAffinityRules = "1,2,Low";
        
         String affinityRules = "1,2,Medium";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsMediumAffinityRule",Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
        
         SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
         
         PrintAffinity.printAffinityOfSM(manifestInfo.componentId_List,
             manifestInfo.smInfo.affinityConstraints_Map,
             manifestInfo.smInfo.antiAffinityConstraints_Map,
             manifestInfo.affinityRule.AffinityRuleList,
             manifestInfo.antiAffinityRule.AffinityRuleList);
         
         return serviceManifestAsString;

         
    }//forMediumAffinityRuleTest()
    
    public static String forHighAffinityRuleTest()
    {
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(3,2,1,1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1));
         
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         
         String antiAffinityRules = "1,2,Low";
        
         String affinityRules = "1,2,High";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsHighAffinityRule",Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
        
         SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
         
         PrintAffinity.printAffinityOfSM(manifestInfo.componentId_List,
             manifestInfo.smInfo.affinityConstraints_Map,
             manifestInfo.smInfo.antiAffinityConstraints_Map,
             manifestInfo.affinityRule.AffinityRuleList,
             manifestInfo.antiAffinityRule.AffinityRuleList);
         
         return serviceManifestAsString;
         
    }//forHighAffinityRuleTest()
    
}//class
