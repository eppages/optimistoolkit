/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.core;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.ecoefficiencytool.core.tools.ConfigManager;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class ProactiveManager {

    protected static Logger log = Logger.getLogger(ProactiveManager.class);
    private EcoEffAssessorIP assessor;
    private EcoEffForecasterIP forecaster;
    private CloudOptimizerRESTClient co;
    private ProactiveInfrastructure proactiveInf;
    private HashMap<String, ProactiveService> proactiveServices;

    public ProactiveManager(EcoEffAssessorIP assessor, EcoEffForecasterIP forecaster) {
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);

        this.assessor = assessor;
        this.forecaster = forecaster;
        co = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"));
        proactiveServices = new HashMap<String, ProactiveService>();
        startInfrastructureAssessment(configEco.getLong("samplingPeriod"));
    }

    /**
     * Starts the assessment of a given IP (infrastructure).
     *
     * @param timeout Timeout between consecutive automatic eco-assessments.
     */
    public synchronized void startInfrastructureAssessment(Long timeout) {
        if (proactiveInf == null) {
            proactiveInf = new ProactiveInfrastructure(assessor, forecaster, timeout);
            proactiveInf.start();
            log.debug("Started infrastructure proactive ecoassessment.");
        }
    }

    /**
     * Stops the assessment of a given IP (infrastructure).
     */
    public synchronized void stopInfrastructureAssessment() {
        if (proactiveInf != null) {
            proactiveInf.stopProactiveInfrastructure();
            proactiveInf = null;
            log.debug("Stopped infrastructure proactive ecoassessment.");
        }
    }

    /**
     * Starts the proactive eco-assessment of a service.
     *
     * @param serviceId Service Identifier
     * @param timeout Timeout between consecutive automatic eco-assessments.
     */
    public synchronized void startServiceAssessment(String serviceId, Long timeout) {
        if (!co.getRunningServices().contains(serviceId)) {
            log.error("Service " + serviceId + " doesn't exist. It won't be automatically assessed.");
            return;
        }

        if (serviceId != null) {
            if (!proactiveServices.containsKey(serviceId)) {
                ProactiveService psertmp = new ProactiveService(assessor, forecaster, serviceId, timeout);
                psertmp.start();
                log.info("Started service " + serviceId + " automatic proactive eco-assessment (IP).");
                proactiveServices.put(serviceId, psertmp);
            } else {
                log.warn("The service '" + serviceId + "' is already under eco-efficiency assessment.");
            }
        } else {
            log.error("The service ID cannot be null!");
        }
    }

    /**
     * Stops the automatic assessment of a given service.
     */
    public synchronized void stopServiceAssessment(String serviceId) {
        if (serviceId != null) {

            if (proactiveServices.containsKey(serviceId)) {
                proactiveServices.remove(serviceId).stopProactiveService();
                log.info("Stopped service " + serviceId + " proactive ecoassessment.");
            } else {
                log.warn("Service '" + serviceId + "' isn't under eco-efficiency assessment.");
            }

            assessor.stopServiceAutoMonitoring(serviceId);
        } else {
            log.error("Service ID cannot be null!");
        }
    }

    public synchronized void setInfrastructureEcoThreshold(Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        if (proactiveInf != null) {
            if (energyEfficiencyTH != null) {
                proactiveInf.setIpEnEffTH(energyEfficiencyTH);
            }
            if (ecologicalEfficiencyTH != null) {
                proactiveInf.setIpEcoEffTH(ecologicalEfficiencyTH);
            }
        }
    }

    public synchronized void setNodeEcoThreshold(String nodeId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        if (proactiveInf != null) {
            if (energyEfficiencyTH != null) {
                proactiveInf.setNodeEnEffTH(nodeId, energyEfficiencyTH);
            }
            if (ecologicalEfficiencyTH != null) {
                proactiveInf.setNodeEcoEffTH(nodeId, ecologicalEfficiencyTH);
            }
        }
    }

    public synchronized void setServiceEcoThreshold(String serviceId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        if (proactiveServices.get(serviceId) != null) {
            if (energyEfficiencyTH != null) {
                proactiveServices.get(serviceId).setServiceEnEffTH(energyEfficiencyTH);
            }
            if (ecologicalEfficiencyTH != null) {
                proactiveServices.get(serviceId).setServiceEcoEffTH(ecologicalEfficiencyTH);
            }
        }
    }

    public synchronized void setVMEcoThreshold(String vmId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        String serviceId = co.getVMServiceId(vmId);
        if (serviceId != null) {
            if (proactiveServices.get(serviceId) != null) {
                if (energyEfficiencyTH != null) {
                    proactiveServices.get(serviceId).setVMEnEffTH(vmId, energyEfficiencyTH);
                }
                if (ecologicalEfficiencyTH != null) {
                    proactiveServices.get(serviceId).setVMEcoEffTH(vmId, ecologicalEfficiencyTH);
                }
            }
        }
    }
}
