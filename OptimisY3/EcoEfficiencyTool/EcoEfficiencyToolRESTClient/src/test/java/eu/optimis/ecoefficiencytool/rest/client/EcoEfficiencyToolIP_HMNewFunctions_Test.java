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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import junit.framework.TestCase;
import net.emotivecloud.utils.ovf.OVFWrapper;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ResourceType;

/**
 *
 * @author jsubirat
 */
public class EcoEfficiencyToolIP_HMNewFunctions_Test extends TestCase {

    public EcoEfficiencyToolIP_HMNewFunctions_Test(String testName) {
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

    public void testMierda() {
        ResourceType resType = new ResourceType();
        System.out.println("value: " + resType.getValue());
        resType.setValue("3");
        System.out.println("value: " + resType.getValue());
    }
    public void testHMNewConditionalFunctions() throws Exception {

        try {
        System.out.println("\n***Testing New Conditional functions for the HM***");
        EcoEfficiencyToolRESTClientIP ecoIP = new EcoEfficiencyToolRESTClientIP("optimis-ipvm", 8080);
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("optimis-ipvm");

        String manifest = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP.xml");

        String forecast = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest, null, "energy");
        System.out.println("Service Deployment OK (>0)?: " + forecast);
        assertTrue(Double.parseDouble(forecast) >= 0.0);

        String vmId = null;
        for (String node : co.getNodesId()) {
            if (co.getVMsId(node) != null) {
                if (co.getVMsId(node).size() > 0) {
                    vmId = co.getVMsId(node).get(0);
                }
            }
        }

        if (vmId != null) {

            forecast = ecoIP.forecastIPEcoEfficiencyVMCancellation(vmId, null, "energy");
            System.out.println("VM " + vmId + " Cancellation OK (>0)?: " + forecast);
            assertTrue(Double.parseDouble(forecast) >= 0.0);

            for (String nodeId : co.getNodesId()) {
                forecast = ecoIP.forecastIPEcoEfficiencyVMMigrationKnownPlacement(vmId, nodeId, co.getNodesId(), "energy", null);
                System.out.println("VM " + vmId + " Migration to node " + nodeId + " OK (>0)?: " + forecast);
                assertTrue(Double.parseDouble(forecast) >= 0.0);
            }

            forecast = ecoIP.forecastIPEcoEfficiencyVMMigrationUnknownPlacement(vmId, "energy", null);
            System.out.println("VM " + vmId + " Migration (Unknown Placement) OK (>0)?: " + forecast);
            assertTrue(Double.parseDouble(forecast) >= 0.0);

        } else {
            System.out.println("NO VMS RUNNING. Couldn't perform the following tests:");
            System.out.println("\tforecastIPEcoEfficiencyVMCancellation");
            System.out.println("\tforecastIPEcoEfficiencyVMMigrationKnownPlacement");
            System.out.println("\tforecastIPEcoEfficiencyVMMigrationUnknownPlacement");
        }

        String file =readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/ovfTest.xml");
        //System.out.println(file);
        OVFWrapper ovfDom = new OVFWrapper(file);
        //System.out.println(ovfDom.toString());

            for (String nodeId : co.getNodesId()) {
                forecast = ecoIP.forecastIPEcoEfficiencyVMDeploymentKnownPlacement(ovfDom, nodeId, co.getNodesId(), "energy", null);
                System.out.println("VM Deployment on node " + nodeId + " OK (>0)?: " + forecast);
                assertTrue(Double.parseDouble(forecast) >= 0.0);
            }

            forecast = ecoIP.forecastIPEcoEfficiencyVMDeploymentUnknownPlacement(ovfDom, "energy", null);
            System.out.println("VM Deployment (Unknown Placement) OK (>0)?: " + forecast);
            assertTrue(Double.parseDouble(forecast) >= 0.0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String readFileToString(String fileName) throws IOException {

        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(fileName), "UTF-8");
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } finally {
            scanner.close();
        }
        return text.toString();
    }
}
