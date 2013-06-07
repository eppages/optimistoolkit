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

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IsFederationAllowedInfo {
    
    public static String extractInfoFederationIsIsAllowed(Element elem, Logger log,Boolean DisplayAllLogs)
	{
                if(DisplayAllLogs)
                log.info("isFederationAllowed invoked");
                
		String isFederationAllowed = extractInfoFederationIsIsAllowed(elem);
		
                if(DisplayAllLogs)
		log.info("isFederationAllowed : "+isFederationAllowed);
		
		return isFederationAllowed;
	}//extractInfoFederationIsIsNotAllowed()
    
    public static String extractInfoFederationIsIsAllowed(Element elem)
	{
		
                NodeList VirtualMachineDescription_nodes = elem.getElementsByTagName("opt:VirtualMachineDescription");
                
                Element VirtualMachineDescription_node = (Element) VirtualMachineDescription_nodes.item(0);
				
		String attrValue = VirtualMachineDescription_node.getAttribute("opt:isFederationAllowed"); 
                
		if(attrValue.contains("true"))attrValue="yes";
		else if(attrValue.contains("false"))attrValue="no";
		
		String isFederationAllowed = attrValue;
				
		return isFederationAllowed;
	}//extractInfoFederationIsIsNotAllowed()

}//class
