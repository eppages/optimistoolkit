/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.allocationOffer;

import eu.optimis.ac.admissioncontroller.utils.FileFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

class AllocationDetailsAsXMLString {
    
    
    protected static String generateXML(String GamsDirectory,ArrayList<ArrayList<String>> AllocationDetails_List,
            List<Service> serviceList,
            Map <String,List<ComponentIdentificationDetails>> Service_Component_Map,
            List<PhysicalHost> physicalHostList,
            List<Service_Component> scList,
            Logger log)
    {
        
        String allocationDetailsAsXMLString = "";
        
        log.info("AllocationDetailsAsXMLString generateXML invoked");
        
        StringBuilder strB = new StringBuilder(200);
        
        strB.append("<allocation_details>");
        
        String temp[] = FileFunctions.readFileAsStringWithPath(GamsDirectory+"services.csv", log).split(",");
        
        for(String serviceName : temp)
        
        for (Service s : serviceList) {
            
            if(serviceName.equals(s.name)==false)continue;
            
            strB.append("<service id=\"");
            strB.append(s.uniqueID);
            strB.append("\">");
            
            log.info(s.name);
            
            List<ComponentIdentificationDetails> comp_List = Service_Component_Map.get(s.name);
            
            for (ComponentIdentificationDetails component : comp_List) {
                    
                    log.info(component.name);
                    
                    String federatedVMs = null;
                    for (Service_Component sc : scList) {
                        
                        if(!s.name.equals(sc.s.name))continue;
                        if(!sc.c.name.equals(component.name))continue;
                        
                        federatedVMs = sc.VMS_FOR_FEDERATION;
                    }//for-sc
    			
                    
                    strB.append("<service_component id=\"");
                    // append components name 
                    strB.append(component.componentId);
                    strB.append("\"");
                    strB.append(" federatedVMs=\"");
                    strB.append(federatedVMs);
                    strB.append("\">");
                    
                    for (PhysicalHost p : physicalHostList) {
                        
                        log.info(p.name);
                        
                        String serv = null;
                        String comp = null;
                        String host = null;
                        String VMs = null;
            
                        for(int i=0;i<AllocationDetails_List.size();i++)
                        {
                
                            ArrayList<String> list = AllocationDetails_List.get(i);
                
                            serv = list.get(0);
                            comp = list.get(1);
                            host = list.get(2);
                            VMs = list.get(3);
                
                            if(!s.name.equals(serv))continue;
                            if(!component.name.equals(comp))continue;
                            if(!p.name.equals(host))continue;
                            
                            strB.append("<physical_host id=\""); 
                            strB.append(p.uniqueID);
                            strB.append("\">");
                            strB.append("<assigned_VMs>");
                            strB.append(VMs);
                            strB.append("</assigned_VMs>");
                            strB.append("</physical_host>");
                        }//for-i
                        
                    }//for-p
                    
                    strB.append("</service_component>");
            }//for-sc
            
            strB.append("</service>");
        }//for-s
        
        strB.append("</allocation_details>");
        
        allocationDetailsAsXMLString = strB.toString();
        
        log.info("AllocationDetailsAsXMLString generateXML finished");
        
        log.info("allocationDetailsAsXMLString :"+allocationDetailsAsXMLString);
        
        return allocationDetailsAsXMLString;
    }//generateXML()
    
}//class
