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

import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientIP;
import eu.optimis.economicframework.rest.client.IPAssess;
import eu.optimis.economicframework.rest.client.IPPredict;
import eu.optimis.infrastructureproviderriskassessmenttool.rest.client.IPRAToolRESTClient;
import eu.optimis.infrastructureproviderriskassessmenttool.rest.client.SPPoFs;
import eu.optimis.tf.clients.TrustFrameworkIPClient;
import java.util.HashMap;
import java.util.List;
import net.emotivecloud.utils.ovf.OVFWrapper;
//import eu.optimis.economicframework.rest.client

/**
 * @author mariamkiran
 */
public class TrecApiIP {

    private TrustFrameworkIPClient TFIPC = null;
    private EcoEfficiencyToolRESTClientIP ecoClient = null;
    private IPPredict costIPPredictClient = null;
    private IPAssess costIPAssessClient = null;
    private IPRAToolRESTClient iprs = null;
	

    public final Eco ECO = new Eco();
    public final Risk RISK = new Risk();
    public final Trust TRUST = new Trust();
    public final Cost COST = new Cost();

	private final static String path_COST = "EconomicFramework";
	
    public TrecApiIP(String host, int port) {
        TFIPC = new TrustFrameworkIPClient(host, port);
        ecoClient = new EcoEfficiencyToolRESTClientIP(host, port);
        costIPAssessClient = new IPAssess(host, port, path_COST);
		costIPPredictClient = new IPPredict(host, port, path_COST);
        iprs = new IPRAToolRESTClient(host, port);
    }

    public class Eco {
        private Eco() {}
        // /ECO
        public void startInfrastructureEcoAssessment(Long timeout) {
            ecoClient.startAssessment(timeout);
        }

        public void stopInfrastructureEcoAssessment() {
            ecoClient.stopAssessment();
        }

        public String assessIPEcoefficiency(String type) {
            return ecoClient.assessIPEcoEfficiency(type);
        }

        public String forecastIPEcoefficiency(Long timeSpan, String type) {
            return ecoClient.forecastIPEcoEfficiency(timeSpan, type);
        }

        public String forecastIPEcoefficiencyServiceDeployment(String manifest, Long timeSpan, String type) {
            return ecoClient.forecastIPEcoEfficiencyServiceDeployment(manifest, timeSpan, type);
        }

        public String forecastIPEcoEfficiencyVMCancellation(String vmId, Long timeSpan, String type) {
            return ecoClient.forecastIPEcoEfficiencyVMCancellation(vmId, timeSpan, type);
        }

        public String forecastIPEcoEfficiencyVMDeploymentKnownPlacement(OVFWrapper ovfDom, String destNode, List<String> activeNodes, String type, Long timeSpan) {
            return ecoClient.forecastIPEcoEfficiencyVMDeploymentKnownPlacement(ovfDom, destNode, activeNodes, type, timeSpan);
        }

        public String forecastIPEcoEfficiencyVMMigrationKnownPlacement(String vmId, String destNode, List<String> activeNodes, String type, Long timeSpan) {
            return ecoClient.forecastIPEcoEfficiencyVMMigrationKnownPlacement(vmId, destNode, activeNodes, type, timeSpan);
        }

        public String forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(OVFWrapper ovfDom, String type, Long timeSpan) {
            return ecoClient.forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(ovfDom, type, timeSpan);
        }

        public String forecastIPEcoEfficiencyVMMigrationUnknownPlacement(String vmId, String type, Long timeSpan) {
            return ecoClient.forecastIPEcoEfficiencyVMMigrationUnknownPlacement(vmId, type, timeSpan);
        }

        public String assessNodeEcoEfficiency(String nodeId, String type) {
            return ecoClient.assessNodeEcoEfficiency(nodeId, type);
        }

        public List<String> assessMultipleNodesEcoEfficiency(List<String> nodeList, String type) {
            return ecoClient.assessMultipleNodesEcoEfficiency(nodeList, type);
        }

        public String forecastNodeEcoEfficiency(String nodeId, List<String> ovfs, Long timeSpan, String type) {
            return ecoClient.forecastNodeEcoEfficiency(nodeId, ovfs, timeSpan, type);
        }

        public List<String> forecastMultipleNodesEcoEfficiency(List<String> nodeList, String type) {
            return ecoClient.forecastMultipleNodesEcoEfficiency(nodeList, type);
        }

        public void startServiceEcoAssessment(String serviceId, Long timeout) {
            ecoClient.startServiceAssessment(serviceId, timeout);
        }

        public void stopServiceEcoAssessment(String serviceId) {
            ecoClient.stopServiceAssessment(serviceId);
        }

