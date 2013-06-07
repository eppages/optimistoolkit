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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.optimis_project.monitoring.Measurement;

/**
 * 
 * In-memory implementation of the StorageManager interface. This is good for
 * testing, but is not suitable for "real" use.
 * 
 * Throws away records above 10,000 to prevent out-of-memory.
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 */
public class InMemoryStorageManager implements StorageManager {

	private final int MAXRECORDS = 10000;
	private final Logger log = Logger.getLogger(InMemoryStorageManager.class
			.getName());

	/**
	 * Custom Map implementation that removes oldest records when reaching
	 * MAXRECORDS
	 */
	private Map<Measurement, Boolean> measurementMap = new LinkedHashMap<Measurement, Boolean>(
			MAXRECORDS, .75F, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				Map.Entry<Measurement, Boolean> eldest) {
			return size() > MAXRECORDS;
		}
	};

	@Override
	public synchronized Set<Measurement> getData(String serviceID) {
		Set<Measurement> result = new HashSet<Measurement>();

		for (Measurement m : measurementMap.keySet()) {
			if (m.getServiceID().equals(serviceID)) {
				result.add(m);
			}
		}

		log.debug("Found " + result.size() + " measurements for serviceID: "
				+ serviceID);
		return result;
	}

	@Override
	public synchronized Set<Measurement> getAllData() {
        log.debug("Got call to getAllData, returning: " +
	            measurementMap.size() + " entries");
		return new HashSet<Measurement>(measurementMap.keySet());
	}

	@Override
	public synchronized int removeData(String serviceID) {
		Iterator<Measurement> it = measurementMap.keySet().iterator();
		Measurement next = null;

		int sizeBefore = measurementMap.size();
		if (sizeBefore == 0) {
			log.debug("No data in set");
			return 0;
		}

		System.out.println("sizeBefore: " + sizeBefore);

		while (it.hasNext()) {
			next = it.next();
			System.out.println(next);

			if (serviceID.equals(next.getServiceID())) {
				it.remove();
				log.debug("Removed entry");
			}
		}

		int removeCount = sizeBefore - measurementMap.size();
		log.debug("Removed " + removeCount + " entries for serviceID: "
				+ serviceID);
		return removeCount;
	}

	@Override
	public synchronized int removeAllData() {
		int mapSize = measurementMap.size();
		measurementMap.clear();
		return mapSize;
	}

	@Override
    public synchronized boolean storeData(Measurement measurement) {
		measurementMap.put(measurement, null);
        return true;
	}

	@Override
	public void shutdown() {
		measurementMap.clear();
		measurementMap = null;
		log.debug("Shut down InMemoryStorageManager.");
	}
}
