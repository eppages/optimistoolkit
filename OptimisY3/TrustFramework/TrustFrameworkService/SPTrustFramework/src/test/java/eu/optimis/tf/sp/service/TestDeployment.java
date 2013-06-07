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

import junit.framework.TestCase;
import eu.optimis.common.trec.db.sp.TrecIPTrustDAO;
import eu.optimis.tf.sp.service.clients.ServiceMgrClient;
import eu.optimis.trec.common.db.sp.model.IpTrust;

public class TestDeployment extends TestCase {

String ipName = "atos";
	
//	public void testSpDeployment(){
//		SPDeployment spd = new SPDeployment();
//		System.out.println(spd.getTrust(ipName));
//	}
	
	public void testgetIPTrust() {
		TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
		try {
			IpTrust ipt = tiptdao.getIPTrust(ipName);
			System.out.println(ipt.getIpInfo().getIpId() + " : "
					+ ipt.getIpTrust());
		} catch (Exception e) {
			System.out.println(5);
		}
	}
	
//	public void testSLA(){
//		String serviceId = "685487b0-3457-4127-b74e-2fe94eecf705";
//		String ipId = "umu";
//		ServiceMgrClient smc = new ServiceMgrClient();
//		System.out.println(smc.getSLAIDbyIP(serviceId, ipId));
//	}
}
