/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.optimis.infrastructureproviderriskassessmenttool.core.configration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author scsmj
 */
public class ConfigManager {
    
    private static Logger log = Logger.getLogger(ConfigManager.class);
    public static final String OPTIMIS_CONFIG_FILE = "/etc/optimis.properties";
    public static final String IPRA_CONFIG_FILE = "/etc/InfrastructureProviderRiskAssessmentTool/ipraconfig.properties";
    public static final String LOG4J_CONFIG_FILE = "/etc/InfrastructureProviderRiskAssessmentTool/log4jIPRA.properties";
    
    public static PropertiesConfiguration getPropertiesConfiguration(String configFile) {
        String filePath = null;
        PropertiesConfiguration config = null;
        
        try {
            filePath = getConfigFilePath(configFile);
            config = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException ex) {
            log.error("IPRA: Error reading " + filePath + " configuration file: " + ex.getMessage());
            ex.printStackTrace();
        }

        return config;
    }
    
    public static String getConfigFilePath(String configFile) {
        String optimisHome = System.getenv("OPTIMIS_HOME");
        if(optimisHome == null) {
            optimisHome = "/opt/optimis";
            log.debug("IPRA: OPTIMIS_HOME: " + optimisHome + " (DEFAULT)");
        } else {
            log.debug("IPRA: OPTIMIS_HOME: " + optimisHome);
        }

        File fileObject = new File(optimisHome.concat(configFile));
        if (!fileObject.exists()) {
            try {
                createDefaultConfigFile(fileObject);
            } catch (Exception ex) {
                log.error("IPRA: Error reading " + optimisHome.concat(configFile) + " configuration file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        
        return optimisHome.concat(configFile);
    }

    private static void createDefaultConfigFile(File fileObject) throws Exception{
        log.info("IPRA: File " + fileObject.getAbsolutePath() + " didn't exist. Creating one with default values...");
        
        //Create parent directories.
        log.info("IPRA: Creating parent directories.");
        new File(fileObject.getParent()).mkdirs();

        //Create an empty file to copy the contents of the default file.
        log.info("IPRA: Creating empty file.");
        new File(fileObject.getAbsolutePath()).createNewFile();

        //Copy file.
        log.info("IPRA: Copying file " +  fileObject.getName());
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
            log.error("IPRA: Couldn't close input stream");
        }
        try {
            streamOut.close();
        } catch (IOException ignore) {
            log.error("IPRA: Couldn't close file output stream");
        }
    }
    
}
