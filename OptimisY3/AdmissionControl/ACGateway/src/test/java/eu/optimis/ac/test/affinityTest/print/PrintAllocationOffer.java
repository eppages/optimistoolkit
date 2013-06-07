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

import eu.optimis.ac.gateway.serviceManifestFunctions.ReadAllocationPatternFromSM;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.util.ArrayList;

public class PrintAllocationOffer {
    
    public static void printAllocationOfferForCertainSM(String returnedSM, SMAnalyzer manifestInfo)
    {
        System.out.print("==>");
        System.out.println("printing Allocation Offer from returned Service Manifest");
        System.out.println("(if service is rejected or partial rejected would be empty");
        System.out.println("if Federation is Not Allowed):");
        System.out.println();
        
        ReadAllocationPatternFromSM allocationPattern
                = new ReadAllocationPatternFromSM(returnedSM);
        
        int maxLength = 0;
        int maxLengthOfBasicPhysicalHostWord = "BasicPhysicalHost".toCharArray().length;
        int maxLengthOfListOfElasticPhysicalHostWord = "ListOfElasticPhysicalHost".toCharArray().length;
        int maxLengthOfNumberOfElasticPhysicalHostWord = "#OfElasticPhysicalHost".toCharArray().length;
        
        ArrayList<String> print_List = new ArrayList<String>();
        
        
        print_List.add("");
        print_List.add("BasicPhysicalHost");
        print_List.add("ListOfElasticPhysicalHost");
        print_List.add("#OfElasticPhysicalHost");
        
        for(int i=0;i<manifestInfo.componentId_List.size();i++)
         {
             String componentId = manifestInfo.componentId_List.get(i);
             
             String writeThis = componentId+" ";
             
             int Length = writeThis.toCharArray().length;
             if(maxLength<Length)maxLength = Length;
             
             print_List.add(writeThis);
             
             String Basic = ReadAllocationPatternFromSM.
                     getBasicPhysicalHostsListAsString(componentId, allocationPattern.Basic_Map);
             
             String Elastic = ReadAllocationPatternFromSM.
                     getElasticPhysicalHostsListAsString(componentId, allocationPattern.Elastic_Map);
             
             int NumberOfElasticPhysicalHost = 0;
             
             if(allocationPattern.Elastic_Map.containsKey(componentId))
                NumberOfElasticPhysicalHost = allocationPattern.Elastic_Map.get(componentId).size();
             
             writeThis = PrintAffinity.getStringWithSpacesAtTheEnd(Basic, maxLengthOfBasicPhysicalHostWord); 
             print_List.add(writeThis);
             
             writeThis = PrintAffinity.getStringWithSpacesAtTheEnd(Elastic, maxLengthOfListOfElasticPhysicalHostWord); 
             print_List.add(writeThis);
             
             writeThis = PrintAffinity.getStringWithSpacesAtTheEnd(Integer.toString(NumberOfElasticPhysicalHost), maxLengthOfNumberOfElasticPhysicalHostWord); 
             print_List.add(writeThis);
            
         }//for-i
        
        for(int i=0;i<print_List.size();i+=4)
         {
             if(i==0)
                System.out.print(PrintAffinity.getStringWithSpacesAtTheFront(print_List.get(i)+"   ",maxLength+3));
             else
                System.out.print(PrintAffinity.getStringWithSpacesAtTheFront(print_List.get(i)+" : ",maxLength+3));
             
             System.out.print(PrintAffinity.getStringWithSpacesAtTheFront(print_List.get(i+1)+" ",maxLengthOfBasicPhysicalHostWord+1));
             System.out.print(PrintAffinity.getStringWithSpacesAtTheFront(print_List.get(i+2)+" ",maxLengthOfListOfElasticPhysicalHostWord+1));
             System.out.println(PrintAffinity.getStringWithSpacesAtTheFront(print_List.get(i+3)+" ",maxLengthOfNumberOfElasticPhysicalHostWord+1));
         }//for-i
    }//printAllocationOfferForCertainSM()
    
}//class
