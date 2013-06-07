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

import eu.optimis.manifest.api.sp.RiskSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAvailabilityType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanRiskSectionType;

/**
 * @author karl.catewicz@scai.fraunhofer.de - 08.12.2011
 */
class RiskSectionImpl extends AbstractManifestElement<XmlBeanRiskSectionType>
    implements RiskSection, eu.optimis.manifest.api.ip.RiskSection
{
    public RiskSectionImpl( XmlBeanRiskSectionType base )
    {
        super( base );
    }

    @Override
    public int getRiskLevel()
    {
        return delegate.getRiskLevel();
    }

    @Override
    public void setRiskLevel( int riskLevel )
    {
        delegate.setRiskLevel( riskLevel );
    }

    @Override
    public AvailabilityImpl[] getAvailabilityArray()
    {
        Vector<AvailabilityImpl> vector = new Vector<AvailabilityImpl>();
        for ( XmlBeanAvailabilityType type : delegate.getAvailabilityArray1().getAvailabilityArray() )
        {
            vector.add( new AvailabilityImpl( type ) );
        }
        return vector.toArray( new AvailabilityImpl[vector.size()] );
    }

    @Override
    public AvailabilityImpl getAvailabilityArray( int i )
    {
        return new AvailabilityImpl( delegate.getAvailabilityArray1().getAvailabilityArray( i ) );
    }

    @Override
    public AvailabilityImpl addNewAvailability( String assessmentInterval, double availabilityValue )
    {
        if ( delegate.getAvailabilityArray1() == null )
        {
            delegate.addNewAvailabilityArray1();
        }
        AvailabilityImpl availability =
            new AvailabilityImpl( delegate.getAvailabilityArray1().addNewAvailability() );
        availability.setAssessmentInterval( assessmentInterval );
        availability.setValue( availabilityValue );
        return availability;
    }

    @Override
    public ScopeImpl getScope()
    {
        return new ScopeImpl( delegate.getScope() );
    }

}
