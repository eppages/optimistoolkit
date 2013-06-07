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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.jboss.logging.Logger;

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecSP2IPDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.tf.ip.service.clients.COClient;
import eu.optimis.tf.ip.service.hm.trust.TrustHMOrchestrator;
import eu.optimis.tf.ip.service.operators.IP2SPFinalTrustCalculator;
import eu.optimis.tf.ip.service.operators.SP2IPFinalTrustCalculator;
import eu.optimis.tf.ip.service.trust.TrustIPOrchestrator;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.IpInfo;
import eu.optimis.trec.common.db.ip.model.IpToSp;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;
import eu.optimis.trec.common.db.ip.model.SpInfo;

public class TrustTimerIP {
	private final ScheduledExecutorService scheduler;
	public static final int PROVIDER = 0;
	public static final int SERVICE = 1;
	Logger log = Logger.getLogger(this.getClass().getName());

	// A handle to the unique Singleton instance.
	static private TrustTimerIP _instance = null;
	long interval = 120;
	long initialDelay = 60;
	//final Runnable myTask;
	final MonitoringThreadIP myTask;
	private TrustTimerIP() 
	{
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		interval = Long.valueOf(PropertiesUtils.getProperty("TRUST","interval"));
		initialDelay = Long
				.valueOf(PropertiesUtils.getProperty("TRUST","initial.delay"));
		// Build elements for thread and tasks scheduling
		scheduler = Executors.newScheduledThreadPool(1);
		myTask = new MonitoringThreadIP();
		scheduler
				.scheduleWithFixedDelay(myTask, initialDelay, interval, TimeUnit.SECONDS);
	}

