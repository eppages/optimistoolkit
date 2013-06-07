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

/**
 *
 * @author scsmj
 */
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class TestGetAggregatorClient {

    public static void main(String[] args) {
        //ResourceBundle rb = ResourceBundle.getBundle("ipraconfig");
        //GetAggregatorClient client = new GetAggregatorClient(rb.getString("config.monservicehost"), Integer.parseInt(rb.getString("config.monserviceport")), "Aggregator/Aggregator");
        //MonitoringResourceDatasets rs4 = client.getCurrentReportForPhysical();
        //System.out.println(rs4.getMonitoring_resource().size());
        //marshallObj(rs4);
        //MonPrinter(rs4);
        testrun();
    }

    public static void marshallObj(MonitoringResourceDatasets ds) {
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(MonitoringResourceDatasets.class);
            StringWriter writer = new StringWriter();
            context.createMarshaller().marshal(ds, writer);
            System.out.println(writer.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

//    private static void MonPrinter(MonitoringResourceDatasets mrd) {
//        List<MonitoringResourceDataset> resources = mrd.getMonitoring_resource();
//        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        for (MonitoringResourceDataset resource : resources) {
//            System.out.println(resource.getMetric_name() + " : " + resource.getMetric_value().toString());
//        }
//        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//    }

    static private void testrun() {

        MonClient mclient = new MonClient();
        double newraresult = 0.00;
        try {
            // Access Monitoring data and re-assess the risk at runtime...   

            List<MonitoringResourceDataset> metirc1 = mclient.getLatestReportForMetricName("free_memory");
            List<MonitoringResourceDataset> metric2 = mclient.getLatestReportForMetricName("total_memory");

            List<MonitoringResourceDataset> metric3 = mclient.getLatestReportForMetricName("status");
            List<MonitoringResourceDataset> metric4 = mclient.getLatestReportForMetricName("last_reboot");

            System.out.println("The status of host ...: " + metric3.get(0).getMetric_value());

            System.out.println("The last reboot timestamp ...: " + metric4.get(0).getMetric_value());

            int free = Integer.parseInt(metirc1.get(0).getMetric_value());
            int total = Integer.parseInt(metric2.get(0).getMetric_value());
            int used = total - free;
            
            
            // conduct the risk assessment
            // output new ra restult into hdb

            newraresult = 0.05 * used / total;
            
        } catch (Exception e) {
            System.out.println("An exception: " + e.getMessage());
        }
        System.out.println("The result of risk assessment on operation phase is output into the hdb ...: " + newraresult);
    }
}