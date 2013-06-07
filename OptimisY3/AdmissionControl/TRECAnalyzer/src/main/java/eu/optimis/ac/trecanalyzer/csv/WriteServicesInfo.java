/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.csv;

import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WriteServicesInfo {
    
    public static void WriteServicesInfoFile2(String AllocationPath,MultivaluedMap<String, String> formParams,
            LinkedList<LinkedList<String>> List,LinkedList<SMAnalyzer> smAnalyzerList,
            Logger log)
    		throws IOException,FileNotFoundException,ParserConfigurationException,TransformerException
    {
    	log.info("Creation of ServicesInfo.xml file");	
    	
    	log.info("size = "+formParams.get("serviceId").size());
    	
    	FileWriter writer = new FileWriter(AllocationPath+"ServicesInfo.xml");
    	
    	writer.append("<?xml version=");
    	writer.append('"');
    	writer.append("1.0");
    	writer.append('"');
    	writer.append(" encoding=");
    	writer.append('"');
    	writer.append("UTF-8");
    	writer.append('"');
    	writer.append(" standalone=");
    	writer.append('"');
    	writer.append("no");
    	writer.append('"');
    	writer.append("?><services>");
    	
		for(int i=0;i<formParams.get("serviceId").size();i++)
		{
                        SMAnalyzer smAnalyzer = smAnalyzerList.get(i);
                        
			log.info(formParams.get("serviceId").get(i));
			
			// set attribute to service element
			
			writer.append("<service ");
			
			writer.append("id=");
			writer.append('"');
	    	writer.append(formParams.get("serviceId").get(i));
	    	writer.append('"');
			
			// set attribute to service element
			
			log.info("a"+(i+1));
			writer.append(" name=");
			writer.append('"');
	    	writer.append("a"+(i+1));
	    	writer.append('"');
			
			// set attribute to service element
			
			log.info("cost_"+(i+1)+".xml");
			writer.append(" xmlCostFile=");
			writer.append('"');
	    	writer.append("cost_"+(i+1)+".xml");
	    	writer.append('"');
	    	writer.append(">");
			
			LinkedList<String> miniList=List.get(i);
			for(int j=0;j<miniList.size();j++)
			{
                                String componentId = smAnalyzer.smInfo.ServiceComponentId_Map_componentId.get(miniList.get(j)).get(0);
				// set attribute to service element
				
				log.info(miniList.get(j));
				writer.append("<vm id=");
				writer.append('"');
		    	writer.append(miniList.get(j));
		    	writer.append('"');
					
				
				// set attribute to service element
				
				log.info("vm"+(j+1));
				writer.append(" name=");
				writer.append('"');
                                writer.append("vm"+(j+1));
                                writer.append('"');
				
                                writer.append(" componentId=");
				writer.append('"');
                                writer.append(componentId);
                                writer.append('"');
                        
		    	writer.append("/>");
		    	
			}//for-j
			
			writer.append("</service>");
		}//for-i
	    
		writer.append("</services>");
		log.info("XML attributes finished");
		
		writer.flush();
	    writer.close();
	
		log.info("ServicesInfo.xml was created!");
	
    }//WriteIdsFile2()
    
    public static void WriteServicesInfoFile(String AllocationPath,MultivaluedMap<String, String> formParams,
            LinkedList<LinkedList<String>> List, LinkedList<SMAnalyzer> smAnalyzerList,
            Logger log)
    		throws IOException,FileNotFoundException,ParserConfigurationException,TransformerException
    {
    	log.info("Creation of ServicesInfo.xml file");	
    	
    	log.info("size = "+formParams.get("serviceId").size());
    	
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("services");
		doc.appendChild(rootElement);
    	
		for(int i=0;i<formParams.get("serviceId").size();i++)
		{
                        SMAnalyzer smAnalyzer = smAnalyzerList.get(i);
                        
			log.info(formParams.get("serviceId").get(i));
			// service elements
			Element serviceElement = doc.createElement("service");
			rootElement.appendChild(serviceElement);
			
			// set attribute to service element
			Attr attr1 = doc.createAttribute("id");
			log.info(formParams.get("serviceId").get(i));
			attr1.setValue(formParams.get("serviceId").get(i));
			
			//attr1.setValue("ABC");
			serviceElement.setAttributeNode(attr1);
			
			// set attribute to service element
			Attr attr2 = doc.createAttribute("name");
			log.info("a"+(i+1));
			attr2.setValue("a"+(i+1));
			serviceElement.setAttributeNode(attr2);
			
			// set attribute to service element
			Attr attr5 = doc.createAttribute("xmlCostFile");
			log.info("cost_"+(i+1)+".xml");
			attr5.setValue("cost_"+(i+1)+".xml");
			serviceElement.setAttributeNode(attr5);
			
			LinkedList<String> miniList=List.get(i);
			for(int j=0;j<miniList.size();j++)
			{
                                String componentId = smAnalyzer.smInfo.ServiceComponentId_Map_componentId.get(miniList.get(j)).get(0);
                                
				Element vmElement = doc.createElement("vm");
				serviceElement.appendChild(vmElement);
				
				// set attribute to service element
				Attr attr3 = doc.createAttribute("id");
				log.info(miniList.get(j));
				attr3.setValue(miniList.get(j));
				vmElement.setAttributeNode(attr3);
				
				// set attribute to service element
				Attr attr4 = doc.createAttribute("name");
				log.info("vm"+(j+1));
				attr4.setValue("vm"+(j+1));
				vmElement.setAttributeNode(attr4);
				
                                Attr attr6 = doc.createAttribute("componentId");
				log.info(componentId);
				attr6.setValue(componentId);
				vmElement.setAttributeNode(attr6);
                                
			}//for-j
			
		}//for-i
	    
		log.info("XML attributes finished");
		
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		log.info("Ready to write xml");
		StreamResult result = new StreamResult(new File(AllocationPath+"ServicesInfo.xml"));
		
		transformer.transform(source, result);
	    
		log.info("ServicesInfo.xml was created!");
	
    }//WriteIdsFile()
    
}//class
