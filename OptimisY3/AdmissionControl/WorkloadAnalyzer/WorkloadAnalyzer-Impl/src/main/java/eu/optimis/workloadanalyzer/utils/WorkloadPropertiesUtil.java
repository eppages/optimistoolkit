/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.workloadanalyzer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author hrasheed
 * 
 */
public class WorkloadPropertiesUtil {
	
	private static final Logger logger = Logger.getLogger(WorkloadPropertiesUtil.class);

	private Properties properties;
	private String fileName;

	public WorkloadPropertiesUtil(String workloadPropertiesFile) throws IOException {
		fileName = workloadPropertiesFile;
		properties = getProperties(workloadPropertiesFile);
	}

	public WorkloadPropertiesUtil() throws IOException {
		String workloadPropertiesFile = WorkloadPropertiesUtil.class.getResource("/workload-analyzer.properties").getPath();
		fileName = workloadPropertiesFile;
		properties = getProperties(workloadPropertiesFile);
	}
	
	private Properties getProperties(String workloadPropertiesFile) throws IOException {
		Properties properties = new Properties();
		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(workloadPropertiesFile));
			properties.load(fis);
			logger.debug("WorkloadAnalyzer Properties loaded and set.");
		} catch (Exception e) {
			String message = "WorkloadAnalyzer properties could not be loaded from " + workloadPropertiesFile;
			logger.error(message);
			throw new IOException(message, e);
		} finally {
			fis.close();
		}
		return properties;
	}
	
	public String getWorkloadAnalyzerHost() throws PropertyMissingException {
		String host = properties.getProperty(WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_HOST);
		if (host == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_HOST + " missing in " + fileName);
		}
		return host;
	}

	public String getWorkloadAnalyzerPort() throws PropertyMissingException, MalformedURLException {
		String port = properties.getProperty(WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_PORT);
		if (port == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_PORT + " missing in " + fileName);
		}
		return port;
	}
	
	public String getWorkloadAnalyzerURL() throws PropertyMissingException, MalformedURLException {
		String workloadURL = properties.getProperty(WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_URL);
		if (workloadURL == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_URL + " missing in " + fileName);
		}
        checkUrlValidity(workloadURL);
		return workloadURL;
	}
	
	public String getWorkloadAnalyzerURLPath() throws PropertyMissingException, MalformedURLException {
		String path = properties.getProperty(WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_URL_PATH);
		if (path == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_URL_PATH + " missing in " + fileName);
		}
		return path;
	}
	
	public String getWorkloadAnalzerMonitoring() throws PropertyMissingException, MalformedURLException {
		String workloadMonitoring = properties.getProperty(WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_MONITORING);
		if (workloadMonitoring == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.WORKLOAD_ANALYZER_MONITORING + " missing in " + fileName);
		}
		return workloadMonitoring;
	}
	
	public String getMonitoringHost() throws PropertyMissingException {
		String host = properties.getProperty(WorkloadAnalyzerConstants.MONITORING_HOST);
		if (host == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.MONITORING_HOST + " missing in " + fileName);
		}
		return host;
	}

	public String getMonitoringPort() throws PropertyMissingException, MalformedURLException {
		String port = properties.getProperty(WorkloadAnalyzerConstants.MONITORING_PORT);
		if (port == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.MONITORING_PORT + " missing in " + fileName);
		}
		return port;
	}
	
	public String getMonitoringURL() throws PropertyMissingException, MalformedURLException {
		String monitorURL = properties.getProperty(WorkloadAnalyzerConstants.MONITORING_MANGER_URL);
		if (monitorURL == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.MONITORING_MANGER_URL + " missing in " + fileName);
		}
        checkUrlValidity(monitorURL);
		return monitorURL;
	}
	
	public String getMonitoringURLPath() throws PropertyMissingException, MalformedURLException {
		String path = properties.getProperty(WorkloadAnalyzerConstants.MONITORING_MANGER_URL_PATH);
		if (path == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.MONITORING_MANGER_URL_PATH + " missing in " + fileName);
		}
		return path;
	}
	
	public static void checkUrlValidity(String url_string) throws MalformedURLException {
        if(logger.isTraceEnabled()) {
        	logger.trace("checking url for validity : "+url_string);
        }
        URL aUrl = new URL(url_string);
        if(logger.isDebugEnabled()) { 
        	logger.debug("valid url found : " + aUrl.toString());
        }
    }
	
    public boolean isMockingEnabled() throws PropertyMissingException{
        
        String mocking_flag = properties.getProperty(WorkloadAnalyzerConstants.MOCK_CLIENTS);
		if (mocking_flag == null){
			throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.MOCK_CLIENTS + " missing in " + fileName);
		}
		return Boolean.parseBoolean(mocking_flag);
        
    }
    
    public String getCloudOptimizerHost() throws PropertyMissingException {
        String host = properties.getProperty(WorkloadAnalyzerConstants.CLOUD_OPTIMIZER_HOST);
        if(host == null){
            throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.CLOUD_OPTIMIZER_HOST + " missing in " + fileName);
        }
        return host;

    }
    
    public String getCloudOptimizerPort() throws PropertyMissingException {
        String port = properties.getProperty(WorkloadAnalyzerConstants.CLOUD_OPTIMIZER_PORT);
        if (port == null) {
            throw new PropertyMissingException("Property " + WorkloadAnalyzerConstants.CLOUD_OPTIMIZER_PORT + " missing in " + fileName);
        }
        return port;
    }

}
