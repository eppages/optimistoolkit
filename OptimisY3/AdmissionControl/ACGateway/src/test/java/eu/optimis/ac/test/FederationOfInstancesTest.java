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
import eu.optimis.ac.gateway.analyzeAllocationDetails.AllocationDetailsInfo;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerComponent;
import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.print.PrintAffinity;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationDetails;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationOffer;
import eu.optimis.ac.test.produceSM.ProduceManifest;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;

import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class FederationOfInstancesTest  extends TestCase{
    
    public static Boolean ThrowRuntimeException = true;
    
    private static String AllocationDetails = null;  
    
    public void testFederationOfInstances()
    {
    	String host = GetServerDetails.Host;
    	
    	FederationOfInstancesTesting(host,"DefaultSolver");
    }//testFederationOfInstances()
    
    public static void FederationOfInstancesTesting(String host,String whichSolver)
    {
        String TestName = AffinityTest.getTestName("testFederationOfInstances");
         
         String serviceManifestAsString = GetSM();
        
         doTest(host,TestName, serviceManifestAsString,null,whichSolver);
         
         ResultTesting(TestName);
         
    }//FederationOfInstancesTesting()
    
    private static void ResultTesting(String TestName)
    {
        System.out.println("==>Testing test Result:");
        
        int countComponentsWithAssignedVMs = 0;
        int countComponentsWithFederatedVMs = 0;
        
        System.out.println("Each Component must have Assigned VMs Non Zero:");
        
        for(int certainComponent=1;certainComponent<=3;certainComponent++)
        {
            int assignedVMs = AllocationDetailsInfo.getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails(AllocationDetailsInfo.getAllocationDetailsForCertainServiceForCertainComponent(AllocationDetails, 1, certainComponent));
            
            System.out.println("Component "+ certainComponent+" AssignedVMs : "+assignedVMs);
            
            if(assignedVMs>0)
                countComponentsWithAssignedVMs++;
            
        }//for-certainComponent
        
        System.out.println("At Least One Component must have Federated VMs Not Zero :");
        
        for(int certainComponent=1;certainComponent<=3;certainComponent++)
        {
            int federatedVMs = FederatedVMsPerComponent.getFederatedVMsForCertainServiceForCertainComponent(AllocationDetails, 1, certainComponent);
            
            System.out.println("Component "+ certainComponent+" FederatedVMs : "+federatedVMs);
            
            if(federatedVMs>0)
                countComponentsWithFederatedVMs++;
        }//for-certainComponent
        
        System.out.println();
        System.out.println("countComponentsWithAssignedVMs = "+countComponentsWithAssignedVMs);
        System.out.println("countComponentsWithFederatedVMs = "+countComponentsWithFederatedVMs);
        
        if((ThrowRuntimeException))
            if((countComponentsWithAssignedVMs!=3)||
                (countComponentsWithFederatedVMs==0))
                    throw new RuntimeException("testFederetionOfInstances failed");
        
        System.out.println(TestName+" Finished");
        System.out.println("---------------------------------------------------------------------");
    }//ResultTesting()
    
    private static void doTest(String host, String TestName, String serviceManifestAsString, String isFederationAllowed,String whichSolver)
    {
         SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
         
         MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

         formParams.add("serviceManifest", serviceManifestAsString);
         if(isFederationAllowed!=null)
            formParams.add("isFederationAllowed", isFederationAllowed);
         
         DoRemoteTest doTest = new DoRemoteTest(formParams,host,whichSolver);
        
         assertEquals(doTest.status,200);
         
         ACdecision.printDecision(doTest.status, doTest.returnedSMs, false, null);
         
         System.out.println();
         
         String returnedSM = doTest.returnedSMs.get("serviceManifest").get(0);
         AllocationDetails = doTest.returnedSMs.get("AllocationDetails").get(0);
         
         PrintAllocationOffer.printAllocationOfferForCertainSM(returnedSM, manifestInfo);
         
         System.out.println();
         
         PrintAllocationDetails.printAllocationDetailForAllComponents(manifestInfo, AllocationDetails);
         
         System.out.println();
         
    }//doTest()
    
    private static String GetSM()
    {
         ArrayList<Integer> affinityConstraints_List = new ArrayList<Integer>(Arrays.asList(1,1,0));
         ArrayList<Integer> antiAffinityConstraints_List = new ArrayList<Integer>(Arrays.asList(0,0,0));
         
         String antiAffinityRules = "1,2,Low";
        
         String affinityRules = "1,2,Medium";
        
         ArrayList<String> componentId_List = new ArrayList<String>(Arrays.asList("compA","compB","compC")); 
         
         ArrayList<Integer> AvailabilityP1M_List = new ArrayList<Integer>(Arrays.asList(100,100,100));
         
         ArrayList<Integer> UpperBound_List = new ArrayList<Integer>(Arrays.asList(1,2,50));
         ArrayList<Integer> Initial_List = new ArrayList<Integer>(Arrays.asList(1,1,1));
         
         String serviceManifestAsString = ProduceManifest.manifestAsString(
                 componentId_List,"ACTestsFederationOfInstances",Boolean.FALSE,
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
         
    }//GetSM()
    
}//class
