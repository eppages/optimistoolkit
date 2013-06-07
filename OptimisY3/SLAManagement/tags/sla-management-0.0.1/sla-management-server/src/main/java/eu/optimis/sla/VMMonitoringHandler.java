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
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringDocument;
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringRecordType;
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringType;

/**
 * @author hrasheed
 * 
 */
public class VMMonitoringHandler
    implements IServiceTermMonitoringHandler
{

    private static final Logger LOG = Logger.getLogger( VMMonitoringHandler.class );

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
    public void monitor( IMonitoringContext monitoringContext ) throws Exception
    {

        ServiceTermStateType state = monitoringContext.getServiceTermStateByName( "OPTIMIS_SERVICE_SDT" );

        state.setState( ServiceTermStateDefinition.READY );
        
        //
        // if the state does not include details -> create the detail section
        //
        if ( state.selectChildren( ResourcesDocument.type.getDocumentElementName() ).length == 0 )
        {
            ResourcesDocument detail = ResourcesDocument.Factory.newInstance();
            detail.addNewResources().addNewCandidateHosts().addHostName( "optimis-host-1" );
            Node imported =
                state.getDomNode().getOwnerDocument().importNode( detail.getResources().getDomNode(), true );
            state.getDomNode().appendChild( imported );
        }

        //
        // retrieving Service_ID from execution properties
        //
        XmlString xmlServiceId =
            (XmlString) monitoringContext.getProperties().get( VMServiceInstantiation.KEY_SERVICE_ID );
        String serviceID = xmlServiceId.getStringValue();

        //
        // retrieving last monitoring time stamp from execution properties
        //
        Calendar startTime =
            ( (XmlDate) monitoringContext.getProperties().get(
                VMServiceInstantiation.KEY_MONITORING_TIMESTAMP ) ).getCalendarValue();

        Calendar endTime = Calendar.getInstance();

        //
        // get the service monitoring interval value from the execution context
        //
        String monitoringInterval =
            ( (XmlString) monitoringContext.getProperties().get(
                VMServiceInstantiation.KEY_MONITORING_INTERVAL ) ).getStringValue();

        //
        // retrieving service monitoring data
        //
        SLASeriveMonitoringRecordType serviceMonitoringRecord =
            ServiceMonitoring.getServiceMonitoringRecord( serviceID, startTime, endTime );
        
        SLASeriveMonitoringType serviceMonitoringType = loadServiceMonitoring( state );

        serviceMonitoringType.setServiceId( serviceID );

        if( serviceMonitoringRecord != null )
        {
            if ( serviceMonitoringType.getSLASeriveMonitoringRecordArray().length > 0 )
            {
                serviceMonitoringType.getSLASeriveMonitoringRecordArray( 0 ).set( serviceMonitoringRecord );
            }
            else
            {
                serviceMonitoringType.addNewSLASeriveMonitoringRecord().set( serviceMonitoringRecord );
            }
        }
        
        if ( LOG.isTraceEnabled() )
        {
            LOG.trace( serviceMonitoringType.xmlText( new XmlOptions().setSavePrettyPrint() ) );
        }
    }

    private synchronized SLASeriveMonitoringType loadServiceMonitoring( ServiceTermStateType state )
        throws Exception
    {

        SLASeriveMonitoringType serviceMonitoringType = null;

        XmlObject[] serviceMonitoring =
            state.selectChildren( SLASeriveMonitoringDocument.type.getDocumentElementName() );

        if ( serviceMonitoring.length >= 1 )
        {
            for ( int i = 0; i < serviceMonitoring.length; i++ )
            {
                state.getDomNode().removeChild( serviceMonitoring[i].getDomNode() );
            }
        }

        SLASeriveMonitoringDocument serviceMonitoringDoc = SLASeriveMonitoringDocument.Factory.newInstance();
        serviceMonitoringDoc.addNewSLASeriveMonitoring();

        Node imported =
            state.getDomNode().getOwnerDocument()
                 .importNode( serviceMonitoringDoc.getSLASeriveMonitoring().getDomNode(), true );
        state.getDomNode().appendChild( imported );

        serviceMonitoring = state.selectChildren( SLASeriveMonitoringDocument.type.getDocumentElementName() );

        serviceMonitoringType = (SLASeriveMonitoringType) serviceMonitoring[0];

        return serviceMonitoringType;
    }
}
