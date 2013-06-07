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

package eu.optimis.serviceproviderriskassessmenttool.core;

import eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.riskassessor.Reliability;
import eu.optimis.serviceproviderriskassessmenttool.core.confidenceservice.riskassessor.RiskAssessor;
import eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase.dao.populate.RiskPropagator;
import org.apache.log4j.Logger;

/**
 *
 * @author admin2
 */
public class OperationPhaseRiskAssessment extends Thread {

    protected static Logger log = Logger.getLogger(OperationPhaseRiskAssessment.class);
    private String serviceId;
    private boolean finish = false;
    private RiskPropagator riskPropagator = null;

    public OperationPhaseRiskAssessment(String serviceId) {
        this.serviceId = serviceId;
        try {
            riskPropagator = new RiskPropagator(RiskPropagator.PROVIDERTYPE_SP, RiskPropagator.SERVICEPHASE_OPERATION, getIPName(), getServiceId(), RiskPropagator.GRAPHTYPE_SP_OPERATION_SLA_RISKLEVEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAssessment(String serviceId) {
        try {
            if(riskPropagator !=null)
                riskPropagator.kill();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.finish = true;
    }

    public String getServiceId() {
        return this.serviceId;
    }
    
    public String getIPName() {
        return "atos";
    }
    
    public double getPoF(){
        return 0.3;
    }

    @Override
    public void run() {
        while (!finish) {
            try {

                log.info("SPRA: Starting OperationPhaseRiskAssessment (" + serviceId + ")");

                double tempdouble = 0.01;
                RiskAssessor newra = new RiskAssessor();
                try {
                    Reliability temp = newra.computeReliability(getIPName(), getPoF());
                    tempdouble = temp.getAdjustedPof();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int poflevel = convertPoFLevel(tempdouble);
                int impactlevel = calculateImpactLevelOfSLAOfferReliability();
                int risklevel = convertRiskLevel(poflevel * impactlevel);
                try {
                    riskPropagator.setRiskValue(risklevel);
                    if(!riskPropagator.isExecuting()){
                        riskPropagator.start();
                    }               
                       

                } catch (Exception e) {
                    e.printStackTrace();
                }

                log.info("SPRA: OperationPhaseRiskAssessment (" + serviceId + "). RiskLevel: " + risklevel);

                Thread.sleep(10000);
            } catch (Exception ex) {
                log.error("SPRA: OperationPhaseRiskAssessment error of service:  " + serviceId);
                ex.printStackTrace();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex2) {
                    log.error("ECO: Error while performing sleep.");
                    ex2.printStackTrace();
                }
            }
        }

    }
    
    private int calculateImpactLevelOfSLAOfferReliability() {
        return 5;
    }

    private int convertPoFLevel(double pof) {

        int poflevel = 0;

        if (pof <= 0.2) {
            poflevel = 1;
        } else if ((pof > 0.2) && (pof <= 0.4)) {
            poflevel = 2;
        } else if ((pof > 0.4) && (pof <= 0.6)) {
            poflevel = 3;
        } else if ((pof > 0.6) && (pof <= 0.8)) {
            poflevel = 4;
        } else if (pof > 0.8) {
            poflevel = 5;
        }
        return poflevel;
    }

    private int convertRiskLevel(int risklevel) {

        int normalisedRiskLevel = 1;

        if (risklevel <= 3) {
            normalisedRiskLevel = 1;
        } else if ((risklevel > 3) && (risklevel <= 7)) {
            normalisedRiskLevel = 2;
        } else if ((risklevel > 7) && (risklevel <= 13)) {
            normalisedRiskLevel = 3;
        } else if ((risklevel > 13) && (risklevel <= 17)) {
            normalisedRiskLevel = 4;
        } else if ((risklevel > 17) && (risklevel <= 21)) {
            normalisedRiskLevel = 5;
        } else if ((risklevel > 21) && (risklevel <= 23)) {
            normalisedRiskLevel = 6;
        } else if ((risklevel > 23) && (risklevel <= 25)) {
            normalisedRiskLevel = 7;
        }
        return normalisedRiskLevel;
    }
}
