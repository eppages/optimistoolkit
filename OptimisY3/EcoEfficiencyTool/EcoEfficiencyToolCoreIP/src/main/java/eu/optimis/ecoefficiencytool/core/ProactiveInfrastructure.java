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
import java.util.HashMap;
import org.apache.log4j.Logger;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.ecoefficiencytool.core.tools.ConfigManager;
import java.io.PrintWriter;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author jsubirat
 */
public class ProactiveInfrastructure extends Thread {

    private static Logger log = Logger.getLogger(ProactiveInfrastructure.class);
    private EcoEffAssessorIP assessor;
    private EcoEffForecasterIP forecaster;
    private long timeout;
    private boolean finish = false;
    private CloudOptimizerRESTClient co;
    
    //Proactive Thresholds.
    private HolisticManagementRESTClient hm;
    private Double ipEnEffTH;
    private Double ipEcoEffTH;
    private HashMap<String, Double> nodeEnEffTH;
    private HashMap<String, Double> nodeEcoEffTH;
    

    public ProactiveInfrastructure(EcoEffAssessorIP assessor, EcoEffForecasterIP forecaster, Long timeout) {
        
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
                
        this.assessor = assessor;
        this.forecaster = forecaster;
        if (timeout != null) {
            this.timeout = timeout.longValue();
        } else {
            this.timeout = configEco.getLong("samplingPeriod");
        }
        co = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"));
        
        //Set Proactive Thresholds
        hm = new HolisticManagementRESTClient(configOptimis.getString("optimis-ipvm"));
        ipEnEffTH = null;
        ipEcoEffTH = null;
        nodeEnEffTH = new HashMap<String, Double>();
        nodeEcoEffTH = new HashMap<String, Double>();
    }

    public void setIpEnEffTH(Double value) {
        log.info("Set energy efficiency TH of IP to " + value.toString() + " MWIPS/W.");
        ipEnEffTH = value;
    }
    
    public void setIpEcoEffTH(Double value) {
        log.info("Set ecological efficiency TH of IP to " + value.toString() + " MWIPS/grCO2.");
        ipEcoEffTH = value;
    }
    
    public void setNodeEnEffTH(String nodeId, Double value) {
        log.info("Set energy efficiency TH of node " + nodeId + " to " + value.toString() + " MWIPS/W.");
        nodeEnEffTH.put(nodeId, value);
    }
    
    public void setNodeEcoEffTH(String nodeId, Double value) {
        log.info("Set ecological efficiency TH of node " + nodeId + " to " + value.toString() + " MWIPS/grCO2.");
        nodeEcoEffTH.put(nodeId, value);
    }
    
    public void stopProactiveInfrastructure() {
        this.finish = true;
    }
    
    private void checkIpTHAndNotify(double eco[], long interval) {
        
        if(ipEnEffTH != null) {
            if((eco[0] != -1.0) && (eco[0] < ipEnEffTH.doubleValue())) {
                log.info("Energy eff. TH of IP (" + ipEnEffTH + " MWIPS/W) surpassed in " + interval + "ms. Value: " + eco[0]);
                hm.notifyInfrastructureEco(eco[0], "energy", interval);
            }
        }
        
        if(ipEcoEffTH != null) {
            if((eco[1] != -1.0) && (eco[1] < ipEcoEffTH.doubleValue())) {
                log.info("Ecological eff. TH of IP (" + ipEcoEffTH + " MWIPS/grCO2) surpassed in " + interval + "ms. Value: " + eco[1]);
                hm.notifyInfrastructureEco(eco[1], "ecological", interval);
            }
        }
    }
    
    private void checkNodeTHAndNotify(String nodeId, double eco[], long interval) {
        if(nodeEnEffTH.get(nodeId) != null) {
            if((eco[0] != -1.0) && (eco[0] < nodeEnEffTH.get(nodeId).doubleValue())) {
                log.info("Energy eff. TH of node " + nodeId + " (" + nodeEnEffTH.get(nodeId) + " MWIPS/W) surpassed in " + interval + "ms. Value: " + eco[0]);
                hm.notifyPhysicalHostEco(nodeId, eco[0], "energy", interval);
            }
        }
        
        if(nodeEcoEffTH.get(nodeId) != null) {
            if((eco[1] != -1.0) && (eco[1] < nodeEcoEffTH.get(nodeId).doubleValue())) {
                log.info("Ecological eff. TH of node " + nodeId + " (" + nodeEcoEffTH.get(nodeId) + " MWIPS/grCO2) surpassed in " + interval + "ms. Value: " + eco[1]);
                hm.notifyPhysicalHostEco(nodeId, eco[1], "ecological", interval);
            }
        }
    }

    @Override
    public void run() {
        while (!finish) {
            try {
                //Proactive IP EcoAssessment
                log.debug("Starting Proactive Infrastructure");
                double ecoIp[] = assessor.assessIPEcoEfficiency(true);
                checkIpTHAndNotify(ecoIp, 0);
                double ecoIpForecast[] = forecaster.forecastIPEcoEfficiency(timeout);
                checkIpTHAndNotify(ecoIpForecast, timeout);
                log.debug("Finished Proactive Infrastructure");
                
                //Proactive Node EcoAssessment
                log.debug("Starting Proactive Nodes");
                for(String nodeId : co.getNodesId()) {
                    double ecoNode[] = assessor.assessNodeEcoEfficiency(nodeId, false);
                    checkNodeTHAndNotify(nodeId, ecoNode, 0);
                    double ecoNodeForecast[] = forecaster.forecastNodeEcoEfficiency(nodeId, null, timeout);
                    checkNodeTHAndNotify(nodeId, ecoNodeForecast, timeout);
                }
                log.debug("Finished Proactive Nodes");
                
                Thread.sleep(timeout);
            } catch (Exception ex) {
                log.error("Proactive Infrastructure/Node Exception", ex);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ex2) {
                     log.error("Error while performing sleep.", ex2);
                }
            }
        }
        log.warn("Stopping Proactive Infrastructure Assessor.");
    }
}
