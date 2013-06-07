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

package eu.optimis.common.trec.db;

import junit.framework.TestCase;
import eu.optimis.common.trec.db.sp.TrecIPinfoDAO;

public class TrecCommonIPTest extends TestCase {

	String ipname = "atos";
	String ipId = "atoses";
	String location = "es";

	public void testCommonIpInfo() {
		TrecIPinfoDAO tipdao = new TrecIPinfoDAO();
		try {
			System.out.println(tipdao.addIp(ipname, ipId, location));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void testCommonSPInfo() {
	// TrecSPinfoDAO tspidao = new TrecSPinfoDAO();
	// try {
	// tspidao.addSP(spName, spId);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public void testAddspTrust(){
	// TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
	// Random generator = new Random();
	// for (int i = 0; i < 30; i++){
	// try {
	// tsptdao.addSp(spId, generator.nextInt(30));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	// public void testAddspTrust() {
	// TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
	// Random generator = new Random();
	// for (int i = 0; i < 30; i++) {
	// try {
	// tsptdao.addSp(spId, generator.nextInt(5));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	// public void testgetSPTrusts() {
	// TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
	// try {
	// List<SpTrust> sptl = tsptdao.getSPTrusts(spId);
	// for (SpTrust spt : sptl) {
	// System.out.println(spt.getSpInfo().getSpId() + " : "
	// + spt.getSpTrust());
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public void testgetSPTrust() {
	// TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
	// try {
	// SpTrust spt = tsptdao.getSPTrust(spId);
	// System.out.println(spt.getSpInfo().getSpId() + " : "
	// + spt.getSpTrust());
	// } catch (Exception e) {
	// System.out.println(5);
	// }
	// }

	String spName = "atosSP";
	String spId = "atosSPId";

	TrecIPinfoDAO tipdao = new TrecIPinfoDAO();

//	public void testAddip() {
//		ArrayList<String> brokerIps = new ArrayList<String>();
//		brokerIps.add("atos");
//		for (String brokerIp : brokerIps) {
//			try {
//				System.out.println("adding: " + brokerIp);
//				tipdao.addIp(brokerIp, brokerIp, "es");
//			} catch (Exception e1) {
//			}
//		}
//	}

//	public void testgetIPTrust() {
//		TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
//		try {
//			List<IpTrust> iptlist = tiptdao.getIPTrusts("atos");
//			for (IpTrust ipt : iptlist){
//			System.out.println(ipt.getIpInfo().getIpId() + " : "
//					+ ipt.getIpTrust());
//			}
//		} catch (Exception e) {
//			System.out.println(5);
//		}
//	}

}
