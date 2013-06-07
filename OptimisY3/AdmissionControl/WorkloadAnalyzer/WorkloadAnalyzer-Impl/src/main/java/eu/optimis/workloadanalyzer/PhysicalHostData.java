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
import eu.optimis.schemas.workload.CpuCoresType;
import eu.optimis.schemas.workload.CurrentType;
import eu.optimis.schemas.workload.LoadType;
import eu.optimis.schemas.workload.MemoryType;
import eu.optimis.schemas.workload.MetricType;
import eu.optimis.schemas.workload.NetworkBandwidthType;
import eu.optimis.schemas.workload.PhysicalHostType;
import eu.optimis.schemas.workload.ResourceInformationType;
import eu.optimis.schemas.workload.StorageType;
import eu.optimis.schemas.workload.WorkloadAnalysisType;
import eu.optimis.workloadanalyzer.utils.WorkloadAnalyzerConstants;

/**
 * @author hrasheed
 * 
 */
public class PhysicalHostData {
	
	private static final Logger log = Logger.getLogger(PhysicalHostData.class);
	
	private List<MonitoringResourceDataset> physicalHostMetrics = null;
	
	private String physicalHostID = "Physical";
	
	private ArrayList<MonitoringResourceDataset> hostStatus = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> cores = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> cpuAverageLoad = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> diskFreeSpace = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> totalMemory = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> freeMemory = new ArrayList<MonitoringResourceDataset>();
	private ArrayList<MonitoringResourceDataset> downStream = new ArrayList<MonitoringResourceDataset>();
	
	private List<VirtualHostData> virtualHosts = new ArrayList<VirtualHostData>();
	
	private List<String> virtualHostIDs = null;
	
	public PhysicalHostData(String physicalHostID) {
	    this.physicalHostID = physicalHostID;
    }
	
	public PhysicalHostData(String physicalHostID, List<MonitoringResourceDataset> physicalHostMetrics, List<String> virtualHostIDs) {
		this.physicalHostID = physicalHostID;
		this.physicalHostMetrics = physicalHostMetrics; 
		this.virtualHostIDs = virtualHostIDs;
	}
	
