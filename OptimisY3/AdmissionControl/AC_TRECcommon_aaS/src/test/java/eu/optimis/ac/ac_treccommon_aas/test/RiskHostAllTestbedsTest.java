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

import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import junit.framework.TestCase;

public class RiskHostAllTestbedsTest   extends TestCase{
    
    public void testAllTestbedsRiskHost()
    {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<ArrayList<String>> List = new ArrayList<ArrayList<String>>();
        
        System.out.println("RiskHost Test At All Testbeds Started");
        
        String host = "localhost";
        
        for(int i=0;i<4;i++)    
        {
            if(i==0)host="212.0.127.140";
            else if(i==1)host="130.239.48.6";
            else if(i==3)host="109.231.120.19";
            else if(i==2)host="213.27.211.124";
            
            System.out.println("Calling RiskHost Common at : "+host);
            
            MultivaluedMap<String, String> returnedMapCommon = RiskHostCommonTest.RiskHostTest(host);
            
            System.out.println("Calling RiskHost Client at : "+host);
            
            MultivaluedMap<String, String> returnedMapClient = RiskHostClientTest.RiskHostTest(host);
            
            list.add(host);
            list.add(returnedMapCommon.get("executionTimeInSeconds").get(0));
            list.add(returnedMapClient.get("executionTimeInSeconds").get(0));
            
            ArrayList<String> listHosts = new ArrayList<String>();
            ArrayList<String> listClient = new ArrayList<String>();
            ArrayList<String> listCommon = new ArrayList<String>();
            
            for(int j=0;j<returnedMapCommon.get("host_id").size();j++)
            {
                listHosts.add(returnedMapCommon.get("host_id").get(j));
            }//for-j
            
            for(int j=0;j<returnedMapCommon.get("riskHost").size();j++)
            {
                listClient.add(returnedMapCommon.get("riskHost").get(j));
            }//for-j
            for(int j=0;j<returnedMapClient.get("riskHost").size();j++)
            {
                listCommon.add(returnedMapClient.get("riskHost").get(j));
            }//for-j
            
            List.add(listHosts);
            List.add(listCommon);
            List.add(listClient);
        }//for
        
        for(int i=0;i<list.size();i+=3)
        {
           host = list.get(i); 
           
           System.out.println("==>RiskHost Common at : "+printHost(host)+" response Time(sec) is : "+list.get(i+1));
           
           ArrayList<String> listHosts = List.get(i);
           ArrayList<String> listClient = List.get(i+1);
           ArrayList<String> listCommon = List.get(i+2);
           
           for(int j=0;j<listHosts.size();j++)
           {
               System.out.println(listHosts.get(j)+" : "+listCommon.get(j));
           }//for-j
           
           System.out.println("==>RiskHost Client at : "+printHost(host)+" response Time(sec) is : "+list.get(i+2));
           
           for(int j=0;j<listHosts.size();j++)
           {
               System.out.println(listHosts.get(j)+" : "+listClient.get(j));
           }//for-j
           
        }//for-i
      
    }//testAllTestbedsTrust()
    
    private String printHost(String host)
    {
       if(host.length()==12)
           return "  "+host;
       if(host.length()==13)
           return " "+host;
       
       return host;
    }//printHost
    
}//class
