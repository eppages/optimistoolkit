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

package eu.optimis.aggregator.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import junit.framework.TestCase;

public class AggregatorPushTest extends TestCase {
	private static String pID = "AGGREPhysicalUnitTestId";
	private static String vID = "AGGREVirtualUnitTestId";
	private String physicalResource;
	private String virtualResource;

	private static final String RESOURCE_FILE = "testaggregator.properties";
	private static String AGGREGATOR_REV_PATH = "Aggregator/monitoringresources";

	private static Logger logger = Logger.getLogger(AggregatorPushTest.class);
	private static final String sourceClass = AggregatorPushTest.class
			.getName();
	private static String AGGREGATOR_URL;
	private static String DB_DRIVER;
	private static String TABLE_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	public AggregatorPushTest() {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(
					AggregatorPushTest.class.getClassLoader().getResource(
							RESOURCE_FILE));
			AGGREGATOR_URL = config.getString("aggregator.url");
			DB_DRIVER = config.getString("db.driver");
			TABLE_URL = config.getString("db.table.url");
			DB_USER = config.getString("db.username");
			DB_PASSWORD = config.getString("db.password");
			logger.info("table url:" + TABLE_URL);
		} catch (Exception e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}
	
	
	public void testPhysicalResourcePush() {
		deleteResources();
		int count = 0;
		String query = "SELECT count(*) AS COUNT FROM monitoring_resource_physical WHERE physical_resource_id like '"
			+"%UnitTest%"+"'";
		WebResource service = this.getAggregatorWebResource();
		String result = null;
		try {
			result = service.path(AGGREGATOR_REV_PATH).path("physical").type(MediaType.TEXT_PLAIN)
					.post(String.class, physicalResource);
			Thread.sleep(15000);
			count = getResourceQueryCount(query);
			logger.info(count);

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
		deleteResources();
		int count = 0;
		String query = "select count(*) AS COUNT FROM monitoring_resource_virtual where virtual_resource_id like '"
				+"%UnitTest%"+"'";
		WebResource service = this.getAggregatorWebResource();
		try {
			service.path(AGGREGATOR_REV_PATH).path("virtual").type(MediaType.TEXT_PLAIN).post(
					String.class, virtualResource);
			Thread.sleep(15000);
			count = getResourceQueryCount(query);
			logger.info(count);
		} catch (UniformInterfaceException interfaceException) {
		} catch (Exception e) {
			logger.error(sourceClass + "- testVirtualResourcePushPull");
			e.printStackTrace();
			fail();
		}
	}

	private int getResourceQueryCount(String query) {
		DBConnection dbconn = new DBConnection();
		Connection conn = dbconn.getConnection();
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
		DBConnection dbconn = new DBConnection();
		Connection conn = dbconn.getConnection();
		String queryPhysical = null;
		String queryVirtual = null;
		queryPhysical = "Delete FROM  monitoring_resource_physical where physical_resource_id like '"
				+ "%UnitTest%" + "'";
		queryVirtual = "Delete FROM  monitoring_resource_virtual where virtual_resource_id like '"
				+ "%UnitTest%" + "'";
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
		logger.info("setup");
		long ltimestamp = Calendar.getInstance().getTimeInMillis() / 1000;
		String timestamp = Long.toString(ltimestamp);

		physicalResource = "<?xml version='1.0'?><MonitoringResources><monitoring_resource>"
				+ "<physical_resource_id>AGGREPhysicalUnitTestId</physical_resource_id>"
				+ "<metric_name>cpu_average_load</metric_name><metric_value>0.030,0.050,0.010</metric_value>"
				+ "<metric_unit></metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id>"
				+ "<virtual_resource_id></virtual_resource_id>"
				+ "<resource_type>physical</resource_type>"
				+ "<monitoring_information_collector_id>collector1</monitoring_information_collector_id>"
				+ "</monitoring_resource><monitoring_resource>"
				+ "<physical_resource_id>AGGREPhysicalUnitTestId</physical_resource_id>"
				+ "<metric_name>disk_free_space</metric_name><metric_value>182929</metric_value>"
				+ "<metric_unit></metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id><virtual_resource_id></virtual_resource_id>"
				+ "<resource_type>physical</resource_type>"
				+ "<monitoring_information_collector_id>collector</monitoring_information_collector_id>"
				+ "</monitoring_resource></MonitoringResources>";

		virtualResource = "<?xml version='1.0'?><MonitoringResources><monitoring_resource>"
				+ "<physical_resource_id>Test1</physical_resource_id>"
				+ "<metric_name>mem_used</metric_name><metric_value>90</metric_value>"
				+ "<metric_unit>percent</metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id>"
				+ "<virtual_resource_id>AGGREVirtualUnitTestId</virtual_resource_id>"
				+ "<resource_type>virtual</resource_type>"
				+ "<monitoring_information_collector_id>collector2</monitoring_information_collector_id>"
				+ "</monitoring_resource><monitoring_resource>"
				+ "<physical_resource_id>Test2</physical_resource_id>"
				+ "<metric_name>cpu_user</metric_name><metric_value>0.04</metric_value>"
				+ "<metric_unit>percent</metric_unit><metric_timestamp>"
				+ timestamp
				+ "</metric_timestamp>"
				+ "<service_resource_id></service_resource_id><virtual_resource_id>AGGREVirtualUnitTestId</virtual_resource_id>"
				+ "<resource_type>virtual</resource_type>"
				+ "<monitoring_information_collector_id>collector</monitoring_information_collector_id>"
				+ "</monitoring_resource>" + "</MonitoringResources>";
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// Delete new records from db.
		logger.info("teardown");
		physicalResource = null;
		virtualResource = null;
		deleteResources();
		super.tearDown();
	}
	
	class DBConnection{
		Connection connection;
		public DBConnection(){
			getDBConnection();
		}
		public Connection getConnection(){
			return connection;
		}
		private Connection getDBConnection(){
			try {
				Class.forName(DB_DRIVER);
				connection = DriverManager.getConnection(TABLE_URL, DB_USER,
						DB_PASSWORD);
			} catch (ClassNotFoundException e) {
				System.out
						.println("Error: DB DRIVER, check you driver please.");
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Cannot open a database connection.");
				throw new RuntimeException(e);
			}
			return connection;
		}
		
	}
	
}
