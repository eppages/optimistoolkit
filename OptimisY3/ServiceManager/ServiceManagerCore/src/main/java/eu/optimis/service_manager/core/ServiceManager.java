/* $Id: ServiceManager.java 12413 2013-04-16 15:47:21Z django $ */

/*
 Copyright 2012 University of Stuttgart

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package eu.optimis.service_manager.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;

//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.VmDocument;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.serviceManager.VmsDocument;
import eu.optimis.serviceManager.VmsDocument.Vms;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.ds.client.DeploymentServiceClient;
import eu.optimis.manifestregistry.client.ManifestRegistryClient;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.serviceproviderriskassessmenttool.rest.client.*;

public class ServiceManager {

	private final static Logger LOGGER = Logger.getLogger(ServiceManager.class
			.getName());

	/** Singleton ServiceManager instance. */
	private static ServiceManager instance = null;
	/** Storage for all services. */
	private ArrayList<ServiceDocument> services;

	/**
	 * Creates a new ServiceManager and initialises the local variables.
	 */
	private ServiceManager() {
		services = new ArrayList<ServiceDocument>();
	}

	/**
	 * Deletes a service from the stored list of services.
	 * 
	 * @param serviceId
	 *            the id of the service to delete
	 */
	public void deleteService(String serviceId) throws ItemNotFoundException {
		for (int i = 0; i < services.size(); i++) {
			if (services.get(i).getService().getServiceId().equals(serviceId)) {
				services.remove(i);
				return;
			}
		}
		throw new ItemNotFoundException("Service with id '" + serviceId
				+ "' does not exist");
	}

	public String deleteVm(String serviceId, String infrastructureProviderId,
			String vmId) throws ItemNotFoundException {

		LOGGER.debug("ServiceManagerCore: Deleting VM " + vmId + " from IP "
				+ infrastructureProviderId + " (service " + serviceId + ")");
		ServiceDocument service = getService(serviceId);

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			LOGGER.debug("ServiceManagerCore: Service does not contain IP with required id");
			throw new ItemNotFoundException("Service '" + serviceId
					+ " does not have an Infrastructure Provider with id "
					+ infrastructureProviderId);
		} else {
			LOGGER.debug("ServiceManagerCore: IP found");
			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);
			Vms vms;
			if (infrastructureProvider.getVms() == null) {
				throw new ItemNotFoundException("Service '" + serviceId
						+ "''s d Infrastructure Provider '"
						+ infrastructureProviderId + "' does not"
						+ " contain a VM '" + vmId + "' (no VMs, actually)");
			} else {
				vms = infrastructureProvider.getVms();

				if (vmExists(vms, vmId)) {
					for (int i = 0; i < vms.sizeOfVmArray(); i++) {
						if (vmId.equals(vms.getVmArray(i).getId())) {
							vms.removeVm(i);
							break;
						}
					}
				} else {
					throw new ItemNotFoundException("Service '" + serviceId
							+ "' does not contain a VM with id '" + vmId + "'");
				}

			}

		}

		return service.toString();
	}

	/**
	 * Deletes an infrastructure provider from a service
	 * 
	 * @param serviceId
	 * 
	 * @param infrastructureProviderId
	 * 
	 *            the id of the service to delete
	 */
	public String deleteIp(String serviceId, String infrastructureProviderId)
			throws ItemNotFoundException {

		ServiceDocument service = getService(serviceId);

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			throw new ItemNotFoundException("Service '" + serviceId
					+ " does not have an Infrastructure Provider with id "
					+ infrastructureProviderId);
		} else {
			for (int i = 0; i < service.getService()
					.sizeOfInfrastructureProviderArray(); i++) {
				if (service.getService().getInfrastructureProviderArray(i)
						.getId().equals(infrastructureProviderId)) {
					service.getService().removeInfrastructureProvider(i);
					break;
				}
			}
		}
		return service.toString();
	}

	/**
	 * Deletes all services.
	 */
	public void deleteServices() {
		services = new ArrayList<ServiceDocument>();
	}

	/**
	 * Adds a new service to the stored list of services.
	 * 
	 * @param service
	 *            the service to add
	 */
	public void addService(ServiceDocument service)
			throws ItemAlreadyExistsException {
		String newServiceId = service.getService().getServiceId();
		// Check for duplicate service (by id) and abort if duplicate is found
		for (int i = 0; i < services.size(); i++) {
			if (services.get(i).getService().getServiceId()
					.equals(newServiceId)) {
				throw new ItemAlreadyExistsException("Service with id '"
						+ newServiceId + "' already exists");
			}
		}

		services.add(service);
	}

	/**
	 * Adds an infrastructure provider to a service with the given
	 * <code>serviceId</code>; fails if the service already has an associated
	 * infrastructure provider.
	 * 
	 * @param serviceId
	 * @param infrastructureProvider
	 * @return
	 * @throws ItemNotFoundException
	 * @throws ItemAlreadyExistsException
	 */
	public ServiceDocument addIp(String serviceId,
			InfrastructureProviderDocument infrastructureProviderDocument)
			throws ItemNotFoundException, ItemAlreadyExistsException {
		ServiceDocument service;
		service = getService(serviceId);

		if (containsInfrastructureProvider(service,
				infrastructureProviderDocument.getInfrastructureProvider()
						.getId()) == true) {
			throw new ItemAlreadyExistsException(
					"Infrastructure Provider for service with id '" + serviceId
							+ "' already existing");
		} else {

			InfrastructureProvider newInfrastructureProvider = service
					.getService().addNewInfrastructureProvider();
			newInfrastructureProvider.setId(infrastructureProviderDocument
					.getInfrastructureProvider().getId());
			newInfrastructureProvider.setSlaId(infrastructureProviderDocument
					.getInfrastructureProvider().getSlaId());
			newInfrastructureProvider
					.setIpAddress(infrastructureProviderDocument
							.getInfrastructureProvider().getIpAddress());
			newInfrastructureProvider
					.setAgreementEndpoint(infrastructureProviderDocument
							.getInfrastructureProvider().getAgreementEndpoint());
			newInfrastructureProvider
					.setInitialTrustValue(infrastructureProviderDocument
							.getInfrastructureProvider().getInitialTrustValue());
			newInfrastructureProvider
					.setInitialRiskValue(infrastructureProviderDocument
							.getInfrastructureProvider().getInitialRiskValue());
			newInfrastructureProvider
					.setInitialEcoValue(infrastructureProviderDocument
							.getInfrastructureProvider().getInitialEcoValue());
			newInfrastructureProvider
					.setInitialCostValue(infrastructureProviderDocument
							.getInfrastructureProvider().getInitialCostValue());
			newInfrastructureProvider.setVms(infrastructureProviderDocument
					.getInfrastructureProvider().getVms());

			return service;
		}
	}

	public ServiceDocument addVm(String serviceId,
			String infrastructureProviderId, VmDocument vm)
			throws ItemNotFoundException, ItemAlreadyExistsException {
		ServiceDocument service = getService(serviceId);

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			throw new ItemNotFoundException("Service '" + serviceId
					+ " does not have an Infrastructure Provider with id "
					+ infrastructureProviderId);
		} else {
			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);

			Vms vms;
			if (infrastructureProvider.getVms() == null) {
				vms = infrastructureProvider.addNewVms();
			} else {
				vms = infrastructureProvider.getVms();
			}

			String vmId = vm.getVm().getId();
			if (vmExists(vms, vmId)) {
				throw new ItemAlreadyExistsException("VM with id '" + vmId
						+ "' already exists");
			}

			Vm newVm = vms.addNewVm();
			// Always set the id
			newVm.setId(vm.getVm().getId());

			// Only set the other fields if present
			if (vm.getVm().getStatus() != null) {
				newVm.setStatus(vm.getVm().getStatus());
			}
			if (vm.getVm().getType() != null) {
				newVm.setType(vm.getVm().getType());
			}

			// Type of this field is int. So, it always has a value.
			newVm.setDeploymentDurationInMs(vm.getVm()
					.getDeploymentDurationInMs());
		}

		return service;
	}

	
	/**
	 * Deploys a service with a give id 
	 * 
	 * @author django
	 * 
	 * @param objective
	 * @param manifestString
	 * @param sdUrl
	 * @return
	 */
	public boolean deployService(String objective, String manifestString, String sdUrl) {		
		DeploymentServiceClient sdoClient = new DeploymentServiceClient(
				sdUrl);
		try {
			return sdoClient.deploy(manifestString, objective);
		} catch (IOException ioe) {
			return false;
		}
	}
	
	/**
	 * Undeploy a service with the given id from the service storage.
	 * 
	 * @param id
	 *            the id of the service to delete
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 */
	public void undeployService(String serviceId, boolean keepData, String sdUrl)
			throws ItemNotFoundException, IOException {

		String infraProviderIds;
		try {
			infraProviderIds = getInfrastructureProviderIds(serviceId);
		} catch (ItemNotFoundException infe) {
			LOGGER.debug("ServiceManagerCore: error in undeployService - when calling getInfrastructureProviderIds");
			throw new ItemNotFoundException("Cannot find service with id "
					+ serviceId);
		}

		// System.out.println("ServiceManagerCore - infraProviderIds = " +
		// infraProviderIds);

		// Proceed only if service has at least one IP.
		if (infraProviderIds != null) {
			String[] IParray = infraProviderIds.split(";");
			DeploymentServiceClient sdoClient = new DeploymentServiceClient(
					sdUrl);

			LOGGER.debug("ServiceManagerCore - undeployService - number of providerIDs is: " + IParray.length);
			
			for (short i = 0; i < IParray.length; i++) {
				LOGGER.debug("ServiceManagerCore: - undeployService - Infrastructure Provider = " + IParray[i]);
				String agrEPR = null;
				try {
					agrEPR = getAgreementEndpoint(serviceId, IParray[i]);
				} catch (ItemNotFoundException infe) {
					LOGGER.error("ServiceManagerCore: - error in undeployService - when calling getAgreementEndpoint");
					throw new ItemNotFoundException(
							"ServiceManagerCore: - error in undeployService - cannot find service with id " + serviceId);
				}

				// System.out.println("ServiceManagerCore - agrEPR = " +
				// agrEPR);
				boolean undeployReturnedVal = false;
				try {
					if (agrEPR != null) {
						// Call only if there is an agreement endpoint.
						undeployReturnedVal = sdoClient.undeploy(serviceId,
								agrEPR, keepData);
						if (undeployReturnedVal == true) {
							LOGGER.debug("ServiceManagerCore: - undeployService - undeploy method in SDO client successful with [serviceId, agrEPR, keepData] = ["
											+ serviceId
											+ ", "
											+ agrEPR
											+ ", "
											+ keepData + "]");
						}
					} else {
						// Was not needed to call undeploy since there's no
						// agreement endpoint.
						// Let's set the value true to the returned value. This
						// will force the
						// removal of the IP from the service.
						undeployReturnedVal = true;
						assert (undeployReturnedVal);
					}
				} catch (IOException ioe) {
					LOGGER.error("ServiceManagerCore: - error in undeployService - undeploy method in SDO client failed with [serviceId, agrEPR, keepData] = ["
									+ serviceId
									+ ", "
									+ agrEPR
									+ ", "
									+ keepData + "]");
				}
				
				//Django: Uncommented to support clearing state
				LOGGER.debug("ServiceManagerCore - undeployReturnedVal = "
								+ undeployReturnedVal);
				if (undeployReturnedVal == true && keepData == false) {
					try {
						// Remove IP from the service
						LOGGER.debug("ServiceManagerCore: - undeployService - trying to delete IP in IParray[" + i + "] = " + IParray[i]);
						deleteIp(serviceId, IParray[i]);
						LOGGER.debug("ServiceManagerCore: - undeployService - deleted IP");
					} catch (ItemNotFoundException infe) {
						LOGGER.error("ServiceManagerCore: - error in undeployService - when calling deleteIp ItemNotFoundException for: " + IParray[i] + " Message: " + infe.getMessage());
					}
				}
				 
			}
		} else {
			LOGGER.debug("ServiceManagerCore: - undeployService - infraProviderIds == null :-(");
		}

		// Uncommented to support clearing state
		// If service has no remaining IP, delete it.
		if (hasInfrastructureProviders(serviceId) == false && keepData == false) {
			try {
				LOGGER.info("ServiceManagerCore: - undeployService - deleting service with ID: " + serviceId);
				deleteService(serviceId);
			} catch (ItemNotFoundException infe) {
				LOGGER.error("ServiceManagerCore: - error in undeployService - when calling deleteService with serviceId: " + serviceId +  " Message: " + infe.getMessage());
				throw new ItemNotFoundException(
						"ServiceManagerCore: - error in undeployService - Service not found. Cannot delete service with ID " + serviceId);
			}
		} else {
			if (keepData == false) {
				LOGGER.error("ServiceManagerCore: - error in undeployService - can't delete service with ID: " + serviceId + " it still has assocaited IPs!");
			}
		}
		 
	}

	
	/**
	 * Redeploy a service with the given id from the service storage.
	 * 
	 * @author django
	 * 
	 * @param id
	 *            the id of the service to delete
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 *            
	 * @throws IOException 
	 * @throws ItemNotFoundException 
	 */
	public void redeployService(String serviceId, boolean keepData, String sdUrl, String mrUrl) throws ItemNotFoundException, IOException {
	
		undeployService(serviceId, keepData, sdUrl);
			
		//Get old service IPs from internal state,
		String ipIds = getInfrastructureProviderIds(serviceId); //Returns comma separated list of ID's
	
		//Add the IP's to the manifest blacklist.	
		ManifestRegistryClient manifestRegistryClient = new ManifestRegistryClient(mrUrl); //Fetch this from service manifest repository.	
		XmlBeanServiceManifestDocument xmlBeanServiceManifestDocument = manifestRegistryClient.get(getService(serviceId).getService().getManifestId());
		//Update the blacklist in the manifest
		Manifest manifest = Manifest.Factory.newInstance(xmlBeanServiceManifestDocument);
		manifest.getServiceProviderExtensionSection().setBlackListIPs(ipIds);
		String manifestString = manifest.toString();
		 
		deployService(getService(serviceId).getService().getObjective(), manifestString, sdUrl);
	}
	
	/**
	 * Redeploy a service with the given id from the service storage when a threshhold is reached
	 * 
	 * @author django
	 * 
	 * @param id
	 *            the id of the service to delete
	 *            
	 * @param riskLevelThresholdToRedployOn
	 *            the riskLevel threshold to redeploy on
	 *            
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 *            
	 * @throws IOException
	 *  
	 * @throws ItemNotFoundException
	 * 
	 * @throws InterruptedException
	 */
	public void runTrigger(String serviceId, int riskLevelThresholdToRedployOn, boolean keepData, String sdUrl, String mrUrl) throws ItemNotFoundException, IOException, InterruptedException {
		
		//TODO Make this non-blocking?
		
		ServiceProviderRiskAssessmentToolRESTClient serviceProviderRiskAssessmentToolRESTClient = new ServiceProviderRiskAssessmentToolRESTClient();
		
		//Get old service IPs from internal state,
		String ipIds = getInfrastructureProviderIds(serviceId); //Returns comma separated list of ID's
		
		List<String> ipIdList = Arrays.asList(ipIds.split("\\s*,\\s*"));
		
		while (true) {
			
			int riskLevel = -1;
			
			for (String providerId : ipIdList) {
				Double proposedPoF = 0.01; //TODO: this should be fetched from somewhere and not hard coded 
				riskLevel = serviceProviderRiskAssessmentToolRESTClient.calculateRiskLevelOfSLAOfferReliability(providerId, serviceId, proposedPoF);
				if(riskLevel >= riskLevelThresholdToRedployOn) {
					break;
				}
			}
			
			if(riskLevel >= riskLevelThresholdToRedployOn) {
				break;
			} else {
				Thread.sleep(4000);
			}
		}

		undeployService(serviceId, keepData, sdUrl);
	
		//Add the IP's to the manifest blacklist.	
		ManifestRegistryClient manifestRegistryClient = new ManifestRegistryClient(mrUrl); //Fetch this from service manifest repository.	
		XmlBeanServiceManifestDocument xmlBeanServiceManifestDocument = manifestRegistryClient.get(getService(serviceId).getService().getManifestId());
		//Update the blacklist in the manifest
		Manifest manifest = Manifest.Factory.newInstance(xmlBeanServiceManifestDocument);
		manifest.getServiceProviderExtensionSection().setBlackListIPs(ipIds);
		String manifestString = manifest.toString();
		 
		deployService(getService(serviceId).getService().getObjective(), manifestString, sdUrl);
	}
	
	private boolean hasInfrastructureProviders(String serviceId)
			throws ItemNotFoundException {
		ServiceDocument service = getService(serviceId);
		InfrastructureProvider[] infrastructureProviders = service.getService()
				.getInfrastructureProviderArray();
		if (infrastructureProviders.length == 0 || infrastructureProviders == null) {
			return false;
		}
		return true;
	}

	private boolean containsInfrastructureProvider(ServiceDocument service,
			String infrastructureProviderId) {
		InfrastructureProvider[] infrastructureProviders = service.getService()
				.getInfrastructureProviderArray();
		if (infrastructureProviders == null) {
			return false;
		}

		for (InfrastructureProvider infrastructureProvider : infrastructureProviders) {
			// System.out.println("InfraProv: " +
			// infrastructureProvider.toString());
			if (infrastructureProvider.getId().equals(infrastructureProviderId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the infrastructure provider of the given service.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * @param infrastructureProvider
	 *            the infrastructure provider details that should be put into
	 *            the service
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             if the service is not found or if the service does not yet
	 *             contain an infrastructure provider
	 */
	public ServiceDocument updateIp(String serviceId,
			InfrastructureProvider infrastructureProvider)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);
		if (containsInfrastructureProvider(service,
				infrastructureProvider.getId()) == false) {
			throw new ItemNotFoundException("Service with id '" + serviceId
					+ "' does not contain an infrastructure provider with id "
					+ infrastructureProvider.getId() + ", cannot update");
		} else {
			InfrastructureProvider[] infrastructureProviderArray = service
					.getService().getInfrastructureProviderArray();
			for (int i = 0; i < infrastructureProviderArray.length; i++) {
				if (infrastructureProviderArray[i].getId().equals(
						infrastructureProvider.getId())) {
					service.getService().setInfrastructureProviderArray(i,
							infrastructureProvider);
				}
			}
			return service;
		}
	}

	/**
	 * Updates the SLA id for the given infrastructure provider of a service.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * @param ipId
	 *            the id of the information provider to update
	 * @param slaId
	 *            the new SLA id
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public ServiceDocument updateSlaId(String serviceId, String ipId,
			String slaId) throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				ifp.setSlaId(slaId);
				return service;
			}
		}
		return service;
	}

	/**
	 * Updates the agreement endpoint for the given infrastructure provider of a
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the id of the infrastructure provider
	 * @param agreementEndpoint
	 *            new value for the agreement endpoint
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public ServiceDocument updateAgreementEndpoint(String serviceId,
			String ipId, String agreementEndpoint) throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				ifp.setAgreementEndpoint(agreementEndpoint);
				return service;
			}
		}
		return service;
	}

	/**
	 * Updates the service's status.
	 * 
	 * @param serviceId
	 *            the id of the service to update
	 * @param status
	 *            the new status of the service
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public ServiceDocument updateStatus(String serviceId, String status)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);

		service.getService().setStatus(status);

		return service;
	}
	
	/**
	 * Gets the Manifest ID of a services
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 * @param manifestId
	 * @return
	 * @throws ItemNotFoundException
	 */
	public String getManifestId(String serviceId)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);

		return service.getService().getManifestId();
	}
	
	/**
	 * Updates the Manifest ID of a service
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 * @param manifestId
	 * @return
	 * @throws ItemNotFoundException
	 */
	public ServiceDocument updateManifestId(String serviceId, String manifestId)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);

		service.getService().setManifestId(manifestId);

		return service;
	}
	
	/**
	 * Gets the objective of a service
	 * 
	 * @author django
	 *  
	 * @param serviceId
	 * @param objective
	 * @return
	 * @throws ItemNotFoundException
	 */
	public String getObjective(String serviceId)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);
		
		return service.getService().getObjective();
	}
	
	/**
	 * Updates the objective of a service
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 * @param objective
	 * @return
	 * @throws ItemNotFoundException
	 */
	public ServiceDocument updateObjective(String serviceId, String objective)
			throws ItemNotFoundException {
		ServiceDocument service;
		service = getService(serviceId);

		service.getService().setObjective(objective);

		return service;
	}

	public ServiceDocument updateService(ServiceDocument newService)
			throws ItemNotFoundException {
		ServiceDocument oldService;
		// IDs need to be equal, obviously
		oldService = getService(newService.getService().getServiceId());

		oldService.setService(newService.getService());

		return oldService;
	}

	/**
	 * Updates the status of the VM with id <code>vmId</code> in the service
	 * with id <code>serviceId</code> to <code>status</code>.
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
	 * @param status
	 *            the new status of the VM
	 * 
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             If no service with id <code>serviceId</code> exists, if the
	 *             service has no infrastructure provider or if the
	 *             infrastructure provider has no VM with id <code>vmId</code>
	 */
	public ServiceDocument updateVmStatus(String serviceId,
			String infrastructureProviderId, String vmId, String status)
			throws ItemNotFoundException {
		LOGGER.debug("ServiceManagerCore: Updating status for VM " + vmId + " in service "
				+ serviceId);
		ServiceDocument service = getService(serviceId);

		LOGGER.debug("ServiceManagerCore: Service " + serviceId + " found");

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			LOGGER.debug("ServiceManagerCore: Service has no infrastructure provider with id "
					+ infrastructureProviderId + ", thus no VMs");
			throw new ItemNotFoundException("Service '" + serviceId
					+ "' does not have an infrastructure provider");
		} else {
			LOGGER.debug("ServiceManagerCore: Inspecting infrastructure provider for VMs");
			// InfrastructureProvider infrastructureProvider = service
			// .getService().getInfrastructureProvider();

			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);

			Vms vms;
			if (infrastructureProvider.getVms() == null) {
				LOGGER.debug("ServiceManagerCore: Infrastructure provider has no VMs");
				throw new ItemNotFoundException("Service '" + serviceId
						+ "' does not have any VMs");
			} else {
				vms = infrastructureProvider.getVms();

				LOGGER.debug("ServiceManagerCore: Inspecting individual VMs");

				// Update VM, if it exists
				if (vmExists(vms, vmId)) {
					LOGGER.debug("ServiceManagerCore: VM " + vmId + " exists");
					for (int i = 0; i < vms.sizeOfVmArray(); i++) {
						Vm vm = vms.getVmArray(i);

						if (vm.getId().equals(vmId)) {
							vm.setStatus(status);
						}
					}

				} else {
					throw new ItemNotFoundException("Service '" + serviceId
							+ "' does not have a VM with id '" + vmId + "'");
				}
			}
		}

		return service;
	}

	/**
	 * Update the deployment duration in ms of the VM with id <code>vmId</code>
	 * in the service with id <code>serviceId</code> to
	 * <code>deploymentDurationInMs</code>.
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
	 * @param status
	 *            the new status of the VM
	 * @return the updated service
	 * @throws ItemNotFoundException
	 *             If no service with id <code>serviceId</code> exists, if the
	 *             service has no infrastructure provider or if the
	 *             infrastructure provider has no VM with id <code>vmId</code>
	 */
	public ServiceDocument updateDeploymentDurationInMs(String serviceId,
			String infrastructureProviderId, String vmId,
			int deploymentDurationInMs) throws ItemNotFoundException {
		LOGGER.debug("ServiceManagerCore: Updating deployment duration in ms for VM " + vmId
				+ " in service " + serviceId);
		ServiceDocument service = getService(serviceId);

		LOGGER.debug("ServiceManagerCore: Service " + serviceId + " found");

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			LOGGER.debug("ServiceManagerCore: ervice has no infrastructure provider with id "
					+ infrastructureProviderId + ", thus no VMs");
			throw new ItemNotFoundException("Service '" + serviceId
					+ "' does not have an infrastructure provider");
		} else {
			LOGGER.debug("ServiceManagerCore: nspecting infrastructure provider for VMs");

			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);

			Vms vms;
			if (infrastructureProvider.getVms() == null) {
				LOGGER.debug("ServiceManagerCore: Infrastructure provider has no VMs");
				throw new ItemNotFoundException("Service '" + serviceId
						+ "' does not have any VMs");
			} else {
				vms = infrastructureProvider.getVms();

				LOGGER.debug("ServiceManagerCore: Inspecting individual VMs");

				// Update VM, if it exists
				if (vmExists(vms, vmId)) {
					LOGGER.debug("VM " + vmId + " exists");
					for (int i = 0; i < vms.sizeOfVmArray(); i++) {
						Vm vm = vms.getVmArray(i);

						if (vm.getId().equals(vmId)) {
							vm.setDeploymentDurationInMs(deploymentDurationInMs);
						}
					}

				} else {
					throw new ItemNotFoundException("ServiceManagerCore: Service '" + serviceId
							+ "' does not have a VM with id '" + vmId + "'");
				}
			}
		}

		return service;
	}

	private boolean vmExists(Vms vms, String vmId) {
		Vm[] vmArray = vms.getVmArray();
		for (int i = 0; i < vmArray.length; i++) {
			if (vmArray[i].getId().equals(vmId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new ServiceManager instance and returns it, if no instance
	 * previously existed; otherwise, the existing instance is returned.
	 * 
	 * @return the existing ServiceManager instance or a new instance if no
	 *         instance previously existed
	 */
	public static ServiceManager getInstance() {
		if (instance == null) {
			instance = new ServiceManager();
		}

		return instance;
	}

	/**
	 * Returns all services.
	 * 
	 * @return a list of all services
	 */
	public ArrayList<ServiceDocument> getServices() {
		return services;
	}

	public String getVms(String serviceId, String infrastructureProviderId)
			throws ItemNotFoundException {
		ServiceDocument service = getService(serviceId);

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			throw new ItemNotFoundException("Service '" + serviceId
					+ " does not have an Infrastructure Provider with id "
					+ infrastructureProviderId);
		} else {
			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);
			Vms vms = infrastructureProvider.getVms();
			if (vms == null) {
				throw new ItemNotFoundException("Service '" + serviceId
						+ " does not have any VMs");
			}

			VmsDocument vmsDoc = VmsDocument.Factory.newInstance();

			vmsDoc.setVms(vms);

			return vmsDoc.toString();
		}
	}

	/**
	 * Returns the service with the given <code>serviceId</code> or throws an
	 * exception if no service with this id is found.
	 * 
	 * @param serviceId
	 *            the if of the service to get
	 * @return the service, if existing
	 * @throws ItemNotFoundException
	 *             thrown if a service with the given id is not found
	 */
	public ServiceDocument getService(String serviceId)
			throws ItemNotFoundException {
		for (int i = 0; i < services.size(); i++) {
			if (services.get(i).getService().getServiceId().equals(serviceId)) {
				return services.get(i);
			}
		}

		throw new ItemNotFoundException("Service with id '" + serviceId
				+ "' does not exist");
	}

	public InfrastructureProvider getInfrastructureProvider(
			ServiceDocument service, String infrastructureProviderId) {
		InfrastructureProvider[] infrastructureProviderArray = service
				.getService().getInfrastructureProviderArray();
		for (InfrastructureProvider infrastructureProvider : infrastructureProviderArray) {
			if (infrastructureProvider.getId().equals(infrastructureProviderId)) {
				return infrastructureProvider;
			}
		}
		return null;
	}

	public String getVmStatus(String serviceId,
			String infrastructureProviderId, String vmId)
			throws ItemNotFoundException {
		LOGGER.debug("ServiceManagerCore: Getting status for VM " + vmId + " in service "
				+ serviceId);
		ServiceDocument service = getService(serviceId);
		LOGGER.debug("ServiceManagerCore: Service " + serviceId + " found");

		if (containsInfrastructureProvider(service, infrastructureProviderId) == false) {
			LOGGER.debug("ServiceManagerCore: Service has no infrastructure provider with id "
					+ infrastructureProviderId + ", thus no VMs");
			throw new ItemNotFoundException("Service '" + serviceId
					+ "' does not have an infrastructure provider with id"
					+ infrastructureProviderId);
		} else {
			LOGGER.debug("ServiceManagerCore: Inspecting infrastructure provider for VMs");
			InfrastructureProvider infrastructureProvider = getInfrastructureProvider(
					service, infrastructureProviderId);

			Vms vms;
			if (infrastructureProvider.getVms() == null) {
				LOGGER.debug("ServiceManagerCore: Infrastructure provider has no VMs");
				throw new ItemNotFoundException("Service '" + serviceId
						+ "' does not have any VMs");
			} else {
				vms = infrastructureProvider.getVms();
				LOGGER.debug("ServiceManagerCore: Inspecting individual VMs");

				// Check if VM even exists
				if (vmExists(vms, vmId)) {
					for (int i = 0; i < vms.sizeOfVmArray(); i++) {
						Vm vm = vms.getVmArray(i);

						if (vm.getId().equals(vmId)) {
							LOGGER.debug("ServiceManagerCore: VM found! Returning status");
							return vm.getStatus();
						}
					}

				} else {
					LOGGER.debug("ServiceManagerCore: VM " + vmId + "does not exist in service "
							+ serviceId);
					throw new ItemNotFoundException("Service '" + serviceId
							+ "' does not have a VM with id '" + vmId + "'");
				}
			}
		}
		LOGGER.fatal("ServiceManagerCore: Potentially inaccessible code reached!");
		// Should never come here
		throw new ItemNotFoundException();
	}

	/**
	 * Returns a String containing the ids of all infrastructure providers for
	 * the service with the given id, separated by a semicolon (;).
	 * 
	 * @param serviceId
	 *            the id of the services whose infrastructure providers are to
	 *            be returned
	 * 
	 * @return a String containing the infrastructure provider ids of the
	 *         service separated by a semicolon
	 * 
	 * @throws ItemNotFoundException
	 *             if a service with the given id cannot be found
	 */
	public String getInfrastructureProviderIds(String serviceId)
			throws ItemNotFoundException {
		StringBuffer result = new StringBuffer();
		// Get service and the infrastructure providers, prepare return value

		// This will throw the ItemNotFoundException if the service cannot be
		// found
		ServiceDocument service = getService(serviceId);

		InfrastructureProvider[] infrastructureProviderArray = service
				.getService().getInfrastructureProviderArray();

		if (infrastructureProviderArray == null) {
			// Value returned when no IP was found
			return null;
		}

		for (InfrastructureProvider infrastructureProvider : infrastructureProviderArray) {
			result.append(infrastructureProvider.getId());
			result.append(';');
		}

		// Remove the last ';' as it does not delimit another service
		if (result.length() > 0) {
			result.deleteCharAt(result.lastIndexOf(";"));
		}

		return result.toString();
	}

	/**
	 * Returns a String containing the slaId of all infrastructure providers for
	 * the service with the given id, separated by a semicolon (;).
	 * 
	 * @param serviceId
	 *            the id of the services whose infrastructure providers are to
	 *            be returned
	 * 
	 * @return a String containing SLA ids of the service separated by a
	 *         semicolon
	 * 
	 * @throws ItemNotFoundException
	 *             if a service with the given id cannot be found
	 */
	public String getSlaIds(String serviceId) throws ItemNotFoundException {
		StringBuffer result = new StringBuffer();
		// Get service and the infrastructure providers, prepare return value
		ServiceDocument service = getService(serviceId);
		InfrastructureProvider[] infrastructureProviderArray = service
				.getService().getInfrastructureProviderArray();

		for (InfrastructureProvider infrastructureProvider : infrastructureProviderArray) {
			result.append(infrastructureProvider.getSlaId());
			result.append(';');
		}

		// Remove the last ';' as it does not delimit another service
		if (result.length() > 0) {
			result.deleteCharAt(result.lastIndexOf(";"));
		}

		System.err.println("ServiceManagerCore: Result: " + result);

		return result.toString();
	}

	/**
	 * Get the SLA id of an infrastructure provider for the given service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the slaId
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public String getSlaId(String serviceId, String ipId)
			throws ItemNotFoundException {
		String slaId = "";
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getSlaId();

			}
		}
		return slaId;
	}

	/**
	 * Get the initial trust value of an infrastructure provider for the given
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the initial trust value
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public float getInitialTrustValue(String serviceId, String ipId)
			throws ItemNotFoundException {
		float initialTrustValue = 0.0f;
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getInitialTrustValue();

			}
		}
		return initialTrustValue;
	}

	/**
	 * Get the initial risk value of an infrastructure provider for the given
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the initial risk value
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public float getInitialRiskValue(String serviceId, String ipId)
			throws ItemNotFoundException {
		float initialRiskValue = 0.0f;
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getInitialRiskValue();

			}
		}
		return initialRiskValue;
	}

	/**
	 * Get the initial eco value of an infrastructure provider for the given
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the initial eco value
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public float getInitialEcoValue(String serviceId, String ipId)
			throws ItemNotFoundException {
		float initialEcoValue = 0.0f;
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getInitialEcoValue();

			}
		}
		return initialEcoValue;
	}

	/**
	 * Get the initial cost value of an infrastructure provider for the given
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the initial cost value
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public float getInitialCostValue(String serviceId, String ipId)
			throws ItemNotFoundException {
		float initialCostValue = 0.0f;
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getInitialCostValue();

			}
		}
		return initialCostValue;
	}

	/**
	 * Get the agreement endpoint of an infrastructure provider for the given
	 * service.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param ipId
	 *            the infrastructure provider id
	 * @return the agreement endpoint
	 * @throws ItemNotFoundException
	 *             if the service is not found
	 */
	public String getAgreementEndpoint(String serviceId, String ipId)
			throws ItemNotFoundException {
		String agreementEndpoint = null;
		ServiceDocument service;
		service = getService(serviceId);
		InfrastructureProvider[] infps = service.getService()
				.getInfrastructureProviderArray();
		for (InfrastructureProvider ifp : infps) {
			if (ifp.getId().equals(ipId)) {
				return ifp.getAgreementEndpoint();

			}
		}
		return agreementEndpoint;
	}
}
