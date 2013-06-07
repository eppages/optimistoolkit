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
/**
 *
 * @author scsmk
 */
public class RiskProactiveResource extends BaseModel {
    private static final long serialVersionUID = -3074661670652673938L;
    
    public RiskProactiveResource()
    {}
    
    public RiskProactiveResource(String samplingPoint, String vm_id, String vm_avail)
    {
                setvmId(vm_id);
		
		//setSPId(spId);
                setsamplingPoint(samplingPoint);
		
                setvmavail(vm_avail);
		
    }
    
     public void setsamplingPoint(String sampPt){
		set("samplingPoint",sampPt);
	}
     
     public void setvmavail(String vmavail){
		set("vm_avail",vmavail);
	}
	
     public void setvmId(String vmId){
		set("vm_id", vmId);
	}
    
       public String getvmId(){
		return (String) get("vm_id");
	}
       
        public String getSamplingpoint(){
		return (String) get("samplingPoint");
	}
    
        	public String getvmavail(){
		return (String) get("vm_avail");
	}
	
}

	
	