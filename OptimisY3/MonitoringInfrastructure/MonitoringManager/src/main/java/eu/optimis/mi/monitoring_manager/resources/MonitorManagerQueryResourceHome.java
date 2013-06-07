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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.mi.monitoring_manager.db.Connection;
import eu.optimis.mi.monitoring_manager.db.ConnectionPool;
import eu.optimis.mi.monitoring_manager.queries.InfrastructureProviderQuery;
import eu.optimis.mi.monitoring_manager.util.ConfigManager;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

/**
 * OPTIMIS Base Toolkit - Monitoring Infrastructure - Monitoring Manager
 * Monitoring Manager provides functionalities to gather monitoring data from
 * different monitoring connectors.
 * 
 * MonitoringManagerQueryResourceHome makes it possible to access the monitoring
 * data from multiple IPs related to a service.
 * 
 * @author Tinghe Wang (twang@hlrs.de)
 * @author Pierre Gilet (gilet@hlrs.de)
 * 
 */
@Path("/QueryResourcesHome")
public class MonitorManagerQueryResourceHome {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private static HashMap<String, String> interfaceMap;

	private final static Logger logger = Logger
			.getLogger(MonitorManagerQueryResourceHome.class.getName());
	public final static String PATH = "MonitoringManager/QueryResources";
	public final static String PORT = "8080";

	private static String DB_TABLE_URL;
	private static String DB_DRIVER;
	private static String DB_USERNAME;
	private static String DB_PASSWORD;

