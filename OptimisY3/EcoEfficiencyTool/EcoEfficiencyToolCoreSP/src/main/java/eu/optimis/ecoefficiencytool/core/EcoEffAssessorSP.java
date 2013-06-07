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

import com.sun.jersey.api.client.UniformInterfaceException;
import eu.optimis.cbr.client.CBRClient;
import eu.optimis.ecoefficiencytool.core.tools.ConfigManager;
import eu.optimis.ecoefficiencytool.rest.client.EcoEfficiencyToolRESTClientIP;
import eu.optimis.ecoefficiencytool.trecdb.sp.EcoServiceTableDAO;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.service_manager.client.ServiceManagerClient;
import java.util.*;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * <b>Eco-Efficiency Assessment Tool (SP)</b> This component is in charge of
 * assessing ecological efficiency-related aspects, such as energy efficiency
 * and carbon emission levels, in a given Cloud service. Performed assessments
 * are stored into the TREC Common DDBB for further graphical representation.
 *
 * @version 2.0
 * @author J. Oriol Fitó (josep.oriol@bsc.es) and Josep Subirats
 * (josep.subirats@bsc.es)
 */
public class EcoEffAssessorSP {

    protected static Logger log = Logger.getLogger(EcoEffAssessorSP.class);
    //Proactive agents
    private HashMap<String, ProactiveService> proactiveServices;
    //External clients
    private ServiceManagerClient smClient;
    private CBRClient cbrClient;
    //private HashMap<String,String> ecoIPHosts;
    private int ecoIPPort;
    private List<String> deploymentMessages;

    /**
     * Class constructor.
     */
    public EcoEffAssessorSP() {
        PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        log.info("ECO: Starting EcoEfficiency Assessor (**SP**)");
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        PropertiesConfiguration configEco = ConfigManager.getPropertiesConfiguration(ConfigManager.ECO_CONFIG_FILE);
        ConfigManager.getConfigFilePath(ConfigManager.SPDDBB_CONFIG_FILE);
        //Proactive agents
        proactiveServices = new HashMap<String, ProactiveService>();

        //External clients
        smClient = new ServiceManagerClient(configOptimis.getString("smClientHost"), configOptimis.getString("smClientPort"));
        cbrClient = new CBRClient(configEco.getString("cbrClientHost"), configEco.getString("cbrClientPort"));

        /*ecoIPHosts = new HashMap<String,String>();        
         Iterator it = configEco.getKeys("ecoIPHost");
         while(it.hasNext()) {
         String key = (String) it.next();
         log.info("ECO: EcoEfficiencyTool Host for IP " + key.trim().split("\\.")[1] + ": " + configEco.getString(key));
         ecoIPHosts.put(key.split("\\.")[1], configEco.getString(key));
         }*/

        ecoIPPort = Integer.parseInt(configEco.getString("ecoIPPort"));
        deploymentMessages = new LinkedList<String>();

        log.info("ECO: EcoefficiencyTool SP correctly started.");
    }

    /**
     * Starts the proactive eco-assessment of a service.
     *
     * @param serviceId Service Identifier
     * @param timeout Timeout between consecutive automatic eco-assessments.
     */
    public synchronized void startServiceAssessment(String serviceId, Long timeout) {
        try {
            String ipIds[] = smClient.getInfrastructureProviderIds(serviceId);
            if(ipIds == null || ipIds.length == 0) {
                throw new Exception();
            }
        } catch (Exception ex) {
            log.error("Service " + serviceId + " doesn't exist. It won't be automatically assessed.");
            return;
        }
        
        if (serviceId != null) {
            if (!proactiveServices.containsKey(serviceId)) {
                ProactiveService psertmp = new ProactiveService(this, serviceId, timeout);
                psertmp.start();
                log.info("ECO: Started service " + serviceId + " automatic proactive eco-assessment (SP).");
                proactiveServices.put(serviceId, psertmp);
            } else {
                log.error("ECO: The service '" + serviceId + "' is already under eco-efficiency assessment.");
            }
        } else {
            log.error("ECO: The service ID cannot be null!");
        }
    }

