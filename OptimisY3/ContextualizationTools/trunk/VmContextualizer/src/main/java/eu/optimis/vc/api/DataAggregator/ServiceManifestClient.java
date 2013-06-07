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
package eu.optimis.vc.api.DataAggregator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.sp.ComponentProperty;
import eu.optimis.manifest.api.sp.ServiceEndpoint;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.Dependency;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;
import eu.optimis.vc.api.DataModel.ContextData;
import eu.optimis.vc.api.DataModel.VirtualMachine;
import eu.optimis.vc.api.DataModel.ContextDataTypes.EndPoint;
import eu.optimis.vc.api.DataModel.ContextDataTypes.LicenseToken;
import eu.optimis.vc.api.DataModel.ContextDataTypes.SecurityKey;
import eu.optimis.vc.api.DataModel.ContextDataTypes.SoftwareDependency;
import eu.optimis.vc.api.DataModel.Image.HardDisk;
import eu.optimis.vc.api.DataModel.Image.Iso;

/**
 * Class for parsing the contextualization data from the Service Manifest
 * 
 * @author Django Armstrong (ULeeds)
 * @version 0.0.5
 */
public class ServiceManifestClient {

	protected static final Logger LOGGER = Logger
			.getLogger(ServiceManifestClient.class);

	private Manifest manifest;

	/**
	 * Constructor for operating on a specific service manifest (see
	 * {@link Manifest})
	 * 
	 * @param manifest
	 *            The manifest to use.
	 */
	public ServiceManifestClient(Manifest manifest) {
		this.manifest = manifest;
	}

