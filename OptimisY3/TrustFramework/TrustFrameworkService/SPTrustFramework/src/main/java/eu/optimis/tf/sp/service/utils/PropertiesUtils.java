/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {

	private static Logger log = LoggerFactory.getLogger("eu.optimis.tf.sp.service.utils.PropertiesUtils");

	private static final String OPTIMIS_CONFIG_FILE = "/etc/optimis.properties";
	private static final String TRUST_CONFIG_FILE = "/etc/sptf/config.properties";
	private static final String LOG4J_CONFIG_FILE = "/etc/sptf/log4jTRUST.properties";

	private static final String OPTIMIS_CONFIG_FILE_WIN = "\\etc\\optimis.properties";
	private static final String TRUST_CONFIG_FILE_WIN = "\\etc\\sptf\\config.properties";
	private static final String LOG4J_CONFIG_FILE_WIN = "\\etc\\sptf\\log4jTRUST.properties";

	public static String getProperty(String configFile, String property){
		PropertiesConfiguration configTrust = PropertiesUtils.getPropertiesConfiguration(configFile);
		return configTrust.getString(property);
	}
	
	public static PropertiesConfiguration getPropertiesConfiguration(
			String configFile) {
		String filePath = null;
		PropertiesConfiguration config = null;
		filePath = file4OS(configFile);
		try {
			config = new PropertiesConfiguration(filePath);
		} catch (ConfigurationException e) {
			log.error("TRUST: Error reading " + filePath + " configuration file: " + e.getMessage());
			e.printStackTrace();
		}
		return config;
	}

	public static String getConfigFilePath(String configFile) {
		String optimisHome = System.getenv("OPTIMIS_HOME");
		//log.info(optimisHome);
		if (optimisHome == null) {
			if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
				optimisHome = "/opt/optimis";
				log.debug("TRUST: OPTIMIS_HOME: " + optimisHome + " (DEFAULT)");
			} else {
				optimisHome = "d:\\opt\\optimis";
				log.debug("TRUST: OPTIMIS_HOME: " + optimisHome + " (DEFAULT)");
			}
		} else {
			log.debug("TRUST: OPTIMIS_HOME: " + optimisHome);
		}

		File fileObject = new File(optimisHome.concat(configFile));
		if (!fileObject.exists()) {
			try {
				createDefaultConfigFile(fileObject);
			} catch (Exception ex) {
				log.error("TRUST: Error reading "
						+ optimisHome.concat(configFile)
						+ " configuration file: " + ex.getMessage());
				ex.printStackTrace();
			}
		}	
		
		return optimisHome.concat(configFile);
	}

	private static void createDefaultConfigFile(File fileObject)
			throws Exception {
		log.info("TRUST: File " + fileObject.getAbsolutePath()
				+ " didn't exist. Creating one with default values...");

		// Create parent directories.
		log.info("TRUST: Creating parent directories.");
		new File(fileObject.getParent()).mkdirs();

		// Create an empty file to copy the contents of the default file.
		log.info("TRUST: Creating empty file.");
		new File(fileObject.getAbsolutePath()).createNewFile();

		// Copy file.
		log.info("TRUST: Copying file " + fileObject.getName());
		InputStream streamIn = PropertiesUtils.class.getResourceAsStream("/"
				+ fileObject.getName());
		FileOutputStream streamOut = new FileOutputStream(
				fileObject.getAbsolutePath());
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
			log.error("TRUST: Couldn't close input stream");
		}
		try {
			streamOut.close();
		} catch (IOException ignore) {
			log.error("TRUST: Couldn't close file output stream");
		}
	}

	private static String file4OS(String configFile) {
		if (configFile.equalsIgnoreCase("TRUST")) {
			if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
				return getConfigFilePath(TRUST_CONFIG_FILE);
			} else {
				return getConfigFilePath(TRUST_CONFIG_FILE_WIN);
			}
		}
		if (configFile.equalsIgnoreCase("LOG")) {
			if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
				return getConfigFilePath(LOG4J_CONFIG_FILE);
			} else {
				return getConfigFilePath(LOG4J_CONFIG_FILE_WIN);
			}
		} else {
			if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
				return getConfigFilePath(OPTIMIS_CONFIG_FILE);
			} else {
				return getConfigFilePath(OPTIMIS_CONFIG_FILE_WIN);
			}
		}
	}
	
	public static String getLogConfig()
	{
		if (System.getProperty("file.separator").equalsIgnoreCase("/")) {
			return getConfigFilePath(LOG4J_CONFIG_FILE);
		} 
		
		return getConfigFilePath(LOG4J_CONFIG_FILE_WIN);		
	}

}
