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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class getClient {

	private static String HOST = "localhost";
	private static int PORT = 8080;
	private static String PATH = "MonitoringManager/QueryResources";

	private final String INTERFACE_1 = "service";
	private final String INTERFACE_2 = "type";
	private final String INTERFACE_3 = "group";
	private final String INTERFACE_4 = "date";
	private final String INTERFACE_5 = "complete";

	private static String dateformat = "yyyyMMddHHmmss";
	private Client client;

	private static void init() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("services");

			HOST = rb.getString("service.mmanager_host");
			PORT = Integer.parseInt(rb.getString("service.mmanager_port"));
			PATH = rb.getString("service.mmanager_path");

		} catch (MissingResourceException e) {
			System.err.println("Error: cannot find the property file.");
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("static-access")
	public getClient() {
		this.init();
		this.client = Client.create();
	}

	public getClient(String host, int port, String path) {
		HOST = host;
		PORT = port;
		PATH = path;
		DefaultClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,	true);
		this.client = Client.create(config);	
	}

	private String getAddress(String interface_option) {
		return "http://" + HOST + ":" + PORT + "/" + PATH + "/"
				+ interface_option;
	}
	// [5] QueryResources/type/{resourceType}/{resourceId}
	public MonitoringResourceDatasets getReportForService(String service_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
			.path("service").path(service_ID);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPhysical(String physical_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("physical").path(physical_ID);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForVirtual(String virtual_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("virtual").path(virtual_ID);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForEnergy(String physical_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("energy").path(physical_ID);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// [6] QueryResource/type/{resourceType}
	public MonitoringResourceDatasets getReportForAllPhysical() {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("physical");
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForAllVirtual() {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("virtual");
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForAllEnergy() {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
				.path("energy");
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	// [7] QueryResources/type/metric/{metricName}/{resourceType}
	public MonitoringResourceDatasets getReportForMetric(String metricName, String resourceType) {
		WebResource service = client.resource(this.getAddress(INTERFACE_2))
			.path("metric").path(metricName).path(resourceType);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// Group Reports
	// [8] QueryResources/group/type/{resourceType}/{resourceId}
	public MonitoringResourceDatasets getLatestReportForEnergy(String physical_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_2).path("energy").path(physical_ID);
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getLatestReportForService(String service_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_2).path("service").path(service_ID);
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getLatestReportForVirtual(String virtual_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_2).path("virtual").path(virtual_ID);
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getLatestReportForPhysical(
			String physical_ID) {
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_2).path("physical").path(physical_ID);
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// [9] QueryResources/group/metric/{metricName}/{resourceType}
	public MonitoringResourceDatasets getLatestReportForMetricName(String metricName, String resourceType) {
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path("metric").path(metricName).path(resourceType);
		MonitoringResourceDatasets dataSet = service.accept(MediaType.APPLICATION_XML)
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// With dates as input args.
	// [10] QueryResource/date/type/{resourceType}/{from}.{to}
	public MonitoringResourceDatasets getReportForPartService(Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("service").path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPartPhysical(Date from,
			Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("physical").path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPartVirtual(Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("virtual").path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}	
	public MonitoringResourceDatasets getReportForPartEnergy(Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("energy").path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// With dates and ids.
	// [11] QueryResource/date/type/{resourceType}/{id}/{from}.{to}
	public MonitoringResourceDatasets getReportForPartServiceId(String serviceId, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("service").path(serviceId).path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPartPhysicalId(String physicalId, Date from,
			Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("physical").path(physicalId).path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPartVirtualId(String virtualId, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("virtual").path(virtualId).path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	public MonitoringResourceDatasets getReportForPartEnergyId(String physicalId, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path(INTERFACE_2).path("energy").path(physicalId).path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// [12] QueryResource/date/metric/{metricName}/{resourceType}/{from}.{to}
	public MonitoringResourceDatasets getReportForPartMetricName(String metricName, String resourceType, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String date = sf+"."+st;
		WebResource service = client.resource(this.getAddress(INTERFACE_4))
				.path("metric").path(metricName).path(resourceType).path(date);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}
	
	// Complete reports.
	// [13] QueryResource/group/complete/service/{serviceId}
	public MonitoringResourceDatasets getLatestCompleteReportForService(String serviceId){
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_5).path("service").path(serviceId);
		MonitoringResourceDatasets cr = service.accept(MediaType.APPLICATION_XML)
		.get(MonitoringResourceDatasets.class);
		return cr;
	}
	// [14] QueryResource/group/complete/virtual/{virtualId}
	public MonitoringResourceDatasets getLatestCompleteReportForVirtual(String virtualId){
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_5).path("virtual").path(virtualId);
		MonitoringResourceDatasets cr = service.accept(MediaType.APPLICATION_XML)
		.get(MonitoringResourceDatasets.class);
		return cr;
	}
	// [15] QueryResource/group/complete/physical/{physicalId}
	public MonitoringResourceDatasets getLatestCompleteReportForPhysical(String physicalId){
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_5).path("physical").path(physicalId);
		MonitoringResourceDatasets cr = service.accept(MediaType.APPLICATION_XML)
		.get(MonitoringResourceDatasets.class);
		return cr;
	}
	// [16] QueryResource/group/complete/energy/{physicalId}
	public MonitoringResourceDatasets getLatestCompleteReportForEnergy(String physicalId){
		WebResource service = client.resource(this.getAddress(INTERFACE_3))
				.path(INTERFACE_5).path("energy").path(physicalId);
		MonitoringResourceDatasets cr = service.accept(MediaType.APPLICATION_XML)
		.get(MonitoringResourceDatasets.class);
		return cr;
	}
}
