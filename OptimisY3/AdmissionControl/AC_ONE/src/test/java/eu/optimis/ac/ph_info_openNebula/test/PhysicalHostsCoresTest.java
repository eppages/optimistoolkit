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

import eu.optimis.ac.ph_info_openNebula.HostInfo;
import eu.optimis.ac.ph_info_openNebula.MonitoringManager;
import junit.framework.TestCase;
import org.opennebula.client.OneException;

public class PhysicalHostsCoresTest extends TestCase{
    
   public void testPrintPhysicalHostsCores()
   {
       String host = "";
       
       try {
           printPhysicalHostsCores(host);
       } catch (OneException ex) {
           System.err.println(ex.getMessage());
       }
       
       System.out.println();System.out.println();System.out.println();
   }//testPrintPhysicalHostsCores()
   
   public void printPhysicalHostsCores(String host) throws OneException
   {
        System.out.println("at host : "+host);
       
        MonitoringManager monitoringManager = null;
        
        try {
                monitoringManager = new MonitoringManager(OneControllerTest.setUpConection());
                
        } catch (Exception ex) {
                System.err.println(ex.getMessage());
        }
      
      for(int i=0;i<monitoringManager.getMonInfoNumHosts();i++)
      {
          HostInfo hostInfo = monitoringManager.getHostInfo(i);
          String host_id = hostInfo.getName();
          
          System.out.println(host_id+" : "+
                    "maxCores="+(Integer.parseInt(hostInfo.getMax_cpu())/100)
                    +" availableCores="+"="+((Integer.parseInt(hostInfo.getMax_cpu())-Integer.parseInt(hostInfo.getAllocated_cpu()))/100)); 
          
      }//for-i
      
   }//PhysicalHostsCores()
   
}//class
