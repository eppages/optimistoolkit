/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
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
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.sp.BCR;
import eu.optimis.manifest.api.sp.DataStorage;
import eu.optimis.manifest.api.sp.IPR;
import eu.optimis.manifest.api.sp.LegalItemSection;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.SCC;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;

/**
 * @author arumpl
 */
public class DataProtectionTest extends AbstractTestApi
{

    public void testDataProtectionLevelIsDPA()
    {
        assertEquals( "Data Protection Level is not DPA;", XmlBeanDataProtectionLevelType.DPA.toString(),
            getManifest().getDataProtectionSection().getDataProtectionLevel() );

        getManifest().getDataProtectionSection().setDataProtectionLevel( "None" );
        assertEquals( XmlBeanDataProtectionLevelType.NONE.toString(),
            getManifest().getDataProtectionSection().getDataProtectionLevel() );
    }

    public void testShouldGetAndSetDataEncryptionAlgorithm()
    {
        assertEquals( "Data Encryption Algorithm is not AES;", XmlBeanEncryptionAlgoritmType.AES.toString(),
            getManifest().getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm() );

        getManifest().getDataProtectionSection().getDataEncryptionLevel()
                     .setEncryptionAlgorithm( XmlBeanEncryptionAlgoritmType.AES_TWOFISH.toString() );
        assertEquals( XmlBeanEncryptionAlgoritmType.AES_TWOFISH.toString(),
            getManifest().getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm() );
    }

    public void testShouldGetAndSetKeySize()
    {
        getManifest().getDataProtectionSection().getDataEncryptionLevel().setEncryptionKeySize( 1 );
        assertEquals( 1, getManifest().getDataProtectionSection().getDataEncryptionLevel()
                                      .getEncryptionKeySize() );
    }

    public void testShouldGetCustomEncryptionLevel()
    {
        getManifest().getDataProtectionSection().getDataEncryptionLevel().getCustomEncryptionLevel();
    }

    public void testAddRemoveEligibleCountries()
    {
        getManifest().getDataProtectionSection().addNewEligibleCountry( "DE" );
        getManifest().getDataProtectionSection().addNewEligibleCountry( "UK" );

        assertEquals( 2, getManifest().getDataProtectionSection().getEligibleCountryList().length );
        getManifest().getDataProtectionSection().removeEligibleCountry( "DE" );
        assertEquals( 1, getManifest().getDataProtectionSection().getEligibleCountryList().length );
    }

    public void testAddRemoveNonEligibleCountries()
    {
        getManifest().getDataProtectionSection().addNewNonEligibleCountry( "DE" );
        getManifest().getDataProtectionSection().addNewNonEligibleCountry( "UK" );

        assertEquals( 2, getManifest().getDataProtectionSection().getNonEligibleCountryList().length );
        getManifest().getDataProtectionSection().removeNonEligibleCountry( "DE" );
        assertEquals( 1, getManifest().getDataProtectionSection().getNonEligibleCountryList().length );
    }

    public void testSetDataStorage()
    {
    	
    	assertEquals(1, getManifest().getDataProtectionSection().getDataStorageArray().length);
    	
    	assertEquals( "storage", getManifest().getDataProtectionSection().getDataStorageArray(0).getName() );
        assertEquals( 0, getManifest().getDataProtectionSection().getDataStorageArray(0).getCapacity() );
        assertEquals( "byte", getManifest().getDataProtectionSection().getDataStorageArray(0).getAllocationUnit() );
        
        DataStorage storage_1 = getManifest().getDataProtectionSection().addNewDataStorage( "jboss", "jboss-test-storage-1" );
    	storage_1.setCapacity( 1234 );
    	storage_1.setAllocationUnit( "byte * 2^10" );
    	
    	DataStorage storage_2 = getManifest().getDataProtectionSection().addNewDataStorage( new String[] {"jboss"}, "jboss-test-storage-2" );
    	storage_2.setCapacity( 12345 );
    	storage_2.setAllocationUnit( "byte * 2^20" );
    	
    	assertEquals(3, getManifest().getDataProtectionSection().getDataStorageArray().length);
    	
    	assertNotNull(getManifest().getDataProtectionSection().getDataStorage( "jboss-test-storage-1" ));
    	
    	getManifest().getDataProtectionSection().removeDataStorage( "jboss-test-storage-2" );
    	
    	assertEquals(2, getManifest().getDataProtectionSection().getDataStorageArray().length);
    	
    	getManifest().getDataProtectionSection().removeDataStorage( 1 );
    	getManifest().getDataProtectionSection().removeDataStorage( 0 );
    	
    	assertEquals(0, getManifest().getDataProtectionSection().getDataStorageArray().length);
    	
    }
    
    public void testLegalElements()
    {
    	
    	//
    	// SCC
    	//
    	assertNotNull(getManifest().getDataProtectionSection().getSCC());
    	
    	SCC scc = getManifest().getDataProtectionSection().getSCC();
    	scc.isSCCEnabled();
    	scc.enableSCC();
    	scc.disableSCC();
    	scc.setLocation("url");
    	scc.setDescription("description");
    	assertEquals("url", scc.getLocation());
    	assertEquals("description", scc.getDescription());
    	
    	LegalItemSection section = scc.addStandardContractualClause();
    	section.setTitle("title");
    	section.setDescription("description");
    	section.addItem("item1");
    	section.addItem("item2");
    	section.addItem("item3");
    	assertEquals("title", section.getTitle());
    	assertEquals("description", section.getDescription());
    	assertEquals(3, section.getItems().length);
    	section.removeItem(1);
    	assertEquals(2, section.getItems().length);
    	
    	//
    	// BCR
    	//
        assertNotNull(getManifest().getDataProtectionSection().getSCC());
    	
    	BCR bcr = getManifest().getDataProtectionSection().getBCR();
    	bcr.isBCREnabled();
    	bcr.enableBCR();
    	bcr.disableBCR();
    	bcr.setLocation("url");
    	bcr.setDescription("description");
    	assertEquals("url", bcr.getLocation());
    	assertEquals("description", bcr.getDescription());
    	
    	LegalItemSection bcrSection = bcr.addBindingContactualRule();
    	bcrSection.setTitle("title");
    	bcrSection.setDescription("description");
    	bcrSection.addItem("item1");
    	bcrSection.addItem("item2");
    	assertEquals("title", bcrSection.getTitle());
    	assertEquals("description", bcrSection.getDescription());
    	assertEquals(2, bcrSection.getItems().length);
    	bcrSection.removeItem(1);
    	assertEquals(1, bcrSection.getItems().length);
    	
    	//
    	// IPR
    	//
        assertNotNull(getManifest().getDataProtectionSection().getIPR());
    	
        IPR ipr = getManifest().getDataProtectionSection().getIPR();
        ipr.isIPREnabled();
    	ipr.enableIPR();
    	ipr.disableIPR();
    	ipr.setLocation("url");
    	ipr.setDescription("description");
    	assertEquals("url", ipr.getLocation());
    	assertEquals("description", ipr.getDescription());
    	
    	LegalItemSection iprSection = ipr.addIntellectualPropertyRule();
    	iprSection.setTitle("title");
    	iprSection.setDescription("description");
    	iprSection.addItem("item1");
    	iprSection.addItem("item2");
    	assertEquals("title", iprSection.getTitle());
    	assertEquals("description", iprSection.getDescription());
    	assertEquals(2, iprSection.getItems().length);
    	iprSection.removeItem(1);
    	assertEquals(1, iprSection.getItems().length);
    	
    	System.out.println(getManifest().getDataProtectionSection().toString());
    }
}
