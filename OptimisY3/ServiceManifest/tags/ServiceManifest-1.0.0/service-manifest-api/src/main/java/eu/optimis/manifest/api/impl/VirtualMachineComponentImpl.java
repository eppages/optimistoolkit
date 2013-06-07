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

package eu.optimis.manifest.api.impl;

import eu.optimis.manifest.api.ovf.impl.OVFDefinitionImpl;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.AffinityConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.VirtualMachineComponentType;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 20.12.2011 Time: 13:22:04
 */
class VirtualMachineComponentImpl extends AbstractManifestElement<VirtualMachineComponentType>
        implements VirtualMachineComponent, eu.optimis.manifest.api.ip.VirtualMachineComponent {

    public VirtualMachineComponentImpl(VirtualMachineComponentType base) {
        super(base);
    }

    @Override
    public OVFDefinitionImpl getOVFDefinition() {
        return new OVFDefinitionImpl(delegate.getOVFDefinition());
    }

    @Override
    public AllocationConstraintImpl getAllocationConstraints() {
        return new AllocationConstraintImpl(delegate.getAllocationConstraints());
    }

    @Override
    public String getAffinityConstraints() {
        return delegate.getAffinityConstraints().toString();
    }

    @Override
    public void setAffinityConstraints(String constraint) {
        delegate.setAffinityConstraints(AffinityConstraintType.Enum.forString(constraint));
    }


    @Override
    public String getComponentId() {
        return delegate.getComponentId();
    }

}
