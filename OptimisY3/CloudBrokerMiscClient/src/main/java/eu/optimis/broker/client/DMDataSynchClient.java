package eu.optimis.broker.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;



public class DMDataSynchClient {

	Client client;
	String host;
	String port;
	
	public DMDataSynchClient(String host, String port) {
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
		    InputStream is = getClass().getClassLoader().getResourceAsStream("BrokerMiscClientProperties");
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

	
	public String isReady2CreateAgreeement(String serviceid){
		String path = getPath("eu.optimis.broker");
		//System.out.println("isReady2CreateAgreeement :" + path);
		WebResource service = client.resource(UriBuilder.fromUri(path).build());

		String resp = service.path("dmsynch").path("isReady2CreateAgreeement").path(serviceid).accept(MediaType.TEXT_PLAIN).get(String.class);
		
		System.out.println(resp);
		return resp;
	}
	

	public ClientResponse dataUploadComplete(String serviceid){
		String path = getPath("eu.optimis.broker");
		//System.out.println("dataUploadComplete :" + path);
		WebResource service = client.resource(UriBuilder.fromUri(path).build());

		ClientResponse response = service.path("dmsynch").path("dataUploadComplete").accept(MediaType.TEXT_PLAIN).post(ClientResponse.class,serviceid);
	
		//System.out.println(response.getStatus());
		return response;
	}


	public ClientResponse Ready2CreateAgreeement(String serviceid, String datapercent){
		String path = getPath("eu.optimis.broker");
		//System.out.println("Ready2CreateAgreeement :" + path);
		WebResource service = client.resource(UriBuilder.fromUri(path).build());

		ClientResponse response = service.path("dmsynch").path("Ready2CreateAgreeement").path(serviceid).path(datapercent).accept(MediaType.TEXT_PLAIN).post(ClientResponse.class);
	
		//System.out.println(response.getStatus());
		return response;
	}

	
	
}
