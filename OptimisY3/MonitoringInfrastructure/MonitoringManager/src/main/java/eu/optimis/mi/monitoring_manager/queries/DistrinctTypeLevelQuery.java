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

package eu.optimis.mi.monitoring_manager.queries;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class DistrinctTypeLevelQuery {
	
	private final static Logger logger = Logger
			.getLogger(DistrinctTypeLevelQuery.class.getName());
	
	public static MonitoringResourceDatasets getResource_type_tid(
			Connection conn, String resourceType, String tId) {
		String sqlStatement = null;
		if (resourceType.toLowerCase().equals("physical"))
			sqlStatement = "SELECT * FROM monitoring_resource_physical WHERE metric_timestamp=(SELECT max(metric_timestamp)"
					+ " FROM monitoring_resource_physical WHERE physical_resource_id='"
					+ tId + "') AND physical_resource_id = '" + tId + "'";
//		sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
//				+ "metric_name,metric_value, metric_unit, max(metric_timestamp) as metric_timestamp FROM monitoring_resource_physical "
//				+ "WHERE physical_resource_id='"
//				+ tId
//				+ "' GROUP BY metric_name";
		else if (resourceType.toLowerCase().equals("energy"))
			sqlStatement = "SELECT * FROM monitoring_resource_energy WHERE metric_timestamp=(SELECT max(metric_timestamp)"
					+ " FROM monitoring_resource_energy WHERE physical_resource_id='"
					+ tId + "') AND physical_resource_id = '" + tId + "'";
		else if (resourceType.toLowerCase().equals("virtual"))
			sqlStatement = "SELECT * FROM monitoring_resource_virtual WHERE metric_timestamp=(SELECT max(metric_timestamp)"
					+ " FROM monitoring_resource_virtual WHERE virtual_resource_id='"
					+ tId + "') AND virtual_resource_id = '" + tId + "'";
			
//			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
//					+ "metric_name,metric_value, metric_unit, max(metric_timestamp) as metric_timestamp FROM monitoring_resource_virtual "
//					+ "WHERE virtual_resource_id='"
//					+ tId
//					+ "' GROUP BY metric_name";
		else if (resourceType.toLowerCase().equals("service"))
			sqlStatement = "SELECT * FROM monitoring_resource_service WHERE metric_timestamp=(SELECT max(metric_timestamp)"
					+ " FROM monitoring_resource_service WHERE service_resource_id='"
					+ tId + "') AND service_resource_id = '" + tId + "'";

		else {
			logger.warn("MI can not recognize the resourcetype:"
					+ resourceType);
			try {
				conn.close();
			} catch (Exception e) {
			}
			return new MonitoringResourceDatasets();
		}
		MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> list = new ArrayList<MonitoringResourceDataset>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);
			while (rs.next()) {
				long sp =  rs.getLong("metric_timestamp");
				Date date = new Date((long)(sp * 1000));

				MonitoringResourceDataset ms = new MonitoringResourceDataset(
						rs.getString("service_resource_Id"),
						rs.getString("virtual_resource_id"),
						rs.getString("physical_resource_id"), resourceType,
						rs.getString("monitoring_information_collector_id"),
						rs.getString("metric_name"),
						rs.getString("metric_value"),
						rs.getString("metric_unit"), date);
				list.add(ms);
				date = null;
			}
			if (list.size() == 0)
				logger.warn("No data for this query: " + sqlStatement);
			msets.setMonitoring_resource(list);
			st.close();
			rs.close();

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return msets;
	}

	public static MonitoringResourceDatasets getResource_metric(
			Connection conn, String metricName, String resourceType) {
		String sqlStatement = null;

		if (resourceType.contains("physical"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_physical "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("energy"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_energy "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("virtual"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_virtual "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("service"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_service "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else{
			logger.warn("MI can not recognize the resourcetype:"
					+ resourceType);
			try {
				conn.close();
			} catch (Exception e) {
			}
			return new MonitoringResourceDatasets();
		}
		MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> list = new ArrayList<MonitoringResourceDataset>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);
			while (rs.next()) {
				long sp =  rs.getLong("metric_timestamp");
				Date date = new Date((long)(sp * 1000));

				MonitoringResourceDataset ms = new MonitoringResourceDataset(
						rs.getString("service_resource_Id"),
						rs.getString("virtual_resource_id"),
						rs.getString("physical_resource_id"), resourceType,
						rs.getString("monitoring_information_collector_id"),
						rs.getString("metric_name"),
						rs.getString("metric_value"),
						rs.getString("metric_unit"), date);
				list.add(ms);
				date = null;
			}
			if (list.size() == 0)
				logger.warn("No data for this query: " + sqlStatement);
			msets.setMonitoring_resource(list);
			st.close();
			rs.close();

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return msets;

	}
	
	
	public static MonitoringResourceDatasets getResource_metric_id(
			Connection conn, String metricName, String resourceType, String resourceId) {
		String sqlStatement = null;
		if (resourceType.contains("physical"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_physical "
					+ "WHERE metric_name ='"
					+ metricName 
					+ "' AND physical_resource_id='"+resourceId
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("energy"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_energy "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' AND physical_resource_id='"+resourceId
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("virtual"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_virtual "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' AND virtual_resource_id='"+resourceId
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else if (resourceType.contains("service"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_service "
					+ "WHERE metric_name ='"
					+ metricName
					+ "' AND service_resource_id='"+resourceId
					+ "' ORDER BY metric_timestamp DESC LIMIT 0, 1";
		else{
			logger.warn("MI can not recognize the resourcetype:"
					+ resourceType);
			try {
				conn.close();
			} catch (Exception e) {
			}
			return new MonitoringResourceDatasets();
		}
		MonitoringResourceDatasets msets = new MonitoringResourceDatasets();
		List<MonitoringResourceDataset> list = new ArrayList<MonitoringResourceDataset>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sqlStatement);
			while (rs.next()) {
				long sp =  rs.getLong("metric_timestamp");
				Date date = new Date((long)(sp * 1000));

				MonitoringResourceDataset ms = new MonitoringResourceDataset(
						rs.getString("service_resource_Id"),
						rs.getString("virtual_resource_id"),
						rs.getString("physical_resource_id"), resourceType,
						rs.getString("monitoring_information_collector_id"),
						rs.getString("metric_name"),
						rs.getString("metric_value"),
						rs.getString("metric_unit"), date);
				list.add(ms);
				date = null;
			}
			if (list.size() == 0)
				logger.warn("No data for this query: " + sqlStatement);
			msets.setMonitoring_resource(list);
			st.close();
			rs.close();

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return msets;

	}
}
