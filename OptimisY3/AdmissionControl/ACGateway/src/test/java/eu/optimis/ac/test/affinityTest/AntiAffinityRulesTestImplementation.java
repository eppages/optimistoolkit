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
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class AntiAffinityRulesTestImplementation {
   
    public static void testAntiAffinityRuleMedium(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
        ArrayList<ArrayList<String>> AntiAffinityRuleList = manifestInfo.antiAffinityRule.AffinityRuleList;
        
        for(int j=0; j<AntiAffinityRuleList.size();j++)
        {
            ArrayList<String> Rule = AntiAffinityRuleList.get(j);
            
            String rule = Rule.get(Rule.size()-1);
            
            if(!rule.contains("Medium"))continue;
            
            System.out.println("AntiAffinityRule:"+Rule+"==>");
            
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
                    
                    System.out.println(componentId+"(Medium) "+"physicalHostsInvolved:"+ListOfPhysicalHostsInvolved);
                }//for-i
                
            }//for-z
            
            System.out.println("physicalHostsInvolvedPerComponent must be Different.");
            
            if(!DoubleListComparison(physicalHostsInvolved))
                System.out.println("testAntiAffinityRuleMedium failed");
            
            if((ThrowRuntimeException)&&(!DoubleListComparison(physicalHostsInvolved)))
                 throw new RuntimeException("testAntiAffinityRuleMedium failed");
        }//for-j
        
    }//testAntiAffinityRuleMedium()
    
    public static void testAntiAffinityRuleHigh(String AllocationDetails, SMAnalyzer manifestInfo, Boolean ThrowRuntimeException)
    {
        ArrayList<ArrayList<String>> AntiAffinityRuleList = manifestInfo.antiAffinityRule.AffinityRuleList;
        
        for(int j=0; j<AntiAffinityRuleList.size();j++)
        {
            ArrayList<String> Rule = AntiAffinityRuleList.get(j);
            
            String rule = Rule.get(Rule.size()-1);
            
            if(!rule.contains("High"))continue;
            
            System.out.println("AntiAffinityRule:"+Rule+"==>");
            
            ArrayList<Integer> ListOfAssingedVMsForEachComponentWhichHasAntiffinityRuleHigh = new ArrayList<Integer>();
            
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
                    
                    int assingedVMs = AllocationDetailsInfo.
                            getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails
                            (the_PhysicalHosts_nodes);
                    
                    ListOfAssingedVMsForEachComponentWhichHasAntiffinityRuleHigh.add(assingedVMs);
                    
                    System.out.println(componentId+"(High) "+"AssingedVMs:"+assingedVMs);
                }//for-i
                
            }//for-z
            
                
           int countAffectedComponentsWithAssingedVMsNotZero = 0;
                
           for(int y=0;y<ListOfAssingedVMsForEachComponentWhichHasAntiffinityRuleHigh.size();y++)
           {
              if(ListOfAssingedVMsForEachComponentWhichHasAntiffinityRuleHigh.get(y)>0)
                   countAffectedComponentsWithAssingedVMsNotZero++;
           }//for-y
                
                
           System.out.println("countAffectedComponentsWithAssingedVMsNotZero = "+ countAffectedComponentsWithAssingedVMsNotZero+
              " which must be 1");
                
           if(countAffectedComponentsWithAssingedVMsNotZero>1)
               System.err.println("testAntiAffinityRuleHigh failed");
                
           if((ThrowRuntimeException)&&(countAffectedComponentsWithAssingedVMsNotZero>1))
                 throw new RuntimeException("testAntiAffinityRuleHigh failed");
        }//for-j
        
    }//testAntiAffinityRuleHigh()
    
    
    private static Boolean DoubleListComparison(ArrayList<ArrayList<String>> physicalHostsInvolved)
    {
        for(int i=0;i<physicalHostsInvolved.size();i++)
        {
            ArrayList<String> ListOfPhysicalHostsInvolved1 = physicalHostsInvolved.get(i);
            
            for(int j=i+1;j<physicalHostsInvolved.size();j++)
            {
                ArrayList<String> ListOfPhysicalHostsInvolved2 = physicalHostsInvolved.get(j);
                
                Boolean x = ListComparison(ListOfPhysicalHostsInvolved1, ListOfPhysicalHostsInvolved2);
                
                if(x==false)return false;
            }//for-j
        }//for-i
        
        return true;
    }//DoubleListComparison()
    
    private static Boolean ListComparison(ArrayList<String> ListOfPhysicalHostsInvolved1, ArrayList<String> ListOfPhysicalHostsInvolved2)
    {
            for(int i=0;i<ListOfPhysicalHostsInvolved1.size();i++)
            {
                String physicalHost = ListOfPhysicalHostsInvolved1.get(i);
                if(ListOfPhysicalHostsInvolved2.contains(physicalHost))
                    return false;
            }//for-i
            
            for(int i=0;i<ListOfPhysicalHostsInvolved2.size();i++)
            {
                String physicalHost = ListOfPhysicalHostsInvolved2.get(i);
                if(ListOfPhysicalHostsInvolved1.contains(physicalHost))
                    return false;
            }//for-i
            
            return true;
    }//ListComparison()
    
}//class
