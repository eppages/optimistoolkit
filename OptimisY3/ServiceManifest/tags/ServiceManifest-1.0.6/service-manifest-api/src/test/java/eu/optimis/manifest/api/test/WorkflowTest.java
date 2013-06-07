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

import eu.optimis.manifest.api.ip.AllocationPattern;
import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.manifest.api.ip.InfrastructureProviderExtension;
import eu.optimis.manifest.api.ovf.impl.DiskFormatType;
import eu.optimis.manifest.api.ovf.impl.OperatingSystemType;
import eu.optimis.manifest.api.ovf.sp.OperatingSystem;
import eu.optimis.manifest.api.ovf.sp.ProductSection;
import eu.optimis.manifest.api.ovf.sp.VirtualHardwareSection;
import eu.optimis.manifest.api.sp.*;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanEncryptionAlgoritmType;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanLEEDCertificationConstraintType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author arumpl
 */
public class WorkflowTest extends AbstractTestApi
{

    /**
     *
     */
    private static final double PRICE_PLAN_CAP = 99.80;

    /**
     *
     */
    private static final int MIN_CPU_SPEED = 500;

    private static final int DEFAULT_STORAGE_CAPACITY = 500;

    private static final int DEFAULT_AVAILABILITY_PER_MONTH = 99;

    private static final int DEFAULT_AVAILABILITY_PER_DAY = 98;

    private static final int DEFAULT_MEMORY_SIZE = 2048;

    private static final int AC_INITIAL = 3;

    private static final int AC_LOWER_BOUND = 1;

    private static final int AC_UPPER_BOUND = 17;

    private static final byte[] TEST_TOKEN =
            new byte[]{ 0x6b, 0x01, 0x6f, 0x1f, 0x10, 0x0a, 0x6b, 0x01, 0x5f, 0x12,
                        0x34, 0x4a, 0x5b, 0x01, 0x6f, 0x2f, 0x10, 0x0a };

    private static final byte[] SSH_KEY =
            new byte[]{ 0x6b, 0x01, 0x6f, 0x1f, 0x10, 0x0a, 0x6b, 0x01, 0x5f, 0x12, 0x34,
                        0x4a, 0x5b, 0x01, 0x6f, 0x2f, 0x10, 0x0a };

    public static final byte[] DATAM_MANAGER_KEY = new byte[]{ -86, -86, 39, 30 };

