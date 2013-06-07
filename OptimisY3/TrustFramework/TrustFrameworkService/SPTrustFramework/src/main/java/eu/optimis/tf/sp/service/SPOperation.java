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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.mysql.jdbc.Driver;

import eu.optimis.common.trec.db.sp.TrecIPTrustDAO;
import eu.optimis.common.trec.db.sp.TrecSP2IPDAO;
import eu.optimis.common.trec.db.sp.TrecSnProviderTrustDAO;
import eu.optimis.tf.sp.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.sp.model.IpTrust;
import eu.optimis.trec.common.db.sp.model.SnTrustProvider;
import eu.optimis.trec.common.db.sp.model.SpToIp;

public class SPOperation {

	private boolean production = false;
	private static final double NO_TURST = 0.2;
	private double rate = 1.0;
	public SPOperation() {
		production = Boolean.valueOf(PropertiesUtils.getProperty("TRUST", "production"));
		rate = Double.valueOf(PropertiesUtils.getProperty("TRUST","maxRate"));
	}

	public double getTrust(String providerId) {
		TrecIPTrustDAO iptdao = new TrecIPTrustDAO();
		try {
			IpTrust ipt = iptdao.getIPTrust(providerId);
			return ipt.getIpTrust();
		} catch (Exception e) {
			return NO_TURST;
		}
	}

	public double getTrustSN(String providerId) {
		TrecSnProviderTrustDAO tsnptdao = new TrecSnProviderTrustDAO();
		try {
			SnTrustProvider sntp = tsnptdao.getSnProviderTrust(providerId, "sp");
			return sntp.getExpectation();
		} catch (Exception e) {
			return NO_TURST;
		}
	}

	public String getHistoricTrust(String providerId) {
		String historic = "";
//		String historicHeader = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		String historicBody = "";
//		try {
//			TrecIPTrustDAO iptdao = new TrecIPTrustDAO();
//			List<IpTrust> iptlst = iptdao.getIPTrusts(providerId);
//			
//			for (IpTrust ipt : iptlst){
//				historicBody = historicBody + "<value>" + (ipt.getIpTrust() * rate)
//						+ "</value>";
//			}
//			String historicEnd = "</trust>";
//			historic = historicHeader + historicBody + historicEnd;
//			return historic;
//		} catch (Exception e) {
			ResourceBundle rb = ResourceBundle.getBundle("trustframework",
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
				ResultSet rs = stmt.executeQuery("SELECT  `ip_trust` FROM  `ip_trust` WHERE  `ip_id` =  '"+providerId+"' limit 100");
				
				// Loop through the result set
				while (rs.next()){
//					historicBody = historicBody + "<value>" + (Double.valueOf(rs.getString(1)) * rate)
//							+ "</value>";
					historicBody = historicBody +(Double.valueOf(rs.getString(1)) * rate) + ",";
				}
				
				// Close the result set, statement and the connection
				rs.close();
				stmt.close();
				conn.close();
				return historicBody;
			} catch (SQLException e1) {
				System.out.println("Error: unable to load infrastructure provider trust");
				return "";
			}
//		}
	}

	public String getHistoricService(String serviceId) {
		String historic = "";
		if (!production) {
			try {
//				TrustNPDAO tnpdao = new TrustNPDAO();
//				List<Trustnp> LTNP;
//
//				LTNP = tnpdao.getNPData(serviceId);
//
//				String historicHeader = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
//				String historicBody = "";
//				for (Trustnp tnp : LTNP) {
//					historicBody = historicBody + "<value>" + tnp.getExpectation()
//							+ "</value>";
//				}
//				String historicEnd = "</trust>";
//				historic = historicHeader + historicBody + historicEnd;
				return historic;
			} catch (Exception e) {
				return historic;
			}
			
		} else {
			TrecSP2IPDAO tsp2ipdao = new TrecSP2IPDAO();
//			String historicHeader = "<trust xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
			String historicBody = "";
			try {
				List<SpToIp> sp2iplst = tsp2ipdao.getSP2IPTrust(serviceId);
				for (SpToIp sp2ip : sp2iplst){
//					historicBody = historicBody + "<value>" + (sp2ip.getServiceTrust() * rate)
//							+ "</value>";
					historicBody = historicBody + (sp2ip.getServiceTrust() * rate) + ",";
				}
				String historicEnd = "</trust>";
				historic = historicBody; //historicHeader + historicBody + historicEnd;
				return historic;
			} catch (Exception e) {
				return historic;
			}
		}
		
	}
}
