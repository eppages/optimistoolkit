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
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jfree.util.Log;

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecIPinfoDAO;
import eu.optimis.common.trec.db.ip.TrecSLADAO;
import eu.optimis.common.trec.db.ip.TrecServiceComponentDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.operators.FuzzyAggregator;
import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

public class TrustIPOrchestrator {
			
	class ip2sp {
		private double legalAspects;
		private double risk;
		private double vmperformance;
		private double security;
		private double reliability;
		private double serviceTime;
		private String serviceId;

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public double getLegalAspects() {
			return legalAspects;
		}

		public void setLegalAspects(double legalAspects) {
			this.legalAspects = legalAspects;
		}

		public double getRisk() {
			return risk;
		}

		public void setRisk(double risk) {
			this.risk = risk;
		}

		public double getVmperformance() {
			return vmperformance;
		}

		public void setVmperformance(double vmperformance) {
			this.vmperformance = vmperformance;
		}

		public double getServiceTime() {
			return serviceTime;
		}

		public void setServiceTime(double serviceTime) {
			this.serviceTime = serviceTime;
		}

		public double getSecurity() {
			return security;
		}

		public void setSecurity(double security) {
			this.security = security;
		}

		public double getReliability() {
			return reliability;
		}

		public void setReliability(double reliability) {
			this.reliability = reliability;
		}

	}
	
	ArrayList<String> ServiceIdList = new ArrayList<String>();
	Logger log = Logger.getLogger(this.getClass().getName());
	double scale = 1.0;
	public TrustIPOrchestrator(ArrayList<String> sidList) {
		ServiceIdList = sidList;
		addIP();
		scale = Double.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
	}
	
	public void calculateIP2SPParams(){
		ip2sp ipsp = new ip2sp();
		
		TrecSLADAO tsladao = new TrecSLADAO();
		
		VMPerformanceSP vmpsp = new VMPerformanceSP();
		
		String ipId = "";
		int cap = 100;		
		
		for (String serviceId : ServiceIdList){
			double performance = 0.0;
			double legal = 0.0;
			double reliability = 0.0;
			double riskval = 0.0;
			double security = 0.0;
			
			log.info("Calculating parameters for service " + serviceId);
			try
			{
				log.info("Calculating VM Performance");
				performance = vmpsp.calculatePerformance(serviceId);
				if (Double.isNaN(performance)){
					performance = CalculateGapTrust(100,80);
				}
			}
			catch (IOException e) {
				log.error("Unable to calculate VM performance");
				log.error(e.getMessage());
			}
			
			try
			{
				log.info("Calculating Legal aspects");
				SPLegalAspects splegal = new SPLegalAspects();
				legal = splegal.calculateLegalAspects(serviceId);
			}
			catch (Exception ex)
			{
				log.error("Error when calculating legal aspects for " + serviceId);
				log.error(ex.getMessage());
				log.error(ex.getCause());
			}
			
			try
			{
				log.info("Calculating Security aspects");
				SPSecurityAspects spSecurity = new SPSecurityAspects();
				security = spSecurity.calculateSecurityAspects(serviceId);
			}
			catch (Exception ex)
			{
				log.error("Error when calculating security aspects for " + serviceId);
				log.error(ex.getMessage());
				log.error(ex.getCause());
			}
			
			try
			{
				log.info("Calculating Risk Assessment");
				RiskAssessment risk = new RiskAssessment();
				riskval = risk.getRisk(ipId);
			}
			catch (Exception ex)
			{
				log.error("Error when calculating risk parameter for " + serviceId);
				log.error(ex.getMessage());
			}

			try
			{
				log.info("Calculating Reliability");
				SPTrust spt = new SPTrust();
				reliability = spt.getNMReliability();
			}
			catch (Exception ex)
			{
				log.error("Error when calculating reliability parameter for " + serviceId);
				log.error(ex.getMessage());
			}
			
			ipsp.setServiceId(serviceId);
			ipsp.setLegalAspects(legal);
			ipsp.setRisk(riskval);
			ipsp.setVmperformance(performance);
			ipsp.setReliability(reliability);
			ipsp.setSecurity(security);
			ipsp.setServiceTime(0.5);
			
			saveIP2SPTrust(ipsp);
		}
	}

