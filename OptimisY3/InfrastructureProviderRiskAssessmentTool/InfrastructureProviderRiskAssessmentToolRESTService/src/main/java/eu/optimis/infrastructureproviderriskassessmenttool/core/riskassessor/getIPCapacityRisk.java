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
import static eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor.riskEngine.log;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author scstki
 */
public class getIPCapacityRisk {

    public static double[] getRisks(String infrastructureID) throws SQLException, Exception {

        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("localhost", 8080);
        String serviceId = "6a0aff59-9e17-4b5f-978f-55d033f0a37f";

        if (co.getRunningServices().isEmpty()) {
            //do nowt
            log.info(" no services running using 6a0aff59-9e17-4b5f-978f-55d033f0a37f ");

        } else {
            List<?> serv = co.getRunningServices();

            serviceId = serv.get(0).toString();
        }
        log.info(" serviceID from CO =  " + serviceId);


        String[] phy = new String[11];
        String[] vm = new String[6];

        //get physical host levels              
        phy = physicalHost.getData("optimis1");
        //get virtual host data
        //   log.info("got phy vals now doing vm");
        vm = virtualHost.getData("9f0e4777-8cb1-47cb-a810-7e0b28201353");

        //calculate probabilities 0-10 by setting bands against value 
        float[] probVM = probabilityVM.calcProb(phy, vm);
        //   log.info("about to parse bands for phy");
        double[] probPhy = probabilityPhy.calcProb(phy, vm);
        //    log.info("about to write probs");
        //add the probabilities to the inventory
        writeProbs.write(probVM, probPhy);
        //   log.info("probs written");
        //now we get the risk values for each threat
        double[] risk = getRisk.calc();
        //   log.info("get risk done");
        //now we calculate the risk bands and group risk into vulnerabilites
        log.info("now doing vulnerab");
        double[] riskFinal = vulnerabilities.calc(risk);
        //    log.info("riskFinal done");
        int d = 0;
        while (d < riskFinal.length - 1) {
            log.info("riskfinal val " + d + " = " + riskFinal[d]);
            d++;
        }
        return riskFinal;
    }
}
