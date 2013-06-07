/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.core;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.ecoefficiencytool.core.tools.ConfigManager;
import java.util.HashMap;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class ProactiveService extends Thread{

    private static Logger log = Logger.getLogger(ProactiveService.class);
    private EcoEffAssessorIP assessor;
    private EcoEffForecasterIP forecaster;
    private CloudOptimizerRESTClient co;
    private String serviceId;
    private long timeout;
    private boolean finish = false;
    
    //Proactive Thresholds.
    private HolisticManagementRESTClient hm;
    private Double serviceEnEffTH;
    private Double serviceEcoEffTH;
    private HashMap<String, Double> vmEnEffTH;
    private HashMap<String, Double> vmEcoEffTH;

    public ProactiveService(EcoEffAssessorIP assessor, EcoEffForecasterIP forecaster, String serviceId, Long timeout) {
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        
        this.assessor = assessor;
        this.forecaster = forecaster;
        this.serviceId = serviceId;
        co = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"));
        if(timeout != null) {
            this.timeout = timeout.longValue();
        } else {
            this.timeout = configEco.getLong("samplingPeriod");
        }
        
        //Set Proactive Thresholds
        hm = new HolisticManagementRESTClient(configOptimis.getString("optimis-ipvm"));
        serviceEnEffTH = null;
        serviceEcoEffTH = null;
        vmEnEffTH = new HashMap<String, Double>();
        vmEcoEffTH = new HashMap<String, Double>();
    }

    public void setServiceEnEffTH(Double value) {
        log.info("Set energy efficiency TH of service " + serviceId + " to " + value.toString() + " MWIPS/W.");
        serviceEnEffTH = value;
    }
    
    public void setServiceEcoEffTH(Double value) {
        log.info("Set ecological efficiency TH of service " + serviceId + " to " + value.toString() + " MWIPS/grCO2.");
        serviceEcoEffTH = value;
    }
    
    public void setVMEnEffTH(String vmId, Double value) {
        log.info("Set energy efficiency TH of VM " + vmId + " to " + value.toString() + " MWIPS/W.");
        vmEnEffTH.put(vmId, value);
    }
    
    public void setVMEcoEffTH(String vmId, Double value) {
        log.info("Set ecological efficiency TH of VM " + vmId + " to " + value.toString() + " MWIPS/grCO2.");
        vmEcoEffTH.put(vmId, value);
    }
    
    public void stopProactiveService() {
        this.finish = true;
    }
    
    public String getServiceId() {
        return this.serviceId;
    }
    
    private void checkServiceTHAndNotify(double eco[], long interval) {
        
        if(serviceEnEffTH != null) {
            if((eco[0] != -1.0) && (eco[0] < serviceEnEffTH.doubleValue())) {
                log.info("Energy eff. TH of service " + serviceId + " (" + serviceEnEffTH + " MWIPS/W) surpassed in " + interval + "ms. Value: " + eco[0]);
                hm.notifyServiceEco(serviceId, eco[0], "energy", interval);
            }
        }
        
        if(serviceEcoEffTH != null) {
            if((eco[1] != -1.0) && (eco[1] < serviceEcoEffTH.doubleValue())) {
                log.info("Ecological eff. TH of service " + serviceId + " (" + serviceEcoEffTH + " MWIPS/grCO2) surpassed in " + interval + "ms. Value: " + eco[1]);
                hm.notifyServiceEco(serviceId, eco[1], "ecological", interval);
            }
        }
    }
    
    private void checkVMTHAndNotify(String vmId, double eco[], long interval) {
        if(vmEnEffTH.get(vmId) != null) {
            if((eco[0] != -1.0) && (eco[0] < vmEnEffTH.get(vmId).doubleValue())) {
                log.info("Energy eff. TH of vm " + vmId + " (" + vmEnEffTH.get(vmId) + " MWIPS/W) surpassed in " + interval + "ms. Value: " + eco[0]);
                hm.notifyVMEco(vmId, eco[0], "energy", interval);
            }
        }
        
        if(vmEcoEffTH.get(vmId) != null) {
            if((eco[1] != -1.0) && (eco[1] < vmEcoEffTH.get(vmId).doubleValue())) {
                log.info("Ecological eff. TH of vm " + vmId + " (" + vmEcoEffTH.get(vmId) + " MWIPS/grCO2) surpassed in " + interval + "ms. Value: " + eco[1]);
                hm.notifyVMEco(vmId, eco[1], "ecological", interval);
            }
        }
    }

    @Override
    public void run() {
        while(!finish) {
            try {
                //Proactive Service EcoAssessment
                log.debug("Starting Proactive Service EcoAssessment (" + serviceId + ")");
                double ecoService[] = assessor.assessServiceEcoEfficiency(serviceId);
                checkServiceTHAndNotify(ecoService, 0);
                double ecoServiceForecast[] = forecaster.forecastServiceEcoEfficiency(serviceId, timeout, null);
                checkServiceTHAndNotify(ecoServiceForecast, timeout);
                log.debug("Finished Proactive Service EcoAssessment (" + serviceId + ")");
                
                //Proactive VM EcoAssessment
                log.debug("Starting Proactive VMs of service " + serviceId);
                for(String vmId : co.getVMsIdsOfService(serviceId)) {
                    double ecoVM[] = assessor.assessVMEcoEfficiency(vmId);
                    checkVMTHAndNotify(vmId, ecoVM, 0);
                    double ecoVMForecast[] = forecaster.forecastVMEcoEfficiency(vmId, timeout);
                    checkVMTHAndNotify(vmId, ecoVMForecast, timeout);
                }
                log.debug("Finished Proactive VMs of service " + serviceId);
                
                Thread.sleep(timeout);
            } catch (Exception ex) {
                log.error("Proactive Service Exception. Error while assessing ecoefficiency of service:  " + serviceId);
                log.error(ex.getMessage());
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ex2) {
                    log.error("Error while performing sleep.");
                    log.error(ex2.getMessage());
                }
            }
        }
        log.debug("Stopping Proactive Ecoassessment of Service " + serviceId);
    }
}
