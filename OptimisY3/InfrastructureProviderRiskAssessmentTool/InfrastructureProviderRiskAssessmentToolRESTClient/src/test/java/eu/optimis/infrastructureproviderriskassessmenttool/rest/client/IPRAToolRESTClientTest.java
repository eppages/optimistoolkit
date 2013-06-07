/*
 *  Copyright 2013 University of Leeds
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.optimis.infrastructureproviderriskassessmenttool.rest.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import junit.framework.TestCase;

/**
 *
 * @author scsmj
 */
public class IPRAToolRESTClientTest extends TestCase {

    String hostname = "optimis-ipvm2.ds.cs.umu.se";
    //String hostname = "localhost";
    int port = 8080;
    String manifestName = "IP-ManifestExampleY3.xml";

    public IPRAToolRESTClientTest(String testName) {
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

    /**
     * Test of preNegotiateSPDeploymentPhase method, of class
     * InfrastructureProviderRiskAssessmentToolRESTClient.
     */
    public void testPreNegotiateSPDeploymentPhase() {
        System.out.println("preNegotiateSPDeploymentPhase");
        String SPName = "atos";
        try {

            String manifestPath = "";
            if (System.getProperty("file.separator").equalsIgnoreCase("\\")) {
                manifestPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + manifestName;
            } else {
                manifestPath = System.getProperty("user.dir") + "/src/test/resources/" + manifestName;
            }
            System.out.println(manifestPath);
            String serviceManifest = new Scanner(new File(manifestPath)).useDelimiter("\\Z").next();
            IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
            SPPoFs result = instance.preNegotiateSPDeploymentPhase(SPName, serviceManifest);
            assertEquals(SPName, result.getSPNames().get(0));
            assertEquals(0.0, result.getPoFSLA().get(0));
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundExceptioncaught");
        }
    }

    public void testCalculatePhyHostPoF() {

        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        double pof = instance.calculatePhyHostPoF("atos", "24");
        System.out.println("atos pof: " + pof);

    }
    public void testCalculateRiskLevelOfPhyHostFailure() {

        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        int riskLevel = instance.calculateRiskLevelOfPhyHostFailure("atos", "24");
        System.out.println("atos riskLevel: " + riskLevel);

    }

 public void testCalculatePhyHostsPoFs() {
        HashMap<String, String> hosts = new HashMap<String, String>();
        hosts.put("atos1", "24");
        hosts.put("atos2", "48");
        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        List<Double> pofs = new ArrayList<Double>(); 
        pofs = instance.calculatePhyHostsPoFs(hosts);
        for (int i = 0 ; i < pofs.size(); i++){
            System.out.println("pof: " + pofs.get(i));           
        }
    }

    public void testCalculateRiskLevelsOfPhyHostFailures() {
        HashMap<String, String> hosts = new HashMap<String, String>();
        hosts.put("atos_a", "24");
        hosts.put("atos_b", "48");
        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        List<Integer> riskLevels = new ArrayList<Integer>();
        riskLevels = instance.calculateRiskLevelsOfPhyHostFailures(hosts);
        for (int i = 0 ; i < riskLevels.size(); i++){
            System.out.println("riskLevel: " + riskLevels.get(i));            
        }
    }
    public void testStartProactiveRiskAssessorREST(){
        
        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        
        String infrastructureID = "IPID_01";
        String infrastructureRiskLevelThreshold = "3";
        HashMap<String, String> physcialHostsRiskLevelThresholds = new HashMap<String, String>();
        physcialHostsRiskLevelThresholds.put("physcialHostID_01", "4");
        physcialHostsRiskLevelThresholds.put("physcialHostID_02", "5");

        HashMap<String, String> servicesRiskLevelThresholds = new HashMap<String, String>();
        servicesRiskLevelThresholds.put("ServiceID_01", "4");
        servicesRiskLevelThresholds.put("ServiceID_02", "5");

        HashMap<String, String> VMsRiskLevelThresholds1 = new HashMap<String, String>();
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_01", "3");
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_02", "4");
        VMsRiskLevelThresholds1.put("ServiceID_01_VMID_03", "5");

        HashMap<String, String> VMsRiskLevelThresholds2 = new HashMap<String, String>();
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_01", "1");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_02", "2");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_03", "3");
        VMsRiskLevelThresholds2.put("ServiceID_02_VMID_04", "4");

        HashMap<String, HashMap<String, String>> servicesVMsRiskLevelThresholds = new HashMap<String, HashMap<String, String>>(new HashMap<String, HashMap<String, String>>());
        servicesVMsRiskLevelThresholds.put("ServiceID_01", VMsRiskLevelThresholds1);
        servicesVMsRiskLevelThresholds.put("ServiceID_02", VMsRiskLevelThresholds2);

        instance.startProactiveRiskAssessorREST(infrastructureID, infrastructureRiskLevelThreshold, physcialHostsRiskLevelThresholds, servicesRiskLevelThresholds, servicesVMsRiskLevelThresholds);
    }
    
    public void testStopProactiveRiskAssessorREST(){
        
        IPRAToolRESTClient instance = new IPRAToolRESTClient(hostname, port);
        instance.stopProactiveRiskAssessorREST();
        
    }
}
