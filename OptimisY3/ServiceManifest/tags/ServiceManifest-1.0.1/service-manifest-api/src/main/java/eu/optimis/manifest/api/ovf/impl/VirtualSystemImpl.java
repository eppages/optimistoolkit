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

package eu.optimis.manifest.api.ovf.impl;

import eu.optimis.manifest.api.impl.AbstractManifestElement;
import eu.optimis.manifest.api.ovf.sp.VirtualSystem;
import eu.optimis.manifest.api.utils.XmlSimpleTypeConverter;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanProductSectionType;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanVirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanVirtualSystemType;

/**
 * @author arumpl
 */
class VirtualSystemImpl extends AbstractManifestElement<XmlBeanVirtualSystemType> implements VirtualSystem, eu.optimis.manifest.api.ovf.ip.VirtualSystem {

    public VirtualSystemImpl(XmlBeanVirtualSystemType base) {
        super(base);
    }

    @Override
    public String getInfo() {
        return delegate.getInfo().getStringValue();
    }

    @Override
    public void setInfo(String info) {
        delegate.setInfo(XmlSimpleTypeConverter.toMsgType(info));
    }

    @Override
    public String getName() {
        if (delegate.isSetName())
            return delegate.getName().getStringValue();
        return null;
    }

    @Override
    public void setName(String name) {
        delegate.setName(XmlSimpleTypeConverter.toMsgType(name));
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public ProductSectionImpl getProductSection() {
        return new ProductSectionImpl((XmlBeanProductSectionType) delegate.getSectionArray(0));
    }

    @Override
    public VirtualHardwareSectionImpl getVirtualHardwareSection() {
        return new VirtualHardwareSectionImpl((XmlBeanVirtualHardwareSectionType) delegate.getSectionArray(1));
    }
}
