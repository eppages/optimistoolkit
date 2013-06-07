/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.produceSM;

import java.util.ArrayList;
import java.util.Arrays;

public class ProduceSMfor_DecisionTest {
    
    private static String GetSM_for_DecisionTest(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List,String serviceId)
    {
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,serviceId,Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_for_DecisionTest()
    
    private static String GetSM_for_Accepted_Test()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
        return GetSM_for_DecisionTest(
             Initial_List, UpperBound_List,"ACTestsDecisionAccepted");
    }//GetSM_for_Accepted_Test()
    
    private static String GetSM_for_Rejected_Test()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1000,1000));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(500,500));
        
        return GetSM_for_DecisionTest(
             Initial_List, UpperBound_List,"ACTestsDecisionRejected");
    }//GetSM_for_Rejected_Test()
    
    private static String GetSM_for_Partial_Test()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,100));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,100));
        
        return GetSM_for_DecisionTest(
             Initial_List, UpperBound_List,"ACTestsDecisionPartial");
    }//GetSM_for_Partial_Test()
    
    public static void WriteSMs_for_Decision_Test()
    {
       Produce_ServiceManifest pr = new Produce_ServiceManifest(); 
       pr.writeToFile(GetSM_for_Accepted_Test(),"SM_1c2","C:\\"); 
       pr.writeToFile(GetSM_for_Rejected_Test(),"BigSM_1c2","C:\\"); 
       pr.writeToFile(GetSM_for_Partial_Test(),"PartialSM_1c2","C:\\"); 
       
    }////WriteSMs_for_Decision_Test()
}//class
