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

import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;


public class COClient {

	Logger log = Logger.getLogger(this.getClass().getName());

	private String ip;
	private int port;
	private String uri;
	
	private CloudOptimizerRESTClient co;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public COClient (String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		co = new CloudOptimizerRESTClient(ip,port);
	}
	
	public String getVMName(String vmId){
		return co.getVMName(vmId);
	}
	
	public List<String> getVMsofService(String serviceId){
		return co.getVMsIdsOfService(serviceId);
	}
	
	public boolean notifyCO (String idEntity, int type, double value)
	{		
		HolisticManagementRESTClient myHMClient = new HolisticManagementRESTClient();
		try
		{
			switch (type)
			{
			case 0:
				myHMClient.notifyInfrastructureTrust(value);
				break;
			case 1:
				myHMClient.notifyServiceTrust(idEntity, value);
			}
		}
		catch (Exception ex)
		{
			log.error("Error when sending notification to the CO!");
			log.error(ex.getMessage());
			return false;
		}
				
		return true;
	}
	
}
