/**
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 * Copyright (C) 2011 Umea University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.optimis_project.monitoring.storage;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.optimis_project.monitoring.JUnitUtil;
import eu.optimis_project.monitoring.Measurement;

public class InMemoryStorageManagerTest {

	private static StorageManager storageManager;
    private final String SERVICEID = "serviceid.test";
    private final String INSTANCEID = "instanceid.test";

	@BeforeClass
	public static void init() {
		storageManager = new InMemoryStorageManager();
	}

	@After
	public void clearData() throws IOException {
		storageManager.removeAllData();
	}

	@AfterClass
	public static void shutdownStorageManager() {
		if (storageManager != null) {
			storageManager.shutdown();
		}
	}

	@Test
	public void testGetAllData() throws IOException {
		Set<Measurement> dataSet = storageManager.getAllData();
		assertEquals(0, dataSet.size());
	}

	@Test
	public void testRemoveData() throws IOException {
        int affectedRecords = storageManager.removeData(SERVICEID);
		assertEquals(0, affectedRecords);
	}

	@Test
	public void testStoreData() throws IOException {
		Set<Measurement> dataSet = storageManager.getAllData();
		assertEquals(0, dataSet.size());

        Measurement measurement = JUnitUtil.generateMeasurement(SERVICEID, INSTANCEID);
		storageManager.storeData(measurement);
		dataSet = storageManager.getAllData();

		assertEquals(1, dataSet.size());
		assertEquals(measurement, dataSet.iterator().next());

	}

	@Test
	public void testCreateRead() throws IOException {
		Set<Measurement> dataSet = storageManager.getAllData();
		assertEquals(0, dataSet.size());

        Measurement measurement = JUnitUtil.generateMeasurement(SERVICEID, INSTANCEID);
		storageManager.storeData(measurement);
		dataSet = storageManager.getAllData();

		assertEquals(1, dataSet.size());
		assertEquals(measurement, dataSet.iterator().next());

        dataSet = storageManager.getData(SERVICEID);

		assertEquals(1, dataSet.size());
		assertEquals(measurement, dataSet.iterator().next());

	}

	@Test
	public void testCRUD() throws IOException {
		Set<Measurement> dataSet = storageManager.getAllData();
		assertEquals(0, dataSet.size());

        Measurement measurement = JUnitUtil.generateMeasurement(SERVICEID, INSTANCEID);
		storageManager.storeData(measurement);
		dataSet = storageManager.getAllData();

		assertEquals(1, dataSet.size());
		assertEquals(measurement, dataSet.iterator().next());

        int affectedRows = storageManager.removeData(SERVICEID);
		assertEquals(1, affectedRows);

		affectedRows = storageManager.removeAllData();
		assertEquals(0, affectedRows);
	}

	@Test
	public void testMaxRecords() throws IOException {
		int maxRecords = 10000;

		Set<Measurement> testSet = JUnitUtil.generateMeasurements(maxRecords);

		assertEquals(maxRecords, testSet.size());

		for (Measurement m : testSet) {
			storageManager.storeData(m);
		}

		Set<Measurement> dataSet = storageManager.getAllData();
		assertEquals(maxRecords, dataSet.size());

		Set<Measurement> testSet2 = JUnitUtil.generateMeasurements(2);

		for (Measurement m : testSet2) {
			storageManager.storeData(m);
		}

		assertEquals(maxRecords, dataSet.size());
	}
}
