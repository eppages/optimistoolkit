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
package eu.optimis.infrastructureproviderriskassessmenttool.core.serviceproviderevaluator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dsahpproviders")
public class DsAhpProviderObject implements Cloneable {

    /**
     * Specification of a DsAhpProviderObject containing provider name, criteria
     * values as well as belief and plausibility for ranking by DS/AHP.
     *
     * @author Iain Gourlay
     *
     */
    public Object clone() {

        try {

            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }


    }
    @Id
    @Column(name = "distName")
    private String distName;
    @Column(name = "pastPerf")
    private double pastPerf = 1000.0;
    @Column(name = "businessStability")
    private double businessStability = 1000.0;
    @Column(name = "security")
    private double security = 1000.0;
    @Column(name = "rating")
    private double rating;
    @Column(name = "belief")
    private double belief;
    @Column(name = "plausibility")
    private double plausibility;
    @Column(name = "t95")
    private int T_95;
    @Column(name = "t50")
    private int T_50;

    /**
     * Accessor for the time interval between receiving quote and making offer
     * such that the probability of acceptance is less than 0.95
     *
     * @return T_95 int a time in seconds.
     */
    public int getT_95() {
        return T_95;
    }

    public void setT_95(int t_95) {
        T_95 = t_95;
    }

    /**
     * Accessor for the time interval between receiving quote and making offer
     * such that the probability of acceptance is less than 0.50
     *
     * @return T_50 int a time in seconds.
     */
    public int getT_50() {
        return T_50;
    }

    public void setT_50(int t_50) {
        T_50 = t_50;
    }

    /**
     * Accessor for the provider's businessStability criterion value.
     *
     * @return customer a double between 0 and 1.
     */
    public double getBusinessStability() {
        return businessStability;
    }

    public void setBusinessStability(double businessStability) {
        if (businessStability >= 0.0 && businessStability <= 1.0) {
            this.businessStability = businessStability;
        } else {
            this.businessStability = 1000.0;
        }
    }

    /**
     * Accessor for the provider's distinguished name.
     *
     * @return distName the provider's distinguished name represented as a
     * string.
     */
    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

    /**
     * Accessor for the provider's past performance criterion value.
     *
     * @return past peformance a double between 0 and 1.
     */
    public double getPastPerf() {
        return pastPerf;
    }

    public void setPastPerf(double pastPerf) {
        if (pastPerf >= 0.0 && pastPerf <= 1.0) {
            this.pastPerf = pastPerf;
        } else {
            this.pastPerf = 1000.0;
        }
    }

    /**
     * Accessor for the provider's security criterion value.
     *
     * @return security a double between 0 and 1.
     */
    public double getSecurity() {
        return security;
    }

    public void setSecurity(double security) {
        if (security >= 0.0 && security <= 1.0) {
            this.security = security;
        } else {
            this.security = 1000.0;
        }

    }

    /**
     * Accessor for the belief in the proposition that this provider is the best
     * choice.
     *
     * @return belief a double between 0 and 1.
     */
    public double getBelief() {
        return belief;
    }

    public void setBelief(double belief) {
        this.belief = belief;
    }

    /**
     * Accessor for the plausibility of the proposition that this provider is
     * the best choice.
     *
     * @return plausibility a double between 0 and 1.
     */
    public double getPlausibility() {
        return plausibility;
    }

    public void setPlausibility(double plausibility) {
        this.plausibility = plausibility;
    }

    /**
     * Accessor for the overall rating of this provider. This is a double
     * between 0 and 1, representing the proportion of provoders (from a large
     * sample) that this provider is preferred to, based on the end-user
     * preferences.
     *
     * @return rating a double between 0 and 1.
     */
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
