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
package eu.optimis.interopt.sla;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;

import java.util.Properties;

/**
 * @author hrasheed - 5/31/12
 *         <p/>
 *         validate the incoming service manifest requirements with service configuration properties
 *         Following are the validation parameters
 *         Max.VM = 10
 *         Max.vCPU per VM= 1 vCPU
 *         Max.RAM per VM= 613 MB
 *         Max.HD size= 500 GB (min. 50 GB)
 */
public class ManifestValidator
{

    int maxInstances = 0;
    int maxMemory = 0;
    int maxCpu = 0;
    int maxSize = 0;

    public ManifestValidator(Properties serviceConfigProps)
    {
        this.maxInstances = Integer.parseInt(serviceConfigProps.getProperty("max.instances"));
        this.maxMemory = Integer.parseInt(serviceConfigProps.getProperty("max.memory"));
        this.maxCpu = Integer.parseInt(serviceConfigProps.getProperty("max.cpu"));
        this.maxSize = Integer.parseInt(serviceConfigProps.getProperty("max.hd.size"));
    }

    public void validate(Manifest manifest) throws Exception
    {
        for (VirtualMachineComponent component : manifest.getVirtualMachineDescriptionSection()
                .getVirtualMachineComponentArray())
        {
            verifyRequestedInstancesBelowThreshold(maxInstances, component);
            verifyRequestedCPUBelowThreshold(maxCpu, component);
            verifyRequestedMemoryBelowThreshold(maxMemory, component);
            verifyRequestedSizeBelowThreshold(maxSize, component);
        }
    }

    /**
     * get the requested vm disk size of a component and throw an exception if its above the given threshold
     * threshold
     *
     * @param maxSize
     * @param component
     * @throws Exception
     */
    private void verifyRequestedSizeBelowThreshold(int maxSize, VirtualMachineComponent component) throws Exception
    {
        int requestedSize =
                Integer.parseInt(component.getOVFDefinition().getDiskSection().getDiskArray(0).getCapacity());
        if (requestedSize > maxSize)
        {
            throw new Exception(
                    "Number of requested disk size (" + requestedSize + ") exceeds allowed maximum (" +
                            maxSize + ")");
        }
    }

    /**
     * get the requested memory vm memory size of a component and throw an exception if its above the given threshold.
     *
     * @param maxMemory
     * @param component
     * @throws Exception
     */
    private void verifyRequestedMemoryBelowThreshold(int maxMemory, VirtualMachineComponent component)
            throws Exception
    {
        int requestedMemory =
                component.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().getMemorySize();
        if (requestedMemory > maxMemory)
        {
            throw new Exception(
                    "Number of requested memory size (" + requestedMemory + ") exceeds allowed maximum (" +
                            maxMemory + ")" );
        }
    }

    /**
     * get the requested number of instances of a component and throw and exception if it is above the given threshold
     *
     * @param maxInstances
     * @param component
     * @throws Exception
     */
    private void verifyRequestedInstancesBelowThreshold(int maxInstances, VirtualMachineComponent component)
            throws Exception
    {
        int requestedInstances =  component.getAllocationConstraints().getUpperBound();
        if (requestedInstances > maxInstances)
        {
            throw new Exception("Number of requested vm instances (" + requestedInstances + 
                    ") exceeds allowed maximum (" + maxInstances + ")");
        }
    }

    /**
     * get the requested number of CPUs for a vm of a component and throw an exception if its above the given threshold
     *
     * @param maxCpu
     * @param component
     * @throws Exception
     */
    private void verifyRequestedCPUBelowThreshold( int maxCpu, VirtualMachineComponent component ) throws Exception
    {
        int requestedCPU = component.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                .getNumberOfVirtualCPUs();
        if (requestedCPU > maxCpu)
        {
            throw new Exception("Number of requested virtual CPUs (" + 
                    requestedCPU + ") exceeds allowed maximum (" + maxCpu + ")");
        }
    }
}
