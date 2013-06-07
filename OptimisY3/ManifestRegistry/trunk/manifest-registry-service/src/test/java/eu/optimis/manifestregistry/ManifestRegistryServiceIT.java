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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import eu.optimis.manifestregistry.client.ManifestRegistryClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 * 
 */
public class ManifestRegistryServiceIT extends TestCase {

    private static final Logger LOG = Logger.getLogger(ManifestRegistryServiceIT.class);
    
    private String URL = "http://localhost:9090/manifest-registry/RegistryService";
	
    @Override
    protected void setUp() {
        if(LOG.isDebugEnabled()) {
        	LOG.debug("================================================================================");
        	LOG.debug("Entering unit test: " + this.getName());
        	LOG.debug("--------------------------------------------------------------------------------");
        }
        
        LOG.info( MessageFormat.format( "manifest registry URL at {0}", new Object[] { this.URL } ) );
    }

    @Override
    protected void tearDown() {
        if(LOG.isDebugEnabled()) {
        	LOG.debug("--------------------------------------------------------------------------------");
        	LOG.debug("Leaving unit test: " + this.getName());
        	LOG.debug("================================================================================");
        }
    }
    
    public void testAddManifest() throws Exception {
        
        try {
        
        	boolean result = ManifestRegistryClient.getInstance(URL).add(getServiceManifest().toString());
            assertTrue(result);
            
        } catch (Exception e) {
        	LOG.error(e);
        	fail("testAddManifest failed: " + e.getMessage());
		}
    }
    
   public void testGetManifest() throws Exception {
        
        try {

        	XmlBeanServiceManifestDocument serviceManifestDoc = ManifestRegistryClient.getInstance(URL).get(getServiceManifest().getServiceManifest().getManifestId());
            assertNotNull(serviceManifestDoc);
            assertNotNull(serviceManifestDoc.getServiceManifest().getManifestId());
            
        } catch (Exception e) {
        	LOG.error(e);
        	fail("testGetManifest failed: " + e.getMessage());
		}
    }
    
   public void testUpdateManifest() throws Exception {
        
        try {

        	boolean result = ManifestRegistryClient.getInstance(URL).update(getServiceManifest().toString());
            assertTrue(result);
            
        } catch (Exception e) {
        	LOG.error(e);
        	fail("testUpdateManifest failed: " + e.getMessage());
		}
    }
   
    public void testGetAllResourcesOfType() throws Exception {
       
       try {

       	List<XmlBeanServiceManifestDocument> entries = ManifestRegistryClient.getInstance(URL).getAllResourcesOfType("SERVICE_MANIFEST");
           assertNotNull(entries);
           
       } catch (Exception e) {
       	LOG.error(e);
       	fail("testGetAllResourcesOfType failed: " + e.getMessage());
		}
    }
   
    public void testRemoveManifest() throws Exception {
        
        try {

        	boolean result = ManifestRegistryClient.getInstance(URL).remove(getServiceManifest().getServiceManifest().getManifestId());
            assertTrue(result);
            
        } catch (Exception e) {
        	LOG.error(e);
        	fail("testRemoveManifest failed: " + e.getMessage());
		}
    }
    
    private XmlBeanServiceManifestDocument getServiceManifest() throws Exception {
    	
    	InputStream in = getClass().getResourceAsStream("/service_manifest.xml");
        
        if (in == null) {
            String message = "The service manifest file was not found.";
            fail(message);
        }
        
        XmlBeanServiceManifestDocument serviceManifestDoc = (XmlBeanServiceManifestDocument) XmlObject.Factory.parse(in);
        
        return serviceManifestDoc;
    }

}
