/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.trust;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.ip.service.clients.COClient;
import eu.optimis.tf.ip.service.clients.MonitoringClient;
import eu.optimis.tf.ip.service.operators.Statistics;
import eu.optimis.tf.ip.service.utils.GetIPManifestValues;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

abstract class VMGap {

	private boolean production = false;
	Logger log = Logger.getLogger(this.getClass().getName());
	private String coHost;
	private int coPort;
	
	public VMGap() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		coHost = PropertiesUtils.getProperty("TRUST","co.host");
		coPort = Integer.valueOf(PropertiesUtils.getProperty("TRUST","co.port"));
	}

	/**
	 * abstract method where to implement the gap between the monitoring info
	 * and the service Manifest
	 * 
	 * @param ServiceId
	 * @throws IOException
	 */
	public abstract double calculateGap(String ServiceId);

	public abstract double aggregate(ArrayList<Double> alist);

	protected double getCovarianceFactor(ArrayList<Double> alist) {
		double mean = Statistics.mean(alist);
		log.debug("Mean: " + mean);
		double variance = Statistics.variance(alist, mean);
		log.debug("Variance: " + variance);
		double estdev = Statistics.estdesv(variance);
		log.debug("Standar deviation: " + estdev);
		log.debug("Coefficient of variance: "
				+ Statistics.coefficientOfVariance(estdev, mean));
		double result = Statistics.coefficientOfVariance(estdev, mean);
		
		// In certain cases it could be higher than 1, so guarantee this won't happen
		if (result > 1.0)
		{
			result = 1.0;
		}
		return result;
	}

	protected ArrayList<VMInformation> loadServiceInfo(String service_ID) {
		log.info("**** Loading serviceInfo "+ service_ID);
		ArrayList<VMInformation> vminfoList = getMonitoringService(service_ID);
//		log.info("**** VMInfo List size "+ vminfoList.size());
//		GetIPManifestValues gipmv = new GetIPManifestValues();
//		String manifest = gipmv.getServiceManifest(service_ID);
//		log.info(manifest);
//		Manifest mani = gipmv.stringManifest2Manifest(manifest);
//		for (VMInformation vminfo : vminfoList) {
//			log.info("Virtual resource ID:"+vminfo.getVmId());
//			log.info("mani Id: "+mani.getManifestId());
//			HashMap<String, Integer> hm = getIPManifestInfoPerInstance(mani,vminfo.getVmId());
//			vminfo.setCpu_asked(hm.get("smcpuspeed"));
//			vminfo.setMemory_asked(hm.get("smmemorysize"));
//			vminfo.setNum_cpu_asked(hm.get("smnumcpu"));
//			vminfo.printContent();
//		}
		return vminfoList;
	}

	public ArrayList<VMInformation> getMonitoringService(String serviceId) {
		log.info("Getting monitoring information for the service");
		// System.out
		// .println("============= getMonitoringService() =============");
//		MonitoringClient mc = new MonitoringClient();
//		List<MonitoringResourceDataset> mrdList = mc
//				.getLatestMonitoringServiceInfo(serviceId);
		COClient coc = new COClient(coHost,coPort);
		ArrayList<VMInformation> monitorList = new ArrayList<VMInformation>();
		List<String> vmIds = coc.getVMsofService(serviceId);
		log.info(vmIds.size());
		for (String vmId : vmIds){
			log.info("VMId: " + vmId);
			String manifestVmId = coc.getVMName(vmId);
			monitorList.add(getVirtualMetrics(manifestVmId, vmId, serviceId));
		}
//		for (MonitoringResourceDataset mrd : mrdList) {
//			// System.out.println(mrd.getMonitoring_information_collector_id());
//			// System.out.println(mrd.getVirtual_resource_id());
//			monitorList.add(getVirtualMetrics(mrd.getVirtual_resource_id(),
//					mrd.getMonitoring_information_collector_id(), serviceId));
//		}
		return monitorList;
	}

	private VMInformation getVirtualMetrics(String virtualResourceId,
			String monitoringInformationColectorId, String ServiceId) {
		log.info("serviceId: "+ServiceId+", virtualResourceId: "+virtualResourceId+", Monitoring information collector: "+monitoringInformationColectorId);
		MonitoringClient mc = new MonitoringClient();
		VMInformation vminfo = new VMInformation(ServiceId,virtualResourceId,
				monitoringInformationColectorId);
		List<MonitoringResourceDataset> mrdList = mc
				.getLatestMonitoringVirtualInfo(monitoringInformationColectorId);
		double memTotal = 0.0;
		for (MonitoringResourceDataset mrd : mrdList) {
			// System.out.println(mrd.getMetric_name() + " => \t"
			// + mrd.getMetric_value() + " " + mrd.getMetric_unit());
			if (mrd.getMetric_name().equalsIgnoreCase("cpu_speed")) {
				vminfo.setCpu_allocated(Double.valueOf(mrd.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("cpu_user")) {
				vminfo.setCpu_usage(Double.valueOf(mrd.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("cpu_vnum")) {
				vminfo.setNum_cpu_allocated(Double.valueOf(mrd
						.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("mem_total")) {
				memTotal = Double.valueOf(mrd.getMetric_value());
				vminfo.setMemory_allocated(Double.valueOf(mrd.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("mem_used")) {
//				vminfo.setMemory_used(Double.valueOf(mrd.getMetric_value()));
				//Set like this because libvirt does not provide the correct value
				vminfo.setMemory_used(Double.valueOf(memTotal * 0.8)); 
			}
		}
		
		log.debug("Monitoring data retrieved: \n CPU allocated:" + vminfo.getCpu_allocated());
		
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		GetIPManifestValues gipmv = new GetIPManifestValues();
		String manifest = gipmv.getServiceManifest(ServiceId);
//		log.info(manifest);
		Manifest mani = gipmv.stringManifest2Manifest(manifest);
//		log.info(mani.getManifestId());
		hm = getIPManifestInfoPerInstance(mani,vminfo.getVmId());
		vminfo.setCpu_asked(hm.get("smcpuspeed"));
		vminfo.setMemory_asked(hm.get("smmemorysize"));
		vminfo.setNum_cpu_asked(hm.get("smnumcpu"));
		//vminfo.printContent();
		return vminfo;
	}

	private HashMap<String, Integer> getIPManifestInfoPerInstance( Manifest mani,
			String instanceId) {
		log.info("InstanceId: "+instanceId);
		// Get IP Extensions
		VirtualHardwareSection vhs = mani
				.getInfrastructureProviderExtensions()
				.getVirtualSystem(instanceId).getVirtualHardwareSection();
		
		int vhscpuSpeed = vhs.getCPUSpeed();		
		int vhsMemorySize = vhs.getMemorySize();
		int vhsNumCPU = vhs.getNumberOfVirtualCPUs();
		
		log.info("smcpuspeed " +vhscpuSpeed);
		log.info("smmemorysize "+ vhsMemorySize);
		log.info("smnumcpu "+ vhsNumCPU);

		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("smcpuspeed", vhscpuSpeed);
		hm.put("smmemorysize", vhsMemorySize);
		hm.put("smnumcpu", vhsNumCPU);
		return hm;
	}

}
