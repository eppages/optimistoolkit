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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class TestGetClient {
	public static void main(String[] args) throws ParseException{

		getClient client = new getClient("localhost", 8080, "MonitoringManager/QueryResources");
		//getClient client = new getClient("localhost", 8080, "MonitoringManager/QueryResources");
		//MonitoringResourceDatasets rs = client.getLatestReportForVirtual("andoni_test_ovf");
		//((MonitoringResourceDatasets rs = client.getLatestReportForPhysical("optimis1");
		MonitoringResourceDatasets rs = client.getLatestReportForService("DemoApp");
		
//		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		// 0121016163620.20121016163700
//		Date a = dfm.parse("2012-10-15 16:36:20");
//		Date b = dfm.parse("2012-10-16 17:00:00");
////		System.out.println(a.getTime()/1000);
////		System.out.println(b.getTime()/1000);
		
//		MonitoringResourceDatasets rs = client.getReportForPartMetricName("CoreTime", "service", a, b);
		
		//MonitoringResourceDatasets rs = client.getReportForPartPhysical(a, b);
		System.out.println(rs.getMonitoring_resource().size());
		for (int i=0; i<5; i++){
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_name());
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_value());
			System.out.println(rs.getMonitoring_resource().get(i).getMetric_timestamp().toString());
		}
   }
}
