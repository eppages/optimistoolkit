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

import eu.optimis.manifest.api.ip.ElasticitySection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanElasticitySectionTypeY1;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanRuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geli - 3/6/12
 */
public class ElasticitySectionImpl extends AbstractManifestElement<XmlBeanElasticitySectionTypeY1>
        implements ElasticitySection, eu.optimis.manifest.api.sp.ElasticitySection
{
    /**
     * Default constructor.
     *
     * @param base the base type is used as internal delegation and data store object
     */
    public ElasticitySectionImpl( XmlBeanElasticitySectionTypeY1 base )
    {
        super( base );
    }

    @Override
    public ElasticityRuleImpl getRule( int i )
    {
        return new ElasticityRuleImpl( delegate.getRuleArray( i ) );
    }

    @Override
    public ElasticityRuleImpl[] getRuleArray()
    {
        List<ElasticityRuleImpl> vector = new ArrayList<ElasticityRuleImpl>();
        for ( XmlBeanRuleType ruleType : delegate.getRuleArray() )
        {
            vector.add( new ElasticityRuleImpl( ruleType ) );
        }
        return vector.toArray( new ElasticityRuleImpl[ vector.size() ] );
    }

    @Override
    public ElasticityRuleImpl addNewRule( String componentId, String name )
    {
        String[] componentIds = { componentId };
        return addNewRule( componentIds, name );
    }

    @Override
    public ElasticityRuleImpl addNewRule( String[] componentList, String name )
    {
        TemplateLoader loader = new TemplateLoader();
        XmlBeanRuleType rule = delegate.addNewRule();
        rule.set( loader.loadOldElasticityRuleTemplate( componentList, name ) );
        new ElasticityRuleImpl( rule );
        return new ElasticityRuleImpl( rule );
    }

    @Override
    public void removeRule( int index )
    {
        delegate.removeRule( index );
    }
}
