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

import org.junit.Test;


public class MonitoringUtilTest {
    @Test
    public void testMeasurementDatasetConversion() {
        Measurement m1 = new Measurement("SomeserviceID", "SomeInstanceID", "KPI-Name", "data data",
                System.currentTimeMillis());
        //FIXME No longer doable without a fakeCO source
        //Measurement m2 = MonitoringUtil.datasetToMeasurements(MonitoringUtil.measurementToDatasets(m1))
         //       .iterator().next();
        //assertEquals("Measurement changed unexpectedly after conversion", m1, m2);
    }

    // TODO Add more of the above
}
