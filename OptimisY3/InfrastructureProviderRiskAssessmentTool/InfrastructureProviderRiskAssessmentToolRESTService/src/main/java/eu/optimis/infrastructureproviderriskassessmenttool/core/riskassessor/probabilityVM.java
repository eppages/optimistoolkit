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

import org.apache.log4j.Logger;

public class probabilityVM {

    protected static Logger log = Logger.getLogger(probabilityVM.class);

    public static float[] calcProb(String[] phy, String[] vm) {

        int a = 0;


        float[] vmProb = setVMBands(vm);

        //	log.info("testing probs ------");

        while (a < vmProb.length) {
                log.info("probability for row " + a + " of vm = " + vmProb[a]);
            a++;
        }


        return vmProb;
    }

    public static float[] setVMBands(String[] vm) {
//log.info("parsing bands");
        float[] vmResult = new float[6];

        try {

            float vm1 = Float.parseFloat(vm[0]);
            float vm2 = Float.parseFloat(vm[1]);
            float vm3 = Float.parseFloat(vm[2]);
            float vm4 = Float.parseFloat(vm[6]);
            float vm5 = Float.parseFloat(vm[5]);
            //float vm6 = Float.parseFloat(vm[1]);

            if ((vm1 < 3000) & (vm1 > 100)) {
                vmResult[0] = (float) 0.9;
            } else if ((vm1 > 3000) & (vm1 < 5000)) {
                vmResult[0] = (float) 0.7;
            } else if ((vm1 > 5000) & (vm1 < 7000)) {
                vmResult[0] = (float) 0.5;
            } else if ((vm1 > 7000) & (vm1 < 900)) {
                vmResult[0] = (float) 0.3;
            } else {
                vmResult[0] = (float) 0.1;
            }

            if (vm2 == 1) {
                vmResult[1] = (float) 0.9;
            } else {
                vmResult[1] = (float) 0.7;
            }


            if (vm3 > 4000) {
                vmResult[2] = (float) 0.9;
            } else if ((vm3 < 4000) & (vm3 > 3000)) {
                vmResult[2] = (float) 0.7;
            } else if ((vm3 < 3000) & (vm3 > 2000)) {
                vmResult[2] = (float) 0.5;
            } else if ((vm3 < 2000) & (vm3 > 1000)) {
                vmResult[2] = (float) 0.3;
            } else {
                vmResult[2] = (float) 0.1;
            }


            if ((vm4 > 5) & (vm4 < 30)) {
                vmResult[3] = (float) 0.9;
            } else if ((vm4 > 30) & (vm4 < 80)) {
                vmResult[3] = (float) 0.7;
            } else if ((vm4 > 80) & (vm4 < 90)) {
                vmResult[3] = (float) 0.5;
            } else if ((vm4 > 90) & (vm4 < 95)) {
                vmResult[3] = (float) 0.3;
            } else {
                vmResult[3] = (float) 0.1;
            }


            if ((vm5 > 5) & (vm5 < 30)) {
                vmResult[4] = (float) 0.9;
            } else if ((vm5 > 30) & (vm5 < 80)) {
                vmResult[4] = (float) 0.7;
            } else if ((vm5 > 80) & (vm5 < 90)) {
                vmResult[4] = (float) 0.5;
            } else if ((vm5 > 90) & (vm5 < 95)) {
                vmResult[4] = (float) 0.3;
            } else {
                vmResult[4] = (float) 0.1;
            }


            vmResult[5] = (float) 0.9;
        } catch (Exception e) {
            log.error("problem with vm bands " + e);

        }

        return vmResult;

    }
}