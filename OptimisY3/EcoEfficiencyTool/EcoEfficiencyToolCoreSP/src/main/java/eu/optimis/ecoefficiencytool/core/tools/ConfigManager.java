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
package eu.optimis.ecoefficiencytool.core.tools;

import java.io.*;
import java.util.logging.Level;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class ConfigManager {

    private static Logger log = Logger.getLogger(ConfigManager.class);
    public static final String OPTIMIS_CONFIG_FILE = "/etc/optimis.properties";
    public static final String ECO_CONFIG_FILE = "/etc/EcoEfficiencyToolSP/config.properties";
    public static final String LOG4J_CONFIG_FILE = "/etc/EcoEfficiencyToolSP/log4jECO.properties";
    public static final String SPDDBB_CONFIG_FILE = "/etc/EcoEfficiencyToolSP/hibernateSP.cfg.xml";
    
    public static PropertiesConfiguration getPropertiesConfiguration(String configFile) {
        String filePath = null;
        PropertiesConfiguration config = null;
        
        try {
            filePath = getConfigFilePath(configFile);
            config = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException ex) {
            log.error("ECO: Error reading " + filePath + " configuration file: " + ex.getMessage());
            ex.printStackTrace();
        }

        return config;
    }
    
    public static String getConfigFilePath(String configFile) {
        String optimisHome = System.getenv("OPTIMIS_HOME");
        if(optimisHome == null) {
            optimisHome = "/opt/optimis";
            log.debug("ECO: OPTIMIS_HOME: " + optimisHome + " (DEFAULT)");
        } else {
            log.debug("ECO: OPTIMIS_HOME: " + optimisHome);
        }

        File fileObject = new File(optimisHome.concat(configFile));
        if (!fileObject.exists()) {
            try {
                createDefaultConfigFile(fileObject);
            } catch (Exception ex) {
                log.error("ECO: Error reading " + optimisHome.concat(configFile) + " configuration file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        
        return optimisHome.concat(configFile);
    }

    private static void createDefaultConfigFile(File fileObject) throws Exception{
        log.info("ECO: File " + fileObject.getAbsolutePath() + " didn't exist. Creating one with default values...");
        
        //Create parent directories.
        log.info("ECO: Creating parent directories.");
        new File(fileObject.getParent()).mkdirs();

        //Create an empty file to copy the contents of the default file.
        log.info("ECO: Creating empty file.");
        new File(fileObject.getAbsolutePath()).createNewFile();

        //Copy file.
        log.info("ECO: Copying file " +  fileObject.getName());
        InputStream streamIn = ConfigManager.class.getResourceAsStream("/" + fileObject.getName());
        FileOutputStream streamOut = new FileOutputStream(fileObject.getAbsolutePath());
        byte[] buf = new byte[8192];
        while (true) {
            int length = streamIn.read(buf);
            if (length < 0) {
                break;
            }
            streamOut.write(buf, 0, length);
        }

        //Close streams after copying.
        try {
            streamIn.close();
        } catch (IOException ignore) {
            log.error("ECO: Couldn't close input stream");
        }
        try {
            streamOut.close();
        } catch (IOException ignore) {
            log.error("ECO: Couldn't close file output stream");
        }
    }
}
