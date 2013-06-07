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

package eu.optimis.ics.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import eu.optimis.ics.core.Constants;
// import org.apache.commons.lang.exception.NestableException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * A PropertiesReader class that reads ICS properties file located outside
 * the webapps directory.
 * @author Anthony Sulistio
 *
 */
public class PropertiesReader {

    /** Log4j logger instance. */
    private static Logger log = Logger.getLogger(PropertiesReader.class.getName());

    /**
     * Gets the properties configuration
     * @param configFile the properties file
     * @return an object
     */
    public static PropertiesConfiguration getPropertiesConfiguration(
            String configFile) {

        String filePath = null;
        PropertiesConfiguration config = null;

        try {
            filePath = getConfigFilePath(configFile);
            config = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException ex) {
            log.error("ics.core.util.PropertiesReader.getPropertiesConfiguration(): Error reading configuration file "
                    + filePath + ex.getMessage());
            //ex.printStackTrace();
        }

        return config;
    }

    /**
     * Gets the configuration file path
     * @param configFile  the properties file
     * @return configuration file path
     */
    public static String getConfigFilePath(String configFile) {
        String optimisHome = System.getenv("OPTIMIS_HOME");
        if (optimisHome == null) {
            optimisHome = Constants.OPTIMIS_HOME_DEFAULT;
            log.debug("ics.core.util.PropertiesReader.getConfigFilePath(): OPTIMIS_HOME_DEFAULT: "
                    + optimisHome);
        } else {
            log.debug("ics.core.util.PropertiesReader.getConfigFilePath(): OPTIMIS_HOME_CUSTOMIZED: "
                    + optimisHome);
        }

        File fileObject = new File(optimisHome.concat(configFile));
        if (!fileObject.exists()) {
            try {
                createDefaultConfigFile(fileObject);
            } catch (Exception ex) {
                log.error("ics.core.util.PropertiesReader.getConfigFilePath(): Error reading "
                        + optimisHome.concat(configFile)
                        + " configuration file: " + ex.getMessage());
                //ex.printStackTrace();
            }
        }

        return optimisHome.concat(configFile);
    }

    /**
     * Creates a default properties file if it doesn't exist
     * @param fileObject    properties file
     * @throws Exception  Unexcepted error occurs
     */
    private static void createDefaultConfigFile(File fileObject)
            throws Exception {

        log.info("ics.core.util.PropertiesReader.createDefaultConfigFile(): File "
                + fileObject.getAbsolutePath()
                + " doesn't exist. Creating one with default values...");

        // Create parent directories.
        log.info("ics.core.util.PropertiesReader.createDefaultConfigFile(): Creating parent directories.");
        new File(fileObject.getParent()).mkdirs();

        // Create an empty file to copy the contents of the default file.
        log.info("ics.core.util.PropertiesReader.createDefaultConfigFile(): Creating empty file.");
        new File(fileObject.getAbsolutePath()).createNewFile();

        // Copy file.
        log.info("ics.core.util.PropertiesReader.createDefaultConfigFile(): Copying file "
                + fileObject.getName());
        InputStream streamIn = PropertiesReader.class.getResourceAsStream("/"
                + fileObject.getName());
        FileOutputStream streamOut = new FileOutputStream(fileObject.getAbsolutePath());
        byte[] buf = new byte[8192];
        while (true) {
            int length = streamIn.read(buf);
            if (length < 0) {
                break;
            }
            streamOut.write(buf, 0, length);
        }

        // Close streams after copying.
        try {
            streamIn.close();
        } catch (IOException ignore) {
            log.error("ics.core.util.PropertiesReader.createDefaultConfigFile(): Couldn't close input stream");
        }

        try {
            streamOut.close();
        } catch (IOException ignore) {
            log.error("ics.core.util.PropertiesReader.createDefaultConfigFile(): Couldn't close file output stream");
        }
    }
}
