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
package eu.optimis.manifestregistry.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 * 
 */
public class ManifestRegistryClient
{
	
	private static final Logger LOG = Logger.getLogger(ManifestRegistryClient.class);
	
	private String URL = "http://localhost:9090/manifest-registry/RegistryService";
    
    private final String OPT_ADD_SERVICE_MANIFEST = "add";
    private final String OPT_GET_SERVICE_MANIFEST = "get";
    private final String OPT_UPDATE_SERVICE_MANIFEST = "update";
    private final String OPT_REMOVE_SERVICE_MANIFEST = "remove";
    private final String OPT_GET_ALL_RESOURCES_OF_TYPE = "getAllResourcesOfType";
    
    private Client client;
    
    private static ManifestRegistryClient registryClientInstance = null;
    
    public ManifestRegistryClient(String url)
    {
    	this.URL = url;
        this.client = Client.create();
    }
    
    public static synchronized ManifestRegistryClient getInstance(String url)
    {
        if ( registryClientInstance == null )
        {
        	registryClientInstance = new ManifestRegistryClient(url);
        }

        return registryClientInstance;
    }
    
    private String getAddress(String interface_option)
    {
        return this.URL + "/" + interface_option;
    }

    public boolean add(String serviceManifest)
    {
    	
    	try {
    	
            WebResource webResourceClient = client.resource(this.getAddress(OPT_ADD_SERVICE_MANIFEST));

            /*ClientResponse response = webResourceClient.post(ClientResponse.class, serviceManifest);
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		if (success) {
    			String storedStr = response.getEntity(String.class);
    			boolean result = (storedStr.equals("true")? true : false);
    			return result;
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			throw new Exception("Failed to data document: " + error);
    		}*/
    		
            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, serviceManifest);
            
            boolean result = Boolean.parseBoolean(outcome);
            
            if(LOG.isDebugEnabled()) {
                LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_ADD_SERVICE_MANIFEST) + "': " + result);
            }
            
            return result;
            
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}	
    }
    
    public XmlBeanServiceManifestDocument get(String serviceManifestID) 
    {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_GET_SERVICE_MANIFEST));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, serviceManifestID);
            
            if(outcome.equals("false")) {
            	if(LOG.isDebugEnabled()) {
                    LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_GET_SERVICE_MANIFEST) + "': " + outcome);
                }
            	return null;
            }
            
            XmlObject xmlDoc = (XmlObject) XmlObject.Factory.parse(outcome);
            
            XmlBeanServiceManifestDocument serviceManifestDoc = (XmlBeanServiceManifestDocument) xmlDoc;

            if(LOG.isDebugEnabled()) {
                LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_GET_SERVICE_MANIFEST) + "': " + serviceManifestDoc.getServiceManifest().getManifestId());
            }
            
            return serviceManifestDoc;
            
        } catch (Exception e) {
			LOG.error(e);
			return null;
		}	
    }
    
    public boolean update(String serviceManifest) 
    {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_UPDATE_SERVICE_MANIFEST));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, serviceManifest);
            
            boolean result = Boolean.parseBoolean(outcome);

            if(LOG.isDebugEnabled()) {
                LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_UPDATE_SERVICE_MANIFEST) + "': " + result);
            }
            
            return result;
            
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}	
    }
    
    public boolean remove(String serviceManifestID) 
    {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_REMOVE_SERVICE_MANIFEST));

            String outcome = webResourceClient.type("text/plain; charset=utf-8").accept("text/plain; charset=utf-8").post(String.class, serviceManifestID);
            
            boolean result = Boolean.parseBoolean(outcome);

            if(LOG.isDebugEnabled()) {
                LOG.debug("Result of the webservice operation '" + this.getAddress(OPT_REMOVE_SERVICE_MANIFEST) + "': " + result);
            }
            
            return result;
            
        } catch (Exception e) {
			LOG.error(e);
			return false;
		}	
    }
    
    public List<XmlBeanServiceManifestDocument> getAllResourcesOfType(String resourceType) 
    {
    	
    	try {
    		
            WebResource webResourceClient = client.resource(this.getAddress(OPT_GET_ALL_RESOURCES_OF_TYPE));

    		ClientResponse response = webResourceClient.post(ClientResponse.class,resourceType);
    		
    		boolean success = (response.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL);
    		
    		ArrayList<XmlBeanServiceManifestDocument> result = new ArrayList<XmlBeanServiceManifestDocument>();
    		
    		if (success) {
    			MultivaluedMap<String, String> entries = response.getEntity(MultivaluedMap.class);
    			for (int i = 0; i < entries.size(); i++) {
    				String manifest = entries.get( "serviceManifest" ).get( i );
    				XmlBeanServiceManifestDocument manifestDoc = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse( manifest );
                    result.add(manifestDoc);
                }
    			
    		} else {
    			String error = response.getClientResponseStatus().getReasonPhrase();
    			LOG.error("error code: " + error);
    			return null;
    		}
    		
            return result;
            
        } catch (Exception e) {
			LOG.error(e);
			return null;
		}	
    }
    
}
