/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.clients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;


public class ServiceMgrClient {

	Logger log = Logger.getLogger(this.getClass().getName());

	String host;
	String port;
	ServiceManagerClient smc;
	
	public ServiceMgrClient(){
		host = PropertiesUtils.getProperty("TRUST","sm.host");
		port = PropertiesUtils.getProperty("TRUST","sm.port");
		smc = new ServiceManagerClient(host, port);
	}
	
	public ServiceMgrClient(String host, int port){
		smc = new ServiceManagerClient(host, String.valueOf(port));
	}
	
	public List<String> getSLAIDs(String serviceId){
		return array2list(smc.getSlaIds(serviceId));
	}
	
	public List<String> getIPIDs(String serviceId){
		return array2list(smc.getInfrastructureProviderIds(serviceId));
	}
	
	public String getSLAIDbyIP(String serviceId, String ipId){
		return smc.getSlaId(serviceId, ipId);
	}
	private List<String> array2list(String[] strarray){
		List<String> strlist = new ArrayList<String>();
		for (int i = 0; i < strarray.length; i++){
			strlist.add(strarray[i]);
		}
		return strlist;
	}
}
