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

import eu.optimis.manifest.api.utils.ManifestElementParser;
import eu.optimis.types.xmlbeans.servicemanifest.*;
import eu.optimis.types.xmlbeans.servicemanifest.service.XmlBeanVirtualMachineComponentConfigurationDocument;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * @author arumpl
 */
public class ManifestParser
{

    private XmlBeanServiceManifestDocument manifestDocument;

    public ManifestParser( XmlBeanServiceManifestDocument manifest )
    {
        manifestDocument = manifest;
    }

    public XmlBeanServiceManifestDocument getManifest()
    {
        return manifestDocument;
    }

    public List<String> selectAllComponentIds()
    {
        List<String> componentIds = new ArrayList<String>();
        for ( XmlObject o : ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt:VirtualMachineComponent/@opt:componentId" ) )
        {
            componentIds.add( o.getDomNode().getNodeValue() );
        }
        return componentIds;
    }

    /**
     * selects all affinity constraints related to the component referenced by componentId
     *
     * @param componentId the id of a component
     * @return a list of constraints for this component
     */
    public List<String> selectAllAffinityConstraintsByComponentId( String componentId )
    {
        List<String> affinityConstraints = new ArrayList<String>();
        for ( XmlObject o : ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt:AffinityRule[opt:Scope/opt:ComponentId = '" + componentId + "']" ) )
        {
            XmlBeanAffinityRuleType rule = ( XmlBeanAffinityRuleType ) o;
            affinityConstraints.add( rule.getAffinityConstraints().toString() );
        }
        return affinityConstraints;
    }
    
    /**
     * selects all anti affinity constraints related to the component referenced by componentId
     *
     * @param componentId the id of a component
     * @return a list of constraints for this component
     */
    public List<String> selectAllAntiAffinityConstraintsByComponentId( String componentId )
    {
        List<String> antiAffinityConstraints = new ArrayList<String>();
        for ( XmlObject o : ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt:AntiAffinityRule[opt:Scope/opt:ComponentId = '" + componentId + "']" ) )
        {
            XmlBeanAntiAffinityRuleType antiRule = ( XmlBeanAntiAffinityRuleType ) o;
            antiAffinityConstraints.add( antiRule.getAntiAffinityConstraints().toString() );
        }
        return antiAffinityConstraints;
    }

    /**
     * This methods removes any elements that are related to a component, plus all rules or price plans that
     * are not related to a component any more. 
     * 1. removes the vm component itself 
     * 2. removes the incarnated vm components
     * 3. removes the componentId from all scope arrays 
     * 4. removes the elasticity rules with empty scope array 
     * 5. removes the affinity rules with empty scope array
     * 6. removes the anti affinity rules with empty scope array 
     * 7. removes the price plans with empty scope array
     * 8. removes the data storage with empty scope array
     *
     * @param componentId the id of the component to be deleted
     */
    public void removeComponent( String componentId )
    {
        // remove the component itself
        deleteObjects( selectVirtualMachineComponent( componentId ) );
        
        // remove the incarnated vm of this component
        deleteObjects( selectIncarnatedVirtualMachineComponent( componentId ) );
        
        // remove the componentId from all scope arrays
        deleteObjects( selectScope( componentId ) );

        //remove component from sp extensions:
        deleteObjects( selectVirtualMachineComponentConfiguration( componentId ) );

        //
        // Now cleanup elements with empty scopes
        //
        removeElementsWithEmptyScopeArray();
    }

    /**
     * select the virtual machine component configuration in the SP extension section.
     *
     * @param componentId
     * @return the xml object array with this components configuration
     */
    private XmlObject[] selectVirtualMachineComponentConfiguration( String componentId )
    {
        QName qName =
                XmlBeanVirtualMachineComponentConfigurationDocument.type.getDocumentElementName();
        return ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt-sp:" + qName.getLocalPart() + "[@opt-sp:componentId ='" + componentId +
                "']" );
    }

    private void removeElementsWithEmptyScopeArray()
    {
        // remove affinity sections with empty scopes
        deleteObjects(
                selectElementsWithEmptyScopeArray(
                        XmlBeanAffinityRuleDocument.type.getDocumentElementName() ) );
        
     // remove anti affinity sections with empty scopes
        deleteObjects(
                selectElementsWithEmptyScopeArray(
                        XmlBeanAntiAffinityRuleDocument.type.getDocumentElementName() ) );

        // delete elasticity rules with empty scopes
        QName elasticitySectionQName =
                XmlBeanElasticitySectionDocument.type.getDocumentElementName();
        deleteObjects( selectElementsWithEmptyScopeArray( elasticitySectionQName ) );

        // delete ecoefficiency sections with empty scopes
        QName ecoEfficiencySectionQName =
                XmlBeanEcoEfficiencySectionDocument.type.getDocumentElementName();
        deleteObjects( selectElementsWithEmptyScopeArray( ecoEfficiencySectionQName ) );

        // delete risk sections with empty scopes
        deleteObjects( selectElementsWithEmptyScopeArray(
                XmlBeanRiskSectionDocument.type.getDocumentElementName() ) );

        // delete trust sections with empty scopes
        deleteObjects( selectElementsWithEmptyScopeArray(
                XmlBeanTrustSectionDocument.type.getDocumentElementName() ) );

        // delete price sections with empty scopes
        deleteObjects( selectElementsWithEmptyScopeArray(
                XmlBeanCostSectionDocument.type.getDocumentElementName() ) );
        
        // delete data storage sections with empty scopes
        deleteObjects( selectElementsWithEmptyScopeArray(
                XmlBeanDataStorageDocument.type.getDocumentElementName() ) );
    }

    private XmlObject[] selectElementsWithEmptyScopeArray( QName qName )
    {
        return ManifestElementParser
                .selectObjects( manifestDocument, "$this//opt:" + qName.getLocalPart()
                                                  + "[not(opt:Scope/opt:ComponentId)]" );
    }

    private XmlObject[] selectScope( String componentId )
    {
        return ManifestElementParser
                .selectObjects( manifestDocument, "$this//opt:Scope/opt:ComponentId[. ='"
                                                  + componentId + "']" );
    }

    private XmlObject[] selectVirtualMachineComponent( String componentId )
    {
        return ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt:VirtualMachineComponent[@opt:componentId ='" + componentId + "']" );
    }

    private XmlObject[] selectIncarnatedVirtualMachineComponent( String componentId )
    {
        return ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt-ip:IncarnatedVirtualMachineComponent[@opt:componentId ='" +
                componentId + "']" );
    }

    private XmlObject[] selectServiceProviderExtensions()
    {
        return ManifestElementParser.selectObjects( manifestDocument,
                "$this//opt-sp:ServiceProviderExtensions" );
    }

    private XmlObject[] selectInfrastructureProviderExtensions()
    {
        XmlObject[] objects =
                ManifestElementParser.selectObjects( manifestDocument,
                        "$this//opt-ip:InfrastructureProviderExtensions" );
        return objects;
    }

    public void removeServiceProviderExtensions()
    {
        deleteObjects( selectServiceProviderExtensions() );
    }

    public void removeInfrastructureProviderExtensions()
    {
        deleteObjects( selectInfrastructureProviderExtensions() );
    }

    public XmlBeanAffinityRuleType[] selectAffinityRulesByComponent( String componentId )
    {
        XmlObject[] objects =
                ManifestElementParser.selectObjects( manifestDocument,
                        "$this//opt:AffinityRule[opt:Scope/opt:ComponentId = '" + componentId +
                        "']" );
        if ( objects.length > 0 )
        {
            return ( XmlBeanAffinityRuleType[] ) objects;
        }
        else
        {
            return new XmlBeanAffinityRuleType[ 0 ];
        }
    }
    
    public XmlBeanAntiAffinityRuleType[] selectAntiAffinityRulesByComponent( String componentId )
    {
        XmlObject[] objects =
                ManifestElementParser.selectObjects( manifestDocument,
                        "$this//opt:AntiAffinityRule[opt:Scope/opt:ComponentId = '" + componentId +
                        "']" );
        if ( objects.length > 0 )
        {
            return ( XmlBeanAntiAffinityRuleType[] ) objects;
        }
        else
        {
            return new XmlBeanAntiAffinityRuleType[ 0 ];
        }
    }

    private void deleteObjects( XmlObject[] selectedObjects )
    {
        for ( XmlObject object : selectedObjects )
        {
            Node element = object.getDomNode();
            Node parent = element.getParentNode();
            parent.removeChild( element );
        }
    }
}
