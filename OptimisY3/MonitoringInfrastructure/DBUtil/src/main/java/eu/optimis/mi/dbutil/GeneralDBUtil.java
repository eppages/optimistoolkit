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

public class GeneralDBUtil {
	private static String DB_DRIVER;
	private static String DB_USER;
	private static String DB_PASSWORD;
	private static String TABLE_URL;

	/**
	 * 
	 * @param table_url
	 *            , db_user, db_password, db_driver
	 * @return
	 * @throws SQLException
	 */

	private static Connection conn;

	public GeneralDBUtil(String table_url, String db_user, String db_password,
			String db_driver) {
		init(table_url, db_user, db_password, db_driver);
	}

	private static void init(String table_url, String db_user,
			String db_password, String db_driver) {
		DB_DRIVER = db_driver;
		DB_USER = db_user;
		DB_PASSWORD = db_password;
		TABLE_URL = table_url;

		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(TABLE_URL, DB_USER, DB_PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println("Error: DB DRIVER, check you driver please.");
			throw new RuntimeException(e);
		} catch (SQLException e) {
			System.out.println("Cannot open a database connection.");
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection(String table_url,
			String db_user, String db_password, String db_driver) {
		try {
			if (conn == null || conn.isClosed()) {
				GeneralDBUtil util = new GeneralDBUtil(table_url, db_user,
						db_password, db_driver);
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
