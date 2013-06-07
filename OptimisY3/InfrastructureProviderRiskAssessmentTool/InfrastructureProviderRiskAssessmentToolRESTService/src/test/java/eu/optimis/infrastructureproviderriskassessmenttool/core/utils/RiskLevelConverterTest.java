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

package eu.optimis.infrastructureproviderriskassessmenttool.core.utils;

import junit.framework.TestCase;

/**
 *
 * @author scsmj
 */
public class RiskLevelConverterTest extends TestCase {
    
    public RiskLevelConverterTest(String testName) {
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
     * Test of convertPoFLevel method, of class RiskLevelConverter.
     */
    public void testConvertPoFLevel() {
        System.out.println("convertPoFLevel");
        double pof = 0.1;
        int expResult = 1;
        int result = RiskLevelConverter.convertPoFLevel(pof);
        assertEquals(expResult, result);
        pof = 0.3;
        expResult = 2;
        result = RiskLevelConverter.convertPoFLevel(pof);
        assertEquals(expResult, result);
        pof = 0.5;
        expResult = 3;
        result = RiskLevelConverter.convertPoFLevel(pof);
        assertEquals(expResult, result);
        pof = 0.7;
        expResult = 4;
        result = RiskLevelConverter.convertPoFLevel(pof);
        assertEquals(expResult, result);
        pof = 0.9;
        expResult = 5;
        result = RiskLevelConverter.convertPoFLevel(pof);
        assertEquals(expResult, result);
    }

    /**
     * Test of convertRiskLevel method, of class RiskLevelConverter.
     */
    public void testConvertRiskLevel() {
        System.out.println("convertRiskLevel");
        int risklevel = 2;
        int expResult = 1;
        int result = RiskLevelConverter.convertRiskLevel(risklevel);
        assertEquals(expResult, result);
    }
}