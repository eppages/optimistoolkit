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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
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
    	String xml = xmlConstruction(dataSet);
    	return pushstrReport(xml);
    }
    public boolean pushstrReport(String dataSet) {
    	WebResource service = client.resource(this.getAddress());
    	ClientResponse response = service.type("text/plain").post(ClientResponse.class, dataSet);
        return response.getClientResponseStatus().getFamily().equals(Response.Status.Family.SUCCESSFUL);
    }
    
    private String xmlConstruction(MonitoringResourceDatasets dsets) {
		String bg = "<?xml version=\"1.0\" encoding=\"utf-8\"?><MonitoringResources>";
		String ed = "</MonitoringResources>";
		StringBuffer ct = new StringBuffer();
		if (dsets.getMonitoring_resource() != null) {
			for (MonitoringResourceDataset a : dsets.getMonitoring_resource()) {
				ct.append("<monitoring_resource>");
				if (a.getPhysical_resource_id() == null)
					ct.append("<physical_resource_id></physical_resource_id>");
				else {
				ct.append("<physical_resource_id>")
						.append(a.getPhysical_resource_id())
						.append("</physical_resource_id>");
				}
				
				if (a.getVirtual_resource_id() == null)
					ct.append("<virtual_resource_id></virtual_resource_id>");
				else {
				ct.append("<virtual_resource_id>")
						.append(a.getVirtual_resource_id())
						.append("</virtual_resource_id>");
				}
				if (a.getService_resource_id() == null)
					ct.append("<service_resource_id></service_resource_id>");
				else
					ct.append("<service_resource_id>")
							.append(a.getService_resource_id())
							.append("</service_resource_id>");
				ct.append("<metric_name>").append(a.getMetric_name())
						.append("</metric_name>");
				ct.append("<metric_unit>").append(a.getMetric_unit())
						.append("</metric_unit>");
				ct.append("<metric_value>").append(a.getMetric_value())
						.append("</metric_value>");
				java.util.Date date = a.getMetric_timestamp();
				long unixtp = date.getTime() / 1000;
				ct.append("<metric_timestamp>").append(unixtp)
						.append("</metric_timestamp>");
				ct.append("<resource_type>").append(a.getResource_type())
						.append("</resource_type>");
				if (a.getMonitoring_information_collector_id() == null
						|| a.getMonitoring_information_collector_id().length() == 0)
					ct.append("<monitoring_information_collector_id>Unknown</monitoring_information_collector_id>");
				else
					ct.append("<monitoring_information_collector_id>")
							.append(a.getMonitoring_information_collector_id())
							.append("</monitoring_information_collector_id>");
				ct.append("</monitoring_resource>");
			}
		}
		return bg + ct.toString() + ed;
	}
    
}
