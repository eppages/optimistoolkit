/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.trecanalyzer.modelInputAsStrings;

import eu.optimis.ac.smanalyzer.SMAnalyzer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.log4j.Logger;

public class WriteServicesInfoAsString {
    
    public static String WriteServicesInfoFileAsString2(String AllocationPath,MultivaluedMap<String, String> formParams,
            LinkedList<LinkedList<String>> List,LinkedList<SMAnalyzer> smAnalyzerList,
            Logger log)
    		throws IOException,FileNotFoundException,ParserConfigurationException,TransformerException
    {
    	log.info("Creation of ServicesInfo.xml file");	
    	
    	log.info("size = "+formParams.get("serviceId").size());
    	
    	StringWriter writer = new StringWriter();
    	
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
	
           return writer.toString();
    }//WriteIdsFileAsString2()
   
}//class
