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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.mi.aggregator.queries.InsertQuery;
import eu.optimis.mi.aggregator.util.ConfParam;
import eu.optimis.mi.aggregator.util.XmlUtil;
import eu.optimis.mi.dbutil.AggregatorDBUtil;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class CollectorThread extends Thread {
	private int timeInterval;
	volatile boolean running = false;
	private String scriptPath;
	private String collectorId;
	private WebResource service;
	
	private int pushTotal;

	private final static Logger logger = Logger.getLogger(CollectorThread.class
			.getName());
	public CollectorThread() {
	}

	public CollectorThread(String collectorId, String scriptPath,
			int timeInterval) {
		this.collectorId = collectorId;
		this.scriptPath = scriptPath;
		this.timeInterval = timeInterval;
		service = this.getWebResource();
//		try {
//			/*PatternLayout layout = new PatternLayout(
//					"%d{ISO8601} %-5p [%t] %c: %m%n");
//			// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
//			// String current = sdf.format(Calendar.getInstance().getTime());
//			String filename = null;
//			if (location.equals("remote")) {
//				filename = "/root/HLRSlogs/" + "Thread" + collectorId + ".log";
//			} else {
//				filename = "C:/OptimisLogs/" + "Thread" + collectorId + ".log";
//			}
//			FileAppender fileAppender = new FileAppender(layout, filename,
//					false);
//			logger.addAppender(fileAppender);
//			// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
//			logger.setLevel(Level.DEBUG);*/
//		} catch (Exception ex) {
//			System.out.println(ex);
//		}

	}

	/**
	 * override the run method of a thread
	 */
	public void run() {
		
		while (running) {
			Runtime r = Runtime.getRuntime();
			Process p = null;
			String line = null;
			String physicalRes = "";
			try {
				// logger.debug("file Path:"+path);
				p = r.exec(this.scriptPath);
				p.waitFor();
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					physicalRes = physicalRes + line;
				}
				input.close();

			} catch (Exception e) {
				logger
						.error("FATAL: couldn't retrieve data from script file. scriptPath:"
								+ scriptPath);
				e.printStackTrace();
			}
			// String dummyRes = "<MonitoringResources><monitoring_resource>"
			// + "<metric_name>clock_speed-test</metric_name>"
			// + "<metric_timesstamp>1303296894</metric_timesstamp>"
			// + "<metric_unit>GHz</metric_unit>"
			// + "<metric_value>5</metric_value>"
			// +
			// "<monitoring_information_collector_id>nagios-100</monitoring_information_collector_id>"
			// + "<physical_resource_id>physical-01</physical_resource_id>"
			// + "<resource_type>physical</resource_type>"
			// + "<service_resource_id>uuidtest</service_resource_id>"
			// + "</monitoring_resource></MonitoringResources>";
			//logger.debug("resources:"+scriptPath+" "+physicalRes);
			if (physicalRes!=null && physicalRes.contains("<MonitoringResources>")&& physicalRes.contains("<metric_timestamp>")) {
				this.storeResource(physicalRes);
				pushTotal++;
			}
			else{
				logger.error("The raw resource data format is not correct: "+physicalRes);
			}
			try {
				Thread.sleep(timeInterval);
			} catch (InterruptedException e) {
			}
		
		}
	}

	public void startRunning() {
		running = true;
		logger.info("==========start monitoring(threadId:" + this.getId() + ")"
				+ "===========");
		logger.info("INFO: col-ID:" + collectorId + "; scriptPath:"
				+ scriptPath + "; timeInterval:" + timeInterval);
		this.start();
	}

	public void stopRunning() {
		running = false;
		logger.info("=======stop monitoring(col-ID:" + collectorId
				+ "; threadId:" + this.getId() + ")" + "========");
		logger.info("FINISH - Total pushed resources:"+pushTotal);
	}

	public WebResource getWebResource() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
		return service;
	}

	// Solution 2
	private void storeResource(String resource) {
		if (service == null) {
			service = this.getWebResource();
		}
		String result = null;
		XmlUtil ut = new XmlUtil();
		String objRecordXml = ut.getObjXml(resource);
		//String objRecordXml = resource;
		try {
			result = service.path("Aggregator/monitoringresources").path(
					collectorId).type(MediaType.APPLICATION_XML).post(
					String.class, new String(objRecordXml));
		} catch (UniformInterfaceException interfaceException) {
			logger
					.error("ERROR: by calling Aggregator/monitoringresoruces/{collecotorId} errorcode:"
							+ interfaceException.getResponse().getStatus());
			logger.error("DEBUG: Aggregator URI:"
					+ this.getBaseURI().toString());
			//logger.debug("DEBUG: orignial resource:" + resource);
		    logger.error("interfaceException",interfaceException);
		}catch (Exception e) {
			logger.error("exception",e);
		}
	}

	private static URI getBaseURI() {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("aggregator");
			String aggregatorUrl = rb.getString("aggregator.url");
			return UriBuilder.fromUri(aggregatorUrl).build();
		} catch (MissingResourceException e) {
			logger.error("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}

//	// Solution 3, str->obj->store, 
//	private void storeObj(String resource) {
//		XmlUtil xutil = new XmlUtil();
//		MonitoringResourceDatasets mds = xutil.getMRDXml(resource);
//		InsertQuery qr = new InsertQuery();
//		qr.insertResources(AggregatorDBUtil.getConnection(ConfParam.location), mds);
//	}

}
