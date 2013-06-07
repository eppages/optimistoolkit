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

import eu.optimis.manifest.api.exceptions.SplittingNotAllowedException;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAffinityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanAntiAffinityRuleType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arumpl
 */
public class ManifestSplitter
{
    private XmlBeanServiceManifestDocument currentManifest;

    private XmlBeanServiceManifestDocument extractedManifest;

    protected ManifestSplitter( XmlBeanServiceManifestDocument manifest )
    {
        this.currentManifest = ( XmlBeanServiceManifestDocument ) manifest.copy();
        this.extractedManifest = ( XmlBeanServiceManifestDocument ) manifest.copy();
    }

    public XmlBeanServiceManifestDocument getCurrentManifest()
    {
        return currentManifest;
    }

    public XmlBeanServiceManifestDocument getExtractedManifest()
    {
        return extractedManifest;
    }

    public void splitManifest( String componentId ) throws SplittingNotAllowedException
    {
        List<String> componentIds = new ArrayList<String>();
        componentIds.add( componentId );
        splitManifest( componentIds );
    }

    public void splitManifest( List<String> componentIds ) throws SplittingNotAllowedException
    {
        if ( !componentsExist( componentIds ) )
        {
            throw new IllegalArgumentException(
                    "One or more of the provided componentIds '" + componentIds.toString()
                    + "' does not exist in the Manifest." );
        }

        ManifestParser currentManifestParser = new ManifestParser( currentManifest );
        
        //
        // check if anti affinity is allowed by taking all componentIds into account.
        //
        boolean isSplittingAllowedForAntiAffinity = isSplittingAllowedForAntiAffinity( componentIds, currentManifestParser );
        if ( !isSplittingAllowedForAntiAffinity )
        {
            throw new SplittingNotAllowedException(
                    "Anti-Affinity constraints do not permit splitting. A component ("
                    + componentIds.toString() +
                    ") has Low anti-affinity to a component not in this list." );
        }
        
        //
        // check if affinity is allowed by taking all componentIds into account.
        //
        boolean isSplittingAllowedForAffinity = isSplittingAllowedForAffinity( componentIds, currentManifestParser );
        if ( !isSplittingAllowedForAffinity )
        {
            throw new SplittingNotAllowedException(
                    "Affinityconstraints do not permit splitting. A component ("
                    + componentIds.toString() +
                    ") has High or Medium affinity to a component not in this list." );
        }

        removeComponents( componentIds, currentManifestParser );

        // select the existing components in the current manifest
        List<String> remainingComponents = currentManifestParser.selectAllComponentIds();

        // from the extracted manifest we will remove the components that remain in the extracted one.
        ManifestParser newManifestParser = new ManifestParser( extractedManifest );
        removeComponents( remainingComponents, newManifestParser );

//        newManifestParser.removeServiceProviderExtensions();
        newManifestParser.removeInfrastructureProviderExtensions();
    }

    private boolean componentsExist( List<String> componentIds )
    {
        boolean exists = true;
        for ( String componentId : componentIds )
        {
            ManifestParser parser = new ManifestParser( currentManifest );
            // we can only split the manifest, if the component exists.
            if ( !isComponentInManifest( componentId, parser ) )
            {
                exists = false;
                break;
            }
        }
        return exists;
    }

    private void removeComponents( List<String> componentIds, ManifestParser manifestParser )
    {
        for ( String componentId : componentIds )
        {
            manifestParser.removeComponent( componentId );
        }
    }

    private boolean isComponentInManifest( String componentId, ManifestParser parser )
    {
        List<String> existingComponents = parser.selectAllComponentIds();
        if ( !existingComponents.contains( componentId ) )
        {
            return false;
        }
        return true;
    }
    
    /**
     * select all anti affinity rule scopes with an anti affinity constraint low. If there are other
     * componentIds in the scope than the provided list, splitting is not allowed.
     *
     * @param componentIds the list of componentIds that are going to be splitted to another manifest
     * @param parser       the manifest parser
     */
    private boolean isSplittingAllowedForAntiAffinity( List<String> componentIds, ManifestParser parser )
    {

        boolean isSplittingAllowed = true;
        
        for ( String componentId : componentIds )
        {
        	//
        	// check anti affinity rules
        	//
            XmlBeanAntiAffinityRuleType[] rules = parser.selectAntiAffinityRulesByComponent( componentId );
            for ( XmlBeanAntiAffinityRuleType rule : rules )
            {
                //check only low medium rules
                if ( rule.getAntiAffinityConstraints().toString().equals( "Low" ) )
                {
                    //if the scope of this rule contains other components than the ones provided in the list, we cannot split
                    for ( String componentInScope : rule.getScope().getComponentIdArray() )
                    {
                        if ( !componentIds.contains( componentInScope ) )
                        {
                            isSplittingAllowed = false;
                            break;
                        }
                    }
                }
            }
        }
        return isSplittingAllowed;
    }

    /**
     * select all affinity rule scopes with an affinity constraint high or medium. If there are other
     * componentIds in the scope than the provided list, splitting is not allowed.
     *
     * @param componentIds the list of componentIds that are going to be splitted to another manifest
     * @param parser       the manifest parser
     */
    private boolean isSplittingAllowedForAffinity( List<String> componentIds, ManifestParser parser )
    {

        boolean isSplittingAllowed = true;
        
        //
    	// check affinity rules
    	//
        for ( String componentId : componentIds )
        {
            //we retrieve all rules with high affinity for this component
            XmlBeanAffinityRuleType[] rules = parser.selectAffinityRulesByComponent( componentId );
            for ( XmlBeanAffinityRuleType rule : rules )
            {
                //check only high or medium rules
                if ( rule.getAffinityConstraints().toString().equals( "High" ) ||
                     rule.getAffinityConstraints().toString().equals( "Medium" ) )
                {
                    //if the scope of this rule contains other components than the ones provided in the list, we cannot split
                    for ( String componentInScope : rule.getScope().getComponentIdArray() )
                    {
                        if ( !componentIds.contains( componentInScope ) )
                        {
                            isSplittingAllowed = false;
                            break;
                        }
                    }
                }
            }
        }
        return isSplittingAllowed;
    }
}