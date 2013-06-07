/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
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
package es.bsc.servicess.ide;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Class to manage the IDE properties
 * @author Jorge Ejarque (Barcelona Supercomputing Center)
 *
 */
public class IDEProperties {
	public static final String LOG_LEVEL = "log.level";
	public static final String MAX_NUM_CORES = "default.max.num.cores";
	public static final String MAX_MEM = "default.max.memory";
	public static final String MAX_DISK = "default.max.disk";
	public static final String NUM_CORES = "default.num.cores";
	public static final String MEM = "default.memory";
	public static final String DISK = "default.disk";
	public static final int DEFAULT_MAX_NUM_CORES = 16;
	public static final long DEFAULT_MAX_MEM = 16000;
	public static final long DEFAULT_MAX_DISK = 100000;
	public static final int DEFAULT_NUM_CORES = 1;
	public static final long DEFAULT_MEM = 1024;
	public static final long DEFAULT_DISK = 10240;
	private PropertiesConfiguration config;

	/** Constructor
	 * @param pathToConfigFile Path top the ide properties file
	 * @throws ConfigurationException
	 */
	public IDEProperties(String pathToConfigFile)
			throws ConfigurationException {
		config = new PropertiesConfiguration(pathToConfigFile);
	}

	/** Constructor
	 * @param file Properties File
	 * @throws ConfigurationException
	 */
	public IDEProperties(File file) throws ConfigurationException {
		config = new PropertiesConfiguration(file);
	}

	/**
	 * Get the IDE log level
	 * @return log level (DEBUG,
	 */
	public String getLogLevel() {
		return config.getString(LOG_LEVEL, Logger.WARN);
	}

	/**
	 * Set the IDE log level
	 * @param logLevel
	 */
	public void setLogLevel(String logLevel) {
		config.setProperty(LOG_LEVEL, logLevel);
	}

	public int getMaxNumCores() {
		return config.getInt(MAX_NUM_CORES, DEFAULT_MAX_NUM_CORES);
	}
	
	public void setMaxNumCores(int maxNumCores) {
		config.setProperty(MAX_NUM_CORES, maxNumCores);
	}

	public long getMaxMemory() {
		return config.getLong(MAX_MEM, DEFAULT_MAX_MEM);
	}
	
	public void setMaxMemory(int maxMemory) {
		config.setProperty(MAX_MEM, maxMemory);
	}
	
	public long getMaxDisk() {
		return config.getLong(MAX_DISK, DEFAULT_MAX_DISK);
	}
	
	public void setMaxDisk(int maxDisk) {
		config.setProperty(MAX_DISK, maxDisk);
	}
	
	public int getDefaultNumCores() {
		return config.getInt(NUM_CORES, DEFAULT_NUM_CORES);
	}
	
	public void setDefaultNumCores(int defNumCores) {
		config.setProperty(NUM_CORES, defNumCores);
	}

	public long getDefaultMemory() {
		return config.getLong(MEM, DEFAULT_MEM);
	}
	
	public void setDefaultMemory(int defMemory) {
		config.setProperty(MEM, defMemory);
	}
	
	public long getDefaultDisk() {
		return config.getLong(DISK, DEFAULT_DISK);
	}
	
	public void setDefaultDisk(int defDisk) {
		config.setProperty(DISK, defDisk);
	}

	
}
