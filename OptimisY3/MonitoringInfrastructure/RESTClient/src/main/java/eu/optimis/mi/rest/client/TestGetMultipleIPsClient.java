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

import java.util.ArrayList;
import java.util.List;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class TestGetMultipleIPsClient {
	public static void main(String[] args){

		// Call to getFromMultipleIPs.
		// This queries the database of the local IP if the service runs only there.
		// And this also queries the database of other IPs if the service runs also in those other IPs.

    	GetMultipleIPsClient client = new GetMultipleIPsClient("localhost", 8080);
		
    	String serviceId = "DemoApp";
    	String methodName = "getLatestReportForEnergy";
    	List<Object> methodArgs = new ArrayList<Object>();
    	methodArgs.add("optimis1");
		
		MonitoringResourceDatasets cs = client.getFromMultipleIPs(serviceId, methodName, methodArgs);
		
		int rsSize = cs.getMonitoring_resource().size();
		if (rsSize > 0) {
		   System.out.println(cs.getMonitoring_resource().get(0).getMetric_name());
		   System.out.println(cs.getMonitoring_resource().get(0).getMetric_value());
		   System.out.println(cs.getMonitoring_resource().get(0).getMetric_timestamp().toString());
		}
   }
}
