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
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_MultivaluedMap;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_String;
import eu.optimis.ac.ac_treccommon_aas.utils.FileFunctions;
import java.util.ResourceBundle;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class Test_aaS    extends TestCase {
    
    public static String filename = "service_manifest.xml";
    
    private static Boolean RuntimeException_Test_aaS = false;
    public static Boolean RuntimeException_EcoSPclient = false;
    public static Boolean RuntimeException_EcoSPcommon = false;
    public static Boolean RuntimeException_EcoHostclient = false;
    public static Boolean RuntimeException_EcoHostcommon = false;
    public static Boolean RuntimeException_RiskHostclient = false;
    public static Boolean RuntimeException_RiskHostcommon = false;
    public static Boolean RuntimeException_CostClient = false;
    public static Boolean RuntimeException_CostCommon = false;
    public static Boolean RuntimeException_TrustClient = false;
    public static Boolean RuntimeException_TrustCommon = false;
    public static Boolean RuntimeException_RiskClient = false;
    public static Boolean RuntimeException_RiskCommon = false;
    
    public static String AC_TREC_host = getBoundle("AC_TREC.ip");

    public static String AC_TREC_port = getBoundle("AC_TREC.port");
        
    public void test()
    {
        String serviceManifest = FileFunctions.readFileAsStringFromResources(filename);
        
        System.out.println("Test_aaS Started");
        System.out.println("with file : "+filename);
        
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();

        formParams.add("serviceManifest", serviceManifest);
        formParams.add("host", AC_TREC_host);
        formParams.add("port", AC_TREC_port);
        
        String url_String = "/AC_TRECcommon_aaS/Test/test_aaS";
        
        doTest(formParams,url_String,RuntimeException_Test_aaS,"value : ");
        
        System.out.println("Test_aaS Finished ----------------------------------");
    }//test()
    
    public static String doTest(MultivaluedMap<String, String> formParams,
            String url_String,Boolean ThrowRuntimeException,String value_str)
    {
            RestClient_MultivaluedMap_String client =
                new RestClient_MultivaluedMap_String(
                Test_aaS.AC_TREC_host,Test_aaS.AC_TREC_port,url_String,formParams,false);
        
            int status = client.status;
            System.out.println("Status : "+status);
        
            String value = null;
            
            if(status == 200)
            {
                value = client.returnedString;
                
                System.out.println(value_str+value); 
                
            }//status == 200
            
            else if(ThrowRuntimeException) 
                throw new RuntimeException();
            
            return value;
    }//doTest()
    
    public static MultivaluedMap<String, String> doMultiTest(MultivaluedMap<String, String> formParams,
            String url_String,Boolean ThrowRuntimeException,String key)
    {
        
            RestClient_MultivaluedMap_MultivaluedMap client =
                new RestClient_MultivaluedMap_MultivaluedMap(
                Test_aaS.AC_TREC_host,Test_aaS.AC_TREC_port,url_String,formParams);
        
            int status = client.status;
            System.out.println("Status : "+status);
        
            if(status == 200)
            {
                for(int i=0;i<client.returnedMap.get(key).size();i++)
                {
                    System.out.println(formParams.get("host_id").get(i) +" "+client.returnedMap.get(key).get(i));
                    client.returnedMap.add("host_id", formParams.get("host_id").get(i));
                }//for-i
                
                return client.returnedMap;
            }//status == 200
            
            else if(ThrowRuntimeException) 
                throw new RuntimeException();
            
            return null;
    }//doTest()
    
    public static String getBoundle(String key){
		
	ResourceBundle bundle = ResourceBundle.getBundle("config");
        
	return bundle.getString(key);
        
    }//getBoundle(String key)
    
}//class
