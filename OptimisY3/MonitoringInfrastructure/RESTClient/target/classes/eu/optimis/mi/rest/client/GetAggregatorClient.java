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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class GetAggregatorClient {

	private static String HOST = "localhost";
	private static int PORT = 8080;
	private static String PATH = "Aggregator/Aggregator";

	private final String INTERFACE_1 = "realtime";
	private Client client;

	private static void init() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("services");

			HOST = rb.getString("service.aggregator_host");
			PORT = Integer.parseInt(rb.getString("service.aggregator_port"));
			PATH = rb.getString("service.aggregator_path");

		} catch (MissingResourceException e) {
			System.err.println("Error: cannot find the property file.");
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("static-access")
	public GetAggregatorClient() {
		this.init();
		this.client = Client.create();
	}

	public GetAggregatorClient(String host, int port, String path) {
		HOST = host;
		PORT = port;
		PATH = path;
		this.client = Client.create();
	}

	private String getAddress(String interface_option) {
		return "http://" + HOST + ":" + PORT + "/" + PATH + "/"
				+ interface_option;
	}
	
	public MonitoringResourceDatasets getCurrentReportForPhysical() {
		WebResource service = client.resource(this.getAddress(INTERFACE_1))
		.path("physical");
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getCurrentReportForVirtual() {
		WebResource service = client.resource(this.getAddress(INTERFACE_1))
		.path("virtual");
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getCurrentReportForService() {
		WebResource service = client.resource(this.getAddress(INTERFACE_1))
		.path("service");
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getCurrentReportForEnergy() {
		WebResource service = client.resource(this.getAddress(INTERFACE_1))
		.path("energy");
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
}
