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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author mmacias
 */
public class Log {
    public static final String LOG4J_CONFIG_FILE_PATH = System.getenv("OPTIMIS_HOME") + "/etc/FaultToleranceEngine/log4j.properties";
    
    private static Map<String,Logger> logs = new HashMap<String, Logger>();   
    
    public static Logger getLogger() {        
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String className = trace[2].getClassName();
        Logger logger = logs.get(className);
        if(logger == null) {
            PropertyConfigurator.configure(LOG4J_CONFIG_FILE_PATH);        
            logger = Logger.getLogger(className);
            logs.put(className, logger);
        }
        return logger;
    }
    
    public static String getStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
