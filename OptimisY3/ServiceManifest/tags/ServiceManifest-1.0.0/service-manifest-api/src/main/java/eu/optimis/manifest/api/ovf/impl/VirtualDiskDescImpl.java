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
import eu.optimis.manifest.api.ovf.ip.VirtualDiskDesc;
import org.dmtf.schemas.ovf.envelope.x1.VirtualDiskDescType;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 20.12.2011 Time: 13:42:42
 */
class VirtualDiskDescImpl extends AbstractManifestElement<VirtualDiskDescType> implements VirtualDiskDesc, eu.optimis.manifest.api.ovf.sp.VirtualDiskDesc {
    // VirtualDiskDescType vddt;
    public VirtualDiskDescImpl( VirtualDiskDescType base )
    {
        super( base );
    }

    @Override
    public String getDiskId()
    {
        return delegate.getDiskId();
    }

    @Override
    public void setDiskId(String diskId)
    {
        delegate.setDiskId( diskId );
    }

    @Override
    public String getFileRef()
    {
        return delegate.getFileRef();
    }

    @Override
    public void setFileRef(String fileRef)
    {
        delegate.setFileRef( fileRef );
    }

    @Override
    public String getCapacity()
    {
        return delegate.getCapacity();
    }

    @Override
    public void setCapacity(String capacity)
    {
        delegate.setCapacity( capacity );
    }

    @Override
    public String getCapacityAllocationUnits()
    {
        return delegate.getCapacityAllocationUnits();
    }

    @Override
    public void setCapacityAllocationUnits(String capacityAllocationUnits)
    {
        delegate.setCapacityAllocationUnits( capacityAllocationUnits );
    }

    @Override
    public String getFormat()
    {
        return delegate.getFormat();
    }

    @Override
    public void setFormat(String format)
    {
        delegate.setFormat( format );
    }

    @Override
    public long getPopulatedSize()
    {
        return delegate.getPopulatedSize();
    }

    @Override
    public void setPopulatedSize(long populatedSize)
    {
        delegate.setPopulatedSize( populatedSize );
    }

    @Override
    public String getParentRef()
    {
        return delegate.getParentRef();
    }

    @Override
    public void setParentRef(String parentRef)
    {
        delegate.setParentRef( parentRef );
    }

}
