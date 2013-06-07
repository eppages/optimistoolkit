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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit tests for TestUtil methods
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 * 
 */
public class JUnitUtilTest {

	@Test
	/*
	 * Tests receiving a full subset of entries
	 */
	public void getFullSubsetTest() {
		int n = 3;

		Set<Measurement> measurements = JUnitUtil.generateMeasurements(n);
		Set<Measurement> subset = (Set<Measurement>) JUnitUtil
				.getSubset(measurements, n);
		assertEquals(measurements.size(), subset.size());
		assertEquals("Expecting full subset to be equal.", measurements, subset);
	}

	@Test
	/*
	 * Tests receiving an empty subset of entries
	 */
	public void getEmptySubsetTest() {
		int n = 8;

		Set<Measurement> measurements = JUnitUtil.generateMeasurements(n);
		Set<Measurement> subset = (Set<Measurement>) JUnitUtil
				.getSubset(measurements, 0);
		assertEquals("Expecting empty subset", subset, Collections.EMPTY_SET);
	}

	@Test
	/*
	 * Tests receiving a subset of entries
	 */
	public void getSubsetTest() {
		int n = 101;
		int nSub = 56;

		Set<Measurement> measurements = JUnitUtil.generateMeasurements(n);
		Set<Measurement> subset = (Set<Measurement>) JUnitUtil
				.getSubset(measurements, nSub);

		assertEquals("Subset size mismatch", nSub, subset.size());

		for (Measurement mSub : subset) {
			boolean found = false;
			for (Measurement m : measurements) {
				if (m.equals(mSub)) {
					found = true;
					break;
				}
			}

			if (!found) {
				fail("Subelement not found in set");
			}
		}
	}

	@Test
	/*
	 * Assert that all generated entries are unique
	 */
	public void generateMeasurementsTest() {
		Set<Measurement> dataSet = JUnitUtil.generateMeasurements(10);
		Measurement[] measurements = dataSet.toArray(new Measurement[0]);

		for (int i = 0; i < measurements.length; i++) {
			for (int j = i + 1; j < measurements.length; j++) {

				// Test equals method
				if (measurements[i].equals(measurements[j])) {
					fail("Expecting unique measurements. Duplicate found:\n "
							+ measurements[i] + "\n" + measurements[j]);
				}
			}
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalgetSubset() throws Exception {
		int n = 101;
		int nSub = n + 1; //Larger than N -> failure

		Set<Measurement> measurements = JUnitUtil.generateMeasurements(n);
		JUnitUtil.getSubset(measurements, nSub);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalGetRandomIndex() {
		int n = 107;
		int nSub = n + 1;

		JUnitUtil.getRandomIndexes(n, nSub);
	}

	@Test
	@Ignore
	public void getRandomIndexesTest() {
		// XXX Formulate this as an automatic test instead of depending on
		// sysout.
		int n = 107;
		int nSub = 42;

		int[] indicies = JUnitUtil.getRandomIndexes(n, nSub);

		for (int i : indicies) {
			System.out.println("Random index between 0 and " + n + ": " + i);
		}
	}

}
