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

package eu.optimis.mi.aggregator.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InformationCollector")
public class InformationCollector {
	public InformationCollector() {

	}

	public InformationCollector(String collector_id, String name, String collector_script_path, int time_interval_in_ms) {
		this.collector_id = collector_id;
		this.name = name;
		this.collector_script_path = collector_script_path;
		this.time_interval_in_ms = time_interval_in_ms;
	}
	public InformationCollector(int row_id, String collector_id, String name, String collector_script_path, int time_interval_in_ms,String description, String connection_arguments, String created_by, Date creation_date ) {
		this.row_id = row_id;
		this.collector_id = collector_id;
		this.name = name;
		this.collector_script_path = collector_script_path;
		this.time_interval_in_ms = time_interval_in_ms;
		this.description = description;
		this.connection_arguments = connection_arguments;
		this.created_by = created_by;
		this.creation_date = creation_date;
	}


	public int getRow_id() {
		return row_id;
	}

	public void setRow_id(int row_id) {
		this.row_id = row_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCollector_script_path() {
		return collector_script_path;
	}

	public void setCollector_script_path(String collector_script_path) {
		this.collector_script_path = collector_script_path;
	}

	public int getTime_interval_in_ms() {
		return time_interval_in_ms;
	}

	public void setTime_interval_in_ms(int time_interval_in_ms) {
		this.time_interval_in_ms = time_interval_in_ms;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConnection_arguments() {
		return connection_arguments;
	}

	public void setConnection_arguments(String connection_arguments) {
		this.connection_arguments = connection_arguments;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public Date getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}

	public String getCollector_id() {
		return collector_id;
	}

	public void setCollector_id(String collector_id) {
		this.collector_id = collector_id;
	}

	private int row_id;
	private String name;
	private String collector_script_path;
	private int time_interval_in_ms;
	private String description;
	private String connection_arguments;
	private String created_by;
	private Date creation_date;
	private String collector_id;

}
