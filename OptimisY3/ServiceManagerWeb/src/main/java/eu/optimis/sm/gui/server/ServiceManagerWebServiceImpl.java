/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import eu.optimis.DataManagerClient.DataManagerClient;
import eu.optimis.cbr.client.CBRClient;
import javax.mail.*;
import javax.mail.internet.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import eu.optimis.sm.gui.client.ServiceManagerWebService;
import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.sm.gui.client.model.IP;
import eu.optimis.sm.gui.client.model.Service;
import eu.optimis.sm.gui.utils.Constants;
import eu.optimis.sm.gui.client.model.ServiceProvider;
import eu.optimis.sm.gui.client.model.ServiceProviderVM;
import eu.optimis.sm.gui.utils.ConfigManager;
import eu.optimis.sm.gui.utils.SLAClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.mindrot.jbcrypt.BCrypt;
import org.ogf.graap.wsag.api.client.AgreementClient;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hsqldb.Server;

public class ServiceManagerWebServiceImpl extends RemoteServiceServlet 
implements  ServiceManagerWebService {
	private static Logger logger = Logger.getLogger(ServiceManagerWebServiceImpl.class);
    PropertiesConfiguration configServiceManagerWeb;
	
	private static final long serialVersionUID = 1L;
	public static String SM_URL;
	public static String SM_PORT;
	public static String SDO_URL;
	public static String IPS_URL;
	public static String VPN_URL;
	public static String SEC_URL;
	public static String TREC_URL;
	public static String dm_gui_atos_URL;
	public static String dm_gui_umea_URL;
	public static String dm_gui_flex_enh_URL;
	public static String dm_gui_flex_URL;
	public static String dm_gui_leeds_URL;
	public static String dm_gui_arsys_URL;
	public static String dm_gui_amazon_URL;
	private static ArrayList<String> session_ids = new ArrayList<String>();// = -1;
	private static ArrayList<Double> session_times = new ArrayList<Double>();// = -1;
	private static ArrayList<String> session_users = new ArrayList<String>();// = -1;
	private static String userKeyUnique;
	private static Server hsqlServer = null;
	static String tableName = "testtable5";
	String store2;
	String file;
	    		
	public ServiceManagerWebServiceImpl() {
        PropertyConfigurator.configure(ConfigManager.getFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        configServiceManagerWeb = ConfigManager.getPropertiesConfiguration(ConfigManager.SMWEB_CONFIG_FILE);
        
		logger.info("ServiceManagerWebServiceImpl initialisation started...");
		try {
			ResourceBundle rb = ResourceBundle.getBundle("config", Locale.getDefault());
			SM_URL = rb.getString("sm.url");
			SM_PORT = rb.getString("sm.port");
			SDO_URL = rb.getString("sdo.url");
			IPS_URL = rb.getString("ips.url");
			VPN_URL = rb.getString("vpn.url");
			SEC_URL = rb.getString("sec.url");
			TREC_URL = rb.getString("trec.url");

		} catch (java.util.MissingResourceException e) {
			GWT.log("cannot found property file for SP Dashboard");
			e.printStackTrace();
		} catch (Exception ex) {
			GWT.log("cannot found property sm");
		}		
		
        hsqlServer = new Server();
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, "xdb");
        hsqlServer.setDatabasePath(0, "file:testdb");
		if(session_ids.size()==0) {
			session_ids.add("-1");
			session_users.add("no_user");
			session_times.add(0.0);
			}
		userKeyUnique = "1";
	}

	public String getTestTest(String id) {
		return "OK";
	}	
//---------------------------------------------------------------
	public ArrayList<IP> ipRegistry(String sess_id) {
        logger.info("ipRegistry: sess_id = " + sess_id);
        logger.info("ipRegistry: session_id = " + session_ids);
        
		ArrayList<IP> ips = new ArrayList<IP>();
		String res;
		res = checkSession(sess_id);
		if(res!=null) {
			logger.info("Wrong session...");
			ips.add(new IP());
			ips.get(ips.size()-1).set("ip_name", "-100");
			ips.get(ips.size()-1).set("ip_ip", res);
			return ips;
		}
        
		String host = SM_URL; // "optimis-spvm.atosorigin.es";
		String port = SM_PORT;
		res = "IPRegistry for host = " + host + ":" + port + "\n";
		try {
		CBRClient cbrClient = new CBRClient(host, port);
		res = res + "Total number of IPs = " + cbrClient.getAllIP().getIPList().size() +"\n";
		for(int i = 0; i < cbrClient.getAllIP().getIPList().size(); i++) {
			res = res + "\nIP #" + i + ":";
			res = res + "\n   Name: " + cbrClient.getAllIP().getIPList().get(i).getName();
			res = res + "\n   IP: " + cbrClient.getAllIP().getIPList().get(i).getIpAddress();
			res = res + "\n   ID: " + cbrClient.getAllIP().getIPList().get(i).getIdentifier();
			res = res + "\n   Provider type: " + cbrClient.getAllIP().getIPList().get(i).getProviderType();
			res = res + "\n   AgrTemplateId: " + cbrClient.getAllIP().getIPList().get(i).getAgrTemplateId();
			res = res + "\n   AgrTemplateName: " + cbrClient.getAllIP().getIPList().get(i).getAgrTemplateName();
			res = res + "\n   CloudQosUrl: " + cbrClient.getAllIP().getIPList().get(i).getCloudQosUrl();
			res = res + "\n   Properties.values(): " + cbrClient.getAllIP().getIPList().get(i).getProperties().values().toString();
			
			String dm_gui_url = cbrClient.getAllIP().getIPList().get(i).getDMUrl();
			logger.info("dm_gui_url = " + dm_gui_url);
			
			ips.add(new IP());
			ips.get(ips.size()-1).set("ip_name", cbrClient.getAllIP().getIPList().get(i).getName());
			ips.get(ips.size()-1).set("ip_ip", cbrClient.getAllIP().getIPList().get(i).getIpAddress());
			ips.get(ips.size()-1).set("ip_id", cbrClient.getAllIP().getIPList().get(i).getIdentifier());
			ips.get(ips.size()-1).set("ip_provider_type", cbrClient.getAllIP().getIPList().get(i).getProviderType());
			ips.get(ips.size()-1).set("cloud_qos_url", cbrClient.getAllIP().getIPList().get(i).getCloudQosUrl());
			ips.get(ips.size()-1).set("dm_gui", dm_gui_url);
		}
		
		logger.info("cbrClient.getAllIP().getIPList() for " + host + ":" + port);
		logger.info(res);
		logger.info("server ips(0) = " + ips.get(0).toString());
		}
		catch(Exception e){}
		return ips;
	}
//---------------------------------------------------------------
	public ArrayList<Service> availableServices(String sess_id, boolean test) {
        logger.info("availableServices: sess_id = " + sess_id);
        
		ArrayList<Service> services = new ArrayList<Service>();
		String res = checkSession(sess_id);
		if(res!=null) {
			logger.info("Wrong session...");
			services.add(new Service());
			services.get(services.size()-1).set("service_number", "-100");
			services.get(services.size()-1).set("service_id", res);
			return services;
		}
		
        PropertyConfigurator.configure(ConfigManager.getFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        configServiceManagerWeb = ConfigManager.getPropertiesConfiguration(ConfigManager.SMWEB_CONFIG_FILE);

        String port, host;
		String output3 = null;
		
		/*
		sla0 = "<agreement_endpoint><xml-fragment> <wsa:Address xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" +
				"http://optimis-ipvm2.ds.cs.umu.se:8080/optimis-sla/services/Agreement</wsa:Address>" +
				"<wsa:ReferenceParameters xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">" +
				"<ResourceId xmlns=\"http://schemas.scai.fraunhofer.de/wsag4j\">WSAG4J_ResourceId-8</ResourceId>" +
				"</wsa:ReferenceParameters><Metadata xmlns=\"http://www.w3.org/2005/08/addressing\">" +
				"<ServerIdentity xmlns=\"http://schemas.scai.fraunhofer.de/2008/11/wsag4j/engine\">" +
				"OU=WSAG4J Development,O=wsag4j.sf.net,1.2.840.113549.1.9.1=#16147365727665724077736167346a2e73662e6e6574," +
				"C=DE,ST=NRW,CN=WSAG4J Server</ServerIdentity> </Metadata> </xml-fragment></agreement_endpoint>";
				
		NO_VM_SERVICE_XML = "<service xmlns=\"http://www.optimis.eu/service-manager\">"
				+ "<service_id>" + serId + "</service_id>" + "<status>pending</status>"
				+ "<infrastructure-provider>" + "<id>" + id0 + "</id>"
			    + "<ip_address>" + ip0 + "</ip_address>" + "<sla_id>" + sla0 + "</sla_id>"
			    + "<agreement_endpoint>" + agr0 + "</agreement_endpoint>"
				+ "</infrastructure-provider>" + "</service>";
		*/
		host = SM_URL; //host = "optimis-spvm2.ds.cs.umu.se";
		port = SM_PORT;
		
    	ServiceManagerClient smClient = new ServiceManagerClient(host,port);
    	
		String ser = "ser:";
		logger.info("------------------------------");
		logger.info("output #3 (host = " + host + ")");
		output3 = smClient.getServices();
		logger.info(output3);
		Document doc = XmlUtil.getDocument(output3);
		NodeList nList = doc.getElementsByTagName(ser+"service");
		List<ServiceProvider> listSP = null;
		List<ServiceProviderVM> listSPVM = null;
		logger.info("number of services = "+nList.getLength());
		ArrayList<Object> returnedArray = new ArrayList<Object>();
		
		if(nList.getLength()==0) {
			logger.info("No services found!");
			services.add(new Service());
			services.get(services.size()-1).set("service_number", "-100");
			services.get(services.size()-1).set("service_id", "No services found!");
			returnedArray.add(0, services);
			return services;
		}

		for (Integer temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				services.add(new Service());
				String servID;
				services.get(services.size()-1).set("service_number", temp.toString());
				services.get(services.size()-1).set("service_id", getTagValue(ser+"service_id", eElement));
				services.get(services.size()-1).set("service_status", getTagValue(ser+"status", eElement));
				services.get(services.size()-1).set("manifest_id", getTagValue(ser+"manifest_id", eElement));
				services.get(services.size()-1).set("listServiceProvider", null);
				servID = getTagValue(ser+"service_id", eElement);

				NodeList nList2 = nNode.getChildNodes();
				listSP = new ArrayList<ServiceProvider>();
				for (int temp2 = 0; temp2 < nList2.getLength(); temp2++) {
					Node nNode2 = nList2.item(temp2);
					if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement2 = (Element) (nNode2);
						if(getTagValue(ser+"id", eElement2)!=null) {
							listSP.add(new ServiceProvider());
							String dm;
							String getCPD = "<br><b>Data Manager client info</b>" + " (for "+getTagValue(ser+"id", eElement2) + ")<br>";
							DataManagerClient dmClient = new DataManagerClient();
							try {
								dm = dmClient.getCPD(getTagValue(ser+"id", eElement2));
								//getCPD = getCPD + "  (dmClient.dmClient.getCPD(): success!)<br>";
								
//-----------------------------------------------------------------------------------------------------------
/**/								
								Document docDM = XmlUtil.getDocument(dm);
								NodeList nListDM = docDM.getElementsByTagName("tns:IaaSProvider");
								for (Integer t = 0; t < nListDM.getLength(); t++) {
									Node nNodeDM = nListDM.item(t);
									if (nNodeDM.getNodeType() == Node.ELEMENT_NODE){											
										getCPD = getCPD + "<b>" + nNodeDM.getNodeName().subSequence(Math.min(nNodeDM.getNodeName().length(), 4), nNodeDM.getNodeName().length()) + "</b>:<br>";
										NodeList nListDM2 = nNodeDM.getChildNodes();
										for (int tt = 0; tt < nListDM2.getLength(); tt++) {
											Node nNodeDM2 = nListDM2.item(tt);
											if (nNodeDM2.getNodeType() == Node.ELEMENT_NODE) {								
												NodeList nListDM3 = nNodeDM2.getChildNodes();
												if(nListDM3.getLength()<=1) {
													if(nNodeDM2.getNodeName()!=null)
														getCPD = getCPD + "-" + nNodeDM2.getNodeName().subSequence(Math.min(nNodeDM2.getNodeName().length(), 4), nNodeDM2.getNodeName().length()) + ": ";
													if(nNodeDM2.getTextContent()!=null)
														getCPD = getCPD + "" + nNodeDM2.getTextContent() + "<br>";
												}
												  for (int ttt = 0; ttt < nListDM3.getLength(); ttt++) {
													Node nNodeDM3 = nListDM3.item(ttt);
													if (nNodeDM3.getNodeType() == Node.ELEMENT_NODE) {
														NodeList nListDM4 = nNodeDM3.getChildNodes();
														if(nListDM4.getLength()<=1) {
															if(nNodeDM3.getNodeName()!=null)
																getCPD = getCPD + "---" + nNodeDM3.getNodeName().subSequence(Math.min(nNodeDM3.getNodeName().length(), 4), nNodeDM3.getNodeName().length()) + ": ";
															if(nNodeDM3.getTextContent()!=null)
																getCPD = getCPD + "" + nNodeDM3.getTextContent() + "<br>";
														}
														  for (int tttt = 0; tttt < nListDM4.getLength(); tttt++) {
															Node nNodeDM4 = nListDM4.item(tttt);
															if (nNodeDM4.getNodeType() == Node.ELEMENT_NODE) {								
																NodeList nListDM5 = nNodeDM4.getChildNodes();
																if(nListDM5.getLength()<=1) {
																	if(nNodeDM4.getNodeName()!=null)
																		getCPD = getCPD + "------" + nNodeDM4.getNodeName().subSequence(Math.min(nNodeDM4.getNodeName().length(), 4), nNodeDM4.getNodeName().length()) + ": ";
																	if(nNodeDM4.getTextContent()!=null)
																		getCPD = getCPD + "" + nNodeDM4.getTextContent() + "<br>";
																}
																  for (int ttttt = 0; ttttt < nListDM5.getLength(); ttttt++) {
																	Node nNodeDM5 = nListDM5.item(ttttt);
																	if (nNodeDM5.getNodeType() == Node.ELEMENT_NODE) {								
																		if(nNodeDM5.getNodeName()!=null)
																			getCPD = getCPD + "------------" + nNodeDM5.getNodeName().subSequence(Math.min(nNodeDM5.getNodeName().length(), 4), nNodeDM5.getNodeName().length()) + ": ";
																		if(nNodeDM5.getTextContent()!=null)
																			getCPD = getCPD + "" + nNodeDM5.getTextContent() + "<br>";
																	}
																  }	
															}
														  }	
													}
												}
											}
										}
									}
								}
/**/								
//-----------------------------------------------------------------------------------------------------------
							} catch (Exception e1) {
								getCPD = getCPD + "  dmClient.dmClient.getCPD() error: " + e1.getMessage() + "...";
								e1.printStackTrace();
								logger.info(e1.getMessage());
							}
						    String str5 = "<style> label {position: relative;} .box {position: absolute;" +
							"left: 0; top: 100%; z-index: 100; -webkit-backface-visibility: hidden;" +
							"-moz-border-radius:    0px; -webkit-border-radius: 0px;" + 
							"border-radius:         0px; -moz-background-clip:    padding;" + 
							"-webkit-background-clip: padding-box; background-clip:         padding-box;" + 
							"width: 20px; padding: 0px; margin: 0px 0; opacity: 0; }" +
							".box:after {content: \"\";	position: absolute; bottom: 100%;" +
							"left: 0px; border-bottom: 0px solid #eee;" +
							"border-left:   0px solid transparent; border-right:  14px solid transparent;" +
							"width:  20px; 	height: 0; } .popUpControl:checked ~ label > .box {" +
							"opacity: 1; } .popUpControl { display: none;  }" +
							".link { color: blue; text-decoration: underline; width: 20px; }" +
							".title"+temp+temp2+getTagValue(ser+"id", eElement2)+"1 { display: block; margin: -10px 0px 0px -75px; color: black; width: 350px; " +
							" font: 14px Sans-Serif; text-decoration: none; background-color:#FFFFFF; }" +
						    ".copy { color: black; text-decoration: none; background-color:#FFFFFF; width: 20px; }" +
							"</style> <div id=\"page-wrap\"> <p>" +
								  "<input type=\"checkbox\" id=\"linkie"+temp+temp2+getTagValue(ser+"id", eElement2)+"\" class=\"popUpControl\">" +
									"<label for=\"linkie"+temp+temp2+getTagValue(ser+"id", eElement2)+"\" class=\"link\">" +
									   "<span>DM client info</span> <span class=\"box\">" +
						  			   "<span class=\"title"+temp+temp2+getTagValue(ser+"id", eElement2)+"1\">" + getCPD + "</span>" +
						  			"</span> </label> </p> </div>";
							
						    getCPD = str5;
							logger.info(getCPD);
							
							listSP.get(listSP.size()-1)
							.set("provider_id", getTagValue(ser+"id", eElement2) + "<br><u><a href=\"" + 
							TREC_URL+"?side=sp&providerId="+getTagValue(ser+"id", eElement2)+"&stage=operation"
									+"&identifier=" + servID + "&level=service"
									+ "\" target=\"_blank\">Link: TREC GUI (for "+getTagValue(ser+"id", eElement2)+" operation)</a>" +
											"</u><br><u><a href=\"" + 
							TREC_URL+"?side=sp&providerId="+getTagValue(ser+"id", eElement2)+"&stage=deployment"
									+"&identifier=" + servID + "&level=service"
									+ "\" target=\"_blank\">Link: TREC GUI (for "+getTagValue(ser+"id", eElement2)+" deployment history)</a>" +
											"</u>");
							listSP.get(listSP.size()-1)
								.set("provider_ip", getTagValue(ser+"ip_address", eElement2));
							listSP.get(listSP.size()-1)
								.set("provider_sla", getTagValue(ser+"sla_id", eElement2));

							String slaInfo = "<br><b>SLA Details</b>" + " (for "+getTagValue(ser+"id", eElement2) + ")<br>";
							SLAClient slaClient = new SLAClient();
							
							try {
								AgreementClient ac = slaClient.getSLA(getTagValue(ser+"agreement_endpoint", eElement2));

								Document docSLA = XmlUtil.getDocument(ac.getTerms().xmlText());

								NodeList nListSLA2 = docSLA.getElementsByTagName("opt:DataProtectionSection");
								for (Integer t = 0; t < nListSLA2.getLength(); t++) {
									Node nNodeSLA2 = nListSLA2.item(t);
									if (nNodeSLA2.getNodeType() == Node.ELEMENT_NODE){											
										slaInfo = slaInfo + "----------Data Protection Section<br>";
										
										NodeList nListSLA22 = nNodeSLA2.getChildNodes();
										for (int tt = 0; tt < nListSLA22.getLength(); tt++) {
											Node nNode22 = nListSLA22.item(tt);
											if (nNode22.getNodeType() == Node.ELEMENT_NODE) {
												
												if(nNode22.getNodeName().equalsIgnoreCase("opt:SCC")) {											
													slaInfo = slaInfo + "----------SCC terms<br>";
													NodeList nListSLA33 = nNode22.getChildNodes();
													for (int ttt = 0; ttt < nListSLA33.getLength(); ttt++) {
														Node nNode33 = nListSLA33.item(ttt);
														if (nNode33.getNodeType() == Node.ELEMENT_NODE)	{
															Element e33 = (Element) nNode33;
															if(getTagValue("Title", e33)!=null)
																slaInfo = slaInfo + "----------" + getTagValue("Title", e33) + ": "; 
															if(getTagValue("Description", e33)!=null)
																slaInfo = slaInfo + getTagValue("Description", e33) + "<br>";
															
															NodeList nListSLA44 = e33.getElementsByTagName("Item");
															for (int tttt = 0; tttt < nListSLA44.getLength(); tttt++) {
																Node nNode44 = nListSLA44.item(tttt);
																if (nNode44.getNodeType() == Node.ELEMENT_NODE) {
																	if(nNode44.getTextContent()!=null)
																		slaInfo = slaInfo + "---" + nNode44.getTextContent() + "<br>";
																	}
															}
														}
													}
												}
												
												if(nNode22.getNodeName().equalsIgnoreCase("opt:BCR")) {											
													if(nNode22.getNodeName()!=null)
														slaInfo = slaInfo + "----------BCR terms<br>";
													NodeList nListSLA33 = nNode22.getChildNodes();
													for (int ttt = 0; ttt < nListSLA33.getLength(); ttt++) {
														Node nNode33 = nListSLA33.item(ttt);
														if (nNode33.getNodeType() == Node.ELEMENT_NODE)	{
															Element e33 = (Element) nNode33;
															if(getTagValue("Title", e33)!=null)
																slaInfo = slaInfo + "----------" + getTagValue("Title", e33) + ": "; 
															if(getTagValue("Description", e33)!=null)
																slaInfo = slaInfo + getTagValue("Description", e33) + "<br>";
															NodeList nListSLA44 = e33.getElementsByTagName("Item");
															for (int tttt = 0; tttt < nListSLA44.getLength(); tttt++) {
																Node nNode44 = nListSLA44.item(tttt);
																if (nNode44.getNodeType() == Node.ELEMENT_NODE) {
																	if(nNode44.getTextContent()!=null)
																		slaInfo = slaInfo + "---" + nNode44.getTextContent() + "<br>";
																	}
															}
														}
													}
												}
												
												if(nNode22.getNodeName().equalsIgnoreCase("opt:IPR")) {											
													slaInfo = slaInfo + "----------IPR terms<br>";
													NodeList nListSLA33 = nNode22.getChildNodes();
													for (int ttt = 0; ttt < nListSLA33.getLength(); ttt++) {
														Node nNode33 = nListSLA33.item(ttt);
														if (nNode33.getNodeType() == Node.ELEMENT_NODE)	{
															Element e33 = (Element) nNode33;
															if(getTagValue("opt:Title", e33)!=null)
																slaInfo = slaInfo + "----------" + getTagValue("opt:Title", e33) + ": "; 
															if(getTagValue("opt:Description", e33)!=null)
																slaInfo = slaInfo + getTagValue("opt:Description", e33) + "<br>";
															NodeList nListSLA44 = e33.getElementsByTagName("opt:Item");
															for (int tttt = 0; tttt < nListSLA44.getLength(); tttt++) {
																Node nNode44 = nListSLA44.item(tttt);
																if (nNode44.getNodeType() == Node.ELEMENT_NODE)	{
																	if(nNode44.getTextContent()!=null)
																		slaInfo = slaInfo + "---" + nNode44.getTextContent() + "<br>";
																	}
															}
														}
													}
												}
											}
										}
									}
								}
//-----------------------------------------------------------------------------------------------------------
							} catch (Exception e) {
								slaInfo = slaInfo + "  slaClient.getSLA() error: " + e.getMessage() + "...";
								e.printStackTrace();
								logger.info(e.getMessage());
							}
						    String str5a = "<style> label {position: relative;} .box {position: absolute;" +
							"left: 0; top: 100%; z-index: 100; -webkit-backface-visibility: hidden;" +
							"-moz-border-radius: 0px; -webkit-border-radius: 0px;" + 
							"border-radius: 0px; -moz-background-clip: padding;" + 
							"-webkit-background-clip: padding-box; background-clip: padding-box;" + 
							"width: 20px; padding: 0px; margin: 0px 0; opacity: 0; }" +
							".box:after {content: \"\";	position: absolute; bottom: 100%;" +
							"left: 0px; border-bottom: 0px solid #eee;" +
							"border-left:   0px solid transparent; border-right:  14px solid transparent;" +
							"width:  20px; height: 0; } .popUpControl:checked ~ label > .box {" +
							"opacity: 1; } .popUpControl { display: none;  }" +
							".link { color: blue; text-decoration: underline; width: 20px; }" +
							".title"+temp+temp2+getTagValue(ser+"id", eElement2)+" { display: block; margin: -10px 0px 0px -100px; color: black; width: 450px; " +
							" font: 14px Sans-Serif; text-decoration: none; background-color:#FFFFFF; }" +
						    ".copy { color: black; text-decoration: none; background-color:#FFFFFF; width: 20px; }" +
							"</style> <div id=\"page-wrap\"> <p>" +
								  "<input type=\"checkbox\" id=\"linkie"+temp+temp2+getTagValue(ser+"id", eElement2)+"2\" class=\"popUpControl\">" +
									"<label for=\"linkie"+temp+temp2+getTagValue(ser+"id", eElement2)+"2\" class=\"link\">" +
									   "<span>SLA info</span> <span class=\"box\">" +
						  			   "<span class=\"title"+temp+temp2+getTagValue(ser+"id", eElement2)+"\">" + slaInfo + "</span>" +
						  			"</span> </label> </p> </div>";						    
						    
						    slaInfo = str5a;

						    String agrEndp;
						    agrEndp = getTagValue(ser+"agreement_endpoint", eElement2);
						    
						    agrEndp = "<style> label {position: relative;} .box {position: absolute;" +
									"left: 0; top: 100%; z-index: 100; -webkit-backface-visibility: hidden;" +
									"-moz-border-radius: 0px; -webkit-border-radius: 0px;" + 
									"border-radius: 0px; -moz-background-clip: padding;" + 
									"-webkit-background-clip: padding-box; background-clip: padding-box;" + 
									"width: 20px; padding: 0px; margin: 0px 0; opacity: 0; }" +
									".box:after {content: \"\";	position: absolute; bottom: 100%;" +
									"left: 0px; border-bottom: 0px solid #eee;" +
									"border-left:   0px solid transparent; border-right:  14px solid transparent;" +
									"width:  20px; height: 0; } .popUpControl:checked ~ label > .box {" +
									"opacity: 1; } .popUpControl { display: none;  }" +
									".link { color: blue; text-decoration: underline; width: 20px; }" +
									".titlex"+temp+temp2+getTagValue(ser+"id", eElement2)+" { display: block; margin: -10px 0px 0px -100px; color: black; width: 450px; " +
									" font: 14px Sans-Serif; text-decoration: none; background-color:#FFFFFF; }" +
								    ".copy { color: black; text-decoration: none; background-color:#FFFFFF; width: 20px; }" +
									"</style> <div id=\"page-wrap\"> <p>" +
										  "<input type=\"checkbox\" id=\"linkiex"+temp+temp2+getTagValue(ser+"id", eElement2)+"2\" class=\"popUpControl\">" +
											"<label for=\"linkiex"+temp+temp2+getTagValue(ser+"id", eElement2)+"2\" class=\"link\">" +
											   "<span>Agreement Endpoint</span> <span class=\"box\">" +
								  			   "<span class=\"titlex"+temp+temp2+getTagValue(ser+"id", eElement2)+"\">" + agrEndp + "</span>" +
								  			"</span> </label> </p> </div>";						    
									    
							listSP.get(listSP.size()-1)
							.set("provider_agreement_endpoint",agrEndp);
							listSP.get(listSP.size()-1)
								.set("provider_initial_trust_value", getTagValue(ser+"initial_trust_value", eElement2));
							listSP.get(listSP.size()-1)
								.set("provider_initial_risk_value", getTagValue(ser+"initial_risk_value", eElement2));
							listSP.get(listSP.size()-1)
								.set("provider_initial_eco_value", getTagValue(ser+"initial_eco_value", eElement2));
							listSP.get(listSP.size()-1)
								.set("provider_initial_cost_value", getTagValue(ser+"initial_cost_value", eElement2));
							listSP.get(listSP.size()-1)
								.set("data_manager_info", getCPD);
							listSP.get(listSP.size()-1)
								.set("sla_details", slaInfo);
								}
						NodeList nList3a = nNode2.getChildNodes();
						for (int temp3a = 0; temp3a < nList3a.getLength(); temp3a++) {
							Node nNode3a = nList3a.item(temp3a);
							if (nNode3a.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement3a = (Element) (nNode3a);
								NodeList nList3 = eElement3a.getElementsByTagName(ser+"vm");
								listSPVM = new ArrayList<ServiceProviderVM>();
								for (int temp3 = 0; temp3 < nList3.getLength(); temp3++) {
									Node nNode3 = nList3.item(temp3);
									if (nNode3.getNodeType() == Node.ELEMENT_NODE)
									{
										Element eElement3 = (Element) (nNode3);
										if(getTagValue(ser+"id", eElement3)!=null)
										{
											listSPVM.add(new ServiceProviderVM());
											listSPVM.get(listSPVM.size()-1)
												.set("vm_id", getTagValue(ser+"id", eElement3));
											listSPVM.get(listSPVM.size()-1)
												.set("vm_type", getTagValue(ser+"type", eElement3));
											listSPVM.get(listSPVM.size()-1)
												.set("vm_status", getTagValue(ser+"status", eElement3));
											listSPVM.get(listSPVM.size()-1)
												.set("vm_deployment_duration_in_ms", getTagValue(ser+"deployment_duration_in_ms", eElement3));
										}
										else
											logger.info("eElement3error===="+eElement3.toString());
									}
									if(listSPVM!=null)
									{
										String vms = new String();
										for (int rr=0; rr<listSPVM.size(); rr++)
											vms = vms + listSPVM.get(rr).toString();
										listSP.get(listSP.size()-1).set("listServiceProviderVMStr", vms);
									}
								}
							}
						}
					}
				}
				String providers = new String();
				for (int tt=0; tt<listSP.size(); tt++)
					providers = providers + listSP.get(tt).toString();
				services.get(services.size()-1).set("listServiceProviderStr", providers);
			}
		}
		returnedArray.add(0, services);
		return services;
		}
