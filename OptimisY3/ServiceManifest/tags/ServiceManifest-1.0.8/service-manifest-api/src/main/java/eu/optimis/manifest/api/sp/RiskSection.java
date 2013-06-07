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
public interface RiskSection
{

    /**
     * Retrieves the OPTIMIS risk level that is used for delegation in a federated cloud scenario
     * 
     * @return
     */
    int getRiskLevel();

    /**
     * Retrieves the availability array, specifying certain guarantees on availability for each component
     * 
     * @return array of availabilities
     * @see Availability
     */
    Availability[] getAvailabilityArray();

    /**
     * Retrieves the availability array at position i, specifying a certain guarantee on availability for a
     * scope of components
     * 
     * @return the availability
     * @see Availability
     */
    Availability getAvailabilityArray( int i );

    /**
     * Creates a new Availability
     * 
     * @param assessmentInterval
     *            interval as a GDuration string e.g. "P1M"
     * @param availability
     *            the availability as percentage. eg. 99 to say 99% of availability
     * @return the created availability
     */
    Availability addNewAvailability( String assessmentInterval, double availability );

    /**
     * Specifies the OPTIMIS risk level that is used for delegation in a federated cloud scenario
     * 
     * @param riskLevel
     *            level of risk
     */
    void setRiskLevel( int riskLevel );

    /**
     * retrieve the scope array containing all referenced componentIds
     * 
     * @return the scope
     */
    Scope getScope();
}