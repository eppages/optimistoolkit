package eu.optimis.cbr.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apache.xmlbeans.XmlException;

import eu.optimis._do.schemas.Objective;
/*
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.IllegalCallParameter;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;
*/
//import eu.optimis.cbr.rest.utils.DBConnection;
import eu.optimis.cbr.rest.utils.DBConnection;
import eu.optimis.cbr.rest.utils.IPInfo;
import eu.optimis.cbr.rest.utils.IPInfoList;
import eu.optimis.cbr.rest.utils.IPRegistry;
import eu.optimis.cbr.rest.utils.MyServletContext;
import eu.optimis.cbr.rest.utils.ServiceRequest;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import eu.optimis.cbr.rest.utils.CloudBrokerMain;

/**
 * @author Pramod Pawar
 */



//Sets the path to base URL + /getDeployment
@Path("/getDeployment")
public class CloudBrokerAPI {
	
//	private static final String IP_REGISTRY_XML="./src/main/resources/IP-Store.xml";
	//private static final IPRegistry ipreg = new IPRegistry(new ArrayList<IPInfo>());
	
	
	@POST 
//	@Consumes(MediaType.APPLICATION_XML)
//	@Produces ("text/html")
	@Path("/details")
	public String getDeploymentDetails(String str){
		System.out.println("Inside GetDeploymentDetails: " + str);   
	
		return "GetDeploymentDetails : "+str;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/getdetails")
	public Response getDeploymentDetails(JAXBElement<ServiceRequest> servicereq) {
		System.out.println("Within GetDeploymentDetails");
		
		ServiceRequest sr = servicereq.getValue();
		//System.out.println("Service Request at Server --> Manifest String :" + sr.getManifest() + " Objective" + sr.getObjective());
		return putAndGetResponse(sr);
	}
	
	private Response putAndGetResponse(ServiceRequest serreq) {
		Response res;
		//System.out.println("Service Request Object Received");
		///////////////////////////Reconstruct Manifest //////////////////////////////
		
		
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
			parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(serreq.getManifest());
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		Manifest spManifest= Manifest.Factory.newInstance(parsedManifest);
		//System.out.println("Manifest VM serviceID : " + spManifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM Affinity Rule : " + spManifest.getVirtualMachineDescriptionSection().getAffinityRule(0).toString());
		//System.out.println("Manifest : " + manifest);
		
		//////////////////////////////////////////////////////////////////////////////
		
		//////////// Communication with CloudBrokerMain /////////////

		String serID = spManifest.getVirtualMachineDescriptionSection().getServiceId();
	
		DBConnection dbcon = new DBConnection(this.getPath("sp.trec.db.url"), 
				this.getPath("sp.trec.db.username"), 
				this.getPath("sp.trec.db.password"));
	
		boolean dbupdatetrec = dbcon.updateSPTRECdb(serID, serreq.getManifest(), true, "Not used", 0);
		if(dbupdatetrec)
			System.out.println("updateSPTRECdb successfully");	
		
		
		Thread cloudBrokerMainThread = new Thread(new CloudBrokerMain(serreq));
		
		
		cloudBrokerMainThread.start();
		
		/////////////////////////////////////////////////////////////
		
		res = Response.ok().build();
		
		return res;
	}

	
	/// IP Registry interfaces///////
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/registerip")
//	public Response registerIP(JAXBElement<IPInfo> ipinfo) throws Exception {
	public Response registerIP(JAXBElement<Provider> ipinfo) throws Exception {
		//IPInfo ip = ipinfo.getValue();
		Provider ip = ipinfo.getValue();
		//System.out.println("IPUUID" + ip.getUUID() + "IPName" + ip.getIPName() + "IPAddress "+ ip.getIPAddress() +"IPType" +ip.getIPType());
		IPRegistry ipreg = IPRegistry.getSingletonObject();
		
		ipreg.getIPInfoList().getIPList().add(ip);
	
		storeIP();
		Response res = Response.ok().build();
		return res;
	}
	
	private void storeIP() throws Exception{
		IPRegistry ipreg = IPRegistry.getSingletonObject();
		JAXBContext context = JAXBContext.newInstance(IPInfoList.class);

		Marshaller m = context.createMarshaller();
	
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		//m.marshal(ipreg, System.out);
		
		Writer w = null;
		try{
			

			String path = getPath("filepath");
			//System.out.println("Path :"+path );
			//w= new FileWriter("C:/documents and settings/605474046/workspace/CloudBrokerREST/src/main/resources/IP-Store.xml");
			
			ServletContext ctext =  MyServletContext.getContext();
			System.out.println("RealPath :" + ctext.getRealPath(path));
			
			String realPath = ctext.getRealPath(path);
			
			w= new FileWriter(realPath);
			
			
			
			m.marshal(ipreg.getIPInfoList(), w);

		}finally{
			try{
				w.close();
			}catch (Exception e){
			}
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getallip")
	public IPInfoList getAllIP() throws Exception {
		//JAXBContext context = JAXBContext.newInstance(IPInfoList.class);
		//System.out.println("Output from File ");
		//Unmarshaller um = context.createUnmarshaller();
		//IPInfoList ipinfo = (IPInfoList)um.unmarshal(new FileReader("/src/main/resources/IP-Store.xml"));
	    //IPInfoList iplist = ipreg.getIPInfoList();
		//System.out.println(ipreg.getIPInfoList().getIPList().toArray().length);
		
		
		IPRegistry ipreg = IPRegistry.getSingletonObject();		
		IPInfoList ipinfo = new IPInfoList();
		ipinfo.setIPList(ipreg.getIPInfoList().getIPList());
	
		
		
		for(int i=0; i<ipinfo.getIPList().toArray().length ; i++){
			//System.out.println("IP" + (i+1) +":" + ipinfo.getIPList().get(i).getUUID());
			//System.out.println("IP" + (i+1) +":" + ipinfo.getIPList().get(i).getName());
		}
		return ipinfo;
	}
	
	@GET
	//@Produces(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_HTML)
	@Path("/getallipHTML")
	//public IPInfoList getAllIP() throws Exception {
	public Response getAllIPHTML() throws Exception {
		//Response res = new Response;
		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		String  outputHTML= "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
				"<html>\n" +
                "<head><title>IPs in the Cloud Broker Registry</title></head>\n" +
                "<body>\n <h1>IP Registry</h1>" +
                "<table border=\"1\"><tr><th>Serial No.</th><th>IP Name</th><th>CloudQoS Endpoint</th></tr>";
		//JAXBContext context = JAXBContext.newInstance(IPInfoList.class);
		//System.out.println("Output from File ");
		//Unmarshaller um = context.createUnmarshaller();
		//IPInfoList ipinfo = (IPInfoList)um.unmarshal(new FileReader("/src/main/resources/IP-Store.xml"));
	    //IPInfoList iplist = ipreg.getIPInfoList();
		//System.out.println(ipreg.getIPInfoList().getIPList().toArray().length);
		
		
		IPRegistry ipreg = IPRegistry.getSingletonObject();
		IPInfoList ipinfo = new IPInfoList();
		ipinfo.setIPList(ipreg.getIPInfoList().getIPList());
		List<Provider> availableIPList = ipreg.getIPInfoList().getIPList();

		for(int i=0; i<ipinfo.getIPList().toArray().length ; i++){
			outputHTML += "<tr><td>"+ (i+1) + "</td><td>" + availableIPList.get(i).getName() + "</td><td>"+ availableIPList.get(i).getCloudQosUrl() +"</td></tr>\n";
			//System.out.println("IP" + (i+1) +":" + ipinfo.getIPList().get(i).getUUID());
			//System.out.println("IP" + (i+1) +":" + ipinfo.getIPList().get(i).getName());
		}
		//return ipinfo;
		outputHTML += "</table></body></html>\n";
		//out.println(outputHTML);
		Response res = Response.status(200).entity(outputHTML).build();
		return res;
		//return outputHTML;
	}

	
	
	public String getPath(String param){
		String path=null;
		
	    Properties properties = new Properties();
		try {
//		    properties.load(new FileInputStream("C:/documents and settings/605474046/workspace/CloudBrokerREST/src/main/resources/BrokerServerProperties"));
//		    properties.load(new FileInputStream("src/main/resources/BrokerServerProperties"));
		    //properties.load(new FileInputStream("BrokerServerProperties"));

		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerServerProperties");
		    properties.load(is); 
		    
		    path = properties.getProperty(param);
		    System.out.println("CloudBroker PATH :" + path);
		} catch (IOException e) {
			System.out.println("File Read Exception");
		}
		return path;
	}
	
	


	
}