//---------------------------------------------------------------
private String checkSession(String sess_id) {
	
	String res = null;
	if(session_times.size()<=1)
	{
		res = "- Wrong session id! Please log out, log in again and refresh the selected option";
		logger.info(res);
		return res;
	}
	if(sess_id.equalsIgnoreCase("0"))
	{
		res = "- Wrong session id! Please log out, log in again and refresh the selected option";
		logger.info(res);
		return res;
	}
	
    if(!session_ids.contains(sess_id)) {
    	res = "- Wrong session id! Please log out, log in again and refresh the selected option";
    	logger.info(res);
    	return res;
    }
    else {
    	try{
    	// 120 minutes as timeout limit
    	Double timeout_limit = 60.0*120.0;
    	logger.info("elapsed session time = " + (System.currentTimeMillis()
    			- session_times.get(session_ids.indexOf(sess_id)))/1000 + " seconds");
    	
    	int index = session_ids.indexOf(sess_id);
    	if(index != 0)
    	 if(index != -1)
    		if(((System.currentTimeMillis() - session_times.get(index))/1000 > timeout_limit)) {
    			res = "- Session time elapsed! Please log out, log in again and refresh the selected option";
    			logger.info(res);
    			return res;
    		}
    		else
    			session_times.set(index, (double) System.currentTimeMillis());

        //Total check
        for(String sess : session_ids)
        {
        	int index4 = session_ids.indexOf(sess);
        	if(index4 != -1)
            	if(index4 != 0)
            		if(((System.currentTimeMillis() - session_times.get(index4))/1000 > timeout_limit)) {
            			logoutUser(sess, session_users.get(index4));
            			//"return" here is to avoid index conflicts
            			return res; 
        	}
        }    
    	} catch (Exception e) {
    		logger.info("Session check exception!  " + e);
    		e.printStackTrace();
    	}    	

    }
	return res;
}
//---------------------------------------------------------------
private String getTagValue(String sTag, Element eElement) {
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch (NullPointerException npe) {
			return null;
		}
	}
