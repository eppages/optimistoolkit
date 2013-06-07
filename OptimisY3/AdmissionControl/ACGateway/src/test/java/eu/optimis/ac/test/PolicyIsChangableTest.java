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

public class PolicyIsChangableTest extends TestCase {
    
    public void testChangeInPolicy() throws XmlException 
    {
        String host = GetServerDetails.Host;
        String port  = GetServerDetails.Port;
        
        changeWeights1(host,port);
        
        String result1 = doTest(host);
        
        changeWeights2(host,port);
        
        String result2 = doTest(host);
        
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
    }//testChangeInPolicy()
    
    private void changeWeights1(String host,String port)
    {
        SetPolicyTest.setWeights(host,port,
            "0.3","0.2","0.4","0.1");       
    }//changeWeights1()
    
    private void changeWeights2(String host,String port)
    {
        SetPolicyTest.setWeights(host,port,
            "0.25","0.25","0.25","0.25");
    }//changeWeights2()
    
    private String doTest(String host) throws XmlException 
    {
        
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
     
        String serviceManifest = GetSM();
        
        SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifest);
        
        formParams.add("serviceManifest", serviceManifest);
		
	formParams.add("doNotBackupSMflag", "True");
        formParams.add("cleanTestbed", "");
        formParams.add("changePolicy", "True");
        
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
    private String GetSM()
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         
         String antiAffinityRules = "1,2,Low";
        
         String affinityRules = "1,2,Low";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100));
         
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(3,2,2));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsPolicyIsChangableTest",Boolean.FALSE,
                 "2",
                 UpperBound_List,
                 Initial_List,
                 AvailabilityP1M_List,
                 affinityConstraints_List, antiAffinityConstraints_List,
                 affinityRules,antiAffinityRules);
         
         return serviceManifestAsString;
         
    }//GetSM()
    
}//class
