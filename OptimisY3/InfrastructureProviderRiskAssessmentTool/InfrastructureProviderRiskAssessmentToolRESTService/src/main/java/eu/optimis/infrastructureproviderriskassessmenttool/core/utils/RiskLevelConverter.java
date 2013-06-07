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

package eu.optimis.infrastructureproviderriskassessmenttool.core.utils;

/**
 *
 * @author scsmj
 */
public class RiskLevelConverter {
    
    public static int convertPoFLevel(double pof) {

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

    public static int convertRiskLevel(int risklevel) {

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
