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

import eu.optimis.manifest.api.ip.PriceComponent;
import eu.optimis.manifest.api.sp.PricePlan;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanPriceComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanPricePlanType;

import java.util.Vector;

/**
 * @author arumpl
 */
class PricePlanImpl extends AbstractManifestElement<XmlBeanPricePlanType>
        implements PricePlan, eu.optimis.manifest.api.ip.PricePlan {

    public PricePlanImpl(XmlBeanPricePlanType base) {
        super(base);
    }

    @Override
    public ScopeImpl getScope() {
        return new ScopeImpl(delegate.getScope());
    }

    @Override
    public float getPlanCap() {
        return delegate.getPlanCap();
    }

    @Override
    public void setPlanCap(float planCap) {
        delegate.setPlanCap(planCap);
    }

    @Override
    public float getPlanFloor() {
        return delegate.getPlanFloor();
    }

    @Override
    public void setPlanFloor(float planFloor) {
        delegate.setPlanFloor(planFloor);
    }

    @Override
    public String getCurrency() {
        return delegate.getCurrency();
    }

    @Override
    public void setCurrency(String currency) {
        delegate.setCurrency(currency);
    }

    @Override
    public PriceComponentImpl[] getPriceComponents() {
        Vector<PriceComponentImpl> vector = new Vector<PriceComponentImpl>();
        for (XmlBeanPriceComponentType priceComponentType : delegate.getPlanComponents().getPriceComponentArray()) {
            vector.add(new PriceComponentImpl(priceComponentType));
        }
        return vector.toArray(new PriceComponentImpl[vector.size()]);
    }

    @Override
    public PriceComponentImpl getPriceComponents(int i) {
        return new PriceComponentImpl(delegate.getPlanComponents().getPriceComponentArray(i));
    }
}