    /**
     * This test demonstrates the basic OPTIMIS workflow, starting with creating a new manifest document from
     * the scratch using the Manifest SP API. The test case adds a set of new components to the SP manifest,
     * creates a set of TREC parameters and a set of elasticity/affinity rules per component. Additionally, a
     * set of software dependencies are configured for the JBoss component. Finally, the manifest is
     * serialized and imported as IP Manifest (SP extensions are preserved).
     * <p/>
     * In a last step, the Manifest is imported with the IP API and the OVFs for all possible virtual systems
     * that can be deployed are incarnated.
     */
    public void testShouldDemonstrateOptimisWorkflow()
    {

        //
        // SP Manifest initialization
        //
        Manifest manifest = Manifest.Factory.newInstance( "OptimisDemoService", "jboss" );

        //
        // add example components (JBoss, VPN, LB) in manifest and update affinity, TREC, data management and
        // elasticity settings
        //
        initializeComponents( manifest );
        setAffinityConstraints( manifest );
        setTRECValues( manifest );
        setDataManagementConstraints( manifest );
        setElasticityRules( manifest );

        //
        // Configure JBoss component with additional software dependencies
        //
        configureJBossSoftwareDependencies( manifest );

        //
        // to connect to the data manager we have to provide the key
        //
        setDataManagerKey();

        //
        // Export SP Manifest
        //
        // validate the manifest before exporting
        validate( manifest );

        // export as string (we cannot export if manifest is invalid)
        String manifestAsString = manifest.toString();
        writeToFile( manifest.toXmlBeanObject(), "SP-Manifest" );

        //
        // New IP Manifest
        //

        // START SNIPPET: IPManifest
        eu.optimis.manifest.api.ip.Manifest ipManifest =
                eu.optimis.manifest.api.ip.Manifest.Factory.newInstance( manifestAsString );

        // END SNIPPET: IPManifest
        //
        // Initializing Incarnated Components
        //

        // START SNIPPET: incarnatedVMComponents
        // this creates for each component a VirtualSystemGroup with all possible vm instances inside
        // it also adds a contextualization file link for each vm instance.
        ipManifest.initializeIncarnatedVirtualMachineComponents();

        //
        // Set properties in the OVF document of the jboss component
        //
        InfrastructureProviderExtension ipExtensions =
                ipManifest.getInfrastructureProviderExtensions();
        IncarnatedVirtualMachineComponent jbossComponent =
                ipExtensions.getIncarnatedVirtualMachineComponentByComponentId( "jboss" );
        eu.optimis.manifest.api.ovf.ip.ProductSection jbossProduct =
                jbossComponent.getOVFDefinition().getVirtualSystemArray( 0 ).getProductSection();
        jbossProduct.getPropertyByKey( "ExternalIP" ).setValue( "212.0.127.138" );
        jbossProduct.getPropertyByKey( "InternalIP" ).setValue( "192.168.252.57" );
        jbossProduct.getPropertyByKey( "InternalMask" ).setValue( "255.255.255.192" );

        // END SNIPPET: incarnatedVMComponents

        //
        // Add allocation pattern
        //
        // START SNIPPET: allocationOffer

        AllocationPattern pattern =
                ipExtensions.addNewAllocationOffer().addNewAllocationPattern( "jboss" );

        // add basic physical hosts
        pattern.addNewPhysicalHost( "my.jboss.hostname.first" );
        pattern.addNewPhysicalHost( "my.jboss.hostname.second" );
        pattern.addNewPhysicalHost( "my.jboss.hostname.third" );

        // add an elastic physical host
        pattern.addNewPhysicalHost( "my.jboss.hostname.elastic" ).setElastic( true );

        ipExtensions.getAllocationOffer().setCost( XmlObject.Factory.newInstance() );
        ipExtensions.getAllocationOffer().setRisk( 12 );

        // set the decision
        ipExtensions.getAllocationOffer().setDecision( "accepted" );

        // END SNIPPET: allocationOffer

        //
        // Export completed manifest to a file
        //
        writeToFile( ipManifest.toXmlBeanObject(), "IP-Manifest" );
    }

    private void setDataManagerKey()
    {

        //START SNIPPET: dataManagerKey
        getManifest().getServiceProviderExtensionSection().setDataManagerKey( DATAM_MANAGER_KEY );
        //END SNIPPET: dataManagerKey
    }

    private void validate( Manifest manifest )
    {
        // START SNIPPET: manifestValidation
        if ( manifest.hasErrors() )
        {
            for ( XmlError error : manifest.getErrors() )
            {
                System.out.println( error );
            }
        }
        // END SNIPPET: manifestValidation
    }

    /**
     * @param manifest
     */
    private void configureJBossSoftwareDependencies( Manifest manifest )
    {
        // START SNIPPET: SPExtensions

        VirtualMachineComponentConfiguration jbossConfiguration =
                manifest.getServiceProviderExtensionSection()
                        .getVirtualMachineComponentConfiguration( "jboss" );
        // add software dependencies
        jbossConfiguration.addNewDependency().setArtifactId( "xmlbeans" );
        jbossConfiguration.getSoftwareDependencies( 0 ).setVersion( "2.3.9" );
        jbossConfiguration.getSoftwareDependencies( 0 ).setGroupId( "org.apache.xmlbeans" );

        // enable security
        jbossConfiguration.enableSSHSecurity();
        jbossConfiguration.enableVPNSecurity();

        // add license tokens
        jbossConfiguration.addToken( TEST_TOKEN );

        // set ssh key
        jbossConfiguration.setSSHKey( SSH_KEY );

        // END SNIPPET: SPExtensions
    }

