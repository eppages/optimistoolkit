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

/**
 *
 * @author scsmj
 */
public class AssertReputability {

    private ProvidersType providersType;
    private double pastPerformanceRank;
    private double businessStabilityRank;
    private double securityRank;
    private double privacyRank;
    private double infrastructureRank;
    private double geographyRank;
    private double certandstdRank;

    public AssertReputability(ProvidersType providersType, double pastPerformanceRank, double businessStabilityRank, double securityRank, double privacyRank, double infrastructureRank, double geographyRank, double certandstdRank) {
        this.providersType = providersType;
        this.pastPerformanceRank = pastPerformanceRank;
        this.businessStabilityRank = businessStabilityRank;
        this.securityRank = securityRank;
        this.privacyRank = privacyRank;
        this.infrastructureRank = infrastructureRank;
        this.geographyRank = geographyRank;
        this.certandstdRank = certandstdRank;
    }

    public void setProvidersType(ProvidersType providersType) {
        this.providersType = providersType;
    }

    public void setPastPerformanceRank(double pastPerformanceRank) {
        this.pastPerformanceRank = pastPerformanceRank;
    }

    public void setBusinessStabilityRank(double businessStabilityRank) {
        this.businessStabilityRank = businessStabilityRank;
    }

    public void setSecurityRank(double securityRank) {
        this.securityRank = securityRank;
    }

    public void setPrivacyRank(double privacyRank) {
        this.privacyRank = privacyRank;
    }

    public void setInfrastructureRank(double infrastructureRank) {
        this.infrastructureRank = infrastructureRank;
    }

    public void setGeographyRank(double geographyRank) {
        this.geographyRank = geographyRank;
    }

    public void setCertandstdRank(double certandstdRank) {
        this.certandstdRank = certandstdRank;
    }

    public ProvidersType getProvidersType() {
        return this.providersType;
    }

    public double getPastPerformanceRank() {
        return this.pastPerformanceRank;
    }

    public double getBusinessStabilityRank() {
        return this.businessStabilityRank;
    }

    public double getSecurityRank() {
        return this.securityRank;
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
}