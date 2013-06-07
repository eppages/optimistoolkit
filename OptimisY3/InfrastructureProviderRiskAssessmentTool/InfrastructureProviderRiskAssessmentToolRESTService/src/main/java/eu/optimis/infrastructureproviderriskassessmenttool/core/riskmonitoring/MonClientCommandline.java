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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class MonClientCommandline {
    
    protected static Logger log = Logger.getLogger(MonClientCommandline.class);

    public static void main(String[] args) {
        
        //PropertyConfigurator.configure(ConfigManager.getConfigFilePath(ConfigManager.LOG4J_CONFIG_FILE));
        log.info("IPRA: Test Monitoring Client...");
        PropertiesConfiguration configOptimis = ConfigManager.getPropertiesConfiguration(ConfigManager.OPTIMIS_CONFIG_FILE);
        
        // Test Monitoring Access
        getClient mclient = new getClient(configOptimis.getString("optimis-ipvm"), Integer.parseInt(configOptimis.getString("monitoringport")), configOptimis.getString("monitoringpath"));
      /*  
        CloudOptimizerRESTClient CoClient = new CloudOptimizerRESTClient(rb.getString("config.coservicehost"), Integer.parseInt(rb.getString("config.coserviceport")), "CloudOptimizer");
        
       System.out.println(CoClient.getNodesId().get(0));
       System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
       System.out.println(CoClient.getPhysicalResource("optimis1"));
       System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
       System.out.println(CoClient.getVMsIdsOfService("DemoApp"));
       System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        
      */  
        MonitoringResourceDatasets mrd = null;
        /*
        mrd = mclient.getReportForAllPhysical();
        MonPrinter(mrd);
        
        mrd = mclient.getReportForAllVirtual();
        MonPrinter(mrd);
        */
        //mrd = mclient.getLatestReportForPhysical("optimis1");
        //mrd = mclient.getLatestReportForMetricName("free_memory","physical");
        //mrd = mclient.getReportForService("DemoApp");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date start = null;
        try {
             start = df.parse("01/01/2010");
            } catch (ParseException e) {
            e.printStackTrace();
        }

        Date end = new Date();

        //mrd = mclient.getReportForPartMetricName("status", "physical", start, end);
        //MonPrinter(mrd);
        mrd = mclient.getReportForPartMetricName("vm_state", "virtual", start, end);
        MonPrinter(mrd);
    }
    private static void MonPrinter(MonitoringResourceDatasets mrd){
        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (MonitoringResourceDataset resource : resources) {
            System.out.println(resource.getMetric_name() + " : " + resource.getMetric_value().toString());
            System.out.println("Timestamp: " + resource.getMetric_timestamp());

        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}