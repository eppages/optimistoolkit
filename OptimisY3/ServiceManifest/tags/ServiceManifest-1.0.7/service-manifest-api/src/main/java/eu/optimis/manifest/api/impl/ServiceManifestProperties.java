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
package eu.optimis.manifest.api.impl;

import java.io.IOException;
import java.util.Properties;

/**
 * CONSTANT DEFINITIONS
 *
 * @author owaeld
 */
public class ServiceManifestProperties extends Properties
{

    public static final String VM_IMAGE_FILE_HREF = "vmImageFile";

    public static final String VM_IMAGE_FILE_FORMAT = "vmImageFormat";

    public static final String VM_IMAGE_FILE_CAPACITY = "vmImageCapacity";

    public static final String VM_CONTEXTUALIZATION_FILE_HREF = "contextualizationImageFile";

    public static final String VM_CONTEXTUALIZATION_FILE_FORMAT = "contextualizationImageFormat";

    public static final String VM_CONTEXTUALIZATION_FILE_CAPACITY =
            "contextualizationImageCapacity";

    public static final String VM_NUMBER_OF_VIRTUAL_CPU = "numberOfVirtualCPUs";

    public static final String VM_OPERATING_SYSTEM_ID = "operatingSystemId";

    public static final String VM_OPERATING_SYSTEM_DESCRIPTION = "operatingSystemDescription";

    public static final String VM_VIRTUAL_HARDWARE_FAMILY = "virtualHardwareFamily";

    public static final String VM_MEMORY_SIZE = "memorySize";

    public static final String VM_CPU_SPEED = "cpuSpeed";

    public static final String VM_INSTANCES_MAX = "maxNumberOfInstances";

    public static final String VM_INSTANCES_MIN = "minNumberOfInstances";

    public static final String VM_INSTANCES_INITIAL = "initialNumberOfInstances";

    public static final String VM_INSTANCES_AFFINITY = "affinityConstraints";
    
    public static final String VM_INSTANCES_ANTI_AFFINITY = "antiAffinityConstraints";

    public static final String TRUST_LEVEL = "trustLevel";

    public static final String RISK_LEVEL = "riskLevel";

    public static final String AVAILABILITY_PER_MONTH = "availabilityPerMonth";

    public static final String AVAILABILITY_PER_DAY = "availabilityPerDay";

    public static final String ECO_LEED_CERTIFICATION = "ecoLEEDCertification";

    public static final String ECO_BREEAM_CERTIFICATION = "ecoBREEAMCertification";

    public static final String ECO_EUCOC_COMPLIANT = "ecoEuCoCCompliant";

    public static final String ECO_ENERGY_STAR_RATING = "ecoEnergyStarRating";
    
    public static final String ECO_ISO14000 = "ecoISO14000";
    
    public static final String ECO_GREEN_STAR = "ecoGreenStar";
    
    public static final String ECO_CASBEE = "ecoCASBEE";
    
    public static final String COST_CURRENCY = "costCurrency";

    public static final String COST_MAX = "costPlanCap";

    public static final String COST_MIN = "costPlanFloor";

    public static final String DATA_PROTECTION_LEVEL = "dataProtectionLevel";

    public static final String DATA_PROTECTION_ENCRYPTION_ALGORITHM = "encryptionAlgorithm";
    
    public static final String DATA_STORAGE_NAME = "dataStorageName";

    public static final String SP_EXTENSION_SECURITY_VPN_ENABLED = "securityVPN";

    public static final String SP_EXTENSION_SECURITY_SSH_ENABLED = "securitySSH";

    private static final String DEFAULT_PROPERTIES_FILE = "/manifest.properties";

    /**
     * this will create a properties object that contains all default values for a service manifest loaded
     * from the manifest.properties file.
     */
    public ServiceManifestProperties()
    {
        try
        {
            this.load( this.getClass().getResourceAsStream( DEFAULT_PROPERTIES_FILE ) );
        }
        catch ( IOException e )
        {
            //this should never happen
            throw new RuntimeException( "Loading default properties failed.", e );
        }
    }
}
