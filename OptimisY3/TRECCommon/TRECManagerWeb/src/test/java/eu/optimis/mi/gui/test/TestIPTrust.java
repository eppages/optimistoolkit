/**
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
package eu.optimis.mi.gui.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.mysql.jdbc.Driver;

import eu.optimis.mi.gui.client.model.Ip2SpModel;

public class TestIPTrust extends TestCase {
	
	String ipId = "umea";
	String spId = "OPTIMUMWEB";
	
//	public void testIPTrust(){
//// tk		MonitoringManagerWebServiceImpl mmwsi = new MonitoringManagerWebServiceImpl();
//// tk		List<TrustResourceSP>  trlist = mmwsi.getIPTrustResources(ipId);
//// tk		for(TrustResourceSP tr : trlist){
////tk			System.out.println("sasf "+tr.getproviderId() + ":" + tr.getproviderTrust());
////tk		}
//	}
	
//	public void testSPIP(){
//			ResourceBundle rb = ResourceBundle.getBundle("mmweb",
//					Locale.getDefault());
//			List<Sp2IpModel> trlist = new ArrayList<Sp2IpModel>();
//			try {
//				String url = rb.getString("db.sp.host");
//				String user = rb.getString("db.user");
//				String password = rb.getString("db.pass");
//				Driver myDriver = new com.mysql.jdbc.Driver();
//				DriverManager.registerDriver(myDriver);
//				Connection conn = DriverManager.getConnection(url, user,
//						password);
//				// Get a statement from the connection
//				Statement stmt = conn.createStatement();
//
//				// Execute the query
//				ResultSet rs = stmt.executeQuery("SELECT sp_id, service_id, service_well_formed, safety_run_gap, elasticity_closely, ip_reaction_time, sla_compliance, ip_compliance_with_legal, service_trust FROM `sp_to_ip` WHERE `sp_id`='"+spId+"' ORDER BY `service_time` DESC LIMIT 100 ");
//				
//				// Loop through the result set
//				while (rs.next()){
//					Sp2IpModel sp2ipmodel = new Sp2IpModel();
//					sp2ipmodel.setSpId(spId);
//					System.out.println(sp2ipmodel.getSpId());
//					sp2ipmodel.setServiceId(rs.getString(2));
//					System.out.println(sp2ipmodel.getServiceId());
//					sp2ipmodel.setServiceFormed(rs.getString(3));
//					System.out.println(sp2ipmodel.getServiceFormed());
//					sp2ipmodel.setRunGap(rs.getString(4));
//					System.out.println(sp2ipmodel.getRunGap());
//					sp2ipmodel.setElasticity(rs.getString(5));
//					System.out.println(sp2ipmodel.getElasticity());
//					sp2ipmodel.setIpReaction(rs.getString(6));
//					System.out.println(sp2ipmodel.getIpReaction());
//					sp2ipmodel.setSla(rs.getString(7));
//					System.out.println(sp2ipmodel.getSla());
//					sp2ipmodel.setLegal(rs.getString(8));
//					System.out.println(sp2ipmodel.getLegal());
//					sp2ipmodel.setServiceTrust(rs.getString(9));
//					System.out.println(sp2ipmodel.getServiceTrust());
//					trlist.add(sp2ipmodel);
//				}
//
//				// Close the result set, statement and the connection
//				rs.close();
//				stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//				System.out.println("Error: unable to load service provider trust");
//				e.printStackTrace();
//			}
//		
//	}
	
	public void testIPSP(){
		ResourceBundle rb = ResourceBundle.getBundle("mmweb",
				Locale.getDefault());
		List<Ip2SpModel> trlist = new ArrayList<Ip2SpModel>();
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
			ResultSet rs = stmt.executeQuery("SELECT `sp_id`, `service_id`, `service_risk`, `sercurity_assessment`, `service_reliability`, `performance`, `legal_openess`, `service_trust` FROM `ip_to_sp` WHERE  `sp_id` =  '"+spId+"' ORDER BY  `service_time` DESC LIMIT 100");
			
			// Loop through the result set
			while (rs.next()){
				Ip2SpModel ip2spmodel = new Ip2SpModel();
				ip2spmodel.setSpId(spId);
				System.out.println(ip2spmodel.getSpId());
				ip2spmodel.setServiceId(rs.getString(2));
				System.out.println(ip2spmodel.getServiceId());
				ip2spmodel.setServiceRisk(rs.getString(3));
				System.out.println(ip2spmodel.getServiceRisk());
				ip2spmodel.setSecurity(rs.getString(4));
				System.out.println(ip2spmodel.getSecurity());
				ip2spmodel.setReliability(rs.getString(5));
				System.out.println(ip2spmodel.getReliability());
				ip2spmodel.setPerformance(rs.getString(6));
				System.out.println(ip2spmodel.getPerformance());
				ip2spmodel.setLegal(rs.getString(7));
				System.out.println(ip2spmodel.getLegal());
				ip2spmodel.setServiceTrust(rs.getString(8));
				System.out.println(ip2spmodel.getServiceTrust());
				trlist.add(ip2spmodel);
			}

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Error: unable to load service provider trust");
			e.printStackTrace();
		}
}

}