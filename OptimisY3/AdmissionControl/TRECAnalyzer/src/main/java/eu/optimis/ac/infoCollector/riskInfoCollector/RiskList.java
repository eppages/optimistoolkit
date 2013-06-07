/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.riskInfoCollector;

import java.io.FileNotFoundException;
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

public class RiskList
{
	// define Logger to keep logs in Apache
	private static Logger log ;	
	
	public LinkedList<String> riskAsList = new LinkedList<String>();
	
	public RiskList(MultivaluedMap<String, String> formParams,Logger thelog)
			throws FileNotFoundException,IOException,Exception		
	{
		log=thelog;
	
		try {
			for(int i=0;i<formParams.get("risk").size();i++)
			{
				String risk_result = processRiskResponse(formParams.get("risk").get(i),i );
				
                                int x=1;
                                if(x==1)risk_result = "0.001";
				
                                
                                riskAsList.addLast(risk_result);
			
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
	
	// extract necessary information from risk
	private String processRiskResponse(String risk_xml,int i)
			throws FileNotFoundException,IOException,ParserConfigurationException,SAXException	
	{
				
		String risk = "";
		log.info("Start of processRiskResponse. Number "+i);
		
			/* parse risk */			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();			  
			builderFactory.setValidating(false);
			builderFactory.setIgnoringElementContentWhitespace(true);				
			DocumentBuilder builder = builderFactory.newDocumentBuilder();				
			InputSource in = new InputSource();			
			in.setCharacterStream(new StringReader(risk_xml));				
			Document document = builder.parse(in);				
			Element elem = document.getDocumentElement();
			/* get Element "sp" */
			NodeList sp_nodes = elem.getElementsByTagName("sp");				
			Element sp_elem = (Element) sp_nodes.item(0);
			/* get "pof" attribute and store it as risk value */ 
			risk = sp_elem.getAttribute("pof");		
			
		log.info("End of processRiskResponse. Number "+i);
		/* end of risk process
		 * return risk result */
		return risk;	
	}//processRiskResponse
	
}//Class RiskList
