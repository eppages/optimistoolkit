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

package eu.optimis.tf.sp.service.np;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.optimis.tf.sp.service.operators.Opinion;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

public class NPCalculator {

	final static Logger log = LoggerFactory.getLogger(NPCalculator.class
			.getName());

	private static int LIMIT = 1;
	//private static String serviceId;

	public static void setCoefficients(ArrayList<HashMap> lstVMMetric,
			List<String> vmIds, String serviceId) {
		double sumTC = 0;
		
		//serviceId = PropertiesUtils.getBoundle("npApp");
		ArrayList<Integer> listTC = new ArrayList<Integer>();

		for (String vmId : vmIds) {
			for (HashMap<String, String> VMMetric : lstVMMetric) {
				if (VMMetric.containsKey(vmId)) {
					listTC.add(Integer.valueOf(VMMetric.get(vmId)));
				}
			}
		}
		log.info("List thread count size: " + listTC.size());
		sumTC = sumatory(listTC);
		Opinion op = calculateTrust(listTC.size(), sumTC);
		insertInDB(op, serviceId);
	}

	private static Opinion calculateTrust(double vmNum, double threadCount) {
		LIMIT = Integer.valueOf(PropertiesUtils.getProperty("TRUST","npLimit"));
		log.info("vm Number = "+vmNum);
		double r = threadCount / (vmNum * LIMIT);
		while (r > 1){
			r = r - 1;
		}
		double s = 1 - r;
		log.info("R: " + r + ", S: " + s);
		Opinion opinion = new Opinion(r, s);
		opinion.setExpectation();
		return opinion;
	}

	private static void insertInDB(Opinion opinion, String serviceId) {
//		TrustNPDAO tnpdao = new TrustNPDAO();
//		try {
//			tnpdao.addNPDatad(opinion.getExpectation(), opinion.getBelief(),
//					opinion.getDisBelief(), opinion.getUnCertainty(),
//					opinion.getRelativeAtomicity(), serviceId);
//		} catch (Exception e) {
//			logger.error("Error insterting data in the database, table trustnp");
//		}
	}

	private static double sumatory(ArrayList<Integer> listTC) {
		double sum = 0;
		for (double i : listTC) {
			sum = sum + i;
		}
		return sum;
	}
}
