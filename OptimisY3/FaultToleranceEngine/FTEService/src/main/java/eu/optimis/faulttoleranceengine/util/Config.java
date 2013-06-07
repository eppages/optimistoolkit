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

package eu.optimis.faulttoleranceengine.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 *
 * @author mmacias
 */
public class Config {
    public static final String CONFIG_FILE_PATH = System.getenv("OPTIMIS_HOME") + "/etc/FaultToleranceEngine/config.properties";

    private static Properties properties = null;
    
    public static String getString(String key) {
        try {
            if(properties == null) {
                properties = new Properties();
                properties.load(new FileInputStream(CONFIG_FILE_PATH));
            }
            return properties.getProperty(key);
        } catch(Exception e) {
            throw new Error("Error loading " + CONFIG_FILE_PATH + " : "  +Log.getStackTrace(e));
        }
        
    }
    
}
