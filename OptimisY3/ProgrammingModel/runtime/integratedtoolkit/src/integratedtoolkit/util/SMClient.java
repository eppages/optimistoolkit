/*
 *  Copyright 2002-2013 Barcelona Supercomputing Center (www.bsc.es)
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
package integratedtoolkit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.xmlbeans.XmlException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import eu.optimis.serviceManager.InfrastructureProviderDocument.InfrastructureProvider;
import eu.optimis.serviceManager.ServiceDocument;
import eu.optimis.serviceManager.VmsDocument.Vms;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class SMClient {

    private final static String MI_DATA_PATH = ":8087/data/";
    private boolean isRemote;
    private String location;
    private WebResource serviceManager;

    public SMClient(String location) {
        if (location.startsWith("http://")) {
            isRemote = true;
            Client c = Client.create();
            serviceManager = c.resource(location);
        } else {
            isRemote = false;
            this.location = location + File.separator + "SM.xml";
            System.out.println("Working with a fake Endpoint");
        }
    }

    public String getStatus(String serviceID) throws XmlException {
        String xml = serviceManager.path(serviceID).get(String.class);
        ServiceDocument service = ServiceDocument.Factory.parse(xml);
        return service.getService().getStatus();
    }

    public String updateVmStatus(String serviceId,
            String infrastructureProviderId, String vmId, String status) {

        if (isRemote) {
            return serviceManager.path(serviceId).path("ip").path(infrastructureProviderId).path("vms").path(vmId).path("status").put(String.class, status);
        } else {
            //
            return "";
        }
    }

    public Map<String, String> getMonitoringDataEndpoints(String serviceID) throws XmlException {
        Map<String, String> map = new HashMap<String, String>();
        String xml = serviceManager.path(serviceID).get(String.class);
        ServiceDocument service = ServiceDocument.Factory.parse(xml);
        InfrastructureProvider[] ips = service.getService().getInfrastructureProviderArray();
        for (InfrastructureProvider ip : ips) {
            String ip_address = ip.getIpAddress();
            map.put(ip.getId(), "http://" + ip_address + MI_DATA_PATH);
        }

        return map;
    }

    public Map<String, String> getIPAddresses(String serviceID) throws XmlException {
        Map<String, String> map = new HashMap<String, String>();
        String xml = serviceManager.path(serviceID).get(String.class);
        ServiceDocument service = ServiceDocument.Factory.parse(xml);
        InfrastructureProvider[] ips = service.getService().getInfrastructureProviderArray();
        for (InfrastructureProvider ip : ips) {
            map.put(ip.getId(), ip.getIpAddress());
        }

        return map;
    }

    public LinkedList<InfrastructureProvider> getProvidersData(String serviceID) throws XmlException {

        LinkedList<InfrastructureProvider> ips = new LinkedList<InfrastructureProvider>();
        
        
        String xml;
        if (isRemote) {
            xml = serviceManager.path(serviceID).get(String.class);
        } else {
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(location);
                int size = fis.available();
                byte[] read = new byte[size];
                fis.read(read);

                xml = new String(read, "UTF-8");
            } catch (Exception e) {
                xml = "";
            }
        }
        ServiceDocument service = ServiceDocument.Factory.parse(xml);
        ips.addAll(Arrays.asList(service.getService().getInfrastructureProviderArray()));
        return ips;
    }

    public static void main(String[] args) throws XmlException {
        SMClient smc = new SMClient("http://optimis-spvm.atosorigin.es:8080/ServiceManager/services/");
        String serviceID = "d7df4732-d379-4b6c-95b1-30bfdee705ed";
        System.out.println("Status: " + smc.getStatus(serviceID));
        Map<String, String> map = smc.getIPAddresses(serviceID);
        for (Entry<String, String> e : map.entrySet()) {
            System.out.println("Entry: " + e.getKey() + " - " + e.getValue());
        }
        map = smc.getMonitoringDataEndpoints(serviceID);
        for (Entry<String, String> e : map.entrySet()) {
            System.out.println("Entry: " + e.getKey() + " - " + e.getValue());
        }
        LinkedList<InfrastructureProvider> ips = smc.getProvidersData(serviceID);
        
        for (InfrastructureProvider ip : ips) {
            System.out.println("Entry: " + ip.getId() + " - " + ip.getVms().toString());
        }


    }
}
