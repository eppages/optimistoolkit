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

package eu.optimis.tf.sp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.mysql.jdbc.Driver;

import eu.optimis.manifest.api.sp.Manifest;
import eu.optimis.tf.sp.service.utils.GetSPManifestValues;

public class TestGetServiceID extends TestCase {

	
	public void testQueryDatabase() {
		String serviceId = "b6041f2d-2c6f-4ae4-af3a-6a965173421b";//"f84b2f64-73d0-47ee-93ab-37b1d4d86a32";

		try {
			String manifest = getManifest(serviceId);
			System.out.println(GetServiceId(manifest));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String getManifest(String serviceId) throws Exception {
		ResourceBundle rb = ResourceBundle.getBundle("trustframework",
				Locale.getDefault());
		System.out.println(serviceId);
		try {
			String url = rb.getString("db.sp.host");
			System.out.println(url);
			String user = rb.getString("db.user");
			String password = rb.getString("db.pass");
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			Connection conn = DriverManager.getConnection(url, user, password);
			// Get a statement from the connection
			Statement stmt = conn.createStatement();
			// Execute the query
			ResultSet rs = stmt
					.executeQuery("SELECT  `service_manifest` FROM  `manifest_raw` WHERE  `service_id` =  '"
							+ serviceId +"'");//+ "' order by `tstamp` desc limit 100");
			
//			ResultSet rs = stmt
//					.executeQuery("SELECT  `service_manifest` FROM  `service_info` WHERE  `service_id` =  '"
//							+ serviceId +"'");
			
			// Loop through the result set
			while (rs.next()) {
				System.out.println(rs.getString(1));
//				TrustResourceSP tr = new TrustResourceSP();
//				tr.setproviderId(spId);
//				double spTrustDBValue = Double.valueOf(rs.getString(1)) * 5;
//				tr.setproviderTrust(String.valueOf(spTrustDBValue));
//				trlist.add(tr);
			}

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			return "";
		} catch (SQLException e) {
			System.out.println("Error: unable to load service provider trust");
			return "";
		}
	}
	
	private String GetServiceId(String ServiceManifest){
		
		String serviceId;
		System.out.println(ServiceManifest);
		GetSPManifestValues gspmv = new GetSPManifestValues();
		Manifest mani = gspmv.stringManifest2Manifest(ServiceManifest);
		serviceId = mani.getVirtualMachineDescriptionSection().getServiceId();
		System.out.println(serviceId);
		
		return serviceId;
	}
}
