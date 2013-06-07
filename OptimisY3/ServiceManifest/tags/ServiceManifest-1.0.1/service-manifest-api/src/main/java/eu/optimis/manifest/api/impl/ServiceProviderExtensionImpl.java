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

import eu.optimis.manifest.api.sp.ServiceProviderExtension;
import eu.optimis.manifest.api.sp.SoftwareDependencies;
import eu.optimis.types.xmlbeans.servicemanifest.service.ServiceProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.service.SoftwareDependenciesType;

import java.util.Vector;

/**
 * @author arumpl
 */
class ServiceProviderExtensionImpl extends AbstractManifestElement<ServiceProviderExtensionType>
        implements ServiceProviderExtension {

    public ServiceProviderExtensionImpl(ServiceProviderExtensionType base) {
        super(base);
    }

    @Override
    public SoftwareDependenciesImpl[] getSoftwareDependenciesArray() {
        Vector<SoftwareDependenciesImpl> softwareDependencies = new Vector<SoftwareDependenciesImpl>();
        for (SoftwareDependenciesType doc : delegate.getSoftwareDependenciesArray()) {
            softwareDependencies.add(new SoftwareDependenciesImpl(doc));
        }
        return softwareDependencies.toArray(new SoftwareDependenciesImpl[softwareDependencies.size()]);
    }

    @Override
    public SoftwareDependencies getSoftwareDependenciesArray(int i) {
        return new SoftwareDependenciesImpl(delegate.getSoftwareDependenciesArray(i));
    }

    @Override
    public SoftwareDependenciesImpl addNewSoftwareDependencies(String componentId) {
        if (getSoftwareDependenciesByComponentId(componentId) == null) {
            SoftwareDependenciesType dependenciesType = delegate.addNewSoftwareDependencies();
            dependenciesType.setComponentId(componentId);
            return new SoftwareDependenciesImpl(dependenciesType);
        } else {
            throw new RuntimeException("You can only add one software dependencies section per component.");
        }

    }

    @Override
    public SoftwareDependenciesImpl getSoftwareDependenciesByComponentId(String componentId) {
        for (SoftwareDependenciesType type : delegate.getSoftwareDependenciesArray()) {
            if (type.getComponentId().equals(componentId)) {
                return new SoftwareDependenciesImpl(type);
            }
        }
        return null;
    }


    @Override
    public void removeSoftwareDependenciesByComponentId(String componentId) {
        for (int i = 0; i < delegate.getSoftwareDependenciesArray().length; i++) {
            if (delegate.getSoftwareDependenciesArray(i).getComponentId().equals(componentId)) {
                delegate.removeSoftwareDependencies(i);
            }
        }
    }

    @Override
    public void removeSoftwareDependencies(int i) {
        delegate.removeSoftwareDependencies(i);
    }


}
