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
package eu.optimis.infrastructureproviderriskassessmenttool.core;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.infrastructureproviderriskassessmenttool.core.configration.ConfigManager;
import eu.optimis.infrastructureproviderriskassessmenttool.core.historicaldatabase.dao.populate.RiskPropagator;
import eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator.DsAhpProviderObject;
import eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator.ServiceProviderEvaluatorByIP;
import eu.optimis.infrastructureproviderriskassessmenttool.core.utils.RiskLevelConverter;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import java.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math.distribution.WeibullDistribution;
import org.apache.commons.math.distribution.WeibullDistributionImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * InfrastructureProviderRiskAssessmentServer
 *
 */
public class InfrastructureProviderRiskAssessmentServer {

    private static InfrastructureProviderRiskAssessmentServer singleInfrastructureProviderRiskAssessmentServer;
    protected static Logger log = Logger.getLogger(InfrastructureProviderRiskAssessmentServer.class);
    private String IPName = null;

    private InfrastructureProviderRiskAssessmentServer() {
        PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        PropertiesConfiguration configIPRA = ConfigManager.getPropertiesConfiguration(ConfigManager.IPRA_CONFIG_FILE);
        IPName = configIPRA.getString("config.ipname");
        log.info("IPRA: InfrastructureProviderRiskAssessmentServer Starts Up ......");
    }

    /**
     * Interface for the creation of the singleton instance of
     * InfrastructureProviderRiskAssessmentServer
     *
     */
    public static InfrastructureProviderRiskAssessmentServer getInfrastructureProviderRiskAssessmentServerRiskAssessmentServer() {
        if (singleInfrastructureProviderRiskAssessmentServer == null) {
            singleInfrastructureProviderRiskAssessmentServer = new InfrastructureProviderRiskAssessmentServer();
        }
        return singleInfrastructureProviderRiskAssessmentServer;
    }

    /**
     * The single interface to be invoked by AC on the deployment phase
     *
     */
    public ReturnSPPoF preNegotiateSPDeploymentPhase(Map<String, String> SP2ServiceManifests) {

        log.info("IPRA: preNegotiateSPDeploymentPhase method starts ......");
        ArrayList<String> sns = new ArrayList<String>();
        DsAhpProviderObject[] rankedProviders = rankSPs(SP2ServiceManifests.keySet().toArray());

        for (int i = 0; i < rankedProviders.length; i++) {
            sns.add(rankedProviders[i].getDistName());
        }

        ArrayList<Double> spfs = new ArrayList<Double>();


        Map<Integer, Double> SM2PoF = new HashMap<Integer, Double>();

        for (int i = 0; i < SP2ServiceManifests.size(); i++) {

            Manifest ipManifest = Manifest.Factory.newInstance(SP2ServiceManifests.get(sns.get(i)));
            SM2PoF.put(SP2ServiceManifests.get(sns.get(i)).hashCode(), calculateServiceManifestPoF(ipManifest));
        }

        for (int i = 0; i < SP2ServiceManifests.size(); i++) {

            spfs.add(SM2PoF.get((SP2ServiceManifests.get(sns.get(i)).hashCode())));
        }

        ReturnSPPoF evaluations = new ReturnSPPoF();
        evaluations.setSPNames(sns);
        evaluations.setPoFSLA(spfs);
        log.info("IPRA: preNegotiateSPDeploymentPhase method stops ......");
        return evaluations;
    }

