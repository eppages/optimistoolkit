/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 **/
package eu.optimis.mi.collectors.virtualitinfrastructure;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import net.emotivecloud.vrmm.rm.data.*;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;

public class XMLParser {

    public XMLParser() {
    }

    public List<MonitoringResourceDataset> gangliaXMLtoMonitoringResources(String xml) {
        List<MonitoringResourceDataset> list = new ArrayList<MonitoringResourceDataset>();
        try {
            Cluster c = RMXMLConversor.parseXML(xml);
            List<Host> hosts = c.getHosts();
            for (Host h : hosts) {
                String hostId = h.getMachineId();
                List<Metric> metrics = h.getMetrics();
                for (Metric m : metrics) {
                    boolean useful = this.isMetricUseful(m.getName());
                    if (useful) {
                        long timestamp = System.currentTimeMillis();
                        Date date = new Date(timestamp);
                        list.add(new MonitoringResourceDataset("", hostId,
                                "", "virtual", "", m.getName(), m.getValue().toString(),
                                m.getUnits(), date));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean isMetricUseful(String name) {
        boolean useful = false;
        if (name.equalsIgnoreCase("os_release")
                || name.equalsIgnoreCase("machine_type")
                || name.equalsIgnoreCase("cpu_vnum")
                || name.equalsIgnoreCase("cpu_speed")
                || name.equalsIgnoreCase("cpu_user")
                || name.equalsIgnoreCase("mem_total")
                || name.equalsIgnoreCase("mem_used")
                || name.equalsIgnoreCase("disk_total")
                || name.equalsIgnoreCase("bytes_in")
                || name.equalsIgnoreCase("bytes_out")
                || name.equalsIgnoreCase("vm_state")) {
            useful = true;
        }
        return useful;
    }
}