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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import eu.optimis.common.trec.db.sp.TrecServiceInfoDAO;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.tf.sp.service.SPCommon;
import eu.optimis.tf.sp.service.thread.TrustTimerSP;
import eu.optimis.tf.sp.service.utils.GetSPManifestValues;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

@Path("/common/sp")
public class CommonSPAPI {

	Logger log = Logger.getLogger(this.getClass().getName());

	// NPMIC npmic = null;

	private final String PROVIDERS_TYPE = "IP";

	private SPCommon spc;
	private ResourceBundle rb;

	public CommonSPAPI() {
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		spc = new SPCommon();
		TrustTimerSP tt = TrustTimerSP.instance();
		rb = ResourceBundle.getBundle("trustframework", Locale.getDefault());
	}

	/**
	 * Provides the trust rank of the providers in the trust framework
	 * 
	 * @return the trust rank of the entities
	 */
	@GET
	@Path("/rank/")
	public String getRank() {
		return "<rank/>";
	}

	/**
	 * Adds a new entity to the trust system
	 * 
	 * @param username
	 * @param certificate
	 * @return true if the user has been added / false if not
	 */
	@POST
	@Path("/slainfo")
	public String provideSLAInfo(String sla) {
		log.debug(sla);
		return String.valueOf(true);
	}

	/**
	 * Informs the trust framework that a service has been deployed, this is
	 * also added to the social netowrk and monitoring list.
	 * 
	 * @param Servic
	 *            Manifset
	 * @return true if the user has been added / false if not
	 */
	@POST
	@Path("/servicedeployed")
	public String ServiceDeployed(String ServiceManifest) {
		boolean production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		boolean spdeployment = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","st.deploy"));

		if (spdeployment) {
			String stsid = PropertiesUtils.getProperty("TRUST","st.sid");
			TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
			try {
				tsidao.updateDeployed(stsid, true);
			} catch (Exception e) {
				log.error("service not started tsid");
			}
		}

		// log.info(ServiceManifest);
		// log.info("production?? "+production);
		String serviceId;
		// NPservicesDAO npsdao = new NPservicesDAO();
		try {
			log.info(ServiceManifest);
			GetSPManifestValues gspmv = new GetSPManifestValues();
			Manifest mani = gspmv.stringManifest2Manifest(ServiceManifest);
			serviceId = mani.getVirtualMachineDescriptionSection()
					.getServiceId();
			log.info(serviceId);
			/*
			 * NON PRODUCTION TIER
			 */
			if (!production) {

				// npmic = new NPMIC(serviceId);
				// npmic.setAlive(true);
				// try{
				// npsdao.addService(serviceId);
				// log.info("service added gathering information");
				// npmic.run();
				// return String.valueOf(true);
				// } catch (Exception e){
				// if
				// (serviceId.equalsIgnoreCase(npsdao.getService(serviceId).getServiceId())){
				// log.info("service already started gathering information");
				// npmic.run();
				// return String.valueOf(true);
				// } else {
				// return String.valueOf(false);
				// }
				// }
				return String.valueOf(production);
				/*
				 * PRODUCTION TIER
				 */
			} else {
				boolean deployed = spc.ServiceDeployed(ServiceManifest);
				return String.valueOf(deployed);
			}

		} catch (Exception e) {
			log.error("service not started");
			return String.valueOf(false);
		}

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
		boolean production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));

		boolean spdeployment = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","st.deploy"));

		if (spdeployment) {
			String stsid = PropertiesUtils.getProperty("TRUST","st.sid");
			TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
			try {
				tsidao.updateDeployed(stsid, false);
			} catch (Exception e) {
				log.error("service not started tsid");
			}
		}

		try {
			if (!production) {
				// NPservicesDAO npsdao = new NPservicesDAO();
				// npmic.setAlive(false);
				// npsdao.deleteService(serviceId);
				return String.valueOf(production);
			} else {
				boolean undeployed = spc.ServiceUndeployed(serviceId);
				return String.valueOf(undeployed);
			}
		} catch (Exception e) {
			log.error("service not started");
			return String.valueOf(false);
		}
	}

	/**
	 * Trust framework pro-active notification service
	 */
	public void trustNotification() {

	}

	private void setNPMonitoringThread(String serviceId) {
		// npmic = new NPMIC(serviceId);
	}

}
