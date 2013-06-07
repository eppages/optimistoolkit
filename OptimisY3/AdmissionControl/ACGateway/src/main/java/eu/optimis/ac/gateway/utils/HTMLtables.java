/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.utils;

import java.util.ArrayList;

public class HTMLtables
{	
	public static String CreateHTMLtable(String border,String Position11,
			ArrayList<String> Header_List,ArrayList<String> Tag_List,
			ArrayList<ArrayList<String>> List)
	{	
            
                
		String result="<table border="+'"'+border+'"'+">";
	                
		result+=CreateTableHeader(Position11,Header_List);
		
		for(int i=0;i<Tag_List.size();i++)
		{
			String TagName=Tag_List.get(i);
			
			result+="<tr>"+"<th>"+writeTag(TagName)+"</th>";
			
			for(int j=0;j<List.size();j++)
			{
				ArrayList<String> myList = new ArrayList<String>();
				myList = List.get(j);
	
				result+="<th>"+writeToTable(myList.get(i))+"</th>";
			}//for -j, each list
			
			result+="</tr>";
		}//for each line
		
		result+="</table>";
		
		return result;
	}//CreateHTMLtable()
	
	private static String CreateTableHeader(String Position11,ArrayList<String> List)
	{
		String result="<tr>";
		
		result+="<th>"+writeHeaderTable(Position11)+"</th>";
		
		for(int i=0;i<List.size();i++)
		{
			result+="<th>"+writeHeaderTable(List.get(i))+"</th>";
			
		}//for -i,
		
		result+="</tr>";
		
		return result;
	}//CreateTableHeader()
	
	private static String addColor(String str,String color)
	{
		String result="";
		
		result="<font color="+'"'+color+'"'+">";
		result+=str;
		result+="</font>";
		
		return result;
	}//addColor(String str,String color)
	
	private static String writeHeaderTable(String str)
	{
		String result="";
		
		result=addColor(str,"15428b");
		
		return result;
	}//writeHeaderTable()
	
	private static String writeTag(String str)
	{
		String result="";
		
		result=addColor(str,"DodgerBlue");
		
		return result;
		
	}//writeTag(String str)
	
	private static String writeToTable(String str)
	{
		String result="";
		
		//result=addColor(str,"blue");
		
		result = "<h4>"+str +"</h4>";
		
		return result;
		
	}//writeToTable(String str)
	
}//class