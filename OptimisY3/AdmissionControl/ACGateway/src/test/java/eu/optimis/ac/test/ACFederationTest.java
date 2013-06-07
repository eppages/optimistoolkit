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
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadDecisionFromSM;
import eu.optimis.ac.gateway.serviceManifestFunctions.ReadExternalDeploymentManifest;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.smanalyzer.SMsAnalyzer;
import eu.optimis.ac.test.produceSM.ProduceSMfor_FederationTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;
import org.apache.xmlbeans.XmlException;

public class ACFederationTest  extends TestCase{
    
    public void testFederationDeployment() throws XmlException {
            
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
		
	String serviceManifest = FileFunctions.readFileAsStringFromResources("SM108/FederationTest/SMtrue_1c2.xml");
        	        
        serviceManifest = ProduceSMfor_FederationTest.GetSM_for_FederationTest();
        //ProduceSMfor_FederationTest.WriteSMs_for_Federation_Test();
	
        formParams.add("serviceManifest", serviceManifest);
		
	formParams.add("doNotBackupSMflag", "True");
        formParams.add("cleanTestbed", "");
        
        new SMsAnalyzer(formParams);
                
	MultivaluedMap<String, String> ReturnedParams = ACGatewayRemoteTest.doTest(formParams,GetServerDetails.Host,"DefaultSolver");
        
        String returnedSM = ReturnedParams.get("serviceManifest").get(0);
        
        String externalDeploymentSM = ReadExternalDeploymentManifest.getExternalDeploymentManifest(returnedSM);
        
        String AC_decision = ReadDecisionFromSM.getDecision(returnedSM);
        String Remote_AC_decision =  ReadExternalDeploymentManifest.getRemoteDecision(externalDeploymentSM);
        
        System.out.println("ExternalDeploymeneServiceManifest AC Decision : "+Remote_AC_decision);
        
        System.out.println();
        
        
        System.out.println("AC_decision must be : "+AllocationOfferDecision.partial.toString()+" "+
                AllocationOfferDecision.partial.toString().equals(AC_decision));
        System.out.println("Remote_AC_decision must be : "+AllocationOfferDecision.accepted.toString()+" "+
                AllocationOfferDecision.accepted.toString().equals(Remote_AC_decision));
        
        assertEquals(AllocationOfferDecision.partial.toString(),AC_decision);
        assertEquals(AllocationOfferDecision.accepted.toString(),Remote_AC_decision);
        
        System.out.println();
        System.out.println();
    }//testFederationDeployment()
    
}//class
