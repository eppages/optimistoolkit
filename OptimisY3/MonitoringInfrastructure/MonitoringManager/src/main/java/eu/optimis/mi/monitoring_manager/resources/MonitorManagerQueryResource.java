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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import eu.optimis.mi.monitoring_manager.util.ConfigManager;
import eu.optimis.mi.monitoring_manager.db.ConnectionPool;
import eu.optimis.mi.monitoring_manager.queries.DateTypeLevelQuery;
import eu.optimis.mi.monitoring_manager.queries.DistrinctTypeLevelQuery;
import eu.optimis.mi.monitoring_manager.queries.GuiQuery;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

/**
 * OPTIMIS basic Toolkit - Monitoring Infrastructure - Monitoring Manager
 * Monitoring Manager provides the functionalities to gather all monitoring data
 * from the different monitoring connectors.
 * 
 * @author Tinghe Wang (twang@hlrs.de)
 * @author Pierre Gilet (gilet@hlrs.de)
 * 
 */
@Path("/QueryResources")
public class MonitorManagerQueryResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private static String dateformat = "yyyyMMddHHmmss";
	private static String DB_TABLE_URL;
	private static String DB_DRIVER;
	private static String DB_USERNAME;
	private static String DB_PASSWORD;

	private final static Logger logger = Logger
			.getLogger(MonitorManagerQueryResource.class);

	public MonitorManagerQueryResource() {
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

	// Group [8]
	@GET
	@Path("group/type/{resourceType}/{resourceId}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getGroupTypeLevelResource(
			@PathParam("resourceType") String resourceType,
			@PathParam("resourceId") String resourceId) {
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		return DistrinctTypeLevelQuery.getResource_type_tid(dbconn,
				resourceType, resourceId);
	}

	// Group Metric [9]
	@GET
	@Path("group/metric/{metricName}/{resourceType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getGroupMetricMonitoringResource2(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType) {
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		return DistrinctTypeLevelQuery.getResource_metric(dbconn, metricName,
				resourceType);
	}

	// date [10]
	@GET
	@Path("date/type/{resourceType}/{from}.{to}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getDateTypeLevelResource(
			@PathParam("resourceType") String resourceType,
			@PathParam("from") String from, @PathParam("to") String to) {

		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		java.util.Date dfrom = null;
		java.util.Date dto = null;
		try {
			dfrom = sdf.parse(from);
			dto = sdf.parse(to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		return DateTypeLevelQuery.getResource_type_all(dbconn, resourceType,
				dfrom, dto);
	}

	// Date [11]
	@GET
	@Path("date/type/{resourceType}/{resourceId}/{from}.{to}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getDateTypeIdLevelResource(
			@PathParam("resourceType") String resourceType,
			@PathParam("resourceId") String resourceId,
			@PathParam("from") String from, @PathParam("to") String to) {

		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		java.util.Date dfrom = null;
		java.util.Date dto = null;
		try {
			dfrom = sdf.parse(from);
			dto = sdf.parse(to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		return DateTypeLevelQuery.getResource_type_tid(dbconn, resourceType,
				resourceId, dfrom, dto);
	}

	// Date metric [12]
	@GET
	@Path("date/metric/{metricName}/{resourceType}/{from}.{to}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getDateTypeMetricLevelResource(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType,
			@PathParam("from") String from, @PathParam("to") String to) {

		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		java.util.Date dfrom = null;
		java.util.Date dto = null;
		try {
			dfrom = sdf.parse(from);
			dto = sdf.parse(to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		logger.info("DB_TABLE_URL:" + DB_TABLE_URL);
		return DateTypeLevelQuery.getResource_type_mname(dbconn, resourceType,
				metricName, dfrom, dto);
	}

	// group complete [13]
	@GET
	@Path("group/complete/service/{serviceId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompleteServiceResource(
			@PathParam("serviceId") String serviceId) {

		MonitoringResourceDatasets smrs = getGroupTypeLevelResource("service",
				serviceId);

		HashMap<String, String> hmap = new HashMap<String, String>();

		for (int i = 0; i < smrs.getMonitoring_resource().size(); i++) {
			String virtualId = smrs.getMonitoring_resource().get(i)
					.getVirtual_resource_id();
			String physicalId = smrs.getMonitoring_resource().get(i)
					.getPhysical_resource_id();
			if (!hmap.containsKey(virtualId))
				hmap.put(virtualId, physicalId);
		}

		MonitoringResourceDatasets virtualSets = new MonitoringResourceDatasets();
		MonitoringResourceDatasets physicalSets = new MonitoringResourceDatasets();
		MonitoringResourceDatasets energySets = new MonitoringResourceDatasets();

		for (String key : hmap.keySet()) {
			String value = hmap.get(key);
			MonitoringResourceDatasets vmrs = getGroupTypeLevelResource(
					"virtual", key);
			if (vmrs.getMonitoring_resource().size() > 0) {
				virtualSets.getMonitoring_resource().addAll(
						vmrs.getMonitoring_resource());
				MonitoringResourceDatasets pmrs = getGroupTypeLevelResource(
						"physical", value);
				physicalSets.getMonitoring_resource().addAll(
						pmrs.getMonitoring_resource());
				MonitoringResourceDatasets emrs = getGroupTypeLevelResource(
						"energy", value);
				energySets.getMonitoring_resource().addAll(
						emrs.getMonitoring_resource());
			}
		}

		MonitoringResourceDatasets returnedDataSet = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> mrList = new ArrayList<MonitoringResourceDataset>();
		mrList.addAll(smrs.getMonitoring_resource());
		mrList.addAll(physicalSets.getMonitoring_resource());
		mrList.addAll(energySets.getMonitoring_resource());
		mrList.addAll(virtualSets.getMonitoring_resource());
		returnedDataSet.setMonitoring_resource(mrList);

		return returnedDataSet;
	}

	// group complete [14]
	@GET
	@Path("group/complete/virtual/{virtualId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompleteVirtualResource(
			@PathParam("virtualId") String virtualId) {

		MonitoringResourceDatasets smrs = getGroupTypeLevelResource("virtual",
				virtualId);

		MonitoringResourceDatasets pmrs = new MonitoringResourceDatasets();
		MonitoringResourceDatasets emrs = new MonitoringResourceDatasets();

		if (smrs.getMonitoring_resource().size() > 0) {
			String physicalId = smrs.getMonitoring_resource().get(0)
					.getPhysical_resource_id();
			pmrs = getGroupTypeLevelResource("physical", physicalId);
			emrs = getGroupTypeLevelResource("energy", physicalId);
		}

		MonitoringResourceDatasets returnedDataSet = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> mrList = new ArrayList<MonitoringResourceDataset>();
		mrList.addAll(smrs.getMonitoring_resource());
		mrList.addAll(pmrs.getMonitoring_resource());
		mrList.addAll(emrs.getMonitoring_resource());
		returnedDataSet.setMonitoring_resource(mrList);

		return returnedDataSet;
	}

	// group complete [15]
	@GET
	@Path("group/complete/physical/{physicalId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompletePhysicalResource(
			@PathParam("physicalId") String physicalId) {

		MonitoringResourceDatasets pmrs = getGroupTypeLevelResource("physical",
				physicalId);

		return pmrs;
	}

	// group complete [16]
	@GET
	@Path("group/complete/energy/{physicalId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompleteEnergyResource(
			@PathParam("physicalId") String physicalId) {

		MonitoringResourceDatasets emrs = getGroupTypeLevelResource("energy",
				physicalId);

		return emrs;
	}

	// [17] GUI Interface
	@GET
	@Path("date/metric/{metricName}/{resourceType}/{id}/{from}.{to}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getDateIDMetricLevelResource(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType,
			@PathParam("id") String id, @PathParam("from") String from,
			@PathParam("to") String to) {

		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		java.util.Date dfrom = null;
		java.util.Date dto = null;
		try {
			dfrom = sdf.parse(from);
			dto = sdf.parse(to);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eu.optimis.mi.monitoring_manager.db.Connection conn;
		java.sql.Connection dbconn;
		try {
			conn = ConnectionPool.getFreeConnection();
			dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
					DB_PASSWORD);
		} catch (Exception e) {
			logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
					+ DB_USERNAME + " | " + DB_PASSWORD);
			logger.error("MonitoringManager DB connection error: "
					+ e.getMessage());
			return new MonitoringResourceDatasets();
		}
		return GuiQuery.getDate_Metric_tid(dbconn, metricName, resourceType,
				id, dfrom, dto);
	}
	
	//[18] extends - Group Metric [9]
		@GET
		@Path("group/metric/id/{metricName}/{resourceType}/{resourceId}")
		@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
		public MonitoringResourceDatasets getGroupMetricMonitoringResourceId(
				@PathParam("metricName") String metricName,
				@PathParam("resourceType") String resourceType,
				@PathParam("resourceId") String resourceId) {
			eu.optimis.mi.monitoring_manager.db.Connection conn;
			java.sql.Connection dbconn;
			try {
				conn = ConnectionPool.getFreeConnection();
				dbconn = conn.getDBConnection(DB_TABLE_URL, DB_DRIVER, DB_USERNAME,
						DB_PASSWORD);
			} catch (Exception e) {
				logger.error("DB info:" + DB_TABLE_URL + " | " + DB_DRIVER + " | "
						+ DB_USERNAME + " | " + DB_PASSWORD);
				logger.error("MonitoringManager DB connection error: "
						+ e.getMessage());
				return new MonitoringResourceDatasets();
			}
			return DistrinctTypeLevelQuery.getResource_metric_id(dbconn, metricName,
					resourceType, resourceId);
		}
	
}
