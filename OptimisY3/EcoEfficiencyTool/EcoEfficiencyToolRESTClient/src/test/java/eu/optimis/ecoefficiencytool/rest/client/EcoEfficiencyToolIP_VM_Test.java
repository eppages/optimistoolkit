/**
 * Copyright (C) 2010-2013 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package eu.optimis.ecoefficiencytool.rest.client;

import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author jsubirat
 */
public class EcoEfficiencyToolIP_VM_Test extends TestCase {

    public EcoEfficiencyToolIP_VM_Test(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVMAssessmentAndForecast() throws Exception {

        System.out.println("\n***Testing EcoEfficiency at VM Level (IP SIDE)***");
        EcoEfficiencyToolRESTClientIP ecoIP = new EcoEfficiencyToolRESTClientIP("optimis-ipvm", 8080);
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("optimis-ipvm");

        String vmId = null;
        for (String node : co.getNodesId()) {
            if (co.getVMsId(node) != null) {
                if (co.getVMsId(node).size() > 0) {
                    vmId = co.getVMsId(node).get(0);
                }
            }
        }

        if (vmId != null) {
            String[] types = {"energy", "ecological"};
            for (String type : types) {
                String assessment = ecoIP.assessVMEcoEfficiency(vmId, type);
                System.out.println("VM " + vmId + " " + type + " efficiency Assessment OK (>0)?: " + assessment);
                assertTrue(Double.parseDouble(assessment) >= 0.0);

                String forecast = ecoIP.forecastVMEcoEfficiency(vmId, type, null);
                System.out.println("VM " + vmId + " " + type + " efficiency Forecast OK (>0)?: " + forecast);
                assertTrue(Double.parseDouble(forecast) >= 0.0);
            }
        } else {
            System.out.println("NO VMS RUNNING. Couldn't perform the following tests:");
            System.out.println("\tassessVMEcoEfficiency");
            System.out.println("\tforecastVMEcoEfficiency");
        }
    }
}
