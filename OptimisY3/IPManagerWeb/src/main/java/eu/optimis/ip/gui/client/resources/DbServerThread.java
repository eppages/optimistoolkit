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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hsqldb.Server;

class DbServerThread {

    private static Server server = null;

    public DbServerThread(PropertiesConfiguration dbConfig) {
        if (server == null) {
            server = new Server();
            try {
                server.setLogWriter(new PrintWriter(new FileOutputStream(dbConfig.getString("logFile"), true)));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Accounting.class.getName()).log(Level.WARNING, "Log file not found. Using System.out");
                server.setLogWriter(new PrintWriter(System.out));
            }
            server.setSilent(false);
            server.setDatabaseName(0, dbConfig.getString("databaseName"));
            server.setDatabasePath(0, dbConfig.getString("databasePath"));
            server.setNoSystemExit(true);
        }
    }

    public boolean isOnline() {
        return server.getState() == org.hsqldb.server.ServerConstants.SERVER_STATE_ONLINE;
    }

    public boolean isShutdown() {
        return server.getState() == org.hsqldb.server.ServerConstants.SERVER_STATE_SHUTDOWN;
    }

    public void startServer() {
        server.start();
        while (!isOnline()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Accounting.class.getName()).log(Level.WARNING, "Error when Thread.sleep(): " + ex.getMessage());
            }
        }
    }

    public void stopServer() {
        server.shutdown();
        while (!isShutdown()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Accounting.class.getName()).log(Level.WARNING, "Error when Thread.sleep(): " + ex.getMessage());
            }
        }
    }
}
