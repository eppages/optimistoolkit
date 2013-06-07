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
Mariam Kiran. University of Leeds
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.operators;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.manifest.api.ip.DataProtectionSection;
import eu.optimis.manifest.api.ip.ElasticityRule;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.tf.ip.service.utils.ServiceManifestXMLProcessor;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;

public class GetIPManifestValues {

	// ManifestParser parser;

	protected final static Logger log = Logger
			.getLogger(GetIPManifestValues.class);

	Manifest ipManifest;
	private boolean production = false;

	public GetIPManifestValues(){}
	
	public GetIPManifestValues(String serviceId) {
		super();
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		stringManifest2Manifest(getServiceManifest(serviceId));
	}

	private String getServiceManifest(String serviceId) {
		if (!production) {
			log.info("getServiceManifest from ip");
			String manifestFile = "IP-Manifest.xml";
			String manifestPath = System.getProperty("user.dir")
					+ "\\src\\test\\resources\\" + manifestFile;
			try {
				return ServiceManifestXMLProcessor
						.readFileAsString(manifestPath);
			} catch (IOException ioe) {
				log.equals("unable to open manifest from the path"
						+ manifestPath);
				return null;
			}
		} else {
			log.info("get service manifest from the db");
			try {
				TrecServiceInfoDAO tsiDAO = new TrecServiceInfoDAO();
				ServiceInfo si = tsiDAO.getService(serviceId);
				return si.getServiceManifest();
			} catch (Exception e) {
				log.equals("unable to open manifest from the db");
				return null;
			}
			
		}
	}

	public Manifest stringManifest2Manifest(String strignManifest) {
		this.ipManifest = Manifest.Factory.newInstance(strignManifest);
		return this.ipManifest;
	}

	public HashMap<String, Integer> getIPManifestInfoPerInstance(
			String instanceId) {

		// Get IP Extensions
		VirtualHardwareSection vhs = ipManifest
				.getInfrastructureProviderExtensions()
				.getVirtualSystem(instanceId).getVirtualHardwareSection();

		int vhscpuSpeed = vhs.getCPUSpeed();
		int vhsMemorySize = vhs.getMemorySize();
		int vhsNumCPU = vhs.getNumberOfVirtualCPUs();

		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("smcpuspeed", vhscpuSpeed);
		hm.put("smmemorysize", vhsMemorySize);
		hm.put("smnumcpu", vhsNumCPU);
		return hm;
	}

	public ElasticityRule[] getElasticityRules() {
		return ipManifest.getElasticitySection().getRuleArray();
	}

	public String getServiceId() {
		return ipManifest.getVirtualMachineDescriptionSection().getServiceId();
	}

	public String getServiceProviderId() {
		return ipManifest.getServiceProviderId();
	}

	public String getSLAAgreementId() {
		// TODO get ServiceLevel Agreement ID
		return "123";
	}
	
	public VirtualMachineComponent[] getNumberOfServiceComponents() {
		return ipManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray();
	}
	
	public DataProtectionSection getDataProtectionSection(){
		return ipManifest.getDataProtectionSection();
	}
}
