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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import junit.framework.TestCase;


/**
 *
 * @author jsubirat
 */
public class EcoEfficiencyToolIP_Infrastructure_Test  extends TestCase {

    public EcoEfficiencyToolIP_Infrastructure_Test(String testName) {
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

    public void testIPAssessmentAndForecast() throws Exception {

        System.out.println("\n***Testing EcoEfficiency at IP Level (IP SIDE)***");
        EcoEfficiencyToolRESTClientIP ecoIP = new EcoEfficiencyToolRESTClientIP("optimis-ipvm", 8080);
        String[] types = {"energy", "ecological"};
        for(String type : types) {
            String assessmentInfrastructure = ecoIP.assessIPEcoEfficiency(type);
            assertTrue(Double.parseDouble(assessmentInfrastructure) >= 0.0);
            System.out.println("Infrastructure " + type + " efficiency assessment = " + assessmentInfrastructure);

            String forecastInfrastructure = ecoIP.forecastIPEcoEfficiency(null, type);
            assertTrue(Double.parseDouble(forecastInfrastructure) >= 0.0);
            System.out.println("Infrastructure " + type + " efficiency forecast = " + forecastInfrastructure);
            
            //String manifest = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/serviceTestManifestSP.xml");
            String manifest = readFileToString("src/test/java/eu/optimis/ecoefficiencytool/rest/client/xmls/manifestY3SP.xml");
            forecastInfrastructure = ecoIP.forecastIPEcoEfficiencyServiceDeployment(manifest, null, type);
            assertTrue(Double.parseDouble(forecastInfrastructure) >= 0.0);
            System.out.println("Service deployment infrastructure " + type + " efficiency forecast = " + forecastInfrastructure);
        }
    }
        
    public static String readFileToString(String fileName) throws IOException {

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
