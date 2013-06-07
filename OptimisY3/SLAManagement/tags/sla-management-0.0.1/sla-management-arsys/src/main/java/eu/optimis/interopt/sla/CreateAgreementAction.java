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

import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.VMManagementClientFactory;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.manifest.api.ip.*;
import eu.optimis.manifest.api.ovf.ip.File;
import eu.optimis.manifest.api.ovf.ip.OVFDefinition;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author hrasheed
 */
public class CreateAgreementAction extends AbstractCreateAgreementAction
{
    
    private static final Logger LOGGER = Logger.getLogger( CreateAgreementAction.class );

    private static final String ARSYS_SYSTEM_URL = "http://optimis.arsys.es/vmmanagement";
    
    public static final String USERNAME = "servicemanager";
    public static final String PASSWORD = "opt1M1$12";
    
    protected static final String KEY_SERVICE_ID = "eu.optimis.manifest.serviceId";

    protected static final String KEY_VM_MANAGEMENT_CLIENT = "eu.optimis.sla.service.client.vmmanagement";
    
    protected static final String KEY_MONITORING_TIMESTAMP = "eu.optimis.sla.service.monitoring.timestamp";

    protected static final String KEY_MONITORING_INTERVAL = "eu.optimis.sla.service.monitoring.interval";
    
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
            Authenticator.setDefault( new Authenticator()
            {
                @Override
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication( USERNAME,
                            PASSWORD.toCharArray() );
                }
            } );
            
            XmlBeanServiceManifestDocument serviceManifestDoc = null;
            
            Manifest ipManifest = null;
            
            String serviceId = null;
            
            try
            {
                serviceManifestDoc = Tools.offerToServiceManifest( offer.getXMLObject() );
                
                ipManifest = Manifest.Factory.newInstance( serviceManifestDoc );
                
                serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();
            }
            catch ( Exception e )
            {
                throw new AgreementFactoryException(
                    "Error in retrieving service manifest from agreement offer.", e );
            }

            //
            //validate the manifest properties
            //
            try 
            {
                Properties serviceConfigurationProperties = Tools.loadServiceConfigurationProperties();
                ManifestValidator validator = new ManifestValidator(serviceConfigurationProperties);
                validator.validate(ipManifest);
            } catch (Exception e) 
            {
                LOGGER.error( "service manifest validation failed for service : [" + serviceId + "] : Reason: " + e.getMessage() );
                throw new AgreementFactoryException( "service manifest validation failed for service : [" + serviceId + "] : Reason: ", e );
            }
            
            List<ServiceComponent> serviceComponents = selectServiceComponents( ipManifest );

            VMManagementSystemClient vmManagementSystemClient = null;
            try 
            {
                LOGGER.info( "creating arsys client [" + ARSYS_SYSTEM_URL + "]" );
                URL systemUrl = new URL( ARSYS_SYSTEM_URL );
                vmManagementSystemClient = VMManagementClientFactory.createClient( systemUrl );
                vmManagementSystemClient.setAuth( USERNAME, PASSWORD );
                LOGGER.info("deploying service [" + serviceId + "] to arsys");
                vmManagementSystemClient.deployService( serviceId, serviceComponents, serviceManifestDoc );
            } catch (Exception e) 
            {
                LOGGER.error( "Failed to deploy a service: [" + serviceId + "] in arsys - Reason: " + e.getMessage() );
                throw new AgreementFactoryException( "Failed to deploy a service: [" + serviceId + "] in arsys - Reason: ", e );
            }
            
            //
            // if the service was deployed successfully, we create a new agreement
            //
            VMAgreementType agreementInstance = new VMAgreementType(offer, serviceId);
            agreementInstance.setAgreementId( serviceId );

            //
            // instantiate agreement monitor and set monitoring interval to once a minute
            //
            MonitorableAgreement agreement = new MonitorableAgreement( agreementInstance );
            agreement.addMonitoringHandler( new VMMonitoringHandler() );

            String cronExpr =
                ComponentConfigurationProvider.getString( "ServiceInstantiation.MonitoringInterval" ); //$NON-NLS-1$

            agreement.setCronExpression( cronExpr );

            //
            // put the service id into the execution context for monitoring
            //
            XmlString xserviceId = XmlString.Factory.newInstance();
            xserviceId.setStringValue( serviceId );
            agreement.getExecutionContext().getExecutionProperties().put( KEY_SERVICE_ID, xserviceId );
            
            //
            // put VMManagementClient into transient execution properties
            //
            agreement.getExecutionContext().getTransientExecutionProperties().put( KEY_VM_MANAGEMENT_CLIENT, vmManagementSystemClient );
            
            //
            // put monitoring interval value into the execution context
            //
            String monitoringInterval =
                ComponentConfigurationProvider.getString( "service.monitoring.interval" ); //$NON-NLS-1$
            XmlString xinterval = XmlString.Factory.newInstance();
            xinterval.setStringValue( monitoringInterval );
            agreement.getExecutionContext().getExecutionProperties().put( KEY_MONITORING_INTERVAL, xinterval );

            //
            // put last monitoring time stamp into the execution context for retrieving service monitoring data
            //
            XmlDate xmlTimestamp = XmlDate.Factory.newInstance();
            xmlTimestamp.setCalendarValue( Calendar.getInstance() );
            agreement.getExecutionContext().getExecutionProperties()
                     .put( KEY_MONITORING_TIMESTAMP, xmlTimestamp );

            agreement.startMonitoring();
            
            LOGGER.info( "an agreement has been created for service [" + serviceId + "]" );

            return agreement;
        }
        catch ( Exception e )
        {
            // TODO if service is already deployed then un-deploy it
            LOGGER.error( e.getMessage() );
            throw new AgreementFactoryException( "failed to create an agreement: ", e );
        }
    }

    /**
     * @param ipManifest
     * @return
     */
    private List<ServiceComponent> selectServiceComponents( Manifest ipManifest ) throws Exception
    {
        //
        // incarnation of the Manifest
        //
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        InfrastructureProviderExtension ipExt = ipManifest.getInfrastructureProviderExtensions();

        //
        // iterate over the abstract service components and extract global component properties
        //
        VirtualMachineDescriptionSection vmDescription =
                ipManifest.getVirtualMachineDescriptionSection();
        VirtualMachineComponent[] componentArray = vmDescription.getVirtualMachineComponentArray();
        Vector<ServiceComponent> componentList = new Vector<ServiceComponent>();

        for ( int i = 0; i < componentArray.length; i++ )
        {
            ServiceComponent serviceComponent = new ServiceComponent();

            VirtualMachineComponent current = componentArray[ i ];
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
            serviceComponent.put( ServiceComponent.OPTIMIS_VM_IMAGE, fileArray[ 0 ].getHref() );

            for ( int j = 1; j < fileArray.length; j++ )
            {
                String contextualizationImage = fileArray[ j ].getHref();
                serviceComponent
                        .put( "eu.optimis.vm.contextualization." + j, contextualizationImage );
            }

            componentList.add( serviceComponent );
        }

        return componentList;
    }
}
