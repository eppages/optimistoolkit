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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author arumpl
 */
public class ElasticityTest extends AbstractTestApi {

    public ElasticityTest(String testName) {
        super(testName);
    }

    public void testComponentIdInScopeIsJboss() {
        assertEquals("jboss", manifest.getElasticitySection().getRuleArray(0).getScope().getComponentIdArray()[0]);
    }

    public void testKPINameIsThreadCount() {
        assertEquals("KPIName is not ThreadCount", "ThreadCount", manifest.getElasticitySection().getRuleArray(0).getKPIName());
    }

    public void testWindowIs5Month() {
        assertEquals("Window is not P5M;", new String("P5M"), manifest.getElasticitySection().getRuleArray(0).getWindow());
    }

    public void testFrequencyIs1() {
        assertEquals("Frequency is not 1;", 1, manifest.getElasticitySection().getRuleArray(0).getFrequency());
    }

    public void testQuotaIs100() {
        assertEquals("Quota is not 100;", 100, manifest.getElasticitySection().getRuleArray(0).getQuota());
    }

    public void testToleranceIs5() {
        assertEquals("Tolerance is not 5;", 5, manifest.getElasticitySection().getRuleArray(0).getTolerance());
    }


}
