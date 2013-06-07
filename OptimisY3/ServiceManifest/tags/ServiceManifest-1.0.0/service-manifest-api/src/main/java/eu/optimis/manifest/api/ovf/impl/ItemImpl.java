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
import eu.optimis.manifest.api.ovf.ip.Item;
import org.dmtf.schemas.ovf.envelope.x1.RASDType;
import org.dmtf.schemas.wbem.wscim.x1.common.CimString;

import java.math.BigInteger;
import java.util.Vector;

/**
 * @author arumpl
 */
class ItemImpl extends AbstractManifestElement<RASDType> implements Item, eu.optimis.manifest.api.ovf.sp.Item {
    public ItemImpl(RASDType base) {
        super(base);
    }

    @Override
    public String getDescription() {
        if (delegate.isSetDescription())
            return delegate.getDescription().getStringValue();
        return null;
    }

    @Override
    public String getElementName() {
        return delegate.getElementName().getStringValue();
    }

    @Override
    public String getInstanceID() {
        return delegate.getInstanceID().getStringValue();
    }

    @Override
    public int getResourceType() {
        return delegate.getResourceType().getIntValue();

    }

    @Override
    public BigInteger getVirtualQuantity() {
        if (delegate.isSetVirtualQuantity()) {
            return delegate.getVirtualQuantity().getBigIntegerValue();
        }
        return null;
    }

    @Override
    public String getAllocationUnits() {
        if (delegate.isSetAllocationUnits())
            return delegate.getAllocationUnits().getStringValue();
        return null;
    }

    @Override
    public Boolean getAutomaticAllocation() {
        if (delegate.isSetAutomaticAllocation())
            return delegate.getAutomaticAllocation().getBooleanValue();
        return null;
    }

    @Override
    public String[] getConnectionArray() {
        Vector<String> vector = new Vector<String>();
        for (CimString type : delegate.getConnectionArray()) {
            vector.add(type.getStringValue());
        }
        return vector.toArray(new String[vector.size()]);
    }

    @Override
    public String getConnectionArray(int i) {
        return delegate.getConnectionArray(i).getStringValue();
    }

    @Override
    public String getParent() {
        if (delegate.isSetParent()) return delegate.getParent().getStringValue();
        return null;
    }

    @Override
    public String[] getHostResourceArray() {
        Vector<String> vector = new Vector<String>();
        for (CimString cimString : delegate.getHostResourceArray()) {
            vector.add(cimString.getStringValue());
        }
        return vector.toArray(new String[vector.size()]);
    }

    @Override
    public String getHostResourceArray(int i) {
        if (delegate.getHostResourceArray().length > i)
            return delegate.getHostResourceArray(i).getStringValue();
        return null;
    }

}
