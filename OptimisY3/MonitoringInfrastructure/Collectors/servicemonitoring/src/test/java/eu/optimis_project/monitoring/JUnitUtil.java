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
package eu.optimis_project.monitoring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class JUnitUtil {
	
	
    public static Measurement generateMeasurement(String serviceID, String instanceID) {
        return generateMeasurements(1, serviceID, instanceID).iterator().next();
	}
	
    public static Set<Measurement> generateMeasurements(int n, String serviceID, String instanceID) {
		Set<Measurement> testEntries = new HashSet<Measurement>(n);

		for (int i = 0; i < n; i++) {
			String entryUUID = generateTestUUID();

			// FIXME Change this once measurement format is specified.
            Measurement testMeasurement = new Measurement(serviceID, instanceID, entryUUID, "data",
					System.currentTimeMillis());
			testEntries.add(testMeasurement);
		}

		return testEntries;
	}

	/**
	 * Returns a subset of elements in a set
	 */
	public static <T> Set<T> getSubset(Set<T> entrySet, int n) {

		if (n > entrySet.size()) {
			throw new IllegalArgumentException("Subset size (" + n
					+ ") larger than set size (" + entrySet.size() + ".");
		}

		if (n == 0) {
			return new HashSet<T>();
		}

		int[] elementIndecies = getRandomIndexes(entrySet.size(), n);
		Arrays.sort(elementIndecies);

		Set<T> resultSet = new HashSet<T>();
		Iterator<T> it = entrySet.iterator();

		int counter = 0;

		for (int i = 0; i < entrySet.size(); i++) {
			if (i == elementIndecies[counter]) {
				resultSet.add(it.next());
				counter++;
			} else {
				it.next();
			}

			if (counter == n) {
				break;
			}
		}

		return resultSet;
	}

	/*
	 * Get a set of N unique random numbers between 0 and size,
	 */
	protected static int[] getRandomIndexes(int size, int n) {

		if (n > size) {
			throw new IllegalArgumentException(
					"Expected subset larger than set.");
		}

		int[] resultSet = new int[n];
		int[] dataSet = new int[size];

		// Fill the dataset between 0 and size
		for (int i = 0; i < size; i++) {
			dataSet[i] = i;
		}

		int counter = 0;

		/*
		 * Choose an index randomly, and empty that data position once chosen.
		 * Repeat until N numbers are uniquely chosen
		 */
		while (counter < n) {
			int randomIndex = (int) Math.floor(Math.random() * size);

			if (dataSet[randomIndex] != -1) {
				resultSet[counter] = dataSet[randomIndex];
				dataSet[randomIndex] = -1;
				counter++;
			}
		}

		return resultSet;
	}

	/**
	 * Generate a set with a random service-id
	 */
	public static Set<Measurement> generateMeasurements(int n) {
        return generateMeasurements(n, generateServiceID(), generateInstanceID());
	}

	/**
	 * Generate an UUID with a given prefix to identify test records
	 */
	public static String generateTestUUID() {
		return "test_" + UUID.randomUUID();
	}

    /**
     * Generate a service ID
     */
	public static String generateServiceID() {
        return "service_" + UUID.randomUUID();
	}

    /**
     * Generate an instanceID
     */
    public static String generateInstanceID() {
        return "instance_" + UUID.randomUUID();
    }

}
