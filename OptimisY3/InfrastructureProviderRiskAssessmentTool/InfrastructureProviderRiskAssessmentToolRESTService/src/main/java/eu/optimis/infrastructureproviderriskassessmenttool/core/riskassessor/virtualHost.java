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
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

public class virtualHost {

    protected static Logger log = Logger.getLogger(virtualHost.class);

    public static String[] getData(String serviceID) throws SQLException {
        String[] vm2 = new String[12];
        //Getting metrics for VM
        MonClient mclient = new MonClient();
        try {
            List<MonitoringResourceDataset> metric2 = mclient.getLatestCompleteReportForVirtual(serviceID);

            int i = 0;



            while (i < metric2.size()) {
                vm2[i] = metric2.get(i).getMetric_value();
                log.info("VM metric " + i + " = " + vm2[i]);
                i++;
            }
        } catch (Exception e) {
            log.info("error in vm get " + e);
        }

        return vm2;

    }
}