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

import eu.optimis.service_manager.client.ServiceManagerClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import junit.framework.TestCase;

/**
 *
 * @author jsubirat
 */
public class EcoEfficiencyToolSP_Service_Test extends TestCase {

    public EcoEfficiencyToolSP_Service_Test(String testName) {
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
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    public void testServiceAssessmentAndForecast() throws Exception {
        System.out.println("\n***Testing EcoEfficiency at Service Level (SP SIDE)***");
        EcoEfficiencyToolRESTClientSP ecoSP = new EcoEfficiencyToolRESTClientSP("optimis-spvm", 8080);
        ServiceManagerClient smClient = new ServiceManagerClient("optimis-spvm", "8080");

        String serviceId = null;
        String servicesRaw = smClient.getServices();
        int finish = servicesRaw.indexOf("</ser:service_id>");
        if (finish != -1) {
            servicesRaw = servicesRaw.substring(0, finish);
            finish = servicesRaw.indexOf("<ser:service_id>");
            servicesRaw = servicesRaw.substring(finish);
            serviceId = servicesRaw.replaceFirst("<ser:service_id>", "");
        }

        String[] types = {"energy", "ecological"};
        for (String type : types) {

            if (serviceId != null) {
                System.out.println("Assessing service \"" + serviceId + "\" " + type + " efficiency...");
                String assessService = ecoSP.assessServiceEcoEfficiency(serviceId, type);
                assertTrue(Double.parseDouble(assessService) >= 0.0);
                System.out.println("Service " + serviceId + " " + type + " efficiency = " + assessService);
            } else {
                System.out.println("Skipping service " + type + " efficiency assessment test because no services exist.");
            }

            //String manifest = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/serviceTestManifestSP.xml");
            String manifest = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP.xml");
            String forecastService = ecoSP.forecastServiceEcoEfficiency("atos", manifest, null, null, type);
            assertTrue(Double.parseDouble(forecastService) >= 0.0);
            System.out.println("Service Deployment " + type + " efficiency forecast = " + forecastService);
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
