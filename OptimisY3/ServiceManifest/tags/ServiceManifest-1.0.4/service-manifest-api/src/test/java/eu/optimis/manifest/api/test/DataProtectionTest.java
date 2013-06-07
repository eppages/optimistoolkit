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
 */

package eu.optimis.manifest.api.test;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;


/**
 * @author arumpl
 */
public class DataProtectionTest extends AbstractTestApi {


    public void testDataProtectionLevelIsDPA() {
        assertEquals("Data Protection Level is not DPA;", XmlBeanDataProtectionLevelType.DPA.toString(), manifest.getDataProtectionSection().getDataProtectionLevel());

        manifest.getDataProtectionSection().setDataProtectionLevel("None");
        assertEquals(XmlBeanDataProtectionLevelType.NONE.toString(), manifest.getDataProtectionSection().getDataProtectionLevel());

    }

    public void testShouldGetAndSetDataEncryptionAlgorithm() {
        assertEquals("Data Encryption Algorithm is not AES;", XmlBeanEncryptionAlgoritmType.AES.toString(), manifest.getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm());

        manifest.getDataProtectionSection().getDataEncryptionLevel().setEncryptionAlgorithm(XmlBeanEncryptionAlgoritmType.AES_TWOFISH.toString());
        assertEquals(XmlBeanEncryptionAlgoritmType.AES_TWOFISH.toString(), manifest.getDataProtectionSection().getDataEncryptionLevel().getEncryptionAlgorithm());
    }

    public void testShouldGetAndSetKeySize(){
        manifest.getDataProtectionSection().getDataEncryptionLevel().setEncryptionKeySize(1);
        assertEquals(1,manifest.getDataProtectionSection().getDataEncryptionLevel().getEncryptionKeySize());
    }
    
    public void testShouldGetCustomEncryptionLevel(){
        manifest.getDataProtectionSection().getDataEncryptionLevel().getCustomEncryptionLevel();
    }

    public void testAddRemoveEligibleCountries(){
        manifest.getDataProtectionSection().addNewEligibleCountry("DE");
        manifest.getDataProtectionSection().addNewEligibleCountry("UK");

        assertEquals(2,manifest.getDataProtectionSection().getEligibleCountryList().length);
        manifest.getDataProtectionSection().removeEligibleCountry("DE");
        assertEquals(1,manifest.getDataProtectionSection().getEligibleCountryList().length);
    }

    public void testAddRemoveNonEligibleCountries(){
        manifest.getDataProtectionSection().addNewNonEligibleCountry("DE");
        manifest.getDataProtectionSection().addNewNonEligibleCountry("UK");

        assertEquals(2,manifest.getDataProtectionSection().getNonEligibleCountryList().length);
        manifest.getDataProtectionSection().removeNonEligibleCountry("DE");
        assertEquals(1,manifest.getDataProtectionSection().getNonEligibleCountryList().length);
    }


}
