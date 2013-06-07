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

package eu.optimis.infrastructureproviderriskassessmenttool.core.riskassessor;

import eu.optimis.infrastructureproviderriskassessmenttool.core.InfrastructureProviderRiskAssessmentServer;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * Author scstki
 *
 *
 */
public class forcastVMMigKnown {

    protected static Logger log = Logger.getLogger(InfrastructureProviderRiskAssessmentServer.class);

    public static double[] calc(String replicationFactor, String VMID, String destNode)
            throws ServletException, IOException, SQLException {
        log.info("in post");

        /*not enough monitoring info to determine individual VM and 
         / how they are deployed. Therefore we assume if the replication 
         * is less than 2 the impact is 0.9. 
         * Otherwise it is as stated in the inventory.
         * We dont know the physical host destination so we take the average risk for 
         * physical host
         */
        //get normal risk calculation
        double[] risk2 = getRisk.calc();
        double[] riskFinal = vulnerabilities.calc(risk2);


        return riskFinal;
    }

    public static int[] recalcRisk() throws SQLException {

        float[] newRisk = new float[1];
        int[] vmRisk = new int[1];
        String url = "jdbc:mysql://kirkhac2.miniserver.com/optimis";
        String user = "root";
        String password = "";

        ResultSet rs = null;
        Statement st = null;
        Connection con = null;

        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();

        rs = st.executeQuery("SELECT threatID, likelyhood FROM `threat` where threatID > 11;");

        int t = 0;
        float[] res = new float[5];
        float[] res2 = new float[5];
        float[] res3 = new float[5];

        while (rs.next()) {
            res[t] = rs.getFloat(1);
            res2[t] = rs.getFloat(2);
            log.info("id = " + res[t]);
            log.info("prob = " + res2[t]);
            log.info("risk = " + res2[t] * 0.9);
            t++;
        }
        newRisk[0] = 1 - ((1 - res3[0]) * (1 - res3[1]) * (1 - res3[2]) * (1 - res3[3]) * (1 - res3[4]) * (1 - res3[5]));
        vmRisk = setBands(newRisk, 1);

        return vmRisk;
    }

    public static int[] getRisk() throws SQLException {

        String url = "jdbc:mysql://kirkhac2.miniserver.com/optimis";
        String user = "root";
        String password = "";

        ResultSet rs = null;
        Statement st = null;
        Connection con = null;

        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();

        rs = st.executeQuery("SELECT * FROM `vulnerability` ORDER BY id DESC LIMIT 6;");
        int t = 0;
        float[] res = new float[6];

        while (rs.next()) {
            res[t] = rs.getFloat(4);
            t++;
        }

        float[] vals = new float[4];
        vals[0] = 1 - ((1 - res[5]) * (1 - res[4]) * (1 - res[3]) * (1 - res[2]) * (1 - res[1]));
        vals[1] = res[0];
        vals[2] = 1 - ((1 - vals[0]) * (1 - res[0]));
        vals[3] = vals[2];
        int[] risks = setBands(vals, 4);

        return risks;
    }

    public static int[] setBands(float[] vals, int rep) {
        int[] bands = new int[rep];
        int p = 0;

        while (p < rep) {
            vals[p] = 1 - vals[p];
            log.info("raw risk score = " + vals[p]);
            if (vals[p] <= 0.1) {
                bands[p] = 1;
            }
            if (vals[p] > 0.1 && vals[p] <= 0.3) {
                bands[p] = 2;

            }
            if (vals[p] > 0.3 && vals[p] <= 0.4) {
                bands[p] = 3;

            }
            if (vals[p] > 0.4 && vals[p] <= 0.5) {
                bands[p] = 4;

            }
            if (vals[p] > 0.5 && vals[p] <= 0.6) {
                bands[p] = 5;

            }
            if (vals[p] > 0.6 && vals[p] < 0.7) {
                bands[p] = 6;

            }
            if (vals[p] > 0.7) {
                bands[p] = 7;

            }

            p++;

        }
        return bands;

    }

    public static int[] avePhy() throws SQLException {
        float[] result = new float[1];
        String url = "jdbc:mysql://kirkhac2.miniserver.com/optimis";
        String user = "root";
        String password = "";
        ResultSet rs = null;
        Statement st = null;
        Connection con = null;
        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();
        rs = st.executeQuery("SELECT AVG(riskScore) FROM `vulnerability`;");

        result[0] = rs.getFloat(1);
        int[] phyRisk = setBands(result, 1);
        return phyRisk;
    }
}
