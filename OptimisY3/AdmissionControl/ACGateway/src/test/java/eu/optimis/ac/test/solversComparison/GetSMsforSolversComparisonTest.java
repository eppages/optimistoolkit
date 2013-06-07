/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.solversComparison;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.test.produceSM.ProduceManifest;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.core.MultivaluedMap;

public class GetSMsforSolversComparisonTest {
    
    
    public static MultivaluedMap<String, String> getSMs()
    {
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
        
        formParams.add("serviceManifest", GetSM_for_SolversComparison1());
	formParams.add("serviceManifest", GetSM_for_SolversComparison2());
	formParams.add("serviceManifest", GetSM_for_SolversComparison3());
	formParams.add("serviceManifest", GetSM_for_SolversComparison4());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison4());
        
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_1());
        
        //formParams.add("serviceManifest", GetSM_for_SolversComparison_5());
        
        
        
        formParams.add("doNotBackupSMflag", "True");
        
        return formParams;
    }//getSMs()
    
    private static String GetSM_for_SolversComparison4()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
        return GetSM(
             Initial_List, UpperBound_List,"ACTestsSolversComparison4");
    }//GetSM_for_SolversComparison4()
    
    private static String GetSM_for_SolversComparison3()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
        return GetSM(
             Initial_List, UpperBound_List,"ACTestsSolversComparison3");
    }//GetSM_for_SolversComparison3()
    
    private static String GetSM_for_SolversComparison2()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
        return GetSM(
             Initial_List, UpperBound_List,"ACTestsSolversComparison2");
    }//GetSM_for_SolversComparison2()
    
    private static String GetSM_for_SolversComparison1()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1));
        
        return GetSM(
             Initial_List, UpperBound_List,"ACTestsSolversComparison1");
    }//GetSM_for_SolversComparison1()
    
    private static String GetSM(
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
         
    }//GetSM()
    
    
    private static String GetSM_for_SolversComparison_1()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1));
        
        return GetSM_1(
             Initial_List, UpperBound_List,"ACTestsSolversComparison_1");
    }//GetSM_for_SolversComparison1()
    
    private static String GetSM_1(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List,String serviceId)
    {
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0));
        
         String affinityRules = "";
         String antiAffinityRules = "";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,serviceId,Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM()
    
    private static String GetSM_for_SolversComparison_4()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1));
        
        return GetSM_4(
             Initial_List, UpperBound_List,"ACTestsSolversComparison_4");
    }//GetSM_for_SolversComparison1()
    
    private static String GetSM_4(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List,String serviceId)
    {
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,serviceId,Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_4()
    
    private static String GetSM_for_SolversComparison_3()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
        
        return GetSM_3(
             Initial_List, UpperBound_List,"ACTestsSolversComparison_3");
    }//GetSM_for_SolversComparison1()
    
    private static String GetSM_3(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List,String serviceId)
    {
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,serviceId,Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_4()
    
    private static String GetSM_for_SolversComparison_5()
    {
        ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1,1));
        ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1,1));
        
        return GetSM_5(
             Initial_List, UpperBound_List,"ACTestsSolversComparison_4");
    }//GetSM_for_SolversComparison1()
    
    private static String GetSM_5(
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List,String serviceId)
    {
        
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD","compE")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100,100,100));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,serviceId,Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM_4()
}//class
