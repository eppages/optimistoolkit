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

package eu.optimis.mi.aggregator.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.mi.dbutil.AggregatorDBUtil;

import junit.framework.TestCase;

public class AggregatorPushTest extends TestCase {
	private static String pID = "PhysicalUnitTestId";
	private static String vID = "VirtualUnitTestId";
	private String physicalResource;
	private String virtualResource;
	
	private static String AGGREGATOR_URL;
	private static String AGGREGATOR_REV_PATH = "Aggregator/monitoringresources";
	private HashMap<String, String> map = new HashMap<String, String>();
	public static String llocal = "extern1";

	private static Logger logger = Logger.getLogger(AggregatorPushTest.class);
	private static final String sourceClass = AggregatorPushTest.class
			.getName();

	public AggregatorPushTest(){
		try {
			ResourceBundle rb = ResourceBundle.getBundle("testaggregator");
			AGGREGATOR_URL = rb.getString("aggregator.url");
		} catch (MissingResourceException e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}
	public void testPhysicalResourcePush() {
		int count = 0;
		String query = "SELECT count(*) AS COUNT FROM monitoring_resource_physical WHERE physical_resource_id=" +
				"'PhysicalUnitTestId'";
		count = getResourceQueryCount(query);
		WebResource service = this.getAggregatorWebResource();
		String result = null;
		try {
			result = service.path(AGGREGATOR_REV_PATH).path(
					"PhysicalcollectorId").type(MediaType.APPLICATION_XML)
					.post(String.class, physicalResource);
			count = getResourceQueryCount(query);
			assertEquals(2, count);			
			
		} catch (UniformInterfaceException interfaceException) {
			interfaceException.printStackTrace();
			fail();
		} catch (Exception e) {
			logger.error(sourceClass + "- testPhysicalResourcePushPull");
			e.printStackTrace();
			fail();
		}
	}

	public void testVirtualResourcePush() {
		int count = 0;
		String query = "select count(*) AS COUNT FROM monitoring_resource_virtual where virtual_resource_id=" +
				"'VirtualUnitTestId'";
		count = getResourceQueryCount(query);
		WebResource service = this.getAggregatorWebResource();
		String result = null;
		try {
			result = service.path(AGGREGATOR_REV_PATH).path(
					"VirtualcollectorId").type(MediaType.APPLICATION_XML).post(
					String.class, virtualResource);
			count = getResourceQueryCount(query);
			assertEquals(2, count);
			
			
		} catch (UniformInterfaceException interfaceException) {
		} catch (Exception e) {
			logger.error(sourceClass + "- testVirtualResourcePushPull");
			e.printStackTrace();
			fail();
		}
	}

	private int getResourceQueryCount(String query) {
		Connection conn = AggregatorDBUtil.getConnection(llocal);
		int count = 0;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			count = rs.getInt("COUNT");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return count;
	}

	private WebResource getAggregatorWebResource() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(AGGREGATOR_URL);
		return service;
	}

	private boolean deleteResources() {
		Connection conn = AggregatorDBUtil.getConnection(llocal);
		String queryPhysical = null;
		String queryVirtual = null;
		queryPhysical = "Delete FROM  monitoring_resource_physical where physical_resource_id='"+pID+"'";
		queryVirtual = "Delete FROM  monitoring_resource_virtual where virtual_resource_id='" + vID + "'";
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(queryPhysical);
			st.executeUpdate(queryVirtual);

		} catch (SQLException e) {
			logger.error("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return true;
	}

	protected void setUp() throws Exception {
		String timestamp = DateFormatUtils.ISO_DATETIME_FORMAT.format(Calendar
				.getInstance().getTime());

		physicalResource = "<?xml version='1.0'?><MonitoringResources><monitoring_resource>"
				+ "<physical_resource_id>PhysicalUnitTestId</physical_resource_id>"
				+ "<metric_name>cpu_average_load</metric_name><metric_value>0.030,0.050,0.010</metric_value>"
				+ "<metric_unit></metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id>"
				+ "<virtual_resource_id></virtual_resource_id>"
				+ "<resource_type>physical</resource_type>"
				+ "<monitoring_information_collector_id>collector1</monitoring_information_collector_id>"
				+ "</monitoring_resource><monitoring_resource>"
				+ "<physical_resource_id>PhysicalUnitTestId</physical_resource_id>"
				+ "<metric_name>disk_free_space</metric_name><metric_value>182929</metric_value>"
				+ "<metric_unit></metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id><virtual_resource_id></virtual_resource_id>"
				+ "<resource_type>physical</resource_type>"
				+ "<monitoring_information_collector_id>c</monitoring_information_collector_id>"
				+ "</monitoring_resource></MonitoringResources>";

		virtualResource = "<?xml version='1.0'?><MonitoringResources><monitoring_resource>"
				+ "<physical_resource_id>Test1</physical_resource_id>"
				+ "<metric_name>mem_used</metric_name><metric_value>90</metric_value>"
				+ "<metric_unit>percent</metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id>"
				+ "<virtual_resource_id>VirtualUnitTestId</virtual_resource_id>"
				+ "<resource_type>virtual</resource_type>"
				+ "<monitoring_information_collector_id>collector2</monitoring_information_collector_id>"
				+ "</monitoring_resource><monitoring_resource>"
				+ "<physical_resource_id>Test2</physical_resource_id>"
				+ "<metric_name>cpu_user</metric_name><metric_value>0.04</metric_value>"
				+ "<metric_unit>percent</metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id><virtual_resource_id>VirtualUnitTestId</virtual_resource_id>"
				+ "<resource_type>virtual</resource_type>"
				+ "<monitoring_information_collector_id></monitoring_information_collector_id>"
				+ "</monitoring_resource>" + "</MonitoringResources>";
		deleteResources();
		logger.setLevel(Level.ALL);
		PatternLayout layout = new PatternLayout("%d [%t] %-5p %m%n");
		ConsoleAppender cAppender = new ConsoleAppender(layout);
		logger.addAppender(cAppender);

		logger.addAppender(new ConsoleAppender());
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// Delete new records from db.
		physicalResource = null;
		virtualResource = null;
		deleteResources();
		super.tearDown();
	}
}