//---------------------------------------------------------------	
	public String undeployService(String sess_id, String serviceID, boolean keepData) {
		
        logger.info("undeployService: sess_id = " + sess_id);
        logger.info("undeployService: session_id = " + session_ids);
		
        String res = checkSession(sess_id);
		if(res!=null)
			return res;

		String port; // = "optimis-spvm.atosorigin.es";
		String host; // = "8080";
		host = SM_URL;
		port = SM_PORT; //"8080";
		
		String undeployedReturnedValue = "null";
		Boolean undeployedReturnedValue0 = null;
		
		logger.info("Undeploying service:");
		logger.info("serviceId = "+serviceID+"; keepData="+keepData);
		
		ServiceManagerClient smRESTClient = new ServiceManagerClient(host, port);
    	try {
    		undeployedReturnedValue0 = smRESTClient.undeploy(serviceID, keepData);
    		undeployedReturnedValue = undeployedReturnedValue0.toString();
			} catch (Exception ex) {
				logger.info("Service not found!");
				logger.info(ex.toString());
				undeployedReturnedValue = undeployedReturnedValue + "; Caught exception: " + ex.toString(); 
		}
    	undeployedReturnedValue = undeployedReturnedValue + " \n(keepData = " + keepData+")";
		logger.info("undeployedReturnedValue = "+undeployedReturnedValue);
		
		return undeployedReturnedValue;
	}
