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

import java.util.Collection;

/**
 * @author hrasheed
 * 
 */
public interface WorkloadAnalyzer {

    /**
     * Performs an analysis of the current workload with respect to the new requirements defined
     * by the passed Service manifest. Based on the required resources for the VMs used by the
     * service described in the Service manifest, the result is a <code>WorkloadAnalysisDocument</code> instance,
     * which includes a list of the hosts able to host the new VMs.
     * <p/>
     * The passed Service manifest document will be added to the list of considered service manifests automatically.
     *
     * @param serviceManifest Service manifest document of the new service.
     *
     * @return Workload analysis document, which contains a list of all hosts able to host the new VMs.
     *
     * @throws WorkloadAnalyzerException
     */
    public WorkloadAnalysisDocument performWorkloadAnalysis(String serviceManifest) throws WorkloadAnalyzerException;

    /**
     * Adds a new Service manifest to the list of considered existing services.
     *
     * @param serviceManifest Service manifest document of the service, which should be considered during analysis.
     *
     * @return true, if add operation was successful, else wise.
     *
     * @throws WorkloadAnalyzerException
     */
    public boolean addServiceManifest(String serviceManifest) throws WorkloadAnalyzerException;

    /**
     * Removes a already added Service manifest document from the list of considered services.
     *
     * @param slaId ID of the service to remove.
     *
     * @return true, if remove operation was successful, else wise.
     *
     * @throws WorkloadAnalyzerException
     */
    public boolean removeServiceManifest(String slaId) throws WorkloadAnalyzerException;

    /**
     * Lists all already added Service manifest documents by their SLA IDs.
     *
     * @return List of SLA IDs, which represent the corresponding Service manifest documents.
     *
     * @throws WorkloadAnalyzerException
     */
    public Collection<String> listServiceManifests() throws WorkloadAnalyzerException;
}
