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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;

import eu.optimis.common.trec.db.sp.TrecIPTrustDAO;
import eu.optimis.common.trec.db.sp.TrecSLADAO;
import eu.optimis.common.trec.db.sp.TrecServiceInfoDAO;
import eu.optimis.common.trec.db.sp.TrecSnProviderTrustDAO;
import eu.optimis.tf.sp.service.operators.ManifestSimilarity;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.IpTrust;
import eu.optimis.trec.common.db.sp.model.ServiceInfo;
import eu.optimis.trec.common.db.sp.model.ServiceSla;
import eu.optimis.trec.common.db.sp.model.SnTrustProvider;

public class SPDeployment {

	private boolean production = false;
	private static final double NO_TURST = 0.2;

	public SPDeployment() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST", "production"));
	}

	public double getTrust(String providerId) {
		TrecIPTrustDAO iptdao = new TrecIPTrustDAO();
		try {
			IpTrust ipt = iptdao.getIPTrust(providerId);
			double iptrust = ipt.getIpTrust();
			if (iptrust < 0.2)
				iptrust = iptrust + 0.3;
			return iptrust;
		} catch (Exception e) {
			return NO_TURST;
		}
	}

	public double getTrustSN(String providerId) {
		TrecSnProviderTrustDAO tsnptdao = new TrecSnProviderTrustDAO();
		try {
			SnTrustProvider sntp = tsnptdao
					.getSnProviderTrust(providerId, "sp");
			return sntp.getExpectation();
		} catch (Exception e) {
			return NO_TURST;
		}
	}

	public String getIPAssessment(String serviceManifest) {
		TrecServiceInfoDAO tsidao = new  TrecServiceInfoDAO();
		TrecSLADAO tsladao = new TrecSLADAO();
		ManifestSimilarity slasimil = new ManifestSimilarity();
		HashMap<String,Double> similarityMap = new HashMap<String,Double>();
		LinkedHashMap lhm = new LinkedHashMap();
		try {
			List<ServiceInfo> silist = tsidao.getActiveServices();
			for (ServiceInfo si : silist){
				double simil = slasimil.getSimilarity(si.getServiceManifest(),serviceManifest);
				List<ServiceSla> slalist = tsladao.getSLAbyServiceId(si.getServiceId());
				for(ServiceSla ssla : slalist){
					similarityMap.put(ssla.getIpId(), simil);
				}
			}	
			lhm = slasimil.sortHashMapByValuesD(similarityMap);
			return slasimil.toSting(lhm);
		} catch (Exception e) {
			return slasimil.toSting(lhm);
		}
		
	}
	
}