//---------------------------------------------------------------
	public String getSDOurl(String sess_id) {
		logger.info("getSDOurl: sess_id = " + sess_id);
		logger.info("getSDOurl: session_id = " + session_ids);
		String res = checkSession(sess_id);
		if(res!=null) return res;
		return SDO_URL;
	}
//---------------------------------------------------------------
	public String getIPSurl(String sess_id) {
		logger.info("getIPSurl: sess_id = " + sess_id);
		logger.info("getIPSurl: session_id = " + session_ids);
		String res = checkSession(sess_id);
		if(res!=null) return res;
		return IPS_URL;
		}
//---------------------------------------------------------------
	public String getVPNurl(String sess_id) {
		logger.info("getVPNurl: sess_id = " + sess_id);
		logger.info("getVPNurl: session_id = " + session_ids);
		String res = checkSession(sess_id);
		if(res!=null) return res;
		return VPN_URL;
	}
//---------------------------------------------------------------
	public String getSECurl(String sess_id) {
		logger.info("getSECurl: sess_id = " + sess_id);
		logger.info("getSECurl: session_id = " + session_ids);
		String res = checkSession(sess_id);
		if(res!=null) return res;
		return SEC_URL;
	}
//---------------------------------------------------------------
		public String getTRECurl(String sess_id) {
			logger.info("getSECurl: sess_id = " + sess_id);
			logger.info("getSECurl: session_id = " + session_ids);
			String res = checkSession(sess_id);
			if(res!=null) return res;
			return TREC_URL;
		}
