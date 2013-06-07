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

public class IP extends BaseModel implements IsSerializable {

private static final long serialVersionUID = 3462378870650903L;

public IP() {
	set("ip_name", null);
    set("ip_ip", null);
	set("ip_id", null);
    set("ip_provider_type", null);
    set("cloud_qos_url", null);
    set("dm_gui", null);
  }

  public IP(String ip_name, String ip_ip, String ip_id,
		  String ip_provider_type, String cloud_qos_url, String dm_gui) {
	set("ip_name", ip_name);
    set("ip_ip", ip_ip);
    set("ip_id", ip_id);
    set("ip_provider_type", ip_provider_type);
    set("cloud_qos_url", cloud_qos_url);
    set("dm_gui", dm_gui);
   }

  public String getName() {
    return (String) get("ip_name");
  }

   public String toString() {
    return new String(get("ip_name").toString() + get("ip_ip").toString()
    		+ get("ip_id").toString() + get("ip_provider_type")
    		+ get("cloud_qos_url") + get("dm_gui"));
  }
}
