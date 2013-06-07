/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acLogsToWeb.fiveLastExecutions;

import eu.optimis.ac.gateway.configuration.GetFileNames;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.gateway.utils.PropertiesUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadExecutions {
    
    public ArrayList<String> Header_List = new ArrayList<String>();
    public ArrayList<ArrayList<String>> Info_List = new ArrayList<ArrayList<String>>();
    
    public ReadExecutions(ArrayList<String> Tag_List,Logger log)
    {
        
        for(int counter=1;counter<=Integer.parseInt(PropertiesUtils.getBoundle("AClogs.lastExecution"));counter++)
        {
            String FileName = GetFileNames.getAClogs_fileName(log).
                    replace(".txt", "_"+Integer.toString(counter)+".xml");
            
            log.info(FileName);
            
            if(!FileFunctions.FileExists(FileName, ""))break;;
            
            ReadExecution(FileName,Tag_List,log);
        }//for-i
    }//constructor
    
    private  void ReadExecution(String FileName,ArrayList<String> Tag_List,Logger log)
    {
        try {
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(FileName));

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            
            NodeList Headers_nodes = doc.getElementsByTagName("header");
            
            log.info("Headers_nodes.getLength() : "+Headers_nodes.getLength());
            
            for  (int i = 0; i < Headers_nodes.getLength(); i++) 
            {
                
                Element Header_node = (Element) Headers_nodes.item(i);
                
                String header = Header_node.getAttribute("name");
                
                log.info("header name : "+header);
                
                Header_List.add(header);
                
                ArrayList<String> info_List = new ArrayList<String>();
                
                for(int j=0;j<Tag_List.size();j++)
                {
                    
                    String attribute_name = Tag_List.get(j).replace("Model Decision", "Model_Decision");
                    log.info("attribute_name : "+attribute_name);
                    
                    String attribute_value = Header_node.getAttribute(attribute_name); 
                    log.info("attribute_value : "+attribute_value); 
                    
                    info_List.add(attribute_value);
                }//for-j
                
                Info_List.add(info_List);
                
            }//for-i
            
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            }//catch 
        
    }//getAllocationDetailsForCertainServiceForCertainComponent()
}//class