	/**
	 * Parse the manifest to a usable object
	 * 
	 * TODO: Split this into discrete methods on a per context data type basis?
	 * 
	 * @return contextData the ContextData parsed from the manifest
	 */
	public ContextData parse() {
		ContextData contextData = new ContextData();

		// Parse VM description here...
		LOGGER.debug("Parsing VM Description from Service Manifest...");
		int virtualMachineComponents = manifest
				.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentArray().length;
		LOGGER.debug("Number of virtualMachineComponents is: "
				+ virtualMachineComponents);
		VirtualMachineComponent[] virtualMachineComponentArray = new VirtualMachineComponent[virtualMachineComponents];
		virtualMachineComponentArray = manifest
				.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentArray();
		// Iterate over all VM components getting details of disk images...
		for (int i = 0; i < virtualMachineComponentArray.length; i++) {
			// Parse the number of instances per VM component here...
			String componentId = virtualMachineComponentArray[i]
					.getComponentId();
			LOGGER.debug("Processing virtualMachineComponent with component ID: "
					+ componentId);
			int upperBound = virtualMachineComponentArray[i]
					.getAllocationConstraints().getUpperBound();
			LOGGER.debug("Allocation constraint upper bound is: " + upperBound);

			// Parse the disk details i.e. URI
			String diskId = virtualMachineComponentArray[i].getOVFDefinition()
					.getDiskSection().getImageDisk().getDiskId();
			LOGGER.debug("Found OVF disk with ID: " + diskId);
			String diskCapacity = virtualMachineComponentArray[i]
					.getOVFDefinition().getDiskSection().getImageDisk()
					.getCapacity();
			LOGGER.debug("OVF disk capacity is: " + diskCapacity);
			String diskFormatString = virtualMachineComponentArray[i]
					.getOVFDefinition().getDiskSection().getImageDisk()
					.getFormat();
			LOGGER.debug("OVF disk format string is: " + diskFormatString);
			String uri = virtualMachineComponentArray[i].getOVFDefinition()
					.getReferences().getImageFile().getHref();
			LOGGER.debug("OVF URI for disk is: " + uri);
			String fileName = new File(uri).getName();

			// Add data to appropriate object(s) in data model.
			VirtualMachine virtualMachine = new VirtualMachine(componentId,
					upperBound);
			LOGGER.debug("Created new VirtualMachine");
			// TODO: add diskFormat...
			HardDisk hardDisk = new HardDisk(diskId, fileName, uri, null,
					diskCapacity, diskFormatString);

			LOGGER.debug("Created new HardDisk");
			virtualMachine.getHardDisks().put(diskId, hardDisk);
			LOGGER.debug("Added HardDisk to VirtualMachine");
			contextData.getVirtualMachines().put(componentId, virtualMachine);
			LOGGER.debug("Added VM to contextData with ID: " + componentId);

			// Get service end points for this virtual machine component
			ServiceEndpoint[] serviceEndpoints = virtualMachineComponentArray[i]
					.getServiceEndpoints();

			for (int j = 0; j < serviceEndpoints.length; j++) {
				String endPointName = serviceEndpoints[j].getName();
				String endPointUri = serviceEndpoints[j].getURI();

				EndPoint endPoint = new EndPoint(endPointName, endPointUri);
				virtualMachine.getEndPoints().put(endPointName, endPoint);
			}

		}

		// Parse the SP extension configuration details here....
		LOGGER.debug("Parsing SP extension configuration from Service Manifest...");
		int componentConfigurations = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfigurationArray().length;
		LOGGER.debug("Number of componentConfigurations is: "
				+ virtualMachineComponents);
		// Test we have the same number of components in both sections otherwise
		// error out?
		if (componentConfigurations != virtualMachineComponents) {
			LOGGER.warn("Number of componentConfigurations does not match number of virtualMachineComponents");
		}

		VirtualMachineComponentConfiguration[] virtualMachineComponentConfigurationArray = new VirtualMachineComponentConfiguration[componentConfigurations];
		virtualMachineComponentConfigurationArray = manifest
				.getServiceProviderExtensionSection()
				.getVirtualMachineComponentConfigurationArray();
		// Iterate over all component configurations...
		for (int i = 0; i < virtualMachineComponentConfigurationArray.length; i++) {

			// Parse values for given component configuration
			String componentId = virtualMachineComponentConfigurationArray[i]
					.getComponentId();
			LOGGER.debug("Processing virtualMachineComponentConfiguration with component ID: "
					+ componentId);

			VirtualMachine virtualMachine = contextData.getVirtualMachines()
					.get(componentId);
			LOGGER.debug("Fetching virtualMachine with component ID from contextData: "
					+ componentId);

			// Additional properties added to SP extension useful for last
			// minute addition to the manifest.
			try {
				ComponentProperty[] componentProperties = virtualMachineComponentConfigurationArray[i]
						.getComponentProperties();
				LOGGER.debug("Number of componentProperties found: "
						+ componentProperties.length);
				for (int j = 0; j < componentProperties.length; j++) {
					String name = componentProperties[j].getName();
					String value = componentProperties[j].getValue();
					LOGGER.debug("Found componentProperty with Name: " + name
							+ " and Value: " + value);
					// TODO: search for any appropriate properties we might
					// need? Like:
					// * Operating System
					// * Current disk format
					// * Desired disk format
				}
			} catch (Exception e) {
				LOGGER.warn("No additional component properties found! ComponentProperties was null!");
			}

			// Security keys

			// Hard coded the SSH key until security API is able to generate it
			virtualMachineComponentConfigurationArray[i].enableSSHSecurity();
			// FIXME: add your key here...
			String tempKey = new String("");
			virtualMachineComponentConfigurationArray[i].setSSHKey(tempKey
					.getBytes());
			LOGGER.warn("Enabled SSH and using harded coded Key Pair!");

			// SSH Key
			if (virtualMachineComponentConfigurationArray[i]
					.isSecuritySSHbased()) {
				virtualMachine.setHasSSHKey(true);
				// We have an SSH keys
				LOGGER.debug("Set virtualMachine hasSSHKey to: " + true);
				byte[] sshKey = virtualMachineComponentConfigurationArray[i]
						.getSSHKey();
				LOGGER.debug("SSH key pair is: " + new String(sshKey));

				// Create SecurityKey object and add security key to contextData
				SecurityKey securityKey = new SecurityKey("SSH", sshKey);
				contextData.getSecurityKeys().put("SSH", securityKey);
			} else {
				LOGGER.debug("Set virtualMachine hasSSHKey to: " + false);
			}
			
			// VPN Key
			if (virtualMachineComponentConfigurationArray[i]
					.isSecurityVPNbased()) {
				// TODO: We have to generate VPN keys
				virtualMachine.setHasVPNKey(true);
				LOGGER.debug("Set virtualMachine hasVPNKey to: " + true);
			} else {
				LOGGER.debug("Set virtualMachine hasVPNKey to: " + false);
			}
			
			// BT Key
			if(virtualMachineComponentConfigurationArray[i].isEncryptedSpaceEnabled()) {
				// We have an BT key
				virtualMachine.setHasBTKey(true);
				LOGGER.debug("Set virtualMachine hasBTKey to: " + true);
				byte[] btKey = virtualMachineComponentConfigurationArray[i].getEncryptedSpace().getEncryptionKey();
				LOGGER.debug("BT key is: " + new String(btKey));
				SecurityKey securityKey = new SecurityKey("BT", btKey);
				contextData.getSecurityKeys().put("BT", securityKey);
			} else {
				LOGGER.debug("Set virtualMachine hasBTKey to: " + false);
			}
			
			// IPS Support
			if(virtualMachineComponentConfigurationArray[i].isIPSEnabled()) {
				// We support IPS
				virtualMachine.setHasIPS(true);
				LOGGER.debug("Set virtualMachine hasIPS to: " + true);
			} else {
				LOGGER.debug("Set virtualMachine hasIPS to: " + false);
			}
			
			// DM Key
			if(manifest.getServiceProviderExtensionSection().isSetDataManagerKey()) {
				// We have an DM key
				virtualMachine.setHasDMKey(true);
				LOGGER.debug("Set virtualMachine hasDMKey to: " + true);
				byte[] dmKey = manifest.getServiceProviderExtensionSection().getDataManagerKey();
				LOGGER.debug("DM key is: " + new String(dmKey));
				SecurityKey securityKey = new SecurityKey("DM", dmKey);
				contextData.getSecurityKeys().put("DM", securityKey);
			} else {
				LOGGER.debug("Set virtualMachine hasDMKey to: " + false);
			}

			// License tokens
			byte[][] licenseTokens = virtualMachineComponentConfigurationArray[i]
					.getTokenArray();

			for (int j = 0; j < licenseTokens.length; j++) {
				LicenseToken licenseToken = new LicenseToken(j,
						licenseTokens[j]);
				LOGGER.debug("Created LicenseToken object using data:\n"
						+ new String(licenseTokens[j]));
				String licenseTokenId = "" + (j + 1);
				virtualMachine.getLicenseTokens().put(licenseTokenId,
						licenseToken);
				LOGGER.debug("Added licenseToken to virtualMachine with licenseTokenId: "
						+ licenseTokenId);
			}

			// Software dependencies
			LOGGER.debug("Parsing dependecies...");
			Dependency[] dependecies = virtualMachineComponentConfigurationArray[i]
					.getSoftwareDependencies();
			LOGGER.debug("Number of dependecies found: " + dependecies.length);
			for (int j = 0; j < dependecies.length; j++) {
				String artifactId = dependecies[j].getArtifactId();
				LOGGER.debug("Dependecy artifactId is: " + artifactId);
				String groupId = dependecies[j].getGroupId();
				LOGGER.debug("Dependecy getGroupId is: " + groupId);
				String version = dependecies[j].getVersion();
				LOGGER.debug("Dependecy version is: " + version);
				// TODO: Confirm that these are all the values we need?
				SoftwareDependency softwareDependency = new SoftwareDependency(
						artifactId, groupId, version);
				LOGGER.debug("Created new SoftwareDependency with ID: "
						+ Integer.toString(j));
				virtualMachine.getSoftwareDependencies().put(
						Integer.toString(j), softwareDependency);
				LOGGER.debug("Added softwareDependency to virtualMachine");
			}
		}

		return contextData;
	}

