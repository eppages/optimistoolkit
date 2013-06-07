/**

Copyright 2013 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.mysql.jdbc.Driver;

import eu.optimis.tf.clients.TrustFrameworkIPClient;

public class IPTrustClientTest extends TestCase {

//	String serviceId = "a60b5031-179f-476f-89c3-532db31180e6";
	String serviceId = "8bb2ac9f-eee4-4b87-a648-93ebf0777386";
	String host = "127.0.0.1";
	int port = 8080;
	
	private String getSMIP(String serviceId){
		String manifest = "";
		ResourceBundle rb = ResourceBundle.getBundle("trecdbconection",
				Locale.getDefault());
		try {
			String url = rb.getString("db.ip.host");
			String user = rb.getString("db.user");
			String password = rb.getString("db.pass");
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
	
	public void testStartMonitoring(){
		TrustFrameworkIPClient tfspc = new TrustFrameworkIPClient(host,port);
		String manifest = getSMIP(serviceId);
		System.out.println(manifest);
		System.out.println(tfspc.serviceDeployed(manifest));
	}
}
