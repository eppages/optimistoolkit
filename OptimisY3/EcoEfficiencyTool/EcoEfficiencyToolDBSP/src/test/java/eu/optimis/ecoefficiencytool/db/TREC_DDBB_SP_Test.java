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
package eu.optimis.ecoefficiencytool.db;

import eu.optimis.ecoefficiencytool.trecdb.sp.EcoServiceTableDAO;
import eu.optimis.trec.common.db.sp.model.SpEcoServiceTable;
import junit.framework.TestCase;

/**
 *
 * @author jsubirat
 */
public class TREC_DDBB_SP_Test extends TestCase {
    
    public TREC_DDBB_SP_Test(String testName) {
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
    
    public void testServiceTable() {
        try {
            System.out.println("\n****SERVICE TEST (SP TREC DDBB)****");
            Integer identifier = EcoServiceTableDAO.addEcoAssessment("testServiceSP",100.45, 88.39);
            SpEcoServiceTable tableRet = EcoServiceTableDAO.getEcoAssessmentById(identifier);
            assertEquals("testServiceSP", tableRet.getServiceId());
            assertEquals(100.45, tableRet.getEnergyEffService());
            assertEquals(88.39, tableRet.getEcologicalEffService());
            EcoServiceTableDAO.removeEcoAssessmentById(identifier);
            tableRet = EcoServiceTableDAO.getEcoAssessmentById(identifier);
            assertNull(tableRet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
