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
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoIpTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoNodeTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoServiceTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoVMTableDAO;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import java.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * <b>Eco-Efficiency Assessment Tool (IP)</b> This component is in charge of
 * assessing ecological efficiency-related aspects, such as energy efficiency
 * and carbon emission levels, in a given Cloud infrastructure, node, or
 * service. In this sense, it uses the information needed from the OPTIMIS
 * monitoring infrastructure, i.e. the energy efficiency-related information
 * collector. Performed assessments are stored into the TREC Common DDBB for
 * further graphical representation.
 *
 * @version 2.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es) and Josep Subirats
 * (josep.subirats@bsc.es)
 */
public class EcoEffAssessorIP {

    protected static Logger log = Logger.getLogger(EcoEffAssessorIP.class);
    //Proactive Manger
    protected ProactiveManager proactiveManager;
    //External clients
    private CloudOptimizerRESTClient co;
    private getClient mi_client;
    //Fields to store previous assessments. Used to do forecasts, to be replaced by time series.
    private HashMap<String, VariableEstimator> vmCPUPredictors;
    //private HashMap<String, HashMap<Long, Double>> vmUtilizations; //VM LEVEL  <vmId,<timeStamp,utilization>
    //Datacenter specific parameters
    private double PUE;
    protected InfrastructureMetrics metrics;
    protected EnergyEstimator energyEstimator;
    protected EcoEffForecasterIP ecoForecaster;
    private EcoEfficiencyToolValidatorRESTClient validator;

