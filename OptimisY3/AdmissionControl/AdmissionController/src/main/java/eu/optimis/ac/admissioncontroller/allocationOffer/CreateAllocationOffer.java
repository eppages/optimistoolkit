/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.admissioncontroller.allocationOffer;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.admissioncontroller.utils.FileFunctions;
import eu.optimis.ac.admissioncontroller.utils.InputFunctions;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CreateAllocationOffer {
	
    
        public MultivaluedMap<String, String> outputParams;
        
        public String AllocationOffer = null;
        
        public String AllocationDetails = null;
        
        
        
        private Logger log = null;
    
        private String IP_Id = null;
        
        private String AllocationInfo = null;    
        private String GamsDirectory = null;
        
        // Services list
        private List<Service> serviceList;
	// Components list 
	private List<Component> componentList;
        private Map <String,List<ComponentIdentificationDetails>> Service_Component_Map;
	// Physical Hosts list 
	private List<PhysicalHost> physicalHostList;
	// Services-Components list
	private List<Service_Component> scList;
	// Services-Components-Physical Hosts list 
	private List<Service_Component_PhysicalHost> scpList;
	
        private ArrayList<ArrayList<String>> AllocationDetails_List;
        
	public CreateAllocationOffer(String outputFile, String gamsDirectory, String allocationInfo,
                String ip_id, Logger the_log) 
        {
                        this.outputParams = new MultivaluedMapImpl();
                        
                        GamsDirectory = gamsDirectory;
                        AllocationInfo = allocationInfo;
                        IP_Id = ip_id;
                        log = the_log;
                        
                        this.serviceList = new ArrayList<Service>();
			this.componentList = new ArrayList<Component>();
			this.physicalHostList = new ArrayList<PhysicalHost>();
			this.scList = new ArrayList<Service_Component>();
			this.scpList = new ArrayList<Service_Component_PhysicalHost>();
                        
                        this.Service_Component_Map  = new HashMap<String, List<ComponentIdentificationDetails>>();
                        
                        this.AllocationDetails_List = new ArrayList<ArrayList<String>>();
                        
                        AllocationOffer = readOutputFromCsv(outputFile);
                        
                        AllocationDetails = AllocationDetailsAsXMLString.generateXML(GamsDirectory,
                                AllocationDetails_List, 
                                serviceList, Service_Component_Map, 
                                physicalHostList,scList,log);
                        
                        outputParams.add("AllocationOffer", AllocationOffer);
                        outputParams.add("AllocationDetails", AllocationDetails);
                        
                        TRECvaluesPerService.setTRECoutputParams(serviceList,
                            Service_Component_Map,outputParams,log);
                        
    }//Constructor

    // auxiliary method to read a value from *.csv file 
    public String readNoFromCsv(String filename) {
    	log.info("Entering readNoFromCsv");
    	// String variable to hold method's output 
    	String output = "";
    	try {
            
                
			// create BufferedReader to read csv file 
    		//InputStream is = getInputStream(filename);
    		//BufferedReader br = new BufferedReader( new InputStreamReader(is));
    		//BufferedReader br = new BufferedReader( new InputStreamReader(AdmissionController.class.getClassLoader().getResourceAsStream(filename) ) );
			
                FileInputStream fstream = new FileInputStream(filename);
                DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
                
                        String strLine = "";
			StringTokenizer st = null;
		
			// read comma separated file line by line 
			if ( (strLine = br.readLine() ) != null) {
				// break comma separated line using "," and "\"" 
				st = new StringTokenizer(strLine, ",\"");
				// ignore first token (is the identifier) 
				log.info("Token1: " + filename + " : " + st.nextToken());
				// second token is the one needed 
				output = st.nextToken();
				log.info("Token2: " + filename + " : " + output);						 
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);
		}
		log.info("Output of " + filename + ": " + output);
		log.info("Exiting readNoFromCsv");
		/* return value */
		return output;
    } // end of readNoFromCsv method
    
      // auxiliary method to read output from a csv file and create offer xml 
    private String readOutputFromCsv(String filename) 
    {
    	log.trace("Entering readOutputFromCsv");
    	try { 
			// create BufferedReader to read csv file 
    		log.info("Opening " + filename);
    		
    		//InputStream is = getInputStream(filename);
    		//BufferedReader br = new BufferedReader(new InputStreamReader(is));
                
                FileInputStream fstream = new FileInputStream(filename);
                DataInputStream in = new DataInputStream(fstream);
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
						
			String strLine = "";
			StringTokenizer st = null;
			String token = null;
			
			// read comma separated file line by line 
			log.info("Parsing " + filename);
			while (( strLine = br.readLine()) != null){
        		if (!strLine.trim().equals("")){
        			st = new StringTokenizer(strLine, "\",");
        			if (st.hasMoreTokens()) {
        				if (st.nextToken().equals("**")) {
        					token = st.nextToken();
        					//log.info(token + "!!");
        					switch (Tags.getByValue(token)) {
							case ACCEPTED_ELASTIC_VMS: //3rd
								log.info("Parsing " +
										"accepted_elastic_vms details");
								parseAcceptedElasticVMs(br);
								break;
							case ACCEPTED_ELASTIC_VMS_PER_COMP: //4th
								log.info("Parsing " +
										"accepted_elastic_vms_per_comp details");
								parseAcceptedElasticVMsPerComp(br);
								break;
							case XFACTOR: //1st
								log.info("Parsing xfactor details");
								parseXFactor(br);
								break;
							case XX: //2nd
								log.info("Parsing xx details");
								parseXX(br);
								break;
                                                        case VMS_FOR_FEDERATION:
                                                                                log.info("Parsing VMS_FOR_FEDERATION");
                                                                                parseVMS_FOR_FEDERATION(br);
                                                                                break;
                                                        case TRUST_FOR_NewService:
                                                                                log.info("Parsing TRUST_FOR_NewService");
                                                                                parseTRUST_FOR_NewService(br);
                                                                                break;    
                                                        case PROBABILITY_FOR_ServiceFail:
                                                                                log.info("Parsing PROBABILITY_FOR_ServiceFail");
                                                                                parsePROBABILITY_FOR_ServiceFail(br);
                                                                                break;    
                                                        case ECO_FOR_NewService:
                                                                                log.info("Parsing ECO_FOR_NewService");
                                                                                parseECO_FOR_NewService(br);
                                                                                break;    
                                                        case COST_FOR_HostingService:
                                                                                log.info("Parsing COST_FOR_HostingService");
                                                                                parseCOST_FOR_HostingService(br);
                                                                                break;    
							default:
								log.warn("Unknown String found," +
										" while parsing output file");
								System.out.println("WARNING: Unknown String found," +
										" while parsing output file");
								break;
							} 
        				}
        			}
        		}
			}
			log.info(filename + " successfully parsed");
			
			readRiskValues();
		
		} catch (IOException ioe) {
			log.error("There was an IOException: ", ioe);
                        ioe.printStackTrace();
		} 	
		
		// function for getting IDs
		retrieveIDs();
		// XML generation function 
		String offer = generateXML();
		log.trace("Exiting readOutputFromCsv");
		// return offer xml 
		return offer;
		
    } // end of readOutputFromCsv method
    

	private void retrieveIDs() {
    	log.trace("Entering retrieveIDs");
    	
    	BufferedReader br;
    	InputStream is;
    	Element servElement, compElement;
    	NodeList servList,compList;
    	String strLine, servName, servID, compName, compID, compComponentId, servCostFile,
    		hostName, hostID;
    	StringTokenizer st;
    	Service s;
    	Service_Component sc;
    	PhysicalHost ph;
    	
    	try {
    		// Parsing first file 
    		DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();			  
			bf.setValidating(false);
			bf.setIgnoringElementContentWhitespace(true);				
			DocumentBuilder builder = bf.newDocumentBuilder();						
			
                        log.info("Opening ServicesInfo.xml");
			
                        //is = getInputStream("AllocationInfo" + File.separator + "ServicesInfo.xml");
		
                        is = InputFunctions.getFileInputStream( AllocationInfo+"ServicesInfo.xml", log);
                        
                        Document doc = builder.parse(is);
			//Document doc = builder.parse(AdmissionController.class.getClassLoader().getResourceAsStream("AllocationInfo"
			//			+ File.separator + "ServicesInfo.xml"));
			
			servList = doc.getElementsByTagName("service");
			
			for (int i = 0; i < servList.getLength(); i++) {
				servElement = (Element) servList.item(i);
				servID = servElement.getAttribute("id");
				servName = servElement.getAttribute("name");
				servCostFile = servElement.getAttribute("xmlCostFile");
				
				s = findService(servName);
				if (s==null) {
					log.warn("Service with name:" + servName +
							" could not be found");
				} else {
					s.uniqueID = servID;
					if (servCostFile!=null)
						s.costFile = servCostFile;
				}
				
                                
                                List<ComponentIdentificationDetails> componentIdentificationList 
                                        = new ArrayList<ComponentIdentificationDetails>();
                                
                                
				compList = servElement.getElementsByTagName("vm");
				for (int j = 0; j < compList.getLength(); j++) {
					compElement = (Element) compList.item(j);
					compID = compElement.getAttribute("id");
					compName = compElement.getAttribute("name");
					compComponentId = compElement.getAttribute("componentId");
                                        
                                        ComponentIdentificationDetails componentIdentificationDetails 
                                                = new ComponentIdentificationDetails(compName,compID,compComponentId);
                                        componentIdentificationList.add(componentIdentificationDetails);
                                        
                                        
					sc = findService_Component(servName, compName);
					if (sc == null) {
						log.warn("Service_Component with service name:" + servName +
								" and component name:" + compName + " could not be found");
					} else {
						sc.uniqueID = compID;
						log.info("Service_Component with service name:" + servName +
								" and component name:" + compName + " assigned id " +
								compID);
					}
				}
                                
                                Service_Component_Map.put(servName, componentIdentificationList);
			}
			
			// Parsing second file
			log.info("Opening hostsInfo.csv");
                        
			//is = getInputStream("AllocationInfo" + File.separator + "hostsInfo.csv");
                        
                        is = InputFunctions.getFileInputStream( AllocationInfo+"hostsInfo.csv", log);
                        
			br = new BufferedReader(new InputStreamReader(is));
                        
                        
			//br = new BufferedReader(new InputStreamReader
			//		(AdmissionController.class.getClassLoader().getResourceAsStream("AllocationInfo" +
			//				File.separator + "hostsInfo.csv") ) );
			
                        
                        while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, ",");
				
				hostName = st.nextToken();
				hostID = st.nextToken();
				
				ph = findPhysicalHost(hostName);
				if (ph == null) {
					log.warn("Physical host with name:" + hostName +
							" could not be found");
				} else {
					ph.uniqueID = hostID;
					log.info("Physical host with name:" + ph.name +
							" assigned id " +
							hostID);
				}
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);
		}  catch (SAXException saxe) {
			saxe.printStackTrace();
			log.error("There was an SAXException at processRiskResponse: ", saxe);
		} catch (ParserConfigurationException pce) {			
			pce.printStackTrace();
			log.error("There was an ParserConfigurationException at processRiskResponse: ", pce);
		}
    	
    	log.trace("Exiting retrieveIDs");
	} //end of retrieveIDs method

	// Reads risk values
    private void readRiskValues() {
    	String serv;
    	
    	log.trace("Entering readRiskValues");
    	try {
    		log.info("Opening risk.csv");
		// create BufferedReader to read csv file 
			
                //InputStream is = getInputStream("gams/risk.csv");
                
                InputStream is = InputFunctions.getFileInputStream( GamsDirectory+"risk.csv", log);
                
    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
    		//BufferedReader br = new BufferedReader( new InputStreamReader(AdmissionController.class.getClassLoader().getResourceAsStream("gams/risk.csv") ) );
			String strLine = "";
			StringTokenizer st = null;
			Service s = null;

			log.info("Reading risk values");
			// read comma separated file line by line 
			while ( (strLine = br.readLine() ) != null) {
				if (!strLine.trim().equals("")) {
					// break comma separated line using "," and "\"" 
					st = new StringTokenizer(strLine, ",\"");
					
					// If service is in the list update its risk value 
					serv = st.nextToken();
					
					s = findORcreateService(serv);				
					s.riskValue = Float.parseFloat(st.nextToken()+"f");
					log.info("Risk value of service:" + s.name + " is " + s.riskValue);
				}
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);
		}
		
    	log.trace("Exiting readRiskValues");
	}

    // Generates XML using information retrieved from other files 
	private String generateXML() {
    	log.trace("Entering generateXML");
    	
    	Service_Component_PhysicalHost scp;
    	// Replaced StringBuffer. StringBuilder 
    	// is preferable as long as
    	// the latest version of Java is used 
    	StringBuilder strB = new StringBuilder(200);
        
    	strB.append("<allocation_offers>");
        
        
        String temp[] = FileFunctions.readFileAsStringWithPath(GamsDirectory+"services.csv", log).split(",");
        
        for(String serviceName : temp)
        
    	for (Service s : serviceList) {
    		// open offer's xml string 
                
                if(serviceName.equals(s.name)==false)continue;
            
    		strB.append("<allocation_offer ip_id=\"");
    		strB.append(IP_Id);
    		strB.append("\">");
    		
    		log.info("Appending allocation offer for service " + s.uniqueID);
    		// append service id 
    		strB.append("<service id=\"");
    		strB.append(s.uniqueID);
                strB.append("\"");
               
                // append admission control decision 
    		strB.append(" admission_control_decision=\"");
    		strB.append(s.accepted?1:0);
    		strB.append("\">");
    		
    		// append allocation pattern 
		strB.append("<allocation_pattern>");
    		
    		for (Service_Component sc : scList) {
    			if ((sc.totalVMs>0) && (s.name.equals(sc.s.name))) {
	    			strB.append("<service_component id=\"");
	    			// append components name 
	    			strB.append(sc.uniqueID);
	    			strB.append("\">");
                                
	    			strB.append("<allocated_VM_instances>");
	    			
	    			for (PhysicalHost p : sc.physicalHostsList) {
	    				//scp = findService_Component_PhysicalHost(
	    					//sc.s.name, sc.c.name, p.name);
	    				scp = findService_Component_PhysicalHost(sc, p);
	    				// First VMs are supposed to be the basic ones 
	    				// When assigned VMs are 0 basic tag opens
	    				// When assigned VMs exceed the basic ones for the first time
	    				// basic tag closes and elastic one opens
                                        
                                        log.info("before Basic   :"+sc.c.name+" "+sc.assignedVMs+" "+scp.VMs+" "+sc.totalVMs+" "+sc.basicVMs+" "+p.name);
                                        
	    				if (scp != null) {
	    					if (sc.basicVMs != 0) {
	    						//if (sc.assignedVMs == 0)
	    						strB.append("<basic type=\"PhysicalHost\">");
	    						
	    						strB.append(p.uniqueID);
	    						
	    						strB.append("</basic>");
	    						
	    					//}
	    					//sc.assignedVMs += scp.VMs;
                                                
                                                if (scp.VMs > sc.basicVMs) 
                                                {
                                                	int value = sc.basicVMs;
                                                    sc.assignedVMs += value;
                                                    scp.VMs -= value;
                                                    sc.basicVMs -= value;
                                                }
                                                else
                                                {
                                                	int value = scp.VMs;
                                                    sc.assignedVMs += value;
                                                    scp.VMs -= value;
                                                    sc.basicVMs -= value;
                                                }
													
													
                                       log.info("before Elastic :"+sc.c.name+" "+sc.assignedVMs+" "+scp.VMs+" "+sc.totalVMs+" "+sc.basicVMs+" "+p.name);         
                             }           
                                                if ((scp.VMs>0)&(sc.assignedVMs < sc.totalVMs)) {
	    						
                                                        sc.assignedVMs += scp.VMs;
                                                        
                                                        strB.append("<elastic type=\"PhysicalHost\">");
	    						
	    						strB.append(p.uniqueID);
	    						
								log.info("middle Elastic :"+sc.c.name+" "+sc.assignedVMs+" "+scp.VMs+" "+sc.totalVMs+" "+sc.basicVMs+" "+p.name);
								
	    						if (sc.assignedVMs <= sc.totalVMs)
	    							strB.append("</elastic>");
	    					}
                                                /*
	    					if (sc.assignedVMs > sc.basicVMs) {
	    						if (sc.assignedVMs - scp.VMs <= sc.basicVMs) {
	    							strB.append("<elastic type=\"PhysicalHost\">");
	    						} 						
	    						
	    						strB.append(p.uniqueID);
	    						
	    						if (sc.assignedVMs == sc.totalVMs)
	    							strB.append("</elastic>");
	    					}*/
	    				} else {
		    				log.warn("Object Service_Component_PhysicalHost" +
		    						" with Service name \"" + sc.s.name + "\"" +
		    						" Component name \"" + sc.c.name + "\"" +
		    						" PhysicalHost name \"" + p.name + "\"" +
    								" could not be found");
		    			}
	    			}
	    			strB.append("</allocated_VM_instances>");
	    			strB.append("</service_component>");
    			}
    		}
		
    		strB.append("</allocation_pattern>");
    		
    		// append the cost file to offer xml 
    		strB.append(
                        FileFunctions.readFileAsStringWithPath(AllocationInfo+s.costFile,log)
                        .replaceAll(" ", "").replace("priceLevelxsi", "priceLevel xsi")
                        //readFileAsString("AllocationInfo" +File.separator + s.costFile)
                                );
    		
    		// append risk to offer xml 
    		strB.append("<risk>");
    		strB.append(s.riskValue);
    		strB.append("</risk>");
    		
    		// close service tag 
    		strB.append("</service>");
    		// close offer's xml string 
    		strB.append("</allocation_offer>");
                
    	}
    	strB.append("</allocation_offers>");
    	
    	log.trace("Exiting generateXML");
    	return strB.toString();
	} //end of generateXML method
        
        private void parseCOST_FOR_HostingService(BufferedReader br) 
        {
        String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv;
    	
    	log.trace("Entering parseCOST_FOR_HostingService");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				s = findORcreateService(serv);
				
				s.COST_FOR_HostingService = 
						st.nextToken().replace(".00", "");
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseCOST_FOR_HostingService");
	} //end of parseCOST_FOR_HostingService
        
        private void parseECO_FOR_NewService(BufferedReader br) 
        {
        String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv;
    	
    	log.trace("Entering parseECO_FOR_NewService");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				s = findORcreateService(serv);
				
				s.ECO_FOR_NewService = 
						st.nextToken().replace(".00", "");
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseECO_FOR_NewService");
	} //end of parsePROBABILITY_FOR_ServiceFail
        
        private void parsePROBABILITY_FOR_ServiceFail(BufferedReader br) 
        {
        String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv;
    	
    	log.trace("Entering parsePROBABILITY_FOR_ServiceFail");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				s = findORcreateService(serv);
				
				s.PROBABILITY_FOR_ServiceFail = 
						st.nextToken().replace(".00", "");
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parsePROBABILITY_FOR_ServiceFail");
	} //end of parsePROBABILITY_FOR_ServiceFail
        
        private void parseTRUST_FOR_NewService(BufferedReader br) 
        {
        String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv;
    	
    	log.trace("Entering parseTRUST_FOR_NewService");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				s = findORcreateService(serv);
				
				s.TRUST_FOR_NewService = 
						st.nextToken().replace(".00", "");
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseTRUST_FOR_NewService");
	} //end of parseTRUST_FOR_NewService
        
        private void parseAcceptedElasticVMs(BufferedReader br) 
        {
        String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv;
    	
    	log.trace("Entering parseAcceptedElasticVMs");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				s = findORcreateService(serv);
				
				s.totalElasticVMs = Integer.parseInt
						(st.nextToken().replace(".00", ""));
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseAcceptedElasticVMs");
	} //end of parseAcceptedElasticVMs
        
        private void parseVMS_FOR_FEDERATION(BufferedReader br) {
		String strLine;
    	StringTokenizer st;
    	String comp, serv;
    	Service_Component sc;
    	int elasticVMs;
    	
    	log.trace("Entering parseVMS_FOR_FEDERATION");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				comp = st.nextToken();
				serv = st.nextToken();
                                sc = findORcreateServiceComponent(serv, comp);
				
                                sc.VMS_FOR_FEDERATION = st.nextToken().replace(".00", "");
                                
				
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseVMS_FOR_FEDERATION");
	}//parseVMS_FOR_FEDERATION()
        
	private void parseAcceptedElasticVMsPerComp(BufferedReader br) {
		String strLine;
    	StringTokenizer st;
    	String comp, serv;
    	Service_Component sc;
    	int elasticVMs;
    	
    	log.trace("Entering parseAcceptedElasticVMsPerComp");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				comp = st.nextToken();
				serv = st.nextToken();
				elasticVMs = Integer.parseInt(st.nextToken().replace(".00", ""));				
				
				if (elasticVMs>0) {
					sc = findORcreateServiceComponent(serv, comp);
					sc.elasticVMs = elasticVMs;
					sc.updateBasic();
				}
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseAcceptedElasticVMsPerComp");
	}//parseAcceptedElasticVMsPerComp()

	private void parseXX(BufferedReader br) {
    	
    	String strLine;
    	StringTokenizer st;
    	String host, comp, serv;
    	PhysicalHost p;
    	Service_Component sc;
    	Service_Component_PhysicalHost scp;
    	int VMs;
    	
    	log.trace("Entering parseXX");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			while (((strLine = br.readLine()) != null) 
					 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				host = st.nextToken();
				comp = st.nextToken();
				serv = st.nextToken();
				VMs = Integer.parseInt(st.nextToken().replace(".00", "").replace(".0", ""));
				
                                AllocationDetailsAsList.addToList(serv, comp, host, VMs, AllocationDetails_List);
                                
				if (VMs>0) {
					log.info(VMs + " VMs allocated in host " + host + 
							", for service " + serv + " and component " + comp);
					scp = findORcreateServiceComponentPhysicalHost(serv,
							comp, host);
					scp.VMs = VMs;
					sc = findORcreateServiceComponent(serv, comp);
					p = findORcreatePhysicalHost(host);
					sc.physicalHostsList.add(p);
					sc.totalVMs+=VMs;
					sc.updateBasic();
				}

			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseXX");
	}
        
	private void parseXFactor(BufferedReader br) {
    	
    	String strLine;
    	StringTokenizer st;
    	Service s;
    	String serv, isAccepted;
    	
    	log.trace("Entering parseXFactor");
    	try {
	    	// Skip an empty line 
			strLine = br.readLine();
			
			while (((strLine = br.readLine()) != null) 
				 && (!strLine.equals(""))) {
				st = new StringTokenizer(strLine, "\",");
				
				serv = st.nextToken();
				
				s = findORcreateService(serv);
				isAccepted = st.nextToken();
                                if(isAccepted.equals("1.0"))isAccepted = "1.00";
				s.accepted = isAccepted.equals("1.00");
				log.info("service admittance status:" + isAccepted);
				log.info(s.accepted?"service " + s.name + " admitted":
						"service " + s.name + " not admitted");
			}
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
			log.error("There was an IOException: ", ioe);	
    	}
    	
    	log.trace("Exiting parseXFactor");
	} // end of parseXFactor
	
        private Service findORcreateService(String serv) {
		Service s;
		
		s = findService(serv);
		if (s == null) {
			s = new Service(serv);
			serviceList.add(s);
		}
		
		return s;
        }//findORcreateService()
	
	private Component findORcreateComponent(String comp) {
		Component c;
		
		c = findComponent(comp);
		if (c == null) {
			c = new Component(comp);
			componentList.add(c);
		}
		
		return c;
	}
	private PhysicalHost findORcreatePhysicalHost(String host) {
		PhysicalHost p;
		
		p = findPhysicalHost(host);
		if (p == null) {
			p = new PhysicalHost(host);
			physicalHostList.add(p);
		}		
		return p;
	}
	private Service_Component findORcreateServiceComponent(String serv,
			String comp) {
		Service_Component sc;
		
		sc = findService_Component(serv, comp);
		if (sc == null) {
			Service s = findORcreateService(serv);
			Component c = findORcreateComponent(comp);
			sc = new Service_Component(s, c);
			scList.add(sc);
		}
		return sc;
	}
	private Service_Component_PhysicalHost
	findORcreateServiceComponentPhysicalHost(String serv, String comp,
			String host) {
		Service_Component_PhysicalHost scp;
	
		scp = findService_Component_PhysicalHost(serv, comp, host);
		if (scp == null) {
			Service_Component sc = findORcreateServiceComponent(serv, comp);
			PhysicalHost p = findORcreatePhysicalHost(host);
			scp = new Service_Component_PhysicalHost(sc, p);
			scpList.add(scp);
		}
		return scp;
	}
	
        private Service findService(String serv) {
		for (Service s : serviceList)
			if (serv.equals(s.name)) 
				return s;
		
		return null;
        }//findService()
        
	private Component findComponent(String comp) {
		for (Component c : componentList)
			if (comp.equals(c.name)) 
				return c;
		
		return null;
	}
	private PhysicalHost findPhysicalHost(String host) {
		for (PhysicalHost p : physicalHostList)
			if (host.equals(p.name)) 
				return p;
		
		return null;
	}
	private Service_Component findService_Component(String serv, String comp) {
		for (Service_Component sc : scList)
			if (serv.equals(sc.s.name) && comp.equals(sc.c.name)) 
				return sc;
		
		return null;
	}
	private Service_Component_PhysicalHost 
	findService_Component_PhysicalHost(String serv, String comp,
				String host) {
		for (Service_Component_PhysicalHost scp: scpList)
			if (serv.equals(scp.sc.s.name) && 
					comp.equals(scp.sc.c.name) && host.equals(scp.p.name))
				return scp;
		
		return null;
	}
	// finds a Service_Component_PhysicalHost
	// using reference equality
	 
	private Service_Component_PhysicalHost 
	findService_Component_PhysicalHost(Service_Component sc,
			PhysicalHost p) {
		for (Service_Component_PhysicalHost scp: scpList)
			if ((scp.sc == sc) && (scp.p == p))
				return scp;
		
		return null;
	}

}//class

