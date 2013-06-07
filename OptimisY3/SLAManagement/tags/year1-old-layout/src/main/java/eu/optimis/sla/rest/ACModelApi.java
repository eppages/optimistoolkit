package eu.optimis.sla.rest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/model")
public interface ACModelApi {

	/**
	 * Perform optimum allocation 
	 * @param Optimization Model
	 * @param Service Manifest
	 * @return Allocation offer 
	 */
	@POST
	@Path("/admissionControl")
	public String admissionControl(@FormParam("opModel") String opModel, @FormParam("serviceManifest") String serviceManifest);
	
	/**
	 * Perform admission control on the services that comprise the given manifest
	 * @param Service Manifest
	 * @return Allocation offer
	 */
	@POST
	@Path("/performACTest")
	//@Consumes("application/xml") 
	public String performACTest(String serviceManifest);
	
	/**
	 * Perform workload analysis
	 * @param Service Manifest
	 * @return Workload analysis
	 */
	@POST
	@Path("/performWorkloadAnalysis")
	public String performWorkloadAnalysis(String serviceManifest);
	
	/**
	 * Generates optimization model 
	 * @param Workload analysis
	 * @param TREC factors
	 * @return Optimization Model
	 */
	@POST
	@Path("/createModel")
	public String createModel(String wlanalysis, String trec);
	
	/**
	 * Provides the trust rank of the entities that use the trust framework
	 * @return TREC factors
	 */
	@GET
	@Path("/getTRECFactors")
	public String getTRECFactors();

	/**
	 * Ask the monitoring system for the data of the resources in the list for a certain period 
	 * @param resources list
	 * @param period
	 * @return statistics between the two dates for a given entity
	 */
	@GET
	@Path("/getMonitoringData/{period}")
	public String getMonitoringData (String resources, @PathParam("period") String period);
}
