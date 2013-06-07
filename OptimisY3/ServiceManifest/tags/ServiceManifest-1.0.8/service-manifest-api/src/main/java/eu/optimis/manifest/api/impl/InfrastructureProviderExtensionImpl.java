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
 * 
 */
package eu.optimis.manifest.api.impl;

import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.manifest.api.ovf.impl.VirtualSystemImpl;
import eu.optimis.manifest.api.ovf.ip.VirtualSystem;
import eu.optimis.manifest.api.utils.ManifestElementParser;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanIncarnatedVirtualMachineComponentsType;
import eu.optimis.types.xmlbeans.servicemanifest.infrastructure.XmlBeanInfrastructureProviderExtensionType;
import org.apache.xmlbeans.XmlObject;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanVirtualSystemType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arumpl
 */
class InfrastructureProviderExtensionImpl
        extends AbstractManifestElement<XmlBeanInfrastructureProviderExtensionType>
        implements eu.optimis.manifest.api.ip.InfrastructureProviderExtension
{

    public InfrastructureProviderExtensionImpl( XmlBeanInfrastructureProviderExtensionType base )
    {
        super( base );
    }

    @Override
    public IncarnatedVirtualMachineComponentImpl[] getIncarnatedVirtualMachineComponents()
    {
        if ( delegate.getIncarnatedServiceComponents() == null )
        {
            return null;
        }
        XmlBeanIncarnatedVirtualMachineComponentsType componentsType =
                ( XmlBeanIncarnatedVirtualMachineComponentsType ) delegate
                        .getIncarnatedServiceComponents();
        if ( componentsType != null )
        {
            List<IncarnatedVirtualMachineComponentImpl> vector =
                    new ArrayList<IncarnatedVirtualMachineComponentImpl>();

            XmlBeanIncarnatedVirtualMachineComponentType[] vComponentArray =
                    componentsType.getIncarnatedVirtualMachineComponentArray();
            for ( XmlBeanIncarnatedVirtualMachineComponentType type : vComponentArray )
            {
                vector.add( new IncarnatedVirtualMachineComponentImpl( type ) );
            }

            return vector.toArray( new IncarnatedVirtualMachineComponentImpl[ vector.size() ] );
        }
        return null;
    }

    @Override
    public IncarnatedVirtualMachineComponent getIncarnatedVirtualMachineComponents( int i )
    {
        XmlBeanIncarnatedVirtualMachineComponentsType componentsType =
                ( XmlBeanIncarnatedVirtualMachineComponentsType ) delegate
                        .getIncarnatedServiceComponents();
        if ( componentsType != null )
        {
            return new IncarnatedVirtualMachineComponentImpl(
                    componentsType.getIncarnatedVirtualMachineComponentArray( i ) );
        }
        return null;
    }

    @Override
    public IncarnatedVirtualMachineComponentImpl
    getIncarnatedVirtualMachineComponentByComponentId( String componentId )
    {
        IncarnatedVirtualMachineComponentImpl[] components =
                getIncarnatedVirtualMachineComponents();
        for ( IncarnatedVirtualMachineComponentImpl component : components )
        {
            if ( component.getComponentId().equals( componentId ) )
            {
                return component;
            }
        }
        return null;
    }

    @Override
    public VirtualSystem getVirtualSystem( String virtualSystemId )
    {
        XmlObject[] objects =
                ManifestElementParser.selectObjects( delegate, "$this//ovf:VirtualSystem[@ovf:id='"
                                                               + virtualSystemId + "']" );
        if ( objects.length > 0 )
        {
            return new VirtualSystemImpl( ( XmlBeanVirtualSystemType ) objects[ 0 ] );
        }
        else
        {
            throw new IllegalArgumentException(
                    "No virtual system could be found for ID " + virtualSystemId );
        }
    }

    @Override
    public AllocationOfferImpl getAllocationOffer()
    {
        if ( !delegate.isSetAllocationOffer() )
        {
            delegate.addNewAllocationOffer();
        }
        return new AllocationOfferImpl( delegate.getAllocationOffer() );
    }

    @Override
    public AllocationOfferImpl addNewAllocationOffer()
    {
        return new AllocationOfferImpl( delegate.addNewAllocationOffer() );
    }

    @Override
    public boolean isSetAllocationOffer()
    {
        return delegate.isSetAllocationOffer();
    }

    @Override
    public void unsetAllocationOffer()
    {
        delegate.unsetAllocationOffer();
    }

    @Override
    public void unsetIncarnatedVirtualMachineComponents()
    {
        delegate.unsetIncarnatedServiceComponents();
    }

    @Override
    public boolean isSetIncarnatedVirtualMachineComponents()
    {
        return delegate.isSetIncarnatedServiceComponents();
    }
}
