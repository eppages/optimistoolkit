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

public class ProduceSMfor_AvailabilityPerComponentTest {
    
    private static String GetSM_c1()
    {
        
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1));
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0));
        
         String affinityRules = "";
         String antiAffinityRules = "";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(99));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsAvailability_c1",Boolean.FALSE,
                 "10",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_c1()
    
    private static String GetSM_c2()
    {
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(90,80));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsAvailability_c2",Boolean.FALSE,
                 "10",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_c2()
    
    private static String GetSM_c3()
    {
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1,1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(90,80,70));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsAvailability_c3",Boolean.FALSE,
                 "10",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_c3()
    
    private static String GetSM_c4()
    {
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1));
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(90,80,70,60));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsAvailability_c4",Boolean.FALSE,
                 "10",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_c4()
    
    private static String GetSM_c5()
    {
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(2,1,1,1,1));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1,1));
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD","compE")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(90,80,70,60,50));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsAvailability_c5",Boolean.FALSE,
                 "10",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_c5()
    
    public static void WriteSMs_for_AvailabilityPerComponent_Test()
    {
       Produce_ServiceManifest pr = new Produce_ServiceManifest(); 
       pr.writeToFile(GetSM_c1(),"SM_1c1","C:\\"); 
       pr.writeToFile(GetSM_c2(),"SM_1c2","C:\\"); 
       pr.writeToFile(GetSM_c3(),"SM_1c3","C:\\"); 
       pr.writeToFile(GetSM_c4(),"SM_1c4","C:\\"); 
       pr.writeToFile(GetSM_c5(),"SM_1c5","C:\\"); 
       
    }////WriteSMs_for_Decision_Test()
}//class
