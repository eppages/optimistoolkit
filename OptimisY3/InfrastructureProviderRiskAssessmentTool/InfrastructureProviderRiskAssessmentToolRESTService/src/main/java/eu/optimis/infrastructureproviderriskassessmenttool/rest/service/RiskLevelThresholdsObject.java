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
package eu.optimis.infrastructureproviderriskassessmenttool.rest.service;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "risklevelthresholdsobject")
/**
 *
 * @author scsmj
 */
public class RiskLevelThresholdsObject {

    private String infrastructureID;
    private String infrastructureRiskLevelThreshold;
    private HashMap<String, String> physicalHostsRiskLevelThresholds;
    private HashMap<String, String> servicesRiskLevelThresholds;
    private HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds;

    public RiskLevelThresholdsObject() {
    }

    public RiskLevelThresholdsObject(String infrastructureID, String infrastructureRiskLevelThreshold, HashMap<String, String> physicalHostsRiskLevelThresholds, HashMap<String, String> servicesRiskLevelThresholds, HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {
        this.infrastructureID = infrastructureID;
        this.infrastructureRiskLevelThreshold = infrastructureRiskLevelThreshold;
        this.physicalHostsRiskLevelThresholds = physicalHostsRiskLevelThresholds;
        this.servicesRiskLevelThresholds = servicesRiskLevelThresholds;
        this.VMsRiskLevelThresholds = VMsRiskLevelThresholds;
    }

    public void setInfrastructureID(String infrastructureID) {

        this.infrastructureID = infrastructureID;

    }

    public void setInfrastructureRiskLevelThresholds(String infrastructureRiskLevelThreshold) {

        this.infrastructureRiskLevelThreshold = infrastructureRiskLevelThreshold;

    }

    public void setPhysicalHostsRiskLevelThresholds(HashMap<String, String> physicalHostsRiskLevelThresholds) {

        this.physicalHostsRiskLevelThresholds = physicalHostsRiskLevelThresholds;

    }

    public void setServicesRiskLevelThresholds(HashMap<String, String> servicesRiskLevelThresholds) {

        this.servicesRiskLevelThresholds = servicesRiskLevelThresholds;

    }

    public void setVMsRiskLevelThresholds(HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {

        this.VMsRiskLevelThresholds = VMsRiskLevelThresholds;

    }

    public String getInfrastructureID() {

        return this.infrastructureID;

    }

    public String getInfrastructureRiskLevelThreshold() {

        return this.infrastructureRiskLevelThreshold;

    }

    public HashMap<String, String> getPhysicalHostsRiskLevelThresholds() {

        return this.physicalHostsRiskLevelThresholds;

    }

    public HashMap<String, String> getServicesRiskLevelThresholds() {

        return this.servicesRiskLevelThresholds;

    }

    public HashMap<String, HashMap<String, String>> getVMsRiskLevelThresholds() {

        return this.VMsRiskLevelThresholds;

    }
}