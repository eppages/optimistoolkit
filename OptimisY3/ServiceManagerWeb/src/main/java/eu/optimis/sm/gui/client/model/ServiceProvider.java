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

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ServiceProvider extends BaseModel implements IsSerializable
 {
	private static final long serialVersionUID = 793462378870650903L;

public ServiceProvider() {
  }

  public ServiceProvider(
		  String provider_id, String provider_ip, String provider_sla,
		  String provider_agreement_endpoint, String provider_initial_trust_value,
		  String provider_initial_risk_value, String provider_initial_eco_value,
		  String provider_initial_cost_value,
		  String data_manager_info, String sla_details,
		  String listServiceProviderVMStr
		  ) {
	  
    set("provider_id", provider_id);
    set("provider_ip", provider_ip);
    set("provider_sla", provider_sla);
    set("provider_agreement_endpoint", provider_agreement_endpoint);
    set("provider_initial_trust_value", provider_initial_trust_value);
    set("provider_initial_risk_value", provider_initial_risk_value);
    set("provider_initial_eco_value", provider_initial_eco_value);
    set("provider_initial_cost_value", provider_initial_cost_value);
    set("data_manager_info", data_manager_info);
    set("sla_details", sla_details);
    set("listServiceProviderVMStr", listServiceProviderVMStr);
  }
  public String getName() {
	    return (String) get("provider_id");
	  }

	   public String toString() {
	    return new String("<br> <br>" +
	    		"<table align=\"left\" border=\"0\" width=\"100%\">" +  // align=\"left\" border=\"1\" width=\"50%\"
	    		//"<font size=\"1\">"  +
	    		"<tr> <td width=\"20%\"> Provider Id </td> <td width=\"40%\">"
	    		+ get("provider_id") + "</td> <td width=\"40%\">  </td> </tr>"+
	    		"<tr> <td> Provider IP Address </td> <td colspan=\"2\"> " //<a href=\"http://"
	    		//+ get("provider_ip") + ":8080/TrecgManagerWeb\">"
	    		+ get("provider_ip") //+": TREC Monitoring</a>"
	    		+ "</td> </tr>"+
	    		"<tr> <td> SLA </td> <td colspan=\"2\">"
	    		+ get("provider_sla") + "</td> </tr>"+
	    		"<tr> <td> Agreement Endpoint </td> <td colspan=\"2\">"
	    		+ get("provider_agreement_endpoint") + "</td> </tr>"+
	    		"<tr> <td> Initial Trust Value </td> <td colspan=\"2\">"
	    		+ get("provider_initial_trust_value") + "</td> </tr>"+
	    		"<tr> <td> Initial Risk Value </td> <td colspan=\"2\">"
	    		+ get("provider_initial_risk_value") + "</td> </tr>"+
	    		"<tr> <td> Initial Eco Value </td> <td colspan=\"2\">"
	    		+ get("provider_initial_eco_value") + "</td> </tr>"+
	    		"<tr> <td> Initial Cost Value </td> <td colspan=\"2\">"
	    		+ get("provider_initial_cost_value") + "</td> </tr>"+
	    		"<tr> <td> Data Manager Info </td> <td> . </td> <td>"
	    		+ get("data_manager_info") + "</td>  </tr>"+
	    		"<tr> <td> SLA Details </td> <td>"
	    		+ get("sla_details") + "</td> <td> . </td> </tr>"+
	    		"<tr> <td> Virtual Machines </td> <td colspan=\"2\">"
	    		+ get("listServiceProviderVMStr") + "</td> </tr>" +
	    		"<tr> <td> . </td colspan=\"2\"> <td>   </td> </tr>" +
	    		" </table> <br> <hr> <br>"
	    		);
	   }
}
