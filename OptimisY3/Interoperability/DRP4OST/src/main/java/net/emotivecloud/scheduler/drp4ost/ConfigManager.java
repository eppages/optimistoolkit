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
package net.emotivecloud.scheduler.drp4ost;

import java.io.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class ConfigManager {

    private static final String PROJ_HOME_ENV = "DRP4OST_HOME";
    private static final String PROJ_HOME_DEFAULT = "/opt/drp4ost";
    public static final String DRP4OST_CONFIG_FILE = "/etc/drp4ost/config.properties";
    private static Logger log = Logger.getLogger(ConfigManager.class);
    private static String optimis_Home = System.getenv(PROJ_HOME_ENV);

    public static PropertiesConfiguration getPropertiesConfiguration(String configFile) {
        String filePath = null;
        PropertiesConfiguration config = null;

        try {
            filePath = getConfigFilePath(configFile);
            config = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException ex) {
            log.error("Error reading " + filePath + " configuration file: " + ex.getMessage());
            log.error(ex.getMessage());
        }

        return config;
    }

    public static String getConfigFilePath(String configFile) {
        String optimisHome = optimis_Home;
        if (optimisHome == null) {
            optimis_Home = System.getenv(PROJ_HOME_ENV);
            optimisHome = PROJ_HOME_DEFAULT;
            log.warn("Please set environment variable " + PROJ_HOME_ENV + ". Using default " + PROJ_HOME_DEFAULT + ".");
        }

        File fileObject = new File(optimisHome + configFile);
        if (!fileObject.exists()) {
            try {
                createDefaultConfigFile(fileObject);
            } catch (Exception ex) {
                log.error("Error reading " + optimisHome.concat(configFile) + " configuration file: " + ex.getMessage());
                log.error(ex.getMessage());
            }
        } else {
            System.out.println("File exists! :" + fileObject.getPath());
        }

        return optimisHome + configFile;
    }

    private static void createDefaultConfigFile(File fileObject) throws Exception {
        log.debug("File " + fileObject.getAbsolutePath() + " didn't exist. Creating one with default values...");
        System.out.println("File " + fileObject.getAbsolutePath() + " didn't exist. Creating one with default values...");

        //Create parent directories.
        log.debug("Creating parent directories.");
        new File(fileObject.getParent()).mkdirs();

        //Create an empty file to copy the contents of the default file.
        log.debug("Creating empty file.");
        new File(fileObject.getAbsolutePath()).createNewFile();

        //Copy file.
        log.debug("Copying file " + fileObject.getName());
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
        } catch (IOException ex) {
            log.error("Couldn't close input stream");
            log.error(ex.getMessage());
        }
        try {
            streamOut.close();
        } catch (IOException ex) {
            log.error("Couldn't close file output stream");
            log.error(ex.getMessage());
        }
    }
}