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

package eu.optimis.serviceproviderriskassessmenttool.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsmj
 */
public class ServiceProviderRiskAssessmentToolRESTClient {
    
	protected static Logger log = Logger.getLogger(ServiceProviderRiskAssessmentToolRESTClient.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "ServiceProviderRiskAssessmentTool";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public ServiceProviderRiskAssessmentToolRESTClient() {
        this(HOST, PORT, PATH);
    }

    public ServiceProviderRiskAssessmentToolRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public ServiceProviderRiskAssessmentToolRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public ServiceProviderRiskAssessmentToolRESTClient(String host, int port, String path) {
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
    
    
    
    public int calculateRiskLevelOfSLAOfferReliability(String providerID, String serviceID, Double  proposedPoF) {
        int ret = 0;
        try {
            if (providerID != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip").path("calculaterisklevelofslaofferreliability");
                if ((providerID !=null)&&(proposedPoF != null)) {
                    resource = resource.queryParam("providerID", providerID);
                    resource = resource.queryParam("serviceID", serviceID);
                    resource = resource.queryParam("proposedPoF", proposedPoF.toString());
                }
                ret = Integer.valueOf(resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class));
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public int calculateRiskLevelOfSLAOfferReliabilityDeployment(String providerID, String serviceID, Double  proposedPoF) {
        int ret = 0;
        try {
            if (providerID != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip").path("calculaterisklevelofslaofferreliabilitydeployment");
                if ((providerID !=null)&&(proposedPoF != null)) {
                    resource = resource.queryParam("providerID", providerID);
                    resource = resource.queryParam("serviceID", serviceID);
                    resource = resource.queryParam("proposedPoF", proposedPoF.toString());
                }
                ret = Integer.valueOf(resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class));
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public double adjustedPoFCal(String providerId, Double  proposedPoF) {
        double ret = 0;
        try {
            if (providerId != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip").path("adjustedpofcal");
                if ((providerId !=null)&&(proposedPoF != null)) {
                    resource = resource.queryParam("providerId", providerId);
                    resource = resource.queryParam("proposedPoF", proposedPoF.toString());
                }
                ret = Double.valueOf(resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class));
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public List<Integer> preNegotiateIPDeploymentPhase(List<String> IPNames) {

        List<String> rankedIPs = null;
        try {
            if (IPNames != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip").path("prenegotiateipdeploymentphase");

                ListStrings IPs = new ListStrings();

                for (String IPName : IPNames) {
                    IPs.add(IPName);
                }
               rankedIPs = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, IPs);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        List<Integer> results = new ArrayList<Integer>();
        for (int i = 0; i < rankedIPs.size(); i++) {
            results.add(Integer.valueOf(rankedIPs.get(i)));
        }
        return results;
    }
    
    /**
     * Starts the assessment of a service
     */
    public void startAssessment(String serviceId) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("ip").path(serviceId);
                resource.post();
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stopAssessment(String serviceId) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress());
                resource.path("ip").path(serviceId).delete();
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
