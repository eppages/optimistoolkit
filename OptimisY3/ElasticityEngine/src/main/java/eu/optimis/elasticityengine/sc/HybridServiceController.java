package eu.optimis.elasticityengine.sc;

import java.rmi.dgc.VMID;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

import org.apache.log4j.Logger;
import net.astesana.javaluator.DoubleEvaluator;

import eu.optimis.elasticityengine.sc.EvaluatorClass;
import eu.optimis.elasticityengine.DaemonThreadFactory;
import eu.optimis.elasticityengine.ElasticityCallback;
import eu.optimis.elasticityengine.ElasticityRule;
import eu.optimis.elasticityengine.monitoring.MonitoringSource;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;

/**
 * Implementation of the service controller interface. Offers a
 * reactive and proactive service controller that has two modes of operation, either Reactive only or 
 * hybrid mode. The reactive only controller mode is similar to Y1's service controller. The hybrid mode
 * of operation uses a reactive controller and a proactive controller to calculate the required number of VMs, 
 * then adjusts the amount of running VMs.
 * 
 * @author Ahmed Ali-Eldin (<a
 *         href="mailto:ahmeda@cs.umu.se">ahmeda@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public class HybridServiceController implements Runnable, ServiceController {

	protected final static Logger log = Logger
			.getLogger(HybridServiceController.class);

	private MonitoringSource monitoringSource;
	protected Map<String, ElasticityRule> rules;
	private ElasticityCallback callback;
	private final String spAddress;
	private final String serviceID;

	private Map<String, ImageController> imageControllers;
				
	private ScheduledFuture<?> future; // Schedule a list of events in the future
	private ScheduledExecutorService ex;

	private String serviceManifest;
	private MonitoringResourceDataset previousMonitoringValue;

	//Proactive controller variables
	private boolean Proactive;
	private Integer proactivePrediction=0;
	private int estimationWindow=2;
	private int timeSinceLastEstimation=0;
	private Integer reactivePrediction;
	private Integer prediction;
	private double VMAggregatorTime=0;
	private double TimeAggregator=0;
	private double VMAggregatorEstTime;
	private double proactControllerAggregator;
	//private double CapacityChange;

	
/*First constructor uses year 1 elasticity engine with no changes.
 * while the second constructor can activate the proactive part by setting the last parameter "boolean Proactive" to true*/
	
	public HybridServiceController(String serviceID, String serviceManifest,
			long predictionInterval, long initialDelay,
			MonitoringSource monitoringSource, String spAddress) {

		this.serviceID = serviceID;
		this.Proactive=true;
		this.serviceManifest = serviceManifest;
		this.imageControllers = Util.initiateImageControllers(serviceManifest);
		log.info(imageControllers.size()+"  "+imageControllers.toString());
		
		this.monitoringSource = monitoringSource;
		this.spAddress = spAddress;

		// Schedule periodic predictions
		ex = Executors.newScheduledThreadPool(2, new DaemonThreadFactory());
		schedulePrediction(predictionInterval, initialDelay);
		log.warn("EE version 0.0.5-SNAPSHOT starting, Proactive = "+Proactive);
	}

	
	public HybridServiceController(String serviceID, String serviceManifest,
			long predictionInterval, long initialDelay,
			MonitoringSource monitoringSource, String spAddress, boolean Proactive) {

		this.serviceID = serviceID;
		this.Proactive=Proactive;
		this.serviceManifest = serviceManifest;
		this.imageControllers = Util.initiateImageControllers(serviceManifest);
		this.monitoringSource = monitoringSource;
		this.spAddress = spAddress;


		// Schedule periodic predictions
		ex = Executors.newScheduledThreadPool(2, new DaemonThreadFactory());
		schedulePrediction(predictionInterval, initialDelay);
		log.warn("EE version 0.0.5-SNAPSHOT, Proactive = "+Proactive);
	}
	
	@Override
	public void destroy(){
		log.debug("calling destroy");
		future.cancel(true);
		ex.shutdownNow();
	}
	@Override
	public void setMode(boolean Proactive){
		this.Proactive=Proactive;
	}
	
	@Override
	public void setCallback(ElasticityCallback callback) {
		this.callback = callback;
	}

	@Override
	public void unSetCallback() {
		this.callback = null;
	}

	protected boolean notifyCallback(String imageID, int recommendedNrVMs,
			int currentNumberVMs) {

		if (callback == null) {
			return false;
		}

		try {
			if (currentNumberVMs > recommendedNrVMs) {
				if (recommendedNrVMs>0 && currentNumberVMs>1){
					int delta = currentNumberVMs - recommendedNrVMs;
					callback.removeVM(serviceID, imageID, delta, spAddress);
					log.debug("Called removeVM for serviceID: " + serviceID
							+ ", imageID: " + imageID + " delta: " + delta + " using spAddress: " + spAddress);
				}
				else if(currentNumberVMs>1){
					recommendedNrVMs=1;
					int delta = currentNumberVMs - recommendedNrVMs;
					log.debug("Calling removeVM for serviceID: " + serviceID
							+ ", imageID: " + imageID + " delta: " + delta + " using spAddress: " + spAddress);
					callback.removeVM(serviceID, imageID, delta, spAddress);
									
				}
				
			} else if (currentNumberVMs < recommendedNrVMs && currentNumberVMs!=0 ) {
				int delta = recommendedNrVMs - currentNumberVMs;
				log.debug("Called addVM for serviceID: " + serviceID
						+ ", imageID: " + imageID + " delta: " + delta + " using spAddress: " + spAddress);
				callback.addVM(serviceID, serviceManifest, imageID, delta, spAddress);
			}
		} catch (Exception e) {
			log.info("Failed to deliver recommendation"+e.getMessage()+ "  "+e.getCause().getMessage());
			return false;
		}

		return true;
	}
	

	/**
	 * Schedules predictions at the specified interval
	 */
	private void schedulePrediction(long predictionInterval, long initialDelay) {
		if (future != null) {
			future.cancel(false);
		}

		future = ex.scheduleAtFixedRate(this, initialDelay, predictionInterval,
				TimeUnit.MILLISECONDS);
		log.info("Doing predictions at interval: " + predictionInterval + "ms");

	}

	@Override
	public int getCurrentPrediction(String imageID, int timeSpanInMinutes) {

		ImageController iController = imageControllers.get(imageID);

		if (iController == null) {
			log.warn("No current elasticity management for imageID: " + imageID);
			return -1;
		}

		return iController.getCurrentAmount();
	}

	public synchronized void run() {
		log.debug("Initiating predictions with " + imageControllers.size()
				+ " elasticity controllers.");
		try {

			for (ImageController iController : imageControllers.values()) {

				log.info("Processing controller for: "
						+ iController.getImageID());
				// Check current running instances in the CO
				String imageID = iController.getImageID();
				String currentNrInstancesStr = callback.getNrInstances(
						serviceID, imageID);
				Integer currentNrInstances;
				try {
					currentNrInstances = Integer.valueOf(currentNrInstancesStr);
				} catch (NumberFormatException e) {
					log.warn("Current nr of instances returned from CO was not a number. found: "
							+ currentNrInstancesStr);
					continue;
				}
				VMAggregatorTime+=currentNrInstances;
				VMAggregatorEstTime+=currentNrInstances;
				// Get KPI and do prediction
				Set<String> kpiNames = iController.getKPINames();
				log.info(kpiNames);
				Map<String, List<MonitoringResourceDataset>> data = monitoringSource
						.getData(serviceID, kpiNames);
//				log.debug("Back to HSC method, got following from monitoring"+data.toString());
				reactivePrediction = getReactivePrediction(iController, data);
				TimeAggregator++;
				if (this.Proactive==true) { 			 //if the proactive component is enabled, get the proactive prediction
					if (estimationWindow==timeSinceLastEstimation){
						timeSinceLastEstimation=0;
						proactivePrediction = getProactivePrediction(iController, data,currentNrInstances);	
						VMAggregatorEstTime=0;
					}
					timeSinceLastEstimation++;
				}
				log.debug("Current number VMs. "+currentNrInstances);
				if (currentNrInstances<reactivePrediction && proactivePrediction >0){
					prediction=reactivePrediction+proactivePrediction;
					
				}
				else if (currentNrInstances<reactivePrediction && proactivePrediction <=0||!Proactive) {
					prediction=reactivePrediction;
					proactControllerAggregator=0;
					
				}
				else if (currentNrInstances>reactivePrediction) {
					prediction=currentNrInstances+proactivePrediction;
				}
				else {
					prediction=currentNrInstances;
				}
				// Check non-null prediction
				if (prediction == null) {
					log.warn("Failed to do prediction!");
					return;
				}
				
				// Check previous recommendation, skip the rest if it would
				// recommend the same
				if (prediction.equals(iController.getCurrentAmount())) {
					log.info("New prediction equals previous ("
							+ iController.getCurrentAmount() + "), ignoring");
					return;
				}

				
				

				log.debug("nrInstances for serviceID: " + serviceID
						+ ", imageID: " + imageID + " resolved to: "
						+ currentNrInstances);
				log.debug("New prediction for imageID: " + imageID + " is: "
						+ prediction);

				if (currentNrInstances < 0) {
					log.warn("Communication with Cloud Optimizer failed. Cannot proceed.");
					continue;
				}

				if (prediction != null && prediction != -1
						&& prediction != currentNrInstances) {
					if (notifyCallback(imageID, prediction, currentNrInstances)) {
						iController.setCurrentAmount(prediction);
						log.info("Updated prediction for imageID: " + imageID
								+ " to: " + prediction);
					} else {
						log.info("Failed to send recommendation, updated prediction ignored for imageID: "
								+ imageID
								+ ". (Suggested: "
								+ prediction
								+ ", was: " + currentNrInstances);
					}
				}
			}

		} catch (Exception e) {
			log.warn("Exception during processing", e);
			e.printStackTrace();
		}

	}

	private Integer getReactivePrediction(ImageController iController,
			Map<String, List<MonitoringResourceDataset>> data) {

		Integer maxPrediction = 1;
		for (ElasticityRule eRule : iController.getRuleSet()) {
			Integer newPrediction = applyRule(eRule, data);
			if (newPrediction != null) {
				maxPrediction = Math.max(maxPrediction, newPrediction);
			}
		}

		return maxPrediction;
	}

	private Integer getProactivePrediction(ImageController iController,
			Map<String, List<MonitoringResourceDataset>> data, Integer currentNrInstances) {
		Integer maxPrediction = 0;
		for (ElasticityRule eRule : iController.getRuleSet()) {
			Integer newPrediction = applyProactiveRule(eRule, data,currentNrInstances);
			if (newPrediction != null) {
				if (newPrediction>0){
					maxPrediction = Math.max(maxPrediction, newPrediction);
				}
				else if (maxPrediction==0){
					maxPrediction=newPrediction;
				}
				else{
					maxPrediction = Math.max(maxPrediction, newPrediction);
				}
					
				}
		}
		return maxPrediction;
		
	}
	
	private Integer applyRule(ElasticityRule eRule,
			Map<String, List<MonitoringResourceDataset>> data) {

		double value;
		// Read values from eRule
		String kpiName = eRule.getKPIName();
    	String[] temp = monitoringSource.getKPIs();
    	log.debug("List from monitoring module"+temp);
    	boolean SplittedTemp=true;
    	String concatenatedKPI="";
    	for(int i =0; i < temp.length ; i++){	
    		if (i%2==0){ 
    				List<MonitoringResourceDataset> dataKey = data.get(temp[i]);
    				Double mostRecent=getMostRecentValue(dataKey, temp[i]);
    				log.debug("Calling get most recent value for: "+temp[i]+" Got "+mostRecent);
    				if (mostRecent!=null){
    				concatenatedKPI+=mostRecent;
    				log.info("Most recent Value for" + temp[i] + " is" + mostRecent);
    				}
    				else {
    					SplittedTemp=false;
                        log.info("Most recent Value for" + temp[i] + " is" + mostRecent);
    				}
    		}
    		else{
    			concatenatedKPI+=temp[i];
    		}
    	}
     
		int quota = eRule.getQuota();
		float tolerance = eRule.getTolerance();

		// Just find the most recent value in the set and use that value

		if (concatenatedKPI == "") {
			log.warn("Found no monitoring data for KPI: " + kpiName);
			return null;
		}
	    log.debug("apply-rule on: "+concatenatedKPI);
		// Create a new evaluator
	    EvaluatorClass evaluator = new EvaluatorClass();
	    log.debug("initiated evaluator");
	    
	    // Evaluate an expression
	    if (SplittedTemp){
	    value = evaluator.eval(concatenatedKPI);
	    }
	    else
	    { value=0;
	    }
	    // Ouput the result
	    log.debug(concatenatedKPI + " = " + value);

		int integerRep = (int) Math.ceil(value / quota);
		int remainder = (int) (value % quota);

		integerRep = Math.max(1, integerRep);
		log.debug(" for KPI: " + kpiName
				+ ", value: " + value + ", quota: " + quota + ", tolerance: "
				+ tolerance);
		
		log.debug(" for KPI: " + kpiName
				+ ", value: " + value + ", quota: " + quota + ", tolerance: "
				+ tolerance);

		return integerRep;
	}
	
	private Integer applyProactiveRule(ElasticityRule eRule,
			Map<String, List<MonitoringResourceDataset>> data, Integer currentNrInstances) {

		double value;
		// Read values from eRule
		String kpiName = eRule.getKPIName();
    	String[] temp = monitoringSource.getKPIs();
    	log.debug("List from monitoring module"+temp);
    	boolean SplittedTemp=true;
    	String concatenatedKPI="";
    	for(int i =0; i < temp.length ; i++){	
    		if (i%2==0){ 
    				List<MonitoringResourceDataset> dataKey = data.get(temp[i]);
    				Double mostRecent=getMostRecentValue(dataKey, temp[i]);
    				log.debug("Calling get most recent value for: "+temp[i]+" Got "+mostRecent);
    				if (mostRecent!=null){
    				concatenatedKPI+=mostRecent;
    				log.info("Most recent Value for" + temp[i] + " is" + mostRecent);
    				}
    				else {
    					SplittedTemp=false;
                        log.info("Most recent Value for" + temp[i] + " is" + mostRecent);
    				}
    		}
    		else{
    			concatenatedKPI+=temp[i];
    		}
    	}
		int quota = eRule.getQuota();
		float tolerance = eRule.getTolerance();

		// Just find the most recent value in the set and use that value

		if (concatenatedKPI == "") {
			log.warn("Found no monitoring data for KPI: " + kpiName);

			return null;
		}

		// Create a new evaluator
	    EvaluatorClass evaluator = new EvaluatorClass();
	    // Evaluate an expression
	    if (SplittedTemp){
		    value = evaluator.eval(concatenatedKPI);
		    }
		    else
		    { value=0;
		    }	    // Ouput the result
	    log.debug(concatenatedKPI + " = " + value);

		int integerRep=0;
		double averageNumberVMTime=VMAggregatorTime/TimeAggregator;
		double averageNumberVMEstimWindow=VMAggregatorEstTime/estimationWindow;
		double deltaLoad=value-currentNrInstances*quota;
		proactControllerAggregator+=deltaLoad*averageNumberVMEstimWindow/(2*averageNumberVMTime*2*quota);
		
		if (Math.abs(deltaLoad)>tolerance){
			integerRep=(int) proactControllerAggregator;
			proactControllerAggregator-=integerRep;
		}


		log.debug(" for KPI: " + kpiName
				+ ", value: " + value + ", quota: " + quota + ", tolerance: "
				+ tolerance);

		return integerRep;
	}


	private boolean isValueTooOld(MonitoringResourceDataset mostRecent) {

		Date oneMinuteAgo = new Date(System.currentTimeMillis() - 60000);
		if (mostRecent.getMetric_timestamp().before(oneMinuteAgo)) {
			return true;
		}

		return false;
	}

	private boolean isValueAlreadyParsed(MonitoringResourceDataset newValue,
			MonitoringResourceDataset previous) {

		if (previous == null) {
			return false;
		}

		return newValue.getMetric_timestamp().equals(
				previous.getMetric_timestamp());
	}

	private Double getMostRecentValue(
			List<MonitoringResourceDataset> data, String kpiString) {
		Map<String, MonitoringResourceDataset> mostRecentPerVM = new HashMap<String, MonitoringResourceDataset>();
		
		if (kpiString.indexOf(':') == -1) {
        	//log.warn("Could not find suitable prefix for kpiString: " + kpiString + ", defaulting to \"service:\"");
        	kpiString = "service:" + kpiString;
        }
		String[] parts = kpiString.split(":");
        //log.warn("KPI Type: "+ parts[0]+" KPIName: "+parts[1]);
        String prefix = parts[0];
        String kpiName = parts[1];
        
		for (MonitoringResourceDataset mData : data) {

			if (!mData.getMetric_name().equals(kpiName)&& (!prefix.equals("service"))) 
			 {
				log.info("Wrong KPI name, expected: " + kpiName + " found: "
						+ mData.getMetric_name()+" Or wrong serviceID, expected:"+ serviceID+" Got "+mData.getService_resource_id()+" Resource Type is:"
						+ mData.getResource_type());
				continue;
			}

			String vmID = mData.getVirtual_resource_id();
			if (vmID.equals(null)){
				log.debug("Got Null for VM ID");
				continue;
			}
				
			MonitoringResourceDataset mostRecent = mostRecentPerVM.get(vmID);
			if (mostRecent == null) {
				mostRecentPerVM.put(vmID, mData);
			} else if (mData.getMetric_timestamp().after(
					mostRecent.getMetric_timestamp())) {
				mostRecentPerVM.put(vmID, mData);
				
			}
		}

	//	log.info("Combined " + data.size() + " values into "
		//		+ mostRecentPerVM.size());

		if (mostRecentPerVM.size() < 1) {
			return null;
		} else {
			Double combinedValue = combineValues(mostRecentPerVM
					.values());
			log.debug("Combined Value: "+combinedValue);
			return combinedValue;
		}
	}

	private Double combineValues(
			Collection<MonitoringResourceDataset> collection) {

		// TODO: This way of combining values may have some flaws!
		Double combinedValue = null;
		for (MonitoringResourceDataset mData : collection) {

			if (isValueTooOld(mData)) {
				log.warn("Data too old, ignoring: "+ mData.getMetric_timestamp()+" Service resource ID: "+mData.getService_resource_id());
				log.debug("Data too old, ignoring: "+ mData.getMetric_timestamp()+" Service resource ID: "+mData.getService_resource_id());
				continue;
			}

			if (combinedValue == null) {
				combinedValue = Double.valueOf(mData.getMetric_value());
				log.info("Setting empty combined value to first value");
				continue;
			}

			// Update the value
			try {
				// TODO messy conversion
				Double newValue = Double.valueOf(mData.getMetric_value());
				log.debug("Adding: " + newValue+ " to "+combinedValue);
				combinedValue = combinedValue + newValue;
				
			} catch (NumberFormatException e) {
				log.warn("Error combining values");
				log.info("Failed to combine value.", e);
			}

			// Set the date to the most recent of the two dates
//			if (mData.getMetric_timestamp().after(
//					combinedValue.getMetric_timestamp())) {
//				combinedValue.setMetric_timestamp(mData.getMetric_timestamp());
				//log.info("Updated date of combined value to more recent.");
//			}
		}
		log.info("Combined values, new value is: " + combinedValue);
		return combinedValue;
	}

	@Override
	public void setManifest(String serviceManifest) {
		this.serviceManifest = serviceManifest;

		try {
			this.imageControllers = Util
					.initiateImageControllers(serviceManifest);
			log.debug("Updated manifest for serviceID: " + serviceID);
		} catch (Exception e) {
			log.warn("Failed to parse the new manifest, ignoring.");
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public ElasticityCallback getCallback() {
		return callback;
	}

	@Override
	public String getServiceManifest() {
		return serviceManifest;
	}
}