    public void addMonitoredHostInfo(WorkloadAnalysisType workloadAnalysisType){
    	
    	// check if this host status is OK
    	MonitoringResourceDataset statusMetric = getStatusMetric();
    	if(statusMetric != null) {
    		if(statusMetric.getMetric_value().equals("OK")) {
    			log.info("Host: " + getPhysicalHostID() + " - status: OK");
    		} else {
    			log.info("Host: " + getPhysicalHostID() + " - status: UNAVAILABLE");
    			return;
    		}
    	} else {
    		log.error("status is UNKNOWN for the host: " + getPhysicalHostID());
    		return;
    	}
		
		
        // add physical hosts
        PhysicalHostType physicalHostType = workloadAnalysisType.addNewPhysicalHost();
        physicalHostType.setId(getPhysicalHostID());
        physicalHostType.setHostName(getPhysicalHostID());
        physicalHostType.setPublicIp("192.168.252.1");
        physicalHostType.setUsedForElasticity(true);
        
        // add a description
        physicalHostType.setDescription("Insfrastructure Provider's Physical Host [" + getPhysicalHostID()  + "] for hosting VMs");
        
        // TODO parse service manifest to add assigned services 
        //AssigendServicesType assigendServicesType = physicalHostType.addNewAssigendServices();
        //ServiceType serviceType = assigendServicesType.addNewService();
        //serviceType.setId("service-id-001");
        //serviceType.setName("service-name-001");
        
        //
        // add load informations
        //
        LoadType loadType = physicalHostType.addNewLoad();
        
        // current load
        CurrentType currentType = loadType.addNewCurrent();
        ResourceInformationType resourceInformationType = currentType.addNewResourceInformation();
        
        MonitoringResourceDataset coresMetric = getCoresMetric();
        if(coresMetric != null) {
        	CpuCoresType coresType = resourceInformationType.addNewCpuCores();
        	coresType.setName(WorkloadAnalyzerConstants.NUMBER_OF_CORES);
        	coresType.setMetricUnit(WorkloadAnalyzerConstants.NUMBER_OF_CORES_UNIT);
        	coresType.setActualValue(coresMetric.getMetric_value());
        	coresType.setStringValue(getFreeCores(coresMetric.getMetric_value()));
        } else {
        	log.debug("no monitoring info received on [number of cores] for the host [" + getPhysicalHostID() + "]");
        }
        
        MonitoringResourceDataset cpuAverageLoadMetric = getCPUAverageLoadMetric();
    	if(cpuAverageLoadMetric != null) {
    		MetricType cupLoad = resourceInformationType.addNewMetric();
    		cupLoad.setMetricName(WorkloadAnalyzerConstants.CPU_LOAD);
    		cupLoad.setMetricUnit(WorkloadAnalyzerConstants.CPU_LOAD_UNIT);
    		cupLoad.setStringValue(getFirstValue(cpuAverageLoadMetric.getMetric_value()));
    	} else {
        	log.debug("no monitoring info received on [processor load] for the host [" + getPhysicalHostID() + "]");
        }
        	
		MonitoringResourceDataset diskFreeSpaceMetric = getDiskFreeSpaceMetric();
		if(diskFreeSpaceMetric != null) {
			StorageType storageType = resourceInformationType.addNewStorage();
			storageType.setName(WorkloadAnalyzerConstants.STORAGE);
			storageType.setMetricUnit(WorkloadAnalyzerConstants.STORAGE_UNIT);
			storageType.setStringValue(diskFreeSpaceMetric.getMetric_value());
		} else {
        	log.debug("no monitoring info received on [free disk space] for the host [" + getPhysicalHostID() + "]");
        }
		
		
		MonitoringResourceDataset totalMemoryMetric = getTotalMemoryMetric();
		MonitoringResourceDataset freeMemoryMetric = getFreeMemoryMetric();
		if(totalMemoryMetric != null) {
			MemoryType memoryType = resourceInformationType.addNewMemory();
			memoryType.setName(WorkloadAnalyzerConstants.MAIN_MEMORY);
			memoryType.setMetricUnit(WorkloadAnalyzerConstants.MAIN_MEMORY_UNIT);
			memoryType.setActualValue(totalMemoryMetric.getMetric_value());
			if(freeMemoryMetric != null)
				memoryType.setStringValue(freeMemoryMetric.getMetric_value());
		} else {
        	log.debug("no monitoring info received on [main memory] for the host [" + getPhysicalHostID() + "]");
        }
		
		MonitoringResourceDataset downStreamMetric = getDownStreamMetric();
		if(downStreamMetric != null) {
			NetworkBandwidthType networkType = resourceInformationType.addNewNetworkBandwidth();
			networkType.setName(WorkloadAnalyzerConstants.NETWORK_BANDWIDTH);
			networkType.setMetricUnit(WorkloadAnalyzerConstants.NETWORK_BANDWIDTH_UNIT);
			//networkType.setActualValue(resourceData.getMetric_value());
			networkType.setStringValue(downStreamMetric.getMetric_value());
		} else {
        	log.debug("no monitoring info received on [down stream] for the host [" + getPhysicalHostID() + "]");
        }
		
    }
    
