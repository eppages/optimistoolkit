/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ph_info_openNebula;

import java.util.ArrayList;
import java.util.Iterator;
import org.opennebula.client.OneException;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;


public class MonitoringManager {
    
    private OneController oc;
    
    public MonitoringManager(OneController oc) {
		
        this.oc = oc;
        
    }//Constructor
    
    public String getMonInfoHosts() throws OneException 
    {
		String output;
		HostPool hostPool = oc.getHostPool();
		if (hostPool != null) {
			output = hostPool.info().getMessage();
		} else {
			output = "Error retrieving host monitoring info.";
		}
		return output;
                
    }//getMonInfoHosts()
    
    public int getMonInfoNumHosts() throws OneException 
    {
		int output = 0;
		HostPool hostPool = oc.getHostPool();

		if (hostPool != null)
			output = hostPool.getLength();

		return output;
                
    }//getMonInfoNumHosts()
    
    public int getMonInfoNumActHosts() throws OneException 
    {
		int output = 0;
		HostPool hostPool = oc.getHostPool();

		if (hostPool != null)
			for (Host h: hostPool)
				if (h.state() == 2)
					output++;
		return output;
                
    }//getMonInfoNumActHosts()
    
    public String getMonInfoHosts(ArrayList<String> ids) throws OneException 
    {		
		String output = "";
		Iterator<String> it = ids.iterator();
		HostPool hostPool = oc.getHostPool();
		HostInfo hm;
		Host h;
		String id;

		if (hostPool != null) {
			while (it.hasNext()) {
				id = it.next();
				h = hostPool.getById(Integer.parseInt(id));
				if (h != null) {
					hm = new HostInfo(h.id(), oc.getOneClient());
					output += hm.info().getMessage();
				} else {
					System.out.println("Host with ID = " + id + " does not exist.");
				}
			}
		} else {
			output = "Error retrieving host monitoring info.";
		}

		return output;
                
    }//getMonInfoHosts()
    
    public HostInfo getHostInfo(int id) throws OneException 
    {

		return oc.getHostInfo(id);
                
    }//getHostInfo()
}//class
