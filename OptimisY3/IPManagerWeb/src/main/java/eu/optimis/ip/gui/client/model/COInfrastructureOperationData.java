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
package eu.optimis.ip.gui.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsubirat
 */
public class COInfrastructureOperationData extends BaseModel {

    private static final long serialVersionUID = -3074661670534373666L;

    public COInfrastructureOperationData() {
        setNodeVMNumber(new Integer(0));
    }

    public static List<ColumnConfig> getInfrastructureTableColumnConfig() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig c0 = new ColumnConfig("nodeId", "Node Identifier", 100);
        c0.setSortable(false);
        ColumnConfig c1 = new ColumnConfig("vmId", "VM Identifier", 100);
        c1.setSortable(false);
        ColumnConfig c2 = new ColumnConfig("publicIP", "Public IP", 100);
        c2.setSortable(false);
        ColumnConfig c3 = new ColumnConfig("privateIP", "Private IP", 100);
        c3.setSortable(false);
        ColumnConfig c4 = new ColumnConfig("trec", "TREC", 100);
        c4.setSortable(false); 
        c4.setStyle("color:blue;");
        
        configs.add(c0);
        configs.add(c1);
        configs.add(c2);
        configs.add(c3);
        configs.add(c4);

        return configs;
    }

    public List<InfrastructureDataResource> getInfrastructureDataResources() {
        List<InfrastructureDataResource> nodeVMs = new ArrayList<InfrastructureDataResource>();

        for (int i = 0; i < getNodeVMNumber().intValue(); i++) {
            nodeVMs.add(new InfrastructureDataResource(getNodeId(i),
                    getVMId(i),
                    getPublicIP(i),
                    getPrivateIP(i),
                    getTREC(i)));
        }
        return nodeVMs;
    }

    public void addInfrastructureDataResource(InfrastructureDataResource nodeVMData) {

        int nodeVMNumber = getNodeVMNumber().intValue();

        setNodeId(nodeVMNumber, nodeVMData.getNodeId());
        setVMId(nodeVMNumber, nodeVMData.getVMId());
        setPublicIP(nodeVMNumber, nodeVMData.getPublicIP());
        setPrivateIP(nodeVMNumber, nodeVMData.getPrivateIP());
        setTREC(nodeVMNumber, nodeVMData.getTREC());

        setNodeVMNumber(new Integer(++nodeVMNumber));
    }

    private void setNodeVMNumber(Integer nodeVMNumber) {
        set("nodeVMNumber", nodeVMNumber);
    }

    private Integer getNodeVMNumber() {
        return (Integer) get("nodeVMNumber");
    }
    
    private void setNodeId(int i, String nodeId) {
        set("nodeId" + i, nodeId);
    }

    public String getNodeId(int i) {
        return (String) get("nodeId" + i);
    }
    
    private void setVMId(int i, String vmId) {
        set("vmId" + i, vmId);
    }

    public String getVMId(int i) {
        return (String) get("vmId" + i);
    }
   
    private void setPublicIP(int i, String publicIP) {
        set("publicIP" + i, publicIP);
    }

    public String getPublicIP(int i) {
        return (String) get("publicIP" + i);
    }
    
    private void setPrivateIP(int i, String privateIP) {
        set("privateIP" + i, privateIP);
    }

    public String getPrivateIP(int i) {
        return (String) get("privateIP" + i);
    }
    
    private void setTREC(int i, String trec) {
        set("trec" + i, trec);
    }

    public String getTREC(int i) {
        return (String) get("trec" + i);
    }
}