	/**
	 * Adds the ISO name and URI to the SP manifest extension.
	 * 
	 * @param virtualMachines
	 *            The VM' for which we want to add the ISO for in the manifest.
	 * @return The altered manifest.
	 */
	public Manifest addIsosToManifest(
			Map<String, VirtualMachine> virtualMachines) {
		// Iterate over all the virtual machines for this service
		for (VirtualMachine virtualMachine : virtualMachines.values()) {
			String componentId = virtualMachine.getComponentId();
			LOGGER.info("Adding ISO to virtual machine with component ID: "
					+ componentId);

			HashMap<String, Iso> isoImages = (HashMap<String, Iso>) virtualMachine.getIsoImages();

			// Add the ISO base name to the OVF
			Iso iso = isoImages.get("1");
			String[] temp;
			String delimiter = "_";
			temp = iso.getFileName().split(delimiter);
			String baseName = temp[0] + "_" + temp[1] + ".iso";
			temp = iso.getUri().split(iso.getFileName());
			String baseUri = temp[0];

			LOGGER.info("Adding ISO base name URI to OVF: " + baseUri + baseName);
			manifest.getVirtualMachineDescriptionSection()
					.getVirtualMachineComponentById(componentId)
					.getOVFDefinition().getReferences()
					.getContextualizationFile().setHref(baseUri + baseName);
		}
		return manifest;
	}

	/**
	 * Adds the HardDisk name and URI to the SP manifest extension.
	 * 
	 * @param virtualMachines
	 *            The VM' for which we want to add the HardDisks for in the
	 *            manifest.
	 * @return The altered manifest.
	 */
	public Manifest addHardDisksToManifest(
			Map<String, VirtualMachine> virtualMachines) {

		for (VirtualMachine virtualMachine : virtualMachines.values()) {
			String componentId = virtualMachine.getComponentId();
			LOGGER.info("Adding HardDisk href to virtual machine with component ID: "
					+ componentId);

			HashMap<String, HardDisk> hardDisks = (HashMap<String, HardDisk>) virtualMachine.getHardDisks();

			for (HardDisk hardDisk : hardDisks.values()) {
				// TODO: Manifest only supports a single image, this should
				// change...
				LOGGER.info("Adding new HardDisk URI to OVF: " + hardDisk.getUri());
				manifest.getVirtualMachineDescriptionSection()
						.getVirtualMachineComponentById(componentId)
						.getOVFDefinition().getReferences().getImageFile()
						.setHref(hardDisk.getUri());
			}
		}

		return manifest;
	}
}
