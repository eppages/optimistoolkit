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

class Memory {
    
    protected static int extractMemoryFromVMC(Element manifest,Logger log,Boolean DisplayAllLogs)
	{
		String memoryInMBs = null;
		
                if(DisplayAllLogs)
		log.info("Start of extractMemoryFromVMC.");
		
		NodeList Item_nodes = manifest.getElementsByTagName("ovf:Item");	
		
		for (int  j = 0; j < Item_nodes.getLength(); j++) {
			
			Element temp_item_node = (Element) Item_nodes.item(j);
			
			// get "rasd:Description" attribute 
			NodeList Description_nodes = temp_item_node.getElementsByTagName("rasd:Description");
			
                        if(DisplayAllLogs)
			log.info("Description = "+Description_nodes.item(0).getTextContent());
			
			// get "rasd:ResourceType" attribute 
			NodeList resourceType_nodes = temp_item_node.getElementsByTagName("rasd:ResourceType");
			
			if ((resourceType_nodes.item(0).getTextContent().equals("4"))
			&&(Description_nodes.item(0).getTextContent().contains("Memory")))
			{
				
				// get "rasd:VirtualQuantity" attribute 
				NodeList VirtualQuantity_nodes = temp_item_node.getElementsByTagName("rasd:VirtualQuantity");
				
				// get memoryInMBs
				memoryInMBs = ((Element)VirtualQuantity_nodes.item(0)).getTextContent();
				
                                if(DisplayAllLogs)
				log.info("memoryInMBs="+memoryInMBs);
				break;
			}//if-4
			
		}//for-j
                
                if(DisplayAllLogs)
		log.info("End of extractMemoryFromVMC.");
		
		return new Integer(memoryInMBs).intValue();
		
	}//extractMemoryFromVMC()
}//class
