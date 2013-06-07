/*
 *  Copyright 2013 University of Leeds
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
package eu.optimis.serviceproviderriskassessmenttool.core;

import eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.riskassessor.Reliability;
import eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.riskassessor.RiskAssessor;
import eu.optimis.serviceproviderriskassessmenttool.core.configration.ConfigManager;
import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate.RiskPropagator;
import eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator.DsAhpProviderObject;
import eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator.InfrastructureProviderEvaluatorBySP;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author scsmj
 */
public class ServiceProviderRiskAssessmentServer {

    private static ServiceProviderRiskAssessmentServer singleServiceProviderRiskAssessmentServer;
    protected static Logger log = Logger.getLogger(ServiceProviderRiskAssessmentServer.class);
    private OperationPhaseRiskAssessment OPRiskAssessor = null;

    //constructor
    public ServiceProviderRiskAssessmentServer() {
        PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        log.info("SPRA: ServiceProviderRiskAssessmentServer Starts Up ......");
    }

    public static ServiceProviderRiskAssessmentServer getServiceProviderRiskAssessmentServer() {
        return singleServiceProviderRiskAssessmentServer;
    }

    public List<String> preNegotiateIPDeploymentPhase(List<String> ipNamesT) {

        List<String> sns = new ArrayList<String>();
        DsAhpProviderObject[] rankedProviders = rankIPs(ipNamesT.toArray());
        for (int i = 0; i < rankedProviders.length; i++) {
            sns.add(rankedProviders[i].getDistName());
        }
        return sns;
    }

