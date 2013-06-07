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

import eu.optimis.manifest.api.sp.EncryptionLevel;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionLevelType;
import org.apache.xmlbeans.XmlObject;

/**
 * User: Karl Catewicz Date: 08.12.2011 Time: 17:04:54
 */
class EncryptionLevelImpl extends AbstractManifestElement<XmlBeanEncryptionLevelType>
        implements EncryptionLevel, eu.optimis.manifest.api.ip.EncryptionLevel {

    public EncryptionLevelImpl(XmlBeanEncryptionLevelType base) {
        super(base);
    }


    @Override
    public String getEncryptionAlgorithm() {
        return delegate.getEncryptionAlgoritm().toString();
    }

    @Override
    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        delegate.setEncryptionAlgoritm(XmlBeanEncryptionAlgoritmType.Enum.forString(encryptionAlgorithm));
    }

    @Override
    public int getEncryptionKeySize() {
        return delegate.getEncryptionKeySize();
    }

    @Override
    public void setEncryptionKeySize(int encryptionKeySize) {
        delegate.setEncryptionKeySize(encryptionKeySize);
    }


    @Override
    public XmlObject getCustomEncryptionLevel() {
        return delegate.getCustomEncryptionLevel();
    }

}
