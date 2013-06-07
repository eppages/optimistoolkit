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

package eu.optimis.cloudoptimizer.rest.client;

import junit.framework.TestCase;

/**
 *
 * @author fito
 */
public class UndeploymentTest extends TestCase {
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("109.231.120.19", 8080); // ip vm @ flex
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6", 8080); // ip vm 2 @ umu
    CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("192.168.252.56", 8080); //ip vm @ atos

    public UndeploymentTest(String testName) {
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

    public void testIPUndeployment() {
        /*System.out.println("**************************************");
        System.out.println("************ UNDEPLOYMENT ************");
        System.out.println("**************************************");
        System.out.println("Going to undeploy the service...");
        boolean a = co.undeploy("DemoApp");
        assertEquals("Problem when undeploying the service...", true, a);
        if (a) {
            System.out.println("Service undeployed!");
        }*/
    }
}
