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

import eu.optimis.manifest.api.ovf.sp.OVFDefinition;

/**
 * @author owaeld
 */
public interface VirtualMachineComponent
{

    /**
     * Retrieves the OVF Definition section . It provides information on location, format, network connection
     * and virtual system description to be used for creating component instances.
     * 
     * @return the ovf section
     * @see OVFDefinition
     */
    OVFDefinition getOVFDefinition();

    /**
     * Retrieves the allocation constraints. It is used to define the maximum and minimum number of component
     * instances.
     * 
     * @return the allocation constraints
     */
    AllocationConstraint getAllocationConstraints();

    /**
     * @return affinity constraint as one of [Low | Medium | High]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType.Enum
     */
    String getAffinityConstraints();

    /**
     * Affinity Constraints describe the level of affinity the incarnated instances must have.
     * 
     * @param constraint
     *            one of [Low | Medium | High]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType
     */
    void setAffinityConstraints( String constraint );
    
    /**
     * @return anti affinity constraint as one of [Low | Medium | High]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityConstraintType.Enum
     */
    String getAntiAffinityConstraints();

    /**
     * Anti Affinity Constraints describe the level of anti affinity the incarnated instances must have.
     * 
     * @param constraint
     *            one of [Low | Medium | High]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityConstraintType
     */
    void setAntiAffinityConstraints( String constraint );

    /**
     * The ID of the component. This must be unique throughout the manifest. This is set when adding a
     * component and cannot be changed afterwards.
     * 
     * @return the component id
     */
    String getComponentId();

    /**
     * retrieve a list of service endpoints to services that have to be accessible by the vm
     * 
     * @return an array of service endpoints
     */
    ServiceEndpoint[] getServiceEndpoints();

    /**
     * Adds a new service endpoint to the list of service endpoints.
     * 
     * @param name
     *            the name of the service
     * @param uri
     *            the uri to the service
     * @return the newly created endpoint
     */
    ServiceEndpoint addNewServiceEndPoint( String name, String uri );

    /**
     * Removes the service endpoint at position i
     * 
     * @param i
     */
    void removeServiceEndpoint( int i );

    /**
     * This method uses the contextualization file ref of the OVF and creates the upperBound number of updated
     * links by adding an index to the link. The method was requested by SDO to be able to provide the list of
     * files to the Data Manager on the SP level in order to create the storage resources for a service.
     * <p/>
     * e.g. fileref in the OVF: /path/to/file.iso maximum number of instances: 5 outcome:
     * <ul>
     * <li>/path/to/file_1.iso</li>
     * <li>/path/to/file_2.iso</li>
     * <li>/path/to/file_3.iso</li>
     * <li>/path/to/file_4.iso</li>
     * <li>/path/to/file_5.iso</li>
     * </ul>
     * 
     * @return a list of file hrefs
     */
    String[] getIncarnatedContextualizationFileArray();
}