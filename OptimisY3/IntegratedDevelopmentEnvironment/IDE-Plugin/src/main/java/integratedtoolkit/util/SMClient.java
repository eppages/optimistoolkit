/*
 *  Copyright 2011-2012 Barcelona Supercomputing Center (www.bsc.es)
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

package integratedtoolkit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import es.bsc.servicess.ide.editors.deployers.TRECValues;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.VmsDocument.Vms;

public class SMClient {
	private final static String MI_DATA_PATH = ":8087/data/";
	private WebResource serviceManager;

	public SMClient(String location) {
		Client c = Client.create();
		serviceManager = c.resource(location);
	}

	public String getStatus(String serviceID) throws XmlException {
		String xml = serviceManager.path(serviceID).get(String.class);
		ServiceDocument service = ServiceDocument.Factory.parse(xml);
		return service.getService().getStatus();
	}

	public String updateVmStatus(String serviceId,
			String infrastructureProviderId, String vmId, String status) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").path(vmId)
				.path("status").put(String.class, status);
	}

	public Map<String, String> getMonitoringDataEndpoints(String serviceID)
			throws XmlException {
		Map<String, String> map = new HashMap<String, String>();
		String xml = serviceManager.path(serviceID).get(String.class);
		ServiceDocument service = ServiceDocument.Factory.parse(xml);
		InfrastructureProvider[] ips = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ip : ips) {
			String ip_address = ip.getIpAddress();
			map.put(ip.getId(), "http://" + ip_address + MI_DATA_PATH);
		}

		return map;
	}

	public Map<String, String> getIPAddresses(String serviceID)
			throws XmlException {
		Map<String, String> map = new HashMap<String, String>();
		String xml = serviceManager.path(serviceID).get(String.class);
		ServiceDocument service = ServiceDocument.Factory.parse(xml);
		InfrastructureProvider[] ips = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ip : ips) {
			map.put(ip.getId(), ip.getIpAddress());
		}

		return map;
	}

	public Map<String, Vms> getMachines(String serviceID) throws XmlException {
		Map<String, Vms> map = new HashMap<String, Vms>();
		String xml = serviceManager.path(serviceID).get(String.class);
		ServiceDocument service = ServiceDocument.Factory.parse(xml);
		InfrastructureProvider[] ips = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ip : ips) {
			map.put(ip.getId(), ip.getVms());
		}

		return map;
	}
	
	public Map<String, TRECValues> getInitialTREC(String serviceID) throws XmlException {
		Map<String, TRECValues> map = new HashMap<String, TRECValues>();
		String xml = serviceManager.path(serviceID).get(String.class);
		ServiceDocument service = ServiceDocument.Factory.parse(xml);
		InfrastructureProvider[] ips = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ip : ips) {
			map.put(ip.getId(),new TRECValues(ip.getInitialTrustValue(), ip.getInitialRiskValue(),ip.getInitialEcoValue(),ip.getInitialCostValue()));
		}

		return map;
	}
	
	public void undeployService(String serviceID, boolean keepData){
		serviceManager.path(serviceID).path("undeploy").post(Boolean.toString(keepData));
	}
	

	public static void main(String[] args) throws XmlException {
		SMClient smc = new SMClient(
				"");
		String serviceID = "c5e0d2ad-4036-4027-bc2d-f15c8ac60e4d";
		System.out.println("Status: " + smc.getStatus(serviceID));
		/*Map<String, String> map = smc.getIPAddresses(serviceID);
		for (Entry<String, String> e : map.entrySet()) {
			System.out.println("Entry: " + e.getKey() + " - " + e.getValue());
		}
		map = smc.getMonitoringDataEndpoints(serviceID);
		for (Entry<String, String> e : map.entrySet()) {
			System.out.println("Entry: " + e.getKey() + " - " + e.getValue());
		}
		Map<String, Vms> map2 = smc.getMachines(serviceID);
		for (Entry<String, Vms> e : map2.entrySet()) {
			System.out.println("Entry: " + e.getKey() + " - "
					+ e.getValue().toString());
		}
		System.out.println("id:" + map2.get("umea").getVmArray()[1].getId());*/
		smc.updateVmStatus(serviceID, "umea",
				"130.239.48.10	", "Saved");
		smc.undeployService(serviceID, false);
		System.out.println("Status: " + smc.getStatus(serviceID));
	}

}
