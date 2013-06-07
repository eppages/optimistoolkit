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
import org.w3c.dom.NodeList;

public class AffinityConstraintsTestImplementation {
    
    public static void testAffinityConstraintMedium(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
    	for(int i=0;i<manifestInfo.componentId_List.size();i++)
        {
            
            String componentId = manifestInfo.componentId_List.get(i);
            
            String component_Constraint = manifestInfo.smInfo.
                    antiAffinityConstraints_Map.get(componentId).get(0);
            
            if(!component_Constraint.contains("Medium"))continue;
            
            int federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium  = FederatedVMsPerComponent.
                    getFederatedVMsForCertainServiceForCertainComponent
                    (AllocationDetails, 1, i+1);
            
            System.out.println(componentId+"("+component_Constraint+") : "+"federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium = "
                    + federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium);
            
            if(federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium>0)
                System.err.println("federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium>0 :"
                        + federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium);
            
            if((ThrowRuntimeException)&&(federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsMedium>0))
                throw new RuntimeException("testAffinityConstraintMedium failed");
            
        }//for-i
    }//testAffinityConstraintMedium()
    
    public static void testAffinityConstraintHigh(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             
             NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             String componentId = manifestInfo.componentId_List.get(i);
             
             String component_Constraint = manifestInfo.smInfo.
                     affinityConstraints_Map.get(componentId).get(0);
             
             if(!component_Constraint.contains("High"))continue;
             
             int numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh = AllocationDetailsInfo.
                     getNumberOfPhysicalHostsInvolvedInTheAllocationDetails
                     (the_PhysicalHosts_nodes);
             
             int federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsHigh = FederatedVMsPerComponent.
                     getFederatedVMsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
             
             System.out.println(componentId+"("+component_Constraint+") : "+"numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh = "
                     + numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh);
             
             System.out.println(componentId+"("+component_Constraint+") : "+"federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsHigh = "
                     + federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsHigh);
             
             if((numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh>1)||(federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsHigh!=0))
                 System.err.println("numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh >1 :"
                         + numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh);
             
             if((ThrowRuntimeException)&&((numberOfInvolvedPhysicalHostsForTheComponentWhichHasAffinityConstraintsHigh>1)||(federatedVMsPerComponentForTheComponentWhichHasAffinityConstraintsHigh!=0)))
                 throw new RuntimeException("testAffinityConstraintHigh failed");
             
         }//for-i
    }//testAffinityConstraintHigh()
    
}//class
