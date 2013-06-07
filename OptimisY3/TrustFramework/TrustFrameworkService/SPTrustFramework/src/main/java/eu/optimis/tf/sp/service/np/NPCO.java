/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.np;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.sp.service.clients.MonitoringClient;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

public class NPCO {

	private static MonitoringClient getMC() {
		// generate monitoring client
		String monitoringIP = PropertiesUtils.getProperty("TRUST","monitoring.host");
		int monitoringPort = Integer.valueOf(PropertiesUtils
				.getProperty("TRUST","monitoring.port"));
		String monitorigUri = PropertiesUtils.getProperty("TRUST","monitoring.uri");
		MonitoringClient myMonitor = new MonitoringClient(monitoringIP, monitoringPort,
				monitorigUri);
		return myMonitor;
	}
	
	public static List<String> getVMidList (String serviceId){
		ArrayList<MonitoringResourceDataset> listMDS = new ArrayList<MonitoringResourceDataset>();
		Set<String> vmSet = new HashSet<String>();
		List<String> vmIds = new ArrayList<String>();
		MonitoringClient mc = getMC();
		listMDS = (ArrayList<MonitoringResourceDataset>) mc.getMonitoringServiceInfo(serviceId);
		System.out.println("listMDS: "+ listMDS.size());
		for (MonitoringResourceDataset mds : listMDS){
			vmSet.add(mds.getVirtual_resource_id());
		}
		vmIds.addAll(vmSet);
		return vmIds;
	}
}
