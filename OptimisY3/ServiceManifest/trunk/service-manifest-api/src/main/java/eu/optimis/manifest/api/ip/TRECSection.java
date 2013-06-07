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
package eu.optimis.manifest.api.ip;


/**
 * @author owaeld
 */
public interface TRECSection
{

    /**
     * @return trust section
     * @see eu.optimis.manifest.api.sp.TRECSection#getTrustSectionArray()
     */
    TrustSection[] getTrustSectionArray();

    /**
     * @param i
     *            the position in the array
     * @return the trust section at position i
     */
    TrustSection getTrustSectionArray( int i );

    /**
     * @return risk section
     * @see eu.optimis.manifest.api.sp.TRECSection#getRiskSectionArray()
     */
    RiskSection[] getRiskSectionArray();

    /**
     * @param i
     *            the index in the array
     * @return the risk section
     * @see eu.optimis.manifest.api.sp.TRECSection#getRiskSectionArray(int)
     */
    RiskSection getRiskSectionArray( int i );

    /**
     * @return eco efficiency section array
     * @see eu.optimis.manifest.api.sp.TRECSection#getEcoEfficiencySectionArray()
     */
    EcoEfficiencySection[] getEcoEfficiencySectionArray();

    /**
     * @return eco efficiency section at position i
     * @see eu.optimis.manifest.api.sp.TRECSection#getEcoEfficiencySectionArray(int)
     */
    EcoEfficiencySection getEcoEfficiencySectionArray( int i );

    /**
     * @return cost section array
     * @see eu.optimis.manifest.api.sp.TRECSection#getCostSectionArray()
     */

    CostSection[] getCostSectionArray();

    /**
     * @param i
     *            the ith position in the array
     * @return cost section array at position i
     * @see eu.optimis.manifest.api.sp.TRECSection#getCostSectionArray(int)
     */
    CostSection getCostSectionArray( int i );
}