package elasticityRestinterface;
/**
* 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;


import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.thoughtworks.xstream.XStream;


public class RestInterface {
	protected final static Logger log = Logger.getLogger(RestInterface.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 4444;
    private final static String PATH = "/ElasticityEngine";
    
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public RestInterface() {
        this(HOST, PORT, PATH);
    }

    public RestInterface(String host) {
        this(host, PORT, PATH);
    }

    public RestInterface(String host, int port) {
        this(host, port, PATH);
    }

    public RestInterface(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
        log.info("New EE REST started ");

        }
    
    private String getAddress() throws URISyntaxException {
        //return "http://"+host+":"+port+"/"+path
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }
    
    public boolean startElasticity (String serviceID, String serviceManifest,boolean LowRiskMode, 
    								String spAddress) {
        try {
        	String url = this.getAddress();
    	log.info("Calling start Elastcity via url: " + url+serviceID);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path(serviceID);
    		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
//    		XStream xstream = new XStream();
    		formData.add("serviceId", serviceID);
    		formData.add("Manifest", serviceManifest);
    		formData.add("Mode", String.valueOf(LowRiskMode));
    		formData.add("SPAdd",spAddress);
    		
    		ClientResponse response = webResource.put(ClientResponse.class, formData);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			if (r.equalsIgnoreCase("true"))
    				return true;
    			else
    				return false;
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return false;
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean stopElasticity (String serviceID) {
        try {
        	String url = this.getAddress();
    		log.info("Calling stop Elasticity via url: " + url+serviceID);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path(serviceID);
    		ClientResponse response = webResource.delete(ClientResponse.class);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			if (r.equalsIgnoreCase("true"))
    				return true;
    			else
    				return false;
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return false;
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateElasticityRules (String serviceID, String serviceManifest) {
        try {
        	String url = this.getAddress();
    		log.info("Calling update Elasticity rules via url: " + url+serviceID);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path(serviceID);
    		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
    		formData.add("Manifest", serviceManifest);
    		ClientResponse response = webResource.post(ClientResponse.class, formData);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			if (r.equalsIgnoreCase("true"))
    				return true;
    			else
    				return false;
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return false;
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPrediction (String serviceID, String imageID,int timeSpanInMinutes) {
        try {
        	String url = this.getAddress();
    		log.info("Calling get prediction via url: " + url+serviceID);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path(serviceID).path("prediction");
    		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
    		formData.add("imageID", imageID);
    		formData.add("time", String.valueOf(timeSpanInMinutes));
    		ClientResponse response = webResource.put(ClientResponse.class, formData);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			return response.getEntity(String.class);
    			
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return "false";
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "false";
    }

    public boolean setMode (String serviceID, boolean proactive) {
        try {
        	String url = this.getAddress();
    		log.info("Calling set mode via url: " + url+"Mode"+serviceID);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path("Mode").path(serviceID).queryParam("Mode", Boolean.toString(proactive));
    		ClientResponse response = webResource.get(ClientResponse.class);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			if (r.equalsIgnoreCase("true"))
    				return true;
    			else
    				return false;
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return false;
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getHtml () {
        try {
        	String url = this.getAddress();
    		log.info("Calling get Html via url: " + url);
    		
    		Client client = Client.create();
    		WebResource webResource = client.resource(url).path("getHtml");
//    		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
//    		formData.add("Mode", String.valueOf(proactive));
    		ClientResponse response = webResource.get(ClientResponse.class);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		if (success)
    		{
    			String r = response.getEntity(String.class);
    			log.debug("Response : " + r);
    			if (r.equalsIgnoreCase("true"))
    				return response.toString();
    			else
    				 return response.toString();
    		}
    		else
    		{
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			log.error(error);
    			return response.toString();
    		
    		}
    	}
     catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "Hi";
    }


}
