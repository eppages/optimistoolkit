/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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
package eu.optimis.manifest.api.sp;

/**
 * @author owaeld
 */
public interface ServiceProviderExtension {

    /**
     * @return all software dependencies of all components
     */
    SoftwareDependencies[] getSoftwareDependenciesArray();

    /**
     * @param i the position to be returned
     * @return all softare dependencies at the ith position
     */
    SoftwareDependencies getSoftwareDependenciesArray(int i);


    /**
     * @param componentId the id of the referenced component
     * @return all software dependencies belonging to the component identified by componentId
     */
    SoftwareDependencies getSoftwareDependenciesByComponentId(String componentId);


    /**
     * Adds a new "SoftwareDependencies" Element for this component
     * If there already exists a section with this componentId, nothing will be done.
     *
     * @param componentId the id of the referenced component
     * @return the newly created SoftwareDependencies element
     * @see SoftwareDependencies
     */
    SoftwareDependencies addNewSoftwareDependencies(String componentId);

    /**
     * Removes all software dependencies belonging to the referenced component
     *
     * @param componentId the id of the referenced component
     */
    void removeSoftwareDependenciesByComponentId(String componentId);

    /**
     * Removes all software dependencies at the ith position
     */
    void removeSoftwareDependencies(int i);

}