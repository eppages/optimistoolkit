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
import eu.optimis.ac.gateway.utils.GetLogger;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.ac.test.affinityTest.AffinityConstraintsTestImplementation;
import eu.optimis.ac.test.affinityTest.AffinityRulesTestImplementation;
import eu.optimis.ac.test.affinityTest.AntiAffinityConstraintsTestImplementation;
import eu.optimis.ac.test.affinityTest.AntiAffinityRulesTestImplementation;
import eu.optimis.ac.test.affinityTest.getSM.GetSMforAffinityTesting;
import eu.optimis.ac.test.affinityTest.getSM.GetSMforAntiAffinityConstraintTesting;
import eu.optimis.ac.test.affinityTest.getSM.GetSMforAntiAffinityRuleTesting;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationDetails;
import eu.optimis.ac.test.affinityTest.print.PrintAllocationOffer;
import eu.optimis.ac.test.remoteTest.ACdecision;
import eu.optimis.ac.test.remoteTest.DoRemoteTest;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class AntiAffinityTest extends TestCase{
    
    public static Boolean ThrowRuntimeException = true;
    
    public static void testAntiAffinity()
    {
    	AntiAffinityTesting(GetServerDetails.Host,"DefaultSolver");
    	
    }//testAntiAffinity()
    
    public static void AntiAffinityTesting(String host,String whichSolver)
    {
                
        AffinityAllLow(host,whichSolver);
        
        AntiAffinityConstraintMedium_FederationIsAllowed_BasicElasticBiggerThanPhysicalHostsNumber(host,whichSolver);
        AntiAffinityConstraintHigh(host,whichSolver);
        
        AntiAffinityRuleMedium(host,whichSolver);
        AntiAffinityRuleMedium_BigElasticBasic(host,whichSolver);
        AntiAffinityRuleHigh(host,whichSolver);
        
    }//AntiAffinityTesting()
    
    public static void doTest(String TestName, String serviceManifestAsString, String isFederationAllowed, String testImplementationMethod,String host,String whichSolver)
    {
         System.out.println();
         
         SMAnalyzer manifestInfo = new SMAnalyzer(serviceManifestAsString,GetLogger.getLogger(),false);
         
         MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

         formParams.add("serviceManifest", serviceManifestAsString);
         formParams.add("cleanTestbed", "");
         if(isFederationAllowed!=null)
            formParams.add("isFederationAllowed", isFederationAllowed);
         
         DoRemoteTest doTest = new DoRemoteTest(formParams,host,whichSolver);
        
         assertEquals(doTest.status,200);
         
         ACdecision.printDecision(doTest.status, doTest.returnedSMs, false, null);
         
         System.out.println();
         
         String returnedSM = doTest.returnedSMs.get("serviceManifest").get(0);
         String AllocationDetails = doTest.returnedSMs.get("AllocationDetails").get(0);
         
         PrintAllocationOffer.printAllocationOfferForCertainSM(returnedSM, manifestInfo);
         
         System.out.println();
         
         PrintAllocationDetails.printAllocationDetailForAllComponents(manifestInfo, AllocationDetails);
         
         System.out.println();
         
         System.out.println("==>Testing test Result:");
         
         if(testImplementationMethod.contains("AllAffinityLow"));
         else if(testImplementationMethod.equals("AffinityConstraintMedium"))
             AffinityConstraintsTestImplementation.testAffinityConstraintMedium(AllocationDetails, manifestInfo, ThrowRuntimeException);
         else if(testImplementationMethod.equals("AffinityConstraintHigh"))
            AffinityConstraintsTestImplementation.testAffinityConstraintHigh(AllocationDetails, manifestInfo, ThrowRuntimeException);
         else if(testImplementationMethod.equals("AffinityRuleMedium"))
            AffinityRulesTestImplementation.testAffinityRulesMedium(AllocationDetails, manifestInfo, ThrowRuntimeException);
         else if(testImplementationMethod.equals("AffinityRuleHigh"))
            AffinityRulesTestImplementation.testAffinityRulesHigh(AllocationDetails, manifestInfo, ThrowRuntimeException);
         
         else if(testImplementationMethod.equals("AntiAffinityConstraintMedium"))
             AntiAffinityConstraintsTestImplementation.testAntiAffinityConstraintMedium(AllocationDetails,
                     manifestInfo, ThrowRuntimeException,
                     GetSMforAntiAffinityConstraintTesting.getInitial_List_forMediumAntiAffinityConstraintTest(),
                     GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forMediumAntiAffinityConstraintTest());
         else if(testImplementationMethod.equals("AntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber"))
             AntiAffinityConstraintsTestImplementation.testAntiAffinityConstraintMedium(AllocationDetails,
                     manifestInfo, ThrowRuntimeException,
                     GetSMforAntiAffinityConstraintTesting.getInitial_List_forMediumAntiAffinityConstraintTest(),
                     GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forMediumAntiAffinityConstraintTest_BasicElasticBiggerThanPhysicalHostsNumber());
         
         else if(testImplementationMethod.equals("AntiAffinityConstraintHigh"))
             AntiAffinityConstraintsTestImplementation.testAntiAffinityConstraintHigh(AllocationDetails,
                     manifestInfo, ThrowRuntimeException,
                     GetSMforAntiAffinityConstraintTesting.getInitial_List_forHighAntiAffinityConstraintTest(),
                     GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forHighAntiAffinityConstraintTest());
         
         else if(testImplementationMethod.equals("AntiAffinityRuleMedium"))
             AntiAffinityRulesTestImplementation.testAntiAffinityRuleMedium(AllocationDetails, manifestInfo, ThrowRuntimeException);
         else if(testImplementationMethod.equals("AntiAffinityRuleHigh"))
             AntiAffinityRulesTestImplementation.testAntiAffinityRuleHigh(AllocationDetails, manifestInfo, ThrowRuntimeException);
         
         System.out.println(TestName+" Finished");
         System.out.println("---------------------------------------------------------------------");
    }//doTest()
    
    public static void AffinityAllLow(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAffinityAllLow");
         
         String serviceManifestAsString = GetSMforAffinityTesting.withAllAffinityLow();
        
         String testImplementationMethod = "AllAffinityLow";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AffinityAllLow()
    
    public static void AntiAffinityConstraintMedium(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityConstraintMedium");
         
         String serviceManifestAsString = GetSMforAntiAffinityConstraintTesting.forMediumAntiAffinityConstraintTest(
                 GetSMforAntiAffinityConstraintTesting.getInitial_List_forMediumAntiAffinityConstraintTest(),
                 GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forMediumAntiAffinityConstraintTest());
        
         String testImplementationMethod = "AntiAffinityConstraintMedium";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityConstraintMedium()
    
    public static void AntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber");
         
         String serviceManifestAsString = GetSMforAntiAffinityConstraintTesting.forMediumAntiAffinityConstraintTest(
                 GetSMforAntiAffinityConstraintTesting.getInitial_List_forMediumAntiAffinityConstraintTest(),
                 GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forMediumAntiAffinityConstraintTest_BasicElasticBiggerThanPhysicalHostsNumber());
        
         String testImplementationMethod = "AntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber";
         
         doTest(TestName, serviceManifestAsString,"No", testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber()
    
    public static void AntiAffinityConstraintMedium_FederationIsAllowed_BasicElasticBiggerThanPhysicalHostsNumber(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityConstraintMedium_FederationIsAllowed_BasicElasticBiggerThanPhysicalHostsNumber");
         
         String serviceManifestAsString = GetSMforAntiAffinityConstraintTesting.forMediumAntiAffinityConstraintTest(
                 GetSMforAntiAffinityConstraintTesting.getInitial_List_forMediumAntiAffinityConstraintTest(),
                 GetSMforAntiAffinityConstraintTesting.getUpperBound_List_forMediumAntiAffinityConstraintTest_BasicElasticBiggerThanPhysicalHostsNumber());
        
         String testImplementationMethod = "AntiAffinityConstraintMedium_FederationIsNotAllowed_BasicElasticBiggerThanPhysicalHostsNumber";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityConstraintMedium_FederationIsAllowed_BasicElasticBiggerThanPhysicalHostsNumber()
    
    public static void AntiAffinityConstraintHigh(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityConstraintHigh");
         
         String serviceManifestAsString = GetSMforAntiAffinityConstraintTesting.forHighAntiAffinityConstraintTest();
        
         String testImplementationMethod = "AntiAffinityConstraintHigh";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityConstraintHigh()
    
    public static void AntiAffinityRuleMedium(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityRuleMedium");
         
         String serviceManifestAsString = GetSMforAntiAffinityRuleTesting.forMediumAntiAffinityRuleTest(
                 GetSMforAntiAffinityRuleTesting.getInitial_List_forHMediumAntiAffinityRuleTest(),
                 GetSMforAntiAffinityRuleTesting.getUpperBound_List_forMediumAntiAffinityRuleTest());
        
         String testImplementationMethod = "AntiAffinityRuleMedium";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityRuleMedium()
    
    public static void AntiAffinityRuleMedium_BigElasticBasic(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityRuleMedium_BigElasticBasic");
         
         String serviceManifestAsString = GetSMforAntiAffinityRuleTesting.forMediumAntiAffinityRuleTest(
                 GetSMforAntiAffinityRuleTesting.getInitial_List_forHMediumAntiAffinityRuleTest_BigElasticBasic(),
                 GetSMforAntiAffinityRuleTesting.getUpperBound_List_forMediumAntiAffinityRuleTest_BigElasticBasic());
        
         String testImplementationMethod = "AntiAffinityRuleMedium";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityRuleMedium_BigElasticBasic()
    
    public static void AntiAffinityRuleHigh(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAntiAffinityRuleHigh");
         
         String serviceManifestAsString = GetSMforAntiAffinityRuleTesting.forHighAntiAffinityRuleTest(
                 GetSMforAntiAffinityRuleTesting.getInitial_List_forHighAntiAffinityRuleTest(),
                 GetSMforAntiAffinityRuleTesting.getUpperBound_List_forHighAntiAffinityRuleTest());
        
         String testImplementationMethod = "AntiAffinityRuleHigh";
         
         doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AntiAffinityRuleHigh()
    
}//class