    public int calculateRiskLevelOfSLAOfferReliability(String ipName, String serviceID, double proposedPoF) {
        double tempdouble = 0.01;
        RiskAssessor newra = new RiskAssessor();
        try {
            Reliability temp = newra.computeReliability(ipName, proposedPoF);
            tempdouble = temp.getAdjustedPof();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int poflevel = convertPoFLevel(tempdouble);
        int impactlevel = calculateImpactLevelOfSLAOfferReliability();
        int risklevel = convertRiskLevel(poflevel * impactlevel);
        try{
            log.info("SPRA: Operation Phase: riskPropagator method starts ......");
            RiskPropagator riskPropagator = new RiskPropagator(RiskPropagator.PROVIDERTYPE_SP, RiskPropagator.SERVICEPHASE_OPERATION, ipName, serviceID, RiskPropagator.GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL);           
            riskPropagator.setRiskValue(risklevel);
            riskPropagator.addRiskValue();
            log.info("SPRA: Operation Phase: riskPropagator method starts ......");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return risklevel;
    }
    
    public int calculateRiskLevelOfSLAOfferReliabilityDeployment(String ipName, String serviceID, double proposedPoF) {
        double tempdouble = 0.01;
        RiskAssessor newra = new RiskAssessor();
        try {
            Reliability temp = newra.computeReliability(ipName, proposedPoF);
            tempdouble = temp.getAdjustedPof();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int poflevel = convertPoFLevel(tempdouble);
        int impactlevel = calculateImpactLevelOfSLAOfferReliability();
        int risklevel = convertRiskLevel(poflevel * impactlevel);
        try{
            log.info("SPRA: Deployment Phase: riskPropagator method starts ......");
            RiskPropagator riskPropagator = new RiskPropagator(RiskPropagator.PROVIDERTYPE_SP, RiskPropagator.SERVICEPHASE_DEPLOYMENT, ipName, serviceID, RiskPropagator.GRAPHTYPE_SP_DEPLOYMENT_RELATIVE_IP_SLA_RISKLEVEL);           
            riskPropagator.setRiskValue(risklevel);
            riskPropagator.addRiskValue();
            riskPropagator = new RiskPropagator(RiskPropagator.PROVIDERTYPE_SP, RiskPropagator.SERVICEPHASE_DEPLOYMENT, ipName, serviceID, RiskPropagator.GRAPHTYPE_SP_DEPLOYMENT_NORMALISED_SLA_RISKLEVEL);           
            double pof = adjustedPOFCal(ipName, proposedPoF*(1+tempdouble));
            int pofl = convertPoFLevel(pof);
            int impactl = calculateImpactLevelOfSLAOfferReliability();
            int riskl = convertRiskLevel(pofl * impactl);
            riskPropagator.setRiskValue(riskl);
            riskPropagator.addRiskValue();
            log.info("SPRA: Deployment Phase: riskPropagator method stops ......");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return risklevel;
    }

    public double adjustedPOFCal(String ipName, double proposedPOF) {
        double apof = 0.01;
        System.out.println("Calling riskAssessor class For IP: " + ipName + " with proposed pof: " + proposedPOF);
        RiskAssessor newra = new RiskAssessor();

        try {
            Reliability temp = newra.computeReliability(ipName, proposedPOF);
            apof = temp.getAdjustedPof();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apof;
    }

    public int calculateImpactLevelOfSLAOfferReliability() {
        return 5;
    }

    private int convertPoFLevel(double pof) {

        int poflevel = 0;

        if (pof <= 0.2) {
            poflevel = 1;
        } else if ((pof > 0.2) && (pof <= 0.4)) {
            poflevel = 2;
        } else if ((pof > 0.4) && (pof <= 0.6)) {
            poflevel = 3;
        } else if ((pof > 0.6) && (pof <= 0.8)) {
            poflevel = 4;
        } else if (pof > 0.8) {
            poflevel = 5;
        }
        return poflevel;
    }

    private int convertRiskLevel(int risklevel) {

        int normalisedRiskLevel = 1;

        if (risklevel <= 3) {
            normalisedRiskLevel = 1;
        } else if ((risklevel > 3) && (risklevel <= 7)) {
            normalisedRiskLevel = 2;
        } else if ((risklevel > 7) && (risklevel <= 13)) {
            normalisedRiskLevel = 3;
        } else if ((risklevel > 13) && (risklevel <= 17)) {
            normalisedRiskLevel = 4;
        } else if ((risklevel > 17) && (risklevel <= 21)) {
            normalisedRiskLevel = 5;
        } else if ((risklevel > 21) && (risklevel <= 23)) {
            normalisedRiskLevel = 6;
        } else if ((risklevel > 23) && (risklevel <= 25)) {
            normalisedRiskLevel = 7;
        }
        return normalisedRiskLevel;
    }

    private DsAhpProviderObject[] rankIPs(Object[] IProviders) {

        InfrastructureProviderEvaluatorBySP ipe = new InfrastructureProviderEvaluatorBySP();

        DsAhpProviderObject[] rankedIProviders = null;
        
        try {
            rankedIProviders = ipe.rankInfrastructureProviderComDBSP(IProviders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rankedIProviders;
    }
    
    public synchronized void startServiceAssessment(String serviceId){
        if (OPRiskAssessor == null) {
            OPRiskAssessor = new OperationPhaseRiskAssessment(serviceId);
            OPRiskAssessor.start();
        }
    }
    
    public synchronized void stopServiceAssessment(String serviceId){
        
        if (OPRiskAssessor != null) {
            OPRiskAssessor.stopAssessment(serviceId);
            OPRiskAssessor = null;
        }
        
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {

        ServiceProviderRiskAssessmentServer spra = new ServiceProviderRiskAssessmentServer();

        System.out.println("----------------------------------");
        System.out.println("The output of the function 1:");

        List<String> ipNames = new ArrayList();
        ipNames.add("atos1");
        ipNames.add("atos2");
        ipNames.add("atos3");

        List<String> ds = new ArrayList();
        ds = spra.preNegotiateIPDeploymentPhase(ipNames);
        for (int k = 0; k < ds.size(); k++) {
            System.out.println("IP Name: " + ds.get(k));
        }

        System.out.println("----------------------------------");
        System.out.println("The output of function 4:");
        
        String ipName = "atos";
        double pof = 0.6;
        double apof = spra.adjustedPOFCal(ipName, pof);
        System.out.println("APoF = " + apof);
        int risklevel = spra.calculateRiskLevelOfSLAOfferReliability(ipName, "service001", pof);
        System.out.println("RiskLevel = " + risklevel);
    }
}
