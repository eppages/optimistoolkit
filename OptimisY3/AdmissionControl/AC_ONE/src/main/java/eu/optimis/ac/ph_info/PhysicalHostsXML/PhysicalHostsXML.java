/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.PhysicalHostsXML;

import eu.optimis.ac.ACRestClients.ACgetPhysicalHostsAsXMLRestClient;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class PhysicalHostsXML {
    
    public static ArrayList<String> parsePhysicalHostsXML(String xml_String, Logger log)
    {
            ArrayList<String> returnedList = new ArrayList<String>();
            
            log.info("xml_String : "+xml_String);
            
            String temp[] = xml_String.replace("</listStrings>", "").split("<list>");
            
            for(int i=1;i<temp.length;i++)
            {
                String host_id = temp[i].replace("</list>", "");
                
                log.info(host_id);
                
                returnedList.add(host_id);
            }//for-i
            
            return returnedList;
    }//parsePhysicalHostsXML
    
    public static ArrayList<String> parsePhysicalHostsXML(String xml_String)
    {
            ArrayList<String> returnedList = new ArrayList<String>();
            
            String temp[] = xml_String.replace("</listStrings>", "").split("<list>");
            
            for(int i=1;i<temp.length;i++)
            {
                String host_id = temp[i].replace("</list>", "");
                
                returnedList.add(host_id);
            }//for-i
            
            return returnedList;
    }//parsePhysicalHostsXML
    
    public static ArrayList<String> getPhysicalHostsList(String host,String port)
   { 
            ACgetPhysicalHostsAsXMLRestClient client = new
                ACgetPhysicalHostsAsXMLRestClient(host,port);
         
            if(client.status!=200)
            {
                String msg ="Communication with CO Rest Clien failed. Status was : "+client.status;
                System.out.println(msg);
                throw new RuntimeException(msg);
            }//status != 200
            
            String xml_String = client.returnedString;
            
            ArrayList<String> returnedList = PhysicalHostsXML.parsePhysicalHostsXML(xml_String);
            
            return returnedList;
   }//getPhysicalHostsList()
    
}//class
