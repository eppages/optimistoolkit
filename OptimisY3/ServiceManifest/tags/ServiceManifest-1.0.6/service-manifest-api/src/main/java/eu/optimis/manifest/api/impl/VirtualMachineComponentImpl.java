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

import eu.optimis.manifest.api.ovf.impl.OVFDefinitionImpl;
import eu.optimis.manifest.api.sp.ServiceEndpoint;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.manifest.api.utils.FileRefIncarnator;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceEndPointType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanVirtualMachineComponentType;
import org.dmtf.schemas.ovf.envelope.x1.XmlBeanFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * karl.catewicz@scai.fraunhofer.de Date: 20.12.2011 Time: 13:22:04
 */
class VirtualMachineComponentImpl
        extends AbstractManifestElement<XmlBeanVirtualMachineComponentType>
        implements VirtualMachineComponent, eu.optimis.manifest.api.ip.VirtualMachineComponent
{

    public VirtualMachineComponentImpl( XmlBeanVirtualMachineComponentType base )
    {
        super( base );
    }

    @Override
    public OVFDefinitionImpl getOVFDefinition()
    {
        return new OVFDefinitionImpl( delegate.getOVFDefinition() );
    }

    @Override
    public AllocationConstraintImpl getAllocationConstraints()
    {
        return new AllocationConstraintImpl( delegate.getAllocationConstraints() );
    }

    @Override
    public String getAffinityConstraints()
    {
        return delegate.getAffinityConstraints().toString();
    }

    @Override
    public void setAffinityConstraints( String constraint )
    {
        delegate.setAffinityConstraints(
                XmlBeanAffinityConstraintType.Enum.forString( constraint ) );
    }

    @Override
    public String getComponentId()
    {
        return delegate.getComponentId();
    }

    @Override
    public ServiceEndpointImpl[] getServiceEndpoints()
    {
        List<ServiceEndpointImpl> endpoints = new ArrayList<ServiceEndpointImpl>();
        for ( XmlBeanServiceEndPointType type : delegate.getServiceEndpoints()
                .getServiceEndpointArray() )
        {
            endpoints.add( new ServiceEndpointImpl( type ) );
        }
        return endpoints.toArray( new ServiceEndpointImpl[ endpoints.size() ] );
    }

    @Override
    public ServiceEndpoint addNewServiceEndPoint( String name, String uri )
    {
        ServiceEndpoint endpoint =
                new ServiceEndpointImpl( delegate.getServiceEndpoints().addNewServiceEndpoint() );
        endpoint.setName( name );
        endpoint.setURI( uri );
        return endpoint;
    }

    @Override
    public void removeServiceEndpoint( int i )
    {
        delegate.getServiceEndpoints().removeServiceEndpoint( i );
    }

    @Override
    public String[] getIncarnatedContextualizationFileArray()
    {

        List<String> files = new ArrayList<String>();

        // we need only the contextualization files which are on the second position
        XmlBeanFileType templateFile =
                delegate.getOVFDefinition().getReferences().getFileArray( 1 );
        for ( int i = 1; i <= delegate.getAllocationConstraints().getUpperBound(); i++ )
        {
            files.add( FileRefIncarnator.updateHrefWithIndex( templateFile.getHref(), i ) );
        }

        return files.toArray( new String[ files.size() ] );
    }
}
