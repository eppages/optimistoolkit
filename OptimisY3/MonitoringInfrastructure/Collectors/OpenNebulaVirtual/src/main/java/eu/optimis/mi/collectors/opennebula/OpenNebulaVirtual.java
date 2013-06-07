/*
 * Copyright 2012 University of Stuttgart
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.optimis.mi.collectors.opennebula;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.opennebula.client.Client;
import org.opennebula.client.ClientConfigurationException;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

import com.sun.jersey.spi.resource.Singleton;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

@Path("/")
@Singleton
public class OpenNebulaVirtual {

	private static Client cl;
	private final static Logger logger = Logger
			.getLogger(OpenNebulaVirtual.class.getName());
	ResourceBundle rb = ResourceBundle.getBundle("config");
	
	String onl_login;
	String onl_host;
	public OpenNebulaVirtual() {
		try {
			 onl_login = rb.getString("onl.userpd");
	         onl_host = rb.getString("onl.host.oca");
			cl = new Client(onl_login, onl_host);
		} catch (ClientConfigurationException e) {
			logger.error("opennebula-virtual: couldn't get connection with opennebula host. host:"+onl_host+" login:"+onl_login);
			e.printStackTrace();
		}catch (Exception e) {
	            e.printStackTrace();
	    }
	}

	@GET
	@Path("/virtual/data/str")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getDataStr() throws Exception {
		MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> mset_pool = new LinkedList<MonitoringResourceDataset>();
		if (cl==null){
			logger.error("opennebula-virtual: client is null");
			return "<?xml version=\"1.0\" encoding=\"utf-8\"?><MonitoringResources></MonitoringResources>";
		}
		VirtualMachinePool vmp = new VirtualMachinePool(cl, -2);
		// TODO  Check if the response is ok 
		OneResponse es = vmp.info();

		for (VirtualMachine vm : vmp) {
			OneResponse res = vm.info();
			String abc = res.getMessage();
			List<MonitoringResourceDataset> vmset = new LinkedList<MonitoringResourceDataset>();
			String metric_name = vm.xpath("CPU");
			for (String metric: getVirtualMetrics()){
				MonitoringResourceDataset mds = new MonitoringResourceDataset();
				if (metric.equals("CPU")){
					mds.setMetric_name("cpu_user");
					mds.setMetric_unit("%");
					mds.setMetric_value(vm.xpath("CPU"));
				}
				else if (metric.equals("MEMORY")){
					mds.setMetric_name("mem_used");
					mds.setMetric_unit("%");
					mds.setMetric_value(vm.xpath("MEMORY"));
				}
				else if (metric.equals("STATE")){
					mds.setMetric_name("vm_state");
					mds.setMetric_unit("");
					mds.setMetric_value(vm.xpath("STATE"));
				}
				else if (metric.equals("NET_TX")){
					mds.setMetric_name("bytes_in");
					mds.setMetric_unit("byte");
					mds.setMetric_value(vm.xpath("NET_TX"));
				}
				else if (metric.equals("NET_RX")){
					mds.setMetric_name("bytes_out");
					mds.setMetric_unit("byte");
					mds.setMetric_value(vm.xpath("NET_RX"));
				}
				else 
					mds.setMetric_name("Unknow "+metric);
				long tstamp = Long.parseLong(vm.xpath("LAST_POLL"));
				mds.setMetric_timestamp(new Date((long)tstamp*1000));
				mds.setMonitoring_information_collector_id("opennebula");
				mds.setPhysical_resource_id(vm.xpath("HISTORY_RECORDS/HISTORY/HOSTNAME"));
				mds.setResource_type("virtual");
				mds.setVirtual_resource_id(vm.xpath("ID"));
				vmset.add(mds);
			}
			mset_pool.addAll(vmset);
		}
		msets.setMonitoring_resource(mset_pool);
		String xml = xmlConstruction(msets);
		return xml;
	}
	
	private String[] getVirtualMetrics(){
		String[] metrics = {"CPU","MEMORY", "NET_TX","NET_RX","STATE"};
		//String[] metrics = {"CPU"};
		return metrics;
	
	}

	private String xmlConstruction(MonitoringResourceDatasets dsets) {
		String bg = "<?xml version=\"1.0\" encoding=\"utf-8\"?><MonitoringResources>";
		String ed = "</MonitoringResources>";
		StringBuffer ct = new StringBuffer();
		if (dsets.getMonitoring_resource() != null) {
			for (MonitoringResourceDataset a : dsets.getMonitoring_resource()) {
				ct.append("<monitoring_resource>");
				ct.append("<physical_resource_id>")
						.append(a.getPhysical_resource_id())
						.append("</physical_resource_id>");
				ct.append("<virtual_resource_id>")
						.append(a.getVirtual_resource_id())
						.append("</virtual_resource_id>");
				if (a.getService_resource_id() == null)
					ct.append("<service_resource_id></service_resource_id>");
				else
					ct.append("<service_resource_id>")
							.append(a.getService_resource_id())
							.append("</service_resource_id>");
				ct.append("<metric_name>").append(a.getMetric_name())
						.append("</metric_name>");
				ct.append("<metric_unit>").append(a.getMetric_unit())
						.append("</metric_unit>");
				ct.append("<metric_value>").append(a.getMetric_value())
						.append("</metric_value>");
				java.util.Date date = a.getMetric_timestamp();
				long unixtp = date.getTime() / 1000;
				ct.append("<metric_timestamp>").append(unixtp)
						.append("</metric_timestamp>");
				ct.append("<resource_type>").append(a.getResource_type())
						.append("</resource_type>");
				if (a.getMonitoring_information_collector_id() == null
						|| a.getMonitoring_information_collector_id().length() == 0)
					ct.append("<monitoring_information_collector_id>003</monitoring_information_collector_id>");
				else
					ct.append("<monitoring_information_collector_id>")
							.append(a.getMonitoring_information_collector_id())
							.append("</monitoring_information_collector_id>");
				ct.append("</monitoring_resource>");
			}
		}
		return bg + ct.toString() + ed;
	}

	public static void main(String[] args) throws Exception {
		OpenNebulaVirtual ov = new OpenNebulaVirtual();
		System.out.println(ov.getDataStr());

//		// host
//		hostPool = new HostPool(cl);
//		hostPool.info();
//
//		Host h = new Host(0, cl);
//		h.info();
//		System.out.println(h.getName());
//		OneResponse or = Host.info(cl, 0);
//		System.out.println(or.getMessage());
//
//		for (Host ht : hostPool) {
//			System.out.println("Hi");
//			System.out.println(ht.getName());
//		}
//
//		// vm
//		VirtualMachinePool vmp = new VirtualMachinePool(cl, -2);
//		OneResponse es = vmp.info();
//		for (VirtualMachine vm : vmp) {
//			OneResponse res = vm.info();
//			String abc = res.getMessage();
//			System.out.println(vm.xpath("TEMPLATE/CPU"));
//		}

	}

	// <STATE>3</STATE> status of VM
	// <LCM_STATE>3</LCM_STATE>
	// <RESCHED>0</RESCHED>
	// <STIME>1360749653</STIME>
	// <ETIME>0</ETIME>
	// <DEPLOY_ID>one-31</DEPLOY_ID>
	// <MEMORY>1051488</MEMORY> Memory used by teh VM - mem_used
	// <CPU>0</CPU> CPU percentage used by the VM - cpu_user
	// <NET_TX>467968</NET_TX> bytes_out
	// <NET_RX>204800</NET_RX> bytes_in
	// os-release 2.6.20-xen3.1
	// || name.equalsIgnoreCase("machine_type") -no x86_64 -
	// <IMAGE><![CDATA[Centos 6.3]]></IMAGE>
	// || name.equalsIgnoreCase("cpu_vnum") -no <CPU><![CDATA[1]]></CPU> |
	// ganglia cpu_num
	// || name.equalsIgnoreCase("cpu_speed") -no ganglia cpu_speed
	// || name.equalsIgnoreCase("cpu_user")
	// || name.equalsIgnoreCase("mem_total") -no ganglia mem_total
	// || name.equalsIgnoreCase("mem_used")
	// || name.equalsIgnoreCase("disk_total") -no <DISK
	// <CLONE><![CDATA[YES]]></CLONE>
	// || name.equalsIgnoreCase("bytes_in")
	// || name.equalsIgnoreCase("bytes_out")
	// || name.equalsIgnoreCase("vm_state" - running-available
	// <HOSTNAME>optimis2.leeds</HOSTNAME>

}
