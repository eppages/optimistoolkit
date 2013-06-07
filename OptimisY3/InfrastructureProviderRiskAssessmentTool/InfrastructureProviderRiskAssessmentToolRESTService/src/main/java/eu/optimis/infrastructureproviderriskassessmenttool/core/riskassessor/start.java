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

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.infrastructureproviderriskassessmenttool.core.historicaldatabase.dao.populate.RiskPropagator;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

/* this class populates the risk inventory with new values, it calculates 
 threat level risk which is then grouped into vulnerabilities. Using the threat
 level risk or the vulnerabilites other combininations of risk can be calcuated
 as in the OPTIMIS Demo
 */
public class start {

    protected static Logger log = Logger.getLogger(start.class);

    public static String risk(String[] riskID, String vmID) throws SQLException, InterruptedException {

        //tom used to loop in order to create stream of monitoring
//risk prop start
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("localhost", 8080);
        String serviceId = "test";
        if (co.getRunningServices().isEmpty()) {
            //do nowt
            log.info(" no services running");

        } else {
            List<?> serv = co.getRunningServices();

            serviceId = serv.get(0).toString();
        }

        try {
            RiskPropagator riskPropagatorOne = new RiskPropagator(
                    RiskPropagator.PROVIDERTYPE_IP,
                    RiskPropagator.SERVICEPHASE_OPERATION,
                    "provider2Test",
                    serviceId,
                    RiskPropagator.GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL);
            riskPropagatorOne.setRiskValue(1.0);
            RiskPropagator riskPropagatorTwo = new RiskPropagator(
                    RiskPropagator.PROVIDERTYPE_IP,
                    RiskPropagator.SERVICEPHASE_OPERATION,
                    "provider2Test",
                    serviceId,
                    RiskPropagator.GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL);
            riskPropagatorTwo.setRiskValue(1.0);
            RiskPropagator riskPropagatorThree = new RiskPropagator(
                    RiskPropagator.PROVIDERTYPE_IP,
                    RiskPropagator.SERVICEPHASE_OPERATION,
                    "provider2Test",
                    serviceId,
                    RiskPropagator.GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL);
            riskPropagatorThree.setRiskValue(1.0);
            RiskPropagator riskPropagatorFour = new RiskPropagator(
                    RiskPropagator.PROVIDERTYPE_IP,
                    RiskPropagator.SERVICEPHASE_OPERATION,
                    "provider2Test",
                    serviceId,
                    RiskPropagator.GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL);
            riskPropagatorFour.setRiskValue(1.0);
            riskPropagatorOne.start();
            riskPropagatorTwo.start();
            riskPropagatorThree.start();
            riskPropagatorFour.start();
            Thread.sleep(5000);
            riskPropagatorOne.setRiskValue(1.1);
            riskPropagatorTwo.setRiskValue(1.2);
            riskPropagatorThree.setRiskValue(1.3);
            riskPropagatorFour.setRiskValue(1.4);
            Thread.sleep(5000);

            //risk prop end

            //    String[] phy = new String[11];
            //   String[] vm = new String[6];
            int tom = 0;

            while (tom < 5) {
                tom++;
                //get physical host metrics
                log.info("starting monitoring count = " + tom);
                String[] phy = physicalHost.getData(vmID);
                //get virtual host metrics
                log.info("got phy vals now doing vm");
                String[] vm = virtualHost.getData("9f0e4777-8cb1-47cb-a810-7e0b28201353");

                //associate data with type
                //String[] vmConst = dataSortvm(vm);
                //calculate probabilities 
                System.out.println("going for vm probs");
                float[] probVM = probabilityVM.calcProb(phy, vm);
                System.out.println("going for phy probs");

                double[] probPhy = probabilityPhy.calcProb(phy, vm);

                //add the probabilities to the inventory
                writeProbs.write(probVM, probPhy);

                //now we get the risk for each threat
                double[] risk = getRisk.calc();

                //now we calculate the vulnerability and record overall levels of risk
                double[] riskFinal = vulnerabilities.calc(risk);
                String[] checkRisk = new String[4];
                checkRisk = riskID;
                //here we notify the HM if risk is overt the threashold


                log.info("done risk calcs now doing HM checks");

                if (riskFinal[0] - Integer.parseInt(checkRisk[0]) > 0) {
                    //       new HolisticManagementRESTClient().notifyInfrastructureRiskLevel(7);
                    checkRisk[0] = String.valueOf((long) riskFinal[0]);
                    riskPropagatorOne.setRiskValue(riskFinal[0]);
                } else {
                    checkRisk[0] = riskID[0];
                    riskPropagatorOne.setRiskValue(riskFinal[0]);
                }
                if (riskFinal[1] - Integer.parseInt(riskID[1]) > 0) {
                    //	 new HolisticManagementRESTClient().notifyPhysicalHostRiskLevel("test", 7);
                    checkRisk[1] = String.valueOf((long) riskFinal[1]);
                    riskPropagatorTwo.setRiskValue(riskFinal[1]);
                } else {
                    riskPropagatorTwo.setRiskValue(riskFinal[1]);
                    checkRisk[1] = riskID[1];

                }
                if (riskFinal[2] - Integer.parseInt(riskID[2]) > 0) {
                    //  new HolisticManagementRESTClient().notifyServiceRiskLevel("test", 7);
                    checkRisk[2] = String.valueOf((long) riskFinal[2]);
                    riskPropagatorThree.setRiskValue(riskFinal[2]);
                } else {

                    checkRisk[2] = riskID[2];
                    riskPropagatorThree.setRiskValue(riskFinal[2]);
                }
                if (riskFinal[3] - Integer.parseInt(riskID[3]) > 0) {
                    //			 new HolisticManagementRESTClient().notifyServiceRiskLevel("test", 7);
                    checkRisk[3] = String.valueOf((long) riskFinal[3]);
                    riskPropagatorFour.setRiskValue(riskFinal[3]);
                } else {
                    checkRisk[3] = riskID[3];

                    riskPropagatorFour.setRiskValue(riskFinal[3]);
                }




            }
            riskPropagatorOne.kill();
            riskPropagatorTwo.kill();
            riskPropagatorThree.kill();
            riskPropagatorFour.kill();

        } catch (Exception e) {
        }

        log.info("done HM checks");
        return "done";
    }

    public static String[] getlistPH() {

        String list[] = new String[11];
        list[0] = "cpu_average_load";
        list[1] = "cpu_speed";
        list[2] = "disk_free_space";
        list[3] = "free_memory";
        list[4] = "count_of_users";
        list[5] = "status";
        list[6] = "status";
        list[7] = "status";
        list[8] = "status";
        list[4] = "mac_address";
        list[5] = "fqdn";
        list[6] = "last_reboot";
        list[7] = "trough_time";
        list[8] = "peak_time";
        list[9] = "Downstream";
        list[10] = "Upstream";
        return list;

    }

    public static String[] getlistVM() {
        String list[] = new String[6];
        list[0] = "cpu_speed";
        list[1] = "cpu_vnum";
        list[2] = "mem_total";
        list[3] = "mem_used";
        list[4] = "disk_total";
        list[5] = "os_release";

        return list;

    }
}