    /**
     * Class constructor.
     */
    public EcoEffAssessorIP() {
        PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        log.debug("Starting EcoEfficiency Assessor (**IP**)");
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        //Copy if necessary the IP DDBB Config file.
        ConfigManager.getConfigFilePath(ConfigManager.IPDDBB_CONFIG_FILE);
        //Proactive agents


        //External clients
        co = new CloudOptimizerRESTClient(configOptimis.getString("optimis-ipvm"));
        mi_client = new getClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("monitoringport")), configOptimis.getString("monitoringpath"));

        vmCPUPredictors = new HashMap<String, VariableEstimator>();
        //vmUtilizations = new HashMap<String, HashMap<Long, Double>>();

        //Datacenter specific parameters
        PUE = Double.parseDouble(configEco.getString("PUE"));
        metrics = new InfrastructureMetrics(co);
        energyEstimator = new EnergyEstimator(co, metrics);
        validator = new EcoEfficiencyToolValidatorRESTClient(configOptimis.getString("optimis-ipvm"));

        ecoForecaster = new EcoEffForecasterIP(vmCPUPredictors, energyEstimator, metrics);

        log.debug("Starting EcoTool Proactive Infrastructure Assessment.");
        proactiveManager = new ProactiveManager(this, ecoForecaster);
        log.debug("EcoefficiencyTool IP correctly started.");
    }

    /**
     * Stops the proactive eco-assessment of a service.
     *
     * @param serviceId Service Identifier
     */
    public synchronized void stopServiceAutoMonitoring(String serviceId) {
        if (serviceId != null) {
            List<String> vmIdsOfService = co.getVMsIdsOfService(serviceId);
            for (String vmId : vmIdsOfService) {
                log.debug("Removing historical CPU utilization data for VM " + vmId);
                vmCPUPredictors.remove(vmId);
                //vmUtilizations.remove(vmId);
            }
        } else {
            log.error("Service ID cannot be null!");
        }
    }

    public synchronized String assessServiceEcoEfficiency(String serviceId, String type) {

        double[] ecoefficiency = assessServiceEcoEfficiency(serviceId);
        //Return desired ecoefficiency type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiency[0]);
        } else {
            return Double.toString(ecoefficiency[1]);
        }

    }

    /**
     * Assesses the eco-efficiency of a given service.
     *
     * @param serviceId Service Identifier.
     * @return Service's ecoefficiency (Double into a String).
     */
    public synchronized double[] assessServiceEcoEfficiency(String serviceId) {

        double ret[] = {-1.0, -1.0};
        double energyefficiency, ecologicalefficiency;
        double totalPerformance = 0.0, totalPower = 0.0;

        List<String> vmsId = co.getVMsIdsOfService(serviceId);
        if (vmsId != null) {
            if (vmsId.size() > 0) {

                //Obtention of partial performance and power.
                for (String vmId : vmsId) {
                    try {
                        double perfPower[] = getVMCurrentPerformanceAndPower(vmId);
                        totalPerformance += perfPower[0];
                        totalPower += perfPower[1];
                    } catch (Exception ex) {
                        log.error("Couldn't obtain VM " + vmId + " performance and power.");
                        log.error(ex.getMessage());
                        return ret;
                    }
                }

                //Ecoefficiency calculation.
                energyefficiency = (totalPerformance) / (totalPower * PUE);
                ecologicalefficiency = (totalPerformance) / (CO2Converter.getCO2FromPower(totalPower * PUE));
                log.info("Service " + serviceId + " EnEff: " + energyefficiency + " EcoEff: " + ecologicalefficiency);
                ret[0] = energyefficiency;
                ret[1] = ecologicalefficiency;

            } else {
                log.error("Service " + serviceId + " didn't contain any VM.");
                return ret;
            }
        } else {
            log.error("Service " + serviceId + " didn't exist.");
            return ret;
        }

        //Store the obtained assessment into the TREC DDBB.
        try {
            EcoServiceTableDAO.addEcoAssessment(serviceId, energyefficiency, ecologicalefficiency, totalPerformance, totalPower * PUE, CO2Converter.getCO2FromPower(totalPower * PUE));
        } catch (Exception ex) {
            log.error("Unable to insert service " + serviceId + " ecoassessment into TREC DDBB", ex);
        }

        return ret;
    }

    public synchronized String assessIPEcoEfficiency(String type, boolean insertToValidator) {

        double[] ecoefficiency = assessIPEcoEfficiency(insertToValidator);
        //Return desired ecoefficiency type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiency[0]);
        } else {
            return Double.toString(ecoefficiency[1]);
        }
    }

    /**
     * Assesses the eco-efficiency of the whole Infrastructure Provider.
     *
     * @return Ecoefficiency assessment (Double into String).
     */
    public synchronized double[] assessIPEcoEfficiency(boolean insertToValidator) {

        double ret[] = {-1.0, -1.0};
        double energyefficiency, ecologicalefficiency;
        double totalPerformance = 0.0, totalPower = 0.0;

        List<String> nodesId = co.getNodesId();
        log.debug("Starting IP Ecoefficiency Assessment. Nodes: " + nodesId);
        if (nodesId.size() > 0) {

            //Obtention of partial performance and power.
            for (String nodeId : nodesId) {
                try {
                    double perfPower[] = getNodeCurrentPerformanceAndPower(nodeId, insertToValidator);
                    totalPerformance += perfPower[0];
                    totalPower += perfPower[1];
                } catch (Exception ex) {
                    log.error("Couldn't obtain node " + nodeId + " performance and power.");
                    log.error(ex.getMessage());
                    return ret;
                }
            }

            EnergyCreditsManager.setCurrentPowerConsumption(totalPower * PUE);

            //Ecoefficiency calculation.
            energyefficiency = (totalPerformance) / (totalPower * PUE);
            ecologicalefficiency = (totalPerformance) / (CO2Converter.getCO2FromPower(totalPower * PUE));
            log.info("IP EnEff: " + energyefficiency + " EcoEff: " + ecologicalefficiency);
            ret[0] = energyefficiency;
            ret[1] = ecologicalefficiency;

        } else {
            log.error("There are no nodes in the system.");
            return ret;
        }

        //Store the obtained assessment into the TREC DDBB.
        try {
            EcoIpTableDAO.addEcoAssessment(energyefficiency, ecologicalefficiency, totalPerformance, totalPower * PUE, CO2Converter.getCO2FromPower(totalPower * PUE));
        } catch (Exception ex) {
            log.error("Unable to insert Infrastructure Provider ecoassessment into TREC DDBB", ex);
        }

        return ret;
    }

    public synchronized void assessAllNodesEcoEfficiency(boolean insertToValidator) {
        System.out.println();
        assessMultipleNodesEfficiency(co.getNodesId(), "energy", insertToValidator);
    }

    public synchronized String assessNodeEcoEfficiency(String nodeId, String type, boolean insertToValidator) {
        double[] ecoefficiency = assessNodeEcoEfficiency(nodeId, insertToValidator);
        //Return desired ecoefficiency type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiency[0]);
        } else {
            return Double.toString(ecoefficiency[1]);
        }
    }

    /**
     * Assesses the eco-efficiency of a given node.
     *
     * @param nodeId Node Identifier
     * @return Eco-efficiency assessment (Double into a String).
     */
    public synchronized double[] assessNodeEcoEfficiency(String nodeId, boolean insertToValidator) {

        double ret[] = {-1.0, -1.0};
        double perfPower[];

        //Check of parameters.
        if (nodeId == null) {
            log.error("Node identifier can't be null.");
            return ret;
        }

        //Performance (perfPower[0]) and Real Power consumed (perfPower[1]) calculation.
        try {
            perfPower = getNodeCurrentPerformanceAndPower(nodeId, insertToValidator);
        } catch (Exception ex) {
            log.error("Couldn't get node performance.", ex);
            return ret;
        }

        //Energy efficiency and ecological efficiency calculation.
        double energyefficiency = (perfPower[0]) / (perfPower[1] * PUE);
        double ecologicalefficiency = (perfPower[0]) / (CO2Converter.getCO2FromPower(perfPower[1] * PUE));
        log.info("Node: " + nodeId + " EnEff: " + energyefficiency + " EcoEff: " + ecologicalefficiency);
        ret[0] = energyefficiency;
        ret[1] = ecologicalefficiency;

        //Store the obtained assessment into the TREC DDBB.
        try {
            EcoNodeTableDAO.addEcoAssessment(nodeId, energyefficiency, ecologicalefficiency, perfPower[0], perfPower[1] * PUE, CO2Converter.getCO2FromPower(perfPower[1] * PUE));
        } catch (Exception ex) {
            log.error("Unable to insert node " + nodeId + " ecoassessment into TREC DDBB", ex);
        }

        return ret;
    }

    public synchronized String assessVMEcoEfficiency(String vmId, String type) {
        double[] ecoefficiency = assessVMEcoEfficiency(vmId);
        //Return desired ecoefficiency type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiency[0]);
        } else {
            return Double.toString(ecoefficiency[1]);
        }
    }

    /**
     * Assesses the eco-efficiency of a given Virtual Machine.
     *
     * @param vmId Virtual Machine Identifier.
     * @return Eco-efficiency assessment (Double into a String).
     */
    public synchronized double[] assessVMEcoEfficiency(String vmId) {

        double ret[] = {-1.0, -1.0};
        double perfPower[];

        //Check of parameters
        if (vmId == null) {
            log.error("VM identifier can't be null.");
            return ret;
        }

        //Performance (perfPower[0]) and Real Power consumed (perfPower[1]) calculation.
        try {
            perfPower = getVMCurrentPerformanceAndPower(vmId);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ret;
        }

        //Ecoefficiency calculation and update.
        double energyefficiency = (perfPower[0]) / (perfPower[1] * PUE);
        double ecologicalefficiency = (perfPower[0]) / (CO2Converter.getCO2FromPower(perfPower[1] * PUE));
        log.info("VM: " + vmId + " EnEff: " + energyefficiency + " EcoEff: " + ecologicalefficiency);
        ret[0] = energyefficiency;
        ret[1] = ecologicalefficiency;

        //Store the obtained assessment into the TREC DDBB.
        try {
            EcoVMTableDAO.addEcoAssessment(vmId, energyefficiency, ecologicalefficiency, perfPower[0], perfPower[1] * PUE, CO2Converter.getCO2FromPower(perfPower[1] * PUE));
        } catch (Exception ex) {
            log.error("Unable to insert VM " + vmId + " ecoassessment into TREC DDBB", ex);
        }

        return ret;
    }

    /**
     * Assesses the ecoefficiency of a list of nodes.
     *
     * @param nodeList List of nodes to evaluate its ecoefficiency.
     * @return The ecoefficiency of each node following the same order as in the
     * input. (Each position of the returned List is a Double into a String).
     */
    public synchronized List<String> assessMultipleNodesEfficiency(List<String> nodeList, String type, boolean insertToValidator) {
        List<String> ret = new LinkedList<String>();

        for (String nodeId : nodeList) {
            ret.add(assessNodeEcoEfficiency(nodeId, type, insertToValidator));
        }
        return ret;
    }

    public String getNodeMaxEco(String nodeId, String type) {
        double ecoNodeMax;
        try {
            if (type.equalsIgnoreCase("energy")) {
                ecoNodeMax = (metrics.getNodeBenchmarkResult(nodeId)) / (energyEstimator.estimatePowerConsumption(nodeId, 100.0) * PUE);
            } else {
                ecoNodeMax = (metrics.getNodeBenchmarkResult(nodeId)) / (CO2Converter.getCO2FromPower(energyEstimator.estimatePowerConsumption(nodeId, 100.0)) * PUE);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ecoNodeMax = -1.0;
        }
        return Double.toString(ecoNodeMax);
    }

    /**
     * **********************Internal Methods*******************************
     */
    /**
     * Evaluates the performance being delivered to the VMs being run on node
     * nodeId, as well as the real power consumed by the same node.
     *
     * @param nodeId Node identifier
     * @return A vector containing the performance delivered to the VMs in
     * nodeId in the first position and the real power consumed by the node on
     * the second position.
     * @throws Exception
     */
    private double[] getNodeCurrentPerformanceAndPower(String nodeId, boolean insertToValidator) throws Exception {

        double perfPow[] = new double[2];
        Date date = new Date();

        //Obtention of Metrics from the Monitoring Infrastructure.
        String xenTopReport = null;
        String realPowerReport = null;
        double realPower = 1.0;
        double vmsCpuUtilization = 0.0;
        double dom0CpuUtilization = 0.0;

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
            if (metric.getMetric_name().equalsIgnoreCase("real_power")) {
                realPowerReport = metric.getMetric_value();
            }
            //timeStamp = metric.getMetric_timestamp().getTime();
        }

        if (xenTopReport == null) {
            log.error("Obtained null xen_top values from the Monitoring");
            throw new Exception("Obtained null xen_top values from the Monitoring");
        }

        String tmp[] = xenTopReport.split(";");
        for (int i = 0; i < tmp.length; i++) {
            String domNameVsCpu[] = tmp[i].split(" ");
            if (domNameVsCpu.length == 2) {
                if (!domNameVsCpu[0].equalsIgnoreCase("Domain-0")) {
                    double vmCpuUtilizationTemp = checkUtilizationBoundaries("VM ".concat(domNameVsCpu[0]), domNameVsCpu[1]);
                    vmsCpuUtilization += vmCpuUtilizationTemp;
                    if (insertToValidator == true) {
                        updatePastVMUtilizations(domNameVsCpu[0], date.getTime(), vmCpuUtilizationTemp);
                    }
                } else {
                    dom0CpuUtilization = checkUtilizationBoundaries(domNameVsCpu[0].concat(nodeId), domNameVsCpu[1]);
                    if (insertToValidator == true) {
                        updatePastVMUtilizations(domNameVsCpu[0].concat(nodeId), date.getTime(), dom0CpuUtilization);
                    }
                }
                //log.info("(REMOVE) Utilization of VM " + domNameVsCpu[0] + ": " + domNameVsCpu[1]);
            } else {
                log.debug("Invalid Domain CPU lecture.");
            }
        }

        //Truncate VMs and Total CPU utilization.
        vmsCpuUtilization = checkUtilizationBoundaries("VMs of Node ".concat(nodeId), Double.toString(vmsCpuUtilization));
        double cpuusage = checkUtilizationBoundaries("Total of Node ".concat(nodeId), Double.toString(vmsCpuUtilization + dom0CpuUtilization));

        if (realPowerReport == null) {
            //log.info("(REMOVE) Estimated Power Consumption.");
            realPower = energyEstimator.estimatePowerConsumption(nodeId, cpuusage);
        } else {
            //log.info("(REMOVE) Real Power Consumption.");
            realPower = checkPowerBoundaries(realPowerReport, nodeId);
            validator.writePowerInformation(nodeId, cpuusage, realPower);
        }

        double performance = (vmsCpuUtilization / 100.0) * metrics.getNodeBenchmarkResult(nodeId);

        perfPow[0] = performance;
        perfPow[1] = realPower;

        log.debug("vmsCPUUtilization " + vmsCpuUtilization + " dom0CpuUtilization " + dom0CpuUtilization + " Benchmark: " + metrics.getNodeBenchmarkResult(nodeId) + " Performance: " + performance + " Power: " + realPower);

        return perfPow;
    }

    /**
     * Evaluates the performance being delivered to this VMs, as well as the
     * real power consumed by it.
     *
     * @param vmId VM identifier
     * @return A vector containing the performance delivered to the VM in the
     * first position and the real power consumed by it on the second position.
     * @throws Exception
     */
    private double[] getVMCurrentPerformanceAndPower(String vmId) throws Exception {

        double perfPow[] = new double[2];
        String nodeId = null;
        String vmName = null;
        int numVmsNode = 1;

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
        numVmsNode = temp.size();
        vmName = co.getVMName(vmId);
        if (vmName == null) {
            log.error("Could not retreive VM name.");
            throw new Exception("Could not retreive VM name.");
        }

        //Obtention of Metrics from the Monitoring Infrastructure.
        String xenTopReport = null;
        String realPowerReport = null;
        double realPower = 1.0;
        double vmsCpuUtilization = 0.0;
        double dom0CpuUtilization = 0.0;
        double vmCpuUtilization = 0.0;
        long timeStamp = 0;

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
            if (metric.getMetric_name().equalsIgnoreCase("real_power")) {
                realPowerReport = metric.getMetric_value();
            }
            timeStamp = metric.getMetric_timestamp().getTime();
        }

        if (xenTopReport == null) {
            log.error("Obtained null xen_top values from the Monitoring");
            throw new Exception("Obtained null xen_top values from the Monitoring");
        }

        //log.info("realPower: " + realPower + " xenTopReport: " + xenTopReport + " timeStamp: " + timeStamp);

        String tmp[] = xenTopReport.split(";");
        for (int i = 0; i < tmp.length; i++) {
            String domNameVsCpu[] = tmp[i].split(" ");
            if (domNameVsCpu.length == 2) {
                if (!domNameVsCpu[0].equalsIgnoreCase("Domain-0")) {
                    double vmCpuUtilizationTemp = checkUtilizationBoundaries("VM ".concat(domNameVsCpu[0]), domNameVsCpu[1]);
                    vmsCpuUtilization += vmCpuUtilizationTemp;
                    if (domNameVsCpu[0].equalsIgnoreCase(vmName)) {
                        vmCpuUtilization += vmCpuUtilizationTemp;
                    }
                } else {
                    dom0CpuUtilization = checkUtilizationBoundaries(domNameVsCpu[0].concat(nodeId), domNameVsCpu[1]);
                }
            } else {
                log.debug("Invalid Domain CPU lecture.");
            }
        }

        //Truncate VMs and Total CPU utilization.
        vmsCpuUtilization = checkUtilizationBoundaries("VMs of Node ".concat(nodeId), Double.toString(vmsCpuUtilization));
        double cpuusage = checkUtilizationBoundaries("Total of Node ".concat(nodeId), Double.toString(vmsCpuUtilization + dom0CpuUtilization));

        if (realPowerReport == null) {
            realPower = energyEstimator.estimatePowerConsumption(nodeId, cpuusage);
        } else {
            realPower = checkPowerBoundaries(realPowerReport, nodeId);
        }

        double performanceVM = (vmCpuUtilization / 100.0) * metrics.getNodeBenchmarkResult(nodeId);
        double proportion = ((vmCpuUtilization + dom0CpuUtilization / ((double) numVmsNode)) / (cpuusage));
        if (proportion <= 0.0) {
            proportion = 0.01;
        } else if (proportion > 1.0) {
            proportion = 1.0;
        }
        double realPowerVM = realPower * proportion;

        perfPow[0] = performanceVM;
        perfPow[1] = realPowerVM;
        return perfPow;
    }

    public synchronized double[] getServicePerformancePowerAndCO2(String serviceId) {

        double ret[] = {-1.0, -1.0, -1.0};
        double totalPerformance = 0.0, totalPower = 0.0;

        List<String> vmsId = co.getVMsIdsOfService(serviceId);
        if (vmsId != null) {
            if (vmsId.size() > 0) {

                //Obtention of partial performance and power.
                for (String vmId : vmsId) {
                    try {
                        double perfPower[] = getVMCurrentPerformanceAndPower(vmId);
                        totalPerformance += perfPower[0];
                        totalPower += perfPower[1];
                    } catch (Exception ex) {
                        log.error("Couldn't obtain VM " + vmId + " performance and power.");
                        log.error(ex.getMessage());
                        return ret;
                    }
                }

                //Result calculation.
                log.info("Service " + serviceId + " TotPerf: " + totalPerformance + " TotPow: " + totalPower * PUE + " GR.CO2: " + CO2Converter.getCO2FromPower(totalPower * PUE));
                ret[0] = totalPerformance;
                ret[1] = totalPower * PUE;
                ret[2] = CO2Converter.getCO2FromPower(totalPower * PUE);

            } else {
                log.error("Service " + serviceId + " didn't contain any VM.");
                return ret;
            }
        } else {
            log.error("Service " + serviceId + " didn't exist.");
            return ret;
        }
        return ret;
    }

    /**
     * Stores the CPU utilization of the VM vmId into EcoEfficiencyTool's
     * volatile memory. This data will be used later to perform forecasts of
     * this VM's CPU utilization.
     *
     * @param vmId VM Identifier
     * @param newTimeStamp Current time in milliseconds since 1/1/1970
     * @param utilization Current CPU utilization
     */
    private synchronized void updatePastVMUtilizations(String vmId, long newTimeStamp, double utilization) {
        VariableEstimator vmCPUPredictor = vmCPUPredictors.get(vmId);

        if (vmCPUPredictor == null) {
            vmCPUPredictor = new VariableEstimator();
            vmCPUPredictors.put(vmId, vmCPUPredictor);
        }

        vmCPUPredictor.addValue(newTimeStamp, utilization);
        validator.storeVMCPUUtilization(vmId, newTimeStamp, utilization);
    }

    /*private synchronized void updatePastVMUtilizations(String vmId, long newTimeStamp, double utilization) {

     HashMap<Long, Double> vmUsPast = vmUtilizations.get(vmId);

     if (utilization < 0.0) {
     utilization = 0.0;
     } else if (utilization > 100.0) {
     utilization = 100.0;
     }

     if (vmUsPast == null) {
     //System.out.println("**ECO-REMOVE: Creating list of past Utilizations for vm " + vmId);
     vmUtilizations.put(vmId, new HashMap<Long, Double>());
     vmUsPast = vmUtilizations.get(vmId);
     }

     if (!vmUsPast.containsKey(newTimeStamp)) {
     if (vmUsPast.size() < Constants.MAX_PREVIOUS_ASSESSMENTS) {
     vmUsPast.put(newTimeStamp, Double.valueOf(utilization));
     } else {

     //System.out.println("**ECO-REMOVE: Updating previous U values for VM: " + vmId);
     Iterator it = vmUsPast.keySet().iterator();
     long oldestTimeStamp = newTimeStamp;
     while (it.hasNext()) {
     Long timeStamp = (Long) it.next();
     if (timeStamp.longValue() < oldestTimeStamp) {
     oldestTimeStamp = timeStamp.longValue();
     }
     }
     //System.out.println("**ECO-REMOVE: Removing oldest timestamp: " + oldestTimeStamp + " Inserting new timestamp: " + newTimeStamp);
     vmUsPast.remove(Long.valueOf(oldestTimeStamp));
     vmUsPast.put(newTimeStamp, Double.valueOf(utilization));
     }
     }

     }*/
    private double checkUtilizationBoundaries(String vmId, String cpuUtilization) {
        double ret = Double.parseDouble(cpuUtilization);
        if (ret < 0.0) {
            log.debug("Obtained negative CPU utilization (" + cpuUtilization + ") for " + vmId);
            return 0.0;
        } else if (ret > 100.0) {
            log.debug("Obtained greater than 100.0 CPU utilization (" + cpuUtilization + ") for " + vmId);
            return 100.0;
        }
        return ret;
    }

    private double checkPowerBoundaries(String power, String nodeId) {
        double ret = Double.parseDouble(power);
        if (ret < 0.0) {
            log.debug("Obtained negative power (" + power + ") for node " + nodeId);
            return energyEstimator.getPidle(nodeId);
        }
        return ret;
    }
}
