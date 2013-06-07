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
import eu.optimis.ac.ph_info.XMLparser.Resources;
import eu.optimis.ac.ph_info.XMLparser.ResourcesXML;
import java.util.ArrayList;
import org.apache.log4j.Logger;


public class UsedCPU_CoresAndMemory {
    
    public static Resources getUsedCPU_CoresAndMemory(String host,String port, String host_id, Logger log)
        {
            log.info("get Used CPU_Cores And Memory Started at host,port : "+host+","+port);
            
            int UsedCPU_Cores = 0;
            int UsedMemory = 0;
            
            ArrayList<String> servicesList  = ServicesList.getServicesList(host,port,host_id,log);           

            for(String serviceId : servicesList)
            {
                
                log.info("serviceId : "+serviceId);
                
                COrestACClientAPPLICATION_XML client = new
                    COrestACClientAPPLICATION_XML(host, port, "/CloudOptimizer/virtualresources/"+serviceId,log);
            
                if(client.status!=200)
                {
                    String msg ="getUsedCPU_CoresAndMemory Communication with CO Rest Clien failed. Status was : "+client.status;
                    log.error(msg);
                    throw new RuntimeException(msg);
                }//status != 200
            
                String xml_String = client.returnedString;
            
                Resources resources = ResourcesXML.parseResourcesXML(xml_String,log);
                
                log.info("UsedCPU_Cores : "+resources.getCpu_Cores());
                log.info("UsedMemory : "+resources.getMemory());
                
                UsedCPU_Cores += resources.getCpu_Cores();
                UsedMemory += resources.getMemory();
            }//for
            
            log.info("Total UsedCPU_Cores : "+UsedCPU_Cores);
            log.info("Total UsedMemory : "+UsedMemory);
            
            log.info("get UsedCPU_Cores And Memory Finished");
            
            return new Resources(UsedCPU_Cores,UsedMemory);
        }//getUsedCPU_CoresAndMemory()
        
        public static Resources getUsedCPU_CoresAndMemory(String host,String port, String host_id)
        {
            
            int UsedCPU_Cores = 0;
            int UsedMemory = 0;
            
            ArrayList<String> servicesList  = ServicesList.getServicesList(host,port,host_id);           

            for(String serviceId : servicesList)
            {
                                
                COrestACClientAPPLICATION_XML client = new
                    COrestACClientAPPLICATION_XML(host, port, "/CloudOptimizer/virtualresources/"+serviceId);
            
                if(client.status!=200)
                {
                    String msg ="getUsedCPU_CoresAndMemory Communication with CO Rest Clien failed. Status was : "+client.status;
                    System.err.println(msg);
                    throw new RuntimeException(msg);
                }//status != 200
            
                String xml_String = client.returnedString;
            
                Resources resources = ResourcesXML.parseResourcesXML(xml_String);
                
                UsedCPU_Cores += resources.getCpu_Cores();
                UsedMemory += resources.getMemory();
            }//for
            
            return new Resources(UsedCPU_Cores,UsedMemory);
        }//getUsedCPU_CoresAndMemory()
}//class
