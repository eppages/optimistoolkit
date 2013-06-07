/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class MonitoringUtil {

	private final static Logger log = Logger
			.getLogger(MonitoringController.class);

	// These are fixed constants for monitoring
	public static final String SERVICE_TYPE = "service";
	public static final String POST_PATH = "Aggregator/Aggregator/monitoringresources/service";
	public static final String GET_PATH = "MonitoringManager/QueryResources";

	public static String utf8Encode(String stringToEncode) {
		try {
			return URLEncoder.encode(stringToEncode, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 encoder provided with JRE
			throw new IllegalStateException(e);
		}
	}

	public static MonitoringResourceDatasets measurementToDatasets(
			Measurement measurement, CloudOptimizerRESTClient client) {
		String serviceID = measurement.getServiceID();
		String virtualResourceID = resolveVirtualID(
				measurement.getInstanceID(), client);
		String physicalResourceID = "";
		String resource_type = SERVICE_TYPE;
		String collectorID = "ServiceCollector";
		String metric_name = measurement.getName();
		String metric_value = measurement.getData();
		String metric_unit = ""; // TODO add units in measurment class
		Date metric_timstamp = new Date(measurement.getTimestamp());

		MonitoringResourceDataset data = new MonitoringResourceDataset(
				serviceID, virtualResourceID, physicalResourceID,
				resource_type, collectorID, metric_name, metric_value,
				metric_unit, metric_timstamp);

		log.debug("Created dataset with serviceId: " + serviceID
				+ ", virtualResourceID: " + virtualResourceID
				+ ", resource_type: " + resource_type + ", collectorID: "
				+ collectorID + ", metric_name: " + metric_name + ", metric_value: " + metric_value);

		List<MonitoringResourceDataset> dataList = new ArrayList<MonitoringResourceDataset>();
		dataList.add(data);

		MonitoringResourceDatasets sets = new MonitoringResourceDatasets();
		sets.setMonitoring_resource(dataList);

		return sets;
	}

	/**
	 * Call to CO to resolve the virtual id (i.e.
	 * a674f5ab-3f63-45dd-bf63-51b30174c3bd) instead of the vm name (i.e
	 * system-optimis-pm-AllCores_instance-1))
	 * 
	 * Implemented as a workaround to a flaw in Monitoring
	 * 
	 * @param instanceID
	 *            The human-readable instanceID
	 * @param CloudOptimizerRESTClient
	 *            the client to use for resolving
	 * @return The virtual ID, or null if resolving fails
	 */
	private static String resolveVirtualID(String instanceID,
			CloudOptimizerRESTClient client) {
		try {
			String vmId = client.getVMId(instanceID);
			return vmId;
		} catch (Exception e) {
			log.warn("Failed to resolve VMid from CloudOptimizer, returning null. Error message was: "
					+ e.getMessage());
			return null;
		}
	}

	public static Set<Measurement> datasetToMeasurements(
			MonitoringResourceDatasets dataSets) {

		List<MonitoringResourceDataset> dataList = dataSets
				.getMonitoring_resource();
		Set<Measurement> measurementSet = new HashSet<Measurement>();

		for (MonitoringResourceDataset data : dataList) {

			String serviceID = data.getService_resource_id();
			String instanceID = data.getMonitoring_information_collector_id();
			String name = data.getMetric_name();
			String value = data.getMetric_value();
			long timestamp = data.getMetric_timestamp().getTime();

			Measurement m = new Measurement(serviceID, instanceID, name, value,
					timestamp);
			measurementSet.add(m);
		}

		return measurementSet;
	}

	public static String measurementToXMLString(Measurement measurement,
			CloudOptimizerRESTClient client) {
		
		StringBuilder xmlString = new StringBuilder();
		
			String serviceID = measurement.getServiceID();
			String virtualResourceID = resolveVirtualID(
					measurement.getInstanceID(), client);
			String physicalResourceID = "";
			String resource_type = SERVICE_TYPE;
			String collectorID = "ServiceCollector";
			String metric_name = measurement.getName();
			String metric_value = measurement.getData();
			String metric_unit = ""; // TODO add units in measurment class
			long timeStamp = measurement.getTimestamp();
			
			//Convert to second precision
			if (timeStamp > 9999999999L) {
				timeStamp /= 1000;
			}
			
			xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			xmlString.append("<MonitoringResources>");
			xmlString.append("<monitoring_resource>");
			xmlString.append("<physical_resource_id></physical_resource_id>");
			xmlString.append("<metric_name>" + metric_name + "</metric_name>");
			xmlString.append("<metric_timestamp>" + timeStamp + "</metric_timestamp>");
			xmlString.append("<metric_unit>" + metric_unit + "</metric_unit>");
			xmlString.append("<metric_value>" + metric_value + "</metric_value>");
			xmlString.append("<monitoring_information_collector_id>" + collectorID + "</monitoring_information_collector_id>");
			xmlString.append("<resource_type>" + resource_type + "</resource_type>");
			xmlString.append("<service_resource_id>" + serviceID + "</service_resource_id>");
			xmlString.append("<virtual_resource_id>" + virtualResourceID + "</virtual_resource_id>");
			xmlString.append("</monitoring_resource>");																				xmlString.append("</MonitoringResources>");
			
			return xmlString.toString();
	}
}
