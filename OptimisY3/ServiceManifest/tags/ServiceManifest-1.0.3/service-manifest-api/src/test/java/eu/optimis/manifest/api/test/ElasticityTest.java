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
 */

package eu.optimis.manifest.api.test;

/**
 * @author arumpl
 */
public class ElasticityTest extends AbstractTestApi {

    public ElasticityTest(String testName) {
        super(testName);
    }

    public void setUp() throws Exception {
        super.setUp();
        initializeElasticity();
    }

    public void testComponentIdInScopeIsJboss() {
        assertEquals("jboss", manifest.getElasticitySection().getRule(0).getScope().getComponentIdArray()[0]);
    }

    public void testKPINameIsThreadCount() {
        assertEquals("MinMemorySize", manifest.getElasticitySection().getRule(0).getName());
    }

    public void testAssessmentIntervalIs1Month() {
        assertEquals("Window is not P1M;", new String("P1M"), manifest.getElasticitySection().getRule(0).getCondition().getAssessmentCriteria().getWindow());
    }

    public void testFrequencyIs1() {
        assertEquals("Frequency is not 1;", 1, manifest.getElasticitySection().getRule(0).getCondition().getAssessmentCriteria().getFrequency());
    }

    public void testExpression() {
        assertEquals("REQ_MEMORY_SIZE le PROVIDED_MEMORY_SIZE", manifest.getElasticitySection().getRule(0).getCondition().getExpression());
    }

    public void testToleranceIs5() {
        assertEquals("Effect is not scaleUp;", "scaleUp", manifest.getElasticitySection().getRule(0).getEffect().getAction());
    }

    public void testShouldAddAndRemoveAVariable() {
        int origLength = manifest.getElasticitySection().getVariableArray().length;
        manifest.getElasticitySection().addNewExternalVariable("REQ_CPU", "int", "somepath");

        manifest.getElasticitySection().removeVariable(0);
        int finalLength = manifest.getElasticitySection().getVariableArray().length;
        assertEquals(origLength, finalLength);
    }

    public void testShouldRemoveVariableByName() {
        manifest.getElasticitySection().addNewExternalVariable("MyVar", "int", "somepath");
        assertNotNull(manifest.getElasticitySection().getVariable("MyVar"));
        manifest.getElasticitySection().removeVariable("MyVar");
        assertNull(manifest.getElasticitySection().getVariable("MyVar"));
    }

    public void testShouldSetSPManagedElasticity() {
        manifest.getElasticitySection().setSPManagedElasticity();
        assertNull(manifest.getElasticitySection().getRuleArray());
        assertNull(manifest.getElasticitySection().getVariableArray());
    }


}
