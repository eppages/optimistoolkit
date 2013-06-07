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

public class EndUserRankToPreference {

    public static UserPreferenceObject computePreferences(EndUserRankingObject ranks) throws Exception {

        double geoVpast = 1.0;
        double geoVcertstd = 1.0;
        double geoVbiz = 1.0;
        double geoVsec = 1.0;
        double geoVinf = 1.0;
        double geoVpriva = 1.0;

        double pastVcertstd = 1.0;
        double pastVbiz = 1.0;
        double pastVsec = 1.0;
        double pastVinf = 1.0;
        double pastVpriva = 1.0;

        double certstdVbiz = 1.0;
        double certstdVsec = 1.0;
        double certstdVinf = 1.0;
        double certstdVpriva = 1.0;

        double bizVsec = 1.0;
        double bizVinf = 1.0;
        double bizVpriva = 1.0;

        double secVinf = 1.0;
        double secVpriva = 1.0;

        double privaVinf = 1.0;

        UserPreferenceObject preferences = new UserPreferenceObject();

        double diff = 0;

        diff = ranks.getGeographyRank() - ranks.getPastPerformanceRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVpast = 9.0;
            } else if (diff >= 6.0) {
                geoVpast = 7.0;
            } else if (diff >= 4.0) {
                geoVpast = 5.0;
            } else if (diff >= 2.0) {
                geoVpast = 3.0;
            } else {
                geoVpast = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVpast = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVpast = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVpast = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVpast = 1.0 / 3.0;
            } else {
                geoVpast = 1.0;
            }
        }
        preferences.setGeoVpast(geoVpast);

        diff = ranks.getGeographyRank() - ranks.getCertandstdRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVcertstd = 9.0;
            } else if (diff >= 6.0) {
                geoVcertstd = 7.0;
            } else if (diff >= 4.0) {
                geoVcertstd = 5.0;
            } else if (diff >= 2.0) {
                geoVcertstd = 3.0;
            } else {
                geoVcertstd = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVcertstd = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVcertstd = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVcertstd = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVcertstd = 1.0 / 3.0;
            } else {
                geoVcertstd = 1.0;
            }
        }
        preferences.setGeoVcertstd(geoVcertstd);

        diff = ranks.getGeographyRank() - ranks.getBusinessStabilityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVbiz = 9.0;
            } else if (diff >= 6.0) {
                geoVbiz = 7.0;
            } else if (diff >= 4.0) {
                geoVbiz = 5.0;
            } else if (diff >= 2.0) {
                geoVbiz = 3.0;
            } else {
                geoVbiz = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVbiz = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVbiz = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVbiz = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVbiz = 1.0 / 3.0;
            } else {
                geoVbiz = 1.0;
            }
        }
        preferences.setGeoVbiz(geoVbiz);

        diff = ranks.getGeographyRank() - ranks.getSecurityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVsec = 9.0;
            } else if (diff >= 6.0) {
                geoVsec = 7.0;
            } else if (diff >= 4.0) {
                geoVsec = 5.0;
            } else if (diff >= 2.0) {
                geoVsec = 3.0;
            } else {
                geoVsec = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVsec = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVsec = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVsec = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVsec = 1.0 / 3.0;
            } else {
                geoVsec = 1.0;
            }
        }
        preferences.setPastVsec(geoVsec);

        diff = ranks.getGeographyRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVinf = 9.0;
            } else if (diff >= 6.0) {
                geoVinf = 7.0;
            } else if (diff >= 4.0) {
                geoVinf = 5.0;
            } else if (diff >= 2.0) {
                geoVinf = 3.0;
            } else {
                geoVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVinf = 1.0 / 3.0;
            } else {
                geoVinf = 1.0;
            }
        }
        preferences.setPastVinf(geoVinf);

        diff = ranks.getGeographyRank() - ranks.getPrivacyRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                geoVpriva = 9.0;
            } else if (diff >= 6.0) {
                geoVpriva = 7.0;
            } else if (diff >= 4.0) {
                geoVpriva = 5.0;
            } else if (diff >= 2.0) {
                geoVpriva = 3.0;
            } else {
                geoVpriva = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                geoVpriva = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                geoVpriva = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                geoVpriva = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                geoVpriva = 1.0 / 3.0;
            } else {
                geoVpriva = 1.0;
            }
        }
        preferences.setPastVpriva(geoVpriva);

        diff = ranks.getPastPerformanceRank() - ranks.getCertandstdRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                pastVcertstd = 9.0;
            } else if (diff >= 6.0) {
                pastVcertstd = 7.0;
            } else if (diff >= 4.0) {
                pastVcertstd = 5.0;
            } else if (diff >= 2.0) {
                pastVcertstd = 3.0;
            } else {
                pastVcertstd = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                pastVcertstd = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                pastVcertstd = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                pastVcertstd = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                pastVcertstd = 1.0 / 3.0;
            } else {
                pastVcertstd = 1.0;
            }
        }
        preferences.setPastVcertstd(pastVcertstd);

        diff = ranks.getPastPerformanceRank() - ranks.getSecurityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                pastVsec = 9.0;
            } else if (diff >= 6.0) {
                pastVsec = 7.0;
            } else if (diff >= 4.0) {
                pastVsec = 5.0;
            } else if (diff >= 2.0) {
                pastVsec = 3.0;
            } else {
                pastVsec = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                pastVsec = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                pastVsec = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                pastVsec = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                pastVsec = 1.0 / 3.0;
            } else {
                pastVsec = 1.0;
            }
        }
        preferences.setPastVsec(pastVsec);

        diff = ranks.getPastPerformanceRank() - ranks.getBusinessStabilityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                pastVbiz = 9.0;
            } else if (diff >= 6.0) {
                pastVbiz = 7.0;
            } else if (diff >= 4.0) {
                pastVbiz = 5.0;
            } else if (diff >= 2.0) {
                pastVbiz = 3.0;
            } else {
                pastVbiz = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                pastVbiz = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                pastVbiz = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                pastVbiz = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                pastVbiz = 1.0 / 3.0;
            } else {
                pastVbiz = 1.0;
            }
        }
        preferences.setPastVbiz(pastVbiz);

        diff = ranks.getPastPerformanceRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                pastVinf = 9.0;
            } else if (diff >= 6.0) {
                pastVinf = 7.0;
            } else if (diff >= 4.0) {
                pastVinf = 5.0;
            } else if (diff >= 2.0) {
                pastVinf = 3.0;
            } else {
                pastVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                pastVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                pastVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                pastVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                pastVinf = 1.0 / 3.0;
            } else {
                pastVinf = 1.0;
            }
        }
        preferences.setPastVinf(pastVinf);

        diff = ranks.getPastPerformanceRank() - ranks.getPrivacyRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                pastVpriva = 9.0;
            } else if (diff >= 6.0) {
                pastVpriva = 7.0;
            } else if (diff >= 4.0) {
                pastVpriva = 5.0;
            } else if (diff >= 2.0) {
                pastVpriva = 3.0;
            } else {
                pastVpriva = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                pastVpriva = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                pastVpriva = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                pastVpriva = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                pastVpriva = 1.0 / 3.0;
            } else {
                pastVpriva = 1.0;
            }
        }
        preferences.setPastVpriva(pastVpriva);


        diff = ranks.getCertandstdRank() - ranks.getBusinessStabilityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                certstdVbiz = 9.0;
            } else if (diff >= 6.0) {
                certstdVbiz = 7.0;
            } else if (diff >= 4.0) {
                certstdVbiz = 5.0;
            } else if (diff >= 2.0) {
                certstdVbiz = 3.0;
            } else {
                certstdVbiz = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                certstdVbiz = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                certstdVbiz = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                certstdVbiz = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                certstdVbiz = 1.0 / 3.0;
            } else {
                certstdVbiz = 1.0;
            }
        }
        preferences.setCertstdVbiz(certstdVbiz);

        diff = ranks.getCertandstdRank() - ranks.getSecurityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                certstdVsec = 9.0;
            } else if (diff >= 6.0) {
                certstdVsec = 7.0;
            } else if (diff >= 4.0) {
                certstdVsec = 5.0;
            } else if (diff >= 2.0) {
                certstdVsec = 3.0;
            } else {
                certstdVsec = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                certstdVsec = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                certstdVsec = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                certstdVsec = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                certstdVsec = 1.0 / 3.0;
            } else {
                certstdVsec = 1.0;
            }
        }
        preferences.setCertstdVsec(certstdVsec);

        diff = ranks.getCertandstdRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                certstdVinf = 9.0;
            } else if (diff >= 6.0) {
                certstdVinf = 7.0;
            } else if (diff >= 4.0) {
                certstdVinf = 5.0;
            } else if (diff >= 2.0) {
                certstdVinf = 3.0;
            } else {
                certstdVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                certstdVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                certstdVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                certstdVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                certstdVinf = 1.0 / 3.0;
            } else {
                certstdVinf = 1.0;
            }
        }
        preferences.setCertstdVinf(certstdVinf);

        diff = ranks.getCertandstdRank() - ranks.getPrivacyRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                certstdVpriva = 9.0;
            } else if (diff >= 6.0) {
                certstdVpriva = 7.0;
            } else if (diff >= 4.0) {
                certstdVpriva = 5.0;
            } else if (diff >= 2.0) {
                certstdVpriva = 3.0;
            } else {
                certstdVpriva = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                certstdVpriva = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                certstdVpriva = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                certstdVpriva = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                certstdVpriva = 1.0 / 3.0;
            } else {
                certstdVpriva = 1.0;
            }
        }
        preferences.setCertstdVpriva(certstdVpriva);

        diff = ranks.getBusinessStabilityRank() - ranks.getSecurityRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                bizVsec = 9.0;
            } else if (diff >= 6.0) {
                bizVsec = 7.0;
            } else if (diff >= 4.0) {
                bizVsec = 5.0;
            } else if (diff >= 2.0) {
                bizVsec = 3.0;
            } else {
                bizVsec = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                bizVsec = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                bizVsec = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                bizVsec = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                bizVsec = 1.0 / 3.0;
            } else {
                bizVsec = 1.0;
            }
        }
        preferences.setBizVsec(bizVsec);

        diff = ranks.getBusinessStabilityRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                bizVinf = 9.0;
            } else if (diff >= 6.0) {
                bizVinf = 7.0;
            } else if (diff >= 4.0) {
                bizVinf = 5.0;
            } else if (diff >= 2.0) {
                bizVinf = 3.0;
            } else {
                bizVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                bizVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                bizVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                bizVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                bizVinf = 1.0 / 3.0;
            } else {
                bizVinf = 1.0;
            }
        }
        preferences.setBizVinf(bizVinf);

        diff = ranks.getBusinessStabilityRank() - ranks.getPrivacyRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                bizVpriva = 9.0;
            } else if (diff >= 6.0) {
                bizVpriva = 7.0;
            } else if (diff >= 4.0) {
                bizVpriva = 5.0;
            } else if (diff >= 2.0) {
                bizVpriva = 3.0;
            } else {
                bizVpriva = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                bizVpriva = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                bizVpriva = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                bizVpriva = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                bizVpriva = 1.0 / 3.0;
            } else {
                bizVpriva = 1.0;
            }
        }
        preferences.setBizVpriva(bizVpriva);

        diff = ranks.getSecurityRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                secVinf = 9.0;
            } else if (diff >= 6.0) {
                secVinf = 7.0;
            } else if (diff >= 4.0) {
                secVinf = 5.0;
            } else if (diff >= 2.0) {
                secVinf = 3.0;
            } else {
                secVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                secVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                secVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                secVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                secVinf = 1.0 / 3.0;
            } else {
                secVinf = 1.0;
            }
        }
        preferences.setSecVinf(secVinf);

        diff = ranks.getSecurityRank() - ranks.getPrivacyRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                secVpriva = 9.0;
            } else if (diff >= 6.0) {
                secVpriva = 7.0;
            } else if (diff >= 4.0) {
                secVpriva = 5.0;
            } else if (diff >= 2.0) {
                secVpriva = 3.0;
            } else {
                secVpriva = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                secVpriva = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                secVpriva = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                secVpriva = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                secVpriva = 1.0 / 3.0;
            } else {
                secVpriva = 1.0;
            }
        }
        preferences.setSecVpriva(secVpriva);

        diff = ranks.getPrivacyRank() - ranks.getInfrastructureRank();
        if (diff >= 0.0) {
            if (diff >= 8.0) {
                privaVinf = 9.0;
            } else if (diff >= 6.0) {
                privaVinf = 7.0;
            } else if (diff >= 4.0) {
                privaVinf = 5.0;
            } else if (diff >= 2.0) {
                privaVinf = 3.0;
            } else {
                privaVinf = 1.0;
            }
        } else {
            diff = 1 - diff;
            if (diff >= 8.0) {
                privaVinf = 1.0 / 9.0;
            } else if (diff >= 6.0) {
                privaVinf = 1.0 / 7.0;
            } else if (diff >= 4.0) {
                privaVinf = 1.0 / 5.0;
            } else if (diff >= 2.0) {
                privaVinf = 1.0 / 3.0;
            } else {
                privaVinf = 1.0;
            }
        }
        preferences.setPrivaVinf(privaVinf);

        return preferences;
    }
}