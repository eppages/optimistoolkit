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
package eu.optimis.serviceproviderriskassessmenttool.core.infrastructureproviderevaluator;

public class EndUserRankingObject {

    private double pastPerformanceRank = 0.0;
    private double businessStabilityRank = 0.0;
    private double securityRank = 0.0;
    private double privacyRank = 0.0;
    private double infrastructureRank = 0.0;
    private double geographyRank = 0.0;
    private double certandstdRank = 0.0;

    /**
     * Constructor which takes the assertReputability interface request object.
     */
    public EndUserRankingObject(AssertReputability request) {

        this.pastPerformanceRank = request.getPastPerformanceRank();
        this.businessStabilityRank = request.getBusinessStabilityRank();
        this.securityRank = request.getSecurityRank();
        this.privacyRank = request.getPrivacyRank();
        this.infrastructureRank = request.getInfrastructureRank();
        this.geographyRank = request.getGeographyRank();
        this.securityRank = request.getCertandstdRank();
    }

    public EndUserRankingObject() {
    }

    public double getPastPerformanceRank() {
        return pastPerformanceRank;
    }

    public double getBusinessStabilityRank() {
        return businessStabilityRank;
    }

    public double getSecurityRank() {
        return securityRank;
    }

    public double getPrivacyRank() {
        return this.privacyRank;
    }

    public double getInfrastructureRank() {
        return this.infrastructureRank;
    }

    public double getGeographyRank() {
        return this.geographyRank;
    }

    public double getCertandstdRank() {
        return this.certandstdRank;
    }

    public void setPastPerformanceRank(double pastPerformanceRank) throws Exception {
        if (pastPerformanceRank >= 0.0 && pastPerformanceRank <= 10.0) {
            this.pastPerformanceRank = pastPerformanceRank;
        } else {
            throw new Exception("pastPerformanceRank not between 0 and 10");
        }
    }

    public void setBusinessStabilityRank(double businessStabilityRank) throws Exception {
        if (businessStabilityRank >= 0.0 && businessStabilityRank <= 10.0) {
            this.businessStabilityRank = businessStabilityRank;
        } else {
            throw new Exception("businessStabilityRank not between 0 and 10");
        }
    }

    public void setSecurityRank(double securityRank) throws Exception {
        if (securityRank >= 0.0 && securityRank <= 10.0) {
            this.securityRank = securityRank;
        } else {
            throw new Exception("securityRank not between 0 and 10");
        }
    }

    public void setPrivacyRank(double privacyRank) throws Exception {
        if (privacyRank >= 0.0 && privacyRank <= 10.0) {
            this.privacyRank = privacyRank;
        } else {
            throw new Exception("privacyRank not between 0 and 10");
        }
    }

    public void setInfrastructureRank(double infrastructureRank) throws Exception {
        if (infrastructureRank >= 0.0 && infrastructureRank <= 10.0) {
            this.infrastructureRank = infrastructureRank;
        } else {
            throw new Exception("infrastructureRank not between 0 and 10");
        }
    }

    public void setGeographyRank(double geographyRank) throws Exception {
        if (geographyRank >= 0.0 && geographyRank <= 10.0) {
            this.geographyRank = geographyRank;
        } else {
            throw new Exception("geographyRank not between 0 and 10");
        }
    }

    public void setCertandstdRank(double certandstdRank) throws Exception {
        if (certandstdRank >= 0.0 && certandstdRank <= 10.0) {
            this.certandstdRank = certandstdRank;
        } else {
            throw new Exception("certandstdRank not between 0 and 10");
        }
    }
}