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

public class Availability {
    
    public MultivaluedMap<String, String> availability_Map = new MultivaluedMapImpl();
    public ArrayList<String> componentId_KeyList = new ArrayList<String>();
    
    public int Max_Availability = 0;
    private String Default_Availability = "100";
    
    public Availability(Element elem, Logger log, Boolean DisplayAllLogs)
    {
        NodeList TRECSection_nodes = elem.getElementsByTagName("opt:TRECSection");
		
	Element TREC_node = (Element) TRECSection_nodes.item(0);
		
	NodeList RiskSection_nodes = TREC_node.getElementsByTagName("opt:RiskSection");
        
        for(int r=0;r<RiskSection_nodes.getLength();r++)
        {
            Element RiskSection_node = (Element) RiskSection_nodes.item(r);
            
            extractAvailabilityInfo(RiskSection_node,log, DisplayAllLogs);
            
        }//for -r, Risk Section
        
        setMaxAvailability(log,DisplayAllLogs);
    }//Constructor
    
    private void extractAvailabilityInfo(Element RiskSection_node,Logger log, Boolean DisplayAllLogs)
	{
			NodeList RiskSection_children = RiskSection_node.getChildNodes();
			
			Node t_node = null;
			Element t_elem = null;
			ArrayList<String> componentId_List = new ArrayList<String>();
                        
			for (int i = 0; i < RiskSection_children.getLength(); i++) 
                        {
                            
                            t_node = RiskSection_children.item(i);
				
				if (t_node.getNodeType() == Node.ELEMENT_NODE) {
					
                                        String AvailabilityPM = null;
                                        
					t_elem = (Element) RiskSection_children.item(i);
					
					if (t_elem.getTagName().equals("opt:Scope")) {
					    
                                            componentId_List.clear();	
                                            
                                            componentId_List = getComponentId(t_elem, log, DisplayAllLogs);
                                            
					}//if-opt:Scope
					
					if (t_elem.getTagName().equals("opt:AvailabilityArray")) {
					
                                            AvailabilityPM = getAvailabilityPerMonth(t_elem, log,DisplayAllLogs);
                                            
                                            for(int j=0;j<componentId_List.size();j++)
                                            {
                                                String componentId = componentId_List.get(j);

                                                availability_Map.add(componentId,AvailabilityPM);
                                          
                                                componentId_KeyList.add(componentId);
                                          
                                            }//for-j
                                            
					}//if-opt:AvailabilityArray
                                      
				}//if-ELEMENT_NODE
			}//for-i		
			
	}//extractAvailabilityInfo()
    
    
    private ArrayList<String> getComponentId(Element t_elem, Logger log, Boolean DisplayAllLogs)
    {
        ArrayList<String> componentId_List = new ArrayList<String>();
        
        NodeList componentId_node = t_elem.getElementsByTagName("opt:ComponentId");
        
       for(int i=0;i<componentId_node.getLength();i++)
       {
           String componentId = ((Element) componentId_node.item(i)).getTextContent();
           
           if(DisplayAllLogs)
           log.info("Availability componentId :"+componentId);
           
           componentId_List.add(componentId);
       }//for-i
       
       return componentId_List;
    }//getComponentId()        
    
    private String getAvailabilityPerMonth(Element t_elem, Logger log, Boolean DisplayAllLogs)
    {
        NodeList availability_node = t_elem.getElementsByTagName("opt:Availability");
	
        if(availability_node.getLength()==0)
        {
            if(DisplayAllLogs)
            log.info("No availability Info in the Manifest, Returning Default Availability");
            return Default_Availability;
        }
        
        String availability_str = ((Element) availability_node.item(0)).getTextContent();
        
        availability_str =convertAvailabilityStringToInteger(availability_str);
        
        if(DisplayAllLogs)
        log.info("Availability is :"+availability_str);
        
        return availability_str;
    }//getAvailabilityPerMonth()
    
    protected static String convertAvailabilityStringToInteger(String availability_str)
    {
        String temp[]=availability_str.replace("."," ").split(" ");
			
	return temp[0];
    }//convertAvailabilityStringToInteger()
    
    private void setMaxAvailability(Logger log,Boolean DisplayAllLogs)
    {
        String Component_Id_withMax_Availability = null;
        
        for(int i=0;i<componentId_KeyList.size();i++)
        {
            String componentId = componentId_KeyList.get(i);
            
            String availability_str = availability_Map.get(componentId).get(0);
            
            if(Max_Availability< Integer.parseInt(availability_str))
            {
                Max_Availability = Integer.parseInt(availability_str);
                Component_Id_withMax_Availability = componentId;
            }
        }//for-i
        
        if(DisplayAllLogs)
        log.info("Component_Id_withMax_Availability : "+Component_Id_withMax_Availability);
        
    }//selectMaxAvailability()
    
}//class
