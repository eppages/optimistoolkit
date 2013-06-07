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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public class PrintAffinity {
    
    public static void printAffinityOfSM(ArrayList<String> componentId_List,
             MultivaluedMap<String, String> affinityConstraints_Map,
             MultivaluedMap<String, String> antiAffinityConstraints_Map,
             ArrayList<ArrayList<String>> AffinityRuleList,
             ArrayList<ArrayList<String>> AntiAffinityRuleList)
     {
         int maxLength = 0;
         int maxLengthOfAffinityWord = "Affinity".toCharArray().length;
         int maxLengthOfAntiAffinityWord = "AntiAffinity".toCharArray().length;
         
         ArrayList<String> print_List = new ArrayList<String>();
         
         print_List.add("");
         print_List.add("Affinity");
         print_List.add("AntiAffinity");
         
         for(int i=0;i<componentId_List.size();i++)
         {
             String componentId = componentId_List.get(i);
             
             String writeThis = componentId+" (Constraints)";
             
             int Length = writeThis.toCharArray().length;
             if(maxLength<Length)maxLength = Length;
             
             print_List.add(writeThis);
             
             String affinityConstraint = affinityConstraints_Map.get(componentId).get(0);
             String antiAffinityConstraint = antiAffinityConstraints_Map.get(componentId).get(0);
             
             writeThis = getStringWithSpacesAtTheEnd(affinityConstraint, maxLengthOfAffinityWord); 
             print_List.add(writeThis);
             
             writeThis = getStringWithSpacesAtTheEnd(antiAffinityConstraint, maxLengthOfAntiAffinityWord); 
             print_List.add(writeThis);
         }//for-i
         
         
         
         ArrayList<String> rule_List = new ArrayList<String>();
         Map <String,String> AffinityRule_Map = new HashMap<String, String>();
         Map <String,String> AntiAffinityRule_Map = new HashMap<String, String>();
         
         for(int i=0;i<AffinityRuleList.size();i++)
         {
             ArrayList<String> rule = AffinityRuleList.get(i);
             
             String rule_value = rule.get(rule.size()-1);
             
             String rule_name = getRuleName(rule);
             
             AffinityRule_Map.put(rule_name, rule_value);
             
             if(rule_List.contains(rule_name))continue;
             
             rule_List.add(rule_name);
             
         }//for-i    
         for(int i=0;i<AntiAffinityRuleList.size();i++)
         {
             ArrayList<String> rule = AntiAffinityRuleList.get(i);
             
             String rule_value = rule.get(rule.size()-1);
             
             String rule_name = getRuleName(rule);
             
             AntiAffinityRule_Map.put(rule_name, rule_value);
             
             if(rule_List.contains(rule_name))continue;
             
             rule_List.add(rule_name);
             
         }//for-i
         
         for(int i=0;i<rule_List.size();i++)
         {
             String id = rule_List.get(i);
             
             print_List.add(id+" (Rule)");
             print_List.add(getStringWithSpacesAtTheEnd(AffinityRule_Map.get(id), maxLengthOfAffinityWord));
             print_List.add(getStringWithSpacesAtTheEnd(AntiAffinityRule_Map.get(id), maxLengthOfAntiAffinityWord));
         }//for-i
         
         
         for(int i=0;i<print_List.size();i+=3)
         {
             if(i==0)
                System.out.print(getStringWithSpacesAtTheFront(print_List.get(i)+"   ",maxLength+3));
             else
                System.out.print(getStringWithSpacesAtTheFront(print_List.get(i)+" : ",maxLength+3));
             
             System.out.print(getStringWithSpacesAtTheFront(print_List.get(i+1)+" ",maxLengthOfAffinityWord+1));
             System.out.println(getStringWithSpacesAtTheFront(print_List.get(i+2)+" ",maxLengthOfAntiAffinityWord+1));
         }//for-i
         
     }//printAffinityOfSM()
     
    
    private static String getRuleName(ArrayList<String> RuleList)
    {
        String result = RuleList.get(0);
        
        for(int i=1;i<RuleList.size()-1;i++)
        {
            result += ", "+RuleList.get(i);
        }//for-i
        
        return result;
    }//getRuleName()
    
     public static String getStringWithSpacesAtTheEnd(String str, int max)
     {
         int Length = str.toCharArray().length;
         
         if(max==Length)return str;
         
         String result = str;
         
         for(int i=Length;i<max;i++)
             result= result+" ";
         
         return result;
     }//getStringWithSpacesAtTheEnd
     
     public static String getStringWithSpacesAtTheFront(String str, int max)
     {
         int Length = str.toCharArray().length;
         
         if(max==Length)return str;
         
         String result = str;
         
         for(int i=Length;i<max;i++)
             result= " "+result;
         
         return result;
     }//getStringWithSpacesAtTheFront
}//class
