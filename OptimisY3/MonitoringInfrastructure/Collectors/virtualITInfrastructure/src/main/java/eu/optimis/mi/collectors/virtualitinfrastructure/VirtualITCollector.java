/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 **/
package eu.optimis.mi.collectors.virtualitinfrastructure;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.List;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.spi.resource.Singleton;

import java.io.IOException;
import net.emotivecloud.vrmm.rm.rest.client.RMClient;
import net.emotivecloud.vrmm.scheduler.VRMMSchedulerException;
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;

import javax.ws.rs.core.MediaType;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.postClient;
import java.util.ResourceBundle;

@Path("/")
@Singleton
public class VirtualITCollector extends Thread {
    ResourceBundle rb = ResourceBundle.getBundle("config");

    private static final Log log = LogFactory.getLog(VirtualITCollector.class);
    private static final int INTERVAL = 60000;

    String rm_host;
    int rm_port;
    String nodeId;
    CloudOptimizerRESTClient co;
    RMClient rm;
    XMLParser xmlp;
    postClient aggregator_post;

    public VirtualITCollector() {
        try {
            rm_host = rb.getString("config.rm_host");
            rm_port = Integer.parseInt(rb.getString("config.rm_port"));
            this.start();
            //co = new CloudOptimizerRESTClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            /*co = new CloudOptimizerRESTClient();
            rm = new RMClient(RM_HOST, RM_PORT);
            //aggregator_post = new postClient(TODO IP address, port);
            xmlp = new XMLParser();
            
            List<String> VMsIds = new LinkedList<String>();
            String host = "localhost:8080";
            MonitoringResourceDatasets xml_ret = null;
            String aux = null;
            
            while (true) {
            List<String> nodes = co.getNodesId();
            xml_ret = "<MonitoringResources>";
            for (int i=0; i<nodes.size(); i++) {
            String nodeId = nodes.get(i);
            List<String> vms = co.getVMsId(nodeId);
            for (int j=0; j<vms.size(); j++) {
            aux = rm.getMeasuredData(VMsIds.get(i));
            if (aux == null) {
            log.info("Null data coming from EMOTIVE Resource Monitoring");
            throw new Exception();
            }
            xml_ret = xml_ret.concat(xmlp.gangliaXMLtoMonitoringResources(aux));
            }	            	
            }
            xml_ret = xml_ret.concat("</MonitoringResources>");
            log.info("[DEBUG ]OUTPUT = " + xml_ret);
            
            //Aggregator's push operation
            aggregator_post.pushReport(xml_ret);
            }*/

            Thread.sleep(INTERVAL);
        } catch (Exception e) {
            log.error("Exception " + e + " in running thread of class Monitor.");
            e.printStackTrace();
        }
    }

    /* 
     * Aggregator's pull operation
     */
    @GET
    @Path("/virtualmonitoring/data")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public MonitoringResourceDatasets getData() throws VRMMSchedulerException, IOException, Exception {
        MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
        List<MonitoringResourceDataset> mset = new LinkedList<MonitoringResourceDataset>();
        List<MonitoringResourceDataset> mset_aux = new LinkedList<MonitoringResourceDataset>();

        co = new CloudOptimizerRESTClient();
        rm = new RMClient(rm_host, rm_port);
        xmlp = new XMLParser();

        String info = null;
        List<String> nodes = co.getNodesId();

        for (int i = 0; i < nodes.size(); i++) {
            String nodeId = nodes.get(i);
            List<String> vms = co.getVMsId(nodeId);            
            //List<String> vms = co.getVMsId("optimis1");
            log.info("There are " + vms.size() + " VMs running in host " + nodeId);
            for (int j = 0; j < vms.size(); j++) {
                info = rm.getMeasuredData(vms.get(j));
                if (info == null) {
                    log.info("Null data coming from EMOTIVE Resource Monitoring");
                    throw new Exception();
                }
                mset_aux = xmlp.gangliaXMLtoMonitoringResources(info);
                for (MonitoringResourceDataset a : mset_aux) {
                    a.setPhysical_resource_id(nodeId);
                    mset.add(a);
                }
            }
        }
        msets.setMonitoring_resource(mset);
        return msets;
    }
    
    /* 
     * Aggregator's pull operation
     */
    @GET
    @Path("/virtualmonitoring/data/str")
    @Produces({MediaType.TEXT_PLAIN})
    public String getDataStr() throws VRMMSchedulerException, IOException, Exception {
        MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
        List<MonitoringResourceDataset> mset = new LinkedList<MonitoringResourceDataset>();
        List<MonitoringResourceDataset> mset_aux = new LinkedList<MonitoringResourceDataset>();

        co = new CloudOptimizerRESTClient();
        rm = new RMClient(rm_host, rm_port);
        xmlp = new XMLParser();

        String info = null;
        List<String> nodes = co.getNodesId();

        for (int i = 0; i < nodes.size(); i++) {
            String nodeId = nodes.get(i);
            List<String> vms = co.getVMsId(nodeId);            
            //List<String> vms = co.getVMsId("optimis1");
            log.info("There are " + vms.size() + " VMs running in host " + nodeId);
            for (int j = 0; j < vms.size(); j++) {
                info = rm.getMeasuredData(vms.get(j));
                if (info == null) {
                    log.info("Null data coming from EMOTIVE Resource Monitoring");
                    throw new Exception();
                }
                mset_aux = xmlp.gangliaXMLtoMonitoringResources(info);
                for (MonitoringResourceDataset a : mset_aux) {
                    a.setPhysical_resource_id(nodeId);
                    mset.add(a);
                }
            }
        }
        msets.setMonitoring_resource(mset);
        String xml = xmlConstruction(msets);
        return xml;
    }
    
    private String xmlConstruction(MonitoringResourceDatasets dsets){
    	String bg = "<?xml version=\"1.0\" encoding=\"utf-8\"?><MonitoringResources>";
    	String ed = "</MonitoringResources>";
    	StringBuffer ct = new StringBuffer();
    	if (dsets.getMonitoring_resource()!=null){
    		for (MonitoringResourceDataset a : dsets.getMonitoring_resource()){
        		ct.append("<monitoring_resource>");
        		ct.append("<physical_resource_id>").append(a.getPhysical_resource_id()).append("</physical_resource_id>");
        		ct.append("<virtual_resource_id>").append(a.getVirtual_resource_id()).append("</virtual_resource_id>");
        		if(a.getService_resource_id()==null)
        			ct.append("<service_resource_id></service_resource_id>");
        		else
        			ct.append("<service_resource_id>").append(a.getService_resource_id()).append("</service_resource_id>");
        		ct.append("<metric_name>").append(a.getMetric_name()).append("</metric_name>");
        		ct.append("<metric_unit>").append(a.getMetric_unit()).append("</metric_unit>");
        		ct.append("<metric_value>").append(a.getMetric_value()).append("</metric_value>");
        		java.util.Date date = a.getMetric_timestamp();
        		long unixtp = date.getTime()/1000;
        		ct.append("<metric_timestamp>").append(unixtp).append("</metric_timestamp>");
        		ct.append("<resource_type>").append(a.getResource_type()).append("</resource_type>");
        		if (a.getMonitoring_information_collector_id()==null || a.getMonitoring_information_collector_id().length()==0)
        			ct.append("<monitoring_information_collector_id>003</monitoring_information_collector_id>");
        		else
        		ct.append("<monitoring_information_collector_id>").append(a.getMonitoring_information_collector_id())
        		.append("</monitoring_information_collector_id>");
        		ct.append("</monitoring_resource>");
    		}
    	}
    	return bg+ct.toString()+ed;	
    }
}
