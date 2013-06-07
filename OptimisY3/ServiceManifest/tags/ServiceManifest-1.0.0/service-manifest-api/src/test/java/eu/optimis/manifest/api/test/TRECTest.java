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

import eu.optimis.manifest.api.sp.Availability;
import eu.optimis.types.xmlbeans.servicemanifest.BREEAMCertificationConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.LEEDCertificationConstraintType;


/**
 * Created by IntelliJ IDEA.
 * Email: karl.catewicz@scai.fraunhofer.de
 * Date: 09.12.2011
 * Time: 18:14:01
 */
public class TRECTest extends AbstractTestApi {

    public TRECTest(String testName) {
        super(testName);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        manifest.getTRECSection().getRiskSection().addNewAvailability("P1D", 99);
        manifest.getTRECSection().getRiskSection().addNewAvailability("P1M", 99);
    }

    public void testThatTrustLevelIs5() {
        assertEquals("wrong trustlevel", 5, manifest.getTRECSection().getTrustSection().getTrustLevel());
    }

    public void testAssessmentIntervalIs98PerDay() {
        Availability availability = manifest.getTRECSection().getRiskSection().getAvailabilityArray(0);
        assertEquals("assessment interval is not 1 Day", "P1D", availability.getAssessmentInterval().toString());
        assertEquals("availability is not 99", 99.0, availability.getValue());
    }

    public void testAssessmentIntervalIs99PerMonth() {
        Availability availability = manifest.getTRECSection().getRiskSection().getAvailabilityArray(1);
        assertEquals("assessment interval is not 1 Month", "P1M", availability.getAssessmentInterval().toString());
        assertEquals("availability is not 98", new Double(99), availability.getValue());
    }

    public void testSetAvailability() {
        manifest.getTRECSection().getRiskSection().getAvailabilityArray(1).setAssessmentInterval("P2M");
        assertEquals("availability is not P2M", "P2M", manifest.getTRECSection().getRiskSection().getAvailabilityArray(1).getAssessmentInterval());
    }

    public void testEnergyStarRatingIsNo() {
        assertEquals("No", manifest.getTRECSection().getEcoEfficiencySection().getEnergyStarRating());
    }

    public void testEuCoCCompliantIsFalse() {
        assertFalse("EuCoCCompliant is not false.", manifest.getTRECSection().getEcoEfficiencySection().getEuCoCCompliant());
    }

    public void testShouldSetGetEcoEfficiencyCertificates() {
        manifest.getTRECSection().getEcoEfficiencySection().setBREEAMCertification(BREEAMCertificationConstraintType.EXCELLENT.toString());
        assertEquals("Excellent", manifest.getTRECSection().getEcoEfficiencySection().getBREEAMCertification());

        manifest.getTRECSection().getEcoEfficiencySection().setLEEDCertification(LEEDCertificationConstraintType.CERTIFIED.toString());
        assertEquals("Certified", manifest.getTRECSection().getEcoEfficiencySection().getLEEDCertification());
    }

}
