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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.optimis.manifestregistry.utils.ManifestRegistryConstants;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;


/**
 * @author hrasheed
 * 
 */
@Path(value = "/RegistryService")
public class ManifestRegistryService {

    private static final Logger LOG = Logger.getLogger(ManifestRegistryService.class);
    
    public ManifestRegistryService()
    {
        LOG.info("Initialized new Manifest registry service");
    }

    @POST
    @Path("add")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String add(String serviceManifest)
    {
    	try {
    		XmlBeanServiceManifestDocument serviceManifestDoc = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse(serviceManifest);
    		ManifestRegistryImpl.getInstance().add(serviceManifestDoc);
    	} catch( Exception e) {
    		LOG.error("error in adding service manifest: " + e.getMessage(), e);
    		return Boolean.toString(false);
    	} 
    	
        return Boolean.toString(true);
    }
    
    @POST
    @Path("get")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String get(String serviceManifestID)
    {
    	XmlBeanServiceManifestDocument serviceManifestDoc = null;
    	try {
    		XmlObject xmlManifestDoc = (XmlObject) ManifestRegistryImpl.getInstance().get(serviceManifestID);
    		if(xmlManifestDoc == null) {
    			return Boolean.toString(false);
    		}
    		serviceManifestDoc = (XmlBeanServiceManifestDocument) xmlManifestDoc;
    	} catch( Exception e) {
    		LOG.error("error in getting service manifest: " + e.getMessage(), e);
    		return Boolean.toString(false);
    	} 
        return serviceManifestDoc.xmlText();
    }

    @POST
    @Path("update")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String update(String serviceManifest)
    {
    	try {
    		XmlBeanServiceManifestDocument serviceManifestDoc = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse(serviceManifest);
    		ManifestRegistryImpl.getInstance().update(serviceManifestDoc);
    	} catch( Exception e) {
    		LOG.error("error in updating service manifest: " + e.getMessage(), e);
    		return Boolean.toString(false);
    	} 
    	
        return Boolean.toString(true);
    }
    
    @POST
    @Path("remove")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String remove(String serviceManifestID)
    {
    	try {
    		ManifestRegistryImpl.getInstance().remove(serviceManifestID);
    	} catch( Exception e) {
    		LOG.error("error in removing service manifest: " + e.getMessage(), e);
    		return Boolean.toString(false);
    	} 
    	
        return Boolean.toString(true);
    }

    @POST
    @Path("getAllResourcesOfType")
    @Consumes(value = "text/plain")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public MultivaluedMap<String, String> getAllResourcesOfType(String type)
    {
    	
    	MultivaluedMap<String, String> manifestParams = new MultivaluedMapImpl();
    	
    	try {
    		
    		List<XmlObject> entries = ManifestRegistryImpl.getInstance().getAllResourcesOfType(ManifestRegistryConstants.SERVICE_MANIFEST);
    	
    		if(entries == null) {
    			entries = new ArrayList<XmlObject>();
    		}
    		
    		XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();
            
    		for (int i = 0; i < entries.size(); i++) {
    			manifestParams.add( "serviceManifest", entries.get(i).xmlText(xmlOptions) );
				
			}
    		
    	} catch( Exception e) {
    		LOG.error("error in listing resources of particular type: " + e.getMessage(), e);
    	} 
    	
        return manifestParams; 
    }
}
