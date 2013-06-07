/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
import eu.optimis.mi.rest.client.getClient;
import eu.optimis.mi.rest.client.postClient;

/**
 * This aggregator client is only used for testing purposes to insert fake
 * values into the monitoring database.
 * 

 * @author Daniel Espling <espling@cs.umu.se>
 * 
 */
public class MonitoringTestClient {

    private postClient postClient;
    private getClient getClient;
    private final String PATH = "Aggregator/Aggregator/monitoringresources";

    private MonitoringTestClient(String hostName, int port) {
        this.postClient = new postClient(hostName, port, PATH);
        this.getClient = new getClient(hostName, port, "MonitoringManager/QueryResources");

    }

    public boolean storeMonitoringData(List<MonitoringResourceDataset> data) {

        System.out.println("Sending data!");
        MonitoringResourceDatasets dataSet = new MonitoringResourceDatasets();
        dataSet.setMonitoring_resource(data);
        return postClient.pushReport(dataSet);
    }

    public void getData(String serviceID, String kpiName) {
        System.out.println("Getting data for serviceID: " + serviceID + " kpiname: " + kpiName);

        /*
         * MonitoringResourceDatasets dataSet =
         * client.getReportForService_Type_Metric(serviceID, "service",
         * kpiName);
         */
        MonitoringResourceDatasets dataSet = getClient.getLatestReportForMetricName(kpiName, MonitoringUtil.SERVICE_TYPE);
        List<MonitoringResourceDataset> data = dataSet.getMonitoring_resource();

        for (MonitoringResourceDataset datum : data) {
            System.out.println("Got data: " + datum);
        }
    }

    public static void main(String[] args) throws IOException {

        String hostName = "212.0.127.140";
        // String hostName = "localhost";
        int port = 8080;
        MonitoringTestClient aClient = new MonitoringTestClient(hostName, port);

        // Used for testing elasticiy in Optimis-single-vm
        String serviceID = "DemoApp.instance-jboss";
        // String kpiName = "ActiveUsers";

        String kpiName = "Max_CPU_15";
        while (true) {
            System.out.println("Press enter to SEND measurement for service/kpi: " + serviceID + '/'
                    + kpiName);
            System.in.read();
            sentMeasurement(aClient, serviceID, kpiName);
            System.out.println("Press enter to READ measurement for service/kpi: " + serviceID + '/'
                    + kpiName);
            System.in.read();
            aClient.getData(serviceID, kpiName);
        }

    }

    private static void sentMeasurement(MonitoringTestClient aClient, String serviceID, String kpiName) {

        Date now = new Date();
        now.setTime(System.currentTimeMillis());

        // Calculate some kind of value which is reasonably stable
        double avg = 0;
        int runs = 5;
        for (int i = 0; i < runs; i++) {
            avg += Math.random();
        }
        avg /= runs;
        String value = "" + (avg * 1000);

        MonitoringResourceDataset data = new MonitoringResourceDataset(serviceID, "someVirtualResID",
                "somePhysicalResID", "service", "collectorID", kpiName, value, "someUnit", now);

        List<MonitoringResourceDataset> dataList = new ArrayList<MonitoringResourceDataset>();
        dataList.add(data);
        if (aClient.storeMonitoringData(dataList)) {
            System.out.println("Sent value: " + value + " for serviceID/kpiName: " + serviceID + '/'
                    + kpiName);
        } else {
            System.out.println("Failed to send value for serviceID/kpiName: " + serviceID + '/' + kpiName);

        }
    }
}
