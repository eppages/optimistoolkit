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

import eu.optimis.manifest.api.ovf.sp.ProductSection;
import eu.optimis.manifest.api.ovf.sp.VirtualHardwareSection;
import eu.optimis.manifest.api.sp.ElasticityRule;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.manifest.api.sp.SoftwareDependencies;
import eu.optimis.manifest.api.sp.VirtualMachineComponent;
import eu.optimis.types.xmlbeans.servicemanifest.EncryptionAlgoritmType;
import junit.framework.TestCase;
import org.apache.xmlbeans.XmlOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author arumpl
 */
public class WorkflowTest extends TestCase {
    public void testShouldDemonstrateOptimisWorkflow() {
        //
        // SP Manifest initialization
        //
        Manifest manifest = Manifest.Factory.newInstance("OptimisDemoService", "jboss");

        //
        // Initializing jboss component
        //
        VirtualMachineComponent jbossComponent = manifest.getVirtualMachineDescriptionSection().getVirtualMachineComponentById("jboss");

        jbossComponent.setAffinityConstraints("Low");

        jbossComponent.getAllocationConstraints().setUpperBound(17);
        jbossComponent.getAllocationConstraints().setLowerBound(1);
        jbossComponent.getAllocationConstraints().setInitial(3);

        jbossComponent.getOVFDefinition().getReferences().getFile().setHref("/opt/optimis/repository/image/DemoApp-jboss.img");
        jbossComponent.getOVFDefinition().getDiskSection().getDisk().setCapacity("6252516");
        ProductSection product = jbossComponent.getOVFDefinition().getVirtualSystem().getProductSection();
        product.setProduct("JBOSS");
        product.setVersion("5.1");


        VirtualHardwareSection hardwareSection = jbossComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        hardwareSection.setMemorySize(1024);
        hardwareSection.setNumberOfVirtualCPUs(5);
        hardwareSection.setVirtualHardwareFamily("xen");

        //
        // Adding mysql component
        //
        VirtualMachineComponent mysqlComponent = manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("mysql");
        mysqlComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setMemorySize(512);
        mysqlComponent.getOVFDefinition().getReferences().getFile().setHref("/opt/optimis/repository/image/DemoApp-db.img");
        mysqlComponent.getOVFDefinition().getDiskSection().getDisk().setCapacity("7380016");


        //
        // Adding VPN component
        //
        VirtualMachineComponent vpnComponent = manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("VPN");
        vpnComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setMemorySize(2048);
        vpnComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setNumberOfVirtualCPUs(2);
        vpnComponent.getOVFDefinition().getReferences().getFile().setHref("/opt/optimis/repository/image/DemoApp-vpn.img");
        vpnComponent.getOVFDefinition().getDiskSection().getDisk().setCapacity("6252516");


        //
        // Adding Load Balancer component
        //
        VirtualMachineComponent lbComponent = manifest.getVirtualMachineDescriptionSection().addNewVirtualMachineComponent("LB");
        lbComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection().setMemorySize(512);
        lbComponent.getOVFDefinition().getDiskSection().getDisk().setCapacity("5160576");
        lbComponent.getOVFDefinition().getReferences().getFile().setHref("/opt/optimis/repository/image/DemoApp-lb.img");

        //
        // Set TREC values
        //

        manifest.getTRECSection().getEcoEfficiencySection().setEuCoCCompliant(false);
        manifest.getTRECSection().getEcoEfficiencySection().setEnergyStarRating("No");
        manifest.getTRECSection().getRiskSection().setRiskLevel(1);
        manifest.getTRECSection().getTrustSection().setTrustLevel(5);
        manifest.getTRECSection().getRiskSection().addNewAvailability("P1D", 98);
        manifest.getTRECSection().getRiskSection().addNewAvailability("P1M", 99);

        //
        // Data Protection
        //
        manifest.getDataProtectionSection().getDataEncryptionLevel().setEncryptionAlgorithm(EncryptionAlgoritmType.AES.toString());
        manifest.getDataProtectionSection().setDataProtectionLevel("DPA");
        manifest.getDataProtectionSection().addNewEligibleCountry("DE");
        manifest.getDataProtectionSection().addNewNonEligibleCountry("AF");

        //
        // Elasticity Section
        //

        ElasticityRule rule = manifest.getElasticitySection().addElasticityRule("jboss");
        rule.setKPIName("ThreadCount");
        rule.setWindow("P5M");
        rule.setFrequency(1);
        rule.setQuota(100);
        rule.setTolerance(5);

        //
        // Add Software Dependencies
        //

        SoftwareDependencies jbossDependencies = manifest.getServiceProviderExtensionSection().getSoftwareDependenciesByComponentId("jboss");
        jbossDependencies.addNewDependency().setArtifactId("xmlbeans");
        jbossDependencies.getDependencyArray(0).setVersion("2.3.9");
        jbossDependencies.getDependencyArray(0).setGroupId("org.apache.xmlbeans");

        //
        // Export SP Manifest to String
        //

        String manifestAsString = manifest.toString();
        assertNotNull(manifestAsString);

        //
        // New IP Manifest
        //

        eu.optimis.manifest.api.ip.Manifest ipManifest = eu.optimis.manifest.api.ip.Manifest.Factory.newInstance(manifestAsString);
        assertNotNull(ipManifest);

        //
        // Initializing Incarnated Components
        //

        ipManifest.initializeIncarnatedVirtualMachineComponentsType();
        //check that we get the same amount of components as in the SP Manifest
        assertEquals(4, ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponents().length);

        //check that we get 17 jboss instances
        int numberOfJbossInstances = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponentByComponentId("jboss").getOVFDefinition().getVirtualSystemCollection().length;
        assertEquals(17, numberOfJbossInstances);

        //set properties

        eu.optimis.manifest.api.ovf.ip.ProductSection jbossProduct = ipManifest.getInfrastructureProviderExtensions().getIncarnatedVirtualMachineComponentByComponentId("jboss").getOVFDefinition().getVirtualSystemByComponentId("jboss").getProductSection();
        jbossProduct.getPropertyByKey("ExternalIP").setValue("212.0.127.138");
        jbossProduct.getPropertyByKey("InternalIP").setValue("192.168.252.57");
        jbossProduct.getPropertyByKey("InternalMask").setValue("255.255.255.192");

        //
        // Export completed manifest to a file
        //

        try {
            File file = new File("target/" + manifest.getVirtualMachineDescriptionSection().getServiceId() + ".xml");
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(ipManifest.toXmlBeanObject().xmlText(new XmlOptions().setSavePrettyPrint()));
            System.out.println("Created manifest file was written to " + file.getAbsolutePath());
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

}
