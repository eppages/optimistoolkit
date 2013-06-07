/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.optimis.treccommon;

import java.util.HashMap;
import java.util.List;
import java.lang.String;

import org.apache.log4j.Logger;

import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientSP;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.serviceproviderriskassessmenttool.rest.client.ServiceProviderRiskAssessmentToolRESTClient;
import eu.optimis.tf.clients.TrustFrameworkSPClient;

import eu.optimis.economicframework.rest.client.SPAssess;
import eu.optimis.economicframework.rest.client.SPPredict;

/**
 * 
 * @author mariamkiran
 * 
 * 
 */
public class TrecApiSP {
	private static Logger logger = Logger.getLogger(TrecApiSP.class);

	private String host;
	private int port;
	private final static String path_COST = "EconomicFramework";

	private TrustFrameworkSPClient TFSPC = null;
	private ServiceProviderRiskAssessmentToolRESTClient riskClient = null;
	private EcoEfficiencyToolRESTClientSP ecoClient = null;
	private SPAssess costSPAssessClient = null;
	private SPPredict costSPPredictClient = null;

	public TrecApiSP(String host, int port) {
		this.host = host;
		this.port = port;
		TFSPC = new TrustFrameworkSPClient(this.host, this.port);
		riskClient = new ServiceProviderRiskAssessmentToolRESTClient(this.host,
				this.port);
		ecoClient = new EcoEfficiencyToolRESTClientSP(this.host, this.port);
		costSPAssessClient = new SPAssess(host, port, path_COST);
		costSPPredictClient = new SPPredict(host, port, path_COST);
		System.out.println("TREC API SP constructor created");
	}

	// /Eco
	public void startEcoefficiencyAssessment(String serviceId, Long timeout) {
		ecoClient.startAssessment(serviceId, timeout);
	}

	public void stopEcoefficiencyAssessment(String serviceId) {
		ecoClient.stopAssessment(serviceId);
	}

	public String assessServiceEcoefficiency(String serviceId, String type) {
		return ecoClient.assessServiceEcoEfficiency(serviceId, type);
	}

	/**
	 * @deprecated Use forecastServiceEcoEfficiency(String providerId, String
	 *             manifest, Long timeSpan, String type), since typeIdReplicas
	 *             can now be specified directly in the Service Manifest.
	 * @param providerId
	 * @param manifest
	 * @param typeIdReplicas
	 * @param timeSpan
	 * @param type
	 * @return
	 */
	@Deprecated
	public String forecastServiceEcoefficiency(String providerId,
			String manifest, HashMap<String, Integer> typeIdReplicas,
			Long timeSpan, String type) {
		logger.debug("Using corrected Eco-API");
		System.out.println("Using corrected Eco-API");
		return ecoClient.forecastServiceEcoEfficiency(providerId, manifest,
				timeSpan, type);
	}

	public String forecastServiceEcoefficiency(String providerId,
			String manifest, Long timeSpan, String type) {
		logger.debug("Using corrected Eco-API");
		System.out.println("Using corrected Eco-API");
		return ecoClient.forecastServiceEcoEfficiency(providerId, manifest,
				timeSpan, type);
	}

	// Cost
	public String assessServiceCost(String serviceID, String from, String to) {
		return costSPAssessClient.assessServiceCost(serviceID, from, to);
	}

	public String assessVmCost(String vmID, String from, String to) {
		return costSPAssessClient.assessVmCost(vmID, from, to);
	}

	// public String getLocalMultiIPQuote(String manifest)
	// {
	// return costSPPredictClient.getLocalMultiIPQuote(manifest);
	//
	// }
	
	public String getQuote(String manifest){
		return costSPPredictClient.getQuote(manifest);
	}
	
	public String predictVMCost(String vmID, String from, String to, String sampleStart, String sampleEnd) {
		return costSPPredictClient.predictServiceCost(vmID, from, to, sampleStart, sampleEnd);
	}
	
	public String predictServiceCost(String serviceID, String from, String to, String sampleStart, String sampleEnd) {
		return costSPPredictClient.predictServiceCost(serviceID, from, to, sampleStart, sampleEnd);
	}

	
	// Trust
	public double getDeploymentProviderTrust(String providerId) {
		return Double.valueOf(TFSPC.getDeploymentTrust(providerId));
	}

	public double getOperationalProviderTrust(String providerId) {
		return Double.valueOf(TFSPC.getOperationTrust(providerId));
	}

	public List<String> getOperationalHistoricProviderTrust(String providerId) {
		// String temp=null;
		return TFSPC.getOperationHistoricTrust(providerId);

	}

	public List<String> getOperationalHistoricServiceTrust(String serviceId) {

		return TFSPC.getOperationHistoricServiceTrust(serviceId);

	}

	// Risk

	public List<Integer> preNegotiateIPDeploymentPhase(List<String> ipNamesT) {

		return riskClient.preNegotiateIPDeploymentPhase(ipNamesT);

	}

	public double adjustedPoFCal(String ipName, double proposedPOF) {

		return riskClient.adjustedPoFCal(ipName, proposedPOF);

	}
        
