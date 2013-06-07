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

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.jaxrs.impl.MetadataMap;
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

    /**
     * Creates a new agreement instance for a VM provisioning service.
     * 
     * @see org.ogf.graap.wsag.server.actions.ICreateAgreementAction#createAgreement(org.ogf.graap.wsag.api.AgreementOffer)
     */
    @Override
    public Agreement createAgreement( AgreementOffer offer ) throws AgreementFactoryException
    {
        
        throw new AgreementFactoryException( "service could not be deployed." );
    }

}