    private String getFreeCores(String totalCores) {
    	
    	int usedCores = 0;
    	int freeCores = 0;
    	
    	try {
    		
    		for (int i = 0; i < virtualHosts.size(); i++) {
        		VirtualHostData virtualHost = (VirtualHostData) virtualHosts.get(i);
        		MonitoringResourceDataset vcpuMetric = virtualHost.getCPUMetric();
        		if(vcpuMetric != null) {
        		    log.debug("virtualHost: [" + virtualHost.getVirtualHostID() + "] Cores: " + vcpuMetric.getMetric_value());
                    usedCores = usedCores + Integer.valueOf(vcpuMetric.getMetric_value());
        		}	
    		}
        	
        	int numTotalCores = Integer.valueOf(totalCores);
        	
        	log.info("calculating free number of cores for the Host [" + getPhysicalHostID() + "]");
        	
        	/*
        	 * calculating free number of cores through cores allocated to the virtual hosts
        	 * this returns negative value when cores allocated to virtual hosts are greater 
        	 * than the actual number of total cores
        	 */
        	/*if(numTotalCores > usedCores) {
        		freeCores = numTotalCores - usedCores;
            	log.info("total cores: " + totalCores + " - used cores: " + usedCores + " - free cores: " + freeCores);
        	}*/
        	
        	/*
        	 * calculating free cores by using current CPU load information
        	 */
        	double cpuLoadValue = getCPULoadValue();
        	
        	/*
        	 * if no load information is available then free number of cores are 0
        	 */
    		if(cpuLoadValue < 0.0) {
    			freeCores = 0;
    			log.debug("load value: " + cpuLoadValue + " - means no load information is available, therefore, free number of cores are 0");
    			return String.valueOf(freeCores); 
    		}
    		/*
    		 * no load on the physical host, therefore free number of cores are equal to the total number of cores
    		 * regardless of how many virtual hosts are running on the physical host
    		 */
    		if(cpuLoadValue == 0.0) {
    			freeCores = numTotalCores;
    			log.debug("load value is 0.0, therefore free number of cores are equal to the total number of cores");
    			return String.valueOf(freeCores); 
    		}
    		
    		/*
    		 * what is the current cpu load in percentage
    		 */
    		double coreLoadPercentage = (cpuLoadValue / numTotalCores) * 100;
    		
    		/*
    		 * cpuLoadPercentage >= 100 means that physical host is overloaded
    		 */
    		if(coreLoadPercentage >= 100) {
    			freeCores = 0;
    			log.debug("cpuLoadPercentage is : " + coreLoadPercentage + "% - means that physical host is overloaded");
    			return String.valueOf(freeCores);
    		} 
    		
    		/*
    		 * cpuLoadPercentage >= 95 means that physical host is ALMOST overloaded
    		 * and shouldn't be used for further allocation
    		 */
    		if(coreLoadPercentage >= 95) {
    			freeCores = 0;
    			log.debug("cpuLoadPercentage is : " + coreLoadPercentage + "% - means that physical host is ALMOST overloaded");
    			return String.valueOf(freeCores);
    		}
    		
    		/*
    		 * cpuLoadPercentage <= 5 means that physical host is totally free
    		 *  
    		 */
    		if(coreLoadPercentage <= 1.0) {
    			freeCores = numTotalCores;
    			log.debug("cpuLoadPercentage is : " + coreLoadPercentage + "% - means that physical host is totally free");
    			return String.valueOf(freeCores);
    		}
    		
    		/*
    		 * calculating per core possible load in percentage 
    		 */
    		double perCoreLoadPercentage =  100 / numTotalCores;
    		
    		/*
    		 * what is the remaining capacity in percentage 
    		 */
    		double remainingCapacityPercentage = 100 - coreLoadPercentage;
    		
    		/*
    		 * now it is possible to calculate the free number of cores from remaining capacity percentage
    		 * and per core percentage load
    		 */
    		double possibleFreeCores = remainingCapacityPercentage / perCoreLoadPercentage;
    		
    		/*
    		 * rounding off the double value 
    		 */
    		long potnetiallyFreeCores = Math.round(possibleFreeCores);
    		
    		/*
    		 * due to precision value it might be the case that poteniallyFreeCores value is greater than numTotalCores value
    		 */
        	if(potnetiallyFreeCores > numTotalCores)
        		potnetiallyFreeCores = numTotalCores;
        	
        	if(potnetiallyFreeCores < 1) {
        		if(numTotalCores == 1) {
        			if(coreLoadPercentage <= 50) {
        				potnetiallyFreeCores = 1;
        			} else {
        				potnetiallyFreeCores = 0;	
        		}
        		} else {
        			potnetiallyFreeCores = 0;
        		}
        	}
        	
        	freeCores = (int) potnetiallyFreeCores;
        	
        	log.debug("total cores: " + numTotalCores);
        	log.debug("coreLoadPercentage: " + coreLoadPercentage);
        	log.debug("perCoreLoadPercentage: " + perCoreLoadPercentage);
        	log.debug("remainingCapacity: " + remainingCapacityPercentage);
        	log.debug("possibleFreeCores-double value: " + possibleFreeCores);
        	log.debug("potnetiallyFreeCores-long: " + potnetiallyFreeCores);
        	
        	log.info("free number of cores: " + freeCores);
        	
    	} catch (Exception e) {
    	    e.printStackTrace();
    		log.error("error in calculating number of free cores for host: [" + getPhysicalHostID() + "]"  + e.getMessage());
		}
    	
		return String.valueOf(freeCores);		
    }
    
