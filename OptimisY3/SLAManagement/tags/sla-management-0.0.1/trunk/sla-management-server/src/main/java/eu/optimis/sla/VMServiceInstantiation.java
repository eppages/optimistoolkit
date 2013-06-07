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
package eu.optimis.sla;

import java.text.MessageFormat;
import java.util.Calendar;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;

import com.sun.jersey.core.util.MultivaluedMapImpl;

import eu.optimis.manifest.api.ip.InfrastructureProviderExtension;
import eu.optimis.manifest.api.ip.Manifest;
import eu.optimis.manifest.api.utils.XmlValidator;
import eu.optimis.sla.accounting.SimpleAccountingSystem;
import eu.optimis.sla.rest.AdmissionControlClient;
import eu.optimis.sla.rest.CloudOptimizerClient;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;

/**
 * @author hrasheed
 */
public class VMServiceInstantiation extends AbstractCreateAgreementAction
{

    private static final String SKIP_AC_KEY = "VMServiceInstantiation.skipACtest"; //$NON-NLS-1$

    private static final String AC_KEY = "VMServiceInstantiation.url.ac"; //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger( VMServiceInstantiation.class );

    protected static final String KEY_SERVICE_ID = "eu.optimis.manifest.serviceId";

    protected static final String KEY_MONITORING_TIMESTAMP = "eu.optimis.sla.service.monitoring.timestamp";

    protected static final String KEY_MONITORING_INTERVAL = "eu.optimis.sla.service.monitoring.interval";

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

            String serviceId = allocateServices( offer );

            //
            // if the services were allocated successful we create a new agreement
            //
            VMAgreementType agreementImpl = new VMAgreementType( offer, serviceId );
            agreementImpl.setAgreementId( serviceId );
            //agreementImpl.setAgreementId( UUID.randomUUID().toString() );
            
            //
            // instantiate agreement monitor and set monitoring interval to once a minute
            //
            MonitorableAgreement monitored = new MonitorableAgreement( agreementImpl );
            monitored.addMonitoringHandler( new VMMonitoringHandler() );

            String cronExpr =
                ComponentConfigurationProvider.getString( "VMServiceInstantiation.MonitoringInterval" ); //$NON-NLS-1$

            monitored.setCronExpression( cronExpr );

            //
            // put the service id into the execution context for monitoring
            //
            XmlString xserviceId = XmlString.Factory.newInstance();
            xserviceId.setStringValue( serviceId );
            monitored.getExecutionContext().getExecutionProperties().put( KEY_SERVICE_ID, xserviceId );

            //
            // put the last monitoring time stamp into the execution context for retrieving service monitoring
            // data
            //
            XmlDate xmlTimestamp = XmlDate.Factory.newInstance();
            xmlTimestamp.setCalendarValue( Calendar.getInstance() );
            monitored.getExecutionContext().getExecutionProperties()
                     .put( KEY_MONITORING_TIMESTAMP, xmlTimestamp );

            //
            // put the monitoring interval value into the execution context
            //
            String monitoringInterval =
                ComponentConfigurationProvider.getString( "service.monitoring.interval" ); //$NON-NLS-1$
            XmlString xinterval = XmlString.Factory.newInstance();
            xinterval.setStringValue( monitoringInterval );
            monitored.getExecutionContext().getExecutionProperties().put( KEY_MONITORING_INTERVAL, xinterval );

            //
            // set the accounting system that store the SLA notification events and make available these
            // events to subscribers
            //
            IAccountingSystem accountingSystem = new SimpleAccountingSystem();
            monitored.setAccountingSystem( accountingSystem );

            monitored.startMonitoring();

