/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadAllocationPatternFromSM;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationOffer;
import eu.optimis.ac.test.produceSM.ProduceManifest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;
import org.apache.xmlbeans.XmlException;

public class HorizontalElasticityTest extends TestCase {
    
    public void testHorizontalElasticityTest() throws XmlException
    {
       String serviceManifest1 = getSM(50);
        
       String result1 = doTest(serviceManifest1,GetServerDetails.Host); 
        
       String serviceManifest2 = getSM(100);
        
       String result2 = doTest(serviceManifest2,GetServerDetails.Host); 
        
        if(result1.hashCode()==result2.hashCode())
            throw new RuntimeException();
        else
        {
            System.out.println();System.out.println();
            System.out.println(" First run returned : "+result1);
            System.out.println("Second run returned : "+result2);
            System.out.println();System.out.println();
            System.out.println("Allocation Pattern in both Cases are different");
            System.out.println();
            System.out.println("Test is Success");
            System.out.println();System.out.println();
        }
    }//testHorizontalElasticityTest()
    
    private String doTest(String serviceManifest,String host) throws XmlException 
    {
        
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
     
        SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifest);
        
        formParams.add("serviceManifest", serviceManifest);
	formParams.add("cleanTestbed", "");
        
	formParams.add("doNotBackupSMflag", "True");
        
        MultivaluedMap<String, String> ReturnedParams = 
                ACGatewayRemoteTest.doTest(formParams,host,"DefaultSolver");
        
        String returnedSM = ReturnedParams.get("serviceManifest").get(0);
        
        String AC_decision = ReadDecisionFromSM.getDecision(returnedSM);
        
        System.out.println("AC Decision : "+AC_decision);
        
        ReadAllocationPatternFromSM allocationPattern =
                new ReadAllocationPatternFromSM(returnedSM);
        
        PrintAllocationOffer.printAllocationOfferForCertainSM(returnedSM, manifestInfo);
        
        return ReadAllocationPatternFromSM.getAllocationPatternAsOneString(manifestInfo, allocationPattern);
        
    }//doTest()
    
    public static String getSM(int availability)
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0,0));
        
         String affinityRules = "1,2,Low";
         String antiAffinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC","compD")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(availability,availability,availability,availability));
         
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(4,3,4,3));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1,1));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsHorizontalElasticity",Boolean.FALSE,
                 "1",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         
         return serviceManifestAsString;
         
    }//getSM()
    
}//class
