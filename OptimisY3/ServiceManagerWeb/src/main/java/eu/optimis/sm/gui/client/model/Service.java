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
import com.google.gwt.user.client.rpc.IsSerializable;

public class Service extends BaseModel implements IsSerializable {

private static final long serialVersionUID = 3462378870650903L;

public Service() {
	set("service_number", null);
    set("service_id", null);
    set("service_status", null);
    set("manifest_id", null);
    set("listServiceProviderStr", null);
  }

  public Service(String service_number, String service_id, String status, String manifest_id,
		  String listServiceProviderStr) {
	set("service_number", service_number);
    set("service_id", service_id);
    set("service_status", status);
    set("manifest_id", manifest_id);
    set("listServiceProviderStr", listServiceProviderStr);
  }

  public String getName() {
    return (String) get("service_id");
  }

   public String toString() {
    return new String(get("service_id").toString() + get("service_status").toString() + get("manifest_id").toString()
    		+ get("listServiceProviderStr"));
  }
}
