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

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.graap.wsag.api.types.AbstractAgreementType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.TerminateInputType;

import eu.optimis.sla.rest.CloudOptimizerREST;

/**
 * Default implementation of an OPTIMIS VM agreement.
 * 
 * OPTIMIS VM agreements are based on the OPTIMIS-VM agreement template.
 * 
 * @author owaeld
 *
 */
public class VMAgreementType extends AbstractAgreementType {
    
	private static Logger log = Logger.getLogger(VMAgreementType.class);
	
	private String serviceId;
	
    /**
     * Creates a new OPTIMIS VM agreement based on an incoming offer.
     * 
     * @param offer
     *      the offer for which this agreement is created 
     */
    public VMAgreementType(AgreementOffer offer, String serviceId) {
        super(offer);
        this.serviceId = serviceId;
    }
    
    public VMAgreementType(AgreementPropertiesType agreementPropertiesType) {
		super(agreementPropertiesType);
	}

	/**
     * Terminates an OPTIMIS VM agreement.
     * 
     * @see org.ogf.graap.wsag.api.Agreement#terminate(org.ogf.schemas.graap.wsAgreement.TerminateInputType)
     */
    public void terminate(TerminateInputType reason) {
    	
    	try {
	    	String co_url = ComponentConfigurationProvider.getString("VMServiceInstantiation.url.co"); //$NON-NLS-1$
	
	    	log.info(MessageFormat.format("calling cloud optimizer at {0}", new Object[] {co_url}));
	        
			CloudOptimizerREST co = JAXRSClientFactory.create(co_url, CloudOptimizerREST.class);
			WebClient.client(co).type(MediaType.APPLICATION_XML);
			
			co.undeploy(serviceId);
			
	        log.info("calling cloud optimizer done...");
    	}
    	catch (Exception ex) {
    		log.error("failed to undeploy service with service id: " + serviceId, ex);
    	}
    }

}
