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

import eu.optimis.manifest.api.sp.ElasticityVariable;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityLocationTypeEnum;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVariableType;

/**
 * @author arumpl
 */
public class ElasticityVariableImpl extends AbstractManifestElement<XmlBeanVariableType>
        implements ElasticityVariable, eu.optimis.manifest.api.ip.ElasticityVariable {
    public ElasticityVariableImpl(XmlBeanVariableType base) {
        super(base);
    }

    @Override
    public String getLocation() {
        return delegate.getLocation();
    }

    @Override
    public void setMetric(String metric) {
        delegate.setMetric(metric);
    }


    @Override
    public String getType() {
        return delegate.getType().toString();
    }

    @Override
    public void setType(String type) {
        delegate.setType(XmlBeanElasticityLocationTypeEnum.Enum.forString(type));
    }

    @Override
    public void setLocation(String location) {
        delegate.setLocation(location);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public String getMetric() {
        return delegate.getMetric();
    }


}
