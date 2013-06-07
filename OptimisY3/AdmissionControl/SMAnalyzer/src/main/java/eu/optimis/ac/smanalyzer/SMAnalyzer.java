/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.smanalyzer;

import eu.optimis.ac.smanalyzer.idInfo.ReadIdsFromSM;
import eu.optimis.ac.smanalyzer.smInfo.AffinityRule;
import eu.optimis.ac.smanalyzer.smInfo.Availability;
import eu.optimis.ac.smanalyzer.smInfo.ExtractInfo;
import eu.optimis.ac.smanalyzer.smInfo.IsFederationAllowedInfo;
import eu.optimis.ac.smanalyzer.smInfo.NumberOfServiceComponents;
import eu.optimis.ac.smanalyzer.smInfo.TotalNumberOfCores;
import eu.optimis.ac.smanalyzer.smInfo.VirtualMachineComponentNames;
import eu.optimis.ac.smanalyzer.smInfo.VirtualMachineComponentNodes;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SMAnalyzer {
    
    public int numberOfServiceComponents = 0;
    
    public String isFederationAllowed = null;
    
    public ArrayList<String> componentId_List = new ArrayList<String>();
    
    public Availability availability;
    
    public AffinityRule affinityRule;
    
    public AffinityRule antiAffinityRule;
    
    public ExtractInfo smInfo;
    
    public String spId="";
    public String serviceId="";
    
    public int totalNumberOfCores = 0;
    
    public SMAnalyzer(String serviceManifest, Logger log,Boolean DisplayAllLogs)
    {
	Element element = getElement(serviceManifest, log, DisplayAllLogs);
        
        ReadIdsFromSM IdsInfo = new ReadIdsFromSM(serviceManifest,log, DisplayAllLogs);
        
        spId = IdsInfo.spId;
	serviceId = IdsInfo.serviceId;
        
        isFederationAllowed = IsFederationAllowedInfo.extractInfoFederationIsIsAllowed(element,log,DisplayAllLogs);
                
        NodeList VirtualMachineComponent_nodes = VirtualMachineComponentNodes.extractVirtualMachineComponentInfo(element,log,DisplayAllLogs);
            
        numberOfServiceComponents = NumberOfServiceComponents.getNumberOfServiceComponents(VirtualMachineComponent_nodes,log,DisplayAllLogs);
                
        componentId_List = VirtualMachineComponentNames.getVirtualMachineComponentNames(VirtualMachineComponent_nodes,numberOfServiceComponents,log, DisplayAllLogs);
        
        availability = new Availability(element,log,DisplayAllLogs);
        
        affinityRule = new AffinityRule(element,log,"Affinity",DisplayAllLogs);
        antiAffinityRule = new AffinityRule(element,log,"AntiAffinity",DisplayAllLogs);
        
        if(DisplayAllLogs){
        log.info("Affinity : "+affinityRule.AffinityRuleList);
        log.info("AntiAffinity : "+antiAffinityRule.AffinityRuleList);}
        
        smInfo = new ExtractInfo(VirtualMachineComponent_nodes,log,DisplayAllLogs);
        
        totalNumberOfCores = TotalNumberOfCores.countTotalNumberOfCores(smInfo);
    }//Constructor
    
    public SMAnalyzer(String serviceManifest)
    {
    
	Element element = null;
        try {
            element = getElement(serviceManifest);
        } catch (ParserConfigurationException ex) {
            System.err.println(ex.getMessage());
        } catch (SAXException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        
        ReadIdsFromSM IdsInfo = new ReadIdsFromSM(serviceManifest);
        
        spId = IdsInfo.spId;
	serviceId = IdsInfo.serviceId;
        
        isFederationAllowed = IsFederationAllowedInfo.extractInfoFederationIsIsAllowed(element);
                
        NodeList VirtualMachineComponent_nodes = VirtualMachineComponentNodes.extractVirtualMachineComponentInfo(element);
            
        numberOfServiceComponents = NumberOfServiceComponents.getNumberOfServiceComponents(VirtualMachineComponent_nodes);
                
        componentId_List = VirtualMachineComponentNames.getVirtualMachineComponentNames(VirtualMachineComponent_nodes,numberOfServiceComponents);
        			
    }//Constructor
    
    private Element getElement(String serviceManifest)throws ParserConfigurationException, SAXException, IOException
    {
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					
        builderFactory.setValidating(false);
	builderFactory.setIgnoringElementContentWhitespace(true);
				
	// parse the xml string 
	DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
        InputSource in = new InputSource();							
	in.setCharacterStream(new StringReader(serviceManifest));				
	Document document = builder.parse(in);
        
        Element element = document.getDocumentElement();
        
        return element;
        
    }//getElement()
//getElement()
    
    private Element getElement(String serviceManifest, Logger log,Boolean DisplayAllLogs)
    {
        if(DisplayAllLogs)
        log.info("About to extract info from manifest");
        
        Element element = null;
        try {
            
            element = getElement(serviceManifest);
            
        } catch (ParserConfigurationException ex) {
            log.error(ex.getMessage());
        } catch (SAXException ex) {
            log.error(ex.getMessage());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        
        if(DisplayAllLogs)
        log.info("getElement Finished");
        
        return element;
    }//getElement()
    
}//class