	private void saveIP2SPTrust(ip2sp ipsp){
		
		// Prepare those parameters where exponential smoothing is applicable
		String serviceId = ipsp.getServiceId();
		ExponentialSmoothingAggregator myEAggregator = new ExponentialSmoothingAggregator();
		double performance = myEAggregator.calculateAggregation(serviceId, ipsp.getVmperformance(), ExponentialSmoothingAggregator.PERFORMANCE);
		double elasticity = myEAggregator.calculateAggregation(serviceId, ipsp.getReliability(), ExponentialSmoothingAggregator.ELASTICITY); //Change when elasticity is ok in ipsp class
		double reliability = myEAggregator.calculateAggregation(serviceId, ipsp.getReliability(), ExponentialSmoothingAggregator.RELIABILITY);
		// Aggregate results with the Fuzzy model
		FuzzyAggregator myCalculator = new FuzzyAggregator();
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("risk", ipsp.getRisk()*scale);
		myHash.put("security", ipsp.getSecurity()*scale);
		myHash.put("elasticityRules", new Double ("0.65")*scale); //To be changed one DB is fixed
		myHash.put("performanceGap", performance*scale);
		myHash.put("reliability", reliability*scale);
		myHash.put("legal", ipsp.getLegalAspects()*scale);
		double serviceTrust = myCalculator.calculateTrustAggregation(myHash)/new Double(scale);
						
		TrecIP2SPDAO trecip2spdao = new TrecIP2SPDAO();
		TrecServiceComponentDAO tscompdao = new TrecServiceComponentDAO();
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {			
			String serviceComponent = tscompdao.getsServiceComponent(serviceId).getComponentId();			
			String spId = tsidao.getService(serviceId).getSpId();			
			String ipId = PropertiesUtils.getProperty("TRUST","ip");	
			/*log.debug("Adding trust calculation with parameters:");
			log.debug("--ServiceID: " + serviceId);
			log.debug("--ServiceComponent: " + serviceComponent);
			log.debug("--SPId: " + spId);
			log.debug("--IPId: " + ipId);
			log.debug("--ServiceTime: " + ipsp.getServiceTime());
			log.debug("--Risk: " + ipsp.getRisk());
			log.debug("--Security: " + ipsp.getSecurity());
			log.debug("--Reliability: " + reliability);
			log.debug("--Performance: " + performance);
			log.debug("--LegalAspects: " + ipsp.getLegalAspects());
			log.debug("--ServiceTrust: " + serviceTrust);*/
			trecip2spdao.addIP2SP(serviceId, serviceComponent, spId, ipId, ipsp.getServiceTime(), ipsp.getRisk(), ipsp.getSecurity(), ipsp.getReliability(), ipsp.getVmperformance(), ipsp.getLegalAspects(), serviceTrust);
		}catch (Exception e)
		{
			log.error("Error when saving IP to SP Trust for service " + serviceId + "!");
			log.error(e.getMessage());
			log.error(e.getCause().getMessage());
		}
	}
	
	private boolean addIP(){
		String ip = "ip";
		TrecIPinfoDAO tipdao = new TrecIPinfoDAO();
		try {
			tipdao.getIP(ip);
			return true;
		} catch (Exception e) {
			try {
				return tipdao.addIp(ip, ip, "es");
			} catch (Exception e1) {
				return false;
			}
			
		}
	}
	
	private double CalculateGapTrust(int maximum, int minimum ){
		double randomNum = 0;
		Random rn = new Random();
		int n = maximum - minimum + 1;
		int i = rn.nextInt() % n;
		randomNum =  minimum + i;
		return Math.abs(randomNum/100);
	}

}
