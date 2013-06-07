/**
 * Copyright (C) 2010-2011 Barcelona Supercomputing Center
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
 
 
package eu.optimis.vmmanager.util;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author mmacias
 */
public class Config {
    public static final String CONFIG_FILE_PATH = System.getenv("OPTIMIS_HOME") + "/etc/VMManager/config.properties";

    private static Properties properties = null;
    
    private static Properties getProperties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(new FileInputStream(CONFIG_FILE_PATH));
            }
            return properties;
        } catch(Exception e) {
            throw new Error("Error loading " + CONFIG_FILE_PATH + " : " + e.getMessage(), e);            
        }
    }
    public static String getString(String key) {
        return getProperties().getProperty(key);
    }
    
    public static boolean getBoolean(String key) {
        String prop = getProperties().getProperty(key);
        if(prop == null || !prop.equalsIgnoreCase("true")) {
            return false;
        } else {
            return true;
        }
    }
    
}
