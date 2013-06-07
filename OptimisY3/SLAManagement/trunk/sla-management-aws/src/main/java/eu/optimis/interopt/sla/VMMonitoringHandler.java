/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
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

import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.monitoring.IMonitoringContext;
import org.ogf.graap.wsag.server.monitoring.IServiceTermMonitoringHandler;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;

/**
 * @author hrasheed
 * 
 */
public class VMMonitoringHandler
    implements IServiceTermMonitoringHandler
{

    private static final Logger LOG = Logger.getLogger(VMMonitoringHandler.class);

    /**
     * Monitors the provisioning of a VM. As soon a service is deployed this handler adds the deployment
     * information such as host name to the corresponding SDT state.
     * 
     * @param monitoringContext
     *            the monitoring context of the VM agreement
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IServiceTermMonitoringHandler#monitor(org.ogf.graap.wsag.server.monitoring.IMonitoringContext)
     */
    @Override
    public void monitor(IMonitoringContext monitoringContext) throws Exception
    {
        try {          
            ServiceTermStateType state = monitoringContext.getServiceTermStateByName("OPTIMIS_SERVICE_SDT");

            // First we have to check if service deployment is successfully completed by looping for the status of a service
            // once service status is available, service term state will set to READY
            state.setState(ServiceTermStateDefinition.NOT_READY);

            // Price SDT status is READY 
            ServiceTermStateType priceState = monitoringContext.getServiceTermStateByName("OPTIMIS_SERVICE_PRICE_SDT");
            priceState.setState(ServiceTermStateDefinition.READY);
            
            // retrieving Service_ID from execution properties
            XmlString xmlServiceId =
                (XmlString) monitoringContext.getProperties().get(CreateAgreementAction.KEY_SERVICE_ID);
            String serviceId = xmlServiceId.getStringValue();

            // retrieving last monitoring time stamp from execution properties
            Calendar startTime =
                ((XmlDate) monitoringContext.getProperties().get(
                    CreateAgreementAction.KEY_MONITORING_TIMESTAMP)).getCalendarValue();
            
            // get the service monitoring interval value from the execution context
            String monitoringInterval =
                ((XmlString) monitoringContext.getProperties().get(
                    CreateAgreementAction.KEY_MONITORING_INTERVAL)).getStringValue();
            
            try 
            {
                LOG.info("getting service status [" + serviceId + "] from arsys");
                
                VMManagementSystemClient vmManagementSystemClient = null;
                vmManagementSystemClient =
                    (VMManagementSystemClient) monitoringContext.getTransientProperties().get(CreateAgreementAction.KEY_VM_MANAGEMENT_CLIENT);
                
                // Check if service has been deployed and fetch service data
                List<VMProperties> vmPropList = vmManagementSystemClient.queryServiceProperties(serviceId);
                
                if( vmPropList != null)
                {
                    if( vmPropList.size() == 0 )
                    {
                        LOG.info("service status [" + serviceId + "] - no vm properties are available");
                    } 
                    else
                    {
                        VMProperties vmProperties = vmPropList.get(0);
                        LOG.info("service status: " + vmProperties.getStatus());
                        if(vmProperties.getStatus().equals("running") || vmProperties.getStatus().equals("pending"))
                        {
                            // if status is 'running' or 'pending', then SDT status is changed to READY
                            state.setState( ServiceTermStateDefinition.READY );
                        }
                        else 
                        {
                            // if status is different, then SDT status is changed to NOT_READY
                            state.setState(ServiceTermStateDefinition.NOT_READY);
                        }
                    }   
                } else
                {
                    LOG.error("error calling arsys to get service status");
                }
                
            } catch (Exception e) 
            {
                throw new AgreementFactoryException("error calling arsys: Failed to get the status of service: [" + serviceId + "] - Reason: ", e);
            }

        } catch (Exception e)
        {
            LOG.error( e );
        }
    }
}