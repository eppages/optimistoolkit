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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;
import net.emotivecloud.commons.HashMapStrings;
import net.emotivecloud.commons.ListStrings;
import net.emotivecloud.utils.ovf.OVFWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client (to be used by IPs) of the Eco-efficiency tool REST API.
 *
 * @author J. Oriol Fitó (josep.oriol@bsc.es) and Josep Subirats
 * (josep.subirats@bsc.es).
 *
 */
public class EcoEfficiencyToolRESTClientIP {

    private static final Log log = LogFactory.getLog(EcoEfficiencyToolRESTClientIP.class);
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "EcoEfficiencyTool";
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;
    public static final Double DISABLE_THRESHOLD = Double.NEGATIVE_INFINITY;

    /**
     * Class constructor.
     */
    public EcoEfficiencyToolRESTClientIP() {
        this(HOST, PORT, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool IP is deployed.
     */
    public EcoEfficiencyToolRESTClientIP(String host) {
        this(host, PORT, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool IP is deployed.
     * @param port Port where the Ecoefficiency Tool IP is deployed.
     */
    public EcoEfficiencyToolRESTClientIP(String host, int port) {
        this(host, port, PATH);
    }

    /**
     * Class constructor.
     *
     * @param host Host where the Ecoefficiency Tool IP is deployed.
     * @param port Port where the Ecoefficiency Tool IP is deployed.
     * @param path Path where the Ecoefficiency Tool IP is deployed.
     */
    public EcoEfficiencyToolRESTClientIP(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;

        DefaultClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
    }

    /**
     * Builds the address to be used to connect to the EcoEfficiencyToolIP.
     *
     * @return The address to be used to connect to the EcoEfficiencyToolIP
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
     * Starts the assessment of the IP at infrastructure level.
     *
     * @param timeout Timeout between successive ecoefficiency automatic
     * assessments. A default timeout value will be used if this parameter is
     * not specified.
     */
    public void startAssessment(Long timeout) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure");
            if (timeout != null) {
                resource = resource.queryParam("timeout", timeout.toString());
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

    /**
     * Stops the assessment of a given IP (infrastructure)
     */
    public void stopAssessment() {
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

    /**
     * Returns the current eco-efficiency of an IP infrastructure
     *
     * @param type Type of eco-efficiency assessment: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency assessment of the IP.
     */
    public String assessIPEcoEfficiency(String type) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("assessecoefficiency");
            if (type != null) {
                resource = resource.queryParam("type", type);
            }
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

    /**
     * Predicts IP’s overall eco-efficiency.
     *
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiency(Long timeSpan, String type) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiency");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }

            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).get(String.class);

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
     * DEPRECATED: Returns the foreseen eco-efficiency of an IP infrastructure
     */
    /**
     * Forecasts the EcoEfficiency of the IP provider when deploying the
     * specified set of VMs. Note that some of them may be already running in
     * the IP.
     *
     * @param ovfs Set of VMs to be deployed in the IP. Some of them can be
     * already running. If a running VM is not specified in this set, it is
     * assumed that it has to be destroyed and therefore will not be treated as
     * a running VM when performing the prediction.
     * @return The IP EcoEfficiency prediction.
     */
    /*
     * public synchronized String forecastIPEcoEfficiency(List<String> ovfs,
     * Long timeSpan, String type) { String ret = null; try { WebResource
     * resource =
     * client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiency");
     * if (timeSpan != null) { resource = resource.queryParam("timeSpan",
     * timeSpan.toString()); } if (type != null) { resource =
     * resource.queryParam("type", type); }
     *
     * ListStrings ovfList = new ListStrings(); if(ovfs != null) { for (String
     * ovf : ovfs) { ovfList.add(ovf); } }
     *
     *
     * ret =
     * resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class,
     * ovfList);
     *
     * } catch (UniformInterfaceException ex) { ClientResponse cr =
     * ex.getResponse(); log.error(cr.getStatus()); ex.printStackTrace(); }
     * catch (URISyntaxException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } return ret;
     }
     */
    /**
     * Predicts IP’s overall eco-efficiency upon a service deployment.
     *
     * @param manifest Service manifest XML descriptor.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyServiceDeployment(String manifest, Long timeSpan, String type) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyPlusService");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }

            if (manifest == null) {
                log.error("Manifest can not be null");
                return "-1.0";
            }


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
     * Predicts IP’s overall eco-efficiency upon a VM cancellation (VM to be
     * shut down).
     *
     * @param vmId VM identifier of the VM to be cancelled
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyVMCancellation(String vmId, Long timeSpan, String type) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyVMCancellation");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }

            if (vmId == null) {
                log.error("vmId can not be null");
                return null;
            } else {
                resource = resource.queryParam("vmId", vmId);
            }


            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).get(String.class);

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
     * Predicts IP’s overall eco-efficiency upon a VM deployment in a known
     * physical host.
     *
     * @param ovfDom OVF descriptor of the VM to be deployed, generated using
     * EMOTIVE’s OVFWrapper (see Installation Guide).
     * @param destNode Physical node where the VM will be deployed.
     * @param activeNodes List of active physical nodes.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyVMDeploymentKnownPlacement(OVFWrapper ovfDom, String destNode, List<String> activeNodes, String type, Long timeSpan) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyVMDeploymentKnownPlacement");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }
            if (destNode == null) {
                log.error("destNode can not be null");
                return null;
            } else {
                resource = resource.queryParam("destNode", destNode);
            }

            ListStrings ovfPlusActiveNodes = new ListStrings();
            ovfPlusActiveNodes.add(ovfDom.toString());
            for (String activeNode : activeNodes) {
                ovfPlusActiveNodes.add(activeNode);
            }

            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, ovfPlusActiveNodes);

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
     * Predicts IP’s overall eco-efficiency upon a VM migration to a known
     * physical host.
     *
     * @param vmId VM identifier of the VM to be migrated.
     * @param destNode Physical node where the VM will be migrated to.
     * @param activeNodes List of active physical nodes.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyVMMigrationKnownPlacement(String vmId, String destNode, List<String> activeNodes, String type, Long timeSpan) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyVMMigrationKnownPlacement");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }
            if (destNode == null) {
                log.error("destNode can not be null");
                return null;
            } else {
                resource = resource.queryParam("destNode", destNode);
            }
            if (vmId == null) {
                log.error("vmId can not be null");
                return null;
            } else {
                resource = resource.queryParam("vmId", vmId);
            }

            ListStrings activeNodesList = new ListStrings();
            for (String activeNode : activeNodes) {
                activeNodesList.add(activeNode);
            }

            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, activeNodesList);

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
     * Predicts IP’s overall eco-efficiency upon a VM deployment in a yet
     * unknown physical host.
     *
     * @param ovfDom OVF descriptor of the VM to be deployed, generated using
     * EMOTIVE’s OVFWrapper (see Installation Guide).
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(OVFWrapper ovfDom, String type, Long timeSpan) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyVMDeploymentUnknownPlacement");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }

            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, ovfDom.toString());

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
     * Predicts IP’s overall eco-efficiency upon a VM migration to a yet unknown
     * physical host.
     *
     * @param vmId VM identifier of the VM to be migrated.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the IP.
     */
    public synchronized String forecastIPEcoEfficiencyVMMigrationUnknownPlacement(String vmId, String type, Long timeSpan) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("forecastecoefficiencyVMMigrationUnknownPlacement");
            if (timeSpan != null) {
                resource = resource.queryParam("timeSpan", timeSpan.toString());
            }
            if (type != null) {
                resource = resource.queryParam("type", type);
            }
            if (vmId == null) {
                log.error("vmId can not be null");
                return null;
            } else {
                resource = resource.queryParam("vmId", vmId);
            }

            ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).get(String.class);

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
     * Assesses the eco-efficiency of a node. The IP is implicit, since it is
     * the one which owns the node and executes this method.
     *
     * @param nodeId Physical node identifier.
     * @param type Type of eco-efficiency assessment: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency evaluation of the node.
     */
    public String assessNodeEcoEfficiency(String nodeId, String type) {
        String ret = null;
        try {
            if (nodeId != null) {
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path(nodeId).path("assessecoefficiency");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                ret = resource.type("text/plain").get(String.class);
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
     * Assesses the eco-efficiency of a set of nodes. The IP is implicit, since
     * it is the one which owns the nodes and executes this method.
     *
     * @param nodeList List containing the nodes’ IDs whose ecoefficiency wants
     * to be evaluated.
     * @param type Type of eco-efficiency assessment: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency evaluation of the set of requested nodes.
     */
    public List<String> assessMultipleNodesEcoEfficiency(List<String> nodeList, String type) {
        List<String> ret = null;
        try {
            if (nodeList != null) {

                ListStrings nodes = new ListStrings();
                for (String nodeId : nodeList) {
                    nodes.add(nodeId);
                }

                //ListStrings ecoAssessments = new ListStrings();
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("multiple").path("assessecoefficiency");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                //ecoAssessments = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, nodes);
                ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, nodes);

                /*
                 * ret = new LinkedList<String>(); for (String ecoAssessment :
                 * ecoAssessments) { ret.add(ecoAssessment);
                 }
                 */
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
     * Forecasts the ecoefficiency of a node. VMs in the "ovfs" parameter which
     * exist in the system will be considered as undeployments, while
     * non-present VMs specified in the "ovfs" parameter will be treated as new
     * deployments.
     *
     * @param nodeId Physical node identifier.
     * @param ovfs List of OVF descriptors of the VMs to deploy (if not existing
     * in the node) or undeploy (if existing in the node), generated using
     * EMOTIVE’s OVFWrapper (see Installation Guide).
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the node.
     */
    public String forecastNodeEcoEfficiency(String nodeId, List<String> ovfs, Long timeSpan, String type) {
        String ret = null;
        try {
            if (nodeId != null) {
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path(nodeId).path("forecastecoefficiency");
                if (timeSpan != null) {
                    resource = resource.queryParam("timeSpan", timeSpan.toString());
                }
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }

                ListStrings ovfList = new ListStrings();
                if (ovfs != null) {
                    for (String ovf : ovfs) {
                        ovfList.add(ovf);
                    }
                }

                ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(String.class, ovfList);
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
     * Predicts the eco-efficiency of a set of nodes. The IP is implicit, since
     * it is the one which owns the nodes and executes this method.
     *
     * @param nodeList List containing the nodes’ IDs whose ecoefficiency wants
     * to be predicted.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency prediction of the set of requested nodes.
     */
    public List<String> forecastMultipleNodesEcoEfficiency(List<String> nodeList, String type) {
        List<String> ret = null;
        try {
            if (nodeList != null) {

                ListStrings nodes = new ListStrings();
                for (String nodeId : nodeList) {
                    nodes.add(nodeId);
                }

                //ListStrings ecoForecasts = new ListStrings();
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("multiple").path("forecastecoefficiency");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                //ecoForecasts = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, nodes);
                ret = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(ListStrings.class, nodes);

                /*
                 * ret = new LinkedList<String>(); for (String ecoForecast :
                 * ecoForecasts) { ret.add(ecoForecast);
                 }
                 */
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
     * Starts the automatic eco-assessment of a service.
     *
     * @param serviceId Service identifier.
     * @param timeout Timeout between successive eco-efficiency automatic
     * assessments. A default timeout value will be used if this parameter is
     * not specified.
     */
    public void startServiceAssessment(String serviceId, Long timeout) {
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
    public void stopServiceAssessment(String serviceId) {
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
     * Returns the current performance being delivered to the service and its
     * associated power consumption. This method is only used by the
     * EcoEfficiency Tool for SP providers.
     *
     * @param serviceId Service identifier.
     * @return
     */
    public double[] getServicePerformancePowerAndCO2(String serviceId) {
        ListStrings result = null;
        double[] ret = {-1.0, -1.0, -1.0};
        try {
            WebResource resource = client.resource(this.getAddress()).path("service").path(serviceId).path("getPerfPowCO2");

            result = resource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).get(ListStrings.class);
            ret[0] = Double.parseDouble(result.get(0));
            ret[1] = Double.parseDouble(result.get(1));
            ret[2] = Double.parseDouble(result.get(2));
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            log.error(e);
        }
        return ret;
    }

    /**
     * @deprecated Use forecastServiceEcoEfficiency(String manifest, Long
     * timeSpan, String type) instead, since typeIdReplicas is no longer used
     * (specified inside the manifest).
     * @param manifest
     * @param typeIdReplicas
     * @param timeSpan
     * @param type
     * @return
     */
    @Deprecated
    public String forecastServiceEcoEfficiency(String manifest, HashMap<String, Integer> typeIdReplicas, Long timeSpan, String type) {
        return forecastServiceEcoEfficiency(manifest, timeSpan, type);
    }

    /**
     * Predicts the service’s eco-efficiency upon its deployment if it doesn’t
     * exist in the system, or it just predicts its future eco-efficiency
     * otherwise.
     *
     * @param manifest Service manifest XML descriptor.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency forecast of the service (to be deployed if it
     * doesn’t exist).
     */
    public String forecastServiceEcoEfficiency(String manifest, Long timeSpan, String type) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("service/forecastecoefficiency");
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
     * @param manifest Service manifest XML descriptor.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @return Energy efficiency forecast of the service (to be deployed if it
     * doesn’t exist). Ecological efficiency forecast of the service (to be
     * deployed if it doesn’t exist).
     */
    public double[] forecastServiceEnEcoEff(String manifest, Long timeSpan) {
        ListStrings result = null;
        double[] ret = {-1.0, -1.0};
        try {
            WebResource resource = client.resource(this.getAddress()).path("/service/forecastenecoeff");
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
     * Evaluates the current eco-efficiency of a VM.
     *
     * @param vmId VM identifier of the VM to be evaluated.
     * @param type Type of eco-efficiency assessment: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @return Eco-efficiency evaluation of the VM.
     */
    public String assessVMEcoEfficiency(String vmId, String type) {
        String ret = null;
        try {
            if (vmId != null) {
                WebResource resource = client.resource(this.getAddress()).path("vm").path(vmId).path("assessecoefficiency");
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
     * Predicts the future eco-efficiency of a VM.
     *
     * @param vmId VM identifier of the VM to be evaluated.
     * @param type Type of eco-efficiency forecast: energy (“energy”) or
     * ecological (“ecological”) efficiency.
     * @param timeSpan Specifies the amount of time in the future in which the
     * prediction will be made.
     * @return
     */
    public String forecastVMEcoEfficiency(String vmId, String type, Long timeSpan) {
        String ret = null;
        try {
            if (vmId != null) {
                WebResource resource = client.resource(this.getAddress()).path("vm").path(vmId).path("forecastecoefficiency");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                if (timeSpan != null) {
                    resource = resource.queryParam("timeSpan", timeSpan.toString());
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

    /**
     * *************PROACTIVE THRESHOLD-SETTING METHODS***********************
     */
    /**
     * Specifies the minimum eco-efficiency thresholds at infrastructure level.
     * If any of them are surpassed, a proactive notification will be generated.
     * Note that setting a value of -1.0 to any of the thresholds will disable
     * them (separately).
     * @param energyEfficiencyTH Energy efficiency threshold.
     * @param ecologicalEfficiencyTH Ecological efficiency threshold.
     */
    public void setInfrastructureEcoThreshold(Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        try {
            WebResource resource = client.resource(this.getAddress()).path("proactive/infrastructure/setth");
            if (energyEfficiencyTH != null) {
                resource = resource.queryParam("enEffTH", energyEfficiencyTH.toString());
            }
            if (ecologicalEfficiencyTH != null) {
                resource = resource.queryParam("ecoEffTH", ecologicalEfficiencyTH.toString());
            }
            resource.post();
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            log.error(cr.getStatus());
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specifies the minimum eco-efficiency thresholds at node level.
     * If any of them are surpassed, a proactive notification will be generated.
     * Note that setting a value of -1.0 to any of the thresholds will disable
     * them (separately).
     * @param nodeId Node identifier.
     * @param energyEfficiencyTH Energy efficiency threshold.
     * @param ecologicalEfficiencyTH Ecological efficiency threshold.
     */
    public void setNodeEcoThreshold(String nodeId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        try {
            if (nodeId != null) {
                WebResource resource = client.resource(this.getAddress()).path("proactive/node").path(nodeId).path("setth");
                if (energyEfficiencyTH != null) {
                    resource = resource.queryParam("enEffTH", energyEfficiencyTH.toString());
                }
                if (ecologicalEfficiencyTH != null) {
                    resource = resource.queryParam("ecoEffTH", ecologicalEfficiencyTH.toString());
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
     * Specifies the minimum eco-efficiency thresholds at service level.
     * If any of them are surpassed, a proactive notification will be generated.
     * Note that setting a value of -1.0 to any of the thresholds will disable
     * them (separately).
     * @param serviceId Service identifier.
     * @param energyEfficiencyTH Energy efficiency threshold.
     * @param ecologicalEfficiencyTH Ecological efficiency threshold.
     */
    public void setServiceEcoThreshold(String serviceId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        try {
            if (serviceId != null) {
                WebResource resource = client.resource(this.getAddress()).path("proactive/service").path(serviceId).path("setth");
                if (energyEfficiencyTH != null) {
                    resource = resource.queryParam("enEffTH", energyEfficiencyTH.toString());
                }
                if (ecologicalEfficiencyTH != null) {
                    resource = resource.queryParam("ecoEffTH", ecologicalEfficiencyTH.toString());
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
     * Specifies the minimum eco-efficiency thresholds at VM level.
     * If any of them are surpassed, a proactive notification will be generated.
     * Note that setting a value of -1.0 to any of the thresholds will disable
     * them (separately).
     *
     * @param vmId VM identifier.
     * @param energyEfficiencyTH Energy efficiency threshold.
     * @param ecologicalEfficiencyTH Ecological efficiency threshold.
     */
    public void setVMEcoThreshold(String vmId, Double energyEfficiencyTH, Double ecologicalEfficiencyTH) {
        try {
            if (vmId != null) {
                WebResource resource = client.resource(this.getAddress()).path("proactive/vm").path(vmId).path("setth");
                if (energyEfficiencyTH != null) {
                    resource = resource.queryParam("enEffTH", energyEfficiencyTH.toString());
                }
                if (ecologicalEfficiencyTH != null) {
                    resource = resource.queryParam("ecoEffTH", ecologicalEfficiencyTH.toString());
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
     **METRIC OBTENTION METHODS: Just used by the TREC GUI for displaying
     * purposes**
     */
    /**
     * Returns the maximum eco-efficiency of a given node.
     */
    public String getNodeMaxEco(String nodeId, String type) {
        String ret = null;
        try {
            if (nodeId != null) {
                WebResource resource = client.resource(this.getAddress()).path("infrastructure").path(nodeId).path("maxeco");
                if (type != null) {
                    resource = resource.queryParam("type", type);
                }
                ret = resource.type("text/plain").get(String.class);
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
     * Returns the mean performance of a single CPU at 100% of CPU utilisation,
     * considering all the CPUs in the datacenter.
     */
    public String getCPUMeanPerformance() {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path("cpumeanperformance");
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

    /**
     * Returns the number of CPUs of a node.
     *
     * @param nodeId Physical node identifier.
     */
    public String getCPUNumber(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("cpunumber");
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

    /**
     * Returns the performance of a single CPU at 100% of CPU utilisation.
     *
     * @param nodeId Physical node identifier.
     */
    public String getCPUPerformance(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("cpuperformance");
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

    /**
     * Returns the performance of the node at 100% of CPU utilization.
     *
     * @param nodeId Physical node identifier.
     */
    public String getMaxPerformance(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("performance");
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

    /**
     * Returns the number of used CPUs in the node.
     *
     * @param nodeId Physical node identifier.
     */
    public String getNodeUsedCPUs(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("usedcpus");
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

    /**
     * Returns the node minimum power.
     *
     * @param nodeId Physical node identifier.
     */
    public String getPidle(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("pidle");
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

    /**
     * Returns the node maximum power.
     *
     * @param nodeId Physical node identifier.
     */
    public String getPMax(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("pmax");
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

    /**
     * Returns the incremental power per CPU.
     *
     * @param nodeId Physical node identifier.
     */
    public String getPIncr(String nodeId) {
        String ret = null;
        try {
            WebResource resource = client.resource(this.getAddress()).path("infrastructure").path("metrics").path(nodeId).path("pincr");
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
}
