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
package eu.optimis.manifest.api.ip;

import eu.optimis.manifest.api.impl.IPManifestFactory;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author owaeld
 */
public interface Manifest {

    public static ManifestFactory Factory = new IPManifestFactory();


    /**
     * @return manifest id
     * @see eu.optimis.manifest.api.sp.Manifest#getManifestId()
     */
    String getManifestId();

    /**
     * @return data protection section
     * @see eu.optimis.manifest.api.sp.Manifest#getDataProtectionSection()
     */
    DataProtectionSection getDataProtectionSection();

    /**
     * @return elasticity section
     * @see eu.optimis.manifest.api.sp.Manifest#getElasticitySection()
     */
    ElasticitySection getElasticitySection();


    /**
     * @return virtual machine description section
     * @see eu.optimis.manifest.api.sp.Manifest#getVirtualMachineDescriptionSection()
     */
    VirtualMachineDescriptionSection getVirtualMachineDescriptionSection();

    /**
     * @return service provider id
     * @see eu.optimis.manifest.api.sp.Manifest#getServiceProviderId()
     */
    String getServiceProviderId();

    /**
     * @return trec section
     * @see eu.optimis.manifest.api.sp.Manifest#getTRECSection()
     */
    TRECSection getTRECSection();


    /**
     * @see eu.optimis.manifest.api.sp.Manifest#toString()
     */
    String toString();

    /**
     * @return jaxB object
     * @see eu.optimis.manifest.api.sp.Manifest#toJaxB()
     */
    JaxBServiceManifest toJaxB();

    /**
     * @return xmlBeans object
     * @see eu.optimis.manifest.api.sp.Manifest#toXmlBeanObject()
     */
    XmlBeanServiceManifestDocument toXmlBeanObject();

    /**
     * getInfrastructureProviderExtensions()
     * <p/>
     * Retrieves the "InfrastructureProviderExtensions" element. If the document is not found, an initial document is
     * created;
     *
     * @return InfrastructureProviderExtension
     */
    InfrastructureProviderExtension getInfrastructureProviderExtensions();

    /**
     * initializeIncarnatedVirtualMachineComponents()
     * <p/>
     * This method creates the initial "InfrastructureProviderExtensions" element. An already existing element will
     * be overwritten.
     * <p/>
     * It is done as described in ID2.2.2:
     * The incarnation process follows a simple algorithm illustrated below.
     * <ul>
     * <li>Copy all references in the OVF template to the incarnated OVF</li>
     * <li>Update the Ids of all copied references such as: newId = Id + "instance_" + i </li>
     * <li>Copy all disks form the OVF template to the incarnated OVF</li>
     * <li>Update the Ids of all copied disks such as: newId = Id + "instance_" + i </li>
     * <li>Update the file references of all copied disks such as: newRef = Ref + "instance_" + i </li>
     * <li>Copy the virtual system definition from the template to the incarnated virtual system group </li>
     * <li>Update the Id of the copied virtual system such as: newId = Id + "instance_" + i  </li>
     * <li>Update the disk references of the copied virtual system such as: newRef = Ref + "instance_" + i</li>
     * </ul>
     * <p/>
     */
    void initializeIncarnatedVirtualMachineComponents();


    /**
     * extracts an existing component with all its references to a new manifest document and deletes
     * it from the current manifest document
     * <p/>
     * <ul>
     * <li>Copy the whole manifest</li>
     * <li>Remove the component to be extracted from the current manifest:</li>
     * <li>Remove the VirtualMachineComponent in the VirtualMachineDescription section</li>
     * <li>Remove all existing references in all Scope arrays from the current manifest</li>
     * <li>Remove all ElasiticyRule, AffinityRule and PricePlan elements with an empty Scope element</li>
     * <li>Remove Incarnated vm components in the Infrastructure Provider extensions</li>
     * <li>Remove component configurations in the Service Provider extensions</li>
     * <li>Retrieve a list of remaining components in the current manifest and remove them in the new manifest
     * by repeating the process for each of them.</li>
     * </ul>
     *
     * @param componentId
     * @return the extracted IP Manifest
     */
    Manifest extractComponent(String componentId);

    /**
     * used to add the infrastructure provider extension section which does not exist per default.
     */
    void initializeInfrastructureProviderExtensions();
}