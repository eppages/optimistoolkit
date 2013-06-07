
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
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class getRisk {

    protected static Logger log = Logger.getLogger(InfrastructureProviderRiskAssessmentServer.class);

    public static double[] calc() throws SQLException {

        //need to get to the inventory to get the probability of threats and impacts of vulnerabilities
        String url = "jdbc:mysql://optimis-database/riskInv";
        String user = "mmanager_usr";
        String password = "";
        Driver myDriver = null;
        Driver myDriver2 = null;

        //here are the statements and result sets / connections for the db calls   
        ResultSet rs = null;
        ResultSet rs2 = null;
        Statement st = null;
        Connection con = null;
        Statement st1 = null;
        Connection con1 = null;
        Statement stup = null;
        Connection conup = null;

        myDriver = new com.mysql.jdbc.Driver();
        DriverManager.registerDriver(myDriver);

        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();

        con1 = DriverManager.getConnection(url, user, password);
        st1 = con1.createStatement();


        //get the probabilities, mitigations from threats
        rs = st.executeQuery("SELECT threatID, likelyhood, mitiID FROM threat");
        //get impacts
        rs2 = st1.executeQuery("SELECT mitiID, impact FROM mitigation");
        int z = 0;
        int t = 0;
        rs.last();
        int as = rs.getRow();
        float[][] res = new float[as][as];
        rs.beforeFirst();
        //get the probabilities and impactID together with threats
        while (rs.next()) {

            res[0][t] = rs.getFloat(2);
            res[1][t] = rs.getInt(3);
            res[2][t] = rs.getInt(1);

            t++;
        }

        //associate impacts for threats
        rs2.last();
        int as2 = rs2.getRow();
        float[][] res2 = new float[as2][as2];
        rs2.beforeFirst();

        while (rs2.next()) {

            res2[0][z] = rs2.getInt(1);
            res2[1][z] = rs2.getFloat(2);

            z++;

        }

        int i = 0;
        int j = 0;
        int g = 0;
        float[] rt = new float[18];
        double[] rt2 = new double[18];
        double[] riskScore = new double[res.length];
        while (i < res.length) {

            if ((int) res2[0][j] == res[1][i]) {


                //Calculate risk for threats

                log.info("risk calculation for item id = " + i + " is " + res2[1][j] + " * " + res[0][i]);
                //tk
                riskScore[g] = res2[1][j] * res[0][i];
            //    log.info("temp risk out = " + riskScore[g]);
                //tk
                rt[(int) res[2][i]] = (res2[1][j] * res[0][i]);
                rt2[i] = 1 - (res2[1][j] * res[0][i]);
                log.info(" and risk output is =  " + String.format("%.1f", rt2[i]));
                   
            //    log.info(" and risk output is =  " + String.format("%.1f", rt[i]));
                //	risk[g]= (res2[i][j] * res[0][i]);
                g++;
            }



            if (i == res.length - 1 & j < 6) {
                i = 0;
                j++;
            }
            i++;
            //          y++;			

        }
        return rt2;


    }
}