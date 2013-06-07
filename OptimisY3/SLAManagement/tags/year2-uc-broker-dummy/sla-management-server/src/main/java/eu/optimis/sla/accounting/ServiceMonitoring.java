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
package eu.optimis.sla.accounting;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

import eu.optimis.sla.rest.MonitoringClient;
import eu.optimis.sla.types.service.monitoring.MonitoringRecordType;
import eu.optimis.sla.types.service.monitoring.SLASeriveMonitoringRecordType;
import eu.optimis.sla.types.service.monitoring.VirtualMachineSystemType;
import eu.optimis.sla.types.service.monitoring.VirtualMachineType;

/**
 * @author hrasheed
 * 
 */
public class ServiceMonitoring
{

    private static final Logger LOG = Logger.getLogger( ServiceMonitoring.class );

    public static SLASeriveMonitoringRecordType getServiceMonitoringRecord( String serviceID,
                                                                            Calendar startTime,
                                                                            Calendar endTime )
    {

        MonitoringClient monitoringClient = new MonitoringClient();

        //
        // TODO ONLY for testing
        //
        startTime.roll( Calendar.MONTH, -1 );

        Date startMonitoring = startTime.getTime();
        Date endMonitoring = endTime.getTime();

        // LOG.debug( "start: " + startMonitoring.toString() + " - end: " + endMonitoring.toString() );

        //
        // retrieving monitoring data for the deployed service
        //

        List<MonitoringResourceDataset> monitoredDataSets = null;

        try
        {

            MonitoringResourceDatasets mrd = monitoringClient.getLatestReportForService( serviceID );

            // MonitoringResourceDatasets mrd = monitoringClient.getReportForPartService( startMonitoring,
            // endMonitoring );

            monitoredDataSets = mrd.getMonitoring_resource();

            LOG.trace( "number of retrieved service monitoring data sets: " + monitoredDataSets.size() );

            for ( int i = 0; i < monitoredDataSets.size(); i++ )
            {
                MonitoringResourceDataset resourceData = monitoredDataSets.get( i );
                if ( LOG.isTraceEnabled() )
                {
                    LOG.trace( "Resource_type: " + resourceData.getResource_type() );
                    LOG.trace( "service_id: " + resourceData.getService_resource_id() );
                    LOG.trace( "Metric_name: " + resourceData.getMetric_name() );
                    LOG.trace( "Metric_unit: " + resourceData.getMetric_unit() );
                    LOG.trace( "Metric_value: " + resourceData.getMetric_value() );
                    LOG.trace( "Metric_timesstamp: " + resourceData.getMetric_timestamp() );
                }

            }
        }
        catch ( Exception e )
        {
            LOG.error( "unable to fetch monitoring records for service: " + serviceID );
        }

        // TODO populate record array with real metric values received from the monitoring manager

        //
        // TEST-ONLY
        //
        SLASeriveMonitoringRecordType monitoringRecordType =
            SLASeriveMonitoringRecordType.Factory.newInstance();

        monitoringRecordType.setTimestamp( endTime );

        MonitoringRecordType serviceRecord = monitoringRecordType.addNewServiceRecord();
        serviceRecord.setMetricName( "STATUS-1" );
        serviceRecord.setMetricValue( "OK" );

        VirtualMachineSystemType virtualSystems = monitoringRecordType.addNewVirtualMachineSystem();

        VirtualMachineType virtualSystem = virtualSystems.addNewVirtualMachine();
        virtualSystem.setVirtualMachineId( "virtual-id" );

        MonitoringRecordType vmRecord = virtualSystem.addNewVirtualMachineRecord();
        vmRecord.setMetricName( "THREADS-1" );
        vmRecord.setMetricValue( "1-1" );

        VirtualMachineType virtualSystem2 = virtualSystems.addNewVirtualMachine();
        virtualSystem2.setVirtualMachineId( "virtual-id-2" );

        MonitoringRecordType vmRecord2 = virtualSystem2.addNewVirtualMachineRecord();
        vmRecord2.setMetricName( "THREADS-2" );
        vmRecord2.setMetricValue( "2-1" );

        return monitoringRecordType;
    }
}
