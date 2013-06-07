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

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecSPTrustDAO;
import eu.optimis.common.trec.db.ip.TrecSnProviderTrustDAO;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.IpToSp;
import eu.optimis.trec.common.db.ip.model.SnTrustProvider;
import eu.optimis.trec.common.db.ip.model.SpTrust;

public class IPOperation {
	
	private boolean production = false;
	private static final double NO_TURST = 0.2;

	public IPOperation() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
	}
	
	public double getTrust(String providerId){
		TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
		try {
			SpTrust spt = tsptdao.getSPTrust(providerId);
			return spt.getSpTrust();
		} catch (Exception e) {
			return NO_TURST;
		}
	}
	
	public double getTrustSN(String providerId){
		TrecSnProviderTrustDAO tsnpdao = new TrecSnProviderTrustDAO();
		try {
			SnTrustProvider snpt = tsnpdao.getSnProviderTrust(providerId, "sp");
			return snpt.getExpectation();
		} catch (Exception e) {
			return NO_TURST;
		}
	}
	
	public double getServiceTrust(String serviceId){
		TrecIP2SPDAO tip2spdao = new TrecIP2SPDAO();
		try {
			IpToSp ip2sp = tip2spdao.getLastIP2SPTrust(serviceId);
			return ip2sp.getServiceTrust();
		} catch (Exception e) {
			return NO_TURST;
		}
	}
	
	public String getHistoricTrust(String providerId){
		TrecSPTrustDAO tsptdao = new TrecSPTrustDAO();
		String historicTrust = "<trust>";
		String trustValue="";
		try {
			List<SpTrust> sptrustlist = tsptdao.getSPTrusts(providerId);
			for (SpTrust spt : sptrustlist){
				trustValue += "<value>"+ spt.getSpTrust()+"</value>";
			}
			historicTrust = historicTrust + trustValue + "</trust>";
			return historicTrust;
		} catch (Exception e) {
			historicTrust = historicTrust + trustValue + "</trust>";
			return historicTrust;
		} 
	}
	
	public String getHistoricServiceTrust(String serviceId){
		
		String historic = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>";
		if (!production) {
			try {
//				TrustNPDAO tnpdao = new TrustNPDAO();
//				List<Trustnp> LTNP;
//
//				LTNP = tnpdao.getNPData(serviceId);
//
//				String historicHeader = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
//				String historicBody = "";
//				for (Trustnp tnp : LTNP) {
//					historicBody = historicBody + "<value>" + tnp.getExpectation()
//							+ "</value>";
//				}
//				String historicEnd = "</trust>";
//				historic = historicHeader + historicBody + historicEnd;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			TrecIP2SPDAO tip2spdao = new TrecIP2SPDAO();
			String historicTrust = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
			String trustValue="";
			try {
				List<IpToSp> ip2splist = tip2spdao.getIP2SPTrust(serviceId);
				for (IpToSp ip2sp : ip2splist){
					trustValue += "<value>"+ ip2sp.getServiceTrust()+"</value>";
				}
				historicTrust = historicTrust + trustValue + "</trust>";
				return historicTrust;
			} catch (Exception e) {
				historicTrust = historicTrust + trustValue + "</trust>";
				return historicTrust;
			} 
		}
		return historic;
		
	}
	
}