	public MonitorManagerQueryResourceHome() {
		interfaceMap = new HashMap<String, String>();
		// [6] a, b, c
		interfaceMap.put("getReportForAllVirtual", "/type/virtual");
		interfaceMap.put("getReportForAllPhysical", "/type/physical");
		interfaceMap.put("getReportForAllPhysical", "/type/energy");
		// [5][8] a, b, c, d
		interfaceMap.put("getReportForService", "/type/service");
		interfaceMap.put("getReportForVirtual", "/type/virtual");
		interfaceMap.put("getReportForPhysical", "/type/physical");
		interfaceMap.put("getReportForEnergy", "/type/energy");
		interfaceMap.put("getLatestReportForVirtual", "/group/type/virtual");
		interfaceMap.put("getLatestReportForService", "/group/type/service");
		interfaceMap.put("getLatestReportForPhysical", "/group/type/physical");
		interfaceMap.put("getLatestReportForEnergy", "/group/type/energy");
		// [10] a, b, c, d
		interfaceMap.put("getReportForPartService", "/date/type/service");
		interfaceMap.put("getReportForPartVirtual", "/date/type/virtual");
		interfaceMap.put("getReportForPartPhysical", "/date/type/physical");
		interfaceMap.put("getReportForPartEnergy", "/date/type/energy");
		// [11] a, b, c, d
		interfaceMap.put("getReportForPartServiceId", "/date/type/service");
		interfaceMap.put("getReportForPartVirtualId", "/date/type/virtual");
		interfaceMap.put("getReportForPartPhysicalId", "/date/type/physical");
		interfaceMap.put("getReportForPartEnergyId", "/date/type/energy");
		// [7]
		interfaceMap.put("getReportForMetric", "/type/metric");
		// [12]
		interfaceMap.put("getReportForPartMetricName", "/date/metric");
		// [9]
		interfaceMap.put("getLatestReportForMetricName", "/group/metric");

		// [13] [14] [15] [16]
		interfaceMap.put("getLatestCompleteReportForService",
				"/group/complete/service");
		interfaceMap.put("getLatestCompleteReportForVirtual",
				"/group/complete/virtual");
		interfaceMap.put("getLatestCompleteReportForPhysical",
				"/group/complete/physical");
		interfaceMap.put("getLatestCompleteReportForEnergy",
				"/group/complete/energy");

		logger.info("Service " + this.getClass().getName());
		// PatternLayout layout = new
		// PatternLayout("%d{ISO8601} %-5p %c{2} [%t,%M:%L] %m%n");
		try {
			PropertyConfigurator.configure(ConfigManager
					.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
			logger.info("Monitoring Manager is ready");
			PropertiesConfiguration config = ConfigManager
					.getPropertiesConfiguration(ConfigManager.MMANAGER_CONFIG_FILE);
			DB_TABLE_URL = config.getString("db.table.url");
			DB_DRIVER = config.getString("db.driver");
			DB_USERNAME = config.getString("db.username");
			DB_PASSWORD = config.getString("db.password");
		} catch (IOException e) {
			logger.error("couldn't find the configuration file");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ConfigurationException e1) {
			logger
					.error("couldn't find the properties defined in the configuration file");
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
	}

	/**
	 * Run all the get* methods (with the exception of
	 * getLatestComplete*Resource methods) on multiple IPs.
	 * 
	 * @param serviceId
	 * @param methodName
	 * @param parameters
	 * @return MonitoringResourceDatasets
	 */
	@GET
	@Path("/MultipleIPs/{serviceId}/{methodName}/{parameters}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getFromMultipleIPs(
			@PathParam("serviceId") String serviceId,
			@PathParam("methodName") String methodName,
			@PathParam("parameters") String parameters) {

		CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();
		// Get list of VMs.
		List<String> vmsList = co.getVMsIdsOfService(serviceId);
		// Get list of physical node ids and list of IP addresses needed to call
		// the endpoints.
		HashMap<String, List<String>> map = getMultipleIpMap(vmsList);
		List<String> ipAddresses = map.get("ipAddresses");
		List<String> physicalIds = map.get("nodes");

		// Debug
		printList(vmsList, "vmsList");
		printList(ipAddresses, "ipAddresses");
		printList(physicalIds, "physicalIds");

		MonitoringResourceDatasets mrds = new MonitoringResourceDatasets();

		Client client = Client.create();

		String paramArray[] = parameters.split(Pattern.quote("."));
		logger.debug("parameter array:" + Arrays.toString(paramArray));

		// [6] a, b, c
		if (methodName.equals("getReportForAllVirtual")
				|| methodName.equals("getReportForAllPhysical")
				|| methodName.equals("getReportForAllEnergy")) {
			for (String ip : ipAddresses) {
				String url = this.getAddress(ip, methodName);
				WebResource service = client.resource(url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		}
		// [5][8] a, b, c, d
		else if (methodName.equals("getReportForService")
				|| methodName.equals("getReportForVirtual")
				|| methodName.equals("getReportForPhysical")
				|| methodName.equals("getReportForEnergy")
				|| methodName.equals("getLatestReportForService")
				|| methodName.equals("getLatestReportForVirtual")
				|| methodName.equals("getLatestReportForPhysical")
				|| methodName.equals("getLatestReportForEnergy")) {
			for (String ip : ipAddresses) {
				String id = paramArray[0];
				String url = this.getAddress(ip, methodName) + "/" + id;
				WebResource service = client.resource(url);
				logger.debug("url = " + url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		}
		// [10] a, b, c, d
		else if (methodName.equals("getReportForPartService")
				|| methodName.equals("getReportForPartVirtual")
				|| methodName.equals("getReportForPartPhysical")
				|| methodName.equals("getReportForPartEnergy")) {
			for (String ip : ipAddresses) {
				String dateFrom = paramArray[0];
				String dateTo = paramArray[1];
				String url = this.getAddress(ip, methodName) + "/" + dateFrom
						+ "." + dateTo;
				WebResource service = client.resource(url);
				logger.debug("url = " + url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		}
		// [11] a, b, c, d
		else if (methodName.equals("getReportForPartServiceId")
				|| methodName.equals("getReportForPartVirtualId")
				|| methodName.equals("getReportForPartPhysicalId")
				|| methodName.equals("getReportForPartEnergyId")) {
			for (String ip : ipAddresses) {
				String id = paramArray[0];
				String dateFrom = paramArray[1];
				String dateTo = paramArray[2];
				String url = this.getAddress(ip, methodName) + "/" + id + "/"
						+ dateFrom + "." + dateTo;
				WebResource service = client.resource(url);
				logger.debug("url = " + url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		}
		// [7] [9]
		else if (methodName.equals("getReportForMetric")
				|| methodName.equals("getLatestReportForMetricName")) {
			for (String ip : ipAddresses) {
				String metricName = paramArray[0];
				String resourceType = paramArray[1];
				String url = this.getAddress(ip, methodName) + "/" + metricName
						+ "/" + resourceType;
				WebResource service = client.resource(url);
				logger.debug("url = " + url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		}
		// [12]
		else if (methodName.equals("getReportForPartMetricName")) {
			for (String ip : ipAddresses) {
				String metricName = paramArray[0];
				String resourceType = paramArray[1];
				String dateFrom = paramArray[2];
				String dateTo = paramArray[3];
				String url = this.getAddress(ip, methodName) + "/" + metricName
						+ "/" + resourceType + "/" + dateFrom + "." + dateTo;
				WebResource service = client.resource(url);
				logger.debug("url = " + url);
				MonitoringResourceDatasets dataSet = service
						.get(MonitoringResourceDatasets.class);
				mrds.getMonitoring_resource().addAll(
						dataSet.getMonitoring_resource());
			}
			return mrds;
		} else
			return mrds;
	}

	/**
	 * Run getLatestComplete*Resource methods on multiple IPs.
	 * 
	 * @param serviceId
	 * @param methodName
	 * @param parameters
	 * @return MonitoringResourceDatasets
	 */
	@GET
	@Path("/MultipleIPs/complete/{serviceId}/{methodName}/{parameters}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getCompleteMultipleIPs(
			@PathParam("serviceId") String serviceId,
			@PathParam("methodName") String methodName,
			@PathParam("parameters") String parameters) {

		CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();
		// Get list of VMs.
		List<String> vmsList = co.getVMsIdsOfService(serviceId);
		// Get list of physical node ids and list of IP addresses needed to call
		// the endpoints.
		HashMap<String, List<String>> map = getMultipleIpMap(vmsList);
		List<String> ipAddresses = map.get("ipAddresses");
		List<String> physicalIds = map.get("nodes");

		// Debug
		printList(vmsList, "vmsList");
		printList(ipAddresses, "ipAddresses");
		printList(physicalIds, "physicalIds");

		Client client = Client.create();

		MonitoringResourceDatasets returnedDataset = new MonitoringResourceDatasets();
		MonitoringResourceDatasets tempMRD = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> mrList = new ArrayList<MonitoringResourceDataset>();

		String paramArray[] = parameters.split(Pattern.quote("."));

		for (String ip : ipAddresses) {
			String url = this.getAddress(ip, methodName) + "/" + paramArray[0];
			WebResource service = client.resource(url);
			tempMRD = service.get(MonitoringResourceDatasets.class);
			mrList.addAll(tempMRD.getMonitoring_resource());
		}

		returnedDataset.setMonitoring_resource(mrList);
		return returnedDataset;
	}

	/**
	 * Returns IP addresses associated with the service id passed as input
	 * argument.
	 * 
	 * @param serviceId
	 * @return List of VM ids associated with serviceId and list of IP addresses
	 */
	@GET
	@Path("/MultipleIPs/vmips/{serviceId}")
	@Produces( { MediaType.TEXT_PLAIN })
	public String getVMIPs(@PathParam("serviceId") String serviceId) {

		CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();
		List<String> vmsList = co.getVMsIdsOfService(serviceId);
		if (vmsList == null) {
			return "empty";
		} else {
			List<String> ipAddresses = getMultipleIpMap(vmsList).get(
					"ipAddresses");
			String rs = array2Str(vmsList, ",") + " ; "
					+ array2Str(ipAddresses, ",");
			return rs;
		}
	}

	private static String getIPVmIpAddress(String infrastructureProviderId) {

		if (infrastructureProviderId == null
				|| infrastructureProviderId.length() < 1) {
			return null;
		}
		com.mysql.jdbc.Connection dbconn;
		try {
			Connection conn = ConnectionPool.getFreeConnection();
			dbconn = (com.mysql.jdbc.Connection) conn.getDBConnection(
					DB_TABLE_URL, DB_DRIVER, DB_USERNAME, DB_PASSWORD);

		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return "";
		}

		String ipAdd = InfrastructureProviderQuery.getVmIpAddress(dbconn,
				infrastructureProviderId);
		return ipAdd;
	}

	private String getAddress(String HOST, String interface_option) {
		return "http://" + HOST + ":" + PORT + "/" + PATH
				+ interfaceMap.get(interface_option);
	}

	/**
	 * @param: VM list
	 * @return: hashmap having 2 lists: one list called "ipAdresses", and one
	 *          list called "nodes" that lists the physical node ids associated
	 *          with the VMs
	 */
	private HashMap<String, List<String>> getMultipleIpMap(List<String> vmList) {

		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> ipAddressList = new ArrayList<String>();
		List<String> nodesList = new ArrayList<String>();

		CloudOptimizerRESTClient co = new CloudOptimizerRESTClient();
		for (String vm : vmList) {
			String physicalId = co.getNodeId(vm);
			String ipId = co.getNodeIpId(physicalId);
			String ipAddress = getIPVmIpAddress(ipId);
			if (!nodesList.contains(physicalId))
				nodesList.add(physicalId);
			if (!ipAddressList.contains(ipAddress) && ipAddress != null)
				ipAddressList.add(ipAddress);
		}
		map.put("ipAddresses", ipAddressList);
		map.put("nodes", nodesList);
		return map;
	}

	private String array2Str(List<String> coll, String delimiter) {
		if (coll.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		for (String x : coll)
			sb.append(x + delimiter);
		sb.delete(sb.length() - delimiter.length(), sb.length());
		return sb.toString();
	}

	private void printList(List<String> list, String listName) {
		String[] myArray = list.toArray(new String[list.size()]);
		logger.debug("List " + listName + " = " + Arrays.toString(myArray));
	}
}
