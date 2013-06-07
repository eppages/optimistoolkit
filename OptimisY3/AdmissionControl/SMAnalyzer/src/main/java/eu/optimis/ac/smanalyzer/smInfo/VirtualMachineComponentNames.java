/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.optimis.ac.smanalyzer.smInfo;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class VirtualMachineComponentNames {   
    
    public static ArrayList<String> getVirtualMachineComponentNames(NodeList VirtualMachineComponent_nodes, int numberOfServiceComponents,Logger log, Boolean DisplayAllLogs)
    {
        ArrayList<String> componentId_List = new ArrayList<String>();
        
        // for every service component
        Element VMC_node = null;
        for (int  z = 0; z < numberOfServiceComponents; z++) 
        {
			
                VMC_node = (Element) VirtualMachineComponent_nodes.item(z);
			
		String serviceComponent = extractNamesFromVMC(VMC_node,z+1,numberOfServiceComponents,log,DisplayAllLogs);
		
                if(DisplayAllLogs)
                log.info("serviceComponent "+(z+1)+"/"+numberOfServiceComponents+" : "+serviceComponent);
                componentId_List.add(serviceComponent);
        }//for -z
            
        return componentId_List;
    }//getVirtualMachineComponentNames()
    
    public static ArrayList<String> getVirtualMachineComponentNames(NodeList VirtualMachineComponent_nodes, int numberOfServiceComponents)
    {
        ArrayList<String> componentId_List = new ArrayList<String>();
        
        // for every service component
        Element VMC_node = null;
        for (int  z = 0; z < numberOfServiceComponents; z++) 
        {
                VMC_node = (Element) VirtualMachineComponent_nodes.item(z);
			
		String serviceComponent = extractNamesFromVMC(VMC_node);
			
                componentId_List.add(serviceComponent);
        }//for -z
            
        return componentId_List;
    }//getVirtualMachineComponentNames()
    
    private static String extractNamesFromVMC(Element VMC_node,int component_list_number,int component_list_size,Logger log, Boolean DisplayAllLogs)
    {
        if(DisplayAllLogs)
	log.info("Ready to extract Info for component : "+
                    component_list_number+"/"+component_list_size);
		
	String componentId = extractNamesFromVMC(VMC_node);
	
        if(DisplayAllLogs)
	log.info("componentId : "+componentId);
		
        return componentId;
        
    }//extractNamesFromVMC()
    
    private static String extractNamesFromVMC(Element VMC_node)		
    {
        
	String attrValue = VMC_node.getAttribute("opt:componentId");
	String componentId = attrValue;
		
        return componentId;
                
    }//extractNamesFromVMC()
    
}//class
