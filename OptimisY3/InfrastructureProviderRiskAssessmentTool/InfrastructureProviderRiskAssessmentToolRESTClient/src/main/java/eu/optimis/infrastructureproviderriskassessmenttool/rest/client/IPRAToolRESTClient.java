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

package eu.optimis.infrastructureproviderriskassessmenttool.rest.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.BufferedReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;


/**
 *
 * @author scsmj
 */
public class IPRAToolRESTClient {

	protected static Logger log = Logger.getLogger(IPRAToolRESTClient.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "InfrastructureProviderRiskAssessmentTool";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;
    String readLine;
//    HttpClient client2 = new HttpClient();
    BufferedReader br = null;

    public IPRAToolRESTClient() {
        this(HOST, PORT, PATH);
    }

    public IPRAToolRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public IPRAToolRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public IPRAToolRESTClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    private String getAddress() throws URISyntaxException {
        //return "http://"+host+":"+port+"/"+path
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }

    public SPPoFs preNegotiateSPDeploymentPhase(String SPName, String serviceManifest) {

        SPPoFs returnSPPoFs = null;
        try {
            if (SPName != null) {
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("prenegotiatespdeploymentphase");
                resource = resource.queryParam("SPName", SPName);
                returnSPPoFs = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(SPPoFs.class, serviceManifest);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return returnSPPoFs;

    }

    public double calculatePhyHostPoF(String physicalHostName, String timePeriod) {
        String pof = null;
        try {

            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("calculatephyhostpof");
            resource = resource.queryParam("physicalHostName", physicalHostName);
            resource = resource.queryParam("timePeriod", timePeriod);
            pof = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return Double.valueOf(pof);
    }

    public List<Double> calculatePhyHostsPoFs(HashMap<String, String> physicalHostNames) {

        MultivaluedMap<String, String> ins = new MultivaluedMapImpl();
        Iterator<String> it = physicalHostNames.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ins.add(key, physicalHostNames.get(key).toString());
        }
          
        ListStrings outs = null;

        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("calculatephyhostspofs");
            resource = resource.queryParams(ins);
            outs = resource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_XML).post(ListStrings.class);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<Double> results = new ArrayList<Double>();
        for (int i = 0; i < outs.size(); i++) {
            results.add(Double.valueOf(outs.get(i)));
        }
        return results;
    }

    public int calculateRiskLevelOfPhyHostFailure(String physicalHostName, String timePeriod) {
        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("calculaterisklevelofphyhostfailure");
            resource = resource.queryParam("physicalHostName", physicalHostName);
            resource = resource.queryParam("timePeriod", timePeriod);
            riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        return Integer.valueOf(riskLevel);
    }

    public List<Integer> calculateRiskLevelsOfPhyHostFailures(HashMap<String, String> physicalHostNames) {
        
        MultivaluedMap<String, String> ins = new MultivaluedMapImpl();
        Iterator<String> it = physicalHostNames.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ins.add(key, physicalHostNames.get(key).toString());
        }

        ListStrings outs = null;

        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("calculaterisklevelsofphyhostfailures");
            resource = resource.queryParams(ins);
            outs = resource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_XML).post(ListStrings.class);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        List<Integer> results = new ArrayList<Integer>();
        for (int i = 0; i < outs.size(); i++) {
            results.add(Integer.valueOf(outs.get(i)));
        }
        return results;
    }

    public void startProactiveRiskAssessorREST(String infrastructureID, String infrastructureRiskLevelThreshold, HashMap<String, String> physicalHostsRiskLevelThresholds, HashMap<String, String> servicesRiskLevelThresholds, HashMap<String, HashMap<String, String>> VMsRiskLevelThresholds) {
    
        RiskLevelThresholdsObject ins = new RiskLevelThresholdsObject();
        ins.setInfrastructureID(infrastructureID);
        ins.setInfrastructureRiskLevelThresholds(infrastructureRiskLevelThreshold);
        ins.setPhysicalHostsRiskLevelThresholds(physicalHostsRiskLevelThresholds);
        ins.setServicesRiskLevelThresholds(servicesRiskLevelThresholds);
        ins.setVMsRiskLevelThresholds(VMsRiskLevelThresholds);
        
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("startproactiveriskassessor");
            resource.type(MediaType.APPLICATION_XML).post(ins);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stopProactiveRiskAssessorREST() {
        
        try {
            WebResource resource = client.resource(this.getAddress());
            resource.path("infrastructure").delete();
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }
    public String forecastVMDeploymentKnown(String replicationFactor, String vmID, String destNode) {
        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastVMDeploymentKnown");
            resource = resource.queryParam("replicationFactor", replicationFactor);
            resource = resource.queryParam("vmID", vmID);
            resource = resource.queryParam("destNode", destNode);
            riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }
 
    
       public void runRiskStart(String serviceID) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("runRiskStart");
            resource = resource.queryParam("serviceID", serviceID);
            resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
     }
 
       public void runRiskStop(String serviceID) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("runRiskStop");
            resource = resource.queryParam("serviceID", serviceID);
            resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
     }
    
    
    
    
    
    public String forecastVMDeploymentUnknown(String replicationFactor, String vmID, String destNode) {
        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastVMDeploymentUnknown");
            resource = resource.queryParam("replicationFactor", replicationFactor);
            resource = resource.queryParam("vmID", vmID);
            resource = resource.queryParam("destNode", destNode);
            riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);
       
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }
     public String forecastVMMigrationKnown(String replicationFactor,  String vmID,  String destNode) {
        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastVMMigrationKnown");
             resource = resource.queryParam("replicationFactor", replicationFactor);
            resource = resource.queryParam("vmID", vmID);
            resource = resource.queryParam("destNode", destNode);
           riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }

    public String DMRisk(@QueryParam("applicationDetail") String applicationDetail, @QueryParam("replicationFactor") String replicationFactor) {

        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("calculaterisklevelofphyhostfailure");
            resource = resource.queryParam("applicationDetail", applicationDetail);
            resource = resource.queryParam("replicationFactor", replicationFactor);
            riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }

    public String forecastVMMigrationUnknown(String replicationFactor, String vmID, String destNode) {
   String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastVMMigrationUnknown");
         resource = resource.queryParam("replicationFactor", replicationFactor);
            resource = resource.queryParam("vmID", vmID);
            resource = resource.queryParam("destNode", destNode);
               riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);

        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }

    public String forecastVMDeploymentCancelled(String vmID, String replicationFactor) {
        String riskLevel = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastVMDeploymentCancelled");
            resource = resource.queryParam("vmID", vmID);
             resource = resource.queryParam("replicationFactor", replicationFactor); 
                 riskLevel = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return riskLevel;
     }
    
}
