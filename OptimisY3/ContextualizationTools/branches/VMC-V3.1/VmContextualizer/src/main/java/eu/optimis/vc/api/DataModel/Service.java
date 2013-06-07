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
package eu.optimis.vc.api.DataModel;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.vc.api.DataAggregator.SecurityClient;
import eu.optimis.vc.api.DataAggregator.ServiceManifestClient;

/**
 * Contains functions for the purpose of gathering context data and storing the
 * ContextData object of a service.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.2
 */
public class Service {

	private String serviceId;
	private ServiceManifestClient serviceManifestClient;
	private ContextData contextData;
	private SecurityClient securityClient;

	/**
	 * Constructor.
	 * 
	 * @param manifest
	 *            The manifest to associate with a given service.
	 */
	public Service(Manifest manifest) {
		if (manifest != null) { 
			serviceId = manifest.getVirtualMachineDescriptionSection().getServiceId();
			serviceManifestClient = new ServiceManifestClient(manifest);
		}
	}

	/**
	 * Parses the Service Manifest for contextualization data
	 */
	public void parseManifest() {
		contextData = serviceManifestClient.parse();
	}

	/**
	 * Generates contextualization data from other OPTIMIS components
	 */
	public void generateContextData() {
		securityClient = new SecurityClient();
		contextData = securityClient.generateKeys(contextData);
	}

	/**
	 * Getter for ContextData objects
	 * 
	 * @return the contextData
	 */
	public ContextData getContextData() {
		return contextData;
	}

	/**
	 * Setter for ContextData objects
	 * 
	 * @param contextData
	 *            the contextData to set
	 */
	public void setContextData(ContextData contextData) {
		this.contextData = contextData;
	}

	/**
	 * Getter for Service Manifest ID
	 * 
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

}
