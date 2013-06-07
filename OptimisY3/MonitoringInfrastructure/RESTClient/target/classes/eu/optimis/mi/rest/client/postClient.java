/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.rest.client;

import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class postClient {

    private static String HOST = "localhost";
    private static int PORT = 8080;
    private static String PATH = "Aggregator/Aggregator/monitoringresources";

   private Client client;

    private static void init() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("services");
			
				HOST = rb.getString("service.aggregator_host");
				PORT = Integer.parseInt(rb.getString("service.aggregator_port"));
				PATH = rb.getString("service.aggregator_path");
			
		} catch (MissingResourceException e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}
    
	@SuppressWarnings("static-access")
	public postClient() {
		this.init();
		this.client = Client.create();	
	}

	public postClient(String host, int port, String path) {
        HOST = host;
		PORT = port;
		PATH = path;
        this.client = Client.create();
	}

	private String getAddress() {
        return "http://" + HOST + ":" + PORT + "/" + PATH;
	}

    public boolean pushReport(MonitoringResourceDatasets dataSet) {
    	WebResource service = client.resource(this.getAddress());
    	ClientResponse response = service.type("application/xml").post(ClientResponse.class, dataSet);
        return response.getClientResponseStatus().getFamily().equals(Response.Status.Family.SUCCESSFUL);
    }
}
