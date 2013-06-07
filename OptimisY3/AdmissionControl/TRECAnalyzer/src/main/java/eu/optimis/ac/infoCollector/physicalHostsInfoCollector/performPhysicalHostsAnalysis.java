/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.physicalHostsInfoCollector;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class performPhysicalHostsAnalysis {
    	
    public int NoOfPhysicalHost=0;
    public LinkedList<String> maxCpusAsList = new LinkedList<String>();
    public LinkedList<String> resCpusAsList = new LinkedList<String>();
    public LinkedList<String> freeMemoryAsList = new LinkedList<String>();
    
    public performPhysicalHostsAnalysis(String physicalHostsInfoAsXML,
            MultivaluedMap<String, String> formParams,
            String key,Logger log)
    {
        try {
            
            log.info("performPhysicalHosts Analysis Started");
            
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					
            builderFactory.setValidating(false);
            builderFactory.setIgnoringElementContentWhitespace(true);
				
            // parse the xml string 
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
            InputSource in = new InputSource();							
            in.setCharacterStream(new StringReader(physicalHostsInfoAsXML));				
            Document document = builder.parse(in);
        
            Element element = document.getDocumentElement();
            
            NodeList physicalHost_nodes = element.getElementsByTagName("PhysicalHost");
            
            NoOfPhysicalHost = physicalHost_nodes.getLength();
            
            for(int j = 0; j< formParams.get(key).size();j++)
            {
                String PhysicalHost_Id = formParams.get(key).get(j);
                
            for  (int i = 0; i < physicalHost_nodes.getLength(); i++) 
            {
                
                Element physicalHost_node = (Element) physicalHost_nodes.item(i);
                
                String physicalHost_Id = physicalHost_node.getAttribute("id");
                
                if(physicalHost_Id.hashCode()!=PhysicalHost_Id.hashCode())
                    continue;
                
                int maxCores = Integer.parseInt(physicalHost_node.getAttribute("maxCores"));
                
                int reservedCores = Integer.parseInt(physicalHost_node.getAttribute("usedCores"));
                
                int maxMemoryInGigabytes = Integer.parseInt(physicalHost_node.getAttribute("maxMemoryInGigabytes"));
                
                int freeMemoryInGigabytes = Integer.parseInt(physicalHost_node.getAttribute("freeMemoryInGigabytes"));
                
                int maxMemory = 1024*maxMemoryInGigabytes;
                int freeMemory = 1024*freeMemoryInGigabytes;
                
                log.info(physicalHost_Id+" "+maxCores+" "+reservedCores);
                
                log.info(physicalHost_Id+" "+maxMemoryInGigabytes+" "+freeMemoryInGigabytes);
                log.info(physicalHost_Id+" "+maxMemory+" "+freeMemory);
                
                if(formParams.containsKey("cleanTestbed"))
                {
                    freeMemory = maxMemory;
                    reservedCores = 0;
                    
                    log.info("----------> cleanTestbed enabled freeMemory new value : "+freeMemory);
                    log.info("----------> cleanTestbed enabled  reservedCores new value : "+reservedCores);
                }//if
                
                if(reservedCores>maxCores)
                {
                    reservedCores = maxCores ;
                    
                    log.info("----------> reservedCores new value : "+reservedCores);
                }//if
                
                if(freeMemory<0)
                {
                    freeMemory = 0;
                    
                    log.info("----------> freeMemory new value : "+freeMemory);
                }//if
                
                maxCpusAsList.addLast(Integer.toString(maxCores));
        			
        	resCpusAsList.addLast(Integer.toString(reservedCores));
                
                freeMemoryAsList.addLast(Integer.toString(freeMemory));
            }//for-i
            }//for-j
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            }//catch 
            
            log.info("performPhysicalHosts Analysis Finished");
    }//Constructor
    
}//class
