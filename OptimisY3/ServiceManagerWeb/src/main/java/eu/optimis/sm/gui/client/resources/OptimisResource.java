/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.optimis.sm.gui.client.resources;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import eu.optimis.sm.gui.client.model.Folder;
import eu.optimis.sm.gui.client.model.Level;

public class OptimisResource {
	public static Folder getTreeModel() {
		Folder f1 = new Folder("OPTIMIS1", new Folder[] {
				new Folder("service"), new Folder("virtual"),
				new Folder("physical"), new Folder("energy") });
		Folder f2 = new Folder("OPTIMIS2", new Folder[] {
				new Folder("service"), new Folder("virtual"),
				new Folder("physical"), new Folder("energy") });
		Folder[] folders = new Folder[] { f1, f2 };
		Folder root = new Folder("root");
		for (int i = 0; i < folders.length; i++) {
			root.add((Folder) folders[i]);
		}
		return root;
	}

	public static List<Level> getLevels() {
		List<Level> levels = new ArrayList<Level>();
		levels.add(new Level("Available services", "optimis1"));
		levels.add(new Level("Deploy service", "optimis1"));
		levels.add(new Level("Undeploy service", "optimis1"));
		return levels;
	}

	public static List<Level> getLevels2() {
		List<Level> levels = new ArrayList<Level>();
		levels.add(new Level("Redeploy service", "optimis2"));
		levels.add(new Level("IP Registry", "optimis2"));
		levels.add(new Level("TREC GUI", "optimis2"));
		levels.add(new Level("Properties", "optimis2"));
		levels.add(new Level("Logs", "optimis2"));
		return levels;
	}

	public static List<Level> getLevels3() {
		List<Level> levels = new ArrayList<Level>();
		levels.add(new Level("IPS", "optimis3"));
		levels.add(new Level("Secure Storage", "optimis3"));
		levels.add(new Level("VPN", "optimis3"));
		return levels;
	}
	public static List<ColumnConfig> getColumnConfigService(RowExpander expander) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c01 = new ColumnConfig("service_number", "Service Number", 50);
		ColumnConfig c0 = new ColumnConfig("service_id", "Service Id", 100);
		ColumnConfig c1 = new ColumnConfig("service_status", "Service Status", 100);
		ColumnConfig c2 = new ColumnConfig("manifest_id", "Manifest Id", 100);
		configs.add(expander);
		configs.add(c01); configs.add(c0);
		configs.add(c1); configs.add(c2);
		return configs;
	}
	
	public static List<ColumnConfig> getColumnConfigProvider(RowExpander expander) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c2 = new ColumnConfig("provider_id", "pr_id", 100);
		ColumnConfig c3 = new ColumnConfig("provider_ip", "pr_ip", 100);
		ColumnConfig c4 = new ColumnConfig("provider_sla", "pr_sla", 100);
		ColumnConfig c5 = new ColumnConfig("provider_agreement_endpoint", "agr_endp", 100);
		
		c5.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				String html = "";
			 	String value = (String) model.get(property);
			 	html += "<textarea>" + value + "</textarea>";
			 	return html;
				}});		
		
		ColumnConfig c6 = new ColumnConfig("provider_initial_trust_value", "trust", 100);
		ColumnConfig c7 = new ColumnConfig("provider_initial_risk_value", "risk", 100);
		ColumnConfig c8 = new ColumnConfig("provider_initial_eco_value", "eco", 100);
		ColumnConfig c9 = new ColumnConfig("provider_initial_cost_value", "cost", 100);
		ColumnConfig c10 = new ColumnConfig("data_manager_info", "data_manager", 100);
		ColumnConfig c11 = new ColumnConfig("sla_details", "sla_details", 100);
		configs.add(expander);
		configs.add(c2); configs.add(c3);
		configs.add(c4); configs.add(c5);
		configs.add(c6); configs.add(c7);
		configs.add(c8); configs.add(c9);
		configs.add(c10); configs.add(c11);
		return configs;
	}
	public static List<ColumnConfig> getColumnConfigVM(RowExpander expander) {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c10 = new ColumnConfig("vm_id", "vm_id", 100);
		ColumnConfig c11 = new ColumnConfig("vm_type", "vm_type", 100);
		ColumnConfig c12 = new ColumnConfig("vm_status", "vm_status", 100);
		ColumnConfig c13 = new ColumnConfig("vm_deployment_duration_in_ms", "time", 100);
		configs.add(expander);
		configs.add(c10); configs.add(c11);
		configs.add(c12); configs.add(c13);
		return configs;
	}
	
	public static List<ColumnConfig> getColumnConfigIP() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c0 = new ColumnConfig("ip_name", "IP Name", 50);
		ColumnConfig c1 = new ColumnConfig("ip_ip", "IP Address", 50);
		ColumnConfig c2 = new ColumnConfig("ip_id", "Id", 50);
		ColumnConfig c3 = new ColumnConfig("ip_provider_type", "IP Provider Type", 50);
		ColumnConfig c4 = new ColumnConfig("cloud_qos_url", "Cloud Qos URL", 100);
		ColumnConfig c5 = new ColumnConfig("dm_gui", "Data Manager GUI", 100);
		configs.add(c0); configs.add(c1);
		configs.add(c2); configs.add(c3);
		configs.add(c4); configs.add(c5);
		c4.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				//Hyperlink link = new Hyperlink() {
				//@Override
				//public void onBrowserEvent(Event event) {
				//super.onBrowserEvent(Event event); }};
				//return link;
			 	String html = "";
			 	String value = (String) model.get(property);
			 	html += "<a href=\""+value+"\" target=\"_blank\">" + value
			 	//+ "Cloud Qos URL: click here"
			 	+ "</a>";
			 	return html;
				}});
		c5.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
			 	String html = "";
			 	String value = (String) model.get(property);
			 	html += "<a href=\""+value+"\" target=\"_blank\">" + value
			 	+ "</a>";
			 	return html;
				}});
		return configs;
	}
}