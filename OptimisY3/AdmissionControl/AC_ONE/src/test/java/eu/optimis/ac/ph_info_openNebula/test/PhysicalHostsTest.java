/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ph_info_openNebula.test;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_MultivaluedMap_MultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class PhysicalHostsTest extends TestCase{
    
   public void testPrintPhysicalHostsNames()
   {
       
       String host = "localhost";
       
       printPhysicalHostsNames(host,PhysicalHostsNames(host,"8080"));
       
       System.out.println();System.out.println();System.out.println();
   }//testPrintPhysicalHostsNamesAt()
    
   private MultivaluedMap<String, String> PhysicalHostsNames(String host,String port)
   {
       
       if(PhysicalHostsInfoTest.getBoundle("PhysicalHostsInfo_aaS.ip").hashCode()==host.hashCode())
           host = "localhost";
        
       MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
       
       formParams.add("host", host);
       formParams.add("port", port);
       
       String url_String = "/AC_PhHostsInfo_OpenNebula/PhysicalHostsList/getList";
       
       RestClient_MultivaluedMap_MultivaluedMap client =
                new RestClient_MultivaluedMap_MultivaluedMap(
               PhysicalHostsInfoTest.getBoundle("PhysicalHostsInfo_aaS.ip"),PhysicalHostsInfoTest.getBoundle("PhysicalHostsInfo_aaS.port"),
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
