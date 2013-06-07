/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/
package eu.optimis.mi.monitoring_manager.util;

import java.io.*;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * ConfigManager provides the following functionalities 
 * - read a configuration file and return back its properties
 * - get the configuration file, if it doesn't exist, it will be created. 
 * 
 * @author Josep Subirats (josep.subirats@bsc.es)
 * @author Tinghe Wang (twang@hlrs.de)
 * 
 */


public class ConfigManager {

    private static Logger log = Logger.getLogger(ConfigManager.class);
    //public static final String OPTIMIS_CONFIG_FILE = "/etc/optimis.properties";
    public static final String MMANAGER_CONFIG_FILE = "/etc/MonitoringManager/mmanager.properties";
    public static final String LOG4J_CONFIG_FILE = "/etc/MonitoringManager/log4j.properties";
    
    //getting resource properties
    public static PropertiesConfiguration getPropertiesConfiguration(String configFile) throws ConfigurationException, IOException{
        String filePath = null;
        PropertiesConfiguration config = null;
        filePath = getConfigFilePath(configFile);
        config = new PropertiesConfiguration(filePath);
        return config;
    }
    
    public static String getConfigFilePath(String configFile) throws IOException{
        String optimisHome = System.getenv("OPTIMIS_HOME");
        if(optimisHome == null) {
            optimisHome = "/opt/optimis";
            log.warn("No environment variable OPTIMIS_HOME. Using default /opt/optimis.");
        }

        File fileObject = new File(optimisHome.concat(configFile));
        //If not exists, copy property files from the source code to %OPTIMIS_HOME%
        if (!fileObject.exists()) {
                createDefaultConfigFile(fileObject); 
        }
        return optimisHome.concat(configFile);
    }
    
    private static void createDefaultConfigFile(File fileObject) throws IOException{
        log.debug("File " + fileObject.getAbsolutePath() + " didn't exist. Creating one with default values...");
        
        //Create parent directories.
        log.debug("Creating parent directories.");
        new File(fileObject.getParent()).mkdirs();

        //Create an empty file to copy the contents of the default file.
        log.debug("Creating empty file.");
        new File(fileObject.getAbsolutePath()).createNewFile();

        //Copy file.
        log.debug("Copying file " +  fileObject.getName() + "into path: "+fileObject.getAbsolutePath());
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
            throw new IOException();
        }
        try {
            streamOut.close();
        } catch (IOException ex) {
            log.error("Couldn't close file output stream");
            log.error(ex.getMessage());
            throw new IOException();
        }
    }
}
