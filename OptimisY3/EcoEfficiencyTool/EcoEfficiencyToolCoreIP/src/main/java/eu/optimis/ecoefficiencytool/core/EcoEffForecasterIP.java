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
import eu.optimis.ecoefficiencytool.core.tools.*;
import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolValidatorRESTClient;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import java.text.DecimalFormat;
import java.util.*;
import net.emotivecloud.utils.ovf.OVFWrapper;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 *
 * @author jsubirat
 */
public class EcoEffForecasterIP {

    protected static Logger log = Logger.getLogger(EcoEffForecasterIP.class);
    private CloudOptimizerRESTClient co;
    private getClient mi_client;
    private InfrastructureMetrics metrics;
    private EnergyEstimator energyEstimator;
    private CertificateTools certificates;
    private double PUE;
    private double initialCPUUtilization;
    private HashMap<String, VariableEstimator> vmCPUPredictors;
    //private HashMap<String, HashMap<Long, Double>> vmUtilizations;
    private List<String> deploymentMessages;
    private EcoEfficiencyToolValidatorRESTClient cpuValidator;

    public EcoEffForecasterIP(HashMap<String, VariableEstimator> vmCPUPredictors, EnergyEstimator energyEstimator, InfrastructureMetrics metrics) {
        //log = Log.getLog(getClass());
        log.debug("Starting EcoEfficiency Forecaster (**IP**)");
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);

