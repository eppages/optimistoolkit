package eu.optimis.trustedinstance;

import eu.elasticlm.schemas.x2009.x05.license.token.LicenseTokenDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author s.reiser
 */
public class DBStorageTest {
    
    private final String SAMPLE_KEY = "SAMPLE_KEY" ;

    private DBStorage dbStorage ;
    private DBStorageEntry sampleEntry ;

    @Before
    public void init() throws Exception {
        dbStorage = new DBStorage();
        LicenseTokenDocument licenseTokenDocument = LicenseTokenDocument.Factory.parse(new File("src/test/resources/sample-token-normal.xml"));
        sampleEntry = new DBStorageEntry(SAMPLE_KEY, licenseTokenDocument);
    }

    @Test
    public void test_storeAndReadEntry() throws Exception {
        assertTrue(dbStorage.store(sampleEntry));
        DBStorageEntry returnedEntry = dbStorage.get(SAMPLE_KEY);
        assertTrue(returnedEntry.getLicenseToken().validate());
        assertTrue(dbStorage.delete(SAMPLE_KEY));
        returnedEntry = dbStorage.get(SAMPLE_KEY) ;
        assertNull(returnedEntry);
    }

    @Test
    public void test_getNonExistingEntry() throws Exception {
        DBStorageEntry dbStorageEntry = dbStorage.get(SAMPLE_KEY);
        assertNull(dbStorageEntry);
    }
}
