/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.smanalyzer.idInfo;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ServiceManifestXMLProcessor {
	
	private static final String OPTIMIS_NS = "http://schemas.optimis.eu/optimis/";
	
	public static String getAttribute(String manifest, String tag,
			String attribute,Logger log) {
		
		Element manifestElem = null;
		
		try {
			
			manifestElem = getElement(manifest);
			
		} catch (ParserConfigurationException e) {
			
			log.error(e.getMessage());
			
		} catch (SAXException e) {
			
			log.error(e.getMessage());
			
		} catch (IOException e) {
			
			log.error(e.getMessage());
		}
		
		return getAttribute(manifestElem, tag, attribute);
	}//getAttribute(String manifest, String tag,String attribute,Logger log)
        
        public static String getAttribute(String manifest, String tag,String attribute) 
        {
            
            	Element manifestElem = null;
		
		try {
			
			manifestElem = getElement(manifest);
			
		} catch (ParserConfigurationException e) {
			
                    return "-";
                    
		} catch (SAXException e) {
                    
                    return "-";
                    
		} catch (IOException e) {
                    
                    return "-";
                    
                }
		
		return getAttribute(manifestElem, tag, attribute);
        }//getAttribute(String manifest, String tag,String attribute)
        
	public static String getAttribute(Element elem, String tag, String attribute) {

		if (tag.equalsIgnoreCase("ServiceManifest")) {
			//System.out.println(elem.getLocalName());
			String spId = elem.getAttributeNS(OPTIMIS_NS, attribute);
			//System.out.println("Service Provider Id: " + spId);
			if(spId.hashCode()==0) return null;
			return spId;
		}
                
		NodeList vmdesc_nodes = elem.getElementsByTagNameNS(OPTIMIS_NS, tag);
		//System.out.println(vmdesc_nodes.getLength());

		Element VMDescr_elem = (Element) vmdesc_nodes.item(0);

		String attribute_value = VMDescr_elem.getAttributeNS(OPTIMIS_NS,attribute);
                
		//if(attribute_value.hashCode()==0) return null;
		return attribute_value;
	}//getAttribute(Element elem, String tag, String attribute)
	
	public static Element getElement(String manifest)
        throws ParserConfigurationException, SAXException, IOException 
        {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
		builderFactory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputSource in = new InputSource();
		in.setCharacterStream(new StringReader(manifest));
		Document document = builder.parse(in);
		Element elem = document.getDocumentElement();
		
                return elem;
                
	}//getElement(String manifest)
	
        public static Element getElement(String manifest, Logger log)
        throws ParserConfigurationException, SAXException, IOException 
        {
            log.info("About to select Element from manifest");
            
            Element elem = getElement(manifest);
            
            log.info("Element Selection from manifest finished");
            
            return elem;
            
        }//getElement(String manifest, Logger log)
	
}//class
