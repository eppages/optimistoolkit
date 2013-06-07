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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Not used class at the moment, just used to recreate a database.
 * @author jsubirat
 */
public class DBManager {
    
    public static void main(String[] args) {
        try {
            Properties dbConfig = new Properties();
            dbConfig.load(DBManager.class.getResourceAsStream("/dbconfig.properties"));
            boolean parseError = false;
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("--reinstall")) {
                    Accounting db = new Accounting();
                    db.removeAndReinstall();
                    
                    db.close();
                    
                } else {
                    parseError = true;
                }
            } else {
                parseError = true;
            }
            
            if(parseError) {
                System.out.println("Arguments:");
                System.out.println("\t--reinstall   Removes the old database and creates new void tables");
            }
            
        } catch (Exception ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
