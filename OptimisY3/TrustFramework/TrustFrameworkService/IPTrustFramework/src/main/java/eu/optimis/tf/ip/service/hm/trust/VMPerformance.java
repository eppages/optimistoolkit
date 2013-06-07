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

package eu.optimis.tf.ip.service.hm.trust;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.tf.ip.service.operators.SP2IPFinalTrustCalculator;
import eu.optimis.tf.ip.service.trust.VMInformation;

public class VMPerformance extends VMGap {
	
	
	/**
	 * Calculates the gap between the parameters of a service manifest, the allocated and the used info from the monitoring infrastructure
	 * @param ServiceId
	 * @throws IOException
	 */
	@Override
	public double calculateGap(String serviceId) {
		ArrayList<VMHMInformation> vminfoList = loadServiceInfo(serviceId);
		ArrayList<Double> cpuList = new ArrayList<Double>();
		ArrayList<Double> memList = new ArrayList<Double>();
		ArrayList<Double> gapFactorList = new ArrayList<Double>();
		for (VMHMInformation vminfo : vminfoList) {
			vminfo.printContent();
			cpuList.add(vminfo.getCpu_allocated());
			cpuList.add(vminfo.getCpu_asked());
			cpuList.add(vminfo.getCpu_allocated() * vminfo.getCpu_usage());
			double cpuCovarianceFactor = getCovarianceFactor(cpuList);
			gapFactorList.add(cpuCovarianceFactor);
			memList.add(vminfo.getMemory_asked());
			memList.add(vminfo.getMemory_allocated() * (vminfo.getMemory_used()/100));
			memList.add(vminfo.getMemory_allocated());
			double memoryCovarianceFactor = getCovarianceFactor(memList);
			gapFactorList.add(memoryCovarianceFactor);
		}
		double gap = aggregate(gapFactorList);
		if (Double.isNaN(gap)) {
			SP2IPFinalTrustCalculator ftc = new SP2IPFinalTrustCalculator();
			gap = ftc.CalculateGapTrust(100, 60);
		}
		log.info("calculated GAP: "+ gap);
		return gap;
//		return calculateErrOP().getExpectation();
	}

	@Override
	public double aggregate(ArrayList<Double> alist) {
		double sum = 0;
		for(double factor : alist){
			sum += factor;
		}
		double aggregate = sum/alist.size();
		if (aggregate > 1.0) {
			return 1.0;
		} else {
			return aggregate;
		}
	}
	
	private Opinion calculateErrOP(){
		Random generator = new Random();
		int r = generator.nextInt(10);
		int s = 10 - r;
		return calculateOpinion(r, s);
	}
	
	private Opinion calculateOpinion(double r, double s) {
		Opinion op = new Opinion(r, s);
		op.setExpectation();
		return op;
	}
}
