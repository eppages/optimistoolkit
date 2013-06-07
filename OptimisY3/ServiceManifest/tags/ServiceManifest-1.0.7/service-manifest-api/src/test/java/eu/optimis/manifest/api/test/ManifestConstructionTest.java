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

import eu.optimis.manifest.api.impl.ServiceManifestProperties;
import eu.optimis.manifest.api.ovf.impl.OperatingSystemType;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanDataProtectionLevelType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author arumpl
 *         This class tests the construction of a manifest by using properties and verifies that the
 *         created documents are valid.
 */
public class ManifestConstructionTest extends TestCase
{
    /**
     *
     */
    private static final double DEFAULT_COST_CAP = 120.0;

    /**
     *
     */
    private static final int DEFAULT_MEMORY_SIZE = 528;

    /**
     *
     */
    private static final int DEFAULT_CPU_SPEED = 500;

    public void testShouldCreateManifestWithProperties()
    {

        String myComponentId = "abc";
        Properties properties = new ServiceManifestProperties();
        properties.setProperty( ServiceManifestProperties.VM_NUMBER_OF_VIRTUAL_CPU, "10" );
        properties.setProperty( ServiceManifestProperties.VM_OPERATING_SYSTEM_ID,
                String.valueOf( OperatingSystemType.LINUX.number() ) );
        properties.setProperty( ServiceManifestProperties.VM_OPERATING_SYSTEM_DESCRIPTION,
                OperatingSystemType.LINUX.name() );
        properties.setProperty( ServiceManifestProperties.VM_VIRTUAL_HARDWARE_FAMILY, "xen" );
        properties.setProperty( ServiceManifestProperties.VM_MEMORY_SIZE, "528" );
        properties.setProperty( ServiceManifestProperties.VM_CPU_SPEED, "500" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MAX, "15" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_MIN, "4" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_INITIAL, "10" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_AFFINITY, "High" );
        properties.setProperty( ServiceManifestProperties.VM_INSTANCES_ANTI_AFFINITY, "High" );
        
        properties.setProperty( ServiceManifestProperties.TRUST_LEVEL, "5" );
        properties.setProperty( ServiceManifestProperties.RISK_LEVEL, "2" );
        properties.setProperty( ServiceManifestProperties.ECO_LEED_CERTIFICATION, "Certified" );
        properties.setProperty( ServiceManifestProperties.ECO_BREEAM_CERTIFICATION, "Excellent" );
        properties.setProperty( ServiceManifestProperties.ECO_EUCOC_COMPLIANT, "true" );
        properties.setProperty( ServiceManifestProperties.ECO_ENERGY_STAR_RATING, "5" );
        properties.setProperty( ServiceManifestProperties.ECO_ISO14000, "ISO14001-Compliant" );
        properties.setProperty( ServiceManifestProperties.ECO_GREEN_STAR, "6" );
        properties.setProperty( ServiceManifestProperties.ECO_CASBEE, "B+" );
        properties.setProperty( ServiceManifestProperties.COST_CURRENCY, "USD" );
        properties.setProperty( ServiceManifestProperties.COST_MAX, "120.0" );
        properties.setProperty( ServiceManifestProperties.COST_MIN, "5.0" );
        properties.setProperty( ServiceManifestProperties.DATA_PROTECTION_LEVEL,
                XmlBeanDataProtectionLevelType.NONE.toString() );
        properties.setProperty( ServiceManifestProperties.DATA_PROTECTION_ENCRYPTION_ALGORITHM,
                XmlBeanEncryptionAlgoritmType.TWOFISH.toString() );
        properties.setProperty( ServiceManifestProperties.SP_EXTENSION_SECURITY_VPN_ENABLED,
                String.valueOf( true ) );
        properties.setProperty( ServiceManifestProperties.SP_EXTENSION_SECURITY_SSH_ENABLED,
                String.valueOf( true ) );

        Manifest newManifest = Manifest.Factory.newInstance( "xxx", myComponentId, properties );

        assertEquals( "minimum trust level does not match", 5,
                newManifest.getTRECSection().getTrustSectionArray( 0 )
                        .getMinimumTrustLevel() );
        assertEquals( "number of virtual cpus does not match", 10,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                        .getNumberOfVirtualCPUs() );
        assertEquals( "operating system ID does not match", OperatingSystemType.LINUX.number(),
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getOperatingSystem().getId() );
        assertEquals( "operating system description does not match",
                OperatingSystemType.LINUX.name(),
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getOperatingSystem()
                        .getDescription() );
        assertEquals( "virtual hardware family does not match", "xen",
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                        .getVirtualHardwareFamily() );
        assertEquals( "memory size does not match", DEFAULT_MEMORY_SIZE,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                        .getMemorySize() );
        assertEquals( "cpu speed does not match", DEFAULT_CPU_SPEED,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                        .getCPUSpeed() );
        assertEquals( "maxNumberOfInstances does not match", 15,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getAllocationConstraints().getUpperBound() );
        assertEquals( "minNumberOfInstances does not match", 4,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getAllocationConstraints().getLowerBound() );
        assertEquals( "initialNumberOfInstances does not match", 10,
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getAllocationConstraints().getInitial() );
        assertEquals( "affinityConstraints does not match", "High",
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getAffinityConstraints() );
        assertEquals( "anti affinityConstraints does not match", "High",
                newManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( myComponentId )
                        .getAntiAffinityConstraints() );
        assertEquals( "riskLevel does not match", 2,
                newManifest.getTRECSection().getRiskSectionArray( 0 )
                        .getRiskLevel() );
        assertEquals( "ecoLEEDCertification does not match", "Certified",
                newManifest.getTRECSection().getEcoEfficiencySectionArray( 0 )
                        .getLEEDCertification() );
        assertEquals( "ecoBREEAMCertification does not match", "Excellent",
                newManifest.getTRECSection().getEcoEfficiencySectionArray( 0 )
                        .getBREEAMCertification() );
        assertEquals( "ecoEuCoCCompliant does not match", true, newManifest.getTRECSection()
                .getEcoEfficiencySectionArray( 0 )
                .getEuCoCCompliant() );
        assertEquals( "ecoEnergyStarRating does not match", 5, newManifest.getTRECSection()
                .getEcoEfficiencySectionArray( 0 ).getEnergyStarRating() );
        assertEquals( "ecoISO14000 does not match", "ISO14001-Compliant", newManifest.getTRECSection()
                .getEcoEfficiencySectionArray( 0 ).getISO14000() );
        assertEquals( "ecoGreenStar does not match", "6", newManifest.getTRECSection()
                .getEcoEfficiencySectionArray( 0 ).getGreenStar() );
        assertEquals( "ecoCASBEE does not match", "B+", newManifest.getTRECSection()
                .getEcoEfficiencySectionArray( 0 ).getCASBEE() );
        assertEquals( "costCurrency does not match", "USD",
                newManifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 )
                        .getCurrency() );
        assertEquals( "costPlanCap does not match", ( float ) DEFAULT_COST_CAP,
                newManifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 )
                        .getPlanCap() );
        assertEquals( "costPlanFloor does not match", ( float ) 5.0, newManifest.getTRECSection()
                .getCostSectionArray( 0 )
                .getPricePlanArray( 0 )
                .getPlanFloor() );
        assertEquals( "dataProtectionLevel does not match",
                XmlBeanDataProtectionLevelType.NONE.toString(),
                newManifest.getDataProtectionSection().getDataProtectionLevel() );
        assertEquals( "encryptionAlgorithm does not match",
                XmlBeanEncryptionAlgoritmType.TWOFISH.toString(),
                newManifest.getDataProtectionSection().getDataEncryptionLevel()
                        .getEncryptionAlgorithm() );

        assertEquals(
                "ssh does not match",
                true,
                newManifest.getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( myComponentId )
                        .isSecuritySSHbased() );
        assertEquals(
                "vpn does not match",
                true,
                newManifest.getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( myComponentId )
                        .isSecurityVPNbased() );
    }

    public void testShouldCreateManifestWithPropertiesFile() throws IOException
    {
        // START SNIPPET: manifestWithPropertyFile
        // load the properties from the sample.manifest.properties file
        InputStream in =
                ManifestConstructionTest.class.getResourceAsStream( "/sample.manifest.properties" );
        Properties properties = new Properties();

        properties.load( in );
        in.close();

        // now we can create a new manifest instance by using the properties file

        Manifest manifest =
                Manifest.Factory
                        .newInstance( "Demo Service", "Demo Service Component", properties );

        // END SNIPPET manifestWithPropertyFile

        //check that the manifest has no errors
        assertFalse( "Manifest should be valid.", manifest.hasErrors() );
    }
}