//---------------------------------------------------------------
	public String redeployService(String sess_id, String serviceID, boolean keepData) {
			
		logger.info("undeployService: sess_id = " + sess_id);
		logger.info("undeployService: session_id = " + session_ids);
		String res = checkSession(sess_id);
		if(res!=null)
			return res;
		
		String port; // = "optimis-spvm.atosorigin.es";
		String host; // = "8080";
		host = SM_URL;
		port = SM_PORT; //"8080";
		
		String redeployedReturnedValue = "null";
		Boolean redeployedReturnedValue0 = null;
			
		logger.info("Undeploying service:");
		logger.info("serviceId = "+serviceID+"; keepData="+keepData);
			
		ServiceManagerClient smRESTClient = new ServiceManagerClient(host, port);
		try {
			redeployedReturnedValue0 = smRESTClient.redeploy(serviceID, keepData);
			redeployedReturnedValue = redeployedReturnedValue0.toString();
		} catch (Exception ex) {
			logger.info("Service not found!");
			logger.info(ex.toString());
			redeployedReturnedValue = redeployedReturnedValue + "; Caught exception: " + ex.toString(); 
		}
		redeployedReturnedValue = redeployedReturnedValue + " \n(keepData = " + keepData+")";
		logger.info("undeployedReturnedValue = "+redeployedReturnedValue);
			
		return redeployedReturnedValue;
		}
