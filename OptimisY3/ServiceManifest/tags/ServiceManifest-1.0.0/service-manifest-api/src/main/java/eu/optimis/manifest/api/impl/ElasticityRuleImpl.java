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

import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.types.xmlbeans.servicemanifest.RuleType;
import org.apache.xmlbeans.GDuration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 09.12.2011 Time: 16:46:08
 */
class ElasticityRuleImpl extends AbstractManifestElement<RuleType>
        implements ElasticityRule, eu.optimis.manifest.api.ip.ElasticityRule {

    public ElasticityRuleImpl(RuleType base) {
        super(base);
    }

    @Override
    public ScopeImpl getScope() {
        return new ScopeImpl(delegate.getScope());
    }


    @Override
    public String getKPIName() {
        return delegate.getKPIName();
    }


    @Override
    public String getWindow() {
        return delegate.getWindow().toString();
    }

    @Override
    public void setKPIName(String kpiName) {
        delegate.setKPIName(kpiName);
    }

    @Override
    public int getFrequency() {
        return delegate.getFrequency().intValue();
    }


    @Override
    public void setWindow(String window) {
        delegate.setWindow(new GDuration(window));
    }

    @Override
    public void setFrequency(int frequency) {
        delegate.setFrequency(BigInteger.valueOf(frequency));
    }

    @Override
    public int getQuota() {
        return delegate.getQuota().intValue();
    }

    @Override
    public void setQuota(int quota) {
        delegate.setQuota(BigInteger.valueOf(quota));
    }

    @Override
    public int getTolerance() {
        return delegate.getTolerance().intValue();
    }

    @Override
    public void setTolerance(float tolerance) {
        delegate.setTolerance(BigDecimal.valueOf(tolerance));
    }

}
