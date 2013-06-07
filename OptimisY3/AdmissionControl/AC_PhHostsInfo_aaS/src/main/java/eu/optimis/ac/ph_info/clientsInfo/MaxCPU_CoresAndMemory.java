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

import eu.optimis.ac.ACRestClients.COrestACClientText_XML;
import eu.optimis.ac.ph_info.XMLparser.Resources;
import eu.optimis.ac.ph_info.XMLparser.ResourcesXML;
import org.apache.log4j.Logger;

public class MaxCPU_CoresAndMemory {
    
        public static Resources getMaxCPU_CoresAndMemory(String host,String port, String host_id, Logger log)
        {
            log.info("get MaxCPU_Cores And Memory Started at host,port : "+host+","+port);
            
            COrestACClientText_XML client = new
                    COrestACClientText_XML(host, port, "/CloudOptimizer/physicalresources/"+host_id,log);
            
            if(client.status!=200)
            {
                String msg ="getMaxCPU_CoresAndMemory Communication with CO Rest Clien failed. Status was : "+client.status;
                log.error(msg);
                throw new RuntimeException(msg);
            }//status != 200
            
            String xml_String = client.returnedString;
            
            Resources resources = ResourcesXML.parseResourcesXML(xml_String,log);
            
            log.info("get MaxCPU_Cores And Memory Finished");
            
            return resources;
        }//getMaxCPU_CoresAndMemory()
    
        public static Resources getMaxCPU_CoresAndMemory(String host,String port, String host_id)
        {
            COrestACClientText_XML client = new
                    COrestACClientText_XML(host, port, "/CloudOptimizer/physicalresources/"+host_id);
            
            if(client.status!=200)
            {
                String msg ="getMaxCPU_CoresAndMemory Communication with CO Rest Clien failed. Status was : "+client.status;
                System.err.println(msg);
                throw new RuntimeException(msg);
            }//status != 200
            
            String xml_String = client.returnedString;
            
            Resources resources = ResourcesXML.parseResourcesXML(xml_String);
            
            return resources;
        }//getMaxCPU_CoresAndMemory()
}//class
