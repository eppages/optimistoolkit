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
public class InfrastructureMetrics {

    private static Logger log = Logger.getLogger(InfrastructureMetrics.class);
    private HashMap<String, Double> benchmarkScore;
    private CloudOptimizerRESTClient co;

    public InfrastructureMetrics(CloudOptimizerRESTClient co) {
        this.co = co;
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        benchmarkScore = new HashMap<String, Double>();
        Iterator it = configEco.getKeys("benchmark");
        while (it.hasNext()) {
            String key = (String) it.next();
            log.debug("Adding benchmark for node " + key.split("\\.",2)[1] + ": " + configEco.getString(key));
            benchmarkScore.put(key.split("\\.",2)[1], Double.parseDouble(configEco.getString(key)));
        }
    }

    /**
     * Returns the benchmark result obtained by this node. If it doesn't exist,
     * it returns the lowest value of all the results of all the nodes.
     *
     * @param nodeId Node Identifier.
     * @return Benchmark result if existent, minimum benchmark result otherwise.
     */
    public double getNodeBenchmarkResult(String nodeId) {
        if (benchmarkScore.containsKey(nodeId)) {
            return benchmarkScore.get(nodeId).doubleValue();
        } else {
            double minvalue = Double.MAX_VALUE;
            for (Double value : benchmarkScore.values()) {
                if (value.doubleValue() < minvalue) {
                    minvalue = value.doubleValue();
                }
            }
            return minvalue;
        }
    }

    public double getCPUMeanPerformance(List<String> activeNodes) {
        double meanCPUPerformance = 0.0;

        if (activeNodes == null) {
            activeNodes = co.getNodesId();
        }

        for (String nodeId : activeNodes) {
            meanCPUPerformance += getCPUPerformance(nodeId);
        }
        meanCPUPerformance /= (double) activeNodes.size();

        log.debug("CPU Mean Performance: " + meanCPUPerformance);

        return meanCPUPerformance;
    }

    public double getCPUPerformance(String nodeId) {
        double performanceMax = benchmarkScore.get(nodeId).doubleValue();
        double cpus = (double) getCPUNumber(nodeId);
        return (performanceMax / cpus);
    }

    public int getCPUNumber(String nodeId) {
        String physicalResourceXML = co.getPhysicalResource(nodeId);
        int beginning = physicalResourceXML.indexOf("cpu_cores") + 10;
        int end = physicalResourceXML.lastIndexOf("cpu_cores") - 2;

        return Integer.parseInt(physicalResourceXML.substring(beginning, end));
    }

    public int getVMCPUNumber(String vmId) {
        /*String virtualResourceXML = co.getVirtualResource(vmId);
         int beginning = virtualResourceXML.indexOf("cpu_cores") + 10;
         int end = virtualResourceXML.lastIndexOf("cpu_cores") - 2;

         return Integer.parseInt(virtualResourceXML.substring(beginning, end));*/
        return Integer.parseInt(co.getVirtualResource(vmId).getCpu_cores());
    }

    public int getNumberOfUsedCPUs(String nodeId) {
        int usedCPUs = 0;

        List<String> vmIds = co.getVMsId(nodeId);
        for (String vmId : vmIds) {
            /*String vResource = co.getVirtualResource(vmId);
             int beginning = vResource.indexOf("cpu_cores") + 10;
             int end = vResource.lastIndexOf("cpu_cores") - 2;
             int numCores = Integer.parseInt(vResource.substring(beginning, end));*/

            usedCPUs += Integer.parseInt(co.getVirtualResource(vmId).getCpu_cores());
        }

        return usedCPUs;
    }
}
