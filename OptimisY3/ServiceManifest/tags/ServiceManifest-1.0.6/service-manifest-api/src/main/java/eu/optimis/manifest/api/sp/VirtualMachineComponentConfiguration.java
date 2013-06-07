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
 * @author arumpl
 */
public interface VirtualMachineComponentConfiguration
{

    /**
     * set ssh key to manifest
     *
     * @param sshKey ssh key as byte array
     */
    void setSSHKey( byte[] sshKey );

    /**
     * remove ssh key from manifest
     */
    void removeSSHKey();

    /**
     * get ssh key from manifest
     *
     * @return return ssh as byte array
     */
    byte[] getSSHKey();

    /**
     * add license token to manifest
     *
     * @param tokenData
     */
    void addToken( byte[] tokenData );

    /**
     * remove license token from manifest
     *
     * @param i index
     */
    void removeToken( int i );

    /**
     * retrieve license token from manifest
     *
     * @param i index
     * @return byte array representation of license token if no token was found, an empty array is returned.
     */
    byte[] getToken( int i );

    /**
     * retrieve all license token from manifest
     *
     * @return returns a array of license tokens
     */
    byte[][] getTokenArray();

    /**
     * check if security is based on VPN
     *
     * @return true if so
     */
    boolean isSecurityVPNbased();

    /**
     * check if security is based on SSH
     *
     * @return true if so
     */
    boolean isSecuritySSHbased();

    /**
     * enable VPN based security
     */
    void enableVPNSecurity();

    /**
     * enable SSH based security
     */
    void enableSSHSecurity();

    /**
     * disable VPN based security
     */
    void disableVPNSecurity();

    /**
     * disable SSH based security
     */
    void disableSSHSecurity();

    /**
     * @return the id of the referenced component
     */
    String getComponentId();

    /**
     * @return an array of software dependencies
     */
    Dependency[] getSoftwareDependencies();

    /**
     * Retrieve a software dependency
     *
     * @param i the index to retrieve
     * @return the dependency at the ith position
     */
    Dependency getSoftwareDependencies( int i );

    /**
     * Adds a new software dependency. ArtifactId, version and groupId are required to have a valid software
     * dependency element
     *
     * @return the newly created dependency
     */
    Dependency addNewDependency( String artifactId, String groupId, String version );

    /**
     * Adds an empty dependency element, you have to set artifactId, version and groupId to have a valid
     * software dependency element
     *
     * @return the newly created dependency
     */
    Dependency addNewDependency();

    /**
     * remove dependency at index i
     *
     * @param i the index
     */
    void removeDependency( int i );

    /**
     * Adds a new property with name and value correctly set
     *
     * @param name
     * @param value
     * @return the newly created property
     */
    ComponentProperty addNewComponentProperty( String name, String value );

    /**
     * retrieve property by name
     *
     * @param name
     * @return the component property
     */
    ComponentProperty getComponentProperty( String name );

    /**
     * get all properties
     *
     * @return an array of component properties
     */
    ComponentProperty[] getComponentProperties();

    /**
     * Removes all property with provided name
     *
     * @param name the property name
     */
    void removeComponentProperty( String name );

    /**
     * retrieves the encryptedSpace element
     *
     * @return the element
     */
    EncryptedSpace getEncryptedSpace();

    /**
     * checks if an encrypted space element is set
     *
     * @return true | false
     */
    boolean isEncryptedSpaceEnabled();

    /**
     * enable the encrypted space element and set the required encryption key
     *
     * @param encryptionKey
     */
    void enableEncryptedSpace( byte[] encryptionKey );

    /**
     * enables the encrypted space element without setting the encrytion key
     */
    void enableEncryptedSpace();

    /**
     * disable the usage of encrypted space.
     */
    void disableEncryptedSpace();
}
