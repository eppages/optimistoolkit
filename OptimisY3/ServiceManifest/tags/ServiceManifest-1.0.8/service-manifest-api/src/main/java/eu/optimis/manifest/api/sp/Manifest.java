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

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.manifest.api.impl.SPManifestFactory;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlError;

import java.util.List;

/**
 * @author owaeld
 */
public interface Manifest
{

    /**
     * Default factory for SP Manifest
     */
    // CHECKSTYLE:OFF - XmlBeans Naming convention
    public static ManifestFactory Factory = new SPManifestFactory();

    // CHECKSTYLE:ON

    /**
     * @return manifest id
     */
    String getManifestId();

    /**
     * @param manifestId the id of the manifest
     */
    void setManifestId( String manifestId );

    /**
     * @return the service provider id
     */
    String getServiceProviderId();

    /**
     * @param serviceProviderId the id of the service provider
     */
    void setServiceProviderId( String serviceProviderId );

    /**
     * Returns the "VirtualMachineDescription" element which is a substitution of "ServiceDescriptionSection"
     * element
     *
     * @return the VirtualMachineDescription element
     */
    VirtualMachineDescriptionSection getVirtualMachineDescriptionSection();

    /**
     * Returns data protection section.
     *
     * @return data protection section
     */
    DataProtectionSection getDataProtectionSection();

    /**
     * @return elasticity section.
     */
    ElasticitySection getElasticitySection();

    /**
     * @return TREC section.
     */
    TRECSection getTRECSection();

    /**
     * Retrieves the "ServiceProviderExtensions" element. If the element is not found, the method returns
     * null;
     *
     * @return the "ServiceProviderExtensions" element
     */

    ServiceProviderExtension getServiceProviderExtensionSection();

    /**
     * @param componentId the id of the component that is going to be extracted
     * @return the extracted IP Manifest
     * @see eu.optimis.manifest.api.sp.Manifest#extractComponent(String)
     */
    Manifest extractComponent( String componentId ) throws SplittingNotAllowedException;

    /**
     * @param componentIds a list of componentIds
     * @return the extracted IP Manifest
     * @see eu.optimis.manifest.api.sp.Manifest#extractComponentList(java.util.List)
     */
    Manifest extractComponentList( List<String> componentIds ) throws SplittingNotAllowedException;

    /**
     * Exports current Manifest state to a String.
     *
     * @return service manifest as String
     */
    @Override
    String toString();

    /**
     * Exports current Manifest state to a Jaxb representation.
     *
     * @return service manifest as JaxB object
     * @see eu.optimis.schemas.optimis.JaxBServiceManifest
     */
    JaxBServiceManifest toJaxB();

    /**
     * Exports current Manifest state to an XMLBeans representation.
     *
     * @return service manifest as XmlBeans object
     * @throws RuntimeException if the manifest is not a valid document
     */
    XmlBeanServiceManifestDocument toXmlBeanObject();

    /**
     * returns validation errors found in the xml document. The validation is done by the xmlbeans validate
     * method.
     *
     * @return a list of errors
     */
    List<XmlError> getErrors();

    /**
     * returns true if the xml object is not valid
     *
     * @return true | false
     */
    boolean hasErrors();

    void unsetServiceProviderExtensions();
}