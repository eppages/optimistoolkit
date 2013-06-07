/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.test.affinityTest;

import eu.optimis.ac.gateway.analyzeAllocationDetails.AllocationDetailsInfo;
import eu.optimis.ac.gateway.analyzeAllocationDetails.FederatedVMsPerComponent;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class AntiAffinityConstraintsTestImplementation {
    
    public static void testAntiAffinityConstraintMedium(String AllocationDetails, 
            SMAnalyzer manifestInfo, Boolean ThrowRuntimeException,
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List)
    {
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             String componentId = manifestInfo.componentId_List.get(i);
             
             String component_Constraint = manifestInfo.smInfo.
                     antiAffinityConstraints_Map.get(componentId).get(0);
             
             if(!component_Constraint.contains("Medium"))continue;
             
             int condition1 = 1;
             int condition2 = 1;
             
             int federatedVMsPerComponent  = FederatedVMsPerComponent.
                     getFederatedVMsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             int sumAssingedVMs = AllocationDetailsInfo.
                     getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails
                     (the_PhysicalHosts_nodes);
             
             System.out.println("Condition 1==>");
             
             System.out.println(componentId+"("+component_Constraint+") : has "+"FederatedVMs = "
                     + federatedVMsPerComponent);
             
             if(federatedVMsPerComponent>0)
                 condition1 = 0;
             
             for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++)
              {
                  String physicalHostId = AllocationDetailsInfo.
                    getCertainPhysicalHostIdFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  int AssingedVMs = AllocationDetailsInfo.
                    getCertainPhysicalHostAssignedVMsFromTheAllocationDetails(the_PhysicalHosts_nodes, z+1);
                  
                  System.out.println(componentId+"("+component_Constraint+") : at "+physicalHostId+" assingedVMs = "
                     + AssingedVMs+" and must be <=1");
                  
                  if(AssingedVMs>1)
                      condition1 = 0;
                  
              }//for-z
             
             System.out.println("OR Condition 2==>");
             
             System.out.println(componentId+"("+component_Constraint+") : has Total "+" AssingedVMs = "
                     + sumAssingedVMs+" amd must be 0");
             System.out.println(componentId+"("+component_Constraint+") : has "+" FederatedVMs = "
                     + federatedVMsPerComponent+" amd must be 0");
             
             if((sumAssingedVMs+federatedVMsPerComponent)==0)
                      condition2 = 0;
             
             if(condition1+condition2<1)
                 System.err.println("testAntiAffinityConstraintMedium failed");
             else
                 System.out.println("testAntiAffinityConstraintMedium was Success");
             
             if((ThrowRuntimeException)&&(condition1+condition2<1))
                 throw new RuntimeException("testAntiAffinityConstraintMedium failed");
             
         }//for-i
    }//testAntiAffinityConstraintMedium()
    
    public static void testAntiAffinityConstraintHigh(String AllocationDetails, 
            SMAnalyzer manifestInfo, Boolean ThrowRuntimeException,
            ArrayList<Integer> Initial_List,ArrayList<Integer> UpperBound_List)
    {
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             String componentId = manifestInfo.componentId_List.get(i);
             
             String component_Constraint = manifestInfo.smInfo.
                     antiAffinityConstraints_Map.get(componentId).get(0);
             
             if(!component_Constraint.contains("High"))continue;
             
             int sumAssingedVMs = AllocationDetailsInfo.
                     getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails
                     (the_PhysicalHosts_nodes);
             
             int federatedVMsPerComponent  = FederatedVMsPerComponent.
                     getFederatedVMsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             System.out.println(componentId+"("+component_Constraint+") : has Total "+" AssingedVMs = "
                     + sumAssingedVMs+" amd must be equal to Basic VMs = "+Initial_List.get(i));
             
             System.out.println(componentId+"("+component_Constraint+") : has "+"FederatedVMs = "
                     + federatedVMsPerComponent+" and must be equal to ElasticVMs-BasicVMs = "+(UpperBound_List.get(i)-Initial_List.get(i)));
             
             if((sumAssingedVMs!=Initial_List.get(i))&&(federatedVMsPerComponent!=(UpperBound_List.get(i)-Initial_List.get(i))))
                 System.err.println("testAntiAffinityConstraintHigh failed");
             else
                 System.out.println("testAntiAffinityConstraintHigh was Success");
             
             if((ThrowRuntimeException)&&(sumAssingedVMs!=Initial_List.get(i))&&(federatedVMsPerComponent!=(UpperBound_List.get(i)-Initial_List.get(i))))
                 throw new RuntimeException("testAntiAffinityConstraintHigh failed");
         }//for-i
    }//testAntiAffinityConstraintHigh()
    
}//class
