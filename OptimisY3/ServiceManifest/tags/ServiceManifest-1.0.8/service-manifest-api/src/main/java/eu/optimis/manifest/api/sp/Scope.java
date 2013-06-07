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
public interface Scope
{

    /**
     * Retrieves the array of componentId's a certain Rule might be applied to.
     * 
     * @return array of componentId's
     */
    String[] getComponentIdArray();

    /**
     * sets the array of componentId's
     */
    void setComponentIdArray( String[] componentIdArray );

    /**
     * Retrieves the array of componentId's at position i
     * 
     * @param i
     *            position
     * @return componentId
     */
    String getComponentIdArray( int i );

    /**
     * Adds a new componentId to the scope Array
     * 
     * @param componentId
     *            reference to a component in the VirtualMachineDescriptionSection
     * @see VirtualMachineDescriptionSection
     */
    void addComponentId( String componentId );

    /**
     * Removes the componentId at position i
     * 
     * @param i
     *            the ith position in the array
     */
    void removeComponentId( int i );

    /**
     * Removes the componentId provided as string
     * 
     * @param componentId
     *            the componentId to be removed
     */
    void removeComponentId( String componentId );

    /**
     * checks if a certain componentId is in the scope
     * 
     * @param componentId
     *            the component id we are looking for
     * @return true | false
     */
    boolean contains( String componentId );
}