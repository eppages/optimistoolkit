/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * 
 * Creates and populates configuration objects based on a system property
 * (configuration.file) or the default configuration file specified.
 * 
 * Initially written for use in the RESERVOIR project
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 */
public class ConfigurationManager {

	private final Logger log = Logger.getLogger(ConfigurationManager.class
			.getName());
	private Configuration configuration;

	/**
	 * Set the default configuration file to fall back on, in case the system
	 * property is not set or not working.
	 * 
	 */
	public ConfigurationManager(String defaultConfigFilePath,
			String defaultConfigFileName) throws ConfigurationException {
		loadConfigurationFile(defaultConfigFilePath, defaultConfigFileName);
	}

	/**
	 * Load configuration file as specified in the system property, and fall
	 * back on the default file if required.
	 */
	private void loadConfigurationFile(String defaultConfigFilePath,
			String defaultConfigFileName) throws ConfigurationException {
		String configFilePath = System.getProperty("configuration.file.path");
		String configFileName = System.getProperty("configuration.file.name");
		String filePath = configFilePath == null ? defaultConfigFilePath
				: configFilePath;
		String fileName = configFileName == null ? defaultConfigFileName
				: configFileName;

		URL configURL;

		if (!filePath.endsWith(File.separator)) {
			filePath += File.separatorChar;
		}

		// Check if the file exists on the system, else try reading it from the
		// classpath
		File configFile = new File(filePath + fileName);
		if (configFile.exists()) {
			try {
				configURL = new URL(filePath + fileName);
			} catch (MalformedURLException e) {
				throw new ConfigurationException(e);
			}
		} else {
			configURL = this.getClass().getClassLoader().getResource(fileName);
		}

		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration(configURL);
		} catch (ConfigurationException e) {
			if (defaultConfigFileName.equals(configFileName)
					&& configFilePath.equals(defaultConfigFilePath)) {
				log.fatal("Could not find default configuration file: '"
						+ configURL + "'", e);
				throw e;
			} else {
				// Try default file too
				try {
					config = new PropertiesConfiguration(defaultConfigFilePath);
				} catch (ConfigurationException e2) {
					log.warn("Could not find default file, trying same dir");
				}
				
				// Try file in same dir
				if (config == null) {
					String sameDirPath = System.getProperty("user.dir") + File.separatorChar + defaultConfigFileName;
					try {
						config = new PropertiesConfiguration(sameDirPath);
						log.info("Using file in same directory:" + sameDirPath);
					} catch (ConfigurationException e2) {
						log.fatal("Could not find specified "
								+ "configuration file: '" + configFilePath
								+ File.separatorChar + configFileName
								+ "', could not find default "
								+ "configuration file: '" + defaultConfigFilePath
								+ File.separatorChar + defaultConfigFileName
								+ "', and could not find file in same dir: "
								+ sameDirPath
								+ "'", e2);
						throw e2;
					}
				}
				
				

				log.warn("Could not find specified " + "configuration file: '"
						+ configFilePath + File.separatorChar + configFileName
						+ "', using default configuration at : '"
						+ defaultConfigFilePath + File.separatorChar
						+ defaultConfigFileName + "'");
			}
		}
		config.setThrowExceptionOnMissing(true);
		this.configuration = config;
	}

	/**
	 * Return a complete Configuration object, created during the
	 * "loadConfiguration" call.
	 */
	public Configuration getConfig() {
		return configuration;
	}
}
