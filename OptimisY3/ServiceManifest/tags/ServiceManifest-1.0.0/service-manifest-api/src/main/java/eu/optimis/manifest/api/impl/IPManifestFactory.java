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
import eu.optimis.manifest.api.ip.ManifestFactory;
import eu.optimis.manifest.api.utils.XmlValidator;
import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.ServiceManifestDocument;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * @author arumpl
 */
public class IPManifestFactory implements ManifestFactory {

    @Override
    public Manifest newInstance(ServiceManifestDocument manifestAsXmlBeans) {
        if (!XmlValidator.validate(manifestAsXmlBeans)) {
            throw new RuntimeException("Document to be imported is invalid!");
        }
        return new ManifestImpl(manifestAsXmlBeans);
    }

    @Override
    public Manifest newInstance(JaxBServiceManifest manifestAsJaxB) {
        try {
            java.io.StringWriter sw = new StringWriter();
            JAXBContext jc = null;

            jc = JAXBContext.newInstance(new Class[]{JaxBServiceManifest.class});

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(manifestAsJaxB, sw);
            String serializedDocument = sw.toString();
            ServiceManifestDocument doc = ServiceManifestDocument.Factory.parse(serializedDocument);
            if (!XmlValidator.validate(doc)) {
                throw new RuntimeException("Document to be imported is invalid!");
            }
            return new ManifestImpl(doc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Manifest newInstance(String manifestAsString) {
        try {
            ServiceManifestDocument newDoc = ServiceManifestDocument.Factory.parse(manifestAsString);
            if (!XmlValidator.validate(newDoc)) {
                throw new RuntimeException("Document to be imported is invalid!");
            }
            return new ManifestImpl(newDoc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
