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

import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.IncarnatedVirtualMachineComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.IncarnatedVirtualMachineComponentsType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.InfrastructureProviderExtensionType;

import java.util.Vector;

/**
 * @author arumpl
 */
class InfrastructureProviderExtensionImpl extends AbstractManifestElement<InfrastructureProviderExtensionType>
        implements eu.optimis.manifest.api.ip.InfrastructureProviderExtension {

    public InfrastructureProviderExtensionImpl(InfrastructureProviderExtensionType base) {
        super(base);
    }

    @Override
    public IncarnatedVirtualMachineComponentImpl[] getIncarnatedVirtualMachineComponents() {
        IncarnatedVirtualMachineComponentsType componentsType = (IncarnatedVirtualMachineComponentsType) delegate.getIncarnatedServiceComponents();
        if (componentsType != null) {
            Vector<IncarnatedVirtualMachineComponentImpl> vector = new Vector<IncarnatedVirtualMachineComponentImpl>();
            for (IncarnatedVirtualMachineComponentType type : componentsType.getIncarnatedVirtualMachineComponentArray()) {
                vector.add(new IncarnatedVirtualMachineComponentImpl(type));
            }
            return vector.toArray(new IncarnatedVirtualMachineComponentImpl[vector.size()]);
        }
        return null;
    }

    @Override
    public IncarnatedVirtualMachineComponent getIncarnatedVirtualMachineComponents(int i) {
        IncarnatedVirtualMachineComponentsType componentsType = (IncarnatedVirtualMachineComponentsType) delegate.getIncarnatedServiceComponents();
        if (componentsType != null) {
            return new IncarnatedVirtualMachineComponentImpl(componentsType.getIncarnatedVirtualMachineComponentArray(i));
        }
        return null;
    }

    @Override
    public IncarnatedVirtualMachineComponentImpl getIncarnatedVirtualMachineComponentByComponentId(String componentId) {
        IncarnatedVirtualMachineComponentImpl[] components = getIncarnatedVirtualMachineComponents();
        for (IncarnatedVirtualMachineComponentImpl component : components) {
            if (component.getComponentId().equals(componentId)) {
                return component;
            }
        }
        return null;
    }


}
