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
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.sp.ElasticityRule;

/**
 * @author arumpl
 */
public class ElasticityTest extends AbstractTestApi
{

    public void setUp() throws Exception
    {
        super.setUp();
        initializeElasticity();
    }

    public void testComponentIdInScopeIsJboss()
    {
        assertEquals( "jboss", getManifest().getElasticitySection().getRule( 0 ).getScope()
                .getComponentIdArray()[ 0 ] );
    }

    public void testKPINameIsThreadCount()
    {
        getManifest().getElasticitySection().getRule( 0 ).setName( "MinMemorySize" );
        assertEquals( "MinMemorySize",
                getManifest().getElasticitySection().getRule( 0 ).getName() );
    }

    public void testGetKPINameWithIPInterface()
    {
        getManifest().getElasticitySection().getRule( 0 ).setName( "MinMemorySize" );

        Manifest ipManifest = Manifest.Factory.newInstance( getManifest().toString() );

        assertEquals( "MinMemorySize", ipManifest.getElasticitySection().getRule( 0 ).getName() );
    }

    public void testAssessmentIntervalIs1Month()
    {
        assertEquals( "Window is not P1M;", new String( "P1M" ),
                getManifest().getElasticitySection()
                        .getRule( 0 ).getWindow() );
    }

    public void testFrequencyIs1()
    {
        assertEquals( "Frequency is not 1;", 1, getManifest().getElasticitySection().getRule( 0 )
                .getFrequency() );
    }

    public void testAddRemoveElasticityRules()
    {
        String[] scope = { "jboss", "LB" };
        ElasticityRule rule =
                getManifest().getElasticitySection().addNewRule( scope, "someRuleName" );
        int length = getManifest().getElasticitySection().getRuleArray().length;
        assertTrue( rule.getName().equals( "someRuleName" ) );
        assertTrue( rule.getScope().toString().contains( "jboss" ) );
        assertTrue( rule.getScope().toString().contains( "LB" ) );
        getManifest().getElasticitySection().removeRule( length - 1 );

        int lengthAfter = getManifest().getElasticitySection().getRuleArray().length;
        assertEquals( "The affinity rule array is not smaller after removing a rule. ", length,
                lengthAfter + 1 );
    }
}
