/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.faulttoleranceengine.core;

import java.util.*;

import eu.optimis.faulttoleranceengine.util.Log;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.faulttoleranceengine.util.Config;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import java.io.IOException;

import net.emotivecloud.scheduler.drp.client.DRPClient;
import net.emotivecloud.vrmm.rm.data.Cluster;
import net.emotivecloud.vrmm.rm.data.Host;
import net.emotivecloud.vrmm.rm.data.Metric;
import net.emotivecloud.vrmm.rm.data.RMXMLConversor;
import net.emotivecloud.vrmm.rm.rest.client.RMClient;
import net.emotivecloud.vrmm.scheduler.VRMMSchedulerException;
import org.kohsuke.rngom.ast.util.CheckingSchemaBuilder;
import sun.management.VMManagement;

public class Monitor extends Thread {

    protected static final int INTERVAL = 60000; // Monitor thread sleep interval (in msecs)

    FTAssessor fta;

    getClient aggregator_get;
    protected CloudOptimizerRESTClient co;
    protected DRPClient drp;
    protected RMClient rm_client;

    public Monitor(FTAssessor FTA) throws IOException {
        fta = FTA;
        co = new CloudOptimizerRESTClient();
        drp = new DRPClient(Config.getString("config.drp_host"), Integer.parseInt(Config.getString("config.drp_port")));
        aggregator_get = new getClient(Config.getString("config.aggregator_host"), Integer.parseInt(Config.getString("config.aggregator_port")), Config.getString("config.aggregator_url"));
        rm_client = new RMClient(Config.getString("config.rm_host"), Integer.parseInt(Config.getString("config.rm_port")));
    }

    public void run() {
        try {
            Log.getLogger().debug("Starting FTE Monitoring Thread...");
            while (true) {
                if (fta.getServicesAndManifests().size() > 0) {
                    List<String> nodeIds = co.getNodesId();
                    detectNodeFailures(nodeIds);
                    detectVMFailures(nodeIds);
                    // Two methods before will remove failed/removed VMs and Nodes, so anticipate methods
                    // will work over still alive VMs/Nodes
                    Set<String> failedNodes = anticipateNodeFailures(nodeIds);
                    for(String nodeId : failedNodes) {
                        StringBuilder sb = new StringBuilder("Going to migrate all vms for node: ").append(nodeId);
                        sb.append("\nResult is: ");
                        sb.append(co.suggestAllVMsMigration(nodeId).toString());
                        Log.getLogger().debug(sb.toString());
                    }

                    for(String nodeId : nodeIds) {
                        // only check VMs from nodes that are not suggested to restart
                        if(!failedNodes.contains(nodeId)) {
                            anticipateVMFailures(co.getVMsId(nodeId));
                        }
                    }

                }
                Thread.sleep(INTERVAL);
            }
        } catch (Exception e) {
            Log.getLogger().error("Exception in running thread of class Monitor: " + Log.getStackTrace(e));
        }
    }

    protected void detectNodeFailures(List<String> nodeIds) {

        // Por Mario:
        // 1 Detectar nodo fallado.
        // 2 Quitarlo de activeNodes.
        // 3 Reiniciar todas las maquinas según el manifest.
        List<String> unreachableNodes = rm_client.getUnreachableNodes();
        for(String nodeId : unreachableNodes) {
            Log.getLogger().info("Node " + nodeId + " has no connection. Notifying Cloud Optimizer");
            try {
                fta.getCO().onNodeFailure(nodeId);
            } catch(Exception e) {
                Log.getLogger().error("When notifying CO: " + e.getMessage());
            }
        }
    }

    protected void detectVMFailures(List<String> nodeIds) {
        for(String node : nodeIds) {
            List<String> vmIds = co.getVMsId(node);
            for(String vmId : vmIds) {
                String metric_value = "";
                MonitoringResourceDatasets virtual_info = aggregator_get.getLatestReportForVirtual(vmId);
                if (!virtual_info.getMonitoring_resource().isEmpty()) {
                    List<MonitoringResourceDataset> vinfo = virtual_info.getMonitoring_resource();
                    Iterator<MonitoringResourceDataset> iterator = vinfo.iterator();
                    while (iterator.hasNext()) {
                        MonitoringResourceDataset vnext = iterator.next();
                        if (vnext.getMetric_name().equals("cpu_user")) {
                            metric_value = vnext.getMetric_value();
                            if (metric_value.equals("0.0") || this.getInstantaneousVMData(vmId, "cpu_user").equals("0.0")) {
                                //Checking VM state
                                try {
                                    if (!drp.getState(vmId).startsWith("Running") && !drp.getState(vmId).equalsIgnoreCase("Idle") && !drp.getState(vmId).equalsIgnoreCase("Pending")) {
                                        Log.getLogger().debug("CPU consumption of VM '" + vmId + "' = " + metric_value + "\nFailure-related state of VM '" + vmId + "' - FTE calling CO to restart this VM!");
                                        fta.vmFailure(vmId);
                                    }
                                } catch(VRMMSchedulerException e) {
                                    Log.getLogger().error(Log.getStackTrace(e));
                                }
                            }
                        }
                    }
                } else {
                    Log.getLogger().info("Virtual monitoring information is empty.");
                }
            }
        }
    }

    // TODO: remove this method and change "anticipateVMFailure" to call directly TREC risk assessor about the pof of
    // a VM/service
    protected int dummy(String fake) { return 0; }

    protected static final int VM_MIGRATION_RISK_THRESHOLD = Integer.MAX_VALUE;

    protected void anticipateVMFailures(List<String> vmIds) {
        for(String vmId : vmIds) {
            if(dummy(vmId) >= VM_MIGRATION_RISK_THRESHOLD) {
                co.saveVMAndRestart(vmId);
            }
        }
    }

    protected static final int NODE_FAILURE_RISK_THRESHOLD = Integer.MAX_VALUE;

    /**
     *
     * @param nodeIds
     * @return the node Ids whose fails are anticipated
     */
    protected Set<String> anticipateNodeFailures(List<String> nodeIds) {
        Set<String> failing = new HashSet<String>();
        for (String nodeId : nodeIds) {
            if(fta.trec.RISK.calculateRiskLevelOfPhyHostFailure(nodeId,String.valueOf(INTERVAL)) >= NODE_FAILURE_RISK_THRESHOLD) {
                failing.add(nodeId);
            }
        }
        return failing;
    }

    /**
     * Obtains instantaneous virtual-level metric directly from the collector
     */
    protected String getInstantaneousVMData(String vmId, String metric_name) {
        String ret = "";
        String xml_ret = rm_client.getMeasuredData(vmId);
        try {
            Cluster c = RMXMLConversor.parseXML(xml_ret);
            List<Host> hosts = c.getHosts();
            for (Host h : hosts) {
                String hostId = h.getMachineId();
                List<Metric> metrics = h.getMetrics();
                for (Metric m : metrics) {
                    if (m.getName().equals(metric_name)) {
                        ret = m.getValue().toString();
                    }
                }
            }
        } catch (Exception e) {
            Log.getLogger().error(Log.getStackTrace(e));
        }
        return ret;
    }
}