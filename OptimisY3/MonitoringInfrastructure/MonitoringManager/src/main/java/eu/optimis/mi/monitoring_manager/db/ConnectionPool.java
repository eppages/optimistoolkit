package eu.optimis.mi.monitoring_manager.db;

/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.*;

public class ConnectionPool {

  private ConnectionPool() {
  }

  public static ConnectionPool getInstance() {
    synchronized (mutex) {
      if (connectionPool == null) {
        connectionPool = new ConnectionPool();
      }
    }
    return connectionPool;
  }

  public synchronized Connection getConnection() throws Exception {
    Connection connection = null;
    connection = new ConnectionImpl();
    return connection;
  }

  public static Connection getFreeConnection() throws Exception {
    return getInstance().getConnection();
  }

  private static ConnectionPool connectionPool = null;
  private static Object mutex = new Object();
}

class ConnectionImpl implements Connection {
  public java.sql.Connection getDBConnection(String db_table_url, String db_driver, String db_username, String db_password) throws Exception {
    if ( (dbConnection == null) || dbConnection.isClosed()) {
             Class.forName(db_driver);
            dbConnection = (DriverManager.getConnection(db_table_url, db_username, db_password));
    }
    return dbConnection;

  }
  @Override
  public java.sql.Connection getDBConnection() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
  protected java.sql.Connection dbConnection = null;
} 
