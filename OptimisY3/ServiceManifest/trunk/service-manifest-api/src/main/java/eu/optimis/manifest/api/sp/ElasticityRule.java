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
public interface ElasticityRule
{

    /**
     * Retrieves the scope which contains referenced componentIds
     *
     * @return the scope containing a list of componentIds
     * @see Scope
     */
    Scope getScope();

    /**
     * retrieve the KPI name this rule applies to. This is a unique key, e.g. "MinAvailability".
     *
     * @return the KPI Name
     */
    String getName();

    /**
     * set the KPI name this rule applies to. This is a unique key, e.g. "MinAvailability".
     *
     * @param name the KPI name
     */
    void setName( String name );

    /**
     * set the monitoring window
     *
     * @param window the monitoring window as GDuration String. E.g. "P1M" --> 1 Month
     */
    void setWindow( String window );

    /**
     * set the tolerance value, e.g if the quota is 500, the tolerance is 20 then the
     * point to scale up is if the value is above 520 or below 480.
     *
     * @param i the tolerance value
     */
    void setTolerance( int i );

    /**
     * set the frequency of the elasticity monitoring.
     *
     * @param i the frequency
     */
    void setFrequency( int i );

    /**
     * set the quota
     *
     * @param i the quota
     */
    void setQuota( int i );

    /**
     * get the monitoring window
     *
     * @return the monitoring window as GDuration String. E.g. "P1M" --> 1 Month
     */
    String getWindow();

    /**
     * retrieve the tolerance value, e.g if the quota is 500, the tolerance is 20 then the
     * point to scale up is if the value is above 520 or below 480.
     *
     * @return the tolerance
     */
    int getTolerance();

    /**
     * retrieve the frequency of the elasticity monitoring.
     *
     * @return the frequency
     */
    int getFrequency();

    /**
     * retrieve the quota which indicates the required value. E.g. 500Mb of memory as a minimum.
     *
     * @return the quota
     */
    int getQuota();
}