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

import eu.optimis.ac.test.remoteTest.GetServerDetails;
import junit.framework.TestCase;

public class HeuristicModelTest  extends TestCase{

	public void testHeuristicModel()
	{
		String host = GetServerDetails.Host;
		
                HeuristicModelTesting(host);
                
	}//testHeuristicModel()
	
	
	private void HeuristicModelTesting(String host)
	{
                String whichSolver = "use_HeuristicSolver_Python_243";
                
		ACdecisionTest.ACacceptanceTest(host,whichSolver);System.out.println();
		ACdecisionTest.ACrejectionTest(host,whichSolver);System.out.println();
		ACdecisionTest.ACpartialAcceptanceTest(host,whichSolver);System.out.println();
		ACresponseTimeTest.ACResponseTimeTest(host,whichSolver);System.out.println();
		AntiAffinityTest.AntiAffinityTesting(host,whichSolver);System.out.println();
		AffinityTest.AffinityTesting(host,whichSolver);System.out.println();
		AllocationDetailsTest.AllocationDetailsTesting(host,whichSolver);System.out.println();
		
		FederationOfInstancesTest.FederationOfInstancesTesting(host,whichSolver);
		
	}//HeuristicModelTesting()
	
}//class
