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
package eu.optimis.workloadanalyzer.ws;

import eu.optimis.schemas.workload.WorkloadAnalysisDocument;
import eu.optimis.workloadanalyzer.WorkloadAnalyzer;
import eu.optimis.workloadanalyzer.WorkloadAnalyzerImpl;
import eu.optimis.workloadanalyzer.exceptions.WorkloadAnalyzerException;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author hrasheed
 * 
 */
@Path(value = "/WorkloadAnalyzerService")
public class WorkloadAnalyzerService {

    private static final Logger log = Logger.getLogger(WorkloadAnalyzerService.class);

    private WorkloadAnalyzer workloadAnalyzer = null;


    public WorkloadAnalyzerService() {
        if(log.isDebugEnabled()) {
            log.debug("Initializing a new WorkloadAnalyzerService instance.");
        }

        workloadAnalyzer = new WorkloadAnalyzerImpl();
    }


    @POST
    @Path("performWorkloadAnalysis")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String performWorkloadAnalysis(String serviceManifest) {
        
    	if(log.isDebugEnabled()) {
            log.debug("-> WS operation 'performWorkloadAnalysis' called.");
        }
        
        // perform workload analysis
        WorkloadAnalysisDocument workloadAnalysisDocument = null;
        
        try {
        	workloadAnalysisDocument = workloadAnalyzer.performWorkloadAnalysis(serviceManifest);
        }
        catch(WorkloadAnalyzerException ex) {
            log.error("Could not perform workload analysis: " + ex.getMessage());
        }

        return workloadAnalysisDocument.xmlText();
    }

    @POST
    @Path("addServiceManifest")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String addServiceManifest(String serviceManifest) {
    	
        if(log.isDebugEnabled()) {
            log.debug("-> WS operation 'addServiceManifest' called.");
        }
        
        if(log.isTraceEnabled()) {
            log.trace("Input service manifest:" + serviceManifest);
        }

        return Boolean.toString(true);
    }

    @POST
    @Path("removeServiceManifest")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String removeServiceManifest(String slaId) {
    	
        if(log.isDebugEnabled()) {
            log.debug("-> WS operation 'removeServiceManifest' called.");
        }
        
        if(log.isTraceEnabled()) {
            log.trace("Input SLA ID:" + slaId);
        }

        return Boolean.toString(true);
    }

    @GET
    @Path("listServiceManifests")
    @Produces(value = "text/plain")
    public String listServiceManifests() {
    	
        if(log.isDebugEnabled()) {
            log.debug("-> WS operation 'listServiceManifests' called.");
        }

        Collection<String> slaIDs = new ArrayList<String>();
        
        try {
            slaIDs = workloadAnalyzer.listServiceManifests();
        }
        catch(WorkloadAnalyzerException ex) {
            log.error("Could not load the list of SLA Ids: " + ex.getMessage());
        }

        // built the result string with a concatenated comma-separated list of SLA IDs
        StringBuilder result = new StringBuilder();

        Iterator<String> iter = slaIDs.iterator();
        while(iter.hasNext()) {
            result.append(iter.next());

            if(iter.hasNext()) {
                result.append(",");
            }
        }

        return result.toString();
    }
}
