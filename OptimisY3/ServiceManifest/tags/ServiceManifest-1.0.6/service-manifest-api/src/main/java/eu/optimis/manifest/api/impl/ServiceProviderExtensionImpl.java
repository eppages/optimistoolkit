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

import eu.optimis.manifest.api.sp.ServiceProviderExtension;
import eu.optimis.manifest.api.sp.VirtualMachineComponentConfiguration;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanServiceProviderExtensionType;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanVirtualMachineComponentConfigurationType;

import java.util.Vector;

/**
 * @author arumpl
 */
class ServiceProviderExtensionImpl
        extends AbstractManifestElement<XmlBeanServiceProviderExtensionType>
        implements ServiceProviderExtension
{

    public ServiceProviderExtensionImpl( XmlBeanServiceProviderExtensionType base )
    {

        super( base );
    }

    @Override
    public String getSLAID()
    {
        return delegate.getSLAID();
    }

    @Override
    public void setSLAID( String newSLAID )
    {
        delegate.setSLAID( newSLAID );
    }

    @Override
    public boolean isSetSLAID()
    {
        return delegate.isSetSLAID();
    }

    @Override
    public void unsetSLAID()
    {
        delegate.unsetSLAID();
    }

    @Override
    public boolean isSetDataManagerKey()
    {
        return delegate.isSetDataManagerKey();
    }

    @Override
    public void setDataManagerKey( byte[] dataManagerKey )
    {
        delegate.setDataManagerKey( dataManagerKey );
    }

    @Override
    public byte[] getDataManagerKey()
    {
        return delegate.getDataManagerKey();
    }

    @Override
    public void unsetDataManagerKey()
    {
        delegate.unsetDataManagerKey();
    }

    @Override
    public VirtualMachineComponentConfiguration[] getVirtualMachineComponentConfigurationArray()
    {
        Vector<VirtualMachineComponentConfiguration> vector =
                new Vector<VirtualMachineComponentConfiguration>();

        XmlBeanVirtualMachineComponentConfigurationType[] configurationArray =
                delegate.getVirtualMachineComponentConfigurationArray();
        for ( XmlBeanVirtualMachineComponentConfigurationType type : configurationArray )
        {
            vector.add( new VirtualMachineComponentConfigurationImpl( type ) );
        }
        return vector.toArray( new VirtualMachineComponentConfiguration[ vector.size() ] );
    }

    @Override
    public VirtualMachineComponentConfiguration getVirtualMachineComponentConfiguration(
            String componentId )
    {
        XmlBeanVirtualMachineComponentConfigurationType[] configurationArray =
                delegate.getVirtualMachineComponentConfigurationArray();
        for ( XmlBeanVirtualMachineComponentConfigurationType type : configurationArray )
        {
            if ( type.getComponentId().equals( componentId ) )
            {
                return new VirtualMachineComponentConfigurationImpl( type );
            }
        }
        return null;
    }

    @Override
    public VirtualMachineComponentConfiguration
    addNewVirtualMachineComponentConfiguration( String componentId )
    {
        if ( getVirtualMachineComponentConfiguration( componentId ) != null )
        {
            throw new RuntimeException( "Configuration for this component already exists!" );
        }

        TemplateLoader loader = new TemplateLoader();
        XmlBeanVirtualMachineComponentConfigurationType config =
                delegate.addNewVirtualMachineComponentConfiguration();
        config.set( loader.loadVirtualMachineComponentConfigurationTemplate( componentId ) );
        return new VirtualMachineComponentConfigurationImpl( config );
    }

    @Override
    public void removeVirtualMachineComponentConfiguration( String componentId )
    {
        for ( int i = 0; i <= delegate.getVirtualMachineComponentConfigurationArray().length; i++ )
        {
            if ( delegate.getVirtualMachineComponentConfigurationArray( i ).getComponentId()
                    .equals( componentId ) )
            {
                delegate.removeVirtualMachineComponentConfiguration( i );
            }
        }
    }
}
