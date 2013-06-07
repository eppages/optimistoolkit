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
import eu.optimis.ac.test.produceSM.Produce_oneSM_asString;
import java.util.ArrayList;

public class GetSMforAffinityTesting {
    
    public static String withAllAffinityLow()
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>();
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>();
        
         affinityConstraints_List.add(0);affinityConstraints_List.add(0);
         affinityConstraints_List.add(0);affinityConstraints_List.add(0);        
         
         antiAffinityConstraints_List.add(0);antiAffinityConstraints_List.add(0);
         antiAffinityConstraints_List.add(0);antiAffinityConstraints_List.add(0);
        
         String affinityRules = "1,2,Low";
        
         String antiAffinityRules = "1,2,Low";
        
         String serviceManifestAsString = Produce_oneSM_asString.Produce_oneSM("AAA",4, Boolean.FALSE,
                affinityConstraints_List,antiAffinityConstraints_List,affinityRules,antiAffinityRules);
         
         SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
         
         PrintAffinity.printAffinityOfSM(manifestInfo.componentId_List,
             manifestInfo.smInfo.affinityConstraints_Map,
             manifestInfo.smInfo.antiAffinityConstraints_Map,
             manifestInfo.affinityRule.AffinityRuleList,
             manifestInfo.antiAffinityRule.AffinityRuleList);
         
         return serviceManifestAsString;
         
    }//withAllAffinityLow()
    
}//class
