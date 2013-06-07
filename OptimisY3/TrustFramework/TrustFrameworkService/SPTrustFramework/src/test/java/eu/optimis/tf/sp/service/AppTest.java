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

import junit.framework.TestCase;
import eu.optimis.tf.sp.service.operators.SP2IPFinalTrustCalculator;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Rigourous Test :-)
	 */
//	public void testApp() {
//		// TrustTimerSP ttsp = TrustTimerSP.instance();
//		TrecIPinfoDAO tipdao = new TrecIPinfoDAO();
//		ArrayList<String> brokerIps = new ArrayList<String>();
//		brokerIps.add(PropertiesUtils.getBoundle("atos.id"));
//		for (String brokerIp : brokerIps) {
//			try {
//				System.out.println("adding: "+brokerIp);
//				tipdao.addIp(brokerIp, brokerIp, "es");
//			} catch (Exception e1) {
//			}
//		}
//	}
	
//	public void testGetSM(){
//		String serviceId = "d7df4732-d379-4b6c-95b1-30bfdee705ed";
//		try {
//			String url = PropertiesUtils.getBoundle("db.host");
//			String user = PropertiesUtils.getBoundle("db.user");
//			String password = PropertiesUtils.getBoundle("db.pass");
//			Driver myDriver = new com.mysql.jdbc.Driver();
//			DriverManager.registerDriver(myDriver);
//			Connection conn = DriverManager.getConnection(url, user,
//					password);
//			// Get a statement from the connection
//			Statement stmt = conn.createStatement();
//
//			// Execute the query
//			ResultSet rs = stmt.executeQuery("SELECT  `service_manifest` FROM  `manifest_raw` WHERE  `service_id` =  '"+serviceId+"'");
//			String serviceManifest = "";
//			// Loop through the result set
//			while (rs.next())
//				serviceManifest = rs.getString(1);
//			System.out.println(serviceManifest);
//
//			// Close the result set, statement and the connection
//			rs.close();
//			stmt.close();
//			conn.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void testCalculation() 
	{		
		SP2IPFinalTrustCalculator spftc = new SP2IPFinalTrustCalculator();
		try 
		{
			spftc.calculateIPTrust("atos");	
			System.out.println ("Trust for Atos was recalculated");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testGetHistoricalTrusts(){
		String providerId = "atos";
		SPOperation spo = new SPOperation();
		System.out.println(spo.getHistoricTrust(providerId));
	}
	
	
}
