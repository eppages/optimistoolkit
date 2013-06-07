package eu.optimis.elasticityengine.monitoring;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;

/**
 * Fake implementation of the monitoring source interface used for testing.
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umeå University
 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class FakeMonitoringSource implements MonitoringSource {

	private final static Logger log = Logger
			.getLogger(FakeMonitoringSource.class);
	private int runIndex = 0;

	public FakeMonitoringSource() {
	}

	@Override
	public List<MonitoringResourceDataset> getData(String collectorID,
			String kpiName) {

		MonitoringResourceDatasets dataSet = generateFakeData(collectorID,
				"service", kpiName);
		return dataSet.getMonitoring_resource();
	}

	private MonitoringResourceDatasets generateFakeData(String collectorID,
			String type, String kpiName) {

		Date now = new Date();
		now.setTime(System.currentTimeMillis());

		// Calculate some kind of value which is reasonably stable
		List<MonitoringResourceDataset> dataList = new ArrayList<MonitoringResourceDataset>();

		runIndex++;
		String value;
		String serviceID;
		MonitoringResourceDataset data;

		switch (runIndex) {

		case 1:
			value = "187";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;

		case 2:
			value = "187";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "38";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;

		case 3:
			value = "107";
			serviceID = "instance-jboss-1";

			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "85";
			serviceID = "instance-jboss-3";

			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "70";
			serviceID = "instance-jboss-2";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			break;
		case 4:
			value = "100";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "60";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "80";
			serviceID = "instance-jboss-2";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		case 5:
			value = "40";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "60";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "10";
			serviceID = "instance-jboss-2";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		case 6:
			value = "187";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "60";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		case 7:
			value = "107";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "60";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "10";
			serviceID = "instance-jboss-2";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		case 8:
			value = "107";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);

			value = "160";
			serviceID = "instance-jboss-3";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		case 9:
			value = "0";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;

		default:
			value = "0";
			serviceID = "instance-jboss-1";
			data = getMonitoringEntry(value, collectorID,serviceID, kpiName,
					now);
			dataList.add(data);
			break;
		}
		
		int total = 0;
		System.out.println();
//		for (MonitoringResourceDataset item : dataList) {
//			System.out.println("Got value: " + item.getMetric_value());
//			total += Integer.valueOf(item.getMetric_value());
//		}
		//System.out.println("Total value: " + total);

		MonitoringResourceDatasets sets = new MonitoringResourceDatasets();
		sets.setMonitoring_resource(dataList);
		return sets;

	}

	// Add one
	private MonitoringResourceDataset getMonitoringEntry(String value,
			String collectorID, String serviceID, String kpiName, Date now) {

		MonitoringResourceDataset data = new MonitoringResourceDataset(
				"someVirtualResID",serviceID,  "somePhysicalResID", "service",
				collectorID, kpiName, value, "someUnit", now);

		log.debug("Value: " + value + " at time: " + now.getTime());

		return data;
	}

	@Override
	public Map<String, List<MonitoringResourceDataset>> getData(String serviceID,
			Set<String> kpiNames) {
		List<MonitoringResourceDataset> dataSet = new ArrayList<MonitoringResourceDataset>();
		for (String kpiName : kpiNames) {
			List<MonitoringResourceDataset> result = getData(serviceID, kpiName);
			if (result != null) {
				dataSet.addAll(result);
				log.info("Got " + result.size()
						+ " items for serviceID/kpiName: " + serviceID + '/'
						+ kpiName);
			}
		}
		return (Map<String, List<MonitoringResourceDataset>>) dataSet;
	}

	// XXX Remove
	public static void main(String[] args) {
		MonitoringSource mSource = new FakeMonitoringSource();
		List<MonitoringResourceDataset> data = mSource.getData("someCollectorID",
				"someKPI");
		System.out.println(data);
	}

	@Override
	public String[] getKPIs() {
		// TODO Auto-generated method stub
		return null;
	}
}
