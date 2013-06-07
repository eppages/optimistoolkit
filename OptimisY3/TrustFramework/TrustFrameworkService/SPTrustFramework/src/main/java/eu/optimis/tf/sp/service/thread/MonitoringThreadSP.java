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

package eu.optimis.tf.sp.service.thread;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.sp.TrecIPinfoDAO;
import eu.optimis.common.trec.db.sp.TrecServiceInfoDAO;
import eu.optimis.tf.sp.service.operators.SP2IPFinalTrustCalculator;
import eu.optimis.tf.sp.service.trust.TrustSPOrchestrator;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.IpInfo;
import eu.optimis.trec.common.db.sp.model.ServiceInfo;

public class MonitoringThreadSP extends Thread {

	Logger log = Logger.getLogger(this.getClass().getName());

	private static final String DEMOAPP_ID = "DemoApp";
	private ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
	
	

	public void run() {
			log.info("getting SP monitoring info");
			lstSI = getActiveServices();
			ArrayList<String> activeServices = getActiveServiceIds(lstSI);
			TrustSPOrchestrator tspo = new TrustSPOrchestrator(activeServices);
			tspo.calculateSP2IPparams();
			updateIPTrust();
	}
	
	private void updateIPTrust() {
		TrecIPinfoDAO tipidao = new TrecIPinfoDAO();
		SP2IPFinalTrustCalculator spftc = new SP2IPFinalTrustCalculator();
		try {
			List<IpInfo> ipilist = tipidao.getIPs();
			for (IpInfo spi : ipilist){
				spftc.calculateIPTrust(spi.getIpId());
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
