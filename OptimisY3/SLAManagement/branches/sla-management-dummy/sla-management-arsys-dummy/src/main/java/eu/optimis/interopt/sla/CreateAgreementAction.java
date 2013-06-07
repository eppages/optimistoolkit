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
package eu.optimis.interopt.sla;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.VMManagementClientFactory;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.manifest.api.ip.AllocationConstraint;
import eu.optimis.manifest.api.ip.IncarnatedVirtualMachineComponent;
import eu.optimis.manifest.api.ip.InfrastructureProviderExtension;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.ip.VirtualMachineComponent;
import eu.optimis.manifest.api.ip.VirtualMachineDescriptionSection;
import eu.optimis.manifest.api.ovf.ip.File;
import eu.optimis.manifest.api.ovf.ip.OVFDefinition;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.sla.Tools;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author owaeld
 * 
 */
public class CreateAgreementAction extends AbstractCreateAgreementAction
{

    private static final String ARSYS_SYSTEM_URL = "http://optimis.arsys.es/vmmanagement";

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.server.actions.ICreateAgreementAction#createAgreement(org.ogf.graap.wsag.api.
     * AgreementOffer)
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer ) throws AgreementFactoryException
    {
        try
        {
            XmlBeanServiceManifestDocument manifest = selectManifest( offer );

            Manifest ipManifest = Manifest.Factory.newInstance( manifest );
            String serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();

            List<ServiceComponent> serviceComponents = selectServiceComponents( ipManifest );

            URL systemUrl = new URL( ARSYS_SYSTEM_URL );
            VMManagementSystemClient vmManagementSystemClient =
                VMManagementClientFactory.createClient( systemUrl );

            vmManagementSystemClient.deployService( serviceId, serviceComponents, manifest );

            AbstractAgreementType agreementInstance = createAgreement( serviceId );

            MonitorableAgreement agreement = new MonitorableAgreement( agreementInstance );
            return agreement;
        }
        catch ( Exception e )
        {
            throw new AgreementFactoryException( "failed to create agreement", e );
        }
    }

    /**
     * @param ipManifest
     * @return
     */
    private List<ServiceComponent> selectServiceComponents( Manifest ipManifest )
    {
        //
        // incarnation of the Manifest
        //
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        InfrastructureProviderExtension ipExt = ipManifest.getInfrastructureProviderExtensions();

        //
        // iterate over the abstract service components and extract global component properties
        //
        VirtualMachineDescriptionSection vmDescription = ipManifest.getVirtualMachineDescriptionSection();
        VirtualMachineComponent[] componentArray = vmDescription.getVirtualMachineComponentArray();
        Vector<ServiceComponent> componentList = new Vector<ServiceComponent>();

        for ( int i = 0; i < componentArray.length; i++ )
        {
            // occi.compute.architecture
            // occi.compute.speed
            // occi.compute.memory
            // occi.compute.cores
            // eu.optimis.vm.image
            // eu.optimis.vm.contextualization.${instance-id}

            ServiceComponent serviceComponent = new ServiceComponent();

            VirtualMachineComponent current = componentArray[i];
            AllocationConstraint allocation = current.getAllocationConstraints();
            OVFDefinition ovf = current.getOVFDefinition();

            VirtualHardwareSection hardware = ovf.getVirtualSystem().getVirtualHardwareSection();

            serviceComponent.put( ServiceComponent.OCCI_COMPUTE_ARCHITECTURE,
                hardware.getVirtualHardwareFamily() );
            serviceComponent.put( ServiceComponent.OCCI_COMPUTE_CORES,
                Integer.toString( hardware.getNumberOfVirtualCPUs() ) );
            serviceComponent.put( ServiceComponent.OCCI_COMPUTE_MEMORY,
                Integer.toString( hardware.getMemorySize() ) );
            serviceComponent.put( ServiceComponent.OCCI_COMPUTE_SPEED,
                Integer.toString( hardware.getCPUSpeed() ) );
            serviceComponent.put( ServiceComponent.OPTIMIS_VM_INSTANCES,
                Integer.toString( allocation.getUpperBound() ) );

            //
            // get image information from the incarnated components, in the file array the first entry is the
            // VM image, the others are contextualization images
            //
            String componentId = current.getComponentId();
            IncarnatedVirtualMachineComponent incarnated =
                ipExt.getIncarnatedVirtualMachineComponentByComponentId( componentId );

            File[] fileArray = incarnated.getOVFDefinition().getReferences().getFileArray();
            serviceComponent.put( ServiceComponent.OPTIMIS_VM_IMAGE, fileArray[0].getHref() );

            for ( int j = 1; j < fileArray.length; j++ )
            {
                String contextualizationImage = fileArray[j].getHref();
                serviceComponent.put( "eu.optimis.vm.contextualization." + j, contextualizationImage );
            }

            componentList.add( serviceComponent );
        }

        return componentList;
    }

    /**
     * @param offer
     * @return
     * @throws AgreementFactoryException
     */
    private XmlBeanServiceManifestDocument selectManifest( AgreementOffer offer )
        throws AgreementFactoryException
    {
        try
        {
            return (XmlBeanServiceManifestDocument) offer.getXMLObject().selectPath(
                Tools.SERVIC_MANIFEST_XPATH )[0];
        }
        catch ( Exception e )
        {
            throw new AgreementFactoryException( "failed to extract manifest from offer" );
        }
    }

    /**
     * @return
     */
    private AbstractAgreementType createAgreement( final String serviceId )
    {
        AbstractAgreementType agreementInstance = new AbstractAgreementType()
        {
            @Override
            public void terminate( TerminateInputType reason )
            {
                try
                {
                    URL systemUrl = new URL( ARSYS_SYSTEM_URL );
                    VMManagementSystemClient vmManagementSystemClient =
                        VMManagementClientFactory.createClient( systemUrl );
                    vmManagementSystemClient.terminate( serviceId );
                }
                catch ( Exception e )
                {
                    // nothing to do here
                }

            }
        };
        return agreementInstance;
    }

}
