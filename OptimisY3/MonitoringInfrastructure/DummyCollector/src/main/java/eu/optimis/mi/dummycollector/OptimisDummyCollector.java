package eu.optimis.mi.dummycollector;

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

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.rest.client.postClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OptimisDummyCollector {

	/**
	 * OptimisDummyCollector - use this class to submit dummy monitoring data to
	 * the OPTIMIS monitoring database.
	 * @param args
	 * @author Pierre Gilet (Universität Stuttgart - HLRS)
	 */
	public static void main(String[] args) {

		String serviceID 		= "DemoApp"; // Mandatory if resourceType in ('service').
		String virtualResID 	= "df9ad00e-48e5-4453-912c-65cc462bf2a6"; // Mandatory if resourceType in ('service', 'virtual').
		String physicalResID 	= "optimis1"; // Mandatory if resourceType in ('service', 'virtual', 'physical', 'energy').
		String resourceType 	= "service"; // Allowed values: physical, energy, virtual, service.
		String collectorID 		= "dummycollector"; // Do not change this value.
		String ipVm				= "localhost";
		
		// The list of metric names that can be inserted into the monitoring database can be found here:
		// https://bscw.scai.fraunhofer.de/bscw/bscw.cgi/d271189/OPTIMIS_Monitored_Metrics.pptx
		
		postClient post = new postClient(ipVm, 8080,
				"Aggregator/Aggregator/monitoringresources/" + collectorID);
		Date timestamp = new Date();
		int millisecondOffset = 604800000; // Used to log a record with timestamp = now - offset in milliseconds
	    timestamp.setTime(System.currentTimeMillis() - millisecondOffset);

		MonitoringResourceDataset md1 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "ThreadCount", "4", "", timestamp);
		
		MonitoringResourceDataset md2 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "processor_speed", "3", "GHz", timestamp);
		
		MonitoringResourceDataset md3 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "main_memory", "4096", "MB", timestamp);
		
		MonitoringResourceDataset md4 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "storage", "40000", "GB", timestamp);
		
		MonitoringResourceDataset md5 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "swap_memory", "8192", "GB", timestamp);
		
		MonitoringResourceDataset md6 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "network_bandwidth", "1000", "MBit/s", timestamp);
		
		List<MonitoringResourceDataset> mdList = new ArrayList<MonitoringResourceDataset>();
        mdList.add(md1);
        mdList.add(md2);
        mdList.add(md3);
        mdList.add(md4);
        mdList.add(md5);
        mdList.add(md6);
        MonitoringResourceDatasets mds = new MonitoringResourceDatasets();
		mds.setMonitoring_resource(mdList);
		System.out.println(post.pushReport(mds));
	}
}
