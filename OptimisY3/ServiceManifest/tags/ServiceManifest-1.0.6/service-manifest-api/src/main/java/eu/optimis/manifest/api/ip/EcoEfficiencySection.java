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
public interface EcoEfficiencySection
{

    /**
     * @return the constraints for the LEEDCertification: one of [ NotRequired | Certified | Silver | Gold |
     *         Platinum ]
     * @see eu.optimis.manifest.api.sp.EcoEfficiencySection#getLEEDCertification()
     */
    String getLEEDCertification();

    /**
     * @return the constraints for the BREEAMCertification: one of [ NotRequired | Pass | Good | VeryGood |
     *         Excellent | Outstanding ]
     * @see eu.optimis.manifest.api.sp.EcoEfficiencySection#getBREEAMCertification()
     */
    String getBREEAMCertification();

    /**
     * @return constraint for euCoC compliance
     * @see eu.optimis.manifest.api.sp.EcoEfficiencySection#getEuCoCCompliant()
     */
    boolean getEuCoCCompliant();

    /**
     * @return
     * @see eu.optimis.manifest.api.sp.EcoEfficiencySection#getEnergyStarRating()
     */
    // TODO: should be reviewed in schema. Check if schema converts properly into classes
    Object getEnergyStarRating();

    /**
     * @return the scope
     * @see eu.optimis.manifest.api.sp.EcoEfficiencySection#getScope()
     */
    Scope getScope();

}