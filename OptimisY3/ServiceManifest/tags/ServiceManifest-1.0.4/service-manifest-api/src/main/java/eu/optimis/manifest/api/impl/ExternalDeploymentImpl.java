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

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.ExternalDeployment;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanManifestType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanExternalDeploymentType;

/**
 * @author arumpl
 */
public class ExternalDeploymentImpl extends AbstractManifestElement<XmlBeanExternalDeploymentType> implements ExternalDeployment {
    public ExternalDeploymentImpl(XmlBeanExternalDeploymentType base) {
        super(base);
    }

    @Override
    public ManifestImpl exportServiceManifest() {
        XmlBeanServiceManifestDocument doc = XmlBeanServiceManifestDocument.Factory.newInstance();
        doc.setServiceManifest(delegate.getServiceManifest());
        return new ManifestImpl(doc);
    }

    @Override
    public void setProviderId(String providerId) {
        delegate.setProviderId(providerId);
    }

    @Override
    public void replaceServiceManifest(Manifest serviceManifest) {
        delegate.setServiceManifest(serviceManifest.toXmlBeanObject().getServiceManifest());
    }


    @Override
    public String getProviderId() {
        return delegate.getProviderId();
    }
}
