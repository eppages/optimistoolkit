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

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
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

public class InfoProviderConfigTest extends TestCase {

	private String collectorId = "ColTestId";
	private String scriptRevPath = "/opt/collector_scripts/dummy_collector.sh";
	private static String AGGREGATOR_URL;
	public static String llocal = "extern1";
	//public static String llocal = "remote";

	private static Logger logger = Logger
			.getLogger(InfoProviderConfigTest.class);
	private static final String sourceClass = InfoProviderConfigTest.class
			.getName();

	public InfoProviderConfigTest(){
		try {
			ResourceBundle rb = ResourceBundle.getBundle("testaggregator");
			AGGREGATOR_URL = rb.getString("aggregator.url");
		} catch (MissingResourceException e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
	}

	public void testInfoProviderConfigBefore() {
		WebResource service = this.getAggregatorWebResource();
		String thResult = null;
		// start monitoring
		try {
			service.path("Aggregator").path("startmonitoring")
					.path(collectorId).type(MediaType.TEXT_PLAIN).post(
							String.class, null);
		} catch (UniformInterfaceException interfaceException) {
			interfaceException.printStackTrace();
			fail();
		} catch (Exception e) {
			logger.error(sourceClass + "- testPhysicalResourcePushPull");
			e.printStackTrace();
			fail();
		}
		// new thread
		
		String threadsCountBefore = service.path("Aggregator").path("threads").path(
				"total").type(MediaType.TEXT_PLAIN).get(String.class);
		//System.out.println("before:" + threadsCountBefore);

		try {
			service.path("Aggregator").path("stopmonitoring").path(collectorId)
					.type(MediaType.TEXT_PLAIN).post(String.class, null);
		} catch (UniformInterfaceException interfaceException) {
			interfaceException.printStackTrace();
			fail();
		} catch (Exception e) {
			logger.error(sourceClass + "");
			e.printStackTrace();
			fail();
		}
		String threadsCountAfter = service.path("Aggregator").path("threads").path(
				"total").type(MediaType.TEXT_PLAIN).get(String.class);
		//System.out.println("after:"+threadsCountAfter);
		int newThreadCount = Integer.valueOf(threadsCountBefore)-Integer.valueOf(threadsCountAfter);
		assertEquals(1, newThreadCount);

	}

//	public void testInfoProviderConfigAfter() {
//		WebResource service = this.getAggregatorWebResource();
//		try {
//			service.path("Aggregator").path("stopmonitoring").path(collectorId)
//					.type(MediaType.TEXT_PLAIN).post(String.class, null);
//		} catch (UniformInterfaceException interfaceException) {
//			interfaceException.printStackTrace();
//			fail();
//		} catch (Exception e) {
//			logger.error(sourceClass + "");
//			e.printStackTrace();
//			fail();
//		}
//		String threads2 = service.path("Aggregator").path("threads").path(
//				"total").type(MediaType.TEXT_PLAIN).get(String.class);
//		System.out.println("after:"+threads2);
//		assertEquals("0", threads2);
//	}

	private WebResource getAggregatorWebResource() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(AGGREGATOR_URL);
		return service;
	}

	private boolean deleteCols() {
		Connection conn = AggregatorDBUtil.getConnection(llocal);
		String query = null;
		// resource_type"
		query = "Delete FROM monitoring_information_collector where collector_id LIKE 'ColTest%'";

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
			;
		}
		return true;
	}

	private boolean addCols() {
		Connection conn = AggregatorDBUtil.getConnection(llocal);
		//System.out.println("DBURL:"+AggregatorDBUtil.getCurrentUrl());
		String query = null;
		// resource_type"
		String scriptpath = scriptRevPath;
		// java.sql.Timestamp current = new
		// java.sql.Timestamp(" CURRENT_TIMESTAMP");
		query = "Insert into monitoring_information_collector(name,connection_script_path,"
				+ "time_interval_in_ms, created_by, creation_date, collector_id)"
				+ " values('test',+ '"
				+ scriptpath
				+ "','50000', 'hlrs test',"
				+ "CURRENT_TIMESTAMP,'" + collectorId + "')";
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
				return false;
			}
			;
		}
		return true;
	}

	protected void setUp() throws Exception {
		// http://wagadugu/nagios/xml/status2xml_v2.cgi
		logger.setLevel(Level.ALL);
		PatternLayout layout = new PatternLayout("%d [%t] %-5p %m%n");
		ConsoleAppender cAppender = new ConsoleAppender(layout);
		logger.addAppender(cAppender);
		logger.addAppender(new ConsoleAppender());
		boolean b = addCols();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		deleteCols();
		super.tearDown();
	}
}
