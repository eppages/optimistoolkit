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
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import eu.optimis.vc.api.VmcApi;
import eu.optimis.vc.api.Core.Libvirt.LibvirtControlBridge;
import eu.optimis.vc.api.Core.Libvirt.LibvirtEventBridge;
import eu.optimis.vc.libvirt.Connect;
import eu.optimis.vc.libvirt.Domain;
import eu.optimis.vc.libvirt.LibvirtException;

/**
 * Core logic of the Recontextualizer.
 * 
 * @author Django Armstrong (ULeeds), Daniel Espling (UMU)
 * @version 0.0.1
 */
public class VirtualMachineRecontextualizer implements Runnable {

	protected final static Logger log = Logger
			.getLogger(VirtualMachineRecontextualizer.class);

	private VmcApi vmcApi;
	
	//Libvirt connection, domain and list of registered callbacks
	private Connect connection;
	private Domain vmDomain;

	private LibvirtEventBridge eventBridge;

	private LibvirtControlBridge controlBridge;
	
	//Create libvirt default event loop once
	static {
		try {
			Connect.initEventLoop();
		} catch (LibvirtException e) {
			log.error("Failed to initiate event loop", e);
		} catch (Error e) {
			//This probably means either we're doing a unit test or libvirt can't be found
			log.error("Failed to to initiate event loop", e); 
		}
	}

	/**
	 * Constructor that provides access to VMC state and config.
	 * @throws IOException If a connection to libvirt cannot be successfully established
	 * 
	 * @param vmcApi ?
	 * @param hypervisorUri The URL to the hypervisor (e.q.: qemu:///system)
	 * @throws IOException If a connection to Libvirt cannot be established
	 *  
	 */
	public VirtualMachineRecontextualizer(VmcApi vmcApi, String hypervisorUri) throws IOException {
		this.vmcApi = vmcApi;

		try {
			connection = new Connect(hypervisorUri);
			connection.setKeepAlive(3, 10);
		} catch (LibvirtException e) {
			log.info("Failed to connect Recontextualizer to Libvirt", e);
			throw new IOException(e);
		} catch (Error e) {
			log.info("Failed to connect Recontextualizer to Libvirt", e);
		}
		
		//Will throw LibvirtException if it fails
		try {
			this.eventBridge = new LibvirtEventBridge(connection);
		} catch (LibvirtException e) {
			log.info("Failed to initiate LibvirtEventBridge", e);
			throw new IOException(e);
		}
		
		this.controlBridge = new LibvirtControlBridge(connection);
		
		log.info("Exposing LibvirtControlBridge as an MBean");
		
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName("eu.optimis.vc.api.Core:type=LibvirtControlBridgeMBean");		
			mbs.registerMBean(this.controlBridge, name);
		} catch (MalformedObjectNameException e) {
			log.info("JMX failed: ", e);
		} catch (InstanceAlreadyExistsException e) {
			log.info("JMX failed: ", e);
		} catch (MBeanRegistrationException e) {
			log.info("JMX failed: ", e);
		} catch (NotCompliantMBeanException e) {
			log.info("JMX failed: ", e);
		}

		log.info("Finished initialising Recontextualizer...");
	}
	
	/**
	 * Activates recontextualization for a specific domain
	 * 
	 * @param domainName The domain name of the VM that should be recontextualized (as seen by the hypervisor)
	 * @throws IOException If a connection to the domain cannot be established
	 */
	public void startRecontextualization(String domainName) throws IOException {
		
		try {
			vmDomain = connection.domainLookupByName(domainName);
			log.info("Connected to Domain:" + vmDomain.getName() + " id "
					+ vmDomain.getID() + " running "
					+ vmDomain.getOSType());
			eventBridge.addListener(new DomainContextualizer(domainName, vmcApi, controlBridge));
		} catch (LibvirtException e) {
			log.info("Failed to connect to domain: " + domainName, e);
			throw new IOException(e);
		}
	}
	
	/**
	 * Deactivates recontextualization for a specific domain
	 * 
	 * @param domainName The domain name of the VM that no longer should be recontextualized (as seen by the hypervisor)
	 * @return True if the listener was found and removed, false if the listener could not be found
	 */
	public boolean stopRecontextualization(String domainName) throws IOException {
		return eventBridge.removeListener(domainName);
	}

	/**
	 * Starts the recontextualization process in a thread (useful for async API
	 * access)
	 * 
	 */
	public void run() {
		// TODO Improve what pertinent information is held on what the
		// Recontextualizer is doing in GlobalState
		vmcApi.getGlobalState().setRecontextRunning(true);
		while (true) {
			if (Thread.interrupted() == true) {
				log.info("I has interupt, bye bye!");
				break;
			}
			try {
				//Steps the Event loop
				if (connection.isAlive()) {
					connection.processEvent();
					log.info("Processed event");
				}
				
				//Polling could be done here if we can't get events working without them
				/*
				try {
					Connect connect = new Connect("locolhost");
					connect.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(e.getCause(), e);
				}
				*/
			} catch (LibvirtException e) {
				log.warn("Exception from libvirt: ", e);
				break;
			}
		}
		
		//Close the connection to libvirt when done
		close();
	}
	
	/**
	 * Unregister for events and close the connection to libvirt when done
	 */
	public synchronized void close() {
		if (connection != null) {
			//Unregister all events by closing the bridge
			eventBridge.close();
				
			//Close connection
			try {
				connection.close();
				connection = null;
			} catch (LibvirtException e) {
				log.warn("Failed to close LibVirt connection", e);
			}
		}
	}
	
	/*
	 * Make sure connection is really closed before GC-ing this object
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
		if (connection != null) {
			log.warn("LibVirt connection still open during finalize, please add a call to close()");
			try {
				close();
			} catch (RuntimeException e) {
				log.warn("Failed to close LibVirt connection", e);
			}
		}
	}
}
