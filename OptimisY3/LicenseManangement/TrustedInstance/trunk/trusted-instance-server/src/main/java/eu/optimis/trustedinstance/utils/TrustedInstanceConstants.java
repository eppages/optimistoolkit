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

import org.apache.xmlbeans.XmlObject;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;

/**
 * @author hrasheed
 * 
 */
public class TrustedInstanceConstants {
	
	public static final String TRUSTED_INSTANCE_HOST = "trusted.instance.host";
	public static final String TRUSTED_INSTANCE_PORT = "trusted.instance.port";
	
	public static final String TRUSTED_INSTANCE_URL = "trusted.instance.url";
	public static final String TRUSTED_INSTANCE_URL_PATH = "trusted.instance.path";
	
	public static final int UNKNOWN = -1;
	
	public static final int LICENSE_TOKEN = 1;
	
	public static final int PROVIDER_INFO = 2;
	
	public static final String[] TYPE_NAMES = { "LicenseToken", "ProviderInfo" };
    
	
	public static int getTypeID(XmlObject object)
	{
		if (object instanceof LicenseTokenDocument)
		{
			return TrustedInstanceConstants.LICENSE_TOKEN;
		}
		
		if (object instanceof XmlObject) {
			return TrustedInstanceConstants.PROVIDER_INFO;
		}

		// resource type not recognized
		return -1;
	}

	public static int getTokenTypeID()
	{
		return TrustedInstanceConstants.LICENSE_TOKEN;
	}

	public static int getProviderTypeID()
	{
		return TrustedInstanceConstants.PROVIDER_INFO;
	}

}