    /**
     * Stops the proactive eco-assessment of a service.
     *
     * @param serviceId Service Identifier
     */
    public synchronized void stopServiceAssessment(String serviceId) {
        if (serviceId != null) {
            if (proactiveServices.containsKey(serviceId)) {
                proactiveServices.remove(serviceId).stopProactiveService();
                log.info("ECO: Stopped service " + serviceId + " proactive ecoassessment.");
            } else {
                log.error("ECO: The service '" + serviceId + "' isn't under eco-efficiency assessment.");
            }
        } else {
            log.error("ECO: The service ID cannot be null!");
        }
    }

    /**
     * Assesses the eco-efficiency of a given service. It contacts all the
     * Infrastructure Providers containing sections of this service in order to
     * perform the assessment.
     *
     * @param serviceId Service Identifier.
     * @return Service's ecoefficiency (Double into a String).
     */
    /*public synchronized String assessServiceEcoEfficiency(String serviceId, String type) {

     double ecoeff = 0.0;
     EcoEfficiencyToolRESTClientIP ecoIPClient = null;

     log.debug("Assessing Eco-efficiency of service " + serviceId + ".");
     String[] ipIds = null;
     try {
     ipIds = smClient.getInfrastructureProviderIds(serviceId);
     } catch (UniformInterfaceException ex) {
     log.error("Couldn't obtain service " + serviceId + " information from the SM.");
     return "-1.0";
     }
     int length = 0;
     for (int i = 0; i < ipIds.length; i++) {
     String ecoIPHost = getProviderIP(ipIds[i]);//ecoIPHosts.get(ipIds[i]);
     if (ecoIPHost == null) {
     log.error("ECO: Unknown provider id: " + ipIds[i]);
     return "-1.0";
     } else {
     ecoIPClient = new EcoEfficiencyToolRESTClientIP(ecoIPHost, ecoIPPort);
     double assessment = Double.valueOf(ecoIPClient.assessServiceEcoEfficiency(serviceId, type));
     if (assessment >= 0.0) {
     ecoeff += assessment;
     length++;
     }
     }
     }
     if (length == 0) {
     return "-1.0";
     } else {
     ecoeff = ecoeff / ((double) length);
     }

     log.info("Service " + serviceId + " EcoEff: " + ecoeff);

     Date date = new Date();
     updatePastServiceEcoefficiencies(serviceId, date.getTime(), (double) ecoeff);
     try {
     EcoServiceTableDAO.addEcoAssessment(serviceId, ecoeff, ecoeff);
     } catch (Exception ex) {
     log.error("ECO: Unable to insert service " + serviceId + " ecoassessment into TREC DDBB", ex);
     }

     return Double.toString(ecoeff);
     }*/
    
    public synchronized String assessServiceEcoEfficiency(String serviceId, String type) {

        double[] ecoefficiency = assessServiceEcoEfficiency(serviceId);
        //Return desired ecoefficiency type.
        if (type.equalsIgnoreCase("energy")) {
            return Double.toString(ecoefficiency[0]);
        } else {
            return Double.toString(ecoefficiency[1]);
        }
    }
    
