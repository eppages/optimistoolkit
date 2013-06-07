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
package eu.optimis.manifest.api.test;

import eu.optimis.manifest.api.sp.*;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanBREEAMCertificationConstraintType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanLEEDCertificationConstraintType;

/**
 * Created by IntelliJ IDEA. Email: karl.catewicz@scai.fraunhofer.de Date: 09.12.2011 Time: 18:14:01
 */
public class TRECTest extends AbstractTestApi
{

    private static final int COST_ABSOLUTE = 200;

    private static final double COST_PLAN_FLOOR = 50.50;

    private static final double COST_PLAN_CAP = 200.20;

    private static final double AVAILABILITY_PER_MONTH = 99;

    private static final double AVAILABILITY_PER_DAY = 98;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        //we need an assessment interval with 98 % availability per day and 99 per Month.
        getManifest().getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1D", 98 );
        getManifest().getTRECSection().getRiskSectionArray( 0 ).addNewAvailability( "P1M", 99 );
    }

    public void testThatTrustLevelIs5()
    {
        assertEquals( "wrong trustlevel", 5,
                getManifest().getTRECSection().getTrustSectionArray( 0 )
                        .getMinimumTrustLevel() );
    }

    public void testSocialNetworkingTrustLevel()
    {
        getManifest().getTRECSection().getTrustSectionArray( 0 ).setSocialNetworkingTrustLevel( 2 );
        assertEquals( 2, getManifest().getTRECSection().getTrustSectionArray( 0 )
                .getSocialNetworkingTrustLevel() );
    }

    public void testAssessmentIntervalIs98PerDay()
    {
        Availability availability =
                getManifest().getTRECSection().getRiskSectionArray( 0 ).getAvailabilityArray( 0 );
        assertEquals( "assessment interval is not 1 Day", "P1D",
                availability.getAssessmentInterval()
                        .toString() );
        assertEquals( "availability is not 98", AVAILABILITY_PER_DAY, availability.getValue() );
    }

    public void testAssessmentIntervalIs99PerMonth()
    {
        Availability availability =
                getManifest().getTRECSection().getRiskSectionArray( 0 ).getAvailabilityArray( 1 );
        assertEquals( "assessment interval is not 1 Month", "P1M",
                availability.getAssessmentInterval()
                        .toString() );
        assertEquals( "availability is not " + AVAILABILITY_PER_MONTH,
                new Double( AVAILABILITY_PER_MONTH ),
                availability.getValue() );
    }

    public void testSetAvailability()
    {
        getManifest().getTRECSection().getRiskSectionArray( 0 ).getAvailabilityArray( 1 )
                .setAssessmentInterval( "P2M" );
        assertEquals( "availability is not P2M", "P2M", getManifest().getTRECSection()
                .getRiskSectionArray( 0 )
                .getAvailabilityArray( 1 )
                .getAssessmentInterval() );
    }

    public void testEnergyStarRatingIsNo()
    {
        assertEquals( "No", getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 )
                .getEnergyStarRating() );
    }

    public void testEuCoCCompliantIsFalse()
    {
        assertFalse( "EuCoCCompliant is not false.", getManifest().getTRECSection()
                .getEcoEfficiencySectionArray( 0 )
                .getEuCoCCompliant() );
    }

    public void testShouldSetGetEcoEfficiencyCertificates()
    {
        getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 )
                .setBREEAMCertification(
                        XmlBeanBREEAMCertificationConstraintType.EXCELLENT.toString() );
        assertEquals( "Excellent", getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 )
                .getBREEAMCertification() );

        getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 )
                .setLEEDCertification(
                        XmlBeanLEEDCertificationConstraintType.CERTIFIED.toString() );
        assertEquals( "Certified", getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 )
                .getLEEDCertification() );
    }

    public void testAddRemoveEcoEfficiencySections()
    {

        int ecoSizeBefore = getManifest().getTRECSection().getEcoEfficiencySectionArray().length;

        System.out.println( ecoSizeBefore );
        getManifest().getTRECSection().addNewEcoEfficiencySection( "LB" );

        int ecoSizeAfter = getManifest().getTRECSection().getEcoEfficiencySectionArray().length;

        System.out.println( ecoSizeAfter );
        getManifest().getTRECSection().removeEcoEfficiencySection( ecoSizeAfter - 1 );

        assertEquals( ecoSizeBefore, ecoSizeAfter - 1 );
    }

    public void testCostSection()
    {
        PricePlan plan =
                getManifest().getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );
        plan.setCurrency( "USD" );
        plan.setPlanCap( ( float ) COST_PLAN_CAP );
        plan.setPlanFloor( ( float ) COST_PLAN_FLOOR );

        plan.addNewPriceComponent( "OptimisMembershipCosts" );

        plan.getPriceComponentArray( 0 ).getPriceLevelArray( 0 )
                .setAbsoluteAmount( ( float ) COST_ABSOLUTE );
        plan.getPriceComponentArray( 0 ).getPriceLevelArray( 0 ).setMultiplier( "Hr" );
        plan.getPriceComponentArray( 0 ).getPriceLevelArray( 0 ).setName( "jboss" );
        plan.getPriceComponentArray( 0 ).getPriceLevelArray( 0 ).setPriceType( "USAGE" );

        assertEquals( ( float ) COST_ABSOLUTE,
                plan.getPriceComponentArray( 0 ).getPriceLevelArray( 0 )
                        .getAbsoluteAmount() );
    }

    public void testAddRemovePriceComponent()
    {
        String priceComponentName = "MembershipCharges";
        PricePlan plan =
                getManifest().getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );

        int lengthBefore = plan.getPriceComponentArray().length;
        System.out.println( "Number of price components in cost section: " + lengthBefore );

        PriceComponent priceComponent = plan.addNewPriceComponent( priceComponentName );

        assertNotNull( priceComponent );

        System.out.println(
                "Price component added:\n" + plan.getPriceComponentArray( lengthBefore ) );

        System.out.println( "Number of price components in cost section after adding: "
                            + plan.getPriceComponentArray().length );

        // check that both methods return the same component
        assertEquals( plan.getPriceComponent( priceComponentName ).toString(),
                plan.getPriceComponentArray( lengthBefore ).toString() );
        plan.removePriceComponent( priceComponentName );

        int lengthAfter = plan.getPriceComponentArray().length;
        assertEquals( lengthBefore, lengthAfter );
        assertNull( plan.getPriceComponent( priceComponentName ) );
    }

    public void testAddRemoveCostSection()
    {
        String[] componentIds = { "jboss", "mysql", "VPN" };
        int before = getManifest().getTRECSection().getCostSectionArray().length;
        System.out.println( "Cost sections in manifest before adding: " + before );

        CostSection costSection = getManifest().getTRECSection().addNewCostSection( componentIds );

        assertNotNull( costSection );
        System.out.println( "cost section added: \n" + costSection.toString() );

        getManifest().getTRECSection().removeCostSection( 1 );

        int after = getManifest().getTRECSection().getCostSectionArray().length;

        System.out.println( "Cost sections in manifest after removing: " + after );

        assertEquals( before, after );
    }

    public void testAddRemoveRiskSection()
    {
        int riskLengthBefore = getManifest().getTRECSection().getEcoEfficiencySectionArray().length;
        getManifest().getTRECSection().addNewRiskSection( "jboss" );
        assertEquals( "jboss", getManifest().getTRECSection().getRiskSectionArray( 1 ).getScope()
                .getComponentIdArray( 0 ) );
        assertEquals( "Risk section availability array should be 0 when adding it.", 0,
                getManifest().getTRECSection().getRiskSectionArray( 1 )
                        .getAvailabilityArray().length );
        // remove the section
        getManifest().getTRECSection().removeRiskSection( riskLengthBefore );
        int riskLengthAfter = getManifest().getTRECSection().getRiskSectionArray().length;

        assertEquals( riskLengthBefore, riskLengthAfter );
    }

    public void testAddRemoveTrustSection()
    {
        int trustLengthBefore = getManifest().getTRECSection().getTrustSectionArray().length;
        // when we add a trust section
        TrustSection trustSection = getManifest().getTRECSection().addNewTrustSection( "jboss" );
        assertEquals( "jboss", trustSection.getScope().getComponentIdArray( 0 ) );
        assertEquals( 5, trustSection.getMinimumTrustLevel() );
        // and then remove it again
        getManifest().getTRECSection().removeTrustSection( trustLengthBefore );

        int trustLengthAfter = getManifest().getTRECSection().getTrustSectionArray().length;

        // then the length before and after must be the same
        assertEquals( trustLengthBefore, trustLengthAfter );
    }

    public void testScopeArraySections()
    {
        String[] componentIds = { "xxx", "yyy", "zzz" };
        EcoEfficiencySection ecoSection =
                getManifest().getTRECSection().getEcoEfficiencySectionArray( 0 );
        ecoSection.getScope().setComponentIdArray( componentIds );
        assertEquals( 3, ecoSection.getScope().getComponentIdArray().length );
        assertTrue( ecoSection.getScope().contains( "yyy" ) );

        TrustSection trustSection = getManifest().getTRECSection().getTrustSectionArray( 0 );

        trustSection.getScope().setComponentIdArray( componentIds );
        assertEquals( 3, trustSection.getScope().getComponentIdArray().length );
        assertTrue( trustSection.getScope().contains( "yyy" ) );

        RiskSection riskSection = getManifest().getTRECSection().getRiskSectionArray( 0 );
        riskSection.getScope().setComponentIdArray( componentIds );
        assertEquals( 3, riskSection.getScope().getComponentIdArray().length );
        assertTrue( riskSection.getScope().contains( "yyy" ) );
    }
}
