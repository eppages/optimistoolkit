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

/**
 *
 * @author jsubirat
 */
public class InfrastructureDataResource extends BaseModel {
    
    private static final long serialVersionUID = -6664661670666673555L;
    
    public InfrastructureDataResource(String nodeId, String vmId, String publicIP, String privateIP, String trec) {
        setNodeId(nodeId);
        setVMId(vmId);
        setPublicIP(publicIP);
        setPrivateIP(privateIP);
        setTREC(trec);
    }
    
    private void setNodeId(String nodeId) {
        set("nodeId", nodeId);
    }

    public String getNodeId() {
        return (String) get("nodeId");
    }
    
    private void setVMId(String vmId) {
        set("vmId", vmId);
    }

    public String getVMId() {
        return (String) get("vmId");
    }
   
    private void setPublicIP(String publicIP) {
        set("publicIP", publicIP);
    }

    public String getPublicIP() {
        return (String) get("publicIP");
    }
    
    private void setPrivateIP(String privateIP) {
        set("privateIP", privateIP);
    }

    public String getPrivateIP() {
        return (String) get("privateIP");
    }
    
    private void setTREC(String trec) {
        set("trec", trec);
    }

    public String getTREC() {
        return (String) get("trec");
    }
}
