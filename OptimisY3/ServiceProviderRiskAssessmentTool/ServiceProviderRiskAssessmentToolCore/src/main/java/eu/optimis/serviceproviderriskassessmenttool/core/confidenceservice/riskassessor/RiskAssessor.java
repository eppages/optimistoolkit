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
package eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.riskassessor;

import eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.*;
import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.*;
import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;

public class RiskAssessor {

    private static double CONFIDENCE_THRESHOLD = 1.5;
    protected static Logger logger = Logger.getLogger(RiskAssessor.class);

    /**
     *
     */
    public RiskAssessor() {
    }

    /**
     * This provides the reliability assessment of a provider's offer in terms
     * of its PoF.
     *
     * @param providername The provider to produce the reliability information for.
     * @param offeredPof The PoF offered by the provider in the SLA
     * @return The reliability object describing the provider PoF estimate.
     * reliability
     * @throws Exception Thrown when errors are found in the historic record of SLAs
     * has errors in it or when the database cannot be reached.
     */
    public Reliability computeReliability(String providername, double offeredPof) throws Exception {

        SLAObject[] slas = null;
        logger.info("Computing the Reliability");
        try {
            // Use TRECCommonDBSP to conneect OPTIMIS DB
            slas = QueryDataSLAComDBSP.getDataComDBSP(providername);
        } catch (Exception e) {
            logger.error("Error detected in query sla data", e);
            throw new Exception(e.getMessage());
        }
        logger.info(slas.length + "SLAs are being used for computing the reliability.");
        //Sort the SLAObject array by time; earliest first
        Arrays.sort(slas, new Comparator<SLAObject>() {
            @Override
            public int compare(SLAObject o1, SLAObject o2) {
                //System.out.println("return"+o1.getStartTime().compareTo(o2.getStartTime()));
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

        double roundedConfidenceInPastSlas, roundedIpPoF;

        try {
            double confidenceInPastSlas = WeightedReliability.computeConfidenceValue(slas);
            roundedConfidenceInPastSlas = roundDecimal(confidenceInPastSlas, 5);
            logger.info("The confidence for the PoF in the historical record is: " + confidenceInPastSlas);

        } catch (Exception e) {
            logger.error("There was an error computing the reliability of the providers PoF estimate", e);
            throw new Exception(e.getMessage());

        }

        try {
            double ipPoF = computeRisk(slas, roundedConfidenceInPastSlas, offeredPof);
            roundedIpPoF = roundDecimal(ipPoF, 5);
            logger.info("The PoF for the IP is: " + roundedIpPoF);
        } catch (Exception e) {
            logger.error("There was an error computing the adjusted of the providers PoF estimate", e);
            throw new Exception(e.getMessage());
        }
        return new Reliability(roundedConfidenceInPastSlas, slas.length, roundedIpPoF);
    }

    /**
     * This calculates the adjusted PoF for accepting an SLA from a provider 
     * for a given PoF that the provider is claiming.
     *
     * @param slas The historic slas to base the risk assessment upon
     * @param confidence The confidence for the historical record of failures. i.e.
     * double confidenceValue = WeightedReliability.computeConfidenceValue(slas);
     * @param offeredPoF The offered pof from the provider as part of the service 
     * deployment
     * @return The risk adjusted pof for accepting the SLA
     */
    public double computeRisk(SLAObject[] slas, double confidence, double offeredPoF) {

        double computedPoF;
        /**
         * The default case for PoF is the offered PoF. If the confidence in the
         * historic data is too low the offered PoF takes precedence.
         *
         * The confidence threshold = 1.5 which represents the 95% confidence
         * interval see page: 1567 of the paper: "Brokering of Risk-Aware
         * Service Level Agreements in Grids"
         */
        if (Math.abs(confidence) <= CONFIDENCE_THRESHOLD) {
            logger.info("Confidence too low using the offered pof (" + confidence + ")" );
            return offeredPoF;
        }

        int numCats;
        int categorySize = 1000;
        double computedDelta;
        int remainder = slas.length % categorySize;

        /**
         * When calculating the risk the slas are placed into categories/groups, 
         * based upon the sequence of arrivals of the slas. The size of the groups
         * are equal apart from the last one which contains the remaining SLAs.
         * calculate how many categories are needed.
         */
        if (remainder == 0) {
            numCats = slas.length / categorySize;
        } else {
            numCats = (slas.length - remainder) / categorySize + 1;
        }

        int[] catSize = new int[numCats];
        double[] catWeight = new double[numCats];

        /**
         * Set the category sizes, ensure the last category size deals with the
         * remainder.
         */
        for (int i = 0; i < numCats - 1; i++) {
            catSize[i] = categorySize;
        }
        if (remainder == 0) {
            catSize[numCats - 1] = categorySize;
        } else {
            catSize[numCats - 1] = remainder;
        }

        /**
         * Set the weights for each of the categories. Also adjusting the last
         * categories weight, which houses the remaining SLAs.
         */
        for (int j = 0; j < numCats; j++) {
            if (catSize[j] == categorySize) {
                catWeight[j] = (double) (numCats - j);
            } else {
                catWeight[j] = (double) (catSize[j]) * (double) (numCats - j) / (double) categorySize;
            }
        }

        int finishSLA; //used for iterating over the SLAs
        //used as a test to see if the offered PoF is in proximity to the current SLA's offered PoF
        double interval = 0.8;

        //used as a temporary store for calculating PoF for a given category i.e. PoF[]
        int numFailsCat;
        int numSLAsCat;

        //for each category/grouping
        double[] PoF = new double[numCats]; //actual pof
        double[] avgOffered = new double[numCats]; //offered pof
        double[] delta = new double[numCats]; //difference between actual and offered

        double totalSLAs = 0; //total slas across all categories (meeting the proximity criteria)
        double slaThreshold = 100; // if total SLAs below this count then not enough evidence has been gathered

        int startSLA = slas.length - 1;

        //for each category
        for (int currentCat = 0; currentCat < numCats; currentCat++) {

            finishSLA = startSLA - catSize[currentCat];
            numSLAsCat = 0; //SLAs in category meeting proximity test criteria
            numFailsCat = 0; //SLAs in category meeting proximity test criteria (and failing to meet the SLA)
            avgOffered[currentCat] = 0;

            //for each member of the category
            for (int catMember = startSLA; catMember > finishSLA; catMember--) {

                if (slas[catMember].getOfferedPof() < offeredPoF * (1 + interval)
                        && slas[catMember].getOfferedPof() > offeredPoF * (1 - interval)) {

                    numSLAsCat++; //SLAs meeting criteria
                    if (slas[catMember].getViolated()) {
                        numFailsCat++; //SLAs meeting selection criteria (but were violated)
                    }
                    //sum of offeredPoF then divide by count meeting criteria later to get average
                    avgOffered[currentCat] += slas[catMember].getOfferedPof();
                }
            }

            if (numSLAsCat != 0) { //if at least one SLA met the criteria in the category
                PoF[currentCat] = ((double) numFailsCat) / ((double) numSLAsCat);
                logger.info("Current Cat numFailsCat" + numFailsCat + " numSLAsCat " + numSLAsCat);
                totalSLAs += numSLAsCat;
                avgOffered[currentCat] = avgOffered[currentCat] / (double) numSLAsCat;
                delta[currentCat] = (PoF[currentCat] / avgOffered[currentCat]) - 1.0;
                logger.info("Pof Current Cat " + PoF[currentCat] + " Avg Offered " + avgOffered[currentCat]);
            } else {
                PoF[currentCat] = 0;
                catWeight[currentCat] = 0;
                delta[currentCat] = 0;

            }
            startSLA = finishSLA;
        }

        // if total SLAs is below the threshold then not enough evidence has been gathered
        if (totalSLAs < slaThreshold) {
            logger.info("Not enough data to give assessment: (SLA Count = " + totalSLAs + ")");
            return offeredPoF;
        }

        double weightSum = 0;
        double deltaSum = 0;

        for (int m = 0; m < numCats; m++) {
            weightSum += catWeight[m];
            deltaSum += delta[m] * catWeight[m];
            //logger.debug("Weight " + catWeight[m] + " Delta " + delta[m]);
        }
        //logger.debug("Weight Total " + weightSum + " Delta Total " + deltaSum);

        computedDelta = deltaSum / weightSum;
        computedPoF = offeredPoF * (1 + computedDelta);

        //logger.debug("A computed pof" + computedPoF + " for an offered pof of " + offeredPoF + "was given.");
        return computedPoF;
    }

    /**
     * Adjusts the pof value for a offer, if the confidence is sufficiently high.
     * @param offeredPof The offered PoF
     * @param numFails The number of SLA failures
     * @param numSLAs The overall number of SLAs
     * @param confidence The confidence value
     * @return The probability of failure, adjusted if confidence is high
     * enough, to the fraction of failures across all SLAs
     */
    public double adjustRisk(double offeredPof, int numFails, int numSLAs, double confidence) {

        double adjPof = offeredPof;
        if (Math.abs(confidence) > CONFIDENCE_THRESHOLD) {
            adjPof = (double) numFails / numSLAs;
        }
        return adjPof;
    }

    /**
     * This rounds a double value to a specified number of decimal places. This
     * rounds using half up rounding.
     *
     * @param toRound The number to round
     * @param decimalPlace The precision to round to
     * @return The rounded number, using half up rounding
     */
    private double roundDecimal(double toRound, int decimalPlace) {
        BigDecimal bd = new BigDecimal(toRound);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * A basic test case for the risk assessor
     * @param args 
     */
    public static void main(String[] args) {
        String HOST = "https://grid-demo-6.cit.tu-berlin.de:8443/wsrf/services/";
        double meanTruePof = 0.001;
        RiskAssessor newra = new RiskAssessor();

        try {
            newra.computeReliability(HOST, meanTruePof);
        } catch (Exception e) {
            logger.error("An exception has been thrown during testing.", e);
        }
    }
}