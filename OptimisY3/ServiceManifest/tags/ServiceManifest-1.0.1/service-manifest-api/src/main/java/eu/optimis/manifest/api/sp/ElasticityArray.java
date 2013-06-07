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
package eu.optimis.manifest.api.sp;

/**
 * @author owaeld
 */
public interface ElasticityArray {

    /**
     * Retrieves the complete array of elasticity rules
     *
     * @return the array of rules
     * @see ElasticityRule
     */
    ElasticityRule[] getRuleArray();

    /**
     * Retrieves the Elasticity Rule given at position i
     *
     * @param i index
     * @return elasticity rule
     * @see ElasticityRule
     */
    ElasticityRule getRuleArray(int i);


    /**
     * Adds a new default elasticity rule to the elasticity array by loading a template with default values
     * <pre>
     *  {@code
     * <opt:ElasticityRule>
     *    <opt:Scope>
     *       <opt:componentId>${componentId}</opt:componentId>
     *    </opt:Scope>
     *    <!-- Current amount of JVM Threads in the JBoss JVM -->
     *    <opt:KPIName>ThreadCount</opt:KPIName>
     *    <!-- These values are not used for this measurement -->
     *    <opt:Window>P1M</opt:Window>
     *    <opt:Frequency>1</opt:Frequency>
     *    <!-- 100 users per thread, with a tolerance of 5% (so trigger at more than 105 users, and down again at below 95 users ) -->
     *    <opt:Quota>100</opt:Quota>
     *    <opt:Tolerance>5</opt:Tolerance>
     * </opt:ElasticityRule>
     * </code>
     *  }
     *  </pre>
     *
     * @param componentId the component id is added to the scope array of the new rule.
     */
    ElasticityRule addElasticityRule(String componentId);

    /**
     * Removes an elasticity rule from the Manifest
     *
     * @param i the position of the rule
     */
    void removeElasticityRule(int i);


}