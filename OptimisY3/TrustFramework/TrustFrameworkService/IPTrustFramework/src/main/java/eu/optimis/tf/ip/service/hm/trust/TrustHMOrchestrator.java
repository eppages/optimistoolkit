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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import eu.optimis.common.trec.db.ip.TrecSLADAO;
import eu.optimis.common.trec.db.ip.TrecSP2IPDAO;
import eu.optimis.common.trec.db.ip.TrecServiceComponentDAO;
import eu.optimis.common.trec.db.ip.TrecServiceInfoDAO;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.operators.FuzzyAggregator;
import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

public class TrustHMOrchestrator {

	Logger log = Logger.getLogger(this.getClass().getName());
	
	class sp2ip {
		private double elasticgap;
		private double vmformation;
		private double vmgap;
		private double vmperformance;
		private String serviceId;
		private double slaagreement;
		private double ipreaction;
		private double legal;

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public double getElasticgap() {
			return elasticgap;
		}

		public void setElasticgap(double elasticgap) {
			this.elasticgap = elasticgap;
		}

		public double getVmformation() {
			return vmformation;
		}

		public void setVmformation(double vmformation) {
			this.vmformation = vmformation;
		}

		public double getVmgap() {
			return vmgap;
		}

		public void setVmgap(double vmgap) {
			this.vmgap = vmgap;
		}

		public double getVmperformance() {
			return vmperformance;
		}

		public void setVmperformance(double vmperformance) {
			this.vmperformance = vmperformance;
		}

		public double getSlaagreement() {
			return slaagreement;
		}

		public void setSlaagreement(double slaagreement) {
			this.slaagreement = slaagreement;
		}
		
		public double getIpReaction() {
			return ipreaction;
		}

		public void setIpReaction(double ipreaction) {
			this.ipreaction = ipreaction;
		}
		
		public double getLegal() {
			return legal;
		}

		public void setlegal(double legal) {
			this.legal = ipreaction;
		}
	}

	ArrayList<String> ServiceIdList = new ArrayList<String>();
	int scale = 1;
	public TrustHMOrchestrator(ArrayList<String> sidList) {
		ServiceIdList = sidList;
		scale = Integer.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
	}

	public boolean calculateSP2IPparams() {
		sp2ip spip = new sp2ip();
		ElasticityGap eg = new ElasticityGap();
		VMFormed vmf = new VMFormed();
		VMPerformance vmp = new VMPerformance();
		SLAAgreement sla = new SLAAgreement();
		Opinion slaOpinion = new Opinion();
		IPreactionTime ipreaction = new IPreactionTime();
		LegalAspects la = new LegalAspects();
		for (String serviceId : ServiceIdList) {
			log.info("calculating params for serviceId: "+serviceId);
			spip.setServiceId(serviceId);
			spip.setElasticgap(eg.getElasticityGap(serviceId));
			spip.setVmformation(vmf.calculateGap(serviceId));
			spip.setVmperformance(vmp.calculateGap(serviceId));
			slaOpinion = sla.getSLAAssessmentOpinion(serviceId);
			spip.setSlaagreement(slaOpinion.getExpectation());
			spip.setIpReaction(ipreaction.getReactiontime(serviceId));
			spip.setlegal(la.getLegalAssessment(serviceId));
			saveServiceInfo(spip);
		}
		return true;
	} 

	private void saveServiceInfo(sp2ip spip) {
		log.info("*********Entering saveServiceInfo for "+ spip.getServiceId());
		String serviceId = spip.getServiceId();

		//double serviceTrust = (spip.getElasticgap() + spip.getSlaagreement()
		//		+ spip.getVmformation() + spip.getVmgap() + spip
		//		.getVmperformance()) / 5;

		// Prepare those parameters where exponential smoothing is applicable
		ExponentialSmoothingAggregator myEAggregator = new ExponentialSmoothingAggregator();
		double runtime = myEAggregator.calculateAggregation(serviceId, spip.getVmperformance(), HMExponentialSmoothingAggregator.RUNTIME);
		double sla = myEAggregator.calculateAggregation(serviceId, spip.getSlaagreement(), HMExponentialSmoothingAggregator.SLA);
		//double ipReaction = myEAggregator.calculateAggregation(serviceId, spip.getElasticgap(), ExponentialSmoothingAggregator.IPREACTION);
		
		// Aggregate results with the Fuzzy model
		FuzzyAggregator myFAggregator = new FuzzyAggregator();
		HashMap<String, Double> myHash = new HashMap<String, Double>();
		myHash.put("vmFormation", spip.getVmformation()*scale);
		myHash.put("runtime", runtime*scale);
		myHash.put("sla", sla*scale); 
		myHash.put("ipReaction", spip.getIpReaction()*scale); // Change when calculation is ready		
		myHash.put("legal", spip.getLegal()*scale); // Change when calculation is ready
		double serviceTrust = myFAggregator.calculateTrustAggregation(myHash)/new Double(scale);		
				
		TrecSP2IPDAO trecsp2ipdao = new TrecSP2IPDAO();
		TrecServiceComponentDAO tscompdao = new TrecServiceComponentDAO();
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		TrecSLADAO tsladao = new TrecSLADAO();
		try {
			String serviceComponent = tscompdao.getsServiceComponent(serviceId)
					.getComponentId();
			String spId = tsidao.getService(serviceId).getSpId();
			String ipId = tsladao.getSLAbyServiceId(serviceId).get(0).getIpId();
			trecsp2ipdao.addSP2IP(spip.getServiceId(), serviceComponent, spId,
					ipId, new Date(), spip.getVmformation(), spip.getVmperformance(),
					spip.getElasticgap(), spip.getIpReaction(), spip.getSlaagreement(), spip.getLegal(),
					serviceTrust);
			log.info("sp2ip trust inserted");
		} catch (Exception e) {
			log.error("Error inserting service trust sp2ip for service " + serviceId + "!");
			log.error(e.getMessage());
		}

	}
}
