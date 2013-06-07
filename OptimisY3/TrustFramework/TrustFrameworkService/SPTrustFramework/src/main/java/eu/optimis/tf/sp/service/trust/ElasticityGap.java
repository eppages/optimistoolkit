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

package eu.optimis.tf.sp.service.trust;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.sp.service.clients.MonitoringClient;
import eu.optimis.tf.sp.service.operators.Opinion;
import eu.optimis.tf.sp.service.operators.Statistics;
import eu.optimis.tf.sp.service.utils.GetSPManifestValues;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;

public class ElasticityGap {

	private boolean production = false;
	Logger log = Logger.getLogger(this.getClass().getName());

	public ElasticityGap() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
	}

	/**
	 * 
	 * @param serviceId
	 * @return Minimum value of the difference
	 */
	public double getElasticityGap(String serviceId) {
		// Uncomment for use with service running
		/*
		Object obj;
		try {
			obj = Collections.min(loadServiceInfo(serviceId));
			return (Double) obj;
		} catch (IOException e) {
			return 0;
		}
 */
		return calculateErrOP().getExpectation();
		
	}

	private double getCovarianceFactor(ArrayList<Double> alist) {
		double mean = Statistics.mean(alist);
		log.info("Mean: " + mean);
		double variance = Statistics.variance(alist, mean);
		log.info("Variance: " + variance);
		double estdev = Statistics.estdesv(variance);
		log.info("Standar deviation: " + estdev);
		log.info("Coefficient of variance: "
				+ Statistics.coefficientOfVariance(estdev, mean));
		return Statistics.coefficientOfVariance(estdev, mean);
	}

	private ArrayList<Double> loadServiceInfo(String service_ID)
			throws IOException {
		List<MonitoringResourceDataset> monitoringInfo = getMonitoringService(service_ID);
		GetSPManifestValues gipmv = new GetSPManifestValues(service_ID);
		ElasticityRule[] elasticityRules = gipmv.getElasticityRules();
		ArrayList<Double> elasticityGap = new ArrayList<Double>();
		// uncomment for use with real data
		/*
		for (int i = 0; i < elasticityRules.length; i++) {
			log.info("checking elasticityRule "+i+" "+elasticityRules[i].getName());
			ArrayList<Double> ruleGap = new ArrayList<Double>();
			for (MonitoringResourceDataset mrd : monitoringInfo) {
				String metricName = mrd.getMetric_name();
				log.info("metric name: "+metricName);
				if (metricName
						.equalsIgnoreCase(elasticityRules[i].getName())) {
					ruleGap.add(Double.valueOf(mrd.getMetric_value()));
					ruleGap.add(Double.valueOf(elasticityRules[i].getQuota()
							+ (elasticityRules[i].getQuota() * (elasticityRules[i]
									.getTolerance() / 100))));
					elasticityGap.add(getCovarianceFactor(ruleGap));
				}
			}
		}
		return elasticityGap;
		*/
		return getEGapArray();	
		
	}

	private List<MonitoringResourceDataset> getMonitoringService(String kpiName) {
		MonitoringClient mc = new MonitoringClient();
		return mc.getReportForPartMetricName(kpiName); 
	}

	private Opinion calculateOpinion(double r, double s) {
		Opinion op = new Opinion(r, s);
		op.setExpectation();
		return op;
	}
	
	private ArrayList<Double> getEGapArray(){
		ArrayList<Double> egap = new ArrayList<Double>();
		for (int i = 0; i < 10; i++){
			egap.add(calculateErrOP().getExpectation());
		}
		return egap;
	}
	
	private Opinion calculateErrOP(){
		Random generator = new Random();
		int r = generator.nextInt(10);
		int s = 10 - r;
		return calculateOpinion(r, s);
	}
}
