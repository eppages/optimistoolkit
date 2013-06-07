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

import java.util.List;

import junit.framework.TestCase;
import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.trec.common.db.ip.model.SpInfo;

public class TrecCommonIPTest extends TestCase {
	
	
	String ipname = "atosIP";
	String ipId = "atosIPId";
	String location = "es";
	String ipType = "ip";
	
//	public void testCommonIpInfo(){
//		TrecIPinfoDAO tipdao = new TrecIPinfoDAO();
//	
//		try {
//			tipdao.addIp(ipname, ipId, location);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	public void testCommonSNproviders(){
//		TrecSNProviderDAO tsnprov = new TrecSNProviderDAO();
//	
//		try {
//			tsnprov.addSNProvider(ipId, ipType);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	String serviceId = "DemoApp";
	String serviceName = "Demo Application";
	String serviceManifest = "Demo app service manifest";
	String slaId = "slaid";
	boolean deployed = true;
	
//	public void testAddService(){
//		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
//		try {
//			tsidao.addService(serviceId, serviceName, serviceManifest, slaId, deployed);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public void testgetService(){
//		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
//		try {
//			ServiceInfo  si = tsidao.getService(serviceId);
//			System.out.println(si.getServiceId());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	String spId = "atosSPid";
//	
//	public void testgetSPTrust(){
//		TrecSPTrustDAO tsidao = new TrecSPTrustDAO();
//		try {
//			SpTrust  si = tsidao.getSPTrust(spId);
//			System.out.println(si.getSpTrust());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public void testgetIP2SP(){
//		
//		String spId = "OPTIMUMWEB";
//		TrecIP2SPDAO trecip2spdao = new TrecIP2SPDAO();
//		
//		try {
//			List<IpToSp> ip2splist = trecip2spdao.getIP2SPTrustsBySpId(spId);
//			double r = 0,s = 0;
//			for (IpToSp ipsp : ip2splist){
//				if (ipsp.getServiceTrust() > 0.5){
//					r += 1;
//				} else {
//					s +=1;
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void testIPSP(){
		TrecIP2SPDAO ip2sp = new TrecIP2SPDAO();
		try {
			List<SpInfo> spilist = ip2sp.getDistinctSpIDs();
			for (SpInfo spi : spilist){
				System.out.println(spi.getSpId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
