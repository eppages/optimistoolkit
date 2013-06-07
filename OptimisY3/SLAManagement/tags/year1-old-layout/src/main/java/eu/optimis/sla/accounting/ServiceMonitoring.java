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
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.sla.ComponentConfigurationProvider;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringRecordType;

/**
 * @author hrasheed
 *
 */
public class ServiceMonitoring {
    
    private static final Logger log = Logger.getLogger(ServiceMonitoring.class);
    
    private static String mm_host = "212.0.127.140";
    private static String mm_port = "8080";
    private static String mm_url_path = "MonitoringManager/QueryResources";
    
    static {
        mm_host = ComponentConfigurationProvider.getString("monitoring.manager.host"); //$NON-NLS-1$
        mm_port = ComponentConfigurationProvider.getString("monitoring.manager.port"); //$NON-NLS-1$
        mm_url_path = ComponentConfigurationProvider.getString("monitoring.manager.url.path"); //$NON-NLS-1$
    }
    
    public static SLASeriveMonitoringRecordType[] getMonitoringRecords(String service_ID,
                                                                      Calendar start_time, Calendar end_time) {
        
        Date start_monitoring = start_time.getTime(); 
        Date end_monitoring = end_time.getTime();
        
        log.debug( "start_monitoring: " + start_monitoring.toString() + " - end_monitoring: " + end_monitoring.toString());
        
        // retrieving monitoring data of the deployed service
        //
        
        List<MonitoringResourceDataset> monitoredDataSets = null;
        try {
            
            getClient gc = new getClient(mm_host,Integer.parseInt(mm_port), mm_url_path);
            
            MonitoringResourceDatasets mrd = gc.getLatestReportForService( service_ID );
            
            monitoredDataSets = mrd.getMonitoring_resource();
            
            System.out.println( "number of retrieved service monitoring data sets: " + monitoredDataSets.size() );
            
            for (int i = 0; i < monitoredDataSets.size(); i++) {
                MonitoringResourceDataset resourceData = (MonitoringResourceDataset) monitoredDataSets.get(i);
                log.debug("Resource_type" + resourceData.getResource_type());
                log.debug("service_id" + resourceData.getService_resource_id());
                log.debug("Metric_name" + resourceData.getMetric_name());
                log.debug("Metric_unit" + resourceData.getMetric_unit());
                log.debug("Metric_value" + resourceData.getMetric_value());
                log.debug("Metric_timesstamp" + resourceData.getMetric_timestamp());
            }
        } catch (Exception e) {
            log.error( "unable to fetch monitoring records for service: " + service_ID );
        }
        
        //TODO populate record array with real metric values received from the monitoring manager
        
        //
        // TEST-ONLY
        //
        SLASeriveMonitoringRecordType monitoringRecordType = SLASeriveMonitoringRecordType.Factory.newInstance();
        monitoringRecordType.setMetricName("STATUS");
        monitoringRecordType.setMetricValue("OK");
        monitoringRecordType.setTimestamp(end_time);
        
        return new SLASeriveMonitoringRecordType[] {monitoringRecordType};
    }

}
