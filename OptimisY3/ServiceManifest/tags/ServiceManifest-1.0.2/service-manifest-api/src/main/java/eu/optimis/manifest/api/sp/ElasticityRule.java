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

import org.apache.xmlbeans.GDuration;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * @author owaeld
 */
public interface ElasticityRule {

    /**
     * Retrieves the scope which contains referenced componentIds
     *
     * @return array
     * @see Scope
     */
    Scope getScope();

    /**
     * Retrieves the key performance indicator this rule applies to.
     *
     * @return kpiName, default: ThreadCount
     */
    String getKPIName();

    /**
     * Retrieves the duration of the rule
     *
     * @return duration, default: "P1M"
     */
    String getWindow();

    /**
     * Retrieves the frequency
     *
     * @return frequency, default: 1
     */
    int getFrequency();

    /**
     * Retrieves the quota, eg. 100 users per thread
     *
     * @return quota, default: 100
     */
    int getQuota();


    /**
     * Retrieves the tolerance, eg.  100 users per thread, with a tolerance of 5%
     * (so trigger at more than 105 users, and down again at below 95 users )
     *
     * @return the tolerance, default: 5
     */
    int getTolerance();


    /**
     * the key performance indicator this rule applies to.
     *
     * @param kpiName default: ThreadCount
     */
    void setKPIName(String kpiName);


    /**
     * Set duration
     *
     * @param window default: "P1M" (eq. 1 Month)
     * @see GDuration
     */
    void setWindow(String window);

    /**
     * Sets the frequency

     * @param frequency default: 1
     */
    void setFrequency(int frequency);

    /**
     * Sets the quota
     *
     * @param quota default: 100
     */
    void setQuota(int quota);


    /**
     * Sets the tolerance
     *
     * @param tolerance default: 5
     */
    void setTolerance(float tolerance);

}