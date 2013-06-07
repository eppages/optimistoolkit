/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import eu.optimis_project.monitoring.Measurement;
import eu.optimis_project.monitoring.MonitoringUtil;
import eu.optimis_project.monitoring.config.ConfigurationManager;

public class MySQLStorageManager implements StorageManager {

    private final Logger log = Logger.getLogger(MySQLStorageManager.class.getName());
    private final String username;
    private final String password;
    private final String url;
    private final String tableName;

    // TODO Replace this catch-all data column and the below indicies with
    // proper columns once measurements has been defined

    private static final String ENTRIES_COLUMNNAME_SERVICEID = "serviceid";
    private static final String ENTRIES_COLUMNNAME_INSTANCEID = "instanceid";
    private static final String ENTRIES_COLUMNNAME_NAME = "name";
    private static final String ENTRIES_COLUMNNAME_DATA = "data";
    private static final String ENTRIES_COLUMNNAME_TIMESTAMP = "timestamp";

    private static final int ENTRIES_COLUMNINDEX_SERVICEID = 1;
    private static final int ENTRIES_COLUMNINDEX_INSTANCEID = 2;
    private static final int ENTRIES_COLUMNINDEX_NAME = 3;
    private static final int ENTRIES_COLUMNINDEX_DATA = 4;
    private static final int ENTRIES_COLUMNINDEX_TIMESTAMP = 5;

    private static final String CONFIG_FILE_PATH = "file:///etc/optimis/modules/service-monitoring/";
    private static final String CONFIG_FILE_NAME = "mysql.properties";

    /*
     * Configuration parameters
     */
    private static final String MYSQL_CONF_HOST_KEY = "hostname";
    private static final String MYSQL_CONF_USERNAME_KEY = "username";
    private static final String MYSQL_CONF_PASSWORD_KEY = "password";
    private static final String MYSQL_CONF_DATABASE_KEY = "database";
    private static final String MYSQL_CONF_TABLE_KEY = "table";

    public MySQLStorageManager() throws ConfigurationException {
        // Set Database connection TODO
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database driver, aborting.", e);
        }

        ConfigurationManager mySQLConf = new ConfigurationManager(CONFIG_FILE_PATH, CONFIG_FILE_NAME);

        String host = mySQLConf.getConfig().getString(MYSQL_CONF_HOST_KEY);
        String database = mySQLConf.getConfig().getString(MYSQL_CONF_DATABASE_KEY);
        this.tableName = mySQLConf.getConfig().getString(MYSQL_CONF_TABLE_KEY);
        this.username = mySQLConf.getConfig().getString(MYSQL_CONF_USERNAME_KEY);
        this.password = mySQLConf.getConfig().getString(MYSQL_CONF_PASSWORD_KEY);

        this.url = "jdbc:mysql://" + MonitoringUtil.utf8Encode(host) + ":3306/"
                + MonitoringUtil.utf8Encode(database);

        try {
            createTable();
        } catch (SQLException e) {
            // FIXME Update this
            throw new RuntimeException(e);
        }

