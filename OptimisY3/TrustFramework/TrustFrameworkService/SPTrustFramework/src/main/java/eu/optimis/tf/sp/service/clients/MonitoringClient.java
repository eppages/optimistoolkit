/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.clients;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

public class MonitoringClient {

	/**
	 * @param args
	 */

	Logger log = Logger.getLogger(this.getClass().getName());

	private String host;
	private String uri;
	private int port;

	public MonitoringClient(String host, int port, String uri) {
		this.host = host;
		this.port = port;
		this.uri = uri;
	}

	public MonitoringClient() {
		// this.host = "192.168.252.56";
		// this.port = 8080;
		this.host = PropertiesUtils.getProperty("TRUST","monitoring.host");
		this.port = Integer.valueOf(PropertiesUtils.getProperty("TRUST","monitoring.port"));
		this.uri = PropertiesUtils.getProperty("TRUST","monitoring.uri");
	}

	public List<MonitoringResourceDataset> getMonitoringServiceInfo(
			String service_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc.getLatestCompleteReportForService(service_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	public List<MonitoringResourceDataset> getMonitoringVirtualInfo(
			String virtual_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc.getLatestReportForVirtual(virtual_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			// for (MonitoringResourceDataset resource: resources){
			// log.info(resource.getMetric_name() +
			// " : "+resource.getMetric_value());
			// }
			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}

	public List<MonitoringResourceDataset> getMonitoringPhysicalInfo(
			String physical_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc
					.getLatestCompleteReportForPhysical(physical_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			// for (MonitoringResourceDataset resource: resources){
			// log.info(resource.getMetric_name() +
			// " : "+resource.getMetric_value());
			// }
			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	
//	public List<MonitoringResourceDataset> getLatestMonitoringPhysicalInfo(
//			String physical_ID) {
//
//		try {
//
//			getClient gc = new getClient(host, port, uri);
//			MonitoringResourceDatasets mrd = gc
//					.getLatestReportForPhysical(physical_ID);
//			List<MonitoringResourceDataset> resources = mrd
//					.getMonitoring_resource();
//
//			// for (MonitoringResourceDataset resource: resources){
//			// log.info(resource.getMetric_name() +
//			// " : "+resource.getMetric_value());
//			// }
//			return resources;
//
//		} catch (Exception e) {
//			System.out.println(e);
//			return null;
//		}
//
//	}
	
	public List<MonitoringResourceDataset> getReportForPartMetricName(String metricName){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar pastCal = Calendar.getInstance();
		pastCal.add(Calendar.HOUR, -1);
		getClient gc = new getClient(host, port, uri);
		MonitoringResourceDatasets mrds = gc.getReportForPartMetricName(metricName, "service", cal.getTime(), pastCal.getTime());
		return mrds.getMonitoring_resource();
	}

	public List<MonitoringResourceDataset> getLatestReport4Phisycal(
			String physical_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc
					.getLatestReportForPhysical(physical_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			// for (MonitoringResourceDataset resource: resources){
			// log.info(resource.getMetric_name() +
			// " : "+resource.getMetric_value());
			// }
			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	
	public List<MonitoringResourceDataset> getLatestMonitoringVirtualInfo(
			String virtual_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc
					.getLatestReportForVirtual(virtual_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	
	public List<MonitoringResourceDataset> getLatestMonitoringServiceInfo(
			String service_ID) {

		try {

			getClient gc = new getClient(host, port, uri);
			MonitoringResourceDatasets mrd = gc
					.getLatestReportForService(service_ID);
			List<MonitoringResourceDataset> resources = mrd
					.getMonitoring_resource();

			return resources;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	
//	public List<MonitoringResourceDataset> getLatestReport4Metric(
//			String metricName) {
//
//		try {
//
//			getClient gc = new getClient(host, port, uri);
//			MonitoringResourceDatasets mrd = gc
//					.getLatestReportForMetricName(metricName);
//			List<MonitoringResourceDataset> resources = mrd
//					.getMonitoring_resource();
//
//			return resources;
//
//		} catch (Exception e) {
//			System.out.println(e);
//			return null;
//		}
//
//	}

}
