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
package eu.optimis.manifestregistry;

import eu.optimis.manifestregistry.exceptions.ResourceInvalid;
import eu.optimis.manifestregistry.exceptions.ResourceNotFound;
import eu.optimis.manifestregistry.exceptions.ResourceTypeUnknown;

import java.util.List;

import org.apache.xmlbeans.XmlObject;

/**
 *  Service Manifest Registry Interface 
 *
 * @author hrasheed
 *
 */
public interface ManifestRegistry {
	
	
	/**
     * Implementation stores given resource
     *
     * @param  XmlObject type Resource
     *
     * @return true if successful
     * 
     * @throws ResourceTypeUnknown, ResourceInvalid
     */
	public boolean add(XmlObject resource) throws ResourceTypeUnknown, ResourceInvalid;
	
	/**
     * Implementation returns requested resource
     *
     * @param unique ID (String) of the resource
     * 
     * @return resource as XmlObject
     * 
     * @throws ResourceNotFound
     */
	public XmlObject get(String resourceID) throws ResourceNotFound;
	
    /**
     * Implementation updates given resource
     *
     * @param XmlObject type Resource
     * 
     * @return true if successful
     * 
     */
	public boolean update(XmlObject resource) throws ResourceNotFound, ResourceTypeUnknown, ResourceInvalid;
	
    
    /**
     * Implementation removes indexed resource
     *
     * @param unique ID (String) of the resource
     * 
     * @return true if successful
     */
	public boolean remove(String resourceID) throws ResourceNotFound;
	
    /**
     * Implementation returns all resources of the same type
     *
     * @param type of resources to retrieve 
     * 
     * @return List of XmlObjects
     */
	public List<XmlObject> getAllResourcesOfType(int type) throws ResourceTypeUnknown;
    
}
