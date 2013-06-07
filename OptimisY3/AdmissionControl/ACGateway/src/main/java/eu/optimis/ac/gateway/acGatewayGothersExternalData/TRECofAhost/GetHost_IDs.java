/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acGatewayGothersExternalData.TRECofAhost;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GetHost_IDs {
    
    public ArrayList<String> Host_IDAsList = new ArrayList<String>();
    
    public GetHost_IDs(String physicalHostsInfo)
    {
        try {
            
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					
            builderFactory.setValidating(false);
            builderFactory.setIgnoringElementContentWhitespace(true);
				
            // parse the xml string 
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
            InputSource in = new InputSource();							
            in.setCharacterStream(new StringReader(physicalHostsInfo));				
            Document document = builder.parse(in);
        
            Element element = document.getDocumentElement();
            
            NodeList physicalHost_nodes = element.getElementsByTagName("PhysicalHost");
            
            for  (int i = 0; i < physicalHost_nodes.getLength(); i++) 
            {
                
                Element physicalHost_node = (Element) physicalHost_nodes.item(i);
                
                String physicalHost_Id = physicalHost_node.getAttribute("id");
                
                Host_IDAsList.add(physicalHost_Id);
                
            }//for-i
            
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            }//catch 
    }//Constructor
}//class
