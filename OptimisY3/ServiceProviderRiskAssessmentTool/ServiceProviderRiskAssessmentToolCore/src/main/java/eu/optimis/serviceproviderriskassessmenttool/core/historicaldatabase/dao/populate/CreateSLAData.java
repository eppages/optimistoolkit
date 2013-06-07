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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate;

import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.persistence.SLAObject;
import eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator.DsAhpProviderObject;
import eu.optimis.trec.common.db.sp.model.Quotes;
import eu.optimis.trec.common.db.sp.model.Rejections;
import eu.optimis.trec.common.db.sp.model.Slas;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CreateSLAData {

    private static int VIOLATED = 1;
    private static int NOT_VIOLATED = 0;
    private static double PENALTY = 1000.0;
    private static long SIX_MONTHS = 15768000000L;
    /**
     * The default is to generate data for the last six months
     */
    private Timestamp startDataTime = new Timestamp(new Date().getTime() - SIX_MONTHS);
    private Timestamp endDataTime = new Timestamp(new Date().getTime());
    private int offerExpiaryTimeMin = 1000 * 60 * 60 * 6; //6 hours default
    private int offerExpiaryTimeMax = 1000 * 60 * 60 * 24; //1 day default
    private double offeredPof = 0.1;
    private double acceptanceProbability = 1.0;
    private double truePof = 0.05;
    private double price = 1000.0;
    /**
     * Adding the time between the arrival of the SLA and the quote been
     * generated 0 - 20 million milli seconds = 333.33 minutes or 5.5 hours by
     * default.
     */
    private int slaArrivalToQuotedTimeMaxVal = 20000000;
    private int quotedTimeMaxValToAcceptedTimeMaxVal = 1000000;

    private int maxSlaArrivalTime = 300; //5 hours;

    /**
     * This returns the date from which to start generating SLA data from
     *
     * @return the time data starts getting generated from
     */
    public Timestamp getStartDataTime() {
        return startDataTime;
    }

    /**
     * This sets the date from which to start generating SLA data from
     *
     * @param startGenerationTime the startGenerationTime to set
     */
    public void setStartDataTime(Timestamp startDataTime) {
        this.startDataTime = startDataTime;
    }

 
    /**
     * This gets the maximum arrival time in mins between service requests.
     * @return the maxSlaArrivalTime
     */
    public int getMaxSlaArrivalTime() {
        return maxSlaArrivalTime;
    }

    /**
     * This sets the maximum arrival time in mins between service requests.
     * @param maxSlaArrivalTime the maximum sla arrival time
     */
    public void setMaxSlaArrivalTime(int maxSlaArrivalTime) {
        this.maxSlaArrivalTime = maxSlaArrivalTime;
    }   
    
    /**
     * @return the slaArrivalToQuotedTimeMaxVal
     */
    public int getSlaArrivalToQuotedTimeMaxVal() {
        return slaArrivalToQuotedTimeMaxVal;
    }

    /**
     * @param slaArrivalToQuotedTimeMaxVal the slaArrivalToQuotedTimeMaxVal to set
     */
    public void setSlaArrivalToQuotedTimeMaxVal(int slaArrivalToQuotedTimeMaxVal) {
        this.slaArrivalToQuotedTimeMaxVal = slaArrivalToQuotedTimeMaxVal;
    }

    /**
     * @return the quotedTimeMaxValToAcceptedTimeMaxVal
     */
    public int getQuotedTimeMaxValToAcceptedTimeMaxVal() {
        return quotedTimeMaxValToAcceptedTimeMaxVal;
    }

    /**
     * @param quotedTimeMaxValToAcceptedTimeMaxVal the quotedTimeMaxValToAcceptedTimeMaxVal to set
     */
    public void setQuotedTimeMaxValToAcceptedTimeMaxVal(int quotedTimeMaxValToAcceptedTimeMaxVal) {
        this.quotedTimeMaxValToAcceptedTimeMaxVal = quotedTimeMaxValToAcceptedTimeMaxVal;
    }    

    /**
     * This returns the date from which no generated SLA data must pass
     *
     * @return the last date at which sla data should be set for
     */
    public Timestamp getEndDataTime() {
        return endDataTime;
    }

    /**
     * This sets the date from which no generated SLA data must pass
     *
     * @param endDataTime The last date at which sla data should be set for
     */
    public void setEndDataTime(Timestamp endDataTime) {
        this.endDataTime = endDataTime;
    }

    /**
     * gets the price to be set for all the SLAs generated
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price for all the SLAs generated
     *
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * This gets provider's offered probability of failure
     *
     * @return the offered Pof
     */
    public double getOfferedPof() {
        return offeredPof;
    }

    /**
     * The sets provider's offered probability of failure
     *
     * @param offeredPof the offered Pof to set
     */
    public void setOfferedPof(double offeredPof) {
        this.offeredPof = offeredPof;
    }

    /**
     * The gets the provider's true probability of failure
     *
     * @return the truePof
     */
    public double getTruePof() {
        return truePof;
    }

    /**
     * The sets the provider's true probability of failure
     *
     * @param truePof the truePof to set
     */
    public void setTruePof(double truePof) {
        this.truePof = truePof;
    }

    /**
     * The returns the provider's probability that it accepts or rejects the
     * offer
     *
     * @return the acceptanceProbability
     */
    public double getAcceptanceProbability() {
        return acceptanceProbability;
    }

    /**
     * This sets the provider's probability that it accepts a service request
     *
     * @param acceptanceProbability the acceptanceProbability to set
     */
    public void setAcceptanceProbability(double acceptanceProbability) {
        this.acceptanceProbability = acceptanceProbability;
    }

    /**
     *
     * @param provider_id The provider id to generate the data for
     * @param provider_dn
     * @param agreementFactoryURL The url used for the agreement factory for the
     * SLA
     * @param numberSLAs The number of SLAs to create
     */
    public boolean addDataToDatabase(int provider_id, String provider_dn, String agreementFactoryURL,
            int numberSLAs) {

        Random rndGenerator = new Random();
        /*
         * Start the data generation phase from the startGenerationTime.
         * The default is 6 months before now
         */
        Timestamp setPoint = (Timestamp) startDataTime.clone();

        Session session = eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.sp.utils.HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {

            for (int i = 0; i < numberSLAs; i++) {
                session.flush();

                //generate the violated status using a random number generator.
                boolean slaViolated = false;
                double slaSuccessFactor = rndGenerator.nextDouble(); //0.0 -> 0.99999
                if (slaSuccessFactor < truePof) {
                    slaViolated = true;
                }
                //Create an SLA Object (A Summary Object of the SLA)
                Timestamp slaArrivalTime = (Timestamp) setPoint.clone();
                SLAObject currentSLA = new SLAObject(offeredPof, slaViolated, slaArrivalTime);

                /**
                 * Generate the SLA Quote object
                 */
                Quotes newQuote = new Quotes();

                UUID uuidAct = UUID.randomUUID();
                String uuid = uuidAct.toString();
                newQuote.setUuid(uuid); 
                newQuote.setProviderDn(provider_dn);

                /**
                 * Adding the time between the arrival of the SLA and the quote
                 * been generated 0 - 20 million milli seconds = 333.33 minutes
                 * or 5.5 hours by default.
                 */
                int slaArrivalToQuoteTime = rndGenerator.nextInt(slaArrivalToQuotedTimeMaxVal);
                /**
                 * setPoint = CurrentSLA time + slaArrivalToQuoteTime
                 */
                setPoint = new Timestamp(currentSLA.getStartTime().getTime() + (long) slaArrivalToQuoteTime);
                Timestamp timeQuoted = (Timestamp) setPoint.clone();
                newQuote.setTimeQuoted(timeQuoted);
                //newQuote.setSlaId(i); //Primary Key

                session.save(newQuote);
                /**
                 * Adding the time between the arrival of the SLA and the quote
                 * been generated 0 - 10 million milli seconds = 166.667 minutes
                 * or 2.77 hours by default
                 *
                 * setPoint = CurrentSLA time + slaArrivalToQuoteTime
                 * timeCommitted = CurrentSLA time + 0...5.5 hours + 0...2.77
                 * hours
                 */
                int slaQuoteToAcceptTime = rndGenerator.nextInt(quotedTimeMaxValToAcceptedTimeMaxVal);
                setPoint = new Timestamp(setPoint.getTime() + (long) slaQuoteToAcceptTime);
                Timestamp timeCommitted = (Timestamp) setPoint.clone();

                // A Past the current time test!!! i.e. timeCommitted.after(now)
                if (getEndDataTime().before(timeCommitted)) {
                    break;
                }

                /**
                 * move forward to either the SLA accept or reject stage!!
                 */
                /**
                 * Perform the accept or reject test!!!!
                 */
                boolean slaRejected = true;
                double slaRejectionFactor = rndGenerator.nextDouble(); //0.0 -> 0.99999
                if (slaRejectionFactor <= acceptanceProbability) {
                    slaRejected = false;
                }
                if (slaRejected) {
                    /**
                     * Create an SLA and place it in the rejected list in the
                     * database
                     */
                    Rejections newRejection = new Rejections();
                    newRejection.setProviderDn(provider_dn);
                    newRejection.setTimeRejected(timeCommitted);
                    newRejection.setUuid(uuid);
                    newRejection.setProviderId(provider_id);
                    //newRejection.setSlaId(i); //Primary Key
                    session.save(newRejection); //Save the rejection of an SLA into the DB
                } else {
                    /**
                     * Create an SLA and place it in the database
                     */
                    Slas newOffer = new Slas();
                    newOffer.setUuid(uuid);
                    newOffer.setOfferTime(timeCommitted);
                    newOffer.setProviderDn(provider_dn);
                    //System.out.println("hhh"+provider_dn);

                    newOffer.setState(NOT_VIOLATED);
                    if (currentSLA.getViolated()) {
                        newOffer.setState(VIOLATED);
                    }
                    //System.out.println("offeredpof"+currentSLA.getOfferedPof());
                    /*
                     * Note: in OPTIMIS Risk = pof * impact
                     * in this case however it is stored as the offered pof
                     */
                    newOffer.setRisk(currentSLA.getOfferedPof());

                    int offerDuration = offerExpiaryTimeMin + rndGenerator.nextInt(offerExpiaryTimeMax - offerExpiaryTimeMin);
                    Timestamp offerExpiryTime = new Timestamp(timeCommitted.getTime() + (long) offerDuration);
                    newOffer.setExpirationTime(offerExpiryTime); //offer ends at current time

                    newOffer.setProviderId(provider_id);
                    newOffer.setPenalty(PENALTY); //Fixed Penalty (This value isn't used in OPTIMIS
                    newOffer.setPrice(price); //Fixed Price
                    //newOffer.setSlaId(i); //Primary Key                   
                    session.save(newOffer); //Save the accepted SLA into the DB
                }
                int slaInterArrivalTime = rndGenerator.nextInt(maxSlaArrivalTime);
                setPoint = new Timestamp(currentSLA.getStartTime().getTime() + (long) (slaInterArrivalTime * 60000));
            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            return false; //unsuccesful so returning false.
        }
        return true; //succesful so returning true.
    }

    public boolean addDSAHPProviderDataToDatabase(int numEntries) {


        Session session = eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.sp.utils.HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Random generateCriteria = new Random();

        double meanPastPerf = 0.97;
        double sigmaPastPerf = 0.0015;
        try {

            for (int i = 0; i < numEntries; i++) {

                DsAhpProviderObject newObject = new DsAhpProviderObject();
                newObject.setDistName("/C=UK/O=eScience/OU=Leeds/L=ISS/CN=host/fakeprovider." + i);
                newObject.setPastPerf(meanPastPerf + sigmaPastPerf * generateCriteria.nextGaussian());
                if (generateCriteria.nextDouble() < 0.5) {
                    newObject.setSecurity(0.0);
                } else {
                    newObject.setSecurity(1.0);
                }
                session.save(newObject);
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            return false;
        }

        session.close();

        return true;

    }
}