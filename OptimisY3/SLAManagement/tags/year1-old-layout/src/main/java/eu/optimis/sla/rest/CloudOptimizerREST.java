package eu.optimis.sla.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Cloud Optimizer REST API
 * 
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 */

@Path("/")
public interface CloudOptimizerREST  {
	
		
	/**
	 * IP external interface to deploy a service
	 * @param allocationOffer
	 * @return the serviceId
	 */
	@POST
	@Path("/service")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String deploy(String allocationOffer);
	
	/**
	 * IP external interface to undeploy a service
	 * @param serviceId
	 */
	@DELETE
	@Path("/service/{serviceid}")
	public void undeploy(@PathParam("serviceid") String serviceId);
	
	@GET
	@Path("/service/{serviceid}/{imageid}/instances")
	public int getNrInstances(@PathParam("serviceid") String serviceId, @PathParam("imageid") String imageId);
	
	/**
	 * Allows adding new VMs
	 * @param serviceId
	 * @param serviceManifest
	 * @param num
	 */
	@POST
	@Path("/service/{serviceid}/{imageid}")
	@Consumes(MediaType.APPLICATION_XML)
	public void addVM (@PathParam("serviceid") String serviceId, String serviceManifest, @PathParam("imageid") String imageId, @QueryParam("num") String num);
	
	/**
	 * Allows removing VMs
	 * @param serviceId
	 */
	@DELETE
	@Path("/service/{serviceid}/{imageid}")
	public void removeVM (@PathParam("serviceid") String serviceId, @PathParam("imageid") String imageId, @QueryParam("num") String num);
	
	/**
	 * Allows migrating a VM
	 * @param vmId
	 */
	@POST
	@Path("/vm/{vmid}/migrate")
	public void migrateVM (@PathParam("vmid") String vmId);
	
	/**
	 * Allows restarting a VM
	 * @param vmId
	 */
	@POST
	@Path("/vm/{vmid}/restart")
	public void restartVM (@PathParam("vmid") String vmId);
	
	/**
	 * Adds a new policy
	 * @param policyId
	 * @param policyRules
	 */
	@POST
	@Path("/policy")
	@Consumes(MediaType.APPLICATION_XML)
	public void addPolicy (@QueryParam("policyid") String policyId, String policyRules);
	
	/**
	 * Updates an already existing policy
	 * @param policyId
	 * @param newPolicyRules
	 */
	@PUT
	@Path("/policy/{policyid}")
	@Consumes(MediaType.APPLICATION_XML)
	public void updatePolicy (@PathParam("policyid") String policyId, String newPolicyRules);
	
	/**
	 * Removes an already existing policy
	 * @param policyId
	 */
	@DELETE
	@Path("/policy/{policyid}")
	public void removePolicy (@PathParam("policyid") String policyId);
	
	@GET
	@Path("/vm")
	@Consumes(MediaType.APPLICATION_XML)
	public String getVMsIds ();
	
}