        co = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"));
        mi_client = new getClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("monitoringport")), configOptimis.getString("monitoringpath"));

        deploymentMessages = new LinkedList<String>();

        PUE = Double.parseDouble(configEco.getString("PUE"));
        initialCPUUtilization = Double.parseDouble(configEco.getString("initialCPUUtilization"));
        this.vmCPUPredictors = vmCPUPredictors;
        //this.vmUtilizations = vmUtilizations;
        this.certificates = new CertificateTools();
        this.metrics = metrics;
        this.energyEstimator = energyEstimator;
        cpuValidator = new EcoEfficiencyToolValidatorRESTClient(configOptimis.getString("optimis-ipvm"));
    }

    public synchronized String forecastVMEcoEfficiency(String vmId, String type, Long timeSpan) {
        double[] ecoefficiencyForecast = forecastVMEcoEfficiency(vmId, timeSpan);
        //Return desired ecoefficiency forecast type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiencyForecast[0]);
        } else {
            return Double.toString(ecoefficiencyForecast[1]);
        }
    }

    /**
     * Forecasts the ecoefficiency of a VM.
     *
     * @param vmId VM identifier.
     * @param timeSpan Time in the future in which the prediction will be made.
     * @return
     */
    public synchronized double[] forecastVMEcoEfficiency(String vmId, Long timeSpan) {

        double futPerfPower[];
        double ret[] = {-1.0, -1.0};

        //Check of parameters
        if (vmId == null) {
            log.error("VM identifier can't be null.");
            return ret;
        }

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Performance (perfPower[0]) and Real Power consumed (perfPower[1]) calculation.
        try {
            futPerfPower = getVMFuturePerformanceAndPower(vmId, timeSpan);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ret;
        }

        //Ecoefficiency calculation and update.
        double energyEfficiencyForecast = (futPerfPower[0]) / (futPerfPower[1] * PUE);
        double ecologicalEfficiencyForecast = (futPerfPower[0]) / (CO2Converter.getCO2FromPower(futPerfPower[1] * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("VM: " + vmId + " EnEffForec: " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);
        ret[0] = energyEfficiencyForecast;
        ret[1] = ecologicalEfficiencyForecast;

        return ret;
    }

    public synchronized String forecastNodeEcoEfficiency(String nodeId, List<String> ovfs, String type, Long timeSpan) {
        double[] ecoefficiencyForecast = forecastNodeEcoEfficiency(nodeId, ovfs, timeSpan);
        //Return desired ecoefficiency forecast type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiencyForecast[0]);
        } else {
            return Double.toString(ecoefficiencyForecast[1]);
        }
    }

    /**
     * Forecasts the ecoefficiency of a node. VMs in the "ovfs" parameter which
     * exist in the system will be considered as undeployments, while
     * non-present VMs specified in the "ovfs" parameter will be treated as new
     * deployments.
     *
     * @param nodeId Physical node identifier.
     * @param ovfs List of OVF descriptors of the VMs to deploy (if not existing
     * in the node) or undeploy (if existing in the node), generated using
     * EMOTIVE’s OVFWrapper (see Installation Guide).
     * @param timeSpan Time in the future in which the prediction will be made.
     * @return
     */
    public synchronized double[] forecastNodeEcoEfficiency(String nodeId, List<String> ovfs, Long timeSpan) {

        double ret[] = {-1.0, -1.0};

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Obtain lists of VMs under deployment or undeployment.
        List<OVFWrapper> deployments = new LinkedList<OVFWrapper>();
        List<String> undeployments = new LinkedList<String>();
        if (ovfs != null) {
            for (String ovf : ovfs) {
                try {
                    OVFWrapper tempOVF = new OVFWrapper(ovf);
                    if (co.getVMsId(nodeId).contains(tempOVF.getId())) {
                        log.debug("Undeployment evaluation. VM " + tempOVF.getId());
                        undeployments.add(co.getVMName(tempOVF.getId()));
                    } else {
                        log.debug("Deployment evaluation. VM " + tempOVF.getId());
                        deployments.add(tempOVF);
                    }
                } catch (Exception ex) {
                    log.error("Error while parsing received OVF.");
                    log.error(ex.getMessage());
                }
            }
        }

        //Obtain node future performance and power consumption, considering deployments and undeployments.
        double futPerfPower[] = null;
        try {
            futPerfPower = getNodeFuturePerformanceAndPower(nodeId, deployments, undeployments, null, timeSpan);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ret;
        }

        //Ecoefficiency calculation and update.
        double energyEfficiencyForecast = (futPerfPower[0]) / (futPerfPower[1] * PUE);
        double ecologicalEfficiencyForecast = (futPerfPower[0]) / (CO2Converter.getCO2FromPower(futPerfPower[1] * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("Node: " + nodeId + " EnEffForec: " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);
        ret[0] = energyEfficiencyForecast;
        ret[1] = ecologicalEfficiencyForecast;

        return ret;
    }

    /**
     * Forecasts the ecoefficiency of a given list of nodes, using the default
     * timeSpan value.
     *
     * @param nodeList List of nodes to forecast its ecoefficiency.
     * @return The predicted ecoefficiency of each node following the same order
     * as in the input. (Each position of the returned List is a Double into a
     * String).
     */
    public synchronized List<String> forecastMultipleNodesEfficiency(List<String> nodeList, String type) {
        return forecastMultipleNodesEfficiency(nodeList, type, new Long(Constants.DEFAULT_TIMESPAN));
    }

    /**
     * Forecasts the ecoefficiency of a given list of nodes, at timeSpan
     * milliseconds in the future.
     *
     * @param nodeList List of nodes to forecast its ecoefficiency.
     * @param timeSpan Milliseconds in the future at which the prediction will
     * be made.
     * @return The predicted ecoefficiency of each node following the same order
     * as in the input. (Each position of the returned List is a Double into a
     * String).
     */
    public synchronized List<String> forecastMultipleNodesEfficiency(List<String> nodeList, String type, Long timeSpan) {
        return forecastMultipleNodesEfficiency(nodeList, null, type, timeSpan);
    }

    public synchronized List<String> forecastMultipleNodesEfficiency(List<String> nodeList, List<String> ovfs, String type, Long timeSpan) {
        List<String> ret = new LinkedList<String>();

        for (String nodeId : nodeList) {
            nodeId = nodeId.trim();
            ret.add(forecastNodeEcoEfficiency(nodeId, ovfs, type, timeSpan));
        }
        return ret;
    }

    public synchronized String forecastIPEcoEfficiency(String type, Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency.");
        return this.forecastIPEcoEfficiencyServiceDeployment(null, type, timeSpan);
    }

    public synchronized double[] forecastIPEcoEfficiency(Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency.");
        return forecastIPEcoEfficiencyServiceDeployment(null, timeSpan);
    }

    public synchronized String forecastIPEcoEfficiencyServiceDeployment(String manifest, String type, Long timeSpan) {

        double[] ecoefficiencyForecast = forecastIPEcoEfficiencyServiceDeployment(manifest, timeSpan);
        //Return desired ecoefficiency forecast type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiencyForecast[0]);
        } else {
            return Double.toString(ecoefficiencyForecast[1]);
        }
    }

    /**
     * Forecasts the EcoEfficiency of the IP provider when deploying a new
     * service.
     *
     * @param manifest Manifest describing the VMs to be deployed in the
     * infrastructure.
     * @param timeSpan Time in the future in which the prediction will be made.
     * @return The IP EcoEfficiency prediction.
     */
    public synchronized double[] forecastIPEcoEfficiencyServiceDeployment(String manifest, Long timeSpan) {

        double ret[] = {-1.0, -1.0};


        if (timeSpan == null) {
            //TODO This shouldn't condition the timespan. Even if manifest is present, we can estimate the non-variant part of the IP eco.
            if (manifest == null) {
                timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
            } else {
                timeSpan = new Long(0);
            }
        }

        //Estimating infrastructure performance and forecast (without any deployment)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : co.getNodesId()) {
            try {
                double temp[] = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                totalPerformanceForecast += temp[0];
                totalPowerForecast += temp[1];
            } catch (Exception ex) {
                log.error("Couldn't get node " + nodeId + " performance and power forecasts.");
                log.error(ex.getMessage());
                return ret;
            }
        }

        //Adding performance and power required by manifest.
        Manifest parsedManifest = null;
        String serviceId = null;
        if (manifest != null) {
            log.debug("Forecasting infrastructure ecoefficiency upon service deployment.");

            //Parse Service Manifest
            log.debug("Parsing manifest...");
            try {
                parsedManifest = Manifest.Factory.newInstance(manifest);
                serviceId = parsedManifest.getVirtualMachineDescriptionSection().getServiceId();
            } catch (Exception ex) {
                log.error("Couldn't parse Service Manifest.");
                log.error(ex.getMessage());
                return ret;
            }
            addDeploymentMessage("Infrastructure-level service " + serviceId + " deployment eco-forecast.");

            //Checking of certificates
            if (certificates.checkCertificates(manifest) == false) {
                log.info("One or more certificate requirements wasn't fulfilled. Service can't be accepted.");
                addDeploymentMessage("Certificate requirements weren't fulfilled for service " + serviceId + ".\nNo additional performance or power considered at infrastructure-level.\n");
                ret[0] = 0.0;
                ret[1] = 0.0;
                return ret;
            } else {
                //Adding the addicional performance and power required to run the service.
                double perfPowServiceForecast[] = getNewServiceFuturePerformanceAndPower(parsedManifest);
                totalPerformanceForecast += perfPowServiceForecast[0];
                totalPowerForecast += perfPowServiceForecast[1];
            }
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        if (manifest == null) {
            log.info("IP EnEffForec: " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);
        } else {
            log.info("IP EnEffForec (Service " + parsedManifest.getVirtualMachineDescriptionSection().getServiceId() + " deployment): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);
            DecimalFormat df = new DecimalFormat("####.##");
            addDeploymentMessage("Energy Efficiency Forecast: " + df.format(new Double(energyEfficiencyForecast)) + "MWIPS/W. Ecological Efficiency Forecast: " + df.format(new Double(ecologicalEfficiencyForecast)) + "MWIPS/(grCO2/s).\n");
        }
        ret[0] = energyEfficiencyForecast;
        ret[1] = ecologicalEfficiencyForecast;

        return ret;
    }

    public synchronized String forecastIPEcoEfficiencyVMCancellation(String vmId, String type, Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency upon VM cancellation.");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Determining in which node VM is placed.
        String vmNode = co.getNodeId(vmId);
        List<String> vmRemoveList = new LinkedList<String>();
        vmRemoveList.add(vmId);

        //Estimating infrastructure performance and forecast (without any deployment)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : co.getNodesId()) {
            try {
                double temp[] = null;
                if (vmNode.equalsIgnoreCase(nodeId)) {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, vmRemoveList, null, timeSpan);
                } else {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                }
                totalPerformanceForecast += temp[0];
                totalPowerForecast += temp[1];
            } catch (Exception ex) {
                log.error("Couldn't get node " + nodeId + " performance and power forecasts.");
                log.error(ex.getMessage());
                return "-1.0";
            }
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("IP EnEffForec (Cancelling VM " + vmId + " in host " + vmNode + "): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);

        //Return desired ecoefficiency forecasted type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(energyEfficiencyForecast);
        } else {
            return Double.toString(ecologicalEfficiencyForecast);
        }
    }

    public synchronized String forecastIPEcoEfficiencyVMDeploymentKnownPlacement(OVFWrapper ovfDom, String destNode, List<String> activeNodes, String type, Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency upon VM deployment on node " + destNode + " .");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        if (activeNodes == null) {
            activeNodes = co.getNodesId();
        }

        List<OVFWrapper> deployments = new LinkedList<OVFWrapper>();
        deployments.add(ovfDom);

        //Estimating infrastructure performance and forecast (without any deployment)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : co.getNodesId()) {
            if (activeNodes.contains(nodeId)) {
                try {
                    double temp[] = null;
                    if (destNode.equalsIgnoreCase(nodeId)) {
                        temp = getNodeFuturePerformanceAndPower(nodeId, deployments, null, null, timeSpan);
                    } else {
                        temp = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                    }
                    totalPerformanceForecast += temp[0];
                    totalPowerForecast += temp[1];
                } catch (Exception ex) {
                    log.debug("Couldn't get node " + nodeId + " performance and power forecasts.");
                    log.error(ex.getMessage());
                    return "-1.0";
                }
            }
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("IP EnEffForec (Deploying VM " + ovfDom.getId() + " in host " + destNode + "): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);

        //Return desired ecoefficiency forecasted type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(energyEfficiencyForecast);
        } else {
            return Double.toString(ecologicalEfficiencyForecast);
        }
    }

    public synchronized String forecastIPEcoEfficiencyVMMigrationKnownPlacement(String vmId, String destNode, List<String> activeNodes, String type, Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency upon VM migration.");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        if (activeNodes == null) {
            activeNodes = co.getNodesId();
        }

        //Determining from which node VM is to be migrated.
        String vmMigrateOutNode = co.getNodeId(vmId);
        List<String> vmMigrateList = new LinkedList<String>();
        vmMigrateList.add(vmId);

        //Estimating infrastructure performance and forecast (without any deployment)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : activeNodes) {
            try {
                double temp[] = null;
                if (vmMigrateOutNode.equalsIgnoreCase(nodeId)) {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, vmMigrateList, null, timeSpan);
                } else if (destNode.equalsIgnoreCase(nodeId)) {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, null, vmMigrateList, timeSpan);
                } else {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                }
                totalPerformanceForecast += temp[0];
                totalPowerForecast += temp[1];
            } catch (Exception ex) {
                log.error("Couldn't get node " + nodeId + " performance and power forecasts.");
                log.error(ex.getMessage());
                return "-1.0";
            }
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("IP EnEffForec (Migrating VM " + vmId + " from host " + vmMigrateOutNode + " to host " + destNode + "): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);

        //Return desired ecoefficiency forecasted type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(energyEfficiencyForecast);
        } else {
            return Double.toString(ecologicalEfficiencyForecast);
        }
    }

    public synchronized String forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(OVFWrapper ovfDom, String type, Long timeSpan) {

        log.debug("Forecasting infrastructure ecoefficiency upon service deployment.");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Estimating infrastructure performance and forecast (without any deployment)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : co.getNodesId()) {
            try {
                double temp[] = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                totalPerformanceForecast += temp[0];
                totalPowerForecast += temp[1];
            } catch (Exception ex) {
                log.error("Couldn't get node " + nodeId + " performance and power forecasts.");
                log.error(ex.getMessage());
                return "-1.0";
            }
        }

        //Adding performance and power required to run the new VM
        if (ovfDom != null) {
            double perfPowVMForecast[] = getNewVMFuturePerformanceAndPowerUnknownPlacement(ovfDom, null);
            totalPerformanceForecast += perfPowVMForecast[0];
            totalPowerForecast += perfPowVMForecast[1];
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("IP EnEffForec (Deployment of VM " + ovfDom.getId() + ", unknown placement): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);

        //Return desired ecoefficiency forecasted type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(energyEfficiencyForecast);
        } else {
            return Double.toString(ecologicalEfficiencyForecast);
        }
    }

    public synchronized String forecastIPEcoEfficiencyVMMigrationUnknownPlacement(String vmId, String type, Long timeSpan) {
        log.debug("Forecasting infrastructure ecoefficiency upon VM migration (unknown destination yet).");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Determining in which node VM is placed.
        String vmNode = co.getNodeId(vmId);
        List<String> vmRemoveList = new LinkedList<String>();
        vmRemoveList.add(vmId);

        //Estimating infrastructure performance and forecast (taking out the VM to be migrated)
        double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
        for (String nodeId : co.getNodesId()) {
            try {
                double temp[] = null;
                if (vmNode.equalsIgnoreCase(nodeId)) {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, vmRemoveList, null, timeSpan);
                } else {
                    temp = getNodeFuturePerformanceAndPower(nodeId, null, null, null, timeSpan);
                }
                totalPerformanceForecast += temp[0];
                totalPowerForecast += temp[1];
            } catch (Exception ex) {
                log.error("Couldn't get node " + nodeId + " performance and power forecasts.");
                log.error(ex.getMessage());
                return "-1.0";
            }
        }
        try {
            //Adding performance (same as if it wasn't migrated)
            double futureVMPerformance = getVMFuturePerformanceAndPower(vmId, timeSpan)[0];
            double futureCPUMeanPower = energyEstimator.getFutureCPUMeanPower(0, null, true); //0 because no extra CPUs will be used, as the VM is already present and being migrated.
            double futureVMPower = ((double) metrics.getCPUNumber(vmId)) * futureCPUMeanPower;

            totalPerformanceForecast += futureVMPerformance;
            totalPowerForecast += futureVMPower;
        } catch (Exception ex) {
            log.error("Error while obtaining VM future performance and forecast upon migration.");
            log.error(ex.getMessage());
        }

        //Ecoefficiency forecast calculation.
        double energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
        double ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
        Date date = new Date();
        long futureTimeStamp = date.getTime() + timeSpan.longValue();
        log.info("IP EnEffForec (Migrating VM " + vmId + ", unknown placement): " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast + " FutTS: " + futureTimeStamp);

        //Return desired ecoefficiency forecasted type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(energyEfficiencyForecast);
        } else {
            return Double.toString(ecologicalEfficiencyForecast);
        }
    }

    public synchronized String forecastServiceEcoEfficiency(String manifest, String type, Long timeSpan) {
        double[] ecoefficiencyForecast = forecastServiceEcoEfficiency(null, timeSpan, manifest);
        //Return desired ecoefficiency forecast type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiencyForecast[0]);
        } else {
            return Double.toString(ecoefficiencyForecast[1]);
        }
    }

    /**
     * Predicts the expected eco-efficiency of a given service. If the service
     * is already deployed it will be calculated based on the current number of
     * VMs (components) of the service. If the service is not deployed (new
     * deployment situation) and typeIdReplicas is null, it will be assumed that
     * it will require one replica of each component when performing the
     * prediction. Otherwise, it will take into account the amount of replicas
     * to be deployed of each component type when performing the prediction.
     *
     * @param manifest Service Manifest.
     * @param typeIdReplicas Number of VM replicas of each type to be deployed.
     * @param timeSpan Time in the future in which the prediction will be made.
     * @return Predicted ecoefficiency in timeSpan milliseconds (Double into a
     * String).
     */
    public synchronized double[] forecastServiceEcoEfficiency(String serviceId, Long timeSpan, String manifest) {

        double ret[] = {-1.0, -1.0};
        double energyEfficiencyForecast, ecologicalEfficiencyForecast;

        log.debug("Forecasting service ecoefficiency.");

        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        //Parse Service Manifest
        //log.info("Parsing manifest...");
        Manifest parsedManifest = null;
        if (serviceId == null && manifest != null) {
            try {
                parsedManifest = Manifest.Factory.newInstance(manifest);
                serviceId = parsedManifest.getVirtualMachineDescriptionSection().getServiceId();
                addDeploymentMessage("Service-level service " + serviceId + " deployment eco-forecast.");
            } catch (Exception ex) {
                log.error("Couldn't parse Service Manifest. Error: \n", ex);
                return ret;
            }

            //Checking of certificates
            if (certificates.checkCertificates(manifest) == false) {
                log.info("One or more certificate requirements wasn't fulfilled.  Service can't be accepted.");
                addDeploymentMessage("Certificate requirements weren't fulfilled for service " + serviceId + ". Returned 0.\n");
                ret[0] = 0.0;
                ret[1] = 0.0;
                return ret;
            }
        }

        List<String> vmsId = co.getVMsIdsOfService(serviceId);
        vmsId = co.getVMsIdsOfService(serviceId);
        if (vmsId != null) {
            if (vmsId.size() > 0) {

                double totalPerformanceForecast = 0.0, totalPowerForecast = 0.0;
                for (String vmId : vmsId) {
                    try {
                        double perfPowerForecast[] = getVMFuturePerformanceAndPower(vmId, timeSpan);
                        totalPerformanceForecast += perfPowerForecast[0];
                        totalPowerForecast += perfPowerForecast[1];
                    } catch (Exception ex) {
                        log.error("Couldn't obtain VM " + vmId + " predicted performance and power.");
                        log.error(ex.getMessage());
                        return ret;
                    }
                }


                //Ecoefficiency Forecast calculation.
                energyEfficiencyForecast = (totalPerformanceForecast) / (totalPowerForecast * PUE);
                ecologicalEfficiencyForecast = (totalPerformanceForecast) / (CO2Converter.getCO2FromPower(totalPowerForecast * PUE));
                ret[0] = energyEfficiencyForecast;
                ret[1] = ecologicalEfficiencyForecast;

                log.info("Service " + serviceId + " EnEffForec: " + energyEfficiencyForecast + " EcoEffForec: " + ecologicalEfficiencyForecast);


            } else {
                if (parsedManifest != null) {
                    log.debug("New deployment evaluation.");

                    double perfPowPrediction[] = getNewServiceFuturePerformanceAndPower(parsedManifest);

                    //Ecoefficiency Forecast calculation.
                    energyEfficiencyForecast = (perfPowPrediction[0]) / (perfPowPrediction[1] * PUE);
                    ecologicalEfficiencyForecast = (perfPowPrediction[0]) / (CO2Converter.getCO2FromPower(perfPowPrediction[1] * PUE));

                    DecimalFormat df = new DecimalFormat("####.##");
                    addDeploymentMessage("Energy Efficiency Forecast: " + df.format(new Double(energyEfficiencyForecast)) + "MWIPS/W. Ecological Efficiency Forecast: " + df.format(new Double(ecologicalEfficiencyForecast)) + "MWIPS/(grCO2/s).\n");

                    ret[0] = energyEfficiencyForecast;
                    ret[1] = ecologicalEfficiencyForecast;

                    log.info("Service " + serviceId + " deployment EnEff: " + energyEfficiencyForecast + " EcoEff: " + ecologicalEfficiencyForecast);

                } else {
                    log.error("Parsed manifest was null, new deployment can't be evaluated.");
                }
            }
        } else {
            log.error("Obtained a null list of VMs from CO.");
            return ret;
        }

        return ret;
    }

    /**
     * **********************Internal Methods*******************************
     */
    private double[] getNodeFuturePerformanceAndPower(String nodeId, List<OVFWrapper> deployments, List<String> undeploymentNames, List<String> migrationVmIds, Long timeSpan) throws Exception {

        double perfPowerPrediction[] = new double[2];

        //Checking of parameters
        if (undeploymentNames == null) {
            undeploymentNames = new LinkedList<String>();
        }
        if (deployments == null) {
            deployments = new LinkedList<OVFWrapper>();
        }

        if (migrationVmIds == null) {
            migrationVmIds = new LinkedList<String>();
        }

        //Obtain xentop_cpu report from Monitoring Infrastructure.
        String xenTopReport = null;
        MonitoringResourceDatasets nodeReportForEnergy = null;
        try {
            nodeReportForEnergy = mi_client.getLatestReportForEnergy(nodeId);
        } catch (Exception ex) {
            log.error("Error while obtaining energy values from the Energy Collector.");
            log.error(ex.getMessage());
            throw new Exception("Error while obtaining energy values from the Energy Collector.");
        }
        List<MonitoringResourceDataset> nodeEnergyMetrics = nodeReportForEnergy.getMonitoring_resource();
        for (MonitoringResourceDataset metric : nodeEnergyMetrics) {
            if (metric.getMetric_name().equalsIgnoreCase("xentop_cpu")) {
                xenTopReport = metric.getMetric_value();
            }
        }
        if (xenTopReport == null) {
            log.error("Obtained null xen_top values from the Monitoring");
            throw new Exception("Obtained null xen_top values from the Monitoring");
        }

        //Obtain which VMs are present in the node (including Domain-0) and forecast its CPU utilization. Discard undeployment utilizations.
        double vmsCpuUtilization = 0.0;
        double dom0CpuUtilization = 0.0;
        String tmp[] = xenTopReport.split(";");
        for (int i = 0; i < tmp.length; i++) {
            String domNameVsCpu[] = tmp[i].split(" ");
            if (domNameVsCpu.length == 2) {
                if (!undeploymentNames.contains(domNameVsCpu[0])) {
                    if (!domNameVsCpu[0].equalsIgnoreCase("Domain-0")) {
                        double tmpCPU = forecastVMCpuUtilization(domNameVsCpu[0], timeSpan);
                        if (tmpCPU > 0.0) {
                            vmsCpuUtilization += tmpCPU;
                        }
                    } else {
                        dom0CpuUtilization = forecastVMCpuUtilization("Domain-0".concat(nodeId), timeSpan);
                    }
                } else {
                    log.debug("VM " + domNameVsCpu[0] + " is not considered in the calculation (VM cancellation).");
                }
            } else {
                log.debug("Invalid Domain CPU lecture.");
            }
        }

        //Calculate additional CPU utilization and power incurred by the new VMs under deployment.
        double additionalCPURequired = 0.0;
        for (OVFWrapper ovfDom : deployments) {
            additionalCPURequired += ((double) ovfDom.getCPUsNumber()) * (initialCPUUtilization / ((double) metrics.getCPUNumber(nodeId)));
        }

        //Calculate additional CPU utilization incurred by the new VMs being migrated to the node.
        for (String vmId : migrationVmIds) {
            double vmFuturePerformance = this.getVMFuturePerformanceAndPower(vmId, timeSpan)[0];
            log.debug("VM " + vmId + " will require " + vmFuturePerformance + " MWIPS. Will result in " + vmFuturePerformance / metrics.getNodeBenchmarkResult(nodeId) + " additional CPU required.");
            additionalCPURequired += vmFuturePerformance / metrics.getNodeBenchmarkResult(nodeId);
        }

        //Add the forecasted additional CPU utilization to the VMs. If it's greater than 100.0, correct.
        vmsCpuUtilization += additionalCPURequired;
        if (vmsCpuUtilization + dom0CpuUtilization > 100.0) {
            vmsCpuUtilization = 100.0 - dom0CpuUtilization;
        } else if (vmsCpuUtilization < 0.0) {
            vmsCpuUtilization = 0.0;
        }

        //Obtain the future performance taking into account the forecasted CPU utilizations.
        double futurePerformance = (vmsCpuUtilization / 100.0) * metrics.getNodeBenchmarkResult(nodeId);
        double futurePower = energyEstimator.estimatePowerConsumption(nodeId, vmsCpuUtilization + dom0CpuUtilization);

        perfPowerPrediction[0] = futurePerformance;
        perfPowerPrediction[1] = futurePower;
        return perfPowerPrediction;
    }

    /**
     * Forecasts the performance which delivered to this VMs, as well as the
     * real power consumed by it, after timeSpan milliseconds.
     *
     * @param vmId VM identifier
     * @return A vector containing the predicted performance delivered to the VM
     * in the first position and the predicted real power consumed by it on the
     * second position.
     * @throws Exception
     */
    private double[] getVMFuturePerformanceAndPower(String vmId, Long timeSpan) throws Exception {

        double futPerfPow[] = new double[2];
        String nodeId = null;
        String vmName = null;
        int numVmsNode = 0;

        nodeId = co.getNodeId(vmId);
        if (nodeId == null) {
            log.error("VM " + vmId + " was not present in the system.");
            throw new Exception("VM " + vmId + " was not present in the system.");
        }
        List<String> temp = co.getVMsId(nodeId);
        if (temp == null) {
            log.error("Error while retreiving number of VMs in node.");
            throw new Exception("Error while retreiving number of VMs in node.");
        }
        vmName = co.getVMName(vmId);
        if (vmName == null) {
            log.error("Could not retreive VM name.");
            throw new Exception("Could not retreive VM name.");
        }

        double vmsCpuUtilization = 0.0;
        double vmCpuUtilization = 0.0;
        double dom0CpuUtilization = 0.0;

        String xenTopReport = null;
        MonitoringResourceDatasets nodeReportForEnergy = null;
        try {
            nodeReportForEnergy = mi_client.getLatestReportForEnergy(nodeId);
        } catch (Exception ex) {
            log.error("Error while obtaining energy values from the Energy Collector.");
            log.error(ex.getMessage());
            throw new Exception("Error while obtaining energy values from the Energy Collector.");
        }
        List<MonitoringResourceDataset> nodeEnergyMetrics = nodeReportForEnergy.getMonitoring_resource();
        for (MonitoringResourceDataset metric : nodeEnergyMetrics) {
            if (metric.getMetric_name().equalsIgnoreCase("xentop_cpu")) {
                xenTopReport = metric.getMetric_value();
            }
        }
        if (xenTopReport == null) {
            log.error("Obtained null xen_top values from the Monitoring");
            throw new Exception("Obtained null xen_top values from the Monitoring.");
        }

        String tmp[] = xenTopReport.split(";");
        for (int i = 0; i < tmp.length; i++) {
            String domNameVsCpu[] = tmp[i].split(" ");
            if (domNameVsCpu.length == 2) {
                if (!domNameVsCpu[0].equalsIgnoreCase("Domain-0")) {
                    numVmsNode++;
                    double tmpCPU = forecastVMCpuUtilization(domNameVsCpu[0], timeSpan);
                    if (tmpCPU > 0.0) {
                        vmsCpuUtilization += tmpCPU;
                        if (domNameVsCpu[0].equalsIgnoreCase(vmName)) {
                            vmCpuUtilization = tmpCPU;
                        }
                    }
                } else {
                    dom0CpuUtilization = forecastVMCpuUtilization("Domain-0".concat(nodeId), timeSpan);
                }
            } else {
                log.debug("Invalid Domain CPU lecture.");
            }
        }

        if (vmsCpuUtilization + dom0CpuUtilization > 100.0) {
            vmsCpuUtilization = 100.0 - dom0CpuUtilization;
        } else if (vmsCpuUtilization < 0.0) {
            vmsCpuUtilization = 0.0;
        }

        double totalcpuforecast = vmsCpuUtilization + dom0CpuUtilization;

        double futurePerformance = (vmCpuUtilization / 100.0) * metrics.getNodeBenchmarkResult(nodeId);
        double futurePower = energyEstimator.estimatePowerConsumption(nodeId, totalcpuforecast) * ((vmCpuUtilization + dom0CpuUtilization / ((double) numVmsNode)) / (totalcpuforecast));

        futPerfPow[0] = futurePerformance;
        futPerfPow[1] = futurePower;
        return futPerfPow;
    }

    private double[] getNewVMFuturePerformanceAndPowerUnknownPlacement(OVFWrapper ovfDom, List<String> activeNodes) {
        double futPerfPow[] = new double[2];

        double totalPerformance = ((double) ovfDom.getCPUsNumber()) * metrics.getCPUMeanPerformance(activeNodes) * (initialCPUUtilization / 100.0);
        double futureCPUMeanPower = 0.0;
        futureCPUMeanPower = energyEstimator.getFutureCPUMeanPower(0, activeNodes, true);
        double totalPower = ((double) ovfDom.getCPUsNumber()) * futureCPUMeanPower;

        futPerfPow[0] = totalPerformance;
        futPerfPow[1] = totalPower;

        return futPerfPow;
    }

    private double[] getNewServiceFuturePerformanceAndPower(Manifest parsedManifest) {
        double futPerfPow[] = new double[2];

        //TODO take into consideration replicas in SM.
        int numCpus = 0;
        for (VirtualMachineComponent vmOvf : parsedManifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentArray()) {
            numCpus += vmOvf.getAllocationConstraints().getInitial() * vmOvf.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().getNumberOfVirtualCPUs();
        }

        double totalPerformance = ((double) numCpus) * metrics.getCPUMeanPerformance(null) * (initialCPUUtilization / 100.0);
        double futureCPUMeanPower = energyEstimator.getFutureCPUMeanPower(numCpus, null, true);
        double totalPower = ((double) numCpus) * futureCPUMeanPower;

        DecimalFormat df = new DecimalFormat("#.##");
        addDeploymentMessage("Num CPUs: " + numCpus + "Initial CPU Utilization: " + initialCPUUtilization + "% Performance delivered: " + df.format(new Double(totalPerformance)) + "MWIPS Power delivered: " + df.format(new Double(totalPower)) + "W");

        futPerfPow[0] = totalPerformance;
        futPerfPow[1] = totalPower;

        return futPerfPow;
    }

    /**
     * Forecasts the CPU utilization of the VM identified by vmId at the time
     * specified in timeSpan based on previous CPU utilization values and using
     * Linear Regression.
     *
     * @param vmId Virtual Machine's Identifier.
     * @param timeSpan Future time specified in milliseconds since 1/1/1970.
     * @return Forecasted CPU utilization.
     */
    private synchronized double forecastVMCpuUtilization(String vmId, Long timeSpan) {
        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        VariableEstimator vmCPUPredictor = vmCPUPredictors.get(vmId);
        if (vmCPUPredictor != null) {
            Date currentDate = new Date();
            //TimeSpan is in minutes, converting it to milliseconds.
            long futurePredictionTimeStamp = currentDate.getTime() + timeSpan.longValue();
            double predictedUtilization = vmCPUPredictor.obtainBestForecast(futurePredictionTimeStamp);
            if (predictedUtilization < 0.0) {
                predictedUtilization = 0.0;
            } else if (predictedUtilization > 100.0) {
                predictedUtilization = 100.0;
            }
            cpuValidator.performForecasts(vmId, futurePredictionTimeStamp);
            return predictedUtilization;
        } else {
            log.error("No previous information was found of VM " + vmId + " to forecast its CPU usage.");
            return -1.0;
        }
    }

    /*
     * private synchronized double forecastVMCpuUtilization(String vmId, Long
     * timeSpan) {
     *
     * //TODO CHECK FOR NULL PARAMETERS. if (timeSpan == null) { timeSpan = new
     * Long(Constants.DEFAULT_TIMESPAN); }
     *
     * HashMap<Long, Double> vmUtilization = vmUtilizations.get(vmId); double
     * predictedUtilization = -1.0; if (vmUtilization != null) {
     *
     * if (vmUtilizations.size() <= 2) { log.error("Trying to interpolate with
     * only 1 previous utilization sample."); return -1.0; }
     *
     * //TODO Change this by timestamps. double xValues[] = new
     * double[vmUtilization.size()]; double yValues[] = new
     * double[vmUtilization.size()];
     *
     * Iterator it = vmUtilization.keySet().iterator(); int i = 0; while
     * (it.hasNext()) { Long tmpTimeStamp = (Long) it.next(); xValues[i] =
     * Double.parseDouble(tmpTimeStamp.toString()); yValues[i] =
     * vmUtilization.get(tmpTimeStamp); //System.out.println("**ECO-REMOVE:
     * Forecasting. Iteration: " + i + " Timestamp: " + tmpTimeStamp + " Value:
     * " + vmUtilization.get(tmpTimeStamp)); i++; }
     *
     * Date currentDate = new Date(); //TimeSpan is in minutes, converting it to
     * milliseconds. double futurePredictionTimeStamp = (double)
     * currentDate.getTime() + timeSpan.doubleValue();
     *
     * LinearRegression lr = new LinearRegression(xValues, yValues);
     * predictedUtilization = lr.calculateY(futurePredictionTimeStamp); if
     * (predictedUtilization < 0.0) { predictedUtilization = 0.0; } else if
     * (predictedUtilization > 100.0) { predictedUtilization = 100.0; } //Date
     * date = new Date(); //System.out.println("**ECO-REMOVE: Predicted
     * utilization: " + predictedUtilization + " Date: " + date.toString() + "
     * Timestamp: " + date.getTime()); } else { log.error("No previous
     * information was found of VM " + vmId + " to forecast its CPU usage.");
     * return -1.0; }
     *
     * //System.out.println("*******ECO-REMOVE: Forecast of VM " + vmId + ": "
     * + predictedUtilization);
     *
     * return predictedUtilization; }
     */
    private void addDeploymentMessage(String message) {
        if (deploymentMessages.size() == Constants.MAX_DEPLOYMENT_MESSAGES) {
            deploymentMessages.remove(0);
        }
        deploymentMessages.add(message);
    }

    public String getAllDeploymentMessages() {
        String ret = "";
        for (String message : deploymentMessages) {
            ret = ret.concat(message).concat("\n");
        }
        return ret;
    }
}
