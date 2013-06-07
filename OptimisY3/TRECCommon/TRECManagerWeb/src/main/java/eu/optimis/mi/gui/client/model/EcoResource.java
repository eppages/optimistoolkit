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

public class EcoResource extends BaseModel {

/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	
	public EcoResource(){}
	public EcoResource(String nodeId, String serviceId, String VMId, String timestamp, String ecoValue, String metric){
		setNodeId(nodeId);
		setServiceId(serviceId);
		setVMId(VMId);
		setTimestamp(timestamp);
		setEcoValue(ecoValue);
                setMetric(metric);
	}
		
	public String getNodeId(){
		return (String) get("nodeId");
	}
        
        public String getNodeEco(){
		return (String) get("nodeEco");
	}
        
        public String getTimeLabel(){
		return (String) get("timeLabel");
	}
	
	public String getServiceId(){
		return (String) get("serviceId");
	}
	
	public String getVMId(){
		return (String) get("VMId");
	}
	
	public String getTimestamp(){
		return (String) get("timestamp");
	}
	
	public String getEcoValue(){
		return (String) get("ecoValue");
	}
        
        public String getMetric(){
		return (String) get("metric");
	}
	
	public void setNodeId(String nodeId){
		set("nodeId",nodeId);
	}
        
        public void setNodeEco(String nodeEco){
		set("nodeEco",nodeEco);
	}
        
        public void setTimeLabel(String timelabel){
		set("timeLabel",timelabel);
	}
	
	public void setServiceId(String serviceId){
		set("serviceId",serviceId);
	}
	
	public void setVMId(String VMId){
		set("VMId",VMId);
	}
	
	public void setTimestamp(String timestamp){
		set("timestamp",timestamp);
	}
	
	public void setEcoValue(String ecoValue){
		set("ecoValue",ecoValue);
	}
	
        public void setMetric(String metric){
		set("metric",metric);
	}
}
