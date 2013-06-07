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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FederatedVMsPerComponent {
    
    public static Integer getFederatedVMsForCertainServiceForCertainComponent
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
                
                String serviceId = Service_node.getAttribute("id");
                
                NodeList Components_nodes = Service_node.getElementsByTagName("service_component");
                
                for  (int j = 0; j < Components_nodes.getLength(); j++) 
                {
                    if((j+1)!=certainComponent)continue;
                    
                    Element Component_node = (Element) Components_nodes.item(j);
                
                    String componentId = Component_node.getAttribute("id");
                    
                    String federatedVMs = Component_node.getAttribute("federatedVMs");
                    
                    return Integer.parseInt(federatedVMs);
                }//for-j
            }//for-i
            
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            }//catch 
        
            return -1;
            
    }//getFederatedVMsForCertainServiceForCertainComponent()  
}//class
