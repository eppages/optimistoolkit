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

import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scstki
 */
public class runRisk extends Thread {
//       protected static final Logger log = Logger.getLogger(start.class);

    //  Thread riskRunner;
    Thread riskRunner;

    public String start(String serviceID, String option) throws SQLException, Exception {
        riskRunner = new runRisk();
        riskRunner.setName("riskRunnerThread");
        riskRunner.start();

        return (null);
    }

    @SuppressWarnings("deprecation") //FIXME
    public String stop(String serviceID, String option) throws SQLException, Exception {

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        int tid = 0;
        for (int i = 0; i < threadArray.length; i++) {

            System.out.println(" thread id = " + i + " thead name = " + threadArray[i].getName());
            if (threadArray[i].getName().contains("riskRunnerThread")) {
                tid = i;
            }
        }
        System.out.println("tid = " + tid);
        threadArray[tid].stop();
        System.out.println("has it stopped?");
        return (null);
    }

    public void run() {
//        double[] res = new double[100];
        int i = 0;
        riskEngine re = new riskEngine();
        while (i < 10000) {
            try {
                double res[] = re.getRisks(null, null, null);
                System.out.println(" result length " + res.length);
            } catch (SQLException ex) {
                Logger.getLogger(runRisk.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(runRisk.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }


    }
}
