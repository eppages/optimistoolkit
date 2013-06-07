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

import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.Constraints;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import junit.framework.TestCase;

/**
 *
 * @author mmacias
 */
public class HMTest extends TestCase {
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("109.231.120.19", 8080); // ip vm @ flex
    //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("130.239.48.6", 8080); // ip vm 2 @ umu
    CloudOptimizerRESTClient co = new CloudOptimizerRESTClient("212.0.127.140", 8080); //ip vm @ atos testing
    HolisticManagementRESTClient hm = new HolisticManagementRESTClient("212.0.127.140", 8080);
    
    public HMTest(String testName) {
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
    
    public void testHolisticManagement() {
        BusinessDescription bd = new BusinessDescription();
                bd.setSender("http://host:8080/IPDashboard");
        
        Objective obj = new Objective();
        bd.setObjective(obj);
        obj.setType(ObjectiveType.MAX_ECO);

        
        Constraints c = new Constraints();
        bd.setConstraints(c);
        c.setTrustGreaterThan(1.0);
        c.setCostLessThan(1.5);

        
        co.addBLO(bd);
        
        hm.notifyInfrastructureTrust(3);
        hm.notifyInfrastructureEco(300.3, "ecological", 1500);
        hm.notifyVMEco("xxx", 3.3, "ecological", 1200);       
        
    }

    public void testOther() {
        //co.anticipateVMFailure("338b7b44-0edf-449c-b97a-2443b0cd5dad");

    }
    
    
}
