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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap; //import javax.ws.rs.FormParam;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

import eu.optimis.mi.aggregator.bean.InformationCollector;
import eu.optimis.mi.aggregator.bean.InformationCollectors;
import eu.optimis.mi.aggregator.queries.InfoCollectorQuery;
import eu.optimis.mi.aggregator.queries.InsertQuery;
import eu.optimis.mi.aggregator.util.ConfParam;
import eu.optimis.mi.aggregator.util.XmlUtil;
import eu.optimis.mi.dbutil.AggregatorDBUtil;

/**
 * OPTIMIS Base Toolkit - Monitoring Infrastructure - Aggregator Aggregator
 * provides the functionalities to gather all monitoring data from the different
 * monitoring collectors and store it into the Monitoring DB.
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
	private static HashMap<String, CollectorThread> map = new HashMap<String, CollectorThread>();
	private static HashMap<String, String> typemap = new HashMap<String, String>();
	private static String location = ConfParam.location;

	private static MonitoringResourceDatasets currentPhysicalRes;
	private static MonitoringResourceDatasets currentVirtualRes;
	private static MonitoringResourceDatasets currentEnergyRes;
	private static MonitoringResourceDatasets currentServiceRes;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testAggregator() {
		return "Aggregator is running, welcome!";
	}

	@GET
	@Path("/threads/total")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRunnungThreadNb() {
		return Integer.toString(map.size());
	}

	@GET
	@Path("/threads/list")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRunnungCollectors() {
		String keystr = "";
		Set<Map.Entry<String, CollectorThread>> mset = map.entrySet();
		for (Map.Entry<String, CollectorThread> me : mset) {
			if (keystr.length() == 0) {
				keystr = me.getKey();
			} else
				keystr = "," + keystr + me.getKey();
		}
		return keystr;
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

	@Path("/startmonitoring")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String startmonAll() {
		/*
		 * 1. Get all the collector configurations from the db 2. Create a thread
		 * for each collector. 3. For each thread, store the monitoring data
		 * collected by the collector into the db.
		 */
		logger.info("==========call start monitoring===========");
		InformationCollectors infoCols = InfoCollectorQuery
				.getInfoCollectors(AggregatorDBUtil.getConnection(location));
		for (InformationCollector incol : infoCols.getInformation_collector()) {
			String scriptPath = incol.getCollector_script_path();
			int timeInterval = incol.getTime_interval_in_ms();
			String collectorId = null;
			collectorId = incol.getCollector_id();
			String collectorName = incol.getName();
			if (collectorName.toUpperCase().contains("PHYSICAL")) {
				typemap.put("physical", collectorId);
			} else if (collectorName.toUpperCase().contains("VIRTUAL")) {
				typemap.put("virtual", collectorId);
			}
			if (!map.containsKey(collectorId)) {
				logger.debug("Create a new thread for  a collector: col-ID:"
						+ collectorId + "; scriptPath:" + scriptPath
						+ "; timeInterval:" + timeInterval);

				CollectorThread oneThread = new CollectorThread(collectorId,
						scriptPath, timeInterval);
				logger.debug("new ThreadID:" + oneThread.getId());
				map.put(collectorId, oneThread);
				oneThread.startRunning();
			} else {
				logger.debug("A thread(id=" + map.get(collectorId).getId()
						+ ") for collector has been running colname:"
						+ incol.getName() + "; collectorId:" + collectorId);
			}
		}
		return "running";
	}

	@Path("/stopmonitoring")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String stopmonAll() {
		logger.info("==========call stop monitoring===========");
		for (String key : map.keySet()) {
			CollectorThread thread = map.get(key);
			logger.debug("stop thread: " + thread.getId());
			thread.stopRunning();
		}
		map.clear();
		typemap.clear();
		return "stop";
	}
	
	// PUSH: service resource, generic resource
	@Path("/monitoringresources")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response pushmonitoringreport(MonitoringResourceDatasets mdsList)
			throws IOException {
		String upperType = mdsList.getMonitoring_resource().get(0)
				.getResource_type().toUpperCase();
		if (upperType.contains("SERVICE"))
			currentServiceRes = mdsList;
		else if (upperType.contains("PHYSICAL"))
			currentPhysicalRes = mdsList;
		else if (upperType.contains("VIRTUAL"))
			currentVirtualRes = mdsList;
		else if (upperType.contains("ENERGY"))
			currentEnergyRes = mdsList;
		for (MonitoringResourceDataset md : mdsList.getMonitoring_resource()) {
			if (checkConsistencyBefore(md) == false) {
				logger
						.error("Cannot store virtual or physical resource, it has not fulfilled the precondition. "
								+ "Resource type:" + md.getResource_type());
			} else {
				if ((md.getPhysical_resource_id() == null || (md
						.getPhysical_resource_id().equals("")))
						&& (md.getResource_type().toUpperCase()
								.equals("SERVICE"))) {
					HashMap<String,String> virtualPhysicalMap = new HashMap<String,String>();
					try {
						CloudOptimizerRESTClient cl = new CloudOptimizerRESTClient();
						List<String> virtualIdList;
						virtualIdList = cl.getVMsIdsOfService(md.getService_resource_id());
						for (String vid : virtualIdList) {
							String physicalId = cl.getNodeId(vid);
							virtualPhysicalMap.put(vid,physicalId);
						}

					} catch (Exception e) {
						logger
								.error("CloudOptimizer error, failed to call getVMsIdsOfService(id) or getNodeId(vid).");
					}
					// Store monitoring data.
					for (String key: virtualPhysicalMap.keySet()){
						md.setVirtual_resource_id(key);
						md.setPhysical_resource_id(virtualPhysicalMap.get(key));
						if (checkConsistencyAfter(md)) {
							java.sql.Connection conn = AggregatorDBUtil
									.getConnection(location);
							InsertQuery.insertAResource(conn, md);
						} else
							logger
									.error("Cannot store virtual or physical resource, because either virtual_id or physical_id is null.");
						}
					
				} else if ((md.getPhysical_resource_id() == null || (md
						.getPhysical_resource_id().equals("")))
						&& (md.getResource_type().toUpperCase()
								.equals("VIRTUAL"))) {
					String physicalId = null;
					String virtualId = md.getVirtual_resource_id();
					try {
						CloudOptimizerRESTClient cl = new CloudOptimizerRESTClient();
						physicalId = cl.getNodeId(virtualId);

						md.setPhysical_resource_id(physicalId);
					} catch (Exception e) {
						logger
								.error("CloudOptimizer error, failed to call getVMId(id) or getNodeId(vid).");
						// continue;
					}

					md.setVirtual_resource_id(virtualId);
					md.setPhysical_resource_id(physicalId);
					
					if (checkConsistencyAfter(md)) {
						java.sql.Connection conn = AggregatorDBUtil
								.getConnection(location);
						InsertQuery.insertAResource(conn, md);
					} else
						logger
								.error("Cannot store virtual or physical resource, because either virtual_id or physical_id is null.");
					}
				}
			}
		return Response.ok().build();
	}
	//

	// PUSH: physical resource, virtual resource
	@Path("/monitoringresources/{collectorId}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public synchronized Response pushColmonitoringreport(
			MonitoringResourceDatasets mdsList,
			@PathParam("collectorId") String collectorId) throws Exception {
		for (MonitoringResourceDataset md : mdsList.getMonitoring_resource()) {
			md.setMonitoring_information_collector_id(collectorId);
		}
		String upperType = mdsList.getMonitoring_resource().get(0)
				.getResource_type().toUpperCase();
		if (upperType.contains("PHYSICAL"))
			currentPhysicalRes = mdsList;
		else if (upperType.contains("VIRTUAL"))
			currentVirtualRes = mdsList;
		else if (upperType.contains("SERVICE"))
			currentServiceRes = mdsList;
		else if (upperType.contains("ENERGY"))
			currentEnergyRes = mdsList;
		else
			;
		for (MonitoringResourceDataset md : mdsList.getMonitoring_resource()) {

			if (checkConsistencyBefore(md) == false) {
				logger
						.error("Cannot store virtual or physical resource, it has not fulfilled the precondition. "
								+ "Resource type:" + md.getResource_type());
			} else {
				if (((md.getPhysical_resource_id() == null) || (md
						.getPhysical_resource_id().equals("")))
						&& (md.getResource_type().toUpperCase()
								.equals("VIRTUAL"))) {

					String virtualId = md.getVirtual_resource_id();
					String physicalId = null;
					try {
						CloudOptimizerRESTClient cl = new CloudOptimizerRESTClient();
						physicalId = cl.getNodeId(virtualId);

						md.setPhysical_resource_id(physicalId);
					} catch (Exception e) {
						logger
								.error("CloudOptimizer error, failed to call getVMId(id) or getNodeId(vid).");
						// continue;
					}
				}
				if (checkConsistencyAfter(md)) {
					java.sql.Connection conn = AggregatorDBUtil
							.getConnection(location);
					InsertQuery.insertAResource(conn, md);
				} else
					logger
							.error("Cannot store virtual or physical resource, because either virtual_id or physical_id is null.");
			}
		}
		return Response.ok().build();
	}

	@Path("/startmonitoring/{collectorId}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String startmon(@PathParam("collectorId") String collectorId) {

		InformationCollectors infoCols = InfoCollectorQuery
				.getOneInfoCollector(AggregatorDBUtil.getConnection(location),
						collectorId);
		InformationCollector incol = infoCols.getInformation_collector().get(0);
		String scriptPath = incol.getCollector_script_path();
		int timeInterval = incol.getTime_interval_in_ms();
		if (!map.containsKey(collectorId)) {
			logger.debug("Create a new thread for  a collector: col-ID:"
					+ collectorId + "; scriptPath:" + scriptPath
					+ "; timeInterval:" + timeInterval);

			CollectorThread oneThread = new CollectorThread(collectorId,
					scriptPath, timeInterval);
			logger.debug("new ThreadID:" + oneThread.getId());
			map.put(collectorId, oneThread);
			oneThread.startRunning();
		} else {
			logger.debug("A thread(id=" + map.get(collectorId).getId()
					+ ") for collector has been running colname:"
					+ incol.getName() + "; collectorId:" + collectorId);
		}

		return "running";
	}

	@Path("/stopmonitoring/{collectorId}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String stopmon(@PathParam("collectorId") String collectorId) {
		logger.info("==========call stop monitoring===========");
		CollectorThread thread = map.get(collectorId);
		logger.debug("stop thread: " + thread.getId());
		thread.stopRunning();
		map.remove(collectorId);
		return "stop";
	}

	private boolean checkConsistencyBefore(MonitoringResourceDataset md) {
		String serviceId = md.getService_resource_id();
		String physicalId = md.getPhysical_resource_id();
		String virtualId = md.getVirtual_resource_id();
		String type = md.getResource_type();
		if (type == null)
			return false;
		if ((type.toUpperCase().contains("PHYSICAL") || type.toUpperCase()
				.contains("ENERGY"))
				&& (physicalId == null || physicalId.length() < 1))
			return false;
		else if (type.toUpperCase().contains("VIRTUAL")
				&& (virtualId == null || virtualId.length() < 1))
			return false;
		else if (type.toUpperCase().contains("SERVICE")
				&& (serviceId == null || serviceId.length() < 1))
			return false;
		else
			return true;
	}

	private boolean checkConsistencyAfter(MonitoringResourceDataset md) {
		String collectorId = md.getMonitoring_information_collector_id();

		if (collectorId == null)
			return false;
		String physicalId = md.getPhysical_resource_id();
		String virtualId = md.getVirtual_resource_id();
		String type = md.getResource_type();

		if (type.toUpperCase().contains("VIRTUAL")
				&& (physicalId == null || physicalId.length() < 1))
			return false;
		else if (type.toUpperCase().contains("SERVICE")
				&& (physicalId == null || virtualId == null
						|| virtualId.length() < 1 || physicalId.length() < 1))
			return false;
		else
			return true;
	}
}
