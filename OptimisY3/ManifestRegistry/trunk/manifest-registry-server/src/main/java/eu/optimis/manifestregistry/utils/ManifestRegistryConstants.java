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
package eu.optimis.manifestregistry.utils;

import org.apache.xmlbeans.XmlObject;

import eu.optimis.manifestregistry.exceptions.ResourceTypeUnknown;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 * 
 */
public class ManifestRegistryConstants {
	
	public static final String MANIFEST_REGISTRY_HOST = "manifest.registry.host";
	public static final String MANIFEST_REGISTRY_PORT = "manifest.registry.port";
	
	public static final String MANIFEST_REGISTRY_URL = "manifest.registry.url";
	public static final String MANIFEST_REGISTRY_URL_PATH = "manifest.registry.path";
	
	public static final int UNKNOWN = -1;
	
	public static final int GENERIC = 0;
	
	public static final int SERVICE_MANIFEST = 1;
	
	public static final int PROVIDER_INFO = 2;
	
	public static final String[] TYPE_NAMES = { "ServiceManifest", "ProviderInfo" };
    
	
	public static int getTypeID(XmlObject object)
	{
		if (object instanceof XmlBeanServiceManifestDocument)
		{
			return ManifestRegistryConstants.SERVICE_MANIFEST;
		}
		
		if (object instanceof XmlObject) {
			return ManifestRegistryConstants.PROVIDER_INFO;
		}

		// resource type not recognized
		return -1;
	}

	public static int getManifestTypeID()
	{
		return ManifestRegistryConstants.SERVICE_MANIFEST;
	}

	public static int getProviderTypeID()
	{
		return ManifestRegistryConstants.PROVIDER_INFO;
	}

	public static XmlObject createResource(int type) throws ResourceTypeUnknown
	{
		
		switch (type) {

		case ManifestRegistryConstants.SERVICE_MANIFEST:
			
			//
			// generate blank service manifest document
			//
			XmlBeanServiceManifestDocument serviceManifestDoc = XmlBeanServiceManifestDocument.Factory.newInstance();
			return serviceManifestDoc;

		case ManifestRegistryConstants.PROVIDER_INFO:
			
			//
			// generate blank provider document
			//
			return null;

			// break;
		default:
			throw new ResourceTypeUnknown();
	
		}
	}
}