        public String assessServiceEcoefficiency(String serviceId, String type) {
            return ecoClient.assessServiceEcoEfficiency(serviceId, type);
        }

        /**
         * @param manifest
         * @param typeIdReplicas
         * @param timeSpan
         * @param type
         * @return
         * @deprecated Use forecastServiceEcoefficiency(String manifest, Long timeSpan, String type) instead,
         *             since typeIdReplicas is no longer used (specified inside the manifest).
         */
        @Deprecated
        public String forecastServiceEcoefficiency(String manifest, HashMap<String, Integer> typeIdReplicas, Long timeSpan, String type) {
            return ecoClient.forecastServiceEcoEfficiency(manifest, typeIdReplicas, timeSpan, type);
        }

        public String forecastServiceEcoefficiency(String manifest, Long timeSpan, String type) {
            return ecoClient.forecastServiceEcoEfficiency(manifest, timeSpan, type);
        }

        public void setInfrastructureEcoThreshold(Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
            ecoClient.setInfrastructureEcoThreshold(energyEfficiencyTH, ecologicalEfficiencyTH);
        }

        public void setNodeEcoThreshold(String nodeId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
            ecoClient.setNodeEcoThreshold(nodeId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }

        public void setServiceEcoThreshold(String serviceId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
            ecoClient.setServiceEcoThreshold(serviceId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }

        public void setVMEcoThreshold(String vmId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
            ecoClient.setVMEcoThreshold(vmId, energyEfficiencyTH, ecologicalEfficiencyTH);
        }
    }


    public class Trust {
        private Trust() {}
        // /TRUST

        /**
         * Trust for a service provider Id at deployment time
         *
         * @param spId
         * @return trust of the SP
         */
        public String getSPDeploymentTrust(String spId) {
            return TFIPC.getDeploymentTrust(spId);

            // return null;
        }

        /**
         * Trust for a service provider at operation time
         *
         * @param spId
         * @return trus of the SP
         */
        public String getSPOperationTrust(String spId) {
            return TFIPC.getOperationTrust(spId);
        }

        /**** Proactive Trust APIs ****/
        
        /**
         * Subscribe to proactive trust for services and provider
         *
         * @param entityId
         * @param threshold
         * @param type
         * @return Subscription accepted or not
         */
        public boolean setProactiveTrustAssessor(String entityId, double threshold, int type)
        {
            return TFIPC.setProactiveTrustAssessor(entityId, threshold, type);
        }
        
        /**
         * Unsubscribe to proactive trust for services and provider
         *
         * @param entityId        
         * @return Removal accepted or not
         */
        public boolean stopProactiveTrust (String id)
        {
            return TFIPC.stopProactiveTrust(id);
        }
        
        /**** Trust forercast APIs for HM ****/
        
        /**
         * Forecast of service trust for holistic manager
         *
         * @param serviceId
         * @param timeSpan
         * @return forecast of service trust after (timespan) evaluations
         */
        public double forecastServiceTrust(String serviceId, int timeSpan) 
        {
            return TFIPC.forecastServiceTrust(serviceId, timeSpan);
        } 
        
        /**
         * Forecast of IP trust for holistic manager
         *
         * @param serviceId
         * @param timeSpan
         * @return forecast of IP trust after (timespan) evaluations
         */
        public double forecastIPTrust(String providerId, int timeSpan) 
        {
            return TFIPC.forecastIPTrust(providerId, timeSpan);
        } 
        
        /**
         * Forecast of service trust for holistic manager when a new service is going to be deployed
         *
         * @param manifest
         * @return forecast of service trust with the new service
         */
        public double forecastServiceDeployment(String manifest) 
        {
            return TFIPC.forecastServiceDeployment(manifest);
        }        
        
        /**
         * Forecast of IP trust for holistic manager when a new service is going to be deployed
         *
         * @param manifest
         * @return forecast of IP trust with the new service
         */
        public double forecastServiceDeploymentIP(String manifest) 
        {
            return TFIPC.forecastServiceDeploymentIP(manifest);
        }
        
        /**
         * Forecast of service trust for holistic manager when deploying a new VM
         *
         * @param serviceId
         * @return forecast of service trust with the new VM for the indicated service
         */
        public double forecastVMDeployment(String serviceId) 
        {
            return TFIPC.forecastVMDeployment(serviceId);
        }
        
        /**
         * Forecast of IP trust for holistic manager when deploying a new VM
         *
         * @param serviceId
         * @return forecast of IP trust with the new VM for the indicated service
         */
        public double forecastVMDeploymentIP(String serviceId) 
        {
            return TFIPC.forecastVMDeploymentIP(serviceId);
        }
        
        /**
         * Forecast of service trust for holistic manager when canceling a VM
         *
         * @param serviceId
         * @return forecast of service trust canceling VM for the indicated service
         */
        public double forecastVMCancellation(String serviceId) 
        {
            return TFIPC.forecastVMCancellation(serviceId);
        }
        
        /**
         * Forecast of IP trust for holistic manager when canceling a VM
         *
         * @param serviceId
         * @return forecast of IP trust canceling VM for the indicated service
         */
        public double forecastVMCancellationIP(String serviceId) 
        {
            return TFIPC.forecastVMCancellationIP(serviceId);
        }

        /**
         * Current self trust. Self id is required for internal turst use
         *
         * @param ipId
         * @return current trust
         */
        public double getTrustAssessment(String ipId) 
        {
            return TFIPC.getSelfAssessment(ipId);
        }

    }

    public class Cost {
        private Cost() {}
        // /COST
        public String forecastIPCost(String host, int port, String ServiceManifest)
                throws NumberFormatException, Exception {
            return costIPPredictClient.getQuote(ServiceManifest);
        }

        public String assessNodeCost(String nodeID, String from, String to) {
            return costIPAssessClient.assessPhysicalCost(nodeID, from, to);
        }

        public String compareVMDeploymentOptions() {
            return null;
        }
        
//---------Example of conversion of timestamps to cost-internal format----------
//        private static final String DATEFORMAT = "yyyyMMddHHmmss";
//        private SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMAT);
//        Timestamp time_to = new Timestamp(System.currentTimeMillis()); //e.g. input timestamp from HM
//        Timestamp time_from = new Timestamp(System.currentTimeMillis());
//        String to = formatter.format(time_to);
//        String from = formatter.format(time_from);
//--------------------------------------------------------------------------------------------

        public String assessServiceCost(String serviceID, String from, String to){            
            return costIPAssessClient.assessServiceCost(serviceID, from, to);
        }

        public String assessVmCost(String vmID, String from, String to){            
            return costIPAssessClient.assessVmCost(vmID, from, to);
        }

        public String assessPhysicalCost(String nodeID, String from, String to){            
            return costIPAssessClient.assessPhysicalCost(nodeID, from, to);
        }

		public String startAssessServiceCost(String serviceID, String interval){            
            return costIPAssessClient.startAssessServiceCost(serviceID, interval);
        }

        public String stopAssessServiceCost(String serviceID){            
            return costIPAssessClient.stopAssessServiceCost(serviceID);
        }

        public String startAssessVMCost(String vmID, String interval){            
            return costIPAssessClient.startAssessVMCost(vmID, interval);
        }

        public String stopAssessVMCost(String vmID){            
            return costIPAssessClient.stopAssessVMCost(vmID);
        }

        public String startAssessPhysicalCost(String nodeID, String interval){            
            return costIPAssessClient.startAssessPhysicalCost(nodeID, interval);
        }

        public String stopAssessPhysicalCost(String nodeID){            
            return costIPAssessClient.stopAssessPhysicalCost(nodeID);
        }

        public String setThresholdPhysical(String nodeID, String costThreshold){            
            return costIPAssessClient.setThresholdPhysical(nodeID, costThreshold);
        }

        public String setThresholdVM(String vmID, String costThreshold){            
            return costIPAssessClient.setThresholdVM(vmID, costThreshold);
        }

        public String setThresholdService(String serviceID, String costThreshold){            
            return costIPAssessClient.setThresholdService(serviceID, costThreshold);
        }
		
		public String getThresholdPhysical(String nodeID){            
            return costIPAssessClient.getThresholdPhysical(nodeID);
        }

        public String getThresholdVM(String vmID){            
            return costIPAssessClient.getThresholdVM(vmID);
        }

        public String getThresholdService(String serviceID){            
            return costIPAssessClient.getThresholdService(serviceID);
        }
		
		public String predictNodeCost(String nodeID, String from, String to, String sampleStart, String sampleEnd) {
			return costIPPredictClient.predictNodeCost(nodeID, from, to, sampleStart, sampleEnd);
		}
		
		public String predictVMCost(String vmID,String from, String to,String sampleStart, String sampleEnd) {
			return costIPPredictClient.predictVMCost(vmID, from, to, sampleStart, sampleEnd);
		}
		
		public String predictServiceCost(String serviceID,String from, String to,String sampleStart, String sampleEnd) {
			return costIPPredictClient.predictServiceCost(serviceID, from, to, sampleStart, sampleEnd);
		}

    }

    public class Risk {
        private Risk() {}

    /* For AC */
    public ReturnSPPoF preNegotiateSPDeploymentPhase(String sp, String manifest) {
        SPPoFs outs = iprs.preNegotiateSPDeploymentPhase(sp, manifest);
        ReturnSPPoF results = new ReturnSPPoF();
        results.setSPNames(outs.getSPNames());
        results.setPoFSLA(outs.getPoFSLA());
        return results;
    }
    
    public double calculatePhyHostPoF(String phyiscalHostName, String timePeriod) {

        return iprs.calculatePhyHostPoF(phyiscalHostName, timePeriod);

    }
    
    public List<Double> calculatePhyHostPoF(HashMap<String, String> physicalHostNames) {

        return iprs.calculatePhyHostsPoFs(physicalHostNames);

    }

    public int calculateRiskLevelOfPhyHostFailure(String physicalHostName, String timePeriod) {

        return iprs.calculateRiskLevelOfPhyHostFailure(physicalHostName, timePeriod);

    }

    public List<Integer> calculateRiskLevelsOfPhyHostFailures(HashMap<String, String> physicalHostNames) {

        return iprs.calculateRiskLevelsOfPhyHostFailures(physicalHostNames);

    }
    
    /* For HM */
     public void startProactiveRiskAssessor(String infrastructureID, String infrastructureRiskLevelThreshold, HashMap<String, String> physicalHostsRiskLevelThresholds, HashMap<String, String> servicesRiskLevelThresholds, HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {

        iprs.startProactiveRiskAssessorREST(infrastructureID, infrastructureRiskLevelThreshold, physicalHostsRiskLevelThresholds, servicesRiskLevelThresholds, VMsRiskLevelThresholds);
    }
     
    public void stopProactiveRiskAssessor() {

        iprs.stopProactiveRiskAssessorREST();

    }

        public String forecastRiskVMMigrationUnknownHost(String replicationFactor, String vmID, String destNode) {

            return iprs.forecastVMMigrationKnown(replicationFactor, vmID, destNode);
        }

        public String forecastRiskVMMigrationKnownHost(String replicationFactor, String vmID, String destNode) {

            return iprs.forecastVMMigrationUnknown(replicationFactor, vmID, destNode);
        }

        public String forecastRiskVMDeploymentUnknownHost(String replicationFactor, String vmID, String destNode) {

            return iprs.forecastVMDeploymentUnknown(replicationFactor, vmID, destNode);
        }

        public String forecastRiskVMDeploymentKnownHost(String replicationFactor, String vmID, String destNode) {

            return iprs.forecastVMDeploymentKnown(replicationFactor, vmID, destNode);
        }

        public String forecastRiskVMCancelled(String VMID, String replicationFactor) {

            return iprs.forecastVMDeploymentCancelled(VMID, replicationFactor);                             
        }
    }
    // FOR monitoring

    public void TREC_IP_startmonitoring(String ServiceManifest,
                                        List<String> vmIdsList, String serviceID, Long timeoutEco,
                                        Long intervalCost) {
        if (intervalCost == null) {
            intervalCost = new Long(60000);
        }
        System.out.println("[REMOVE] Starting proactive TREC for service " + serviceID + ".");
        System.out.println("[REMOVE] Starting proactive Eco.");
        // ECO
        ECO.startServiceEcoAssessment(serviceID, timeoutEco);

        // cost
        
        System.out.println("[REMOVE] Starting proactive Cost (service).");
        costIPAssessClient.startAssessServiceCost(serviceID, intervalCost.toString());
        for (String vmId : vmIdsList) {
            System.out.println("[REMOVE] Starting proactive Cost (VM " + vmId + ").");
            costIPAssessClient.startAssessVMCost(vmId, intervalCost.toString());
        }
        
        // trust
        System.out.println("[REMOVE] Starting proactive Trust.");
        TFIPC.serviceDeployed(ServiceManifest);
        System.out.println("[REMOVE] After proactive Trust. FINISHED OK.");
        
        // risk
        iprs.runRiskStart(serviceID);
        System.out.println("[REMOVE] Starting proactive Risk.");
       

    }

    public void TREC_IP_stopmonitoring(String serviceID, List<String> vmIdsList) {

        costIPAssessClient.stopAssessServiceCost(serviceID);
        for (String vmId : vmIdsList) {
            costIPAssessClient.stopAssessVMCost(vmId);
        }

        TFIPC.serviceUndeployed(serviceID);

        // ECO
        ECO.stopServiceEcoAssessment(serviceID);
        
        //Risk
        iprs.runRiskStop(serviceID);
        System.out.println("[REMOVE] Stopping proactive Risk.");

    }
}// end of class
