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
package eu.optimis.sla.rest.impl;

import java.util.Date;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.sla.ComponentConfigurationProvider;

/**
 * Monitoring Client based on Jersy Framework
 * 
 * @author hrasheed
 */
public class MonitoringJersyClient
    implements MonitoringREST
{

    private static final String PATH = "MonitoringManager/QueryResources";

    private getClient monitoringClient = null;

    public MonitoringJersyClient()
    {

        String mmHost = ComponentConfigurationProvider.getString( "monitoring.manager.host" ); //$NON-NLS-1$
        String mmPort = ComponentConfigurationProvider.getString( "monitoring.manager.port" ); //$NON-NLS-1$

        monitoringClient = new getClient( mmHost, Integer.parseInt( mmPort ), PATH );
    }

    @Override
    public MonitoringResourceDatasets getLatestReportForService( String serviceID )
    {
        return monitoringClient.getLatestReportForService( serviceID );
    }

    @Override
    public MonitoringResourceDatasets getLatestReportForVirtual( String virtualID )
    {
        return monitoringClient.getLatestReportForVirtual( virtualID );
    }

    @Override
    public MonitoringResourceDatasets getReportForPartServiceId( String serviceId, Date from, Date to )
    {
        return monitoringClient.getReportForPartServiceId( serviceId, from, to );
    }

    @Override
    public MonitoringResourceDatasets getReportForPartVirtualId( String virtualId, Date from, Date to )
    {
        return monitoringClient.getReportForPartVirtualId( virtualId, from, to );
    }
}