    public synchronized double[] assessServiceEcoEfficiency(String serviceId) {

        double ret[] = {-1.0, -1.0};
        
        log.debug("Assessing Eco-efficiency of service " + serviceId + ".");

        //Obtention of IPs where service fragments are being executed.
        String[] ipIds = null;
        try {
            ipIds = smClient.getInfrastructureProviderIds(serviceId);
        } catch (UniformInterfaceException ex) {
            log.error("Couldn't obtain service " + serviceId + " information from the SM.");
            return ret;
        }

        //Total Performance, Power and Emissions Obtention
        EcoEfficiencyToolRESTClientIP ecoIPClient = null;
        double totalPerformance = 0.0, totalPower = 0.0, totalEmissions = 0.0;
        for (int i = 0; i < ipIds.length; i++) {
            String ecoIPHost = getProviderIP(ipIds[i]);//ecoIPHosts.get(ipIds[i]);
            if (ecoIPHost == null) {
                log.error("ECO: Unknown provider id: " + ipIds[i]);
                return ret;
            } else {
                ecoIPClient = new EcoEfficiencyToolRESTClientIP(ecoIPHost, ecoIPPort);
                double retTemp[] = ecoIPClient.getServicePerformancePowerAndCO2(serviceId);
                if (retTemp != null && retTemp[0] != -1.0 && retTemp[1] != -1.0 && retTemp[2] != -1.0) {
                    totalPerformance += retTemp[0];
                    totalPower += retTemp[1];
                    totalEmissions += retTemp[2];
                }
            }
        }

        //Energy and Ecological Efficiency Calculation.
        double energyEfficiency = totalPerformance / totalPower;
        double ecologicalEfficiency = totalPerformance / totalEmissions;
        log.info("Service " + serviceId + " EnEff: " + energyEfficiency + " EcoEff: " + ecologicalEfficiency);
        ret[0] = energyEfficiency;
        ret[1] = ecologicalEfficiency;
        
        try {
            EcoServiceTableDAO.addEcoAssessment(serviceId, energyEfficiency, ecologicalEfficiency);
        } catch (Exception ex) {
            log.error("ECO: Unable to insert service " + serviceId + " ecoassessment into TREC DDBB", ex);
        }

        return ret;
    }

    /**
     * Predicts the expected eco-efficiency of a given service section (can be a
     * whole service) deployed in the Infrastructure Provider specified by the
     * parameter "providerId". If the service is already deployed it will be
     * calculated based on the current number of VMs (components) of the
     * service. If the service is not deployed (new deployment situation) and
     * typeIdReplicas is null, it will be assumed that it will require one
     * replica of each component when performing the prediction. Otherwise, it
     * will take into account the amount of replicas to be deployed of each
     * component type when performing the prediction.
     *
     * @param providerId Infrastructure Provider Identifier.
     * @param manifest Service Manifest.
     * @param timeSpan Time in the future in which the prediction will be made.
     * @return Predicted ecoefficiency in timeSpan milliseconds (Double into a
     * String).
     */
    public synchronized String forecastServiceEcoEfficiency(String providerId, String manifest, String type, Long timeSpan) {
        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }
        
        Manifest parsedManifest = null;
        String serviceId = null;
        try {
            parsedManifest = Manifest.Factory.newInstance(manifest);
            serviceId = parsedManifest.getVirtualMachineDescriptionSection().getServiceId();
        } catch (Exception ex) {
            log.error("Couldn't parse Service Manifest. Error: \n", ex);
            return "-1.0";
        }

        double ecoeff = 0.0;

        log.debug("Forecasting service " + serviceId + " Eco-efficiency at deployment at provider " + providerId + ".");
        //Manifest parsedManifest = Manifest.Factory.newInstance(manifest);
        EcoEfficiencyToolRESTClientIP ecoIPClient = null;

        /*String[] ipIds = smClient.getInfrastructureProviderIds(parsedManifest.getVirtualMachineDescriptionSection().getServiceId());
         for(int i=0; i<ipIds.length; i++) {
         ecoIPClient = new EcoEfficiencyToolRESTClientIP(ecoIPHosts.get(ipIds[i]), ecoIPPort);
         ecoeff += Double.valueOf(ecoIPClient.forecastServiceEcoEfficiency(manifest, typeIdReplicas, timeSpan));
         }*/
        String ecoIPHost = getProviderIP(providerId);//ecoIPHosts.get(providerId);
        if (ecoIPHost == null) {
            log.error("ECO: Unknown provider id: " + providerId);
            return "-1.0";
        } else {
            ecoIPClient = new EcoEfficiencyToolRESTClientIP(ecoIPHost, ecoIPPort);
            ecoeff = Double.valueOf(ecoIPClient.forecastServiceEcoEfficiency(manifest, timeSpan, type));
        }