    /*
     * Stage 3
     */
    public double calculateServiceManifestPoF(Manifest serviceManifest) {
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        CloudOptimizerRESTClient CoClient = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("coport")));
        List<String> nodeIDs = CoClient.getNodesId();
        Iterator<String> it = nodeIDs.iterator();
        double pof = 1.0;
        while (it.hasNext()) {
            pof = calculatePhyHostPoF(it.next(), null) * pof;
        }
        int poflevel = RiskLevelConverter.convertPoFLevel(pof);
        int impactlevel = calculateImpactLevelOfSLAFailure();
        int risklevel = RiskLevelConverter.convertRiskLevel(poflevel * impactlevel);

        String serviceID = serviceManifest.getVirtualMachineDescriptionSection().getServiceId();
        log.info("IPRA: updateRiskDB method starts ......");
        updateRiskDB(serviceID, risklevel);
        log.info("IPRA: updateRiskDB method stops ......");
        return risklevel;
    }

    public double calculatePhyHostPoF(String hostName, Long timePeriod) {
        if (timePeriod == null) {
            timePeriod = new Long(24);
        }
        double pof = 0.0;
        long alreadyOnTime = 1000 * 3600 * 24;
        String last_reboot_ts = null;
        double a = 0.3455;
        double m = 265000;
        getClient MonClient = null;

        try {
            PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
            PropertiesConfiguration configIPRA = ConfigManager.getPropertiesConfiguration(ConfigManager.IPRA_CONFIG_FILE);
            a = Double.parseDouble(configIPRA.getString("config.weibullpara1"));
            m = Double.parseDouble(configIPRA.getString("config.weibullpara2"));
            MonClient = new getClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("monitoringport")), configOptimis.getString("monitoringpath"));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        WeibullDistribution wbd = new WeibullDistributionImpl(a, m);
        log.info("IPRA: hostName is: " + hostName);
        MonitoringResourceDatasets mrd = MonClient.getLatestReportForPhysical(hostName);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();

        for (MonitoringResourceDataset resource : resources) {
            if (resource.getMetric_name().equals("last_reboot")) {
                last_reboot_ts = resource.getMetric_value().toString();
                log.debug("IPRA: last reboot time stamp is: " + last_reboot_ts);
                alreadyOnTime = Long.parseLong(last_reboot_ts.substring(last_reboot_ts.indexOf('(') + 1, last_reboot_ts.indexOf(')')));
                log.debug("IPRA: elapse_time_secs: " + alreadyOnTime);
                break;
            }
        }

        try {
            pof = (wbd.cumulativeProbability(alreadyOnTime + timePeriod * 3600) - wbd.cumulativeProbability(alreadyOnTime)) / (1 - wbd.cumulativeProbability(alreadyOnTime));
            log.debug("IPRA: Prob(X < " + timePeriod + " hours) is " + pof);
        } catch (Exception e) {
        }

        return pof;
    }

    public List<Double> calculatePhyHostPoFs(Map<String, Long> hosts) {
        List<Double> PoFs = new ArrayList<Double>();
        Iterator<?> it = hosts.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes") //FIXME
            Map.Entry host_time_pairs = (Map.Entry) it.next();
            String hostName = (String) host_time_pairs.getKey();
            double pof = 0.0;
            if (host_time_pairs.getValue() != null) {
                Long hostTime = (Long) host_time_pairs.getValue();
                pof = calculatePhyHostPoF(hostName, hostTime);
            } else {
                pof = calculatePhyHostPoF(hostName, null);
            }
            PoFs.add(pof);
        }
        return PoFs;
    }

    private DsAhpProviderObject[] rankSPs(Object[] serviceProviders) {

        ServiceProviderEvaluatorByIP spe = new ServiceProviderEvaluatorByIP();

        DsAhpProviderObject[] rankedProviders = null;

        try {
            rankedProviders = spe.rankServiceProviderComDBIP(serviceProviders);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return rankedProviders;
    }

    public int calculateImpactLevelOfPhyHostFailure() {
        return 5;
    }

    public int calculateImpactLevelOfSLAFailure() {
        return 7;
    }

    public int calculateRiskLevelOfPhyHostFailure(String phyHostName, Long timePeriod) {
        if (timePeriod == null) {
            timePeriod = new Long(24);
        }
        double pof = calculatePhyHostPoF(phyHostName, timePeriod);
        int poflevel = RiskLevelConverter.convertPoFLevel(pof);
        int impactlevel = calculateImpactLevelOfPhyHostFailure();
        int risklevel = poflevel * impactlevel;
        return RiskLevelConverter.convertRiskLevel(risklevel);

    }

    public List<Integer> calculateRiskLevelsOfPhyHostFailures(Map<String, Long> hosts) {

        List<Integer> RiskLevels = new ArrayList<Integer>();
        Iterator<?> it = hosts.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes") //FIXME
            Map.Entry host_time_pairs = (Map.Entry) it.next();
            String hostName = (String) host_time_pairs.getKey();
            int risklevel = 1;
            if (host_time_pairs.getValue() != null) {
                Long hostTime = (Long) host_time_pairs.getValue();
                risklevel = calculateRiskLevelOfPhyHostFailure(hostName, hostTime);
            } else {
                risklevel = calculateRiskLevelOfPhyHostFailure(hostName, null);
            }
            RiskLevels.add(risklevel);
        }
        return RiskLevels;
    }

    private void updateRiskDB(String serviceID, int riskLevel) {
        try {
            log.info("IPRA: riskPropagator method starts ......");
            RiskPropagator riskPropagator = new RiskPropagator(RiskPropagator.PROVIDERTYPE_IP, RiskPropagator.SERVICEPHASE_DEPLOYMENT, IPName, serviceID, RiskPropagator.GRAPHTYPE_IP_DEPLOYMENT_SLA_RISKLEVEL);
            riskPropagator.setRiskValue(riskLevel);
            riskPropagator.addRiskValue();
            log.info("IPRA: riskPropagator method stops ......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
