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
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsubirat
 */
public class EcoServiceDeploymentInfoData extends BaseModel {

    private static final long serialVersionUID = -3074661670666673666L;

    public EcoServiceDeploymentInfoData() {
        setNodeNumber(new Integer(0));
    }

    public static List<ColumnConfig> getNodeTableColumnConfig() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c0 = new ColumnConfig("nodeId", "Node", 100);
                ColumnConfig c1 = new ColumnConfig("cpuNumber", "CPUs Total", 100);
                ColumnConfig c2 = new ColumnConfig("usedcpus", "CPUs Used", 100);
		ColumnConfig c3 = new ColumnConfig("performance", "Performance (MWIPS)", 100);
                ColumnConfig c4 = new ColumnConfig("cpuperformance", "Performance/CPU (MWIPS/CPU)", 100);
		ColumnConfig c5 = new ColumnConfig("pidle", "Pidle (W)", 100);
                ColumnConfig c6 = new ColumnConfig("pmax", "Pmax (W)", 100);
                ColumnConfig c7 = new ColumnConfig("pincr", "Pincr (W)", 100);
		
		
                configs.add(c0);
		configs.add(c1);
		configs.add(c2);
		configs.add(c3);
		configs.add(c4);
                configs.add(c5);
                configs.add(c6);
                configs.add(c7);
                
		return configs;
	}

    public List<EcoNodeInfoResource> getNodeInfoResources() {
        List<EcoNodeInfoResource> nodes = new ArrayList<EcoNodeInfoResource>();
        
        for(int i = 0; i < getNodeNumber().intValue(); i++) {
            nodes.add(new EcoNodeInfoResource(getNodeId(i),
                                                getCPUNumber(i),
                                                getUsedCpus(i),
                                                getPerformance(i),
                                                getCPUPerformance(i),
                                                getPidle(i),
                                                getPmax(i),
                                                getPincr(i)));
        }
        return nodes;
    }
    
    public void addNodeInfoResource(EcoNodeInfoResource newNode) {
        
        int nodeNumber = getNodeNumber().intValue();
        
        setNodeId(nodeNumber, newNode.getNodeId());
        setCPUNumber(nodeNumber, newNode.getCPUNumber());
        setUsedCpus(nodeNumber, newNode.getUsedCpus());
        setPerformance(nodeNumber, newNode.getPerformance());
        setCPUPerformance(nodeNumber, newNode.getCPUPerformance());
        setPidle(nodeNumber, newNode.getPidle());
        setPmax(nodeNumber, newNode.getPmax());
        setPincr(nodeNumber, newNode.getPincr());
        
        setNodeNumber(new Integer(++nodeNumber));
    }
    
    /****** DEPLOYMENT OUTPUT *******/
    public void setDeploymentOutput(String output) {
        set("deploymentOutput", output);
    }

    public String getDeploymentOutput() {
        return (String) get("deploymentOutput");
    }
    
    /****** NODE FIELDS *******/

    private void setNodeNumber(Integer nodeNumber) {
        set("nodeNumber", nodeNumber);
    }

    private Integer getNodeNumber() {
        return (Integer) get("nodeNumber");
    }

    private String getNodeId(int i) {
        return (String) get("nodeId" + i);
    }

    private void setNodeId(int i, String nodeId) {
        set("nodeId" + i, nodeId);
    }

    private String getPerformance(int i) {
        return (String) get("performance" + i);
    }

    private void setPerformance(int i, String performance) {
        set("performance" + i, performance);
    }
    
    public String getCPUPerformance(int i) {
        return (String) get("cpuperformance" + i);
    }

    public void setCPUPerformance(int i, String cpuperformance) {
        set("cpuperformance" + i, cpuperformance);
    }

    private String getCPUNumber(int i) {
        return (String) get("cpuNumber" + i);
    }

    private void setCPUNumber(int i, String cpuNumber) {
        set("cpuNumber" + i, cpuNumber);
    }
    
    public String getUsedCpus(int i) {
        return (String) get("usedcpus" + i);
    }

    public void setUsedCpus(int i, String usedcpus) {
        set("usedcpus" + i, usedcpus);
    }
    
    public String getPmax(int i) {
        return (String) get("pmax" + i);
    }

    public void setPmax(int i, String pmax) {
        set("pmax" + i, pmax);
    }
    
    public String getPidle(int i) {
        return (String) get("pidle" + i);
    }

    public void setPidle(int i, String pidle) {
        set("pidle" + i, pidle);
    }
    
    public String getPincr(int i) {
        return (String) get("pincr" + i);
    }

    public void setPincr(int i, String pincr) {
        set("pincr" + i, pincr);
    }
}
