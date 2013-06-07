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

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class ServiceProviderVM extends BaseModel implements Serializable {

private static final long serialVersionUID = -5793462378870650903L;

public ServiceProviderVM() {
  }

  public ServiceProviderVM(String vm_id, String vm_type, String vm_status, String vm_deployment_duration_in_ms) {
	  
    set("vm_id", vm_id);
    set("vm_type", vm_type);
    set("vm_status", vm_status);
    set("vm_deployment_duration_in_ms", vm_deployment_duration_in_ms);
  }
 
  public String getName() {
    return (String) get("vm_id");
  }

   public String toString() {
	    return new String("<br> vm id = "+get("vm_id")+
	    		"<br> type = "+get("vm_type")+
	    		"<br> status = "+get("vm_status")+	    		
	    		"<br> deployment duration in ms = "+get("vm_deployment_duration_in_ms")
	    		+ "<br>"
	    		);
  }
 
}
