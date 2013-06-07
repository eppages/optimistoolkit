/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import eu.optimis.cloudoptimizer.rest.client.HolisticManagementRESTClient;
import eu.optimis.treccommon.TrecApiIP;
import junit.framework.TestCase;

public class TestTREC extends TestCase {
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("109.231.120.19", 8080); // ip vm @ flex
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6", 8080); // ip vm 2 @ umu
    CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("212.0.127.140", 8080); //ip vm @ atos testing
    HolisticManagementRESTClient hm = new HolisticManagementRESTClient("212.0.127.140", 8080);

    public TestTREC(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("^^");
    }

    public void testHolisticManagement() {
//        TrecApiIP t = new TrecApiIP("130.239.48.6",8080);
//        CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6",8080);
//
//        for(String service : co.getRunningServices()) {
//            System.out.println("* Service " + service);
//            for(String vm : co.getVMsIdsOfService(service)) {
//                System.out.println("\t- VM: " + vm);
//                System.out.println("\t\tRisk if cancelled: " + t.RISK.forecastRiskVMCancelled(vm,"1"));
//                //System.out.println("\t\tRisk if migration (unknown host): " + t.RISK.forecastRiskVMMigrationUnknownHost("1",vm,"opti"));
//            }
//
//        }
//        System.out.println(t.RISK.forecastRiskVMCancelled()
//        System.out.println(t.RISK.forecastRiskVMMigrationKnownHost("somevm","1"));
//        System.out.println(t.RISK.forecastRiskVMCancelled("somevm","1"));
    }
}
