/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.interopt.sla;

import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.VMManagementClientFactory;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.manifest.api.ip.*;
import eu.optimis.manifest.api.ovf.ip.File;
import eu.optimis.manifest.api.ovf.ip.OVFDefinition;
import eu.optimis.manifest.api.ovf.ip.VirtualHardwareSection;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;

public class CreateAgreementAction extends AbstractCreateAgreementAction
{
    
    private static final Logger LOGGER = Logger.getLogger(CreateAgreementAction.class);
    
    private static String AWS_PUBLIC_KEY = "AKIAJMVELIFD3ZSVY2HQ";
    private static String AWS_SECRET_KEY = "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";
    
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
    public Agreement createAgreement(AgreementOffer offer) throws AgreementFactoryException
    {
        try
        {
            
            XmlBeanServiceManifestDocument serviceManifestDoc = null;            
            Manifest ipManifest = null;          
            String serviceId = null;
            
            try
            {
                serviceManifestDoc = Tools.offerToServiceManifest(offer.getXMLObject());                
                ipManifest = Manifest.Factory.newInstance(serviceManifestDoc);                
                serviceId = ipManifest.getVirtualMachineDescriptionSection().getServiceId();
            }
            catch (Exception e)
            {
                throw new AgreementFactoryException(
                    "Error in retrieving service manifest from agreement offer.", e );
            }

            //validate the manifest properties
            try 
            {
                Properties serviceConfigurationProperties = Tools.loadServiceConfigurationProperties();
                ManifestValidator validator = new ManifestValidator(serviceConfigurationProperties);
                validator.validate(ipManifest);
            } catch (Exception e) 
            {
                LOGGER.error("service manifest validation failed for service : [" + serviceId + "] : Reason: " + e.getMessage());
                throw new AgreementFactoryException("service manifest validation failed for service : [" + serviceId + "] : Reason: ", e);
            }
            
            List<ServiceComponent> serviceComponents = selectServiceComponents(ipManifest);

            VMManagementSystemClient vmManagementSystemClient = null;
            try 
            {
                LOGGER.info("creating AWS client");
                vmManagementSystemClient = VMManagementClientFactory.createClient();
                vmManagementSystemClient.setAuth(AWS_PUBLIC_KEY, AWS_SECRET_KEY);
                LOGGER.info("deploying service [" + serviceId + "] to AWS");
                vmManagementSystemClient.deployService(serviceId, serviceComponents, serviceManifestDoc);
            } catch (Exception e) 
            {
                LOGGER.error("Failed to deploy a service: [" + serviceId + "] in aws - Reason: " + e.getMessage());
                throw new AgreementFactoryException("Failed to deploy a service: [" + serviceId + "] in aws - Reason: ", e);
            }
            
            // if the service was deployed successfully, we create a new agreement
            VMAgreementType agreementInstance = new VMAgreementType(offer, serviceId);
            agreementInstance.setAgreementId(serviceId);

            // instantiate agreement monitor and set monitoring interval to once a minute
            MonitorableAgreement agreement = new MonitorableAgreement(agreementInstance);
            agreement.addMonitoringHandler(new VMMonitoringHandler());

            String cronExpr =
                ComponentConfigurationProvider.getString("ServiceInstantiation.MonitoringInterval"); //$NON-NLS-1$

            agreement.setCronExpression(cronExpr);

            // put the service id into the execution context for monitoring
            XmlString xserviceId = XmlString.Factory.newInstance();
            xserviceId.setStringValue(serviceId);
            agreement.getExecutionContext().getExecutionProperties().put(KEY_SERVICE_ID, xserviceId);
            
            // put VMManagementClient into transient execution properties
            agreement.getExecutionContext().getTransientExecutionProperties().put(KEY_VM_MANAGEMENT_CLIENT, vmManagementSystemClient);
            
            // put monitoring interval value into the execution context
            String monitoringInterval =
                ComponentConfigurationProvider.getString("service.monitoring.interval"); //$NON-NLS-1$
            XmlString xinterval = XmlString.Factory.newInstance();
            xinterval.setStringValue(monitoringInterval);
            agreement.getExecutionContext().getExecutionProperties().put(KEY_MONITORING_INTERVAL, xinterval);

            // put last monitoring time stamp into the execution context for retrieving service monitoring data
            XmlDate xmlTimestamp = XmlDate.Factory.newInstance();
            xmlTimestamp.setCalendarValue(Calendar.getInstance());
            agreement.getExecutionContext().getExecutionProperties()
                     .put(KEY_MONITORING_TIMESTAMP, xmlTimestamp);

            agreement.startMonitoring();
            
            LOGGER.info("an agreement has been created for service [" + serviceId + "]");

            return agreement;
        }
        catch ( Exception e )
        {
            LOGGER.error(e.getMessage());
            throw new AgreementFactoryException("failed to create an agreement: ", e);
        }
    }

    /**
     * @param ipManifest
     * @return
     */
    private List<ServiceComponent> selectServiceComponents(Manifest ipManifest) throws Exception
    {
        // incarnation of the Manifest
        ipManifest.initializeIncarnatedVirtualMachineComponents();
        InfrastructureProviderExtension ipExt = ipManifest.getInfrastructureProviderExtensions();

        // iterate over the abstract service components and extract global component properties
        VirtualMachineDescriptionSection vmDescription =
                ipManifest.getVirtualMachineDescriptionSection();
        VirtualMachineComponent[] componentArray = vmDescription.getVirtualMachineComponentArray();
        List<ServiceComponent> componentList = new ArrayList<ServiceComponent>();

        for (int i = 0; i < componentArray.length; i++)
        {
            ServiceComponent serviceComponent = new ServiceComponent();

            VirtualMachineComponent current = componentArray[i];
            AllocationConstraint allocation = current.getAllocationConstraints();
            OVFDefinition ovf = current.getOVFDefinition();

            VirtualHardwareSection hardware = ovf.getVirtualSystem().getVirtualHardwareSection();

            serviceComponent.setArchitecture(hardware.getVirtualHardwareFamily());
            serviceComponent.setCores(hardware.getNumberOfVirtualCPUs());
            serviceComponent.setMemory(hardware.getMemorySize());
            serviceComponent.setSpeed(hardware.getCPUSpeed());
            serviceComponent.setInstances(allocation.getUpperBound());

            // get image information from the incarnated components, in the file array the first entry is the
            // VM image, the others are contextualization images
            String componentId = current.getComponentId();
            IncarnatedVirtualMachineComponent incarnated =
                    ipExt.getIncarnatedVirtualMachineComponentByComponentId(componentId);

            File[] fileArray = incarnated.getOVFDefinition().getReferences().getFileArray();
            serviceComponent.setImage(fileArray[0].getHref());
            componentList.add(serviceComponent);
        }

        return componentList;
    }
}
