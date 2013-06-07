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

import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class vulnerabilities {

    protected static Logger log = Logger.getLogger(vulnerabilities.class);

    public static double[] calc(double[] risk) throws SQLException {

        double[] calcv = new double[6];
        double[] calc = new double[6];
        double[] calcDbl = new double[6];
        int p = 0;
        @SuppressWarnings("unused") //FIXME
        String vulname = null;
   //     log.info("in vuln risk[0] = " + risk[0]);
//log.info("in vuln risk[0] = " + risk[0]);

//Physical host CPU vulnerability
        calcv[0] = 1 - risk[0];
        //Physical host Memory vulnerability
        calcv[1] = 1 - risk[3];
        //Physical host Disk vulnerability
        calcv[2] = 1 - risk[2];
        //Physical host Network vulnerability
        calcv[3] = 1 - ((1 - risk[9]) * (1 - risk[10]) * (1 - risk[7]) * (1 - risk[8]));
        //Physical host OS vulnerability
        calcv[4] = risk[6];
        //VM vulnerability
        calcv[5] = 1 - ((1 - risk[11]) * (1 - risk[12]) * (1 - risk[13]) * (1 - risk[14]) * (1 - risk[15]) * (1 - risk[16]));

        //now we give the risks a score on 1-7		
        //Phy
        calc[0] = calcv[0];
        //  log.info("phy = " + calc[0]);

        //VM
        calc[1] = calcv[2];
        //    calc[1] = 0.6;
        log.info("vm = " + calc[1]);

        //SLA
        calc[2] = (1 - ((1 - calc[1]) * (1 - calc[0])));
        log.info("sla = " + calc[2]);

        //IP
        calc[3] = calc[2];
        log.info("ip = " + calc[3]);

        while (p < 5) {

            if (calc[p] > 0.9) {
                calcDbl[p] = 7;
            }
            if (calc[p] > 0.8 && calc[p] <= 0.9) {
                calcDbl[p] = 6;

            }
            if (calc[p] > 0.7 && calc[p] <= 0.8) {
                calcDbl[p] = 5;

            }
            if (calc[p] > 0.5 && calc[p] <= 0.7) {
                calcDbl[p] = 4;

            }
            if (calc[p] > 0.4 && calc[p] <= 0.5) {
                calcDbl[p] = 3;

            }
            if (calc[p] > 0.3 && calc[p] < 0.4) {
                calcDbl[p] = 2;

            }
            if (calc[p] < 0.3) {
                calcDbl[p] = 1;

            }


            if (p == 0) {
                vulname = "physical host CPU";
            }
            if (p == 1) {
                vulname = "physical host Memory ";
            }
            if (p == 2) {
                vulname = "physical host disk ";
            }
            if (p == 3) {
                vulname = "network ";
            }
            if (p == 4) {
                vulname = "OS ";
            }
            if (p == 5) {
                vulname = "VM ";
            }

            p++;
        }
//add vuln to risk inventory
        writeDBVuln(calc);
        return calcDbl;
    }

    static String writeDBVuln(double[] vuln) throws SQLException {

        String url = "jdbc:mysql://optimis-database/riskInv";

        String user = "mmanager_usr";
        String password = "";
        int n = 0;
        int nn = 1;
        Driver myDriver = null;
        Statement st = null;
        Connection con = null;
        myDriver = new com.mysql.jdbc.Driver();
        DriverManager.registerDriver(myDriver);

        con = DriverManager.getConnection(url, user, password);
        st = con.createStatement();

        while (n < 6) {
            st.executeUpdate("INSERT INTO vulnerability (vulnerID, vulnerDesc, riskScore, vulnerName) VALUES (" + nn + ", 'mm' ," + vuln[n] + ", 'nn')");
            n++;
            nn++;
        }
        con.close();
        st.close();
        return null;

    }
}
