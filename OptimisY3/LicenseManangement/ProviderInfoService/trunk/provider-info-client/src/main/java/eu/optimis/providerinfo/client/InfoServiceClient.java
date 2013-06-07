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
package eu.optimis.providerinfo.client;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.Response;

/**
 * @author hrasheed
 * 
 */
public class InfoServiceClient
{
	
	private static final Logger LOG = Logger.getLogger(InfoServiceClient.class);
	
	private String URL = "http://localhost:7070/provider-info/Service";
    
    private final String OPT_GET_INFO = "getInfo";
    
    private Client client;
    
    private static InfoServiceClient infoServiceClientInstance = null;

    public InfoServiceClient(String url)
    {
    	this.URL = url;
        this.client = Client.create();
    }
    
    public static synchronized InfoServiceClient getInstance(String url)
    {
        if ( infoServiceClientInstance == null )
        {
        	infoServiceClientInstance = new InfoServiceClient(url);
        }

        return infoServiceClientInstance;
    }

    private String getAddress(String interface_option)
    {
        return this.URL + "/" + interface_option;
    }
    
    public String getInfo() 
    {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_GET_INFO));

            ClientResponse response = webResourceClient.get(ClientResponse.class);
    	    
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		if (success) {
    			
    			String returnedString = response.getEntity(String.class);
    			
    			if(returnedString.equals("false")) {
    				LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_GET_INFO) + "': " + returnedString);
    				throw new Exception("failed to get info from provider info service: internal server error.");
    			}
    	
    			return returnedString;
    			
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			throw new Exception("failed to get info from provider info service - reason: " + error);
    		}
    		
        } catch (Exception e) {
			LOG.error(e);
			return null;
		}	
    }
}
