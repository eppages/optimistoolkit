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

import eu.optimis.ac.ph_info_openNebula.HostInfo;
import eu.optimis.ac.ph_info_openNebula.MonitoringManager;
import eu.optimis.ac.ph_info_openNebula.SetUpConnection;
import java.io.StringWriter;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;

@Path("/PhysicalHostsInfo")
public class PhysicalHostsInfo_aaS {
	
        protected static Logger log = Logger.getLogger(PhysicalHostsInfo_aaS.class);
    
        @GET
	@Path("/getXML/{host}/{port}")
	@Produces("text/plain")
	public String getXML(@PathParam("host") String host,
			@PathParam("port") String port)
	{
		
                log.info("host : "+host);
                log.info("port : "+port);
                
                StringWriter writer = new StringWriter();
    	
                writer.append("<PhysicalHosts>");
                
                MonitoringManager monitoringManager = null;
        
                try {
                    SetUpConnection setUpConnection = new SetUpConnection(log);
                    monitoringManager = new MonitoringManager(setUpConnection.oneClient);
                
                for(int i=0;i<monitoringManager.getMonInfoNumHosts();i++)
                {
                    HostInfo hostInfo = monitoringManager.getHostInfo(i);
                    String host_id = hostInfo.getName();
                    
                    writer.append("<PhysicalHost ");
                    writer.append("id=");
                    writer.append('"');
                    writer.append(host_id);
                    writer.append('"');
                    
                    writer.append(" maxCores=");
                    writer.append('"');
                    writer.append(Integer.toString(Integer.parseInt(hostInfo.getMax_cpu())/100));
                    writer.append('"');
                    
                    writer.append(" availableCores=");
                    writer.append('"');
                    writer.append(Integer.toString(((Integer.parseInt(hostInfo.getMax_cpu())-Integer.parseInt(hostInfo.getAllocated_cpu()))/100)));
                    writer.append('"');
                    
                    writer.append(">");
                    writer.append("</PhysicalHost>");
                    
                    log.info("id="+host_id+" maxCores="+Integer.toString(Integer.parseInt(hostInfo.getMax_cpu())/100)+
                            " availableCores= : "+Integer.toString(((Integer.parseInt(hostInfo.getMax_cpu())-Integer.parseInt(hostInfo.getAllocated_cpu()))/100)));
                  
                }//for-i each physical host
                
                } catch (Exception ex) {
                        log.error(ex.getMessage());
                }
		writer.append("</PhysicalHosts>");
                
                return writer.toString();
	}//getXML()
	
}//class
