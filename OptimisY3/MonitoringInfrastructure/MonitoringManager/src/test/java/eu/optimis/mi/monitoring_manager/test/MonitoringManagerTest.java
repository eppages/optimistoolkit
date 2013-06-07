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

package eu.optimis.mi.monitoring_manager.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import junit.framework.TestCase;

public class MonitoringManagerTest extends TestCase{
	private static final String RESOURCE_FILE = "testmmanager.properties";
	private static final String sourceClass = MonitoringManagerTest.class.getName();
	private static Logger logger = Logger.getLogger(MonitoringManagerTest.class);
	
	private static String pID = "PhysicalUnitTestId";
	private static String vID = "VirtualUnitTestId";
	private String physicalResource;
	private String virtualResource;
	private static String MMANAGER_PATH_PHYSICAL;
	private static String MMANAGER_PATH_VIRTUAL;
	private static String AGGREGATOR_URL;
	private static String MMANAGER_URL;
	private static String AGGREGATOR_REV_PATH = "Aggregator/monitoringresources";
	private String DB_DRIVER;
	private String TABLE_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	
	public MonitoringManagerTest(){
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(
					MonitoringManagerTest.class.getClassLoader().getResource(
							RESOURCE_FILE));
			AGGREGATOR_URL = config.getString("aggregator.url");
			MMANAGER_URL = config.getString("mmanager.url");
			DB_DRIVER = config.getString("db.driver");
			TABLE_URL = config.getString("db.table.url");
			DB_USER = config.getString("db.username");
			DB_PASSWORD = config.getString("db.password");
		} catch (Exception e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
		//{from}.{to}
		MMANAGER_PATH_PHYSICAL= "QueryResources/date/type/physical";
		MMANAGER_PATH_VIRTUAL= "QueryResources/date/type/virtual";

	}
	public void testPhysicalResourcePushPull(){
		deleteResources("physical");
		WebResource	service = this.getAggregatorWebResource();
		try {
			service.path(AGGREGATOR_REV_PATH).path(
					"physical").type(MediaType.TEXT_PLAIN).post(
					String.class,physicalResource);
			Thread.sleep(15000);	
			//date/type/{resourceType}/{resourceId}/{from}.{to} yyyyMMddHHmmss
			WebResource wservice = Client.create().resource(MMANAGER_URL).path(MMANAGER_PATH_PHYSICAL).
			path(pID).path("20121230000000.20130112030201");
			MonitoringResourceDatasets dataSet = wservice.get(MonitoringResourceDatasets.class);
			logger.info(dataSet.getMonitoring_resource().size()); 
			
		} catch (UniformInterfaceException interfaceException) {
			interfaceException.printStackTrace();
			fail();
		}catch (Exception e) {
			logger.error(sourceClass +"- testPhysicalResourcePushPull");
			e.printStackTrace();
			fail();
		}
		assertTrue(true);
	}
	public void testVirtualResourcePushPull(){
		deleteResources("vitual");
		WebResource	service = this.getAggregatorWebResource();
		try {
			service.path(AGGREGATOR_REV_PATH).path(
					"virtual").type(MediaType.TEXT_PLAIN).post(
					String.class,virtualResource);
			Thread.sleep(15000);
			WebResource wservice = Client.create().resource(MMANAGER_URL).path(MMANAGER_PATH_VIRTUAL).
			path(vID).path("20121230000000.20130112030201");
			MonitoringResourceDatasets dataSet = wservice.get(MonitoringResourceDatasets.class);
			logger.info(dataSet.getMonitoring_resource().size()); 
			
		} catch (UniformInterfaceException interfaceException) {
		}catch (Exception e) {
			logger.error(sourceClass +"- testVirtualResourcePushPull");
			e.printStackTrace();
			fail();
		}
	}
	
	private WebResource getAggregatorWebResource() {
		ClientConfig config = new DefaultClientConfig();
		 config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, "true");
		Client client = Client.create(config);
		WebResource service = client.resource(AGGREGATOR_URL);
		return service;
	}
	
	private boolean deleteResources(String type) {
		DBConnection dbconn = new DBConnection();
		Connection conn = dbconn.getConnection();
		
		String query;
		if (type.equals("physical"))
		query = "Delete FROM monitoring_resource_physical where physical_resource_id like '%UnitTest%'";
		else
		query = "Delete FROM monitoring_resource_virtual where virtual_resource_id like '%UnitTest%'";
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(query);

		} catch (SQLException e) {
			logger.error("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
	protected void setUp() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 0, 01, 01, 02, 03);
		long ltimestamp = cal.getTimeInMillis()/1000;
		String timestamp = Long.toString(ltimestamp);
		System.out.println(timestamp);
		physicalResource = 
		"<?xml version='1.0'?><MonitoringResources><monitoring_resource>" +
		"<physical_resource_id>PhysicalUnitTestId</physical_resource_id>" +
		"<metric_name>cpu_average_load</metric_name><metric_value>0.030,0.050,0.010</metric_value>" +
		"<metric_unit></metric_unit><metric_timestamp>" +
		timestamp+
		"</metric_timestamp>" +
		"<service_resource_id></service_resource_id>" +
		"<virtual_resource_id></virtual_resource_id>" +
		"<resource_type>physical</resource_type>" +
		"<monitoring_information_collector_id></monitoring_information_collector_id>"+
		"</monitoring_resource><monitoring_resource>" +
		"<physical_resource_id>PhysicalUnitTestId</physical_resource_id>" +
		"<metric_name>disk_free_space</metric_name><metric_value>182929</metric_value>" +
		"<metric_unit></metric_unit><metric_timestamp>" +
		timestamp+
		"</metric_timestamp>" +
		"<service_resource_id></service_resource_id><virtual_resource_id></virtual_resource_id>" +
		"<resource_type>physical</resource_type>" +
		"<monitoring_information_collector_id></monitoring_information_collector_id>"+
		"</monitoring_resource></MonitoringResources>";

		virtualResource = 
			"<?xml version='1.0'?><MonitoringResources><monitoring_resource>" +
			"<physical_resource_id>T</physical_resource_id>" +
			"<metric_name>mem_used</metric_name><metric_value>90</metric_value>" +
			"<metric_unit>percent</metric_unit><metric_timestamp>" +
			timestamp+
			"</metric_timestamp>" +
			"<service_resource_id></service_resource_id>" +
			"<virtual_resource_id>VirtualUnitTestId</virtual_resource_id>" +
			"<resource_type>virtual</resource_type>" +
			"<monitoring_information_collector_id></monitoring_information_collector_id>"+
			"</monitoring_resource><monitoring_resource>" +
			"<physical_resource_id>T</physical_resource_id>" +
			"<metric_name>cpu_user</metric_name><metric_value>0.04</metric_value>" +
			"<metric_unit>percent</metric_unit><metric_timestamp>" +
			timestamp +
			"</metric_timestamp>" +
			"<service_resource_id></service_resource_id><virtual_resource_id>VirtualUnitTestId</virtual_resource_id>" +
			"<resource_type>virtual</resource_type>" +
			"<monitoring_information_collector_id>test</monitoring_information_collector_id>"+
			"</monitoring_resource></MonitoringResources>";
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		// Delete new records from db.
		physicalResource = null;
		virtualResource = null;
		deleteResources("physical");
		deleteResources("virtual");
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
