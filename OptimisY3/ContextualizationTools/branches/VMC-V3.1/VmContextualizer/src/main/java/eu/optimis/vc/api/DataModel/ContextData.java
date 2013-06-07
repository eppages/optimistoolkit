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

import java.util.HashMap;

import eu.optimis.vc.api.DataModel.ContextDataTypes.SecurityKey;

/**
 * Class for storing the context data of a associated {@link Service}.
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.2
 */
public class ContextData {

	private HashMap<String, SecurityKey> securityKeys;
	private HashMap<String, VirtualMachine> virtualMachines;

	/**
	 * Default constructor that creates a {@link HashMap} for the storage of
	 * contextualization data on a per {@link VirtualMachine} basis and at the
	 * entire service level across all VMs
	 */
	public ContextData() {
		securityKeys = new HashMap<String, SecurityKey>();
		virtualMachines = new HashMap<String, VirtualMachine>();
	}

	/**
	 * @return the securityKeys
	 */
	public HashMap<String, SecurityKey> getSecurityKeys() {
		return securityKeys;
	}

	/**
	 * @param securityKeys
	 *            the securityKeys to set
	 */
	public void setSecurityKeys(HashMap<String, SecurityKey> securityKeys) {
		this.securityKeys = securityKeys;
	}

	/**
	 * @return the virtualMachines
	 */
	public HashMap<String, VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}

	/**
	 * @param virtualMachines
	 *            the virtualMachines to set
	 */
	public void setVirtualMachines(
			HashMap<String, VirtualMachine> virtualMachines) {
		this.virtualMachines = virtualMachines;
	}

}
