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

import java.util.List;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.mi.rest.client.postClient;

import org.apache.log4j.Logger;

import eu.optimis.workloadanalyzer.WorkloadAnalyzerImpl;

/**
 * @author hrasheed
 * 
 */
public class MonitoringClient {
	
	private static final Logger log = Logger.getLogger(MonitoringClient.class);
	
	private String host = "192.168.42.226";
	private int port = 8080;
	
	private String GETPATH  = "MonitoringManager/QueryResources";
	private String POSTPATH = "Aggregator/Aggregator/monitoringresources";
	
	public MonitoringClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public MonitoringClient() {
		try {
    		WorkloadPropertiesUtil props = new WorkloadPropertiesUtil();
    		this.host = props.getMonitoringHost();
    		this.port = Integer.parseInt(props.getMonitoringPort());
    	}catch (Exception e) {
			log.error(e);
		}
	}
	
	public List<MonitoringResourceDataset> getMonitoringInfo() throws Exception {
		
		try {
			
			getClient gc = new getClient(host,port, GETPATH);
			
			MonitoringResourceDatasets mrd = gc.getReportForAllPhysical();
			
			List<MonitoringResourceDataset> monitoredResources = mrd.getMonitoring_resource();
			
			for (int i = 0; i < monitoredResources.size(); i++) {
				MonitoringResourceDataset resourceData = (MonitoringResourceDataset) monitoredResources.get(i);
				log.debug("Resource_type" + resourceData.getResource_type());
				log.debug("Physical_resource_id" + resourceData.getPhysical_resource_id());
				log.debug("Metric_name" + resourceData.getMetric_name());
				log.debug("Metric_unit" + resourceData.getMetric_unit());
				log.debug("Metric_value" + resourceData.getMetric_value());
				log.debug("Metric_timesstamp" + resourceData.getMetric_timestamp());
			}
			
			return monitoredResources;
			
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}
	}
	
}
