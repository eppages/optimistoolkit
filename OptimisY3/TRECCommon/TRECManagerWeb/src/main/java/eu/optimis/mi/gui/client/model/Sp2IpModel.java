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

public class Sp2IpModel extends BaseModel {

	private static final long serialVersionUID = -8866086357842206833L;

	public Sp2IpModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Sp2IpModel(String serviceId, String serviceFormed, String runGap,
			String elasticity, String ipReaction, String sla, String legal,
			String serviceTrust, String ipId) {
		setServiceId(serviceId);
		setServiceFormed(serviceFormed);
		setRunGap(runGap);
		setElasticity(elasticity);
		setIpReaction(ipReaction);
		setSla(sla);
		setLegal(legal);
		setServiceTrust(serviceTrust);
		setIpId(ipId);
	}

	public String getServiceId() {
		return (String) get("serviceId");
	}

	public void setServiceId(String serviceId) {
		set("serviceId", serviceId);
	}
	
	public String getServiceFormed() {
		return (String) get("serviceFormed");
	}

	public void setServiceFormed(String serviceFormed) {
		set("serviceFormed", serviceFormed);
	}
	
	public String getRunGap() {
		return (String) get("runGap");
	}

	public void setRunGap(String runGap) {
		set("runGap", runGap);
	}
	
	public String getElasticity() {
		return (String) get("elasticity");
	}

	public void setElasticity(String elasticity) {
		set("elasticity", elasticity);
	}
	
	public String getIpReaction() {
		return (String) get("ipReaction");
	}

	public void setIpReaction(String ipReaction) {
		set("ipReaction", ipReaction);
	}
	
	public String getSla() {
		return (String) get("sla");
	}

	public void setSla(String sla) {
		set("sla", sla);
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
	
	public String getIpId() {
		return (String) get("ipId");
	}

	public void setIpId(String ipId) {
		set("ipId", ipId);
	}
}
