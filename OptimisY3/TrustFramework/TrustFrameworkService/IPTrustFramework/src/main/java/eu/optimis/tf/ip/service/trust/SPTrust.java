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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.jboss.logging.Logger;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.ip.service.clients.MonitoringClient;
import eu.optimis.tf.ip.service.operators.Opinion;
import eu.optimis.tf.ip.service.operators.OpinionModel;
import eu.optimis.tf.ip.service.utils.GetIPManifestValues;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

public class SPTrust {
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
	private Opinion op = new Opinion();
	private ArrayList<Opinion> cpuSpeedList;
	private ArrayList<Opinion> memorySizeList;
	private static HashMap list_cpu;
	private static HashMap list_memory;
	private static int index1, index2;
	private String serviceId;
	private GetIPManifestValues ipvalues;
	private int capacity;
	private Manifest mani;
	
	public SPTrust(){
		
	}
	
	public SPTrust(String serviceid, int cap){
		log.info("Calculating service reliability");
		
		GetIPManifestValues gipmv = new GetIPManifestValues();
		String manifest = gipmv.getServiceManifest(serviceId);
//		log.info(manifest);
		this.mani  = gipmv.stringManifest2Manifest(manifest);
		this.ipvalues = new GetIPManifestValues(serviceid);
		
		this.cpuSpeedList = new ArrayList<Opinion>();
		this.memorySizeList = new ArrayList<Opinion>();
		
		this.serviceId=serviceid;
		this.list_cpu = new HashMap(cap);
		this.list_memory = new HashMap(cap);
		this.index1 = 0;
		this.index2 = 0;
		this.capacity = cap;
		
		for(int i=0; i<cap; i++){
			this.list_cpu.put(String.valueOf(i), 0.0d);
			this.list_memory.put(String.valueOf(i), 0.0d);
		}
	}
	
	private void createOpinionList(){
		VirtualMachineComponent[] VMcomponents = mani.getVirtualMachineDescriptionSection()
				.getVirtualMachineComponentArray();//this.ipvalues.getNumberOfServiceComponents();
		
		int numberofcomponents = VMcomponents.length;
		log.debug("Number of Components : " + VMcomponents.length );
	
		ArrayList<VMInformation> VMInfo = getMonitoringService(this.serviceId);
		
		HashMap<String, Integer>  hmap = null;
		for(int i=0; i< numberofcomponents; i++){
			String vmInstanceID = VMcomponents[i].getOVFDefinition().getVirtualSystem().getId();
			hmap = getIPManifestInfoPerInstance(mani, vmInstanceID	);//this.ipvalues.getIPManifestInfoPerInstance(vmInstanceID);
			Integer cpuspeed = hmap.get("smcpuspeed");
			Integer memory = hmap.get("smmemorysize");
			
			for(int j=0; j < VMInfo.size(); j++){
				if(VMInfo.get(j).getVirtualResourceId().equalsIgnoreCase(vmInstanceID)){
					this.addMonitoringDataCPU(VMInfo.get(j).getCpu_usage());
					//this.displayMonitoringWindowCPU();
					Double slaParamCPU = Double.parseDouble(cpuspeed.toString());
					Opinion cpu_opinion = this.getSPSLAparameterOpinionForCPU(slaParamCPU);
					this.cpuSpeedList.add(cpu_opinion);
					
					this.addMonitoringDataMemory(VMInfo.get(j).getMemory_used());
					//this.displayMonitoringWindowMemory();
					Double slaParamMemory = Double.parseDouble(memory.toString());
					Opinion memory_opinion = this.getSPSLAparameterOpinionForMemory(slaParamMemory);
					this.memorySizeList.add(memory_opinion);
				}
			}
		}
	}
	
	public Opinion getSPReliabilityTrustOpinion(){
		
		this.createOpinionList();
		
		OpinionModel om = new OpinionModel();
		
		Opinion cpu_consensus_opinion = this.cpuSpeedList.get(0);
		for(int i=1; i< this.cpuSpeedList.size(); i++){
			cpu_consensus_opinion = om.concensus(cpu_consensus_opinion, this.cpuSpeedList.get(i));
		}
		
		Opinion memory_consensus_opinion = this.memorySizeList.get(0);
		for(int j=1; j<this.memorySizeList.size(); j++){
			memory_consensus_opinion = om.concensus(memory_consensus_opinion, this.memorySizeList.get(j));
		}
		
		Opinion cpu_mem_conjunct_opinion = om.conjuntion(cpu_consensus_opinion, memory_consensus_opinion);
		log.info("reliability opinion: "+cpu_mem_conjunct_opinion.getExpectation());
		return cpu_mem_conjunct_opinion;
		
	}
	
	private void addMonitoringDataCPU(Double value){
		
		if(SPTrust.index1 % this.capacity == 0)
			SPTrust.index1 = 0;
		String key = String.valueOf(SPTrust.index1);
		list_cpu.put(key, value);
		
		SPTrust.index1 = SPTrust.index1 + 1;
	}

