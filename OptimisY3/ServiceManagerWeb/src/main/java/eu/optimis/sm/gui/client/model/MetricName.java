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
package eu.optimis.sm.gui.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MetricName extends BaseModel {

	private static final long serialVersionUID = 1L;

	public MetricName() {
	}

	public MetricName(String name, String type) {
		set("name", name);
		set("type", type);
	}

	public String getName() {
		return (String) get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public void setType(String type) {
		set("type", type);
	}

	public String toString() {
		return getName();
	}

	public String getType() {
		return (String) get("type");
	}

}
