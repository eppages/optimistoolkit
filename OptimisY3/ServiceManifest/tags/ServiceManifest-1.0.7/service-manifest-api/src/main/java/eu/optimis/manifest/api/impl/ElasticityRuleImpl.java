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

import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanRuleType;
import org.apache.xmlbeans.GDuration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author geli - 3/6/12
 */
public class ElasticityRuleImpl extends AbstractManifestElement<XmlBeanRuleType>
        implements ElasticityRule, eu.optimis.manifest.api.ip.ElasticityRule
{
    public ElasticityRuleImpl( XmlBeanRuleType ruleType )
    {
        super( ruleType );
    }

    @Override
    public ScopeImpl getScope()
    {
        return new ScopeImpl( delegate.getScope() );
    }

    @Override
    public String getName()
    {
        return delegate.getKPIName();
    }

    @Override
    public void setName( String name )
    {
        delegate.setKPIName( name );
    }

    @Override
    public void setWindow( String window )
    {
        delegate.setWindow( new GDuration( window ) );
    }

    @Override
    public void setTolerance( int i )
    {
        delegate.setTolerance( BigDecimal.valueOf( i ) );
    }

    @Override
    public void setFrequency( int i )
    {
        delegate.setFrequency( BigInteger.valueOf( i ) );
    }

    @Override
    public void setQuota( int i )
    {
        delegate.setQuota( BigInteger.valueOf( i ) );
    }

    @Override
    public String getWindow()
    {
        return delegate.getWindow().toString();
    }

    @Override
    public int getTolerance()
    {
        if ( delegate.isSetTolerance() )
        {
            return delegate.getTolerance().intValue();
        }
        else
        {
            return -1;
        }
    }

    @Override
    public int getFrequency()
    {

        return delegate.getFrequency().intValue();
    }

    @Override
    public int getQuota()
    {
        return delegate.getQuota().intValue();
    }
}
