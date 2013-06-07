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
import eu.optimis.ac.ph_info_openNebula.OneController;
import junit.framework.TestCase;


public class MonitoringManagerTest extends TestCase{
    
    
    public void testMonitoringManager()
    {
        System.out.println("testMonitoringManager Started");
        
        try {
                OneController oneClient = OneControllerTest.setUpConection();
                
                MonitoringManager monitoringManager = new MonitoringManager(oneClient);
                
                String XML_string_with_host_pool_monitoring_information = monitoringManager.getMonInfoHosts();
                int number_of_hosts_in_the_cloud = monitoringManager.getMonInfoNumHosts();
                int number_of_active_hosts_in_the_cloud = monitoringManager.getMonInfoNumActHosts();
                //String XML_string_with_monitoring_information_about_the_hosts = monitoringManager.getMonInfoHosts(ArrayList<String> ids);
                
                System.out.println("XML_string_with_host_pool_monitoring_information : "+XML_string_with_host_pool_monitoring_information);
                System.out.println("number_of_hosts_in_the_cloud : "+number_of_hosts_in_the_cloud);
                System.out.println("number_of_active_hosts_in_the_cloud : "+number_of_active_hosts_in_the_cloud);
                
                for(int i=0;i<number_of_hosts_in_the_cloud;i++)
                {
                    HostInfo hostInfo = monitoringManager.getHostInfo(i);
                    
                    System.out.println("Name of Host : "+i+" is "+hostInfo.getName()
                            +" and MaxCPU are "+hostInfo.getMax_cpu()
                            +" and UsedCPU are "+hostInfo.getUsed_cpu()
                            +" and AllocatedCPU are "+hostInfo.getAllocated_cpu());
                    
                    
                    
                }//for-i
                
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        
        System.out.println("testMonitoringManager Finished");
    }//testMonitoringManager()
    
}//class
