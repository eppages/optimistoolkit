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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.tf.sp.service.utils.GetIPManifestValues;

public class ManifestValuesTest extends TestCase {

//	public void testVMFormed(){
//		String serviceId = "DemoApp17";
//		VMFormed vmf = new VMFormed();
//		vmf.calculateGap(serviceId);
//		
//		VMPerformance vmp = new VMPerformance();
//		vmp.calculateGap(serviceId);
//	}
	
	public void testManifestValues() throws IOException{
		GetIPManifestValues gipmv = new GetIPManifestValues();
		String strManifest = readFileAsString(System.getProperty("user.dir")+"\\src\\test\\resources\\DemoApp6Manifest.xml");
		Manifest mani = gipmv.stringManifest2Manifest(strManifest);
		String instanceId = "system-optimis-pm-GeneDetection_instance-1";
		getManifestValues(instanceId, mani);
	}
	
	private void getManifestValues(String instanceId, Manifest mani){
		
		System.out.println("instanceId: "+instanceId);
		// Get IP Extensions
		VirtualHardwareSection vhs = mani
				.getInfrastructureProviderExtensions()
				.getVirtualSystem(instanceId).getVirtualHardwareSection();
		
		int vhscpuSpeed = vhs.getCPUSpeed();		
		int vhsMemorySize = vhs.getMemorySize();
		int vhsNumCPU = vhs.getNumberOfVirtualCPUs();
		
		System.out.println("smcpuspeed " +vhscpuSpeed);
		System.out.println("smmemorysize "+ vhsMemorySize);
		System.out.println("smnumcpu "+ vhsNumCPU);
	}
	
	private String readFileAsString(String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}
}
