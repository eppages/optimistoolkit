/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ip.gui.client.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hsqldb.cmdline.SqlFile;


public class Accounting {
    
    private static DbServerThread serverThread;
    private Connection connection = null;   
    private PropertiesConfiguration dbconfig;

    /**
     * If DB server is not running, instantiates it.
     * 
     * @param dbConfig Configuration files
     * @throws SQLException 
     */
    public Accounting() throws SQLException {
        dbconfig = ConfigManager.getPropertiesConfiguration(ConfigManager.DATABASE_CONFIG_FILE);
        serverThread = new DbServerThread(dbconfig);
    }
    
    /**
     * If DB server is not running, runs it.
     * If Database tables are not created, it creates them.
     * Also prepares all the SQL statements.
     */
    private void start() {
        serverThread.startServer();
        try {
            Class.forName(dbconfig.getString("driver"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();            
            throw new RuntimeException(ex.getMessage(), ex);
        }        
        
        // If SA user has no password (because the DB does not exist), it creates the SA user and a read-only-user
        try {
            Connection saConnection = DriverManager.getConnection(dbconfig.getString("url"), dbconfig.getString("sa.username"), "");
            PreparedStatement ps = saConnection.prepareStatement("SET PASSWORD '" + dbconfig.getProperty("sa.password") +"';");
            ps.executeUpdate();
            ps.close();
            saConnection.close();
        } catch(SQLInvalidAuthorizationSpecException ex) {
            Logger.getLogger(Accounting.class.getName()).log(Level.INFO, "SA user has already a password. Neither new users are creater nor permissions are re-assigned");
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        
        try {
            connection = DriverManager.getConnection(
                    dbconfig.getString("url"),
                    dbconfig.getString("sa.username"),
                    dbconfig.getString("sa.password"));
            createDB();
            
        } catch (SQLException ex) {
            Logger.getLogger(Accounting.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Drops already defined RO (read-only) user, and creates a new with the password specified in scheduler.properties
        try {          
            PreparedStatement ps = connection.prepareStatement("DROP USER " + dbconfig.getProperty("ro.username") +";");
            ps.executeUpdate();
            ps.close();
        } catch(SQLInvalidAuthorizationSpecException ex) {
            Logger.getLogger(Accounting.class.getName()).log(Level.INFO, "Don't dropping read-only user because does not exist. Creating...");
        } catch(SQLException ex) {
            Logger.getLogger(Accounting.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }     
    }
    
    /**
     * Drops all the tables and creates them newly. WARNING: that will erase all the data in the databas.
     */
    protected void removeAndReinstall() {
        Logger.getLogger(Accounting.class.getName()).log(Level.INFO, "Reinstalling Database");
        start();
        dropDB();
        createDB();
        close();
    }
    private void dropDB() {      
        try {            
            InputStream sqlStream = getClass().getResourceAsStream("removeDB.sql");
            File f = File.createTempFile("sql"+System.currentTimeMillis(), ".sql");            
            FileOutputStream fos = new FileOutputStream(f);
            
            byte[] data = new byte[1024];
            int read = -1;
            do {
                read = sqlStream.read(data);
                if(read > 0) {
                    fos.write(data, 0, read);
                }
            } while( read >= 0);
            
            
            SqlFile sf = new SqlFile(f);
            sf.setConnection(connection);
            Logger.getLogger(Accounting.class.getName()).log(Level.INFO, "Dropping all tables");
            sf.execute();
            f.delete();

        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }          
    }
    private void createDB() {   
        try {            
            InputStream sqlStream = getClass().getResourceAsStream("createDB.sql");
            File f = File.createTempFile("sql"+System.currentTimeMillis(), ".sql");            
            FileOutputStream fos = new FileOutputStream(f);
            
            byte[] data = new byte[1024];
            int read = -1;
            do {
                read = sqlStream.read(data);
                if(read > 0) {
                    fos.write(data, 0, read);
                }
            } while( read >= 0);
            
            
            SqlFile sf = new SqlFile(f);
            sf.setConnection(connection);
            sf.execute();
            f.delete();
            
            connection.commit();
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }     
    }

    /**
     * Closes all the connections and stops the database server.
     */
    public void close() {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Accounting.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            serverThread.stopServer();
        }
    }
    
    
    public void addUser(String name, String password) {
        try {
            start();
            PreparedStatement p = connection.prepareStatement("INSERT INTO Users (userName, password) VALUES (?, ?);");
            p.setString(1, name);
            p.setString(2, password);
            p.executeUpdate();
            connection.commit();
            p.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage(),ex);
        } finally {
            close();
        }
    }
    
    public boolean doesUserExist(String user) {
        boolean isContained = false;
        try {
            start();
            PreparedStatement p = connection.prepareStatement("SELECT * FROM Users WHERE userName=?;");
            p.setString(1, user);
            ResultSet rs = p.executeQuery();
            if(rs.next()) {
                isContained = true;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return isContained;
    }
    
    public String getUserPassword(String user) {
        String ret = null;
        try {
            start();
            PreparedStatement p = connection.prepareStatement("SELECT * FROM Users WHERE userName=?;");
            p.setString(1, user);
            ResultSet rs = p.executeQuery();
            if(rs.next()) {
                ret = rs.getString(2);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }
}
