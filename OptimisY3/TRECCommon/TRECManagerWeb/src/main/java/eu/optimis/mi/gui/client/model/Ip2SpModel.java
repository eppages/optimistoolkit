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

public class Ip2SpModel extends BaseModel {

	private static final long serialVersionUID = -4197050709597585669L;

	public Ip2SpModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Ip2SpModel(String serviceId, String serviceRisk, String security,
			String reliability, String performance, String legal,
			String serviceTrust, String spId) {
		setServiceId(serviceId);
		setServiceRisk(serviceRisk);
		setSecurity(security);
		setReliability(reliability);
		setPerformance(performance);
		setLegal(legal);
		setServiceTrust(serviceTrust);
		setSpId(spId);
	}

	public String getServiceId() {
		return (String) get("serviceId");
	}

	public void setServiceId(String serviceId) {
		set("serviceId", serviceId);
	}

	public String getServiceRisk() {
		return (String) get("serviceRisk");
	}

	public void setServiceRisk(String serviceRisk) {
		set("serviceRisk", serviceRisk);
	}

	public String getSecurity() {
		return (String) get("security");
	}

	public void setSecurity(String security) {
		set("security", security);
	}

	public String getReliability() {
		return (String) get("reliability");
	}

	public void setReliability(String reliability) {
		set("reliability", reliability);
	}
	
	public String getPerformance() {
		return (String) get("performance");
	}

	public void setPerformance(String performance) {
		set("performance", performance);
	}
	
	public String getLegal() {
		return (String) get("legal");
	}

	public void setLegal(String legal) {
		set("legal", legal);
	}
	
	public String getServiceTrust() {
		return (String) get("serviceTrust");
	}

	public void setServiceTrust(String serviceTrust) {
		set("serviceTrust", serviceTrust);
	}
	
	public String getSpId() {
		return (String) get("spId");
	}

	public void setSpId(String spId) {
		set("spId", spId);
	}
}
