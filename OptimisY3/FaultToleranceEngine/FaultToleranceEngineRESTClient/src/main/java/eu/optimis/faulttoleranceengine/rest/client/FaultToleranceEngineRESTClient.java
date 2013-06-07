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
package eu.optimis.faulttoleranceengine.rest.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.schemas.trec.blo.BusinessDescription;

import javax.ws.rs.core.MediaType;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Client of the Fault Tolerance Engine REST API
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 *
 */
public class FaultToleranceEngineRESTClient {

    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "FTE";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public FaultToleranceEngineRESTClient() {
        this(HOST, PORT, PATH);
    }

    public FaultToleranceEngineRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public FaultToleranceEngineRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public FaultToleranceEngineRESTClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    private String getAddress() throws URISyntaxException {
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }

    public String setPolicy(String serviceId, String policyRules) {
        String ret = "";
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path("policy");
                ret = resource.type(MediaType.TEXT_PLAIN).post(String.class, policyRules);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public void updatePolicy(String serviceId, String policyRules) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path("policy");
                resource.type(MediaType.TEXT_PLAIN).put(String.class, policyRules);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String newServiceDeployed(String serviceId, String serviceManifest) {
        String ret = "";
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId);
                ret = resource.type(MediaType.TEXT_PLAIN).post(String.class, serviceManifest);
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public void newServiceUndeployed(String serviceId) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId);
                resource.delete();
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method used by the Risk tool to proactively send assessments
     * @param vmId
     * @param riskLevel
     */
    public String notifyVMRiskLevel(String vmId, int riskLevel) {
        String ret = "";
        try {
            WebResource resource = client.resource(this.getAddress()).path("vm/" + vmId + "/risk");
            ret = resource.type(MediaType.TEXT_PLAIN).post(String.class, String.valueOf(riskLevel));
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public String notifyHostRiskLevel(String hostid, int riskLevel) {
        String ret = "";
        try {
            WebResource resource = client.resource(this.getAddress()).path("vm/" + hostid + "/risk");
            ret = resource.type(MediaType.TEXT_PLAIN).post(String.class, String.valueOf(riskLevel));
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException(ex.getMessage() + ". REturned status: " + cr.getStatus(), ex );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }
    
    public void setSchedulingPolicy(BusinessDescription blo) throws BLOException {
        BLOUtils.validate(blo);
        try {
            WebResource resource = client.resource(this.getAddress()).path("/policy");
            resource.type(MediaType.APPLICATION_XML).post(BLOUtils.toString(blo));
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        } catch(UniformInterfaceException ex) {
            throw new RuntimeException("Bad client Response: (Status " + ex.getResponse().getStatus() + ") ");
        }
    }    
}