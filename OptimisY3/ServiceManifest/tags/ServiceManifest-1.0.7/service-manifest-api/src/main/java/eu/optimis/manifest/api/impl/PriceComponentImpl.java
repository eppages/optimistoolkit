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

import java.util.Vector;

import eu.optimis.manifest.api.sp.PriceComponent;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanPriceComponentType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanPriceLevelType;

/**
 * @author arumpl
 */
public class PriceComponentImpl extends AbstractManifestElement<XmlBeanPriceComponentType>
    implements PriceComponent, eu.optimis.manifest.api.ip.PriceComponent
{

    public PriceComponentImpl( XmlBeanPriceComponentType base )
    {
        super( base );
    }

    @Override
    public PriceLevelImpl[] getPriceLevelArray()
    {
        Vector<PriceLevelImpl> vector = new Vector<PriceLevelImpl>();
        for ( XmlBeanPriceLevelType priceLevelType : delegate.getPriceLevelArray() )
        {
            vector.add( new PriceLevelImpl( priceLevelType ) );
        }
        return vector.toArray( new PriceLevelImpl[vector.size()] );
    }

    @Override
    public PriceLevelImpl getPriceLevelArray( int i )
    {
        return new PriceLevelImpl( delegate.getPriceLevelArray( i ) );
    }

    @Override
    public float getComponentCap()
    {
        return delegate.getComponentCap();
    }

    @Override
    public void setComponentCap( float componentCap )
    {
        delegate.setComponentCap( componentCap );
    }

    @Override
    public float getComponentFloor()
    {
        return delegate.getComponentFloor();
    }

    @Override
    public void setComponentFloor( float componentFloor )
    {
        delegate.setComponentFloor( componentFloor );
    }

    @Override
    public String getName()
    {
        return delegate.getName();
    }

    @Override
    public void setName( String name )
    {
        delegate.setName( name );
    }

    @Override
    public PriceLevelImpl addNewPriceLevel()
    {
        return new PriceLevelImpl( delegate.addNewPriceLevel() );
    }

    @Override
    public void removePriceLevel( int i )
    {
        delegate.removePriceLevel( i );
    }
}
