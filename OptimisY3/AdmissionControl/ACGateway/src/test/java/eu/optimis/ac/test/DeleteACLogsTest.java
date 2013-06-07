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

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import static eu.optimis.ac.test.All_ACGatewayRemoteTest.All_IPs;
import eu.optimis.ac.test.remoteTest.GetServerDetails;
import junit.framework.TestCase;

public class DeleteACLogsTest extends TestCase {
    
    public void testDeleteACLogsAtAllTestbeds()
    {
            System.out.println();
            System.out.println("testDeleteACLogsAtAllTestbeds Started");
            System.out.println();
            
            String host = "localhost";
            
            for(int i=0;i<9;i++)
            {           
                        host = All_IPs(i);
                        if(host==null)continue;
                        
                        deleteACLogs(host,GetServerDetails.Port);
            }//for-i           
            
    }//testDeleteACLogsAtAllTestbeds()
    
    
    private void deleteACLogs(String host,String port)
    {
        System.out.println("At host : "+host);
        
        RestClient_noInput_String client = 
                new RestClient_noInput_String(host, port, "/ACGateway/clear/ACLogsDeletion");
        
        System.out.println("Status : "+client.status);
        if(client.status==200)
            System.out.println(client.returnedString);
    }//deleteACLogs()
}//class
