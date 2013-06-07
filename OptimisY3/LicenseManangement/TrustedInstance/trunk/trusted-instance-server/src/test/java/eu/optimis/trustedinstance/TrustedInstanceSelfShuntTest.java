package eu.optimis.trustedinstance;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import eu.elasticlm.schemas.x2009.x05.license.token.secure.LicenseTokenSecureDocument;
import eu.elasticlm.schemas.x2009.x05.lsdl.LicenseDescriptionType;
import eu.elasticlm.schemas.x2009.x07.security.user.token.authorization.UserTokenAuthorizationDocument;
import eu.optimis.providerinfo.client.InfoServiceClient;
import eu.optimis.trustedinstance.exceptions.ResourceInvalid;
import eu.optimis.trustedinstance.exceptions.ResourceNotFound;
import eu.optimis.trustedinstance.exceptions.TrustedInstanceException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.* ;

public class TrustedInstanceSelfShuntTest extends TrustedInstanceImpl {

    private InfoServiceClient mockInfoService;
    private LicenseTokenDocument licenseTokenDocument;
    private UserTokenAuthorizationDocument userTokenAuthorizationDocument;

    @Override
    protected InfoServiceClient getInfoServiceClient(String url) {
        return mockInfoService;
    }
    
    @Before
    public void init() throws Exception {
        mockInfoService = mock(InfoServiceClient.class);
        when(mockInfoService.getInfo()).thenReturn("INFO_SERVICE_SAMPLE");
        licenseTokenDocument = LicenseTokenDocument.Factory.parse(new File("src/test/resources/sample-token-normal.xml"));
        userTokenAuthorizationDocument = UserTokenAuthorizationDocument.Factory.parse(new File("src/test/resources/sample-token-user-authorization.xml"));
    }

    @Test
    public void storeToken_shouldRun() throws Exception {
        assertTrue(storeToken(licenseTokenDocument));
    }

    @Test(expected = TrustedInstanceException.class)
    public void storeToken_shouldThrow_ifTokenAlreadyInStorage() throws Exception {
        storeToken(licenseTokenDocument);
        storeToken(licenseTokenDocument);
    }

    @Test(expected = ResourceInvalid.class)
    public void storeToken_shouldThrow_ifTokenMissingID() throws Exception {
        LicenseTokenDocument missingId = licenseTokenDocument;
        missingId.getLicenseToken().setTokenId("");
        storeToken(missingId);
    }

    @Test
    public void getToken_shouldReturnCorrectToken() throws Exception {
        LicenseTokenSecureDocument receivedToken = getToken(userTokenAuthorizationDocument);
        assertTrue(receivedToken.validate());
    }

    @Test
     public void removeToken_shouldRemoveToken() throws Exception {
        removeToken(licenseTokenDocument) ;
    }

    @Test(expected = TrustedInstanceException.class)
     public void removeToken_shouldThrow_ifTokenNotInStorage() throws Exception {
        removeToken(licenseTokenDocument) ;
    }
}
