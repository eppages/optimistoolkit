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
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.api;

import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.tf.sp.service.SPDeployment;
import eu.optimis.tf.sp.service.thread.TrustTimerSP;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

@Path("/deploy/sp")
public class DeploymentSPtrustAPI {

	Logger log = Logger.getLogger(this.getClass().getName());

	int scale = 1;
	SPDeployment spd;
	boolean production;

	public DeploymentSPtrustAPI() {
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		spd = new SPDeployment();
		scale = Integer.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		TrustTimerSP tt = TrustTimerSP.instance();
	}

	/**
	 * Provides the trust level for the provider asked
	 * 
	 * @param entityId
	 * @return trust level
	 */
	@GET
	@Path("/getTrust/{providerId}")
	public String getTrust(@PathParam("providerId") String providerId) {
//		production = Boolean.valueOf(PropertiesUtils.getBoundle("production"));
//		log.info("production boundle = "+ PropertiesUtils.getBoundle("production"));
//		log.info("production value = "+production);
//		if (production) {
			double trustvalue = spd.getTrust(providerId) * scale;
			log.info("getTrust of " + providerId + " = " + trustvalue);
			return String.valueOf(trustvalue);
//		} else {
//			if (providerId.equalsIgnoreCase(PropertiesUtils.getBoundle("atos.id"))){
//				return "2.0";
//			} else if(providerId.equalsIgnoreCase(PropertiesUtils.getBoundle("flex.id"))){ 
//				return "5.0";
//			} else if(providerId.equalsIgnoreCase(PropertiesUtils.getBoundle("dummy.id"))){
//				return "5.0";
//			} else if(providerId.equalsIgnoreCase(PropertiesUtils.getBoundle("ip4.id"))){
//				return "1.0";
//			} else if(providerId.equalsIgnoreCase(PropertiesUtils.getBoundle("ip5.id"))){
//				return "1.0";
//			}else{
//				return "3.0";
//			}
//		}
	}
	
	/**
	 * Provides the trust level for the provider asked
	 * 
	 * @param entityId
	 * @return social network trust level
	 */
	@GET
	@Path("/sn/getTrust/{providerId}")
	public String getTrustSN(@PathParam("providerId") String providerId) {
		int trustsnvalue = 4;
		log.info("getTrust of " + providerId + " = " + trustsnvalue);
		return String.valueOf(spd.getTrustSN(providerId) * scale);
	}

	/**
	 * Provides the trust level for the provider asked
	 * 
	 * @param entityId
	 * @return trust level
	 */
	@POST
	@Path("/getIPassessment/")
	public String getIPAssessment(String serviceManifest) {
		log.info("getIPAssessment");
		return spd.getIPAssessment(serviceManifest);
	}
	
	private int nProdValue(){
		Random generator = new Random();
		return generator.nextInt(5);
	}

}
