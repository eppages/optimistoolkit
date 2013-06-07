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

package eu.optimis.mi.aggregator.resources;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.aggregator.util.ConfigManager;
import eu.optimis.mi.aggregator.util.XmlUtil;

/**
 * OPTIMIS Base Toolkit - Monitoring Infrastructure - Aggregator Aggregator
 * provides the functionalities to gather all monitoring data from the different
 * monitoring collectors and store it into the Monitoring database.
 * 
 * @author Tinghe Wang (twang@hlrs.de)
 * 
 */

@Path("/Aggregator")
public class AggregatorResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private final static Logger logger = Logger
			.getLogger(AggregatorResource.class.getName());

	private static MonitoringResourceDatasets currentPhysicalRes;
	private static MonitoringResourceDatasets currentVirtualRes;
	private static MonitoringResourceDatasets currentEnergyRes;
	private static MonitoringResourceDatasets currentServiceRes;

	private static String physicalPath;
	private static String virtualPath;
	private static String energyPath;
	private static String servicePath;

	public AggregatorResource() {
		try {
			PropertyConfigurator.configure(ConfigManager
					.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));

			PropertiesConfiguration configAgt = ConfigManager
					.getPropertiesConfiguration(ConfigManager.AGGREGATOR_CONFIG_FILE);
			servicePath = configAgt.getString("resource.service.path");
			physicalPath = configAgt.getString("resource.physical.path");
			virtualPath = configAgt.getString("resource.virtual.path");
			energyPath = configAgt.getString("resource.energy.path");
		} catch (IOException e) {
			logger.error("couldn't find the configuration file");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ConfigurationException e1) {
			logger.error("couldn't find the properties defined in the configuration file");
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testAggregator() {
		return "Aggregator is running, welcome!\n";
	}

	@GET
	@Path("realtime/physical")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getCurrentPhysicalResource() {
		if (currentPhysicalRes == null)
			return new MonitoringResourceDatasets();
		else
			return currentPhysicalRes;
	}

	@GET
	@Path("realtime/virtual")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getCurrentVirtualResource() {
		if (currentVirtualRes == null)
			return new MonitoringResourceDatasets();
		else
			return currentVirtualRes;
	}

	@GET
	@Path("realtime/energy")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getCurrentEnergyResource() {
		if (currentEnergyRes == null)
			return new MonitoringResourceDatasets();
		else
			return currentEnergyRes;
	}

	@GET
	@Path("realtime/service")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getCurrentServiceResource() {
		if (currentServiceRes == null)
			return new MonitoringResourceDatasets();
		else
			return currentServiceRes;
	}

	// Y3 push solution
	@Path("/monitoringresources/{resourceType}")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response pushmonitoringResStr(String xml,
			@PathParam("resourceType") String resourceType) throws IOException {
		// Build Filename
		Date date = new Date();
		long current = (long) Math.floor(date.getTime() / 1000);
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(10000);
		String fileName = Long.toString(current) + "-"
				+ Integer.toString(randomInt) + ".xml";
		String filePath = null;
		if (resourceType.equals("physical")) {
			filePath = physicalPath;
		} else if (resourceType.equals("virtual"))
			filePath = virtualPath;
		else if (resourceType.equals("energy"))
			filePath = energyPath;
		else if (resourceType.equals("service"))
			filePath = servicePath;
		else
			throw new IOException();

		String filePathName = filePath + fileName;
		org.apache.commons.io.FileUtils.writeStringToFile(
				new File(filePathName), xml);
		return Response.ok().build();
	}

	@Path("/realtime/monitoringresources/{resourceType}")
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response setRealtime(String xml,
			@PathParam("resourceType") String type) {
		String upperType = type.toUpperCase();
		// xml input (unixtimestamp) -> needed xml input (data type string)
		XmlUtil util = new XmlUtil();
		List<MonitoringResourceDataset> mlist = util.getMRDObjY3(xml);
		if (mlist != null) {
			if (mlist.size() < 1) {
				logger.warn("Aggregator Post a empty realtime resource:" + xml
						+ " ; resource type is " + type);
			}
			MonitoringResourceDatasets mds = new MonitoringResourceDatasets();
			mds.setMonitoring_resource(mlist);
			if (upperType.contains("SERVICE")) {
				currentServiceRes = null;
				currentServiceRes = mds;
			} else if (upperType.contains("PHYSICAL")) {
				currentPhysicalRes = null;
				currentPhysicalRes = mds;
			} else if (upperType.contains("VIRTUAL")) {
				currentVirtualRes = null;
				currentVirtualRes = mds;
			} else if (upperType.contains("ENERGY")) {
				currentEnergyRes = null;
				currentEnergyRes = mds;
			}

		} else
			logger.error("Aggregator Post a invalid realtime resource:" + xml
					+ " ; resource type is " + type);
		return Response.ok().build();
	}
}
