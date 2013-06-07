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

import eu.optimis.ac.ph_info.clientsInfo.InfrastructureProviderId;
import eu.optimis.ac.ph_info.clientsInfo.MaxCPU_CoresAndMemory;
import eu.optimis.ac.ph_info.clientsInfo.PhysicalHosts;
import eu.optimis.ac.ph_info.clientsInfo.UsedCPU_CoresAndMemory;
import java.util.ArrayList;
import junit.framework.TestCase;

public class PhysicalHostsCoresTest extends TestCase{
    
   public void testPrintPhysicalHostsCoresAtAllTestbeds()
   {
       String host = "localhost";
       
       for(int i=0;i<5;i++)    
       {
            if(i==0)host="212.0.127.140";
            else if(i==1)host="130.239.48.6";
            else if(i==3)host="109.231.120.19";
            else if(i==4)host="109.231.122.54";
            else if(i==2)host="213.27.211.124";
          
           printPhysicalHostsCores(host);
       }//for-i
       
       System.out.println();System.out.println();System.out.println();
   }//testPrintPhysicalHostsCoresAtAllTestbeds()
   
   public void printPhysicalHostsCores(String host)
   {
      System.out.println("at host : "+host);
      
      ArrayList<String> physicalHostsList = PhysicalHosts.getPhysicalHostsList(host,"8080");
      
      for(int i=0;i<physicalHostsList.size();i++)
      {
          String host_id = physicalHostsList.get(i);
          
          System.out.println(host_id+"("+InfrastructureProviderId.getIP_Id(host, "8080")+") : "+
                MaxCPU_CoresAndMemory.getMaxCPU_CoresAndMemory(host, "8080", host_id).getCpu_Cores()+" : "+
                MaxCPU_CoresAndMemory.getMaxCPU_CoresAndMemory(host, "8080", host_id).getMemory() +" : "+
                UsedCPU_CoresAndMemory.getUsedCPU_CoresAndMemory(host, "8080", host_id).getCpu_Cores() +" : "+
                UsedCPU_CoresAndMemory.getUsedCPU_CoresAndMemory(host, "8080", host_id).getMemory()
                );
        
        System.out.println("----------------------------------------------------------------");
      }//for-i
      
   }//PhysicalHostsCores()
   
}//class
