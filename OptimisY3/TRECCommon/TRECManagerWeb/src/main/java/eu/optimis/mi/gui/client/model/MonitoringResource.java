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

public class MonitoringResource extends BaseModel {

	private static final long serialVersionUID = 4180850375922740286L;

	public MonitoringResource() {

	}

	public MonitoringResource(String physical_resource_id,
			String resource_type, String metric_name, String metric_value) {
		setPhysicalResourceId(physical_resource_id);
		setMetricName(metric_name);
		setMetricValue(metric_value);
		setResourceType(resource_type);
	}

	public MonitoringResource(String physical_resource_id,
			String resource_type, String metric_name, String metric_value,
			String metric_unit, String metric_timestamp, String collector_id) {
		setPhysicalResourceId(physical_resource_id);
		setMetricName(metric_name);
		setMetricValue(metric_value);
		setResourceType(resource_type);
		setMetricUnit(metric_unit);
		setMetricTimestamp(metric_timestamp);
		setCollectorId(collector_id);
		// setMetricTimestamp(Calendar.getInstance().getTime());
	}

	public void setMetricName(String metric_name) {
		set("metric_name", metric_name);
	}

	public void setMetricValue(String metric_value) {
		set("metric_value", metric_value);
	}

	public void setMetricUnit(String metric_unit) {
		set("metric_unit", metric_unit);
	}

	public void setMetricTimestamp(String metric_timestamp) {
		set("metric_timestamp", metric_timestamp);
	}

	public void setResourceType(String resource_type) {
		set("resource_type", resource_type);
	}

	public void setPhysicalResourceId(String physical_resource_id) {
		set("physical_resource_id", physical_resource_id);
	}

	public void setVirtualResourceId(String virtual_resource_id) {
		set("virtual_resource_id", virtual_resource_id);
	}

	public void setServiceResourceId(String service_resource_id) {
		set("service_resource_id", service_resource_id);
	}

	public void setCollectorId(String collector_id) {
		set("collector_id", collector_id);
	}

	public String getPhysicalResourceId() {
		return (String) get("physical_resource_id");
	}

	public String getVirtuallResourceId() {
		return (String) get("virtual_resource_id");
	}

	public String getServiceResourceId() {
		return (String) get("service_resource_id");
	}

	public String getMetricName() {
		return (String) get("metric_name");
	}

	public String getMetricValue() {
		return (String) get("metric_value");
	}

	public String getMetricUnit() {
		return (String) get("metric_unit");
	}

	public String getMetricTimestamp() {
		return (String) get("metric_timestamp");
	}

	public String getCollectorId() {
		return (String) get("collector_id");
	}

	public String getResourceType() {
		return (String) get("resource_type");
	}

}
