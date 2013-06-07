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
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service.hm.trust;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.ip.service.clients.COClient;
import eu.optimis.tf.ip.service.clients.MonitoringClient;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.operators.Statistics;
import eu.optimis.tf.ip.service.trust.VMInformation;
import eu.optimis.tf.ip.service.utils.GetIPManifestValues;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

public class ForecastVMGap {

	private boolean production = false;
	Logger log = Logger.getLogger(this.getClass().getName());
	private String coHost;
	private int coPort;
	private boolean addVM = false;
	
	public ForecastVMGap(boolean addVMFactor) 
	{
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST","production"));
		coHost = PropertiesUtils.getProperty("TRUST","co.host");
		coPort = Integer.valueOf(PropertiesUtils.getProperty("TRUST","co.port"));
		addVM = addVMFactor;
	}
		
	public double calculateVMGapAddVM (String idService)
	{
		// Step 1 --> Get service load and adapt all the CPU/mem usage
		ArrayList<VMInformation> instancesList = getVMInstancesLoads (idService);
		// If only 1 VM is running and we cancel, gap value is the worst possible
		if (instancesList == null) return 0.0;
				
		// Step 2 --> Construct array for the covariance and calculate
		ArrayList<Double> cpuList = new ArrayList<Double>();
		ArrayList<Double> memList = new ArrayList<Double>();
		ArrayList<Double> gapFactorList = new ArrayList<Double>();
		for (VMInformation vminfo : instancesList) 
		{			
			// Covariance factor for CPU
			cpuList.add(vminfo.getCpu_allocated());
			cpuList.add(vminfo.getCpu_asked());
			cpuList.add(vminfo.getCpu_allocated() * vminfo.getCpu_usage());
			double cpuCovarianceFactor = getCovarianceFactor(cpuList);			
			gapFactorList.add(cpuCovarianceFactor);
			
			// Covariance factor for memory
			memList.add(vminfo.getMemory_asked());
			memList.add(vminfo.getMemory_allocated() * (vminfo.getMemory_used()/100));
			memList.add(vminfo.getMemory_allocated());
			double memoryCovarianceFactor = getCovarianceFactor(memList);
			gapFactorList.add(memoryCovarianceFactor);
		}
		
		// Step 3 --> Calculate aggregation
		double mean = Statistics.mean(gapFactorList);
		log.info("Gap Mean: " + mean);
		
		return mean;
	}
	
	private double getCovarianceFactor(ArrayList<Double> alist) {
		double mean = Statistics.mean(alist);
		log.debug("Mean: " + mean);
		double variance = Statistics.variance(alist, mean);
		log.debug("Variance: " + variance);
		double estdev = Statistics.estdesv(variance);
		log.debug("Standar deviation: " + estdev);
		log.debug("Coefficient of variance: "
				+ Statistics.coefficientOfVariance(estdev, mean));
		return Statistics.coefficientOfVariance(estdev, mean);
	}		
	
