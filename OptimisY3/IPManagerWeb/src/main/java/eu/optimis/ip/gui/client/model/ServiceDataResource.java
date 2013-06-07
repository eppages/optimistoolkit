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
public class ServiceDataResource extends BaseModel {
    
    private static final long serialVersionUID = -6664661670666673666L;
    
    public ServiceDataResource(String serviceId, String vmId, String placement, String reason, String trec) {
        setServiceId(serviceId);
        setVMId(vmId);
        setPlacement(placement);
        setReason(reason);
        setTREC(trec);
    }
    
    private void setServiceId(String serviceId) {
        set("serviceId", serviceId);
    }

    public String getServiceId() {
        return (String) get("serviceId");
    }
    
    private void setVMId(String vmId) {
        set("vmId", vmId);
    }

    public String getVMId() {
        return (String) get("vmId");
    }
    
    private void setPlacement(String placement) {
        set("placement", placement);
    }

    public String getPlacement() {
        return (String) get("placement");
    }
    
    private void setReason(String reason) {
        set("reason", reason);
    }

    public String getReason() {
        return (String) get("reason");
    }
    
    private void setTREC(String trec) {
        set("trec", trec);
    }

    public String getTREC() {
        return (String) get("trec");
    }
}
