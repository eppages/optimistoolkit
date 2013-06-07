/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.core;

import elasticityRestinterface.RestInterface;
import eu.optimis.ac.ACRestClients.ACRestClient;
import eu.optimis.ac.ACRestClients.ACinternalClient;
import eu.optimis.ac.ACRestClients.COrestACClientAPPLICATION_XML;
import eu.optimis.cloudoptimizer.data.CODecision;
import eu.optimis.cloudoptimizer.persistence.DBUtil;
import eu.optimis.cloudoptimizer.persistence.Queries;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.cloudoptimizer.util.Log;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import eu.optimis.DataManagerClient.DataManagerClient;
import eu.optimis.faulttoleranceengine.rest.client.FaultToleranceEngineRESTClient;
import eu.optimis.schemas.trec.blo.Constraints;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.vmmanager.rest.client.VMManagerRESTClient;
import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.cloudoptimizer.util.Config;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import eu.optimis.treccommon.TrecApiIP;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 */
public class HolisticManager {

    HolisticManagementRESTClient vmmHm;

    private static final float TRUST_MIN_TH = 0.3F;
    private static final float RISK_MAX_TH = 0.75F;
    private static final float ECO_MIN_TH = 20.0F;
    private static final float COST_MAX_TH = 3000.0F;
    
    private static final String VMM_BASIC_MODE = "0.0:1.0:0.0";

    CloudManager cm;

    public HolisticManager(CloudManager cm) {
        this.cm = cm;
        //dmm = new DataManagerClient(null);
        //ac = new AdmissionController();

        vmmHm = new HolisticManagementRESTClient(
                Config.getString("config.ipvm_host"),
                Integer.parseInt(Config.getString("config.ipvm_port")),
                "VMManager");

        // First setting of policies - eco mode
        TRECWatcher.instance.initialize(cm.getTREC());
    }


    /**
     * Configures the basic mode of AC, DO, VMM, and DM
     * No need to do the same with EE and FTE, since it is done per service, not applicable for IP operation without services
     */
    public void setBasicMode() {
        Log.getLogger().info("Setting basic operational mode to IP components.");
        //vmm.setPolicy(VMM_BASIC_MODE);
        Log.getLogger().info("VMM done.");
        //setACPolicy(Config.getString("config.ipvm_host"), Config.getString("config.ipvm_port"), "0.0", "0.0", "1.0", "0.0");
        Log.getLogger().info("AC done.");

        try {
            Connection conn = DBUtil.getConnection();
            BusinessDescription blo = Queries.getStoredBLO(conn,Log.getLogger());
            conn.close();
            if(blo != null) {
                Log.getLogger().info("Restoring previously set BLO: " + BLOUtils.toString(blo));
                setBLO(blo);
            }

        } catch(Exception e) {
            Log.getLogger().error("When restoring BLOs: " + e.getMessage());
        }
        //TODO Y3 - dmm.setPolicy(this.policyMgmtFmwk.get("dm"));
    }


    /*
     * Method used to set the policy used by the Admission Control
     */
    public void setACPolicy(String host, String port, String trust_weight, String cost_weight, String eco_weight, String risk_weight) {
        try {
            String urlStr = "http://" + host + ":" + port + "/ACGateway/data/setPolicy" 
                    + "/" + trust_weight + "/" + eco_weight
                    + "/" + risk_weight + "/" + cost_weight;
            
            Client client = Client.create();
            WebResource webResource = client.resource(urlStr);
            ClientResponse response = webResource.accept("text/plain").get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
        } catch (Exception e) {
            Log.getLogger().error(e.getMessage(),e);
            e.printStackTrace();
        }
    }

