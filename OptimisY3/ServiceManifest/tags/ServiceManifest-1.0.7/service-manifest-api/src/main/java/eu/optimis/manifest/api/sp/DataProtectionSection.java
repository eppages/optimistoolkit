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
     * Retrieves standard contractual clauses
     * 
     * @return an array of clauses
     */
    SCC getSCC();
    
    /**
     * Retrieves binding cooperate rules
     * 
     * @return an array of rules
     */
    BCR getBCR();
    
    /**
     * Retrieves intellectual property rights
     * 
     * @return an array of rights
     */
    IPR getIPR();

    /**
     * Creates a new DataStorage
     * 
     * @param componentId
     *            the referenced component
     * @param storage Name
     *            the name of the data storage
     * @return the created DataStorage
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType
     */
    DataStorage addNewDataStorage( String componentId, String storageName );
    
    /**
     * Creates a new DataStorage
     * 
     * @param componentIdList
     *            the referenced components
     * @param storage Name
     *            the name of the data storage
     * @return the created DataStorage
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType
     */
    DataStorage addNewDataStorage( String[] componentIdList, String storageName );
    
    /**
     * retrieve the data storage array
     * 
     * @return array of DataStorage
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType
     */
    DataStorage[] getDataStorageArray();
    
    /**
     * Retrieves the data storage entry at position i
     * 
     * @return the DataStorage
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType
     */
    DataStorage getDataStorageArray( int i );
    
    /**
     * Retrieves the data storage entry by its name
     * 
     * @return the DataStorage
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataStorageType
     */
    DataStorage getDataStorage( String name );
    
    /**
     * removes the DataStorage entry at position i from the DataStorage array
     * 
     * @param i
     *            the position in the array
     */
    void removeDataStorage( int i );
    
    /**
     * remove the DataStorage by its name
     * 
     * @param name
     *            the name of the DataStorage
     */
    void removeDataStorage( String name );
}