//---------------------------------------------------------------	
	public ArrayList<Object> loginUser(String name, String pass) {
		String resultName = "none";
		String res = "0";
	    String id, p = null;
    
	    ResultSet results = null;
		try {
			results = readDB(name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    Boolean found = false;
	    if (results!=null) {
	    	try {
	    		resultName = results.getString(2);
	    		logger.info("result = " + resultName	+ "; id = " + results.getString(1));
	    		//+ "; pass = " + results.getString(3));
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    	if(resultName.equals(name)) {
	    		found = true;
	    		try {
	    			p = results.getString(3);
	    		} catch (SQLException e) {
	    			e.printStackTrace();
	    		}
	    		boolean valid = BCrypt.checkpw(pass, p);
	    		if(valid) {
	    			res = "User found! Welcome to SP Dashboard, " + name + "!";
	    		    	//+ "; your password is: " + pass + "; your encrypted password is: " + p;
	    		    }
	    		else {
	    			res = "User/pass are wrong! Please correct input data or register an account";
	    			logger.info("User/pass are wrong; reason: pass is wrong");
	    		}
	    	}
	    	if(!found) {
	    		res = "User/pass are wrong! Please correct input data or register an account";
	    		logger.info("User/pass are wrong; reason: name not found");
	    	}
	    } else {
	    	res = "User/pass are wrong! Please correct input data or register an account";
	    	logger.info("User/pass are wrong; reason: results==null");
	    }
	    logger.info(res);
		
		ArrayList<Object> returnedArray = new ArrayList<Object>();
		returnedArray.add(0, res);
		id = UUID.randomUUID().toString();
    	session_ids.add(id);
    	session_users.add(name);
    	session_times.add((double)System.currentTimeMillis());

		logger.info("Session created; id = "+ id + "; name = " + name + "; " +
				"time = " + session_times.get(session_times.size()-1) + 
				"; session_ids.size() = " + session_ids.size() + "; session_users.size() = " + session_users.size());
		
		returnedArray.add(1, id.toString());
		return returnedArray;
	}
//---------------------------------------------------------------	
	public Boolean logoutUser(String sess_id, String name) {
		try {
		int index1 = session_ids.indexOf(sess_id); 
			logger.info("Session deleting: id = "+ sess_id + "; name = " + name + "; index1 = " + index1 + //"; index2 = " + index2 + ";" +
					"; time = " + (System.currentTimeMillis() - session_times.get(index1))/1000 + 
					" session_ids.size() = " + session_ids.size() + "; session_users.size() = " + session_users.size());
			
		session_ids.remove(index1);
		session_users.remove(index1);
		session_times.remove(index1);
		
		logger.info("Session deleting: success!");
		} catch (Exception e) {
			logger.info("logoutUser: removing exception!  " + e);
			e.printStackTrace();
		}
		
		return true;
	}
//---------------------------------------------------------------	
	public String newAccount(String name, String pass)
	{

		String res = null;
	    ResultSet results = null;
		try {
			results = readDB(name);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    if (results!=null) {		
	    	res = "This user already exists. Please select another user name!";
	    	logger.info(res);
	    	return res;
	    }
	    else {
	    	userKeyUnique = UUID.randomUUID().toString();
			//Adding new account to database example for java using jBCrypt: 
		    String hash = BCrypt.hashpw(pass, BCrypt.gensalt());
		    //(create new user entry in db storing ONLY username and hash, *NOT* the password).
	    	try {
				writeDB(userKeyUnique, name, hash);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	    	logger.info("newUserKey = " + userKeyUnique);
	    	res = "New account created for user: " + name; // + "; with password: "+ pass;
	    	logger.info(res);
	    	
	    }
		return res;
	}	
//---------------------------------------------------------------	
	private static void listPath(File path) {
		  int indentLevel = -1;
		  File files[]; 
		    indentLevel++; 
	        logger.info("List path: ");
	        files = path.listFiles();
		    Arrays.sort(files);
		    for (int i = 0, n = files.length; i < n; i++) {
		      for (int indent = 0; indent < indentLevel; indent++) {
		        System.out.print("  ");
		      }
		      logger.info(files[i].toString());
		      if (files[i].isDirectory()) {
		        listPath(files[i]);
		      }
		    }
		    indentLevel--; 
		  }
//---------------------------------------------------------------
	private static ResultSet readDB(String name) throws SQLException, ClassNotFoundException {
    
		 ResultSet rs = null;
		 ResultSet rs2 = null;
		 
	try {
         hsqlServer.start();
         Connection connection = null;
         try {
             //Getting a connection to the newly started database
             Class.forName("org.hsqldb.jdbcDriver");
             connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
        	 try{
             rs = connection.prepareStatement("select * from " + tableName + " where name = '" + name +"';").executeQuery();
             logger.info("selecting user from userDB: success!");
             
	 		} catch (Exception ex) {
	             logger.info(ex);
	             return null;
	 		}
             //Checking if the data is correct
             if(rs.next())
             {
            	logger.info("Id: " + rs.getString(1) + " Name: " + rs.getString(2)/* + " Pass: " + rs.getString(3)*/);
               
            	try{
            		rs2 = connection.prepareStatement("select * from " + tableName + ";").executeQuery();
            	} catch (Exception ex) {
            		logger.info(ex);
            		return null;
            	}
            	logger.info("List of registered users:");
            	while(rs2.next())
            	{
            		logger.info("Id: " + rs2.getString(1) + " Name: " + rs2.getString(2)/* + " Pass: " + rs.getString(3)*/);
            	};
             }
             	
             else
            	 {
            	 logger.info("Nothing was found in the table = " + tableName + " (name = " + name + ")");
            	 rs = null;
            	 }
         } finally {
             if (connection != null) {
                 connection.close();
             }
         }
     } finally {
         if (hsqlServer != null) {
             hsqlServer.stop();
         }
     }
	return rs;
	}
//---------------------------------------------------------------
	private static void writeDB(String sess_id, String name, String pass) throws SQLException, ClassNotFoundException {
		try {
	         hsqlServer.start();
	         Connection connection = null;
	         try {
	             Class.forName("org.hsqldb.jdbcDriver");
	             connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");

	             DatabaseMetaData md = connection.getMetaData();
	             String[] types = {"TABLE", "VIEW"};
	             ResultSet rs = md.getTables(null, null, null, types);
	             
	             Boolean exist = false;
	             if(rs.next()) {
				 logger.info("rs.getString(3) = " + rs.getString(3));
	             if(rs.getString(3).equals(tableName.toUpperCase())) {
	            	 logger.info("Table " + rs.getString(3) + " already exists!");
	            	 exist = true;
	            	 }
	             else
             		 logger.info("current table: " + rs.getString(3));
	            	while(rs.next()) {
	             	  logger.info("rs.getString(3) = " + rs.getString(3));
	             	  if(rs.getString(3).equals(tableName.toUpperCase())) {
	             		 logger.info("Table " + rs.getString(3) + " already exists!");
		            	 exist = true;
	            	 }
	             	  else
	              		 logger.info("current table: " + rs.getString(3));
	             }
	            	 
	             }
	             if(!exist) {
		             logger.info("creating table" + tableName);
	            	 logger.info("Table " + tableName + " is created!");
		             connection.prepareStatement("create table " + tableName + " (id VARCHAR(255), " +
		                 "name VARCHAR(255), pass VARCHAR(255));").execute();
	             }
	             logger.info("insert into " + tableName + "(id, name, pass) " +
	                 "values ('"+sess_id+"', '"+name+"', '"+pass+"');");
	             
	             try {
	             connection.prepareStatement("insert into " + tableName + "(id, name, pass) " +
	                 "values ('"+sess_id+"', '"+name+"', '"+pass+"');").execute();
	             logger.info("insert: success!");
	             
		 		} catch (Exception ex) {
		             logger.info(ex);
		 		}

	         } finally {
	             if (connection != null) {
	                 connection.close();
	             }
	         }
	     } finally {
	         if (hsqlServer != null) {
	             hsqlServer.stop();
	         }
	     }
		}
//---------------------------------------------------------------
	public ArrayList<String> getFileList(String sess_id, String selectedComponent) {
		ArrayList<String> ret = new ArrayList<String>();
		String path = null;
		
		String res = checkSession(sess_id);
		if(res!=null)
			return null;

		if (selectedComponent.equalsIgnoreCase("Folders")) {
			path = "/opt/optimis/etc/";
		} else {
			path = "/opt/optimis/etc/" + selectedComponent; 
		}

		File dir = new File(path);
		File[] files = dir.listFiles();
    for(File file : files) {
        ret.add(file.getName());
    }
    return ret;
}
//---------------------------------------------------------------
	public String getFile(String sess_id, String selectedComponent, String file) {
		String ret = null;
		String path = null;

		String res = checkSession(sess_id);
		if(res!=null)
			return null;
    
		path = "/opt/optimis/etc/" + selectedComponent;
		try {
			ret = readFileTail(path + "/" + file, 1000);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(ServiceManagerWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			ret = "Error while reading file";
		}
		return ret;
	}
//---------------------------------------------------------------
	private String readFileTail(String file, int lines) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		List<String> read = new ArrayList<String>();

		while ((line = reader.readLine()) != null) {
			read.add(line);
		}

		if (lines == 0) {
			read = read.subList(0, read.size());
		} else {
			if(read.size() <= lines) {
				read = read.subList(0, read.size());
			} else {
				read = read.subList(read.size() - lines, read.size());
			}
		}

		String ret = new String();
		for (String lineRead : read) {
			ret = ret.concat(lineRead + "\n");
		}
		reader.close();
		return ret;
	}
//---------------------------------------------------------------
	private ArrayList<String> getFileListLogs(String selectedComponent) {
		ArrayList<String> ret = new ArrayList<String>();
		String path = null;

		if (selectedComponent.equalsIgnoreCase("Folders")) {
			path = "/opt/optimis/var/log/";
		} else {
			path = "/opt/optimis/var/log/" + selectedComponent;
		}
    
		File dir = new File(path);
		File[] files = dir.listFiles();

		for(File file : files) {
			ret.add(file.getName());
		}
		return ret;
	}
//---------------------------------------------------------------
	@Override
	public ArrayList<String> getComponentLogList(String sess_id) {
		ArrayList<String> ret = new ArrayList<String>();
		String res = checkSession(sess_id);
		if(res!=null)
			return null;

		File configDir = new File(Constants.COMPONENT_LOGGING_FOLDER);
		File[] folders = configDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				logger.debug(dir.getAbsolutePath().concat("/").concat(name));
				return (new File(dir.getAbsolutePath().concat("/").concat(name))).isDirectory();
			}
		});
		for (File file : folders) {
			ret.add(file.getName());
		}
		return ret;
	}
//---------------------------------------------------------------
	@Override
	public ArrayList<String> getLogList(String sess_id, String selectedComponent) {
		ArrayList<String> ret = new ArrayList<String>();
    
		String res = checkSession(sess_id);
		if(res!=null)
			return null;

		File dir = new File(Constants.COMPONENT_LOGGING_FOLDER + "/" + selectedComponent);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File file = new File(dir.getAbsolutePath() + "/" + name);
				return !file.isDirectory();
			}
		});
		for (File file : files) {
			ret.add(file.getName());
		}
		return ret;
	}
