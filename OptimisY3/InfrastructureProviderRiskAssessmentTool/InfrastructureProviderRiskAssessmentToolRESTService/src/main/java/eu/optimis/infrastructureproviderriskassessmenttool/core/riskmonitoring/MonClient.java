/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.optimis.infrastructureproviderriskassessmenttool.core.riskmonitoring;

import eu.optimis.infrastructureproviderriskassessmenttool.core.configration.ConfigManager;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MonClient {

    private getClient mMonClient;

    public MonClient() {
        try {
            PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
            mMonClient = new getClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("monitoringport")), configOptimis.getString("monitoringpath"));
            } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public List<MonitoringResourceDataset> getLatestReportForMetricName(String metricName) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestReportForMetricName(metricName,"physical");
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }
  
      public List<MonitoringResourceDataset> getLatestReportForMetricName(String metricName, String resourceType, String resourceId) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestReportForMetricName(metricName, resourceType);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }
  
    public List<MonitoringResourceDataset> getLatestCompleteReportForService(String serviceName) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestCompleteReportForService(serviceName);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }
        public List<MonitoringResourceDataset> getLatestCompleteReportForVirtual(String virtualMachine) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestCompleteReportForVirtual(virtualMachine);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }
  
              public List<MonitoringResourceDataset> getLatestCompleteReportForPhysical(String physicalMachine) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestCompleteReportForPhysical(physicalMachine);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }

  

              
        
        public List<MonitoringResourceDataset> getLatestReportForMetricName(String metricName, String resource){ 
        MonitoringResourceDatasets mrd = mMonClient.getLatestReportForMetricName(metricName, resource);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }
        public List<MonitoringResourceDataset> getLatestReportForMetricNameId(String metricName, String resourceType, String resourceId){ 
        MonitoringResourceDatasets mrd = mMonClient.getLatestReportForMetricNameId(metricName, resourceType, resourceId);
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        return resources;
    }

        
 
    public MonitoringResourceDatasets getLatestReportForEnergy(String nodeId) {
        MonitoringResourceDatasets mrd = mMonClient.getLatestReportForEnergy(nodeId);

        return mrd;
    }

    
  
        
  
}