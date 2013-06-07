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
 * @author hrasheed
 */
public interface EcoEfficiencySection
{

    /**
     * Retrieves the constraints for the LEEDCertification
     * 
     * @return one of [ NotRequired | Certified | Silver | Gold | Platinum ]
     */
    String getLEEDCertification();

    /**
     * Sets constraints for the LEEDCertification
     * 
     * @param leedCertification
     *            one of [ NotRequired | Certified | Silver | Gold | Platinum ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanLEEDCertificationConstraintType.Enum
     */
    void setLEEDCertification( String leedCertification );

    /**
     * Retrieves the constraints for the BREEAMCertification
     * 
     * @return one of [ NotRequired | Pass | Good | VeryGood | Excellent | Outstanding ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanBREEAMCertificationConstraintType.Enum
     */
    String getBREEAMCertification();

    /**
     * Sets the constraints for the BREEAMCertification as String
     * 
     * @param breeamCertification
     *            one of [ NotRequired | Pass | Good | VeryGood | Excellent | Outstanding ]
     */
    void setBREEAMCertification( String breeamCertification );

    /**
     * Retrieves constraint for euCoCC compliance
     * 
     * @return true | false
     */
    boolean getEuCoCCompliant();

    /**
     * Sets constraint for euCoCC compliance
     * 
     * @param euCoCCompliant
     *            true | false
     */
    void setEuCoCCompliant( boolean euCoCCompliant );

    /**
     * @return
     */
    Object getEnergyStarRating();

    /**
     * @param energyStarRating
     */
    void setEnergyStarRating( String energyStarRating );
    
    /**
     * Sets constraint for ISO14000 (whether or not the ISO14001 standard is met)
     * 
     * @param iso14000
     * 			one of [ No | ISO14001-Compliant ]
     */
    void setISO14000( String iso14000 );
    
    /**
     * Retrieves constraint for ISO14000
     * 
     * @return one of [ No | ISO14001-Compliant ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanISO14000Type.Enum
     */
    String getISO14000();
    
    /**
     * Sets GreenStar rating as 4 Star, 5 Star or 6 Star 
     * 
     * @param greenStar
     *            one of [ No | 4 | 5 | 6 ]
     */
    void setGreenStar( String greenStar );
   
    /**
     * Retrieves constraint for GreenStar
     * 
     * @return one of [ No | 4 | 5 | 6 ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanGreenStarType.Enum
     */
    String getGreenStar();
    
    /**
     * Sets constraint for CASBEE with possible values: class C (poor), class B-, class B+, class A, and class S (excellent)
     * 
     * @param casbee
     *            one of [ No | C | B- | B+ | A | S ]
     */
    void setCASBEE( String casbee );
    
    /**
     * Retrieves constraint for CASBEE
     * 
     * @return one of [ No | C | B- | B+ | A | S ]
     * @see eu.optimis.types.xmlbeans.servicemanifest.XmlBeanCASBEEType.Enum
     */   
    String getCASBEE();
    
    /**
     * Retrieves the ecoMetric array
     * 
     * @return array of ecoMetric
     * @see EcoMetric
     */
    EcoMetric[] getEcoMetricArray();

    /**
     * Retrieves the ecoMetric array at position i
     * 
     * @return the EcoMetric
     * @see EcoMetric
     */
    EcoMetric getEcoMetricArray( int i );
    
    /**
     * retrieves the ecoMetric for the provided metric name
     * 
     * @param name
     *            the name of the ecoMetric
     * @return the ecoMetric with the provided name.
     */
    EcoMetric getEcoMetric( String name );

    /**
     * Creates a new EcoMetric
     * 
     * @param metricName
     *            the name of the ecoMetirc
     * @return the created EcoMetric
     * @see EcoMetric
     */
    EcoMetric addNewEcoMetric( String metricName );
    
    /**
     * removes the eco efficiency metric at position i from the ecoMetric array
     * 
     * @param i
     *            the position in the array
     */
    void removeEcoMetric( int i );
    
    /**
     * remove the EcoMetric by its name
     * 
     * @param name
     *            the name of the EcoMetric
     */
    void removeEcoMetric( String name );
    
    /**
     * retrieves the list of components that apply to this eco efficiency section
     * 
     * @return the scope
     */
    Scope getScope();

}