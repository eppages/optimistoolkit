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

package eu.optimis.cloudoptimizer.blo;

import eu.optimis.schemas.trec.blo.BusinessDescription;
import eu.optimis.schemas.trec.blo.Constraints;
import eu.optimis.schemas.trec.blo.Objective;
import eu.optimis.schemas.trec.blo.ObjectiveType;
import java.io.ByteArrayInputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmacias
 */
public class BLOSchemaTest {
    
    public BLOSchemaTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testHello() {
        BusinessDescription bd = new BusinessDescription();
        bd.setSender("http://host:8080/IPDashboard");
        
        Objective obj = new Objective();
        bd.setObjective(obj);
        obj.setType(ObjectiveType.MAX_ECO);
        
        Constraints c = new Constraints();
        bd.setConstraints(c);
        c.setTrustGreaterThan(1.0);
        c.setCostLessThan(1.5);

        
        System.out.println(BLOUtils.toString(bd));
        boolean saltada = false;
        try {
            BLOUtils.validate(bd);
        } catch(BLOException ex) {
            saltada = true;            
        }
        assertFalse(saltada);        
    }
    
    
    @Test
    public void testBadUnmarshall() {
        String xmlBad = "<ns2:BusinessDescription xmlns:ns2=\"http://schemas.optimis.eu/trec/blo\" sender=\"http://localhost/jur\">\n" +
        "    <Objective type=\"holahola\"/>\n" +
        /*"    <Constraints>\n" +
        "       <Trust operator=\"greaterThan\">100</Trust>\n" +
        "       <Cost operator=\"lessThanOrEqual\">4000</Cost>\n" +
        "    </Constraints>\n" +*/
        "</ns2:BusinessDescription>";
        boolean hasException = false;
        try {
            BLOUtils.read(new ByteArrayInputStream(xmlBad.getBytes()));
            
        } catch (BLOException ex) {
            hasException = true;
        }
        assertTrue(hasException);
    }
    
    /*@Test
    public void testBadUnmarshall2() {
        String xmlBad = "<ns2:BusinessDescription xmlns:ns2=\"http://schemas.optimis.eu/trec/blo\" sender=\"http://localhost/jur\">\n" +
        "    <Objective>\n" +
        "       <Trust>0.1</Trust>\n" +
        "       <Trust>0.2</Trust>\n" +
        "       <Eco>0.3</Eco>\n" +
        "       <Cost>0.4</Cost>\n" +
        "    </Objective>\n" +
        "</ns2:BusinessDescription>";
        boolean hasException = false;
        try {
            BLOUtils.read(new ByteArrayInputStream(xmlBad.getBytes()));
            
            
        } catch (BLOException ex) {
            hasException = true;
        }
        assertTrue(hasException);
        
    
    }*/
    
    @Test
    public void testGoodUnmarshall() {
       String xmlGood = "<ns2:BusinessDescription xmlns:ns2=\"http://schemas.optimis.eu/trec/blo\" sender=\"http://localhost/jur\">\n" +
        "    <Objective type=\"MinCost\"/>\n" +
        /*"    <Constraints>\n" +
        "       <Trust operator=\"greaterThan\">100</Trust>\n" +
        "       <Cost operator=\"lessThanOrEqual\">4000</Cost>\n" +
        "    </Constraints>\n" +*/
        "</ns2:BusinessDescription>";
        boolean hasException = false;
        BusinessDescription blo = null;
        try {
             blo = BLOUtils.read(new ByteArrayInputStream(xmlGood.getBytes()));                        
        } catch (BLOException ex) {
            ex.printStackTrace();
            hasException = true;
        }
        assertFalse(hasException);
        assertEquals(blo.getSender(),"http://localhost/jur");
    }    
}
