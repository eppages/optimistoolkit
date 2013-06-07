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
package eu.optimis.serviceproviderriskassessmenttool.core.historicaldatabase;

public class HistoricalDatabase {

    int prov_id = 0;
    int sla_no = 0;
    int failed_sla_no = 0;
    double risk = 0.0;

    public HistoricalDatabase() {
    }

    public int getProvider_id() {
        return prov_id;
    }

    public void setProvider_id(int id) {
        prov_id = id;
    }

    public int getNo_slas() {
        return sla_no;
    }

    public void setNo_slas(int asla_no) {
        sla_no = asla_no;
    }

    public void setNo_failed_slas(int afailed_sla_no) {
        failed_sla_no = afailed_sla_no;
    }

    public void setRisk(double[] arisk) {
        //	risk=arisk;//copy array MK: Why is there an array?
    }
}
