/**
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
package eu.optimis.mi.gui.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 *
 * @author jsubirat
 */
public class EcoNodeInfoResource extends BaseModel {
    
    private static final long serialVersionUID = -6664661670666673666L;
    
    public EcoNodeInfoResource(String nodeId, String cpuNumber, String usedCpus, String performance, String cpuPerformance, String pidle, String pmax, String pincr) {
        setNodeId(nodeId);
        setCPUNumber(cpuNumber);
        setUsedCpus(usedCpus);
        setPerformance(performance);
        setCPUPerformance(cpuPerformance);
        setPidle(pidle);
        setPmax(pmax);
        setPincr(pincr);       
    }
    
    public String getNodeId() {
        return (String) get("nodeId");
    }

    public void setNodeId(String nodeId) {
        set("nodeId", nodeId);
    }

    public String getPerformance() {
        return (String) get("performance");
    }

    public void setPerformance(String performance) {
        set("performance", performance);
    }
    
    public String getCPUPerformance() {
        return (String) get("cpuperformance");
    }

    public void setCPUPerformance(String cpuperformance) {
        set("cpuperformance", cpuperformance);
    }

    public String getCPUNumber() {
        return (String) get("cpuNumber");
    }

    public void setCPUNumber(String cpuNumber) {
        set("cpuNumber", cpuNumber);
    }
    
    public String getUsedCpus() {
        return (String) get("usedcpus");
    }

    public void setUsedCpus(String usedcpus) {
        set("usedcpus", usedcpus);
    }
    
    public String getPmax() {
        return (String) get("pmax");
    }

    public void setPmax(String pmax) {
        set("pmax", pmax);
    }
    
    public String getPidle() {
        return (String) get("pidle");
    }

    public void setPidle(String pidle) {
        set("pidle", pidle);
    }
    
    public String getPincr() {
        return (String) get("pincr");
    }

    public void setPincr(String pincr) {
        set("pincr", pincr);
    }
}
