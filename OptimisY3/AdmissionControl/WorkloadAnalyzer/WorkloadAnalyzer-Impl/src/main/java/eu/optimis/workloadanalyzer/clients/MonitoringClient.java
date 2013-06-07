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
package eu.optimis.workloadanalyzer.clients;

import java.util.Date;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;

/**
 * @author hrasheed
 * 
 */
public class MonitoringClient
{
    private static final Logger LOG = Logger.getLogger( MonitoringClient.class );
    
    private getClient monitoringClient = null;
    
    public MonitoringClient(String host, int port, String path) throws Exception 
    {    
        monitoringClient = new getClient(host,port,path);
    }
    
    public MonitoringResourceDatasets getLatestReportForPhysical( String physicalId )
    {
        return monitoringClient.getLatestReportForPhysical( physicalId );
    }

    public MonitoringResourceDatasets getLatestReportForVirtual( String virtualID )
    {
        return monitoringClient.getLatestReportForVirtual( virtualID );
    }

    public MonitoringResourceDatasets getReportForPartPhysicalId( String physicalId, Date from, Date to )
    {
        //monitoringClient.getLatestReportForMetricName( metricName, resourceType );
        //monitoringClient.getReportForPartMetricName( metricName, resourceType, from, to );
        return monitoringClient.getReportForPartPhysicalId( physicalId, from, to );
    }

    public MonitoringResourceDatasets getReportForPartVirtualId( String virtualId, Date from, Date to )
    {
        return monitoringClient.getReportForPartVirtualId( virtualId, from, to );
    }
    
}
