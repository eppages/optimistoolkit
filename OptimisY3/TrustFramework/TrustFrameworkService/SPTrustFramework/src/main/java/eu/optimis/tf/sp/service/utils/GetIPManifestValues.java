/**

Copyright 2013 ATOS SPAIN S.A. and City University London

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
Marian Kiram. City University of London
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.sp.service.utils;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;

import eu.optimis.manifest.api.ip.ElasticityRule;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;

public class GetIPManifestValues {

	// ManifestParser parser;

	protected final static Logger log = Logger
			.getLogger(GetIPManifestValues.class);

	private Manifest ipManifest;
	private boolean production = false;

	public GetIPManifestValues(){
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
	}
	
	public GetIPManifestValues(String serviceId) {
		super();
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		stringManifest2Manifest(getServiceManifest(serviceId));
	}

	public String getServiceManifest(String serviceId) {
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
				String url = PropertiesUtils.getProperty("TRUST","db.host");
				String user = PropertiesUtils.getProperty("TRUST","db.user");
				String password = PropertiesUtils.getProperty("TRUST","db.pass");
				Driver myDriver = new com.mysql.jdbc.Driver();
				DriverManager.registerDriver(myDriver);
				Connection conn = DriverManager.getConnection(url, user,
						password);
				// Get a statement from the connection
				Statement stmt = conn.createStatement();

				// Execute the query
				ResultSet rs = stmt.executeQuery("SELECT  `service_manifest` FROM  `manifest_raw` WHERE  `service_id` =  '"+serviceId+"'");
				String serviceManifest = "";
				// Loop through the result set
				while (rs.next()){
					serviceManifest = rs.getString(1);
//					System.out.println(serviceManifest);
				}

				// Close the result set, statement and the connection
				rs.close();
				stmt.close();
				conn.close();
				return serviceManifest;
			} catch (SQLException e) {
				log.error("Error: unable to load service manifest!");
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
		log.info("instanceId: "+instanceId);
		// Get IP Extensions
		VirtualHardwareSection vhs = ipManifest
				.getInfrastructureProviderExtensions()
				.getVirtualSystem(instanceId).getVirtualHardwareSection();

		int vhscpuSpeed = vhs.getCPUSpeed();		
		int vhsMemorySize = vhs.getMemorySize();
		int vhsNumCPU = vhs.getNumberOfVirtualCPUs();
		
		log.info("smcpuspeed " +vhscpuSpeed);
		log.info("smmemorysize "+ vhsMemorySize);
		log.info("smnumcpu "+ vhsNumCPU);

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
		return ipManifest.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentArray();
	}
}
