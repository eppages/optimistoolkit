/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.ph_info.test;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import java.util.ResourceBundle;
import junit.framework.TestCase;

public class PhysicalHostsInfoTest extends TestCase{
    
    public void testPhysicalHostsInfo()
    {
        System.out.println();System.out.println();System.out.println();
        
        String host = getBoundle("PhysicalHostsInfo.ip");
        
        String host_aaS = getBoundle("PhysicalHostsInfo_aaS.ip");
        
        System.out.println("at host_aaS : "+host_aaS);
        System.out.println("at host : "+host);
        
        PhysicalHostsInfoTest(host,host_aaS);
        
        System.out.println();System.out.println();System.out.println();
    }//testPhysicalHostsInfo()
    
    public static void PhysicalHostsInfoTest(String host,String host_aaS)
    {
        if(host_aaS.hashCode()==host.hashCode())
            host = "localhost";
        
        String port = getBoundle("PhysicalHostsInfo.port");
        
        String url_String = "/AC_PhHostsInfo_aaS/PhysicalHostsInfo/getXML/"+host+"/"+port;
        
        doTest(host_aaS,url_String,false,"PhysicalHostsInfo : ");
    }//PhysicalHostsInfo()
    
    private static String doTest(String host_aaS,
            String url_String,Boolean ThrowRuntimeException,String value_str)
    {
            RestClient_noInput_String client =
                new RestClient_noInput_String(
                host_aaS,getBoundle("PhysicalHostsInfo_aaS.port"),
                url_String);
        
            int status = client.status;
            System.out.println("Status : "+status);
        
            String value = null;
            
            if(status == 200)
            {
                value = client.returnedString;
                
                System.out.println(value_str+value); 
                
            }//status == 200
            
            else if(ThrowRuntimeException) 
            {
                System.out.println("Status != 200 : "+status);
                throw new RuntimeException();
            }
            return value;
    }//doTest()
    
    public static String getBoundle(String key){
		
	ResourceBundle bundle = ResourceBundle.getBundle("config");
        
	return bundle.getString(key);
        
    }//getBoundle(String key)
}//class
