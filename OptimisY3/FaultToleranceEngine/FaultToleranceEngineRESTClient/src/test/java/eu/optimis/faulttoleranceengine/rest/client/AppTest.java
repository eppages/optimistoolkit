/**
 * Copyright (C) 2010-2011 Barcelona Supercomputing Center
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
package eu.optimis.faulttoleranceengine.rest.client;

import eu.optimis.cloudoptimizer.blo.BLOException;
import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.Constraints;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    /*
     * public static Test suite() { return new TestSuite( AppTest.class ); }
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testFake() {}

    /*public void testPolicies() {
        FaultToleranceEngineRESTClient fte_client = new FaultToleranceEngineRESTClient("optimis-ipvm", 8080);

        System.out.println("Testing set policy method...");
        fte_client.setPolicy("DemoApp", "0.9");

        System.out.println("Testing update policy method...");
        fte_client.updatePolicy("DemoApp", "0.8");
    }

    public void testServicesManagement() {
        FaultToleranceEngineRESTClient fte_client = new FaultToleranceEngineRESTClient("optimis-ipvm", 8080);

        fte_client.newServiceDeployed("DemoApp", "dummyManifest");
        fte_client.newServiceUndeployed("DemoApp");

    }

    public void testProactiveFTEInteractionsWithRisk() {
        FaultToleranceEngineRESTClient fte_client = new FaultToleranceEngineRESTClient("optimis-ipvm", 8080);
        TrecApiIP trec = new TrecApiIP("192.168.252.56", 8080);
        
        System.out.println("Starting proactive risk assessment");
        trec.startFTEProactiveRiskAssessment("DemoApp");
        Map<String, Double> vmsIdsAvail = new HashMap<String, Double>();
        vmsIdsAvail.put("VM001", 0.4);
        
        System.out.println("Setting values for the proactive risk assessment of a service");
        trec.setFTEProactRAThresholdAvail("DemoApp", vmsIdsAvail);
        
        System.out.println("Testing the receivement of proactive values from risk tool");
        fte_client.putVMRiskPoF("VM001", "0.7");
        
    }*/
    
    
    public void testFTE_CO() {
        FaultToleranceEngineRESTClient fte = new FaultToleranceEngineRESTClient("212.0.127.140");
        
        BusinessDescription bd = new BusinessDescription();
        bd.setSender("http://host:8080/IPDashboard");
        
        Objective obj = new Objective();
        bd.setObjective(obj);
        obj.setType(ObjectiveType.MAX_ECO);
        

        
        Constraints c = new Constraints();
        bd.setConstraints(c);
        c.setTrustGreaterThan(1.0);
        c.setCostLessThan(1.5);
        

        try {
            fte.setSchedulingPolicy(bd);
        } catch (BLOException ex) {
            assertFalse(true);
            ex.printStackTrace();
        }
    }
}