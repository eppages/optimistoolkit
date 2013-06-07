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
 * Each section, Trust, Risk and EcoEfficiency can be defined individually for ech component. Therefore you
 * can add several sections where each has a scope to identify the referenced components.
 * 
 * @author owaeld
 */
public interface TRECSection
{

    /**
     * @return the array of risk sections
     */
    RiskSection[] getRiskSectionArray();

    /**
     * retrieves the risk section at position i
     * 
     * @param i
     *            the index in the risk section array
     * @return the risk section at position i
     */
    RiskSection getRiskSectionArray( int i );

    /**
     * use this method to add a Risk section with default settings for this componentId
     * 
     * @param componentId
     *            the referenced component
     * @return the newly created RiskSection
     */
    RiskSection addNewRiskSection( String componentId );

    /**
     * use this method to add a Risk section with default settings for the referenced components in
     * componentIdList
     * 
     * @param componentIdList
     *            the referenced components
     * @return the newly created RiskSection
     */
    RiskSection addNewRiskSection( String[] componentIdList );

    /**
     * remove the risk section at position i of the risk section array
     * 
     * @param i
     *            the position in the array
     */
    void removeRiskSection( int i );

    /**
     * @return the "CostSection" array
     */
    CostSection[] getCostSectionArray();

    /**
     * @param i
     *            the ith position in the cost section array
     * @return the cost section at position i
     */
    CostSection getCostSectionArray( int i );

    /**
     * use this method to add a Cost section with default settings for the referenced components in
     * componentIdList
     * 
     * @param componentIdList
     *            the referenced components
     * @return the newly created CostSection
     */
    CostSection addNewCostSection( String[] componentIdList );

    /**
     * use this method to add a cost section with default settings for the provided componentId
     * 
     * @param componentId
     *            the referenced component
     * @return the newly created CostSection
     */
    CostSection addNewCostSection( String componentId );

    /**
     * remove the cost section at position i of the cost section array
     * 
     * @param i
     *            the position in the array
     */
    void removeCostSection( int i );

    /**
     * @return the "EcoEfficiencySection" array
     * @see EcoEfficiencySection
     */
    EcoEfficiencySection[] getEcoEfficiencySectionArray();

    /**
     * get the eco efficiency at position i
     * 
     * @param i
     *            the index
     * @return the ecoefficiency section at position i
     */
    EcoEfficiencySection getEcoEfficiencySectionArray( int i );

    /**
     * adds a new default eco efficiency section with for the component referenced by componentId
     * 
     * @param componentId
     *            the id of the referenced component
     * @return the newly created eco efficiency section
     */
    EcoEfficiencySection addNewEcoEfficiencySection( String componentId );

    /**
     * adds a new default eco efficiency section for the components referenced in the componentIdList array
     * 
     * @param componentIdList
     *            the ids of the referenced components
     * @return the newly created eco efficiency section
     */
    EcoEfficiencySection addNewEcoEfficiencySection( String[] componentIdList );

    /**
     * removes the eco efficiency section at position i from the eco efficiency array
     * 
     * @param i
     *            the position in the array
     */
    void removeEcoEfficiencySection( int i );

    /**
     * retrieve the array of trust sections.
     * 
     * @return the array of trust sections
     */
    TrustSection[] getTrustSectionArray();

    /**
     * retrieves the trust section at position i
     * 
     * @param i
     *            the position in the trust array
     * @return the trust section at position i
     */
    TrustSection getTrustSectionArray( int i );

    /**
     * adds a new trust section and adds the provided componentId to the scope
     * 
     * @param componentId
     *            the referenced component
     * @return the newly created trustsection
     */
    TrustSection addNewTrustSection( String componentId );

    /**
     * adds a new trust section and adds the provided componentIds to the scope
     * 
     * @param componentIdList
     *            the referenced component
     * @return the newly created trustsection
     */
    TrustSection addNewTrustSection( String[] componentIdList );

    /**
     * removes the trust section at position i
     * 
     * @param i
     *            the position in the trust array
     */
    void removeTrustSection( int i );
}