    /**
     * @param manifest
     */
    private void setElasticityRules( Manifest manifest )
    {

        // it was decided to switch back to elasticity of year 1.
        // START SNIPPET: elasticityY1
        // add a new CPU Speed rule for the LB component
        manifest.getElasticitySection().addNewRule( "LB", "CPUSpeed" );

        // the monitoring window
        manifest.getElasticitySection().getRule( 0 ).setWindow( "P1M" );

        // a tolerance of 5, a quota of 95 to 105 is tolerated
        manifest.getElasticitySection().getRule( 0 ).setTolerance( 5 );

        // the frequency of the monitoring check
        manifest.getElasticitySection().getRule( 0 ).setFrequency( 1 );

        // the quota. e.g. a CPU Speed of 500 Mhz minimum
        manifest.getElasticitySection().getRule( 0 ).setQuota( MIN_CPU_SPEED );

        // add another component to this rule, so they both have to comply to the rule
        manifest.getElasticitySection().getRule( 0 ).getScope().addComponentId( "mysql" );

        // END SNIPPET: elasticityY1

        //
        // Elasticity Section
        //

        // we want to scale up if the provided availability does not fulfill our requirements.
        // first we add two variables, required and provided availability, those can be used to evaluate the
        // expression in the rules

        // internal: the location is an xpath expression which refers to a variable in our manifest document
        // manifest.getElasticitySection().addNewInternalVariable( "REQ_AVAILABILITY", "int",
        // "//opt:RiskSection/opt:AvailabilityArray/Availability[@opt:assessmentInterval='P1D']" );
        // // external: the url to a monitoring interface which monitors the actual value of our variable
        // manifest.getElasticitySection().addNewExternalVariable( "PROVIDED_AVAILABILITY", "int",
        // "http://my-monitoring-system.org/DemoApp/system-jboss/availability" );
        //
        // // now we add a rule and use the variables in our expression
        // ElasticityRule rule = manifest.getElasticitySection().addNewRule( "jboss", "MinAvailability" );
        // rule.getCondition().setExpression( "REQ_AVAILABILITY le PROVIDED_AVAILABILITY" );
        // rule.getCondition().getAssessmentCriteria().setFrequency( 1 );
        // rule.getCondition().getAssessmentCriteria().setWindow( "P1D" );
        // rule.getEffect().setAction( "scaleUp" );
        // rule.getEffect().setImportance( 10 );
    }

    /**
     * @param manifest
     */
    private void setDataManagementConstraints( Manifest manifest )
    {
        //
        // Data Protection
        //
        // START SNIPPET: dataProtection
        // set the encryption algorithm
        manifest.getDataProtectionSection().getDataEncryptionLevel()
                .setEncryptionAlgorithm( XmlBeanEncryptionAlgoritmType.AES.toString() );

        // set the data protection level
        manifest.getDataProtectionSection().setDataProtectionLevel( "DPA" );

        // add new eligible or non eligible countries
        manifest.getDataProtectionSection().addNewEligibleCountry( "DE" );
        manifest.getDataProtectionSection().addNewNonEligibleCountry( "AF" );

        // END SNIPPET: dataProtection

        // for now we can also specify the desired data storage space here by providing
        // the desired capacity and an allocation unit.

        // START SNIPPET: dataStorage
        // set the capacity
        manifest.getDataProtectionSection().getDataStorage()
                .setCapacity( DEFAULT_STORAGE_CAPACITY );

        // set the allocation unit
        // byte * 2^10 -> kb <br/>
        // byte * 2^20 -> mb <br/>
        // byte * 2^30 -> gb <br/>
        // byte * 2^40 -> tb
        manifest.getDataProtectionSection().getDataStorage().setAllocationUnit( "byte * 2^20" );
        // END SNIPPET dataStorage

    }

