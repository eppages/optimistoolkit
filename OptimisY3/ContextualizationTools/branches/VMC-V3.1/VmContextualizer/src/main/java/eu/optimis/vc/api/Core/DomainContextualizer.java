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
package eu.optimis.vc.api.Core;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.optimis.vc.api.VmcApi;
import eu.optimis.vc.api.Core.Libvirt.LibvirtControlBridge;
import eu.optimis.vc.api.DataModel.GlobalConfiguration;
import eu.optimis.vc.api.IsoCreator.IsoImageCreation;
import eu.optimis.vc.libvirt.LibvirtException;
/**
 * Responds to events regarding the domain and manages the actual context changes
 * 
 * @author Django Armstrong (ULeeds), Daniel Espling (UMU)
 * @version 0.0.1
 */
public class DomainContextualizer {

	protected final static Logger log = Logger
			.getLogger(DomainContextualizer.class);

	private static final String CONTEXT_INTERFACE_NAME = "hdc";
	private static final String RECONTEXT_INTERFACE_NAME = "hdc";

	private String domainName;
	private VmcApi vmcApi;

	private LibvirtControlBridge libvirtControl;

	public DomainContextualizer(String domainName, VmcApi vmcApi, LibvirtControlBridge libvirtControl) {
		this.domainName = domainName;
		this.vmcApi = vmcApi;
		this.libvirtControl = libvirtControl;
	}

	public void vmDomainStarted() {
		log.info("Got call to domainStarted for domainName: " + domainName);
		
		//Detach any existing recontext
		try {
			libvirtControl.detachIso(domainName, RECONTEXT_INTERFACE_NAME);
			log.info("Detached existing recontext info on stop");
		} catch (LibvirtException e){
			log.info("Failed to detach existing recontext info");
		}
		
		//Create new recontext iso and attach it
		try {
			String isoPath = createIso(libvirtControl.getExistingIsoPath(domainName, CONTEXT_INTERFACE_NAME));
			libvirtControl.attachIso(domainName, isoPath, RECONTEXT_INTERFACE_NAME);
			log.info("Attached new recontext info");
		} catch (LibvirtException e) {
			log.warn("Failed to attach recontext info.", e);
		} catch (IOException e) {
			log.warn("Failed to read/extract existing ISO.", e);
		}
	}

	public void vmDomainStopped() {
		log.info("Got call to domainStopped for domainName: " + domainName);
		//Detach any existing recontext
		try {
			libvirtControl.detachIso(domainName, RECONTEXT_INTERFACE_NAME);
			log.info("Detached existing recontext info on stop");
		} catch (LibvirtException e){
			log.info("Failed to detach existing recontext info");
		}
	}

	public void vmDomainMigrationStarted() {
		log.info("Got call to domainMigrationStarted for domainName: " + domainName);

		// TODO Remove existing context info from the domain
	}

	public void vmDomainMigrationCompleted() {
		log.info("Got call to domainMigrationComplete for domainName: " + domainName);

		// TODO Construct context info and attach it to the domain
	}

	/**
	 * @returns true if the domainName sent as argument is the same as the domainName being managed by this DomainContextualizer
	 */
	public Boolean watchesDomainName(String domainName) {
		return this.domainName.equals(domainName);
	}

	public String getDomainName() {
		return this.domainName;
	}
	
	
	//TODO Add javadoc
	//Creates a recontext ISO and returns its path to be attached to later to a VM
	private String createIso(String existingIsoPath) throws IOException {
		
		GlobalConfiguration configuration = vmcApi.getGlobalState().getConfiguration();
		
		//Construct for fetching data from an existing ISO image
		IsoImageCreation isoImageCreation = new IsoImageCreation(configuration);
		
		//Creation of new ISO starts here
		String isoPath = isoImageCreation.recontext();

		return isoPath;
	}
}
