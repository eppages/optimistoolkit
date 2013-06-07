/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package eu.optimis.workloadanalyzer;

import java.util.Collection;
import java.util.Iterator;

import eu.optimis.schemas.workload.CpuCoresType;
import eu.optimis.schemas.workload.CurrentType;
import eu.optimis.schemas.workload.MemoryType;
import eu.optimis.schemas.workload.MetricType;
import eu.optimis.schemas.workload.NetworkBandwidthType;
import eu.optimis.schemas.workload.PhysicalHostType;
import eu.optimis.schemas.workload.ResourceInformationType;
import eu.optimis.schemas.workload.StorageType;
import eu.optimis.schemas.workload.WorkloadAnalysisDocument;
import eu.optimis.schemas.workload.WorkloadAnalysisType;
import eu.optimis.workloadanalyzer.client.WorkloadanalyzerClient;
import eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException;
import eu.optimis.workloadanalyzer.utils.WorkloadPropertiesUtil;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * @author hrasheed
 * 
 */
public class WorkloadAnalyzerServiceIT extends TestCase {

    private static final Logger log = Logger.getLogger(WorkloadAnalyzerServiceIT.class);
    
    private String host = "localhost";
	private int port = 9000;
	private String path = "WorkloadAnalyzer/WorkloadAnalyzerService";
	
	private WorkloadanalyzerClient client = null;

    @Override
    protected void setUp() {
        if(log.isDebugEnabled()) {
            log.debug("================================================================================");
            log.debug("Entering unit test: " + this.getName());
            log.debug("--------------------------------------------------------------------------------");
        }
        
        initializeWorkloadClient();
    }

    @Override
    protected void tearDown() {
        if(log.isDebugEnabled()) {
            log.debug("--------------------------------------------------------------------------------");
            log.debug("Leaving unit test: " + this.getName());
            log.debug("================================================================================");
        }
    }
    
    private void initializeWorkloadClient() {
    	
    	try {
    		
    		WorkloadPropertiesUtil props = new WorkloadPropertiesUtil();
    		
    		this.host = props.getWorkloadAnalyzerHost();
    		this.port = Integer.parseInt((props.getWorkloadAnalyzerPort()).trim());
            this.path = props.getWorkloadAnalyzerURLPath();
            
            if(log.isDebugEnabled()) {
            	log.debug("workload-analyzer-host: " + this.host);
            	log.debug("workload-analyzer-port: " + this.port);
            	log.debug("workload-analyzer-path: " + this.path);
            }
            
            client = new WorkloadanalyzerClient(this.host,this.port);
             
    	} catch (Exception e) {
			log.error("error in creating workload analyzer client: Reason - " + e);
		}
    }

    public void testPerformWorkloadAnalysis() throws Exception {
        
        try {
        	
        	WorkloadAnalysisDocument workloadAnalysisDoc = client.getWorkloadAnalysis("dummy_service_maifest");
        	
        	if(log.isTraceEnabled())
        		log.trace("workload-analysis: " + workloadAnalysisDoc.xmlText());
        	
        	WorkloadAnalysisType workloadAnalysisType = workloadAnalysisDoc.getWorkloadAnalysis();
        	PhysicalHostType[] physicalHostTypes = workloadAnalysisType.getPhysicalHostArray();
        	
        	for (int i = 0; i < physicalHostTypes.length; i++) {
        		PhysicalHostType physicalHostType = physicalHostTypes[i];
        		log.info("Host ID: " + physicalHostType.getId());
        		CurrentType currentType = physicalHostType.getLoad().getCurrent();
        		ResourceInformationType resourceInfo = currentType.getResourceInformation();
        		
        		// check for NULL for every metric
        		
        		CpuCoresType cores = resourceInfo.getCpuCores();
        		if(cores != null) {
        			cores.getName();
        			cores.getMetricUnit();
        			// actual number of Cores on a Physical Host
        			cores.getActualValue();
        			// currently available Cores on a Physical Host 
        			// this excludes the reserved Cores of virtual machines hosting on this physical host
        			cores.getStringValue();
        		}
        		
        		MemoryType mainMemory = resourceInfo.getMemory();
        		if(mainMemory != null) {
        			mainMemory .getName();
        			mainMemory .getMetricUnit();
        			// actual value is the total memory (KB) of the resource
        			mainMemory .getActualValue();
        			// this is the currently available memory of the reosurce
        			mainMemory .getStringValue();
        		}
        		
        		// only free disk space (KB) info, actual disk size is not returned by monitoring module at the moment
        		StorageType storage = resourceInfo.getStorage();
        		if(storage != null) {
        			storage.getName();
        			storage.getMetricUnit();
        			storage.getStringValue();
        		}
        		
        		NetworkBandwidthType bandwidth = resourceInfo.getNetworkBandwidth();
        		if(bandwidth != null) {
        			bandwidth.getName();
        			bandwidth.getMetricUnit();
        			// downstream bandwidth currently available in Kbps
        			bandwidth.getStringValue();
        		}
        		
        		// generic array of metrics to include new monitoring metrics into XML Schema 
        		// at the moment, there is only ONE metric 
        		MetricType[] metrics = resourceInfo.getMetricArray();
        		for (int j = 0; j < metrics.length; j++) {
        			MetricType processorLoadMetric = metrics[j]; 
        			if(processorLoadMetric != null) {
        				// this is processor load metric with name "processor_load"
        				processorLoadMetric.getMetricName();
            			// this value gives the percentage value on the current load of processors
        				processorLoadMetric.getStringValue();
            		}
				}
        		
			}
        	
        } catch (WorkloadAnalyzerException e) {
        	log.error(e);
        	fail("testPerformWorkloadAnalysis failed: " + e.getMessage());
		} 
        catch (Exception e) {
        	log.error(e);
        	fail("testPerformWorkloadAnalysis failed: " + e.getMessage());
		}
    }

    public void testAddServiceManifest() throws Exception {
    	
        try {
        	
        	boolean result = client.addServiceManifest("Service_Maifest_Dummy");
        	log.info("adding service manifest: " + result);
        	
        } catch (WorkloadAnalyzerException e) {
        	log.error(e);
        	fail("testAddServiceManifest failed: " + e.getMessage());
		} 
        catch (Exception e) {
        	log.error(e);
        	fail("testAddServiceManifest failed: " + e.getMessage());
		}
    }

    public void testRemoveServiceManifest() throws Exception {
    	
        try {
        	
        	boolean result = client.removeServiceManifest("Service_Maifest_Dummy");
        	log.info("removing service manifest: " + result);
        	
        } catch (WorkloadAnalyzerException e) {
        	log.error(e);
        	fail("testRemoveServiceManifest failed: " + e.getMessage());
		} 
        catch (Exception e) {
        	log.error(e);
        	fail("testRemoveServiceManifest failed: " + e.getMessage());
		}
    }

    public void testListServiceManifests() throws Exception {
    	
        try {
        	
        	Collection<String> slaIDs = client.listServiceManifests();
        	
        	for (Iterator<String> iterator = slaIDs.iterator(); iterator.hasNext();) {
				String slaID = (String) iterator.next();
				log.info("service manifest ID: " + slaID);
			}
        	
        } catch (WorkloadAnalyzerException e) {
        	log.error(e);
        	fail("testListServiceManifests failed: " + e.getMessage());
		} 
        catch (Exception e) {
        	log.error(e);
        	fail("testListServiceManifests failed: " + e.getMessage());
		}
    }
}
