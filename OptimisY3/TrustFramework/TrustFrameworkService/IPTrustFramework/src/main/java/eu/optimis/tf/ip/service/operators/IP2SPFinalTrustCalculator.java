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

package eu.optimis.tf.ip.service.operators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;

import eu.optimis.common.trec.db.ip.TrecIP2SPDAO;
import eu.optimis.common.trec.db.ip.TrecSPTrustDAO;
import eu.optimis.tf.ip.service.utils.PropertiesUtils;
import eu.optimis.trec.common.db.ip.model.IpToSp;

public class IP2SPFinalTrustCalculator {
	
	Logger log = Logger.getLogger(this.getClass());

	public void calculateIPTrust(String spId) {
		TrecSPTrustDAO tsptdao = new TrecSPTrustDAO(); 
		TrecIP2SPDAO tip2sp =  new TrecIP2SPDAO();
		ExponentialSmoothingAggregator myEAggregator = new ExponentialSmoothingAggregator();
		double alpha = 0.5;
		try {
			List<IpToSp> ip2spList = tip2sp.getIP2SPTrustsBySpId(spId);
			ArrayList<Double> serviceTrustList = setTrustList(ip2spList);
			serviceTrustList.trimToSize();
			Double[]trutsArray = new Double[serviceTrustList.size()];
			serviceTrustList.toArray(trutsArray);
			double trust = myEAggregator.simpleExponentialSmoothing(alpha,trutsArray);
			if (trust < 0.25){
				trust = trust + 0.3;
			}
			log.info("\n\n final trust: "+trust+"\n\n");
			tsptdao.addSp(spId, trust);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Double> getServiceTrust(String spId){
		ArrayList<Double> serviceTrustsList = new ArrayList<Double>();
		try {
			String url = PropertiesUtils.getProperty("TRUST","db.ip.host");
			String user = PropertiesUtils.getProperty("TRUST","db.user");
			String password = PropertiesUtils.getProperty("TRUST","db.pass");
			Driver myDriver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(myDriver);
			Connection conn = DriverManager.getConnection(url, user,
					password);
			// Get a statement from the connection
			Statement stmt = conn.createStatement();

			// Execute the query
			ResultSet rs = stmt.executeQuery("SELECT  `service_trust` FROM  `ip_to_sp`  WHERE  `sp_id` =  '"+spId+"'");
			// Loop through the result set
			while (rs.next()){
				serviceTrustsList.add(Double.valueOf(rs.getString(1)));
//				System.out.println(serviceManifest);
			}

			// Close the result set, statement and the connection
			rs.close();
			stmt.close();
			conn.close();
			return serviceTrustsList;
		} catch (SQLException e) {
			return serviceTrustsList;
		}
	}
	
	private ArrayList<Double> setTrustList(List<IpToSp> ip2spList){
		ArrayList<Double> trustList = new ArrayList<Double>();
		for (IpToSp ip2sp : ip2spList){
			trustList.add(ip2sp.getServiceTrust());
		}
		return trustList;
	}
}