	private void addMonitoringDataMemory(Double value){
		
		if(SPTrust.index2 % this.capacity == 0)
			SPTrust.index2 = 0;
		String key = String.valueOf(SPTrust.index2);
		list_memory.put(key, value);
		
		SPTrust.index2 = SPTrust.index2 + 1;
	}

	
	
	private void displayMonitoringWindowCPU(){

		Set set = list_cpu.entrySet(); 
		Iterator it = set.iterator(); 
		while(it.hasNext()) { 
			Map.Entry me = (Map.Entry)it.next(); 
			log.debug(me.getKey() + ": "); 
			log.debug(me.getValue());
		}
	}
	
	private void displayMonitoringWindowMemory(){

		Set set = list_memory.entrySet(); 
		Iterator it = set.iterator(); 
		while(it.hasNext()) { 
			Map.Entry me = (Map.Entry)it.next(); 
			log.debug(me.getKey() + ": "); 
			log.debug(me.getValue());
		}
	}

	
	

	private Opinion getSPSLAparameterOpinionForCPU(double param){

		LinkedList <Double> powerlist = new LinkedList();

		double total = 0.0d;
		Set set = list_cpu.entrySet(); 
		Iterator it = set.iterator(); 
		while(it.hasNext()) { 
			Map.Entry me = (Map.Entry)it.next(); 
			//System.out.print(me.getKey() + ": "); 
			//System.out.println(me.getValue());
			total = total + Double.parseDouble(me.getValue().toString());
			if(total<=0)
				total=1.00;
		}

		for(int i=1; i<=100; i++){
			double percent = param * i/100;
			double sum = 0.0d;
			Set set1 = list_cpu.entrySet(); 
			Iterator it1 = set1.iterator(); 
			while(it1.hasNext()) { 
				Map.Entry me1 = (Map.Entry)it1.next(); 
				//System.out.print(me1.getKey() + ": "); 
				//System.out.println(me1.getValue());
				if(percent >= Double.parseDouble(me1.getValue().toString()))
			    	sum = sum + Double.parseDouble(me1.getValue().toString());
			}	
				
			
			double power = sum / total; 
			powerlist.add(i-1,power);
		}

		System.out.println("Total :" + total);
		//for(int j=0; j< powerlist.size(); j++){
			//if(j%10==0)
		//	System.out.println(powerlist.get(j));
		//}

		double smallestpower=0.0d, highestpower=1.0d;
		int smallestfilter=0, highestfilter=0;
		for(int j=0; j< powerlist.size(); j++){
			if(powerlist.get(j)<= smallestpower)
				smallestfilter = j+1;
			if(powerlist.get(j) < highestpower)
				highestfilter = j+1;
		}

		//System.out.println("SmallestFilter :" + smallestfilter +
		//		"  HighestFilter :" + highestfilter);
		
		double totalevidence=5.0d;
		double belief=0.0d, disbelief=0.0d, uncertainty=0.0d;
		belief = (100-(highestfilter+1))/100.0d;
		disbelief = (smallestfilter+1)/100.0d;
		uncertainty = (highestfilter - smallestfilter)/(100.0d);
		
		//System.out.println("Beleif :" +belief+ " Disbelief :"+disbelief+" Uncertainty :"+uncertainty);
		
		op.setBelief(belief);
		op.setDisBelief(disbelief);
		op.setUnCertainty(uncertainty);
		op.setRelativeAtomicity(0.5d);
		op.setExpectation();
		
		return op;
	}

