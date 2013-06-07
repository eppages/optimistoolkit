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
package eu.optimis.trustedinstance;

import java.io.InputStream;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.optimis.trustedinstance.client.TrustedInstanceClient;

/**
 * @author hrasheed
 * 
 */
public class TrustedInstanceServiceIT extends TestCase {

    private static final Logger LOG = Logger.getLogger(TrustedInstanceServiceIT.class);
    
    //private String URL = "http://localhost:9090/trusted-instance/TokenService";
    private String URL = "http://localhost:8080/trusted-instance/TokenService";

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

    public void testStoreToken() throws Exception {

        try {

            boolean result = TrustedInstanceClient.getInstance(URL).storeToken(getLicenseTokenDoc());
            assertTrue(result);

        } catch (Exception e) {
            LOG.error(e);
            fail("testStoreToken failed: " + e.getMessage());
        }
    }

    public void testGetToken() throws Exception {

        try {

        	LicenseTokenSecureDocument tokenTrustedDoc = TrustedInstanceClient.getInstance(URL).getToken(getUserAuthorizationDoc());
            assertNotNull(tokenTrustedDoc);

        } catch (Exception e) {
        	LOG.error(e);
        	fail("testGetToken failed: " + e.getMessage());
		}
    }

    public void testRemoveToken() throws Exception {

        try {

        	boolean result = TrustedInstanceClient.getInstance(URL).removeToken(getLicenseTokenDoc());
            assertTrue(result);

        } catch (Exception e) {
        	LOG.error(e);
        	fail("testRemoveToken failed: " + e.getMessage());
		}
    }

    private LicenseTokenDocument getLicenseTokenDoc() throws Exception {

    	InputStream in = getClass().getResourceAsStream("/sample-token-normal.xml");

        if (in == null) {
            String message = "The license token file was not found.";
            fail(message);
        }

        LicenseTokenDocument tokenDoc = (LicenseTokenDocument) XmlObject.Factory.parse(in);

        return tokenDoc;
    }

    private UserTokenAuthorizationDocument getUserAuthorizationDoc() throws Exception {

    	InputStream in = getClass().getResourceAsStream("/sample-token-user-authorization.xml");

        if (in == null) {
            String message = "The user token authorization file was not found.";
            fail(message);
        }

        UserTokenAuthorizationDocument userTokenAuthorizationDoc = (UserTokenAuthorizationDocument) XmlObject.Factory.parse(in);

        return userTokenAuthorizationDoc;
    }
}
