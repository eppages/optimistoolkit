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

package eu.optimis.service_manager.rest;

//Django: Removed due to dependency issue
//import java.sql.ResultSet; //For broker code in updateServiceStatus()
//import java.sql.SQLException; //For broker code in updateServiceStatus()
//import java.sql.Statement; //For broker code in updateServiceStatus()
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;

import com.sun.jersey.spi.resource.Singleton;

import eu.optimis.serviceManager.InfrastructureProviderDocument;
import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.ServiceDocument.Service;
import eu.optimis.serviceManager.VmDocument;
import eu.optimis.serviceManager.VmDocument.Vm;
import eu.optimis.service_manager.core.ServiceManager;
import eu.optimis.service_manager.exception.ItemAlreadyExistsException;
import eu.optimis.service_manager.exception.ItemNotFoundException;
import eu.optimis.service_manager.rest.util.ConfigManager;

//Django: Removed due to dependency issue
//import eu.optimis.mi.dbutil.GeneralDBUtil; //For broker code in updateServiceStatus()
import eu.optimis.treccommon.TrecApiSP;

/**
 * A single service resource.
 * 
 * @author roland
 * @author django (ULeeds)
 */
// @Path("/services/{id}")
@Path("/")
@Singleton
public class ServiceManagerREST {

	private static ServiceManager serviceManager;

	private final static Logger LOGGER = Logger
			.getLogger(ServiceManagerREST.class.getName());
	private static String /*
						 * Django: Removed due to dependency issue
						 * TREC_DB_DRIVER, TREC_TABLE_URL, TREC_DB_USER,
						 * TREC_DB_PASSWORD, TREC_HOST, TREC_PORT,
						 */SD_URL, MANIFEST_REPO_URL;

	private String TREC_HOST;

	private String TREC_PORT;

	// Django: used for broker commented out
	// private String TREC_TABLE_URL;
	// private String TREC_DB_USER;
	// private String TREC_DB_PASSWORD;
	// private String TREC_DB_DRIVER;

