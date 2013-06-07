/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.XMLparser;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public class VirtualResourcesXML {
    
    public static ArrayList<String> parseVirtualResourcesXML(String xml_String, Logger log)
    {
            ArrayList<String> returnedList = new ArrayList<String>();
                        
            log.info("xml_String : "+xml_String);
            
            String temp[] = xml_String.replace("</listStrings>", "").split("<list>");
            
            for(int i=1;i<temp.length;i++)
            {
                String serviceId = temp[i].replace("</list>", "");
                
                log.info(serviceId);
                
                returnedList.add(serviceId);
            }//for-i
            
            return returnedList;
    }//parseVirtualResourcesXML
    
    public static ArrayList<String> parseVirtualResourcesXML(String xml_String)
    {
            ArrayList<String> returnedList = new ArrayList<String>();
            
            String temp[] = xml_String.replace("</listStrings>", "").split("<list>");
            
            for(int i=1;i<temp.length;i++)
            {
                String serviceId = temp[i].replace("</list>", "");
                
                returnedList.add(serviceId);
            }//for-i
            
            return returnedList;
    }//parseVirtualResourcesXML
    
    
}//class
