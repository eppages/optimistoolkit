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

public class CostResourceSP extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3959081103496722433L;

	public CostResourceSP() {
	}

	public CostResourceSP(String providerId, String timestamp,
			String servicename, String plancap,
			String planfloor) {
		setProviderId(providerId);
		setTime(timestamp);
		setService(servicename);
		setPlanCAP(plancap);
		setPlanFLOOR(planfloor);
	}


	// SP Cost
	public void setProviderId(String providerId) {
		set("providerId", providerId);
	}
	public void setTime(String timestamp) {
		set("timestamp", timestamp);
	}
	public void setService(String servicename) {
		set("servicename", servicename);
	}
	public void setPlanCAP(String plancap) {
		set("plancap", plancap);
	}
	public void setPlanFLOOR(String planfloor) {
		set("planfloor", planfloor);
	}

	public void setCostAVRG(String average) { 
		set("average", average);
	}

	public String getProviderId() {
		return (String) get("providerId");
	}

	public String getTime() {
		return (String) get("timestamp");
	}
	public String getService() {
		return (String) get("servicename");
	}
	public String getPlanCAP() {
		return (String) get("plancap");
	}
	public String getPlanFLOOR() {
		return (String) get("planfloor");
	}
	public String getCostAVRG() {
		return (String) get("average");
	}
}
