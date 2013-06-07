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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExtractInfo {
    
    public MultivaluedMap<String, String> componentId_Map_ServiceComponentId = new MultivaluedMapImpl();
    public MultivaluedMap<String, String> ServiceComponentId_Map_componentId = new MultivaluedMapImpl();

    public MultivaluedMap<String, String> affinityConstraints_Map = new MultivaluedMapImpl();
    public MultivaluedMap<String, String> antiAffinityConstraints_Map = new MultivaluedMapImpl();
    
    public ArrayList<ServiceComponentInfo> serviceComponents = new ArrayList<ServiceComponentInfo>();
    
    public ExtractInfo(NodeList VirtualMachineComponent_nodes,Logger log,Boolean DisplayAllLogs)
    {
        extractServiceComponentInfo(VirtualMachineComponent_nodes,log,DisplayAllLogs);
        
    }//Constructor
    
    private void extractServiceComponentInfo(NodeList VirtualMachineComponent_nodes,Logger log,Boolean DisplayAllLogs)
	{
                //----start of each VirtualMachimeComponent
		
		// for every service component
		Element VMC_node = null;
		for (int  z = 0; z < VirtualMachineComponent_nodes.getLength(); z++) {
			
			VMC_node = (Element) VirtualMachineComponent_nodes.item(z);
			
			ServiceComponentInfo serviceComponent = extractInfoFromVMC(VMC_node,log,DisplayAllLogs);
			
			serviceComponents.add(serviceComponent);
		}//for -z
	
		//----end of each VirtualMachimeComponent

	}//extractServiceComponentInfo()
    
    private ServiceComponentInfo extractInfoFromVMC(Element VMC_node,Logger log,Boolean DisplayAllLogs)
	{
		// ServiceComponentInfo to store necessary values 
		ServiceComponentInfo sc_info = new ServiceComponentInfo();
		
		String attrValue = VMC_node.getAttribute("opt:componentId");
		String componentId = attrValue;
		
                if(DisplayAllLogs)
		log.info("componentId : "+componentId);
		
		NodeList VirtualMachineComponent_children = VMC_node.getChildNodes();
		Node tmp_node = null;
		Element tmp_elem = null;
						
		for (int i = 0; i < VirtualMachineComponent_children.getLength(); i++) {
			tmp_node = VirtualMachineComponent_children.item(i);
					
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) {						
				tmp_elem = (Element) VirtualMachineComponent_children.item(i);
				
				if (tmp_elem.getTagName().equals("opt:OVFDefinition")) {
					
					String serviceComponentId = ServiceComponentId.extractServiceComponentIdFromVMC(tmp_elem,log,DisplayAllLogs);
					
					componentId_Map_ServiceComponentId.add(componentId,serviceComponentId);
					ServiceComponentId_Map_componentId.add(serviceComponentId,componentId);
                                        
                                        if(DisplayAllLogs)
					log.info("ServiceComponentId="+serviceComponentId);
					
					sc_info.setId(serviceComponentId);
					
					int numberOfVirtualCpus = VirtualCpus.extractVirtualCpusFromVMC(tmp_elem,log,DisplayAllLogs);
					
                                        int memoryInMBs = Memory.extractMemoryFromVMC(tmp_elem, log, DisplayAllLogs);
                                        
					sc_info.setVirtualCpus(numberOfVirtualCpus);
					sc_info.setMemoryInMBs(memoryInMBs);
				}//if-opt:OVFDefinition
				
				if (tmp_elem.getTagName().equals("opt:AllocationConstraints")) {
					
					AllocationConstraints.getAllocationConstraints(
                                                tmp_elem,sc_info,componentId,log,DisplayAllLogs);
                                        
				}//if-opt:AllocationConstraints					
				
				if (tmp_elem.getTagName().equals("opt:AffinityConstraints")) {
					
                                    String AffinityConstraint = 
					AffinityConstraints.extractAffinityConstraints(tmp_elem,sc_info,log,"Affinity",DisplayAllLogs);
					
                                         affinityConstraints_Map.add(componentId,AffinityConstraint);
				}//if-opt:AffinityConstraints
				
                                if (tmp_elem.getTagName().equals("opt:AntiAffinityConstraints")) {
					
                                    String AntiAffinityConstraint = 
					AffinityConstraints.extractAffinityConstraints(tmp_elem,sc_info,log,"AntiAffinity",DisplayAllLogs);
					
                                        antiAffinityConstraints_Map.add(componentId,AntiAffinityConstraint);
				}//if-opt:AntiAffinityConstraints
				
			}//if-ELEMENT_NODE
			
		}//for-i
		
		return sc_info;
	}//extractInfoFromVMC()
	
}//class
