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

/**
 *
 * @author jsubirat
 */
public class EcoEfficiencyToolIP_ServiceManifest_Test extends TestCase {

    public EcoEfficiencyToolIP_ServiceManifest_Test(String testName) {
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

    public void testServiceManifest() throws Exception {
        System.out.println("\n***Testing EcoEfficiency fields of the Service Manifest***");
        EcoEfficiencyToolRESTClientIP ecoIP = new EcoEfficiencyToolRESTClientIP("optimis-ipvm", 8080);
        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("optimis-ipvm");
        
        String manifest_LEED_OK = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_LEED_OK.xml");
        String manifest_LEED_KO = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_LEED_KO.xml");
        String manifest_BREEAM_OK = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_BREEAM_OK.xml");
        String manifest_BREEAM_KO = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_BREEAM_KO.xml");
        String manifest_CASBEE_OK = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_CASBEE_OK.xml");
        String manifest_CASBEE_KO = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_CASBEE_KO.xml");
        String manifest_EnergySR_OK = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_EnergySR_OK.xml");
        String manifest_EnergySR_KO = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_EnergySR_KO.xml");
        String manifest_GreenStar_KO = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_GreenStar_KO.xml");
        String manifest_GreenStar_OK = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP_GreenStar_OK.xml");

        String forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_LEED_OK, null, "energy");
        System.out.println("Manifest LEED OK (>0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) >= 0.0);
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_LEED_KO, null, "energy");
        System.out.println("Manifest LEED KO (=0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) == 0.0);
        
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_BREEAM_OK, null, "energy");
        System.out.println("Manifest BREEAM OK (>0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) >= 0.0);
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_BREEAM_KO, null, "energy");
        System.out.println("Manifest BREEAM KO (=0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) == 0.0);
        
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_CASBEE_OK, null, "energy");
        System.out.println("Manifest CASBEE OK (>0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) >= 0.0);
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_CASBEE_KO, null, "energy");
        System.out.println("Manifest CASBEE KO (=0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) == 0.0);
        
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_EnergySR_OK, null, "energy");
        System.out.println("Manifest EnergySR OK (>0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) >= 0.0);
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_EnergySR_KO, null, "energy");
        System.out.println("Manifest EnergySR KO (=0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) == 0.0);
        
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_GreenStar_OK, null, "energy");
        System.out.println("Manifest GreenStar OK (>0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) >= 0.0);
        forecastService = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest_GreenStar_KO, null, "energy");
        System.out.println("Manifest GreenStar KO (=0)?: " + forecastService);
        assertTrue(Double.parseDouble(forecastService) == 0.0);
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
