/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.infoCollector.smInfoCollector;

import eu.optimis.ac.smanalyzer.smInfo.Availability;
import eu.optimis.ac.smanalyzer.smInfo.ExtractInfo;
import eu.optimis.ac.smanalyzer.smInfo.ServiceComponentInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

class ServiceComponentsInfoAsLists {
    
    protected LinkedList<String> Basic = new LinkedList<String>();
    protected LinkedList<String> Elastic = new LinkedList<String>();
    protected LinkedList<String> Table = new LinkedList<String>();
    protected LinkedList<String> MemoryPerComponent = new LinkedList<String>();
    protected LinkedList<String> AffinityConstraints = new LinkedList<String>();
    protected LinkedList<String> AntiAffinityConstraints = new LinkedList<String>();
    
    protected LinkedList<String> ServiceComponents = new LinkedList<String>();
	
    protected LinkedList<String> ListOfId = new LinkedList<String>();

    protected LinkedList<String> ListOfAvailabilities = new LinkedList<String>();
    
    protected ServiceComponentsInfoAsLists(ExtractInfo smInfo,Availability availability)
    {
           ArrayList<ServiceComponentInfo> components = smInfo.serviceComponents;
           
           Iterator<?> iter = components.iterator();
		
		// for every service component
    	while (iter.hasNext()) 
    	{
    		ServiceComponentInfo serv_comp =  (ServiceComponentInfo) iter.next();
                
                Basic.addLast(serv_comp.getBaseVms());
                String Elastic_value = serv_comp.getElasticVms();
                if(Integer.parseInt(Elastic_value)==0)
                    Elastic_value = "1";
    		Elastic.addLast(Elastic_value);
    		
    		AffinityConstraints.addLast(serv_comp.getAffinityConstraints());
    		AntiAffinityConstraints.addLast(serv_comp.getAntiAffinityConstraints());
                
    		int table_value=serv_comp.getVirtualCpus();
    		
    		Table.addLast(String.valueOf(table_value));
    		
                int memory_value=serv_comp.getMemoryInMBs();
    		
    		MemoryPerComponent.addLast(String.valueOf(memory_value));
                
                ServiceComponents.addLast("yes");
    		
    		ListOfId.addLast(serv_comp.getId());
                
                String component_id = smInfo.ServiceComponentId_Map_componentId.get(serv_comp.getId()).get(0);
                
                String availability_value = "0";
                
                if(availability.availability_Map.containsKey(component_id))
                    availability_value=availability.availability_Map.get(component_id).get(0);
                if((Integer.parseInt(availability_value)==100)
                        ||(Integer.parseInt(availability_value)==0))
                    availability_value = "1.0";
                else
                    availability_value = "0."+availability.availability_Map.get(component_id).get(0);
                ListOfAvailabilities.addLast(availability_value);
                
        }//while
    		
    }//Constructor
    
}//class
