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

package eu.optimis.manifest.api.utils;

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arumpl
 */
public class ManifestParser extends ManifestElementParser{


    public ManifestParser(XmlBeanServiceManifestDocument manifest) {
        super(manifest);
    }

    public XmlBeanServiceManifestDocument getManifest() {
        return (XmlBeanServiceManifestDocument) getManifestElement();
    }

    public List<String> selectAllComponentIds() {
        List<String> componentIds = new ArrayList<String>();
        for (XmlObject o : selectObjects("$this//opt:VirtualMachineComponent/@opt:componentId")) {
            componentIds.add(o.getDomNode().getNodeValue());
        }
        return componentIds;
    }

    public List<String> selectAllAffinityConstraintsByComponentId(String componentId) {
        List<String> affinityConstraints = new ArrayList<String>();
        for (XmlObject o : selectObjects("$this//opt:AffinityRule[opt:Scope/opt:ComponentId = '" + componentId + "']")) {
            XmlBeanAffinityRuleType rule = (XmlBeanAffinityRuleType) o;
            affinityConstraints.add(rule.getAffinityConstraints().toString());
        }
        return affinityConstraints;
    }

    public void removeComponent(String componentId) {
        deleteObjects(selectVirtualMachineComponent(componentId));
        deleteObjects(selectIncarnatedVirtualMachineComponent(componentId));
        deleteObjects(selectElasticityRulesWithEmptyScopeArray());
        deleteObjects(selectScope(componentId));
        deleteObjects(selectPricePlansWithEmptyScopeArray());
        deleteObjects(selectAffinityRulesWithEmptyScopeArray());
        deleteObjects(selectElasticityRulesWithEmptyScopeArray());
    }

    public XmlObject[] selectScope(String componentId) {
        return selectObjects("$this//opt:Scope/opt:ComponentId[. ='" + componentId + "']");
    }

    public XmlObject[] selectPricePlansWithEmptyScopeArray() {
        return selectObjects("$this//opt:PricePlan[not(opt:Scope/opt:ComponentId)]");
    }

    public XmlObject[] selectAffinityRulesWithEmptyScopeArray() {
        return selectObjects("$this//opt:AffinityRule[not(opt:Scope/opt:ComponentId)]");
    }

    public XmlObject[] selectElasticityRulesWithEmptyScopeArray() {
        return selectObjects("$this//opt:ElasticityRule[not(opt:Scope/opt:ComponentId)]");
    }

    public XmlObject[] selectVirtualMachineComponent(String componentId) {
        return selectObjects("$this//opt:VirtualMachineComponent[@opt:componentId ='" + componentId + "']");
    }

    public XmlObject[] selectVirtualMachineComponentConfiguration(String componentId) {
        return selectObjects("$this//opt:VirtualMachineComponentConfiguration[@opt-sp:componentId ='" + componentId + "']");

    }

    public XmlObject[] selectIncarnatedVirtualMachineComponent(String componentId) {
        return selectObjects("$this//opt-ip:IncarnatedVirtualMachineComponent[@opt:componentId ='" + componentId + "']");
    }

    public XmlObject[] selectServiceProviderExtensions() {
        return selectObjects("$this//opt-sp:ServiceProviderExtensions");
    }

    public XmlObject[] selectInfrastructureProviderExtensions() {
        return selectObjects("$this//opt-ip:InfrastructureProviderExtensions");
    }

    public void removeServiceProviderExtensions() {
        deleteObjects(selectServiceProviderExtensions());
    }

    public void removeInfrastructureProviderExtensions() {
        deleteObjects(selectInfrastructureProviderExtensions());
    }
}
