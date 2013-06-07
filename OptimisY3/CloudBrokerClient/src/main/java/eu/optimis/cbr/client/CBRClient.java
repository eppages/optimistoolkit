package eu.optimis.cbr.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;

import eu.optimis.cbr.client.utils.IPInfo;
import eu.optimis.cbr.client.utils.IPInfoList;
import eu.optimis.cbr.client.utils.ServiceRequest;

/**
 * @author Pramod Pawar
 */



public class CBRClient {
	Client client;
	String host;
	String port;
	
	public CBRClient(String host, String port) {
		this.client = setClient();
		this.host = host;
		this.port = port;
	}

	private Client setClient() {
		ClientConfig config = new DefaultClientConfig();
		return Client.create(config);
	}
	
	public String getPath(String param){
		String path=null;
		String fullpath=null;
	    Properties properties = new Properties();
		try {
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerClientProperties");
		    properties.load(is);
		    //properties.load(new FileInputStream("src/main/resources/BrokerClientProperties"));
		    path = properties.getProperty(param);
		    fullpath="http://"+ this.host + ":" + this.port +"/"+path;
			System.out.println("FullPATH :" + fullpath);
		    
		} catch (IOException e) {
			System.out.println("File Read Exception");
		}
		return fullpath;
	}
	
	
	public ClientResponse getDeploymentDetails(String manifest, String objv){
		String path = getPath("eu.optimis.cbr");
		System.out.println("GetDeploymentDetails :" + path);
		WebResource service = client.resource(UriBuilder.fromUri(path).build());

		//System.out.println("Manifest VM serviceID : " + manifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM Affinity Rule : " + manifest.getVirtualMachineDescriptionSection().getAffinityRule(0).toString());
		//System.out.println("Manifest : " + manifest);

		ServiceRequest sr = new ServiceRequest(manifest, objv);
		//System.out.println("Service req:"+ sr.getManifest()+ " Srvice req obj :" + sr.getObjective());
		ClientResponse response = service.path("getDeployment").path("getdetails").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, sr);
		// Return code should be 201 == created resource
		//System.out.println(response.getStatus());
		return response;
	}
	
	
	/// IPRegistry interface invocation
	
//	public ClientResponse registerIP(IPInfo ip){
		public ClientResponse registerIP(Provider ip){
		String path = getPath("eu.optimis.cbr");
		WebResource service = client.resource(UriBuilder.fromUri(path).build());
		ClientResponse response = service.path("getDeployment").path("registerip").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, ip);
		// Return code should be 201 == created resource
		//System.out.println(response.getStatus());
		return response;
	}

	public IPInfoList  getAllIP(){
		String path = getPath("eu.optimis.cbr");
		WebResource service = client.resource(UriBuilder.fromUri(path).build());
		IPInfoList iplist = service.path("getDeployment").path("getallip").accept(MediaType.APPLICATION_XML).get(IPInfoList.class );
		// Return code should be 201 == created resource
		//System.out.println(response.getStatus());
		return iplist;
	}
		
}
