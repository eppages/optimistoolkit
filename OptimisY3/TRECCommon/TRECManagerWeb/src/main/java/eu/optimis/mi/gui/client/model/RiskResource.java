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

public class RiskResource extends BaseModel {

	private static final long serialVersionUID = -3074661670652673938L;

	public RiskResource() {
	}

	//Describes the data points
	public RiskResource(String serviceId, String providerId, String providerType, String servicePhase) {
		setServiceId(serviceId);
		setProviderId(providerId);
		setProviderType(providerType);
		setServicePhase(servicePhase);
	}
	
	//X and Y values of data points
	public RiskResource(String timeStamp, String riskValue, String graphType) {
		setGraphType(graphType);
		setRiskValue(riskValue);
		setTimeStamp(timeStamp);
	}
	
	//Service ID
	public void setServiceId(String serviceId) {
		set("serviceId", serviceId);
	}	
	public String getServiceId(){
		return (String) get("serviceId");
	}
	
	//Provider ID
	public void setProviderId(String providerId) {
		set("providerId", providerId);
	}
	public String getProviderId() {
		return (String) get("providerId");
	}
	
	//Provider Type
	public void setProviderType(String providerType) {
		set("providerType", providerType);
	}
	public String getProviderType() {
		return (String) get("providerType");
	}
	
	//Service Phase
	public void setServicePhase(String servicePhase) {
		set("servicePhase", servicePhase);
	}
	public String getServicePhase() {
		return (String) get("servicePhase");
	}
	
	//Graph Type for this data point
	public void setGraphType(String graphType){
		set("graphType", graphType);
	}
	public String getGraphType(){
		return (String) get("graphType");
	}
	
	//Y-Axis
	public void setRiskValue(String riskValue){
		set("riskValue", riskValue);
	}
	public String getRiskValue(){
		return (String) get("riskValue");
	}

	//X-Axis
	public void setTimeStamp(String timeStamp){
		set("timeStamp", timeStamp);
	}
	public String getTimeStamp(){
		return (String) get("timeStamp");
	}
	
	//Error Message
	public void setErrorMessage(String errorMessage){
		set("errorMessage", errorMessage);
	}
	public String getErrorMessage(){
		return (String) get("errorMessage");
	}
}