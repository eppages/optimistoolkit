/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.optimis.ac.ph_info.api;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.optimis.ac.ph_info_openNebula.HostInfo;
import eu.optimis.ac.ph_info_openNebula.MonitoringManager;
import eu.optimis.ac.ph_info_openNebula.SetUpConnection;
//import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

@Path("/PhysicalHostsList")
public class PhysicalHostsList_aaS {
	
        private static Logger log = PhysicalHostsInfo_aaS.log;
    
        @POST
	@Path("/getList")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public MultivaluedMap<String, String> getList(MultivaluedMap<String, String> Params)
	{
                String host = Params.get("host").get(0);
                String port = Params.get("port").get(0);
                
                log.info("get List Started at host,port : "+host+","+port);
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                MonitoringManager monitoringManager = null;
        
                try {
                    SetUpConnection setUpConnection = new SetUpConnection(log);
                    monitoringManager = new MonitoringManager(setUpConnection.oneClient);
                
                for(int i=0;i<monitoringManager.getMonInfoNumHosts();i++)
                {
                    HostInfo hostInfo = monitoringManager.getHostInfo(i);
                    String host_id = hostInfo.getName();
                    
                    log.info(host_id);
                    
                    formParams.add("host_id", host_id);
                    
                }//for-i
                
                } catch (Exception ex) {
                        log.error(ex.getMessage());
                }
                
                return formParams;
	}//getList()
        
}//class
