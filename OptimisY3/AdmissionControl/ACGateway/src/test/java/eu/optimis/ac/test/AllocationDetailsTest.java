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

import eu.optimis.ac.ACRestClients.ACperServiceConstraintsRestClient;
import eu.optimis.ac.gateway.analyzeAllocationDetails.AllocationDetailsInfo;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerComponent;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerService;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadAllocationPatternFromSM;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;
import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;
import org.w3c.dom.NodeList;

public class AllocationDetailsTest  extends TestCase{
    
    private static String AllocationDetails = null;
    
    private static MultivaluedMap<String, String> formParams;
    
    private static MultivaluedMap<String, String> returnedSMs;
    
    public static void testAllocationDetailsTest()
    {
    	AllocationDetailsTesting(GetServerDetails.Host,"DefaultSolver");
    	
    }//testAllocationDetailsTest()
    
    public static void AllocationDetailsTesting(String host,String whichSolver)
    {        
	
        doTest(host,whichSolver);
        AllocationDetails_Test();
        FederatedVMsDetailsPerComponentTest();
        FederatedVMsDetailsPerServiceTest();
        AllocationPatternTest();
        
    }//AllocationDetailsTesting()
    
    public static void doTest(String host,String whichSolver)
    {
        formParams = ACGatewayRemoteTest.setTestInput();
        
        DoRemoteTest doTest = new DoRemoteTest(formParams,host,whichSolver);
                    
        assertEquals(doTest.status,200);
        
        AllocationDetails = doTest.returnedSMs.get("AllocationDetails").get(0);
        
        returnedSMs = doTest.returnedSMs;
        
        System.out.println(AllocationDetails);
    }//doTest()
    
    public static void AllocationDetails_Test() {
        
        System.out.println("testAllocationDetails Started:");
        
        for(int i=0;i<formParams.get("serviceManifest").size();i++)
        {
            
            String serviceManifestAsString = formParams.get("serviceManifest").get(i);
            
            SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
            
            System.out.println("AllocationDetails for SM:"+(i+1)+" with components: "+manifestInfo.numberOfServiceComponents+" ==>");
            
            for(int j=0;j<manifestInfo.componentId_List.size();j++)
            {
             
              NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, i+1, j+1);
              
              for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++)
              {
                  //String physicalHostId = AllocationDetailsInfo.getCertainPhysicalHostIdFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  int AssignedVMs = AllocationDetailsInfo.
                    getCertainPhysicalHostAssignedVMsFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  //System.out.println("a"+(i+1)+","+manifestInfo.componentId_List.get(j)+","+physicalHostId+","+AssignedVMs);
                  
                  System.out.println("ph"+(z+1)+","+"vm"+(j+1)+","+"a"+(i+1)+","+AssignedVMs);
              }//for-z
             
            }//for-j
            
            System.out.println("------------------------------------");
        }//for-i
       
        System.out.println("testAllocationDetails Finished");
        System.out.println("");
    }//AllocationDetailsTest()
    
    public static void FederatedVMsDetailsPerComponentTest()
    {
        
        System.out.println("testFederatedVMsDetailsPerComponent Started:");
        
        for(int i=0;i<formParams.get("serviceManifest").size();i++)
        {
            
            String serviceManifestAsString = formParams.get("serviceManifest").get(i);
            
            SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
            
            System.out.println("FederatedVMsDetails for SM:"+(i+1)+" with components: "+manifestInfo.numberOfServiceComponents+" ==>");
            
            for(int j=0;j<manifestInfo.componentId_List.size();j++)
            {
             
                    int federatedVMsPerComponent = FederatedVMsPerComponent.
                            getFederatedVMsForCertainServiceForCertainComponent
                            (AllocationDetails, i+1,j+1);
                  
                  //System.out.println("a"+(i+1)+","+manifestInfo.componentId_List.get(j)+","+physicalHostId+","+AssignedVMs);
                  
                  System.out.println("vm"+(j+1)+","+"a"+(i+1)+","+federatedVMsPerComponent);
              
             
            }//for-j
            
            System.out.println("------------------------------------");
        }//for-i
        
        System.out.println("testFederatedVMsDetailsPerComponent Finished");
        System.out.println("");
    }//FederatedVMsDetailsPerComponentTest()
    
    public static void FederatedVMsDetailsPerServiceTest()
    {
        
        System.out.println("testFederatedVMsDetailsPerService Started:");
        
        for(int i=0;i<formParams.get("serviceManifest").size();i++)
        {
            
            int federatedVMsPerService = FederatedVMsPerService.getFederatedVMsForCertainService(AllocationDetails, i+1);
            
            System.out.println("sumFederatedVMs for SM:"+(i+1)+" is: "+federatedVMsPerService);
            
        }//for-i
        
        System.out.println("------------------------------------");
        
        System.out.println("testFederatedVMsDetailsPerService Finished");
        System.out.println("");
    }//FederatedVMsDetailsPerServiceTest()
    
    public static void AllocationPatternTest()
    {
        System.out.println("testAllocationPattern Started:");
        
        for(int i=0;i<returnedSMs.get("serviceManifest").size();i++)
        {
            System.out.println((i+1)+"==>");
            
            String serviceManifestAsString = returnedSMs.get("serviceManifest").get(i);
            
            Boolean accepted = ReadDecisionFromSM.
                    isDecisionAccepted(serviceManifestAsString);
            
            if(!accepted)
                System.out.println("Not Accepted");
            
            SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
            
            ReadAllocationPatternFromSM allocationPattern = 
                    new ReadAllocationPatternFromSM(serviceManifestAsString);
            
            for(int j=0;j<manifestInfo.componentId_List.size();j++)
            {
             
                System.out.println("Component : "+(j+1));
                
                if(allocationPattern.Basic_Map.containsKey(manifestInfo.componentId_List.get(j)))
                {
                    System.out.print("Basic :");
                    for(int b=0;b<allocationPattern.Basic_Map.get(manifestInfo.componentId_List.get(j)).size();b++)
                        System.out.print(" "+allocationPattern.Basic_Map.get(manifestInfo.componentId_List.get(j)).get(b));
                    System.out.println();
                }//if-has-Basic
                
                if(allocationPattern.Elastic_Map.containsKey(manifestInfo.componentId_List.get(j)))
                {
                    System.out.print("Elastic :");
                    for(int e=0;e<allocationPattern.Elastic_Map.get(manifestInfo.componentId_List.get(j)).size();e++)
                        System.out.print(" "+allocationPattern.Elastic_Map.get(manifestInfo.componentId_List.get(j)).get(e));
                    System.out.println();
                }//if-has-Elastic
                
            }//for-j
            
            System.out.println("------------------------------------");
        }//for-i
       
        System.out.println("testAllocationPattern Finished");
        System.out.println("");
    }//AllocationPatternTest()
}//class
