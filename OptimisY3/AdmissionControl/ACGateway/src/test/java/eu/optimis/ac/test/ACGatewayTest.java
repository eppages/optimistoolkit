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
import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.gateway.utils.ReachableHost;
import eu.optimis.ac.gateway.utils.ReachableTomcat;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import eu.optimis.ac.test.produceSM.ProduceSMfor_AvailabilityPerComponentTest;
import eu.optimis.ac.test.produceSM.ProduceSMfor_DecisionTest;
import eu.optimis.ac.test.produceSM.ProduceSMfor_FederationTest;
import eu.optimis.ac.test.produceSM.Produce_ServiceManifest;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.ac.test.remoteTest.WriteSM;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class ACGatewayTest extends TestCase{
	
        public void testRemoteACGateway() {
            
            //ProduceSMfor_FederationTest.WriteSMs_for_Federation_Test();
            
            RemoteACG();
        }//testRemoteACGateway()
    
	public static void RemoteACG() {
                
		MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
		
                //String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/0407/UMU/GeneDetectionBroker/20130406_11.23.05_Input_1-1.xml");
                
                //String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/0403/UMU/e429ae92/20130403_16.39.14_Input_1-1.xml");
                
                //String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/manifestSPECWeb2005_Josep.xml");
                //String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/f2fHLRSmanifest_JORGE3_ARSYS.xml");
                String serviceManifest1 = FileFunctions.readFileAsStringFromResources("SM108/OptimisGen_1.xml");
                //String serviceManifest1 = FileFunctions.readFileAsStringFromResources("Y3SMs/manifestY3SP_TST.xml");
		
        //        String serviceManifest1 = FileFunctions.readFileAsStringFromResources("Y3SMs/IP-Manifest.xml");
		//String serviceManifest2 = FileFunctions.readFileAsStringFromResources("SM107/SM_1c1.xml");
        //        String serviceManifest3 = FileFunctions.readFileAsStringFromResources("SM107/SM_1c2.xml");
                //String serviceManifest4 = FileFunctions.readFileAsStringFromResources("SM107/SM_1c3.xml");
                
		formParams.add("serviceManifest", serviceManifest1);
		//formParams.add("serviceManifest", serviceManifest2);
		//formParams.add("serviceManifest", serviceManifest3);
		//formParams.add("serviceManifest", serviceManifest4);
		formParams.add("doNotBackupSMflag", "True");
                
                new SMsAnalyzer(formParams);
                
		doTest(formParams,"DefaultSolver");
		
	}//testRemoteACG()
	
	public static void doTest(MultivaluedMap<String, String> formParams,String whichSolver)
	{
            FileFunctions.deleteFolder(WriteSM.testsOutputPath);
            
            DoRemoteTest dotest = new DoRemoteTest(formParams,whichSolver);
                    
            assertEquals(dotest.status,200);
            
            ACdecision.printDecisionWithWriteSM(dotest.status, dotest.returnedSMs);
            
            System.out.println();
            System.out.println();
            System.out.println();
	}//doTest()
	
}//class
