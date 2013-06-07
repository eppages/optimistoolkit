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

/**
 * @author hrasheed
 * 
 */
public class WorkloadAnalyzerConstants {
	
	public static final String WORKLOAD_ANALYZER_HOST = "workload.analyzer.host";
	public static final String WORKLOAD_ANALYZER_PORT = "workload.analyzer.port";
	
	public static final String WORKLOAD_ANALYZER_URL = "workload.analyzer.url";
	public static final String WORKLOAD_ANALYZER_URL_PATH = "workload.analyzer.url.path";
	
	public static final String WORKLOAD_ANALYZER_MONITORING = "workload.analyzer.monitoring";
	
	public static final String MONITORING_HOST = "monitoring.manager.host";
	public static final String MONITORING_PORT = "monitoring.manager.port";
	
	public static final String MONITORING_MANGER_URL = "monitoring.manager.url";
	public static final String MONITORING_MANGER_URL_PATH = "monitoring.manager.url.path";

    public static final String CLOUD_OPTIMIZER_HOST = "cloudoptimizer.host";
    public static final String CLOUD_OPTIMIZER_PORT = "cloudoptimizer.port";
   

    // debug constants
    public static final String MOCK_CLIENTS = "mock.clients.enabled";
	
	// schema metrics
	public static final String NUMBER_OF_PROCESSORS = "number_of_processor";
    public static final String NUMBER_OF_PROCESSORS_UNIT = "Processors";
    
    public static final String NUMBER_OF_CORES = "number_of_cores";
    public static final String NUMBER_OF_CORES_UNIT = "Cores";
    
    public static final String PROCESSOR_SPEED = "processor_speed";
    public static final String PROCESSOR_SPEED_UNIT = "GHz";
    
	public static final String MAIN_MEMORY = "main_memory";
	public static final String MAIN_MEMORY_UNIT = "kB";
	
	public static final String SWAP_MEMORY = "swap_memory";
	public static final String SWAP_MEMORY_UNIT = "kB";
	
	public static final String STORAGE = "storage";
	public static final String STORAGE_UNIT = "kB";
	
	public static final String NETWORK_BANDWIDTH = "netowrk_bandwidth";
	public static final String NETWORK_BANDWIDTH_UNIT = "Kbps";
	
	public static final String CPU_LOAD = "cpu_load";
	public static final String CPU_LOAD_UNIT = "%";

    public static final String COUNT_OF_USERS = "count_of_users";
    public static final String COUNT_OF_USERS_UNIT = "users";

	
	// Monitoring Metrics
	// Physical Host metrics
	public static final String STATUS = "status";
	public static final String NO_OF_PROCESSORS = "No_of_processors";
	public static final String NO_OF_CORES = "No_of_cores";
	public static final String DISK_FREE_SPACE = "disk_free_space";
	public static final String TOTAL_MEMORRY = "total_memory";
	public static final String FREE_MEMORY = "free_memory";
	public static final String DOWNSTREAM = "Downstream";
	public static final String CPU_AVERAGE_LOAD = "cpu_average_load";
	public static final String TOTAL_RUNNING_PROCESSES = "Total Processes";
    public static final String USERS = "count_of_users";
	
	// Virtual Host metrics
	public static final String OS_RELEASE = "os_release";
	public static final String CPU_VNUM = "cpu_vnum";
	public static final String MACHINE_TYPE = "machine_type";
	public static final String CPU_SPEED = "cpu_speed";
	public static final String CPU_USAGE = "cpu_user";
	public static final String MEM_TOTAL = "mem_total";
	public static final String MEM_USED = "mem_used";
	public static final String DISK_TOTAL = "disk_total";
	
	
}
