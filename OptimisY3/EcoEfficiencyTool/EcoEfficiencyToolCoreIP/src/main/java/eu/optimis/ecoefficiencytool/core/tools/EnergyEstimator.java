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
package eu.optimis.ecoefficiencytool.core.tools;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class EnergyEstimator {
    
    private static Logger log = Logger.getLogger(EnergyEstimator.class);
    private CloudOptimizerRESTClient co;
    private InfrastructureMetrics metrics;
    private HashMap<String,LinearRegression> powerEstimator;
    private HashMap<String, Double> minPowerConsumption;
    private HashMap<String, Double> maxPowerConsumption;
    private double meanPower = 0.0;
    private double initialCPUUtilization;
    
    public EnergyEstimator(CloudOptimizerRESTClient co, InfrastructureMetrics metrics) {
        //log = Log.getLog(getClass());
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        this.co = co;
        this.metrics = metrics;
        powerEstimator = new HashMap<String,LinearRegression>();
        initialCPUUtilization = Double.parseDouble(configEco.getString("initialCPUUtilization"));;
        minPowerConsumption = new HashMap<String,Double>();
        maxPowerConsumption = new HashMap<String,Double>();
        Iterator it = configEco.getKeys("powermin");
        while(it.hasNext()) {
            String key = (String) it.next();
            log.debug("Minimum power consumption for node " + key.trim().split("\\.",2)[1] + ": " + configEco.getString(key));
            minPowerConsumption.put(key.split("\\.",2)[1], Double.parseDouble(configEco.getString(key)));
        }
        it = configEco.getKeys("powermax");
        while(it.hasNext()) {
            String key = (String) it.next();
            log.debug("Maximum power consumption for node " + key.trim().split("\\.",2)[1] + ": " + configEco.getString(key));
            maxPowerConsumption.put(key.split("\\.",2)[1], Double.parseDouble(configEco.getString(key)));
        }
        if(minPowerConsumption.size() != maxPowerConsumption.size()) {
            log.error("Power consumption wasn't specified correctly. Different amount of min/max values.");
        }
        it = minPowerConsumption.keySet().iterator();
        while(it.hasNext()) {
            String host = (String)it.next();
            if(maxPowerConsumption.containsKey(host)) {
                double x[] = new double[2];
                double y[] = new double[2];
                x[0] = 0.0;
                x[1] = 100.0;
                y[0] = minPowerConsumption.get(host).doubleValue();
                y[1] = maxPowerConsumption.get(host).doubleValue();
                LinearRegression lin = new LinearRegression(x,y);
                powerEstimator.put(host, lin);
                if(meanPower == 0.0) {
                    meanPower = (y[0] + y[1])/2.0;
                }
                else {
                    meanPower = (meanPower + ((y[0] + y[1])/2.0))/2.0;
                }
            } else {
                log.error("Couldn't find corresponding max value for node " + host);
            }
        }
    }

    public double estimatePowerConsumption(String nodeId, double cpuusage) throws Exception{

        log.debug("Estimating power consumption for node " + nodeId + ". CPU usage: " + cpuusage);
        if (cpuusage < 0.0) {
            cpuusage = 0.0;
        } else if (cpuusage > 100.0) {
            cpuusage = 100.0;
        }
        
        if(powerEstimator.containsKey(nodeId.trim())) {
            return powerEstimator.get(nodeId).calculateY(cpuusage);
        } else {
            log.error("Couldn't find power estimator for node " + nodeId);
            throw new Exception("Couldn't find power estimator for node " + nodeId);
        }
    }
    
    public double getMeanPowerConsumption() {
        return meanPower;
    }
    
    /**
     * Gets the future CPU mean power for the best case (less nodes turned on)
     * and the worst case (more nodes turned on).
     * @param mode true=best case false = worst case. (Not used at the moment)
     * @return 
     */
    public double getFutureCPUMeanPower(int extraCPUs, List<String> activeNodes, boolean mode) {
        double futureCPUMeanPower = 0.0;
        
        if(activeNodes == null) {
            activeNodes = co.getNodesId();
        }
        
        //Calculate total idle power (current)
        double totalCurrentIdle = 0.0;
        int totalUsedCpus = 0;
        for(String nodeId : activeNodes) {
            totalCurrentIdle += minPowerConsumption.get(nodeId).doubleValue();
            totalUsedCpus += metrics.getNumberOfUsedCPUs(nodeId);
        }

        futureCPUMeanPower = (totalCurrentIdle / ((double)totalUsedCpus + (double)extraCPUs)) + getMeanIncrementalPowerPerCPU(activeNodes)*(initialCPUUtilization/100.0);
        
        log.debug("Total Idle: " + totalCurrentIdle + " Total Used CPUs: " + totalUsedCpus + " Mean Incremental Power: " + getMeanIncrementalPowerPerCPU(activeNodes));
        
        return futureCPUMeanPower;
    }
    
    public double getIncrementalPowerPerCPU(String nodeId) {
        double incrementalPowerCPU = (maxPowerConsumption.get(nodeId).doubleValue() - minPowerConsumption.get(nodeId).doubleValue()) / metrics.getCPUNumber(nodeId);
        log.debug("Incremental Power / CPU for node " + nodeId + ": " + incrementalPowerCPU);
        return incrementalPowerCPU;
    }
    
    public double getMeanIncrementalPowerPerCPU(List<String> activeNodes) {
        
        double meanIncrementalPowerPerCPU = 0.0;
        for(String nodeId : activeNodes) {
            meanIncrementalPowerPerCPU += getIncrementalPowerPerCPU(nodeId);
        }
        meanIncrementalPowerPerCPU /= activeNodes.size();
        return meanIncrementalPowerPerCPU;
    }
    
    public double getPidle(String nodeId) {
        return minPowerConsumption.get(nodeId).doubleValue();
    }
    
    public double getPMax(String nodeId) {
        return maxPowerConsumption.get(nodeId).doubleValue();
    }
}
