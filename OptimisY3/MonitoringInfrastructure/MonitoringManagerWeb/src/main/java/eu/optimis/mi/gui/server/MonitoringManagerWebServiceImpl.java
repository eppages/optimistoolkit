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

package eu.optimis.mi.gui.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.optimis.mi.gui.client.MonitoringManagerWebService;
import eu.optimis.mi.gui.client.model.MonitoringResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@SuppressWarnings("serial")
public class MonitoringManagerWebServiceImpl extends RemoteServiceServlet
		implements MonitoringManagerWebService {
	private final static Logger logger = Logger.getLogger(MonitoringManagerWebServiceImpl.class);
	private static String MMANAGER_URL; 
	public MonitoringManagerWebServiceImpl() {
		logger.info("Monitoring Manager Web Service is ready.");
				
		try {
			PropertyConfigurator.configure(ConfigManager
					.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
			PropertiesConfiguration config = ConfigManager
					.getPropertiesConfiguration(ConfigManager.MMEB_CONFIG_FILE);
			MMANAGER_URL = config.getString("mmanager.url");
		} catch (IOException e) {
			logger.error("couldn't find the configuration file, using default url");
			e.printStackTrace();
			MMANAGER_URL = "http://localhost:8080/MonitoringManager/";
			//throw new RuntimeException(e);
		} catch (ConfigurationException e1) {
			logger.error("couldn't find the properties defined in the configuration file, using default url");
			e1.printStackTrace();
			MMANAGER_URL = "http://localhost:8080/MonitoringManager/";
			//throw new RuntimeException(e1);
		}
	}

	public List<MonitoringResource> getMonitoringResources(String level,
			String id) {
		String xml = new String("");
		String urlString;
		if (level.equals("service")) {
			//urlString = MMANAGER_URL + "QueryResources/group/complete/service/"+ id;
			urlString = MMANAGER_URL + "QueryResources/group/type/service/"+ id;
		} else if (level.equals("virtual")) {
			urlString = MMANAGER_URL + "QueryResources/group/complete/virtual/"
					+ id;
		} else if (level.equals("physical")) {
			urlString = MMANAGER_URL
					+ "QueryResources/group/complete/physical/" + id;
		} else if (level.equals("energy")) {
			urlString = MMANAGER_URL + "QueryResources/group/complete/energy/"
					+ id;
		} else {
			return new ArrayList<MonitoringResource>();
		}

		try {

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/XML");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String li;
			while ((li = br.readLine()) != null) {
				xml = xml.concat(li);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			logger.error("cannot get resource by url:"+ urlString);
			e.printStackTrace();

		} catch (IOException e) {
			logger.error("IO connection timeout");
			//GWT.log("IO connection timeout");
			e.printStackTrace();

		}

		XmlUtil util = new XmlUtil();
		List<MonitoringResource> list;
		if (xml != null && xml.contains("metric_name")) {
			list = util.getMonitoringRsModel(xml);
		} else {
			list = new ArrayList<MonitoringResource>();
		}
		logger.info("OK calling "+urlString);
		return list;
	}

	public List<MonitoringResource> getIdMetricDateListMonitoringResources(
			String id, String level, String metricName, String dfrom, String dto) {
		String xml = new String("");

		String urlString = MMANAGER_URL + "QueryResources/date/metric/"
				+ metricName + "/" + level + "/" + id + "/" + dfrom + "." + dto;

		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/XML");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			System.out.println("Output from Server... \n" + dfrom.toString());
			String li;
			while ((li = br.readLine()) != null) {
				xml = xml.concat(li);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			logger.debug("Could get resources from the URL:" + urlString);
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		XmlUtil util = new XmlUtil();
		List<MonitoringResource> list;
		if (xml != null && xml.contains("metric_name")) {
			list = util.getMonitoringRsModel(xml);
		} else {
			list = new ArrayList<MonitoringResource>();
		}
		return list;
	}

	public String getIdMetricDateStrMonitoringResources(String id,
			String level, String metricName, String dfrom, String dto) {
		String xml = new String("");

		String urlString = MMANAGER_URL + "QueryResources/date/metric/"
				+ metricName + "/" + level + "/" + id + "/" + dfrom + "." + dto;
		logger.debug("Metric monitoring resource URL:" + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/XML");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			logger.debug("Output from Server... \n" + dfrom.toString());
			String li;
			while ((li = br.readLine()) != null) {
				xml = xml.concat(li);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return xml;
	}

}
