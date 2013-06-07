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

import eu.optimis.manifest.api.sp.PriceLevel;
import eu.optimis.types.xmlbeans.servicemanifest.*;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * @author arumpl
 */
public class PriceLevelImpl extends AbstractManifestElement<XmlBeanPriceLevelType>
        implements PriceLevel,eu.optimis.manifest.api.ip.PriceLevel {

    public PriceLevelImpl(XmlBeanPriceLevelType base) {
        super(base);
    }

    @Override
    public BigDecimal getAbsoluteAmount() {
        return delegate.getAbsoluteAmount();
    }

    @Override
    public void setAbsoluteAmount(BigDecimal absoluteAmount) {
        delegate.setAbsoluteAmount(absoluteAmount);
    }

    @Override
    public PriceMetricImpl[] getPriceMetrics() {
        Vector<PriceMetricImpl> vector = new Vector<PriceMetricImpl>();
        for (XmlBeanPriceMetricType priceMetricType : delegate.getPriceMetrics().getPriceMetricArray()) {
            vector.add(new PriceMetricImpl(priceMetricType));
        }
        return vector.toArray(new PriceMetricImpl[vector.size()]);
    }

    @Override
    public PriceMetricImpl getPriceMetrics(int i) {
        return new PriceMetricImpl(delegate.getPriceMetrics().getPriceMetricArray(i));
    }

    @Override
    public PriceMetricImpl addNewPriceMetric() {
        return new PriceMetricImpl(delegate.getPriceMetrics().addNewPriceMetric());
    }


    @Override
    public PriceFenceImpl[] getPriceFences() {
        Vector<PriceFenceImpl> vector = new Vector<PriceFenceImpl>();
        for (XmlBeanPriceFenceType priceFenceType : delegate.getLevelFences().getPriceFenceArray()) {
            vector.add(new PriceFenceImpl(priceFenceType));
        }
        return vector.toArray(new PriceFenceImpl[vector.size()]);
    }

    @Override
    public PriceFenceImpl getPriceFences(int i) {
        return new PriceFenceImpl(delegate.getLevelFences().getPriceFenceArray(i));
    }


}
