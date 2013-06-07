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

import eu.optimis.ac.ACRestClients.ACperServiceConstraintsRestClient;
import eu.optimis.ac.test.affinityTest.getSM.GetSMforAffinityConstraintTesting;
import eu.optimis.ac.test.affinityTest.getSM.GetSMforAffinityRuleTesting;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import junit.framework.TestCase;

public class AffinityTest extends TestCase{
    
	public void testAffinity()
	{
		AffinityTesting(GetServerDetails.Host,"DefaultSolver");
	}//testAffinity()
	
    public static void AffinityTesting(String host,String whichSolver)
    {
        AffinityConstraintMedium(host,whichSolver);
        AffinityConstraintHigh(host,whichSolver);
        AffinityRuleMedium(host,whichSolver);
        AffinityRuleHigh(host,whichSolver);
     
    }//AffinityTesting()
    
    public static void AffinityConstraintMedium(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAffinityConstraintMedium");
         
         String serviceManifestAsString = GetSMforAffinityConstraintTesting.forMediumAffinityConstraintsTest();
        
         String testImplementationMethod = "AffinityConstraintMedium";
         
         AntiAffinityTest.doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AffinityConstraintMedium()
    
     public static void AffinityConstraintHigh(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAffinityConstraintHigh");
         
         String serviceManifestAsString = GetSMforAffinityConstraintTesting.forHighAffinityConstraintsTest();
        
         String testImplementationMethod = "AffinityConstraintHigh";
         
         AntiAffinityTest.doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AffinityConstraintHigh()
     
     public static void AffinityRuleMedium(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAffinityRuleMedium");
         
         String serviceManifestAsString = GetSMforAffinityRuleTesting.forMediumAffinityRuleTest();
        
         String testImplementationMethod = "AffinityRuleMedium";
         
         AntiAffinityTest.doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AffinityConstraintMedium()
    
     public static void AffinityRuleHigh(String host,String whichSolver)
     {
         String TestName = AffinityTest.getTestName("testAffinityRuleHigh");
         
         String serviceManifestAsString = GetSMforAffinityRuleTesting.forHighAffinityRuleTest();
        
         String testImplementationMethod = "AffinityRuleHigh";
         
         AntiAffinityTest.doTest(TestName, serviceManifestAsString,null, testImplementationMethod,host,whichSolver);
         
     }//AffinityRuleHigh()
     
     public static String getTestName(String name)
     {
         String TestName = name;
         
         System.out.println(TestName+" Started");
         System.out.println();
         
         return TestName;
     }//getTestName()
     
}//class
