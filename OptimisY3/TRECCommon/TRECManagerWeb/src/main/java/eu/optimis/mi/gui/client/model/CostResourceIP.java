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

public class CostResourceIP extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3959081103496722433L;

	public CostResourceIP() {
	}

	public CostResourceIP(String providerId, String costPerVCPU,
			String costPerMBMemory, String costPerGBStorage,
			String costPerGBUploaded, String costPerGBDownloaded,
			String costPerWatt) {
		setProviderId(providerId);
		setCostPerVCPU(costPerVCPU);
		setCostPerMBMemory(costPerMBMemory);
		setCostPerGBStorage(costPerGBStorage);
		setCostPerGBUploaded(costPerGBUploaded);
		setCostPerGBDownloaded(costPerGBDownloaded);
		setCostPerWatt(costPerWatt);
	}

	// IP Cost
	public void setProviderId(String providerId) {
		set("providerId", providerId);
	}

	public void setCostPerVCPU(String costPerVCPU) { // was providerCost
		set("costPerVCPU", costPerVCPU);
	}

	public void setCostPerMBMemory(String costPerMBMemory) { 
		set("costPerMBMemory", costPerMBMemory);
	}

	public void setCostPerGBStorage(String costPerGBStorage) { 
		set("costPerGBStorage", costPerGBStorage);
	}

	public void setCostPerGBUploaded(String costPerGBUploaded) {
		set("costPerGBUploaded", costPerGBUploaded);
	}

	public void setCostPerGBDownloaded(String costPerGBDownloaded) { 
		set("costPerGBDownloaded", costPerGBDownloaded);
	}

	public void setCostPerWatt(String costPerWatt) { 
		set("costPerWatt", costPerWatt);
	}

	public void setCostTotal(String costTotal) { 
		set("costTotal", costTotal);
	}

	public String getProviderId() {
		return (String) get("providerId");
	}

	public String getCostPerVCPU() {
		return (String) get("costPerVCPU");
	}
	public String getCostPerMBMemory() {
		return (String) get("costPerMBMemory");
	}
	public String getCostPerGBStorage() {
		return (String) get("costPerGBStorage");
	}
	public String getCostPerGBUploaded() {
		return (String) get("costPerGBUploaded");
	}
	public String getCostPerGBDownloaded() {
		return (String) get("costPerGBDownloaded");
	}
	public String getCostPerWatt() {
		return (String) get("costPerWatt");
	}
	public String getCostTotal() {
		return (String) get("costTotal");
	}
}
