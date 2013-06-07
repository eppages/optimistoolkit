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

import org.apache.log4j.Logger;

import eu.optimis.mi.dbutil.MManagerDBUtil;
import eu.optimis.mi.monitoring_manager.queries.DateTypeLevelQuery;
import eu.optimis.mi.monitoring_manager.queries.DistrinctTypeLevelQuery;
import eu.optimis.mi.monitoring_manager.queries.GuiQuery;
import eu.optimis.mi.monitoring_manager.queries.TypeLevelQuery;
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
	private static String location = "remote";
	private static String dateformat = "yyyyMMddHHmmss";

	private final static Logger logger = Logger
			.getLogger(MonitorManagerQueryResource.class.getName());

	// [5]
	@GET
	@Path("type/{resourceType}/{resourceId}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getTypeLevelResource(
			@PathParam("resourceType") String resourceType,
			@PathParam("resourceId") String resourceId) {
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return TypeLevelQuery.getResource_type_tid(conn, resourceType,
				resourceId);
	}

	// [6]
	@GET
	@Path("type/{resourceType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getAllTypeLevelResource(
			@PathParam("resourceType") String resourceType) {
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return TypeLevelQuery.getResource_type_all(conn, resourceType);
	}

	// [7]
	@GET
	@Path("type/metric/{metricName}/{resourceType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getMetricMonitoringResource2(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType) {
		//		
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return TypeLevelQuery.getResource_metricName(conn, metricName,
				resourceType);
	}

	// Group [8]
	@GET
	@Path("group/type/{resourceType}/{resourceId}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getGroupTypeLevelResource(
			@PathParam("resourceType") String resourceType,
			@PathParam("resourceId") String resourceId) {
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return DistrinctTypeLevelQuery.getResource_type_tid(conn, resourceType,
				resourceId);
	}

	// Group Metric [9]
	@GET
	@Path("group/metric/{metricName}/{resourceType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getGroupMetricMonitoringResource2(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType) {
		//		
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return DistrinctTypeLevelQuery.getResource_metric(conn, metricName,
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
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return DateTypeLevelQuery.getResource_type_all(conn, resourceType,
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
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return DateTypeLevelQuery.getResource_type_tid(conn, resourceType,
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
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return DateTypeLevelQuery.getResource_type_mname(conn, resourceType,
				metricName, dfrom, dto);
	}

	// group complete [13]
	@GET
	@Path("group/complete/service/{serviceId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompleteServiceResource(
			@PathParam("serviceId") String serviceId) {

		MonitoringResourceDatasets smrs = getGroupTypeLevelResource("service", serviceId);

		HashMap<String, String> hmap = new HashMap<String, String>();
		
		for (int i = 0; i < smrs.getMonitoring_resource().size(); i++) {
			String virtualId = smrs.getMonitoring_resource().get(i).getVirtual_resource_id();
			String physicalId = smrs.getMonitoring_resource().get(i).getPhysical_resource_id();
			if (!hmap.containsKey(virtualId)) hmap.put(virtualId, physicalId);
		}
		
		MonitoringResourceDatasets virtualSets = new MonitoringResourceDatasets();
		MonitoringResourceDatasets physicalSets = new MonitoringResourceDatasets();
		MonitoringResourceDatasets energySets = new MonitoringResourceDatasets();
		
		for (String key : hmap.keySet()) {
			String value = hmap.get(key);
			MonitoringResourceDatasets vmrs = getGroupTypeLevelResource("virtual", key);
			if (vmrs.getMonitoring_resource().size() > 0) {
				virtualSets.getMonitoring_resource().addAll(vmrs.getMonitoring_resource());
				MonitoringResourceDatasets pmrs = getGroupTypeLevelResource("physical", value);
				physicalSets.getMonitoring_resource().addAll(pmrs.getMonitoring_resource());
				MonitoringResourceDatasets emrs = getGroupTypeLevelResource("energy", value);
				energySets.getMonitoring_resource().addAll(emrs.getMonitoring_resource());
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

		MonitoringResourceDatasets smrs = getGroupTypeLevelResource("virtual", virtualId);
		
		MonitoringResourceDatasets pmrs = new MonitoringResourceDatasets();
		MonitoringResourceDatasets emrs = new MonitoringResourceDatasets();
		
		if (smrs.getMonitoring_resource().size() > 0) {
			String physicalId = smrs.getMonitoring_resource().get(0).getPhysical_resource_id();
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

		MonitoringResourceDatasets pmrs = getGroupTypeLevelResource("physical", physicalId);

		return pmrs;
	}

	// group complete [16]
	@GET
	@Path("group/complete/energy/{physicalId}")
	@Produces( { MediaType.APPLICATION_XML })
	public MonitoringResourceDatasets getLatestCompleteEnergyResource(
			@PathParam("physicalId") String physicalId) {

		MonitoringResourceDatasets emrs = getGroupTypeLevelResource("energy", physicalId);

		return emrs;
	}
	
	// GUI Interface
	@GET
	@Path("gui/ids/{type}")
	@Produces( { MediaType.TEXT_PLAIN })
	public String getIds(
			@PathParam("type") String type) {
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return GuiQuery.getIds(conn, type);
	}
	
	// [17] GUI Interface
	@GET
	@Path("date/metric/{metricName}/{resourceType}/{id}/{from}.{to}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MonitoringResourceDatasets getDateIDMetricLevelResource(
			@PathParam("metricName") String metricName,
			@PathParam("resourceType") String resourceType,
			@PathParam("id") String id,
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
		java.sql.Connection conn = MManagerDBUtil.getConnection(location);
		return GuiQuery.getDate_Metric_tid(conn,
				metricName, resourceType, id, dfrom, dto);
	}
}
