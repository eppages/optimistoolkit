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

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import java.util.List;

/**
 * @author arumpl
 */
public class ManifestSplitter {
    XmlBeanServiceManifestDocument currentManifest;
    XmlBeanServiceManifestDocument extractedManifest;

    public ManifestSplitter(XmlBeanServiceManifestDocument manifest) {
        this.currentManifest = (XmlBeanServiceManifestDocument)manifest.copy();
        this.extractedManifest = (XmlBeanServiceManifestDocument)manifest.copy();
    }

    public XmlBeanServiceManifestDocument getCurrentManifest() {
        return currentManifest;
    }

    public XmlBeanServiceManifestDocument getExtractedManifest() {
        return extractedManifest;
    }

    public void splitManifest(String componentId) {
        checkIfSplittingIsAllowed(componentId);
        ManifestParser currentManifestParser = new ManifestParser(currentManifest);
        currentManifestParser.removeComponent(componentId);
        List<String> remainingComponents = currentManifestParser.selectAllComponentIds();

        ManifestParser newManifestParser = new ManifestParser(extractedManifest);
        for (String c : remainingComponents) {
            newManifestParser.removeComponent(c);
        }
        newManifestParser.removeServiceProviderExtensions();
        newManifestParser.removeInfrastructureProviderExtensions();
    }

    private void checkIfSplittingIsAllowed(String componentId) {
        //first we have to check for the affinity constraints
        ManifestParser parser = new ManifestParser(currentManifest);
        List<String> affinityConstraints = parser.selectAllAffinityConstraintsByComponentId(componentId);
        if (affinityConstraints.contains(XmlBeanAffinityConstraintType.HIGH.toString()) || affinityConstraints.contains(XmlBeanAffinityConstraintType.MEDIUM.toString())) {
            throw new SplittingNotAllowedException("Affinity constraints do not permit splitting. One of Medium or High was found for component " + componentId);
        }
        //federation must be allowed
        if (!parser.getManifest().getServiceManifest().getServiceDescriptionSection().getIsFederationAllowed()){
            throw new SplittingNotAllowedException("Federation constraints do not permit splitting. Federation is not allowed.");
        }
    }


}
