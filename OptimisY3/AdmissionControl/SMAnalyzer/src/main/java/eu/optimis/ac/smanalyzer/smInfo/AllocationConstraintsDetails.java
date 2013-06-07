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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class AllocationConstraintsDetails
{
    protected String UpperBound = null;
    protected String Initial = null;
                
    protected  AllocationConstraintsDetails(Element manifest)
    {
		NodeList AllocationConstraints_Childern = manifest.getChildNodes();	
		
		for (int  j = 0; j < AllocationConstraints_Childern.getLength(); j++) {
			
			Node temp_node = (Node) AllocationConstraints_Childern.item(j);
			
			if(temp_node.getNodeName().equals("opt:LowerBound"))
				continue;
			else if(temp_node.getNodeName().equals("opt:UpperBound"))
				UpperBound = temp_node.getTextContent();
			else if(temp_node.getNodeName().equals("opt:Initial"))
				Initial = temp_node.getTextContent();
			
		}//for-j
                
    }//Constructor
    
}//class