    /**
     * @param manifest
     */
    private void setTRECValues( Manifest manifest )
    {
        // START SNIPPET: TRECValues
        String[] vpnAndLbComponents = { "VPN", "LB" };
        String[] mysqlAndJbossComponents = { "mysql", "jboss" };

        //
        // set eco efficiency
        //
        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).setEuCoCCompliant( false );
        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).setEnergyStarRating( "No" );
        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 )
                .setLEEDCertification( XmlBeanLEEDCertificationConstraintType.SILVER.toString() );

        // this eco efficiency section applies to VPN and LB
        manifest.getTRECSection().getEcoEfficiencySectionArray( 0 ).getScope()
                .setComponentIdArray( vpnAndLbComponents );

        // add another eco efficiency section for mysql and jboss component
        EcoEfficiencySection eco =
                manifest.getTRECSection().addNewEcoEfficiencySection( mysqlAndJbossComponents );
        eco.setEuCoCCompliant( true );
        eco.setLEEDCertification( XmlBeanLEEDCertificationConstraintType.GOLD.toString() );

        //
        // set trust level
        //
        manifest.getTRECSection().getRiskSectionArray( 0 ).setRiskLevel( 1 );
        manifest.getTRECSection().getTrustSectionArray( 0 ).setTrustLevel( 5 );
        manifest.getTRECSection().getTrustSectionArray( 0 ).getScope()
                .setComponentIdArray( mysqlAndJbossComponents );

        // we have another trust level for the VPN and LB components
        TrustSection trust = manifest.getTRECSection().addNewTrustSection( vpnAndLbComponents );
        trust.setSocialNetworkingTrustLevel( 1 );
        trust.setMinimumTrustLevel( 3 );
        trust.setTrustLevel( 10 );

        //
        // add risk definitions
        //
        RiskSection riskForMysqlAndJboss = manifest.getTRECSection().getRiskSectionArray( 0 );
        riskForMysqlAndJboss.getScope().setComponentIdArray( mysqlAndJbossComponents );
        riskForMysqlAndJboss.addNewAvailability( "P1D", DEFAULT_AVAILABILITY_PER_DAY );
        riskForMysqlAndJboss.addNewAvailability( "P1M", DEFAULT_AVAILABILITY_PER_MONTH );

        RiskSection riskForLBAndVPN =
                manifest.getTRECSection().addNewRiskSection( vpnAndLbComponents );
        riskForLBAndVPN.addNewAvailability( "P1D", DEFAULT_AVAILABILITY_PER_DAY );
        riskForLBAndVPN.addNewAvailability( "P1M", DEFAULT_AVAILABILITY_PER_MONTH );

        //
        // Add Cost section
        //

        // add all components to the scope:
        String[] componentIds = { "VPN", "LB", "jboss", "mysql" };
        manifest.getTRECSection().getCostSectionArray( 0 ).getScope()
                .setComponentIdArray( componentIds );
        PricePlan pricePlan =
                manifest.getTRECSection().getCostSectionArray( 0 ).getPricePlanArray( 0 );
        // set the maximum price for the whole service to 99.80 EUR
        pricePlan.setPlanCap( ( float ) PRICE_PLAN_CAP );
        pricePlan.setCurrency( "EUR" );
        // END SNIPPET: TRECValues
    }

    /**
     * @param manifest
     */
    private void setAffinityConstraints( Manifest manifest )
    {
        // START SNIPPET: affinityConstraints
        // set affinity constraints between jboss and mysql to "High"
        String[] highAffinityComponents = { "mysql", "LB" };
        manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 ).getScope()
                .setComponentIdArray( highAffinityComponents );
        manifest.getVirtualMachineDescriptionSection().getAffinityRule( 0 )
                .setAffinityConstraints( "High" );

        // set affinity constraints between VPN and LB to "Low"
        String[] lowAffinityComponents = { "VPN", "LB" };
        manifest.getVirtualMachineDescriptionSection()
                .addNewAffinityRule( lowAffinityComponents, "Low" );

        // END SNIPPET: affinityConstraints

    }

    /**
     * @param manifest
     */
    private void initializeComponents( Manifest manifest )
    {
        //
        // Initializing jboss component
        //
        // START SNIPPET: vmComponentInitialization
        VirtualMachineComponent jbossComponent =
                manifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( "jboss" );

        // set affinity between component's vm instances
        jbossComponent.setAffinityConstraints( "Low" );

        // set allocation constraints: the max, min and initial number of vm instances
        jbossComponent.getAllocationConstraints().setUpperBound( AC_UPPER_BOUND );
        jbossComponent.getAllocationConstraints().setLowerBound( AC_LOWER_BOUND );
        jbossComponent.getAllocationConstraints().setInitial( AC_INITIAL );

        // set the path to the contextualization image
        jbossComponent.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-jboss.iso" );

        // set the path to the vm image disk
        jbossComponent.getOVFDefinition().getReferences().getImageFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-jboss.vmdk" );

        // set the format of the vm image disk to VMDK
        jbossComponent.getOVFDefinition().getDiskSection().getImageDisk()
                .setFormat( DiskFormatType.VMDK.getSpecificationUrl() );

        // set the capacity of the vm image disk
        jbossComponent.getOVFDefinition().getDiskSection().getImageDisk().setCapacity( "6252516" );

        // add information about the product
        ProductSection product =
                jbossComponent.getOVFDefinition().getVirtualSystem().getProductSection();
        product.setProduct( "JBOSS" );
        product.setVersion( "5.1" );

        // set the operating system of the component
        OperatingSystem os =
                jbossComponent.getOVFDefinition().getVirtualSystem().getOperatingSystem();
        os.setId( OperatingSystemType.LINUX.number() );
        os.setVersion( "Debian6" );
        os.setDescription( "This is the debian description." );

        // set memory size, number of cpus and the virtual hardware family
        VirtualHardwareSection hardwareSection =
                jbossComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();
        hardwareSection.setMemorySize( 1024 );
        hardwareSection.setNumberOfVirtualCPUs( 5 );
        hardwareSection.setVirtualHardwareFamily( "xen" );

        // END SNIPPET: vmComponentInitialization

        // START SNIPPET: AddComponents

        //
        // Add mysql component
        //
        VirtualMachineComponent mysqlComponent =
                manifest.getVirtualMachineDescriptionSection()
                        .addNewVirtualMachineComponent( "mysql" );
        mysqlComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                .setMemorySize( 512 );
        mysqlComponent.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-db.img" );
        mysqlComponent.getOVFDefinition().getDiskSection().getImageDisk().setCapacity( "7380016" );

        //
        // Add VPN component
        //
        VirtualMachineComponent vpnComponent =
                manifest.getVirtualMachineDescriptionSection()
                        .addNewVirtualMachineComponent( "VPN" );
        vpnComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                .setMemorySize( DEFAULT_MEMORY_SIZE );
        vpnComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                .setNumberOfVirtualCPUs( 2 );
        vpnComponent.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-vpn.img" );
        vpnComponent.getOVFDefinition().getDiskSection().getImageDisk().setCapacity( "6252516" );

        //
        // Add Load Balancer component
        //
        VirtualMachineComponent lbComponent =
                manifest.getVirtualMachineDescriptionSection()
                        .addNewVirtualMachineComponent( "LB" );
        lbComponent.getOVFDefinition().getVirtualSystem().getVirtualHardwareSection()
                .setMemorySize( DEFAULT_MEMORY_SIZE );
        lbComponent.getOVFDefinition().getDiskSection().getImageDisk().setCapacity( "5160576" );
        lbComponent.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-lb.img" );

        lbComponent.getOVFDefinition().getDiskSection().getContextualizationDisk()
                .setCapacity( "5160576" );
        lbComponent.getOVFDefinition().getReferences().getContextualizationFile()
                .setHref( "/opt/optimis/repository/image/DemoApp-lb.img" );
        // END SNIPPET AddComponents
    }

    public void testDemonstrateHowToGetHardwareInfoFromManifest()
    {

        eu.optimis.manifest.api.ip.Manifest ipManifest =
                eu.optimis.manifest.api.ip.Manifest.Factory.newInstance( getManifest().toJaxB() );

        System.out.println( "Get info for jboss component: " );
        eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection jbossHardwareSection =
                ipManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( "jboss" )
                        .getOVFDefinition().getVirtualSystem().getVirtualHardwareSection();

        int memorySize = jbossHardwareSection.getMemorySize();
        System.out.println( "Memory size: " + memorySize );

        int numberOfCPUs = jbossHardwareSection.getNumberOfVirtualCPUs();
        System.out.println( "Number of cpus: " + numberOfCPUs );

        int numberOfInstances =
                ipManifest.getVirtualMachineDescriptionSection()
                        .getVirtualMachineComponentById( "jboss" )
                        .getAllocationConstraints().getUpperBound();
        System.out.println( "Number of maximum instances: " + numberOfInstances );
    }

    public void testCreatesDummyY2Manifest() throws Exception
    {

        Properties properties = new Properties();
        InputStream in = this.getClass().getResourceAsStream( "/dummy.manifest.properties" );
        properties.load( in );
        Manifest dummyManifest =
                Manifest.Factory.newInstance( "DummyApp", "dummyComponent", properties );
        writeToFile( dummyManifest.toXmlBeanObject(), "DummySPManifest" );
    }
}
