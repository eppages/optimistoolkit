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
import eu.optimis.manifest.api.ovf.ip.OVFDefinition;
import eu.optimis.manifest.api.ovf.ip.OVFDefinitionIncarnated;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import org.dmtf.schemas.ovf.envelope.x1.*;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 20.12.2011 Time: 13:30:14
 */
public class OVFDefinitionImpl extends AbstractManifestElement<EnvelopeType>
        implements OVFDefinition, eu.optimis.manifest.api.ovf.sp.OVFDefinition, OVFDefinitionIncarnated {

    // EnvelopeType et;
    public OVFDefinitionImpl(EnvelopeType base) {
        super(base);
    }

    @Override
    public ReferencesImpl getReferences() {
        return new ReferencesImpl(delegate.getReferences());
    }

    @Override
    public DiskSectionImpl getDiskSection() {
        return new DiskSectionImpl((DiskSectionType) delegate.getSectionArray(0));
    }

    @Override
    public NetworkSectionImpl getNetworkSection() {
        return new NetworkSectionImpl((NetworkSectionType) delegate.getSectionArray(1));
    }

    @Override
    public VirtualSystemImpl getVirtualSystem() {
        return new VirtualSystemImpl((VirtualSystemType) delegate.getContent());
    }

    @Override
    public VirtualSystemImpl[] getVirtualSystemCollection() {
        Vector<VirtualSystemImpl> vector = new Vector<VirtualSystemImpl>();
        VirtualSystemCollectionType collectionType = (VirtualSystemCollectionType) delegate.getContent();
        if (collectionType != null) {
            for (ContentType contentType : collectionType.getContentArray()) {
                vector.add(new VirtualSystemImpl((VirtualSystemType) contentType));
            }
            return vector.toArray(new VirtualSystemImpl[vector.size()]);
        }
        return null;
    }

    @Override
    public VirtualSystem getVirtualSystemCollection(int i) {
        VirtualSystemCollectionType collectionType = (VirtualSystemCollectionType) delegate.getContent();
        if (collectionType != null) {
            return new VirtualSystemImpl((VirtualSystemType) collectionType.getContentArray(i));
        }
        return null;
    }

    @Override
    public VirtualSystem getVirtualSystemByComponentId(String componentId) {
        for (VirtualSystemImpl system : getVirtualSystemCollection()) {
            if (system.getId().contains(componentId)) {
                return system;
            }
        }
        return null;
    }


}
