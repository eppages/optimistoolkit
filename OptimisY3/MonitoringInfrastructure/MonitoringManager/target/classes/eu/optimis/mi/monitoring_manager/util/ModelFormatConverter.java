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

package eu.optimis.mi.monitoring_manager.util;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class ModelFormatConverter {
	public static String toHTML(MonitoringResourceDatasets mrds){
		
		StringBuffer rsList = new StringBuffer();
		rsList.append("<html><head/><body>Monitoring Resources:");
		rsList.append("<table border=1><tr><td>service_resource_id</td><td>physical_resource_id</td><td>virtual_resource_id</td>" +
				"<td>resource_type</td><td>monitoring_information_collector_id</td><td>metric_name</td>" +
				"<td>metric_value</td><td>metric_unit</td><td>metric_timestamp</td></tr>");
		for (int i = 0; i < mrds.getMonitoring_resource().size(); i++) {
			MonitoringResourceDataset mds = mrds.getMonitoring_resource().get(i);
			rsList.append("<tr><td>" +mds.getService_resource_id()+"</td><td>" + mds.getPhysical_resource_id()+"</td><td>" 
					+ mds.getVirtual_resource_id()+"</td><td>" +mds.getResource_type()+"</td><td>" + mds.getMonitoring_information_collector_id()+"</td><td>"
					+mds.getMetric_name()+"</td><td>" +mds.getMetric_value()+"</td><td>"+ mds.getMetric_unit()+"</td><td>" +mds.getMetric_timestamp()+"</td></tr>");
		}
		rsList.append("</table></body></html>");
		return rsList.toString();
	}
}