    private String getFirstValue(String metricValue) {
    	int subValueLength = 4;
    	int length = metricValue.length();
    	if(length <= subValueLength){
    	  return metricValue;
    	}
    	return metricValue.substring(0,subValueLength);
    }
    
    private String getLastValue(String metricValue) {
    	int subValueLength = 5;
    	int length = metricValue.length();
    	if(length <= subValueLength){
    	  return metricValue;
    	}
    	int startIndex = length - subValueLength;
    	return metricValue.substring(startIndex);
    }
    
    private double getCPULoadValue() {
    	
    	double cpuLoadValue = -1;
    	
    	MonitoringResourceDataset cpuAverageLoadMetric = getCPUAverageLoadMetric();
    	
    	if(cpuAverageLoadMetric == null)
    		return cpuLoadValue;
    	
    	cpuLoadValue = Double.valueOf(getFirstValue(cpuAverageLoadMetric.getMetric_value()));
    	
    	return cpuLoadValue;
    	
    	// calculate the 'mean' from cpu load values 
    	/*if(this.cpuAverageLoad.size() == 0) {
    		return -1;
    	}
    	double sum = 0;
    	for (int i = 0; i < cpuAverageLoad.size(); i++) {
    		MonitoringResourceDataset cpuAverageLoadMetric = (MonitoringResourceDataset) cpuAverageLoad.get(i);
    		double value = Double.valueOf(getFirstValue(cpuAverageLoadMetric.getMetric_value()));
    		sum += value;
		}
    	double averageLoadValue = sum/cpuAverageLoad.size();*/	
    }
    