//---------------------------------------------------------------
	@Override
	public String getLog(String sess_id, String selectedComponent, String file, int lines) {
		String ret = null;
    
		String res = checkSession(sess_id);
		if(res!=null)
			return null;

		try {
			ret = readFileTail(Constants.COMPONENT_LOGGING_FOLDER + "/" + selectedComponent + "/" + file, lines);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(ServiceManagerWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			ret = "Error while reading file";
		}
		return ret;
	}
//---------------------------------------------------------------
	private String getFileLogs(String selectedComponent, String file) {
		String ret = null;
		String path = null;
    
		path = "/opt/optimis/var/log/" + selectedComponent; 
		try {
			ret = readFileTail(path + "/" + file, 1000);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(ServiceManagerWebServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			ret = "Error while reading file";
		}
		return ret;
	}
//---------------------------------------------------------------
	public static void Send(final String username, final String password, String recipientEmail, String ccEmail,
			String title, String message) throws AddressException, MessagingException {
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		Properties props = System.getProperties();
		props.setProperty("mail.smtps.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtps.auth", "true");

		props.put("mail.smtps.quitwait", "false");

		Session session = Session.getInstance(props, null);
		final MimeMessage msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(username + "@gmail.com"));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

		if (ccEmail.length() > 0) {
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
		}

		msg.setSubject(title);
		msg.setText(message, "utf-8");
		msg.setSentDate(new Date());

		SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
		t.connect("smtp.gmail.com", username, password);
		t.sendMessage(msg, msg.getAllRecipients());      
		t.close();
	}
}
