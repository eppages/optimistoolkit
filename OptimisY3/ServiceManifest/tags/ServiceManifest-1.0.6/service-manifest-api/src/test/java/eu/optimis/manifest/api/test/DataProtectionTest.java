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

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;

/**
 * @author arumpl
 */
public class DataProtectionTest extends AbstractTestApi
{

    /**
     *
     */
    private static final int DEFAULT_CAPACITY = 1234;

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
        // at the beginning data storage is 0
        assertEquals( 0, getManifest().getDataProtectionSection().getDataStorage().getCapacity() );
        // and unit is byte
        assertEquals( "byte", getManifest().getDataProtectionSection().getDataStorage().getAllocationUnit() );

        // when we set unit and bytes
        getManifest().getDataProtectionSection().getDataStorage().setCapacity( DEFAULT_CAPACITY );
        getManifest().getDataProtectionSection().getDataStorage().setAllocationUnit( "byte * 2^10" );

        // then those values must be set correctly
        assertEquals( DEFAULT_CAPACITY, getManifest().getDataProtectionSection().getDataStorage()
                                                     .getCapacity() );
        assertEquals( "byte * 2^10", getManifest().getDataProtectionSection().getDataStorage()
                                                  .getAllocationUnit() );
    }
}