	private ArrayList<VMInformation> getVMInstancesLoads (String idService)
	{
		// Get current VMs and determine multipliers
		COClient coc = new COClient(coHost,coPort);		
		List<String> vmIds = coc.getVMsofService(idService);
		log.debug("Number of current VMs for the service: " + vmIds.size());
		ArrayList<VMInformation> infoArray = new ArrayList<VMInformation>();		
		double multiplier = 1.0-(1.0/(((double)vmIds.size())+1.0));
		double multiplier2 = 1.0-(1.0/(((double)vmIds.size())-1.0));
		log.info("Multipliers for the current VMs: AddVM -> " + multiplier + "; RemoveVM -> " + multiplier2);
		
		// If only 1 VM is running and we cancel, gap value is the worst possible
		if (vmIds.size()<=1 && !addVM)
		{
			log.info ("Only 1 instance running -> If removed, performance trust = 0!");
			return null;
		}
		
		// Iterate through all the VMs
		for (String vmId : vmIds)
		{			
			// Obtain monitored values
			String vmName = coc.getVMName(vmId);
			log.debug("VMId: " + vmId);
			log.debug("VMName: " + vmName);			
			
			ArrayList<VMInformation> infoList = new ArrayList<VMInformation>();
			ArrayList<Double> cpuLoadList = new ArrayList<Double>();
			ArrayList<Double> memLoadList = new ArrayList<Double>();
			MonitoringClient mc = new MonitoringClient();
			VMInformation vmInfo = new VMInformation(idService, vmName, vmId);
			List<MonitoringResourceDataset> mrdList = null;
			
			// Prepare dates for retrieving the info
			try
			{
				Calendar cal = new GregorianCalendar();
				cal.setTime(cal.getTime());
				SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = dfm.parse(dfm.format(cal.getTime()));
				//cal.add(Calendar.HOUR, -7200);
				cal.add(Calendar.HOUR, -504); // 3 weeks before
				Date from = dfm.parse(dfm.format(cal.getTime()));
				log.info("Retrieving data for " + vmId + " from " + dfm.format(from) + " to " + dfm.format(now));
				mrdList = mc.getMonitoringVirtualInfo(vmId, from, now);
			}
			catch (Exception ex)
			{
				log.error ("Error when retrieving monitoring info!!");
				log.error(ex.getMessage());
			}
			
			// Get min value for CPU and memory	
			double minCPU = 0.0;			
			double minMem = 0.0;
			Date usedTimestamp = null;
			for (MonitoringResourceDataset mrd : mrdList) 
			{			
				Date currentTimestamp = mrd.getMetric_timestamp();
				if (usedTimestamp == null) usedTimestamp = currentTimestamp;
				
				// If metrics correspond to different timestamps, go to next VMInfo object
				if (usedTimestamp.compareTo(currentTimestamp)!=0)
				{
					infoList.add(vmInfo);
					vmInfo = new VMInformation(idService, vmName, vmId);
					usedTimestamp = currentTimestamp;
				}
				
				String metricName = mrd.getMetric_name();
				if (metricName.equalsIgnoreCase("cpu_speed")) 
				{
					vmInfo.setCpu_allocated(Double.valueOf(mrd.getMetric_value()));
				}			
				else if (metricName.equalsIgnoreCase("mem_total")) 
				{					
					vmInfo.setMemory_allocated(Double.valueOf(mrd.getMetric_value()));
				}
				else if (metricName.equalsIgnoreCase("cpu_user")) 
				{
					// Retrieve % of CPU used
					double cpuUsed = Double.valueOf(mrd.getMetric_value());
					vmInfo.setCpu_usage(cpuUsed);					
					if (cpuUsed < minCPU) minCPU = cpuUsed;
				}
				else if (metricName.equalsIgnoreCase("mem_used")) 
				{
					// Retrieve amount of memory used
					double memUsed = Double.valueOf(mrd.getMetric_value());
					if (memUsed<1) memUsed = 1024; 
					vmInfo.setMemory_used(memUsed);					
					if (memUsed < minMem) minMem = memUsed;
				}				
				
			}
			
			// Now, calculate service load in each moment and reassign expected resources
			// Reduce the proper part, using the multiplier with the service load
			for (VMInformation currentVM : infoList) 
			{
				double currentMem = currentVM.getMemory_used();
				double currentCPU = currentVM.getCpu_usage();
				double newMem = 0.0;
				double newCPU = 0.0;
				if (addVM)
				{
					// New memory and CPU when a VM is added
					newMem = currentMem - (currentMem - minMem)*multiplier;
					newCPU = currentCPU - (currentCPU - minCPU)*multiplier;
				}
				else
				{
					// New memory and CPU when a VM is removed
					newMem = currentMem + (currentMem - minMem)*multiplier2;
					newCPU = currentCPU + (currentCPU - minCPU)*multiplier2;
				}							
				memLoadList.add(new Double (newMem));
				cpuLoadList.add(new Double (newCPU));
				log.debug("Previous memory: " + currentMem + "  -> New memory: " + newMem);
				log.debug("Previous CPU: " + currentCPU + "  -> New CPU: " + newCPU);
			}
			
			// Obtain resource forecast according to the new VM load
			VMInformation resulVM = new VMInformation (idService, vmName, vmId);
			// Forecast memory usage			
			String interval = PropertiesUtils.getProperty("TRUST","interval");
			int period = (60/(Integer.parseInt(interval)/60))*24;
			if (infoList.size() < 720) period = 24;
			
			memLoadList.trimToSize();
			Double[]memForecastList = new Double[memLoadList.size()];
			memLoadList.toArray(memForecastList);
			ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
			double forecast = 0.0;
			if (infoList.size()<24)
			{
				// Not enough data --> Simple exponential smoothing
				forecast = forecaster.simpleExponentialSmoothing(0.5, memForecastList);
			}
			else
			{
				forecast = forecaster.tripleExponentialSmoothing(0.4863, 0.0001, 0.0011, period, memForecastList, 2);
			}
			resulVM.setMemory_used(forecast);
			log.info("Memory usage forecast for current VM: " + forecast);
						
			// Forecast CPU usage	
			cpuLoadList.trimToSize();
			Double[]cpuForecastList = new Double[cpuLoadList.size()];
			cpuLoadList.toArray(cpuForecastList);
			if (infoList.size()<24)
			{
				// Not enough data --> Simple exponential smoothing
				forecast = forecaster.simpleExponentialSmoothing(0.5, cpuForecastList);
			}
			else
			{
				forecast = forecaster.tripleExponentialSmoothing(0.4863, 0.0001, 0.0011, period, cpuForecastList, 2);
			}
			resulVM.setCpu_usage(forecast);
			log.info("CPU usage forecast for current VM: " + forecast);
			
			// Complete info about allocated resources
			resulVM.setCpu_allocated(infoList.get(infoList.size()-1).getCpu_allocated());
			resulVM.setMemory_allocated(infoList.get(infoList.size()-1).getMemory_allocated());
			
			// Retrieve asked resources from the IP manifest			
			HashMap<String, Integer> hm = getIPManifestInfoPerInstance(idService,vmName);
			resulVM.setCpu_asked(hm.get("smcpuspeed"));
			resulVM.setMemory_asked(hm.get("smmemorysize"));
			resulVM.setNum_cpu_asked(hm.get("smnumcpu"));
			
			//  Add the VM data to the result array
			infoArray.add(resulVM);
		}
		
		return infoArray;
	}
	
