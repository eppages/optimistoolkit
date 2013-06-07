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

package eu.optimis.manifest.api.sp;

/**
 * @author arumpl
 */
public interface VirtualMachineComponentConfiguration {

    /**
     * retrieves the security key type
     *
     * @return one of SSH | VPN | None
     */
    String getSecurityKeyType();

    /**
     * retrieves the security key type
     *
     * @param securityKeyType one of SSH | VPN | None
     */
    void setSecurityKeyType(String securityKeyType);

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
     * @return the dependency at the ith postion
     */
    Dependency getSoftwareDependencies(int i);

    /**
     * Adds a new software dependency. ArtifactId, version and groupId are required
     * to have a valid software dependency element
     *
     * @return the newly created dependency
     */
    Dependency addNewDependency(String artifactId, String groupId, String version);


    /**
     * Adds an empty dependency element, you have to set artifactId,version and groupId are required
     * to have a valid software dependency element
     *
     * @return the newly created dependency
     */
    Dependency addNewDependency();

    /**
     * remove dependency at index i
     *
     * @param i the index
     */
    void removeDependency(int i);

    /**
     * Adds a new property with name and value correctly set
     *
     * @param name
     * @param value
     * @return the newly created property
     */
    ComponentProperty addNewComponentProperty(String name, String value);

    /**
     * retrieve property by name
     * @param name
     * @return the component property
     */
    ComponentProperty getComponentProperty(String name);

    /**
     * get all properties
     * @return an array of component properties
     */
    ComponentProperty[] getComponentProperties();

    /**
     * Removes all property with provided name
     * @param name the property name
     */
    void removeComponentProperty(String name);
}
