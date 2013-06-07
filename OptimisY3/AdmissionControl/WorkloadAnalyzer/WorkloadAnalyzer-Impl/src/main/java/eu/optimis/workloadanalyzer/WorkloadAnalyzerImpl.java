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

import eu.optimis.schemas.workload.WorkloadAnalysisDocument;
import eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hrasheed
 * 
 */
public class WorkloadAnalyzerImpl implements WorkloadAnalyzer {

    private static final Logger log = Logger.getLogger(WorkloadAnalyzerImpl.class);

    private Map<String, String> serviceManifests;


    public WorkloadAnalyzerImpl() {
        // initialize service manifest store
        serviceManifests = new HashMap<String, String>();
    }

    public WorkloadAnalysisDocument performWorkloadAnalysis(String serviceManifest) throws WorkloadAnalyzerException {
        
    	try {
    		
//    		if(log.isTraceEnabled()) {
//                log.trace("Input service manifest:" + serviceManifest);
//            }
    		
	        // creating workload manager
	        WorkloadManager workloadManager = new WorkloadManager();

            // pass client objects and retrieve workload analysis
            WorkloadAnalysisDocument workloadAnalysisDocument = workloadManager.getWorkloadAnalysis(serviceManifest);
	        
			if(log.isTraceEnabled()) 
			{
				log.trace(workloadAnalysisDocument.toString());
			}
	         
            return workloadAnalysisDocument;
    		
    	} catch (Exception e) {
			log.error(e);
			throw new WorkloadAnalyzerException(e);
		} 	
    }
    
    public boolean addServiceManifest(String serviceManifest) throws WorkloadAnalyzerException {
        
    	// TODO: retrieve/parse SLA ID

        // add the passed service manifest, if it wasn't already added
    	
        if(!serviceManifests.containsKey(serviceManifest)) {
            serviceManifests.put("", serviceManifest);
        }
        else {
            throw new WorkloadAnalyzerException("Service manifest with SLA ID '' was already added.");
        }

        return true;
    }

    public boolean removeServiceManifest(String slaId) throws WorkloadAnalyzerException {
        
    	// TODO: retrieve/parse SLA ID

        // remove service manifest for the passed SLA ID, if the service manifest was added
    	
        if(serviceManifests.containsKey(slaId)) {
            serviceManifests.remove(slaId);
        }
        else {
            throw new WorkloadAnalyzerException("Service manifest with SLA ID '' was not found.");
        }

        return true;
    }

    public Collection<String> listServiceManifests() throws WorkloadAnalyzerException {
        return serviceManifests.values();
    }
}
