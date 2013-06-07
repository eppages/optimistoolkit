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

import eu.optimis.manifest.api.sp.Scope;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanScopeArrayType;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 09.12.2011 Time: 16:48:03
 */
class ScopeImpl extends AbstractManifestElement<XmlBeanScopeArrayType>
    implements Scope, eu.optimis.manifest.api.ip.Scope
{

    public ScopeImpl( XmlBeanScopeArrayType base )
    {
        super( base );
    }

    @Override
    public String[] getComponentIdArray()
    {
        return delegate.getComponentIdArray();
    }

    @Override
    public void setComponentIdArray( String[] componentIdArray )
    {
        unsetComponentIdArray();
        addComponentIdArray( componentIdArray );
    }

    @Override
    public String getComponentIdArray( int i )
    {
        return delegate.getComponentIdArray( i );
    }

    @Override
    public boolean contains( String componentId )
    {
        for ( String c : delegate.getComponentIdArray() )
        {
            if ( c.equals( componentId ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addComponentId( String componentId )
    {
        delegate.addNewComponentId().setStringValue( componentId );
    }

    @Override
    public void removeComponentId( int i )
    {
        delegate.removeComponentId( i );
    }

    @Override
    public void removeComponentId( String componentId )
    {
        for ( int i = 0; i < delegate.getComponentIdArray().length; i++ )
        {
            if ( delegate.getComponentIdArray( i ).equals( componentId ) )
            {
                delegate.removeComponentId( i );
            }
        }
    }

    private void addComponentIdArray( String[] componentIdArray )
    {
        for ( String componentId : componentIdArray )
        {
            delegate.addComponentId( componentId );
        }
    }

    private void unsetComponentIdArray()
    {
        for ( int i = 0; i < delegate.getComponentIdArray().length; i++ )
        {
            delegate.removeComponentId( i );
        }
    }
}
