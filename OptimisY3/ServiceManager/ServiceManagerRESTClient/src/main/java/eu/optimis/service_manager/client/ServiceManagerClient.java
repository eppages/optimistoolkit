/* $Id: ServiceManagerClient.java 1074 2011-05-16 09:55:03Z rkuebert $ */

/*
 Copyright (c) 2011 University of Stuttgart
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.optimis.service_manager.client;

import java.net.URI;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.MissingResourceException;
//import java.util.ResourceBundle;
import java.util.UUID;
//import java.util.Date;
//import java.util.Calendar;
//import java.text.SimpleDateFormat;
//import java.text.ParseException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

//import org.apache.axis2.deployment.ServiceDeployer;
import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

//import org.w3.x2005.x08.addressing.EndpointReferenceType;

//import eu.optimis.mi.dbutil.GeneralDBUtil;
import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.ServiceDocument.Service;
import eu.optimis.serviceManager.VmDocument;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument;
//import eu.optimis.sd.SD;
//import eu.optimis.ds.client.DeploymentServiceClient;

public class ServiceManagerClient {

	WebResource serviceManager;
	String host;
	String port;
	String accepts = MediaType.APPLICATION_XML;
	String contentType = MediaType.APPLICATION_XML;

	// private static String TREC_DB_DRIVER, TREC_TABLE_URL, TREC_DB_USER,
	// TREC_DB_PASSWORD;

	/**
	 * A client for the service manager for the given <code>host</code> and
	 * <code>port</code>.
	 * <p/>
	 * Default content type is MediaType.TEXT_XML.
	 * 
	 * @param host
	 *            the host where the ServiceManager service is running
	 * 
	 * @param port
	 *            the port where the ServiceManager service is running
	 */
	public ServiceManagerClient(String host, String port) {
		this.host = host;
		this.port = port;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		serviceManager = client.resource(getBaseURI("http://" + host + ":"
				+ port + "/ServiceManager/services"));

		// try {
		// ResourceBundle rb = ResourceBundle.getBundle("restclient");
		// TREC_TABLE_URL = rb.getString("sp.trec.db.url");
		// TREC_DB_USER = rb.getString("sp.trec.db.username");
		// TREC_DB_PASSWORD = rb.getString("sp.trec.db.password");
		// TREC_DB_DRIVER = rb.getString("sp.trec.db.driver");
		//
		// } catch (MissingResourceException e) {
		// System.err.println("Error: cannot find the resource bundle path.");
		// throw new RuntimeException(e);
		// }
	}

	/**
	 * Creates a service in the service manager with the given id and status.
	 * 
	 * @param serviceId
	 *            the service's id
	 * 
	 * @param status
	 *            the service's status
	 * 
	 * @return the ServiceManager's state after the service creation
	 */
	public boolean addService(String serviceId, String status) {
		ServiceDocument serviceDocument = ServiceDocument.Factory.newInstance();
		Service service = serviceDocument.addNewService();
		service.setServiceId(serviceId);
		service.setStatus(status);

		String resp = serviceManager.accept(accepts).type(contentType)
				.post(String.class, serviceDocument.xmlText());

		try {
			ServiceDocument doc = ServiceDocument.Factory.parse(resp);
			Service s = doc.getService();
			return s.getServiceId().equals(service.getServiceId())
					&& s.getStatus().equals(service.getStatus());
		} catch (XmlException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
	}

	/**
	 * Creates a service in the service manager with the given id and status
	 * <code>pending</code>.
	 * 
	 * @param serviceId
	 *            the service's id
	 * 
	 * 
	 * @return the ServiceManager's state after the service creation
	 */
	public boolean addService(String serviceId) {
		return addService(serviceId, "pending");
	}

	/**
	 * Creates a service in the service manager with the given id and status.
	 * 
	 * @param serviceId
	 *            the service's id
	 * 
	 * @param status
	 *            the service's status
	 * 
	 * @param manifestId
	 *            the service's manifestId
	 * 
	 * @param objective
	 *            the service's objective
	 * 
	 * @return the ServiceManager's state after the service creation
	 */
	public boolean addService(String serviceId, String status,
			String manifestId, String objective) {
		ServiceDocument serviceDocument = ServiceDocument.Factory.newInstance();
		Service service = serviceDocument.addNewService();
		service.setServiceId(serviceId);
		service.setStatus(status);
		service.setManifestId(manifestId);
		service.setObjective(objective);

		String resp = serviceManager.accept(accepts).type(contentType)
				.post(String.class, serviceDocument.xmlText());

		try {
			ServiceDocument doc = ServiceDocument.Factory.parse(resp);
			Service s = doc.getService();
			return s.getServiceId().equals(service.getServiceId())
					&& s.getStatus().equals(service.getStatus())
					&& s.getManifestId().equals(service.getManifestId())
					&& s.getObjective().equals(service.getObjective());
		} catch (XmlException e) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
	}

	/**
	 * Creates a service in the service manager from the service given as XML.
	 * 
	 * @param serviceXml
	 *            the service's XML description
	 * 
	 * 
	 * @return the ServiceManager's state after the service creation
	 */
	public String addServiceFromXml(String serviceXml) {
		return serviceManager.accept(accepts).type(contentType)
				.post(String.class, serviceXml);
	}

	/**
	 * Adds the IP with the given id and internet protocol address to the the
	 * service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider
	 * 
	 * @param ipAddress
	 *            the IP address of the infrastructure provider
	 * 
	 * @param agreementEndpoint
	 *            the endpoint of the agreement stored in the SLA repository
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String addInfrastructureProvider(String serviceId,
			String infrastructureProviderId, String ipAddress, String slaId,
			String agreementEndpoint, float initialTrustValue,
			float initialRiskValue, float initialEcoValue,
			float initialCostValue) {

		InfrastructureProviderDocument infrastructureProviderDocument = InfrastructureProviderDocument.Factory
				.newInstance();
		InfrastructureProvider infrastructureProvider = infrastructureProviderDocument
				.addNewInfrastructureProvider();
		infrastructureProvider.setId(infrastructureProviderId);
		infrastructureProvider.setIpAddress(ipAddress);
		infrastructureProvider.setSlaId(slaId);
		infrastructureProvider.setAgreementEndpoint(agreementEndpoint);
		infrastructureProvider.setInitialTrustValue(initialTrustValue);
		infrastructureProvider.setInitialRiskValue(initialRiskValue);
		infrastructureProvider.setInitialEcoValue(initialEcoValue);
		infrastructureProvider.setInitialCostValue(initialCostValue);

		return serviceManager.path(serviceId).path("ip")
				.post(String.class, infrastructureProviderDocument.xmlText());
	}

	/**
	 * Adds the VM with the given <code>vmId</code> into the service with the id
	 * <code>serviceId</code>.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider which contains the VM
	 * 
	 * @param vmId
	 *            the id of VM to add to the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String addVm(String serviceId, String infrastructureProviderId,
			String vmId) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms")
				.post(String.class, createVm(vmId).xmlText());
	}

	/**
	 * Adds the VM with the given <code>vmId</code>, <code>status</code> and
	 * <code>type</code>into the service with the id <code>serviceId</code>.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider which contains the VM
	 * 
	 * @param vmId
	 *            the id of VM to add to the service
	 * 
	 * @param type
	 *            the type of VM to add to the service
	 * 
	 * @param status
	 *            the status of VM to add to the service
	 * 
	 * @param deploymentDurationInMs
	 *            time it took to deploy the VM
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String addVm(String serviceId, String infrastructureProviderId,
			String vmId, String type, String status, int deploymentDurationInMs) {
		return serviceManager
				.path(serviceId)
				.path("ip")
				.path(infrastructureProviderId)
				.path("vms")
				.post(String.class,
						createVm(vmId, type, status, deploymentDurationInMs)
								.xmlText());
	}

	private VmDocument createVm(String vmId) {
		return createVm(vmId, null, null, 0);
	}

	private VmDocument createVm(String vmId, String type, String status,
			int deploymentDurationInMs) {
		VmDocument vmDocument = VmDocument.Factory.newInstance();
		Vm vm = vmDocument.addNewVm();
		vm.setId(vmId);
		vm.setType(type);
		vm.setStatus(status);
		vm.setDeploymentDurationInMs(deploymentDurationInMs);
		return vmDocument;
	}

	/**
	 * Deletes the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service to delete
	 * 
	 */
	public void deleteService(String serviceId) {
		serviceManager.path(serviceId).delete();
	}

	/**
	 * Deletes the VM with the given <code>vmId</code> into the service with the
	 * id <code>serviceId</code>.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param infrastructureProviderId
	 *            the infrastructure provider from which to delete the VM
	 * 
	 * @param vmId
	 *            the id of VM to add to the service
	 */
	public void deleteVm(String serviceId, String infrastructureProviderId,
			String vmId) {
		serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").path(vmId).delete();
	}

	/**
	 * Builds a URI from the given string.
	 * 
	 * @param uri
	 *            the string to convert to a URI
	 * 
	 * @return the URI built from the given string
	 */
	private URI getBaseURI(String uri) {
		return UriBuilder.fromUri(uri).build();
	}

	/**
	 * Returns all infrastructure provider ids for the service with the given
	 * <code>serviceId</code>.
	 * 
	 * @param serviceId
	 * @return
	 */
	public String[] getInfrastructureProviderIds(String serviceId) {
		String infraProviderIds = serviceManager.path(serviceId).path("ip")
				.path("ids").get(String.class);
		String[] idsArray = infraProviderIds.split(";");
		return idsArray;
	}

	public String[] getSlaIds(String serviceId) {
		String infraProviderSlas = serviceManager.path(serviceId).path("ip")
				.path("slas").get(String.class);
		String[] slasArray = infraProviderSlas.split(";");
		return slasArray;
	}

	/**
	 * Returns all services.
	 * 
	 * @return all service currently in the service image manager
	 */
	public String getServices() {
		return serviceManager.accept(accepts).type(contentType)
				.get(String.class);
	}

	/**
	 * Obtains a list of VMs for the service with the given
	 * <code>serviceId</code> and returns them as an XML string.
	 * 
	 * @param serviceId
	 *            the id of the service whose VM list to get
	 * 
	 * @param infrastructureProviderId
	 *            the infrastructure provider whose VM list to get
	 * 
	 * @return an XML String containing the ids of the service's VMs
	 */
	public String getVms(String serviceId, String infrastructureProviderId) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").get(String.class);
	}

	/**
	 * Obtains the status of a a VM with given <code>vmId</code> from the
	 * service with the given <code>serviceId</code>.
	 * 
	 * @param serviceId
	 *            the id of the service which contains the VM
	 * 
	 * @param infrastructureProviderId
	 *            the infrastructure provider which contains the VM
	 * 
	 * @param vmId
	 *            the id of the VM whose status to return
	 * 
	 * @return the status of the requested VM
	 */
	public String getVmStatus(String serviceId,
			String infrastructureProviderId, String vmId) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").path(vmId)
				.path("status").get(String.class);
	}

	/**
	 * Obtains a list of VM ids for the service with the given
	 * <code>serviceId</code> and returns them as a String array.
	 * 
	 * @param serviceId
	 *            the id of the service whose VM ids list to get
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider whose VM ids list to get
	 * 
	 * @return a String array containing the ids of the service's VMs
	 */
	public String[] getVmIdsAsArray(String serviceId,
			String infrastructureProviderId) {
		// Strangely, the following does not work.
		// XMLBeans advocates to do it like this, however, see
		// http://xmlbeans.apache.org/documentation/tutorial_getstarted.html

		String xml = serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").get(String.class);
		xml = xml.replace("<vms>",
				"<vms xmlns=\"http://www.optimis.eu/service-manager\">");
		// System.out.println("XML: " + xml);
		try {
			VmsDocument vmsDocument = VmsDocument.Factory.parse(xml);
			eu.optimis.serviceManager.VmsDocument.Vms vms = vmsDocument
					.getVms();
			Vm[] vmArray = vms.getVmArray();
			// System.out.println("Vms list has " + vmArray.length +
			// " elements");

			String[] vmIds = new String[vmArray.length];
			for (int i = 0; i < vmArray.length; i++) {
				vmIds[i] = new String(vmArray[i].getId());
				// System.out.println("VM " + i + ":");
				// System.out.println(vmArray[i].getId());
			}

			return vmIds;
		} catch (XmlException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Returns the SLA id of the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the SLA id
	 */

	public String getSlaId(String serviceId, String ipId) {
		return serviceManager.path(serviceId).path(ipId).path("sla")
				.get(String.class);
	}

	/**
	 * Returns the initial trust value of the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the initial trust value
	 */
	public float getInitialTrustValue(String serviceId, String ipId) {
		String returnedVal = serviceManager.path(serviceId).path(ipId)
				.path("initialtrustvalue").get(String.class);
		return Float.valueOf(returnedVal).floatValue();
	}

	/**
	 * Returns the initial risk value of the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the initial risk value
	 */

	public float getInitialRiskValue(String serviceId, String ipId) {
		String returnedVal = serviceManager.path(serviceId).path(ipId)
				.path("initialriskvalue").get(String.class);
		return Float.valueOf(returnedVal).floatValue();
	}

	/**
	 * Returns the initial eco value of the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the initial eco value
	 */

	public float getInitialEcoValue(String serviceId, String ipId) {
		String returnedVal = serviceManager.path(serviceId).path(ipId)
				.path("initialecovalue").get(String.class);
		return Float.valueOf(returnedVal).floatValue();
	}

	/**
	 * Returns the initial cost value of the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the initial cost value
	 */

	public float getInitialCostValue(String serviceId, String ipId) {
		String returnedVal = serviceManager.path(serviceId).path(ipId)
				.path("initialcostvalue").get(String.class);
		return Float.valueOf(returnedVal).floatValue();
	}

	/**
	 * Returns the agreement endpoint of the service with the given id as a
	 * String.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @return the agreement endpoint
	 */
	public String getAgreementEndpoint(String serviceId, String ipId) {
		return serviceManager.path(serviceId).path(ipId)
				.path("agreementendpoint").get(String.class);
	}

	/**
	 * Updates the service with the given id's status to <code>status</code>.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param status
	 *            the new status of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateServiceStatus(String serviceId, String status) {
		String serviceXml = serviceManager.path(serviceId).path("status")
				.put(String.class, status);
		ServiceDocument service = null;
		try {
			service = ServiceDocument.Factory.parse(serviceXml);
		} catch (XmlException xmlException) {
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		return service.getService().getStatus();
	}

	/**
	 * Adds a manifestId to the service with the given id.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param manifest_id
	 *            the new manifest_id of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String addManifestId(String serviceId, String manifestId) {
		return updateManifestId(serviceId, manifestId);
	}

	/**
	 * Gets the manifest id of a service.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @return the manifest id
	 */
	public String getManifestId(String serviceId) {
		return serviceManager.path(serviceId).path("manifest_id")
				.get(String.class);
	}

	/**
	 * Updates the service with the given id's manifestId to
	 * <code>manifest_id</code>.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param manifest_id
	 *            the new manifest_id of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateManifestId(String serviceId, String manifestId) {
		String serviceXml = serviceManager.path(serviceId).path("manifest_id")
				.put(String.class, manifestId);
		ServiceDocument service = null;
		try {
			service = ServiceDocument.Factory.parse(serviceXml);
		} catch (XmlException xmlException) {
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		return service.getService().getManifestId();
	}

	/**
	 * Adds a objective to the service with the given id.
	 * 
	 * @author django
	 * 
	 * @param objective
	 *            the id of the service to update
	 * 
	 * @param manifest_id
	 *            the new manifest_id of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String addObjective(String serviceId, String objective) {
		return updateObjective(serviceId, objective);
	}

	/**
	 * Gets the objective of a service.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @return the objective
	 */
	public String getObjective(String serviceId) {
		return serviceManager.path(serviceId).path("objective")
				.get(String.class);
	}

	/**
	 * Updates the objective of a service with the given id.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param objective
	 *            the new objective of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateObjective(String serviceId, String objective) {
		String serviceXml = serviceManager.path(serviceId).path("objective")
				.put(String.class, objective);
		ServiceDocument service = null;
		try {
			service = ServiceDocument.Factory.parse(serviceXml);
		} catch (XmlException xmlException) {
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		return service.getService().getObjective();
	}

	/**
	 * Updates the status for the VM given by <code>vmId</code> of the service
	 * given by <code>serviceId</code> to <code>status</code>.
	 * 
	 * @param serviceId
	 *            the id of the service which contains the VM
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provide which contains the VM
	 * 
	 * @param vmId
	 *            the id of the VM to update
	 * 
	 * @param status
	 *            the new status
	 * 
	 * @return the updated services
	 */
	public String updateVmStatus(String serviceId,
			String infrastructureProviderId, String vmId, String status) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").path(vmId)
				.path("status").put(String.class, status);
	}

	/**
	 * Update the deployment duration in ms of a selected VM
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider that runs the VM
	 * 
	 * @param vmId
	 *            the id of VM
	 * 
	 * @param deploymentDurationInMs
	 *            the deployment duration time in ms to update in the VM
	 * 
	 *            return the new state of the VM
	 */
	public String updateDeploymentDurationInMs(String serviceId,
			String infrastructureProviderId, String vmId,
			int deploymentDurationInMs) {
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("vms").path(vmId)
				.path("deployment_duration_in_ms")
				.put(String.class, Integer.toString(deploymentDurationInMs));
	}

	/**
	 * Updates the service with the given id's infrastructure provider IP
	 * address.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param infrastructureProviderId
	 *            the id of the infrastructure provider to update
	 * 
	 * @param ipAddress
	 *            the new IP address of the infrastructureProviderId of the
	 *            service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateInfrastructureProviderIpAddress(String serviceId,
			String infrastructureProviderId, String ipAddress) {
		System.out.println(serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("ipaddress"));
		return serviceManager.path(serviceId).path("ip")
				.path(infrastructureProviderId).path("ipaddress")
				.put(String.class, ipAddress);
	}

	/**
	 * Updates the SLA id of the service with the given id to <code>slaid</code>
	 * .
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @param slaid
	 *            the new SLA identifier of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateSlaId(String serviceId, String ipId, String slaid) {
		return serviceManager.path(serviceId).path(ipId).path("slaid")
				.put(String.class, slaid);
	}

	/**
	 * Updates the agreement endpoint of the service with the given id to
	 * <code>agreementEndpoint</code>.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * 
	 * @param ipId
	 *            the id of the infrastructure provider
	 * 
	 * @param agreementEndpoint
	 *            the new agreement endpoint of the service
	 * 
	 * @return the new state of the ServiceManager
	 */
	public String updateAgreementEndpoint(String serviceId, String ipId,
			String agreementEndpoint) {
		return serviceManager.path(serviceId).path(ipId)
				.path("agreementendpoint").put(String.class, agreementEndpoint);
	}

	/**
	 * Deploys a service.
	 * 
	 * @author django
	 * 
	 * @param objective
	 *            Objective
	 * 
	 * @param manifestString
	 *            Manifest
	 * 
	 * @return true if deploy is successful
	 */
	public boolean deploy(String objective, String manifestString)
			throws Exception {
		try {
			String retval = serviceManager.path("deploy").path("objective")
					.path(objective).post(String.class, manifestString);
			if (retval.equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Deploy of service failed.");
		}
	}

	/**
	 * Undeploys a service.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to undeploy
	 * 
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 * 
	 * @return true if undeploy is successful
	 */
	public boolean undeploy(String serviceId, boolean keepData)
			throws Exception {
		try {
			String retval = serviceManager.path(serviceId).path("undeploy")
					.post(String.class, Boolean.toString(keepData));
			if (retval.equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Undeploy of service failed.");
		}
	}

	/**
	 * Redeploys a service.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to redeploy
	 * 
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 * 
	 * @return true if redeploy is successful
	 */
	public boolean redeploy(String serviceId, boolean keepData)
			throws Exception {

		try {
			String retval = serviceManager.path(serviceId).path("redeploy")
					.post(String.class, Boolean.toString(keepData));
			if (retval.equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Redeploy of service failed.");
		}

	}

	/**
	 * Redeploys a service after a riskLevel threshold is reached for any IP in
	 * use by the service, will block until that point (might not be a good idea
	 * due to timeouts?)
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to redeploy
	 * 
	 * @param riskLevelThresholdToRedployOn
	 *            the riskLevelThresholdToRedployOn to use when triggering a redeploy
	 * 
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 * 
	 * @return true if redeploy is successful
	 */
	public boolean redeployOnRiskThreshold(String serviceId,
			Integer riskLevelThresholdToRedployOn, boolean keepData)
			throws Exception {

		try {
			String retval = serviceManager.path(serviceId)
					.path("redeployOnRiskThreshold")
					.path(riskLevelThresholdToRedployOn.toString())
					.post(String.class, Boolean.toString(keepData));
			if (retval.equals("OK")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Redeploy of service using trigger threshold failed.");
		}
	}

	public static void main(String[] args) {

		String serviceId = "12345678910";
		String infraProvider1Id = "b15f2e08-2b86-42d6-97a0-673c90d737e3";

		String NO_VM_SERVICE_XML = "<service xmlns=\"http://www.optimis.eu/service-manager\">"
				+ "<service_id>"
				+ serviceId
				+ "</service_id>"
				+ "<status>pending</status>"
				+ "<infrastructure-provider>"
				+ "<id>"
				+ infraProvider1Id
				+ "</id>"
				+ "<sla_id>3c840218-9273-463b-bb96-69c15b95842c</sla_id>"
				+ "</infrastructure-provider>" + "</service>";

		System.out.println(NO_VM_SERVICE_XML);

		ServiceManagerClient smClient = new ServiceManagerClient(
				"optimis-spvm.atosorigin.es", "8080");
		// ServiceManagerClient smClient = new ServiceManagerClient("localhost",
		// "8080");

		// System.out.println(smClient.getServices());

		// Delete service if it exists
		try {
			smClient.deleteService(serviceId);
		} catch (Exception e) {
		}

		// Add service and two VMs
		System.out.println(smClient.addServiceFromXml(NO_VM_SERVICE_XML));
		System.out.println("------------------------------");
		System.out.println("Adding VM");
		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9999"));

		System.out.println("Adding infrastructure provider");
		System.out.println(smClient.addInfrastructureProvider(serviceId,
				"randomInfraId", "192.168.10.10", "slaId1",
				"agreementendpoint1", 1.1f, 2.2f, 3.3f, 4.4f));
		System.out.println("------------------------------");
		System.out.println("Adding another infrastructure provider");
		System.out.println(smClient.addInfrastructureProvider(serviceId,
				"randomInfraId2", "192.168.10.11", "slaId2",
				"agreementendpoint2", 1.1f, 2.2f, 3.3f, 4.4f));
		System.out.println("------------------------------");
		System.out.println("Getting infrastructure provider ids:");
		String[] infrastructureProviderIds = smClient
				.getInfrastructureProviderIds(serviceId);
		for (int i = 0; i < infrastructureProviderIds.length; i++) {
			System.out.println("\t" + infrastructureProviderIds[i]);
		}
		// System.out.println(smClient.getInfrastructureProviderIds(serviceId));
		System.out.println("------------------------------");
		System.out.println("Adding VM");
		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9998"));
		System.out.println("------------------------------");
		System.out.println("Updating status for VM 9998");
		System.out.println(smClient.updateVmStatus(serviceId, infraProvider1Id,
				"9998", "running-as-fast-as-sonic"));
		System.out.println("------------------------------");
		System.out.println("Getting status for VM 9998");
		System.out.println(smClient.getVmStatus(serviceId, infraProvider1Id,
				"9998"));
		System.out.println("------------------------------");
		System.out.println("Adding VM with status and type");

		System.out.println(smClient.addVm(serviceId, infraProvider1Id, "9997",
				"CE", "paused", 1000));
		System.out.println("------------------------------");
		System.out.println("Updating VM status to running");
		System.out.println(smClient.updateVmStatus(serviceId, infraProvider1Id,
				"9997", "running"));
		System.out.println("------------------------------");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		System.out.println("Listing VMs");
		System.out.println(smClient.getVms(serviceId, infraProvider1Id));
		System.out.println("------------------------------");

		System.out.println("------------------------------");
		System.out.println("Listing VMs as arary");
		String[] vmIdsAsArray = smClient.getVmIdsAsArray(serviceId,
				infraProvider1Id);
		for (String string : vmIdsAsArray) {
			System.out.println("VM id: " + string);
		}
		System.out.println("------------------------------");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");

		// Delete VMs, then service
		smClient.deleteVm(serviceId, infraProvider1Id, "9999");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		smClient.deleteVm(serviceId, infraProvider1Id, "9998");
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");
		System.out.println("Setting service to 'running'");
		System.out.println("New status: "
				+ smClient.updateServiceStatus(serviceId, "running"));
		System.out.println("------------------------------");
		String newSlaId = UUID.randomUUID().toString();
		System.out.println("Updating SLA id to '" + newSlaId + "'");
		smClient.updateSlaId(serviceId, infraProvider1Id, newSlaId);
		System.out.println("------------------------------");

		System.out.println("------------------------------");
		System.out.println("get sla of  infra provider");
		System.out.println("slaId: "
				+ smClient.getSlaId(serviceId, infraProvider1Id));

		String newIpAddress = "2.2.2.2";
		System.out.println("Updating infra provider to new IP address '"
				+ newIpAddress + "'");
		System.out.println("Infra provider id: " + infraProvider1Id);
		System.out.println(smClient.updateInfrastructureProviderIpAddress(
				serviceId, infraProvider1Id, newIpAddress));

		System.out.println("------------------------------");
		smClient.deleteService(serviceId);
		System.out.println(smClient.getServices());
		System.out.println("------------------------------");

		// java.sql.Connection conn =
		// GeneralDBUtil.getConnection(TREC_TABLE_URL,
		// TREC_DB_USER, TREC_DB_PASSWORD, TREC_DB_DRIVER);
		// try {
		// Statement st = conn.createStatement();
		// ResultSet rs = st
		// .executeQuery("select * from manifest_raw where service_id='"
		// + "'");
		// Boolean bo = false;
		// while (rs.next()) {
		// Boolean bo0 = rs.getBoolean("is_broker");
		// System.out.println(bo0);
		// System.out.println(rs.getString("broker_host"));
		// System.out.println(rs.getInt("broker_port"));
		// }
		// } catch (SQLException e) {
		// System.err.println("SQLException:" + e.getMessage() + ":"
		// + e.getSQLState());
		// } finally {
		// try {
		// conn.close();
		// } catch (Exception e) {
		// }
		// }
	}
}