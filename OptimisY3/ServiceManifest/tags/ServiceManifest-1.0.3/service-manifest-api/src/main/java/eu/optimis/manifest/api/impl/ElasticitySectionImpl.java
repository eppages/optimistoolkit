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

import eu.optimis.manifest.api.sp.ElasticitySection;
import eu.optimis.manifest.api.sp.ElasticityVariable;
import eu.optimis.manifest.api.utils.TemplateLoader;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityLocationTypeEnum;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticitySectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVariableType;

import java.util.Vector;

/**
 * @author arumpl
 */
public class ElasticitySectionImpl extends AbstractManifestElement<XmlBeanElasticitySectionType>
        implements ElasticitySection, eu.optimis.manifest.api.ip.ElasticitySection {

    public ElasticitySectionImpl(XmlBeanElasticitySectionType base) {
        super(base);
    }

    @Override
    public ElasticityRuleImpl getRule(int i) {
        return new ElasticityRuleImpl(delegate.getElasticityRules().getElasticityRuleArray(i));
    }


    @Override
    public ElasticityRuleImpl[] getRuleArray() {
        if (!delegate.isSetElasticityRules()) return null;
        Vector<ElasticityRuleImpl> vector = new Vector<ElasticityRuleImpl>();
        for (XmlBeanElasticityRuleType rule : delegate.getElasticityRules().getElasticityRuleArray()) {
            vector.add(new ElasticityRuleImpl(rule));
        }
        return vector.toArray(new ElasticityRuleImpl[vector.size()]);
    }

    @Override
    public ElasticityRuleImpl addNewRule(String componentId, String name) {
        if (delegate.isSetSPManagedElasticity()) delegate.unsetSPManagedElasticity();
        if (!delegate.isSetElasticityRules()) delegate.addNewElasticityRules();
        TemplateLoader loader = new TemplateLoader();
        XmlBeanElasticityRuleType newRule = delegate.getElasticityRules().addNewElasticityRule();
        newRule.set(loader.loadElasticityRuleTemplate(componentId, name));
        return new ElasticityRuleImpl(newRule);
    }

    @Override
    public void removeRule(int i) {
        delegate.getElasticityRules().removeElasticityRule(i);
    }

    @Override
    public void setSPManagedElasticity() {
        if (!isSetSPManagedElasticity())
            delegate.addNewSPManagedElasticity();
        delegate.getSPManagedElasticity().setNil();
        //we have to remove all variables and rules
        delegate.unsetVariableSet();
        delegate.unsetElasticityRules();

    }

    @Override
    public boolean isSetSPManagedElasticity() {
        return delegate.isSetSPManagedElasticity();
    }

    @Override
    public ElasticityVariableImpl[] getVariableArray() {
        if (!delegate.isSetVariableSet()) return null;
        Vector<ElasticityVariableImpl> vector = new Vector<ElasticityVariableImpl>();
        for (XmlBeanVariableType type : delegate.getVariableSet().getVariableArray()) {
            vector.add(new ElasticityVariableImpl(type));
        }
        return vector.toArray(new ElasticityVariableImpl[vector.size()]);
    }


    @Override
    public void removeVariable(int i) {
        delegate.getVariableSet().removeVariable(i);
    }

    @Override
    public void removeVariable(String name) {
        for (int i = 0; i < delegate.getVariableSet().getVariableArray().length; i++) {
            if (delegate.getVariableSet().getVariableArray(i).getName().equals(name)) {
                delegate.getVariableSet().removeVariable(i);
            }
        }
    }

    @Override
    public ElasticityVariable addNewInternalVariable(String name, String metric, String location) {
        return addNewVariable(name, metric, XmlBeanElasticityLocationTypeEnum.INTERNAL, location);
    }

    @Override
    public ElasticityVariable addNewExternalVariable(String name, String metric, String location) {
        return addNewVariable(name, metric, XmlBeanElasticityLocationTypeEnum.EXTERNAL, location);
    }

    @Override
    public ElasticityVariableImpl getVariable(int i) {
        return new ElasticityVariableImpl(delegate.getVariableSet().getVariableArray(i));
    }

    @Override
    public ElasticityVariableImpl getVariable(String name) {
        for (int i = 0; i < delegate.getVariableSet().getVariableArray().length; i++) {
            if (delegate.getVariableSet().getVariableArray(i).getName().equals(name)) {
                return new ElasticityVariableImpl(delegate.getVariableSet().getVariableArray(i));
            }
        }
        return null;
    }

    private ElasticityVariableImpl addNewVariable(String name, String metric, XmlBeanElasticityLocationTypeEnum.Enum type, String location) {
        if (delegate.isSetSPManagedElasticity()) delegate.unsetSPManagedElasticity();
        if (!delegate.isSetVariableSet()) delegate.addNewVariableSet();

        TemplateLoader loader = new TemplateLoader();
        XmlBeanVariableType variable = delegate.getVariableSet().addNewVariable();
        variable.set(loader.loadElasticityVariableTemplate(name, metric, type, location));
        return new ElasticityVariableImpl(variable);
    }


}
