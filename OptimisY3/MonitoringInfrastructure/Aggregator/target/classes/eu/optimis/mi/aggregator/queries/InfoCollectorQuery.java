/**

Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**/

package eu.optimis.mi.aggregator.queries;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import eu.optimis.mi.aggregator.bean.InformationCollector;
import eu.optimis.mi.aggregator.bean.InformationCollectors;

public class InfoCollectorQuery {
	public static InformationCollectors getInfoCollectors(Connection conn) {
		String sqlStatement = "SELECT * FROM monitoring_information_collector";
		InformationCollectors msets = new InformationCollectors();
		List<InformationCollector> list = new ArrayList<InformationCollector>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);

			while (rs.next()) {
				InformationCollector ms = new InformationCollector(rs
						.getString("collector_id"), rs
						.getString("name"), rs
						.getString("connection_script_path"),
						rs.getInt("time_interval_in_ms"));
				list.add(ms);
			}
			msets.setInformation_collector(list);

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
		return msets;
	}
	public static InformationCollectors getFullInfoCollectors(Connection conn) {

		String sqlStatement = "SELECT * FROM monitoring_information_collector";
		InformationCollectors msets = new InformationCollectors();
		List<InformationCollector> list = new ArrayList<InformationCollector>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);

			while (rs.next()) {	
				java.sql.Timestamp tp = rs.getTimestamp("creation_date");
				Date date = new Date(tp.getTime());
				InformationCollector ms = new InformationCollector(
						rs.getInt("row_id"),
						rs.getString("collector_id"), rs
						.getString("name"), rs
						.getString("connection_script_path"),
						rs.getInt("time_interval_in_ms"),
						rs.getString("description"),
						rs.getString("connection_arguments"),
						rs.getString("created_by"),
						date
						);
				list.add(ms);
			}
			msets.setInformation_collector(list);

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
		return msets;
	}
	
	public static InformationCollectors getOneInfoCollector(Connection conn, String collectorId) {

		String sqlStatement = "SELECT * FROM monitoring_information_collector where collector_id='" +
				collectorId+"'"; 
		InformationCollectors msets = new InformationCollectors();
		List<InformationCollector> list = new ArrayList<InformationCollector>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);

			while (rs.next()) {	
				java.sql.Timestamp tp = rs.getTimestamp("creation_date");
				Date date = new Date(tp.getTime());
				InformationCollector ms = new InformationCollector(
						rs.getInt("row_id"),
						rs.getString("collector_id"), rs
						.getString("name"), rs
						.getString("connection_script_path"),
						rs.getInt("time_interval_in_ms"),
						rs.getString("description"),
						rs.getString("connection_arguments"),
						rs.getString("created_by"),
						date
						);
				list.add(ms);
			}
			msets.setInformation_collector(list);

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
		}
		return msets;
	}
	
	
	public static boolean insertInfoCollectors(Connection conn, InformationCollectors ics) {
		
		boolean rs = true;
		for (InformationCollector ic : ics.getInformation_collector()) {
			java.sql.Timestamp current = new java.sql.Timestamp(ic.getCreation_date().getTime());
			String Query = "INSERT INTO monitoring_information_collector VALUES('" + 
			ic.getCollector_id() + "','"
					+ ic.getName() + "', '"
					+ ic.getCollector_script_path() + "', '"
					+ ic.getTime_interval_in_ms() + "', '"
					+ ic.getDescription() + "', '" + ic.getConnection_arguments()
					+ "', '"
					+ ic.getCreated_by() + "','"+current + "')";

			try {
				Statement st = conn.createStatement();
				if (st!=null){
					st.executeUpdate(Query);
				}

			} catch (SQLException e) {
				System.err.println("SQLException:" + e.getMessage() + ":"
						+ e.getSQLState());
				rs = false;
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
				}
				;
			}
		}
		return rs;

	}
	
public static boolean delOneInfoCollector(Connection conn, String collectorId) {
		boolean rs = true;
			String Query = "DELETE FROM monitoring_information_collector WHERE collector_id="
				+"'"+collectorId+"'";

			try {
				Statement st = conn.createStatement();
				if (st!=null){
					st.executeUpdate(Query);
				}

			} catch (SQLException e) {
				System.err.println("SQLException:" + e.getMessage() + ":"
						+ e.getSQLState());
				rs = false;
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
				}
				;
			}
		return rs;
	}
}
