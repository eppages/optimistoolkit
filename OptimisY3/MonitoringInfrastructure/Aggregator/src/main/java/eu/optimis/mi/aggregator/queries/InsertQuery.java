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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.UUID;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

public class InsertQuery {
	public static boolean insertAResource(Connection conn,
			MonitoringResourceDataset md) {
		String row_id = UUID.randomUUID().toString();

		java.sql.Timestamp current = new java.sql.Timestamp(md
				.getMetric_timestamp().getTime());
		String query = null;
		if (md.getResource_type().contains("physical")){
			query = "INSERT INTO monitoring_resource_physical(row_id, physical_resource_id,virtual_resource_id," +
			"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
			"metric_timestamp) VALUES('" + row_id
			+ "','" + md.getPhysical_resource_id() + "','"
			+ md.getVirtual_resource_id() + "', '"
			+ md.getService_resource_id() + "', '"
			+ md.getMonitoring_information_collector_id() + "', '"
			+ md.getMetric_name() + "', '" 
			+ md.getMetric_unit() + "', '" 
			+ md.getMetric_value() + "', '"
			+ current +  "')";
		}
		else if (md.getResource_type().contains("virtual")){
			query = "INSERT INTO monitoring_resource_virtual(row_id, physical_resource_id,virtual_resource_id," +
			"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
			"metric_timestamp) VALUES('" + row_id
			+ "','" + md.getPhysical_resource_id() + "','"
			+ md.getVirtual_resource_id() + "', '"
			+ md.getService_resource_id() + "', '"
			+ md.getMonitoring_information_collector_id() + "', '"
			+ md.getMetric_name() + "', '" 
			+ md.getMetric_unit() + "', '" 
			+ md.getMetric_value() + "', '"
			+ current +  "')";
		}
		else if (md.getResource_type().contains("service")){
			query = "INSERT INTO monitoring_resource_service(row_id, physical_resource_id,virtual_resource_id," +
			"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
			"metric_timestamp) VALUES('" + row_id
			+ "','" + md.getPhysical_resource_id() + "','"
			+ md.getVirtual_resource_id() + "', '"
			+ md.getService_resource_id() + "', '"
			+ md.getMonitoring_information_collector_id() + "', '"
			+ md.getMetric_name() + "', '" 
			+ md.getMetric_unit() + "', '" 
			+ md.getMetric_value() + "', '"
			+ current +  "')";
		}
		else if (md.getResource_type().contains("energy")){
			query = "INSERT INTO monitoring_resource_energy(row_id, physical_resource_id,virtual_resource_id," +
			"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
			"metric_timestamp) VALUES('" + row_id
			+ "','" + md.getPhysical_resource_id() + "','"
			+ md.getVirtual_resource_id() + "', '"
			+ md.getService_resource_id() + "', '"
			+ md.getMonitoring_information_collector_id() + "', '"
			+ md.getMetric_name() + "', '" 
			+ md.getMetric_unit() + "', '" 
			+ md.getMetric_value() + "', '"
			+ current +  "')";
		}
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(query);

		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage() + ":"
					+ e.getSQLState());
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
			;
		}
		return true;
	}

	public boolean insertResources(Connection conn,
			MonitoringResourceDatasets mds) {
		boolean rs = true;
		for (MonitoringResourceDataset md : mds.getMonitoring_resource()) {
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(1000000);
			String row_id = Integer.toString(randomInt);
			java.sql.Timestamp current = new java.sql.Timestamp(md
					.getMetric_timestamp().getTime());
			String query = null;
			if (md.getResource_type().contains("physical")){
				query = "INSERT INTO monitoring_resource_physical(row_id, physical_resource_id,virtual_resource_id," +
				"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
				"metric_timestamp) VALUES('" + row_id
				+ "','" + md.getPhysical_resource_id() + "','"
				+ md.getVirtual_resource_id() + "', '"
				+ md.getService_resource_id() + "', '"
				+ md.getMonitoring_information_collector_id() + "', '"
				+ md.getMetric_name() + "', '" 
				+ md.getMetric_unit() + "', '" 
				+ md.getMetric_value() + "', '"
				+ current +  "')";
			}
			else if (md.getResource_type().contains("virtual")){
				query = "INSERT INTO monitoring_resource_virtual(row_id, physical_resource_id,virtual_resource_id," +
				"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
				"metric_timestamp) VALUES('" + row_id
				+ "','" + md.getPhysical_resource_id() + "','"
				+ md.getVirtual_resource_id() + "', '"
				+ md.getService_resource_id() + "', '"
				+ md.getMonitoring_information_collector_id() + "', '"
				+ md.getMetric_name() + "', '" 
				+ md.getMetric_unit() + "', '" 
				+ md.getMetric_value() + "', '"
				+ current +  "')";
			}
			else if (md.getResource_type().contains("service")){
				query = "INSERT INTO monitoring_resource_service(row_id, physical_resource_id,virtual_resource_id," +
				"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
				"metric_timestamp) VALUES('" + row_id
				+ "','" + md.getPhysical_resource_id() + "','"
				+ md.getVirtual_resource_id() + "', '"
				+ md.getService_resource_id() + "', '"
				+ md.getMonitoring_information_collector_id() + "', '"
				+ md.getMetric_name() + "', '" 
				+ md.getMetric_unit() + "', '" 
				+ md.getMetric_value() + "', '"
				+ current +  "')";
			}
			else if (md.getResource_type().contains("energy")){
				query = "INSERT INTO monitoring_resource_energy(row_id, physical_resource_id,virtual_resource_id," +
				"service_resource_id, monitoring_information_collector_id, metric_name, metric_unit, metric_value, " +
				"metric_timestamp) VALUES('" + row_id
				+ "','" + md.getPhysical_resource_id() + "','"
				+ md.getVirtual_resource_id() + "', '"
				+ md.getService_resource_id() + "', '"
				+ md.getMonitoring_information_collector_id() + "', '"
				+ md.getMetric_name() + "', '" 
				+ md.getMetric_unit() + "', '" 
				+ md.getMetric_value() + "', '"
				+ current +  "')";
			}

			try {
				Statement st = conn.createStatement();
				if (st!=null){
					st.executeUpdate(query);
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
}
