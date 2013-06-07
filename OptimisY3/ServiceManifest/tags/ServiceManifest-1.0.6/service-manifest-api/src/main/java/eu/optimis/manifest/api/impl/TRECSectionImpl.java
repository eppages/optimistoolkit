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

import eu.optimis.manifest.api.sp.TRECSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanCostSectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEcoEfficiencySectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanRiskSectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanTRECSectionType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanTrustSectionType;

/**
 * @author angela.rumpl@scai.fraunhofer.de
 */
class TRECSectionImpl extends AbstractManifestElement<XmlBeanTRECSectionType>
    implements TRECSection, eu.optimis.manifest.api.ip.TRECSection
{

    public TRECSectionImpl( XmlBeanTRECSectionType base )
    {
        super( base );
    }

    @Override
    public TrustSectionImpl[] getTrustSectionArray()
    {
        Vector<TrustSectionImpl> vector = new Vector<TrustSectionImpl>();
        for ( XmlBeanTrustSectionType trustSectionType : delegate.getTrustSectionArray() )
        {
            vector.add( new TrustSectionImpl( trustSectionType ) );
        }
        return vector.toArray( new TrustSectionImpl[vector.size()] );
    }

    @Override
    public TrustSectionImpl getTrustSectionArray( int i )
    {
        return new TrustSectionImpl( delegate.getTrustSectionArray( i ) );
    }

    @Override
    public TrustSectionImpl addNewTrustSection( String componentId )
    {
        String[] scope = { componentId };
        return addNewTrustSection( scope );
    }

    @Override
    public TrustSectionImpl addNewTrustSection( String[] componentIdList )
    {
        TemplateLoader loader = new TemplateLoader();

        XmlBeanTrustSectionType type = delegate.addNewTrustSection();
        type.set( loader.loadTrustTemplate( componentIdList ) );
        return new TrustSectionImpl( type );
    }

    @Override
    public void removeTrustSection( int i )
    {
        delegate.removeTrustSection( i );
    }

    @Override
    public RiskSectionImpl[] getRiskSectionArray()
    {
        Vector<RiskSectionImpl> vector = new Vector<RiskSectionImpl>();
        for ( XmlBeanRiskSectionType type : delegate.getRiskSectionArray() )
        {
            vector.add( new RiskSectionImpl( type ) );
        }
        return vector.toArray( new RiskSectionImpl[vector.size()] );
    }

    @Override
    public RiskSectionImpl getRiskSectionArray( int i )
    {
        return new RiskSectionImpl( delegate.getRiskSectionArray( i ) );
    }

    @Override
    public RiskSectionImpl addNewRiskSection( String componentId )
    {
        return addNewRiskSection( getScope( componentId ) );
    }

    @Override
    public RiskSectionImpl addNewRiskSection( String[] componentIdList )
    {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanRiskSectionType type = delegate.addNewRiskSection();
        type.set( loader.loadRiskSectionTemplate( componentIdList ) );
        return new RiskSectionImpl( type );
    }

    @Override
    public void removeRiskSection( int i )
    {
        delegate.removeRiskSection( i );
    }

    @Override
    public EcoEfficiencySectionImpl[] getEcoEfficiencySectionArray()
    {
        Vector<EcoEfficiencySectionImpl> vector = new Vector<EcoEfficiencySectionImpl>();
        for ( XmlBeanEcoEfficiencySectionType type : delegate.getEcoEfficiencySectionArray() )
        {
            vector.add( new EcoEfficiencySectionImpl( type ) );
        }
        return vector.toArray( new EcoEfficiencySectionImpl[vector.size()] );
    }

    @Override
    public EcoEfficiencySectionImpl getEcoEfficiencySectionArray( int i )
    {
        return new EcoEfficiencySectionImpl( delegate.getEcoEfficiencySectionArray( i ) );
    }

    @Override
    public EcoEfficiencySectionImpl addNewEcoEfficiencySection( String componentId )
    {

        return addNewEcoEfficiencySection( getScope( componentId ) );
    }

    @Override
    public EcoEfficiencySectionImpl addNewEcoEfficiencySection( String[] componentIdList )
    {
        TemplateLoader loader = new TemplateLoader();

        XmlBeanEcoEfficiencySectionType type = delegate.addNewEcoEfficiencySection();
        type.set( loader.loadEcoEfficiencyTemplate( componentIdList ) );
        return new EcoEfficiencySectionImpl( type );
    }

    @Override
    public void removeEcoEfficiencySection( int i )
    {
        delegate.removeEcoEfficiencySection( i );
    }

    @Override
    public CostSectionImpl[] getCostSectionArray()
    {
        Vector<CostSectionImpl> vector = new Vector<CostSectionImpl>();
        for ( XmlBeanCostSectionType costSectionType : delegate.getCostSectionArray() )
        {
            vector.add( new CostSectionImpl( costSectionType ) );
        }
        return vector.toArray( new CostSectionImpl[vector.size()] );
    }

    @Override
    public CostSectionImpl getCostSectionArray( int i )
    {
        return new CostSectionImpl( delegate.getCostSectionArray( i ) );
    }

    @Override
    public CostSectionImpl addNewCostSection( String[] componentIdList )
    {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanCostSectionType costSectionType = delegate.addNewCostSection();
        costSectionType.set( loader.loadCostSectionTemplate( componentIdList ) );
        return new CostSectionImpl( costSectionType );
    }

    @Override
    public CostSectionImpl addNewCostSection( String componentId )
    {
        String[] componentIdList = { componentId };
        return addNewCostSection( componentIdList );
    }

    @Override
    public void removeCostSection( int i )
    {
        delegate.removeCostSection( i );
    }

    private String[] getScope( String componentId )
    {
        String[] scope = { componentId };
        return scope;
    }
}
