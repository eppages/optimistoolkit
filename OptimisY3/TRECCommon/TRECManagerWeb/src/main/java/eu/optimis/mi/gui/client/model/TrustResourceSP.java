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

public class TrustResourceSP extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3959081103496722433L;

	public TrustResourceSP(){}
	public TrustResourceSP(String providerId, String providerTrust){
		setproviderId(providerId);
		setproviderTrust(providerTrust);
	}
	// SP TRUST
		public void setproviderId(String providerId){
			set("providerId", providerId);
		}
		
		public void setproviderTrust(String providerTrust){
			set("providerTrust", providerTrust);
		}
		
		public String getproviderId(){
			return (String) get("providerId");
		}
		
		public String getproviderTrust(){
			return (String) get("providerTrust");
		}
}
