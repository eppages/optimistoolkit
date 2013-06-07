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

public class TrustResource extends BaseModel {

/**
	 * 
	 */
	private static final long serialVersionUID = -3074661670652673938L;

//	private static final long serialVersionUID = 4180850375922740286L;
	
	public TrustResource(){}
	
	public TrustResource(String ipId, String ipTrust){
		setIPId(ipId);
		setIPTrust(ipTrust);
		
	}
	
	
	// IP TRUST
	public void setIPId(String ipId){
		set("ipId", ipId);
	}
	
	public void setIPTrust(String ipTrust){
		set("ipTrust", ipTrust);
	}
	
	public String getIPId(){
		return (String) get("ipId");
	}
	
	public String getIPTrust(){
		return (String) get("ipTrust");
	}
	
}