    public void processMetricDatasets() {
		
    	if(physicalHostMetrics.size() == 0)
    		return;
    	
		for (int i = 0; i < physicalHostMetrics.size(); i++) {
			MonitoringResourceDataset metricData = (MonitoringResourceDataset) physicalHostMetrics.get(i);
			String resourceMetric = metricData.getMetric_name();
			if(resourceMetric.equals(WorkloadAnalyzerConstants.STATUS)) {
				hostStatus.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.NO_OF_CORES)) {
				cores.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.CPU_AVERAGE_LOAD)) {
				cpuAverageLoad.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.DISK_FREE_SPACE)) {
				diskFreeSpace.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.TOTAL_MEMORRY)) {
				totalMemory.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.FREE_MEMORY)) {
				freeMemory.add(metricData);
			} else if(resourceMetric.equals(WorkloadAnalyzerConstants.DOWNSTREAM)) {
				downStream.add(metricData);
			}	
		} 
    	
    	// sort all metric data-sets based on the metric time stamp
    	sortMetricDataSet(hostStatus);
    	sortMetricDataSet(cores);
    	sortMetricDataSet(cpuAverageLoad);
    	sortMetricDataSet(diskFreeSpace);
    	sortMetricDataSet(totalMemory);
    	sortMetricDataSet(freeMemory);
    	sortMetricDataSet(downStream);
    	
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
    
    public void setPhysicalHostData(List<MonitoringResourceDataset> physicalHostMetrics) {
		this.physicalHostMetrics = physicalHostMetrics;
	}
	
	public List<MonitoringResourceDataset> getPhysicalHostData() {
		return this.physicalHostMetrics;
	}
	
	public void setPhysicalHostID(String physicalHostID) {
    	this.physicalHostID = physicalHostID;
    }
	
	public String getPhysicalHostID() {
    	return this.physicalHostID;
    }
	
	public void setVirtualHosts(List<VirtualHostData> virtualHosts) {
		this.virtualHosts = virtualHosts;
	}
	
	public List<VirtualHostData> getVirtualHosts() {
		return this.virtualHosts;
	}
	
	public void setVirtualHostIDs(List<String> virtualHostIDs) {
        this.virtualHostIDs = virtualHostIDs;
    }
    
    public List<String> getVirtualHostIDs() {
        return this.virtualHostIDs;
    }
	
	public void addVirtualHost(VirtualHostData virtualHost) {
		this.virtualHosts.add(virtualHost);
	}
	
	public VirtualHostData getVirtualHost(String virtualHostID) {
		for (int i = 0; i < virtualHosts.size(); i++) {
			VirtualHostData storedVirtualHost = virtualHosts.get(i);
			if(storedVirtualHost.getVirtualHostID().equals(virtualHostID))
				return storedVirtualHost;
		}
		return null;
	}
	
	public MonitoringResourceDataset getStatusMetric() {
		if(this.hostStatus.size() == 0)
			return null;
		MonitoringResourceDataset statusMetric = (MonitoringResourceDataset) hostStatus.get(0);
    	return statusMetric;
    }
	
	public MonitoringResourceDataset getCoresMetric() {
		if(this.cores.size() == 0)
			return null;
    	MonitoringResourceDataset coresMetric = (MonitoringResourceDataset) this.cores.get(0);
    	return coresMetric;
    }
	
	public MonitoringResourceDataset getCPUAverageLoadMetric() {
		if(this.cpuAverageLoad.size() == 0)
			return null;
		MonitoringResourceDataset cpuAverageLoadMetric = (MonitoringResourceDataset) cpuAverageLoad.get(0);
    	return cpuAverageLoadMetric;
    }
	
	public MonitoringResourceDataset getDiskFreeSpaceMetric() {
		if(this.diskFreeSpace.size() == 0)
			return null;
		MonitoringResourceDataset diskFreeSpaceMetric = (MonitoringResourceDataset) diskFreeSpace.get(0);
    	return diskFreeSpaceMetric;
    }
	
	public MonitoringResourceDataset getTotalMemoryMetric() {
		if(this.totalMemory.size() == 0)
			return null;
		MonitoringResourceDataset totalMemoryMetric = (MonitoringResourceDataset) totalMemory.get(0);
    	return totalMemoryMetric;
    }
	
	public MonitoringResourceDataset getFreeMemoryMetric() {
		if(this.freeMemory.size() == 0)
			return null;
		MonitoringResourceDataset freeMemoryMetric = (MonitoringResourceDataset) freeMemory.get(0);
    	return freeMemoryMetric;
    }
	
	public MonitoringResourceDataset getDownStreamMetric() {
		if(this.downStream.size() == 0)
			return null;
		MonitoringResourceDataset downStreamMetric = (MonitoringResourceDataset) downStream.get(0);
    	return downStreamMetric;
    }
	
	public void toPrint() {
		if(physicalHostMetrics == null)
			return;
		for (int j = 0; j < physicalHostMetrics.size(); j++) {
			MonitoringResourceDataset resourceData = (MonitoringResourceDataset) physicalHostMetrics.get(j);
			log.info("Physical_resource_id:" + resourceData.getPhysical_resource_id() +
					"-Metric_name:" + resourceData.getMetric_name() + 
					"-Metric_unit:" + resourceData.getMetric_unit() +
					"-Metric_value:" + resourceData.getMetric_value() +
					"-Metric_timesstamp:" + resourceData.getMetric_timestamp());
		}
		log.info("physical dataset Size: " + physicalHostMetrics.size());
	}
	
	public void toCPULoadPrint() {
		for (int i = 0; i < cpuAverageLoad.size(); i++) {
			MonitoringResourceDataset resourceData = (MonitoringResourceDataset) cpuAverageLoad.get(i);
			log.info("name:" + resourceData.getMetric_name() + 
					"-value:[" + resourceData.getMetric_value() +
					"]-timesstamp:" + resourceData.getMetric_timestamp());
		}
	}

}
