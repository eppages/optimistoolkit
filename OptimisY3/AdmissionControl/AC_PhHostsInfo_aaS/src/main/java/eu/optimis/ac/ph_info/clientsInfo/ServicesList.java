/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.clientsInfo;

import eu.optimis.ac.ACRestClients.COrestACClientAPPLICATION_XML;
import eu.optimis.ac.ph_info.XMLparser.VirtualResourcesXML;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class ServicesList {
    
        public static ArrayList<String> getServicesList(String host,String port, String host_id,Logger log)
        {
             log.info("get Services List Started at host,port : "+host+","+port);
            
            COrestACClientAPPLICATION_XML client = new
                COrestACClientAPPLICATION_XML(host,port,"/CloudOptimizer/virtualresources/vms/"+host_id,log);
         
            if(client.status!=200)
            {
                String msg ="getServicesList Communication with CO Rest Clien failed. Status was : "+client.status;
                log.error(msg);
                throw new RuntimeException(msg);
            }//status != 200
            
            String xml_String = client.returnedString;
            
            ArrayList<String> returnedList = VirtualResourcesXML.parseVirtualResourcesXML(xml_String, log);
            
            log.info("getServicesList Finished");
            
            return returnedList;
        }//getServicesList()
        
        public static ArrayList<String> getServicesList(String host,String port, String host_id)
        {
             
            COrestACClientAPPLICATION_XML client = new
                COrestACClientAPPLICATION_XML(host,port,"/CloudOptimizer/virtualresources/vms/"+host_id);
         
            if(client.status!=200)
            {
                String msg ="getServicesList Communication with CO Rest Clien failed. Status was : "+client.status;
                System.err.println(msg);
                throw new RuntimeException(msg);
            }//status != 200
            
            String xml_String = client.returnedString;
            
            ArrayList<String> returnedList = VirtualResourcesXML.parseVirtualResourcesXML(xml_String);
                        
            return returnedList;
        }//getServicesList()
}//class
