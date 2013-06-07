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

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.ResourcesDocument;
import org.ogf.graap.wsag.server.monitoring.IMonitoringContext;
import org.ogf.graap.wsag.server.monitoring.IServiceTermMonitoringHandler;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.w3c.dom.Node;

import eu.optimis.sla.accounting.ServiceMonitoring;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringDocument;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringRecordType;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringType;

/**
 * @author owaeld
 *
 */
public class VMMonitoringHandler implements IServiceTermMonitoringHandler {

    private static final Logger log = Logger.getLogger(VMMonitoringHandler.class);
    /**
     * Monitors the provisioning of a VM. As soon a service is deployed
     * this handler adds the deployment information such as host name
     * to the corresponding SDT state.
     * 
     * @param monitoringContext
     *      the monitoring context of the VM agreement
     * 
     * @see org.ogf.graap.wsag.server.monitoring.IServiceTermMonitoringHandler#monitor(org.ogf.graap.wsag.server.monitoring.IMonitoringContext)
     */
    public void monitor(IMonitoringContext monitoringContext) throws Exception {
        
        ServiceTermStateType state = monitoringContext.getServiceTermStateByName("VirtualSystemDescription");
        state.setState(ServiceTermStateDefinition.READY);
        
        //
        // if the state does not include details -> create the detail section
        //
        if (state.selectChildren(ResourcesDocument.type.getDocumentElementName()).length == 0) {
            ResourcesDocument detail = ResourcesDocument.Factory.newInstance(); 
            detail.addNewResources().addNewCandidateHosts().addHostName("optimis-host-1");
            Node imported = state.getDomNode().getOwnerDocument().importNode(detail.getResources().getDomNode(), true);
            state.getDomNode().appendChild(imported);
        }
        
        //
        // retrieving Service_ID from execution properties
        //
        String service_ID = ((XmlString) monitoringContext.getProperties().get(VMServiceInstantiation.KEY_SERVICE_ID)).getStringValue();
        log.debug( "service-ID: " + service_ID );
        
        //
        // retrieving last monitoring time stamp from execution properties
        //
        Calendar start_time = ((XmlDate) monitoringContext.getProperties().get(VMServiceInstantiation.KEY_MONITORING_TIMESTAMP)).getCalendarValue();
        
        Calendar end_time = Calendar.getInstance();
        
        //
        // get the service monitoring interval value from the execution context
        //
        String monitoring_interval = ((XmlString) monitoringContext.getProperties().get(VMServiceInstantiation.KEY_MONITORING_INTERVAL)).getStringValue();
        log.debug( "service monitoring interval: " + monitoring_interval );
        
        SLASeriveMonitoringRecordType[] serviceMonitoringRecords = ServiceMonitoring.getMonitoringRecords( service_ID, start_time, end_time );
        
        SLASeriveMonitoringType serviceMonitoringType = loadServiceMonitoring(state);
        
        serviceMonitoringType.setServiceId( service_ID );
        
        for ( int i = 0; i < serviceMonitoringRecords.length; i++ ) {
            serviceMonitoringType.addNewSLASeriveMonitoringRecord().set( serviceMonitoringRecords[i] );
        }
        
        System.out.println(serviceMonitoringType.xmlText(new XmlOptions().setSavePrettyPrint()));
    }
    
    private synchronized SLASeriveMonitoringType loadServiceMonitoring(ServiceTermStateType state) {
        
        XmlObject[] serviceMonitoring = state.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        
        if (serviceMonitoring.length == 0) {
            if(log.isTraceEnabled()) {
                log.trace("Initialize service monitoring doc in service term state.");
            }
            
            SLASeriveMonitoringDocument serviceMonitoringDoc = SLASeriveMonitoringDocument.Factory.newInstance();
            serviceMonitoringDoc.addNewSLASeriveMonitoring();
            
            Node imported = state.getDomNode().getOwnerDocument().importNode(serviceMonitoringDoc.getSLASeriveMonitoring().getDomNode(), true);
            state.getDomNode().appendChild(imported);
            
            serviceMonitoring = state.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        }
        
        if (serviceMonitoring.length > 1) {
            if(log.isDebugEnabled()) {
                log.debug("Multiple service monitoring doc founds in service term state. Keeping the first, removing the others.");
            }
            
            for (int i = 1; i < serviceMonitoring.length; i++) {
                state.getDomNode().removeChild(serviceMonitoring[i].getDomNode());
            }
            
            serviceMonitoring = state.selectChildren(SLASeriveMonitoringDocument.type.getDocumentElementName());
        }
        
        SLASeriveMonitoringType serviceMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];
        
        return serviceMonitoringType;
    } 
}
