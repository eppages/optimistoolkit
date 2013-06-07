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
public interface VirtualMachineDescriptionSection
{
    /**
     * @return the serviceId
     */
    String getServiceId();

    /**
     * @return array of VirtualMachineComponent
     */
    VirtualMachineComponent[] getVirtualMachineComponentArray();

    /**
     * @param i
     *            the index
     * @return VirtualMachineComponent at the ith position
     */
    VirtualMachineComponent getVirtualMachineComponentArray( int i );

    /**
     * Looks for a "VirtualMachineComponent" by its componentId
     * 
     * @param componentId
     * @return
     */
    VirtualMachineComponent getVirtualMachineComponentById( String componentId );

    /**
     * adds a new "VirtualMachineComponent" Element and sets all default values
     * 
     * @param componentId
     * @return
     */
    VirtualMachineComponent addNewVirtualMachineComponent( String componentId );

    /**
     * @param serviceId
     *            the service id
     */
    void setServiceId( String serviceId );

    /**
     * Removes a virtual machine component by its id.
     * 
     * @param componentId
     *            the id of the component to be removed
     */
    void removeVirtualMachineComponentById( String componentId );

    /**
     * Retrieves an array of affinity rules
     * 
     * @return array
     */
    AffinityRule[] getAffinityRules();

    /**
     * Retrieves the affinity rule at position i
     * 
     * @param i
     *            the ith position
     * @return the affinity rule
     */
    AffinityRule getAffinityRule( int i );

    /**
     * Adds a new affinity rule to the AffinitySection
     * <p/>
     * <p/>
     * 
     * <pre>
     * {@code
     * <opt:AffinityRule>
     *    <opt:Scope>
     *       <opt:componentId>${componentId}</opt:componentId>
     *    </opt:Scope>
     *    <opt:AffinityConstraints>${affinityConstraints}</opt:AffinityConstraints>
     * </opt:AffinityRule>
     * </code>
     * }
     * </pre>
     * 
     * @param componentId
     *            the id of the referenced component
     * @param affinityLevel
     *            one of [Low | Medium | High ]
     * @return the created affinity rule
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType
     */
    AffinityRule addNewAffinityRule( String componentId, String affinityLevel );

    /**
     * adds an affinity rule and directly adds a list of components to the scope
     * 
     * @param componentIds
     *            a list of strings containing the ids of referenced components.
     * @param affinityLevel
     *            the affinity the components have to each other.
     * @return the newly created affinity rule
     */
    AffinityRule addNewAffinityRule( String[] componentIds, String affinityLevel );

    /**
     * removeAffinityRule
     * <p/>
     * Removes an affinity rule from the AffinitySection.
     * 
     * @param index
     *            of the rule
     */
    void removeAffinityRule( int index );
    
    /**
     * Retrieves an array of anti affinity rules
     * 
     * @return array
     */
    AntiAffinityRule[] getAntiAffinityRules();

    /**
     * Retrieves the anti affinity rule at position i
     * 
     * @param i
     *            the ith position
     * @return the anti affinity rule
     */
    AntiAffinityRule getAntiAffinityRule( int i );

    /**
     * Adds a new anti affinity rule to the AntiAffinitySection
     * <p/>
     * <p/>
     * 
     * <pre>
     * {@code
     * <opt:AntiAffinityRule>
     *    <opt:Scope>
     *       <opt:componentId>${componentId}</opt:componentId>
     *    </opt:Scope>
     *    <opt:AntiAffinityConstraints>${affinityConstraints}</opt:AntiAffinityConstraints>
     * </opt:AntiAffinityRule>
     * </code>
     * }
     * </pre>
     * 
     * @param componentId
     *            the id of the referenced component
     * @param antiAffinityLevel
     *            one of [Low | Medium | High ]
     * @return the created anti affinity rule
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityConstraintType
     */
    AntiAffinityRule addNewAntiAffinityRule( String componentId, String antiAffinityLevel );

    /**
     * adds an anti affinity rule and directly adds a list of components to the scope
     * 
     * @param componentIds
     *            a list of strings containing the ids of referenced components.
     * @param antiAffinityLevel
     *            the antiaffinity components have to each other.
     * @return the newly created anti affinity rule
     */
    AntiAffinityRule addNewAntiAffinityRule( String[] componentIds, String antiAffinityLevel );

    /**
     * removeAntiAffinityRule
     * <p/>
     * Removes an anti affinity rule from the AntiAffinitySection.
     * 
     * @param index
     *            of the rule
     */
    void removeAntiAffinityRule( int index );

    /**
     * set if federation should be allowed
     * 
     * @param isFederationAllowed
     *            true | false
     */
    void setIsFederationAllowed( boolean isFederationAllowed );

    /**
     * checks if federation is allowed
     * 
     * @return true | false
     */
    boolean isFederationAllowed();
}