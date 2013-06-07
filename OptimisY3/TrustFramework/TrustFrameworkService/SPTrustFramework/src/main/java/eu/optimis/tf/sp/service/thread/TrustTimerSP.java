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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import eu.optimis.common.trec.db.sp.TrecIPTrustDAO;
import eu.optimis.common.trec.db.sp.TrecIPinfoDAO;
import eu.optimis.common.trec.db.sp.TrecSP2IPDAO;
import eu.optimis.common.trec.db.sp.TrecServiceInfoDAO;
import eu.optimis.tf.sp.service.operators.SP2IPFinalTrustCalculator;
import eu.optimis.tf.sp.service.trust.TrustSPOrchestrator;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.IpInfo;
import eu.optimis.trec.common.db.sp.model.ServiceInfo;

public class TrustTimerSP {
	private final ScheduledExecutorService scheduler;
	static Logger log = Logger
			.getLogger("eu.optimis.tf.sp.service.thread.TrustTimerSP");

	// A handle to the unique Singleton instance.
	static private TrustTimerSP _instance = null;
	long interval = 10;
	long initialDelay = 60;
	static boolean broker = false;

	private TrustTimerSP() {
		interval = Long.valueOf(PropertiesUtils.getProperty("TRUST","interval"));
		initialDelay = Long
				.valueOf(PropertiesUtils.getProperty("TRUST","initial.delay"));
		broker = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","broker"));
		// Build elements for thread and tasks scheduling
		scheduler = Executors.newScheduledThreadPool(1);
		final Runnable myTask = new MonitoringThreadSP();
		scheduler
				.scheduleWithFixedDelay(myTask, initialDelay, interval, TimeUnit.SECONDS);
	}

	static public TrustTimerSP instance() {
		if (null == _instance) {
			_instance = new TrustTimerSP();
			inserbrokerData();
		}
		return _instance;
	}

	private static void inserbrokerData() {
		if (broker) {
			TrecIPinfoDAO tipdao = new TrecIPinfoDAO();
			ArrayList<String> brokerIps = new ArrayList<String>();
			brokerIps.add(PropertiesUtils.getProperty("TRUST","atos.id"));
			brokerIps.add(PropertiesUtils.getProperty("TRUST","flex.id"));
			brokerIps.add(PropertiesUtils.getProperty("TRUST","dummy.id"));
			brokerIps.add(PropertiesUtils.getProperty("TRUST","ip4.id"));
			brokerIps.add(PropertiesUtils.getProperty("TRUST","ip5.id"));
			brokerIps.add(PropertiesUtils.getProperty("TRUST","arsys"));
			for (String brokerIp : brokerIps) {
				// try {
				// tipdao.getIP(brokerIp);
				// } catch (Exception e) {
				try {
					log.error("ip not added, adding it");
					tipdao.addIp(brokerIp, brokerIp, "es");
				} catch (Exception e1) {
					log.error("unable to add broker ips");
				}
				// }
			}
			SP2IPFinalTrustCalculator ftc = new SP2IPFinalTrustCalculator();
			TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
			try {
				double atostrust = ftc.CalculateBurstingTrust(30, 15);
//				log.info("***** atos Trust: "+ atostrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","atos.id"),
						atostrust);
				double flextrust = ftc.CalculateBurstingTrust(100, 80);
//				log.info("***** flex trust: " +flextrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","flex.id"),
						flextrust);
				double  dummytrust = ftc.CalculateBurstingTrust(100, 80);
//				log.info("***** dummy trust: "+ dummytrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","dummy.id"),
						dummytrust);
				double  arsystrust = ftc.CalculateBurstingTrust(100, 80);
//				log.info("***** arsys trust: "+ arsystrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","arsys"),
						arsystrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","ip4.id"),
						Double.valueOf(PropertiesUtils.getProperty("TRUST","ip4.trust")));
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","ip5.id"),
						Double.valueOf(PropertiesUtils.getProperty("TRUST","ip5.trust")));
			} catch (NumberFormatException e) {
				log.error (e);
				e.printStackTrace();
			} catch (Exception e) {
				log.error (e);
				e.printStackTrace();
			}
		}
	}

	// class MonitoringTask implements Runnable
	// {
	//
	// public void run()
	// {
	// log.info("******TIMER ACTIVATED*******");
	// for (int i = 10; i > 0; i--){
	// log.info("thread finishing in..."+i);
	// }
	// log.info("******TIMER FINISHED*******");
	// }
	//
	// }

	class MonitoringThreadSP extends Thread {

		Logger log = Logger.getLogger(this.getClass().getName());

		private static final String DEMOAPP_ID = "DemoApp";
		private ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();

		public void run() {
			if (broker) {
				broker();
			} else {
				log.info("**************getting SP monitoring info");
				lstSI = getActiveServices();
				ArrayList<String> activeServices = getActiveServiceIds(lstSI);
				TrustSPOrchestrator tspo = new TrustSPOrchestrator(
						activeServices);
				tspo.calculateSP2IPparams();
				updateIPTrust();
			}
		}

		private void updateIPTrust() {
			TrecSP2IPDAO sp2ip = new TrecSP2IPDAO();
			SP2IPFinalTrustCalculator spftc = new SP2IPFinalTrustCalculator();
			try {
				List<IpInfo> ips = sp2ip.getDistinctIpIDs();
				for (IpInfo ip : ips) {
					spftc.calculateIPTrust(ip.getIpId());
				}
			} catch (Exception e) {
				log.error (e);
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

		private void broker() {
			SP2IPFinalTrustCalculator ftc = new SP2IPFinalTrustCalculator();
			TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
			try {
				double atostrust = ftc.CalculateBurstingTrust(30, 15);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","atos.id"),
						atostrust);
				double flextrust = ftc.CalculateBurstingTrust(100, 80);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","flex.id"),
						flextrust);
				double  dummytrust = ftc.CalculateBurstingTrust(100, 80);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","dummy.id"),
						dummytrust);
				double  arsystrust = ftc.CalculateBurstingTrust(90, 60);
//				log.info("***** arsys trust: "+ arsystrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","arsys"),
						arsystrust);
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","ip4.id"),
						Double.valueOf(PropertiesUtils.getProperty("TRUST","ip4.trust")));
				tiptdao.addIp(PropertiesUtils.getProperty("TRUST","ip5.id"),
						Double.valueOf(PropertiesUtils.getProperty("TRUST","ip5.trust")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
