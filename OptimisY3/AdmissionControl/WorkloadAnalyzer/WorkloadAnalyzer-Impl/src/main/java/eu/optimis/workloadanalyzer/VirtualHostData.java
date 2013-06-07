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
package eu.optimis.workloadanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.workloadanalyzer.utils.WorkloadAnalyzerConstants;

/**
 * @author hrasheed
 * 
 */
public class VirtualHostData {
	
private static final Logger log = Logger.getLogger(VirtualHostData.class);
	
	private List<MonitoringResourceDataset> virtualHostMetrics = null;
	
	private String virtualHostID = "Virtual-Dummy";
	private String physicalHostID = "Physical-Dummy";
	
	private ArrayList<MonitoringResourceDataset> os_release = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> cpu_vnum = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> machine_type = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> cpu_speed = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> cpu_usage = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> mem_total = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> mem_used = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> disk_total = new ArrayList<MonitoringResourceDataset>();
	
	public VirtualHostData(String virtualHostID) {
        this.virtualHostID = virtualHostID;
    }
	
	public VirtualHostData(String virtualHostID, List<MonitoringResourceDataset> virtualHostMetrics) {
		this.virtualHostID = virtualHostID;
		this.virtualHostMetrics = virtualHostMetrics; 
	}
	
	public void setVirtualHostData(List<MonitoringResourceDataset> virtualHostMetrics) {
		this.virtualHostMetrics = virtualHostMetrics;
	}
	
	public List<MonitoringResourceDataset> getVirtualHostData() {
		return this.virtualHostMetrics;
	}
	
	public void setVirtualHostID(String virtualHostID) {
    	this.virtualHostID = virtualHostID;
    }
	
	public String getVirtualHostID() {
    	return this.virtualHostID;
    }
	
	public String getPhysicalHostID() {
		MonitoringResourceDataset cpuMetric = getCPUMetric(); 
		if(cpuMetric == null)
			return this.physicalHostID;
		if(cpuMetric.getPhysical_resource_id() != null && !(cpuMetric.getPhysical_resource_id().equals(""))) {
			this.physicalHostID = cpuMetric.getPhysical_resource_id();
		} else {
			//this.physicalHostID = "optimis01"; // for testing only
		}	
    	return this.physicalHostID;
    }
	
    public void processMetricDatasets() {
		
    	if(virtualHostMetrics.size() == 0)
    		return;
    	
		for (int i = 0; i < virtualHostMetrics.size(); i++) {
			MonitoringResourceDataset metricData = (MonitoringResourceDataset) virtualHostMetrics.get(i);
			String resourceMetric = metricData.getMetric_name();
			if(resourceMetric.equals(WorkloadAnalyzerConstants.OS_RELEASE)) {
				os_release.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.CPU_VNUM)) {
				cpu_vnum.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.MACHINE_TYPE)) {
				machine_type.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.CPU_SPEED)) {
				cpu_speed.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.CPU_USAGE)) {
				cpu_usage.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.MEM_TOTAL)) {
				mem_total.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.MEM_USED)) {
				mem_used.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.DISK_TOTAL)) {
				disk_total.add(metricData);
			}	
		} 
    	
    	// sort all metric data-sets based on the metric time stamp
    	sortMetricDataSet(os_release);
    	sortMetricDataSet(cpu_vnum);
    	sortMetricDataSet(machine_type);
    	sortMetricDataSet(cpu_speed);
    	sortMetricDataSet(cpu_usage);
    	sortMetricDataSet(mem_total);
    	sortMetricDataSet(mem_used);
    	sortMetricDataSet(disk_total);
    
	}
    
    private void sortMetricDataSet(ArrayList<MonitoringResourceDataset> metricDataSet) {
    	Collections.sort(metricDataSet, new Comparator<MonitoringResourceDataset>() {
			public int compare(MonitoringResourceDataset metric1, MonitoringResourceDataset metric2) {
				if (metric1.getMetric_timestamp().getTime() > metric2.getMetric_timestamp().getTime()) {
					return -1;
				} else if (metric1.getMetric_timestamp().getTime() < metric2.getMetric_timestamp().getTime()) {
					return +1;
				} else {
					return 0;
				}
			}
		}); 
    }
    
    public MonitoringResourceDataset getCPUMetric() {
    	if(this.cpu_vnum.size() == 0)
    		return null;
    	MonitoringResourceDataset cpusMetric = (MonitoringResourceDataset) this.cpu_vnum.get(0);
    	return cpusMetric;
    }
	
    public void toPrint() {
		if(virtualHostMetrics == null)
			return;
		for (int j = 0; j < virtualHostMetrics.size(); j++) {
			MonitoringResourceDataset resourceData = (MonitoringResourceDataset) virtualHostMetrics.get(j);
			log.info("virtual_resource_id:" + resourceData.getVirtual_resource_id() +
					"-Metric_name:" + resourceData.getMetric_name() + 
					"-Metric_unit:" + resourceData.getMetric_unit() +
					"-Metric_value:" + resourceData.getMetric_value() +
					"-Metric_timesstamp:" + resourceData.getMetric_timestamp());
		}
	}

}
