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
public class EcoEfficiencyToolIP_Node_Test extends TestCase {

    public EcoEfficiencyToolIP_Node_Test(String testName) {
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

    public void testNodeAssessmentAndForecast() throws Exception {

        System.out.println("\n***Testing EcoEfficiency at Node Level (IP SIDE)***");
        EcoEfficiencyToolRESTClientIP ecoIP = new EcoEfficiencyToolRESTClientIP("optimis-ipvm", 8080);
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("optimis-ipvm");
        List<String> nodes = co.getNodesId();
        String[] types = {"energy", "ecological"};
        for (String type : types) {
            if (nodes.size() > 0) {
                String nodeEcoAss = null;
                String nodeEcoPred = null;

                for (String node : nodes) {
                    nodeEcoAss = ecoIP.assessNodeEcoEfficiency(node, type);
                    System.out.println("Node " + node + " " + type + " efficiency assessment = " + nodeEcoAss);
                    assertTrue(Double.parseDouble(nodeEcoAss) >= 0.0);
                }

                List<String> ret = ecoIP.assessMultipleNodesEcoEfficiency(nodes, type);
                System.out.println("Multiple Nodes (" + nodes + ")" + type + " efficiency assessment: " + ret);

                for (String node : nodes) {
                    nodeEcoPred = ecoIP.forecastNodeEcoEfficiency(node, null, null, type);
                    System.out.println("Node " + node + " " + type + " efficiency forecast = " + nodeEcoPred);
                    assertTrue(Double.parseDouble(nodeEcoPred) >= 0.0);
                }

                ret = ecoIP.forecastMultipleNodesEcoEfficiency(nodes, type);
                System.out.println("Multiple Nodes (" + nodes + ")" + type + " efficiency forecast: " + ret);

            } else {
                System.err.println("There are no nodes in the system. Ommiting test");
            }
        }
    }
}
