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
package eu.optimis.providerinfo;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import eu.optimis.providerinfo.client.InfoServiceClient;

/**
 * @author hrasheed
 * 
 */
public class ProviderInfoServiceIT extends TestCase {

    private static final Logger LOG = Logger.getLogger(ProviderInfoServiceIT.class);
    
    //private String URL = "http://localhost:7070/provider-info/Service";
    private String URL = "http://localhost:8080/provider-info/Service";

    @Override
    protected void setUp() {
        if(LOG.isDebugEnabled()) {
        	LOG.debug("================================================================================");
        	LOG.debug("Entering unit test: " + this.getName());
        	LOG.debug("--------------------------------------------------------------------------------");
        }
    }

    @Override
    protected void tearDown() {
        if(LOG.isDebugEnabled()) {
        	LOG.debug("--------------------------------------------------------------------------------");
        	LOG.debug("Leaving unit test: " + this.getName());
        	LOG.debug("================================================================================");
        }
    }
    
    public void testGetInfo() throws Exception {
        
        try {

        	String infoString = InfoServiceClient.getInstance(URL).getInfo();
            assertNotNull(infoString);
            System.out.println(infoString);
        } catch (Exception e) {
        	LOG.error(e);
        	fail("testGetInfo failed: " + e.getMessage());
		}
    }
}
