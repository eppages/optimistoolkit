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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;
import eu.optimis.tf.clients.TrustFrameworkSPClient;

public class SPTrustClientTest extends TestCase {

	String serviceId = "a249ecf5-6eb8-4595-9038-e5b2b0960a68";
	String host = "127.0.0.1";
	int port = 8080;
	
	private String getSMIP(String serviceId){
		String manifest = "";
		ResourceBundle rb = ResourceBundle.getBundle("trecdbconection",
				Locale.getDefault());
		try {
			String url = rb.getString("db.sp.host");
			String user = rb.getString("db.user");
			String password = rb.getString("db.pass");
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			Connection conn = DriverManager.getConnection(url, user,
					password);
			// Get a statement from the connection
			Statement stmt = conn.createStatement();
			// Execute the query
			ResultSet rs = stmt.executeQuery("SELECT  `service_manifest` FROM  `manifest_raw` WHERE  `service_id` =  '"+serviceId+"'");
			System.out.println("query sent");
			// Loop through the result set
			while (rs.next()){	
				System.out.println(rs.getString(1));
				manifest = rs.getString(1);
			}

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			return manifest;
		} catch (SQLException e) {
			System.out.println("Error: unable to load infrastructure provider trust");
			System.out.println(e.getCause());
			return manifest;
		}
	}
	
	public void testStartMonitoring(){
		TrustFrameworkSPClient tfspc = new TrustFrameworkSPClient(host,port);
		String manifest = getSMIP(serviceId);
//		System.out.println(manifest);
		System.out.println(tfspc.serviceDeployed(manifest));
	}
	
//	public void testGetHistoric(){
//		TrustFrameworkSPClient tfspc = new TrustFrameworkSPClient(host,port);
//		List<String> historic = tfspc.getOperationHistoricTrust("atos");
//		System.out.println(historic.size());
//		for (String trust: historic){
//			System.out.println(trust);
//		}
//		
//	}
	
	
//	public void testGetTrust(){
//		TrustFrameworkSPClient tfspc = new TrustFrameworkSPClient(host,port);
//		System.out.println(tfspc.getDeploymentTrust("atos"));
//		
//	}
	
}
