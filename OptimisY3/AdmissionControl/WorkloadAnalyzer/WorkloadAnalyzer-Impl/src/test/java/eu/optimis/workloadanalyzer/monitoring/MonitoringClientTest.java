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
package eu.optimis.workloadanalyzer.monitoring;

import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.workloadanalyzer.clients.MonitoringClient;

/**
 * @author hrasheed
 * 
 */
public class MonitoringClientTest extends AbstractWorkloadTest
{
    private static final Logger LOG = Logger.getLogger(MonitoringClientTest.class);
    
    @Override
    protected void setUp() throws Exception 
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception 
    {
        super.tearDown();
    }

    public void testMonitoringClient() throws Exception 
    {
        MonitoringClient monitoringClient = new MonitoringClient(getMonitoringHost(), getMonitoringPort(), getMonitoringPath());
        
        MonitoringResourceDatasets mrdHostData = monitoringClient.getLatestReportForPhysical( "optimis1" );
        
        List<MonitoringResourceDataset> physicalHostMetrics = mrdHostData.getMonitoring_resource();
        
        LOG.info( "number of physical data sets: " + mrdHostData.getMonitoring_resource().size() );
        
        for (int j = 0; j < physicalHostMetrics.size(); j++) {
            MonitoringResourceDataset resourceData = (MonitoringResourceDataset) physicalHostMetrics.get(j);
            LOG.info("Physical_resource_id:" + resourceData.getPhysical_resource_id() +
                    "-Metric_name:" + resourceData.getMetric_name() + 
                    "-Metric_unit:" + resourceData.getMetric_unit() +
                    "-Metric_value:" + resourceData.getMetric_value() +
                    "-Metric_timesstamp:" + resourceData.getMetric_timestamp());
        }
    }

}