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
import java.sql.SQLException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


import junit.framework.TestCase;

public class RealtimeDataTest extends TestCase {
	
	private static String AGGREGATOR_URL;
	private static final String RESOURCE_FILE = "testaggregator.properties";
	private static Logger logger = Logger.getLogger(RealtimeDataTest.class);

	private String DB_DRIVER;
	private String TABLE_URL;
	private String DB_USER;
	private String DB_PASSWORD;
	
	public RealtimeDataTest() {
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
	    
	
	    /**
	     * Test realtime data.
	     */
	    public void testGetRealtimeData() {
	    	// push a resource
	    	// getRealtime data
	    	// if the result is not null, then alert true
			WebResource service = this.getAggregatorWebResource();
			String result = null;
			try {
				String realtimeStr = service.path("Aggregator").path("realtime").path("physical").get(String.class);			
				System.out.println("realtime Str:"+realtimeStr);
				assertTrue(true);
				
			} catch (UniformInterfaceException interfaceException) {
				interfaceException.printStackTrace();
				fail();
			} catch (Exception e) {
				System.err.println(" error - testGetRealtimeData");
				e.printStackTrace();
				fail();
			}
	    	
	    }
	    
	    
	    private WebResource getAggregatorWebResource() {
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			WebResource service = client.resource(AGGREGATOR_URL);
			return service;
		}
	    
	    @Override
	    protected void setUp() throws Exception{
	    	super.setUp();
		}
	    
	    @Override
	    protected void tearDown() throws Exception {
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
