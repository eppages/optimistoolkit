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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.tf.ip.service.IPDeployment;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.thread.TrustTimerIP;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

@Path("/deploy/ip")
public class DeploymentIPtrustAPI {

	Logger log = Logger.getLogger(this.getClass().getName());
	
	private final String PROVIDERS_TYPE = "SP";
	
	IPDeployment ipd;
	int scale = 1;
	public DeploymentIPtrustAPI(){
		PropertyConfigurator.configure(PropertiesUtils.getConfigFilePath("LOG"));
		TrustTimerIP ttip = TrustTimerIP.instance();
		ipd = new IPDeployment();
		scale = Integer.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
	}
		
	/**
	 * Provides the trust level for the provider asked
	 * @param entityId
	 * @return trust level
	 */
	@GET
	@Path("/getTrust/{providerId}")
	public String getTrust(@PathParam("providerId") String providerId){
		int trustvalue = 5;
		log.info("getTrust of "+providerId+" = " + trustvalue);
//		return String.valueOf(trustvalue);
		return String.valueOf(ipd.getTrust(providerId)* scale);
	}
	
	/**
	 * Provides the trust level for the provider asked
	 * @param entityId
	 * @return social network trust level
	 */
	@GET
	@Path("/sn/getTrust/{providerId}")
	public String getTrustSN(@PathParam("providerId") String providerId){
		int trustsnvalue = 4;
		log.info("getTrust of "+providerId+" = " + trustsnvalue);
//		return String.valueOf(trustsnvalue);
		return String.valueOf(ipd.getTrustSN(providerId)*scale);
	}		
		
	/**
	 * Provides the trust level for the provider asked
	 * @param entityId
	 * @return social network trust level
	 */
	@GET	
	@Path("/getSelfAssessment/{providerId}")
	public String getSelfAssessment(@PathParam("providerId") String providerId){
		log.info("manifest for HM assessment received");
		return String.valueOf(ipd.getSelfAssessment(providerId));
	}

}
