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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsubirat
 */
public class COServiceOperationData extends BaseModel {

    private static final long serialVersionUID = -3074661670555673666L;

    public COServiceOperationData() {
        setServiceNumber(new Integer(0));
    }

    public static List<ColumnConfig> getServiceTableColumnConfig() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig c0 = new ColumnConfig("serviceId", "Service Identifier", 100);
        c0.setSortable(false);
        ColumnConfig c1 = new ColumnConfig("vmId", "VM Identifier", 100);
        c1.setSortable(false);
        ColumnConfig c2 = new ColumnConfig("placement", "Placement", 100);
        c2.setSortable(false);
        ColumnConfig c3 = new ColumnConfig("reason", "Reason", 100);
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

    public List<ServiceDataResource> getServiceDataResources() {
        List<ServiceDataResource> services = new ArrayList<ServiceDataResource>();

        for (int i = 0; i < getServiceNumber().intValue(); i++) {
            services.add(new ServiceDataResource(getServiceId(i),
                    getVMId(i),
                    getPlacement(i),
                    getReason(i),
                    getTREC(i)));
        }
        return services;
    }

    public void addServiceDataResource(ServiceDataResource serviceData) {

        int serviceNumber = getServiceNumber().intValue();

        setServiceId(serviceNumber, serviceData.getServiceId());
        setVMId(serviceNumber, serviceData.getVMId());
        setPlacement(serviceNumber, serviceData.getPlacement());
        setReason(serviceNumber, serviceData.getReason());
        setTREC(serviceNumber, serviceData.getTREC());

        setServiceNumber(new Integer(++serviceNumber));
    }

    private void setServiceNumber(Integer serviceNumber) {
        set("serviceNumber", serviceNumber);
    }

    private Integer getServiceNumber() {
        return (Integer) get("serviceNumber");
    }

    private void setServiceId(int i, String serviceId) {
        set("serviceId" + i, serviceId);
    }

    private String getServiceId(int i) {
        return (String) get("serviceId" + i);
    }

    private void setVMId(int i, String vmId) {
        set("vmId" + i, vmId);
    }

    private String getVMId(int i) {
        return (String) get("vmId" + i);
    }

    private void setPlacement(int i, String placement) {
        set("placement" + i, placement);
    }

    private String getPlacement(int i) {
        return (String) get("placement" + i);
    }

    private void setReason(int i, String reason) {
        set("reason" + i, reason);
    }

    private String getReason(int i) {
        return (String) get("reason" + i);
    }

    private void setTREC(int i, String trec) {
        set("trec" + i, trec);
    }

    private String getTREC(int i) {
        return (String) get("trec" + i);
    }
}
