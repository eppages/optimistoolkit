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
package eu.optimis.cloudoptimizer.rest.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MediaType;

public class HolisticManagementRESTClient {
    
    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "CloudOptimizer";
    
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public HolisticManagementRESTClient() {
        this(HOST, PORT, PATH);
    }

    public HolisticManagementRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public HolisticManagementRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public HolisticManagementRESTClient(String host, int port, String path) {
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
    
    public void notifyVMTrust(String vmId, double trust) {
        try {
            if(vmId == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/trust/vm/" + vmId);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(trust).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }        
    }

    public void notifyServiceTrust(String serviceID, double trust) {
        try {
            if(serviceID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/trust/service/" + serviceID);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(trust).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyPhysicalHostTrust(String physicalHostID, double trust) {
        try {
            if(physicalHostID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/trust/node/" + physicalHostID);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(trust).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyInfrastructureTrust(double trust) {
        try {          
            WebResource resource = client.resource(this.getAddress()).path("/hm/trust/ip");
            resource.type(MediaType.TEXT_PLAIN).post( new Double(trust).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }    
    
    public void notifyVMRiskLevel(String vmId, int riskLevel) {
        try {
            if(vmId == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/risk/vm/" + vmId);
            resource.type(MediaType.TEXT_PLAIN).post( new Integer(riskLevel).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }        
    }

    public void notifyServiceRiskLevel(String serviceID, int riskLevel) {
        try {
            if(serviceID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/risk/service/" + serviceID);
            resource.type(MediaType.TEXT_PLAIN).post( new Integer(riskLevel).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyPhysicalHostRiskLevel(String physicalHostID, int riskLevel) {
        try {
            if(physicalHostID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/risk/node/" + physicalHostID);
            resource.type(MediaType.TEXT_PLAIN).post( new Integer(riskLevel).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyInfrastructureRiskLevel(int riskLevel) {
        try {          
            WebResource resource = client.resource(this.getAddress()).path("/hm/risk/ip");
            resource.type(MediaType.TEXT_PLAIN).post( new Integer(riskLevel).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }    
    
    public void notifyVMEco(String vmId, double eco, String type, long intervalMS) {
        try {
            if(vmId == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/eco/vm/" + type + "/" + intervalMS + "/" + vmId);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(eco).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }        
    }

    public void notifyServiceEco(String serviceID, double eco, String type, long intervalMS) {
        try {
            if(serviceID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/eco/service/" + type + "/" + intervalMS + "/"  + serviceID);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(eco).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyPhysicalHostEco(String physicalHostID, double eco, String type, long intervalMS) {
        try {
            if(physicalHostID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/eco/node/" + type + "/" + intervalMS + "/"  + physicalHostID);
            resource.type(MediaType.TEXT_PLAIN).post(new Double(eco).toString());
        } catch (UniformInterfaceException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);                        
            ClientResponse cr = ex.getResponse();
            sw.append(cr.toString());
            throw new RuntimeException("Error when calling service: " + cr.getStatus() + "\n " + sw.toString(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyInfrastructureEco(double eco, String type, long intervalMS) {
        try {          
            WebResource resource = client.resource(this.getAddress()).path("/hm/eco/ip/" + type + "/" + intervalMS);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(eco).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }    

    public void notifyVMCost(String vmId, double cost) {
        try {
            if(vmId == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/cost/vm/" + vmId);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(cost).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }        
    }

    public void notifyServiceCost(String serviceID, double cost) {
        try {
            if(serviceID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/cost/service/" + serviceID);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(cost).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyPhysicalHostCost(String physicalHostID, double cost) {
        try {
            if(physicalHostID == null) {
                throw new NullPointerException("vmId must not be null");
            }            
            WebResource resource = client.resource(this.getAddress()).path("/hm/cost/node/" + physicalHostID);
            resource.type(MediaType.TEXT_PLAIN).post( new Double(cost).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }

    public void notifyInfrastructureCost(double cost) {
        try {          
            WebResource resource = client.resource(this.getAddress()).path("/hm/cost/ip");
            resource.type(MediaType.TEXT_PLAIN).post( new Double(cost).toString());
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            throw new RuntimeException("Error when calling service: " + cr.getStatus(), ex);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error when calling service", e);
        }            
    }    
    
}
