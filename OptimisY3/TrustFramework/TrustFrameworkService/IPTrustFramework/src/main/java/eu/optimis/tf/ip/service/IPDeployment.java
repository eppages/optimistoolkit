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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import eu.optimis.common.trec.db.ip.TrecIPTrustDAO;
import eu.optimis.common.trec.db.ip.TrecSLADAO;
import eu.optimis.common.trec.db.ip.TrecSPTrustDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.common.trec.db.ip.TrecSnProviderTrustDAO;
import eu.optimis.tf.ip.service.operators.ManifestSimilarity;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.ServiceInfo;
import eu.optimis.trec.common.db.ip.model.ServiceSla;
import eu.optimis.trec.common.db.ip.model.SnTrustProvider;
import eu.optimis.trec.common.db.ip.model.SpTrust;

public class IPDeployment {

	private boolean production = false;
	private static final double NO_TURST = 0.2;
	
	public IPDeployment() {
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
	
	public double getHMAssessment(String manifest){
		TrecServiceInfoDAO tsidao = new  TrecServiceInfoDAO();
		TrecSLADAO tsladao = new TrecSLADAO();
		ManifestSimilarity slasimil = new ManifestSimilarity();
		HashMap<String,Double> similarityMap = new HashMap<String,Double>();
		double bestSimil = 0.0;
		try {
			List<ServiceInfo> silist = tsidao.getActiveServices();
			for (ServiceInfo si : silist){
				double simil = slasimil.getSimilarity(si.getServiceManifest(),manifest);
				if (simil < bestSimil){
					bestSimil = simil;
				}
			}	
		} catch (Exception e) {
			return bestSimil;
		}
		// introduce linear regrex	
		return bestSimil;
	}

	public double getSelfAssessment(String providerId) {
		TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
		try {
			return tiptdao.getIPTrust(providerId).getIpTrust();
		} catch (Exception e) {
			return NO_TURST;
		}
	}
}
