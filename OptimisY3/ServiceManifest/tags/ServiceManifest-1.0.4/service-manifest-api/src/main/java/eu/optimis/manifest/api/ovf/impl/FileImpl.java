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

package eu.optimis.manifest.api.ovf.impl;

import eu.optimis.manifest.api.impl.AbstractManifestElement;
import eu.optimis.manifest.api.ovf.ip.File;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanFileType;

import java.math.BigInteger;

/**
 * @author karl.catewicz@scai.fraunhofer.de
 */
class FileImpl extends AbstractManifestElement<XmlBeanFileType> implements File, eu.optimis.manifest.api.ovf.sp.File {

    // FileType ft;
    public FileImpl(XmlBeanFileType base) {
        super(base);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getHref() {
        return delegate.getHref();
    }

    @Override
    public void setHref(String href) {
        delegate.setHref(href);
    }

    @Override
    public BigInteger getSize() {
        return delegate.getSize();
    }

    @Override
    public void setSize(BigInteger size) {
        delegate.setSize(size);
    }

    @Override
    public String getCompression() {
        return delegate.getCompression();
    }

    @Override
    public void setCompression(String compression) {
        delegate.setCompression(compression);
    }

    @Override
    public long getChunkSize() {
        return delegate.getChunkSize();
    }

    @Override
    public void setChunkSize(long chunkSize) {
        delegate.setChunkSize(chunkSize);
    }

}
