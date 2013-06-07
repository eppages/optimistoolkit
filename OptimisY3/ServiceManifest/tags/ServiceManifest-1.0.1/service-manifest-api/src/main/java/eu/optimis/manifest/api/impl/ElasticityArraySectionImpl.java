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

import eu.optimis.manifest.api.sp.ElasticityArray;
import eu.optimis.manifest.api.utils.TemplateLoader;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityArraySectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanRuleType;

import java.util.Vector;

/**
 * @author Karl Catewicz
 */
class ElasticityArraySectionImpl extends AbstractManifestElement<XmlBeanElasticityArraySectionType>
        implements ElasticityArray, eu.optimis.manifest.api.ip.ElasticityArray {


    public ElasticityArraySectionImpl(XmlBeanElasticityArraySectionType base) {
        super(base);
    }


    @Override
    public ElasticityRuleImpl getRuleArray(int i) {
        return new ElasticityRuleImpl(delegate.getRuleArray(i));
    }

    @Override
    public ElasticityRuleImpl[] getRuleArray() {
        Vector<ElasticityRuleImpl> vector = new Vector<ElasticityRuleImpl>();
        for (XmlBeanRuleType rule : delegate.getRuleArray()) {
            vector.add(new ElasticityRuleImpl(rule));
        }
        return vector.toArray(new ElasticityRuleImpl[vector.size()]);
    }

    @Override
    public ElasticityRuleImpl addElasticityRule(String componentId) {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanRuleType newRule = delegate.addNewRule();
        newRule.set(loader.loadElasticityTemplate(componentId));
        return new ElasticityRuleImpl(newRule);
    }

    @Override
    public void removeElasticityRule(int i) {
        delegate.removeRule(i);
    }

}
