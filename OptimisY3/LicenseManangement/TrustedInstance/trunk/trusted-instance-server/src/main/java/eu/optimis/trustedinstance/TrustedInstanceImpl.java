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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureType;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.ProviderHashType;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.ProviderInfoType;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.elasticlm.security.tokens.TokenSigner;
import eu.optimis.providerinfo.client.InfoServiceClient;
import eu.optimis.trustedinstance.exceptions.ResourceInvalid;
import eu.optimis.trustedinstance.exceptions.ResourceNotFound;
import eu.optimis.trustedinstance.exceptions.ResourceTypeUnknown;

import eu.optimis.trustedinstance.exceptions.TrustedInstanceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.util.Base64;

/**
 * @author hrasheed
 * 
 */
public class TrustedInstanceImpl implements TrustedInstance {

    private static final Logger LOG = Logger.getLogger(TrustedInstanceImpl.class);

    private DBStorage storage = null;
    
    private static TrustedInstanceImpl trustedInstance = null;
    
    private InputStream keyStore_input_stream = null;
    
    private char[] ti_keyStorePass = null;
    
    private String ti_keyStoreAlias = null;
    
    private InputStream publicCert_input_stream = null;

    private DBStorage dbStorage = null;
    
    private String infoServiceUrl = null ;
    
    private String infoServiceName = null ;

    private X509Certificate ti_certificate = null;

    private KeyStore ti_ks = null ;
    
    private synchronized void initialize()
    {
        //storage = new DBStorage();
    	
    	String keystore = ComponentConfigurationProvider.getString("trusted.instance.keystore"); //$NON-NLS-1$
    	String password = ComponentConfigurationProvider.getString("trusted.instance.keystore.password"); //$NON-NLS-1$
    	String alias = ComponentConfigurationProvider.getString("trusted.instance.keystore.alias"); //$NON-NLS-1$
    	String publicCert = ComponentConfigurationProvider.getString("trusted.instance.keystore.public.cert"); //$NON-NLS-1$
        infoServiceName = ComponentConfigurationProvider.getString("trusted.instance.infoservice.client.name");
        infoServiceUrl = ComponentConfigurationProvider.getString("trusted.instance.infoservice.client.url") ;
    	
    	try {
    		
    		keyStore_input_stream = getClass().getResourceAsStream(keystore);
            
            if (keyStore_input_stream == null) {
                throw new Exception("unable to load keystore of the trusted instance");    
            }
            
            if (password == null) {
                throw new Exception("unable to load passowrd of the keystore");    
            }
            
            ti_keyStorePass = password.toCharArray();
            
            if (alias == null) {
                throw new Exception("unable to load alias of the keystore");    
            }
            
            ti_keyStoreAlias = alias;
            
            publicCert_input_stream = getClass().getResourceAsStream(publicCert);

            if (publicCert_input_stream == null) {
                throw new Exception("unable to load public certificate of the trusted instance");
            }

            CertificateFactory ti_cf = CertificateFactory.getInstance("X.509");
            ti_certificate = (X509Certificate) ti_cf.generateCertificate(publicCert_input_stream);

            ti_ks = KeyStore.getInstance("JKS");
            ti_ks.load(keyStore_input_stream, ti_keyStorePass);

            if(infoServiceName == null) {
                throw new Exception("unable to find name for ProviderInfoService");
            }

            if(infoServiceUrl== null) {
                throw new Exception("unable to find URL for ProviderInfoService");
            }
            
    	} catch(Exception e) {
    		System.out.println("ERROR: " + e.getMessage());
    	}
    }
    
    public TrustedInstanceImpl() {
        dbStorage = new DBStorage() ;
    	initialize();
    }
    
    public static synchronized TrustedInstanceImpl getInstance()
    {
        if ( trustedInstance == null )
        {
        	trustedInstance = new TrustedInstanceImpl();
        }

        return trustedInstance;
    }

