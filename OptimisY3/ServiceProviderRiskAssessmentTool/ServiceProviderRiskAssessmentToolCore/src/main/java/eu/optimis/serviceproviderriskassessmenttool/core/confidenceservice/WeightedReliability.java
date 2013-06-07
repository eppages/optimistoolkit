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
package eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice;

import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.persistence.SLAObject;
import org.apache.log4j.Logger;

public class WeightedReliability {

    protected static Logger logger = Logger.getLogger(WeightedReliability.class);

    public static double computeConfidenceValue(SLAObject[] SLAs) throws Exception {
        int numberOfSlas = SLAs.length;
        System.out.println("number of SLAs" + numberOfSlas);
        double alpha = 0.1;
        int categorySize = 1000;
        double confidence = 0;
        double totalPredictedFails;
        double predictedVariance;
        double numFails;
        int remainder = numberOfSlas % categorySize;
        int numCats;
        double RPlus;
        double RPlusSigma;

        if (remainder == 0) {
            numCats = (int) ((numberOfSlas) / (categorySize));
        } else {
            numCats = (int) ((numberOfSlas - remainder) / (categorySize) + 1);
        }
        int catSize[] = new int[numCats];
        double weights[] = new double[numCats];

        if (remainder == 0) {
            for (int k = 0; k < numCats; k++) {
                catSize[k] = categorySize;
            }
        } else {
            for (int i = 0; i < numCats - 1; i++) {
                catSize[i] = categorySize;
            }
            catSize[numCats - 1] = remainder;
        }

        logger.info("Basic Information");
        logger.info("Count of SLAs: " + SLAs.length);
        logger.info("Reliability Information");
        logger.info("=======================");
        logger.info("numCats:" + numCats);

        for (int j = 0; j < numCats; j++) {
            weights[j] = (double) (numCats - j) * ((double) catSize[j] / (double) categorySize);
            logger.info("weight:" + weights[j]);
        }
        int start = numberOfSlas - 1;
        int finish;
        //logger.debug("weighted");
        //COMPUTE 1.R_i using basic rel. for each category
        //2. Store predicted fails for each category and predicted st. deviation for subsequent computation of R+

        double F[] = new double[numCats];
        double sigma[] = new double[numCats];
        double rel[] = new double[numCats];

        for (int l = 0; l < numCats; l++) {
            finish = start - catSize[l];
            totalPredictedFails = 0;
            predictedVariance = 0;
            numFails = 0;

            SLAObject currentCatItem;
            for (int u = start; u > finish; u--) {
                currentCatItem = SLAs[u];
                totalPredictedFails += currentCatItem.getOfferedPof();
                predictedVariance += currentCatItem.getOfferedPof() * (1 - currentCatItem.getOfferedPof());
//                logger.debug("Running Predicted Variance: " + predictedVariance);
//                logger.debug("Current Item Predicted Variance " + currentCatItem.getOfferedPof());
                if (currentCatItem.getViolated()) {
                    numFails++;
                }
            }

            double predictedStDev = Math.sqrt(predictedVariance);
            logger.info("variance: " + predictedVariance);
            rel[l] = (totalPredictedFails - numFails) / predictedStDev; //writing out results for this category
            logger.info("total predicted fail, numfails, predicted St dev: " + totalPredictedFails + " " + numFails + " " + predictedStDev);
            logger.info("relweighted:" + rel[l]);
            sigma[l] = predictedStDev; //writing out results for this category
            F[l] = totalPredictedFails; //writing out results for this category
            start = finish;
        }
        
        double weightsSum = 0;
        double weightsSquaredSum = 0;
        double wF_sig = 0;
        for (int m = 0; m < numCats; m++) {
            wF_sig += weights[m] * F[m] / sigma[m];
            weightsSum += weights[m];
            weightsSquaredSum += Math.pow(weights[m], 2);
            logger.debug("weights " + weights[m] + rel[m]);
            confidence += weights[m] * rel[m];
        }
        RPlus = alpha * wF_sig / weightsSum;
        RPlusSigma = Math.sqrt(weightsSquaredSum / Math.pow(weightsSum, 2));
        //logger.debug("weighted2" + confidence);
        confidence = confidence / weightsSum;
        //logger.debug("weighted3" + confidence);
        if (confidence < 0) {
            confidence = (confidence + RPlus) / RPlusSigma;
        } else {
            confidence = (confidence - RPlus) / RPlusSigma;
        }
        logger.info("weighted confidence: " + confidence);
        if (Double.isNaN(confidence)) {
            throw new Exception("Problem exists with provider data. The confidence cannot be computed.");
        }

        return confidence;
    }
}