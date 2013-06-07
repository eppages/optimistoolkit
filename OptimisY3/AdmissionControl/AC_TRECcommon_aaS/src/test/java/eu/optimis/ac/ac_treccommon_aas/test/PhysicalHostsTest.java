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
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class PhysicalHostsTest extends TestCase{
    
   public void testPrintPhysicalHostsNamesAtAllTestbeds()
   {
       String host = "localhost";
       
       for(int i=0;i<4;i++)    
       {
            if(i==0)host="212.0.127.140";
            else if(i==1)host="130.239.48.6";
            else if(i==3)host="109.231.120.19";
            else if(i==2)host="213.27.211.124";
          
            printPhysicalHostsNames(host,PhysicalHostsNames(host));
            
       }//for-i
   }//testPrintPhysicalHostsNamesAtAllTestbeds()
    
   public static MultivaluedMap<String, String> PhysicalHostsNames(String host)
   {
       MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
       
       formParams.add("host", "localhost");
       formParams.add("port", Integer.toString(8080));
       
       String url_String = "/AC_PhHostsInfo_aaS/PhysicalHostsList/getList";
       
       RestClient_MultivaluedMap_MultivaluedMap client =
                new RestClient_MultivaluedMap_MultivaluedMap(
               host,Integer.toString(8080),
               url_String,formParams);
        
            int status = client.status;
            System.out.println("Status : "+status);
        
            if(status == 200)
            {
               return client.returnedMap;
            }//status == 200
            
            else
            {
                System.out.println("Status != 200 : "+status);
                throw new RuntimeException();
            }
   }//PhysicalHostsNames()
   
   private void printPhysicalHostsNames(String host,MultivaluedMap<String, String> listOfHosts)
   {
       System.out.println("at host : "+host);
       for(int i=0;i<listOfHosts.get("host_id").size();i++)
       {
           System.out.println(listOfHosts.get("host_id").get(i));
       }//for-i
   }//printPhysicalHostsNames()
   
}//class
