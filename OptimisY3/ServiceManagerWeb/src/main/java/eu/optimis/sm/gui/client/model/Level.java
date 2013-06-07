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

public class Level extends BaseModel {

	private static final long serialVersionUID = 4459304695183206662L;

	public Level() {
	}

	public Level(String name, String host) {
		set("name", name);
		set("host", host);
	}

	public String getHost() {
		return get("host");
	}

	public String getName() {
		return (String) get("name");
	}

	public String toString() {
		return getName();
	}

}
