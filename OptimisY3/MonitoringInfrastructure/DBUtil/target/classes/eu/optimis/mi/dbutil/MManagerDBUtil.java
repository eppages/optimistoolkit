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

package eu.optimis.mi.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MManagerDBUtil {
	private static String DB_DRIVER;
	private static String DB_USER;
	private static String DB_PASSWORD;
	private static String TABLE_URL;

	/**
	 * Retrieve the corresponding SLA document through a specified SLA ID.
	 * 
	 * @param
	 * @return
	 * @throws SQLException
	 */
	// service Id all related resources
	private static Connection conn;
	private static String location;

	private MManagerDBUtil() {
		init(location);
	}

	private static void init(String location) {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("db");
			if (location.equals("local")) {
				DB_DRIVER = rb.getString("db.driver");
				TABLE_URL = rb.getString("db.table.url");
				DB_USER = rb.getString("db.username");
				DB_PASSWORD = rb.getString("db.password");
			} else if (location.equals("remote")) {
				DB_DRIVER = rb.getString("optimis1.db.driver");
				TABLE_URL = rb.getString("optimis1.db.table.url");
				DB_USER = rb.getString("db.aggregator.username");
				DB_PASSWORD = rb.getString("db.aggregator.password");
			} else if (location.equals("extern1")) {
				DB_DRIVER = rb.getString("ex.optimis1.db.driver");
				TABLE_URL = rb.getString("ex.optimis1.db.table.url");
				DB_USER = rb.getString("ex.db.aggregator.username");
				DB_PASSWORD = rb.getString("ex.db.aggregator.password");
			} else {
				DB_DRIVER = rb.getString("umea.db.driver");
				TABLE_URL = rb.getString("umea.db.table.url");
				DB_USER = rb.getString("umea.db.aggregator.username");
				DB_PASSWORD = rb.getString("umea db.aggregator.password");
			}
		}

		catch (MissingResourceException e) {
			System.err.println("Error: cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
		try {

			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(TABLE_URL, DB_USER, DB_PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println("Error: DB DRIVER, check you driver please ");
			throw new RuntimeException(e);
		} catch (SQLException e) {
			System.out.println("Cannot open a database Conncetion");
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection(String lo) {
		location = lo;
		try {
			if (location == null || conn == null || conn.isClosed()) {
				MManagerDBUtil util = new MManagerDBUtil();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return conn;
	}

	public static String getCurrentUrl() {
		return TABLE_URL;
	}
}
