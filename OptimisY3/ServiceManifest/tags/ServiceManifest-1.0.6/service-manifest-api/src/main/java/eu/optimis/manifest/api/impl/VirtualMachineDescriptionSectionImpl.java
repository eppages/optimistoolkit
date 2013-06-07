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

import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.sp.VirtualMachineDescriptionSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVirtualMachineComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVirtualMachineDescriptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Karl Catewicz Email: karl.catewicz@scai.fraunhofer.de Date: 08.12.2011
 * Time: 16:30:22
 */
class VirtualMachineDescriptionSectionImpl
        extends AbstractManifestElement<XmlBeanVirtualMachineDescriptionType>
        implements VirtualMachineDescriptionSection,
                   eu.optimis.manifest.api.ip.VirtualMachineDescriptionSection
{

    public VirtualMachineDescriptionSectionImpl( XmlBeanVirtualMachineDescriptionType base )
    {
        super( base );
    }

    @Override
    public AffinityRuleImpl[] getAffinityRules()
    {
        List<AffinityRuleImpl> affinityArray = new ArrayList<AffinityRuleImpl>();
        for ( XmlBeanAffinityRuleType affinityType : delegate.getAffinitySection()
                .getAffinityRuleArray() )
        {
            affinityArray.add( new AffinityRuleImpl( affinityType ) );
        }
        return affinityArray.toArray( new AffinityRuleImpl[ affinityArray.size() ] );
    }

    @Override
    public AffinityRuleImpl getAffinityRule( int i )
    {
        return new AffinityRuleImpl( delegate.getAffinitySection().getAffinityRuleArray( i ) );
    }

    @Override
    @Deprecated
    public AffinityRuleImpl addNewAffinityRule( String componentId, String affinityLevel )
    {
        String[] scope = { componentId };
        return addNewAffinityRule( scope, affinityLevel );
    }

    public AffinityRuleImpl addNewAffinityRule( String[] componentIds, String affinityLevel )
    {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanAffinityRuleType affintiyRule = delegate.getAffinitySection().addNewAffinityRule();
        affintiyRule.set( loader.loadAffinityTemplate( componentIds, affinityLevel ) );
        return new AffinityRuleImpl( affintiyRule );
    }

    @Override
    public void removeAffinityRule( int i )
    {
        delegate.getAffinitySection().removeAffinityRule( i );
    }

    @Override
    public VirtualMachineComponentImpl[] getVirtualMachineComponentArray()
    {
        List<VirtualMachineComponentImpl> vmArray = new ArrayList<VirtualMachineComponentImpl>();
        for ( XmlBeanVirtualMachineComponentType type : delegate.getVirtualMachineComponentArray() )
        {
            vmArray.add( new VirtualMachineComponentImpl( type ) );
        }
        return vmArray.toArray( new VirtualMachineComponentImpl[ vmArray.size() ] );
    }

    @Override
    public VirtualMachineComponentImpl getVirtualMachineComponentArray( int i )
    {
        return new VirtualMachineComponentImpl( delegate.getVirtualMachineComponentArray( i ) );
    }

    @Override
    public VirtualMachineComponentImpl getVirtualMachineComponentById( String componentId )
    {
        for ( XmlBeanVirtualMachineComponentType component : delegate
                .getVirtualMachineComponentArray() )
        {
            if ( component.getComponentId().equals( componentId ) )
            {
                return new VirtualMachineComponentImpl( component );
            }
        }
        return null;
    }

    @Override
    public VirtualMachineComponent addNewVirtualMachineComponent( String componentId )
    {
        // check if there is no other component with this id
        if ( getVirtualMachineComponentById( componentId ) != null )
        {
            throw new RuntimeException( "A component with this ID already exists." );
        }

        // load template
        TemplateLoader loader = new TemplateLoader();

        // add new VM component section & set it from template document
        XmlBeanVirtualMachineComponentType virtualMachineComponentType =
                delegate.addNewVirtualMachineComponent();
        virtualMachineComponentType
                .set( loader.loadVirtualMachineComponentTemplate( delegate.getServiceId(),
                        componentId ) );

        return new VirtualMachineComponentImpl( virtualMachineComponentType );
    }

    // TODO the component should only be removed if all references have been removed before.
    @Override
    public void removeVirtualMachineComponentById( String componentId )
    {
        XmlBeanVirtualMachineComponentType[] componentArray =
                delegate.getVirtualMachineComponentArray();
        for ( int i = 0; i < componentArray.length; i++ )
        {
            // remove element if componentId found
            if ( componentArray[ i ].getComponentId().equals( componentId ) )
            {
                delegate.removeVirtualMachineComponent( i );
                break;
            }
        }
    }

    @Override
    public String getServiceId()
    {
        return delegate.getServiceId();
    }

    @Override
    public void setServiceId( String serviceId )
    {
        delegate.setServiceId( serviceId );
    }

    @Override
    public void setIsFederationAllowed( boolean isFederationAllowed )
    {
        delegate.setIsFederationAllowed( isFederationAllowed );
    }

    @Override
    public boolean isFederationAllowed()
    {
        return delegate.getIsFederationAllowed();
    }
}
