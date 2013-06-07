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
import eu.optimis.manifestregistry.utils.ComponentConfigurationProvider;
import eu.optimis.manifestregistry.utils.ManifestRegistryConstants;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hrasheed
 * 
 */
public class ManifestRegistryImpl implements ManifestRegistry {

    private static final Logger LOG = Logger.getLogger(ManifestRegistryImpl.class);

    private DBStorage storage = null;
    
    private static ManifestRegistryImpl registryInstance = null;
    
    private synchronized void initialize()
    {
        storage = new DBStorage();
    }
    
    public ManifestRegistryImpl() {
    	initialize();
    }
    
    public static synchronized ManifestRegistryImpl getInstance()
    {
        if ( registryInstance == null )
        {
        	registryInstance = new ManifestRegistryImpl();
        }

        return registryInstance;
    }
    
    @Override
    public boolean add(XmlObject resource) throws ResourceTypeUnknown, ResourceInvalid
    {
    	
        try {
        	
        	LOG.info( "adding service manifest document" );
        	
        	XmlBeanServiceManifestDocument serviceManifestDoc = null;
        	
        	try {
        		serviceManifestDoc = (XmlBeanServiceManifestDocument) resource;
        	} catch(Exception e) {
        		LOG.error(e);
        		throw new ResourceTypeUnknown("resource type unknown");
        	}
        	
        	boolean xmlValidation = ComponentConfigurationProvider.getBoolean("manifest.registry.schema.validation", false); //$NON-NLS-1$
        	
        	if(xmlValidation) {
        		List<Object> errorList = new ArrayList<Object>();
                
                if (!serviceManifestDoc.validate(new XmlOptions().setErrorListener(errorList))) {
                    for (Object error : errorList) {
                        LOG.error(error);
                    }
                    throw new ResourceInvalid("service manifest document doesn't validate against XML schema.");
                }
        	}
            
            String key =  serviceManifestDoc.getServiceManifest().getManifestId();
            DBStorageEntry addEntry = new DBStorageEntry(key, ManifestRegistryConstants.SERVICE_MANIFEST, serviceManifestDoc.xmlText());
            
            if (!storage.store(addEntry)) {
                throw new Exception("[add] service manifest document could not be stored.");
            }

            LOG.info( "service manifest added successfully with manifestID: " + key );
            
        } catch (Exception e) {
        	e.printStackTrace();
        	LOG.error(e);
        	return false;
        }
        
        return true;
    }
    
    @Override
    public XmlObject get(String resourceID) throws ResourceNotFound
    {
    	XmlObject xmlDoc = null;
    	
        try {
        	
        	LOG.info( "getting service manifest document" );
        	
        	DBStorageEntry entry = storage.get(resourceID);
            
            if (entry == null) {
                throw new ResourceNotFound("[get] service manifest document could not be found for manifestID: " + resourceID);
            }

            xmlDoc = XmlObject.Factory.parse(entry.getValue());
            
            LOG.info( "service manifest retrieved successfully for manifestID: " + resourceID );
            
        } catch (Exception e) {
        	e.printStackTrace();
        	LOG.error(e);
        	return null;
        }
        
        return xmlDoc;
    }

    @Override
    public boolean update(XmlObject resource) throws ResourceNotFound, ResourceTypeUnknown, ResourceInvalid
    {
    	
        try {
        	
        	LOG.info( "updating service manifest document" );
        	
        	XmlBeanServiceManifestDocument serviceManifestDoc = null;
        	
        	try {
        		serviceManifestDoc = (XmlBeanServiceManifestDocument) resource;
        	} catch(Exception e) {
        		LOG.error(e);
        		throw new ResourceTypeUnknown("resource type unknown");
        	}
        	
            boolean xmlValidation = ComponentConfigurationProvider.getBoolean("manifest.registry.schema.validation", false); //$NON-NLS-1$
        	
        	if(xmlValidation) {
        		List<Object> errorList = new ArrayList<Object>();
                
                if (!serviceManifestDoc.validate(new XmlOptions().setErrorListener(errorList))) {
                    for (Object error : errorList) {
                        LOG.error(error);
                    }
                    throw new ResourceInvalid("service manifest document doesn't validate against XML schema.");
                }
        	}
            
            String key =  serviceManifestDoc.getServiceManifest().getManifestId();
            DBStorageEntry updateEntry = new DBStorageEntry(key, ManifestRegistryConstants.SERVICE_MANIFEST, serviceManifestDoc.xmlText());
            
            if (!storage.update(updateEntry)) {
                throw new ResourceNotFound("[update] service manifest document could not be updated.");
            }

            LOG.info( "service manifest updated successfully for manifestID: " + key );
            
        } catch (Exception e) {
        	e.printStackTrace();
        	LOG.error(e);
        	return false;
        }
        
        return true;
    }
    
    @Override
    public boolean remove(String resourceID) throws ResourceNotFound
    {
    	
        try {
        	
        	LOG.info( "removing service manifest document" );
        	
        	if (!storage.delete(resourceID)) {
                throw new ResourceNotFound("[update] resource with ID [" + resourceID + "] does not exist.");
            }
                   
            LOG.info( "service manifest removed successfully with manifestID: " + resourceID );
            
        } catch (Exception e) {
        	LOG.error(e);
        	return false;
        }
        
    	return true;
    }

    @Override
    public List<XmlObject> getAllResourcesOfType(int type) throws ResourceTypeUnknown
    {
    	
    	LOG.info( "getting all resources of type: " + type );
    	
    	if (type != ManifestRegistryConstants.SERVICE_MANIFEST) {
            throw new ResourceTypeUnknown("unknown resource type: " + type);
        }
    	
        ArrayList<XmlObject> result = new ArrayList<XmlObject>();
        
        List<DBStorageEntry> entrys = storage.filterByType(type);
        
        if (entrys == null) {
            throw new ResourceTypeUnknown("[getAllResourcesOfType] no entries found for document type: " + type);
        }
        
        try {
        	
        	for (int i = 0; i < entrys.size(); i++) {
            	XmlObject xmlDoc = XmlObject.Factory.parse(entrys.get(i).getValue());
                result.add(xmlDoc);
            }
        	
        	LOG.info( "number of entries successfully retrieved: " + result.size() );
        	
        } catch(Exception e) {
        	e.printStackTrace();
        	LOG.error(e);
        	return null;
        }
        
        return result;
    }
}
