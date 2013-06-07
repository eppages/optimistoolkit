//package eu.optimis.elasticityengine.sc;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.log4j.Logger;
//
//import eu.optimis.elasticityengine.DaemonThreadFactory;
//import eu.optimis.elasticityengine.ElasticityCallback;
//import eu.optimis.elasticityengine.ElasticityRule;
//import eu.optimis.elasticityengine.monitoring.MonitoringSource;
//import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
//
///**
// * Simplistic implementation of the service controller interface. Offers a
// * reactive service controller that only reacts to load and then adjusts the
// * amount of running VMs.
// * 
// * @author Daniel Espling (<a
// *         href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
//*Copyright (C) 2012 Umeå University
//
//* This program is free software: you can redistribute it and/or modify
//* it under the terms of the GNU General Public License as published by
//* the Free Software Foundation, either version 3 of the License, or
//* (at your option) any later version.
//
//* This program is distributed in the hope that it will be useful,
//* but WITHOUT ANY WARRANTY; without even the implied warranty of
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//* GNU General Public License for more details.
//
//* You should have received a copy of the GNU General Public License
//* along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// */
//public class ReactiveServiceController implements Runnable, ServiceController {
//
//	protected final static Logger log = Logger
//			.getLogger(ReactiveServiceController.class);
//
//	private MonitoringSource monitoringSource;
//	protected Map<String, ElasticityRule> rules;
//	private ElasticityCallback callback;
//	private final String spAddress;
//	private final String serviceID;
//
//	private Map<String, ImageController> imageControllers;
//
//	private ScheduledFuture<?> future;
//	private ScheduledExecutorService ex;
//
//	private String serviceManifest;
//
//	private MonitoringResourceDataset previousMonitoringValue;
//
//	public ReactiveServiceController(String serviceID, String serviceManifest,
//			long predictionInterval, long initialDelay,
//			MonitoringSource monitoringSource, String spAddress) {
//
//		this.serviceID = serviceID;
//		this.serviceManifest = serviceManifest;
//		this.imageControllers = Util.initiateImageControllers(serviceManifest);
//		this.monitoringSource = monitoringSource;
//		this.spAddress = spAddress;
//
//		// Schedule periodic predictions
//		ex = Executors.newScheduledThreadPool(2, new DaemonThreadFactory());
//		schedulePrediction(predictionInterval, initialDelay);
//	}
//
//	@Override
//	public void setCallback(ElasticityCallback callback) {
//		this.callback = callback;
//	}
//
//	@Override
//	public void unSetCallback() {
//		this.callback = null;
//	}
//
//	protected boolean notifyCallback(String imageID, int recommendedNrVMs,
//			int currentNumberVMs) {
//
//		if (callback == null) {
//			return false;
//		}
//
//		try {
//			if (currentNumberVMs > recommendedNrVMs) {
//				int delta = currentNumberVMs - recommendedNrVMs;
//				callback.removeVM(serviceID, imageID, delta, spAddress);
//				log.debug("Called removeVM for serviceID: " + serviceID
//						+ ", imageID: " + imageID + " delta: " + delta + " using spAddress: " + spAddress);
//			} else if (currentNumberVMs < recommendedNrVMs) {
//				int delta = recommendedNrVMs - currentNumberVMs;
//				callback.addVM(serviceID, serviceManifest, imageID, delta, spAddress);
//				log.debug("Called addVM for serviceID: " + serviceID
//						+ ", imageID: " + imageID + " delta: " + delta + " using spAddress: " + spAddress);
//			}
//		} catch (Exception e) {
//			log.info("Failed to deliver recommendation");
//			return false;
//		}
//
//		return true;
//	}
//
//	/**
//	 * Schedules predictions at the specified interval
//	 */
//	private void schedulePrediction(long predictionInterval, long initialDelay) {
//		if (future != null) {
//			future.cancel(false);
//		}
//
//		future = ex.scheduleAtFixedRate(this, initialDelay, predictionInterval,
//				TimeUnit.MILLISECONDS);
//		log.info("Doing predictions at interval: " + predictionInterval + "ms");
//
//	}
//
//	@Override
//	public int getCurrentPrediction(String imageID, int timeSpanInMinutes) {
//
//		ImageController iController = imageControllers.get(imageID);
//
//		if (iController == null) {
//			log.warn("No current elasticity management for imageID: " + imageID);
//			return -1;
//		}
//
//		return iController.getCurrentAmount();
//	}
//
//	public synchronized void run() {
//		log.debug("Initiating predictions with " + imageControllers.size()
//				+ " elasticity controllers.");
//
//		try {
//
//			for (ImageController iController : imageControllers.values()) {
//
//				log.info("Processing controller for: "
//						+ iController.getImageID());
//
//				// Get KPI and do prediction
//				String imageID = iController.getImageID();
//				Set<String> kpiNames = iController.getKPINames();
//				List<MonitoringResourceDataset> data = monitoringSource
//						.getData(serviceID, kpiNames);
//				Integer prediction = getPrediction(iController, data);
//
//				// Check non-null prediction
//				if (prediction == null) {
//					log.warn("Failed to do prediction!");
//					return;
//				}
//
//				// Check previous recommendation, skip the rest if it would
//				// recommend the same
//				if (prediction.equals(iController.getCurrentAmount())) {
//					log.info("New prediction equals previous ("
//							+ iController.getCurrentAmount() + "), ignoring");
//					return;
//				}
//
//				// Check current running instances in the CO
//				String currentNrInstancesStr = callback.getNrInstances(
//						serviceID, imageID);
//				Integer currentNrInstances;
//
//				try {
//					currentNrInstances = Integer.valueOf(currentNrInstancesStr);
//				} catch (NumberFormatException e) {
//					log.warn("Current nr of instances returned from CO was not a number. found: "
//							+ currentNrInstancesStr);
//					continue;
//				}
//
//				log.debug("nrInstances for serviceID: " + serviceID
//						+ ", imageID: " + imageID + " resolved to: "
//						+ currentNrInstances);
//				log.debug("New prediction for imageID: " + imageID + " is: "
//						+ prediction);
//
//				if (currentNrInstances < 0) {
//					log.warn("Communication with Cloud Optimizer failed. Cannot proceed.");
//					continue;
//				}
//
//				if (prediction != null && prediction != -1
//						&& prediction != currentNrInstances) {
//					if (notifyCallback(imageID, prediction, currentNrInstances)) {
//						iController.setCurrentAmount(prediction);
//						log.info("Updated prediction for imageID: " + imageID
//								+ " to: " + prediction);
//					} else {
//						log.info("Failed to send recommendation, updated prediction ignored for imageID: "
//								+ imageID
//								+ ". (Suggested: "
//								+ prediction
//								+ ", was: " + currentNrInstances);
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			log.warn("Exception during processing", e);
//			e.printStackTrace();
//		}
//
//	}
//
//	private Integer getPrediction(ImageController iController,
//			List<MonitoringResourceDataset> data) {
//
//		Integer maxPrediction = -1;
//		for (ElasticityRule eRule : iController.getRuleSet()) {
//			Integer newPrediction = applyRule(eRule, data);
//			if (newPrediction != null) {
//				maxPrediction = Math.max(maxPrediction, newPrediction);
//			}
//		}
//
//		return maxPrediction;
//	}
//
//	private Integer applyRule(ElasticityRule eRule,
//			List<MonitoringResourceDataset> data) {
//
//		// Read values from eRule
//		String kpiName = eRule.getKPIName();
//		int quota = eRule.getQuota();
//		float tolerance = eRule.getTolerance();
//
//		// Just find the most recent value in the set and use that value
//		MonitoringResourceDataset mostRecent = getMostRecentValue(data, kpiName);
//
//		if (mostRecent == null) {
//			log.debug("Found no monitoring data for KPI: " + kpiName);
//			return null;
//		}
//
//		boolean alreadyParsed = isValueAlreadyParsed(mostRecent,
//				previousMonitoringValue);
//		if (alreadyParsed) {
//			log.info("Already parsed this value, ignoring");
//			return null;
//		} else {
//			previousMonitoringValue = mostRecent;
//		}
//
//		double value = Double.valueOf(mostRecent.getMetric_value()); // XXX
//																		// this
//																		// can
//																		// get
//																		// messy
//
//		int integerRep = (int) (value / quota);
//		int remainder = (int) (value % quota);
//
//		// TODO: This interprets the tolerance value as a percentage.
//		// Double-check this later.
//		int toleratedLimit = (int) (quota * (tolerance / 100));
//		if (remainder > toleratedLimit) {
//			integerRep++;
//		} else {
//		}
//
//		integerRep = Math.max(1, integerRep);
//
//		log.debug("Recommending: " + integerRep + " for KPI: " + kpiName
//				+ ", value: " + value + ", quota: " + quota + ", tolerance: "
//				+ tolerance);
//
//		return integerRep;
//	}
//
//	private boolean isValueTooOld(MonitoringResourceDataset mostRecent) {
//
//		Date oneMinuteAgo = new Date(System.currentTimeMillis() - 60000);
//		if (mostRecent.getMetric_timestamp().before(oneMinuteAgo)) {
//			return true;
//		}
//
//		return false;
//	}
//
//	private boolean isValueAlreadyParsed(MonitoringResourceDataset newValue,
//			MonitoringResourceDataset previous) {
//
//		if (previous == null) {
//			return false;
//		}
//
//		return newValue.getMetric_timestamp().equals(
//				previous.getMetric_timestamp());
//	}
//
//	private MonitoringResourceDataset getMostRecentValue(
//			List<MonitoringResourceDataset> data, String kpiName) {
//		Map<String, MonitoringResourceDataset> mostRecentPerVM = new HashMap<String, MonitoringResourceDataset>();
//
//		for (MonitoringResourceDataset mData : data) {
//
//			if (!mData.getMetric_name().equals(kpiName)) {
//				log.info("Wrong KPI name, expected: " + kpiName + " found: "
//						+ mData.getMetric_name());
//				continue;
//			}
//
//			String vmID = mData.getMonitoring_information_collector_id();
//			MonitoringResourceDataset mostRecent = mostRecentPerVM.get(vmID);
//
//			if (mostRecent == null) {
//				mostRecentPerVM.put(vmID, mData);
//			} else if (mData.getMetric_timestamp().after(
//					mostRecent.getMetric_timestamp())) {
//				mostRecentPerVM.put(vmID, mData);
//			}
//		}
//
//		log.info("Combined " + data.size() + " values into "
//				+ mostRecentPerVM.size());
//
//		if (mostRecentPerVM.size() < 1) {
//			return null;
//		} else {
//			MonitoringResourceDataset combinedValue = combineValues(mostRecentPerVM
//					.values());
//			return combinedValue;
//		}
//	}
//
//	private MonitoringResourceDataset combineValues(
//			Collection<MonitoringResourceDataset> collection) {
//
//		// TODO: This way of combining values may have some flaws!
//		MonitoringResourceDataset combinedValue = null;
//		for (MonitoringResourceDataset mData : collection) {
//
//			if (isValueTooOld(mData)) {
//				log.info("Data too old, ignoring");
//				continue;
//			}
//
//			if (combinedValue == null) {
//				combinedValue = mData;
//				log.info("Setting empty combined value to first value");
//				continue;
//			}
//
//			// Update the value
//			try {
//				// TODO messy conversion
//				Double value = Double.valueOf(combinedValue.getMetric_value());
//				Double newValue = Double.valueOf(mData.getMetric_value());
//				Double combined = value + newValue;
//				combinedValue.setMetric_value(combined.toString());
//				log.info("Combined values " + value + ", " + newValue
//						+ ", new value is: " + combined);
//			} catch (NumberFormatException e) {
//				log.info("Failed to combine value.", e);
//			}
//
//			// Set the date to the most recent of the two dates
//			if (mData.getMetric_timestamp().after(
//					combinedValue.getMetric_timestamp())) {
//				combinedValue.setMetric_timestamp(mData.getMetric_timestamp());
//				log.info("Updated date of combined value to more recent.");
//			}
//		}
//
//		return combinedValue;
//	}
//
//	@Override
//	public void setManifest(String serviceManifest) {
//		this.serviceManifest = serviceManifest;
//
//		try {
//			this.imageControllers = Util
//					.initiateImageControllers(serviceManifest);
//			log.debug("Updated manifest for serviceID: " + serviceID);
//		} catch (Exception e) {
//			log.warn("Failed to parse the new manifest, ignoring.");
//			throw new IllegalArgumentException(e);
//		}
//	}
//
//	@Override
//	public ElasticityCallback getCallback() {
//		return callback;
//	}
//
//	@Override
//	public String getServiceManifest() {
//		return serviceManifest;
//	}
//
//	@Override
//	public void setMode(boolean Proactive) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void destroy() {
//		// TODO Auto-generated method stub
//		
//	}
//}
