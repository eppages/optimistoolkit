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
package eu.optimis.manifest.api.ip;

import eu.optimis.manifest.api.impl.AllocationOfferImpl;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;

/**
 * @author owaeld
 */
public interface InfrastructureProviderExtension
{
    /**
     * @return the "IncarnatedVirtualMachineComponents" element
     */
    IncarnatedVirtualMachineComponent[] getIncarnatedVirtualMachineComponents();

    /**
     * @param i index of the component we want to get.
     * @return the "IncarnatedVirtualMachineComponent" at the provided index position
     */
    IncarnatedVirtualMachineComponent getIncarnatedVirtualMachineComponents( int i );

    /**
     * @param componentId the id of the component we are looking for
     * @return the "IncarnatedVirtualMachineComponent" element with the provided componentId
     */
    IncarnatedVirtualMachineComponent getIncarnatedVirtualMachineComponentByComponentId(
            String componentId );

    /**
     * retrieves the virtual system by its id
     *
     * @param virtualSystemId the id, e.g. system-mysql_instance-1
     * @return
     */
    VirtualSystem getVirtualSystem( String virtualSystemId );

    /**
     * retrieve the allocation offer element.
     *
     * @return
     */
    AllocationOffer getAllocationOffer();

    /**
     * add a new allocation offer element
     *
     * @return
     */
    AllocationOfferImpl addNewAllocationOffer();

    /**
     * check if the allocation offer is set
     *
     * @return true if the element exists
     */
    boolean isSetAllocationOffer();

    /**
     * remove the allocation offer section
     */
    void unsetAllocationOffer();

    /**
     * remove the incarnated vm components section
     */
    void unsetIncarnatedVirtualMachineComponents();

    /**
     * Check if incarnated vm components exist.
     *
     * @return true|false
     */
    boolean isSetIncarnatedVirtualMachineComponents();
}