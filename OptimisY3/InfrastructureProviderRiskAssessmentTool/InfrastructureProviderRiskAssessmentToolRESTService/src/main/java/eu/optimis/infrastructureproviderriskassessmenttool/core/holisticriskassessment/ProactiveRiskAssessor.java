/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.optimis.infrastructureproviderriskassessmenttool.core.holisticriskassessment;

import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.infrastructureproviderriskassessmenttool.core.configration.ConfigManager;
import eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor.getIPCapacityRisk;
import eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor.riskEngine;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author scsmj
 */
public class ProactiveRiskAssessor extends Thread {

    protected static Logger log = Logger.getLogger(ProactiveRiskAssessor.class);
    private String infrastructureID;
    private String infrastructureRiskLevelThreshold;
    private HashMap<String, String> physicalHostsRiskLevelThresholds = new HashMap<String, String>();
    private HashMap<String, String> servicesRiskLevelThresholds = new HashMap<String, String>();
    private HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds = new HashMap<String, HashMap<String, String>>(new HashMap<String, HashMap<String, String>>());
    private riskEngine re = null;
    private HolisticManagementRESTClient HMClient;
    private boolean end = false;
    
    public ProactiveRiskAssessor(String infrastructureID, String infrastructureRiskLevelThreshold, HashMap<String, String> physicalHostsRiskLevelThresholds, HashMap<String, String> servicesRiskLevelThresholds, HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {
        this.infrastructureID = infrastructureID;
        this.infrastructureRiskLevelThreshold = infrastructureRiskLevelThreshold;
        this.physicalHostsRiskLevelThresholds = physicalHostsRiskLevelThresholds;
        this.servicesRiskLevelThresholds = servicesRiskLevelThresholds;
        this.VMsRiskLevelThresholds = VMsRiskLevelThresholds;
        re = new riskEngine();
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        HMClient = new HolisticManagementRESTClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("hmport")));
    }

    public void run() {

        while (!end) {
            log.info("The ProactiveRiskAssessor for HM is Running...");
            try {
                Thread.sleep(10000);
                ProactiveRiskAssessmentForPhysicalHosts();
                ProactiveRiskAssessmentForVMs();
                ProactiveRiskAssessmentForServices();
                ProactiveRiskAssessmentForIP();
            } catch (Exception ex) {
                Logger.getLogger(ProactiveRiskAssessor.class.getName()).log(Level.FATAL, null, ex);
                System.out.println("Thread interrupted.");
            }
        }
    }

    public void stopProactiveRiskAssessor() {
        this.end = true;
        log.info("ProactiveRiskAssessor stops...");
    }

    private void ProactiveRiskAssessmentForVMs() throws Exception {

        log.info("Start: ProactiveRiskAssessmentForVMs");

        Set<String> serviceIDs = VMsRiskLevelThresholds.keySet();

        Iterator<String> itr = serviceIDs.iterator();

        while (itr.hasNext()) {
            String serviceID = itr.next();
            HashMap<String, String> VMs = VMsRiskLevelThresholds.get(serviceID);

            Set<String> serviceIDs2 = VMs.keySet();

            Iterator<String> itrVMs = serviceIDs2.iterator();

            while (itrVMs.hasNext()) {
                String VMID = itrVMs.next();
                int VMRiskLevelThreshold = Integer.valueOf(VMs.get(VMID));
                double currentVMRiskLevel = re.getVm(VMID);
                if (currentVMRiskLevel >= VMRiskLevelThreshold) {
                    // notify HM;
                    HMClient.notifyVMRiskLevel(VMID, (int) currentVMRiskLevel);
                }
            }
        }
        log.info("End: ProactiveRiskAssessmentForVMs");
    }

    private void ProactiveRiskAssessmentForIP() throws Exception {
        log.info("Start: ProactiveRiskAssessmentForIP");

        int infrastructureRiskLevel = calculateInfrastructureCapacityRiskLevel(infrastructureID);
        if (infrastructureRiskLevel >= Integer.valueOf(infrastructureRiskLevelThreshold)) {
            // notify HM;
            HMClient.notifyInfrastructureRiskLevel(infrastructureRiskLevel);

            int ms = MitigationStrategy.getMitigationStrategy(1, infrastructureRiskLevel);
            if (ms != 0) {
                HMClient.notifyInfrastructureRiskLevel(5);    
            }
        }

        log.info("End: ProactiveRiskAssessmentForIP");
    }
    
    private void ProactiveRiskAssessmentForPhysicalHosts() throws Exception {
        log.info("Start: ProactiveRiskAssessmentForPhysicalHosts");
        Set<String> physicalHostIDs = physicalHostsRiskLevelThresholds.keySet();
        Iterator<String> itr = physicalHostIDs.iterator();

        while (itr.hasNext()) {
            String physicalHostID = itr.next();
            int physicalHostRiskLevelThreshold = Integer.valueOf(physicalHostsRiskLevelThresholds.get(physicalHostID));
            double physicalHostRiskLevel = re.getPhy(physicalHostID);

            if (physicalHostRiskLevel >= physicalHostRiskLevelThreshold) {
                // notify HM;
                HMClient.notifyPhysicalHostRiskLevel(physicalHostID, (int) physicalHostRiskLevel);
            }
        }

        log.info("End: ProactiveRiskAssessmentForPhysicalHosts");
    }

    private void ProactiveRiskAssessmentForServices() throws Exception {
        log.info("Start: ProactiveRiskAssessmentForServices");
        Set<String> serviceIDs = servicesRiskLevelThresholds.keySet();

        Iterator<String> itr = serviceIDs.iterator();

        String serviceID;

        while (itr.hasNext()) {
            serviceID = itr.next();
            int serviceRiskLevelThreshold = Integer.valueOf(servicesRiskLevelThresholds.get(serviceID));
            double serviceRiskLevel = re.getSla(serviceID);
            if (serviceRiskLevel >= serviceRiskLevelThreshold) {
                // notify HM;
                HMClient.notifyServiceRiskLevel(serviceID, (int) serviceRiskLevel);
            }
        }
        log.info("End: ProactiveRiskAssessmentForServices");
    }

    private int calculateInfrastructureCapacityRiskLevel(String infrastructureID) throws Exception {

        double[] risk = getIPCapacityRisk.getRisks(infrastructureID);
        double prob = risk[3];                   
        return (int)prob;
    }
}
