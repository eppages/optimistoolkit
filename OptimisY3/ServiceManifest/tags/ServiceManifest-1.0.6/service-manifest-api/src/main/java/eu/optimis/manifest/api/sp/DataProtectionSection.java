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
package eu.optimis.manifest.api.sp;

/**
 * @author owaeld
 */
public interface DataProtectionSection
{

    /**
     * @return the encryption level element containing encryption algorithm and encryption key size
     * @see EncryptionLevel
     */
    EncryptionLevel getDataEncryptionLevel();

    /**
     * Retrieves the data protection level as
     * 
     * @return string (one of [DPA | None])
     */
    String getDataProtectionLevel();

    /**
     * Sets the DataProtationLevel
     * 
     * @param dataProtectionLevel
     *            one of [ "DPA" | "None" ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType.Enum
     */
    void setDataProtectionLevel( String dataProtectionLevel );

    /**
     * Retrieves all eligible countries
     * 
     * @return array with eligible countries
     */
    String[] getEligibleCountryList();

    /**
     * adds a new eligible country to the list
     * 
     * @param country
     *            the Two-letter (alpha-2) ISO 3166-1 code for one of the 243 countries. These codes are
     *            subject to change. For valid values refer to
     * @url{http://www.iso.org/iso/list-en1-semic-3.txt
     */
    void addNewEligibleCountry( String country );

    /**
     * removes an eligible country from the list
     * 
     * @param country
     *            the Two-letter (alpha-2) ISO 3166-1 code for one of the 243 countries. These codes are
     *            subject to change. For valid values refer to
     * @url{http://www.iso.org/iso/list-en1-semic-3.txt
     */
    void removeEligibleCountry( String country );

    /**
     * Retrieves all non eligible countries
     * 
     * @return array with non eligible countries
     */
    String[] getNonEligibleCountryList();

    /**
     * adds a new eligible country to the list
     * 
     * @param country
     *            the Two-letter (alpha-2) ISO 3166-1 code for one of the 243 countries. These codes are
     *            subject to change. For valid values refer to
     * @url{http://www.iso.org/iso/list-en1-semic-3.txt
     */
    void addNewNonEligibleCountry( String country );

    /**
     * removes an eligible country from the list
     * 
     * @param country
     *            the Two-letter (alpha-2) ISO 3166-1 code for one of the 243 countries. These codes are
     *            subject to change. For valid values refer to
     * @url{http://www.iso.org/iso/list-en1-semic-3.txt
     */
    void removeNonEligibleCountry( String country );

    /**
     * retrieve the data storage section this section is used to allocate the desired file system size
     * 
     * @return DataStorage
     */
    DataStorage getDataStorage();
}