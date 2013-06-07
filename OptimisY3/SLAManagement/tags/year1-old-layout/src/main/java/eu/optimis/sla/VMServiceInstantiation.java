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
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.exceptions.AgreementFactoryException;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag.server.actions.AbstractCreateAgreementAction;
import org.ogf.graap.wsag.server.monitoring.MonitorableAgreement;

import eu.optimis.sla.accounting.SimpleAccountingSystem;
import eu.optimis.sla.rest.ACModelApi;
import eu.optimis.sla.rest.CloudOptimizerREST;
import eu.optimis.types.ac.AllocationOfferDocument;
import eu.optimis.types.servicemanifest.ServiceManifestDocument;

/**
 * @author owaeld
 *
 */
public class VMServiceInstantiation extends AbstractCreateAgreementAction {
    
    private static final Logger log = Logger.getLogger(VMServiceInstantiation.class);
    
    protected static final String KEY_SERVICE_ID = "eu.optimis.manifest.serviceId";
    
    protected static final String KEY_MONITORING_TIMESTAMP = "eu.optimis.sla.service.monitoring.timestamp";
    
    protected static final String KEY_MONITORING_INTERVAL = "eu.optimis.sla.service.monitoring.interval";
    
    /**
     * Creates a new agreement instance for a VM provisioning service.
     * 
     * 
     * @see org.ogf.graap.wsag.server.actions.ICreateAgreementAction#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    public Agreement createAgreement(AgreementOffer offer) throws AgreementFactoryException {
        try {
            
            //
            // TODO: we have to extract the service id here 
            // and put it in the agreement execution context. 
            // This id should be passed to TREC when guarantees 
            // are evaluated (backward reference to the affected 
            // service)
            //
            String serviceId = allocateServices(offer);

            //
            // if the services were allocated successful we create a new agreement
            //
            VMAgreementType agreementImpl = new VMAgreementType(offer, serviceId);
            agreementImpl.setAgreementId(UUID.randomUUID().toString());
            
            //
            // instantiate agreement monitor and set monitoring interval to once a minute
            //
            MonitorableAgreement monitored = new MonitorableAgreement(agreementImpl);
            monitored.addMonitoringHandler(new VMMonitoringHandler());
            monitored.setCronExpression(ComponentConfigurationProvider.getString("VMServiceInstantiation.MonitoringInterval")); //$NON-NLS-1$

            //
            // put the service id into the execution context for monitoring
            //
            XmlString xserviceId = XmlString.Factory.newInstance();
            xserviceId.setStringValue(serviceId);
            monitored.getExecutionContext().getExecutionProperties().put(KEY_SERVICE_ID, xserviceId);
            
            //
            // put the last monitoring time stamp into the execution context for retrieving service monitoring data
            //
            XmlDate xmlTimestamp = XmlDate.Factory.newInstance();
            xmlTimestamp.setCalendarValue( Calendar.getInstance() );
            monitored.getExecutionContext().getExecutionProperties().put(KEY_MONITORING_TIMESTAMP, xmlTimestamp);
            
            //
            // put the monitoring interval value into the execution context
            //
            String monitoring_interval = ComponentConfigurationProvider.getString("service.monitoring.interval"); //$NON-NLS-1$
            XmlString xinterval = XmlString.Factory.newInstance();
            xinterval.setStringValue(monitoring_interval);
            monitored.getExecutionContext().getExecutionProperties().put(KEY_MONITORING_INTERVAL, xinterval);
            
            //
            // set the accounting system that store the SLA notification events and make available these events to subscribers 
            //
            IAccountingSystem accountingSystem = new SimpleAccountingSystem();
            monitored.setAccountingSystem( accountingSystem );
            
            monitored.startMonitoring();
            
            return monitored;
            
        } catch (Exception e) {
            throw new AgreementFactoryException(e);
        }
    }

    private String allocateServices(AgreementOffer offer) throws AgreementFactoryException {
        
    	String ac_url = ComponentConfigurationProvider.getString("VMServiceInstantiation.url.ac"); //$NON-NLS-1$
    	String co_url = ComponentConfigurationProvider.getString("VMServiceInstantiation.url.co"); //$NON-NLS-1$
    	
    	ServiceManifestDocument manifest = Tools.offerToServiceManifest(offer.getXMLObject());
    	
    	log.debug("allocate service for manifest:\n" + manifest.xmlText(new XmlOptions().setSavePrettyPrint()));
    	
    	String allocationOffer = "";

    	boolean skipACtest = ComponentConfigurationProvider.getBoolean("VMServiceInstantiation.skipACtest", false); //$NON-NLS-1$

    	if (!skipACtest) {
	    	try {
	    	    log.info(MessageFormat.format("calling admission control at {0}", new Object[] {ac_url}));
	    	    
		    	ACModelApi ac = JAXRSClientFactory.create(ac_url, ACModelApi.class);
		    	allocationOffer = ac.performACTest(manifest.xmlText());

				// allocationOffer = ac.admissionControl("allocate", manifest.xmlText());
				log.info("calling admission control done...");
				

                log.info(MessageFormat.format("processing admission control response", new Object[] {}));
                log.debug("admission control plain response: \n" + allocationOffer);
                
                XmlOptions opt = new XmlOptions();
                opt.setLoadReplaceDocumentElement(AllocationOfferDocument.type.getDocumentElementName());
                AllocationOfferDocument ao = AllocationOfferDocument.Factory.parse(allocationOffer, opt);
                
                log.debug("admission control parsed response:");
                log.debug(ao.xmlText(new XmlOptions().setSavePrettyPrint()));
                
                if (ao.getAllocationOffer().getAdmissionControlDecision() == 0) {
                    throw new AgreementFactoryException("service it is not admidded by admission control.");
                }
                
				
	    	} catch (Exception e) {
	    	    if (e instanceof AgreementFactoryException) {
	    	        throw (AgreementFactoryException)e;
	    	    }
	    	    
				throw new AgreementFactoryException("Error calling admission control.", e);
			}
    	}
    	else {
    		log.info("admission control test is skipped");
    	}

    	try {
            log.info(MessageFormat.format("calling cloud optimizer at {0}", new Object[] {co_url}));
            
			CloudOptimizerREST co = JAXRSClientFactory.create(co_url, CloudOptimizerREST.class);
			WebClient.client(co).type(MediaType.APPLICATION_XML);
			
			String response = co.deploy(manifest.xmlText());
			
            log.info("calling cloud optimizer done...");
            log.debug("cloud optimizer response: '" + response + "'");
            
            if ((response == null) || ("".equals(response))) {
                throw new AgreementFactoryException("Service could not be deployed by cloud optimizer.");
            }
            
		} catch (Exception e) {
            if (e instanceof AgreementFactoryException) {
                throw (AgreementFactoryException)e;
            }
            
			throw new AgreementFactoryException("Error calling cloud optimizer.", e);
		}
		
		return manifest.getServiceManifest().getServiceDescriptionSection().getServiceId();
		
    }
}
