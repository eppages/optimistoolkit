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
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class DateTypeLevelQuery {
	private final static Logger logger = Logger
			.getLogger(DateTypeLevelQuery.class.getName());

	public static String dateformat = "yyyy-MM-dd HH:mm:ss";

	public static MonitoringResourceDatasets getResource_type_all(
			Connection conn, String resourceType, java.util.Date dfrom,
			java.util.Date dto) {

		long sfrom = dfrom.getTime() / 1000;
		long sto = dto.getTime() / 1000;

		String sqlStatement = null;

		if (resourceType.contains("service")) {
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_service "
					+ "WHERE metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		} else if (resourceType.contains("virtual")) {
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_virtual "
					+ "WHERE metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		} else if (resourceType.contains("physical")) {
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_physical "
					+ "WHERE metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		} else if (resourceType.contains("energy")) {
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_energy "
					+ "WHERE metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		} else {
			logger.info("MI can not recognize the resourcetype:" + resourceType);
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
				long sp = rs.getLong("metric_timestamp");
				Date date = new Date((long)(sp*1000));
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
			if (list.size() == 0){
				logger.warn("No data for this query: " + sqlStatement);
				logger.warn("datefrom:"+dfrom.toString() +";  dateto"+dto.toString() + "millisec from:"+dfrom.getTime() + "millisec to:"+dto.getTime());
			}
			msets.setMonitoring_resource(list);

		} catch (SQLException e) {
			logger.error("SQLException:" + e.getMessage() + ":"
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

	public static MonitoringResourceDatasets getResource_type_tid(
			Connection conn, String resourceType, String tId,
			java.util.Date dfrom, java.util.Date dto) {

		long sfrom = dfrom.getTime() / 1000;
		long sto = dto.getTime() / 1000;

		String sqlStatement = null;
		if (resourceType.contains("service"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_service "
					+ "WHERE service_resource_id='"
					+ tId
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else if (resourceType.contains("virtual"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_virtual "
					+ "WHERE virtual_resource_id='"
					+ tId
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else if (resourceType.contains("physical"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_physical "
					+ "WHERE physical_resource_id='"
					+ tId
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else if (resourceType.contains("energy"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_energy "
					+ "WHERE physical_resource_id='"
					+ tId
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else {
			System.out.println("MI can not recognize the resourcetype:"
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
				long sp = rs.getLong("metric_timestamp");
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
			if (list.size() == 0){
				logger.warn("No data for this query: " + sqlStatement);
				logger.warn("datefrom:"+dfrom.toString() +";  dateto"+dto.toString() + "millisec from:"+dfrom.getTime() + "millisec to:"+dto.getTime());
			}
			msets.setMonitoring_resource(list);

		} catch (SQLException e) {
			logger.error("SQLException:" + e.getMessage() + ":"
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

	public static MonitoringResourceDatasets getResource_type_mname(
			Connection conn, String resourceType, String metricName,
			java.util.Date dfrom, java.util.Date dto) {

		long sfrom = dfrom.getTime() / 1000;
		long sto = dto.getTime() / 1000;

		String sqlStatement = null;
		if (resourceType.contains("service"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_service "
					+ "WHERE (metric_name='"
					+ metricName
					+ "') AND (metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "')";
		else if (resourceType.contains("virtual"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_virtual "
					+ "WHERE metric_name='"
					+ metricName
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else if (resourceType.contains("physical"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_physical "
					+ "WHERE metric_name='"
					+ metricName
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else if (resourceType.contains("energy"))
			sqlStatement = "SELECT service_resource_id,virtual_resource_id, physical_resource_id, monitoring_information_collector_id,"
					+ "metric_name,metric_value, metric_unit,metric_timestamp FROM monitoring_resource_energy "
					+ "WHERE metric_name='"
					+ metricName
					+ "' AND metric_timestamp BETWEEN '"
					+ sfrom
					+ "' AND '"
					+ sto + "'";
		else {
			logger.error("MI Error: can not recognize the resourcetype:"
					+ resourceType + " sqlstatement:" + sqlStatement);
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
			logger.info("MI sqlStatement: " + sqlStatement);
			while (rs.next()) {
				long sp = rs.getLong("metric_timestamp");
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
			if (list.size() == 0){
				logger.warn("No data for this query: " + sqlStatement);
				logger.warn("datefrom:"+dfrom.toString() +";  dateto"+dto.toString() + "millisec from:"+dfrom.getTime() + "millisec to:"+dto.getTime());
			}
			msets.setMonitoring_resource(list);

		} catch (SQLException e) {
			logger.error("SQLException:" + e.getMessage() + ":"
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
