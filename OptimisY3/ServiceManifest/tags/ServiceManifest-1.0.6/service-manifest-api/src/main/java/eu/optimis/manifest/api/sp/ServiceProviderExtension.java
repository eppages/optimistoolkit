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
public interface ServiceProviderExtension
{

    /**
     * @return all software dependencies of all components
     */
    VirtualMachineComponentConfiguration[] getVirtualMachineComponentConfigurationArray();

    /**
     * @param componentId the id of the referenced component
     * @return get component configuration belonging to the component identified by componentId
     */
    VirtualMachineComponentConfiguration getVirtualMachineComponentConfiguration(
            String componentId );

    /**
     * @param componentId the reference to the component this configuration belongs to
     * @return the components configuration
     */
    VirtualMachineComponentConfiguration addNewVirtualMachineComponentConfiguration(
            String componentId );

    /**
     * remove a components configuration
     *
     * @param componentId the reference to the component this configuration belongs to
     */
    void removeVirtualMachineComponentConfiguration( String componentId );

    /**
     * sets the sla ID in the SP extension documents. The
     * SLA ID is set by the SDO after creating the agreement.
     *
     * @param newSLAID
     */
    void setSLAID( String newSLAID );

    /**
     * retrieve the SLA ID. This SLA ID is required by all
     * components to be able to subscribe to the notification
     * service provided by the CQoS component.
     *
     * @return the agreements SLAID
     */
    String getSLAID();

    /**
     * the SLA ID is not required, it is added not during manifest
     * construction but later.
     *
     * @return true|false
     */
    boolean isSetSLAID();

    /**
     * removes the SLAID element from the manifest
     */
    void unsetSLAID();

    /**
     * checks if the key for accessing the Data Manager is set in the manifest
     *
     * @return true|false
     */
    boolean isSetDataManagerKey();

    /**
     * sets the data manager key
     *
     * @param dataManagerKey the key provided as a byte array.
     */
    void setDataManagerKey( byte[] dataManagerKey );

    /**
     * retrieves the data manager key if it was set before.
     *
     * @return the data manager key
     */
    byte[] getDataManagerKey();

    /**
     * removes the data manager key from the manifest.
     */
    void unsetDataManagerKey();
}