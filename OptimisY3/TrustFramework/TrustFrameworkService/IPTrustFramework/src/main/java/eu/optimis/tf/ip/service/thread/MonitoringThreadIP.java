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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecSPinfoDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.tf.ip.service.operators.IP2SPFinalTrustCalculator;
import eu.optimis.tf.ip.service.trust.TrustIPOrchestrator;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;
import eu.optimis.trec.common.db.ip.model.SpInfo;

public class MonitoringThreadIP extends Thread {

	Logger log = Logger.getLogger(this.getClass().getName());

	private static final String DEMOAPP_ID = "DemoApp";
	private ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
	private long interval = 0;
	private boolean start = false;
	
	public MonitoringThreadIP() {
		start = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","spside"));
		// Thead runs every
		interval = Long.valueOf(PropertiesUtils.getProperty("TRUST","interval")) * 1000;
	}

	public void run() {
		while (start) {
			log.info("getting IP monitoring info");
			lstSI = getActiveServices();
			ArrayList<String> activeServices = getActiveServiceIds(lstSI);
			TrustIPOrchestrator tipo = new TrustIPOrchestrator(activeServices);
			tipo.calculateIP2SPParams();
			updateSPTrust();
			try {
				Thread.yield();
				Thread.sleep(interval);
				Thread.yield();
			} catch (InterruptedException e) {
				log.error("Trust operation thread didn't go to sleep");
			}
		}
	}

	private void updateSPTrust() {
		TrecSPinfoDAO tspidao = new TrecSPinfoDAO();
		IP2SPFinalTrustCalculator spftc = new IP2SPFinalTrustCalculator();
		try {
			List<SpInfo> spilist = tspidao.getSPs();
			for (SpInfo spi : spilist){
				spftc.calculateIPTrust(spi.getSpId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<ServiceInfo> getActiveServices() {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {
			return (ArrayList<ServiceInfo>) tsidao.getActiveServices();
		} catch (Exception e) {
			log.error("Error getting active services");
			return lstSI;
		}
	}

	private ArrayList<String> getActiveServiceIds(
			ArrayList<ServiceInfo> ServiceList) {
		ArrayList<String> sidList = new ArrayList<String>();
		// TODO call to the DB
		try {
			for (ServiceInfo si : ServiceList) {
				sidList.add(si.getServiceId());
			}
			return sidList;
		} catch (Exception e) {
			sidList.add(DEMOAPP_ID);
			return sidList;
		}
	}

}
