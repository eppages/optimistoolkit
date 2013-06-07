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
package eu.optimis.sla.rest;

import java.util.Date;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.sla.rest.impl.MonitoringJersyClient;
import eu.optimis.sla.rest.impl.MonitoringREST;

/**
 * @author hrasheed
 * 
 */
public class MonitoringClient
{
    private static final Logger LOG = Logger.getLogger( MonitoringClient.class );

    private static MonitoringREST delegate;

    /**
     * @param serviceID
     * @return
     * @see eu.optimis.mi.rest.client.getClient#getLatestReportForService(java.lang.String)
     */
    public MonitoringResourceDatasets getLatestReportForService( String serviceID )
    {
        return getMonitoringClient().getLatestReportForService( serviceID );
    }

    /**
     * @param virtualID
     * @return
     * @see eu.optimis.mi.rest.client.getClient#getLatestReportForVirtual(java.lang.String)
     */
    public MonitoringResourceDatasets getLatestReportForVirtual( String virtualID )
    {
        return getMonitoringClient().getLatestReportForVirtual( virtualID );
    }

    /**
     * @param serviceId
     * @param from
     * @param to
     * @return
     * @see eu.optimis.mi.rest.client.getClient#getReportForPartServiceId(java.lang.String, java.util.Date,
     *      java.util.Date)
     */
    public MonitoringResourceDatasets getReportForPartServiceId( String serviceId, Date from, Date to )
    {
        return getMonitoringClient().getReportForPartServiceId( serviceId, from, to );
    }

    /**
     * @param virtualId
     * @param from
     * @param to
     * @return
     * @see eu.optimis.mi.rest.client.getClient#getReportForPartVirtualId(java.lang.String, java.util.Date,
     *      java.util.Date)
     */
    public MonitoringResourceDatasets getReportForPartVirtualId( String virtualId, Date from, Date to )
    {
        return getMonitoringClient().getReportForPartVirtualId( virtualId, from, to );
    }

    /**
     * instantiate the monitoring client
     * 
     * @return the monitoring client
     */
    private MonitoringREST createJersyClient()
    {
        MonitoringJersyClient monitoringClient = new MonitoringJersyClient();
        return monitoringClient;
    }

    public synchronized MonitoringREST getMonitoringClient()
    {
        if ( getDefaultMonitoringClient() == null )
        {
            delegate = createJersyClient();
        }
        return getDefaultMonitoringClient();
    }

    public static MonitoringREST getDefaultMonitoringClient()
    {
        return delegate;
    }

    public static void setDefaultMonitoringClient( MonitoringREST defaultClient )
    {
        MonitoringClient.delegate = defaultClient;
    }
}
