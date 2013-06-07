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

import eu.optimis.ac.ac_treccommon_aas.utils.FileFunctions;
import eu.optimis.ac.smanalyzer.SMAnalyzer;
import eu.optimis.tf.clients.TrustFrameworkIPClient;
import junit.framework.TestCase;

public class TrustDirectClientTest   extends TestCase{
    
    private static String filename = Test_aaS.filename;
    
    public void testTrust()
    {
        String host = Test_aaS.getBoundle("trust.ip");
        
        TrustTest(host);
        
    }//testTrust()
    
    public static void TrustTest(String host)
    {
        String serviceManifest = FileFunctions.readFileAsStringFromResources(filename);
        
        System.out.println("Trust Direct Client Test Started");
        System.out.println("at host : "+host);
        System.out.println("with file : "+filename);
        
        String port = Test_aaS.getBoundle("trust.port");
        
        SMAnalyzer smAnalyzer = new SMAnalyzer(serviceManifest);
                
        String spId = smAnalyzer.spId;
                
        System.out.println("spId : "+spId);
                
        TrustFrameworkIPClient trustClient = new TrustFrameworkIPClient(host, Integer.parseInt(port));
                
        String Trust = trustClient.getDeploymentTrust(spId);
                
         System.out.println("Trust Direct Client Value: "+Trust);
    }//TrustTest
}//class
