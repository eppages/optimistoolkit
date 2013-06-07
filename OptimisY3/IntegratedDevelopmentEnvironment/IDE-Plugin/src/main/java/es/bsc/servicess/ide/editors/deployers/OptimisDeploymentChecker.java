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

package es.bsc.servicess.ide.editors.deployers;

import integratedtoolkit.util.SMClient;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import es.bsc.servicess.ide.views.DeploymentChecker;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument.Vms;

public class OptimisDeploymentChecker implements DeploymentChecker {
	private String SM_endpoint;
	private SMClient SM_client;

	public OptimisDeploymentChecker(String SM_endpoint) {
		this.SM_endpoint = SM_endpoint;
		this.SM_client = new SMClient(SM_endpoint);
	}

	@Override
	public String getStatus(String serviceID) {
		try {
			return SM_client.getStatus(serviceID);
		} catch (XmlException e) {

			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Map<String, Map<String, String>> getMachines(String serviceID) {
		try {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, Vms> ips = SM_client.getMachines(serviceID);
			for (Entry<String, Vms> e : ips.entrySet()) {
				Vm[] vmarr = e.getValue().getVmArray();
				Map<String, String> vms = new HashMap<String, String>();
				for (Vm vm : vmarr) {
					vms.put(vm.getId(), vm.getType());
				}
				map.put(e.getKey(), vms);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error getting machines from service manager");
			return null;
		}
	}

	@Override
	public String getGraph(String serviceID) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void undeploy(String serviceID, boolean keepData) {
		SM_client.undeployService(serviceID, keepData);
		
	}

	@Override
	public Map getOtherValues(String serviceID) {
		try {
			Map map = SM_client.getInitialTREC(serviceID);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error getting machines from service manager");
			return null;
		}
	}
	
	

}
