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

package eu.optimis.tf.sp.service;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.sp.TrecIPinfoDAO;
import eu.optimis.common.trec.db.sp.TrecSLADAO;
import eu.optimis.common.trec.db.sp.TrecSPinfoDAO;
import eu.optimis.common.trec.db.sp.TrecServiceComponentDAO;
import eu.optimis.common.trec.db.sp.TrecServiceInfoDAO;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.tf.sp.service.clients.ServiceMgrClient;
import eu.optimis.tf.sp.service.np.NPMIC;
import eu.optimis.tf.sp.service.utils.GetSPManifestValues;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.ServiceInfo;

public class SPCommon {

	Logger log = Logger.getLogger(this.getClass().getName());
	private boolean production = true;
	private NPMIC npmic = null;

	public SPCommon() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST", "production"));
	}

	public boolean ServiceDeployed(String ServiceManifest) {
		// log.info(ServiceManifest);
		// log.info("production?? "+production);
		String serviceId;
		// NPservicesDAO npsdao = new NPservicesDAO();
		try {
			// serviceId = ServiceManifestXMLProcessor.getAttribute(
			// ServiceManifest, "VirtualMachineDescription", "serviceId");
			GetSPManifestValues gspmv = new GetSPManifestValues();
			Manifest mani = gspmv.stringManifest2Manifest(ServiceManifest);
			serviceId = mani.getVirtualMachineDescriptionSection().getServiceId();
			log.info("Trust deployed service: "+ serviceId);
			/*
			 * NON PRODUCTION TIER
			 */
			if (!production) {
				// npmic = new NPMIC(serviceId);
				// npmic.setAlive(true);
				// try {
				// npsdao.addService(serviceId);
				// log.info("service added gathering information");
				// npmic.run();
				// return true;
				// } catch (Exception e) {
				// if (serviceId.equalsIgnoreCase(npsdao.getService(serviceId)
				// .getServiceId())) {
				// log.info("service already started gathering information");
				// npmic.run();
				// return true;
				// } else {
				// return false;
				// }
				// }
				return production;
				/*
				 * PRODUCTION TIER
				 */
			} else {
				if (existService(serviceId)) {
					return updateDeployed(serviceId, true);
				} else {
					boolean serviceAdded = false;
					serviceAdded = addService(ServiceManifest)
							&& addIP(serviceId) && addSP(serviceId)
							&& addSLA(serviceId);
					return serviceAdded;
				}
			}

		} catch (Exception e) {
			log.error("service not deployed for TRUST");
			return false;
		}
	}

	private boolean addSP(String serviceId) {
		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		TrecSPinfoDAO tsidao = new TrecSPinfoDAO();
		String spId = "";
		try {
			ServiceInfo si = tsiDAO.getService(serviceId);
			spId = si.getSpId();
			tsidao.getSP(spId);
			return true;
		} catch (Exception e) {
			try {
				return tsidao.addSP(spId, spId);
			} catch (Exception e1) {
				return false;
			}
		}
	}

	// adding IP to the data base
	private boolean addIP(String serviceId) {
		ServiceMgrClient smc = new ServiceMgrClient();
		List<String> ipIds = smc.getIPIDs(serviceId);
		TrecIPinfoDAO tiidao = new TrecIPinfoDAO();
		boolean ipadded = false;
		for (String ipId : ipIds) {
			try {
				tiidao.getIP(ipId);
				ipadded = true;
			} catch (Exception e) {
				try {
					return tiidao.addIp(ipId, ipId, null);
				} catch (Exception e1) {
					ipadded = false;
				}
			}
		}
		return ipadded;
	}

	// adding service to the data base
	private boolean addService(String serviceManifest) {
		GetSPManifestValues gspmv = new GetSPManifestValues();
		Manifest mani = gspmv.stringManifest2Manifest(serviceManifest);
		String serviceId = gspmv.getServiceId();
		String spId = gspmv.getServiceProviderId();

		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		try {
			Boolean serviceAdded = tsiDAO.addService(serviceId, spId,
					serviceManifest, true);
			Boolean componentAdded = addServiceComponent(serviceId,
					serviceManifest);
			return serviceAdded && componentAdded;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean addServiceComponent(String serviceId, String serviceManifest) {
		TrecServiceComponentDAO tscdao = new TrecServiceComponentDAO();
		try {
			return tscdao.addComponentId(serviceId, serviceId, serviceManifest);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean addSLA(String serviceId) {
		ServiceMgrClient smc = new ServiceMgrClient();
		List<String> ipIds = smc.getIPIDs(serviceId);
//		log.info("=========================== "+ipIds.size());
		TrecSLADAO tsladao = new TrecSLADAO();
		boolean addedslas = false;
		try {
			for (String ipId : ipIds) {
				addedslas = false;
//				log.info("=========================== "+ipId);
				String slaId = smc.getSLAIDbyIP(serviceId, ipId);
				tsladao.addIp(serviceId, ipId, slaId);
				addedslas = true;
			}
			return addedslas;
		} catch (Exception e) {
			return addedslas;
		}
	}

	private boolean existService(String serviceId) {
		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		try {
			tsiDAO.getService(serviceId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean updateDeployed(String serviceId, boolean deployed) {
		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		try {
			ServiceInfo si = tsiDAO.getService(serviceId);
			Boolean isDeployed = si.getDeployed();
			if (isDeployed) {
				return isDeployed;
			} else {
				return tsiDAO.updateDeployed(serviceId, deployed);
			}
		} catch (Exception e) {
			return false;
		}
	}

	public boolean ServiceUndeployed(String serviceId) {
		try {
			if (!production) {
				// NPservicesDAO npsdao = new NPservicesDAO();
				// npmic.setAlive(false);
				// npsdao.deleteService(serviceId);
				return true;
			} else {
				return updateDeployed(serviceId, false);
			}
		} catch (Exception e) {
			log.error("service not undeployed for TRUST");
			return false;
		}
	}

}
