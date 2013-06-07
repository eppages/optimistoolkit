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

public class Reliability {
    
    //Instance Variables
    private double confidenceValue;
    private int numSLAs;
    private double adjustedPof;

    //Constructor methods
    public Reliability(double confidenceValue, int numSLAs, double adjustedPof) {
        this.confidenceValue = confidenceValue;
        this.numSLAs = numSLAs;
        this.adjustedPof = adjustedPof;
    }

    public Reliability() {
    }

    //Accessors
    public double getConfidenceValue() {
        return confidenceValue;
    }

    public int getNumSLAs() {
        return numSLAs;
    }

    public double getAdjustedPof() {
        return adjustedPof;
    }

    //Mutators
    public void setConfidenceValue(double newConfidence) {
        confidenceValue = newConfidence;
    }

    public void setNumSLAs(int newNumSLAs) {
        numSLAs = newNumSLAs;
    }

    public void setAdjustedPof(double newAdjustedPof) {
        adjustedPof = newAdjustedPof;
    }
}