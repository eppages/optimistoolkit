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
import eu.optimis.manifest.api.ovf.ip.ProductProperty;
import eu.optimis.manifest.api.ovf.ip.ProductSection;
import eu.optimis.manifest.api.utils.XmlSimpleTypeConverter;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanMsgType;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanProductSectionType;
import org.dmtf.schemas.wbem.wscim.x1.common.CimString;

import java.util.Vector;

/**
 * @author arumpl
 */
class ProductSectionImpl extends AbstractManifestElement<XmlBeanProductSectionType> implements ProductSection, eu.optimis.manifest.api.ovf.sp.ProductSection {
    public ProductSectionImpl(XmlBeanProductSectionType base) {
        super(base);
    }

    @Override
    public void setProduct(String product) {
        delegate.setProduct(XmlSimpleTypeConverter.toMsgType(product));
    }

    @Override
    public String getProduct() {
        return delegate.getProduct().getStringValue();
    }

    @Override
    public String getVersion() {
        return delegate.getVersion().getStringValue();
    }

    @Override
    public void setVersion(String version) {
        CimString cimString = CimString.Factory.newInstance();
        cimString.setStringValue(version);
        delegate.setVersion(cimString);
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
    public ProductPropertyImpl getPropertyByKey(String key) {
        for (XmlBeanProductSectionType.Property p : delegate.getPropertyArray()){
            if(p.getKey().equals(key)){
                return new ProductPropertyImpl(p);
            }
        }
        return null;  
    }

    @Override
    public ProductPropertyImpl addNewProperty(String key, String type, String value) {
        XmlBeanProductSectionType.Property p = delegate.addNewProperty();
        p.setKey(key);
        p.setType(type);
        p.setValue2(value);
        return new ProductPropertyImpl(p);
    }

    @Override
    public ProductPropertyImpl[] getPropertyArray() {
        Vector<ProductPropertyImpl> vector = new Vector<ProductPropertyImpl>();
        for (XmlBeanProductSectionType.Property type : delegate.getPropertyArray()){
            vector.add(new ProductPropertyImpl(type));
        }
        return vector.toArray(new ProductPropertyImpl[vector.size()]);
    }

    public ProductPropertyImpl getPropertyArray(int i) {
        return new ProductPropertyImpl(delegate.getPropertyArray(i));
    }

    public void removeProperty(int i) {
        delegate.removeProperty(i);
    }

}
