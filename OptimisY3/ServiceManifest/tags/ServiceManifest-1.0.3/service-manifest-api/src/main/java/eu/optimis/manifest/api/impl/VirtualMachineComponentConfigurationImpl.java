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

import eu.optimis.manifest.api.sp.*;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanComponentPropertyType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanSecurityKeyType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanSoftwareDependencyArrayType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanVirtualMachineComponentConfigurationType;

import java.util.Vector;

/**
 * @author arumpl
 */
public class VirtualMachineComponentConfigurationImpl extends AbstractManifestElement<XmlBeanVirtualMachineComponentConfigurationType>
        implements VirtualMachineComponentConfiguration {
    public VirtualMachineComponentConfigurationImpl(XmlBeanVirtualMachineComponentConfigurationType base) {
        super(base);
    }

    @Override
    public DependencyImpl[] getSoftwareDependencies() {
        Vector<DependencyImpl> dependencyArray = new Vector<DependencyImpl>();
        for (XmlBeanSoftwareDependencyArrayType.Dependency dependency : delegate.getSoftwareDependencies().getDependencyArray()) {
            dependencyArray.add(new DependencyImpl(dependency));
        }
        return dependencyArray.toArray(new DependencyImpl[dependencyArray.size()]);
    }

    @Override
    public DependencyImpl getSoftwareDependencies(int i) {
        return new DependencyImpl(delegate.getSoftwareDependencies().getDependencyArray(i));
    }

    @Override
    public DependencyImpl addNewDependency(String groupId, String artifactId, String version) {
        XmlBeanSoftwareDependencyArrayType.Dependency dependency = delegate.getSoftwareDependencies().addNewDependency();
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        dependency.setGroupId(groupId);
        return new DependencyImpl(dependency);
    }

    @Override
    public Dependency addNewDependency() {
        return new DependencyImpl(delegate.getSoftwareDependencies().addNewDependency());
    }

    @Override
    public void removeDependency(int i) {
        delegate.getSoftwareDependencies().removeDependency(i);
    }

    @Override
    public String getSecurityKeyType() {
        return delegate.getSecurityKeyType().toString();
    }

    @Override
    public void setSecurityKeyType(String value) {
        delegate.setSecurityKeyType(XmlBeanSecurityKeyType.Enum.forString(value));
    }

    @Override
    public String getComponentId() {
        return delegate.getComponentId();
    }

    @Override
    public ComponentPropertyImpl addNewComponentProperty(String name, String value) {
        if (delegate.getComponentProperties() == null) delegate.addNewComponentProperties();
        XmlBeanComponentPropertyType property = delegate.getComponentProperties().addNewComponentProperty();
        property.setName(name);
        property.setValue(value);
        return new ComponentPropertyImpl(property);
    }

    @Override
    public ComponentPropertyImpl getComponentProperty(String name) {
        for (XmlBeanComponentPropertyType property : delegate.getComponentProperties().getComponentPropertyArray()) {
            if (property.getName().equals(name)) return new ComponentPropertyImpl(property);
        }
        return null;
    }

    @Override
    public ComponentPropertyImpl[] getComponentProperties() {
        Vector<ComponentPropertyImpl> vector = new Vector<ComponentPropertyImpl>();
        for (XmlBeanComponentPropertyType propertyType : delegate.getComponentProperties().getComponentPropertyArray()) {
            vector.add(new ComponentPropertyImpl(propertyType));
        }
        return vector.toArray(new ComponentPropertyImpl[vector.size()]);
    }

    @Override
    public void removeComponentProperty(String name) {
        for (int i = 0; i < delegate.getComponentProperties().getComponentPropertyArray().length; i++) {
            if (delegate.getComponentProperties().getComponentPropertyArray(i).getName().equals(name)) {
                delegate.getComponentProperties().removeComponentProperty(i);
            }
        }
    }
}
