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

public class GetSMforAntiAffinityRuleTesting {
    
    public static ArrayList<Integer> getInitial_List_forHighAntiAffinityRuleTest()
    {
        
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1));
         
        return Initial_List;
    }//getInitial_List_forHighAntiAffinityRuleTest()
    
    public static ArrayList<Integer> getUpperBound_List_forHighAntiAffinityRuleTest()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(5,1,1,1));
        
        return UpperBound_List;
         
    }//getUpperBound_List_forHighAntiAffinityRuleTest()
    
     public static ArrayList<Integer> getInitial_List_forHMediumAntiAffinityRuleTest()
    {
        
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1));
         
        return Initial_List;
    }//getInitial_List_forMediumAntiAffinityRuleTest()
    
    public static ArrayList<Integer> getUpperBound_List_forMediumAntiAffinityRuleTest()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1));
        
        return UpperBound_List;
         
    }//getUpperBound_List_forMediumAntiAffinityRuleTest()
    
    public static ArrayList<Integer> getInitial_List_forHMediumAntiAffinityRuleTest_BigElasticBasic()
    {
        
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(10,1,1,1));
         
        return Initial_List;
    }//getInitial_List_forMediumAntiAffinityRuleTest()
    
    public static ArrayList<Integer> getUpperBound_List_forMediumAntiAffinityRuleTest_BigElasticBasic()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(15,1,1,1));
        
        return UpperBound_List;
         
    }//getUpperBound_List_forMediumAntiAffinityRuleTest()
    
    public static String forMediumAntiAffinityRuleTest(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List)
    {
         
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         
         String affinityRules = "1,2,Low";
        
         String antiAffinityRules = "1,2,Medium";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsMediumAntiAffinityRule",Boolean.FALSE,
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
         
    }//forMediumAntiAffinityRuleTest()
    
    public static String forHighAntiAffinityRuleTest(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List)
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         
         String affinityRules = "1,2,Low";
        
         String antiAffinityRules = "1,2,High";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsHighAntiAffinityRule",Boolean.FALSE,
                 "10",
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
         
    }//forHighAntiAffinityRuleTest()
    
}//class
