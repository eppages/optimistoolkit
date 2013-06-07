/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.affinityTest.print;

import eu.optimis.ac.gateway.analyzeAllocationDetails.AllocationDetailsInfo;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerComponent;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import org.w3c.dom.NodeList;


public class PrintAllocationDetails {
    
    public static void printAllocationDetailForAllComponents(SMAnalyzer manifestInfo,String AllocationDetails)
    {
        System.out.print("==>");
        System.out.println("printing Allocation Details as returned From Admission Controller:");
        System.out.println();
        
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             
            int federatedVMsPerComponent  = FederatedVMsPerComponent.
                     getFederatedVMsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
            System.out.println("componentId : "+manifestInfo.componentId_List.get(i)+" FederatedVMs : "+federatedVMsPerComponent);
            
             NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             printAllocationDetailsForOneServiceManifest(manifestInfo, the_PhysicalHosts_nodes, i);
         }//for-i
    }//printAllocationDetailForAllComponents
    
    public static void printAllocationDetailsForOneServiceManifest(SMAnalyzer manifestInfo,NodeList the_PhysicalHosts_nodes,int i)
    {
              for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++)
              {
                  String physicalHostId = AllocationDetailsInfo.
                    getCertainPhysicalHostIdFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  int AssingedVMs = AllocationDetailsInfo.
                    getCertainPhysicalHostAssignedVMsFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  printAssignedVMsForCertainPhysicalHost(manifestInfo.componentId_List.get(i), physicalHostId, AssingedVMs);
                  
              }//for-z
        
    }//printAllocationDetailsForOneServiceManifest()
    
    public static void printAssignedVMsForCertainPhysicalHost(String componentId, String physicalHostId, int AssingedVMs) 
    {
        System.out.println("componentId : "+componentId+" at "+physicalHostId+
                " with AssignedVMs : "+AssingedVMs);
        
    }//printAssignedVMsForCertainPhysicalHost()
}//class
