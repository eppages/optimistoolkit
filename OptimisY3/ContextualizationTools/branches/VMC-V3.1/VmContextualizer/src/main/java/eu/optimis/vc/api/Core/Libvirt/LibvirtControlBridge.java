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
package eu.optimis.vc.api.Core.Libvirt;

import org.apache.log4j.Logger;

import eu.optimis.vc.libvirt.Connect;
import eu.optimis.vc.libvirt.Domain;
import eu.optimis.vc.libvirt.LibvirtException;

/**
 * Control bridge wrapping the calls to libvirt and/or system processes
 * 
 * @author Django Armstrong (ULeeds), Daniel Espling (UMU)
 * @version 0.0.1
 */
public class LibvirtControlBridge implements LibvirtControlBridgeMBean {
	
	protected final static Logger log = Logger
			.getLogger(LibvirtControlBridge.class);

	private Connect connection;
	
	/**
	 * Create a new control bridge for a specific Libvirt connection
	 * @param connection The connection to use when sending commands
	 */
	public LibvirtControlBridge(Connect connection) {
		this.connection = connection;
		log.info("Created ControlBridge");
	}

	/**
	 * Attach an iso to a domain
	 * @param domainName The name of the Domain
	 * @param interfaceName The interface name to attach
	 * @throws LibvirtException Thrown if connecting or attach fails
	 */
	@Override
	public void attachIso(String domainName, String isoPath, String interfaceName) throws LibvirtException {
		Domain domain = connection.domainLookupByName(domainName);
		String isoXML = getIsoXML(isoPath, interfaceName, true);
		domain.attachDevice(isoXML);
	}
	
	/**
	 * Detach an iso from a domain
	 * @param domainName The name of the Domain
	 * @param interfaceName The interface name to detach
	 * @throws LibvirtException Thrown if connecting or detaching fails
	 */
	@Override
	public void detachIso(String domainName, String interfaceName) throws LibvirtException {
		Domain domain = connection.domainLookupByName(domainName);
		String isoXML = getIsoXML(null, interfaceName, false);
		domain.updateDeviceFlags(isoXML, 4); //4 should be VIR_DOMAIN_DEVICE_MODIFY_FORCE
	}
	
	/*
	 * Construct an XML device represenation for attaching/detaching
	 * @param isoPath The iso file path to attach (may be null when detaching)
	 * @param interfaceName The name of the interface where the ISO should be mounted
	 * @param attaching True when attaching, false when detaching
	 */
	private String getIsoXML(String isoPath, String interFaceName, boolean attaching) {
		StringBuilder xml = new StringBuilder(100);
		xml.append("<disk type='file' device='cdrom'>");
		xml.append("<target dev='" + interFaceName + "'/>");
		if (attaching) {
			xml.append("<source file='" + isoPath + "'/>");
		}
		xml.append("<readonly/>");
		xml.append("</disk>");
		
		log.info("Using XML: " + xml.toString());
		return xml.toString();
	}
	
	/**
	 * Get the existing iso file path of a device mounted at an interface
	 * @param domainName Domain to query
	 * @param interfaceName The name of the interface of interest 
	 */	
	@Override
	public String getExistingIsoPath(String domainName, String interfaceName) throws LibvirtException {
		Domain domain = connection.domainLookupByName(domainName);
		String xmlDesc = domain.getXMLDesc(0);
		log.debug("Got XML: " +xmlDesc);
		//TODO: Continue here
		return null;
	}
}
