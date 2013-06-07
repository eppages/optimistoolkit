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

import java.util.HashMap;
import org.apache.log4j.Logger;
/**
 *
 * @author scsmj
 */
public class HolisticRiskAssessment {

    private ProactiveRiskAssessor proactiveRiskAssessor = null;
    protected static Logger log = Logger.getLogger(HolisticRiskAssessment.class);

    public HolisticRiskAssessment() {
        //PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        log.info("IPRA: HolisticRiskAssessment Starts Up ......");
    }

    public synchronized void startProactiveRiskAssessor(String infrastructureID, String infrastructureRiskLevelThreshold, HashMap<String, String> physicalHostsRiskLevelThresholds, HashMap<String, String> servicesRiskLevelThresholds, HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {
        if (proactiveRiskAssessor == null) {
            proactiveRiskAssessor = new ProactiveRiskAssessor(infrastructureID, infrastructureRiskLevelThreshold, physicalHostsRiskLevelThresholds, servicesRiskLevelThresholds, VMsRiskLevelThresholds);
            proactiveRiskAssessor.start();
        }
    }

    public synchronized void stopProactiveRiskAssessor() {
        if (proactiveRiskAssessor != null) {
            proactiveRiskAssessor.stopProactiveRiskAssessor();
            proactiveRiskAssessor = null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) //FIXME
	public static void main(String[] args) {

        HolisticRiskAssessment infrastructureProviderRiskAssessmentTool = new HolisticRiskAssessment();
        // Testing the ProactiveRiskAssessor

        String infrastructureID = "IPID_01";
        String infrastructureRiskLevelThreshold = "3";
        HashMap<String, String> physcialHostsRiskLevelThresholds = new HashMap();
        physcialHostsRiskLevelThresholds.put("physcialHostID_01", "4");
        physcialHostsRiskLevelThresholds.put("physcialHostID_02", "5");

        HashMap<String, String> servicesRiskLevelThresholds = new HashMap();
        servicesRiskLevelThresholds.put("ServiceID_01", "4");
        servicesRiskLevelThresholds.put("ServiceID_02", "5");

        HashMap<String, String> VMsRiskLevelThresholds1 = new HashMap();
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_01", "3");
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_02", "4");
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_03", "5");

        HashMap<String, String> VMsRiskLevelThresholds2 = new HashMap();
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_01", "1");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_02", "2");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_03", "3");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_04", "4");

        HashMap<String, HashMap<String, String>> servicesVMsRiskLevelThresholds = new HashMap(new HashMap());
        servicesVMsRiskLevelThresholds.put("ServiceID_01", VMsRiskLevelThresholds1);
        servicesVMsRiskLevelThresholds.put("ServiceID_02", VMsRiskLevelThresholds2);

        try {
            infrastructureProviderRiskAssessmentTool.startProactiveRiskAssessor(infrastructureID, infrastructureRiskLevelThreshold, physcialHostsRiskLevelThresholds, servicesRiskLevelThresholds, servicesVMsRiskLevelThresholds);
            Thread.sleep(30000);
            infrastructureProviderRiskAssessmentTool.stopProactiveRiskAssessor();
            Thread.sleep(60000);
            infrastructureProviderRiskAssessmentTool.startProactiveRiskAssessor(infrastructureID, infrastructureRiskLevelThreshold, physcialHostsRiskLevelThresholds, servicesRiskLevelThresholds, servicesVMsRiskLevelThresholds);
            Thread.sleep(30000);
            infrastructureProviderRiskAssessmentTool.stopProactiveRiskAssessor();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted.");
        }
    }
}
