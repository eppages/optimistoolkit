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
package eu.optimis.vmmanager.rest.client;

import net.emotivecloud.commons.ListStrings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.cloudoptimizer.blo.BLOUtils;
import eu.optimis.schemas.trec.blo.BusinessDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.emotivecloud.vrmm.scheduler.VRMMSchedulerException;

/**
 * Client of the VM Manager REST API
 * @author J. Oriol Fitó (josep.oriol@bsc.es)
 *
 */
public class VMManagerRESTClient {

    private Client client = null;
    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static String PATH = "VMManager";
    
    private String host = HOST;
    private int port = PORT;
    private String path = PATH;

    public VMManagerRESTClient() {
        this(HOST, PORT, PATH);
    }

    public VMManagerRESTClient(String host) {
        this(host, PORT, PATH);
    }

    public VMManagerRESTClient(String host, int port) {
        this(host, port, PATH);
    }

    public VMManagerRESTClient(String host, int port, String path) {
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

    
    /**
     * Used to create a VM by means of an OVF
     * @returns OVF with the information updated
     */
    public synchronized String addVM(String OVF, String trec_opt) throws VRMMSchedulerException {
        String ovf_ret = null;
        try {
            if (OVF != null) {
                WebResource resource = client.resource(this.getAddress()).path("compute").queryParam("trec_opt", trec_opt);
                ovf_ret = resource.type(MediaType.APPLICATION_XML).post(String.class, OVF);
            }
        } catch (URISyntaxException e) {
            throw new VRMMSchedulerException(e.getMessage(), e);
        }
        return ovf_ret;
    }

    /**
     * It allows removing an already created VM
     */
    public synchronized void removeVM(String vmId) throws VRMMSchedulerException {
        try {
            if (vmId != null) {
                WebResource resource = client.resource(this.getAddress());
                resource.path("compute").path(vmId).delete();
            }
        } catch (UniformInterfaceException ex) {
            ClientResponse cr = ex.getResponse();
            cr.getStatus();
            ex.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to get the location of a given VM
     * @returns location
     */
    public String getLocation(String vmId) {
        String resp = null;
        try {
            if (vmId != null) {
                WebResource resource = client.resource(this.getAddress()).path("/compute/location").queryParam("vmid", vmId);
                resp = resource.type(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).get(String.class);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return resp;
    }

    /**
     * Suggests a migration for a VM
     * @param vmId the vm to migrate
     * @return true if the migration has been performed. false if not
     */
    public boolean migrateVM(String vmId) {
        boolean done = false;
        try {
            WebResource resource = client.resource(this.getAddress()).path("/compute/" + vmId + "/migrate");
            done = new Boolean(resource.type(MediaType.TEXT_PLAIN).post(String.class,vmId));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return done;
    }

    public List<String> getVMsAtNode(String nodeId) {
        List<String> ret = new ArrayList<String>();
        try {
            WebResource resource = client.resource(this.getAddress()).path("/allcompute/"+nodeId);
            ListStrings ls = resource.get(ListStrings.class);
            for(int i = 0 ; i < ls.size() ; i++) {
                ret.add(ls.get(i));
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }


}