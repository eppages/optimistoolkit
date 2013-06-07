/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.analyzeAllocationDetails;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AllocationDetailsInfo {
    
    public static NodeList getAllocationDetailsForCertainServiceForCertainComponent
            (String AllocationDetails, int certainService, int certainComponent)
    {
        try {
            
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					
            builderFactory.setValidating(false);
            builderFactory.setIgnoringElementContentWhitespace(true);
				
            // parse the xml string 
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
            InputSource in = new InputSource();							
            in.setCharacterStream(new StringReader(AllocationDetails));				
            Document document = builder.parse(in);
        
            Element element = document.getDocumentElement();
            
            NodeList Services_nodes = element.getElementsByTagName("service");
            
            for  (int i = 0; i < Services_nodes.getLength(); i++) 
            {
                if((i+1)!=certainService)continue;
                
                Element Service_node = (Element) Services_nodes.item(i);
                
                //String serviceId = 
                		Service_node.getAttribute("id");
                
                NodeList Components_nodes = Service_node.getElementsByTagName("service_component");
                
                for  (int j = 0; j < Components_nodes.getLength(); j++) 
                {
                    if((j+1)!=certainComponent)continue;
                    
                    Element Component_node = (Element) Components_nodes.item(j);
                
                    //String componentId = 
                    		Component_node.getAttribute("id");
                
                    NodeList PhysicalHosts_nodes = Component_node.getElementsByTagName("physical_host");
                    
                    return PhysicalHosts_nodes;
                }//for-j
            }//for-i
            
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            }//catch 
        
            return null;
            
    }//getAllocationDetailsForCertainServiceForCertainComponent()  
    
    public static ArrayList<String> getListOfPhysicalHostsInvolvedInTheAllocationDetailsForCertainComponent(NodeList the_PhysicalHosts_nodes)
    {
        ArrayList<String> list = new ArrayList<String>();
        
        for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++) 
        {
            
            Element PhysicalHost_node = (Element) the_PhysicalHosts_nodes.item(z);
            
            String physicalHostId = PhysicalHost_node.getAttribute("id");
            
            int AssignedVMs = -1;
            
            Node tmp_node = null;
            Element tmp_elem = null;
            NodeList PhysicalHost_children = PhysicalHost_node.getChildNodes();
            
            for (int i = 0; i < PhysicalHost_children.getLength(); i++) {
			tmp_node = PhysicalHost_children.item(i);
					
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) 
                        {						
				
                            tmp_elem = (Element) PhysicalHost_children.item(i);
                                
                            AssignedVMs = Integer.parseInt(tmp_elem.getTextContent());
                            
                        }//if             
            }//for-i
                       
            if(AssignedVMs>0)
                list.add(physicalHostId);
        }//for-z
        
        return list;
    }//getListOfPhysicalHostsInvolvedInTheAllocationDetailsForCertainComponent()
    
    public static int getNumberOfPhysicalHostsInvolvedInTheAllocationDetails(NodeList the_PhysicalHosts_nodes)
    {
        int numberOfInvolvedPhysicalHosts = 0;
        
        for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++) 
        {
            
            Element PhysicalHost_node = (Element) the_PhysicalHosts_nodes.item(z);
            
            //String physicalHostId = 
            		PhysicalHost_node.getAttribute("id");
            
            int AssignedVMs = -1;
            
            Node tmp_node = null;
            Element tmp_elem = null;
            NodeList PhysicalHost_children = PhysicalHost_node.getChildNodes();
            
            for (int i = 0; i < PhysicalHost_children.getLength(); i++) {
			tmp_node = PhysicalHost_children.item(i);
					
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) 
                        {						
				
                            tmp_elem = (Element) PhysicalHost_children.item(i);
                                
                            AssignedVMs = Integer.parseInt(tmp_elem.getTextContent());
                            
                        }//if
                                
            }//for-i
                       
            if(AssignedVMs>0)
                numberOfInvolvedPhysicalHosts++;
        }//for-z
        
        return numberOfInvolvedPhysicalHosts;
    }//getNumberOfPhysicalHostsInvolvedInTheAllocationDetails()
    
    
    public static int getCertainPhysicalHostAssignedVMsFromTheAllocationDetails(NodeList the_PhysicalHosts_nodes, int certainPhysicalHost)
    {
        
        for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++) 
        {
            if((z+1)!=certainPhysicalHost)continue;
            
            Element PhysicalHost_node = (Element) the_PhysicalHosts_nodes.item(z);
            
            int AssignedVMs = -1;
            
            Node tmp_node = null;
            Element tmp_elem = null;
            NodeList PhysicalHost_children = PhysicalHost_node.getChildNodes();
            
            for (int i = 0; i < PhysicalHost_children.getLength(); i++) {
			tmp_node = PhysicalHost_children.item(i);
					
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) 
                        {						
				
                            tmp_elem = (Element) PhysicalHost_children.item(i);
                                
                            AssignedVMs = Integer.parseInt(tmp_elem.getTextContent());
                            
                            return AssignedVMs;
                        }//if
                                
            }//for-i
            
        }//for-z
        
        return -1;
    }//getCertainPhysicalHostAssignedVMsFromTheAllocationDetails()
    
    public static String getCertainPhysicalHostIdFromTheAllocationDetails(NodeList the_PhysicalHosts_nodes, int certainPhysicalHost)
    {    
        for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++) 
        {
            if((z+1)!=certainPhysicalHost)continue;
            
            Element PhysicalHost_node = (Element) the_PhysicalHosts_nodes.item(z);
            
            String physicalHostId = PhysicalHost_node.getAttribute("id");
            
            return physicalHostId;
            
        }//for-z
        
        return null;
    }//getCertainPhysicalHostIdFromTheAllocationDetails()
    
    
    
    public static int getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails(NodeList the_PhysicalHosts_nodes)
    {
        int sum = 0;
        
        for  (int z = 0; z < the_PhysicalHosts_nodes.getLength(); z++) 
        {
            
            Element PhysicalHost_node = (Element) the_PhysicalHosts_nodes.item(z);
            
            int AssingedVMs = -1;
            
            Node tmp_node = null;
            Element tmp_elem = null;
            NodeList PhysicalHost_children = PhysicalHost_node.getChildNodes();
            
            for (int i = 0; i < PhysicalHost_children.getLength(); i++) {
			tmp_node = PhysicalHost_children.item(i);
					
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) 
                        {						
				
                            tmp_elem = (Element) PhysicalHost_children.item(i);
                                
                            AssingedVMs = Integer.parseInt(tmp_elem.getTextContent());
                            
                            sum += AssingedVMs;
                        }//if                    
            }//for-i
            
        }//for-z
        
        return sum;
    }//getSumOfAssignedVMsOfAllPhysicalHostsInvolvedInTheAllocationDetails()
    
}//class
