/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.monitoring_resources;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="monitoring_resource")
public class MonitoringResourceDataset {
	private String physical_resource_id;

	private String virtual_resource_id;
	private String service_resource_id;
	private String resource_type;
	private String monitoring_information_collector_id;
	private Date metric_timestamp;
	private String metric_name;
	private String metric_value;
	private String metric_unit;
	
	public MonitoringResourceDataset(){}
	public MonitoringResourceDataset(String service_resource_id, String virtual_resource_id, String physical_resource_id,
			String resource_type, String monitoring_information_collector_id, String metric_name,String metric_value, String metric_unit,
			Date metric_timestamp){	
		this.service_resource_id = service_resource_id;
		this.virtual_resource_id = virtual_resource_id;
		this.physical_resource_id = physical_resource_id;
		this.resource_type = resource_type;
		this.monitoring_information_collector_id = monitoring_information_collector_id;
		this.metric_name = metric_name;
		this.metric_value = metric_value;
		this.metric_unit = metric_unit;
		this.metric_timestamp = metric_timestamp;
		
	}
	public String getPhysical_resource_id() {
		return physical_resource_id;
	}

	public void setPhysical_resource_id(String physical_resource_id) {
		this.physical_resource_id = physical_resource_id;
	}

	public String getVirtual_resource_id() {
		return virtual_resource_id;
	}

	public void setVirtual_resource_id(String virtual_resource_id) {
		this.virtual_resource_id = virtual_resource_id;
	}

	public String getService_resource_id() {
		return service_resource_id;
	}

	public void setService_resource_id(String service_resource_id) {
		this.service_resource_id = service_resource_id;
	}

	public String getMonitoring_information_collector_id() {
		return monitoring_information_collector_id;
	}

	public void setMonitoring_information_collector_id(
			String monitoring_information_collector_id) {
		this.monitoring_information_collector_id = monitoring_information_collector_id;
	}

	public String getMetric_name() {
		return metric_name;
	}

	public void setMetric_name(String metric_name) {
		this.metric_name = metric_name;
	}

	public String getMetric_value() {
		return metric_value;
	}

	public void setMetric_value(String metric_value) {
		this.metric_value = metric_value;
	}

	public String getMetric_unit() {
		return metric_unit;
	}

	public void setMetric_unit(String metric_unit) {
		this.metric_unit = metric_unit;
	}

	public String getResource_type() {
		return resource_type;
	}

	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
	}

	public Date getMetric_timestamp() {
		return metric_timestamp;
	}

	public void setMetric_timestamp(Date metric_timestamp) {
		this.metric_timestamp = metric_timestamp;
	}
}
