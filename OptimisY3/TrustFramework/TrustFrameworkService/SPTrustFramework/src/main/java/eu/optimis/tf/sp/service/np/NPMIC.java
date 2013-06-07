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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.sp.service.clients.MonitoringClient;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.ServiceInfo;

public class NPMIC extends Thread {

	Logger log = Logger.getLogger(this.getClass().getName());

	ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
	MonitoringClient myMonitor = null;
	String METRIC = null;
	String SERVICEID = null;
	int INTERVAL = 0;
	boolean alive = true;
	
	public void setAlive(boolean live){
		alive = live;
	}
	
	public NPMIC(String serviceId) {
		// generate monitoring client
		String monitoringIP = PropertiesUtils.getProperty("TRUST","monitoring.host");
		int monitoringPort = Integer.valueOf(PropertiesUtils
				.getProperty("TRUST","monitoring.port"));
		String monitorigUri = PropertiesUtils.getProperty("TRUST","monitoring.uri");
		myMonitor = new MonitoringClient(monitoringIP, monitoringPort,
				monitorigUri);
		METRIC = PropertiesUtils.getProperty("TRUST","nonProductionMetric");
		SERVICEID = serviceId;//PropertiesUtils.getBoundle("npApp");
		INTERVAL = Integer.valueOf(PropertiesUtils.getProperty("TRUST","interval"))*1000;
	}

	public void run() {
//		NPservicesDAO npsdao = new NPservicesDAO();
//		try {
//			npsdao.getService(SERVICEID);
//		} catch (Exception e) {
//			log.error("error retrievig service ID from database, getting default service Id: "+SERVICEID);
//			SERVICEID = PropertiesUtils.getBoundle("npApp");
//		}
		while (alive) {
			List<String> vmlist = getVMIDSperService(SERVICEID);
			log.info("VM List Size: " + vmlist.size());
			getVMMonitoring(vmlist);
			try {
				sleep(INTERVAL);
			} catch (InterruptedException e) {
				log.error("failed sleep");
			}
		}
	}

	private void getVMMonitoring(List<String> vmIds) {
		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> lstVMMetric = new ArrayList<HashMap>();
		List<MonitoringResourceDataset> lstMrd = myMonitor
				.getMonitoringServiceInfo(SERVICEID);
		log.info("lstMrd size: " + lstMrd.size());
		for (MonitoringResourceDataset mrd : lstMrd) {
			String metricName = mrd.getMetric_name();
			if (metricName.equalsIgnoreCase(METRIC)) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put(mrd.getVirtual_resource_id(), mrd.getMetric_value());
				lstVMMetric.add(hm);
			}
		}
		log.info("lstVMMetric size: " + lstVMMetric.size());
		NPCalculator.setCoefficients(lstVMMetric, vmIds, SERVICEID);
	}

	private List<String> getVMIDSperService(String serviceId) {
		return NPCO.getVMidList(serviceId);
	}
}
