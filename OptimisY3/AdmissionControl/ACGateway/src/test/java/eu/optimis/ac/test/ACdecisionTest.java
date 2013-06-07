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
import eu.optimis.ac.ACRestClients.ACperServiceConstraintsRestClient;
import eu.optimis.ac.gateway.utils.FileFunctions;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import eu.optimis.manifest.api.impl.AllocationOfferDecision;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class ACdecisionTest extends TestCase{
    
    private static Boolean ThrowRuntimeException = true;
    
    public static String tempfileNameForAcceptance = "SM108/UMU_service_manifest.xml";
    
    public static String fileNameForAcceptance = "SM108/DecisionTest/SM_1c2.xml";
    public static String fileNameForRejection = "SM108/DecisionTest/BigSM_1c2.xml";
    public static String fileNameForPartial = "SM108/DecisionTest/PartialSM_1c2.xml";
    
    public static void testACacceptance()
    {
    	ACacceptanceTest(GetServerDetails.Host,"DefaultSolver");
    	
    }//testACacceptance()
    
    public static void ACacceptanceTest(String host,String whichSolver)
    {   
        String serviceManifest = FileFunctions.readFileAsStringFromResources(fileNameForAcceptance);
        
        Boolean NotSuccess = DecisionTest(whichSolver,serviceManifest,ThrowRuntimeException,
                                AllocationOfferDecision.accepted.toString(),"testACacceptance",fileNameForAcceptance,host);
                        
       if((NotSuccess)&&(ThrowRuntimeException))doThisIfNotSuccess();
                            
    }//ACacceptanceTest()
    
    public static void testACrejection()
    {
    	ACrejectionTest(GetServerDetails.Host,"DefaultSolver");
    	
    }//testACrejection()
    
    public static void ACrejectionTest(String host,String whichSolver)
    {
        String serviceManifest = FileFunctions.readFileAsStringFromResources(fileNameForRejection);
        
        Boolean NotSuccess = DecisionTest(whichSolver,serviceManifest,ThrowRuntimeException,
                                AllocationOfferDecision.rejected.toString(),"testACrejection",fileNameForRejection,host);
        
        if((NotSuccess)&&(ThrowRuntimeException))doThisIfNotSuccess();
    }//ACrejectionTest()
    
    public static void testACpartialAcceptance()
    {
    	ACpartialAcceptanceTest(GetServerDetails.Host,"DefaultSolver");
    	
    }//testACpartialAcceptance()
    
    public static void ACpartialAcceptanceTest(String host,String whichSolver)
    {
        String serviceManifest = FileFunctions.readFileAsStringFromResources(fileNameForPartial);
        
        Boolean NotSuccess = DecisionTest(whichSolver,serviceManifest,ThrowRuntimeException,
                                AllocationOfferDecision.partial.toString(),"testACpartialAcceptance",fileNameForPartial,host);
        
        if((NotSuccess)&&(ThrowRuntimeException))doThisIfNotSuccess();
        
    }//ACpartialAcceptanceTest()
    
    private static void doThisIfNotSuccess()
    {
        throw new RuntimeException();
    }//doThisIfNotSuccess()
    
    private static Boolean DecisionTest(String whichSolver,String serviceManifest,
            Boolean ThrowRuntimeException,String WantedDecision, String testName, String fileName,String host)
    {
                
                System.out.println("--------------------------------------------------------------");
                System.out.println("Start test : "+testName);
                System.out.println("with  : "+fileName);
                
                SMAnalyzer smInfo = new SMAnalyzer(serviceManifest);
                
                System.out.println("isFederationAllowed Value : "+smInfo.isFederationAllowed);
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

                formParams.add("serviceManifest", serviceManifest);
                formParams.add("cleanTestbed", "");
                
                DoRemoteTest doTest = new DoRemoteTest(formParams,host,whichSolver);
                
                assertEquals(doTest.status,200);
                
                Boolean success = ACdecision.printDecision(doTest.status, doTest.returnedSMs, ThrowRuntimeException, WantedDecision);
                
                return success;
  }//DecisionTest()
    
}//class
