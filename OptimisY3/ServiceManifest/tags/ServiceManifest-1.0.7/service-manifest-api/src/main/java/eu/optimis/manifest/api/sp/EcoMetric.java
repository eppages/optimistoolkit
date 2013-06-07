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
 * Two threasholds: EnergyEfficiency and EcologicalEfficiency Unit: A/(CU/W) as
 * Computing Units per Watt Unit: A/(CU/kgCO2) as CO2 emissions Unit: A/s as
 * money paid for each second we want to establish the minimum thresholds per
 * service in terms of energy efficiency (performance/W, currently as Computing
 * Units per Watt, CU/W) and ecological efficiency (performance/CO2 emissions).
 * These thresholds should be specified with their corresponding penalizations
 * (SLA) in case they are surpassed. These penalizations need to be specified in
 * terms of magnitude (money paid per each CU/W below the threshold) and time
 * (money paid per each second the threshold is surpassed). In addition, they
 * could be specified as soft (desirable thresholds, no penalization if
 * surpassed) or hard (penalization if surpassed) thresholds.
 * 
 * @author hrasheed
 * 
 */
public interface EcoMetric {
	
	/**
     * Retrieves name of the eco metric
     * 
     * @return metric name
     */
    String getName();

    /**
     * Specifies name of the eco metric
     * 
     * @param name           
     */
    void setName( String name );
    
    /**
     * returns the threshold value
     * 
     * @return NotSpecified as string or float value
     */
    Object getThresholdValue();

    /**
     * set the threshold value
     * 
     * @param thresholdValue
     */
    void setThresholdValue( String thresholdValue );
    
    /**
     * Retrieves constraint for SLA type
     * 
     * @return one of [ Soft | Hard ]
     */
    String getSLAType();

    /**
     * Sets constraint for SLA type
     * 
     * @param slaType
     *            one of [ Soft | Hard ]
     */
    void setSLAType( String slaType );
    
    /**
     * returns the magnitudePenalty value
     * 
     * @return NA as string or float value
     */
    Object getMagnitudePenalty();

    /**
     * set the magnitudePenalty value
     * 
     * @param magnitudePenalty
     */
    void setMagnitudePenalty( String magnitudePenalty );

    /**
     * returns the timePenalty value
     * 
     * @return NA as string or float value
     */
    Object getTimePenalty();

    /**
     * set the timePenalty value
     * 
     * @param timePenalty
     */
    void setTimePenalty( String timePenalty );
}
