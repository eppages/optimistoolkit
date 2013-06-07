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

import java.io.IOException;
import java.util.*;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.faulttoleranceengine.util.Config;
import eu.optimis.faulttoleranceengine.util.Log;
import eu.optimis.manifest.api.ip.Availability;
import eu.optimis.manifest.api.ip.RiskSection;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.treccommon.TrecApiIP;
import eu.optimis.vmmanager.rest.client.VMManagerRESTClient;

/**
 * <b>Fault Tolerance Engine</b>
 * The fault tolerance engine is responsible for the monitoring and alerting parts of self-healing infrastructure 
 * operation. As such, it asks for periodic updates from the monitoring system about the state of physical hardware 
 * devices, virtual-IT infrastructure (i.e. VMs), and data sets etc.; and applies some data mining (machine learning) 
 * to this information. Later, based on the internal fault tolerance rules, this engine decides whether any corrective 
 * actions are required, and sends them to the Cloud Optimizer
 * 
 * @version 1.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 *
 */
public class FTAssessor {

    BusinessDescription blo = null;
    private HashMap<String, String> serviceManifests;  // service ---> serviceManifest


    private VMManagerRESTClient vmm;
    private Monitor mon;
    private boolean init;
    private CloudOptimizerRESTClient co;
    //InfrastructureProviderRiskAssessmentServer ipra;
    TrecApiIP trec;
    // Used to prevent a vm or host failure or anticipated failure to be notified twice
    private Set<String> failedVMsId;
    private Set<String> failedHostsId;
    private Set<String> anticipatedFailedVMsId;
    private Set<String> anticipatedFailedHostsId;

    /*
     * Class constructor
     */
    protected FTAssessor() {
        Log.getLogger().info("Starting Fault Tolerance Engine...");
        serviceManifests = new HashMap<String, String>();
        failedVMsId = new TreeSet<String>();
        failedHostsId = new TreeSet<String>();
        anticipatedFailedVMsId = new TreeSet<String>();
        anticipatedFailedHostsId = new TreeSet<String>();
        init = true;
        trec = new TrecApiIP(Config.getString("config.ipvm_host"), Integer.parseInt(Config.getString("config.ipvm_port")));
        co = new CloudOptimizerRESTClient();
        vmm = new VMManagerRESTClient();
    }

    /*
     * Used by the Cloud Optimizer in order to send a notification to this engine when a new service has been deployed
     */
    protected String newServiceDeployed(String serviceId, String serviceManifest) {
        Map<String, Double> vmsIdsAvail = new HashMap<String, Double>();
        
        Log.getLogger().info("A new service has been deployed (id = '" + serviceId + "')");
        if (!serviceManifests.containsKey(serviceId)) {
            Manifest ipManifest = Manifest.Factory.newInstance(serviceManifest);

            RiskSection[] risksecs = ipManifest.getTRECSection().getRiskSectionArray();
            for (RiskSection rs : risksecs) {
                Availability[] availabilityArray = rs.getAvailabilityArray();
                for (Availability a : availabilityArray) {
                    if (a.getAssessmentInterval().equals("P1D")) {
                        double avail = a.getValue();
                        Log.getLogger().info("Availability = " + avail);
                        if (avail > 0) {
                            String[] componentIdArray = rs.getScope().getComponentIdArray();
                            List<String> vMsIdsOfService = co.getVMsIdsOfService(serviceId);
                            for (String b : componentIdArray) {
                                for (String c : vMsIdsOfService) {
                                    if (co.getVMName(c).contains(b)) vmsIdsAvail.put(c, avail);
                                }
                            }
                        }
                    }
                }
            }
            if (init) { // Starting Proactive Risk once the first service is deployed into the infrastructure
                //trec. startFTEProactiveRiskAssessment(serviceId);
                Log.getLogger().info("Starting monitoring daemon...");
                try {
                    String testProperty = Config.getString("testingMonitor");
                    if(testProperty != null && (
                            testProperty.trim().equalsIgnoreCase("yes")
                            || testProperty.trim().equals("1")
                            || testProperty.trim().equalsIgnoreCase("true")
                            )
                        ) {
                        mon = new TestingMonitor(this);
                    } else {
                        mon = new Monitor(this);
                    }
                } catch (IOException ex) {
                    Log.getLogger().error(Log.getStackTrace(ex));
                }
                mon.start();

                init = false;
            }
            serviceManifests.put(serviceId, serviceManifest);
            //Setting thresholds to proactive risk
            //trec.setFTEProactRAThresholdAvail(serviceId, vmsIdsAvail);
        } else {
            Log.getLogger().error("The service '" + serviceId + "' is already under monitoring.");
        }
        return serviceId;
    }

    protected void newServiceUndeployed(String serviceId) {
        Log.getLogger().info("A new service has been undeployed (id = '" + serviceId + "')");
        if (serviceManifests.containsKey(serviceId)) {
            serviceManifests.remove(serviceId);
        } else {
            Log.getLogger().error("The service '" + serviceId + "' is not under monitoring.");
        }
    }

    protected void vmFailure(String vmId) {
        Log.getLogger().info("Calling Cloud Optimizer to restore the VM '" + vmId + "', which has recently failed");
        if (!failedVMsId.contains(vmId)) {
            co.restartVM(vmId);
            failedVMsId.add(vmId);
        } else {
            Log.getLogger().info("Failure notification of VM with id = '" + vmId + "' already sent. The fault tolerance assessor omits the suggestion.");
        }
    }

    protected void vmAnticipatedFailure(String vmId) {
        if (!anticipatedFailedVMsId.contains(vmId)) {
            co.saveVMAndRestart(vmId);
            anticipatedFailedVMsId.add(vmId);
        } else {
            Log.getLogger().info("Aniticpated failure notification of VM with id = '" + vmId + "' already sent. The fault tolerance assessor omits the suggestion.");
        }
    }

    protected void nodeAnticipatedFailure(String nodeId) {
        if (!anticipatedFailedHostsId.contains(nodeId)) {
            co.suggestAllVMsMigration(nodeId);
            anticipatedFailedHostsId.add(nodeId);
        } else {
            Log.getLogger().info("Failure notification of physical host with id = '" + nodeId + "' already sent. The fault tolerance assessor omits the suggestion.");
        }
    }

    public HashMap<String, String> getServicesAndManifests() {
        return serviceManifests;
    }


    protected void setBLO(BusinessDescription blo) {
        this.blo = blo;
    }

    public VMManagerRESTClient getVMM() {
        return vmm;
    }

    public CloudOptimizerRESTClient getCO() {
        return co;
    }

    
}
