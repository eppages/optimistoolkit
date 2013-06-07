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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.schemas.workload.WorkloadAnalysisDocument;
import eu.optimis.schemas.workload.WorkloadAnalysisType;
import eu.optimis.workloadanalyzer.clients.CloudOptimizerClient;
import eu.optimis.workloadanalyzer.clients.MonitoringClient;
import eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException;
import eu.optimis.workloadanalyzer.utils.PropertyMissingException;
import eu.optimis.workloadanalyzer.utils.WorkloadPropertiesUtil;

/**
 * @author hrasheed
 * 
 */
public class WorkloadManager {
	
	private static final Logger LOG = Logger.getLogger(WorkloadManager.class);

	public WorkloadManager() {	
	}
	/** Description of getWorkloadAnalysis(String serviceManifest)
	 *
	 * @param serviceManifest			not used at the moment
	 * @return			                WorkloadAnalysisDocument
     *
     * @throws eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException is not used at the moment
	 */
    public WorkloadAnalysisDocument getWorkloadAnalysis(String serviceManifest) throws WorkloadAnalyzerException {

        //
        // reading property file
        //
        String mmHost = "localhost";
        int mmPort = 8080;
        String mmPath = "";
        
        String coHost = "localhost";
        int coPort = 8080;
        
        try {
     
            WorkloadPropertiesUtil props = new WorkloadPropertiesUtil();
            
            mmHost = props.getMonitoringHost();
            mmPort = Integer.parseInt((props.getMonitoringPort()).trim());
            mmPath = props.getMonitoringURLPath();
            
            coHost = props.getCloudOptimizerHost();
            coPort = Integer.parseInt((props.getCloudOptimizerPort()).trim());
            
        } catch (PropertyMissingException e) {
            LOG.error("Property is missing: ", e);
            throw new WorkloadAnalyzerException("Property is missing:", e);
        } catch (IOException e) {
            LOG.error("Property file is missing: ",e);
            throw new WorkloadAnalyzerException("Property file is missing:", e);
        }
        
        //
        // creating cloud optimizer client
        //
        CloudOptimizerClient cloudOptimizerClient = null;
        try {   
            cloudOptimizerClient = new CloudOptimizerClient(coHost, coPort);
            LOG.info( "cloud optimizer client is created [" + coHost + ":" + coPort + "]");
        } catch (Exception e) {
            LOG.error( "error in creating cloud optimizer client", e);
            throw new WorkloadAnalyzerException("error in creating cloud optimizer client", e);
        }
        
        //
        // creating monitoring client
        //
        MonitoringClient monitoringClient = null;
        try {
            monitoringClient = new MonitoringClient(mmHost, mmPort, mmPath);
            LOG.info( "monitoring client is created [" + mmHost + ":" + mmPort + "]" );
        } catch (Exception e) {
            LOG.error( "error in creating monitoring client" );
            throw new WorkloadAnalyzerException("error in creating monitoring client", e);
        }
        
        //
        // retrieving physical host IDs
        //
        List<String> physicalHostIDs = null;
        try {
            LOG.info("retrieving physical host IDs from cloud optimizer");
            physicalHostIDs = cloudOptimizerClient.getPhysicalHosts();
            LOG.info("number of physical hosts: " + physicalHostIDs.size());
        } catch (Exception e) {
            LOG.error( "Error calling Cloud Optimizer: unable to retrieve physical host IDs", e);
            throw new WorkloadAnalyzerException("Error calling Cloud Optimizer: unable to retrieve physical host IDs", e);
        }
        
        List<PhysicalHostData> physicalHosts = new ArrayList<PhysicalHostData>();
        
        //
        // retrieving virtual IDs and monitoring reports for each physical host
        //
        for(String physicalHostID : physicalHostIDs) {
            
            PhysicalHostData physicalHost = new PhysicalHostData(physicalHostID);
            
            try {   
                LOG.info("retrieving virtual host IDs from the cloud optimizer for [" + physicalHostID + "]");
                List<String> virtualHostIDs = cloudOptimizerClient.getVirtualHosts( physicalHostID );
                LOG.info("number of virtual hosts: [" + virtualHostIDs.size() + "] for [" + physicalHostID + "]");
                physicalHost.setVirtualHostIDs( virtualHostIDs );
            } catch (Exception e) {
                LOG.error( "Error calling Cloud Optimizer: unable to get virtual host IDs for [" + physicalHostID + "]", e);
                continue;
            }
            
            try {   
                LOG.info("retrieving monitoring dataset for [" + physicalHostID + "]");
                MonitoringResourceDatasets mrdHostData = monitoringClient.getLatestReportForPhysical(physicalHostID);
                LOG.info("monitoring dataset size for [" + physicalHostID + "]: " + mrdHostData.getMonitoring_resource().size());
                physicalHost.setPhysicalHostData( mrdHostData.getMonitoring_resource() );
            } catch (Exception e) {
                LOG.error( "Error calling Monitoring: unable to fetch monitoring dataset for [" + physicalHostID + "]", e);
                continue;
            }
                
            try {
                if(physicalHost.getPhysicalHostData().size() > 0) {
                    LOG.debug("monitoring dataset (physical) processing for [" + physicalHostID + "] starting...");
                    physicalHost.processMetricDatasets();
                    LOG.debug("monitoring dataset (physical) processing for [" + physicalHostID + "] done...");
                    physicalHost.toCPULoadPrint();
                }
            } catch (Exception e) {
                LOG.error( "Error processing (physical) monitoring datasets for [" + physicalHostID + "]", e);
                continue;
            }
            
            physicalHosts.add(physicalHost);       
        }
        
        //
        // retrieving reports for all virtual hosts of a given physical host
        //
        for (PhysicalHostData physicalHost : physicalHosts) {
            
            List<String> virtualHostIDs = physicalHost.getVirtualHostIDs();
            
            for (String virtualHostID : virtualHostIDs) {
                
                VirtualHostData virtualHost = new VirtualHostData(virtualHostID);
                
                try {
                    LOG.info("retrieving monitoring dataset for virtual host [" + virtualHost.getVirtualHostID() + "]");
                    MonitoringResourceDatasets mrdHostData = monitoringClient.getLatestReportForVirtual(virtualHostID);
                    LOG.info("monitoring dataset size for [" + virtualHost.getVirtualHostID() + "]: " + mrdHostData.getMonitoring_resource().size());
                    virtualHost.setVirtualHostData( mrdHostData.getMonitoring_resource() );
                    virtualHost.toPrint();
                } catch (Exception e) {
                    LOG.error( "Error calling Monitoring: unable to fetch monitoring dataset for virtual host [" + virtualHostID + "] of [" + physicalHost.getPhysicalHostID() + "]", e);
                    continue;
                }
                
                try {
                    if(virtualHost.getVirtualHostData().size() > 0) {
                        LOG.debug("monitoring dataset (virtual) processing for virtual host [" + virtualHost.getVirtualHostID() + "] starting...");
                        virtualHost.processMetricDatasets();
                        LOG.debug("monitoring dataset (virtual) processing for virtual host [" + virtualHost.getVirtualHostID() + "] done...");
                    }
                } catch (Exception e) {
                    LOG.error( "Error processing (virtual) monitoring datasets for virtual host [" + virtualHost.getVirtualHostID() + "]", e);
                    continue;
                }
                
                physicalHost.addVirtualHost(virtualHost);
            }
        }
        
        for (PhysicalHostData physicalHost : physicalHosts) {
            LOG.info("physical-host: [" + physicalHost.getPhysicalHostID() + "] with virtual-hosts: " + physicalHost.getVirtualHosts().size());
        }

        // build return document
        WorkloadAnalysisDocument workloadAnalysisDocument = WorkloadAnalysisDocument.Factory.newInstance();
        WorkloadAnalysisType workloadAnalysisType = workloadAnalysisDocument.addNewWorkloadAnalysis();

        for (PhysicalHostData physicalHost : physicalHosts) {
            physicalHost.addMonitoredHostInfo(workloadAnalysisType);
        }
        
        //if (LOG.isTraceEnabled()) {
            //LOG.trace("returning workload analysis document : " + workloadAnalysisDocument.toString());
        //}

        return workloadAnalysisDocument;
    }
}
