/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.analyzeAllocationOffer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AllocationOfferInfoAsList {
    
    public ArrayList<AllocationOfferInfo> AllocationOfferList = new ArrayList<AllocationOfferInfo>();
    
    public int Number_Of_Allocation_Offer;
    
    public AllocationOfferInfoAsList(String AllocationOffers)
    {
        try {
            
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					
            builderFactory.setValidating(false);
            builderFactory.setIgnoringElementContentWhitespace(true);
				
            // parse the xml string 
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
	
            InputSource in = new InputSource();							
            in.setCharacterStream(new StringReader(AllocationOffers));				
            Document document = builder.parse(in);
        
            Element element = document.getDocumentElement();
            
            NodeList allocation_offer_nodes = element.getElementsByTagName("allocation_offer");
            
            Number_Of_Allocation_Offer = allocation_offer_nodes.getLength();
            
            for  (int i = 0; i < allocation_offer_nodes.getLength(); i++) 
            {
                AllocationOfferInfo allocationOfferInfo = new AllocationOfferInfo();
                
                Element allocation_offer_node = (Element) allocation_offer_nodes.item(i);
                
                String ip_Id = allocation_offer_node.getAttribute("ip_id");
                
                NodeList service_nodes = allocation_offer_node.getElementsByTagName("service");
                
                for  (int j = 0; j < service_nodes.getLength(); j++) 
                {
                    Element service_node = (Element) service_nodes.item(j);
                
                    String serviceId = service_node.getAttribute("id");
                    
                    int admission_control_decision = Integer.parseInt(service_node.getAttribute("admission_control_decision"));
                    
                    NodeList allocation_pattern_nodes = service_node.getElementsByTagName("allocation_pattern");
                    
                    NodeList risk_nodes = service_node.getElementsByTagName("risk");
                    
                    NodeList pricePlan_nodes = service_node.getElementsByTagName("pricePlan");
                    
                    for  (int ii = 0; ii < allocation_pattern_nodes.getLength(); ii++) 
                    {
                        Element allocation_pattern_node = (Element) allocation_pattern_nodes.item(ii);
                
                        NodeList service_component_nodes = allocation_pattern_node.getElementsByTagName("service_component");
                        
                        int numberOfAcceptedServiceComponents = service_component_nodes.getLength();
                        
                        for  (int jj = 0; jj < service_component_nodes.getLength(); jj++) 
                        {
                            Element service_component_node = (Element) service_component_nodes.item(jj);
                
                            String AcceptedServiceComponentID = service_component_node.getAttribute("id");
                            
                            NodeList allocated_VM_instances_nodes = service_component_node.getElementsByTagName("allocated_VM_instances");
                            
                            for  (int a = 0; a < allocated_VM_instances_nodes.getLength(); a++) 
                            {
                                Element allocated_VM_instances_node = (Element) allocated_VM_instances_nodes.item(a);
                                
                                NodeList basic_nodes = allocated_VM_instances_node.getElementsByTagName("basic");
                                
                                NodeList elastic_nodes = allocated_VM_instances_node.getElementsByTagName("elastic");
                                
                                for  (int b = 0; b < basic_nodes.getLength(); b++) 
                                {
                                    Element basic_node = (Element) basic_nodes.item(b);
                                    
                                    String Basic_physicalHost = basic_node.getTextContent();
                                    
                                    allocationOfferInfo.BasicPhysicalHost.add(AcceptedServiceComponentID,Basic_physicalHost);
                                }//for-b    
                                
                                for  (int e = 0; e < elastic_nodes.getLength(); e++) 
                                {
                                    Element elastic_node = (Element) elastic_nodes.item(e);
                                    
                                    String Elastic_physicalHost = elastic_node.getTextContent();
                                    
                                    allocationOfferInfo.ElasticPhysicalHost.add(AcceptedServiceComponentID,Elastic_physicalHost);
                                }//for-e
                                
                            }//for-a
                            
                            allocationOfferInfo.ListOfAccepted_ServiceComponents.add(AcceptedServiceComponentID);
                            
                        }//for-jj
                    
                        allocationOfferInfo.setNumberOfAcceptedServiceComponents(numberOfAcceptedServiceComponents);
                    }//for-ii
                    
                    String the_Risk = null;
                    for  (int r = 0; r < risk_nodes.getLength(); r++) 
                    {
                        Element risk_node = (Element) risk_nodes.item(r);
                        
                        the_Risk = risk_node.getTextContent();
                        
                    }//for-r
                    
                    XmlObject the_pricePlan = null;
                    for  (int c = 0; c < risk_nodes.getLength(); c++) 
                    {
                        Element pricePlan_node = (Element) pricePlan_nodes.item(c);
                        
                        the_pricePlan = XmlObject.Factory.parse(pricePlan_node);
                        
                    }//for-r
                    
                    allocationOfferInfo.setService_Id(serviceId);
                    allocationOfferInfo.setIP_Id(ip_Id);
                    allocationOfferInfo.setAdmissionControlDecision(admission_control_decision);
                    allocationOfferInfo.setPricePlan(the_pricePlan);
                    allocationOfferInfo.setRisk(the_Risk);
                    
                }//for-j
                
                AllocationOfferList.add(allocationOfferInfo);
            }//for-i
            
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (XmlException ex) {
                ex.printStackTrace();
            }//catch 
    
    }//Constructor
    
}//class
