package eu.optimis.elasticityengine;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.optimis.elasticityengine.monitoring.FakeMonitoringSource;
import eu.optimis.elasticityengine.monitoring.MonitoringSource;
import eu.optimis.elasticityengine.monitoring.RESTMonitoringSource;
import eu.optimis.elasticityengine.sc.HybridServiceController;
import eu.optimis.elasticityengine.sc.ServiceController;
import eu.optimis.elasticityengine.callback.*;;

/**
 * Main class for the Elasticity Engine. Routes general EE calls to per-service
 * specific instances of ElasticityControllers.
 *
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
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
public class ElasticityEngineImpl implements ElasticityEngine, ElasticityEngineImplMBean {
    
    private final Map<String, ServiceController> controllers;
    private final static Logger log = Logger.getLogger(ElasticityEngineImpl.class);

    public ElasticityEngineImpl() {
        controllers = new HashMap<String, ServiceController>(0);
        log.info("ElasticityEngine Y3 initiated: ApplyRule modified");
    }

    @Override
    public void startElasticity(String serviceID, String serviceManifest,boolean LowRiskMode, String spAddress) {

        if (serviceID == null) {
            throw new IllegalArgumentException("Service ID may not be null");
        }

        if (serviceManifest == null) {
            throw new IllegalArgumentException("ServiceManifest argument must be non-null");
        }
        
        ServiceController controller = controllers.get(serviceID);
        RESTCOElasticityCallback callback=null;
        if (controller == null) {
            long interval_ms = 15000;
            long delay_ms = 5000;
            String host = "localhost";
            int port = 8080;
            MonitoringSource mSource = null;
            if ("true".equals(System.getProperty("fakeMonitoring"))) {
                log.debug("USING FAKE MONITORING and fake callback");
                mSource = new FakeMonitoringSource();
                CallbackPrinter callback1=new CallbackPrinter();
                controller.setCallback(callback1);
            } else {
                mSource = new RESTMonitoringSource(host, port);
                callback=new RESTCOElasticityCallback(host, port);
            }

            // MonitoringSource
            try {
	            controller = new HybridServiceController(serviceID, serviceManifest, interval_ms, delay_ms,
	                    mSource, spAddress, LowRiskMode);
	            controllers.put(serviceID, controller);
	            log.debug("Running After put: '" + controllers.keySet() + "'");
	            log.debug("Created Elasticity controller for Service: '" + serviceID + "'");
            } catch (IllegalArgumentException e) {
            	log.warn("Failed to start elasticity: " + e.getMessage());
            	return;
            }
        } else {
            controller.setManifest(serviceManifest);
            log.warn("Elasticity controller already exists for Service: '" + serviceID + "'"
                    + ", updating rules instead.");
        }
        if (callback!=null){
        controller.setCallback(callback);
        log.info("Started elasticity and registered callback for serviceID: '" + serviceID + "'");}
        else{
        	log.error("Could not register callback interface for Service: '" + serviceID + "'");
        }
    }

    @Override
    public void stopElasticity(String serviceID) {
    	log.debug("Running : '" + controllers.keySet() + "'");
        ServiceController controller = controllers.get(serviceID);
        if (controller == null) {
            log.warn("No EE running for this serviceID: " + serviceID);
            return;
        }
        controllers.get(serviceID).destroy();
        controllers.remove(serviceID);
        log.info("Removed elasticitycontroller for serviceID: " + serviceID);
    }

    @Override
    public void updateCallback(String serviceID, ElasticityCallback callback) {
        ServiceController controller = controllers.get(serviceID);
        if (controller == null) {
            throw new IllegalArgumentException("No running elasticity for serviceID: '" + serviceID + "'");
        }

        controller.setCallback(callback);
        log.info("Registered callback for serviceID: " + serviceID);
    }


    @Override
    public void updateElasticityRules(String serviceID, String serviceManifest) {
        ServiceController controller = controllers.get(serviceID);
        if (controller == null) {
            throw new IllegalArgumentException("No running elasticity for serviceID: '" + serviceID + "'");
        }

        controller.setManifest(serviceManifest);
        log.info("Updated elasticityrules for serviceID: " + serviceID);
    }

    @Override
    public int getPrediction(String serviceID, String imageID, int timeSpanInMinutes) {
        ServiceController controller = controllers.get(serviceID);
        if (controller == null) {
            throw new IllegalArgumentException("No running elasticity for serviceID: '" + serviceID + "'");
        }

        int prediction = controller.getCurrentPrediction(imageID, timeSpanInMinutes);
        log.info("Got prediction: " + prediction + " for serviceID: '" + serviceID + "', imageID: '"
                + imageID + "', timeSpanInMinutes: '" + timeSpanInMinutes);
        return prediction;
    }

    @Override
    public String triggerGetNrInstances(String serviceID, String imageID) {
        ServiceController controller = controllers.get(serviceID);
        
        if (controller == null) {
            return "Illegal ServiceID";
        }
        
        return controller.getCallback().getNrInstances(serviceID, imageID);
    }

    @Override
    public String triggerAddVM(String serviceID, String imageID, int delta, String spAddress) {
        ServiceController controller = controllers.get(serviceID);

        if (controller == null) {
            return "Illegal ServiceID";
        }

        controller.getCallback().addVM(serviceID, controller.getServiceManifest(), imageID, delta, spAddress);
        return "Triggered addVM";

    }

    @Override
    public String triggerRemoveVM(String serviceID, String imageID, int delta, String spAddress) {
        ServiceController controller = controllers.get(serviceID);

        if (controller == null) {
            return "Illegal ServiceID";
        }

        controller.getCallback().removeVM(serviceID, imageID, delta, spAddress);
        return "Triggered removeVM";
    }

	@Override
	public void setMode(String serviceID, boolean Proactive) {
		ServiceController controller = controllers.get(serviceID);
        if (controller == null) {
            throw new IllegalArgumentException("No running elasticity for serviceID: '" + serviceID + "'");
        }

        controller.setMode(Proactive);
        log.info("High Cost/Low Risk mode " + Proactive);
		
	}
    
}
