/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.rest.client;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class testPOST {
	public static void main(String[] args) {

		String serviceID = "DemoApp"; // Mandatory if resourceType in ('service').
		String virtualResID = "123456"; // Mandatory if resourceType in ('service', 'virtual').
		String physicalResID = "optimis1"; // Mandatory if resourceType in ('service', 'virtual', 'physical', 'energy').
		String resourceType = "service"; // Allowed values: physical, energy, virtual, service.
		String collectorID = "dummycollector"; // Do not change this value.
		
		postClient post = new postClient("localhost", 8080,
				"Aggregator/Aggregator/monitoringresources/" + collectorID);
		Date now = new Date();
	    now.setTime(System.currentTimeMillis());

		MonitoringResourceDataset md1 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "num_of_cores", "4", "", now);
		
		MonitoringResourceDataset md2 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "processor_speed", "3", "GHz", now);
		
		MonitoringResourceDataset md3 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "main_memory", "4096", "MB", now);
		
		MonitoringResourceDataset md4 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "storage", "40000", "GB", now);
		
		MonitoringResourceDataset md5 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "swap_memory", "8192", "GB", now);
		
		MonitoringResourceDataset md6 = new MonitoringResourceDataset(serviceID, virtualResID,
				physicalResID, resourceType, collectorID, "network_bandwidth", "1000", "MBit/s", now);
		
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
