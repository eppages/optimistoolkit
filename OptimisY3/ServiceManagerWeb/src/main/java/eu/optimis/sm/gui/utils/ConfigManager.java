/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class ConfigManager {

    private static Logger log = Logger.getLogger(ConfigManager.class);
    private static String optimis_Home = System.getenv("OPTIMIS_HOME");
    public static final String SMWEB_CONFIG_FILE = "/etc/ServiceManagerWeb/config.properties";
    public static final String LOG4J_CONFIG_FILE = "/etc/ServiceManagerWeb/log4jSMMW.properties";
    
    public static PropertiesConfiguration getPropertiesConfiguration(String configFile) {
        String filePath = null;
        PropertiesConfiguration config = null;

        try {
            filePath = getFilePath(configFile);
            config = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException ex) {
            log.error("Error reading " + filePath + " configuration file: " + ex.getMessage());
            log.error(ex.getMessage());
        }
        return config;
    }

    public static String getFilePath(String configFile) {
        String optimisHome = optimis_Home;
        if (optimisHome == null) {
            optimis_Home = System.getenv("OPTIMIS_HOME");
            optimisHome = "/opt/optimis";
            log.warn("Please set environment variable OPTIMIS_HOME. Using default /opt/optimis");
        }

        File fileObject = new File(optimisHome.concat(configFile));
            try {
                createDefaultConfigFile(fileObject);
            } catch (Exception ex) {
                log.error("Error reading " + optimisHome.concat(configFile) + " configuration file: " + ex.getMessage());
                log.error(ex.getMessage());
            }
        return optimisHome.concat(configFile);
    }

    private static void createDefaultConfigFile(File fileObject) throws Exception {

        new File(fileObject.getParent()).mkdirs();
        new File(fileObject.getAbsolutePath()).delete();
        new File(fileObject.getAbsolutePath()).createNewFile();

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
