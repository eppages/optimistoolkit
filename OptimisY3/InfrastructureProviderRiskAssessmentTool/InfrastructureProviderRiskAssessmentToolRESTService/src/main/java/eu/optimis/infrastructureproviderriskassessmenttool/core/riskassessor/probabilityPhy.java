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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.log4j.Logger;

public class probabilityPhy {

    protected static Logger log = Logger.getLogger(probabilityPhy.class);

    public static double[] calcProb(String[] phy, String[] vm) throws FileNotFoundException {

        int b = 0;
        //set bands for probability  
        double[] phyProb = setPhyBands(phy);

        return phyProb;
    }

    public static double[] setPhyBands(String[] phy) throws FileNotFoundException {
//here we go puting each value into its own float for comparison
        log.info(" CPU AVE LOAD LEVEL " + phy[0]);

        String value = phy[0].replace(" ", "");

        if (value.contains(",")) {
            value = value.substring(0, value.indexOf(','));
        }

        value = value.replaceAll("^\"|\"$", "");
        double[] phyResult = new double[10];
    //    log.info("parse1a val=" + value + "=ends");
        try {

            float phy1 = Float.parseFloat(value.trim());
          //  log.info("parse1c");
            float phy2 = Float.parseFloat(phy[1]);
          //  log.info("parse2");
            float phy3 = 1000000000;
           // log.info("parse3");
            float phy4 = Float.parseFloat(phy[3]);
          //  log.info("parse4");
        //    log.info("parse5");
            float phy10 = Float.parseFloat(phy[9]);
      //      log.info("in phy bands cpu ave load = " + phy1);
            //get specific risks for cpu
            float[] riskVal = riskVals();

            //go through each type to give probability value
            if (phy1 > riskVal[3]) {
                phyResult[0] = (float) 0.9;
            } else if ((phy1 > riskVal[2]) & (phy1 < riskVal[3])) {
                phyResult[0] = (float) 0.7;
            } else if ((phy1 > riskVal[1]) & (phy1 < riskVal[2])) {
                phyResult[0] = (float) 0.5;
            } else if ((phy1 > riskVal[0]) & (phy1 < riskVal[1])) {
                phyResult[0] = (float) 0.3;
            } else {
                phyResult[0] = (float) 0.1;
            }
//and again
            log.info("cpu ave load probability = " + phyResult[0]);

            if ((phy2 > 10) & (phy2 < 3000)) {
                phyResult[1] = (float) 0.9;
            } else if ((phy2 < 4000) & (phy2 > 3000)) {
                phyResult[1] = (float) 0.7;
            } else if ((phy2 < 5000) & (phy2 > 4000)) {
                phyResult[1] = (float) 0.5;
            } else if ((phy2 < 6000) & (phy2 > 4000)) {
                phyResult[1] = (float) 0.3;
            } else {
                phyResult[1] = (float) 0.1;
            }
//log.info("phy 2 = " + phyResult[1]);
//and again		
            if (phy3 > 400000) {
                phyResult[2] = (float) 0.9;
            } else if ((phy3 > 300000) & (phy3 < 400000)) {
                phyResult[2] = (float) 0.7;
            } else if ((phy3 > 200000) & (phy3 < 300000)) {
                phyResult[2] = (float) 0.5;
            } else if ((phy3 > 50000) & (phy3 < 200000)) {
                phyResult[2] = (float) 0.3;
            } else {
                phyResult[2] = (float) 0.1;
            }
//log.info("phy 3 = " + phyResult[2]);
//and again - there probabily is a nicer way of doing this and it would be nice to be dynamic based on standard deviation but for now and for demo purposes this should be enough
            if (phy4 > 400000) {
                phyResult[3] = (float) 0.9;
            } else if ((phy4 > 300000) & (phy4 < 400000)) {
                phyResult[3] = (float) 0.7;
            } else if ((phy4 > 200000) & (phy4 < 300000)) {
                phyResult[3] = (float) 0.5;
            } else if ((phy4 > 50000) & (phy4 < 200000)) {
                phyResult[3] = (float) 0.3;
            } else {
                phyResult[3] = (float) 0.1;
            }
//log.info("phy 4 = " + phyResult[3]);
//only one here
            phyResult[4] = (float) 0.9;
//and here
            phyResult[5] = (float) 0.9;

//and here down to 8 as these are the ones that cant be split to bands
            phyResult[6] = (float) 0.9;

            phyResult[7] = (float) 0.9;

            phyResult[8] = (float) 0.9;

            //              log.info("phys 5,6,7,8,9 = 0.9");
// back to bands for the last one		
            if ((phy10 > 5) & (phy10 < 30)) {
                phyResult[9] = (float) 0.9;
            } else if ((phy10 > 30) & (phy10 < 80)) {
                phyResult[9] = (float) 0.7;
            } else if ((phy10 > 80) & (phy10 < 90)) {
                phyResult[9] = (float) 0.5;
            } else if ((phy10 > 90) & (phy10 < 95)) {
                phyResult[9] = (float) 0.3;
            } else {
                phyResult[9] = (float) 0.1;
            }
//log.info("phy 10 = " + phyResult[9]);
// and were done for the physical host, phew
        } catch (Exception e) {
            log.info("error in phy bands" + e);
        }
        return phyResult;

    }

    public static float[] riskVals() throws FileNotFoundException {
        float[] risks = new float[5];
      //  log.info("before read of risk.conf");
        try {
            int i = 0;
            FileReader fileReader = new FileReader("/opt/optimis/risk.conf");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                char l1 = line.charAt(0);
                char l2 = line.charAt(1);
                String l3 = String.valueOf(l1);
                String l4 = String.valueOf(l2);
                line = new StringBuilder().append(l3).append(l4).toString();
                risks[i] = Float.parseFloat(line);
                log.info("risk band " + i + " = " + risks[i]);
                i++;
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("error in bands for phy " + e);
        }
        return risks;
    }
}