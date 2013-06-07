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
package eu.optimis.ecoefficiencytool.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.emotivecloud.commons.HashMapStrings;
import net.emotivecloud.commons.ListStrings;

/**
 * Client (to be used by SPs) of the Eco-efficiency tool REST API
 *
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 *
 */
public class EcoEfficiencyToolRESTClientSP {

    private static final Log log = LogFactory.getLog(EcoEfficiencyToolRESTClientSP.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "EcoEfficiencyTool";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public EcoEfficiencyToolRESTClientSP() {
        this(HOST, PORT, PATH);
    }

    public EcoEfficiencyToolRESTClientSP(String host) {
        this(host, PORT, PATH);
    }

    public EcoEfficiencyToolRESTClientSP(String host, int port) {
        this(host, port, PATH);
    }

    public EcoEfficiencyToolRESTClientSP(String host, int port, String path) {
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

    /**
     * Starts the automatic eco-assessment of a service.
     *
     * @param serviceId Service identifier.
     * @param timeout Timeout between successive ecoefficiency automatic
     * assessments. A default timeout value will be used if this parameter is
     * not specified.
     */
    public void startAssessment(String serviceId, Long timeout) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId);
                if (timeout != null) {
                    resource = resource.queryParam("timeout", timeout.toString());
                }
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

    /**
     * Stops the automatic eco-assessment of a service.
     *
     * @param serviceId Service identifier.
     */
    public void stopAssessment(String serviceId) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress());
                resource.path("service").path(serviceId).delete();
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

    /**
     * Evaluates the current eco-efficiency of a service.
     *
     * @param serviceId Evaluates the current eco-efficiency of a service.
     * @param type Type of eco-efficiency assessment: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency evaluation of the service.
     */
    public String assessServiceEcoEfficiency(String serviceId, String type) {
        String ret = null;
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path("assessecoefficiency");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                ret = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @deprecated Use forecastServiceEcoEfficiency(String providerId, String
     * manifest, Long timeSpan, String type), since typeIdReplicas can now be
     * specified directly in the Service Manifest.
     */
    @Deprecated
    public String forecastServiceEcoEfficiency(String providerId, String manifest, HashMap<String, Integer> typeIdReplicas, Long timeSpan, String type) {

        return forecastServiceEcoEfficiency(providerId, manifest, timeSpan, type);
    }

    /**
     * Predicts the service’s eco-efficiency upon its deployment if it doesn’t
     * exist in the system, or it just predicts its future eco-efficiency
     * otherwise.
     *
     * @param providerId IP provider identificator, where the service is (to be)
     * deployed.
     * @param manifest Service manifest XML descriptor.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the service (to be deployed if it
     * doesn’t exist).
     */
    public String forecastServiceEcoEfficiency(String providerId, String manifest, Long timeSpan, String type) {
        String ret = null;
        try {

            WebResource resource = client.resource(this.getAddress()).path("service/forecastecoefficiency").queryParam("providerId", providerId);
            if (type != null) {
                resource = resource.queryParam("type", type);
            }
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }

            //if (manifest != null) resource = resource.queryParam("manifest", manifest);
            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, manifest);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Predicts the service’s energy and ecological efficiency upon its
     * deployment if it doesn’t exist in the system, or it just predicts its
     * future energy and ecological efficiency otherwise.
     *
     * @param providerId IP provider identificator, where the service is (to be)
     * deployed.
     * @param manifest Service manifest XML descriptor.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @return Energy efficiency forecast of the service (to be deployed if it
     * doesn’t exist). Ecological efficiency forecast of the service (to be
     * deployed if it doesn’t exist).
     */
    public double[] forecastServiceEnEcoEff(String providerId, String manifest, Long timeSpan) {
        ListStrings result = null;
        double[] ret = {-1.0, -1.0};
        try {
            WebResource resource = client.resource(this.getAddress()).path("/service/forecastenecoeff");
            if (providerId != null) {
                resource = resource.queryParam("providerId", providerId);
            }
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }

            result = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, manifest);
            ret[0] = Double.parseDouble(result.get(0));
            ret[1] = Double.parseDouble(result.get(1));
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Returns the last service deployment messages.
     */
    public String getAllDeploymentMessages() {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("service").path("alldeploymentmessages");
            ret = resource.type("text/plain").get(String.class);
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    /*public String forecastServiceEcoEfficiency(String providerId, String manifest, HashMap<String,Integer> typeIdReplicas, Long timeSpan, String type) {
     String ret = null;
     String newLayout = "no";
     try {
     if(typeIdReplicas != null) {
     //Sending new VM layout of the service.
     updateFutureDeployment(typeIdReplicas);
     newLayout = "yes";
     }

     WebResource resource = client.resource(this.getAddress()).path("service/forecastecoefficiency").queryParam("newLayout", newLayout).queryParam("providerId", providerId);
     if (type != null) {
     resource = resource.queryParam("type", type);
     }
     if (timeSpan != null) {
     resource = resource.queryParam("timeSpan", timeSpan.toString());
     }

     //if (manifest != null) resource = resource.queryParam("manifest", manifest);
     ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, manifest);
     } catch (UniformInterfaceException ex) {
     ClientResponse cr = ex.getResponse();
     log.error(cr.getStatus());
     ex.printStackTrace();
     } catch (URISyntaxException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     return ret;
     }

     private void updateFutureDeployment(HashMap<String,Integer> typeIdReplicas) {

     HashMapStrings<String,String> maptosend = new HashMapStrings();
     Iterator it = typeIdReplicas.keySet().iterator();
     while(it.hasNext()) {
     String key = (String) it.next();
     maptosend.getMapProperty().put(key, typeIdReplicas.get(key).toString());
     }

     try {
     WebResource resource = client.resource(this.getAddress()).path("/service/updatefuturedeployment");
     //if (manifest != null) resource = resource.queryParam("manifest", manifest);
     resource.type(MediaType.APPLICATION_XML).post(maptosend);
     } catch (UniformInterfaceException ex) {
     ClientResponse cr = ex.getResponse();
     log.error(cr.getStatus());
     ex.printStackTrace();
     } catch (URISyntaxException e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     }*/
}
