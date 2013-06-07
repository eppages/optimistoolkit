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
import eu.optimis.manifest.api.ovf.ip.DiskSection;
import eu.optimis.manifest.api.ovf.sp.VirtualDiskDesc;
import eu.optimis.manifest.api.utils.XmlSimpleTypeConverter;
import org.dmtf.schemas.ovf.envelope.x1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope.x1.VirtualDiskDescType;

import java.util.Vector;

/**
 * @author kcatewicz
 */
class DiskSectionImpl extends AbstractManifestElement<DiskSectionType>
        implements DiskSection, eu.optimis.manifest.api.ovf.sp.DiskSection {


    public DiskSectionImpl(DiskSectionType base) {
        super(base);
    }

    @Override
    public VirtualDiskDescImpl[] getDiskArray() {
        Vector<VirtualDiskDescImpl> diskArray = new Vector();
        for (VirtualDiskDescType diskSectionType : delegate.getDiskArray()) {
            diskArray.add(new VirtualDiskDescImpl(diskSectionType));
        }
        return diskArray.toArray(new VirtualDiskDescImpl[diskArray.size()]);
    }

    @Override
    public VirtualDiskDescImpl getDiskArray(int i) {
        return new VirtualDiskDescImpl(delegate.getDiskArray(i));
    }


    @Override
    public VirtualDiskDesc getDisk() {
        return new VirtualDiskDescImpl(delegate.getDiskArray(0));
    }


    @Override
    public String getInfo() {
        return delegate.getInfo().getStringValue();
    }

    @Override
    public void setInfo(String info) {
        delegate.setInfo(XmlSimpleTypeConverter.toMsgType(info));
    }

}
