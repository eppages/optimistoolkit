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

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.optimis.trustedinstance.exceptions.ResourceInvalid;
import eu.optimis.trustedinstance.exceptions.ResourceNotFound;
import eu.optimis.trustedinstance.exceptions.ResourceTypeUnknown;
import eu.optimis.trustedinstance.exceptions.TrustedInstanceException;

/**
 *  Trusted Instance Interface 
 *
 * @author hrasheed
 *
 */
public interface TrustedInstance {

	/**
     * Implementation returns license token based on a given license description file
     *
     * @param UserTokenAuthorizationDocument for the requested license token
     * 
     * @return LicenseTokenSecureDocument
     * 
     * @throws ResourceNotFound
     */
	public LicenseTokenSecureDocument getToken(UserTokenAuthorizationDocument userTokenAuthorizationDoc) throws ResourceNotFound, ResourceInvalid, TrustedInstanceException;
	
	//
	// Token Administration 
	//
	
	/**
     * Implementation stores given license token
     *
     * @param  LicenseTokenDocument type
     *
     * @return true if successful
     * 
     * @throws ResourceTypeUnknown, ResourceInvalid
     */
	public boolean storeToken(LicenseTokenDocument tokenDoc) throws ResourceTypeUnknown, ResourceInvalid, TrustedInstanceException;
	
    /**
     * Implementation removes given license token 
     *
     * @param LicenseTokenDocument tokenDoc
     * 
     * @return true if successful
     */
	public boolean removeToken(LicenseTokenDocument tokenDoc) throws ResourceNotFound, ResourceTypeUnknown, ResourceInvalid, TrustedInstanceException;
	
//	/**
//     * Implementation updates given license token based on license token ID 
//     *
//     * @param LicenseTokenDocument tokenDoc
//     * 
//     * @return true if successful
//     * 
//     */
//	public boolean updateToken(LicenseTokenDocument tokenDoc) throws ResourceNotFound, ResourceTypeUnknown, ResourceInvalid;
}
