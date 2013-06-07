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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ServiceComponentId {
    
        //Get Unique ComponentId
	//for each ServiceComonent
	protected static String extractServiceComponentIdFromVMC(Element manifest,Logger log,Boolean DisplayAllLogs)
			
	{
                if(DisplayAllLogs)
		log.info("Start of extractServiceComponentIdFromVMC.");
		
		String ServiceComponentId = null;
		
		NodeList OVFDefinition_children = manifest.getChildNodes();
		Node tmp_node = null;
		Element tmp_elem = null;

		for (int i = 0; i < OVFDefinition_children.getLength(); i++) {
			tmp_node = OVFDefinition_children.item(i);

			if (tmp_node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
				tmp_elem = (Element) OVFDefinition_children.item(i);

				if (!tmp_elem.getTagName().equals("ovf:VirtualSystem")) 
					continue;
				
				ServiceComponentId = tmp_elem.getAttribute("ovf:id");
		}//for-i
		
		if(DisplayAllLogs)		
		log.info("End of extractServiceComponentIdFromVMC.");
		
		return ServiceComponentId;
		
	}//extractServiceComponentIdFromVMC()
}//class
