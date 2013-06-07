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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class GetMultipleIPsClient {

	private static String HOST = "localhost";
	private static int PORT = 8080;
	private static String PATH = "MonitoringManager/QueryResourcesHome/MultipleIPs";
	private static String dateformat = "yyyyMMddHHmmss";
	private Client client;
	private static HashMap<String, String> argsMap;
	private final static Logger logger = Logger
			.getLogger(GetMultipleIPsClient.class.getName());

	public GetMultipleIPsClient() {
		argsMap = new HashMap<String, String>();
		argsMap.put("getReportForAllVirtual", "0");
		argsMap.put("getReportForAllPhysical", "0");
		argsMap.put("getReportForAllEnergy", "0");

		argsMap.put("getReportForService", "1");
		argsMap.put("getReportForVirtual", "1");
		argsMap.put("getLatestReportForVirtual", "1");
		argsMap.put("getLatestReportForService", "1");

		argsMap.put("getReportForPhysical", "1");
		argsMap.put("getReportForEnergy", "1");
		argsMap.put("getLatestReportForPhysical", "1");
		argsMap.put("getLatestReportForEnergy", "1");

		argsMap.put("getReportForPartService", "2");
		argsMap.put("getReportForPartVirtual", "2");
		argsMap.put("getReportForPartPhysical", "2");
		argsMap.put("getReportForPartEnergy", "2");

		argsMap.put("getReportForPartServiceId", "3");
		argsMap.put("getReportForPartVirtualId", "3");
		argsMap.put("getReportForPartPhysicalId", "3");
		argsMap.put("getReportForPartEnergyId", "3");

		argsMap.put("getReportForMetric", "2");
		argsMap.put("getLatestReportForMetricName", "2");
		argsMap.put("getReportForPartMetricName", "4");
		
		argsMap.put("getLatestCompleteReportForService", "1");
		argsMap.put("getLatestCompleteReportForVirtual", "1");
		argsMap.put("getLatestCompleteReportForPhysical", "1");
		argsMap.put("getLatestCompleteReportForEnergy", "1");

		DefaultClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,	true);
		this.client = Client.create(config);	

		logger.info("Service " + this.getClass().getName());
		PatternLayout layout = new PatternLayout(
				"%d{ISO8601} %-5p %c{2} [%t,%M:%L] %m%n");
		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		logger.setLevel(Level.ALL);
	}

	public GetMultipleIPsClient(String host, int port) {
		this();
		HOST = host;
		PORT = port;
	}

	private String getAddress() {
		return "http://" + HOST + ":" + PORT + "/" + PATH;
	}

	public MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName, List<Object> args) {
		int size;
		if (args == null) {
			size = 0;
		} else
			size = args.size();

		if (!checkInterface(methodName, size))
			return null;

		if (args.size() == 0) { // [6] a, b, c getReportForAll*
			return getFromMultipleIPs(serviceId, methodName);
		} else if (args.size() == 1) {
			// [5][8] a, b, c, d getLatestReportFor* getReportFor*
			// [13] [14] [15] [16] getLatestCompleteReportFor*
			String param = (String) args.get(0);
			return getFromMultipleIPs(serviceId, methodName, param);
		} else if (args.size() == 2) {
            if (methodName.toLowerCase().contains("metric")) {
				// [9] getLatestReportForMetricName
				// [7] getReportForMetric
				String param1 = (String) args.get(0); // metric name
				String param2 = (String) args.get(1); // resource type
				return getFromMultipleIPs(serviceId, methodName, param1,
						param2);
			} else {
				// [10] a, b, c, d getReportForPart*
				Date from, to;
				try {
					from = (Date) args.get(0);
					to = (Date) args.get(1);
				} catch (Exception e) {
					logger.warn("Input parameters are not correct. Method name: "
							+ methodName + "with parameters: 1: "
							+ args.get(0).toString() + ", 2: "
							+ args.get(1).toString());
					return null;
				}
				return getFromMultipleIPs(serviceId, methodName, from, to);
			}
		} else if (args.size() == 3) {
			// [11] a, b, c, d getReportForpart*Id
			String id = (String) args.get(0);
			Date from;
			Date to;
			try {
				from = (Date) args.get(1);
				to = (Date) args.get(2);
			} catch (Exception e) {
				logger.warn("Input parameters are not correct. Method name: "
						+ methodName + "with parameters: 1: "
						+ args.get(0).toString() + ", 2: "
						+ args.get(1).toString() + ", 3: "
						+ args.get(2).toString());
				return null;
			}
			return getFromMultipleIPs(serviceId, methodName, id, from, to);

		} else if (args.size() == 4) {
			// [12] getReportForPartMetricName
			// metricName, resourceType, from, to
			String metricName = (String) args.get(0);
			String type = (String) args.get(1);
			Date from, to;
			try {
				from = (Date) args.get(2);
				to = (Date) args.get(3);
			} catch (Exception e) {
				logger.warn("Input parameters are not correct. Method name: "
						+ methodName + "with parameters: 1: "
						+ args.get(0).toString() + ", 2: "
						+ args.get(1).toString() + ", 3: "
						+ args.get(2).toString() + ", 4: "
						+ args.get(3).toString());
				return null;
			}
			return getFromMultipleIPs(serviceId, methodName, metricName,
					type, from, to);
		} else
			return null;
	}
	
	private MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName) {
		WebResource service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(".");
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName, String id) {
		String params = id;
		WebResource service = null;
				
		// [13] [14] [15] [16] getLatestCompleteReportFor*
		if (methodName.contains("getLatestCompleteReportFor")) {
			service = client.resource(this.getAddress())
			.path("complete").path(serviceId).path(methodName).path(params);
		} else {
			// [5][8] a, b, c, d getLatestReportFor* getReportFor*
			service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(params);
		}
		
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String params = sf + "." + st;
		WebResource service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(params);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private MonitoringResourceDatasets getFromMultipleIPs(
			String serviceId, String methodName, String metricName, String type) {
		String params = metricName + "." + type;
		WebResource service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(params);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName, String id, Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String params = id + "." + sf + "." + st;
		WebResource service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(params);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private MonitoringResourceDatasets getFromMultipleIPs(String serviceId,
			String methodName, String metricName, String resourceType,
			Date from, Date to) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		String sf = sdf.format(from);
		String st = sdf.format(to);
		String params = metricName + "." + resourceType + "." + sf + "." + st;

		WebResource service = client.resource(this.getAddress())
				.path(serviceId).path(methodName).path(params);
		MonitoringResourceDatasets dataSet = service
				.get(MonitoringResourceDatasets.class);
		return dataSet;
	}

	private boolean checkInterface(String name, int size) {
		if (argsMap.containsKey(name)
				&& (argsMap.get(name).equals(Integer.toString(size)))) {
			return true;
		} else
			return false;
	}
}
