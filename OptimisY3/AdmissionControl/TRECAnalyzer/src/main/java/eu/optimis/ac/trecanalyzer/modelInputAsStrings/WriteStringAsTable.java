/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.modelInputAsStrings;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.log4j.Logger;


public class WriteStringAsTable {
    
    public static String WriteStringFileAsTable(String GamsPath,LinkedList<LinkedList<String>> List,String ValueName
    		,String ValueForNotExistance,Logger log) throws IOException,FileNotFoundException
	{
		StringWriter writer = new StringWriter();
	    
		int maxListSize=0;
		for(int i=0;i<List.size();i++)
		{
                        
			LinkedList<String> miniList = List.get(i);
                        
			if(maxListSize<miniList.size())
				maxListSize=miniList.size();	
		}//for
		
		//Write First Line
		
		writer.append("dummy,");
                
	    for(int i=1;i<=List.size();i++)
		{
	    	writer.append("a"+i);
	    	
		    if(i!=List.size())
		    	writer.append(',');
		    	
		}//for
	    writer.append('\n');
	    
	    int LineCounter=1;
		while(LineCounter<=maxListSize)
		{
			//Write vm+number
			writer.append("vm"+LineCounter);
			writer.append(",");
			
			for(int i=0;i<List.size();i++)
			{
				
				LinkedList<String> miniList = List.get(i);
				
				String value=ValueForNotExistance;
				
				if(miniList.size()>=LineCounter)
					value=miniList.get(LineCounter-1);
				
                                if(value==null)
                                    value="Low";
                                
                                if(value.contains("Low"))
                                    value="0";
                                else if(value.contains("Medium"))
                                    value="1";
                                else if(value.contains("High"))
                                    value="2";
                                
				writer.append(value);
		    	
			    if(i==List.size()-1)
			    	writer.append('\n');
			    else
			    	writer.append(',');
			}//for
			
			LineCounter++;
		}//while
	        
	    writer.flush();
	    writer.close();
            
            return writer.toString();
	}//WriteStringFileAsTable(LinkedList<LinkedList<String>> List)
    
    public static String WriteStringFileAsTable2(String GamsPath,ArrayList<String> ListOfCompinations,int maxNumberOfComponents,String ValueName)
    		throws IOException,FileNotFoundException
	{
		StringWriter writer = new StringWriter();
		
		//Write First Line
		
		writer.append("dummy,");
	    for(int i=1;i<=maxNumberOfComponents;i++)
		{
	    	writer.append("vm"+i);
	    	
		    if(i!=maxNumberOfComponents)
		    	writer.append(',');
		    	
		}//for
	    writer.append('\n');
	    
	    int LineCounter=1;
		while(LineCounter<=ListOfCompinations.size())
		{
			//Write vm+number
			writer.append("c"+LineCounter);
			writer.append(",");
			
			String value=ListOfCompinations.get(LineCounter-1).replace("vm","");
			
			ArrayList<String> ListOfExisted = new ArrayList<String>();
			String[] temp;
			String delimiter = "-";
			temp = value.split(delimiter);
			for(int j =0; j < temp.length ; j++)
				ListOfExisted.add(temp[j]);
				
			
			for(int i=1;i<=maxNumberOfComponents;i++)
			{
				
				String writeValue="0";
				
				if(ListOfExisted.contains(Integer.toString(i)))
					writeValue="1";
				
				writer.append(writeValue);
		    	
			    if(i==maxNumberOfComponents)
			    	writer.append('\n');
			    else
			    	writer.append(',');
			}//for
			
			LineCounter++;
		}//while
	        
	    writer.flush();
	    writer.close();
	
            return writer.toString();
	}//WriteStringFileAsTable2()
    
    public static String WriteStringFileAsTable3(String GamsPath,ArrayList<ArrayList<ArrayList<String>>> List,ArrayList<String> ListOfCompinations,
    		String ValueName, Logger log, LinkedList<LinkedList<String>> Ids_List)
    		throws IOException,FileNotFoundException
	{
		StringWriter writer = new StringWriter();
		
		//Write First Line
		
		writer.append("dummy,");
	    for(int i=1;i<=ListOfCompinations.size();i++)
		{
	    	writer.append("c"+i);
	    	
		    if(i!=ListOfCompinations.size())
		    	writer.append(',');
		    	
		}//for
	    writer.append('\n');
			
			for(int i=0;i<List.size();i++)
			{
				
				//Write a+number
				writer.append("a"+(i+1));
				writer.append(",");
				
				ArrayList<ArrayList<String>> miniList = List.get(i);
				LinkedList<String> componentId_List = Ids_List.get(i);
                                        
				ArrayList<String> compination_List = new ArrayList<String>();
				ArrayList<String> rule_List = new ArrayList<String>();
				
				for(int j=0;j<miniList.size();j++)
				{
					String compination = null;
					String rule = null;
					ArrayList<String> ruleList = miniList.get(j);
					for(int y=0;y<ruleList.size();y++)
					{
						if(ruleList.get(y).contains("Low"))
							rule = "0";
						else if(ruleList.get(y).contains("Medium"))
							rule = "1";
						else if(ruleList.get(y).contains("High"))
							rule = "2";
						else
						{
                                                        String str = ruleList.get(y);
                                                        str = "vm"+getPositionIn_componentId_List(str,componentId_List);
                                                        
							if(compination == null)
								compination = str+"-";
							else
								compination += str+"-";
						}//else
						
					}//for-y
					
					compination_List.add(compination);
					rule_List.add(rule);
					
					log.info("a"+(i+1)+" => compination :"+compination +" rule :"+rule);
				}//for-j each compination
				
				
				for(int x=0;x<ListOfCompinations.size();x++)
				{
					String value = ListOfCompinations.get(x);
					
					if(!compination_List.contains(value))
						writer.append("0");
					else
					{
						int pos = find_position(value,compination_List);
						String ruleString = rule_List.get(pos);
						writer.append(ruleString);
					}//else
					
					if(x==ListOfCompinations.size()-1)
				    	writer.append('\n');
				    else
				    	writer.append(',');
						
				}//for-x, each compination
		
			}//for-i each service (a)
			  
	    writer.flush();
	    writer.close();
	
            return writer.toString();
	}//WriteStringFileAsTable3()
        
         private static int find_position(String str,ArrayList<String> compination_List)
         {
		for(int i=0;i<compination_List.size();i++)
		{
			if(compination_List.get(i).contains(str))
				return i;
		}//for
		
		return -1;
         }//find_position()
         
         private static int getPositionIn_componentId_List(String str, LinkedList<String> componentId_List)
		{
			for(int i=0;i<componentId_List.size();i++)
			{
				if(componentId_List.get(i).contains(str))
					return i+1;
			}//for
			
			return -1;
		}//getPositionIn_componentId_List
}//class
