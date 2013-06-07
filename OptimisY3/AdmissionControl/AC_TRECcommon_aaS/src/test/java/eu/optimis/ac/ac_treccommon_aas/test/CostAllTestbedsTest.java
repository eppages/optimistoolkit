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
import junit.framework.TestCase;
import eu.optimis.ac.ac_treccommon_aas.utils.XMLLike;

public class CostAllTestbedsTest   extends TestCase{
    
    public void testAllTestbedsCost()
    {
        ArrayList<String> list = new ArrayList<String>();
        
        System.out.println("Cost Test At All Testbeds Started");
        
        String host = "localhost";
        
        for(int i=0;i<5;i++)    
        {
            if(i==0)host = Test_aaS.getBoundle("cost.Direct.ip");
            else if(i==1)host="212.0.127.140";
            else if(i==2)host="130.239.48.6";
            else if(i==4)host="109.231.120.19";
            else if(i==3)host="213.27.211.124";
            
            if(host.hashCode()== Test_aaS.AC_TREC_host.hashCode())
                host = "localhost";
            
            System.out.println("Calling Cost Common at : "+host);
            
            ReturnedValue valueCommon = CostCommonTest.CostTest(host);
            
            System.out.println("Calling Cost Client at : "+host);
            
            ReturnedValue valueClient = CostClientTest.CostTest(host);
            
            list.add(host);
            list.add(valueCommon.getValue());
            list.add(Integer.toString(valueCommon.getResponseTime()));
            list.add(valueClient.getValue());
            list.add(Integer.toString(valueClient.getResponseTime()));
        }//for
        
        
        for(int i=0;i<list.size();i+=5)
        {
           host = list.get(i); 
           String valueCommon =  list.get(i+1);
           String responseTimeCommon =  list.get(i+2);
           String valueClient =  list.get(i+3);
           String responseTimeClient =  list.get(i+4);
           
           System.out.println("Cost Common at : "+printHost(host)
                   +" response Time(sec) is : "+responseTimeCommon);
           if(!XMLLike.isXMLLike(valueCommon))
                System.out.println("and Value is : "+valueCommon);
           System.out.println("Cost Client at : "+printHost(host)
                   +" response Time(sec) is : "+responseTimeClient);
           if(!XMLLike.isXMLLike(valueClient))
                System.out.println("and Value is : "+valueClient);
           
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