	static public TrustTimerIP instance() {
		if (null == _instance) {
			_instance = new TrustTimerIP();
		}
		return _instance;
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

	class MonitoringThreadIP extends Thread 
	{
		Logger log = Logger.getLogger(this.getClass().getName());
		
		private static final String DEMOAPP_ID = "DemoApp";
		private ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
		private long interval = 0;
		private boolean start = false;
		private final HashMap<String, Double> servicesAlerts;
		private final HashMap<String, Double> providersAlerts;
		
		public MonitoringThreadIP() 
		{
			servicesAlerts = new HashMap<String, Double>();
			providersAlerts = new HashMap<String, Double>();
		}

		public void run() {
				log.info("getting IP monitoring info");
				lstSI = getActiveServices();
				log.info("Active Services "+lstSI.size());
				ArrayList<String> activeServices = getActiveServiceIds(lstSI);
				try
				{
					TrustIPOrchestrator tipo = new TrustIPOrchestrator(activeServices);
					tipo.calculateIP2SPParams();
					updateSPTrust();
				}
				catch (Exception ex)
				{
					log.error("Failure when recalculating Trust for services.");
					log.error(ex.getMessage());
				}
				
				try
				{
					TrustHMOrchestrator thmo = new TrustHMOrchestrator(activeServices);
					thmo.calculateSP2IPparams();
					updateHMTrust();
				}
				catch (Exception ex)
				{
					log.error("Failure when recalculating HM Trust forecast for services.");
					log.error(ex.getMessage());
				}
				
				if (!servicesAlerts.isEmpty() || !providersAlerts.isEmpty())
				{
					checkAlerts();
				}
				
		}

		private void updateSPTrust() {
			TrecIP2SPDAO tip2sp = new TrecIP2SPDAO();
			IP2SPFinalTrustCalculator spftc = new IP2SPFinalTrustCalculator();
			try {
				List<SpInfo> spilist = tip2sp.getDistinctSpIDs();
				for (SpInfo spi : spilist)
				{
					spftc.calculateIPTrust(spi.getSpId());
				}
			} catch (Exception e) 
			{
				log.error ("Error storing trust data for SP ");
				log.error(e.getMessage());
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
		
		public synchronized void subscribeAlert (double threshold, String idEntity, int type)
		{
			switch (type)
			{
			case 0:
				providersAlerts.put(idEntity, new Double (threshold));
				break;
			case 1:
				servicesAlerts.put(idEntity, new Double (threshold));
			}
			log.info("Subscription accepted for " + idEntity + " with threshold " + threshold);
		}
		
		public synchronized void unSubscribeAlert (String idEntity, int type)
		{
			switch (type)
			{
			case 0:
				providersAlerts.remove(idEntity);
				break;
			case 1:
				servicesAlerts.remove(idEntity);
			}
			log.info("Subcription removed for " + idEntity);
		}
		
		private void checkAlerts()
		{
			String coHost = PropertiesUtils.getProperty("TRUST","co.host");
			int coPort = Integer.valueOf(PropertiesUtils.getProperty("TRUST","co.port"));
			COClient notifier = new COClient(coHost, coPort);
			
			log.info("Checking subscriptions about providers");
			// Now, only current IP is considered, no other IPs used
			// Per each ServiceId, retrieve trust value and compare with the threshold
			Set<String> providersList = providersAlerts.keySet();
			Iterator<String> provIterator = providersList.iterator();
			while (provIterator.hasNext())
			{
				String currentId = provIterator.next();
				double threshold = providersAlerts.get(currentId);
				double scale = 1.0;
				try
				{
					TrecIP2SPDAO dbServices = new TrecIP2SPDAO();
					IpToSp lastCalc = dbServices.getLastIP2SPTrust(currentId);					
					scale = Double.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
					double currentVal = lastCalc.getServiceTrust() * scale;
					if (currentVal<threshold)
					{
						// Trust is lower than expected --> Notify
						notifier.notifyCO(currentId, 1, currentVal);
						log.info("Provider " + currentId + " has a trust below the stablished threshold. CO notified!");
					}
				}
				catch (Exception ex)
				{
					log.error("Problems retrieving trust for provider " + currentId + " or notifying");
					log.error(ex.getMessage());
				}
			}
			
			log.info("Checking subscriptions about services");
			// Per each ServiceId, retrieve trust value and compare with the threshold
			Set<String> servicesList = servicesAlerts.keySet();
			Iterator<String> servIterator = servicesList.iterator();
			while (servIterator.hasNext())
			{
				String currentId = servIterator.next();
				double threshold = servicesAlerts.get(currentId);
				try
				{
					TrecIP2SPDAO dbServices = new TrecIP2SPDAO();
					IpToSp lastCalc = dbServices.getLastIP2SPTrust(currentId);
					double currentVal = lastCalc.getServiceTrust();
					if (currentVal<threshold)
					{
						// Trust is lower than expected --> Notify
						notifier.notifyCO(currentId, 1, currentVal);
						log.info("Service " + currentId + " has a trust below the stablished threshold. CO notified!");
					}
				}
				catch (Exception ex)
				{
					log.error("Problems retrieving trust for service " + currentId + " or notifying");
					log.error(ex.getMessage());
				}				
			}
			
			log.info("All subscriptions for alerts processed.");
		}
	}

	public void updateHMTrust() {
		TrecSP2IPDAO sp2ip = new TrecSP2IPDAO();
		SP2IPFinalTrustCalculator spftc = new SP2IPFinalTrustCalculator();
		try {
			List<IpInfo> ips = sp2ip.getDistinctIpIDs();
			for (IpInfo ip : ips) {
				spftc.calculateIPTrust(ip.getIpId());
			}
		} catch (Exception e) 
		{
			log.error ("Error storing trust data for IP ");
			log.error(e.getMessage());
		}
	}
	
	public void subscribeAlert (double threshold, String idEntity, int type)
	{
		myTask.subscribeAlert(threshold, idEntity, type);
	}
	
	public void unSubscribeAlert (String idEntity, int type)
	{
		myTask.unSubscribeAlert(idEntity, type);
	}
}
