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


package eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor;

import eu.optimis.infrastructureproviderriskassessmenttool.core.riskmonitoring.MonClient;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

//THIS CLASS RETURNS THE VALUES FOR EACH METRIC 
public class physicalHost {

    protected static Logger log = Logger.getLogger(physicalHost.class);

    public static String[] getData(String phyID) throws SQLException, Exception {

        String[] vm2 = new String[11];

        //set up monitoring client
        MonClient mclient = new MonClient();
       
        //invoke for specific metrics
       
        log.info("node ID = " + phyID);
         vm2[0] = cpuUtil(phyID); 
        log.info("Level of CPU Utilisation = + " + vm2[0]);
       
         List<MonitoringResourceDataset> metric2 = mclient.getLatestReportForMetricNameId("cpu_speed", "physical", phyID);
         vm2[1] = metric2.get(0).getMetric_value();
         log.info("got Mon Client");
         List<MonitoringResourceDataset> metric3 = mclient.getLatestReportForMetricNameId("disk_free_space", "physical", phyID);
         vm2[2] = metric3.get(0).getMetric_value();
         log.info("got Mon Client");
         List<MonitoringResourceDataset> metric4 = mclient.getLatestReportForMetricNameId("free_memory", "physical", phyID);
         vm2[3] = metric4.get(0).getMetric_value();
         //log.info("got Mon Client");
         List<MonitoringResourceDataset> metric5 = mclient.getLatestReportForMetricNameId("mac_address", "physical", phyID);
         vm2[4] = metric5.get(0).getMetric_value();
         //log.info("got Mon Client");
         List<MonitoringResourceDataset> metric6 = mclient.getLatestReportForMetricNameId("fqdn", "physical", phyID);
         vm2[5] = metric6.get(0).getMetric_value();
         //log.info("got Mon Client");
         List<MonitoringResourceDataset> metric7 = mclient.getLatestReportForMetricNameId("last_reboot", "physical", phyID);
         vm2[6] = metric7.get(0).getMetric_value();
         //        log.info("got Mon Client");
         List<MonitoringResourceDataset> metric8 = mclient.getLatestReportForMetricNameId("trough_time", "physical", phyID);
         vm2[7] = metric8.get(0).getMetric_value();
         //      log.info("got Mon Client");
         List<MonitoringResourceDataset> metric9 = mclient.getLatestReportForMetricNameId("peak_time", "physical", phyID);
         vm2[8] = metric9.get(0).getMetric_value();
         log.info("got Mon Client");
         List<MonitoringResourceDataset> metric10 = mclient.getLatestReportForMetricNameId("Upstream", "physical", phyID);
         vm2[9] = metric10.get(0).getMetric_value();
         //       log.info("got Mon Client");  
         List<MonitoringResourceDataset> metric11 = mclient.getLatestReportForMetricNameId("Downstream", "physical", phyID);
         vm2[10] = metric11.get(0).getMetric_value();
         //    log.info("got Mon Client");
          
         log.info("got all data from phy");
         

        return vm2;

    }

    private static double checkUtilizationBoundaries(String vmId, String cpuUtilization) {
        double ret = Double.parseDouble(cpuUtilization);

        if (ret < 0.0) {
            log.debug("Obtained negative CPU utilization (" + cpuUtilization + ") for " + vmId);
            return 0.0;
        } else if (ret > 100.0) {
            log.debug("Obtained greater than 100.0 CPU utilization (" + cpuUtilization + ") for " + vmId);
            return 100.0;

        }
        return ret;
    }

    public static String cpuUtil(String phy) throws Exception {
        String xenTopReport = null;
        double vmsCpuUtilization = 0.0;
        double dom0CpuUtilization = 0.0;
        String nodeId = phy;
        new Date();
        double phyUtil = 0.0;

        MonClient mi_client = new MonClient();

        MonitoringResourceDatasets nodeReportForEnergy = null;
        try {
            nodeReportForEnergy = mi_client.getLatestReportForEnergy(nodeId);
        } catch (Exception ex) {
            log.error("Error while obtaining energy values from the Energy Collector.");
            log.error(ex.getMessage());
            throw new Exception("Error while obtaining energy values from the Energy Collector.");
        }
        List<MonitoringResourceDataset> nodeEnergyMetrics = nodeReportForEnergy.getMonitoring_resource();
        for (MonitoringResourceDataset metric : nodeEnergyMetrics) {
            if (metric.getMetric_name().equalsIgnoreCase("xentop_cpu")) {
                xenTopReport = metric.getMetric_value();
                log.info("xenTopReport = " + metric.getMetric_value());
            }
            if (metric.getMetric_name().equalsIgnoreCase("real_power")) {
                metric.getMetric_value();
            }
            //timeStamp = metric.getMetric_timestamp().getTime();
        }

        if (xenTopReport == null) {
            log.error("Obtained null xen_top values from the Monitoring");
            throw new Exception("Obtained null xen_top values from the Monitoring");
        }

        xenTopReport.length();


        String[] tmp = xenTopReport.split(";");



        String val1 = tmp[1];
        int val2 = val1.indexOf(" ");
        val1.substring(val2);
        // log.info("retun value will be " + val3); 
        for (int i = 0; i < tmp.length; i++) {
            String domNameVsCpu[] = tmp[i].split(" ");
            if (domNameVsCpu.length == 2) {
                if (!domNameVsCpu[0].equalsIgnoreCase("Domain-0")) {
                    double vmCpuUtilizationTemp = checkUtilizationBoundaries("VM ".concat(domNameVsCpu[0]), domNameVsCpu[1]);
                    vmsCpuUtilization += vmCpuUtilizationTemp;
                    //   log.info("##############vmsCpuUtilization########### = " + vmsCpuUtilization);
                    phyUtil = vmsCpuUtilization;
                    //    updatePastVMUtilizations(domNameVsCpu[0], date.getTime(), vmCpuUtilizationTemp);
                } else {
                    dom0CpuUtilization = checkUtilizationBoundaries(domNameVsCpu[0].concat(nodeId), domNameVsCpu[1]);
                    //  updatePastVMUtilizations(domNameVsCpu[0].concat(nodeId), date.getTime(), dom0CpuUtilization);
                    //    log.info("##############dom0CpuUtilization########### = " + dom0CpuUtilization);
                    phyUtil = dom0CpuUtilization;
                }
                //log.info("(REMOVE) Utilization of VM " + domNameVsCpu[0] + ": " + domNameVsCpu[1]);
            } else {
                log.debug("Invalid Domain CPU lecture.");
            }
           }
        // return cpuusage;
        return Double.toString(phyUtil);
    }

    public static String forFlex() {
        String[] tr2 = new String[4];
        BufferedReader rd;
        OutputStreamWriter wr;
        String val = null;
        try {
            URL url = new URL("http://109.231.122.28/monit.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            int i = 0;

            while ((line = rd.readLine()) != null) {
                if (line.length() > 2) {
                    String tr = line.trim();
                    log.info("here is the value before split " + line);
                    tr2 = tr.split("(?!^)");
                }

            }
            line = new StringBuilder().append(tr2[0]).append(tr2[2]).toString();
            log.info("line final = " + line);
            char ggg1 = line.charAt(0);
            String gg = String.valueOf(ggg1);
            char ggg2 = line.charAt(1);
            String gg2 = String.valueOf(ggg2);
            line = new StringBuilder().append(gg).append(gg2).toString();

            log.info("3 and line is = " + line);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        System.out.println("val = " + val);

        return val;
    }
}
