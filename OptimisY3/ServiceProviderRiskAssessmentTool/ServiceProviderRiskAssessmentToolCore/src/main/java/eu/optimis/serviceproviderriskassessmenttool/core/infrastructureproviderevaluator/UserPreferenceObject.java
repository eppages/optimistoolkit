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

public class UserPreferenceObject {

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

    public double getGeoVpast() {
        return geoVpast;
    }

    public double getGeoVcertstd() {
        return geoVcertstd;
    }

    public double getGeoVbiz() {
        return geoVbiz;
    }

    public double getGeoVsec() {
        return geoVsec;
    }

    public double getGeoVinf() {
        return geoVinf;
    }

    public double getGeoVpriva() {
        return geoVpriva;
    }

    public double getPastVcertstd() {
        return pastVcertstd;
    }

    public double getPastVbiz() {
        return pastVbiz;
    }

    public double getPastVsec() {
        return pastVsec;
    }

    public double getPastVinf() {
        return pastVinf;
    }

    public double getPastVpriva() {
        return pastVpriva;
    }

    public double getCertstdVbiz() {
        return certstdVbiz;
    }

    public double getCertstdVsec() {
        return certstdVsec;
    }

    public double getCertstdVinf() {
        return certstdVinf;
    }

    public double getCertstdVpriva() {
        return certstdVpriva;
    }

    public double getBizVsec() {
        return bizVsec;
    }

    public double getBizVinf() {
        return bizVinf;
    }

    public double getBizVpriva() {
        return bizVpriva;
    }

    public double getSecVinf() {
        return secVinf;
    }

    public double getSecVpriva() {
        return secVpriva;
    }

    public double getPrivaVinf() {
        return privaVinf;
    }

    public void setGeoVpast(double geoVpast) throws Exception {
        if (geoVpast > 0.0) {
            this.geoVpast = geoVpast;
        } else {
            throw new Exception("geoVpast is not greater than zero");
        }
    }

    public void setGeoVcertstd(double geoVcertstd) throws Exception {
        if (geoVcertstd > 0.0) {
            this.geoVcertstd = geoVcertstd;
        } else {
            throw new Exception("geoVcertstd is not greater than zero");
        }
    }

    public void setGeoVbiz(double geoVbiz) throws Exception {
        if (geoVbiz > 0.0) {
            this.geoVbiz = geoVbiz;
        } else {
            throw new Exception("geoVbiz is not greater than zero");
        }
    }

    public void setGeoVsec(double geoVsec) throws Exception {
        if (geoVsec > 0.0) {
            this.geoVsec = geoVsec;
        } else {
            throw new Exception("geoVsec is not greater than zero");
        }
    }

    public void setGeoVinf(double geoVinf) throws Exception {
        if (geoVinf > 0.0) {
            this.geoVinf = geoVinf;
        } else {
            throw new Exception("geoVinf is not greater than zero");
        }
    }

    public void setGeoVpriva(double geoVpriva) throws Exception {
        if (geoVpriva > 0.0) {
            this.geoVpriva = geoVpriva;
        } else {
            throw new Exception("geoVpriva is not greater than zero");
        }
    }

    public void setPastVcertstd(double pastVcertstd) throws Exception {
        if (pastVcertstd > 0.0) {
            this.pastVcertstd = pastVcertstd;
        } else {
            throw new Exception("pastVcertstd is not greater than zero");
        }
    }

    public void setPastVbiz(double pastVbiz) throws Exception {
        if (pastVbiz > 0.0) {
            this.pastVbiz = pastVbiz;
        } else {
            throw new Exception("pastVbiz is not greater than zero");
        }
    }

    public void setPastVsec(double pastVsec) throws Exception {
        if (pastVsec > 0.0) {
            this.pastVsec = pastVsec;
        } else {
            throw new Exception("pastVsec is not greater than zero");
        }
    }

    public void setPastVinf(double pastVinf) throws Exception {
        if (pastVinf > 0.0) {
            this.pastVinf = pastVinf;
        } else {
            throw new Exception("pastVinf is not greater than zero");
        }
    }

    public void setPastVpriva(double pastVpriva) throws Exception {
        if (pastVpriva > 0.0) {
            this.pastVpriva = pastVpriva;
        } else {
            throw new Exception("pastVpriva is not greater than zero");
        }
    }

    public void setCertstdVbiz(double certstdVbiz) throws Exception {
        if (certstdVbiz > 0.0) {
            this.certstdVbiz = certstdVbiz;
        } else {
            throw new Exception("certstdVbiz is not greater than zero");
        }
    }

    public void setCertstdVsec(double certstdVsec) throws Exception {
        if (certstdVsec > 0.0) {
            this.certstdVsec = certstdVsec;
        } else {
            throw new Exception("certstdVsec is not greater than zero");
        }
    }

    public void setCertstdVinf(double certstdVinf) throws Exception {
        if (certstdVinf > 0.0) {
            this.certstdVinf = certstdVinf;
        } else {
            throw new Exception("certstdVinf is not greater than zero");
        }
    }

    public void setCertstdVpriva(double certstdVpriva) throws Exception {
        if (certstdVpriva > 0.0) {
            this.certstdVpriva = certstdVpriva;
        } else {
            throw new Exception("certstdVpriva is not greater than zero");
        }
    }

    public void setBizVsec(double bizVsec) throws Exception {
        if (bizVsec > 0.0) {
            this.bizVsec = bizVsec;
        } else {
            throw new Exception("bizVsec is not greater than zero");
        }
    }

    public void setBizVinf(double bizVinf) throws Exception {
        if (bizVinf > 0.0) {
            this.bizVinf = bizVinf;
        } else {
            throw new Exception("bizVinf is not greater than zero");
        }
    }

    public void setBizVpriva(double bizVpriva) throws Exception {
        if (bizVpriva > 0.0) {
            this.bizVpriva = bizVpriva;
        } else {
            throw new Exception("bizVpriva is not greater than zero");
        }
    }

    public void setSecVinf(double secVinf) throws Exception {
        if (secVinf > 0.0) {
            this.secVinf = secVinf;
        } else {
            throw new Exception("secVinf is not greater than zero");
        }
    }

    public void setSecVpriva(double secVpriva) throws Exception {
        if (secVpriva > 0.0) {
            this.secVinf = secVpriva;
        } else {
            throw new Exception("secVpriva is not greater than zero");
        }
    }

    public void setPrivaVinf(double privaVinf) throws Exception {
        if (privaVinf > 0.0) {
            this.privaVinf = privaVinf;
        } else {
            throw new Exception("infVpriva is not greater than zero");
        }
    }
}