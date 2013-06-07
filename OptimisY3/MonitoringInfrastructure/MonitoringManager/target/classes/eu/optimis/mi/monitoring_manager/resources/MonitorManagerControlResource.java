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

package eu.optimis.mi.monitoring_manager.resources;

import java.net.URI;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * OPTIMIS basic Toolkit - Monitoring Infrastructure - Monitoring Manager
 * Monitoring Manager provides the functionalities to gather all monitoring data
 * from the different monitoring connectors
 * 
 * @author Tinghe Wang (twang@hlrs.de)
 * 
 */

@Path("/control")
public class MonitorManagerControlResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private final static Logger logger = Logger
			.getLogger(MonitorManagerControlResource.class.getName());

	@POST
	@Path("startmonitoring")
	@Produces(MediaType.TEXT_PLAIN)
	public String startAllMonitors() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String result = null;
		try {
			result = service.path("Aggregator/startmonitoring").accept(
					MediaType.TEXT_PLAIN).post(String.class, null);
		} catch (UniformInterfaceException interfaceException) {
			logger.error("ERROR: by calling Aggregator, errorcode:"
					+ interfaceException.getResponse().getStatus());
			logger.error("interfaceException", interfaceException);
		}

		return "monitoring started";
	}

	@POST
	@Path("stopmonitoring")
	@Produces(MediaType.TEXT_PLAIN)
	public String stopAllMonitors() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		String result = null;
		try {
			result = service.path("Aggregator/stopmonitoring").accept(
					MediaType.TEXT_PLAIN).post(String.class, null);
		} catch (UniformInterfaceException interfaceException) {
			logger.error("ERROR: by calling Aggregator, errorcode:"
					+ interfaceException.getResponse().getStatus());
			logger.error("interfaceException", interfaceException);
		}

		return "monitoring stopped";
	}

	private static URI getBaseURI() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("mmanager");
			String aggregatorUrl = rb.getString("aggregator.url");
			return UriBuilder.fromUri(aggregatorUrl).build();
		} catch (MissingResourceException e) {
			logger.error("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}
}
