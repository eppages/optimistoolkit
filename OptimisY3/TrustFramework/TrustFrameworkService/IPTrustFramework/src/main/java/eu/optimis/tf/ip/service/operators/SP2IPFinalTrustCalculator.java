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

package eu.optimis.tf.ip.service.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecIPTrustDAO;
import eu.optimis.common.trec.db.ip.TrecSP2IPDAO;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.SpToIp;

public class SP2IPFinalTrustCalculator {

	Logger log = Logger.getLogger(this.getClass());
	
	public void calculateIPTrust(String ipId) {
		double threshold = Double.valueOf(PropertiesUtils.getProperty("TRUST","threshold"));
		TrecSP2IPDAO trecsp2ipdao = new TrecSP2IPDAO();
		TrecIPTrustDAO tiptdao = new TrecIPTrustDAO();
		ExponentialSmoothingAggregator myEAggregator = new ExponentialSmoothingAggregator();
		double alpha = 0.5;
		try {
			List<SpToIp> sp2iplist = trecsp2ipdao.getSP2IPTrustsByIpId(ipId);
			ArrayList<Double> trustList = setTrustList(sp2iplist);
			// Transform array and call simple exponential smoothing
			trustList.trimToSize();
			Double[]trutsArray = new Double[trustList.size()];
			trustList.toArray(trutsArray);
			double trust = myEAggregator.simpleExponentialSmoothing(alpha,trutsArray);
			if (trust < 0.25){
				trust = trust + 0.3;
			}
			log.info("\n\n Final IP trust: "+trust+"\n\n");
			tiptdao.addIp(ipId, trust);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error when storing data for IP trust!");
			log.error (e.getMessage());
		}
	}
	
	private ArrayList<Double> setTrustList(List<SpToIp> sp2ipList){
		ArrayList<Double> trustList = new ArrayList<Double>();
		for (SpToIp sp2ip : sp2ipList){
			trustList.add(sp2ip.getServiceTrust());
		}
		return trustList;
	}
	
	public double CalculateBurstingTrust(int maximum, int minimum ){
		double randomNum = 0;
		Random rn = new Random();
		int n = maximum - minimum + 1;
		int i = rn.nextInt() % n;
		randomNum =  minimum + i;
		return Math.abs(randomNum/100);
	}
	
	public double CalculateGapTrust(int maximum, int minimum ){
		double randomNum = 0;
		Random rn = new Random();
		int n = maximum - minimum + 1;
		int i = rn.nextInt() % n;
		randomNum =  minimum + i;
		return Math.abs(randomNum/100);
	}
}