	public ServiceManagerREST() {
		try {
			PropertyConfigurator.configure(ConfigManager
					.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
			LOGGER.debug("ServiceManagerREST: Constructor invoked");
			PropertiesConfiguration config = ConfigManager
					.getPropertiesConfiguration(ConfigManager.SM_CONFIG_FILE);
			SD_URL = config.getString("sp.sd.url");
			LOGGER.debug("ServiceManagerREST: SD_URL is: " + SD_URL);
			MANIFEST_REPO_URL = config.getString("sp.mr.url");
			LOGGER.debug("ServiceManagerREST: MANIFEST_REPO_URL is: " + MANIFEST_REPO_URL);
		} catch (Exception e) {
			LOGGER.error("ServiceManagerREST: Error reading configuration file or getting properties");
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			ResourceBundle rb = ResourceBundle.getBundle("services");

			TREC_HOST = rb.getString("service.trec_host");
			TREC_PORT = rb.getString("service.trec_port");

			LOGGER.debug("ServiceManagerREST: TREC_HOST is: " + TREC_HOST + ", TREC_PORT is: " + TREC_PORT);
			
			// Django: Used for broker commented out
			// TREC_TABLE_URL = rb.getString("sp.trec.db.url");
			// TREC_DB_USER = rb.getString("sp.trec.db.username");
			// TREC_DB_PASSWORD = rb.getString("sp.trec.db.password");
			// TREC_DB_DRIVER = rb.getString("sp.trec.db.driver");

			SD_URL = rb.getString("sp.sd.url");
		} catch (MissingResourceException e) {
			LOGGER.error("ServiceManagerREST: Error - cannot find the resource bundle path.");
			throw new RuntimeException(e);
		}
		if (serviceManager == null) {
			serviceManager = ServiceManager.getInstance();
		}
	}

	/**
	 * Deletes a VM with the given id from the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param serviceId
	 *            the id of the VM to delete
	 */
	@DELETE
	@Path("/services/{serviceId}/ip/{ipid}/vms/{vmId}")
	public void deleteVm(@PathParam("serviceId") String serviceId,
			@PathParam("ipid") String infrastructureProviderId,
			@PathParam("vmId") String vmId) {
		LOGGER.debug("ServiceManagerREST: Delete VM called (service id: " + serviceId
				+ ", infra provider id: " + infrastructureProviderId
				+ ", VM id: " + vmId + ")");
		try {
			serviceManager.deleteVm(serviceId, infrastructureProviderId, vmId);
		} catch (ItemNotFoundException e) {
			// If service is not found, has no infrastructure provider or
			// the VM with the given id does not exist, throw an error
			// 404 (not found).
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Deletes a service with the given id from the service storage.
	 * 
	 * @param id
	 *            the id of the service to delete
	 */
	@DELETE
	@Path("services/{id}")
	public void deleteService(@PathParam("id") String serviceId) {
		try {
			LOGGER.debug("ServiceManagerREST: Deleting service with id " + serviceId);
			serviceManager.deleteService(serviceId);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot delete service with id '" + serviceId
					+ "' - not found");
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Creates a new service with a given id and status.
	 * <p/>
	 * A correct input message looks like this:
	 * <p>
	 * &lt;service&gt;<br/>
	 * &lt;service_id&gt;101&lt;/service_id&gt;<br/>
	 * &lt;status&gt;pending&lt;/service_id&gt;<br/>
	 * &lt;/service&gt;<br/>
	 * 
	 * @param message
	 *            a message containing the service id and status
	 * 
	 * @return the created service
	 */
	@POST
	@Path("/services")
	public String createService(String serviceString) {
		LOGGER.debug("ServiceManagerREST: POST /services called");
		LOGGER.debug("ServiceManagerREST: Received message: '" + serviceString + "'");
		ServiceDocument service = null;
		try {
			service = ServiceDocument.Factory.parse(serviceString);
		} catch (XmlException xmlException) {
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		try {
			serviceManager.addService(service);
		} catch (ItemAlreadyExistsException itemExistsException) {
			LOGGER.error("ServiceManagerREST: Service already exists (id "
					+ service.getService().getServiceId() + ")");
			// Throw an error 403 (forbidden) as we do should CREATE here, not
			// UPDATE
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}

		return service.toString();
	}

	/**
	 * Adds a VM to a service.
	 * 
	 * @param serviceId
	 *            the id of the service to which the VM is to be added
	 * 
	 * @param message
	 *            the message containing a VM element
	 * 
	 * @return the new service
	 */
	@POST
	@Path("/services/{id}/ip/{ipid}/vms")
	public String addVm(@PathParam("id") String serviceId,
			@PathParam("ipid") String infrastructureProviderId, String xmlText) {
		// TODO Fail if the VM id already exists?
		LOGGER.debug("ServiceManagerREST: Adding VM to service " + serviceId);
		LOGGER.debug("ServiceManagerREST: Received XML: " + xmlText);
		VmDocument vm = null;
		try {
			vm = VmDocument.Factory.parse(xmlText);
		} catch (XmlException xmlException) {
			LOGGER.debug("ServiceManagerREST: Could not parse XML", xmlException);
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		try {
			ServiceDocument updatedService = serviceManager.addVm(serviceId,
					infrastructureProviderId, vm);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update VM for service with id '" + serviceId
					+ "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} catch (ItemAlreadyExistsException e) {
			LOGGER.error("ServiceManagerREST: VM already exists (id " + vm.getVm().getId() + ")");
			// Throw an error 403 (forbidden) as we do should CREATE here, not
			// UPDATE
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}

	/**
	 * Adds an infrastructure provider to a service.
	 * 
	 * @param serviceId
	 *            the id of the service to which the VM is to be added
	 * 
	 * @param message
	 *            the XML message containing an infrastructure provider
	 * 
	 * @return the new service
	 */
	@POST
	@Path("/services/{id}/ip/")
	public String addIp(@PathParam("id") String serviceId, String xmlText) {
		LOGGER.debug("ServiceManagerREST: Adding IP to service " + serviceId);
		LOGGER.debug("ServiceManagerREST: Received XML: " + xmlText);
		InfrastructureProviderDocument infraProvider = null;
		try {
			infraProvider = InfrastructureProviderDocument.Factory
					.parse(xmlText);
		} catch (XmlException xmlException) {
			LOGGER.debug("ServiceManagerREST: Could not parse XML", xmlException);
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		try {
			ServiceDocument updatedService = serviceManager.addIp(serviceId,
					infraProvider);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update IP for service with id '" + serviceId
					+ "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} catch (ItemAlreadyExistsException e) {
			LOGGER.error("ServiceManagerREST: IP already exists (id "
					+ infraProvider.getInfrastructureProvider().getId() + ")");
			// Throw an error 403 (forbidden) as we do should CREATE here, not
			// UPDATE
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
	}

	/**
	 * Deploy a service with the given id from the service storage.
	 * 
	 * @author django
	 * 
	 * @param objective
	 *            the objective of the service
	 * @param manifestString
	 *            manifest to deploy
	 *            
	 * @return results of the deploy
	 */
	@POST
	@Path("services/deploy/objective/{objective}")
	public String deployService(@PathParam("objective") String objective,
			String manifestString) {
		try {
			LOGGER.debug("ServiceManagerREST: deployService() called (objective=" + objective + ")");
			serviceManager.deployService(objective, manifestString, SD_URL);
			LOGGER.debug("ServiceManagerREST: returned from deployService()");
			return "OK";
		} catch (Exception e) {
			LOGGER.error("ServiceManagerREST: Error in deployService - when calling deployService of ServiceManagerCore");
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Undeploy a service with the given id from the service storage.
	 * 
	 * @author django
	 * 
	 * @param id
	 *            the id of the service to delete
	 * @param keepData
	 *            if true the SD will keep data associated with the service
	 *            after undeployment
	 * @return result of the undeploy 
	 */
	@POST
	@Path("services/{id}/undeploy")
	public String undeployService(@PathParam("id") String serviceId,
			String keepData) {
		try {
			LOGGER.debug("ServiceManagerREST: undeployService() called for serviceId: " + serviceId + "(keepdate=" + keepData + ")");
			serviceManager.undeployService(serviceId, Boolean.valueOf(keepData)
					.booleanValue(), SD_URL);
			LOGGER.debug("ServiceManagerREST: returned from undeployService() for serviceId: " + serviceId);
			return "OK";
		} catch (Exception e) {
			LOGGER.error("ServiceManagerREST: Error in undeployService - when calling undeployService of ServiceManagerCore");
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Redeploy a service with the given id from the service storage.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service to redeploy
	 * @param keepData
	 *            whether to keep data after redeployment
	 * @return result of the redeploy 
	 */
	@POST
	@Path("services/{serviceId}/redeploy")
	public String redeployService(@PathParam("serviceId") String serviceId,
			String keepData) {
		try {
			LOGGER.debug("ServiceManagerREST: redeployService() called for serviceId: " + serviceId + "(keepdate=" + keepData + ")");
			serviceManager.redeployService(serviceId,
					Boolean.parseBoolean(keepData), SD_URL, MANIFEST_REPO_URL);
			LOGGER.debug("ServiceManagerREST: returned from redeployService() for serviceId: " + serviceId);
			return "OK";
		} catch (Exception e) {
			LOGGER.error("ServiceManagerREST: Error in redeployService - when calling redeployService of ServiceManagerCore");
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Redeploy a service with the given id from the service storage when a
	 * threshold riskLevel is reached.
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the id of the service
	 * @param riskLevelThresholdToRedployOn
	 *            the threshold that will trigger redeployment
	 * @param keepData
	 *            keep data after deleting
	 * @return result of the redeployOnRiskThreshold
	 */
	@POST
	@Path("services/{serviceId}/redeployOnRiskThreshold/{riskLevelThresholdToRedployOn}")
	public String redeployOnRiskThreshold(
			@PathParam("serviceId") String serviceId,
			@PathParam("riskLevelThresholdToRedployOn") String riskLevelThresholdToRedployOn,
			String keepData) {
		try {
			LOGGER.debug("ServiceManagerREST: running trigger mechanism [runTrigger()] for: " + serviceId + " and riskLevelThresholdToRedployOn: " + riskLevelThresholdToRedployOn);
			serviceManager.runTrigger(serviceId,
					Integer.parseInt(riskLevelThresholdToRedployOn),
					Boolean.parseBoolean(keepData), SD_URL, MANIFEST_REPO_URL);
			LOGGER.debug("ServiceManagerREST: returned from trigger mechanism  [runTrigger()] for: " + serviceId);
			return "OK";
		} catch (Exception e) {
			LOGGER.error("ServiceManagerREST: error in redeployService - when calling redeployService of ServiceManagerCore");
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@PUT
	@Consumes("application/xml")
	@Produces("application/xml")
	@Path("services/{id}")
	public String updateService(@PathParam("id") String serviceId,
			String serviceString) {
		ServiceDocument service = null;

		LOGGER.debug("ServiceManagerREST: updateService() called for serviceId: " + serviceId);
		
		try {
			service = ServiceDocument.Factory.parse(serviceString);
		} catch (XmlException xmlException) {
			// XML was wrong, so throw an error 400 (bad request)
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		ServiceDocument updatedService = null;
		try {
			updatedService = serviceManager.updateService(service);
			LOGGER.debug("ServiceManagerREST: updated Service with ID: " + serviceId);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update service with id '" + serviceId + "': "
					+ itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return updatedService.toString();
	}

	/**
	 * Updates a services status
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the service ID
	 * @param status
	 *            the status of the service
	 * @return the new service
	 */
	@PUT
	@Produces("application/xml")
	@Path("/services/{serviceId}/status")
	public String updateServiceStatus(@PathParam("serviceId") String serviceId,
			String status) {
		LOGGER.debug("ServiceManagerREST: updateServiceStatus() called for serviceId: " + serviceId + " status: " + status);
		ServiceDocument updatedService;
		try {
			updatedService = serviceManager.updateStatus(serviceId, status);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update status for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Django: Borker code not used?
		// java.sql.Connection conn =
		// GeneralDBUtil.getConnection(TREC_TABLE_URL,
		// TREC_DB_USER, TREC_DB_PASSWORD, TREC_DB_DRIVER);
		//
		//
		// boolean bo = false;
		// String broker_host = null;
		// int broker_port = 0;
		//
		// try {
		// Statement st = conn.createStatement();
		// ResultSet rs = st
		// .executeQuery("select * from manifest_raw where service_id='"
		// + serviceId + "'");
		// while (rs.next()) {
		// bo = rs.getBoolean("is_broker");
		// broker_host = rs.getString("broker_host");
		// broker_port = rs.getInt("broker_port");
		// }
		// } catch (SQLException e) {
		// System.err.println("SQLException:" + e.getMessage() + ":"
		// + e.getSQLState());
		// LOGGER.error("failed by connection with TREC DB, url:"
		// + TREC_TABLE_URL);
		// } finally {
		// try {
		// conn.close();
		// } catch (Exception e) {
		// LOGGER.error("failed by connection with TREC DB, URL: "
		// + TREC_TABLE_URL);
		// }
		// }

		String trec_host = TREC_HOST;
		int trec_port = Integer.parseInt(TREC_PORT);

		// Django: Broker code not used?
		// if (bo == true) {
		// trec_host = broker_host;
		// trec_port = broker_port;
		// }

		TrecApiSP trec = new TrecApiSP(trec_host, trec_port);
		if (status.toUpperCase().equals("DEPLOYED")) {
			LOGGER.debug("ServiceManagerREST: Starting TREC_SP_startmonitoring() at URI: " + trec_host + ":" + trec_port);
			try {
				trec.TREC_SP_startmonitoring(null, serviceId,
						new Long("150000"), "150000");
				LOGGER.debug("ServiceManagerREST: TREC_SP_startmonitoring() returned");
			} catch (Exception e) {
				LOGGER.error("ServiceManagerREST: failed calling trec_sp_startmonitoring, serviceId:"
						+ serviceId);
				e.printStackTrace();
			}
		} else if (status.toUpperCase().equals("UNDEPLOYED")) {
			LOGGER.debug("ServiceManagerREST: Stoping TREC_SP_stopmonitoring() for serviceId of: " + serviceId);
			trec.TREC_SP_stopmonitoring(serviceId);
			LOGGER.debug("ServiceManagerREST: TREC_SP_stopmonitoring() returned");
		}
		return updatedService.toString();
	}

	/**
	 * Gets the manifest ID of a Service
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the service ID
	 * @return the Manifest ID
	 */
	@GET
	@Path("/services/{serviceId}/manifest_id")
	public String getServiceManifestId(@PathParam("serviceId") String serviceId) {
		LOGGER.debug("ServiceManagerREST: getServiceManifestId() called for serviceId: " + serviceId);
		String manifestId;
		try {
			manifestId = serviceManager.getManifestId(serviceId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get manifestId for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return manifestId;
	}

	/**
	 * Updates a service's manifest ID
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the service ID
	 * @param manifestId
	 *            the Manifest ID
	 * @return the new service
	 */
	@PUT
	@Produces("application/xml")
	@Path("/services/{serviceId}/manifest_id")
	public String updateServiceManifestId(
			@PathParam("serviceId") String serviceId, String manifestId) {
		LOGGER.debug("ServiceManagerREST: updateServiceManifestId() called for serviceId: " + serviceId);
		ServiceDocument updatedService;
		try {
			updatedService = serviceManager.updateManifestId(serviceId,
					manifestId);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update manifestId for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return updatedService.toString();
	}

	/**
	 * Gets the objective of the service
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the service ID
	 * @return the service's objective
	 */
	@GET
	@Path("/services/{serviceId}/objective")
	public String getServiceObjective(@PathParam("serviceId") String serviceId) {
		LOGGER.debug("ServiceManagerREST: getServiceObjective() called for serviceId: " + serviceId);
		String objective;
		try {
			objective = serviceManager.getObjective(serviceId);
			LOGGER.debug("ServiceManagerREST: objective is: " + objective);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get objective for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return objective;
	}

	/**
	 * Updates the objective of a service
	 * 
	 * @author django
	 * 
	 * @param serviceId
	 *            the service ID
	 * @param objective
	 *            the objective to update
	 * @return the new service
	 */
	@PUT
	@Produces("application/xml")
	@Path("/services/{serviceId}/objective")
	public String updateServiceObjective(
			@PathParam("serviceId") String serviceId, String objective) {
		LOGGER.debug("ServiceManagerREST: updateServiceObjective() called for serviceId: " + serviceId + " with objective: " + objective);
		ServiceDocument updatedService;
		try {
			updatedService = serviceManager.updateObjective(serviceId,
					objective);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update objective for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return updatedService.toString();
	}

	@PUT
	@Path("/services/{serviceId}/ip/{ipid}/vms/{vmId}/status")
	public String updateVmStatus(@PathParam("serviceId") String serviceId,
			@PathParam("ipid") String infrastructureProviderId,
			@PathParam("vmId") String vmId, String status) {
		LOGGER.debug("ServiceManagerREST: updateVmStatus() called for serviceId: " + serviceId + " with infrastructureProviderId: " + infrastructureProviderId + ", vmId: " + vmId +", and status: " + status);
		try {
			ServiceDocument updatedService = serviceManager.updateVmStatus(
					serviceId, infrastructureProviderId, vmId, status);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update status for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
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
	@PUT
	@Path("/services/{serviceId}/ip/{ipid}/vms/{vmId}/deployment_duration_in_ms")
	public String updateDeploymentDurationInMs(
			@PathParam("serviceId") String serviceId,
			@PathParam("ipid") String infrastructureProviderId,
			@PathParam("vmId") String vmId, String deploymentDurationInMs) {
		LOGGER.debug("ServiceManagerREST: updateDeploymentDurationInMs() called for serviceId: " + serviceId + " with infrastructureProviderId: " + infrastructureProviderId + ", vmId: " + vmId +", and deploymentDurationInMs: " + deploymentDurationInMs);
		try {
			ServiceDocument updatedService = serviceManager
					.updateDeploymentDurationInMs(serviceId,
							infrastructureProviderId, vmId,
							Integer.parseInt(deploymentDurationInMs));
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update deployment duration in ms for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Updates the service's SLA id for a given service.
	 * <p/>
	 * Fails if the service does not exist.
	 * <p/>
	 * Does <b>NOT</b> fail if the SLA id is not yet set!
	 * 
	 * @param serviceId
	 *            id of the service to update
	 * 
	 * @param slaId
	 *            the new SLA id of the service
	 * 
	 * @return the updated service
	 */
	@PUT
	@Path("/services/{id}/{ipId}/slaid")
	public String updateSlaId(@PathParam("id") String serviceId,
			@PathParam("ipId") String ipId, String slaId) {
		LOGGER.debug("ServiceManagerREST: updateSlaId() called for serviceId: " + serviceId + " with ipId: " + ipId + " and slaId: " + slaId);
		try {
			ServiceDocument updatedService = serviceManager.updateSlaId(
					serviceId, ipId, slaId);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update SLA id for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Updates the agreement endpoint of a service.
	 * <p/>
	 * Fails if the service does not exist.
	 * <p/>
	 * Does <b>NOT</b> fail if the agreement endpoint is not yet set!
	 * 
	 * @param serviceId
	 *            id of the service
	 * 
	 * @param agreementEndpoint
	 *            new value for the agreement endpoint
	 * 
	 * @return the updated service
	 */
	@PUT
	@Path("/services/{id}/{ipId}/agreementendpoint")
	public String updateAgreementEndpoint(@PathParam("id") String serviceId,
			@PathParam("ipId") String ipId, String agreementEndpoint) {
		LOGGER.debug("ServiceManagerREST: updateAgreementEndpoint() called for serviceId: " + serviceId + " with ipId: " + ipId + " and agreementEndpoint: " + agreementEndpoint);
		try {
			ServiceDocument updatedService = serviceManager
					.updateAgreementEndpoint(serviceId, ipId, agreementEndpoint);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update agreement endpoint for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Updates the infrastructure provider for a given service.
	 * <p/>
	 * Fails if the service does not exist <b>OR</b> if the service has no IP to
	 * begin with.
	 * 
	 * @param serviceId
	 *            id of the service to update
	 * 
	 * @param ipId
	 *            the new id of the infrastructure provider
	 * 
	 * @param ipAddress
	 *            the new IP address
	 * 
	 * @return the updated service
	 */
	@PUT
	@Path("/services/{id}/ip/{ipId}/ipaddress")
	public String updateIPs(@PathParam("id") String serviceId,
			@PathParam("ipId") String ipId, String ipAddress) {
		LOGGER.debug("ServiceManagerREST: updateIPs() called for serviceId: " + serviceId + " with ipId: " + ipId + " and ipAddress: " + ipAddress);
		InfrastructureProvider infrastructureProvider = InfrastructureProvider.Factory
				.newInstance();
		infrastructureProvider.setId(ipId);
		infrastructureProvider.setIpAddress(ipAddress);

		try {
			ServiceDocument updatedService = serviceManager.updateIp(serviceId,
					infrastructureProvider);
			return updatedService.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot update IP for service with id '" + serviceId
					+ "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

	}

	// /**
	// * Sets the infrastructure provider for a given service.
	// * <p/>
	// * Fails if the service does not exist <b>OR</b> if the service has an IP
	// to
	// * begin with.
	// *
	// * @param serviceId
	// * id of the service to update
	// *
	// * @param ipId
	// * the new id of the infrastructure provider
	// *
	// * @param ipAddress
	// * the new IP address
	// *
	// * @return the resulting service
	// */
	// @POST
	// @Path("/services/{serviceId}/ip/{ipId}")
	// public String setIp(@PathParam("serviceId") String serviceId,
	// @PathParam("ipId") String ipId, String ipAddress) {
	// InfrastructureProvider infrastructureProvider =
	// InfrastructureProvider.Factory
	// .newInstance();
	// infrastructureProvider.setId(ipId);
	// infrastructureProvider.setIpAddress(ipAddress);
	//
	// try {
	// ServiceDocument updatedService = serviceManager.addIp(serviceId,
	// infrastructureProvider);
	// return updatedService.toString();
	// } catch (ItemAlreadyExistsException itemExistsException) {
	// LOGGER.debug("Cannot add IP for service with id '" + serviceId
	// + "': " + itemExistsException.getMessage());
	// // Throw an error 403 (forbidden) as we should PUT here, not POST
	// throw new WebApplicationException(Response.Status.FORBIDDEN);
	// } catch (ItemNotFoundException itemNotFoundException) {
	// LOGGER.debug("Cannot add IP for service with id '" + serviceId
	// + "' - not found");
	// // Throw an error 404 (not found) if the resource does not exist
	// throw new WebApplicationException(Response.Status.NOT_FOUND);
	// }
	// }

	/**
	 * Gets the status for the Vm with the given <code>vmId</code> from the
	 * service with the given <code>serviceId</code>.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param vmId
	 *            the id of the VM
	 * 
	 * @return the status of the requested VM
	 */
	@GET
	@Path("/services/{serviceId}/ip/{ipid}/vms/{vmId}/status")
	public String getVmStatus(@PathParam("serviceId") String serviceId,
			@PathParam("ipid") String infrastructureProviderId,
			@PathParam("vmId") String vmId) {
		LOGGER.debug("ServiceManagerREST: getVmStatus() called for serviceId: " + serviceId + " with infrastructureProviderId: " + infrastructureProviderId + " and vmId: " + vmId);

		try {
			return serviceManager.getVmStatus(serviceId,
					infrastructureProviderId, vmId);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get VMs for service with id '" + serviceId,
					itemNotFoundException);
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Returns the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service to get
	 * 
	 * @return the service with the given id
	 */
	@GET
	@Path("services/{id}")
	@Produces("application/xml")
	public String getService(@PathParam("id") String serviceId) {
		LOGGER.debug("ServiceManagerREST: getService() called for serviceId: " + serviceId);

		try {
			ServiceDocument service = serviceManager.getService(serviceId);
			return service.toString();
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get service with id '" + serviceId
					+ "' - not found");
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

	}

	/**
	 * Gets the Vms for the service with the given id.
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @return the VMs contained in the service
	 */
	@GET
	@Path("/services/{id}/ip/{ipid}/vms")
	public String getVms(@PathParam("id") String serviceId,
			@PathParam("ipid") String infrastructureProviderId) {
		LOGGER.debug("ServiceManagerREST: getVms() called for serviceId: " + serviceId + " with infrastructureProviderId: " + infrastructureProviderId);

		try {
			return serviceManager.getVms(serviceId, infrastructureProviderId);
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get VMs for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Returns the list of infrastructure provider ids for a given service.
	 * <p/>
	 * This operations is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/ids</code>
	 * 
	 * @param serviceId
	 *            the id of the service whose infrastructure provider ids are to
	 *            be returned
	 * 
	 * @return a String[] containing the infrastructure provider ids
	 */
	@GET
	@Path("/services/{serviceId}/ip/ids")
	public String getInfrastructureProviderIds(
			@PathParam("serviceId") String serviceId) {
		LOGGER.debug("ServiceManagerREST: getInfrastructureProviderIds() called for serviceId: " + serviceId);
		
		try {
			String infraProviderIds = serviceManager
					.getInfrastructureProviderIds(serviceId);
			return infraProviderIds;
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Returns the list of SLAs for a given service.
	 * <p/>
	 * This operations is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/slas</code>
	 * 
	 * @param serviceId
	 *            the id of the service whose SLAs are to be returned
	 * 
	 * @return a String[] containing the slas
	 */
	@GET
	@Path("/services/{serviceId}/ip/slas")
	public String getSlas(@PathParam("serviceId") String serviceId) {
		LOGGER.debug("ServiceManagerREST: getSlas() called for serviceId: " + serviceId);
		try {
			String slaIds = serviceManager.getSlaIds(serviceId);
			return slaIds;
		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			// Throw an error 404 (not found) if the resource does not exist
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Returns a infrastructure provider related SLA for a given service.
	 * <p/>
	 * This operations is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/slas</code>
	 * 
	 * @param serviceId
	 *            the id of the service whose infrastructure provider ids are to
	 *            be returned
	 * @param ipId
	 *            the id of the service whose infrastructure provider ids are to
	 *            be returned
	 * 
	 * @return a String[] containing the slas
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/sla")
	public String getSla(@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getSlas() called for serviceId: " + serviceId + " with ipId: " + ipId);
		String slaId;
		try {
			slaId = serviceManager.getSlaId(serviceId, ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return slaId;
	}

	/**
	 * Returns the initial trust value of an infrastructure provider.
	 * <p/>
	 * This operation is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/initialtrustvalue</code>
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the infrastructure provider id
	 * 
	 * @return initial trust value
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/initialtrustvalue")
	public String getInitialTrustValue(
			@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getInitialTrustValue() called for serviceId: " + serviceId + " with ipId: " + ipId);
		float initialTrustValue;
		try {
			initialTrustValue = serviceManager.getInitialTrustValue(serviceId,
					ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Float.toString(initialTrustValue);
	}

	/**
	 * Returns the initial risk value of an infrastructure provider.
	 * <p/>
	 * This operation is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/initialriskvalue</code>
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the infrastructure provider id
	 * 
	 * @return initial risk value
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/initialriskvalue")
	public String getInitialRiskValue(@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getInitialRiskValue() called for serviceId: " + serviceId + " with ipId: " + ipId);
		float initialRiskValue;
		try {
			initialRiskValue = serviceManager.getInitialRiskValue(serviceId,
					ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Float.toString(initialRiskValue);
	}

	/**
	 * Returns the initial eco value of an infrastructure provider.
	 * <p/>
	 * This operation is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/initialecovalue</code>
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the infrastructure provider id
	 * 
	 * @return initial eco value
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/initialecovalue")
	public String getInitialEcoValue(@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getInitialEcoValue() called for serviceId: " + serviceId + " with ipId: " + ipId);
		float initialEcoValue;
		try {
			initialEcoValue = serviceManager
					.getInitialEcoValue(serviceId, ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Float.toString(initialEcoValue);
	}

	/**
	 * Returns the initial cost value of an infrastructure provider.
	 * <p/>
	 * This operation is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/initialcostvalue</code>
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the infrastructure provider id
	 * 
	 * @return initial cost value
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/initialcostvalue")
	public String getInitialCostValue(@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getInitialCostValue() called for serviceId: " + serviceId + " with ipId: " + ipId);
		float initialCostValue;
		try {
			initialCostValue = serviceManager.getInitialCostValue(serviceId,
					ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return Float.toString(initialCostValue);
	}

	/**
	 * Returns the agreement endpoint of an infrastructure provider for a given
	 * service.
	 * <p/>
	 * This operation is exposed through this URL:
	 * <p/>
	 * <code>/services/{serviceId}/ip/agreementendpoint</code>
	 * 
	 * @param serviceId
	 *            the id of the service
	 * 
	 * @param ipId
	 *            the infrastructure provider id
	 * 
	 * @return a String containing the agreement endpoint
	 */
	@GET
	@Path("/services/{serviceId}/{ipId}/agreementendpoint")
	public String getAgreementEndpoint(
			@PathParam("serviceId") String serviceId,
			@PathParam("ipId") String ipId) {
		LOGGER.debug("ServiceManagerREST: getAgreementEndpoint() called for serviceId: " + serviceId + " with ipId: " + ipId);
		String agreementEndpoint;
		try {
			agreementEndpoint = serviceManager.getAgreementEndpoint(serviceId,
					ipId);

		} catch (ItemNotFoundException itemNotFoundException) {
			LOGGER.error("ServiceManagerREST: Cannot get infrastructure provider ids for service with id '"
					+ serviceId + "': " + itemNotFoundException.getMessage());
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return agreementEndpoint;
	}

	@GET
	@Produces("application/xml")
	@Path("/services")
	public String getServices() {
		LOGGER.debug("ServiceManagerREST: getServices() called");
		ArrayList<ServiceDocument> services = serviceManager.getServices();

		StringBuffer result = new StringBuffer();

		result.append("<services>\n");
		for (int i = 0; i < services.size(); i++) {
			result.append(services.get(i).toString());
		}
		result.append("</services>\n");

		return result.toString();
	}

	@GET
	@Produces("text/html")
	@Path("/services")
	public String getServicesHtml() {
		LOGGER.debug("ServiceManagerREST: getServicesHtml() called");
		ArrayList<ServiceDocument> services = serviceManager.getServices();

		StringBuffer htmlString = new StringBuffer();

		htmlString
				.append("<table border=\"1\" cellpadding=\"3\" style=\" font-family:sans-serif; font-size:10pt; \">\n"
						+ "<tr>\n"
						+ "\t<td><B>Service ID</B></td>\n"
						+ "\t<td><B>Status</B></td>\n"
						+ "\t<td><B>Link</B></td>\n"
						+ "\t<td><B>IP ID</B></td>\n"
						+ "\t<td><B>IP domain/address</B></td>\n"
						+ "\t<td><B>VM List</B></td>\n" + "</tr>\n");

		for (int i = 0; i < services.size(); i++) {
			Service service = services.get(i).getService();

			htmlString.append("<tr>\n");
			htmlString.append("<td>" + service.getServiceId() + "</td>\n"
					+ "<td>" + service.getStatus() + "</td>\n" + "<td>"
					+ "/ServiceManager/services/" + service.getServiceId()
					+ "</td>\n");

			InfrastructureProvider[] infrastructureProviders = service
					.getInfrastructureProviderArray();

			if (infrastructureProviders != null
					&& infrastructureProviders.length > 0) {
				// Create a sub-table for the infra providers
				htmlString.append("<table>");
				for (InfrastructureProvider infrastructureProvider : infrastructureProviders) {
					// Each infra provider is a table row
					htmlString.append("<tr>");
					htmlString.append("<td>" + infrastructureProvider.getId()
							+ "</td>\n" + "<td>"
							+ infrastructureProvider.getIpAddress() + "</td>\n"
							+ "<td>" + infrastructureProvider.getSlaId()
							+ "</td>\n");
					htmlString.append("<td>\n");
					htmlString.append("<ul>\n");
					if (infrastructureProvider.getVms() != null) {
						Vm[] vms = infrastructureProvider.getVms().getVmArray();
						for (int j = 0; j < vms.length; j++) {
							htmlString.append("<li>" + vms[j].getId()
									+ "</li>\n");
						}
					}
					htmlString.append("</ul>\n");
					htmlString.append("</td>\n");
					htmlString.append("</tr>");
					// End of infra provider printing
				}
				// End of infra providers printing
				htmlString.append("</table>");
			} else {
				htmlString
						.append("<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
			}

			htmlString.append("</tr>\n");
		}

		htmlString.append("</table>\n");
		return htmlString.toString();
	}
}
