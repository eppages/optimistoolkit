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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.ip.service.clients.COClient;
import eu.optimis.tf.ip.service.clients.MonitoringClient;
import eu.optimis.tf.ip.service.operators.ExponentialSmoothingAggregator;
import eu.optimis.tf.ip.service.trust.VMInformation;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;

public class TrustForecaster 
{
	private int scale = 1;
	Logger log = Logger.getLogger(this.getClass().getName());
	private String coHost;
	private int coPort;
	
	public TrustForecaster()
	{
		scale = Integer.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
		coHost = PropertiesUtils.getProperty("TRUST","co.host");
		coPort = Integer.valueOf(PropertiesUtils.getProperty("TRUST","co.port"));
	}

	public double forecastServiceTrust(String serviceId, int timeSpan)
	{		
		log.info("Provide trust forecast for the service "+serviceId);
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(serviceId, Double.NaN, ExponentialSmoothingAggregator.SERVTRUST, timeSpan);
		return forecast*scale;
	}
	
	public double forecastIPTrust(String providerId, int timeSpan)
	{		
		log.info("Provide trust forecast for the provider "+providerId);
		ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
		double forecast = forecaster.calculateTripleAggregation(providerId, Double.NaN, ExponentialSmoothingAggregator.IPTRUST, timeSpan);
		return forecast*scale;
	}
	
	public double forecastVMDeployment (String idService)
	{
		log.info("Provide trust forecast for the service "+idService+ " when a new VM is developed.");
		
		// Step 1 --> Determine load included by the service execution (max - min load)
		// Get current VMs
		COClient coc = new COClient(coHost,coPort);		
		List<String> vmIds = coc.getVMsofService(idService);
		log.debug("Number of current VMs for the service: " + vmIds.size());
		ArrayList<ArrayList<VMInformation>> infoMatrix = new ArrayList<ArrayList<VMInformation>>();		
		double multiplier = 1.0-(1.0/(((double)vmIds.size())+1.0));
		log.info("Multiplier for the current VMs: " + multiplier);
		
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
			for (MonitoringResourceDataset mrd : mrdList) 
			{			
				
				if (mrd.getMetric_name().equalsIgnoreCase("cpu_user")) 
				{
					// Retrieve % of CPU used
					double cpuUsed = Double.valueOf(mrd.getMetric_value());
					vmInfo.setCpu_usage(cpuUsed);					
					if (cpuUsed < minCPU) minCPU = cpuUsed;
				}
				else if (mrd.getMetric_name().equalsIgnoreCase("mem_used")) 
				{
					// Retrieve amount of memory used
					double memUsed = Double.valueOf(mrd.getMetric_value());
					if (memUsed<1) memUsed = 1024; 
					vmInfo.setMemory_used(memUsed);					
					if (memUsed < minMem) minMem = memUsed;
				}				
				infoList.add(vmInfo);
			}
			
			// Now, calculate service load in each moment and reassign expected resources
			// Reduce the proper part, using the multiplier with the service load
			for (VMInformation currentVM : infoList) 
			{
				double currentMem = currentVM.getMemory_used();
				double currentCPU = currentVM.getCpu_usage();
				double newMem = currentMem - (currentMem - minMem)*multiplier;
				double newCPU = currentCPU - (currentCPU - minCPU)*multiplier;				
				memLoadList.add(new Double (newMem));
				cpuLoadList.add(new Double (newCPU));
				log.debug("Previous memory: " + currentMem + "  -> New memory: " + newMem);
				log.debug("Previous CPU: " + currentCPU + "  -> New CPU: " + newCPU);
			}
			
			// Obtain resource forecast according to the new VM load			
			// Forecast memory usage			
			String interval = PropertiesUtils.getProperty("TRUST","interval");
			int period = (60/(Integer.parseInt(interval)/60))*24;
			if (infoList.size() < 720) period = 24;
			
			memLoadList.trimToSize();
			Double[]memForecastList = new Double[memLoadList.size()];
			memLoadList.toArray(memForecastList);
			ExponentialSmoothingAggregator forecaster = new ExponentialSmoothingAggregator ();
			double forecast = forecaster.tripleExponentialSmoothing(0.4863, 0.0001, 0.0011, period, memForecastList, 2);
			log.info("Memory usage forecast for current VM: " + forecast);
						
			// Forecast CPU usage	
			cpuLoadList.trimToSize();
			Double[]cpuForecastList = new Double[cpuLoadList.size()];
			cpuLoadList.toArray(cpuForecastList);
			forecast = forecaster.tripleExponentialSmoothing(0.4863, 0.0001, 0.0011, period, cpuForecastList, 2);
			log.info("CPU usage forecast for current VM: " + forecast);
						
			infoMatrix.add(infoList);
		}
		
		// Split the corresponding load to the new VM and rest of them
		
		// Re-calculate trust with the new projection of resources usage (especially VMPerformance)
		
		return 2.5;
	}
	
	public static void main(String[] args) 
	{
		TrustForecaster forecaster = new TrustForecaster();
		//double result = forecaster.forecastServiceTrust("a4169454-a7bc-441c-b1b2-378ede095180", 6);
		double result = forecaster.forecastVMDeployment("GeneDetectionBroker");
		System.out.println ("Received value: " + result);

	}

}