    @Override
    public LicenseTokenSecureDocument getToken(UserTokenAuthorizationDocument userTokenAuthorizationDoc) throws ResourceNotFound, ResourceInvalid, TrustedInstanceException {
        if(userTokenAuthorizationDoc == null)
            throw new ResourceInvalid("Resource is null") ;


        // TODO validate timestamps and signature of userTokenAuthorizationDoc?
        // TODO get tokenID from UserTokenAuthorizationDocument or LicenseDescription?
        String tokenId = userTokenAuthorizationDoc.getUserTokenAuthorization().getTokenID();

        if(tokenId == null || tokenId.equals(""))
            throw new ResourceInvalid("UserTokenAuthorization is missing TokenID") ;

        DBStorageEntry receivedEntry = null;
        try {
            receivedEntry = getStorageEntry(tokenId);
        } catch (Exception e) {
            throw new TrustedInstanceException(e.getMessage(), e);
        }

        LicenseTokenSecureDocument licenseTokenSecureDocument;
        try {
            LicenseTokenDocument licenseToken = receivedEntry.getLicenseToken() ;

            LicenseTokenSecureDocument tokenSecDoc = LicenseTokenSecureDocument.Factory.newInstance();
            LicenseTokenSecureType tokenSecType =  tokenSecDoc.addNewLicenseTokenSecure();

            ByteArrayOutputStream token_bos = new ByteArrayOutputStream();
            licenseToken.save(token_bos);
            byte[] token_encoded = Base64.encode(token_bos.toByteArray());

            tokenSecType.setLicenseToken(token_encoded);

            ProviderInfoType providerInfo = tokenSecType.addNewProviderInfo();
            String providerInfoString = getProviderInfo(infoServiceUrl);
            providerInfo.setName(infoServiceName);
            ProviderHashType hashType = providerInfo.addNewProviderHash();
            hashType.setHashAlgorithm( "sha1" );
            hashType.setStringValue( DigestUtils.shaHex(providerInfoString));
            providerInfo.setInfoServiceURL(infoServiceUrl);

            // server private key
            PrivateKey tiPrivateKey = (PrivateKey) ti_ks.getKey(ti_keyStoreAlias, ti_keyStorePass);

            TokenSigner ts_secure = new TokenSigner(ti_certificate, tiPrivateKey);

            licenseTokenSecureDocument = ts_secure.getSignedToken(tokenSecDoc);

            if (!licenseTokenSecureDocument.validate()) {
                throw new ResourceInvalid("invalid trusted instance token authorization.");
            }
        } catch (XmlException e) {
            // TODO add throws TrustedInstanceException?
            throw new ResourceNotFound("Unable to parse LicenseToken", e);
        } catch (Exception e) {
            throw new TrustedInstanceException(e.getMessage(), e);
        }

        return licenseTokenSecureDocument;
    }

    private String getProviderInfo(String URL) throws TrustedInstanceException {
        InfoServiceClient infoServiceClient = getInfoServiceClient(URL);
        String info = infoServiceClient.getInfo();
        if(info == null || info.equals(""))
            throw new TrustedInstanceException("InfoServiceClient does not return Info-String.");
        return info ;
    }
    
    protected InfoServiceClient getInfoServiceClient(String url) {
        return InfoServiceClient.getInstance(url) ;    
    }
    
    @Override
    public boolean storeToken(LicenseTokenDocument resource) throws ResourceTypeUnknown, ResourceInvalid, TrustedInstanceException
    {
        String id = getIdFromToken(resource) ;
        try {
            if(dbStorage.get(id) != null) {
                LOG.error("Token with ID=" + id + " already in store");
                throw new TrustedInstanceException("Token already in store");
            }
        } catch (Exception e) {
            throw new TrustedInstanceException(e.getMessage(), e);
        }

        DBStorageEntry dbStorageEntry ;
        try {
            dbStorageEntry = new DBStorageEntry(id, resource);
        } catch (IOException e) {
            throw new TrustedInstanceException("Unable to store resource", e);
        }

        try {
            return dbStorage.store(dbStorageEntry);
        } catch (Exception e) {
            throw new TrustedInstanceException("Unable to store", e);
        }
    }
    
    @Override
    public boolean removeToken(LicenseTokenDocument tokenDoc) throws ResourceNotFound, ResourceTypeUnknown, ResourceInvalid, TrustedInstanceException {
        String id = getIdFromToken(tokenDoc) ;
        try {
            getStorageEntry(id);
            return dbStorage.delete(id);
        } catch (Exception e) {
            throw new TrustedInstanceException(e.getMessage(), e);
        }
    }

    private DBStorageEntry getStorageEntry(String id) throws ResourceNotFound, Exception {
        DBStorageEntry dbStorageEntry = dbStorage.get(id);
        if(dbStorageEntry == null)
            throw new ResourceNotFound("Token not present in Storage") ;
        return dbStorageEntry ;
    }

    private String getIdFromToken(LicenseTokenDocument token) throws ResourceInvalid {
        // check license
        if( !token.validate())
            throw new ResourceInvalid("Token can not be validated") ;

        // check if license has a TokenID
        String id = token.getLicenseToken().getTokenId();
        if(id == null || id.equals("")) {
            throw new ResourceInvalid("ID missing in Token") ;
        } 
        return id ;
    } 
}
