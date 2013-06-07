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

public class ACresponseTimeTest extends TestCase{
	
	private static int MaximumResponseTime = 60;
	private static Boolean AllowThrowRuntimeException = true;
	
	public static void testACresponseTime()
	{
		ACResponseTimeTest(GetServerDetails.Host,"DefaultSolver");
		
	}//testACresponseTime()
	
	public static void ACResponseTimeTest(String host,String whichSolver) 
        {
                
            long startTime = System.currentTimeMillis();
  
            ACGatewayRemoteTest.RemoteACG(host,whichSolver);
            
            long endTime = System.currentTimeMillis();
            
            long executionTime = endTime-startTime;
            
            int executionTimeInSeconds = (int)(executionTime/1000);
            
			System.out.println("executionTimeInSeconds:"+executionTimeInSeconds);
			System.out.println("MaximumResponseTime:"+MaximumResponseTime);
            if((AllowThrowRuntimeException)&&(executionTimeInSeconds>MaximumResponseTime))
                throw new RuntimeException(""
                        + "\n AC Responce Time was : "+executionTimeInSeconds
                        +" which is Greater than MaximumResponseTime : "
                        +MaximumResponseTime);
            
            System.out.println("Total elapsed time in execution of method callMethod() is :"+ executionTimeInSeconds);

	}//ACResponseTimeTest()
	
	
}//class
