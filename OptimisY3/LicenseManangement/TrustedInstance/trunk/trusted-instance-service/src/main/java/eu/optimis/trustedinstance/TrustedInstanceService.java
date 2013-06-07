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

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.elasticlm.schemas.x2010.x01.token.envelope.TokenEnvelopeDocument;
import eu.optimis.trustedinstance.TrustedInstanceImpl;

import javax.ws.rs.*;

/**
 * @author hrasheed
 * @author s.reiser
 * 
 */
@Path(value = "/TokenService")
public class TrustedInstanceService {

    private static final Logger LOG = Logger.getLogger(TrustedInstanceService.class);
    
    public TrustedInstanceService()
    {
        LOG.debug("initializing new instance of trusted instance service");
    }
    
    @POST
    @Path("getToken")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String getToken(String userTokenAuthorization)
    {
    	try {
    		TokenEnvelopeDocument userAuthEnvelopedDoc = (TokenEnvelopeDocument) XmlObject.Factory.parse(userTokenAuthorization);
    		UserTokenAuthorizationDocument userTokenAuthorizationDoc = UserTokenAuthorizationDocument.Factory.parse(new ByteArrayInputStream(userAuthEnvelopedDoc.getTokenEnvelope().getEncodedToken()));
    		LicenseTokenSecureDocument tokenTrustedDoc = TrustedInstanceImpl.getInstance().getToken(userTokenAuthorizationDoc);
    		if(tokenTrustedDoc == null) {
    			throw new Exception(" trusted token authorization is null");
    		}
    		TokenEnvelopeDocument tokenEnvelopedDoc = TokenEnvelopeDocument.Factory.newInstance();
    		tokenEnvelopedDoc.addNewTokenEnvelope().setEncodedToken(tokenTrustedDoc.xmlText().getBytes());
    		return tokenEnvelopedDoc.toString();
    	} catch( Exception e) {
    		LOG.error("error in getting trusted instance token authorization: " + e.getMessage(), e);
    		return e.getMessage();
    	} 
    }
    
    @POST
    @Path("storeToken")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String storeToken(String licenseTokenDocument)
    {
        boolean result = false ;
    	try {
    		TokenEnvelopeDocument tokenEnvelopedDoc = (TokenEnvelopeDocument) XmlObject.Factory.parse(licenseTokenDocument);
    		LicenseTokenDocument tokenDoc = LicenseTokenDocument.Factory.parse(new ByteArrayInputStream(tokenEnvelopedDoc.getTokenEnvelope().getEncodedToken()));
    		result = TrustedInstanceImpl.getInstance().storeToken(tokenDoc);
    	} catch( Exception e) {
    		LOG.error("error in ading token document: " + e.getMessage(), e);
            return e.getMessage() ;
    		//return Boolean.toString(false);
    	}

        return String.valueOf(result);
    }
    
    @POST
    @Path("removeToken")
    @Consumes(value = "text/plain")
    @Produces(value = "text/plain")
    public String removeToken(String licenseTokenDocument)
    {
        boolean result = false ;
    	try {
    		TokenEnvelopeDocument tokenEnvelopedDoc = (TokenEnvelopeDocument) XmlObject.Factory.parse(licenseTokenDocument);
    		LicenseTokenDocument tokenDoc = LicenseTokenDocument.Factory.parse(new ByteArrayInputStream(tokenEnvelopedDoc.getTokenEnvelope().getEncodedToken()));
    		result = TrustedInstanceImpl.getInstance().removeToken(tokenDoc);
    	} catch( Exception e) {
    		LOG.error("error in removing token document: " + e.getMessage(), e);
    		return e.getMessage() ;
    		//return Boolean.toString(false);
    	} 
    	
        return String.valueOf(result);
    }
}
