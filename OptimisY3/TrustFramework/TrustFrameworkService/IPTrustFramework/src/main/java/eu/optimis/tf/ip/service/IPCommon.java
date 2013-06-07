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

package eu.optimis.tf.ip.service;

import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecIPinfoDAO;
import eu.optimis.common.trec.db.ip.TrecSLADAO;
import eu.optimis.common.trec.db.ip.TrecSPinfoDAO;
import eu.optimis.common.trec.db.ip.TrecServiceComponentDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.tf.ip.service.clients.ServiceMgrClient;
import eu.optimis.tf.ip.service.utils.GetIPManifestValues;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;

public class IPCommon {

	Logger log = Logger.getLogger(this.getClass().getName());
	private boolean production = true;
//	private NPMIC npmic = null;

	public IPCommon() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
	}

	public boolean ServiceDeployed(String ServiceManifest) {
		// log.info(ServiceManifest);
		// log.info("production?? "+production);
		String serviceId;
//		NPservicesDAO npsdao = new NPservicesDAO();
		try {
			// serviceId = ServiceManifestXMLProcessor.getAttribute(
			// ServiceManifest, "VirtualMachineDescription", "serviceId");
			GetIPManifestValues gipmv = new GetIPManifestValues();
			Manifest mani = gipmv.stringManifest2Manifest(ServiceManifest);
			serviceId = mani.getVirtualMachineDescriptionSection().getServiceId();

			/*
			 * NON PRODUCTION TIER
			 */
			if (!production) {
				System.out.println ("NOT IN PRODUCTION!! -> " + serviceId);
//				npmic = new NPMIC(serviceId);
//				npmic.setAlive(true);
//				try {
//					npsdao.addService(serviceId);
//					log.info("service added gathering information");
//					npmic.run();
//					return true;
//				} catch (Exception e) {
//					if (serviceId.equalsIgnoreCase(npsdao.getService(serviceId)
//							.getServiceId())) {
//						log.info("service already started gathering information");
//						npmic.run();
//						return true;
//					} else {
//						return false;
//					}
//				}
				return production;
				/*
				 * PRODUCTION TIER
				 */
			} else {
				if (existService(serviceId)) {
					System.out.println ("In production and service exists -> " + serviceId);
					return updateDeployed(serviceId, true);
				} else {
					System.out.println ("In production and service doesn't exist -> " + serviceId);
					boolean serviceAdded = false;
					serviceAdded = addService(ServiceManifest)
							& addIP(serviceId) & addSP(serviceId);
							//&& addSLA(serviceId); 
					return serviceAdded;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Service not deployed for TRUST");
			return false;
		}
	}

	private boolean addSP(String serviceId) {
		log.debug("Adding SP... ");
		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		TrecSPinfoDAO tsidao = new TrecSPinfoDAO();
		String spId = "";
		try {
			log.debug("Adding SP for service " + serviceId);
			ServiceInfo si = tsiDAO.getService(serviceId);
			spId = si.getSpId();
			tsidao.getSP(spId);
			return true;
		} catch (Exception e) {
			try {
				return tsidao.addSP(spId, spId);
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error("SP not added!");
				log.error(e1.getMessage());
				return false;
			}
		}
	}

	// adding IP to the data base
	private boolean addIP(String serviceId) {	
		log.debug("Adding IP for service...");
		ServiceMgrClient smc = new ServiceMgrClient();
		List<String> ipIds = smc.getIPIDs(serviceId);
		TrecIPinfoDAO tiidao = new TrecIPinfoDAO();
		boolean ipadded = false;
		log.debug("Adding IP for service " + serviceId);
		for (String ipId : ipIds) {
			try {
				tiidao.getIP(ipId);
				ipadded = true;
			} catch (Exception e) {
				try {
					return tiidao.addIp(ipId, ipId, null);
				} catch (Exception e1) {
					e1.printStackTrace();
					log.error("IP not added!");
					log.error(e1.getMessage());
					ipadded = false;
				}
			}
		}
		return ipadded;
	}

	// adding service to the data base
	private boolean addService(String serviceManifest) {
		GetIPManifestValues gipmv = new GetIPManifestValues();
		Manifest mani = gipmv.stringManifest2Manifest(serviceManifest);
		String serviceId = mani.getVirtualMachineDescriptionSection().getServiceId();
		String spId = gipmv.getServiceProviderId();

		TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
		try {
			Boolean serviceAdded = tsiDAO.addService(serviceId, spId, serviceManifest,true);
			Boolean componentAdded = addServiceComponent(serviceId, serviceManifest);
			Boolean slaAdded = addSLA(serviceId);
			return serviceAdded && componentAdded && slaAdded;
		} catch (Exception e) {
			log.error("Service not added!");
			log.error(e.getMessage());
			return false;
		}
	}
	
	private boolean addServiceComponent(String serviceId, String serviceManifest){
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
		TrecSLADAO tsladao = new TrecSLADAO();
		try {
			for (String ipId : ipIds) {
				String slaId = smc.getSLAIDbyIP(serviceId, ipId);
				tsladao.addIp(serviceId, ipId, slaId);
			}
			return true;
		} catch (Exception e) {
			return false;
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
//				NPservicesDAO npsdao = new NPservicesDAO();
//				npmic.setAlive(false);
//				npsdao.deleteService(serviceId);
				return true;
			} else {
				return updateDeployed(serviceId, false);
			}
		} catch (Exception e) {
			log.error("Service not undeployed for TRUST");
			return false;
		}
	}

	public String getServiceManifest(String serviceId) {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {
			return tsidao.getService(serviceId).getServiceManifest();
		} catch (Exception e) {
			log.error("Service manifest not found for "+serviceId);
			return null;
		}
	}

}
