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
package eu.optimis_project.monitoring.storage;

import java.io.IOException;
import java.util.Set;

import eu.optimis_project.monitoring.Measurement;
import eu.optimis_project.monitoring.MonitoringUtil;

/**
 * This aggregator client is only used for testing purposes to insert fake
 * values into the monitoring database.
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 * 
 */
public class OptimisMonitoringTestClient {


    private OptimisMonitoringStorageManager storageManager;

    private OptimisMonitoringTestClient(String monitoring_host, int monitoring_port, String cloudoptimizer_host, int cloudoptimizer_port) {
        this.storageManager = new OptimisMonitoringStorageManager(monitoring_host, monitoring_port, MonitoringUtil.POST_PATH,
                MonitoringUtil.GET_PATH, cloudoptimizer_host, cloudoptimizer_port);
    }

    public boolean storeMonitoringData(Measurement measurement) {
        System.out.println("Sending data!");
        return storageManager.storeData(measurement);
    }

    private Set<Measurement> readMonitoringData(String serviceID) {
        System.out.println("Reading data");
        return storageManager.getData(serviceID);
    }

    public static void main(String[] args) throws IOException {

        String monitoringHostName = "212.0.127.140";
        int monitoringPort = 8080;
        String coHostName = "212.0.127.140";
        int coPort = 8080;
        OptimisMonitoringTestClient aClient = new OptimisMonitoringTestClient(monitoringHostName, monitoringPort, coHostName, coPort);

        // Used for testing elasticiy in Optimis-single-vm
        while (true) {

            String serviceID = "DemoApp";
            String instanceID = "instance-jboss-group";
            String kpiName = "ThreadCount";

            System.out.println("Press enter to send measurement for service/kpi: " + serviceID + '/'
                    + kpiName);
            System.in.read();

            String kpiValue = getRandomValue();
            kpiValue = "110";

            Measurement m1 = new Measurement(serviceID, instanceID, kpiName, kpiValue,
                    System.currentTimeMillis());
            boolean success = aClient.storeMonitoringData(m1);
            System.out.println(success ? "Success!" : "Failure");
            
            if (success) {
                Set<Measurement> values = aClient.readMonitoringData(serviceID);
                System.out.println("Found " + values.size() + " stored values for serviceID: " + serviceID);
            }
        }

    }

    private static String getRandomValue() {

        // Calculate some kind of value which is reasonably stable
        double avg = 0;
        int runs = 5;
        for (int i = 0; i < runs; i++) {
            avg += Math.random();
        }
        avg /= runs;
        String value = "" + (avg * 400);

        return value;
    }
}
