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
package eu.optimis.trustedinstance.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.optimis.trustedinstance.exceptions.PropertyMissingException;

/**
 * @author hrasheed
 * 
 */
public class TrustedInstancePropertiesUtil
{
	
	private static final Logger logger = Logger.getLogger(TrustedInstancePropertiesUtil.class);

	private Properties properties;
	private String fileName;

	public TrustedInstancePropertiesUtil(String registryPropertiesFile) throws IOException
	{
		fileName = registryPropertiesFile;
		properties = getProperties(registryPropertiesFile);
	}

	public TrustedInstancePropertiesUtil() throws IOException 
	{
		String registryPropertiesFile = TrustedInstancePropertiesUtil.class.getResource("/trusted-instance.properties").getPath();
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
			logger.debug("TrustedInstance Properties loaded and set.");
		} catch (Exception e) {
			String message = "TrustedInstance properties could not be loaded from " + registryPropertiesFile;
			logger.error(message);
			throw new IOException(message, e);
		} finally {
			fis.close();
		}
		return properties;
	}
	
	public String getTrustedInstanceHost() throws PropertyMissingException
	{
		String host = properties.getProperty(TrustedInstanceConstants.TRUSTED_INSTANCE_HOST);
		if (host == null){
			throw new PropertyMissingException("Property " + TrustedInstanceConstants.TRUSTED_INSTANCE_HOST + " missing in " + fileName);
		}
		return host;
	}

	public String getTrustedInstancePort() throws PropertyMissingException, MalformedURLException
	{
		String port = properties.getProperty(TrustedInstanceConstants.TRUSTED_INSTANCE_PORT);
		if (port == null){
			throw new PropertyMissingException("Property " + TrustedInstanceConstants.TRUSTED_INSTANCE_PORT + " missing in " + fileName);
		}
		return port;
	}
	
	public String getTrustedInstanceURL() throws PropertyMissingException, MalformedURLException
	{
		String registryURL = properties.getProperty(TrustedInstanceConstants.TRUSTED_INSTANCE_URL);
		if (registryURL == null){
			throw new PropertyMissingException("Property " + TrustedInstanceConstants.TRUSTED_INSTANCE_URL + " missing in " + fileName);
		}
        checkUrlValidity(registryURL);
		return registryURL;
	}
	
	public String getTrustedInstanceURLPath() throws PropertyMissingException, MalformedURLException
	{
		String path = properties.getProperty(TrustedInstanceConstants.TRUSTED_INSTANCE_URL_PATH);
		if (path == null){
			throw new PropertyMissingException("Property " + TrustedInstanceConstants.TRUSTED_INSTANCE_URL_PATH + " missing in " + fileName);
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
