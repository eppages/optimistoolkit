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
package eu.optimis.interopt.provider;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.net.UnknownServiceException;
import java.util.List;

/**
 * Client uses to communicate with a VM Management system.
 * 
 * @author owaeld
 * 
 */
public interface VMManagementSystemClient
{

    /**
     * A call to the deploy method starts the deployment process of the specified service in an infrastructure
     * provider. The components of the service are specified by the service components parameter. Each
     * component consists of one or more virtual machine instances that use the same VM image. additionally,
     * each VM instance of a component has a specific contextualization image that must be mounted at VM
     * provisioning time. These component definitions have to be acknowledged by the infrastructure provider
     * in order to provide basic compliance with the OPTIMIS infrastructure. Additionally, a service manifest
     * document is passed to the {@link #deployService(String, java.util.List, eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument)} method. An
     * infrastructure provider can use this document in order to provide full compliance with other OPTIMIS
     * providers.
     * 
     * @param serviceId
     *            a unique id that identifies the service (collection of virtual machines) in the
     *            infrastructure provider domain.
     * 
     * @param serviceComponents
     *            The list of components associated with the service. A component specifies parameters such as
     *            the number of VM instances that must be provided for this component, the virtual machine
     *            image to use, the id's/names of the contextualization images for the individual VM
     *            instances, the individual CPU speed of each VM instance, etc. For more details refer to
     *            {@link ServiceComponent}.
     * 
     * @param manifest
     *            The manifest document contains the full OPTIMIS service manifest that is used during service
     *            contracting. The interpretation of the manifest is only relevant for providers that want to
     *            establish FULL compliance with the OPTIMIS infrastructure.
     * 
     * @throws ServiceInstantiationException
     *             indicates an ongoing error with respect to the resource allocation process, i.e. the
     *             requested number of VMs exceed the maximum instances that can be allocated at once.
     */
    void deployService( String serviceId, List<ServiceComponent> serviceComponents,
                        XmlBeanServiceManifestDocument manifest ) throws ServiceInstantiationException;

    /**
     * This method queries the properties of a deployed service. It returns a list of VM properties, one for
     * each VM instance. The required information that should be provided are defined in the
     * {@link VMProperties}.
     * 
     * @param serviceId
     *            the id of the service provisioning process
     * 
     * @return a set of {@link VMProperties}, one for each VM instance that belongs to the service
     * 
     * @throws UnknownServiceException
     *             indicates that the specified service id is unknown
     */
    List<VMProperties> queryServiceProperties( String serviceId ) throws UnknownServiceException;

    /**
     * Terminates the service provisioning process with the given serrvice id.
     * 
     * @param serviceId
     *            the id of the service provisioning process
     * 
     * @throws UnknownServiceException
     *             indicates that the specified service id is unknown
     */
    void terminate( String serviceId ) throws UnknownServiceException;
    
    /**
     * Checks whether a service is currently deployed or not
     * 
     * @param serviceId
     *            the id of the service provisioning process
     */
    boolean isDeployed(String serviceId);

    /**
     * set the username and password for authentication
     * @param username
     * @param password
     */
    void setAuth( String username, String password );
}
