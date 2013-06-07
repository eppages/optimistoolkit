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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client of the EcoEfficiencyToolValidator REST API.
 *
 * @author Josep Subirats (josep.subirats@bsc.es).
 *
 */
public class EcoEfficiencyToolValidatorRESTClient {

    private static final Log log = LogFactory.getLog(EcoEfficiencyToolValidatorRESTClient.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "EcoEfficiencyToolValidator";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;
    public final static String VM_DEPLOYMENT = "VM DEPLOYMENT";
    public final static String VM_MIGRATION = "VM MIGRATION";
    public final static String VM_CANCELLATION = "VM CANCELLATION";
    public final static String SERVICE_DEPLOYMENT = "SERVICE DEPLOYMENT";

    /**
     * Class constructor.
     */
    public EcoEfficiencyToolValidatorRESTClient() {
        this(HOST, PORT, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool Validator is deployed.
     */
    public EcoEfficiencyToolValidatorRESTClient(String host) {
        this(host, PORT, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool Validator is deployed.
     * @param port Port where the Ecoefficiency Tool Validator is deployed.
     */
    public EcoEfficiencyToolValidatorRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool Validator is deployed.
     * @param port Port where the Ecoefficiency Tool Validator is deployed.
     * @param path Path where the Ecoefficiency Tool Validator is deployed.
     */
    public EcoEfficiencyToolValidatorRESTClient(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    /**
     * Builds the address to be used to connect to the
     * EcoEfficiencyToolValidator.
     *
     * @return The address to be used to connect to the
     * EcoEfficiencyToolValidator
     * @throws URISyntaxException
     */
    private String getAddress() throws URISyntaxException {
        //return "http://"+host+":"+port+"/"+path
        String auxPath = path;
        if (!path.startsWith("/")) {
            auxPath = "/" + auxPath;
        }
        return new URI("http", null, host, port, auxPath, null, null).toString();
    }

    /**
     * Logs a conditional-action-obtained forecast for further validation.
     *
     * @param deltaT Increment of time for which the forecast has been performed
     * with respect to the current time.
     * @param value Forecasted value.
     * @param reason Potential action which was evaluated when performing the
     * forecast. Choose one of the provided: VM_DEPLOYMENT, VM_MIGRATION,
     * VM_CANCELLATION, SERVICE_DEPLOYMENT
     */
    public void logConditionalAction(Long deltaT, Double value, String reason) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("conditional");
            if (deltaT != null) {
                resource = resource.queryParam("deltaT", deltaT.toString());
            } else {
                log.error("Parameter \"deltaT\" must be specified.");
                return;
            }
            if (value != null) {
                resource = resource.queryParam("value", value.toString());
            } else {
                log.error("Parameter \"value\" must be specified.");
                return;
            }
            if (reason != null) {
                resource = resource.queryParam("reason", reason);
            } else {
                log.error("Parameter \"reason\" must be specified.");
                return;
            }
            resource.post();
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void storeVMCPUUtilization(String vmId, long timeStamp, double cpuUtilization) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("cpuvalidation").path(vmId).path("store").queryParam("ts", Long.toString(timeStamp)).queryParam("cpu", Double.toString(cpuUtilization));
            resource.post();
        } catch (Exception ex) {
            //Silent mode. No exceptions if Eco-Validator not in operation.
        }
    }

    public void performForecasts(String vmId, long timeStamp) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("cpuvalidation").path(vmId).path("evaluate").queryParam("ts", Long.toString(timeStamp));
            resource.post();
        } catch (Exception ex) {
            //Silent mode. No exceptions if Eco-Validator not in operation.
        }
    }
    
    public void writePowerInformation(String nodeId, double cpuUtilization, double powerConsumption) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("powermodel").path(nodeId).queryParam("cpu", Double.toString(cpuUtilization)).queryParam("power", Double.toString(powerConsumption));
            resource.post();
        } catch (Exception ex) {
            //Silent mode. No exceptions if Eco-Validator not in operation.
        }
    }
}
