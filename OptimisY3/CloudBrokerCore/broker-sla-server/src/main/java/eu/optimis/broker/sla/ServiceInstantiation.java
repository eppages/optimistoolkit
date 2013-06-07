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
package eu.optimis.broker.sla;


import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.client.AgreementClient;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;

import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;

import eu.optimis.broker.core.BrokerAgreement;
import eu.optimis.broker.core.BrokerCloudQoSClient;
import eu.optimis.broker.core.OutputStub4Demo;
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;
import eu.optimis.ipdiscovery.datamodel.Provider;
import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.service_manager.client.ServiceManagerClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 */
public class ServiceInstantiation extends AbstractCreateAgreementAction
{

    private static final Logger LOG = Logger.getLogger( ServiceInstantiation.class );

    protected static final String KEY_SERVICE_ID = "eu.optimis.manifest.serviceId";

    /**
     * Creates a new agreement instance for a VM provisioning service.
     * 
     * @see org.ogf.graap.wsag.server.actions.ICreateAgreementAction#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer ) throws AgreementFactoryException
    {

        try
        {
        	BrokerVisualMonitor.APICall(Actions.SD_BFE_31, EndPointType.START, StatusCode.OK, "status-message");
			BrokerVisualMonitor.APICall(Actions.SD_BFE_31, EndPointType.END, StatusCode.OK, "status-message");

        	
        	XmlBeanServiceManifestDocument serviceManifestDoc = null;

            try
            {
                serviceManifestDoc = Tools.offerToServiceManifest( offer.getXMLObject() );
                
                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "offer service manifest: " + serviceManifestDoc.xmlText() );
                }
            }
            catch ( Exception e )
            {
                throw new AgreementFactoryException(
                    "Error in retrieving service manifest from agreement offer.", e );
            }
            
            //
            // retrieve placement solutions for this agreement those have already been identified during negotiation
            //
          
            String contextID = offer.getName();
            
            LOG.info( "retrieving placement solutions from broker negotiation context with ID: " +  contextID );
            
            HashMap<String, Provider> deploymentSolutions = (HashMap<String, Provider>) BrokerContext.negotiationContext.get(contextID);
            
            //--------------------- Agreements with the IPs -----------------------//
            if( deploymentSolutions != null)
            {
            	LOG.info( "deployment solutions for this agreement offer: " + deploymentSolutions.size() );
            	BrokerAgreement brokerAgree = new BrokerAgreement(deploymentSolutions);
            	brokerAgree.createIPAgreements();
            	System.out.println("Agreements with IPs created");
    			
            	BrokerVisualMonitor.APICall(Actions.BFE_SD_36, EndPointType.START, StatusCode.OK, "status-message");
    			BrokerVisualMonitor.APICall(Actions.BFE_SD_36, EndPointType.END, StatusCode.OK, "status-message");

            	
            } else   
            {
            	LOG.info(" no deployment solutions found for agreement offer with context ID: " + contextID);
            	throw new AgreementFactoryException( "no deployment solutions found for agreement offer with context ID: " + contextID );
            }
            
            //
            // here we create a new agreement
            //
            ServiceAgreementType agreementImpl = new ServiceAgreementType( offer, "serviceId" );
            agreementImpl.setAgreementId( "serviceId" );
            
            
            //
            // instantiate agreement monitor and set monitoring interval to once a minute
            //
            MonitorableAgreement monitored = new MonitorableAgreement( agreementImpl );
            monitored.addMonitoringHandler( new ServiceMonitoringHandler() );

            String cronExpr =
                ComponentConfigurationProvider.getString( "VMServiceInstantiation.MonitoringInterval" ); //$NON-NLS-1$

            monitored.setCronExpression( cronExpr );

            //
            // put the service id into the execution context for monitoring
            //
            XmlString xserviceId = XmlString.Factory.newInstance();
            xserviceId.setStringValue( "serviceId" );
            monitored.getExecutionContext().getExecutionProperties().put( KEY_SERVICE_ID, xserviceId );

            monitored.startMonitoring();

            return monitored;
        }
        catch ( Exception e )
        {
            LOG.error( e );
            throw new AgreementFactoryException( e );
        }
    }
    
}