       public int calculateRiskLevelOfSLAOfferReliability(String providerID, String serviceID, double proposedPoF) {

                return riskClient.calculateRiskLevelOfSLAOfferReliability(providerID, serviceID, proposedPoF);
                
       }

	/*
	 * provider id means the host id ip or sp used for trust host and port are
	 * used by the ecoefficiency tool for their constructor service manifest is
	 * not being used in any input here so can be ignored proposed Pof is for
	 * risk to say what is the pof the ip is saying about the service
	 */
	public TrecObject getTRECs(String providerId, Manifest manifest,
			double proposedPOF, Long timeSpan) {
		TrecObject result = new TrecObject();

		String serviceId = manifest.getVirtualMachineDescriptionSection()
				.getServiceId();

		double trust_returned = 0;
		int risk_level;
		double[] eco_returned = { -1.0, -1.0 };
		// String cost_prediction = null;

		logger.debug("Using providerId:" + providerId + ", proposedPOF:"
				+ proposedPOF + " to call the ServiceProviderRiskAssessment.");

		// RISK
		risk_level = riskClient.calculateRiskLevelOfSLAOfferReliabilityDeployment(
				providerId, serviceId, proposedPOF);

		logger.debug("risk level returned:=" + risk_level);

		result.set_risk(risk_level);

		logger.debug("Calling Trust using providerId: " + providerId);

		// TRUST
		trust_returned = this.getDeploymentProviderTrust(providerId); // NEED TO

		logger.debug("Trust returned:=" + trust_returned);
		result.set_trust(trust_returned);

		logger.debug("Calling Eco using service:" + serviceId);

		// ECO
		eco_returned = ecoClient.forecastServiceEnEcoEff(providerId,
				manifest.toString(), timeSpan);

		logger.debug("Eco returned:=" + eco_returned[0]
				+ " (energy efficiency)  " + eco_returned[1]
				+ " (ecological efficiency)");

		result.setEnergyEfficiency(eco_returned[0]);
		result.setEcologicalEfficiency(eco_returned[1]);

		logger.debug("Calling the cost prediction.");
		
		// COST
		String cost_prediction = costSPPredictClient.getQuote(manifest.toString());
		result.set_cost(cost_prediction); //Django: not currently used by the SDO but still needed by TREC UI do NOT comment out!!!

		logger.debug("Cost returned:=" + cost_prediction);

		logger.debug("TrecApiSP.getTRECs call done..");

		return result;

	}

	// FOR monitoring

	public void TREC_SP_startmonitoring(String ServiceManifest,
			String serviceId, Long timeouteco, String tiemoutcost)
			throws Exception {

		logger.debug("Starting TREC SP Monitoring for serviceId: " + serviceId);
		
		// ManifestRawObject sms = null;
		String manifest_collected = null;
		try {
			manifest_collected = QueryDatabase.getManifest(serviceId);
			// sms = QueryDatabase.getManifest(serviceID);
			logger.debug("Fetched manifest from database via QueryDatabase() for trust");
		} catch (Exception e) {
			logger.error("Error detected");
			throw new Exception(e.getMessage());
		}

		// Risk
		logger.debug("Calling Risk startAssessServiceCost()");
		riskClient.startAssessment(serviceId);
		logger.debug("Risk startAssessServiceCost() Returned");
		
		// Eco
		logger.debug("Calling Eco startEcoefficiencyAssessment()");
		startEcoefficiencyAssessment(serviceId, timeouteco);
		logger.debug("Eco startEcoefficiencyAssessment() Returned");
		
		// TRUST
		logger.debug("Calling Trust serviceDeployed()");
		TFSPC.serviceDeployed(manifest_collected);
		logger.debug("Trust serviceDeployed() Returned");
		
		// Cost
		logger.debug("Calling Cost startAssessServiceCost()");
		costSPAssessClient.startAssessServiceCost(serviceId, tiemoutcost);
		logger.debug("Cost startAssessServiceCost() Returned");
		
		logger.debug("Started TREC SP Monitoring for serviceId: " + serviceId);
	}

	public void TREC_SP_stopmonitoring(String serviceId) {

		logger.debug("Stopping TREC SP Monitoring for serviceId: " + serviceId);
		
        // Risk
		logger.debug("Calling Risk stopAssessment()");
        riskClient.stopAssessment(serviceId);
        logger.debug("Risk stopAssessment() Returned");
		
		// Eco
		logger.debug("Calling Eco stopEcoefficiencyAssessment()");
		stopEcoefficiencyAssessment(serviceId);
		logger.debug("Eco stopEcoefficiencyAssessment() Returned");

		// Trust
		logger.debug("Calling Trust serviceUndeployed()");
		TFSPC.serviceUndeployed(serviceId);
		logger.debug("Trust serviceUndeployed() Returned");
		
		// Cost
		logger.debug("Calling Cost stopAssessServiceCost()");
		costSPAssessClient.stopAssessServiceCost(serviceId);
		logger.debug("Cost stopAssessServiceCost() Returned");
		
		logger.debug("Stopped TREC SP Monitoring for serviceId: " + serviceId);
	}

}
