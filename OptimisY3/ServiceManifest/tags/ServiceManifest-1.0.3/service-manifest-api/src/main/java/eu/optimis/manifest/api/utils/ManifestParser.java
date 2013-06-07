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

import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityRuleDocument;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

/**
 * @author arumpl
 */
public class ManifestParser {
    private static final String namespaceDeclaration = "declare namespace opt='http://schemas.optimis.eu/optimis/'; ";

    XmlBeanServiceManifestDocument manifest;

    public ManifestParser(XmlBeanServiceManifestDocument manifest) {
        this.manifest = manifest;
    }

    public XmlBeanElasticityRuleDocument[] copyAllElasticityRules(String componentId) {
        // The expression: Get the  ElasticityRule with  elements.
        String queryExpression =
                "for $e in $this//opt:ElasticityRule " +
                        "where $e/opt:Scope/opt:ComponentId = '" + componentId + "' " +
                        "return $e";
        return (XmlBeanElasticityRuleDocument[]) manifest.execQuery(namespaceDeclaration + queryExpression);
    }

    public XmlBeanElasticityRuleType[] selectAllElasticityRules(String componentId) {
        XmlObject[] rules = manifest.selectPath(namespaceDeclaration + "$this//opt:ElasticityRule[opt:Scope/opt:ComponentId = '" + componentId + "']");
        return ((XmlBeanElasticityRuleType[]) rules);
    }

    public void removeComponentIdFromScope(String componentId) {
        XmlObject[] selected = manifest.selectPath(namespaceDeclaration + "$this//opt:Scope[opt:ComponentId ='" + componentId + "']");
        System.out.println(selected[0].xmlText());

        for (XmlObject object : selected) {
            Node element = object.getDomNode();
            Node parent = element.getParentNode();
            parent.removeChild(element);
        }
    }


}
