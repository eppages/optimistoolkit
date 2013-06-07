/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

public class DBUtil {

    private static String DB_DRIVER;
    private static String URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    private static final String CONFIG_FILE_PATH = System.getenv("OPTIMIS_HOME") + "/etc/CloudOptimizer/config.properties";  
    private static Properties properties = null;
    
    private static void loadProperties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(new FileInputStream(CONFIG_FILE_PATH));
            }
        } catch (IOException ex) {
            throw new Error(ex.getMessage(), ex);
        }
    }
    public static Connection getConnection() {
        Connection connection = null;
        try {            
            loadProperties();
            String location = properties.getProperty("db.location"); //database location
            DB_DRIVER = properties.getProperty("db.driver");
            DB_USER = properties.getProperty("db.username");
            DB_PASSWORD = properties.getProperty("db.password");
            URL = properties.getProperty(location + ".url");

            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

            return connection;
        } catch (ClassNotFoundException e) {
            throw new Error(e.getMessage(), e);
        } catch (SQLException e) {
            throw new Error(e.getMessage(), e);
        }
    }

    public static Connection getTRECConnection() {
        Connection connection = null;
        try {
            loadProperties();
            String location = properties.getProperty("db.location"); //database location
            DB_DRIVER = properties.getProperty("db.driver");
            DB_USER = properties.getProperty("db.username");
            DB_PASSWORD = properties.getProperty("db.password");
            URL = properties.getProperty(location + ".url_trec");

            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);

            return connection;
        } catch (ClassNotFoundException e) {
            throw new Error(e.getMessage(), e);
        } catch (SQLException e) {            
            throw new Error(e.getMessage(), e);
        }
    }

}
