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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AffinityRule {
    
    public ArrayList<ArrayList<String>> AffinityRuleList = new ArrayList<ArrayList<String>>();
    
    public AffinityRule(Element elem,Logger log,String Tag,Boolean DisplayAllLogs)
    {
        extractAffinityRuleInfo(elem,log,Tag,DisplayAllLogs);
        
    }//Constructor
    
    private void extractAffinityRuleInfo(Element elem,Logger log,String Tag,Boolean DisplayAllLogs)
    {
		//----start of each AffinityRule
		NodeList AffinityRule_nodes = elem.getElementsByTagName("opt:"+Tag+"Rule");
		
                if(DisplayAllLogs)
		log.info("numberOf"+Tag+"Rules : "+AffinityRule_nodes.getLength());
			
		// for every AffinityRule
		Element AffinityRule_node = null;
		for (int  z = 0; z < AffinityRule_nodes.getLength(); z++) {
			
			AffinityRule_node = (Element) AffinityRule_nodes.item(z);
			
                        extractInfoFromAffinityRule(AffinityRule_node,log,Tag, DisplayAllLogs);
			
		}//for -z
		
		//----end of each AffinityRule
				
    }//extractAffinityRuleInfo()
    
    private void extractInfoFromAffinityRule(Element AffinityRule_node,Logger log,String Tag, Boolean DisplayAllLogs)
	{
                if(DisplayAllLogs)
		log.info("Start of extractInfoFrom"+Tag+"Rule.");
                
                ArrayList<String> list = new ArrayList<String>();
                
		NodeList AffinityRule_children = AffinityRule_node.getChildNodes();
		
		Node tmp_node = null;
		Element tmp_elem = null;
						
		for (int i = 0; i < AffinityRule_children.getLength(); i++) {
			tmp_node = AffinityRule_children.item(i);
			
			if (tmp_node.getNodeType() == Node.ELEMENT_NODE) {						
				tmp_elem = (Element) AffinityRule_children.item(i);
				
				if (tmp_elem.getTagName().equals("opt:Scope")) {
					
                                        list.clear();	
                                            
                                        list = getComponentId(tmp_elem, log,Tag, DisplayAllLogs);
                                        
				}//if opt:Scope
				
				if (tmp_elem.getTagName().equals("opt:"+Tag+"Constraints")) {
					
                                        String Rule = tmp_elem.getTextContent();
                                        
                                        list.add(Rule);
					AffinityRuleList.add(list);
                                        
                                        if(DisplayAllLogs)
                                        log.info(Tag+" : "+list);
                                        if(DisplayAllLogs)
					log.info(Tag+"Constraints : "+Rule);
					
				}//if opt:AffinityConstraints
				
			}//if ELEMENT_NODE
			
		}//for-i
		
                if(DisplayAllLogs)
                log.info(Tag+" : "+AffinityRuleList);
                if(DisplayAllLogs)
		log.info("End of extractInfoFrom"+Tag+"Rule.");
		
	}//extractInfoFromAffinityRule()
    
    private ArrayList<String> getComponentId(Element t_elem, Logger log,String Tag, Boolean DisplayAllLogs)
    {
        ArrayList<String> componentId_List = new ArrayList<String>();
        
        NodeList componentId_node = t_elem.getElementsByTagName("opt:ComponentId");
        
       for(int i=0;i<componentId_node.getLength();i++)
       {
           String componentId = ((Element) componentId_node.item(i)).getTextContent();
           
           if(DisplayAllLogs)
           log.info(Tag+" componentId :"+componentId);
           
           componentId_List.add(componentId);
       }//for-i
       
       return componentId_List;
    }//getComponentId()
}//class
