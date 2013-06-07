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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.optimis.manifestregistry.exceptions.PropertyMissingException;

/**
 * @author hrasheed
 * 
 */
public class RegistryPropertiesUtil
{
	
	private static final Logger logger = Logger.getLogger(RegistryPropertiesUtil.class);

	private Properties properties;
	private String fileName;

	public RegistryPropertiesUtil(String registryPropertiesFile) throws IOException
	{
		fileName = registryPropertiesFile;
		properties = getProperties(registryPropertiesFile);
	}

	public RegistryPropertiesUtil() throws IOException 
	{
		String registryPropertiesFile = RegistryPropertiesUtil.class.getResource("/manifest-registry.properties").getPath();
		fileName = registryPropertiesFile;
		properties = getProperties(registryPropertiesFile);
	}
	
	private Properties getProperties(String registryPropertiesFile) throws IOException
	{
		Properties properties = new Properties();
		InputStream fis = null;
		try {
			fis = new FileInputStream(new File(registryPropertiesFile));
			properties.load(fis);
			logger.debug("ManifestRegistry Properties loaded and set.");
		} catch (Exception e) {
			String message = "ManifestRegistry properties could not be loaded from " + registryPropertiesFile;
			logger.error(message);
			throw new IOException(message, e);
		} finally {
			fis.close();
		}
		return properties;
	}
	
	public String getManifestRegistryHost() throws PropertyMissingException
	{
		String host = properties.getProperty(ManifestRegistryConstants.MANIFEST_REGISTRY_HOST);
		if (host == null){
			throw new PropertyMissingException("Property " + ManifestRegistryConstants.MANIFEST_REGISTRY_HOST + " missing in " + fileName);
		}
		return host;
	}

	public String getManifestRegistryPort() throws PropertyMissingException, MalformedURLException
	{
		String port = properties.getProperty(ManifestRegistryConstants.MANIFEST_REGISTRY_PORT);
		if (port == null){
			throw new PropertyMissingException("Property " + ManifestRegistryConstants.MANIFEST_REGISTRY_PORT + " missing in " + fileName);
		}
		return port;
	}
	
	public String getManifestRegistryURL() throws PropertyMissingException, MalformedURLException
	{
		String registryURL = properties.getProperty(ManifestRegistryConstants.MANIFEST_REGISTRY_URL);
		if (registryURL == null){
			throw new PropertyMissingException("Property " + ManifestRegistryConstants.MANIFEST_REGISTRY_URL + " missing in " + fileName);
		}
        checkUrlValidity(registryURL);
		return registryURL;
	}
	
	public String getManifestRegistryURLPath() throws PropertyMissingException, MalformedURLException
	{
		String path = properties.getProperty(ManifestRegistryConstants.MANIFEST_REGISTRY_URL_PATH);
		if (path == null){
			throw new PropertyMissingException("Property " + ManifestRegistryConstants.MANIFEST_REGISTRY_URL_PATH + " missing in " + fileName);
		}
		return path;
	}
	
	public static void checkUrlValidity(String url_string) throws MalformedURLException
	{
        if(logger.isTraceEnabled()) {
        	logger.trace("checking url for validity : "+url_string);
        }
        URL aUrl = new URL(url_string);
        if(logger.isDebugEnabled()) { 
        	logger.debug("valid url found : " + aUrl.toString());
        }
    }
	
}
