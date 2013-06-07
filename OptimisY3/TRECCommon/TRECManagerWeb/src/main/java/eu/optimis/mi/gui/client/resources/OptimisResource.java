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
package eu.optimis.mi.gui.client.resources;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import eu.optimis.mi.gui.client.model.Folder;
import eu.optimis.mi.gui.client.model.MetricName;
import eu.optimis.mi.gui.client.model.MonitoringLevel;

public class OptimisResource {
	public static final String t1 = "PHYSICAL";
	public static final String t2 = "VIRTUAL";
	public static final String t3 = "SERVICE";
	public static final String t4 = "ENERGY";
	
	
	public static Folder getMonitoringTreeModel() {
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

	public static List<MonitoringLevel> getMonitoringLevels() {
		List<MonitoringLevel> levels = new ArrayList<MonitoringLevel>();
		levels.add(new MonitoringLevel("service", "optimis1"));
		levels.add(new MonitoringLevel("virtual", "optimis1"));
		levels.add(new MonitoringLevel("physical", "optimis1"));
		levels.add(new MonitoringLevel("energy", "optimis1"));
		return levels;
	}

	public static List<MonitoringLevel> getMonitoringLevels2() {
		List<MonitoringLevel> levels = new ArrayList<MonitoringLevel>();
		levels.add(new MonitoringLevel("service", "optimis2"));
		levels.add(new MonitoringLevel("virtual", "optimis2"));
		levels.add(new MonitoringLevel("physical", "optimis2"));
		levels.add(new MonitoringLevel("energy", "optimis2"));
		return levels;
	}
	
	public static List<MetricName> getServiceMetrics() {
		List<MetricName> list = new ArrayList<MetricName>();
		list.add(new MetricName("ThreadCount", t3));
		return list;
	}
	
	public static List<MetricName> getPhysicalMetrics() {
		List<MetricName> list = new ArrayList<MetricName>();
		list.add(new MetricName("count_of_users", t1));
		list.add(new MetricName("disk_free_space", t1));
		list.add(new MetricName("Downstream", t1));
		list.add(new MetricName("free_memory", t1));
		list.add(new MetricName("Upstream", t1));
		list.add(new MetricName("cpu_speed", t1));
		return list;
	}

	public static List<MetricName> getVirtualMetrics() {
		List<MetricName> list = new ArrayList<MetricName>();
		list.add(new MetricName("cpu_speed", t2));
		list.add(new MetricName("cpu_user", t2));
		list.add(new MetricName("cpu_vnum", t2));
		list.add(new MetricName("disk_total", t2));
		list.add(new MetricName("mem_total", t2));
		list.add(new MetricName("mem_used", t2));
		return list;
	}

	

	public static List<MetricName> getEnergyMetrics() {
		List<MetricName> list = new ArrayList<MetricName>();
		list.add(new MetricName("current", t4));
		list.add(new MetricName("real_power", t4));
		list.add(new MetricName("real_energy", t4));
		list.add(new MetricName("apparent_power", t4));
		list.add(new MetricName("apparent_energy", t4));
		list.add(new MetricName("power_factor", t4));
		return list;
	}

	public static List<ColumnConfig> getColumnConfig() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c0 = new ColumnConfig("physical_resource_id",
				"physical_resource_id", 100);
		ColumnConfig c1 = new ColumnConfig("virtual_resource_id",
				"virtual_resource_id", 100);
		ColumnConfig c2 = new ColumnConfig("service_resource_id",
				"service_resource_id", 100);
		ColumnConfig c3 = new ColumnConfig("metric_name", "metric_name", 100);
		ColumnConfig c4 = new ColumnConfig("metric_value", "metric_value", 100);
		ColumnConfig c5 = new ColumnConfig("metric_timestamp",
				"metric_timestamp", 100);
		ColumnConfig c6 = new ColumnConfig("resource_type", "resource_type",
				100);
		ColumnConfig c7 = new ColumnConfig("collector_id", "collector_id", 100);
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
	
	public static List<ColumnConfig> getSp2IpColumnConfig() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c0 = new ColumnConfig("serviceId",
				"serviceId", 100);
		ColumnConfig c1 = new ColumnConfig("serviceFormed",
				"serviceFormed", 100);
		ColumnConfig c2 = new ColumnConfig("runGap",
				"runGap", 100);
		ColumnConfig c3 = new ColumnConfig("elasticity", "elasticity", 100);
		ColumnConfig c4 = new ColumnConfig("ipReaction", "ipReaction", 100);
		ColumnConfig c5 = new ColumnConfig("sla",
				"sla", 100);
		ColumnConfig c6 = new ColumnConfig("legal", "legal",
				100);
		ColumnConfig c7 = new ColumnConfig("serviceTrust", "serviceTrust", 100);
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
	
	public static List<ColumnConfig> getIp2SpColumnConfig() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig c0 = new ColumnConfig("serviceId",
				"serviceId", 100);
		ColumnConfig c1 = new ColumnConfig("serviceRisk",
				"serviceRisk", 100);
		ColumnConfig c2 = new ColumnConfig("security",
				"security", 100);
		ColumnConfig c3 = new ColumnConfig("reliability", "reliability", 100);
		ColumnConfig c4 = new ColumnConfig("performance", "performance", 100);
		ColumnConfig c5 = new ColumnConfig("legal", "legal", 100);
		ColumnConfig c6 = new ColumnConfig("serviceTrust", "serviceTrust",
				100);
		configs.add(c0);
		configs.add(c1);
		configs.add(c2);
		configs.add(c3);
		configs.add(c4);
		configs.add(c5);
		configs.add(c6);
		return configs;

	}

}