        log.info("Successfully started MySQL storage manager");
    }

    private void createTable() throws SQLException {
        final String createTable = "CREATE TABLE IF NOT EXISTS " + tableName + "("
                + ENTRIES_COLUMNNAME_SERVICEID + " VARCHAR(256) NOT NULL, " + ENTRIES_COLUMNNAME_INSTANCEID
                + " VARCHAR(256) NOT NULL, " + ENTRIES_COLUMNNAME_NAME
                + " VARCHAR(256) NOT NULL, " + ENTRIES_COLUMNNAME_DATA + " VARCHAR(256) NOT NULL, "
                + ENTRIES_COLUMNNAME_TIMESTAMP + " BIGINT NOT NULL, " + "PRIMARY KEY ("
                + ENTRIES_COLUMNNAME_SERVICEID + ", " + ENTRIES_COLUMNNAME_TIMESTAMP + ", "
                + ENTRIES_COLUMNNAME_NAME + ")" + ");";

        Statement statement = null;
        try {
            log.debug("Executing query: " + createTable);
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            statement.execute(createTable);
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                log.debug("Failed to close statement.", e);
            }
        }
    }

    private java.sql.Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Set<Measurement> getData(String serviceID) throws IOException {

        String query = "SELECT * FROM " + tableName + " WHERE " + ENTRIES_COLUMNNAME_SERVICEID + " = ?;";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, serviceID);

            log.info("Using statement: " + statement);

            ResultSet rs = statement.executeQuery();

            Set<Measurement> entryList = new HashSet<Measurement>();
            for (rs.beforeFirst(); rs.next();) {
                try {
                    String instanceID = rs.getString(ENTRIES_COLUMNINDEX_INSTANCEID);
                    String data = rs.getString(ENTRIES_COLUMNINDEX_DATA);
                    String name = rs.getString(ENTRIES_COLUMNINDEX_NAME);
                    long timestamp = rs.getLong(ENTRIES_COLUMNINDEX_TIMESTAMP);

                    entryList.add(new Measurement(serviceID, instanceID, name, data, timestamp));
                } catch (Exception e) {
                    throw new IOException("Failed to read measurement from database.", e);
                }
            }

            log.debug("Found " + entryList.size() + " measurements with parameters serviceID: " + serviceID);

            return entryList;

        } catch (SQLException e) {

            throw new IOException(e);
        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Set<Measurement> getAllData() throws IOException {

        String query = "SELECT * FROM " + tableName;

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            log.info("Using statement: " + statement);

            ResultSet rs = statement.executeQuery();

            Set<Measurement> entryList = new HashSet<Measurement>();
            for (rs.beforeFirst(); rs.next();) {
                try {
                    String serviceID = rs.getString(ENTRIES_COLUMNINDEX_SERVICEID);
                    String instanceID = rs.getString(ENTRIES_COLUMNINDEX_INSTANCEID);
                    String name = rs.getString(ENTRIES_COLUMNINDEX_NAME);
                    String data = rs.getString(ENTRIES_COLUMNINDEX_DATA);
                    long timestamp = rs.getLong(ENTRIES_COLUMNINDEX_TIMESTAMP);
                    entryList.add(new Measurement(serviceID, instanceID, name, data, timestamp));
                } catch (Exception e) {
                    throw new IOException("Failed to read measurement from database.", e);
                }
            }

            log.debug("Found " + entryList.size() + " measurements in database");
            return entryList;

        } catch (SQLException e) {

            throw new IOException(e);
        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public int removeData(String serviceID) throws IOException {

        final String query = "DELETE FROM " + tableName + " WHERE " + ENTRIES_COLUMNNAME_SERVICEID + " = ?;";
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(query);
                statement.setString(ENTRIES_COLUMNINDEX_SERVICEID, serviceID);
                int affectedRows = statement.executeUpdate();
                log.info("Removed " + affectedRows + " measurements for serviceID: " + serviceID);
                return affectedRows;
            } catch (SQLException e) {
                log.debug("Failed to remove measurements for serviceID: " + serviceID, e);
                throw new IOException(e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    log.warn("Failed to close statement.");
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }

    }

    @Override
    public int removeAllData() throws IOException {

        final String query = "DELETE FROM " + tableName;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(query);
                int affectedRows = statement.executeUpdate();
                log.info("Removed all " + affectedRows + " measurements.");
                return affectedRows;
            } catch (SQLException e) {
                log.debug("Failed to remove measurements", e);
                throw new IOException(e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    log.warn("Failed to close statement.");
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public boolean storeData(Measurement measurement) throws IOException {
        final String query = "INSERT INTO " + tableName + " VALUES (?,?,?,?)";

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(query);
                statement.setString(ENTRIES_COLUMNINDEX_SERVICEID, measurement.getServiceID());
                statement.setString(ENTRIES_COLUMNINDEX_NAME, measurement.getName());
                statement.setString(ENTRIES_COLUMNINDEX_DATA, measurement.getData());
                statement.setLong(ENTRIES_COLUMNINDEX_TIMESTAMP, measurement.getTimestamp());
                statement.execute();
            } catch (SQLException e) {
                log.debug("Failed to add measurement: " + measurement, e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    log.warn("Failed to close statement.");
                }
            }
        } catch (SQLException e) {
            throw new IOException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
            }
        }

        return true;
    }

    @Override
    public void shutdown() {

        log.debug("Shutting down MySQLStorageManager");
    }

    public void dropTable() throws SQLException {
        final String createTable = "DROP TABLE IF EXISTS " + tableName + ";";

        Statement statement = null;
        try {
            log.debug("Executing query: " + createTable);
            statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            statement.execute(createTable);
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                log.debug("Failed to close statement.", e);
            }
        }
    }

}
