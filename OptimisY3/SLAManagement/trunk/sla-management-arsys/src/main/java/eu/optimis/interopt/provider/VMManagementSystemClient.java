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
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.interopt.provider;

import java.net.UnknownServiceException;
import java.util.List;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

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
     * set the username and password for http authentication
     * @param username
     * @param password
     */
    void setAuth( String username, String password );
}
