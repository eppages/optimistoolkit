/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ac_treccommon_aas.test;

import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class EcoHostCommonTest   extends TestCase{
    
    public void testEcoHost()
    {
        String host = Test_aaS.getBoundle("ecoHost.ip");
        
        EcoHostTest(host);
    }//testEcoHost()
    
    public static MultivaluedMap<String, String> EcoHostTest(String host)
    {
        String port = Test_aaS.getBoundle("ecoHost.port");
        
        System.out.println("EcoHost Common Test Started");
        System.out.println("at host : "+host);
        
        MultivaluedMap<String, String> formParams = PhysicalHostsTest.PhysicalHostsNames(host);
        
        if(host.hashCode()== Test_aaS.AC_TREC_host.hashCode())
                host = "localhost";
        
        formParams.add("host", host);
        formParams.add("port", port);
        
        String url_String = "/AC_TRECcommon_aaS/TRECcommon/getEcoHost";
        
        long startTime = System.currentTimeMillis();
            
        MultivaluedMap<String, String> returnedMap = Test_aaS.doMultiTest(formParams,url_String,Test_aaS.RuntimeException_EcoHostcommon,"ecoHost");
            
        long endTime = System.currentTimeMillis();
        long executionTime = endTime-startTime;
        int executionTimeInSeconds = (int)(executionTime/1000);
        
        returnedMap.add("executionTimeInSeconds", Integer.toString(executionTimeInSeconds));
        
        System.out.println("executionTimeInSeconds : "+executionTimeInSeconds);
        
        System.out.println("EcoSPCommonTest Finished ----------------------------------");
        
        return returnedMap;
    }//EcoHostTest()
    
}//class
