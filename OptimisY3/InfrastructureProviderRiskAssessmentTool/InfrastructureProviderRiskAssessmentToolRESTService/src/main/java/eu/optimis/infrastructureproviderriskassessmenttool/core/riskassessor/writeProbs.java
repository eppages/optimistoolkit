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

public class writeProbs {

    protected static Logger log = Logger.getLogger(writeProbs.class);

    public static String write(float[] probVM, double[] probPhy) {

// here we add the probabilities for the threats to the risk inventory

        String url = "jdbc:mysql://optimis-database/riskInv";
        String user = "mmanager_usr";
        String password = "";
        Driver myDriver = null;
        Statement st = null;
        Connection con = null;

        try {

            myDriver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(myDriver);
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            //vm inserts
            st.executeUpdate("UPDATE threat set likelyhood = " + probVM[0] + " where threatID = 13");
            st.executeUpdate("UPDATE threat set likelyhood = " + probVM[1] + " where threatID = 17");
            st.executeUpdate("UPDATE threat set likelyhood = " + probVM[2] + " where threatID = 12");
            st.executeUpdate("UPDATE threat set likelyhood = " + probVM[3] + " where threatID = 14");
            st.executeUpdate("UPDATE threat set likelyhood = " + probVM[4] + " where threatID = 16");
            //phy inserts
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[0] + " where threatID = 1");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[1] + " where threatID = 2");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[2] + " where threatID = 3");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[3] + " where threatID = 4");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[4] + " where threatID = 5");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[5] + " where threatID = 6");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[6] + " where threatID = 7");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[7] + " where threatID = 8");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[8] + " where threatID = 9");
            st.executeUpdate("UPDATE threat set likelyhood = " + probPhy[9] + " where threatID = 10");
log.info("writes done");

            st.close();
            con.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "write done";

    }
}