    /*
     * Method used to set the policy of the Data Manager component
     */
    public String setDMPolicy(String trust, String risk, String eco, String cost) {
        String xmlString = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("trec");
            doc.appendChild(rootElement);

            // trec values
            Element t = doc.createElement("trust");
            t.appendChild(doc.createTextNode(trust));
            rootElement.appendChild(t);

            Element r = doc.createElement("risk");
            r.appendChild(doc.createTextNode(risk));
            rootElement.appendChild(r);

            Element e = doc.createElement("eco");
            e.appendChild(doc.createTextNode(eco));
            rootElement.appendChild(e);

            Element c = doc.createElement("cost");
            c.appendChild(doc.createTextNode(cost));
            rootElement.appendChild(c);

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            xmlString = sw.toString();


        } catch (TransformerException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
        } catch (ParserConfigurationException ex) {
            Log.getLogger().error(ex.getMessage(),ex);
        }
        return xmlString;
    }

    public void setBLO(BusinessDescription bd) throws BLOException {
        BLOUtils.validate(bd);

        Constraints c = bd.getConstraints();
        if (c != null) {
            Connection conn = DBUtil.getConnection();
            Queries.storeBLO(conn,bd,Log.getLogger());
            try {
                List<String> nodes = Queries.getActiveNodesId(conn);
                List<String> services = Queries.getRunningServiceIds(conn);

                TrecApiIP t = cm.getTREC();
//            if(c.getTrustGreaterThan() != null) {
//                t.TRUST.setProactiveTrustAssessor(cm.getLocalIPId(),c.getTrustGreaterThan(),)
//            }
                /**
                 * SETTING TRUST VALUES
                 */
                Double tv = c.getTrustGreaterThan();
                if(tv == null) {
                    tv = -1.0;
                }
                for(String s : services) {
                    final int TYPE_FOR_SERVICE = 1;
                    t.TRUST.setProactiveTrustAssessor(s, tv, TYPE_FOR_SERVICE );
                }

                /**
                 * SETTING RISK VALUES
                 */
                Integer r = c.getRiskLessThan();
                if (r == null) {
                    t.RISK.stopProactiveRiskAssessor();
                } else {
                    Log.getLogger().debug("Setting risk level threshold: <= " + r);

                    // Setting risk for nodes

                    HashMap<String, String> nodesRisk = new HashMap<String, String>();
                    String rstr = r.toString();
                    for (String n : nodes) {
                        nodesRisk.put(n, rstr);
                    }

                    // Setting risk for services, aen lond each of its vms
                    HashMap<String, String> servicesRisk = new HashMap<String, String>();
                    // The next hashmap is structured as follows
                    //    - Outer map key: service Id
                    //    - Inner map key: vm Id
                    //    - Inner map value: vm risk threshold
                    HashMap<String, HashMap<String, String>> vmsRisk = new HashMap<String, HashMap<String, String>>();
                    for (String s : services) {
                        servicesRisk.put(s, rstr);
                        HashMap<String, String> svmR = new HashMap<String, String>();
                        vmsRisk.put(s, svmR);
                        List<String> vms = Queries.getVMsIdsOfService(conn, s);
                        for (String v : vms) {
                            svmR.put(v, rstr);
                        }
                    }
                    t.RISK.startProactiveRiskAssessor(cm.getLocalIPId(), rstr, nodesRisk, servicesRisk, vmsRisk);

                }
                /**
                 * SETTING ECO THRESHOLDS
                 */
                Double e = c.getEcoGreaterThan();
                if (e == null) {
                    e = -1.0;
                } else {
                    Log.getLogger().debug("Setting eco threshold: > " + e);
                }
                t.ECO.setInfrastructureEcoThreshold(e,e);
                for(String n : nodes) {
                    t.ECO.setNodeEcoThreshold(n,e,e);
                }
                for(String s : services) {
                    t.ECO.setServiceEcoThreshold(s,e,e);
                    List<String> vms = Queries.getVMsIdsOfService(conn,s);
                    for(String v : vms) {
                        t.ECO.setVMEcoThreshold(v,e,e);
                    }
                }
            } catch (SQLException e) {
                Log.getLogger().debug(Log.getStackTrace(e));
            } finally {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        ObjectiveType type = bd.getObjective().getType();
        // Configure LLMs supporting TREC        
        cm.setBLOs(bd);
        cm.getVMM().setSchedulingPolicy(bd);
        cm.getFTE().setSchedulingPolicy(bd);

        // TODO: contact ADMISSION CONTROL and DATA MANAGER people        


        if (type == ObjectiveType.MAX_ECO || type == ObjectiveType.MAX_ENERGY_EFF) {
            onEcoMaximizationBLO(bd);
        } else if (type == ObjectiveType.MAX_TRUST) {
            onTrustMaximizationBLO(bd);
        } else if (type == ObjectiveType.MIN_COST) {
            onCostMinimizationBLO(bd);
        } else if (type == ObjectiveType.MIN_RISK) {
            onRiskMinimizationBLO(bd);
        }
    }
    
    /**
     * Configures LLMs not supporting TREC
     * @param bd
     * @throws BLOException 
     */
    private void onEcoMaximizationBLO(BusinessDescription bd) throws BLOException {
        Log.getLogger().debug("Setting " + bd.getObjective().getType().value());
        cm.getSD().setOperationMode(ServiceDeployer.OperationMode.LOW_COST);
        // TODO: contact con elasticity engine para temas rest y poder especificar lowcost/lowrisk mode
//        cm.getDM().specifyObjective(cm.getLocalIPId(), cm.getDM().TREC_OBJECTIVE_FUNCTION_ECO);
        setEEModeForAllRunningServices(false);
//        cm.getDM().specifyObjective(cm.getLocalIPId(),DataManagerClient.)
        setACPolicy(Config.getString("config.ipvm_host"), Config.getString("config.ipvm_port"), "0.0", "0.0", "1.0", "0.0");
    }
    /**
     * Configures LLMs not supporting TREC
     * @param bd
     * @throws BLOException 
     */    
    private void onTrustMaximizationBLO(BusinessDescription bd) throws BLOException {
        Log.getLogger().debug("Setting TRUST maximization");
        cm.getSD().setOperationMode(ServiceDeployer.OperationMode.LOW_RISK);
//        cm.getDM().specifyObjective(cm.getLocalIPId(), cm.getDM().TREC_OBJECTIVE_FUNCTION_TRUST);
        setEEModeForAllRunningServices(true);
        try {
            //fte.setTargetAvailability(1.0f);
        } catch(Exception e) {
            Log.getLogger().error(e.getMessage(), e);
        }
        setACPolicy(Config.getString("config.ipvm_host"), Config.getString("config.ipvm_port"), "1.0", "0.0", "0.0", "0.0");
    }
    /**
     * Configures LLMs not supporting TREC
     * @param bd
     * @throws BLOException 
     */    
    private void onCostMinimizationBLO(BusinessDescription bd) throws BLOException {
        Log.getLogger().debug("Setting COST minimization");
        cm.getSD().setOperationMode(ServiceDeployer.OperationMode.LOW_COST);
//        cm.getDM().specifyObjective(cm.getLocalIPId(), DataManagerClient.TREC_OBJECTIVE_FUNCTION_COST);
        setEEModeForAllRunningServices(false);
        setACPolicy(Config.getString("config.ipvm_host"), Config.getString("config.ipvm_port"), "0.0", "1.0", "0.0", "0.0");
    }
    /**
     * Configures LLMs not supporting TREC
     * @param bd
     * @throws BLOException 
     */    
    private void onRiskMinimizationBLO(BusinessDescription bd) throws BLOException {
        Log.getLogger().debug("Setting RISK minimization");
        cm.getSD().setOperationMode(ServiceDeployer.OperationMode.LOW_RISK);
//        cm.getDM().specifyObjective(cm.getLocalIPId(), cm.getDM().TREC_OBJECTIVE_FUNCTION_TRUST);
        setEEModeForAllRunningServices(true);
        try {
            //fte.setTargetAvailability(1.0f);
        } catch(Exception e) {
            Log.getLogger().error(e.getMessage(), e);
        }
        setACPolicy(Config.getString("config.ipvm_host"), Config.getString("config.ipvm_port"), "0.0", "0.0", "0.0", "1.0");
    }


    /*
        Hack done until all TRECs are available. If only eco is considered, it would always
        choose "reject" or "accept_remote"
     */
    private static final boolean ALWAYS_ACCEPT_LOCAL = true;
    private static final int T = 0, R = 1, E = 2, C = 3;
    private static final int REJ = 0 , LOC = 1, REM = 2;

    public CODecision decideServicePlacement(Manifest serviceManifest, String componentId) {
        if(ALWAYS_ACCEPT_LOCAL) return CODecision.ACCEPT_LOCAL;

        BusinessDescription bd = cm.getBLOs();
        double[][] forecasts = forecastActions(serviceManifest, componentId);
        double wTrust = 0 , wRisk = 0, wEco = 0, wCost = 0;

        if(ObjectiveType.MAX_TRUST.equals(bd)) {
            wTrust = 1;
        } else if(ObjectiveType.MIN_RISK.equals(bd)) {
            wRisk = 1;
        } else if(ObjectiveType.MIN_COST.equals(bd)) {
            wCost = 1;
        } else {
            wEco = 1;
        }

        double utilityReject = forecasts[0][0] * wTrust +
                forecasts[1][0] * wRisk +
                forecasts[2][0] * wEco +
                forecasts[3][0] * wCost;

        double utilityLocal = forecasts[0][1] * wTrust +
                forecasts[1][1] * wRisk +
                forecasts[2][1] * wEco +
                forecasts[3][1] * wCost;
        
        double utilityRemote = forecasts[0][2] * wTrust +
                forecasts[1][2] * wRisk +
                forecasts[2][2] * wEco +
                forecasts[3][2] * wCost;

        if(utilityReject > utilityLocal) {
            if(utilityReject > utilityRemote) {
                return CODecision.REJECT;
            } else {
                return CODecision.ACCEPT_REMOTE;
            }
        } else {
            if(utilityLocal >= utilityRemote) {
                return CODecision.ACCEPT_LOCAL;
            } else {
                return CODecision.ACCEPT_REMOTE;
            }
        }

    }

    private double[][] forecastActions(Manifest serviceManifest, String componentId) {
        String ecoFactor = "ecological";

        if(cm.getBLOs() != null && cm.getBLOs().getObjective() != null
                && ObjectiveType.MAX_ENERGY_EFF.equals(cm.getBLOs().getObjective().getType())) {
            ecoFactor = "energy";
        }

        // Define 4 vectors of size 3 (reject, accpt local, accept remote) where each cell
        // refresents the forecast of {trust, risk, eco, cost} for the IP for the corres-
        // ponding action
        double[][] forecasts = new double[4][3];
        String smString =  serviceManifest.toString();

        TrecApiIP t = cm.getTREC();

        // Forecasts for TRUST
        forecasts[T][LOC] = t.TRUST.forecastServiceDeploymentIP(smString);
        forecasts[T][REJ] = forecasts[T][REM] = t.TRUST.forecastIPTrust(cm.getLocalIPId(),1);

        // Forecasts for RISK
        // missing forecasts for risk status

        // Forecasting ECO
        // For accept_local, forecast the eco efficiency for deployment
        // For reject or accept_remote, the eco efficiency remains the same as now
        forecasts[E][LOC] = Double.valueOf(t.ECO.forecastIPEcoefficiencyServiceDeployment(smString, new Long(0), ecoFactor));
        forecasts[E][REJ] = forecasts[E][REM] = Double.valueOf(t.ECO.forecastIPEcoefficiency(new Long(0), ecoFactor));

        // Forecasting COST
        //t.COST.forecastIPCost()
        return forecasts;
    }


    public void notifyVMRisk(String vmId, int risk, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Integer threshold = c.getRiskLessThan();
            if(threshold != null && threshold < risk) {
                vmmHm.notifyVMRiskLevel(vmId,risk);
            }
        }
    }


    public void notifyServiceRisk(String serviceID, int risk, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Integer threshold = c.getRiskLessThan();
            if(threshold != null && threshold < risk) {
                vmmHm.notifyServiceRiskLevel(serviceID,risk);
            }
        }
    }

    public void notifyPhysicalHostRisk(String physicalHostID, int risk, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Integer threshold = c.getRiskLessThan();
            if(threshold != null && threshold < risk) {
                vmmHm.notifyPhysicalHostRiskLevel(physicalHostID,risk);
            }
        }
    }

    public void notifyInfranstructureRisk(int risk, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Integer threshold = c.getRiskLessThan();
            if(threshold != null && threshold < risk) {
                vmmHm.notifyInfrastructureRiskLevel(risk);
            }
        }
    }
    
    public void notifyVMEco(String vmId, String eco, String type, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getEcoGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(eco)) {
                vmmHm.notifyVMEco(vmId,Double.valueOf(eco),type,intervalMS);
            }
        }
    }


    public void notifyServiceEco(String serviceID, String eco, String type, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getEcoGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(eco)) {
                vmmHm.notifyServiceEco(serviceID,  Double.valueOf(eco),type,intervalMS);
            }
        }
    }

    public void notifyPhysicalHostEco(String physicalHostID, String eco, String type, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getEcoGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(eco)) {
                vmmHm.notifyPhysicalHostEco(physicalHostID, Double.valueOf(eco),type,intervalMS);
            }
        }
    }

    public void notifyInfranstructureEco(String eco, String type, long intervalMS) {
        BusinessDescription blo = cm.getBLOs();
        if(blo != null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getEcoGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(eco)) {
                vmmHm.notifyInfrastructureEco(Double.valueOf(eco),type,intervalMS);
            }
        }
    }

    public void notifyVMTrust(String vmId, String trust) {
        BusinessDescription blo = cm.getBLOs();
        if(blo!=null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getTrustGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(trust)) {
                vmmHm.notifyVMTrust(vmId, Double.valueOf(trust));
            }
        }
    }


    public void notifyServiceTrust(String serviceId, String trust) {
        BusinessDescription blo = cm.getBLOs();
        if(blo!=null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getTrustGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(trust)) {
                vmmHm.notifyServiceTrust(serviceId, Double.valueOf(trust));
            }
        }    }


    public void notifyPhysicalHostTrust(String physicalHostID, String trust) {
        BusinessDescription blo = cm.getBLOs();
        if(blo!=null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getTrustGreaterThan();
            if(threshold != null && threshold >= Double.valueOf(trust)) {
                vmmHm.notifyPhysicalHostTrust(trust, Double.valueOf(trust));
            }
        }    }


    public void notifyInfranstructureTrust(String trust) {
        BusinessDescription blo = cm.getBLOs();
        if (blo != null) {
            Constraints c = blo.getConstraints();
            Double threshold = c.getTrustGreaterThan();
            if (threshold != null && threshold >= Double.valueOf(trust)) {
                vmmHm.notifyInfrastructureTrust(Double.valueOf(trust));
            }
        }
    }


    private void setEEModeForAllRunningServices(boolean highCostLoWRisk) {
        Collection<String> serviceIds = cm.getServicesIPManifest().keySet();
        try {
            for(String sId : serviceIds) {
                Log.getLogger().debug("Calling EE.setMode() with the next parameters: serviceId = " + sId + "; highCostLowRisk = " + highCostLoWRisk);
                cm.getEE().setMode(sId,highCostLoWRisk);
            }
        } catch(Exception e) {
            Log.getLogger().error(e.getMessage());
        }
    }

}
