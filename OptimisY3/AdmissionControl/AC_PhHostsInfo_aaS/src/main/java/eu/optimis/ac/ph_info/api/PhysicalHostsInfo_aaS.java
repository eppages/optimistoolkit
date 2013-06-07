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

//import eu.optimis.cloudoptimizer.rest.client.CloudOptimizerRESTClient;
//import eu.optimis.mi.monitoring_resources.MonitoringResourceDatasets;
//import eu.optimis.mi.rest.client.getClient;
import eu.optimis.ac.ph_info.clientsInfo.InfrastructureProviderId;
import eu.optimis.ac.ph_info.clientsInfo.MaxCPU_CoresAndMemory;
import eu.optimis.ac.ph_info.clientsInfo.PhysicalHosts;
import eu.optimis.ac.ph_info.clientsInfo.UsedCPU_CoresAndMemory;
import java.io.StringWriter;
import java.util.ArrayList;
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
                
                String IP_Id = InfrastructureProviderId.getIP_Id(host, port);
                
                writer.append("<PhysicalHosts");
                
                writer.append(" IP_Id=");
                writer.append('"');
                writer.append(IP_Id);
                writer.append("\">");
                    
                ArrayList<String> physicalHostsList = PhysicalHosts.getPhysicalHostsList(host,port,log);
                
                //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient(host, Integer.parseInt(port));
                //getClient client = new getClient(host, 8080, "MonitoringManager/QueryResources");
                
                for(int i=0;i<physicalHostsList.size();i++)
                {
                    String host_id = physicalHostsList.get(i);
                    
                    /*
                    MonitoringResourceDatasets rs = 
                        client.getLatestReportForMetricNameId("No_of_cores", "physical", host_id);
                    
                    if(rs.getMonitoring_resource().size()!=1)
                    {
                        String msg ="PhysicalHostsInfo_aaS rs.getMonitoring_resource().size()!=1";
                        log.error(msg);
                        throw new RuntimeException(msg);
                    }//if
                    
                    if(!rs.getMonitoring_resource().get(0).getMetric_name().contains("No_of_cores"))
                    {
                        String msg ="PhysicalHostsInfo_aaS !rs.getMonitoring_resource().get(0).getMetric_name().contains(\"No_of_cores\")";
                        log.error(msg);
                        throw new RuntimeException(msg);
                    }//if
                    
                    String No_of_cores = rs.getMonitoring_resource().get(0).getMetric_value();
                    */
                    
                    int maxCores = MaxCPU_CoresAndMemory.getMaxCPU_CoresAndMemory(host, port, host_id).getCpu_Cores();
                    int usedCores = UsedCPU_CoresAndMemory.getUsedCPU_CoresAndMemory(host, port, host_id).getCpu_Cores();
                    
                    int maxMemoryInGigabytes = MaxCPU_CoresAndMemory.getMaxCPU_CoresAndMemory(host, port, host_id).getMemory();
                    
                    int freeMemoryInGigabytes = maxMemoryInGigabytes - UsedCPU_CoresAndMemory.getUsedCPU_CoresAndMemory(host, port, host_id).getMemory();
                    
                    
                    
                    writer.append("<PhysicalHost ");
                    writer.append("id=");
                    writer.append('"');
                    writer.append(host_id);
                    writer.append('"');
                    
                    writer.append(" maxCores=");
                    writer.append('"');
                    writer.append(Integer.toString(maxCores));
                    writer.append('"');
                    
                    writer.append(" usedCores=");
                    writer.append('"');
                    writer.append(Integer.toString(usedCores));
                    writer.append('"');
                    
                    writer.append(" maxMemoryInGigabytes=");
                    writer.append('"');
                    writer.append(Integer.toString(maxMemoryInGigabytes));
                    writer.append('"');
                    
                    writer.append(" freeMemoryInGigabytes=");
                    writer.append('"');
                    writer.append(Integer.toString(freeMemoryInGigabytes));
                    writer.append('"');
                    
                    writer.append(">");
                    writer.append("</PhysicalHost>");
                    
                    log.info("id="+host_id+" IP_Id="+IP_Id
                            +" maxCores="+maxCores
                            +" usedCores="+usedCores
                            +" maxMemoryInGigabytes="+maxMemoryInGigabytes
                            +" freeMemoryInGigabytes="+freeMemoryInGigabytes
                            );
                  
                }//for-i each physical host
                
		writer.append("</PhysicalHosts>");
                
                return writer.toString();
	}//getXML()
	
}//class
