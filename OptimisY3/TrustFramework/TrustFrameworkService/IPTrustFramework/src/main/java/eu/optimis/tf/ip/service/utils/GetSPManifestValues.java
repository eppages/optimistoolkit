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

package eu.optimis.tf.ip.service.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.ovf.sp.ProductProperty;
import eu.optimis.manifest.api.ovf.sp.VirtualHardwareSection;
import eu.optimis.manifest.api.ovf.sp.VirtualSystem;
import eu.optimis.manifest.api.sp.DataProtectionSection;
import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.manifest.api.sp.Manifest;

public class GetSPManifestValues {

    Manifest spManifest;
    private boolean production = false;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	
    public GetSPManifestValues(String serviceId) {
		super();
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		stringManifest2Manifest(getServiceManifest(serviceId));
	}
    
    private String getServiceManifest(String serviceId) {
		if (production) {
			log.info("getServiceManifest for testing porpuses");
			String manifestFile = "SP-Manifest.xml";
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
			// TODO Create query to get the manifest from the DB
			return "ServiceManifest";
		}
	}
    
    private void stringManifest2Manifest(String strignManifest){
    	this.spManifest = Manifest.Factory
				.newInstance(strignManifest);
    }
    
    public String getServiceId(String serviceManifest){
    	stringManifest2Manifest(serviceManifest);
    	return spManifest.getVirtualMachineDescriptionSection().getServiceId();
    }
    
	private void getSPManifestInfo() throws IOException {

		VirtualHardwareSection jbossHardwareSection;
		jbossHardwareSection = spManifest.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentById("jboss").getOVFDefinition()
				.getVirtualSystem().getVirtualHardwareSection();

		int memorySize = jbossHardwareSection.getMemorySize();
		int numberOfCPUs = jbossHardwareSection.getNumberOfVirtualCPUs();
		int numberOfInstances = spManifest
				.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentById("jboss")
				.getAllocationConstraints().getUpperBound();

		// Printing virtual hardware section
		log.debug("MemorySize: " + memorySize);
		log.debug("NumberOfCPUs: " + numberOfCPUs);
		log.debug("NumberOfInstances: " + numberOfInstances);

		// Printing virtyal System
		VirtualSystem virtsys = spManifest
				.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentById("jboss").getOVFDefinition()
				.getVirtualSystem();
		log.debug(virtsys.getId());
		ProductProperty[] ppArray = virtsys.getProductSection()
				.getPropertyArray();
		for (int i = 0; i < ppArray.length; i++) {
			log.debug(ppArray[i]);
		}

	}
	
	public DataProtectionSection getSPLegalManifest(){
		return spManifest.getDataProtectionSection();
	}
	
	public String getServiceId(){
		return spManifest.getVirtualMachineDescriptionSection().getServiceId();
	}
	
	public String getServiceProviderId(){
		return spManifest.getServiceProviderId();
	}
	
	public ElasticityRule[] getElasticityRules() {
		return spManifest.getElasticitySection().getRuleArray();
	}
	
}
