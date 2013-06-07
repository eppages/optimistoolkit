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

import eu.optimis.manifest.api.ip.AllocationPattern;
import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.*;
import org.apache.xmlbeans.XmlObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author arumpl
 */
class InfrastructureProviderExtensionImpl extends AbstractManifestElement<XmlBeanInfrastructureProviderExtensionType>
        implements eu.optimis.manifest.api.ip.InfrastructureProviderExtension {

    public InfrastructureProviderExtensionImpl(XmlBeanInfrastructureProviderExtensionType base) {
        super(base);
    }

    @Override
    public IncarnatedVirtualMachineComponentImpl[] getIncarnatedVirtualMachineComponents() {
        if (delegate.getIncarnatedServiceComponents() == null) {
            return null;
        }
        XmlBeanIncarnatedVirtualMachineComponentsType componentsType = (XmlBeanIncarnatedVirtualMachineComponentsType) delegate.getIncarnatedServiceComponents();
        if (componentsType != null) {
            Vector<IncarnatedVirtualMachineComponentImpl> vector = new Vector<IncarnatedVirtualMachineComponentImpl>();
            for (XmlBeanIncarnatedVirtualMachineComponentType type : componentsType.getIncarnatedVirtualMachineComponentArray()) {
                vector.add(new IncarnatedVirtualMachineComponentImpl(type));
            }
            return vector.toArray(new IncarnatedVirtualMachineComponentImpl[vector.size()]);
        }
        return null;
    }

    @Override
    public IncarnatedVirtualMachineComponent getIncarnatedVirtualMachineComponents(int i) {
        XmlBeanIncarnatedVirtualMachineComponentsType componentsType = (XmlBeanIncarnatedVirtualMachineComponentsType) delegate.getIncarnatedServiceComponents();
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

    @Override
    public AllocationPatternImpl[] getAllocationPatternArray() {
        List<AllocationPatternImpl> list = new ArrayList<AllocationPatternImpl>();
        for (XmlBeanAllocationPatternType type : delegate.getAllocationPatternArray()) {
            list.add(new AllocationPatternImpl(type));
        }
        return list.toArray(new AllocationPatternImpl[list.size()]);
    }


    @Override
    public AllocationPatternImpl getAllocationPattern(int i) {
        return new AllocationPatternImpl(delegate.getAllocationPatternArray(i));
    }

    @Override
    public AllocationPattern getAllocationPattern(String componentId) {
        for(XmlBeanAllocationPatternType allocationPatternType : delegate.getAllocationPatternArray()){
            if (allocationPatternType.getComponentId().equals(componentId)){
                return new AllocationPatternImpl(allocationPatternType);
            }
        }
        return null;
    }

    @Override
    public AllocationPatternImpl addNewAllocationPattern(String componentId) {
        AllocationPatternImpl pattern = new AllocationPatternImpl(delegate.addNewAllocationPattern());
        pattern.setComponentId(componentId);
        return pattern;
    }

    @Override
    public void removeAllocationPattern(int i) {
        delegate.removeAllocationPattern(i);
    }

    @Override
    public ExternalDeploymentImpl[] getExternalDeploymentArray() {
        List<ExternalDeploymentImpl> list = new ArrayList<ExternalDeploymentImpl>();
        for (XmlBeanExternalDeploymentType type : delegate.getExternalDeploymentArray()) {
            list.add(new ExternalDeploymentImpl(type));
        }
        return list.toArray(new ExternalDeploymentImpl[list.size()]);
    }

    @Override
    public ExternalDeploymentImpl getExternalDeployment(int i) {
        return new ExternalDeploymentImpl(delegate.getExternalDeploymentArray(i));
    }

    @Override
    public ExternalDeploymentImpl addNewExternalDeployment(String providerId, Manifest externalManifest) {
        XmlBeanExternalDeploymentType externalDeploymentType = delegate.addNewExternalDeployment();
        externalDeploymentType.setServiceManifest(externalManifest.toXmlBeanObject().getServiceManifest());
        return new ExternalDeploymentImpl(externalDeploymentType);

    }

    @Override
    public void removeExternalDeployment(int i) {
        delegate.removeExternalDeployment(i);
    }

    @Override
    public XmlObject getCost() {
        return delegate.getCost();
    }

    @Override
    public void setCost(XmlObject cost) {
        delegate.setCost(cost);
    }

    @Override
    public float getRisk() {
        return delegate.getRisk();
    }

    @Override
    public void setRisk(float risk) {
        delegate.setRisk(risk);
    }
}
