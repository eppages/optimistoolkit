/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.costInfoCollector;

import java.io.FileNotFoundException;
import java.io.FileWriter;
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

public class CostList
{
	// define Logger to keep logs in Apache
	private static Logger log ;	

	public LinkedList<String> BasicCost = new LinkedList<String>();
	public LinkedList<String> ExtraCost = new LinkedList<String>();
	
	public CostList(MultivaluedMap<String, String> formParams,String path,Logger thelog)
			throws FileNotFoundException,IOException,Exception
	{
		log=thelog;
		
		try {
			for(int i=0;i<formParams.get("cost").size();i++)
			{
				
				
				// Extract necessary information from cost
				Cost cost = processCostResponse(formParams.get("cost").get(i), path,i);
				
				String basicCost = String.valueOf(10*cost.getBasic_cost());
				
				String extraCost = String.valueOf(cost.getExtra_cost_per_elastic_vm());
		    	
				BasicCost.addLast(basicCost);
				
				ExtraCost.addLast(extraCost);
								
			}//for
	
		} catch (ParserConfigurationException pce) {
			  log.error("There was a ParserConfigurationException: "+ pce.getMessage());
			  pce.printStackTrace();
			  
			  throw new Exception(pce.getMessage());
			  
		} catch (SAXException se) {
			  log.error("There was a SAXException: "+ se.getMessage());
			  se.printStackTrace();	
			  
			  throw new Exception(se.getMessage());
			  
		}//catch
		
	}//Constructor
	
	// extract necessary information from Cost
	public Cost processCostResponse(String cost_xml, String path,int i)
			throws FileNotFoundException,IOException,ParserConfigurationException,SAXException
	{
		  Cost cost = new Cost();
		  log.info("Start of processCostResponse. Number "+i);
		  
		  	  
			  /* parse Cost */
			  DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();			  
			  builderFactory.setValidating(false);
			  builderFactory.setIgnoringElementContentWhitespace(true);				
			  DocumentBuilder builder = builderFactory.newDocumentBuilder();				
			  InputSource in = new InputSource();
			  
			  /* create cost.xml - file to hold all cost xml string
			   * needed for admission controller's response */
			  log.info("try to creat cost file "+(i+1)+" "+path + "cost_"+(i+1)+".xml");
			  FileWriter writer = new FileWriter(path + "cost_"+(i+1)+".xml");			 
	  		  writer.append(cost_xml);	 
	  		  /* close cost.xml file */
	  		  writer.flush();
	  		  writer.close();
	  		  /* end of cost.xml file creation */				
			  System.out.println(cost_xml);
				
			  in.setCharacterStream(new StringReader(cost_xml));				
			  Document document = builder.parse(in);
			  /* get Element "currency" */
			  Element elem = document.getDocumentElement();				
			  NodeList currency_nodes = elem.getElementsByTagName("currency");				
			  Element currency_elem = (Element) currency_nodes.item(0);
			  /* get Element "planFloor" */
			  NodeList cost_nodes = elem.getElementsByTagName("planFloor");				
			  Element cost_elem = (Element) cost_nodes.item(0);
			  /* get basicCost value and store it */
			  cost.setBasic_cost((new Float(cost_elem.getTextContent())).intValue());
			  /* get Element "absoluteAmount" */
			  NodeList extra_cost_nodes = elem.getElementsByTagName("absoluteAmount");				
			  Element extra_cost_elem = (Element) extra_cost_nodes.item(0);
			  /* get extra cost value and store it */
			  cost.setExtra_cost_per_elastic_vm((new Float(extra_cost_elem.getTextContent())).intValue());		  
		  
		  log.info("End of processCostResponse. Number "+i);
		  /* end of cost process and return Cost response */
		  return cost;	
	}//processCostResponse
	
}//CostList Class
