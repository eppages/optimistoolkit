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

public class TestGetClient {
	public static void main(String[] args){

		getClient client = new getClient("212.0.127.140", 8080, "MonitoringManager/QueryResources");
	    
		MonitoringResourceDatasets rs = client.getLatestReportForEnergy("optimis1");

		for (int i=0; i<rs.getMonitoring_resource().size(); i++){
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_name());
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_value());
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_timestamp().toString());
		}
   }
}
