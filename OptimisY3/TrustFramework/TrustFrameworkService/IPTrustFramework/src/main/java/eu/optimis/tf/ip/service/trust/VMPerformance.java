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

package eu.optimis.tf.ip.service.trust;

import java.io.IOException;
import java.util.ArrayList;

public class VMPerformance extends VMGap {
	
	
	/**
	 * Calculates the gap between the parameters of a service manifest, the allocated and the used info from the monitoring infrastructure
	 * @param ServiceId
	 * @throws IOException
	 */
	@Override
	public double calculateGap(String serviceId) {
		ArrayList<VMInformation> vminfoList = loadServiceInfo(serviceId);
		ArrayList<Double> cpuList = new ArrayList<Double>();
		ArrayList<Double> memList = new ArrayList<Double>();
		ArrayList<Double> gapFactorList = new ArrayList<Double>();
		for (VMInformation vminfo : vminfoList) {
			vminfo.printContent();
			cpuList.add(vminfo.getCpu_allocated());
			cpuList.add(vminfo.getCpu_asked());
			// CPU usage is defined in % of allocated one
			cpuList.add(vminfo.getCpu_allocated() * vminfo.getCpu_usage());			
			log.debug("Calculating covariance factor for CPU");
			double cpuCovarianceFactor = getCovarianceFactor(cpuList);
			gapFactorList.add(cpuCovarianceFactor);
			memList.add(vminfo.getMemory_asked());
			//memList.add(vminfo.getMemory_allocated() * (vminfo.getMemory_used()/100));
			memList.add(vminfo.getMemory_used());
			memList.add(vminfo.getMemory_allocated());
			log.debug("Calculating covariance factor for memory");
			double memoryCovarianceFactor = getCovarianceFactor(memList);
			gapFactorList.add(memoryCovarianceFactor);
		}

		return aggregate(gapFactorList);
	}

	@Override
	public double aggregate(ArrayList<Double> alist) {
		double sum = 0;
		for(double factor : alist){
			sum += factor;
		}
		return sum/alist.size();
	}

}
