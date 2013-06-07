/*
 *  Copyright 2013 University of Leeds UK, ATOS SPAIN S.A., City University London, Barcelona Supercomputing Centre and SAP
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

package eu.optimis.treccommon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Driver;

public class QueryDatabase {

	public String getIpServiceManifest(String serviceId){
		String manifest = "";
		/*
		ResourceBundle rb = ResourceBundle.getBundle("trecapiip",
				Locale.getDefault());
		*/
		try {
			/*
			String url = rb.getString("db.sp.host");
			String user = rb.getString("db.user");
			String password = rb.getString("db.pass");
			*/
			
			String url = "jdbc:mysql://optimis-database:3306/sptrecdb";
			String user = "tf_admin";
			String password = "";
			
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			Connection conn = DriverManager.getConnection(url, user,
					password);
			// Get a statement from the connection
			Statement stmt = conn.createStatement();

			// Execute the query
			ResultSet rs = stmt.executeQuery("SELECT  `service_manifest` FROM  `manifest_raw` WHERE  `service_id` =  '"+serviceId+"' limit 100");
			
			// Loop through the result set
			while (rs.next()){
//				System.out.println(rs.getString(1));
				manifest = rs.getString(1);
			}

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			return manifest;
		} catch (SQLException e) {
			System.out.println("Error: unable to load infrastructure provider trust");
			return manifest;
		}
	}
}
