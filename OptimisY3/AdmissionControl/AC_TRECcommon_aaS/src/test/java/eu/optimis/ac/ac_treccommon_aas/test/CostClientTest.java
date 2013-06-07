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

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ac_treccommon_aas.utils.FileFunctions;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class CostClientTest   extends TestCase{
    
    private static String filename = Test_aaS.filename;
    
    public void testCost()
    {
        String HostDirect = Test_aaS.getBoundle("cost.Direct.ip");
        String Host = Test_aaS.getBoundle("cost.ip");
        
        for(int i=0;i<2;i++)
        {
            String host = null;
            
            if(i==0)host = HostDirect;
            if(i==1)host = Host;
            
            CostTest(host);
            
        }//for-i
    }//testCost()
    
    public static ReturnedValue CostTest(String host)
    {
        String serviceManifest = FileFunctions.readFileAsStringFromResources(filename);
        
        String port = Test_aaS.getBoundle("cost.port");
        
        System.out.println("Cost Client Test Started");
        System.out.println("with file : "+filename);
        System.out.println("at host : "+host);
            
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

        formParams.add("serviceManifest", serviceManifest);
        formParams.add("host", host);
        formParams.add("port", port);
        
        String url_String = "/AC_TRECcommon_aaS/TRECclients/getCost";
            
        long startTime = System.currentTimeMillis();
            
        String value = Test_aaS.doTest(formParams,url_String,Test_aaS.RuntimeException_CostClient,"Cost : ");
            
        long endTime = System.currentTimeMillis();
        long executionTime = endTime-startTime;
        int executionTimeInSeconds = (int)(executionTime/1000);
            
        System.out.println("executionTimeInSeconds : "+executionTimeInSeconds);
        System.out.println("Cost Client Test Finished ----------------------------------");
        
        return new ReturnedValue(value,executionTimeInSeconds);
    }//CostTest()
    
}//class