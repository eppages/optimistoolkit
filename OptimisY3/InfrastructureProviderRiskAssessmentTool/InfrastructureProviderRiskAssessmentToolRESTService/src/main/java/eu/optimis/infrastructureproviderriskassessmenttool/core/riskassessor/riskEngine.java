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

/**
 *
 * @author scstki
 */
public class riskEngine {

    protected static Logger log = Logger.getLogger(start.class);

    public double[] getRisks(String vmID, String phys, String ServiceID) throws SQLException, Exception {
        log.info("risk engine started");

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("localhost", 8080);
        String serviceId = "6a0aff59-9e17-4b5f-978f-55d033f0a37f";
        if (co.getRunningServices().isEmpty()) {
            log.info(" no services running using 6a0aff59-9e17-4b5f-978f-55d033f0a37f ");

        } else {
            List<?> serv = co.getRunningServices();
            serviceId = serv.get(0).toString();
        }

        log.info(" serviceID from CO =  " + serviceId);

        String[] phy = new String[11];
        String[] vm = new String[6];
        phy = physicalHost.getData(phys);

        log.info("got phy vals now doing vm");

        vm = virtualHost.getData(serviceId);

        //calculate probabilities 0-10 by setting bands against value 
        float[] probVM = probabilityVM.calcProb(phy, vm);
        log.info("about to parse bands for phy");
        double[] probPhy = probabilityPhy.calcProb(phy, vm);
        log.info("about to write probs");
        //add the probabilities to the inventory
        writeProbs.write(probVM, probPhy);

      //  log.info("probs written");
        //now we get the risk values for each threat
        double[] risk = getRisk.calc();
     //   log.info("get risk done");
        //now we calculate the risk bands and group risk into vulnerabilites
     //   log.info("now doing vulnerab");
        double[] riskFinal = vulnerabilities.calc(risk);
    //    log.info("riskFinal done");
        int d = 0;
        while (d < riskFinal.length - 1) {
            log.info("riskfinal val " + d + " = " + riskFinal[d]);
            d++;
        }
        log.info("risk graph 1 = " + riskFinal[0]);
        log.info("risk graph 2 = " + riskFinal[1]);
        log.info("risk graph 3 = " + riskFinal[2]);
        log.info("risk graph 4 = " + riskFinal[3]);
      RiskPropagator riskPropagatorOne = new RiskPropagator(
                RiskPropagator.PROVIDERTYPE_IP,
                RiskPropagator.SERVICEPHASE_OPERATION,
                "umea",
                serviceId,
                RiskPropagator.GRAPHTYPE_IP_OPERATION_PHYSICAL_HOST_RISKLEVEL);
        riskPropagatorOne.setRiskValue(riskFinal[0]);
        RiskPropagator riskPropagatorTwo = new RiskPropagator(
                RiskPropagator.PROVIDERTYPE_IP,
                RiskPropagator.SERVICEPHASE_OPERATION,
                "umea",
                serviceId,
                RiskPropagator.GRAPHTYPE_IP_OPERATION_VIRTUAL_MACHINE_RISKLEVEL);
        riskPropagatorTwo.setRiskValue(riskFinal[1]);
        RiskPropagator riskPropagatorThree = new RiskPropagator(
                RiskPropagator.PROVIDERTYPE_IP,
                RiskPropagator.SERVICEPHASE_OPERATION,
                "umea",
                serviceId,
                RiskPropagator.GRAPHTYPE_IP_OPERATION_SLA_RISKLEVEL);
        riskPropagatorThree.setRiskValue(riskFinal[2]);
        RiskPropagator riskPropagatorFour = new RiskPropagator(
                RiskPropagator.PROVIDERTYPE_IP,
                RiskPropagator.SERVICEPHASE_OPERATION,
                "umea",
                serviceId,
                RiskPropagator.GRAPHTYPE_IP_OPERATION_TOTAL_RISKLEVEL);
        riskPropagatorFour.setRiskValue(riskFinal[3]);
        riskPropagatorOne.start();
        riskPropagatorTwo.start();
        riskPropagatorThree.start();
        riskPropagatorFour.start();
        Thread.sleep(5000);
           
        riskPropagatorOne.setRiskValue(riskFinal[0]);
         riskPropagatorTwo.setRiskValue(riskFinal[1]);
         riskPropagatorThree.setRiskValue(riskFinal[2]);
         riskPropagatorFour.setRiskValue(riskFinal[3]);
         
        riskPropagatorOne.kill();
        riskPropagatorTwo.kill();
        riskPropagatorThree.kill();
        riskPropagatorFour.kill();

        return riskFinal;
    }

    public double getPhy(String Phy) throws SQLException, Exception {

        double[] ret = getRisks(null, Phy, null);

        return ret[0];
    }

    public double getVm(String VMID) throws SQLException, Exception {

        double[] ret = getRisks(VMID, null, null);

        return ret[1];
    }

    public double getIp(String infrastructureID) throws SQLException, Exception {

        double[] ret = getRisks(null, null, null);

        return ret[2];
    }

    public double getSla(String ServiceID) throws SQLException, Exception {

        double[] ret = getRisks(null, null, ServiceID);

        return ret[3];

    }

    public int demoRisk(double[] phy1) {
        int risk = 0;
        double phyResult = 0;
        double impact = 0;

        if ((phy1[0] < 0.75) & (phy1[0] > 0.2)) {
            phyResult = 0.9;
        } else if ((phy1[0] > 0.75) & (phy1[0] < 0.85)) {
            phyResult = 0.7;
        } else if ((phy1[0] > 0.85) & (phy1[0] < 0.95)) {
            phyResult = 0.5;
        } else if ((phy1[0] > 0.95) & (phy1[0] < 0.97)) {
            phyResult = 0.3;
        } else {
            phyResult = 0.1;
        }
        impact = 0.9;

        phyResult = ((1 - phyResult) * (1 - impact));
        if (phyResult < 0.1) {
            risk = 1;
        }
        if (phyResult > 0.1 && phyResult < 0.25) {
            risk = 2;
        }
        if (phyResult > 0.25 && phyResult < 0.35) {
            risk = 3;
        }

        if (phyResult > 0.35 && phyResult < 0.5) {
            risk = 4;
        }
        if (phyResult > 0.5 && phyResult < 0.7) {
            risk = 5;
        }
        if (phyResult > 0.7 && phyResult < 0.8) {
            risk = 6;
        }
        if (phyResult > 0.8) {
            risk = 7;
        }


        return risk;
    }

    public static double[] newRiskFinal(double[] list) {
        double[] newList = new double[list.length + 1];
        for (int j = 0; j < list.length - 1; j++) {

            newList[j] = list[j];
        }
        return newList;
    }
}