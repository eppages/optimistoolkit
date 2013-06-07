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

import eu.optimis.manifest.api.sp.EcoEfficiencySection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanBREEAMCertificationConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanCASBEEType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEcoEfficiencySectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEcoMetricType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanGreenStarType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanISO14000Type;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanLEEDCertificationConstraintType;

/**
 * @author hrasheed
 */
class EcoEfficiencySectionImpl extends AbstractManifestElement<XmlBeanEcoEfficiencySectionType>
    implements EcoEfficiencySection, eu.optimis.manifest.api.ip.EcoEfficiencySection
{

    public EcoEfficiencySectionImpl( XmlBeanEcoEfficiencySectionType base )
    {
        super( base );
    }

    @Override
    public String getLEEDCertification()
    {
        return delegate.getLEEDCertification().toString();
    }

    @Override
    public void setLEEDCertification( String leedCertification )
    {
        delegate.setLEEDCertification( XmlBeanLEEDCertificationConstraintType.Enum.forString( leedCertification ) );
    }

    @Override
    public String getBREEAMCertification()
    {
        return delegate.getBREEAMCertification().toString();
    }

    @Override
    public void setBREEAMCertification( String breeamCertification )
    {
    	XmlBeanBREEAMCertificationConstraintType.Enum breem = XmlBeanBREEAMCertificationConstraintType.Enum.forString( breeamCertification );
        delegate.setBREEAMCertification( breem );
    }

    @Override
    public boolean getEuCoCCompliant()
    {
        return delegate.getEuCoCCompliant();
    }

    @Override
    public void setEuCoCCompliant( boolean euCoCCompliant )
    {
        delegate.setEuCoCCompliant( euCoCCompliant );
    }

    @Override
    public Object getEnergyStarRating()
    {
        return delegate.getEnergyStarRating();
    }

    @Override
    public void setEnergyStarRating( String energyStarRating )
    {
        delegate.setEnergyStarRating( energyStarRating );
    }

	@Override
	public void setISO14000( String iso14000 )
	{
		XmlBeanISO14000Type.Enum isoEnum = XmlBeanISO14000Type.Enum.forString( iso14000 );
		delegate.setISO14000( isoEnum );
	}

	@Override
	public String getISO14000()
	{
		return delegate.getISO14000().toString();
	}

	@Override
	public void setGreenStar( String greenStar )
	{
		XmlBeanGreenStarType.Enum greenEnum = XmlBeanGreenStarType.Enum.forString( greenStar );
		delegate.setGreenStar( greenEnum );
	}

	@Override
	public String getGreenStar() 
	{
		return delegate.getGreenStar().toString();
	}

	@Override
	public void setCASBEE( String casbee ) 
	{
		XmlBeanCASBEEType.Enum casbeeEnum = XmlBeanCASBEEType.Enum.forString( casbee );
		delegate.setCASBEE( casbeeEnum );
	}

	@Override
	public String getCASBEE() 
	{
		return delegate.getCASBEE().toString();
	}

	@Override
	public EcoMetricImpl[] getEcoMetricArray()
	{
		if( delegate.isSetEcoMetricArray1() ) 
		{
			Vector<EcoMetricImpl> vector = new Vector<EcoMetricImpl>();
	        for ( XmlBeanEcoMetricType type : delegate.getEcoMetricArray1().getEcoMetricArray() )
	        {
	            vector.add( new EcoMetricImpl( type ) );
	        }
	        return vector.toArray( new EcoMetricImpl[vector.size()] );
		}
		return new EcoMetricImpl[]{};
	}

	@Override
	public EcoMetricImpl getEcoMetricArray( int i )
	{
		if( delegate.isSetEcoMetricArray1() ) 
		{
			return new EcoMetricImpl( delegate.getEcoMetricArray1().getEcoMetricArray( i ) );
		}
		return null;
	}

	@Override
	public EcoMetricImpl getEcoMetric( String name )
	{
		if( delegate.isSetEcoMetricArray1() ) 
		{
			for ( XmlBeanEcoMetricType type : delegate.getEcoMetricArray1().getEcoMetricArray() )
	        {
	            if( type.getName().equals( name ) )
	            {
	            	return new EcoMetricImpl( type );
	            }
	        }
		} 
		return null;
	}

	@Override
	public EcoMetricImpl addNewEcoMetric( String metricName )
	{
		if ( delegate.getEcoMetricArray1() == null )
        {
            delegate.addNewEcoMetricArray1();
        }
		EcoMetricImpl ecoMetric = new EcoMetricImpl( delegate.getEcoMetricArray1().addNewEcoMetric() );
		ecoMetric.setName( metricName );
        return ecoMetric;
	}

	@Override
	public void removeEcoMetric( int i )
	{
		if( delegate.isSetEcoMetricArray1() ) 
		{
			if( delegate.getEcoMetricArray1().getEcoMetricArray().length > 0) {
				delegate.getEcoMetricArray1().removeEcoMetric( i );
			}
		}
	}

	@Override
	public void removeEcoMetric( String name )
	{
		if( delegate.isSetEcoMetricArray1() ) 
		{
			for ( int i = 0; i < delegate.getEcoMetricArray1().getEcoMetricArray().length; i++ )
	        {
	            if ( delegate.getEcoMetricArray1().getEcoMetricArray( i ).getName().equals( name ) )
	            {
	            	delegate.getEcoMetricArray1().removeEcoMetric( i );
	            }
	        }
		}
	}
	
	@Override
    public ScopeImpl getScope()
    {
        return new ScopeImpl( delegate.getScope() );
    }
}
