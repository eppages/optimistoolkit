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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import eu.optimis.manifestregistry.utils.ManifestRegistryConstants;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import junit.framework.TestCase;

/**
 * @author hrasheed
 * 
 */
public class ManifestRegistryTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(ManifestRegistryTest.class);
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testManifestRegistry() throws Exception {
    	
    	ManifestRegistry manifestRegistry = new ManifestRegistryImpl();
    	
        InputStream in = getClass().getResourceAsStream("/service_manifest.xml");
        
        if (in == null) {
            String message = "The service manifest file was not found.";
            fail(message);
        }
        
        XmlObject serviceManifestDoc = XmlObject.Factory.parse(in);
        
        XmlBeanServiceManifestDocument manifestDoc = (XmlBeanServiceManifestDocument) serviceManifestDoc;
        
        String manifestID = manifestDoc.getServiceManifest().getManifestId();
        
        boolean add = manifestRegistry.add(serviceManifestDoc);
        assertTrue(add);
        
        XmlObject xmlDoc = manifestRegistry.get(manifestID);
        assertNotNull(xmlDoc);
        XmlBeanServiceManifestDocument getManifestDoc = (XmlBeanServiceManifestDocument) xmlDoc;
        assertEquals(manifestID, getManifestDoc.getServiceManifest().getManifestId());
        
        boolean update = manifestRegistry.update(manifestDoc);
        assertTrue(update);
        
        List<XmlObject> entries = manifestRegistry.getAllResourcesOfType(ManifestRegistryConstants.SERVICE_MANIFEST);
        assertNotNull(entries);
        assertEquals(1, entries.size());
        
        boolean remove = manifestRegistry.remove(manifestID);
        assertTrue(remove);
        
        LOG.info("manifestRegistry test completed successfully");
    }
}
