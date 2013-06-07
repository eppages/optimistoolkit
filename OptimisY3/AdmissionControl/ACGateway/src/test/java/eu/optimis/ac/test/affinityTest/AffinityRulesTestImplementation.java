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
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationDetails;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class AffinityRulesTestImplementation {
    
    public static void testAffinityRulesMedium(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
        ArrayList<ArrayList<String>> AffinityRuleList = manifestInfo.affinityRule.AffinityRuleList;
        
        for(int j=0; j<AffinityRuleList.size();j++)
        {
            ArrayList<String> Rule = AffinityRuleList.get(j);
            
            String rule = Rule.get(Rule.size()-1);
            
            if(!rule.contains("Medium"))continue;
            
            System.out.println("AffinityRule:"+Rule+"==>");
            
            int numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium = 0;
            
            for(int z=0; z<Rule.size()-1;z++)
            {
                String componentId_WithRule = Rule.get(z);
                
                for(int i=0;i<manifestInfo.componentId_List.size();i++)
                {
                    NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
                    
                    String componentId = manifestInfo.componentId_List.get(i);
                    
                    if(!componentId.equals(componentId_WithRule))continue;
                    
                    int sumAssignedVMs = AllocationDetailsInfo.
                            getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails
                            (the_PhysicalHosts_nodes);
                    
                    for(int x=0;x<the_PhysicalHosts_nodes.getLength();x++)
                    {
                        String physicalHostId = AllocationDetailsInfo.
                                getCertainPhysicalHostIdFromTheAllocationDetails(the_PhysicalHosts_nodes, x+1);
                        int AssingedVMs = AllocationDetailsInfo.
                                getCertainPhysicalHostAssignedVMsFromTheAllocationDetails(the_PhysicalHosts_nodes, x+1);
                        
                        PrintAllocationDetails.printAssignedVMsForCertainPhysicalHost(componentId, physicalHostId, AssingedVMs);
                        
                    }//for-x
                    
                    if(sumAssignedVMs>0)
                        numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium++;
                    
                }//for-i
                
            }//for-z
            
            System.out.println(Rule);
            System.out.println("numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium = "
                    +numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium);
            
            if(numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium==0)continue;
            
            if((ThrowRuntimeException)&&(numberOfAffectedComponentsWithAssignedVMsInTheCloudWhichHasAffinityRuleMedium!=Rule.size()-1))
                 throw new RuntimeException("testAffinityRulesMedium failed");
        }//for-j
        
    }//testAffinityRulesMedium()
    
    public static void testAffinityRulesHigh(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
        
        ArrayList<ArrayList<String>> AffinityRuleList = manifestInfo.affinityRule.AffinityRuleList;
        
        for(int j=0; j<AffinityRuleList.size();j++)
        {
            ArrayList<String> Rule = AffinityRuleList.get(j);
            
            String rule = Rule.get(Rule.size()-1);
            
            if(!rule.contains("High"))continue;
            
            System.out.println("AffinityRule:"+Rule+"==>");
            
            ArrayList<ArrayList<String>> physicalHostsInvolved = new ArrayList<ArrayList<String>>();
            
            for(int z=0; z<Rule.size()-1;z++)
            {
                String componentId_WithRule = Rule.get(z);
                
                for(int i=0;i<manifestInfo.componentId_List.size();i++)
                {
                    NodeList the_PhysicalHosts_nodes = AllocationDetailsInfo.
                     getAllocationDetailsForCertainServiceForCertainComponent
                     (AllocationDetails, 1, i+1);
                    
                    String componentId = manifestInfo.componentId_List.get(i);
                    
                    if(!componentId.equals(componentId_WithRule))continue;
                    
                    ArrayList<String> ListOfPhysicalHostsInvolved = AllocationDetailsInfo.
                            getListOfPhysicalHostsInvolvedInTheAllocationDetailsForCertainComponent
                            (the_PhysicalHosts_nodes);
                    
                    physicalHostsInvolved.add(ListOfPhysicalHostsInvolved);
                    
                }//for-i
                
            }//for-z
            
            System.out.println("physicalHostsInvolved:"+physicalHostsInvolved);
            
            int pointer = selectFirstPointerWithOnlyOnePhysicalHostInvolved(physicalHostsInvolved);
            
            if(pointer==-2)
            {
                System.err.println("ListOfPhysicalHostsInvolved.size()>1 "+physicalHostsInvolved);
                
                if(ThrowRuntimeException)
                 throw new RuntimeException("testAffinityRulesHigh failed");
            }//-2
            
            if(pointer==-1)
            {
                System.out.println("ListOfPhysicalHostsInvolved.size()=0 "+physicalHostsInvolved);
                System.out.println("Components Where Rejected");
            }//-2
            
            String physicalHostId = physicalHostsInvolved.get(pointer).get(0);
            
            for(int y=0; y<physicalHostsInvolved.size();y++)
            {
                ArrayList<String> ListOfPhysicalHostsInvolved = physicalHostsInvolved.get(y);
                
                if(physicalHostId.equals(ListOfPhysicalHostsInvolved.get(0))) continue;
                
                System.err.println("ListOfPhysicalHostsInvolved != physicalHostId " +ListOfPhysicalHostsInvolved+" "+physicalHostId);
                
                if(ThrowRuntimeException)
                 throw new RuntimeException("testAffinityRulesHigh failed");
            }//for-y
            
            
        }//for-j
        
    }//testAffinityRulesHigh()
    
    private static int selectFirstPointerWithOnlyOnePhysicalHostInvolved(ArrayList<ArrayList<String>> physicalHostsInvolved)
    {
        for(int y=0; y<physicalHostsInvolved.size();y++)
        {
                ArrayList<String> ListOfPhysicalHostsInvolved = physicalHostsInvolved.get(y);
                
                if(ListOfPhysicalHostsInvolved.size()>1)return -2;
                
                if(ListOfPhysicalHostsInvolved.size()==1)return y;
        }//for-y
        
        return -1;
    }//selectFirstPointerWithOnlyOnePhysicalHostInvolved
}//class