	private Opinion getSPSLAparameterOpinionForMemory(double param){

		LinkedList <Double> powerlist = new LinkedList();

		double total = 0.0d;
		Set set = list_memory.entrySet(); 
		Iterator it = set.iterator(); 
		while(it.hasNext()) { 
			Map.Entry me = (Map.Entry)it.next(); 
			//System.out.print(me.getKey() + ": "); 
			//System.out.println(me.getValue());
			total = total + Double.parseDouble(me.getValue().toString());
			if(total<=0)
				total=1.00;
		}

		for(int i=1; i<=100; i++){
			double percent = param * i/100;
			double sum = 0.0d;
			Set set1 = list_memory.entrySet(); 
			Iterator it1 = set1.iterator(); 
			while(it1.hasNext()) { 
				Map.Entry me1 = (Map.Entry)it1.next(); 
				//System.out.print(me1.getKey() + ": "); 
				//System.out.println(me1.getValue());
				if(percent >= Double.parseDouble(me1.getValue().toString()))
			    	sum = sum + Double.parseDouble(me1.getValue().toString());
			}	
				
			
			double power = sum / total; 
			powerlist.add(i-1,power);
		}

		System.out.println("Total :" + total);
		//for(int j=0; j< powerlist.size(); j++){
			//if(j%10==0)
		//	System.out.println(powerlist.get(j));
		//}

		double smallestpower=0.0d, highestpower=1.0d;
		int smallestfilter=0, highestfilter=0;
		for(int j=0; j< powerlist.size(); j++){
			if(powerlist.get(j)<= smallestpower)
				smallestfilter = j+1;
			if(powerlist.get(j) < highestpower)
				highestfilter = j+1;
		}

		//System.out.println("SmallestFilter :" + smallestfilter +
		//		"  HighestFilter :" + highestfilter);
		
		double totalevidence=5.0d;
		double belief=0.0d, disbelief=0.0d, uncertainty=0.0d;
		belief = (100-(highestfilter+1))/100.0d;
		disbelief = (smallestfilter+1)/100.0d;
		uncertainty = (highestfilter - smallestfilter)/(100.0d);
		
		//System.out.println("Beleif :" +belief+ " Disbelief :"+disbelief+" Uncertainty :"+uncertainty);
		
		op.setBelief(belief);
		op.setDisBelief(disbelief);
		op.setUnCertainty(uncertainty);
		op.setExpectation();
		
		return op;
	}

	
	

	
	private ArrayList<VMInformation> getMonitoringService(String serviceId) {
		log.debug("============= getMonitoringService() =============");
		MonitoringClient mc = new MonitoringClient();
		
		List<MonitoringResourceDataset> mrdList = mc.getLatestMonitoringServiceInfo(serviceId);

		ArrayList<VMInformation> monitorList = new ArrayList<VMInformation>();
		for (MonitoringResourceDataset mrd : mrdList) {
			// System.out.println(mrd.getMonitoring_information_collector_id());
			// System.out.println(mrd.getVirtual_resource_id());
			monitorList.add(getVirtualMetrics(mrd.getVirtual_resource_id(),
					mrd.getMonitoring_information_collector_id(), serviceId));
		}
	
		
		return monitorList;
	}
	
	private VMInformation getVirtualMetrics(String virtualResourceId,
			String monitoringInformationColectorId, String ServiceId) {
		HashMap<String, Double> hm = new HashMap<String, Double>();
		MonitoringClient mc = new MonitoringClient();
		VMInformation vminfo = new VMInformation(virtualResourceId,
				monitoringInformationColectorId, ServiceId);
	
		List<MonitoringResourceDataset> mrdList = mc.getLatestMonitoringVirtualInfo(virtualResourceId);
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
				vminfo.setMemory_allocated(Double.valueOf(mrd.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("mem_used")) {
				vminfo.setMemory_used(Double.valueOf(mrd.getMetric_value()));
			} else if (mrd.getMetric_name().equalsIgnoreCase("disk_total")) {
				vminfo.setDisk_allocated(Double.valueOf(mrd.getMetric_value()));
			}
		}
		
		/// Set vminfo with values from the Manifest
		
		 vminfo.printContent();
		return vminfo;
	}
	
	private double getManfestValue(String manifest, String param){
		double param_value=0d;
		
		XmlBeanServiceManifestDocument parsedManifest = null;
		try {
			parsedManifest = XmlBeanServiceManifestDocument.Factory.parse(manifest);
		} catch (XmlException e) {
			e.printStackTrace();
		}
	
		Manifest spManifest= Manifest.Factory.newInstance(parsedManifest);
		log.debug("SergiceID :" + spManifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM serviceID : " + spManifest.getVirtualMachineDescriptionSection().getServiceId());
		//System.out.println("Manifest VM Affinity Rule : " + spManifest.getVirtualMachineDescriptionSection().getAffinityRule(0).toString());
		//System.out.println("Manifest : " + manifest);
		
		String serviceID = spManifest.getVirtualMachineDescriptionSection().getServiceId();
		
		if(param.equalsIgnoreCase("cpu_speed")){
			
		}
		else if(param.equalsIgnoreCase("mem_total")){
			
		}
	
		
		return param_value;
	}
	
	
	public void updateList(){
		
		
		
		HashMap hm = new HashMap(5);
		hm.put("1", new Double(1.34));
		hm.put("2", new Double(2.34));
		hm.put("3", new Double(3.34));
		hm.put("4", new Double(4.34));
		hm.put("5", new Double(5.34));
		

		Set set = hm.entrySet(); 
		
		// Get an iterator 
		Iterator i = set.iterator(); 
		// Display elements 
		while(i.hasNext()) { 
			Map.Entry me = (Map.Entry)i.next(); 
			log.debug(me.getKey() + ": "); 
			log.debug(me.getValue()); 
		}
		

		hm.put("5", new Double(6.66));
		set = hm.entrySet();
		i = set.iterator(); 

		log.debug("After change:");
		
		while(i.hasNext()) { 
			Map.Entry me = (Map.Entry)i.next(); 
			log.debug(me.getKey() + ": "); 
			log.debug(me.getValue()); 
		}
		
		//return mp;
	}
	
	private HashMap<String, Integer> getIPManifestInfoPerInstance( Manifest mani,
			String instanceId) {
		log.info("instanceId: "+instanceId);
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
	
	public double getNMReliability(){
		Random generator = new Random();
		int positive =  generator.nextInt(1000);
		if (positive < 300){
			positive += 300;
		}
		Opinion op = new Opinion(positive, 1000 - positive);
		return op.getExpectation();
	}
	
}
