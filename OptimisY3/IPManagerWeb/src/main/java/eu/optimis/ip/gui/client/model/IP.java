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
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;
import java.util.List;

public class IP extends BaseModel implements IsSerializable {

    private static final long serialVersionUID = 3462378870650903L;

    public IP() {
        set("ip_name", null);
        set("ip_ip", null);
        set("ip_id", null);
        set("ip_provider_type", null);
        set("cloud_qos_url", null);
        set("dm_gui_url", null);
    }

    public IP(String ip_name, String ip_ip, String ip_id,
            String ip_provider_type, String cloud_qos_url, String dm_gui_url) {
        set("ip_name", ip_name);
        set("ip_ip", ip_ip);
        set("ip_id", ip_id);
        set("ip_provider_type", ip_provider_type);
        set("cloud_qos_url", cloud_qos_url);
        set("dm_gui_url", dm_gui_url);
    }

    public static List<ColumnConfig> getColumnConfigIP() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig c0 = new ColumnConfig("ip_name", "IP Name", 50);
        ColumnConfig c1 = new ColumnConfig("ip_ip", "IP Address", 50);
        ColumnConfig c2 = new ColumnConfig("ip_id", "Id", 50);
        ColumnConfig c3 = new ColumnConfig("ip_provider_type", "IP Provider Type", 50);
        ColumnConfig c4 = new ColumnConfig("cloud_qos_url", "Cloud Qos URL", 100);
        ColumnConfig c5 = new ColumnConfig("dm_gui_url", "DM GUI URL", 100);
        configs.add(c0);
        configs.add(c1);
        configs.add(c2);
        configs.add(c3);
        configs.add(c4);
        configs.add(c5);
        c4.setRenderer(new GridCellRenderer() {
            @Override
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore store, Grid grid) {
                String html = "";
                String value = (String) model.get(property);
                html += "<a href=\"" + value + "\" target=\"_blank\">" + value
                        //+ "Cloud Qos URL: click here"
                        + "</a>";
                return html;
            }
        });
        c5.setRenderer(new GridCellRenderer() {
            @Override
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore store, Grid grid) {
                String html = "";
                String value = (String) model.get(property);
                html += "<a href=\"" + value + "\" target=\"_blank\">" + value
                        //+ "Cloud Qos URL: click here"
                        + "</a>";
                return html;
            }
        });
        return configs;
    }

    public String getName() {
        return (String) get("ip_name");
    }

    public String toString() {
        return new String(get("ip_name").toString() + get("ip_ip").toString()
                + get("ip_id").toString() + get("ip_provider_type")
                + get("cloud_qos_url"));
    }
}
