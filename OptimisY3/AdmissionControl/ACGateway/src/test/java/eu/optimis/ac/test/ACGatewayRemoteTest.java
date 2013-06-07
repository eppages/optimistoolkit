/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.test;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.ac.test.remoteTest.WriteSM;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class ACGatewayRemoteTest extends TestCase{
	
        public void testRemoteACGateway() {
            RemoteACG(GetServerDetails.Host,"DefaultSolver");
        }//testRemoteACGateway()
        
        public static MultivaluedMap<String, String> setTestInput()
        {
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
		String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/DecisionTest/SM_1c2.xml");		
                String serviceManifest2 = FileFunctions.readFileAsStringFromResources("SM108/DecisionTest/BigSM_1c2.xml");		
                String serviceManifest3 = FileFunctions.readFileAsStringFromResources("SM108/DecisionTest/PartialSM_1c2.xml");		
		        
		formParams.add("serviceManifest", serviceManifest1);
		formParams.add("serviceManifest", serviceManifest2);
		formParams.add("serviceManifest", serviceManifest3);
		
		formParams.add("doNotBackupSMflag", "True");
                formParams.add("cleanTestbed", "");
                
                return formParams;
        }//setTestInput()
    
	public static void RemoteACG(String host,String whichSolver) {
                
		MultivaluedMap<String, String> formParams = setTestInput();
                
                new SMsAnalyzer(formParams);
                
		doTest(formParams,host,whichSolver);
		
	}//testRemoteACG()
	
	public static MultivaluedMap<String, String> doTest(MultivaluedMap<String, String> formParams,String host,String whichSolver)
	{
            FileFunctions.deleteFolder(WriteSM.testsOutputPath);
            
            DoRemoteTest dotest = new DoRemoteTest(formParams,host,whichSolver);
                    
            assertEquals(dotest.status,200);
            
            ACdecision.printDecisionWithWriteSM(dotest.status, dotest.returnedSMs);
            
            System.out.println();
            System.out.println();
            System.out.println();
            
            return dotest.returnedSMs;
	}//doTest()
	
}//class
