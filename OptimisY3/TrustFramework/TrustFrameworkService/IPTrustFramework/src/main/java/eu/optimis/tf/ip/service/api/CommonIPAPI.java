/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.api;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.tf.ip.service.IPCommon;
import eu.optimis.tf.ip.service.IPDeployment;
import eu.optimis.tf.ip.service.hm.trust.TrustForecaster;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.thread.TrustTimerIP;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

@Path("/common/ip")
public class CommonIPAPI {

	private int scale = 1;
	private IPCommon ipc = new IPCommon();
	private IPDeployment ipd;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public CommonIPAPI() {
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		TrustTimerIP ttip = TrustTimerIP.instance();
		scale = Integer.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
		ipd = new IPDeployment();
	}
	
	/**
	 * Provides the trust rank of the providers in the trust framework
	 * @return the trust rank of the entities
	 */
	@GET
	@Path("/rank/")
	public String getRank(){
		return "<rank/>";
	}
	
	/**
	 * Informs the trust framework that a service has been deployed, 
	 * this is also added to the social netowrk and monitoring list.
	 * 
	 * @param Servic Manifset
	 * @return true if the user has been added / false if not
	 */
	@POST
	@Path("/servicedeployed")
	public String ServiceDeployed(String ServiceManifest) {
		return String.valueOf(ipc.ServiceDeployed(ServiceManifest));
	}
	
	/**
	 * Delete a new service from the trust framework 
	 * 
	 * @param serviceId
	 * @return true if the user has been deleted / false if not
	 */
	@DELETE
	@Path("/serviceundeployed/{serviceId}")
	public String ServiceUndeployed(@PathParam("serviceId") String serviceId) {
		return String.valueOf(ipc.ServiceUndeployed(serviceId));
	}
	
	/**
	 * Trust framework proactive notification service. It creates an alert for the given entity (service or provider)
	 * @param entityId Entity for which the alert is set
	 * @param threshold Limit for the trust value. If trust goes below the value, a notification is sent to the HM
	 * @param type Type of entity: provider (0) or service (1) 
	 * @return Nothing is returned
	 */
	@PUT
	@Path("/setproactivetrustAssessor/{entityId}")
	public String setProactiveTrustAssessor(@PathParam("entityId")String entityId, @QueryParam("threshold")double threshold, @QueryParam("type")int type)
	{
		TrustTimerIP ttip = TrustTimerIP.instance();
		ttip.subscribeAlert(threshold, entityId, type);	
		return new Boolean(true).toString();
	}
	
	/**
	 * Delete an existing alert 
	 * 
	 * @param entityId
	 * @return Nothing is returned
	 */
	@DELETE
	@Path("/stopproactivetrust/{entityId}")
	public String stopProactiveTrust (@PathParam("entityId") String id)
	{
		TrustTimerIP ttip = TrustTimerIP.instance();
		ttip.unSubscribeAlert(id, TrustTimerIP.SERVICE);
		return new Boolean(true).toString();
	}
	
	@GET
	@Path("/getmanifest/{serviceId}")
	public String getRank(@PathParam("serviceId") String serviceId){
		return ipc.getServiceManifest(serviceId);
	}
	
	/**************** APIs for Forecasting Trust according to HM requirements ***************/
	
	/**
	 * Provides the trust level forecast for the service asked
	 * @param serviceId
	 * @param timespan
	 * @return trust level forecast in the next timespan calculation
	 */
	@GET
	@Path("/forecast/forecastServiceTrust/{serviceId}")
	public String forecastServiceTrust(@PathParam("serviceId") String serviceId, @QueryParam("timespan")int timeSpan)
	{		
		TrustForecaster forecaster = new TrustForecaster();
		double forecast = forecaster.forecastServiceTrust(serviceId, timeSpan);
		return String.valueOf(forecast);
	}
	
	/**
	 * Provides the trust level forecast for the provider asked
	 * @param providerId
	 * @param timespan
	 * @return trust level forecast in the next timespan calculation
	 */
	@GET
	@Path("/forecast/forecastIPTrust/{providerId}")
	public String forecastIPTrust(@PathParam("providerId") String providerId, @QueryParam("timespan")int timeSpan)
	{		
		TrustForecaster forecaster = new TrustForecaster();
		double forecast = forecaster.forecastIPTrust(providerId, timeSpan);
		return String.valueOf(forecast);
	}
	
	/**
	 * Provides the trust level forecast for the new service with the indicated manifest
	 * @param manifest
	 * @return Forecasted trust level
	 */
	@POST	
	@Path("/forecast/forecastServiceDeployment")
	public String forecastServiceDeployment(String manifest){
		log.info("Manifest for HM assessment received");
		log.info("Provide trust forecast for the new service");
		return String.valueOf(ipd.getHMAssessment(manifest));
	}
	
	/**
	 * Provides the trust level forecast for the IP if a new service is deployed with the indicated manifest
	 * @param manifest
	 * @return Forecasted trust level
	 */
	@POST	
	@Path("/forecast/forecastServiceDeploymentIP")
	public String forecastServiceDeploymentIP(String manifest){
		log.info("Manifest for HM assessment received");
		log.info("Provide trust forecast for the new service");
		return String.valueOf(ipd.getHMAssessment(manifest));
	}
	
	/**
	 * Provides the trust level forecast for the service asked if a new VM is created
	 * @param serviceId	 
	 * @return trust level forecast if a new VM is created
	 */
	@GET
	@Path("/forecast/forecastVMDeployment/{serviceId}")
	public String forecastVMDeployment(@PathParam("serviceId") String serviceId)
	{		
		log.info("Provide trust forecast for the service "+serviceId+ " when a new VM is developed.");
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, 1);
		return String.valueOf(forecast*scale);
	}
	
	/**
	 * Provides the trust level forecast for the IP if a new VM is created for a service
	 * @param serviceId	 
	 * @return trust level forecast if a new VM is created
	 */
	@GET
	@Path("/forecast/forecastVMDeploymentIP/{serviceId}")
	public String forecastVMDeploymentIP(@PathParam("serviceId") String serviceId)
	{		
		log.info("Provide trust forecast for the service "+serviceId+ " when a new VM is developed.");
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, 1);
		return String.valueOf(forecast*scale);
	}
	
	/**
	 * Provides the trust level forecast for the service asked if a VM is removed
	 * @param serviceId	 
	 * @return trust level forecast if a VM is removed
	 */
	@GET
	@Path("/forecast/forecastVMCancellation/{serviceId}")
	public String forecastVMCancellation(@PathParam("serviceId") String serviceId)
	{		
		log.info("Provide trust forecast for the service "+serviceId+ " when a new VM is developed.");
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, 1);
		return String.valueOf(forecast*scale);
	}
	
	/**
	 * Provides the trust level forecast for the IP if a VM is removed for a service
	 * @param serviceId	 
	 * @return trust level forecast if a VM is removed
	 */
	@GET
	@Path("/forecast/forecastVMCancellationIP/{serviceId}")
	public String forecastVMCancellationIP(@PathParam("serviceId") String serviceId)
	{		
		log.info("Provide trust forecast for the service "+serviceId+ " when a new VM is developed.");
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, 1);
		return String.valueOf(forecast*scale);
	}
	
	private void setNPMonitoringThread(String serviceId){
//		npmic = new NPMIC(serviceId);
	}
}
