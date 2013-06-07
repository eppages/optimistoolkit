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
import eu.optimis.ac.ACRestClients.COrestACClientAPPLICATION_XML;
import eu.optimis.ac.ACRestClients.COrestACClientText_XML;
import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.ph_info.XMLparser.PhysicalHostsXML;
import eu.optimis.ac.ph_info.XMLparser.Resources;
import eu.optimis.ac.ph_info.XMLparser.ResourcesXML;
import eu.optimis.ac.ph_info.XMLparser.VirtualResourcesXML;
import eu.optimis.ac.ph_info.clientsInfo.PhysicalHosts;
import java.util.ArrayList;
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
                
                ArrayList<String> physicalHostsList = PhysicalHosts.getPhysicalHostsList(host,port,log);
                //CloudOptimizerRESTClient co = new CloudOptimizerRESTClient(host, Integer.parseInt(port));
                
                MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
                
                for(int i=0;i<physicalHostsList.size();i++)
                {
                    String host_id = physicalHostsList.get(i);
                    
                    log.info(host_id);
                    
                    formParams.add("host_id", host_id);
                    
                }//for-i
                
                return formParams;
	}//getList()
        
}//class
