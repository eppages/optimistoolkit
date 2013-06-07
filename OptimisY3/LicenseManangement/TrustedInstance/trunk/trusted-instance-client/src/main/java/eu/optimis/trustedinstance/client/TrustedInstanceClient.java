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
package eu.optimis.trustedinstance.client;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.elasticlm.schemas.x2010.x01.token.envelope.TokenEnvelopeDocument;

import javax.ws.rs.core.Response;

/**
 * @author hrasheed
 * 
 */
public class TrustedInstanceClient
{
	
	private static final Logger LOG = Logger.getLogger(TrustedInstanceClient.class);
	
	private String URL = "http://localhost:9090/trusted-instance/TokenService";
    
    private final String OPT_GET_LICENSE_TOKEN = "getToken";
    private final String OPT_STORE_LICENSE_TOKEN = "storeToken";
    private final String OPT_REMOVE_LICENSE_TOKEN = "removeToken";
    
    private Client client;
    
    private static TrustedInstanceClient trustedClientInstance = null;

    public TrustedInstanceClient()
    {
    	this.init();
		this.client = Client.create();
    }
    
    public TrustedInstanceClient(String url)
    {
    	this.URL = url;
        this.client = Client.create();
    }
    
    private void init()
    {
    	try {
    		 this.URL = ComponentConfigurationProvider.getString( "trusted.instance.url" ); //$NON-NLS-1$
             LOG.info( MessageFormat.format( "trusted instance URL {0}", new Object[] { this.URL } ) );
    	}catch (Exception e) {
			LOG.error(e);
		}
	}
    
    public static synchronized TrustedInstanceClient getInstance(String url)
    {
        if ( trustedClientInstance == null )
        {
        	trustedClientInstance = new TrustedInstanceClient(url);
        }

        return trustedClientInstance;
    }

    private String getAddress(String interface_option)
    {
        return this.URL + "/" + interface_option;
    }
    
    public LicenseTokenSecureDocument getToken(UserTokenAuthorizationDocument userTokenAuthorizationDoc) throws Exception
    {
    	
    	try {
    		
    		TokenEnvelopeDocument userAuthEnvelopedDoc = TokenEnvelopeDocument.Factory.newInstance();
    		userAuthEnvelopedDoc.addNewTokenEnvelope().setEncodedToken(userTokenAuthorizationDoc.xmlText().getBytes());
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_GET_LICENSE_TOKEN));

            ClientResponse response = webResourceClient.post(ClientResponse.class, userAuthEnvelopedDoc.toString());
    	    
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success) {
    			
    			String returnedString = response.getEntity(String.class);

                LOG.debug("Message of the webservice operation '" + this.getAddress(OPT_GET_LICENSE_TOKEN) + "': " + returnedString);
    			if(returnedString.equals("false")) {
    				LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_GET_LICENSE_TOKEN) + "': " + returnedString);
    				throw new Exception("failed to get token from a trusted instance: internal server error.");
    			}
    			
    			TokenEnvelopeDocument tokenEnvelopedDoc = (TokenEnvelopeDocument) XmlObject.Factory.parse(returnedString);
    			
    			LicenseTokenSecureDocument tokenTrustedDoc = LicenseTokenSecureDocument.Factory.parse(new ByteArrayInputStream(tokenEnvelopedDoc.getTokenEnvelope().getEncodedToken()));
                
    			return tokenTrustedDoc;
    			
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			throw new Exception("failed to get token from a trusted instance - reason: " + error);
    		}
    		
        } catch (Exception e) {
			LOG.error(e);
			throw new Exception("trusted-instance error: " + e.getMessage(), e);
		}	
    }
    
    public boolean storeToken(LicenseTokenDocument tokenDoc)
    {
    	
    	try {
    	
    		TokenEnvelopeDocument tokenEnvelopedDoc = TokenEnvelopeDocument.Factory.newInstance();
    		tokenEnvelopedDoc.addNewTokenEnvelope().setEncodedToken(tokenDoc.xmlText().getBytes());
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_STORE_LICENSE_TOKEN));
          
            ClientResponse response = webResourceClient.post(ClientResponse.class, tokenEnvelopedDoc.toString());
            
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success) {
    			String returnedString = response.getEntity(String.class);
    			boolean result = (returnedString.equals("true")? true : false);
    			LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_STORE_LICENSE_TOKEN) + "': " + result);
    			LOG.debug("Message of the webservice operation '" + this.getAddress(OPT_STORE_LICENSE_TOKEN) + "': " + returnedString);
                return result;
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			throw new Exception("failed to store license token: " + error);
    		}
    		
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}	
    }
    
    public boolean removeToken(LicenseTokenDocument tokenDoc) 
    {
    	
    	try {
    		
    		TokenEnvelopeDocument tokenEnvelopedDoc = TokenEnvelopeDocument.Factory.newInstance();
    		tokenEnvelopedDoc.addNewTokenEnvelope().setEncodedToken(tokenDoc.xmlText().getBytes());
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_REMOVE_LICENSE_TOKEN));

            ClientResponse response = webResourceClient.post(ClientResponse.class, tokenEnvelopedDoc.toString());
            
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success) {
    			String returnedString = response.getEntity(String.class);
    			boolean result = (returnedString.equals("true")? true : false);
    			LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_REMOVE_LICENSE_TOKEN) + "': " + result);
                LOG.debug("Message of the webservice operation '" + this.getAddress(OPT_REMOVE_LICENSE_TOKEN) + "': " + returnedString);
    			return result;
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			throw new Exception("failed to remove license token: " + error);
    		}
    		
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}	
    }
}
