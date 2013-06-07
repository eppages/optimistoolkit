/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
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
package eu.optimis.ecoefficiencytool.db;

import eu.optimis.ecoefficiencytool.trecdb.ip.EcoIpTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoNodeTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoServiceTableDAO;
import eu.optimis.ecoefficiencytool.trecdb.ip.EcoVMTableDAO;
import eu.optimis.trec.common.db.ip.model.*;
import junit.framework.TestCase;

/**
 * This test set evaluates the insertion, obtention and deleting of eco
 * assessments in the different eco-related tables of the TREC Common IP
 * database.
 *
 * @author jsubirat
 */
public class TREC_DDBB_IP_Test extends TestCase {

    
    public TREC_DDBB_IP_Test(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        //ret = setupIPEnvironment();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //restorePreviousEnvironment(ret[0], ret[1]);
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

    
    public void testIpTable() {        
        try {
            System.out.println("\n****IP TEST (IP TREC DDBB)****");
            Integer identifier = EcoIpTableDAO.addEcoAssessment(90.96, 20.34, 120.0, 350.6, 2.3);
            IpEcoIpTable tableRet = EcoIpTableDAO.getEcoAssessmentById(identifier);
            assertEquals(90.96, tableRet.getEnergyEffIp());
            assertEquals(20.34, tableRet.getEcologicalEffIp());
            assertEquals(120.0, tableRet.getPerformanceIp());
            assertEquals(350.6, tableRet.getPowerIp());
            assertEquals(2.3, tableRet.getGrCo2sIp());
            EcoIpTableDAO.removeEcoAssessmentById(identifier);
            tableRet = EcoIpTableDAO.getEcoAssessmentById(identifier);
            assertNull(tableRet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testNodeTable() {
        try {
            System.out.println("\n****NODE TEST (IP TREC DDBB)****");
            Integer identifier = EcoNodeTableDAO.addEcoAssessment("testNode", 80.45, 34.76, 32.43, 100.2, 0.7);
            IpEcoNodeTable tableRet = EcoNodeTableDAO.getEcoAssessmentById(identifier);
            assertEquals("testNode", tableRet.getNodeId());
            assertEquals(80.45, tableRet.getEnergyEffNode());
            assertEquals(34.76, tableRet.getEcologicalEffNode());
            assertEquals(32.43, tableRet.getPerformanceNode());
            assertEquals(100.2, tableRet.getPowerNode());
            assertEquals(0.7, tableRet.getGrCo2sNode());
            EcoNodeTableDAO.removeEcoAssessmentById(identifier);
            tableRet = EcoNodeTableDAO.getEcoAssessmentById(identifier);
            assertNull(tableRet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testServiceTable() {
        try {
            System.out.println("\n****SERVICE TEST (IP TREC DDBB)****");
            Integer identifier = EcoServiceTableDAO.addEcoAssessment("testServiceIP", 100.45, 2.45, 55.89, 45.4, 3.2);
            IpEcoService tableRet = EcoServiceTableDAO.getEcoAssessmentById(identifier);
            assertEquals("testServiceIP", tableRet.getServiceId());
            assertEquals(100.45, tableRet.getEnergyEffService());
            assertEquals(2.45, tableRet.getEcologicalEffService());
            assertEquals(55.89, tableRet.getPerformanceService());
            assertEquals(45.4, tableRet.getPowerService());
            assertEquals(3.2, tableRet.getGrCo2sService());
            EcoServiceTableDAO.removeEcoAssessmentById(identifier);
            tableRet = EcoServiceTableDAO.getEcoAssessmentById(identifier);
            assertNull(tableRet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVMTable() {
        try {
            System.out.println("\n****VM TEST (IP TREC DDBB)****");
            Integer identifier = EcoVMTableDAO.addEcoAssessment("testVM", 354.87, 24.35, 222.2, 777.7, 3.2);
            IpEcoVmTable tableRet = EcoVMTableDAO.getEcoAssessmentById(identifier);
            assertEquals("testVM", tableRet.getVmId());
            assertEquals(354.87, tableRet.getEnergyEffVm());
            assertEquals(24.35, tableRet.getEcologicalEffVm());
            assertEquals(222.2, tableRet.getPerformanceVm());
            assertEquals(777.7, tableRet.getPowerVm());
            assertEquals(3.2, tableRet.getGrCo2sVm());
            EcoVMTableDAO.removeEcoAssessmentById(identifier);
            tableRet = EcoVMTableDAO.getEcoAssessmentById(identifier);
            assertNull(tableRet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
