/*
 * Copyright (c) 2013 National Technical University of Athens (NTUA)
 *	
 *   							MIT License
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package eu.optimis.ac.gateway.acGatewayGothersExternalData;

import eu.optimis.ac.ACRestClients.MyRestClients.RestClient_noInput_String;
import eu.optimis.ac.gateway.init_finish.Initialize;
import eu.optimis.ac.gateway.utils.FileFunctions;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.log4j.Logger;

public class GetPhysicalHostsInfo {

        public String physicalHostsInfo;
	public String finalMessage ="PH ";
	private int status = -1;
        private Logger log;
        private int StartCounter = 0;
        
	public GetPhysicalHostsInfo(Initialize initialize, String ip_Id,MultivaluedMap<String, String> Params,
                String host, String port, Logger the_log)
	{
		log = the_log;
                	
                if(initialize.skipPhysicalHostInfoLevel>0)
                    StartCounter = initialize.skipPhysicalHostInfoLevel;
                
                if(StartCounter>1)StartCounter=1;
                
                for (int i = StartCounter; i < 2; i++) {
            
                    if (i == 0) {
                        getPhysicalHostsInfoFromClients(host,port,initialize);
                        if(status == 200) break;
                    } else {
                        getPhysicalHostsInfoFromFile(ip_Id);
                        break;
                    }
            
                }//for-i
                
                log.info("physicalHostsInfo : "+physicalHostsInfo);
	}//constructor
	
        private void getPhysicalHostsInfoFromClients(String host,String port, Initialize initialize)
        {
            log.info("Physical Hosts Info From Clients");
            
            log.info("Physical Hosts info : "+host +" "+port);
            
            String CloudOptimizerIP = initialize.CloudOptimizerIP;
            String CloudOptimizerPort = initialize.CloudOptimizerPort;
            
            String url_String = "/AC_PhHostsInfo_aaS/PhysicalHostsInfo/getXML/"+CloudOptimizerIP+"/"+CloudOptimizerPort;
            
            RestClient_noInput_String client =
                new RestClient_noInput_String(
                host,port,
                url_String,log);
            
            status = client.status;
            
            if(status == 200)
                physicalHostsInfo = client.returnedString;
            
            finalMessage = "PHl ";
                        
        }//getTrustInfoFromTRECcommon()
        
        private void getPhysicalHostsInfoFromFile(String Ip_Id)
        {
            log.info("Physical Hosts Info From file");
            
            log.info("Ip_Id : "+Ip_Id);
            
            String filename = null;
            
            if(Ip_Id.equals("UMU"))
                filename = "PhysicalHostsInfo_UMEA(UMU).xml";
            else if(Ip_Id.equals("ATOS"))
                filename = "PhysicalHostsInfo_ATOS(TST).xml";
            else if(Ip_Id.equals("ATOS 2"))
                filename = "PhysicalHostsInfo_ATOS(INT).xml";
            else if(Ip_Id.equals("FLEX"))
                filename = "PhysicalHostsInfo_FLEX.xml";
            else if(Ip_Id.equals("FLEX 2"))
                filename = "PhysicalHostsInfo_FLEX.xml";
            else if(Ip_Id.equals("ARSYS"))
                filename = "PhysicalHostsInfo_ARSYS.xml";
            else
                //filename = "PhysicalHostsInfo_ATOS(TST).xml";
            filename = "PhysicalHostsInfo_UMEA(UMU).xml";
            physicalHostsInfo = 
            FileFunctions.readFileAsStringFromResources("PhHosts/"+filename, log);
            
            if(StartCounter!=0)
                finalMessage = "PH("+StartCounter+")f ";
            else
                finalMessage = "PHf ";
            
        }//getTrustInfoFromFile()
        
}//class