            return monitored;
        }
        catch ( Exception e )
        {
            LOG.error( e );
            throw new AgreementFactoryException( e );
        }
    }

    /**
     * retrieves the service manifest from the offer, performs an AC Test and deploys the service to the Cloud
     * Optimizer. If everything went well, the serviceID is returned.
     * 
     * @param offer
     *            the agreement offer retrieved from SDO
     * @return the serviceId
     * @throws AgreementFactoryException
     */
    private String allocateServices( AgreementOffer offer ) throws AgreementFactoryException
    {

        //
        // simply grep the manifest document provided in the offer
        //
        Manifest offerManifest = retrieveManifestFromOffer( offer );
        
        try {
            if( offerManifest.isSetInfrastructureProviderExtensions() )
            {
                offerManifest.unsetInfrastructureProviderExtensions();
            }
            offerManifest.initializeIncarnatedVirtualMachineComponents();
        } catch (Exception e) {
            e.printStackTrace(); //
            String message = "error in parsing service manifest - incarnation of virtual machines failed";
            LOG.error(message);
            throw new AgreementFactoryException( message + "-" + e.getMessage());
        }
        
        if ( LOG.isTraceEnabled() )
        {
            LOG.trace( "agreement offer manifest: " + offerManifest.toXmlBeanObject().xmlText() );
        }

        //
        // in admission control we check if the service can be deployed, if yes, they will
        // send a manifest with included allocation pattern for service deployment.
        Manifest acManifest = performACTest( offerManifest );

        //
        // from admission control we receive a manifest with an update IP extension section
        // containing all information about allocation pattern and risk, cost. This is used to
        // deploy the service at the CO
        //
        return deployService( acManifest );
    }

    /**
     * performs the admission conrol test and returns the manifest retrieved from AC including allocation
     * pattern
     * 
     * It is possible to configure that ACTest is skipped, if this is set to true, the method will simply
     * return the offerManifest.
     * 
     * @param offerManifest
     * @return the manifest retrieved by AC
     * @throws AgreementFactoryException
     */
    private Manifest performACTest( Manifest offerManifest ) throws AgreementFactoryException
    {

        String allocationOffer = null;
        
        try
        {
            String acURL = ComponentConfigurationProvider.getString( AC_KEY );

            LOG.info( MessageFormat.format( "calling admission control at {0}", new Object[] { acURL } ) );

            AdmissionControlClient admissionControlClient = new AdmissionControlClient();

            XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();

            XmlBeanServiceManifestDocument manifestBean = offerManifest.toXmlBeanObject();

            MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
            formParams.add( "serviceManifest", manifestBean.xmlText( xmlOptions ) );

            MultivaluedMap<String, String> allocationOffers = admissionControlClient.performACTest( formParams );

            allocationOffer = allocationOffers.get( "serviceManifest" ).get( 0 );

            LOG.info( "calling admission control done: number of received allocation offers: ["
                + allocationOffers.size() + "] for service: " +  offerManifest.getVirtualMachineDescriptionSection().getServiceId());

            if ( LOG.isTraceEnabled() )
            {
                LOG.trace( "admission control plain response: " + allocationOffer );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace(); 
            LOG.error( e.getMessage() );
            throw new AgreementFactoryException( "Error calling admission control.", e );
        }
        
        try
        {
            //
            // response is a service manifest with IP extensions
            //
            XmlBeanServiceManifestDocument manifestDoc =
                (XmlBeanServiceManifestDocument) XmlObject.Factory.parse( allocationOffer );
            XmlValidator.validate( manifestDoc );

            Manifest acManifest = Manifest.Factory.newInstance( manifestDoc );

            if ( LOG.isTraceEnabled() )
            {
                XmlOptions xmlOptions = new XmlOptions().setSavePrettyPrint();
                xmlOptions.setSaveAggressiveNamespaces();
                LOG.trace( "admission control parsed allocation offer: " + manifestDoc.xmlText( xmlOptions ) );
            }

            InfrastructureProviderExtension ipExtensions =
                acManifest.getInfrastructureProviderExtensions();

            if ( ipExtensions.isSetAllocationOffer() )
            {
                if ( ipExtensions.getAllocationOffer().getDecision() != null )
                {
                    if ( ipExtensions.getAllocationOffer().getDecision().equals( "rejected" ) )
                    {
                        throw new AgreementFactoryException(
                            "service could not be admidded by admission control: rejected allocation offer" );
                    }
                    else if ( ipExtensions.getAllocationOffer().getDecision().equals( "partial" ) )
                    {
                        throw new AgreementFactoryException(
                            "service could not be admidded by admission control: partially accepted allocation offer" );
                    }
                }
                else
                {
                    String message =
                        "service could not be admidded by admission control: "
                            + "can't interpret decision in allocation offer";
                    LOG.error( message );
                    throw new AgreementFactoryException( message );
                }

            }
            else
            {
                LOG.error( "no allocation offer received from AC for service: " + offerManifest.getVirtualMachineDescriptionSection().getServiceId() );
                throw new AgreementFactoryException(
                    "service could not be admidded by admission control: no allocation offer received for service: " + offerManifest.getVirtualMachineDescriptionSection().getServiceId() );
            }
             
            return acManifest;
        }
        catch ( Exception e )
        {
            LOG.error( e );
            if ( e instanceof AgreementFactoryException )
            {
                throw (AgreementFactoryException) e;
            }
            throw new AgreementFactoryException( "Error processing admission control response.", e );
        }
        
    }

    /**
     * deploys the service described in the manifest using the CloudOptimizer Rest interface.
     * 
     * @param acManifest
     *            the manifest retrieved from admission control
     * @return the serviceId of the deployed service.
     * @throws AgreementFactoryException
     */
    private String deployService( Manifest acManifest ) throws AgreementFactoryException
    {

        try
        {
            String coURL = ComponentConfigurationProvider.getString( "VMServiceInstantiation.url.co" ); //$NON-NLS-1$

            LOG.info( MessageFormat.format( "calling cloud optimizer at {0}", new Object[] { coURL } ) );

            CloudOptimizerClient cloudOptimizerClient = new CloudOptimizerClient();

            String response = cloudOptimizerClient.deploy( acManifest.toString(), "slaId" );

            LOG.info( "calling cloud optimizer done...response: ServiceId [" + response + "]" );

            if ( ( response == null ) || ( "".equals( response ) ) )
            {
                throw new AgreementFactoryException( "Service could not be deployed by cloud optimizer." );
            }

            return response;
        }
        catch ( Exception e )
        {
            e.printStackTrace(); //
            LOG.error( e );
            if ( e instanceof AgreementFactoryException )
            {
                throw (AgreementFactoryException) e;
            }
            throw new AgreementFactoryException( "Error calling cloud optimizer.", e );
        }
    }

    private Manifest retrieveManifestFromOffer( AgreementOffer offer ) throws AgreementFactoryException
    {
        XmlBeanServiceManifestDocument serviceManifestDoc = null;

        try
        {
            serviceManifestDoc = Tools.offerToServiceManifest( offer.getXMLObject() );
        }
        catch ( Exception e )
        {
            throw new AgreementFactoryException(
                "Error in retrieving service manifest from agreement offer.", e );
        }

        return Manifest.Factory.newInstance( serviceManifestDoc );
    }
}