	private HashMap<String, Integer> getIPManifestInfoPerInstance(String idService, String instanceId) 
	{
		log.info("Manifest data for InstanceId: "+instanceId);
		
		// Get Manifest
		GetIPManifestValues gipmv = new GetIPManifestValues();
		String manifest = gipmv.getServiceManifest(idService);
		Manifest mani = gipmv.stringManifest2Manifest(manifest);
		
		// Get IP Extensions
		VirtualHardwareSection vhs = mani.getInfrastructureProviderExtensions().getVirtualSystem(instanceId).getVirtualHardwareSection();
		
		int vhscpuSpeed = vhs.getCPUSpeed();		
		int vhsMemorySize = vhs.getMemorySize();
		int vhsNumCPU = vhs.getNumberOfVirtualCPUs();
		
		log.debug("Requested CPU Speed: " +vhscpuSpeed);
		log.debug("Requested Memory Size: "+ vhsMemorySize);
		log.debug("Requested Num CPU: "+ vhsNumCPU);

		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("smcpuspeed", vhscpuSpeed);
		hm.put("smmemorysize", vhsMemorySize);
		hm.put("smnumcpu", vhsNumCPU);
		return hm;
	}
	
	public static void main(String[] args) 
	{
		ForecastVMGap forecaster = new ForecastVMGap(true);
		//double result = forecaster.forecastServiceTrust("a4169454-a7bc-441c-b1b2-378ede095180", 6);
		double result = forecaster.calculateVMGapAddVM("trec-realtime-service");
		System.out.println ("Received value: " + result);

	}
}
