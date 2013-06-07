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

import java.util.Properties;

import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author arumpl
 */
public interface ManifestFactory
{

    /**
     * Creates a new Service Manifest based on the "ServiceManifestDocument.vm" template. The loaded template
     * will provide default values for all required sections.
     * 
     * @param serviceId
     *            The id of the service
     * @param componentId
     *            The id of the initial component. (e.g. JBOSS, MYSQL, etc)
     * @return the Interface for manipulating a full service manifest document with the access rights of a
     *         Service Provider.
     * @see Manifest
     */
    Manifest newInstance( String serviceId, String componentId );

    /**
     * Constructs a service manifest by using properties
     * 
     * @param serviceId
     *            the serviceId
     * @param componentId
     *            the initial componentId
     * @param properties
     *            properties for all values in the manifest
     * @return the newly created service manifest
     */
    Manifest newInstance( String serviceId, String componentId, Properties properties );

    /**
     * Imports an SP Manifest from an XmlBeansObject
     * 
     * @param manifestAsXmlBeans
     * @return the sp manifest interface
     */
    Manifest newInstance( XmlBeanServiceManifestDocument manifestAsXmlBeans );

    /**
     * Imports an SP Manifest from a JaxB Object
     * 
     * @param manifestAsJaxB
     * @return the sp manifest interface
     */
    Manifest newInstance( JaxBServiceManifest manifestAsJaxB );

    /**
     * Imports an SP Manifest from a String
     * 
     * @param manifestAsString
     * @return the sp manifest interface
     */
    Manifest newInstance( String manifestAsString );

}