        log.info("Service " + serviceId + " EcoEff (" + type + ") deployment forecast at provider " + providerId + ": " + ecoeff);
        addDeploymentMessage("Service " + serviceId + " EcoEff (" + type + ") deployment forecast at provider " + providerId + ": " + ecoeff);

        return Double.toString(ecoeff);
    }

    public synchronized double[] forecastServiceEnEcoEff(String providerId, String manifest, Long timeSpan) {

        
        double ret[] = {-1.0, -1.0};
        if (timeSpan == null) {
            timeSpan = new Long(Constants.DEFAULT_TIMESPAN);
        }

        Manifest parsedManifest = null;
        String serviceId = null;
        try {
            parsedManifest = Manifest.Factory.newInstance(manifest);
            serviceId = parsedManifest.getVirtualMachineDescriptionSection().getServiceId();
        } catch (Exception ex) {
            log.error("Couldn't parse Service Manifest. Error: \n", ex);
            return ret;
        }
        log.debug("Forecasting service " + serviceId + " energy and ecological efficiency at deployment at provider " + providerId + ".");


        EcoEfficiencyToolRESTClientIP ecoIPClient = null;

        String ecoIPHost = getProviderIP(providerId);//ecoIPHosts.get(providerId);
        if (ecoIPHost == null) {
            log.error("ECO: Unknown provider id: " + providerId);
            return ret;
        } else {
            ecoIPClient = new EcoEfficiencyToolRESTClientIP(ecoIPHost, ecoIPPort);
            ret = ecoIPClient.forecastServiceEnEcoEff(manifest, timeSpan);
        }

        log.info("Service " + serviceId + " deployment forecast at provider " + providerId + ". EnEff: " + ret[0] + ". EcoEff: " + ret[1]);
        addDeploymentMessage("Service " + serviceId + " deployment forecast at provider " + providerId + ". EnEff: " + ret[0] + ". EcoEff: " + ret[1]);
        
        return ret;
    }

    /**
     * ************************Internal Methods*******************************
     */
    private String getProviderIP(String providerId) {
        for (Provider provider : cbrClient.getAllIP().getIPList()) {
            if (provider.getIdentifier().equalsIgnoreCase(providerId)) {
                log.debug("Obtained IP " + provider.getIpAddress() + " for provider " + provider.getIdentifier() + " from the IPRegistry.");
                return provider.getIpAddress();
            }
        }
        return null;
    }

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
    /**
     * Stores the ecoefficiency value of the service serviceId into
     * EcoEfficiencyTool's volatile memory. This data will be used later to
     * perform forecasts of this service's ecoefficiency.
     *
     * @param serviceId Service Identifier
     * @param newTimeStamp Current time in milliseconds since 1/1/1970
     * @param ecoefficiency Current node's ecoefficiency value.
     */
    /*private synchronized void updatePastServiceEcoefficiencies(String serviceId, long newTimeStamp, double ecoefficiency) {

     HashMap<Long, Double> ecoServicePast = ecoServicesPast.get(serviceId);
     if (ecoServicePast == null) {
     log.info("ECO: Creating list of past assessments for service " + serviceId);
     ecoServicesPast.put(serviceId, new HashMap<Long, Double>());
     ecoServicePast = ecoServicesPast.get(serviceId);
     }

     if (!ecoServicePast.containsKey(newTimeStamp)) {
     if (ecoServicePast.size() < Constants.MAX_PREVIOUS_SERVICE_ASSESSMENTS) {
     ecoServicePast.put(newTimeStamp, Double.valueOf(ecoefficiency));
     } else {
     Iterator it = ecoServicePast.keySet().iterator();
     long oldestTimeStamp = newTimeStamp;
     while (it.hasNext()) {
     Long timeStamp = (Long) it.next();
     if (timeStamp.longValue() < oldestTimeStamp) {
     oldestTimeStamp = timeStamp.longValue();
     }
     }
     ecoServicePast.remove(Long.valueOf(oldestTimeStamp));
     ecoServicePast.put(newTimeStamp, Double.valueOf(ecoefficiency));
     }
     }

     }*/
}
